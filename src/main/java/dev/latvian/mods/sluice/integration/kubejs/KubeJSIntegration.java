package dev.latvian.mods.sluice.integration.kubejs;

import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.mods.sluice.SluiceMod;
import net.minecraft.resources.ResourceLocation;

public class KubeJSIntegration {
	public static void init() {
		RegisterRecipeHandlersEvent.EVENT.register(KubeJSIntegration::registerRecipeHandlers);
	}

	private static void registerRecipeHandlers(RegisterRecipeHandlersEvent event) {
		event.register(new ResourceLocation(SluiceMod.MOD_ID, "sluice"), SluiceRecipeJS::new);
	}
}
