package dev.latvian.mods.sluice.block;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class TankBlockEntity extends BlockEntity {
    private final LazyOptional<FluidTank> fluidTank = LazyOptional.of(() -> new FluidTank(5000, stack -> true));

    public TankBlockEntity() {
        super(SluiceModBlockEntities.TANK.get());
    }

    @Override
    public void load(BlockState state, CompoundTag nbt) {
        super.load(state, nbt);
        fluidTank.ifPresent(e -> e.readFromNBT(nbt));
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CompoundTag save = super.save(compound);
        fluidTank.ifPresent(e -> e.writeToNBT(save));
        return save;
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        return new ServerboundBlockEntityTagQuery(getBlockPos(), )
    }
}
