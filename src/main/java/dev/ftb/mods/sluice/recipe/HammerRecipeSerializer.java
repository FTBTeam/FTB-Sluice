package dev.ftb.mods.sluice.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.ftb.mods.sluice.item.HammerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class HammerRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<HammerRecipe> {
    @Override
    public HammerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        HammerRecipe r = new HammerRecipe(recipeId, json.has("group") ? json.get("group").getAsString() : "");

        if (GsonHelper.isArrayNode(json, "ingredient")) {
            r.ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "ingredient"));
        } else {
            r.ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
        }

        for (JsonElement e : json.get("results").getAsJsonArray()) {
            JsonObject o = e.getAsJsonObject();
            r.results.add(ShapedRecipe.itemFromJson(o));
        }

        for (JsonElement e : json.get("hammers").getAsJsonArray()) {
            HammerTypes t = HammerTypes.MAP.getOrDefault(e.getAsString(), HammerTypes.WOODEN);
            r.hammers.add(t);
        }

        return r;
    }

    @Override
    public HammerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        HammerRecipe r = new HammerRecipe(recipeId, buffer.readUtf(Short.MAX_VALUE));
        r.ingredient = Ingredient.fromNetwork(buffer);
        int w = buffer.readVarInt();

        for (int i = 0; i < w; i++) {
            r.results.add(buffer.readItem());
        }

        int i = buffer.readByte() & 0xFF;

        for (HammerTypes t : HammerTypes.VALUES) {
            if ((i & (1 << t.ordinal())) != 0) {
                r.hammers.add(t);
            }
        }

        return r;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, HammerRecipe r) {
        buffer.writeUtf(r.getGroup(), Short.MAX_VALUE);
        r.ingredient.toNetwork(buffer);

        buffer.writeVarInt(r.results.size());

        for (ItemStack i : r.results) {
            buffer.writeItem(i);
        }

        int hammers = 0;

        for (HammerTypes t : r.hammers) {
            hammers |= 1 << t.ordinal();
        }

        buffer.writeByte(hammers);
    }
}
