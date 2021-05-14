package dev.ftb.mods.sluice.block;

import dev.ftb.mods.sluice.recipe.SluiceModRecipeSerializers;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
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
			setChanged();
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

	public final LazyOptional<ItemStackHandler> inventoryOptional = LazyOptional.of(() -> inventory);
	public final LazyOptional<FluidTank> fluidOptional = LazyOptional.of(() -> tank);
	public int cooldown;

	public SluiceBlockEntity() {
		super(SluiceModBlockEntities.SLUICE.get());
	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		CompoundTag fluidTag = new CompoundTag();
		tank.writeToNBT(fluidTag);

		compound.put("Inventory", inventory.serializeNBT());
		compound.put("Fluid", fluidTag);
		compound.putInt("Cooldown", cooldown);
		return super.save(compound);
	}

	@Override
	public void load(BlockState state, CompoundTag compound) {
		inventory.deserializeNBT(compound.getCompound("Inventory"));
		cooldown = compound.getInt("Cooldown");
		if (compound.contains("Fluid")) {
			tank.readFromNBT(compound.getCompound("Fluid"));
		}
		super.load(state, compound);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return inventoryOptional.cast();
		}

		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return fluidOptional.cast();
		}

		return super.getCapability(cap, side);
	}

	@Override
	public void tick() {
		if (level == null || level.isClientSide()) {
			return;
		}

		if (cooldown > 0) {
			cooldown--;
			return;
		}

		BlockState state = getBlockState();

		if (!(state.getBlock() instanceof SluiceBlock)) {
			return;
		}

		Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		MeshType mesh = state.getValue(SluiceBlock.MESH);

		ItemStack input = inventory.getStackInSlot(0);
		if (input.isEmpty()) {
			return;
		}

		List<ItemStack> out = SluiceModRecipeSerializers.getRandomResult(level, mesh, input);
		out.forEach(e -> ejectItem(level, direction, e));

		inventory.setStackInSlot(0, ItemStack.EMPTY);
		cooldown = 100;
		setChanged();
//		if (out.weight == 0) {
//			ejectItem(level, direction, input);
//			inventory.setStackInSlot(0, ItemStack.EMPTY);
//			setChanged();
//			return;
//		}
//
//		ejectItem(level, direction, ItemHandlerHelper.copyStackWithSize(out.item, 1));
//		input.shrink(1);
//		cooldown = out.weight;
//		setChanged();
	}

	private void ejectItem(Level w, Direction direction, ItemStack stack) {
		System.out.println("Dropping " + stack);
		BlockEntity tileEntity = w.getBlockEntity(worldPosition.relative(direction));
		IItemHandler handler = tileEntity == null ? null : tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).orElse(null);

		if (handler != null) {
			stack = ItemHandlerHelper.insertItem(handler, stack, false);
		}

		if (!stack.isEmpty()) {
			double x = worldPosition.getX() + 0.5D + direction.getStepX() * 0.7D;
			double y = worldPosition.getY() + 0.5D;
			double z = worldPosition.getZ() + 0.5D + direction.getStepZ() * 0.7D;
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
}
