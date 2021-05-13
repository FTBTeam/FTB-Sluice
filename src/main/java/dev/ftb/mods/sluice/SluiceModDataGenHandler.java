package dev.ftb.mods.sluice;

import dev.ftb.mods.sluice.block.MeshType;
import dev.ftb.mods.sluice.block.SluiceBlock;
import dev.ftb.mods.sluice.block.SluiceModBlocks;
import dev.ftb.mods.sluice.item.SluiceModItems;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = SluiceMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SluiceModDataGenHandler {
	public static final String MODID = SluiceMod.MOD_ID;

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
			gen.addProvider(new SMItemTags(gen, blockTags));
			gen.addProvider(new SMRecipes(gen));
		}
	}

	private static class SMLang extends LanguageProvider {
		public SMLang(DataGenerator gen, String modid, String locale) {
			super(gen, modid, locale);
		}

		@Override
		protected void addTranslations() {
			add("itemGroup." + MODID, "Sluice");

			for (MeshType type : MeshType.REAL_VALUES) {
				addItem(type.meshItem, type.getSerializedName().substring(0, 1).toUpperCase() + type.getSerializedName().substring(1) + " Mesh");
			}

			for (Pair<Supplier<Block>, String> p : SluiceModBlocks.SLUICES) {
				addBlock(p.getLeft(), p.getRight().substring(0, 1).toUpperCase() + p.getRight().substring(1) + " Sluice");
			}
		}
	}

	private static class SMBlockStateModels extends BlockStateProvider {
		private final SMBlockModels blockModels;

		public SMBlockStateModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper, SMBlockModels bm) {
			super(generator, modid, existingFileHelper);
			blockModels = bm;
		}

		@Override
		public BlockModelProvider models() {
			return blockModels;
		}

		@Override
		protected void registerStatesAndModels() {
			Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
			int[] dirsRot = {0, 180, 270, 90};

			for (Pair<Supplier<Block>, String> p : SluiceModBlocks.SLUICES) {
				MultiPartBlockStateBuilder builder = getMultipartBuilder(p.getLeft().get());

				for (int d = 0; d < 4; d++) {
					builder.part().modelFile(models().getExistingFile(modLoc("block/" + p.getRight() + "_frame"))).rotationY(dirsRot[d]).addModel().condition(BlockStateProperties.HORIZONTAL_FACING, dirs[d]);
					builder.part().modelFile(models().getExistingFile(modLoc("block/water"))).rotationY(dirsRot[d]).addModel().condition(SluiceBlock.WATER, true).condition(BlockStateProperties.HORIZONTAL_FACING, dirs[d]);

					for (MeshType type : MeshType.REAL_VALUES) {
						builder.part().modelFile(models().getExistingFile(modLoc("block/" + type.getSerializedName() + "_mesh"))).rotationY(dirsRot[d]).addModel().condition(SluiceBlock.MESH, type).condition(BlockStateProperties.HORIZONTAL_FACING, dirs[d]);
					}
				}
			}
		}
	}

	private static class SMBlockModels extends BlockModelProvider {
		public SMBlockModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
			super(generator, modid, existingFileHelper);
		}

		@Override
		protected void registerModels() {
			for (MeshType type : MeshType.REAL_VALUES) {
				withExistingParent(type.getSerializedName() + "_mesh", modLoc("block/mesh"))
						.texture("mesh", modLoc("item/" + type.getSerializedName() + "_mesh"))
				;
			}

			for (Pair<Supplier<Block>, String> p : SluiceModBlocks.SLUICES) {
				withExistingParent(p.getRight() + "_frame", modLoc("block/frame"))
						.texture("base", modLoc("block/" + p.getRight() + "_sluice_base"))
						.texture("back", modLoc("block/" + p.getRight() + "_sluice_back"))
						.texture("side", modLoc("block/" + p.getRight() + "_sluice_side"))
				;
			}
		}
	}

	private static class SMItemModels extends ItemModelProvider {
		public SMItemModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
			super(generator, modid, existingFileHelper);
		}

		@Override
		protected void registerModels() {
			for (MeshType type : MeshType.REAL_VALUES) {
				singleTexture(type.getSerializedName() + "_mesh", new ResourceLocation("minecraft", "item/generated"), "layer0", modLoc("item/" + type.getSerializedName() + "_mesh"));
			}

			for (Pair<Supplier<Block>, String> p : SluiceModBlocks.SLUICES) {
				withExistingParent(p.getRight() + "_sluice", modLoc("block/" + p.getRight() + "_frame"));
			}
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
		public SMItemTags(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider) {
			super(dataGenerator, blockTagProvider);
		}

		@Override
		protected void addTags() {
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
						.unlockedBy("has_item", has(STRING))
						.group(MODID + ":mesh")
						.pattern("SIS")
						.pattern("ICI")
						.pattern("SIS")
						.define('S', STICK)
						.define('C', STRING)
						.define('I', type.getIngredient())
						.save(consumer);
			}

			ShapedRecipeBuilder.shaped(SluiceModItems.OAK_SLUICE.get())
					.unlockedBy("has_item", has(STRING))
					.pattern("WS")
					.pattern("WW")
					.define('S', STICK)
					.define('W', Items.OAK_LOG)
					.save(consumer);

			ShapedRecipeBuilder.shaped(SluiceModItems.IRON_SLUICE.get())
					.unlockedBy("has_item", has(STRING))
					.pattern("IC")
					.pattern("SI")
					.define('S', SluiceModItems.OAK_SLUICE.get())
					.define('I', IRON_INGOT)
					.define('C', Items.CHAIN)
					.save(consumer);

			ShapedRecipeBuilder.shaped(SluiceModItems.DIAMOND_SLUICE.get())
					.unlockedBy("has_item", has(SluiceModItems.IRON_SLUICE.get()))
					.pattern("DD")
					.pattern("SD")
					.define('S', SluiceModItems.IRON_SLUICE.get())
					.define('D', DIAMOND_GEM)
					.save(consumer);

			UpgradeRecipeBuilder.smithing(Ingredient.of(SluiceModItems.DIAMOND_SLUICE.get()), Ingredient.of(Items.NETHERITE_INGOT), SluiceModItems.NETHERITE_SLUICE.get())
					.unlocks("has_item", has(Items.NETHERITE_INGOT))
					.save(consumer, SluiceMod.MOD_ID + ":netherite_sluice");
		}
	}
}
