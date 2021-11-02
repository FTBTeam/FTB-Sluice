package dev.ftb.mods.sluice.tags;

import dev.ftb.mods.sluice.FTBSluice;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class SluiceTags {
    public static class Items {
        public static final Tag.Named<Item> HAMMERS = tag("hammers");
        public static final Tag.Named<Item> MESHES = tag("meshes");
        public static final Tag.Named<Item> WATER_BUCKETS = tag("water_buckets");
        public static final Tag.Named<Item> EMPTY_BUCKETS = tag("empty_buckets");

        private static Tag.Named<Item> tag(String name) {
            return ItemTags.bind(String.format("%s:%s", FTBSluice.MOD_ID, name));
        }
    }

    public static class Blocks {
        public static final Tag.Named<Block> SLUICES = tag("sluices");
        public static final Tag.Named<Block> AUTO_HAMMERS = tag("auto_hammers");

        private static Tag.Named<Block> tag(String name) {
            return BlockTags.bind(String.format("%s:%s", FTBSluice.MOD_ID, name));
        }
    }
}
