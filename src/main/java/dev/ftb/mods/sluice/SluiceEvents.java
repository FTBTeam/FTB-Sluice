package dev.ftb.mods.sluice;

import dev.ftb.mods.sluice.recipe.FTBSluiceRecipes;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = FTBSluice.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SluiceEvents {
    @SubscribeEvent
    public static void refreshRecipeCache(TagsUpdatedEvent event) {
        FTBSluiceRecipes.clearCache();
    }
}
