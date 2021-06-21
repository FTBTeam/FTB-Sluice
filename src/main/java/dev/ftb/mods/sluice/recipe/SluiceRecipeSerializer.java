package dev.ftb.mods.sluice.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.ftb.mods.sluice.block.MeshType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Objects;

public class SluiceRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<SluiceRecipe> {
    @Override
    public SluiceRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        SluiceRecipe r = new SluiceRecipe(recipeId, json.has("group") ? json.get("group").getAsString() : "");

        r.fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(json.get("fluid").getAsString()));
        r.ingredient = Ingredient.fromJson(json.get("ingredient"));

        r.max = json.get("max").getAsInt();

        for (JsonElement e : json.get("results").getAsJsonArray()) {
            JsonObject o = e.getAsJsonObject();
            r.results.add(new ItemWithWeight(ShapedRecipe.itemFromJson(o), o.get("chance").getAsDouble()));
        }

        for (JsonElement e : json.get("meshes").getAsJsonArray()) {
            MeshType t = MeshType.MAP.getOrDefault(e.getAsString(), MeshType.NONE);

            if (t != MeshType.NONE) {
                r.meshes.add(t);
            }
        }

        return r;
    }

    @Override
    public SluiceRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        SluiceRecipe r = new SluiceRecipe(recipeId, buffer.readUtf(Short.MAX_VALUE));
        r.fluid = ForgeRegistries.FLUIDS.getValue(buffer.readResourceLocation());
        r.ingredient = Ingredient.fromNetwork(buffer);
        r.max = buffer.readVarInt();

        int w = buffer.readVarInt();

        for (int i = 0; i < w; i++) {
            r.results.add(new ItemWithWeight(buffer.readItem(), buffer.readDouble()));
        }

        int i = buffer.readByte() & 0xFF;

        for (MeshType t : MeshType.VALUES) {
            if ((i & (1 << t.ordinal())) != 0) {
                r.meshes.add(t);
            }
        }

        return r;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, SluiceRecipe r) {
        buffer.writeUtf(r.getGroup(), Short.MAX_VALUE);
        buffer.writeResourceLocation(r.fluid.getRegistryName());

        r.ingredient.toNetwork(buffer);

        buffer.writeVarInt(r.max);
        buffer.writeVarInt(r.results.size());

        for (ItemWithWeight i : r.results) {
            buffer.writeItem(i.item);
            buffer.writeDouble(i.weight);
        }

        int meshes = 0;

        for (MeshType t : r.meshes) {
            meshes |= 1 << t.ordinal();
        }

        buffer.writeByte(meshes);
    }
}
