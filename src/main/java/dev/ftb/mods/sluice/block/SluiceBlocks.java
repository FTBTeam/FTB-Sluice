package dev.ftb.mods.sluice.block;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.block.pump.PumpBlock;
import dev.ftb.mods.sluice.block.sluice.SluiceBlock;
import dev.ftb.mods.sluice.block.sluice.SluiceProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;


public class SluiceBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, FTBSluice.MOD_ID);

    public static final RegistryObject<Block> OAK_SLUICE = REGISTRY.register("oak_sluice", () -> new SluiceBlock(SluiceProperties.OAK));
    public static final RegistryObject<Block> IRON_SLUICE = REGISTRY.register("iron_sluice", () -> new SluiceBlock(SluiceProperties.IRON));
    public static final RegistryObject<Block> DIAMOND_SLUICE = REGISTRY.register("diamond_sluice", () -> new SluiceBlock(SluiceProperties.DIAMOND));
    public static final RegistryObject<Block> NETHERITE_SLUICE = REGISTRY.register("netherite_sluice", () -> new SluiceBlock(SluiceProperties.NETHERITE));
    public static final RegistryObject<Block> EMPOWERED_SLUICE = REGISTRY.register("empowered_sluice", () -> new SluiceBlock(SluiceProperties.EMPOWERED));

    public static final RegistryObject<Block> PUMP = REGISTRY.register("pump", PumpBlock::new);

//    public static final RegistryObject<Block> TANK = REGISTRY.register("tank", () -> new Tank(false));
//    public static final RegistryObject<Block> TANK_CREATIVE = REGISTRY.register("tank_creative", () -> new Tank(true));

    // MISC
    public static final RegistryObject<Block> DUST_BLOCK = REGISTRY.register("dust", () -> new FallingBlock(Properties.of(Material.SAND).harvestTool(ToolType.SHOVEL).strength(0.4F).sound(SoundType.SAND)));
    public static final RegistryObject<Block> CRUSHED_NETHERRACK = REGISTRY.register("crushed_netherrack", () -> new FallingBlock(Properties.of(Material.SAND, MaterialColor.NETHER).requiresCorrectToolForDrops().strength(0.35F).sound(SoundType.NETHERRACK)));
    public static final RegistryObject<Block> CRUSHED_BASALT = REGISTRY.register("crushed_basalt", () -> new FallingBlock(Properties.of(Material.SAND, MaterialColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(0.8F, 2.75F).sound(SoundType.BASALT)));
    public static final RegistryObject<Block> CRUSHED_ENDSTONE = REGISTRY.register("crushed_endstone", () -> new FallingBlock(Properties.of(Material.SAND, MaterialColor.SAND).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));

    public static final List<Pair<Supplier<Block>, String>> SLUICES = Arrays.asList(
            Pair.of(OAK_SLUICE, "oak"),
            Pair.of(IRON_SLUICE, "iron"),
            Pair.of(DIAMOND_SLUICE, "diamond"),
            Pair.of(NETHERITE_SLUICE, "netherite"),
            Pair.of(EMPOWERED_SLUICE, "empowered")
    );
}
