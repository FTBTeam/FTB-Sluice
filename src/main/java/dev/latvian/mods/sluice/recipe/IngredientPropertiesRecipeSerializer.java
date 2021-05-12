//package dev.latvian.mods.sluice.recipe;
//
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import dev.latvian.mods.sluice.block.MeshType;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.GsonHelper;
//import net.minecraft.world.item.crafting.Ingredient;
//import net.minecraft.world.item.crafting.RecipeSerializer;
//import net.minecraftforge.registries.ForgeRegistryEntry;
//
///**
// * @author LatvianModder
// */
//public class IngredientPropertiesRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<IngredientPropertiesRecipe> {
//	@Override
//	public IngredientPropertiesRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
//		IngredientPropertiesRecipe r = new IngredientPropertiesRecipe(recipeId, json.has("group") ? json.get("group").getAsString() : "");
//
//		if (json.has("no_item_weight")) {
//			r.noItemWeight = json.get("no_item_weight").getAsInt();
//		}
//
//		if (json.has("time")) {
//			r.time = json.get("time").getAsInt();
//		}
//
//		if (GsonHelper.isArrayNode(json, "ingredient")) {
//			r.ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "ingredient"));
//		} else {
//			r.ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
//		}
//
//		if (json.has("meshes")) {
//			for (JsonElement e : json.get("meshes").getAsJsonArray()) {
//				MeshType t = MeshType.MAP.getOrDefault(e.getAsString(), MeshType.NONE);
//
//				if (t != MeshType.NONE) {
//					r.meshes.add(t);
//				}
//			}
//		} else {
//			for (MeshType t : MeshType.VALUES) {
//				if (t != MeshType.NONE) {
//					r.meshes.add(t);
//				}
//			}
//		}
//
//		return r;
//	}
//
//	@Override
//	public IngredientPropertiesRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
//		IngredientPropertiesRecipe r = new IngredientPropertiesRecipe(recipeId, buffer.readUtf(Short.MAX_VALUE));
//		r.noItemWeight = buffer.readVarInt();
//		r.time = buffer.readVarInt();
//		r.ingredient = Ingredient.fromNetwork(buffer);
//		return r;
//	}
//
//	@Override
//	public void toNetwork(FriendlyByteBuf buffer, IngredientPropertiesRecipe r) {
//		buffer.writeUtf(r.getGroup(), Short.MAX_VALUE);
//		buffer.writeVarInt(r.noItemWeight);
//		buffer.writeVarInt(r.time);
//		r.ingredient.toNetwork(buffer);
//	}
//}
