package dev.latvian.mods.sluice.block;

import dev.latvian.mods.sluice.SluiceMod;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author LatvianModder
 */
public class SluiceModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, SluiceMod.MOD_ID);

	public static final RegistryObject<BlockEntityType<SluiceBlockEntity>> SLUICE = REGISTRY.register("sluice", () -> BlockEntityType.Builder.of(SluiceBlockEntity::new,
			SluiceModBlocks.OAK_SLUICE.get(),
			SluiceModBlocks.IRON_SLUICE.get(),
			SluiceModBlocks.DIAMOND_SLUICE.get(),
			SluiceModBlocks.NETHERITE_SLUICE.get()
	).build(null));

	public static final RegistryObject<BlockEntityType<TapBlockEntity>> TAP = REGISTRY.register("tap", () -> BlockEntityType.Builder.of(TapBlockEntity::new, SluiceModBlocks.TAP.get()).build(null));
}
