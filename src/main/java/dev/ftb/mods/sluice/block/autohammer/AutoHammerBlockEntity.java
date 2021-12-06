package dev.ftb.mods.sluice.block.autohammer;

import dev.ftb.mods.sluice.block.SluiceBlockEntities;
import dev.ftb.mods.sluice.item.SluiceModItems;
import dev.ftb.mods.sluice.recipe.FTBSluiceRecipes;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AutoHammerBlockEntity extends BlockEntity implements TickableBlockEntity {
    private static final int[][] IO_DIRECTIONAL_MATRIX = new int[][] {
            {4, 5}, // 2 north -> input[west] -> output[east]
            {5, 4}, // 3 south -> input[east] -> output[west]
            {3, 2}, // 4 west  -> input[south] -> output[north]
            {2, 3}, // 5 east  -> input[north] -> output[south]
    };

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

    private final AutoHammerOutputItemHandler outputInventory = new AutoHammerOutputItemHandler(this, 8);

    private final LazyOptional<ItemStackHandler> inputInvLazy = LazyOptional.of(() -> inputInventory);
    private final LazyOptional<AutoHammerOutputItemHandler> outputInvLazy = LazyOptional.of(() -> outputInventory);
    private final Supplier<Item> hammerItem;

    private int progress = 0;
    private int maxProgress = 0;
    private int timeOut = 0;
    private boolean processing = false;
    private ItemStack heldItem = ItemStack.EMPTY;

    public AutoHammerBlockEntity(BlockEntityType<?> blockEntityType, Supplier<Item> hammerItem) {
        super(blockEntityType);
        this.hammerItem = hammerItem;
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

        BlockState blockState = this.level.getBlockState(this.worldPosition);

        boolean isActive = blockState.getValue(AutoHammerBlock.ACTIVE);
        boolean shouldBeActive = inputHasItemsAndOutputIsClear();
        if (shouldBeActive && !isActive) {
            this.level.setBlock(this.worldPosition, blockState.setValue(AutoHammerBlock.ACTIVE, true), 3);
        } else if(!shouldBeActive && isActive) {
            this.level.setBlock(this.worldPosition, blockState.setValue(AutoHammerBlock.ACTIVE, false), 3);
        }

        if (!processing) {
            ItemStack inputStack = inputInventory.getStackInSlot(0);
            if (!inputStack.isEmpty()) {
                // Attempt to insert the items into the output, Time out and stop if any items would be lost
                List<ItemStack> hammerDrops = FTBSluiceRecipes.getHammerDrops(level, inputStack);

                // If we consumed all items, start processing
                if (hammerDrops.size() > 0 && pushIntoInternalOutputInventory(hammerDrops, true) >= hammerDrops.size()) {
                    heldItem = inputStack.copy();
                    heldItem.setCount(1);

                    inputInventory.extractItem(0, 1, false);
                    processing = true;
                    maxProgress = getProps().getHammerSpeed().get();
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
                ItemStack stackInSlot = outputInventory.extractItem(i, outputInventory.getStackInSlot(i).getCount(), true);
                if (!stackInSlot.isEmpty()) {
                    ItemStack stack = ItemHandlerHelper.insertItem(external, stackInSlot, false);
                    outputInventory.extractItem(i, stackInSlot.getCount() - stack.getCount(), false);
                    break;
                }
            }
        }

        // Now try and insert items into the input inventory
        IItemHandler pullSource = getExternalInventory(getInputDirection(facing));
        for (int i = 0; i < pullSource.getSlots(); i++) {
            ItemStack stack = pullSource.extractItem(i, 64, true);
            if (!stack.isEmpty() && hasItemAndIsHammerable(stack)) {
                ItemStack insertedStack = ItemHandlerHelper.insertItemStacked(inputInventory, stack, false);
                pullSource.extractItem(i, stack.getCount() - insertedStack.getCount(), false);
            }
        }
    }

    /**
     * Attempts to insert the item into the output inventory, and if successful, starts processing
     *
     * @param stack The item to insert
     */
    private boolean hasItemAndIsHammerable(ItemStack stack) {
        BlockState blockState = Block.byItem(stack.getItem()).defaultBlockState();

        boolean toolEffective = blockState.isToolEffective(ToolType.PICKAXE);
        boolean toolEffective2 = blockState.isToolEffective(ToolType.SHOVEL);
        int requiredLevel = blockState.getHarvestLevel();

        ItemStack stack1 = new ItemStack(hammerItem.get());
        int harvestLevel1 = hammerItem.get().getHarvestLevel(stack1, ToolType.PICKAXE, null, null);
        int harvestLevel2 = hammerItem.get().getHarvestLevel(stack1, ToolType.SHOVEL, null, null);

        if ((toolEffective && harvestLevel1 < requiredLevel) || (toolEffective2 && harvestLevel2 < requiredLevel)) {
            return false;
        }

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

    public boolean inputHasItemsAndOutputIsClear() {
        if (inputInventory.getStackInSlot(0).isEmpty()) {
            return false;
        }

        boolean hasSpace = false;
        for (int i = 0; i < outputInventory.getSlots(); i++) {
            ItemStack stackInSlot = outputInventory.getStackInSlot(i);
            if (stackInSlot.isEmpty() || (stackInSlot.getCount() != stackInSlot.getMaxStackSize() && stackInSlot.getItem() == inputInventory.getStackInSlot(0).getItem())) {
                hasSpace = true;
                break;
            }
        }

        return hasSpace;
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
        progress = tag.getInt("Progress");
        maxProgress = tag.getInt("MaxProgress");
        processing = tag.getBoolean("Processing");

        inputInvLazy.ifPresent(e -> e.deserializeNBT(tag.getCompound("InputInventory")));
        outputInvLazy.ifPresent(e -> e.deserializeNBT(tag.getCompound("OutputInventory")));

        super.load(state, tag);
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
            super(SluiceBlockEntities.IRON_AUTO_HAMMER.get(), SluiceModItems.IRON_HAMMER);
        }
    }

    public static class Gold extends AutoHammerBlockEntity {
        public Gold() {
            super(SluiceBlockEntities.GOLD_AUTO_HAMMER.get(), SluiceModItems.GOLD_HAMMER);
        }

        @Override
        public AutoHammerProperties getProps() {
            return AutoHammerProperties.GOLD;
        }
    }

    public static class Diamond extends AutoHammerBlockEntity {
        public Diamond() {
            super(SluiceBlockEntities.DIAMOND_AUTO_HAMMER.get(), SluiceModItems.DIAMOND_HAMMER);
        }

        @Override
        public AutoHammerProperties getProps() {
            return AutoHammerProperties.DIAMOND;
        }
    }

    public static class Netherite extends AutoHammerBlockEntity {
        public Netherite() {
            super(SluiceBlockEntities.NETHERITE_AUTO_HAMMER.get(), SluiceModItems.NETHERITE_HAMMER);
        }

        @Override
        public AutoHammerProperties getProps() {
            return AutoHammerProperties.NETHERITE;
        }
    }
}
