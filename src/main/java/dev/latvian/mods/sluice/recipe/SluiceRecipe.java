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

/**
 * @author LatvianModder
 */
public class SluiceRecipe implements IRecipe<NoInventory>
{
	private final ResourceLocation id;
	public String group;
	public int weight;
	public Ingredient ingredient;
	public ItemStack result;
	public HashSet<MeshType> meshes;

	public SluiceRecipe(ResourceLocation i, String g)
	{
		id = i;
		group = g;
		weight = 1;
		ingredient = Ingredient.EMPTY;
		result = ItemStack.EMPTY;
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
		return result.copy();
	}

	@Override
	public boolean canFit(int width, int height)
	{
		return true;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return result;
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
		return SluiceModRecipeSerializers.SLUICE.get();
	}

	@Override
	public IRecipeType<?> getType()
	{
		return SluiceModRecipeSerializers.SLUICE_TYPE;
	}

	public String chanceString(int totalWeight)
	{
		if (totalWeight <= 0)
		{
			return "??%";
		}
		else if (weight <= 0)
		{
			return "0%";
		}
		else if (weight >= totalWeight)
		{
			return "100%";
		}

		int chance = weight * 100 / totalWeight;
		double chanced = weight * 100D / (double) totalWeight;

		if (chance != chanced)
		{
			if (chanced < 0.01D)
			{
				return "<0.01%";
			}

			return String.format("%.2f%%", chanced);
		}

		return chance + "%";
	}
}