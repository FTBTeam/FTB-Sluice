package dev.latvian.mods.sluice.recipe;

import dev.latvian.mods.sluice.SluiceMod;
import dev.latvian.mods.sluice.block.MeshType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author LatvianModder
 */
public class SluiceModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SluiceMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<?>> SLUICE = REGISTRY.register("sluice", SluiceRecipeSerializer::new);
    public static final RecipeType<SluiceRecipe> SLUICE_TYPE = RecipeType.register(SluiceMod.MOD_ID + ":sluice");

//    public static final RegistryObject<RecipeSerializer<?>> INGREDIENT_PROPERTIES = REGISTRY.register("ingredient_properties", IngredientPropertiesRecipeSerializer::new);
//    public static final RecipeType<IngredientPropertiesRecipe> INGREDIENT_PROPERTIES_TYPE = RecipeType.register(SluiceMod.MOD_ID + ":ingredient_properties");

    private static final Map<Pair<Item, MeshType>, List<ItemWithWeight>> sluiceCache = new HashMap<>();
//    private static final Map<Pair<Item, MeshType>, IngredientPropertiesRecipe> ingredientPropertiesCache = new HashMap<>();

    public static void clearCache() {
        sluiceCache.clear();
//        ingredientPropertiesCache.clear();
    }

    public static List<ItemWithWeight> getSluiceRecipes(Level world, MeshType mesh, ItemStack input) {
        return sluiceCache.computeIfAbsent(Pair.of(input.getItem(), mesh), key -> {
            List<ItemWithWeight> list = new ArrayList<>();

            for (SluiceRecipe recipe : world.getRecipeManager().getRecipesFor(SLUICE_TYPE, NoInventory.INSTANCE, world)) {
                if (recipe.meshes.contains(mesh) && recipe.ingredient.test(input)) {
                    recipe.results.forEach(e -> list.add(new ItemWithWeight(e.item, e.weight)));
                }
            }

            return list;
        });
    }

//    @Nullable
//    public static IngredientPropertiesRecipe getProperties(Level world, MeshType mesh, ItemStack input) {
//        return ingredientPropertiesCache.computeIfAbsent(Pair.of(input.getItem(), mesh), key -> {
//            for (IngredientPropertiesRecipe recipe : world.getRecipeManager().getRecipesFor(INGREDIENT_PROPERTIES_TYPE, NoInventory.INSTANCE, world)) {
//                if (recipe.meshes.contains(mesh) && recipe.ingredient.test(input)) {
//                    recipe.items = new ArrayList<>();
//
//                    for (SluiceRecipe r : getSluiceRecipes(world, mesh, input)) {
//                        recipe.items.addAll(r.results);
//                    }
//
//                    recipe.totalWeight = recipe.noItemWeight;
//
//                    for (ItemWithWeight entry : recipe.items) {
//                        recipe.totalWeight += entry.weight;
//                    }
//
//                    return recipe;
//                }
//            }
//
//            return null;
//        });
//    }

    public static List<ItemStack> getRandomResult(Level world, MeshType mesh, ItemStack input) {
        List<ItemStack> outputResults = new ArrayList<>();
        List<ItemWithWeight> resultsWithWeights = getSluiceRecipes(world, mesh, input);

        for (ItemWithWeight result : resultsWithWeights) {
            float number = world.getRandom().nextInt(result.weight);
            System.out.println(number);
            System.out.println(result.weight);
        }

        return outputResults;
//        }
//
//        int number = world.getRandom().nextInt(properties.totalWeight) + 1;
//        int currentWeight = properties.noItemWeight;
//
//        if (currentWeight < number) {
//            for (ItemWithWeight i : properties.items) {
//                currentWeight += i.weight;
//
//                if (currentWeight >= number) {
//                    return new ItemWithWeight(i.item, properties.time);
//                }
//            }
//        }
//
//        return new ItemWithWeight(ItemStack.EMPTY, properties.time);
    }
}
