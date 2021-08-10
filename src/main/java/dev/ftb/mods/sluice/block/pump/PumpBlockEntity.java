package dev.ftb.mods.sluice.block.pump;

import dev.ftb.mods.sluice.block.SluiceBlockEntities;
import dev.ftb.mods.sluice.block.sluice.SluiceBlockEntity;
import dev.ftb.mods.sluice.capabilities.FluidCap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class PumpBlockEntity extends BlockEntity implements TickableBlockEntity {
    public int timeLeft = 0;

    private final int checkInterval = 50;
    private int checkTimeout = 0;
    private boolean foundValidBlock = false;
    private int lastTick = 0;
    boolean creative = false;
    Fluid creativeFluid = Fluids.WATER;

    private BlockPos targetPos = null;

    public PumpBlockEntity() {
        super(SluiceBlockEntities.PUMP.get());
    }

    @Override
    public void tick() {
        if (this.level == null || (this.timeLeft <= 0 && !this.creative)) {
            return;
        }

        // Just do it
        if (this.creative) {
            this.getTargetPos().ifPresent(e -> {
                BlockEntity blockEntity = level.getBlockEntity(e);
                if (blockEntity == null) {
                    return;
                }

                this.provideFluidToSluice(blockEntity);
            });

            // Don't do anything else, creative means creative
            return;
        }

        FluidState fluidState = level.getBlockState(this.getBlockPos().below()).getFluidState();

        // No valid fluid source
        if (fluidState.isEmpty() || !fluidState.isSource() || !fluidState.is(FluidTags.WATER)) {
            return;
        }

        // Try and find a valid block;
        if (!foundValidBlock) {
            // Time to check
            if (checkTimeout > checkInterval) {
                for (Direction direction : Direction.values()) {
                    BlockEntity blockEntity = level.getBlockEntity(this.getBlockPos().relative(direction));
                    if (blockEntity instanceof SluiceBlockEntity) {
                        this.targetPos = this.getBlockPos().relative(direction);
                        this.foundValidBlock = true;
                    }
                }

                checkTimeout = 0;
            }

            checkTimeout ++;
            return;
        }

        this.lastTick ++;
        if (this.lastTick < 20) {
            return;
        }

        this.lastTick = 0;

        this.getTargetPos().ifPresent(e -> {
            BlockEntity blockEntity = level.getBlockEntity(e);
            if (blockEntity == null) {
                return;
            }

            this.provideFluidToSluice(blockEntity);

            this.timeLeft -= 20;
            if (this.timeLeft < 0) {
                this.timeLeft = 0;
            }

            blockEntity.setChanged();
        });
    }

    private void provideFluidToSluice(BlockEntity blockEntity) {
        // It's gone! Poof
        if (!(blockEntity instanceof SluiceBlockEntity)) {
            this.foundValidBlock = false;
            this.targetPos = null;
            return;
        }

        // Give it water!
        FluidCap tank = ((SluiceBlockEntity) blockEntity).tank;
        if (tank.getFluidAmount() < tank.getCapacity()) {
            tank.internalFill(new FluidStack(this.creative ? this.creativeFluid : Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    public Optional<BlockPos> getTargetPos() {
        return Optional.ofNullable(targetPos);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putInt("time_left", this.timeLeft);
        compound.putBoolean("is_creative", this.creative);

        if (this.creativeFluid != Fluids.WATER) {
            compound.putString("creative_fluid", Objects.requireNonNull(this.creativeFluid.getRegistryName()).toString());
        }

        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundTag compound) {
        this.timeLeft = compound.getInt("time_left");
        this.creative = compound.getBoolean("is_creative");

        if (compound.contains("creative_fluid")) {
            Fluid creativeFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(compound.getString("creative_fluid")));
            this.creativeFluid = creativeFluid == null ? Fluids.WATER : creativeFluid;
        }

        super.load(state, compound);
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
        return new ClientboundBlockEntityDataPacket(this.getBlockPos(), 0, this.save(new CompoundTag()));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(this.getBlockState(), pkt.getTag());
    }
}
