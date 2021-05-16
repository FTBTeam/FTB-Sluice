package dev.ftb.mods.sluice.recipe;

import dev.ftb.mods.sluice.SluiceMod;
import dev.ftb.mods.sluice.block.MeshType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class SluiceModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SluiceMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<?>> SLUICE = REGISTRY.register("sluice", SluiceRecipeSerializer::new);
    public static final RecipeType<SluiceRecipe> SLUICE_TYPE = RecipeType.register(SluiceMod.MOD_ID + ":sluice");

    private static final Map<Pair<Item, MeshType>, InputRecipeResult> sluiceCache = new HashMap<>();

    public static void clearCache() {
        sluiceCache.clear();
    }

    /**
     * Runs through all sluice recipes to find matching mesh -> resulting items.
     *
     * @param world level
     * @param mesh  the type of mesh for the sluice
     * @param input an input item to find results for
     * @return A list of items with the chances.
     */
    public static InputRecipeResult getSluiceRecipes(Level world, MeshType mesh, ItemStack input) {
        return sluiceCache.computeIfAbsent(Pair.of(input.getItem(), mesh), key -> {
            List<ItemWithWeight> list = new ArrayList<>();

            int max = -1;
            for (SluiceRecipe recipe : world.getRecipeManager().getRecipesFor(SLUICE_TYPE, NoInventory.INSTANCE, world)) {
                if (recipe.meshes.contains(mesh) && recipe.ingredient.test(input)) {
                    // Only set based on the first min max we see.
                    if (max == -1) {
                        max = recipe.max;
                    }

                    recipe.results.forEach(e -> list.add(new ItemWithWeight(e.item, e.weight)));
                }
            }

            return new InputRecipeResult(list, max);
        });
    }

    /**
     * Checks that a given input has any result.
     */
    public static boolean itemHasSluiceResults(Level level, MeshType mesh, ItemStack input) {
        return !getSluiceRecipes(level, mesh, input).getItems().isEmpty();
    }

    /**
     * Computes a list of resulting output items based on an input. We get the outputting items from the
     * custom recipe.
     */
    public static List<ItemStack> getRandomResult(Level world, MeshType mesh, ItemStack input) {
        List<ItemStack> outputResults = new ArrayList<>();
        InputRecipeResult recipe = getSluiceRecipes(world, mesh, input);

        for (ItemWithWeight result : recipe.getItems()) {
            float number = world.getRandom().nextFloat();
            if (number <= result.weight) {
                if (outputResults.size() >= recipe.getMaxDrops()) {
                    break;
                }

                outputResults.add(result.item.copy());
            }
        }

        return outputResults;
    }
}
