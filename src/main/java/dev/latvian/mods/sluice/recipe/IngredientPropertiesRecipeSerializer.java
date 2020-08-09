package dev.latvian.mods.sluice.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.sluice.block.MeshType;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * @author LatvianModder
 */
public class IngredientPropertiesRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<IngredientPropertiesRecipe>
{
	@Override
	public IngredientPropertiesRecipe read(ResourceLocation recipeId, JsonObject json)
	{
		IngredientPropertiesRecipe r = new IngredientPropertiesRecipe(recipeId, json.has("group") ? json.get("group").getAsString() : "");

		if (json.has("no_item_weight"))
		{
			r.noItemWeight = json.get("no_item_weight").getAsInt();
		}

		if (json.has("time"))
		{
			r.time = json.get("time").getAsInt();
		}

		if (JSONUtils.isJsonArray(json, "ingredient"))
		{
			r.ingredient = Ingredient.deserialize(JSONUtils.getJsonArray(json, "ingredient"));
		}
		else
		{
			r.ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
		}

		if (json.has("meshes"))
		{
			for (JsonElement e : json.get("meshes").getAsJsonArray())
			{
				MeshType t = MeshType.MAP.getOrDefault(e.getAsString(), MeshType.NONE);

				if (t != MeshType.NONE)
				{
					r.meshes.add(t);
				}
			}
		}
		else
		{
			for (MeshType t : MeshType.VALUES)
			{
				if (t != MeshType.NONE)
				{
					r.meshes.add(t);
				}
			}
		}

		return r;
	}

	@Override
	public IngredientPropertiesRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
	{
		IngredientPropertiesRecipe r = new IngredientPropertiesRecipe(recipeId, buffer.readString(Short.MAX_VALUE));
		r.noItemWeight = buffer.readVarInt();
		r.time = buffer.readVarInt();
		r.ingredient = Ingredient.read(buffer);
		return r;
	}

	@Override
	public void write(PacketBuffer buffer, IngredientPropertiesRecipe r)
	{
		buffer.writeString(r.getGroup(), Short.MAX_VALUE);
		buffer.writeVarInt(r.noItemWeight);
		buffer.writeVarInt(r.time);
		r.ingredient.write(buffer);
	}
}
