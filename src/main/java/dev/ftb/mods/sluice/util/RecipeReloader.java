package dev.ftb.mods.sluice.util;

import dev.ftb.mods.sluice.recipe.FTBSluiceRecipes;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class RecipeReloader implements ResourceManagerReloadListener {

    private final ServerResources res;

    public RecipeReloader(ServerResources res) {
        this.res = res;
    }

    @Override
    public void onResourceManagerReload(ResourceManager rm) {
        FTBSluiceRecipes.refreshCaches(res.getRecipeManager());
    }
}
