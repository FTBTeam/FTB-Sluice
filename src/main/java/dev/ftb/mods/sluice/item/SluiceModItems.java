package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.SluiceMod;
import dev.ftb.mods.sluice.block.MeshType;
import dev.ftb.mods.sluice.block.SluiceModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class SluiceModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SluiceMod.MOD_ID);

	public static final RegistryObject<Item> CLOTH_MESH = REGISTRY.register("cloth_mesh", () -> new MeshItem(MeshType.CLOTH));
	public static final RegistryObject<Item> IRON_MESH = REGISTRY.register("iron_mesh", () -> new MeshItem(MeshType.IRON));
	public static final RegistryObject<Item> GOLD_MESH = REGISTRY.register("gold_mesh", () -> new MeshItem(MeshType.GOLD));
	public static final RegistryObject<Item> DIAMOND_MESH = REGISTRY.register("diamond_mesh", () -> new MeshItem(MeshType.DIAMOND));

	public static final RegistryObject<Item> TANK = REGISTRY.register("tank", () -> new BlockItem(SluiceModBlocks.TANK.get(), new Item.Properties().tab(SluiceMod.group)));
	public static final RegistryObject<Item> TANK_CREATIVE = REGISTRY.register("creative_tank", () -> new BlockItem(SluiceModBlocks.TANK_CREATIVE.get(), new Item.Properties().tab(SluiceMod.group)));
	public static final RegistryObject<Item> TAP = REGISTRY.register("tap", () -> new BlockItem(SluiceModBlocks.TAP.get(), new Item.Properties().tab(SluiceMod.group)));

	public static final RegistryObject<BlockItem> OAK_SLUICE = REGISTRY.register("oak_sluice", () -> new BlockItem(SluiceModBlocks.OAK_SLUICE.get(), new Item.Properties().tab(SluiceMod.group)));
	public static final RegistryObject<BlockItem> IRON_SLUICE = REGISTRY.register("iron_sluice", () -> new BlockItem(SluiceModBlocks.IRON_SLUICE.get(), new Item.Properties().tab(SluiceMod.group)));
	public static final RegistryObject<BlockItem> DIAMOND_SLUICE = REGISTRY.register("diamond_sluice", () -> new BlockItem(SluiceModBlocks.DIAMOND_SLUICE.get(), new Item.Properties().tab(SluiceMod.group)));
	public static final RegistryObject<BlockItem> NETHERITE_SLUICE = REGISTRY.register("netherite_sluice", () -> new BlockItem(SluiceModBlocks.NETHERITE_SLUICE.get(), new Item.Properties().tab(SluiceMod.group)));

	// MISC
	public static final RegistryObject<Item> DUST = REGISTRY.register("dust", () -> new BlockItem(SluiceModBlocks.DUST_BLOCK.get(), new Item.Properties().tab(SluiceMod.group)));

	// Hammers
	public static final RegistryObject<Item> WOODEN_HAMMER = REGISTRY.register("wooden_hammer", () -> new HammerItem());
	public static final RegistryObject<Item> STONE_HAMMER = REGISTRY.register("stone_hammer", () -> new HammerItem());
	public static final RegistryObject<Item> IRON_HAMMER = REGISTRY.register("iron_hammer", () -> new HammerItem());
	public static final RegistryObject<Item> GOLD_HAMMER = REGISTRY.register("gold_hammer", () -> new HammerItem());
	public static final RegistryObject<Item> DIAMOND_HAMMER = REGISTRY.register("diamond_hammer", () -> new HammerItem());

	public static final RegistryObject<Item> CLAY_BUCKET = REGISTRY.register("clay_bucket", ClayBucket::new);
}
