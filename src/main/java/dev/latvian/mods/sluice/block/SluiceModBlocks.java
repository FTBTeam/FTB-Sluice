package dev.latvian.mods.sluice.block;

import dev.latvian.mods.sluice.SluiceMod;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author LatvianModder
 */
public class SluiceModBlocks
{
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, SluiceMod.MOD_ID);

	public static final RegistryObject<Block> SLUICE = REGISTRY.register("sluice", SluiceBlock::new);
}
