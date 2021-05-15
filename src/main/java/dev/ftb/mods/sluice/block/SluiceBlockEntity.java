package dev.ftb.mods.sluice.block;

import dev.ftb.mods.sluice.SluiceConfig;
import dev.ftb.mods.sluice.capabilities.Energy;
import dev.ftb.mods.sluice.capabilities.Fluid;
import dev.ftb.mods.sluice.recipe.SluiceModRecipeSerializers;
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
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class SluiceBlockEntity extends BlockEntity implements TickableBlockEntity {
	public final ItemStackHandler inventory = new ItemStackHandler(1) {
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


	public final LazyOptional<ItemStackHandler> inventoryOptional = LazyOptional.of(() -> this.inventory);
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

	public SluiceBlockEntity(BlockEntityType<?> type, SluiceProperties properties) {
		this(type, properties, false);
	}

	public SluiceBlockEntity(BlockEntityType<?> type, SluiceProperties properties, boolean isNetherite) {
		super(type);

		// Finds the correct properties from the block for the specific sluice tier
		this.properties = properties;
		this.isNetherite = isNetherite;

		this.energy = new Energy(!isNetherite ? 0 : SluiceConfig.NETHERITE_SLUICE.energyStorage.get(), e -> {
			if (!this.isNetherite) return;
			this.setChanged();
		});

		this.energyOptional = LazyOptional.of(() -> this.energy);
		this.maxProcessed = this.properties.processingTime.get();

		// Handles state changing
		this.tank = new Fluid(SluiceConfig.SLUICES.tankStorage.get(), e -> e.isEmpty() || e.getFluid().isSame(Fluids.WATER), (fluid, value, action) -> {
			if (this.level == null || this.getBlockState() == null) return;

			if (action.execute() && !fluid.isEmpty() && !this.getBlockState().getValue(SluiceBlock.WATER)) {
				this.level.setBlock(this.worldPosition, this.getBlockState().setValue(SluiceBlock.WATER, true), 3);
			}
		}, (fluid, value, action) -> {
			if (this.level == null || this.getBlockState() == null) return;

			if (action.execute() && fluid.isEmpty() && this.getBlockState().getValue(SluiceBlock.WATER)) {
				this.level.setBlock(this.worldPosition, this.getBlockState().setValue(SluiceBlock.WATER, false), 3);
			}
		});

		this.fluidOptional = LazyOptional.of(() -> this.tank);
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

		if (this.isNetherite && this.energy.getEnergyStored() > 0 && this.tank.getFluidAmount() <= SluiceConfig.SLUICES.tankStorage.get()) {
			this.tank.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
		}

		ItemStack input = this.inventory.getStackInSlot(0);
		if (!this.isProcessing) {
			if (input.isEmpty() || this.tank.isEmpty() || this.tank.getFluidAmount() < this.properties.fluidUsage.get()) {
				return;
			}

			this.processed = this.properties.processingTime.get();

			this.isProcessing = true;
			this.tank.drain(this.properties.fluidUsage.get(), IFluidHandler.FluidAction.EXECUTE);
			if (this.isNetherite) {

			}
			this.setChanged();
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
		} else {
			if(this.processed > 0) {
				this.processed--;
			} else {
				Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
				MeshType mesh = state.getValue(SluiceBlock.MESH);

				this.processed = 0;
				this.isProcessing = false;
				List<ItemStack> out = SluiceModRecipeSerializers.getRandomResult(this.level, mesh, input);
				out.forEach(e -> this.ejectItem(this.level, direction, e));

				this.inventory.setStackInSlot(0, ItemStack.EMPTY);

				if (this.isNetherite) {
					this.energy.consumeEnergy(SluiceConfig.NETHERITE_SLUICE.costPerUse.get(), false);
				}

				this.setChanged();
				this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
			}
		}

		if (this.processed % 10 == 0) {
			this.setChanged();
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
		}
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
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.properties.allowsIO) {
			return this.inventoryOptional.cast();
		}

		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && !this.isNetherite) {
			return this.fluidOptional.cast();
		}

		if (cap == CapabilityEnergy.ENERGY && this.isNetherite) {
			return this.energyOptional.cast();
		}

		return super.getCapability(cap, side);
	}

	private void ejectItem(Level w, Direction direction, ItemStack stack) {
		BlockEntity tileEntity = w.getBlockEntity(this.worldPosition.relative(direction));
		IItemHandler handler = tileEntity == null ? null : tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).orElse(null);

		if (handler != null) {
			stack = ItemHandlerHelper.insertItem(handler, stack, false);
		}

		if (!stack.isEmpty()) {
			double x = this.worldPosition.getX() + 0.5D + direction.getStepX() * 0.7D;
			double y = this.worldPosition.getY() + 0.5D;
			double z = this.worldPosition.getZ() + 0.5D + direction.getStepZ() * 0.7D;
			double in = w.random.nextFloat() * 0.1D;
			double mx = direction.getStepX() * in;
			double my = 0.14D;
			double mz = direction.getStepZ() * in;

			ItemEntity itemEntity = new ItemEntity(w, x, y, z, stack);
			itemEntity.setNoPickUpDelay();
			itemEntity.setDeltaMovement(mx, my, mz);
			w.addFreshEntity(itemEntity);
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
