package dev.ftb.mods.sluice.integration.kubejs;

import dev.ftb.mods.sluice.FTBSluice;
import dev.latvian.kubejs.KubeJSPlugin;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import net.minecraft.resources.ResourceLocation;

public class KubeJSIntegration extends KubeJSPlugin {
    @Override
    public void addRecipes(RegisterRecipeHandlersEvent event) {
        event.register(new ResourceLocation(FTBSluice.MOD_ID, "sluice"), SluiceRecipeJS::new);
        event.register(new ResourceLocation(FTBSluice.MOD_ID, "hammer"), HammerRecipeJS::new);
    }
}
