package dev.ftb.mods.sluice.block.autohammer;

import dev.ftb.mods.sluice.block.SluiceBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AutoHammerBlockEntity extends BlockEntity {
    public AutoHammerBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
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
    }

    public static class Diamond extends AutoHammerBlockEntity {
        public Diamond() {
            super(SluiceBlockEntities.DIAMOND_AUTO_HAMMER.get());
        }
    }

    public static class Netherite extends AutoHammerBlockEntity {
        public Netherite() {
            super(SluiceBlockEntities.NETHERITE_AUTO_HAMMER.get());
        }
    }
}
