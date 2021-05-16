package dev.ftb.mods.sluice.integration.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

public class SluiceRecipeJS extends RecipeJS {
	private int max = 0;

	@Override
	public void create(ListJS args) {
		this.meshes(args.get(0));
		this.inputItems.add(this.parseIngredientItem(args.get(1)));
		this.json.addProperty("max", (Number) args.get(2));

		this.max = ((Number) args.get(2)).intValue();

		for (Object o : ListJS.orSelf(args.get(3))) {
			ListJS l = ListJS.orSelf(o);

			ItemStackJS i = this.parseResultItem(l.get(0));

			if (l.size() >= 2) {
				i = i.withChance(((Number) l.get(1)).doubleValue());
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

	@Override
	public void deserialize() {
		this.inputItems.add(this.parseIngredientItem(this.json.get("ingredient")));
		this.max = this.json.get("max").getAsInt();

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
			this.json.addProperty("max", this.max);
		}
	}
}
