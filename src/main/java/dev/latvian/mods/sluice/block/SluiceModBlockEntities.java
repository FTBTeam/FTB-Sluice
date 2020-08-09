package dev.latvian.mods.sluice.block;

import dev.latvian.mods.sluice.SluiceMod;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author LatvianModder
 */
public class SluiceModBlockEntities
{
	public static final DeferredRegister<TileEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, SluiceMod.MOD_ID);

	public static final RegistryObject<TileEntityType<SluiceBlockEntity>> SLUICE = REGISTRY.register("sluice", () -> TileEntityType.Builder.create(SluiceBlockEntity::new, SluiceModBlocks.SLUICE.get()).build(null));
}