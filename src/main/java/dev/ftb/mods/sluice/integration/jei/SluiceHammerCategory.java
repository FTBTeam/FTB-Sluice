package dev.ftb.mods.sluice.integration.jei;

import dev.ftb.mods.sluice.SluiceMod;
import dev.ftb.mods.sluice.recipe.HammerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public class SluiceHammerCategory implements IRecipeCategory<HammerRecipe> {
    public static final ResourceLocation ID = new ResourceLocation(SluiceMod.MOD_ID, "hammers_jei");
    public static final ResourceLocation BACKGROUND = new ResourceLocation(SluiceMod.MOD_ID, "textures/gui/hammer_jei_background.png");

    private final IDrawableStatic background;
    private final IDrawableStatic slot;

    public SluiceHammerCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(BACKGROUND, 0, 0, 156, 62).setTextureSize(180, 62).build();
        this.slot = guiHelper.drawableBuilder(BACKGROUND, 161, 4, 18, 18).setTextureSize(180, 62).build();
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends HammerRecipe> getRecipeClass() {
        return HammerRecipe.class;
    }

    @Override
    public String getTitle() {
        return "Hammers";
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(HammerRecipe hammerRecipe, IIngredients iIngredients) {
        iIngredients.setInputs(VanillaTypes.ITEM, Arrays.asList(hammerRecipe.ingredient.getItems()));
        iIngredients.setOutputs(VanillaTypes.ITEM, hammerRecipe.results);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, HammerRecipe hammerRecipe, IIngredients iIngredients) {
        recipeLayout.getItemStacks().init(0, true, 4, 4);
        recipeLayout.getItemStacks().set(0, Arrays.asList(hammerRecipe.ingredient.getItems()));

        for (int i = 0; i < hammerRecipe.results.size(); i++) {
            recipeLayout.getItemStacks().init(1 + i, false, 27 + (i % 7 * 18), 4 + i / 7 * 18);
            recipeLayout.getItemStacks().set(1 + i, hammerRecipe.results.get(i));
        }
    }
}
