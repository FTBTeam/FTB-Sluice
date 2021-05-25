package dev.ftb.mods.sluice.integration.jei;

import dev.ftb.mods.sluice.SluiceMod;
import dev.ftb.mods.sluice.item.SluiceModItems;
import dev.ftb.mods.sluice.recipe.NoInventory;
import dev.ftb.mods.sluice.recipe.SluiceModRecipeSerializers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.RegistryObject;

import java.util.HashSet;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final ResourceLocation SLUICE_JEI = new ResourceLocation(SluiceMod.MOD_ID, "jei");
    private static HashSet<RegistryObject<Item>> HAMMERS = new HashSet<RegistryObject<Item>>() {{
        this.add(SluiceModItems.WOODEN_HAMMER);
        this.add(SluiceModItems.STONE_HAMMER);
        this.add(SluiceModItems.IRON_HAMMER);
        this.add(SluiceModItems.GOLD_HAMMER);
        this.add(SluiceModItems.DIAMOND_HAMMER);
        this.add(SluiceModItems.NETHERITE_HAMMER);
    }};

    @Override
    public ResourceLocation getPluginUid() {
        return SLUICE_JEI;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration r) {
        r.addRecipeCategories(new SluiceHammerCategory(r.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration r) {
        Level level = Minecraft.getInstance().level;
        //		r.addRecipes(level.getRecipeManager().getRecipesFor(SluiceModRecipeSerializers.SLUICE_TYPE, NoInventory.INSTANCE, level), SluiceHammerCategory.ID);
        r.addRecipes(level.getRecipeManager().getRecipesFor(SluiceModRecipeSerializers.HAMMER_TYPE, NoInventory.INSTANCE, level), SluiceHammerCategory.ID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration r) {
        HAMMERS.forEach(hammer -> r.addRecipeCatalyst(new ItemStack(hammer.get()), SluiceHammerCategory.ID));

        // r.addRecipeCatalyst(new ItemStack(SluiceModItems.OAK_SLUICE.get()), SluiceCategory.UID);
        // r.addRecipeCatalyst(new ItemStack(SluiceModItems.IRON_SLUICE.get()), SluiceCategory.UID);
        // r.addRecipeCatalyst(new ItemStack(SluiceModItems.DIAMOND_SLUICE.get()), SluiceCategory.UID);
        // r.addRecipeCatalyst(new ItemStack(SluiceModItems.NETHERITE_SLUICE.get()), SluiceCategory.UID);
    }
}
