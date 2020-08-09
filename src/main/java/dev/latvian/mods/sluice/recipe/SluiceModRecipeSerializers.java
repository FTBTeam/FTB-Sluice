package dev.latvian.mods.sluice.recipe;

import dev.latvian.mods.sluice.SluiceMod;
import dev.latvian.mods.sluice.block.MeshType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class SluiceModRecipeSerializers
{
	public static final DeferredRegister<IRecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SluiceMod.MOD_ID);

	public static final RegistryObject<IRecipeSerializer<?>> SLUICE = REGISTRY.register("sluice", SluiceRecipeSerializer::new);
	public static final IRecipeType<SluiceRecipe> SLUICE_TYPE = IRecipeType.register(SluiceMod.MOD_ID + ":sluice");

	public static final RegistryObject<IRecipeSerializer<?>> INGREDIENT_PROPERTIES = REGISTRY.register("ingredient_properties", IngredientPropertiesRecipeSerializer::new);
	public static final IRecipeType<IngredientPropertiesRecipe> INGREDIENT_PROPERTIES_TYPE = IRecipeType.register(SluiceMod.MOD_ID + ":ingredient_properties");

	private static final Map<Pair<Item, MeshType>, List<SluiceRecipe>> sluiceCache = new HashMap<>();
	private static final Map<Pair<Item, MeshType>, IngredientPropertiesRecipe> ingredientPropertiesCache = new HashMap<>();

	public static void clearCache()
	{
		sluiceCache.clear();
		ingredientPropertiesCache.clear();
	}

	private static List<SluiceRecipe> getSluiceRecipes(World world, MeshType mesh, ItemStack input)
	{
		return sluiceCache.computeIfAbsent(Pair.of(input.getItem(), mesh), key -> {
			List<SluiceRecipe> list = new ArrayList<>();

			for (SluiceRecipe recipe : world.getRecipeManager().getRecipes(SLUICE_TYPE, NoInventory.INSTANCE, world))
			{
				if (recipe.meshes.contains(mesh) && recipe.ingredient.test(input))
				{
					list.add(recipe);
				}
			}

			return list;
		});
	}

	@Nullable
	public static IngredientPropertiesRecipe getProperties(World world, MeshType mesh, ItemStack input)
	{
		return ingredientPropertiesCache.computeIfAbsent(Pair.of(input.getItem(), mesh), key -> {
			for (IngredientPropertiesRecipe recipe : world.getRecipeManager().getRecipes(INGREDIENT_PROPERTIES_TYPE, NoInventory.INSTANCE, world))
			{
				if (recipe.meshes.contains(mesh) && recipe.ingredient.test(input))
				{
					recipe.recipes = getSluiceRecipes(world, mesh, input);

					if (recipe.recipes.isEmpty())
					{
						return null;
					}

					recipe.totalWeight = recipe.noItemWeight;

					for (SluiceRecipe r : recipe.recipes)
					{
						recipe.totalWeight += r.weight;
					}

					return recipe;
				}
			}

			return null;
		});
	}

	public static Pair<ItemStack, Integer> getRandomResult(World world, MeshType mesh, ItemStack input)
	{
		IngredientPropertiesRecipe properties = getProperties(world, mesh, input);

		if (properties == null)
		{
			return Pair.of(ItemStack.EMPTY, 0);
		}

		int number = world.getRandom().nextInt(properties.totalWeight) + 1;
		int currentWeight = properties.noItemWeight;

		if (currentWeight < number)
		{
			for (SluiceRecipe r : properties.recipes)
			{
				currentWeight += r.weight;

				if (currentWeight >= number)
				{
					return Pair.of(r.result, properties.time);
				}
			}
		}

		return Pair.of(ItemStack.EMPTY, properties.time);
	}
}