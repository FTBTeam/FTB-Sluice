package dev.ftb.mods.sluice.block.autohammer;

import dev.ftb.mods.sluice.block.SluiceBlockEntities;
import dev.ftb.mods.sluice.recipe.FTBSluiceRecipes;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AutoHammerBlockEntity extends BlockEntity implements TickableBlockEntity {
    private static final int[][] IO_DIRECTIONAL_MATRIX = new int[][] {
            {4, 5}, // 2 north -> input[west] -> output[east]
            {5, 4}, // 3 south -> input[east] -> output[west]
            {3, 2}, // 4 west  -> input[south] -> output[north]
            {2, 3}, // 5 east  -> input[north] -> output[south]
    };

    public class InternalInsertableItemHandler extends ItemStackHandler {
        public InternalInsertableItemHandler(int size) {
            super(size);
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }

        @NotNull
        public ItemStack internalInsert(int slot, @NotNull ItemStack stack, boolean simulate) {
            ItemStack itemStack = super.insertItem(slot, stack, simulate);
            if (!simulate) {
                AutoHammerBlockEntity.this.setChanged();
            }
            return itemStack;
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack itemStack = super.extractItem(slot, amount, simulate);
            if (!simulate) {
                AutoHammerBlockEntity.this.setChanged();
            }
            return itemStack;
        }

        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 46656;
        }
    }

    private final ItemStackHandler inputInventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            AutoHammerProperties props = AutoHammerBlockEntity.this.getProps();

            ItemStack hammerStack = new ItemStack(props.getHammerItem().get());
            Block blockOfInput = Block.byItem(stack.getItem());
            boolean correctToolForDrops = hammerStack.getItem().isCorrectToolForDrops(blockOfInput.defaultBlockState());

            if (correctToolForDrops) {
                return FTBSluiceRecipes.hammerable(blockOfInput.defaultBlockState());
            }

            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            AutoHammerBlockEntity.this.setChanged();
        }
    };

    private static final Triple<Integer, IItemHandler, ItemStack> EMPTY_INPUT = Triple.of(-1, EmptyHandler.INSTANCE, ItemStack.EMPTY);

    private final InternalInsertableItemHandler outputInventory = new InternalInsertableItemHandler(12);

    private final LazyOptional<ItemStackHandler> inputInvLazy = LazyOptional.of(() -> inputInventory);
    private final LazyOptional<InternalInsertableItemHandler> outputInvLazy = LazyOptional.of(() -> outputInventory);

    private int progress = 0;
    private int maxProgress = 0;
    private int timeOut = 0;
    private boolean processing = false;
    private ItemStack heldItem = ItemStack.EMPTY;

    public AutoHammerBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }

        if (timeOut > 0) {
            timeOut--;
            return;
        }

        // By default, lets try and insert and export items in and out of the internal buffers
        pushPullInventories();

        if (!processing) {
            ItemStack inputStack = inputInventory.getStackInSlot(0);

            if (!inputStack.isEmpty()) {
                // Attempt to insert the items into the output, Time out and stop if any items would be lost
                List<ItemStack> hammerDrops = FTBSluiceRecipes.getHammerDrops(level, inputStack);

                // If we consumed all items, start processing
                if (pushIntoInternalOutputInventory(hammerDrops, true) >= hammerDrops.size()) {
                    heldItem = inputStack.copy();
                    heldItem.setCount(1);

                    inputInventory.extractItem(0, 1, false);
                    processing = true;
                    maxProgress = 10;
                    progress = 0;
                } else {
                    timeOut = getTimeoutDuration(); // Timeout for a while
                }
            } else {
                timeOut = getTimeoutDuration(); // Timeout for a while
            }
        } else {
            if (progress < maxProgress) {
                // if we're running, tick the progress
                progress++;
            } else {
                // We're done, lets try to insert the items into the output
                processing = false;
                progress = 0;
                maxProgress = 0;

                pushIntoInternalOutputInventory(FTBSluiceRecipes.getHammerDrops(level, heldItem), false);
                heldItem = ItemStack.EMPTY;
            }
        }
    }

    private void pushPullInventories() {
        // First, try and push items out of the output if any exist
        Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        IItemHandler external = getExternalInventory(getOutputDirection(facing));
        if (!(external instanceof EmptyHandler)) {
            for (int i = 0; i < outputInventory.getSlots(); i++) {
                ItemStack stackInSlot = outputInventory.getStackInSlot(i);
                ItemStack stack = ItemHandlerHelper.insertItem(external, stackInSlot, false);
                if (stackInSlot.getCount() != stack.getCount()) {
                    outputInventory.extractItem(i, stackInSlot.getCount() - stack.getCount(), false);
                    break;
                }
            }
        }

        // Now try and insert items into the input inventory
        ItemStack inputStack = inputInventory.getStackInSlot(0);
        IItemHandler internal = getExternalInventory(getInputDirection(facing));
        for (int i = 0; i < internal.getSlots(); i++) {
            ItemStack stack = internal.getStackInSlot(i);
            if ((inputStack.isEmpty() || inputStack.getItem() == stack.getItem()) && hasItemAndIsHammerable(stack)) {
                ItemStack insertedItem = inputInventory.insertItem(0, stack.copy(), false);
                if (insertedItem.getCount() != stack.getCount()) {
                    internal.extractItem(i, stack.getCount() - insertedItem.getCount(), false);
                }
            }
        }
    }

    /**
     * Attempts to insert the item into the output inventory, and if successful, starts processing
     *
     * @param stack The item to insert
     */
    private boolean hasItemAndIsHammerable(ItemStack stack) {
        return !stack.isEmpty() && FTBSluiceRecipes.hammerable(stack);
    }

    private int pushIntoInternalOutputInventory(List<ItemStack> items, boolean simulate) {
        int inserted = 0;

        for (ItemStack item : items) {
            for (int i = 0; i < outputInventory.getSlots(); i++) {
                ItemStack insert = outputInventory.internalInsert(i, item.copy(), simulate);
                if (insert.isEmpty()) {
                    inserted ++;
                    break;
                }
            }
        }

        return inserted;
    }

    private IItemHandler getExternalInventory(Direction direction) {
        BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(direction));
        if (blockEntity != null) {
            return blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).orElse(EmptyHandler.INSTANCE);
        }

        return EmptyHandler.INSTANCE;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("Progress", progress);
        tag.putInt("MaxProgress", maxProgress);
        tag.putBoolean("Processing", processing);

        inputInvLazy.ifPresent(e -> tag.put("InputInventory", e.serializeNBT()));
        outputInvLazy.ifPresent(e -> tag.put("OutputInventory", e.serializeNBT()));

        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);

        progress = tag.getInt("Progress");
        maxProgress = tag.getInt("MaxProgress");
        processing = tag.getBoolean("Processing");

        inputInvLazy.ifPresent(e -> e.deserializeNBT(tag.getCompound("InputInventory")));
        outputInvLazy.ifPresent(e -> e.deserializeNBT(tag.getCompound("OutputInventory")));
    }

    public static Direction getInputDirection(Direction facing) {
        return Direction.from3DDataValue(IO_DIRECTIONAL_MATRIX[facing.get3DDataValue() - 2][0]);
    }

    public static Direction getOutputDirection(Direction facing) {
        return Direction.from3DDataValue(IO_DIRECTIONAL_MATRIX[facing.get3DDataValue() - 2][1]);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        Direction dir = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);

        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == getInputDirection(dir)) {
                return inputInvLazy.cast();
            } else if (side == getOutputDirection(dir)) {
                return outputInvLazy.cast();
            }
        }

        return super.getCapability(cap, side);
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public int getTimeoutDuration() {
        return 100;
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }

    public AutoHammerProperties getProps() {
        return AutoHammerProperties.IRON;
    }

    public static class Iron extends AutoHammerBlockEntity {
        public Iron() {
            super(SluiceBlockEntities.IRON_AUTO_HAMMER.get());
        }
    }

    public static class Gold extends AutoHammerBlockEntity {
        public Gold() {
            super(SluiceBlockEntities.GOLD_AUTO_HAMMER.get());
        }

        @Override
        public AutoHammerProperties getProps() {
            return AutoHammerProperties.GOLD;
        }
    }

    public static class Diamond extends AutoHammerBlockEntity {
        public Diamond() {
            super(SluiceBlockEntities.DIAMOND_AUTO_HAMMER.get());
        }

        @Override
        public AutoHammerProperties getProps() {
            return AutoHammerProperties.DIAMOND;
        }
    }

    public static class Netherite extends AutoHammerBlockEntity {
        public Netherite() {
            super(SluiceBlockEntities.NETHERITE_AUTO_HAMMER.get());
        }

        @Override
        public AutoHammerProperties getProps() {
            return AutoHammerProperties.NETHERITE;
        }
    }
}
