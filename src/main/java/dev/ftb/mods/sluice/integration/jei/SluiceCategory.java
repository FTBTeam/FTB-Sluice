package dev.ftb.mods.sluice.integration.jei;


/**
 * @author LatvianModder
 */
public class SluiceCategory /*implements IRecipeCategory<SluiceRecipe>*/ {
	/*
	public static final ResourceLocation UID = new ResourceLocation(JarMod.MOD_ID + ":jar");

	private final IDrawable background;
	private final IDrawable icon;

	public SluiceCategory(IGuiHelper guiHelper) {
		background = guiHelper.drawableBuilder(new ResourceLocation(JarMod.MOD_ID + ":textures/gui/tempered_jar_jei.png"), 0, 0, 128, 64).setTextureSize(128, 64).build();
		icon = guiHelper.createDrawableIngredient(new ItemStack(JarModItems.TEMPERED_JAR.get()));
	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	public Class<? extends JarRecipe> getRecipeClass() {
		return JarRecipe.class;
	}

	@Override
	public String getTitle() {
		return I18n.get("block.jarmod.tempered_jar");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setIngredients(JarRecipe recipe, IIngredients ingredients) {
		List<List<ItemStack>> inputItems = new ArrayList<>();

		for (ItemIngredientPair ingredient : recipe.inputItems) {
			List<ItemStack> stackList = new ArrayList<>();

			for (ItemStack is : ingredient.ingredient.getItems()) {
				ItemStack is1 = is.copy();
				is1.setCount(ingredient.amount);
				stackList.add(is1);
			}

			inputItems.add(stackList);
		}

		ingredients.setInput(JarModIngredients.TEMPERATURE, recipe.temperature);
		ingredients.setInputLists(VanillaTypes.ITEM, inputItems);
		ingredients.setInputs(VanillaTypes.FLUID, recipe.inputFluids);
		ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputItems);
		ingredients.setOutputs(VanillaTypes.FLUID, recipe.outputFluids);
	}

	@Override
	public void setRecipe(IRecipeLayout layout, JarRecipe recipe, IIngredients ingredients) {
		IGuiIngredientGroup<Temperature> heatStacks = layout.getIngredientsGroup(JarModIngredients.TEMPERATURE);
		IGuiItemStackGroup itemStacks = layout.getItemStacks();
		IGuiFluidStackGroup fluidStacks = layout.getFluidStacks();

		heatStacks.init(0, true, 56, 40);

		for (int i = 0; i < recipe.inputItems.size(); i++) {
			itemStacks.init(i, true, 29, 3 + 20 * i);
		}

		for (int i = 0; i < recipe.outputItems.size(); i++) {
			itemStacks.init(i + recipe.inputItems.size(), false, 81, 3 + 20 * i);
		}

		SortedFluids inputFluids = new SortedFluids(64, 8000, recipe.inputFluids.size(), i -> recipe.inputFluids.get(i).getAmount());
		SortedFluids outputFluids = new SortedFluids(64, 8000, recipe.outputFluids.size(), i -> recipe.outputFluids.get(i).getAmount());

		for (int i = 0; i < recipe.inputFluids.size(); i++) {
			fluidStacks.init(i, true, 2, inputFluids.offsets[i], 22, inputFluids.heights[i], inputFluids.amounts[i], false, null);
		}

		for (int i = 0; i < recipe.outputFluids.size(); i++) {
			fluidStacks.init(i + recipe.inputFluids.size(), false, 104, outputFluids.offsets[i], 22, outputFluids.heights[i], outputFluids.amounts[i], false, null);
		}

		heatStacks.set(ingredients);
		itemStacks.set(ingredients);
		fluidStacks.set(ingredients);
	}

	@Override
	public List<Component> getTooltipStrings(JarRecipe recipe, double mouseX, double mouseY) {
		if (mouseX >= 55D && mouseY >= 3D && mouseX < 73D && mouseY < 30D) {
			return Collections.singletonList(new TranslatableComponent("jarmod.processing_time", recipe.time / 20));
		}

		return Collections.emptyList();
	}
	 */
}
