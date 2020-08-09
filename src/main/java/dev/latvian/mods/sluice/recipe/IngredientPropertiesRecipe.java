package dev.latvian.mods.sluice.recipe;

import dev.latvian.mods.sluice.block.MeshType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;

/**
 * @author LatvianModder
 */
public class IngredientPropertiesRecipe implements IRecipe<NoInventory>
{
	private final ResourceLocation id;
	public String group;
	public int noItemWeight;
	public int time;
	public Ingredient ingredient;
	public HashSet<MeshType> meshes;

	public List<SluiceRecipe> recipes;
	public int totalWeight;

	public IngredientPropertiesRecipe(ResourceLocation i, String g)
	{
		id = i;
		group = g;
		noItemWeight = 0;
		time = 80;
		ingredient = Ingredient.EMPTY;
		meshes = new HashSet<>();
	}

	@Override
	public boolean matches(NoInventory inv, World world)
	{
		return true;
	}

	@Override
	public ItemStack getCraftingResult(NoInventory inv)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height)
	{
		return true;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId()
	{
		return id;
	}

	@Override
	public String getGroup()
	{
		return group;
	}

	@Override
	public IRecipeSerializer<?> getSerializer()
	{
		return SluiceModRecipeSerializers.INGREDIENT_PROPERTIES.get();
	}

	@Override
	public IRecipeType<?> getType()
	{
		return SluiceModRecipeSerializers.INGREDIENT_PROPERTIES_TYPE;
	}
}