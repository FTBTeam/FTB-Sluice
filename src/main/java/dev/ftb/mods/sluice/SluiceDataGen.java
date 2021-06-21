package dev.ftb.mods.sluice;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.ftb.mods.sluice.block.MeshType;
import dev.ftb.mods.sluice.block.SluiceBlocks;
import dev.ftb.mods.sluice.block.sluice.SluiceBlock;
import dev.ftb.mods.sluice.item.SluiceModItems;
import dev.ftb.mods.sluice.tags.SluiceTags;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeLootTableProvider;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;


@Mod.EventBusSubscriber(modid = FTBSluice.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SluiceDataGen {
    public static final String MODID = FTBSluice.MOD_ID;

    @SubscribeEvent
    public static void dataGenEvent(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            gen.addProvider(new SMLang(gen, MODID, "en_us"));
            SMBlockModels blockModels = new SMBlockModels(gen, MODID, event.getExistingFileHelper());
            gen.addProvider(blockModels);
            gen.addProvider(new SMItemModels(gen, MODID, event.getExistingFileHelper()));
            gen.addProvider(new SMBlockStateModels(gen, MODID, event.getExistingFileHelper(), blockModels));
        }

        if (event.includeServer()) {
            SMBlockTags blockTags = new SMBlockTags(gen);
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

            for (Pair<Supplier<Block>, String> p : SluiceBlocks.SLUICES) {
                this.addBlock(p.getLeft(), p.getRight().substring(0, 1).toUpperCase() + p.getRight().substring(1) + " Sluice");
            }

            this.addBlock(SluiceBlocks.DUST_BLOCK, "Dust");
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
            this.addBlock(SluiceBlocks.CRUSHED_NETHERRACK, "Crushed Netherrack");
            this.addBlock(SluiceBlocks.CRUSHED_BASALT, "Crushed Basalt");
            this.addBlock(SluiceBlocks.CRUSHED_ENDSTONE, "Crushed Endstone");

            this.add(MODID + ".jei.processingTime", "Processing Time: %s ticks");
            this.add(MODID + ".jei.fluidUsage", "Uses %smB of Fluid");
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

            for (Pair<Supplier<Block>, String> p : SluiceBlocks.SLUICES) {
                MultiPartBlockStateBuilder builder = this.getMultipartBuilder(p.getLeft().get());

                for (int d = 0; d < 4; d++) {
                    builder.part().modelFile(this.models().getExistingFile(this.modLoc("block/" + p.getRight() + "_sluice_body"))).rotationY(dirsRot[d]).addModel().condition(BlockStateProperties.HORIZONTAL_FACING, dirs[d]).condition(SluiceBlock.PART, SluiceBlock.Part.MAIN);
                    builder.part().modelFile(this.models().getExistingFile(this.modLoc("block/" + p.getRight() + "_sluice_front"))).rotationY(dirsRot[d]).addModel().condition(BlockStateProperties.HORIZONTAL_FACING, dirs[d]).condition(SluiceBlock.PART, SluiceBlock.Part.FUNNEL);

                    for (MeshType type : MeshType.REAL_VALUES) {
                        builder.part().modelFile(this.models().getExistingFile(this.modLoc("block/" + type.getSerializedName() + "_mesh"))).rotationY(dirsRot[d]).addModel().condition(SluiceBlock.MESH, type).condition(BlockStateProperties.HORIZONTAL_FACING, dirs[d]).condition(SluiceBlock.PART, SluiceBlock.Part.MAIN);
                    }
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
            this.registerBlockModel(SluiceBlocks.DUST_BLOCK.get());
            this.registerBlockModel(SluiceBlocks.CRUSHED_NETHERRACK.get());
            this.registerBlockModel(SluiceBlocks.CRUSHED_BASALT.get());
            this.registerBlockModel(SluiceBlocks.CRUSHED_ENDSTONE.get());
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
        public SMBlockTags(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        protected void addTags() {
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

            ShapedRecipeBuilder.shaped(SluiceModItems.OAK_SLUICE.get())
                    .unlockedBy("has_item", has(this.STRING))
                    .pattern("WS")
                    .pattern("WW")
                    .define('S', this.STICK)
                    .define('W', Items.OAK_LOG)
                    .save(consumer);

            ShapedRecipeBuilder.shaped(SluiceModItems.IRON_SLUICE.get())
                    .unlockedBy("has_item", has(this.STRING))
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

            UpgradeRecipeBuilder.smithing(Ingredient.of(SluiceModItems.DIAMOND_SLUICE.get()), Ingredient.of(Items.NETHERITE_INGOT), SluiceModItems.NETHERITE_SLUICE.get())
                    .unlocks("has_item", has(Items.NETHERITE_INGOT))
                    .save(consumer, FTBSluice.MOD_ID + ":netherite_sluice");

            this.hammer(SluiceModItems.WOODEN_HAMMER.get(), ItemTags.PLANKS, consumer);
            this.hammer(SluiceModItems.STONE_HAMMER.get(), Items.STONE, consumer);
            this.hammer(SluiceModItems.IRON_HAMMER.get(), this.IRON_INGOT, consumer);
            this.hammer(SluiceModItems.GOLD_HAMMER.get(), Items.GOLD_INGOT, consumer);
            this.hammer(SluiceModItems.DIAMOND_HAMMER.get(), this.DIAMOND_GEM, consumer);
            this.hammer(SluiceModItems.NETHERITE_HAMMER.get(), Items.NETHERITE_INGOT, consumer);
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
            this.dropSelf(SluiceBlocks.TANK.get());
            this.dropSelf(SluiceBlocks.DUST_BLOCK.get());
            SluiceBlocks.SLUICES.forEach(e -> this.dropSelf(e.getKey().get()));
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
