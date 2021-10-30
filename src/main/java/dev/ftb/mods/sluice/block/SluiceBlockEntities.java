package dev.ftb.mods.sluice.block;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.block.autohammer.AutoHammerBlock;
import dev.ftb.mods.sluice.block.autohammer.AutoHammerBlockEntity;
import dev.ftb.mods.sluice.block.pump.PumpBlockEntity;
import dev.ftb.mods.sluice.item.SluiceModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static dev.ftb.mods.sluice.block.sluice.SluiceBlockEntity.*;


public class SluiceBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, FTBSluice.MOD_ID);

    public static final RegistryObject<BlockEntityType<OakSluiceBlockEntity>> OAK_SLUICE = REGISTRY.register("oak_sluice", () -> BlockEntityType.Builder.of(OakSluiceBlockEntity::new, SluiceBlocks.OAK_SLUICE.get()).build(null));
    public static final RegistryObject<BlockEntityType<IronSluiceBlockEntity>> IRON_SLUICE = REGISTRY.register("iron_sluice", () -> BlockEntityType.Builder.of(IronSluiceBlockEntity::new, SluiceBlocks.IRON_SLUICE.get()).build(null));
    public static final RegistryObject<BlockEntityType<DiamondSluiceBlockEntity>> DIAMOND_SLUICE = REGISTRY.register("diamond_sluice", () -> BlockEntityType.Builder.of(DiamondSluiceBlockEntity::new, SluiceBlocks.DIAMOND_SLUICE.get()).build(null));
    public static final RegistryObject<BlockEntityType<NetheriteSluiceBlockEntity>> NETHERITE_SLUICE = REGISTRY.register("netherite_sluice", () -> BlockEntityType.Builder.of(NetheriteSluiceBlockEntity::new, SluiceBlocks.NETHERITE_SLUICE.get()).build(null));
    public static final RegistryObject<BlockEntityType<EmpoweredSluiceBlockEntity>> EMPOWERED_SLUICE = REGISTRY.register("empowered_sluice", () -> BlockEntityType.Builder.of(EmpoweredSluiceBlockEntity::new, SluiceBlocks.NETHERITE_SLUICE.get()).build(null));

    public static final RegistryObject<BlockEntityType<PumpBlockEntity>> PUMP = REGISTRY.register("pump", () -> BlockEntityType.Builder.of(PumpBlockEntity::new, SluiceBlocks.PUMP.get()).build(null));

    public static final RegistryObject<BlockEntityType<AutoHammerBlockEntity.Iron>> IRON_AUTO_HAMMER = REGISTRY.register("iron_auto_hammer", () -> BlockEntityType.Builder.of(AutoHammerBlockEntity.Iron::new, SluiceBlocks.IRON_AUTO_HAMMER.get()).build(null));
    public static final RegistryObject<BlockEntityType<AutoHammerBlockEntity.Gold>> GOLD_AUTO_HAMMER = REGISTRY.register("gold_auto_hammer", () -> BlockEntityType.Builder.of(AutoHammerBlockEntity.Gold::new, SluiceBlocks.GOLD_AUTO_HAMMER.get()).build(null));
    public static final RegistryObject<BlockEntityType<AutoHammerBlockEntity.Diamond>> DIAMOND_AUTO_HAMMER = REGISTRY.register("diamond_auto_hammer", () -> BlockEntityType.Builder.of(AutoHammerBlockEntity.Diamond::new, SluiceBlocks.DIAMOND_AUTO_HAMMER.get()).build(null));
    public static final RegistryObject<BlockEntityType<AutoHammerBlockEntity.Netherite>> NETHERITE_AUTO_HAMMER = REGISTRY.register("netherite_auto_hammer", () -> BlockEntityType.Builder.of(AutoHammerBlockEntity.Netherite::new, SluiceBlocks.NETHERITE_AUTO_HAMMER.get()).build(null));

//    public static final RegistryObject<BlockEntityType<TankBlockEntity>> TANK = REGISTRY.register("tank", () -> BlockEntityType.Builder.of(TankBlockEntity::new, SluiceBlocks.TANK.get()).build(null));
//    public static final RegistryObject<BlockEntityType<TankBlockEntity.CreativeTankBlockEntity>> CREATIVE_TANK = REGISTRY.register("creative_tank", () -> BlockEntityType.Builder.of(TankBlockEntity.CreativeTankBlockEntity::new, SluiceBlocks.TANK_CREATIVE.get()).build(null));
}
