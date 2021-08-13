package dev.ftb.mods.sluice.integration.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;

public class SluiceRecipeJS extends RecipeJS {
    @Override
    public void create(ListJS args) {
        this.json.addProperty("fluid", "minecraft:water");
        this.json.addProperty("max", 3);

        this.meshes(args.get(0));
        this.inputItems.add(this.parseIngredientItem(args.get(1)));

        for (Object o : ListJS.orSelf(args.get(2))) {
            ListJS l = ListJS.orSelf(o);

            ItemStackJS i = this.parseResultItem(l.get(0));

            if (l.size() >= 2) {
                i = i.withChance(UtilsJS.parseDouble(l.get(1), 1));
            }

            this.outputItems.add(i);
        }
    }

    public SluiceRecipeJS meshes(Object a) {
        JsonArray meshes = new JsonArray();

        for (Object o : ListJS.orSelf(a)) {
            meshes.add(o.toString());
        }

        this.json.add("meshes", meshes);
        this.save();
        return this;
    }

    public SluiceRecipeJS time(int ticks) {
        this.json.addProperty("time", ticks);
        this.save();
        return this;
    }

    public SluiceRecipeJS mb(int mb) {
        return fluidAmount(mb);
    }

    public SluiceRecipeJS fluidAmount(int mb) {
        this.json.addProperty("fluid_amount", mb);
        this.save();
        return this;
    }

    public SluiceRecipeJS fluid(String fluid) {
        this.json.addProperty("fluid", fluid);
        this.save();
        return this;
    }

    public SluiceRecipeJS max(int max) {
        this.json.addProperty("max", max);
        this.save();
        return this;
    }

    @Override
    public void deserialize() {
        this.inputItems.add(this.parseIngredientItem(this.json.get("ingredient")));

        for (JsonElement e : this.json.get("results").getAsJsonArray()) {
            JsonObject o = e.getAsJsonObject();
            this.outputItems.add(this.parseResultItem(o).withChance(o.has("chance") ? o.get("chance").getAsDouble() : 1D));
        }
    }

    @Override
    public void serialize() {
        if (this.serializeOutputs) {
            JsonArray array = new JsonArray();

            for (ItemStackJS o : this.outputItems) {
                array.add(o.toResultJson());
            }

            this.json.add("results", array);
        }

        if (this.serializeInputs) {
            this.json.add("ingredient", this.inputItems.get(0).toJson());
        }
    }
}
