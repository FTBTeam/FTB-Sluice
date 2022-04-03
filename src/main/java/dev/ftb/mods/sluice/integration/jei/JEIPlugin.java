package dev.ftb.mods.sluice.integration.jei;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.block.SluiceBlocks;
import dev.ftb.mods.sluice.item.SluiceModItems;
import dev.ftb.mods.sluice.recipe.FTBSluiceRecipes;
import dev.ftb.mods.sluice.recipe.NoInventory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final ResourceLocation SLUICE_JEI = new ResourceLocation(FTBSluice.MOD_ID, "jei");
    public static List<RegistryObject<Item>> HAMMERS = Arrays.asList(
        SluiceModItems.WOODEN_HAMMER,
        SluiceModItems.STONE_HAMMER,
        SluiceModItems.IRON_HAMMER,
        SluiceModItems.GOLD_HAMMER,
        SluiceModItems.DIAMOND_HAMMER,
        SluiceModItems.NETHERITE_HAMMER
    );
    public static List<RegistryObject<BlockItem>> AUTO_HAMMERS = Arrays.asList(
        SluiceModItems.IRON_AUTO_HAMMER,
        SluiceModItems.GOLD_AUTO_HAMMER,
        SluiceModItems.DIAMOND_AUTO_HAMMER,
        SluiceModItems.NETHERITE_AUTO_HAMMER
    );

    @Override
    public ResourceLocation getPluginUid() {
        return SLUICE_JEI;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration r) {
        r.addRecipeCategories(new SluiceHammerCategory(r.getJeiHelpers().getGuiHelper()));
        r.addRecipeCategories(new SluiceMeshCategory(r.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration r) {
        Level level = Minecraft.getInstance().level;
        r.addRecipes(level.getRecipeManager().getRecipesFor(FTBSluiceRecipes.HAMMER_TYPE, NoInventory.INSTANCE, level), SluiceHammerCategory.ID);
        r.addRecipes(level.getRecipeManager().getRecipesFor(FTBSluiceRecipes.SLUICE_TYPE, NoInventory.INSTANCE, level), SluiceMeshCategory.ID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration r) {
        HAMMERS.forEach(hammer -> r.addRecipeCatalyst(new ItemStack(hammer.get()), SluiceHammerCategory.ID));
        AUTO_HAMMERS.forEach(hammer -> r.addRecipeCatalyst(new ItemStack(hammer.get()), SluiceHammerCategory.ID));
        SluiceBlocks.ALL_SLUICES.forEach(e -> r.addRecipeCatalyst(new ItemStack(e.getKey().get()), SluiceMeshCategory.ID));
    }
}
