package dev.latvian.mods.sluice.item;

import dev.latvian.mods.sluice.SluiceMod;
import dev.latvian.mods.sluice.block.MeshType;
import dev.latvian.mods.sluice.block.SluiceModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author LatvianModder
 */
public class SluiceModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SluiceMod.MOD_ID);

	public static final RegistryObject<Item> CLOTH_MESH = REGISTRY.register("cloth_mesh", () -> new MeshItem(MeshType.CLOTH));
	public static final RegistryObject<Item> IRON_MESH = REGISTRY.register("iron_mesh", () -> new MeshItem(MeshType.IRON));
	public static final RegistryObject<Item> GOLD_MESH = REGISTRY.register("gold_mesh", () -> new MeshItem(MeshType.GOLD));
	public static final RegistryObject<Item> DIAMOND_MESH = REGISTRY.register("diamond_mesh", () -> new MeshItem(MeshType.DIAMOND));

	public static final RegistryObject<BlockItem> OAK_SLUICE = REGISTRY.register("oak_sluice", () -> new BlockItem(SluiceModBlocks.OAK_SLUICE.get(), new Item.Properties().tab(SluiceMod.group)));
	public static final RegistryObject<BlockItem> IRON_SLUICE = REGISTRY.register("iron_sluice", () -> new BlockItem(SluiceModBlocks.IRON_SLUICE.get(), new Item.Properties().tab(SluiceMod.group)));
	public static final RegistryObject<BlockItem> DIAMOND_SLUICE = REGISTRY.register("diamond_sluice", () -> new BlockItem(SluiceModBlocks.DIAMOND_SLUICE.get(), new Item.Properties().tab(SluiceMod.group)));
	public static final RegistryObject<BlockItem> NETHERITE_SLUICE = REGISTRY.register("netherite_sluice", () -> new BlockItem(SluiceModBlocks.NETHERITE_SLUICE.get(), new Item.Properties().tab(SluiceMod.group)));
}