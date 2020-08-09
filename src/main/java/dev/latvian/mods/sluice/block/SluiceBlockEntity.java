package dev.latvian.mods.sluice.block;

import dev.latvian.mods.sluice.recipe.SluiceModRecipeSerializers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class SluiceBlockEntity extends TileEntity implements ITickableTileEntity
{
	public final ItemStackHandler inventory = new ItemStackHandler(1)
	{
		@Override
		protected void onContentsChanged(int slot)
		{
			markDirty();
		}
	};

	public final LazyOptional<ItemStackHandler> inventoryOptional = LazyOptional.of(() -> inventory);
	public int cooldown;

	public SluiceBlockEntity()
	{
		super(SluiceModBlockEntities.SLUICE.get());
	}

	@Override
	public CompoundNBT write(CompoundNBT compound)
	{
		compound.put("Inventory", inventory.serializeNBT());
		compound.putInt("Cooldown", cooldown);
		return super.write(compound);
	}

	@Override
	public void read(BlockState state, CompoundNBT compound)
	{
		inventory.deserializeNBT(compound.getCompound("Inventory"));
		cooldown = compound.getInt("Cooldown");
		super.read(state, compound);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
	{
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? inventoryOptional.cast() : super.getCapability(cap, side);
	}

	@Override
	public void tick()
	{
		if (world == null || world.isRemote())
		{
			return;
		}

		if (cooldown > 0)
		{
			cooldown--;
			return;
		}

		BlockState state = getBlockState();

		if (state.getBlock() != SluiceModBlocks.SLUICE.get())
		{
			return;
		}

		Direction direction = state.get(BlockStateProperties.HORIZONTAL_FACING);
		MeshType mesh = state.get(SluiceBlock.MESH);

		ItemStack input = inventory.getStackInSlot(0);

		if (input.isEmpty())
		{
			return;
		}

		Pair<ItemStack, Integer> out = SluiceModRecipeSerializers.getRandomResult(world, mesh, input);

		if (out.getRight() == 0)
		{
			ejectItem(world, direction, input);
			inventory.setStackInSlot(0, ItemStack.EMPTY);
			markDirty();
			return;
		}

		ejectItem(world, direction, ItemHandlerHelper.copyStackWithSize(out.getLeft(), 1));
		input.shrink(1);
		cooldown = out.getRight();
		markDirty();
	}

	private void ejectItem(World w, Direction direction, ItemStack stack)
	{
		TileEntity tileEntity = w.getTileEntity(pos.offset(direction));
		IItemHandler handler = tileEntity == null ? null : tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).orElse(null);

		if (handler != null)
		{
			stack = ItemHandlerHelper.insertItem(handler, stack, false);
		}

		if (!stack.isEmpty())
		{
			double x = pos.getX() + 0.5D + direction.getXOffset() * 0.7D;
			double y = pos.getY() + 0.5D;
			double z = pos.getZ() + 0.5D + direction.getZOffset() * 0.7D;
			double in = w.rand.nextFloat() * 0.1D;
			double mx = direction.getXOffset() * in;
			double my = 0.14D;
			double mz = direction.getZOffset() * in;

			ItemEntity itemEntity = new ItemEntity(w, x, y, z, stack);
			itemEntity.setNoPickupDelay();
			itemEntity.setMotion(mx, my, mz);
			w.addEntity(itemEntity);
		}
	}
}
