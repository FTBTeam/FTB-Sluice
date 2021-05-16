package dev.ftb.mods.sluice.integration.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

public class HammerRecipeJS extends RecipeJS {

	@Override
	public void create(ListJS args) {
		this.hammers(args.get(0));
		this.inputItems.add(this.parseIngredientItem(args.get(1)));

		for (Object o : ListJS.orSelf(args.get(2))) {
			ItemStackJS i = this.parseResultItem(o);
			this.outputItems.add(i);
		}
	}

	public HammerRecipeJS hammers(Object a) {
		JsonArray hammers = new JsonArray();

		for (Object o : ListJS.orSelf(a)) {
			hammers.add(o.toString());
		}

		this.json.add("hammers", hammers);
		this.save();
		return this;
	}

	@Override
	public void deserialize() {
		this.inputItems.add(this.parseIngredientItem(this.json.get("ingredient")));

		for (JsonElement e : this.json.get("results").getAsJsonArray()) {
			JsonObject o = e.getAsJsonObject();
			this.outputItems.add(this.parseResultItem(o));
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
