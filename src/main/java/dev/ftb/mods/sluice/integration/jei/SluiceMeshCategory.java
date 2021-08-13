package dev.ftb.mods.sluice.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.block.MeshType;
import dev.ftb.mods.sluice.recipe.ItemWithWeight;
import dev.ftb.mods.sluice.recipe.SluiceRecipe;
import dev.ftb.mods.sluice.util.TextUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.stream.Collectors;

public class SluiceMeshCategory implements IRecipeCategory<SluiceRecipe> {
    public static final ResourceLocation ID = new ResourceLocation(FTBSluice.MOD_ID, "sluice_jei");
    public static final ResourceLocation BACKGROUND = new ResourceLocation(FTBSluice.MOD_ID, "textures/gui/sluice_jei_background.png");

    private final static Comparator<ItemWithWeight> COMPARATOR = (a, b) -> (int) ((b.weight * 100) - (a.weight * 100));

    private final IDrawableStatic background;

    public SluiceMeshCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(BACKGROUND, 0, 0, 156, 78).setTextureSize(180, 78).build();
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
        return "Sluicing";
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
        iIngredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(Arrays.asList(sluiceRecipe.ingredient.getItems()), sluiceRecipe.meshes.stream().map(MeshType::getItemStack).collect(Collectors.toList())));
        iIngredients.setInputLists(VanillaTypes.FLUID, Collections.singletonList(Collections.singletonList(new FluidStack(sluiceRecipe.fluid, 1000))));

        List<ItemWithWeight> results = new ArrayList<>(sluiceRecipe.results);
        results.sort(COMPARATOR);

        iIngredients.setOutputs(VanillaTypes.ITEM, results.stream().map(e -> e.item).collect(Collectors.toList()));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SluiceRecipe sluiceRecipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

        itemStacks.init(0, true, 4, 4);
        itemStacks.init(1, true, 4, 23);

        ArrayList<ItemWithWeight> itemWithWeights = new ArrayList<>(sluiceRecipe.results);
        itemWithWeights.sort(COMPARATOR);

        for (int i = 0; i < itemWithWeights.size(); i++) {
            itemStacks.init(2 + i, false, 27 + (i % 7 * 18), 4 + i / 7 * 24);
        }

        fluidStacks.init(0, true, 5, 42);

        itemStacks.set(ingredients);
        fluidStacks.set(ingredients);

        itemStacks.addTooltipCallback((idx, input, stack, tooltip) -> {
            if (idx == 1 && input) {
                tooltip.add(new TranslatableComponent("ftbsluice.jei.processingTime",
                        new TextComponent(sluiceRecipe.time + "").withStyle(TextUtil.COLOUR_HIGHLIGHT))
                        .withStyle(ChatFormatting.GRAY));
            }
        });

        fluidStacks.addTooltipCallback((idx, input, stack, tooltip) -> {
            if (idx == 0 && input) {
                tooltip.add(new TranslatableComponent("ftbsluice.jei.fluidUsage",
                        new TextComponent(sluiceRecipe.mb + "").withStyle(TextUtil.COLOUR_HIGHLIGHT))
                        .withStyle(ChatFormatting.GRAY));
            }
        });
    }

    @Override
    public void draw(SluiceRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, matrixStack, mouseX, mouseY);

        ArrayList<ItemWithWeight> itemWithWeights = new ArrayList<>(recipe.results);
        itemWithWeights.sort(COMPARATOR);

        int row = 0;
        for (int i = 0; i < itemWithWeights.size(); i++) {
            if (i > 0 && i % 7 == 0) {
                row++;
            }
            matrixStack.pushPose();
            matrixStack.translate(36 + (i % 7 * 18), 23.5f + (row * 24), 100);
            matrixStack.scale(.5F, .5F, 8000F);
            Gui.drawCenteredString(matrixStack, Minecraft.getInstance().font, Math.round(itemWithWeights.get(i).weight * 100) + "%", 0, 0, 0xFFFFFF);
            matrixStack.popPose();
        }
    }
}
