package dev.ftb.mods.sluice.recipe;

import com.google.common.base.MoreObjects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class HammerRecipe implements Recipe<NoInventory> {
    private final ResourceLocation id;
    public String group;
    public Ingredient ingredient;
    public List<ItemStack> results;

    public HammerRecipe(ResourceLocation i, String g) {
        this.id = i;
        this.group = g;
        this.ingredient = Ingredient.EMPTY;
        this.results = new ArrayList<>();
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
        return SluiceModRecipeSerializers.HAMMER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return SluiceModRecipeSerializers.HAMMER_TYPE;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", this.id)
            .add("group", this.group)
            .add("ingredient", this.ingredient)
            .add("results", this.results)
            .toString();
    }
}
