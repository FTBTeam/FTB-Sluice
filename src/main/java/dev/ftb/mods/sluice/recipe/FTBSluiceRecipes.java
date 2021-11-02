package dev.ftb.mods.sluice.recipe;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.block.MeshType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.stream.Collectors;


public class FTBSluiceRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, FTBSluice.MOD_ID);

    public static final RegistryObject<RecipeSerializer<?>> SLUICE = REGISTRY.register("sluice", SluiceRecipeSerializer::new);
    public static final RecipeType<SluiceRecipe> SLUICE_TYPE = RecipeType.register(FTBSluice.MOD_ID + ":sluice");

    public static final RegistryObject<RecipeSerializer<?>> HAMMER = REGISTRY.register("hammer", HammerRecipeSerializer::new);
    public static final RecipeType<HammerRecipe> HAMMER_TYPE = RecipeType.register(FTBSluice.MOD_ID + ":hammer");
    public static final Set<Ingredient> hammerableCache = new HashSet<>();

    private static final Map<Triple<Fluid, Item, MeshType>, SluiceRecipeInfo> sluiceCache = new HashMap<>();

    // Ignores the fluid requirement to check for valid insert actions
    private static final HashMap<MeshType, HashSet<Ingredient>> sluiceInputCache = new HashMap<>();

    private static final Map<Item, List<ItemStack>> hammerCache = new HashMap<>();

    public static void createSluiceCaches(RecipeManager recipeManager) {
        List<SluiceRecipe> sluiceRecipes = recipeManager.getAllRecipesFor(SLUICE_TYPE);

        // Mesh -> has -> Ingredients.
        for (SluiceRecipe e : sluiceRecipes) {
            for (MeshType a : e.meshes) {
                sluiceInputCache.computeIfAbsent(a, (_0) -> new HashSet<>()).add(e.ingredient);
            }
        }
    }

    public static void createHammerables(RecipeManager manager) {
        hammerableCache.addAll(manager.getAllRecipesFor(HAMMER_TYPE).stream().map(e -> e.ingredient).collect(Collectors.toList()));
    }

    public static void refreshCaches(RecipeManager manager) {
        clearCache();
        createSluiceCaches(manager);
        createHammerables(manager);
    }

    public static void clearCache() {
        sluiceCache.clear();
        sluiceInputCache.clear();

        hammerCache.clear();
        hammerableCache.clear();
    }

    /**
     * Runs through all sluice recipes to find matching mesh -> resulting items.
     *
     * @param world level
     * @param mesh  the type of mesh for the sluice
     * @param input an input item to find results for
     * @return A list of items with the chances.
     */
    public static SluiceRecipeInfo getSluiceRecipes(Fluid fluid, Level world, MeshType mesh, ItemStack input) {
        return sluiceCache.computeIfAbsent(Triple.of(fluid, input.getItem(), mesh), key -> {
            List<ItemWithWeight> list = new ArrayList<>();

            int max = -1;
            int time = -1;
            int mb = -1;
            for (SluiceRecipe recipe : world.getRecipeManager().getRecipesFor(SLUICE_TYPE, NoInventory.INSTANCE, world)) {
                if (recipe.meshes.contains(mesh) && recipe.ingredient.test(input) && fluid.isSame(recipe.fluid)) {
                    // Only set based on the first min max we see.
                    if (max == -1) {
                        max = recipe.max;
                    }

                    if (time == -1) {
                        time = recipe.time;
                    }

                    if (mb == -1) {
                        mb = recipe.mb;
                    }

                    recipe.results.forEach(e -> list.add(new ItemWithWeight(e.item, e.weight)));
                }
            }

            return new SluiceRecipeInfo(list, max, time, mb);
        });
    }

    /**
     * Checks that a given input has any result.
     */
    public static boolean itemHasSluiceResults(Fluid fluid, Level level, MeshType mesh, ItemStack input) {
        return !getSluiceRecipes(fluid, level, mesh, input).getItems().isEmpty();
    }

    public static boolean itemIsSluiceInput(MeshType mesh, ItemStack input) {
        if (!sluiceInputCache.containsKey(mesh)) {
            return false;
        }

        return sluiceInputCache.get(mesh).stream().anyMatch(e -> e.test(input));
    }

    public static List<ItemStack> getHammerDrops(Level level, ItemStack input) {
        return hammerCache.computeIfAbsent(input.getItem(), key -> {
            List<ItemStack> drops = new ArrayList<>();
            for (HammerRecipe recipe : level.getRecipeManager().getRecipesFor(HAMMER_TYPE, NoInventory.INSTANCE, level)) {
                if (recipe.ingredient.test(input)) {
                    recipe.results.forEach(e -> drops.add(e.copy()));
                }
            }

            return drops;
        });
    }

    public static boolean hammerable(BlockState state) {
        return hammerableCache.stream().anyMatch(e -> {
            ItemStack stack = new ItemStack(state.getBlock());
            return e.test(stack);
        });
    }

    public static boolean hammerable(ItemStack stack) {
        return hammerableCache.stream().anyMatch(e -> e.test(stack));
    }
}
