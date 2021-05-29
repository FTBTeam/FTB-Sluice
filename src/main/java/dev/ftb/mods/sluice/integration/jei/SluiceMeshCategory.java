package dev.ftb.mods.sluice.integration.jei;

import dev.ftb.mods.sluice.SluiceMod;
import dev.ftb.mods.sluice.block.MeshType;
import dev.ftb.mods.sluice.block.SluiceModBlocks;
import dev.ftb.mods.sluice.recipe.SluiceRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SluiceMeshCategory implements IRecipeCategory<SluiceRecipe> {
    public static final ResourceLocation ID = new ResourceLocation(SluiceMod.MOD_ID, "sluice_jei");
    public static final ResourceLocation BACKGROUND = new ResourceLocation(SluiceMod.MOD_ID, "textures/gui/sluice_jei_background.png");

    private final IDrawableStatic background;

    public SluiceMeshCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(BACKGROUND, 0, 0, 156, 62).setTextureSize(180, 62).build();
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends SluiceRecipe> getRecipeClass() {
        return SluiceRecipe.class;
    }

    @Override
    public String getTitle() {
        return "Meshs";
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
    public void setIngredients(SluiceRecipe sluiceRecipe, IIngredients iIngredients) {
        iIngredients.setInputs(VanillaTypes.ITEM, Arrays.asList(sluiceRecipe.ingredient.getItems()));
        iIngredients.setOutputs(VanillaTypes.ITEM, sluiceRecipe.results.stream().map(e -> e.item).collect(Collectors.toList()));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SluiceRecipe sluiceRecipe, IIngredients iIngredients) {
        recipeLayout.getItemStacks().init(0, true, 4, 4);
        recipeLayout.getItemStacks().set(0, Arrays.asList(sluiceRecipe.ingredient.getItems()));

        for (int i = 0; i < sluiceRecipe.results.size(); i++) {
            recipeLayout.getItemStacks().init(1 + i, false, 27 + (i % 7 * 18), 4 + i / 7 * 18);
            recipeLayout.getItemStacks().set(1 + i, sluiceRecipe.results.get(i).item);
        }

        recipeLayout.getItemStacks().init(60, false, 4, 22);
        recipeLayout.getItemStacks().set(60, sluiceRecipe.meshes.stream().map(MeshType::getItemStack).collect(Collectors.toList()));

        recipeLayout.getItemStacks().init(61, false, 4, 40);
        recipeLayout.getItemStacks().set(61, SluiceModBlocks.SLUICES.stream().map(e -> new ItemStack(e.getKey().get())).collect(Collectors.toList()));
    }
}
