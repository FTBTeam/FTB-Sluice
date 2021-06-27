//package dev.ftb.mods.sluice.block;
//
//import net.minecraft.core.Direction;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.util.LazyOptional;
//import net.minecraftforge.fluids.FluidStack;
//import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
//import net.minecraftforge.fluids.capability.templates.FluidTank;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//
//public class TankBlockEntity extends BlockEntity {
//    public final FluidTank tank = new FluidTank(10000);
//    public final LazyOptional<FluidTank> fluidInventory = LazyOptional.of(() -> this.tank);
//
//    public TankBlockEntity() {
//        super(SluiceBlockEntities.TANK.get());
//    }
//
//    @Nonnull
//    @Override
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
//        return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? this.fluidInventory.cast() : super.getCapability(cap);
//    }
//
//    @Override
//    public CompoundTag save(CompoundTag compound) {
//        CompoundTag fluidTag = new CompoundTag();
//        this.tank.writeToNBT(fluidTag);
//        compound.put("Fluid", fluidTag);
//        return super.save(compound);
//    }
//
//    @Override
//    public void load(BlockState state, CompoundTag compound) {
//        super.load(state, compound);
//
//        if (compound.contains("Fluid")) {
//            this.tank.readFromNBT(compound.getCompound("Fluid"));
//        }
//    }
//
//    public static class CreativeTankBlockEntity extends BlockEntity {
//        public final FluidTank tank = new FluidTank(Integer.MAX_VALUE) {
//            @Override
//            public int fill(FluidStack resource, FluidAction action) {
//                if (action.simulate()) {
//                    return super.fill(resource, action);
//                }
//
//                int fill = super.fill(resource, action);
//                if (fill > 0) {
//                    this.fluid.setAmount(Integer.MAX_VALUE);
//                    this.onContentsChanged();
//                    return Integer.MAX_VALUE;
//                }
//
//                return fill;
//            }
//
//            @Nonnull
//            @Override
//            public FluidStack drain(int maxDrain, FluidAction action) {
//                return new FluidStack(this.fluid, Integer.MAX_VALUE);
//            }
//        };
//        public final LazyOptional<FluidTank> fluidInventory = LazyOptional.of(() -> this.tank);
//
//        public CreativeTankBlockEntity() {
//            super(SluiceBlockEntities.CREATIVE_TANK.get());
//        }
//
//        @Override
//        public CompoundTag save(CompoundTag compound) {
//            CompoundTag fluidTag = new CompoundTag();
//            this.tank.writeToNBT(fluidTag);
//            compound.put("Fluid", fluidTag);
//            return super.save(compound);
//        }
//
//        @Override
//        public void load(BlockState state, CompoundTag compound) {
//            super.load(state, compound);
//
//            if (compound.contains("Fluid")) {
//                this.tank.readFromNBT(compound.getCompound("Fluid"));
//            }
//        }
//
//        @Nonnull
//        @Override
//        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
//            return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? this.fluidInventory.cast() : super.getCapability(cap);
//        }
//    }
//}
