package dev.ftb.mods.sluice.block;

import dev.ftb.mods.sluice.SluiceConfig;
import dev.ftb.mods.sluice.capabilities.Energy;
import dev.ftb.mods.sluice.capabilities.Fluid;
import dev.ftb.mods.sluice.capabilities.ItemsHandler;
import dev.ftb.mods.sluice.recipe.SluiceModRecipeSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SluiceBlockEntity extends BlockEntity implements TickableBlockEntity {
    public final ItemsHandler inventory;
    public final LazyOptional<ItemsHandler> inventoryOptional;
    public final Fluid tank;
    public final LazyOptional<Fluid> fluidOptional;
    private final SluiceProperties properties;
    private final boolean isNetherite;

    public Energy energy;
    public LazyOptional<Energy> energyOptional;
    /**
     * Amount of progress the processing step has made, 100 being fully processed and can drop
     * the outputs
     */
    public int processed;
    public int maxProcessed;
    public boolean isProcessing = false;

    private Pair<BlockPos, Direction> closestInventory;

    public SluiceBlockEntity(BlockEntityType<?> type, SluiceProperties properties) {
        this(type, properties, false);
    }

    public SluiceBlockEntity(BlockEntityType<?> type, SluiceProperties properties, boolean isNetherite) {
        super(type);

        // Finds the correct properties from the block for the specific sluice tier
        this.properties = properties;
        this.isNetherite = isNetherite;

        this.energy = new Energy(!isNetherite
                ? 0
                : SluiceConfig.NETHERITE_SLUICE.energyStorage.get(), e -> {
            if (!this.isNetherite) {
                return;
            }
            this.setChanged();
        });

        this.energyOptional = LazyOptional.of(() -> this.energy);
        this.maxProcessed = this.properties.processingTime.get();

        // Handles state changing
        this.tank = new Fluid(true, SluiceConfig.SLUICES.tankStorage.get(), e -> true, this::fluidStateUpdate, this::fluidStateUpdate);
        this.fluidOptional = LazyOptional.of(() -> this.tank);

        this.inventory = new ItemsHandler(type == SluiceModBlockEntities.OAK_SLUICE.get(), 1) {
            @Override
            protected void onContentsChanged(int slot) {
                SluiceBlockEntity.this.setChanged();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }
        };

        this.inventoryOptional = LazyOptional.of(() -> this.inventory);
    }

    private void fluidStateUpdate(Fluid fluid, int value, IFluidHandler.FluidAction action) {
//        if (this.level == null || this.getBlockState() == null || action.simulate()) {
//            return;
//        }
//
//        if (!fluid.isEmpty() && !this.getBlockState().getValue(SluiceBlock.WATER)) {
//            this.setFluidModel(true);
//        }
//
//        if (fluid.isEmpty() && this.getBlockState().getValue(SluiceBlock.WATER)) {
//            this.setFluidModel(false);
//        }
    }

    private void setFluidModel(boolean show) {
        assert this.level != null;

        this.level.setBlock(this.worldPosition, this.getBlockState().setValue(SluiceBlock.WATER, show), Constants.BlockFlags.DEFAULT_AND_RERENDER);
        BlockPos relative = this.getBlockPos().relative(this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));
        BlockState blockState = this.level.getBlockState(relative);
        if (blockState.getBlock() instanceof SluiceBlock && blockState.getValue(SluiceBlock.PART) == SluiceBlock.Part.FUNNEL) {
            this.level.setBlock(relative, blockState.setValue(SluiceBlock.WATER, show), Constants.BlockFlags.DEFAULT_AND_RERENDER);
        }
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof SluiceBlock)) {
            return;
        }

        if (this.isNetherite && this.energy.getEnergyStored() > 0 && this.tank.getFluidAmount() < SluiceConfig.SLUICES.tankStorage.get()) {
            this.tank.internalFill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
            this.energy.consumeEnergy(5, false); // Sip some power for the water.
        }

        ItemStack input = this.inventory.getStackInSlot(0);
        if (!this.isProcessing) {
            this.startProcessing(this.level, input);
        } else {
            if (this.processed > 0) {
                this.processed--;
            } else {
                this.finishProcessing(this.level, state, input);
            }
        }

        if (this.processed % 10 == 0) {
            this.setChanged();
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
        }
    }

    /**
     * Starts the processing process as long as we have enough fluid and an item in the inventory.
     * We also push a block update to make sure the TES is up to date.
     */
    private void startProcessing(@Nonnull Level level, ItemStack stack) {
        // No energy, no go.
        if (this.isNetherite && this.energy.getEnergyStored() <= 0) {
            return;
        }

        if (stack.isEmpty() || this.tank.isEmpty() || this.tank.getFluidAmount() < this.properties.fluidUsage.get()) {
            return;
        }

        // Throw items out if we don't have a recipe from them. It's simpler than giving the cap a world and mesh.
        if (!SluiceModRecipeSerializers.itemHasSluiceResults(this.tank.getFluid().getFluid(), level, this.getBlockState().getValue(SluiceBlock.MESH), stack)) {
            this.ejectItem(level, this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING), stack);
            this.inventory.setStackInSlot(0, ItemStack.EMPTY);
            this.setChanged();
            return;
        }

        this.processed = this.properties.processingTime.get();
        this.isProcessing = true;

        this.setChanged();
        level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
    }

    /**
     * Handles the output of the process. If we're using netherite, we have different uses here.
     *
     * @param itemStack the input item from the start of the process.
     */
    private void finishProcessing(@Nonnull Level level, BlockState state, ItemStack itemStack) {
        MeshType mesh = state.getValue(SluiceBlock.MESH);

        this.processed = 0;
        this.isProcessing = false;

        SluiceModRecipeSerializers.getRandomResult(this.tank.getFluid().getFluid(), level, mesh, itemStack)
                .forEach(e -> this.ejectItem(level, state.getValue(BlockStateProperties.HORIZONTAL_FACING), e));

        this.inventory.setStackInSlot(0, ItemStack.EMPTY);
        this.tank.internalDrain(this.properties.fluidUsage.get(), IFluidHandler.FluidAction.EXECUTE);

        if (this.isNetherite) {
            this.energy.consumeEnergy(SluiceConfig.NETHERITE_SLUICE.costPerUse.get(), false);
        }

        this.setChanged();
        level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CompoundTag fluidTag = new CompoundTag();
        this.tank.writeToNBT(fluidTag);

        compound.put("Inventory", this.inventory.serializeNBT());
        compound.put("Fluid", fluidTag);
        compound.putInt("Processed", this.processed);
        compound.putInt("MaxProcessed", this.maxProcessed);
        compound.putBoolean("IsProcessing", this.isProcessing);
        if (this.isNetherite) {
            this.energyOptional.ifPresent(e -> compound.put("Energy", e.serializeNBT()));
        }

        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundTag compound) {
        super.load(state, compound);

        this.inventory.deserializeNBT(compound.getCompound("Inventory"));
        this.processed = compound.getInt("Processed");
        this.maxProcessed = compound.getInt("MaxProcessed");
        this.isProcessing = compound.getBoolean("IsProcessing");

        if (this.isNetherite) {
            this.energyOptional.ifPresent(e -> e.deserializeNBT(compound.getCompound("Energy")));
        }
        if (compound.contains("Fluid")) {
            this.tank.readFromNBT(compound.getCompound("Fluid"));
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && !(this instanceof OakSluiceBlockEntity)) {
            return this.inventoryOptional.cast();
        }

        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return this.fluidOptional.cast();
        }

        if (cap == CapabilityEnergy.ENERGY && this.isNetherite) {
            return this.energyOptional.cast();
        }

        return super.getCapability(cap, side);
    }

    private void ejectItem(Level w, Direction direction, ItemStack stack) {
        if (this.properties.allowsIO) {
            // Find the closest inventory to the block.
            IItemHandler handler = this.seekNearestInventory(w, this.getBlockPos()).orElseGet(EmptyHandler::new);

            // Empty handler does not have slots and is thus very simple to check against.
            if (handler.getSlots() != 0) {
                stack = ItemHandlerHelper.insertItem(handler, stack, false);
            }
        }

        if (!stack.isEmpty()) {
            BlockPos pos = this.worldPosition.relative(direction);

            double my = 0.14D * (w.random.nextFloat() * 0.4D);

            ItemEntity itemEntity = new ItemEntity(w, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
            itemEntity.setNoPickUpDelay();
            itemEntity.setDeltaMovement(0, my, 0);
            w.addFreshEntity(itemEntity);
        }
    }

    /**
     * Attempts to seek out a valid inventory, when found, we hold it in memory. We then
     * check that on every use after being found. If the tile disappears we'll try to find
     * another, otherwise, we'll send back and empty. Max call depth 1, max loops 6.
     * (Don't allow sluice tile as a cap)
     *
     * @param level level to find the inventory from
     * @param start the starting pos (the tiles pos)
     * @return A valid IItemHandler or a empty optional
     */
    private LazyOptional<IItemHandler> seekNearestInventory(Level level, BlockPos start) {
        if (this.closestInventory == null) {
            // Go around the block and find a valid cap. We then use getOpposite to ensure the side we've found
            // Of the other block is actually accepting an inventory. Some pipes are very directional.
            for (Direction direction : Direction.values()) {
                BlockPos relative = start.relative(direction);
                BlockEntity blockEntity = level.getBlockEntity(relative);
                if (blockEntity != null && !(blockEntity instanceof SluiceBlockEntity)) {
                    LazyOptional<IItemHandler> capability = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite());
                    if (capability.isPresent()) {
                        this.closestInventory = Pair.of(relative, direction.getOpposite());
                        return capability;
                    }
                }
            }

            return LazyOptional.empty();
        } else {
            // Validate that the tile is still valid before sending it's cap back.
            BlockEntity blockEntity = level.getBlockEntity(this.closestInventory.getKey());
            if (blockEntity == null || blockEntity instanceof SluiceBlockEntity) {
                this.closestInventory = null;
                return this.seekNearestInventory(level, start); // Try again.
            }

            LazyOptional<IItemHandler> capability = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.closestInventory.getValue());
            if (!capability.isPresent()) {
                this.closestInventory = null;
                return this.seekNearestInventory(level, start); // Try again.
            }

            return capability;
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundTag tag) {
        this.load(state, tag);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 0, this.save(new CompoundTag()));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(this.getBlockState(), pkt.getTag());
    }

    public static class OakSluiceBlockEntity extends SluiceBlockEntity {
        public OakSluiceBlockEntity() {
            super(SluiceModBlockEntities.OAK_SLUICE.get(), SluiceProperties.OAK);
        }
    }

    public static class IronSluiceBlockEntity extends SluiceBlockEntity {
        public IronSluiceBlockEntity() {
            super(SluiceModBlockEntities.IRON_SLUICE.get(), SluiceProperties.IRON);
        }
    }

    public static class DiamondSluiceBlockEntity extends SluiceBlockEntity {
        public DiamondSluiceBlockEntity() {
            super(SluiceModBlockEntities.DIAMOND_SLUICE.get(), SluiceProperties.DIAMOND);
        }
    }

    public static class NetheriteSluiceBlockEntity extends SluiceBlockEntity {
        public NetheriteSluiceBlockEntity() {
            super(SluiceModBlockEntities.NETHERITE_SLUICE.get(), SluiceProperties.NETHERITE, true);
        }
    }
}
