package dev.ftb.mods.sluice.tags;

import dev.ftb.mods.sluice.SluiceMod;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public class SluiceTags {
    public static class Items {
        public static final Tag.Named<Item> HAMMERS = tag("hammers");

        private static Tag.Named<Item> tag(String name) {
            return ItemTags.bind(String.format("%s:%s", SluiceMod.MOD_ID, name));
        }
    }
}
