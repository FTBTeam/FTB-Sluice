package dev.ftb.mods.sluice;

import dev.ftb.mods.sluice.recipe.SluiceModRecipeSerializers;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = SluiceMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SluiceModEventHandler {
	@SubscribeEvent
	public static void refreshRecipeCache(TagsUpdatedEvent event) {
		SluiceModRecipeSerializers.clearCache();
	}
}
