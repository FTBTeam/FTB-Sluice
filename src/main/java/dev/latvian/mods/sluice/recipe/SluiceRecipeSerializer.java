package dev.latvian.mods.sluice.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.sluice.block.MeshType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * @author LatvianModder
 */
public class SluiceRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SluiceRecipe>
{
	@Override
	public SluiceRecipe read(ResourceLocation recipeId, JsonObject json)
	{
		SluiceRecipe r = new SluiceRecipe(recipeId, json.has("group") ? json.get("group").getAsString() : "");

		if (json.has("weight"))
		{
			r.weight = json.get("weight").getAsInt();
		}

		if (JSONUtils.isJsonArray(json, "ingredient"))
		{
			r.ingredient = Ingredient.deserialize(JSONUtils.getJsonArray(json, "ingredient"));
		}
		else
		{
			r.ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
		}

		if (json.get("result").isJsonObject())
		{
			r.result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
		}
		else
		{
			String s1 = JSONUtils.getString(json, "result");
			ResourceLocation resourcelocation = new ResourceLocation(s1);
			r.result = new ItemStack(Registry.ITEM.getValue(resourcelocation).orElseThrow(() -> new IllegalStateException("Item: " + s1 + " does not exist")));
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
	public SluiceRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
	{
		SluiceRecipe r = new SluiceRecipe(recipeId, buffer.readString(Short.MAX_VALUE));
		r.weight = buffer.readVarInt();
		r.ingredient = Ingredient.read(buffer);
		r.result = buffer.readItemStack();

		int i = buffer.readByte() & 0xFF;

		for (MeshType t : MeshType.VALUES)
		{
			if ((i & (1 << t.ordinal())) != 0)
			{
				r.meshes.add(t);
			}
		}

		return r;
	}

	@Override
	public void write(PacketBuffer buffer, SluiceRecipe r)
	{
		buffer.writeString(r.getGroup(), Short.MAX_VALUE);
		buffer.writeVarInt(r.weight);
		r.ingredient.write(buffer);
		buffer.writeItemStack(r.result);

		int meshes = 0;

		for (MeshType t : r.meshes)
		{
			meshes |= 1 << t.ordinal();
		}

		buffer.writeByte(meshes);
	}
}
