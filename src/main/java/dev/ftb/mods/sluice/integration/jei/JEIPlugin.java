package dev.ftb.mods.sluice.integration.jei;

import dev.ftb.mods.sluice.SluiceMod;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
	public static final ResourceLocation SLUICE_JEI = new ResourceLocation(SluiceMod.MOD_ID, "jei");

	@Override
	public ResourceLocation getPluginUid() {
		return SLUICE_JEI;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration r) {
		// r.addRecipeCategories(new SluiceCategory(r.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration r) {
		// Level level = Minecraft.getInstance().level;
		// r.addRecipes(level.getRecipeManager().getRecipesFor(SluiceModRecipeSerializers.SLUICE_TYPE, NoInventory.INSTANCE, level), SluiceCategory.UID);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration r) {
		// r.addRecipeCatalyst(new ItemStack(SluiceModItems.OAK_SLUICE.get()), SluiceCategory.UID);
		// r.addRecipeCatalyst(new ItemStack(SluiceModItems.IRON_SLUICE.get()), SluiceCategory.UID);
		// r.addRecipeCatalyst(new ItemStack(SluiceModItems.DIAMOND_SLUICE.get()), SluiceCategory.UID);
		// r.addRecipeCatalyst(new ItemStack(SluiceModItems.NETHERITE_SLUICE.get()), SluiceCategory.UID);
	}
}
