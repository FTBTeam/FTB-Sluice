package dev.ftb.mods.sluice.block;

import dev.ftb.mods.sluice.capabilities.Energy;
import dev.ftb.mods.sluice.recipe.SluiceModRecipeSerializers;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

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

		@NotNull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}
	};

	public final FluidTank tank = new FluidTank(1000, e -> e.isEmpty() || e.getFluid().isSame(Fluids.WATER));
	public final LazyOptional<ItemStackHandler> inventoryOptional = LazyOptional.of(() -> this.inventory);
	public final LazyOptional<FluidTank> fluidOptional = LazyOptional.of(() -> this.tank);
	private final SluiceProperties properties;
	private final boolean isNetherite;
	public Energy energy;
	public LazyOptional<Energy> energyOptional;
	/**
	 * Amount of progress the processing step has made, 100 being fully processed and can drop
	 * the outputs
	 */
	public int processed;
	public boolean isProcessing = false;

	public SluiceBlockEntity(BlockEntityType<?> type, SluiceProperties properties) {
		this(type, properties, false);
	}

	public SluiceBlockEntity(BlockEntityType<?> type, SluiceProperties properties, boolean isNetherite) {
		super(type);

		// Finds the correct properties from the block for the specific sluice tier
		this.properties = properties;
		this.isNetherite = isNetherite;

		this.energy = new Energy(100000, e -> {
			if (!this.isNetherite) return;
			this.setChanged();
		});

		System.out.println(this.energy);

		this.energyOptional = LazyOptional.of(() -> this.energy);
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
		if (!this.isProcessing) {
			if (input.isEmpty() || this.tank.isEmpty() || this.tank.getFluidAmount() < this.properties.fluidUsage.get()) {
				return;
			}

			this.processed = this.properties.processingTime.get();
			this.isProcessing = true;
			this.tank.drain(this.properties.fluidUsage.get(), IFluidHandler.FluidAction.EXECUTE);
			System.out.println("Time set to process item");
		} else {
			if(this.processed > 0) {
				this.processed--;
				System.out.println("Processing progress: " + this.processed + "%");
			} else {
				Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
				MeshType mesh = state.getValue(SluiceBlock.MESH);

				System.out.println("Finished processing");
				this.processed = 0;
				this.isProcessing = false;
				List<ItemStack> out = SluiceModRecipeSerializers.getRandomResult(this.level, mesh, input);
				out.forEach(e -> this.ejectItem(this.level, direction, e));

				this.inventory.setStackInSlot(0, ItemStack.EMPTY);
				this.setChanged();
			}
		}
	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		CompoundTag fluidTag = new CompoundTag();
		this.tank.writeToNBT(fluidTag);

		compound.put("Inventory", this.inventory.serializeNBT());
		compound.put("Fluid", fluidTag);
		compound.putInt("Processed", this.processed);
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
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
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
