package dev.latvian.mods.sluice;

import dev.latvian.mods.sluice.block.SluiceModBlocks;
import dev.latvian.mods.sluice.item.SluiceModItems;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = SluiceMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SluiceModDataGenHandler
{
	public static final String MODID = SluiceMod.MOD_ID;

	@SubscribeEvent
	public static void dataGenEvent(GatherDataEvent event)
	{
		DataGenerator gen = event.getGenerator();

		if (event.includeClient())
		{
			gen.addProvider(new JMLang(gen, MODID, "en_us"));
			gen.addProvider(new JMBlockModels(gen, MODID, event.getExistingFileHelper()));
			gen.addProvider(new JMItemModels(gen, MODID, event.getExistingFileHelper()));
		}

		if (event.includeServer())
		{
			JMBlockTags blockTags = new JMBlockTags(gen);
			gen.addProvider(blockTags);
			gen.addProvider(new JMItemTags(gen, blockTags));
			gen.addProvider(new JMRecipes(gen));
		}
	}

	private static class JMLang extends LanguageProvider
	{
		public JMLang(DataGenerator gen, String modid, String locale)
		{
			super(gen, modid, locale);
		}

		@Override
		protected void addTranslations()
		{
			add("itemGroup." + MODID, "Sluice");
			addItem(SluiceModItems.CLOTH_MESH, "Cloth Mesh");
			addItem(SluiceModItems.IRON_MESH, "Iron Mesh");
			addItem(SluiceModItems.GOLD_MESH, "Gold Mesh");
			addItem(SluiceModItems.DIAMOND_MESH, "Diamond Mesh");
			addBlock(SluiceModBlocks.SLUICE, "Sluice");
		}
	}

	private static class JMBlockModels extends BlockModelProvider
	{
		public JMBlockModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper)
		{
			super(generator, modid, existingFileHelper);
		}

		@Override
		protected void registerModels()
		{
		}
	}

	private static class JMItemModels extends ItemModelProvider
	{
		public JMItemModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper)
		{
			super(generator, modid, existingFileHelper);
		}

		@Override
		protected void registerModels()
		{
		}
	}

	private static class JMBlockTags extends BlockTagsProvider
	{
		public JMBlockTags(DataGenerator generatorIn)
		{
			super(generatorIn);
		}

		@Override
		protected void registerTags()
		{
		}
	}

	private static class JMItemTags extends ItemTagsProvider
	{
		public JMItemTags(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider)
		{
			super(dataGenerator, blockTagProvider);
		}

		@Override
		protected void registerTags()
		{
		}
	}

	private static class JMRecipes extends RecipeProvider
	{
		public final ITag<Item> IRON_INGOT = ItemTags.makeWrapperTag("forge:ingots/iron");
		public final ITag<Item> IRON_NUGGET = ItemTags.makeWrapperTag("forge:nuggets/iron");
		public final ITag<Item> GOLD_INGOT = ItemTags.makeWrapperTag("forge:ingots/gold");
		public final ITag<Item> DIAMOND_GEM = ItemTags.makeWrapperTag("forge:gems/diamond");
		public final ITag<Item> STRING = ItemTags.makeWrapperTag("forge:string");
		public final ITag<Item> STICK = ItemTags.makeWrapperTag("forge:rods/wooden");

		public JMRecipes(DataGenerator generatorIn)
		{
			super(generatorIn);
		}

		@Override
		protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
		{
			List<Pair<Supplier<Item>, ITag<Item>>> meshes = Arrays.asList(
					Pair.of(SluiceModItems.CLOTH_MESH, STRING),
					Pair.of(SluiceModItems.IRON_MESH, IRON_INGOT),
					Pair.of(SluiceModItems.GOLD_MESH, GOLD_INGOT),
					Pair.of(SluiceModItems.DIAMOND_MESH, DIAMOND_GEM)
			);

			for (Pair<Supplier<Item>, ITag<Item>> p : meshes)
			{
				ShapedRecipeBuilder.shapedRecipe(p.getLeft().get())
						.addCriterion("has_item", hasItem(STRING))
						.setGroup(MODID + ":mesh")
						.patternLine("SIS")
						.patternLine("ICI")
						.patternLine("SIS")
						.key('S', STICK)
						.key('C', STRING)
						.key('I', p.getRight())
						.build(consumer);
			}

			ShapedRecipeBuilder.shapedRecipe(SluiceModItems.SLUICE.get())
					.addCriterion("has_item", hasItem(STRING))
					.patternLine("I I")
					.patternLine("N N")
					.patternLine("I I")
					.key('I', IRON_INGOT)
					.key('N', IRON_NUGGET)
					.build(consumer);
		}
	}

}
