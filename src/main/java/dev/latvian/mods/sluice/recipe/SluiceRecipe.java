package dev.latvian.mods.sluice.recipe;

import dev.latvian.mods.sluice.block.MeshType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author LatvianModder
 */
public class SluiceRecipe implements Recipe<NoInventory> {
	private final ResourceLocation id;
	public String group;
	public Ingredient ingredient;
	public List<ItemWithWeight> results;
	public HashSet<MeshType> meshes;

	public SluiceRecipe(ResourceLocation i, String g) {
		id = i;
		group = g;
		ingredient = Ingredient.EMPTY;
		results = new ArrayList<>();
		meshes = new HashSet<>();
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
		return id;
	}

	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SluiceModRecipeSerializers.SLUICE.get();
	}

	@Override
	public RecipeType<?> getType() {
		return SluiceModRecipeSerializers.SLUICE_TYPE;
	}

	/*
	public String chanceString(int totalWeight) {
		if (totalWeight <= 0) {
			return "??%";
		} else if (weight <= 0) {
			return "0%";
		} else if (weight >= totalWeight) {
			return "100%";
		}

		int chance = weight * 100 / totalWeight;
		double chanced = weight * 100D / (double) totalWeight;

		if (chance != chanced) {
			if (chanced < 0.01D) {
				return "<0.01%";
			}

			return String.format("%.2f%%", chanced);
		}

		return chance + "%";
	}
	*/
}