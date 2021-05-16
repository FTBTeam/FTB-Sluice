package dev.ftb.mods.sluice.block;

import dev.ftb.mods.sluice.SluiceMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;


public class SluiceModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, SluiceMod.MOD_ID);

	public static final RegistryObject<Block> OAK_SLUICE = REGISTRY.register("oak_sluice", SluiceBlock::new);
	public static final RegistryObject<Block> IRON_SLUICE = REGISTRY.register("iron_sluice", SluiceBlock::new);
	public static final RegistryObject<Block> DIAMOND_SLUICE = REGISTRY.register("diamond_sluice", SluiceBlock::new);
	public static final RegistryObject<Block> NETHERITE_SLUICE = REGISTRY.register("netherite_sluice", SluiceBlock::new);

	public static final RegistryObject<Block> TAP = REGISTRY.register("tap", TapBlock::new);
	public static final RegistryObject<Block> TANK = REGISTRY.register("tank", () -> new Tank(false));
	public static final RegistryObject<Block> TANK_CREATIVE = REGISTRY.register("tank_creative", () -> new Tank(true));

	// MISC
	public static final RegistryObject<Block> DUST_BLOCK = REGISTRY.register("dust", () -> new Block(BlockBehaviour.Properties.of(Material.SAND).harvestTool(ToolType.SHOVEL).strength(0.5F).sound(SoundType.SAND)));

	public static final List<Pair<Supplier<Block>, String>> SLUICES = Arrays.asList(
			Pair.of(OAK_SLUICE, "oak"),
			Pair.of(IRON_SLUICE, "iron"),
			Pair.of(DIAMOND_SLUICE, "diamond"),
			Pair.of(NETHERITE_SLUICE, "netherite")
	);
}
