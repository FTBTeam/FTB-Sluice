package dev.ftb.mods.sluice.block;

import static dev.ftb.mods.sluice.block.SluiceBlockEntity.*;

import dev.ftb.mods.sluice.SluiceMod;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class SluiceModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, SluiceMod.MOD_ID);

	public static final RegistryObject<BlockEntityType<OakSluiceBlockEntity>> OAK_SLUICE = REGISTRY.register("oak_sluice", () -> BlockEntityType.Builder.of(OakSluiceBlockEntity::new, SluiceModBlocks.OAK_SLUICE.get()).build(null));
	public static final RegistryObject<BlockEntityType<IronSluiceBlockEntity>> IRON_SLUICE = REGISTRY.register("iron_sluice", () -> BlockEntityType.Builder.of(IronSluiceBlockEntity::new, SluiceModBlocks.IRON_SLUICE.get()).build(null));
	public static final RegistryObject<BlockEntityType<DiamondSluiceBlockEntity>> DIAMOND_SLUICE = REGISTRY.register("diamond_sluice", () -> BlockEntityType.Builder.of(DiamondSluiceBlockEntity::new, SluiceModBlocks.DIAMOND_SLUICE.get()).build(null));
	public static final RegistryObject<BlockEntityType<NetheriteSluiceBlockEntity>> NETHERITE_SLUICE = REGISTRY.register("netherite_sluice", () -> BlockEntityType.Builder.of(NetheriteSluiceBlockEntity::new, SluiceModBlocks.NETHERITE_SLUICE.get()).build(null));

	public static final RegistryObject<BlockEntityType<TapBlockEntity>> TAP = REGISTRY.register("tap", () -> BlockEntityType.Builder.of(TapBlockEntity::new, SluiceModBlocks.TAP.get()).build(null));
	public static final RegistryObject<BlockEntityType<TankBlockEntity>> TANK = REGISTRY.register("tank", () -> BlockEntityType.Builder.of(TankBlockEntity::new, SluiceModBlocks.TANK.get()).build(null));
	public static final RegistryObject<BlockEntityType<TankBlockEntity.CreativeTankBlockEntity>> CREATIVE_TANK = REGISTRY.register("creative_tank", () -> BlockEntityType.Builder.of(TankBlockEntity.CreativeTankBlockEntity::new, SluiceModBlocks.TANK_CREATIVE.get()).build(null));
}
