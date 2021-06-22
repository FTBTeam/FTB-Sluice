package dev.ftb.mods.sluice.block.sluice;

import dev.ftb.mods.sluice.SluiceConfig;
import dev.ftb.mods.sluice.block.MeshType;
import dev.ftb.mods.sluice.block.SluiceBlockEntities;
import dev.ftb.mods.sluice.capabilities.Energy;
import dev.ftb.mods.sluice.capabilities.FluidCap;
import dev.ftb.mods.sluice.capabilities.ItemsHandler;
import dev.ftb.mods.sluice.item.UpgradeItem;
import dev.ftb.mods.sluice.item.Upgrades;
import dev.ftb.mods.sluice.recipe.FTBSluiceRecipes;
import dev.ftb.mods.sluice.recipe.ItemWithWeight;
import dev.ftb.mods.sluice.recipe.SluiceRecipeInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SluiceBlockEntity extends BlockEntity implements TickableBlockEntity, MenuProvider {
    public final ItemsHandler inventory;
    public final LazyOptional<ItemsHandler> inventoryOptional;
    public final FluidCap tank;
    public final LazyOptional<FluidCap> fluidOptional;
    private final SluiceProperties properties;
    private final boolean isNetherite;

    public final ItemStackHandler upgradeInventory = new ItemStackHandler(3) {
        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.getItem() instanceof UpgradeItem) {
                return super.insertItem(slot, stack, simulate);
            }

            return stack;
        }

        @Override
        protected void onContentsChanged(int slot) {
            SluiceBlockEntity.this.updateUpgradeCache(this);
        }
    };

    public Energy energy;
    public LazyOptional<Energy> energyOptional;
    /**
     * Amount of progress the processing step has made, 100 being fully processed and can drop
     * the outputs
     */
    public int processed;
    public int maxProcessed;
    private int fluidUsage;

    // Upgrade type, multiplication
    public final HashMap<Upgrades, Integer> upgradeCache = new HashMap<>();

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
            // Shouldn't be needed but it's better safe.
            if (!this.isNetherite) {
                return;
            }
            this.setChanged();
        });

        this.energyOptional = LazyOptional.of(() -> this.energy);
        this.maxProcessed = -1;
        this.fluidUsage = -1;

        // Handles state changing
        this.tank = new FluidCap(true, properties.tankCap.get(), e -> true);
        this.fluidOptional = LazyOptional.of(() -> this.tank);

        this.inventory = new ItemsHandler(!properties.allowsIO, 1) {
            @Override
            protected void onContentsChanged(int slot) {
                SluiceBlockEntity.this.setChanged();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (FTBSluiceRecipes.itemIsSluiceInput(getLevel(), SluiceBlockEntity.this.getBlockState().getValue(SluiceBlock.MESH), stack)) {
                    return super.insertItem(slot, stack, simulate);
                }

                return stack;
            }

            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }
        };

        this.inventoryOptional = LazyOptional.of(() -> this.inventory);
    }

    /**
     * Computes a list of resulting output items based on an input. We get the outputting items from the
     * custom recipe.
     */
    public static List<ItemStack> getRandomResult(SluiceBlockEntity sluice, ItemStack input) {
        List<ItemStack> outputResults = new ArrayList<>();
        if (sluice.level == null) {
            return outputResults;
        }

        SluiceRecipeInfo recipe = FTBSluiceRecipes.getSluiceRecipes(sluice.tank.getFluid().getFluid(), sluice.level, sluice.getBlockState().getValue(SluiceBlock.MESH), input);
        int additional = 0;
        if (sluice.upgradeCache.containsKey(Upgrades.LUCK)) {
            additional += Upgrades.LUCK.effectedChange * sluice.upgradeCache.get(Upgrades.LUCK);
        }

        List<ItemWithWeight> items = recipe.getItems();
        Collections.shuffle(items); // Spin the wheel to make it a little less predictable
        for (ItemWithWeight result : items) {
            float number = sluice.level.getRandom().nextFloat();
            if (number <= Mth.clamp(result.weight + (additional / 100D), 0, 1)) {
                if (outputResults.size() >= recipe.getMaxDrops()) {
                    break;
                }

                outputResults.add(result.item.copy());
            }
        }

        return outputResults;
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

        ItemStack input = this.inventory.getStackInSlot(0);
        if (this.maxProcessed < 0) {
            this.startProcessing(this.level, input);
        } else {
            if (this.processed < this.maxProcessed) {
                if (getBlockState().getValue(SluiceBlock.MESH) == MeshType.NONE) {
                    cancelProcessing(level, input);
                    return;
                }
                this.processed++;
            } else {
                this.finishProcessing(this.level, state, input);
            }
            if (this.processed % 4 == 0) {
                this.setChanged();
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
            }
        }

        if (level.getGameTime() % 10L == 0) {
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

        if (stack.isEmpty()) {
            return;
        }

        // Throw out any residual stacks if the player has removed the mesh
        if (getBlockState().getValue(SluiceBlock.MESH) == MeshType.NONE) {
            cancelProcessing(level, stack);
            return;
        }

        if(this.tank.isEmpty() || this.tank.getFluidAmount() < this.properties.fluidUsage.get()) {
            return;
        }

        System.out.println(computePowerCost());

        SluiceRecipeInfo recipe = FTBSluiceRecipes.getSluiceRecipes(this.tank.getFluid().getFluid(), level, this.getBlockState().getValue(SluiceBlock.MESH), stack);

        // Throw items out if we don't have a recipe from them. It's simpler than giving the cap a world and mesh.
        if (recipe.getItems().isEmpty()) {
            cancelProcessing(level, stack);
            return;
        }

        this.processed = 0;
        this.maxProcessed = (int) Math.ceil(recipe.getProcessingTime() * this.properties.processingTime.get());
        this.fluidUsage = (int) Math.ceil(recipe.getFluidUsed() * this.properties.fluidUsage.get());

        this.setChanged();
        level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
    }

    /**
     * Handles the output of the process. If we're using netherite, we have different uses here.
     *
     * @param itemStack the input item from the start of the process.
     */
    private void finishProcessing(@Nonnull Level level, BlockState state, ItemStack itemStack) {
        this.processed = 0;
        this.maxProcessed = -1;

        getRandomResult(this, itemStack)
                .forEach(e -> this.ejectItem(level, state.getValue(BlockStateProperties.HORIZONTAL_FACING), e));

        this.inventory.setStackInSlot(0, ItemStack.EMPTY);
        this.tank.internalDrain(this.fluidUsage, IFluidHandler.FluidAction.EXECUTE);

        this.fluidUsage = -1;

        if (this.isNetherite) {
            this.energy.consumeEnergy(SluiceConfig.NETHERITE_SLUICE.costPerUse.get(), false);
        }

        this.setChanged();
        level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
    }

    private void cancelProcessing(Level level, ItemStack stack) {
        this.ejectItem(level, this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING), stack);
        this.inventory.setStackInSlot(0, ItemStack.EMPTY);
        this.processed = 0;
        this.maxProcessed = -1;
        this.fluidUsage = -1;
        this.setChanged();
        level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
    }

    private int computePowerCost() {
        int baseCost = SluiceConfig.NETHERITE_SLUICE.costPerUse.get();
        if (upgradeCache.size() == 0) {
            return baseCost;
        }

        int sum = 0;
        for (Integer x : upgradeCache.values()) {
            int i = x;
            sum += i;
        }

        return (int) (baseCost + baseCost * (sum * SluiceConfig.GENERAL.percentageCostPerUpgrade.get()) / 100);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CompoundTag fluidTag = new CompoundTag();
        this.tank.writeToNBT(fluidTag);

        compound.put("Inventory", this.inventory.serializeNBT());
        compound.put("Fluid", fluidTag);
        compound.putInt("Processed", this.processed);
        compound.putInt("MaxProcessed", this.maxProcessed);
        compound.putInt("FluidUsage", this.fluidUsage);
        if (this.isNetherite) {
            compound.put("Upgrades", upgradeInventory.serializeNBT());
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
        this.fluidUsage = compound.getInt("FluidUsage");

        if (this.isNetherite) {
            this.energyOptional.ifPresent(e -> e.deserializeNBT(compound.getCompound("Energy")));
            this.upgradeInventory.deserializeNBT(compound.getCompound("Upgrades"));
            this.updateUpgradeCache(this.upgradeInventory);
        }

        if (compound.contains("Fluid")) {
            this.tank.readFromNBT(compound.getCompound("Fluid"));
        }
    }

    private void updateUpgradeCache(ItemStackHandler handler) {
        SluiceBlockEntity.this.upgradeCache.clear();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!(stack.getItem() instanceof UpgradeItem)) {
                continue;
            }

            SluiceBlockEntity.this.upgradeCache.put(((UpgradeItem) stack.getItem()).getUpgrade(), Math.min(stack.getCount(), 18));
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.properties.allowsIO) {
            return this.inventoryOptional.cast();
        }

        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this.properties.allowsTank) {
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
            IItemHandler handler = this.seekNearestInventory(w).orElseGet(EmptyHandler::new);

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
     * @param level level to find the inventory from
     * @return A valid IItemHandler or a empty optional
     */
    private LazyOptional<IItemHandler> seekNearestInventory(Level level) {
        BlockPos pos = this.getBlockPos().relative(this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING), 2);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null && !(blockEntity instanceof SluiceBlockEntity)) {
            LazyOptional<IItemHandler> capability = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (capability.isPresent()) {
                return capability;
            }
        }

        return LazyOptional.empty();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(this.getBlockPos()).inflate(1);
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

    @Override
    public Component getDisplayName() {
        return new TextComponent("Sluice");
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory arg, Player arg2) {
        return !(this instanceof NetheriteSluiceBlockEntity) ? null : new SluiceBlockContainer(i, arg, this);
    }

    public static class OakSluiceBlockEntity extends SluiceBlockEntity {
        public OakSluiceBlockEntity() {
            super(SluiceBlockEntities.OAK_SLUICE.get(), SluiceProperties.OAK);
        }
    }

    public static class IronSluiceBlockEntity extends SluiceBlockEntity {
        public IronSluiceBlockEntity() {
            super(SluiceBlockEntities.IRON_SLUICE.get(), SluiceProperties.IRON);
        }
    }

    public static class DiamondSluiceBlockEntity extends SluiceBlockEntity {
        public DiamondSluiceBlockEntity() {
            super(SluiceBlockEntities.DIAMOND_SLUICE.get(), SluiceProperties.DIAMOND);
        }
    }

    public static class NetheriteSluiceBlockEntity extends SluiceBlockEntity {
        public NetheriteSluiceBlockEntity() {
            super(SluiceBlockEntities.NETHERITE_SLUICE.get(), SluiceProperties.NETHERITE, true);
        }
    }
}
