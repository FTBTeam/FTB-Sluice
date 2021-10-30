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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AutoHammerBlockEntity extends BlockEntity implements TickableBlockEntity {
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

        if (!processing) {
            ItemStack inputItem = inputInventory.getStackInSlot(0);

            if (hasItemAndIsHammerable(inputItem)) {
                ItemStack outputItem = this.outputInventory.getStackInSlot(0);
                if (outputItem.isEmpty()) {
                    takeItemAndStartProcessing(inputItem);
                } else {
                    // Attempt to insert the items into the output, Time out and stop if any items would be lost
                    List<ItemStack> hammerDrops = FTBSluiceRecipes.getHammerDrops(level, inputItem);
                    boolean consumedAllItems = true;
                    for (ItemStack drop : hammerDrops) {
                        for (int i = 0; i < outputInventory.getSlots(); i++) {
                            ItemStack itemStack = outputInventory.internalInsert(i, drop.copy(), true);
                            if (!itemStack.isEmpty()) {
                                consumedAllItems = false;
                                timeOut = 100; // Timeout for a while
                                break;
                            }
                        }
                    }

                    // If we consumed all items, start processing
                    if (consumedAllItems) {
                        takeItemAndStartProcessing(inputItem);
                    }
                }
            }
        } else {
            if (progress < maxProgress) {
                progress++;
            } else {
                processing = false;
                progress = 0;
                maxProgress = 0;

                // Find a target inventory to insert to
                IItemHandler iItemHandler = Optional.ofNullable(level.getBlockEntity(worldPosition.east()))
                        .map(e -> e.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.EAST).orElse(outputInventory))
                        .orElse(outputInventory);

                FTBSluiceRecipes.getHammerDrops(level, heldItem).forEach(drop -> {
                    insertItemsToInventory(iItemHandler, drop.copy());
                });

                heldItem = ItemStack.EMPTY;
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

    /**
     * Take the item and start processing
     *
     * @param stack The item to process
     */
    private void takeItemAndStartProcessing(ItemStack stack) {
        heldItem = stack.copy();
        heldItem.setCount(1);

        inputInventory.extractItem(0, 1, false);
        processing = true;
        maxProgress = 20;
        progress = 0;
    }

    /**
     * Inserts the item into the inventory, if possible.
     *
     * @param inventory The inventory to insert into
     * @param stack The item to insert
     */
    private void insertItemsToInventory(IItemHandler inventory, ItemStack stack) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack itemStack = inventory instanceof InternalInsertableItemHandler
                    ? ((InternalInsertableItemHandler) inventory).internalInsert(i, stack, false)
                    : inventory.insertItem(i, stack, false);

            if (itemStack.isEmpty()) {
                break;
            }
        }
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

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == Direction.EAST) {
                return outputInvLazy.cast();
            } else if (side == Direction.WEST) {
                return inputInvLazy.cast();
            }
        }

        return super.getCapability(cap, side);
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
