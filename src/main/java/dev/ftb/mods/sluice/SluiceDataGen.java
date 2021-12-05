package dev.ftb.mods.sluice;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import dev.ftb.mods.sluice.block.MeshType;
import dev.ftb.mods.sluice.block.SluiceBlocks;
import dev.ftb.mods.sluice.block.autohammer.AutoHammerBlock;
import dev.ftb.mods.sluice.block.pump.PumpBlock;
import dev.ftb.mods.sluice.block.sluice.SluiceBlock;
import dev.ftb.mods.sluice.item.SluiceModItems;
import dev.ftb.mods.sluice.tags.SluiceTags;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeLootTableProvider;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;


@Mod.EventBusSubscriber(modid = FTBSluice.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SluiceDataGen {
    public static final String MODID = FTBSluice.MOD_ID;

    @SubscribeEvent
    public static void dataGenEvent(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            SMBlockModels blockModels = new SMBlockModels(gen, MODID, event.getExistingFileHelper());

            gen.addProvider(blockModels);
            gen.addProvider(new SMLang(gen, MODID, "en_us"));
            gen.addProvider(new SMItemModels(gen, MODID, event.getExistingFileHelper()));
            gen.addProvider(new SMBlockStateModels(gen, MODID, event.getExistingFileHelper(), blockModels));
        }

        if (event.includeServer()) {
            SMBlockTags blockTags = new SMBlockTags(gen, event.getExistingFileHelper());

            gen.addProvider(blockTags);
            gen.addProvider(new SMItemTags(gen, blockTags, event.getExistingFileHelper()));
            gen.addProvider(new SMRecipes(gen));
            gen.addProvider(new SMLootTableProvider(gen));
        }
    }

    private static class SMLang extends LanguageProvider {
        public SMLang(DataGenerator gen, String modid, String locale) {
            super(gen, modid, locale);
        }

        @Override
        protected void addTranslations() {
            this.add("itemGroup." + MODID, "Sluice");

            for (MeshType type : MeshType.REAL_VALUES) {
                this.addItem(type.meshItem, type.getSerializedName().substring(0, 1).toUpperCase() + type.getSerializedName().substring(1) + " Mesh");
            }

            for (Pair<Supplier<Block>, String> p : SluiceBlocks.ALL_SLUICES) {
                this.addBlock(p.getLeft(), p.getRight().substring(0, 1).toUpperCase() + p.getRight().substring(1) + " Sluice");
            }

            this.addBlock(SluiceBlocks.PUMP, "Manual pump");
            this.addBlock(SluiceBlocks.DUST_BLOCK, "Dust");
            this.addBlock(SluiceBlocks.CRUSHED_NETHERRACK, "Crushed Netherrack");
            this.addBlock(SluiceBlocks.CRUSHED_BASALT, "Crushed Basalt");
            this.addBlock(SluiceBlocks.CRUSHED_ENDSTONE, "Crushed Endstone");

            this.addBlock(SluiceBlocks.IRON_AUTO_HAMMER, "Iron Auto-hammer");
            this.addBlock(SluiceBlocks.GOLD_AUTO_HAMMER, "Gold Auto-hammer");
            this.addBlock(SluiceBlocks.DIAMOND_AUTO_HAMMER, "Diamond Auto-hammer");
            this.addBlock(SluiceBlocks.NETHERITE_AUTO_HAMMER, "Netherite Auto-hammer");

            this.addItem(SluiceModItems.DAMAGED_CANTEEN, "Damaged Canteen");
//            this.addItem(SluiceModItems.CANTEEN, "Canteen");
            this.addItem(SluiceModItems.CLAY_BUCKET, "Clay Bucket");
            this.addItem(SluiceModItems.CLAY_WATER_BUCKET, "Clay Water Bucket");
            this.addItem(SluiceModItems.WOODEN_HAMMER, "Wooden Hammer");
            this.addItem(SluiceModItems.STONE_HAMMER, "Stone Hammer");
            this.addItem(SluiceModItems.IRON_HAMMER, "Iron Hammer");
            this.addItem(SluiceModItems.GOLD_HAMMER, "Gold Hammer");
            this.addItem(SluiceModItems.DIAMOND_HAMMER, "Diamond Hammer");
            this.addItem(SluiceModItems.NETHERITE_HAMMER, "Netherite Hammer");
            this.addItem(SluiceModItems.FORTUNE_UPGRADE, "Fortune Upgrade");
            this.addItem(SluiceModItems.CONSUMPTION_UPGRADE, "Consumption Upgrade");
            this.addItem(SluiceModItems.SPEED_UPGRADE, "Speed Upgrade");

            this.add("death.attack.static_electric", "%1$s was killed by static electricity!");

            this.add("fluid.ftbsluice.lava", "Lava");
            this.add("fluid.ftbsluice.water", "Water");

            this.add("ftbsluice.tooltip.oak_sluice", "Rather basic, but it gets the job done.");
            this.add("ftbsluice.tooltip.iron_sluice", "A bit on the slow side still, but it seems to use a lot less fluid than before.");
            this.add("ftbsluice.tooltip.diamond_sluice", "Significantly faster than the iron one, but also a bit less fluid-efficient.");
            this.add("ftbsluice.tooltip.netherite_sluice", "Forged from Netherite, this sluice proves itself to be both efficient and modular.");
            this.add("ftbsluice.tooltip.empowered_sluice", "A lot like the netherite sluice but quicker, holds more fluid and can accept the blazing mesh!");

            this.add("ftbsluice.tooltip.upgrade_fortune", "Increases drop chance by 3% per upgrade");
            this.add("ftbsluice.tooltip.upgrade_speed", "Increases the speed of the sluice by 5% per upgrade");
            this.add("ftbsluice.tooltip.upgrade_fluid", "Reduces the fluid cost by 5% per upgrade.");
            this.add("ftbsluice.tooltip.upgrade_meta", "Each upgrade increase the power cost exponentially: base cost + (%s ^ upgrades)");

            this.add("ftbsluice.tooltip.damaged_canteen", "Hmm, looks broken, maybe good for emptying my sluice?");
            this.add("ftbsluice.tooltip.canteen", "Can store multiple fluids at once up to 16MB worth of each");

            this.add("ftbsluice.power_cost", "Cost: %s");

            this.add(MODID + ".jei.processingTime", "Processing Time: %s ticks");
            this.add(MODID + ".jei.fluidUsage", "Uses %smB of Fluid");
            this.add(MODID + ".properties.processing_time", "Processing Time: %sx");
            this.add(MODID + ".properties.fluid_usage", "Fluid Usage Multiplier: %sx");
            this.add(MODID + ".properties.tank", "Can hold %s mB of Fluid");
            this.add(MODID + ".properties.auto", "Allows automation of: %s | %s");
            this.add(MODID + ".properties.auto.item", "Items");
            this.add(MODID + ".properties.auto.fluid", "Fluids");
            this.add(MODID + ".properties.upgradeable", "Can be upgraded to further increase efficiency; requires RF to function");
            this.add(MODID + ".block.sluice.warning.wrong_sluice", "You can not use the Blazing Mesh on this Sluice");
        }
    }

    private static class SMBlockStateModels extends BlockStateProvider {
        private final SMBlockModels blockModels;

        public SMBlockStateModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper, SMBlockModels bm) {
            super(generator, modid, existingFileHelper);
            this.blockModels = bm;
        }

        @Override
        public BlockModelProvider models() {
            return this.blockModels;
        }

        @Override
        protected void registerStatesAndModels() {
            Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
            int[] dirsRot = {0, 180, 270, 90};

            for (Pair<Supplier<Block>, String> p : SluiceBlocks.ALL_SLUICES) {
                MultiPartBlockStateBuilder builder = this.getMultipartBuilder(p.getLeft().get());

                for (int d = 0; d < 4; d++) {
                    builder.part().modelFile(this.models().getExistingFile(this.modLoc("block/" + p.getRight() + "_sluice_body"))).rotationY(dirsRot[d]).addModel().condition(HORIZONTAL_FACING, dirs[d]).condition(SluiceBlock.PART, SluiceBlock.Part.MAIN);
                    builder.part().modelFile(this.models().getExistingFile(this.modLoc("block/" + p.getRight() + "_sluice_front"))).rotationY(dirsRot[d]).addModel().condition(HORIZONTAL_FACING, dirs[d]).condition(SluiceBlock.PART, SluiceBlock.Part.FUNNEL);

                    for (MeshType type : MeshType.REAL_VALUES) {
                        // Don't create models for the blazing mesh on non-empowered sluices
                        if (p.getKey() != SluiceBlocks.EMPOWERED_SLUICE && type == MeshType.BLAZING) {
                            continue;
                        }

                        builder.part().modelFile(this.models().getExistingFile(this.modLoc("block/" + type.getSerializedName() + "_mesh"))).rotationY(dirsRot[d]).addModel().condition(SluiceBlock.MESH, type).condition(HORIZONTAL_FACING, dirs[d]).condition(SluiceBlock.PART, SluiceBlock.Part.MAIN);
                    }
                }
            }

            int[] dirsRot2 = new int[]{90, 270, 0, 180};
            MultiPartBlockStateBuilder builder = this.getMultipartBuilder(SluiceBlocks.PUMP.get());
            for (int d = 0; d < 4; d++) {
                builder.part().modelFile(this.models().getExistingFile(this.modLoc("block/pump_off"))).rotationY(dirsRot2[d]).addModel().condition(PumpBlock.ON_OFF, false).condition(HORIZONTAL_FACING, dirs[d]);
                builder.part().modelFile(this.models().getExistingFile(this.modLoc("block/pump_on"))).rotationY(dirsRot2[d]).addModel().condition(PumpBlock.ON_OFF, true).condition(HORIZONTAL_FACING, dirs[d]);
                builder.part().modelFile(this.models().getExistingFile(this.modLoc("block/pump_20"))).rotationY(dirsRot2[d]).addModel().condition(PumpBlock.ON_OFF, true).condition(HORIZONTAL_FACING, dirs[d]).condition(PumpBlock.PROGRESS, PumpBlock.Progress.TWENTY);
                builder.part().modelFile(this.models().getExistingFile(this.modLoc("block/pump_40"))).rotationY(dirsRot2[d]).addModel().condition(PumpBlock.ON_OFF, true).condition(HORIZONTAL_FACING, dirs[d]).condition(PumpBlock.PROGRESS, PumpBlock.Progress.FORTY);
                builder.part().modelFile(this.models().getExistingFile(this.modLoc("block/pump_60"))).rotationY(dirsRot2[d]).addModel().condition(PumpBlock.ON_OFF, true).condition(HORIZONTAL_FACING, dirs[d]).condition(PumpBlock.PROGRESS, PumpBlock.Progress.SIXTY);
                builder.part().modelFile(this.models().getExistingFile(this.modLoc("block/pump_80"))).rotationY(dirsRot2[d]).addModel().condition(PumpBlock.ON_OFF, true).condition(HORIZONTAL_FACING, dirs[d]).condition(PumpBlock.PROGRESS, PumpBlock.Progress.EIGHTY);
                builder.part().modelFile(this.models().getExistingFile(this.modLoc("block/pump_100"))).rotationY(dirsRot2[d]).addModel().condition(PumpBlock.ON_OFF, true).condition(HORIZONTAL_FACING, dirs[d]).condition(PumpBlock.PROGRESS, PumpBlock.Progress.HUNDRED);
            }

            List<Pair<String, RegistryObject<Block>>> hammerTypes = new ArrayList<Pair<String, RegistryObject<Block>>>() {{
                add(Pair.of("iron", SluiceBlocks.IRON_AUTO_HAMMER));
                add(Pair.of("gold", SluiceBlocks.GOLD_AUTO_HAMMER));
                add(Pair.of("diamond", SluiceBlocks.DIAMOND_AUTO_HAMMER));
                add(Pair.of("netherite", SluiceBlocks.NETHERITE_AUTO_HAMMER));
            }};

            for (Pair<String, RegistryObject<Block>> hammerType : hammerTypes) {
                MultiPartBlockStateBuilder b = this.getMultipartBuilder(hammerType.getRight().get());
                String path = hammerType.getRight().get().getRegistryName().getPath();
                for (int d = 0; d < 4; d++) {
                    b.part().modelFile(this.models().getExistingFile(this.modLoc("block/" + path))).rotationY(dirsRot[d]).addModel().condition(AutoHammerBlock.ACTIVE, false).condition(HORIZONTAL_FACING, dirs[d]);
                    b.part().modelFile(this.models().getExistingFile(this.modLoc("block/" + path + "_active"))).rotationY(dirsRot[d]).addModel().condition(AutoHammerBlock.ACTIVE, true).condition(HORIZONTAL_FACING, dirs[d]);
                }
            }

            this.simpleBlock(SluiceBlocks.DUST_BLOCK.get());
            this.simpleBlock(SluiceBlocks.CRUSHED_NETHERRACK.get());
            this.simpleBlock(SluiceBlocks.CRUSHED_BASALT.get());
            this.simpleBlock(SluiceBlocks.CRUSHED_ENDSTONE.get());
        }
    }

    private static class SMBlockModels extends BlockModelProvider {
        public SMBlockModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
            super(generator, modid, existingFileHelper);
        }

        @Override
        protected void registerModels() {
        }
    }

    private static class SMItemModels extends ItemModelProvider {
        public SMItemModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
            super(generator, modid, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            String path = SluiceBlocks.PUMP.get().getRegistryName().getPath();
            this.getBuilder(path).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + path + "_on")));

            this.registerBlockModel(SluiceBlocks.IRON_AUTO_HAMMER.get());
            this.registerBlockModel(SluiceBlocks.GOLD_AUTO_HAMMER.get());
            this.registerBlockModel(SluiceBlocks.DIAMOND_AUTO_HAMMER.get());
            this.registerBlockModel(SluiceBlocks.NETHERITE_AUTO_HAMMER.get());

            this.registerBlockModel(SluiceBlocks.DUST_BLOCK.get());
            this.registerBlockModel(SluiceBlocks.CRUSHED_NETHERRACK.get());
            this.registerBlockModel(SluiceBlocks.CRUSHED_BASALT.get());
            this.registerBlockModel(SluiceBlocks.CRUSHED_ENDSTONE.get());

            this.simpleItem(SluiceModItems.DAMAGED_CANTEEN);
//            this.simpleItem(SluiceModItems.CANTEEN);
            this.simpleItem(SluiceModItems.CLAY_BUCKET);
            this.simpleItem(SluiceModItems.CLAY_WATER_BUCKET);
            this.simpleItem(SluiceModItems.WOODEN_HAMMER);
            this.simpleItem(SluiceModItems.STONE_HAMMER);
            this.simpleItem(SluiceModItems.IRON_HAMMER);
            this.simpleItem(SluiceModItems.GOLD_HAMMER);
            this.simpleItem(SluiceModItems.DIAMOND_HAMMER);
            this.simpleItem(SluiceModItems.NETHERITE_HAMMER);
            this.simpleItem(SluiceModItems.FORTUNE_UPGRADE);
            this.simpleItem(SluiceModItems.CONSUMPTION_UPGRADE);
            this.simpleItem(SluiceModItems.SPEED_UPGRADE);
        }

        private void simpleItem(Supplier<Item> item) {
            String path = item.get().getRegistryName().getPath();
            this.singleTexture(path, this.mcLoc("item/handheld"), "layer0", this.modLoc("item/" + path));
        }

        private void registerBlockModel(Block block) {
            String path = block.getRegistryName().getPath();
            this.getBuilder(path).parent(new ModelFile.UncheckedModelFile(this.modLoc("block/" + path)));
        }
    }

    private static class SMBlockTags extends BlockTagsProvider {
        public SMBlockTags(DataGenerator generatorIn, ExistingFileHelper helper) {
            super(generatorIn, FTBSluice.MOD_ID, helper);
        }

        @Override
        protected void addTags() {
            this.tag(SluiceTags.Blocks.SLUICES).add(
                    SluiceBlocks.OAK_SLUICE.get(),
                    SluiceBlocks.IRON_SLUICE.get(),
                    SluiceBlocks.DIAMOND_SLUICE.get(),
                    SluiceBlocks.NETHERITE_SLUICE.get(),
                    SluiceBlocks.EMPOWERED_SLUICE.get()
            );

            this.tag(SluiceTags.Blocks.AUTO_HAMMERS).add(
                    SluiceBlocks.IRON_AUTO_HAMMER.get(),
                    SluiceBlocks.GOLD_AUTO_HAMMER.get(),
                    SluiceBlocks.DIAMOND_AUTO_HAMMER.get(),
                    SluiceBlocks.NETHERITE_AUTO_HAMMER.get()
            );
        }
    }

    private static class SMItemTags extends ItemTagsProvider {
        public SMItemTags(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, ExistingFileHelper helper) {
            super(dataGenerator, blockTagProvider, FTBSluice.MOD_ID, helper);
        }

        @Override
        protected void addTags() {
            this.tag(SluiceTags.Items.HAMMERS).add(
                    SluiceModItems.WOODEN_HAMMER.get(),
                    SluiceModItems.STONE_HAMMER.get(),
                    SluiceModItems.IRON_HAMMER.get(),
                    SluiceModItems.GOLD_HAMMER.get(),
                    SluiceModItems.DIAMOND_HAMMER.get(),
                    SluiceModItems.NETHERITE_HAMMER.get()
            );

            this.tag(SluiceTags.Items.MESHES).add(
                    SluiceModItems.CLOTH_MESH.get(),
                    SluiceModItems.IRON_MESH.get(),
                    SluiceModItems.GOLD_MESH.get(),
                    SluiceModItems.DIAMOND_MESH.get(),
                    SluiceModItems.BLAZING_MESH.get()
            );

            this.tag(SluiceTags.Items.WATER_BUCKETS).add(Items.WATER_BUCKET, SluiceModItems.CLAY_WATER_BUCKET.get());
            this.tag(SluiceTags.Items.EMPTY_BUCKETS).add(Items.BUCKET, SluiceModItems.CLAY_BUCKET.get());
        }
    }

    private static class SMRecipes extends RecipeProvider {
        public final Tag<Item> IRON_INGOT = ItemTags.bind("forge:ingots/iron");
        public final Tag<Item> DIAMOND_GEM = ItemTags.bind("forge:gems/diamond");
        public final Tag<Item> STRING = ItemTags.bind("forge:string");
        public final Tag<Item> STICK = ItemTags.bind("forge:rods/wooden");

        public SMRecipes(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        protected void buildShapelessRecipes(Consumer<FinishedRecipe> consumer) {
            for (MeshType type : MeshType.REAL_VALUES) {
                ShapedRecipeBuilder.shaped(type.meshItem.get())
                        .unlockedBy("has_item", has(this.STRING))
                        .group(MODID + ":mesh")
                        .pattern("SIS")
                        .pattern("ICI")
                        .pattern("SIS")
                        .define('S', this.STICK)
                        .define('C', this.STRING)
                        .define('I', type.getIngredient())
                        .save(consumer);
            }

            ShapedRecipeBuilder.shaped(SluiceModItems.CLAY_BUCKET.get())
                    .unlockedBy("has_item", has(Items.CLAY_BALL))
                    .pattern("   ")
                    .pattern("c c")
                    .pattern(" c ")
                    .define('c', Items.CLAY_BALL)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(SluiceModItems.DAMAGED_CANTEEN.get())
                    .unlockedBy("has_item", has(Tags.Items.LEATHER))
                    .pattern("lil")
                    .pattern("sbs")
                    .pattern("lml")
                    .define('l', Tags.Items.LEATHER)
                    .define('i', Tags.Items.INGOTS_IRON)
                    .define('m', SluiceModItems.CLOTH_MESH.get())
                    .define('s', Tags.Items.STRING)
                    .define('b', Items.BUCKET)
                    .save(consumer);

//            ShapedRecipeBuilder.shaped(SluiceModItems.CANTEEN.get())
//                    .unlockedBy("has_item", has(Items.LEATHER))
//                    .pattern("lnl")
//                    .pattern("sbs")
//                    .pattern("dmd")
//                    .define('l', Tags.Items.LEATHER)
//                    .define('n', Tags.Items.INGOTS_NETHERITE)
//                    .define('m', SluiceModItems.CLOTH_MESH.get())
//                    .define('d', Tags.Items.GEMS_DIAMOND)
//                    .define('s', Tags.Items.STRING)
//                    .define('b', Items.BUCKET)
//                    .save(consumer);

            ShapedRecipeBuilder.shaped(SluiceModItems.PUMP.get())
                    .unlockedBy("has_item", has(Items.CLAY_BALL))
                    .pattern("www")
                    .pattern("sss")
                    .pattern("bbb")
                    .define('w', SluiceTags.Items.WATER_BUCKETS)
                    .define('s', Items.STONE)
                    .define('b', Items.OAK_PLANKS)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(SluiceModItems.OAK_SLUICE.get())
                    .unlockedBy("has_item", has(this.STICK))
                    .pattern("WS")
                    .pattern("WW")
                    .define('S', this.STICK)
                    .define('W', Items.OAK_LOG)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(SluiceModItems.IRON_SLUICE.get())
                    .unlockedBy("has_item", has(this.IRON_INGOT))
                    .pattern("IC")
                    .pattern("SI")
                    .define('S', SluiceModItems.OAK_SLUICE.get())
                    .define('I', this.IRON_INGOT)
                    .define('C', Items.CHAIN)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(SluiceModItems.DIAMOND_SLUICE.get())
                    .unlockedBy("has_item", has(SluiceModItems.IRON_SLUICE.get()))
                    .pattern("DD")
                    .pattern("SD")
                    .define('S', SluiceModItems.IRON_SLUICE.get())
                    .define('D', this.DIAMOND_GEM)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(SluiceModItems.EMPOWERED_SLUICE.get())
                    .unlockedBy("has_item", has(SluiceModItems.NETHERITE_SLUICE.get()))
                    .pattern("DDX")
                    .pattern("SDX")
                    .pattern("AAA")
                    .define('S', SluiceModItems.NETHERITE_SLUICE.get())
                    .define('D', Tags.Items.INGOTS_NETHERITE)
                    .define('X', Blocks.DIAMOND_BLOCK)
                    .define('A', Blocks.GOLD_BLOCK)
                    .save(consumer);

            UpgradeRecipeBuilder.smithing(Ingredient.of(SluiceModItems.DIAMOND_SLUICE.get()), Ingredient.of(Items.NETHERITE_INGOT), SluiceModItems.NETHERITE_SLUICE.get())
                    .unlocks("has_item", has(Items.NETHERITE_INGOT))
                    .save(consumer, FTBSluice.MOD_ID + ":netherite_sluice");

            this.hammer(SluiceModItems.WOODEN_HAMMER.get(), ItemTags.PLANKS, consumer);
            this.hammer(SluiceModItems.STONE_HAMMER.get(), Items.COBBLESTONE, consumer);
            this.hammer(SluiceModItems.IRON_HAMMER.get(), this.IRON_INGOT, consumer);
            this.hammer(SluiceModItems.GOLD_HAMMER.get(), Items.GOLD_INGOT, consumer);
            this.hammer(SluiceModItems.DIAMOND_HAMMER.get(), this.DIAMOND_GEM, consumer);
            this.hammer(SluiceModItems.NETHERITE_HAMMER.get(), Items.NETHERITE_INGOT, consumer);

            ShapedRecipeBuilder.shaped(SluiceModItems.CONSUMPTION_UPGRADE.get())
                    .unlockedBy("has_item", has(Items.LAVA_BUCKET))
                    .pattern("III")
                    .pattern("GSG")
                    .pattern("III")
                    .define('I', Tags.Items.INGOTS_IRON)
                    .define('G', Tags.Items.INGOTS_GOLD)
                    .define('S', Items.LAVA_BUCKET)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(SluiceModItems.SPEED_UPGRADE.get())
                    .unlockedBy("has_item", has(Items.BLAZE_ROD))
                    .pattern("III")
                    .pattern("IBI")
                    .pattern("III")
                    .define('I', Tags.Items.INGOTS_IRON)
                    .define('B', Items.BLAZE_ROD)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(SluiceModItems.FORTUNE_UPGRADE.get())
                    .unlockedBy("has_item", has(Tags.Items.GEMS_EMERALD))
                    .pattern("III")
                    .pattern("IEI")
                    .pattern("III")
                    .define('I', Tags.Items.INGOTS_IRON)
                    .define('E', Tags.Items.GEMS_EMERALD)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(SluiceModItems.IRON_AUTO_HAMMER.get())
                    .unlockedBy("has_item", has(SluiceModItems.IRON_HAMMER.get()))
                    .pattern("IGI")
                    .pattern("XHX")
                    .pattern("RGR")
                    .define('I', Tags.Items.INGOTS_IRON)
                    .define('X', Tags.Items.GLASS)
                    .define('R', Tags.Items.DUSTS_REDSTONE)
                    .define('G', Tags.Items.INGOTS_GOLD)
                    .define('H', SluiceModItems.IRON_HAMMER.get())
                    .save(consumer);

            autoHammer(SluiceModItems.GOLD_AUTO_HAMMER.get(), SluiceModItems.IRON_AUTO_HAMMER.get(), SluiceModItems.GOLD_HAMMER.get(), consumer);
            autoHammer(SluiceModItems.DIAMOND_AUTO_HAMMER.get(), SluiceModItems.GOLD_AUTO_HAMMER.get(), SluiceModItems.DIAMOND_HAMMER.get(), consumer);
            autoHammer(SluiceModItems.NETHERITE_AUTO_HAMMER.get(), SluiceModItems.DIAMOND_AUTO_HAMMER.get(), SluiceModItems.NETHERITE_HAMMER.get(), consumer);
        }

        private void autoHammer(ItemLike output, Item center, Item top, Consumer<FinishedRecipe> consumer) {
            ShapedRecipeBuilder.shaped(output)
                    .unlockedBy("has_item", has(center))
                    .pattern("ITI")
                    .pattern("XCX")
                    .pattern("RGR")
                    .define('I', Tags.Items.INGOTS_IRON)
                    .define('R', Tags.Items.DUSTS_REDSTONE)
                    .define('G', Tags.Items.INGOTS_GOLD)
                    .define('X', Tags.Items.GLASS)
                    .define('T', top)
                    .define('C', center)
                    .save(consumer);
        }

        private void hammer(ItemLike output, Tag<Item> head, Consumer<FinishedRecipe> consumer) {
            ShapedRecipeBuilder.shaped(output)
                    .unlockedBy("has_item", has(head))
                    .pattern("hrh")
                    .pattern(" r ")
                    .pattern(" r ")
                    .define('h', head)
                    .define('r', this.STICK)
                    .save(consumer);
        }

        private void hammer(ItemLike output, ItemLike head, Consumer<FinishedRecipe> consumer) {
            ShapedRecipeBuilder.shaped(output)
                    .unlockedBy("has_item", has(head))
                    .pattern("hrh")
                    .pattern(" r ")
                    .pattern(" r ")
                    .define('h', head)
                    .define('r', this.STICK)
                    .save(consumer);
        }
    }

    private static class SMLootTableProvider extends ForgeLootTableProvider {
        private final List<com.mojang.datafixers.util.Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> lootTables = Lists.newArrayList(com.mojang.datafixers.util.Pair.of(SMBlockLootProvider::new, LootContextParamSets.BLOCK));

        public SMLootTableProvider(DataGenerator dataGeneratorIn) {
            super(dataGeneratorIn);
        }

        @Override
        protected List<com.mojang.datafixers.util.Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
            return this.lootTables;
        }
    }

    public static class SMBlockLootProvider extends BlockLoot {
        private final Map<ResourceLocation, LootTable.Builder> tables = Maps.newHashMap();

        @Override
        protected void addTables() {
//            this.dropSelf(SluiceBlocks.TANK.get());
            this.dropSelf(SluiceBlocks.DUST_BLOCK.get());
            this.dropSelf(SluiceBlocks.CRUSHED_BASALT.get());
            this.dropSelf(SluiceBlocks.CRUSHED_ENDSTONE.get());
            this.dropSelf(SluiceBlocks.CRUSHED_NETHERRACK.get());
            this.dropSelf(SluiceBlocks.PUMP.get());
            this.dropSelf(SluiceBlocks.IRON_AUTO_HAMMER.get());
            this.dropSelf(SluiceBlocks.GOLD_AUTO_HAMMER.get());
            this.dropSelf(SluiceBlocks.DIAMOND_AUTO_HAMMER.get());
            this.dropSelf(SluiceBlocks.NETHERITE_AUTO_HAMMER.get());

            SluiceBlocks.SLUICES.stream()
                    .map(e -> e.getKey().get())
                    .forEach(this::dropSelf);

            SluiceBlocks.POWERED_SLUICES.stream()
                    .map(e -> e.getKey().get())
                    .forEach(this::sluiceTable);
        }

        public void sluiceTable(Block block) {
            this.add(block, LootTable.lootTable().withPool(
                    applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantIntValue.exactly(1))
                        .add(LootItem.lootTableItem(block)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                            .apply(CopyNbtFunction.copyData(CopyNbtFunction.DataSource.BLOCK_ENTITY).copy("Energy", "BlockEntityTag.Energy"))
                        )
                    )
            ));
        }

        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            this.addTables();

            for (ResourceLocation rs : new ArrayList<>(this.tables.keySet())) {
                if (rs != BuiltInLootTables.EMPTY) {
                    LootTable.Builder builder = this.tables.remove(rs);

                    if (builder == null) {
                        throw new IllegalStateException(String.format("Missing loottable '%s'", rs));
                    }

                    consumer.accept(rs, builder);
                }
            }

            if (!this.tables.isEmpty()) {
                throw new IllegalStateException("Created block loot tables for non-blocks: " + this.tables.keySet());
            }
        }

        @Override
        protected void add(Block blockIn, LootTable.Builder table) {
            this.tables.put(blockIn.getLootTable(), table);
        }
    }
}
