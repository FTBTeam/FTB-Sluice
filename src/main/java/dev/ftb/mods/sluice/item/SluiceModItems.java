package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.block.MeshType;
import dev.ftb.mods.sluice.block.SluiceBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class SluiceModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, FTBSluice.MOD_ID);

    public static final RegistryObject<Item> CLOTH_MESH = REGISTRY.register("cloth_mesh", () -> new MeshItem(MeshType.CLOTH));
    public static final RegistryObject<Item> IRON_MESH = REGISTRY.register("iron_mesh", () -> new MeshItem(MeshType.IRON));
    public static final RegistryObject<Item> GOLD_MESH = REGISTRY.register("gold_mesh", () -> new MeshItem(MeshType.GOLD));
    public static final RegistryObject<Item> DIAMOND_MESH = REGISTRY.register("diamond_mesh", () -> new MeshItem(MeshType.DIAMOND));
    public static final RegistryObject<Item> BLAZING_MESH = REGISTRY.register("blazing_mesh", () -> new MeshItem(MeshType.BLAZING));

    public static final RegistryObject<Item> DAMAGED_CANTEEN = REGISTRY.register("damaged_canteen", () -> new CanteenItem(false));
//    public static final RegistryObject<Item> CANTEEN = REGISTRY.register("canteen", () -> new CanteenItem(true));

//    public static final RegistryObject<Item> TANK = REGISTRY.register("tank", () -> new BlockItem(SluiceBlocks.TANK.get(), new Item.Properties().tab(FTBSluice.group)));
//    public static final RegistryObject<Item> TANK_CREATIVE = REGISTRY.register("creative_tank", () -> new BlockItem(SluiceBlocks.TANK_CREATIVE.get(), new Item.Properties().tab(FTBSluice.group)));

    public static final RegistryObject<BlockItem> OAK_SLUICE = REGISTRY.register("oak_sluice", () -> new BlockItem(SluiceBlocks.OAK_SLUICE.get(), new Item.Properties().tab(FTBSluice.group)));
    public static final RegistryObject<BlockItem> IRON_SLUICE = REGISTRY.register("iron_sluice", () -> new BlockItem(SluiceBlocks.IRON_SLUICE.get(), new Item.Properties().tab(FTBSluice.group)));
    public static final RegistryObject<BlockItem> DIAMOND_SLUICE = REGISTRY.register("diamond_sluice", () -> new BlockItem(SluiceBlocks.DIAMOND_SLUICE.get(), new Item.Properties().tab(FTBSluice.group)));
    public static final RegistryObject<BlockItem> NETHERITE_SLUICE = REGISTRY.register("netherite_sluice", () -> new BlockItem(SluiceBlocks.NETHERITE_SLUICE.get(), new Item.Properties().tab(FTBSluice.group)));
    public static final RegistryObject<BlockItem> EMPOWERED_SLUICE = REGISTRY.register("empowered_sluice", () -> new BlockItem(SluiceBlocks.EMPOWERED_SLUICE.get(), new Item.Properties().tab(FTBSluice.group)));

    public static final RegistryObject<BlockItem> PUMP = REGISTRY.register("pump", () -> new BlockItem(SluiceBlocks.PUMP.get(), new Item.Properties().tab(FTBSluice.group)));

    public static final RegistryObject<BlockItem> IRON_AUTO_HAMMER = REGISTRY.register("iron_auto_hammer", () -> new BlockItem(SluiceBlocks.IRON_AUTO_HAMMER.get(), new Item.Properties().tab(FTBSluice.group)));
    public static final RegistryObject<BlockItem> GOLD_AUTO_HAMMER = REGISTRY.register("gold_auto_hammer", () -> new BlockItem(SluiceBlocks.GOLD_AUTO_HAMMER.get(), new Item.Properties().tab(FTBSluice.group)));
    public static final RegistryObject<BlockItem> DIAMOND_AUTO_HAMMER = REGISTRY.register("diamond_auto_hammer", () -> new BlockItem(SluiceBlocks.DIAMOND_AUTO_HAMMER.get(), new Item.Properties().tab(FTBSluice.group)));
    public static final RegistryObject<BlockItem> NETHERITE_AUTO_HAMMER = REGISTRY.register("netherite_auto_hammer", () -> new BlockItem(SluiceBlocks.NETHERITE_AUTO_HAMMER.get(), new Item.Properties().tab(FTBSluice.group)));

    // Upgrades
    public static final RegistryObject<Item> FORTUNE_UPGRADE = REGISTRY.register("sluice_fortune_upgrade", () -> new UpgradeItem(Upgrades.LUCK));
    public static final RegistryObject<Item> SPEED_UPGRADE = REGISTRY.register("sluice_speed_upgrade", () -> new UpgradeItem(Upgrades.SPEED));
    public static final RegistryObject<Item> CONSUMPTION_UPGRADE = REGISTRY.register("sluice_consumption_upgrade", () -> new UpgradeItem(Upgrades.CONSUMPTION));

    // MISC
    public static final RegistryObject<Item> DUST = REGISTRY.register("dust", () -> new BlockItem(SluiceBlocks.DUST_BLOCK.get(), new Item.Properties().tab(FTBSluice.group)));
    public static final RegistryObject<Item> CRUSHED_NETHERRACK = REGISTRY.register("crushed_netherrack", () -> new BlockItem(SluiceBlocks.CRUSHED_NETHERRACK.get(), new Item.Properties().tab(FTBSluice.group)));
    public static final RegistryObject<Item> CRUSHED_BASALT = REGISTRY.register("crushed_basalt", () -> new BlockItem(SluiceBlocks.CRUSHED_BASALT.get(), new Item.Properties().tab(FTBSluice.group)));
    public static final RegistryObject<Item> CRUSHED_ENDSTONE = REGISTRY.register("crushed_endstone", () -> new BlockItem(SluiceBlocks.CRUSHED_ENDSTONE.get(), new Item.Properties().tab(FTBSluice.group)));

    // Hammers
    public static final RegistryObject<Item> WOODEN_HAMMER = REGISTRY.register("wooden_hammer", () -> new HammerItem(Tiers.WOOD, 1, -2.8F));
    public static final RegistryObject<Item> STONE_HAMMER = REGISTRY.register("stone_hammer", () -> new HammerItem(Tiers.STONE, 1, -2.8F));
    public static final RegistryObject<Item> IRON_HAMMER = REGISTRY.register("iron_hammer", () -> new HammerItem(Tiers.IRON, 1, -2.8F));
    public static final RegistryObject<Item> GOLD_HAMMER = REGISTRY.register("gold_hammer", () -> new HammerItem(Tiers.GOLD, 1, -2.8F));
    public static final RegistryObject<Item> DIAMOND_HAMMER = REGISTRY.register("diamond_hammer", () -> new HammerItem(Tiers.DIAMOND, 1, -2.8F));
    public static final RegistryObject<Item> NETHERITE_HAMMER = REGISTRY.register("netherite_hammer", () -> new HammerItem(Tiers.NETHERITE, 1, -2.8F, true));

    public static final RegistryObject<Item> CLAY_BUCKET = REGISTRY.register("clay_bucket", () -> new ClayBucket(() -> Fluids.EMPTY));
    public static final RegistryObject<Item> CLAY_WATER_BUCKET = REGISTRY.register("clay_water_bucket", () -> new ClayBucket(() -> Fluids.WATER));
}
