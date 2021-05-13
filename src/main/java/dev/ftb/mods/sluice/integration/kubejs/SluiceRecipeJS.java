package dev.ftb.mods.sluice.integration.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

public class SluiceRecipeJS extends RecipeJS {
	@Override
	public void create(ListJS args) {
		meshes(args.get(0));
		inputItems.add(parseIngredientItem(args.get(1)));

		for (Object o : ListJS.orSelf(args.get(2))) {
			ListJS l = ListJS.orSelf(o);

			ItemStackJS i = parseResultItem(l.get(0));

			if (l.size() >= 2) {
				i = i.withChance(((Number) l.get(1)).doubleValue());
			}

			outputItems.add(i);
		}
	}

	public SluiceRecipeJS meshes(Object a) {
		JsonArray meshes = new JsonArray();

		for (Object o : ListJS.orSelf(a)) {
			meshes.add(o.toString());
		}

		json.add("meshes", meshes);
		save();
		return this;
	}

	@Override
	public void deserialize() {
		inputItems.add(parseIngredientItem(json.get("ingredient")));

		for (JsonElement e : json.get("results").getAsJsonArray()) {
			JsonObject o = e.getAsJsonObject();
			outputItems.add(parseResultItem(o).withChance(o.has("chance") ? o.get("chance").getAsDouble() : 1D));
		}
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			JsonArray array = new JsonArray();

			for (ItemStackJS o : outputItems) {
				array.add(o.toResultJson());
			}

			json.add("results", array);
		}

		if (serializeInputs) {
			json.add("ingredient", inputItems.get(0).toJson());
		}
	}
}
