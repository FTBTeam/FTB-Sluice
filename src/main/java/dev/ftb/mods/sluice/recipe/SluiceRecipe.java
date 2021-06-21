package dev.ftb.mods.sluice.recipe;

import dev.ftb.mods.sluice.block.MeshType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class SluiceRecipe implements Recipe<NoInventory> {
    private final ResourceLocation id;
    public String group;
    public Ingredient ingredient;
    public Fluid fluid;
    public int mb;
    public List<ItemWithWeight> results;
    public HashSet<MeshType> meshes;
    public int max;
    public int time;

    public SluiceRecipe(ResourceLocation i, String g) {
        this.id = i;
        this.group = g;
        this.fluid = Fluids.WATER;
        this.mb = 1000;
        this.ingredient = Ingredient.EMPTY;
        this.results = new ArrayList<>();
        this.meshes = new HashSet<>();
        this.max = 0;
        this.time = 100;
    }

    @Override
    public boolean matches(NoInventory inv, Level world) {
        return true;
    }

    @Override
    public ItemStack assemble(NoInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FTBSluiceRecipes.SLUICE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return FTBSluiceRecipes.SLUICE_TYPE;
    }
}
