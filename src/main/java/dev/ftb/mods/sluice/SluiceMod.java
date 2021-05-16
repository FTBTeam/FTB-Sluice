package dev.ftb.mods.sluice;

import dev.ftb.mods.sluice.block.SluiceModBlockEntities;
import dev.ftb.mods.sluice.block.SluiceModBlocks;
import dev.ftb.mods.sluice.integration.kubejs.KubeJSIntegration;
import dev.ftb.mods.sluice.item.HammerItem;
import dev.ftb.mods.sluice.item.SluiceModItems;
import dev.ftb.mods.sluice.recipe.SluiceModRecipeSerializers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(SluiceMod.MOD_ID)
public class SluiceMod {
	public static final String MOD_ID = "ftbsluice";
	public static final CreativeModeTab group = new CreativeModeTab(MOD_ID) {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(SluiceModItems.IRON_SLUICE.get());
		}
	};

	public static SluiceMod instance;

	public SluiceMod() {
		instance = this;

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SluiceConfig.COMMON_CONFIG);

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		SluiceModBlocks.REGISTRY.register(bus);
		SluiceModItems.REGISTRY.register(bus);
		SluiceModBlockEntities.REGISTRY.register(bus);
		SluiceModRecipeSerializers.REGISTRY.register(bus);

		bus.addListener(this::clientSetup);

		MinecraftForge.EVENT_BUS.register(this);

		if (ModList.get().isLoaded("kubejs")) {
			KubeJSIntegration.init();
		}
	}

	private void clientSetup(FMLClientSetupEvent event) {
		SluiceClient.init();
	}

	// Cancel the break event if we need to handle it with the hammer
	@SubscribeEvent
	public void breakEvent(BlockEvent.BreakEvent event) {
		Player player = event.getPlayer();
		if (player.getMainHandItem().getItem() instanceof HammerItem || player.getOffhandItem().getItem() instanceof HammerItem) {
			ItemStack hammer = player.getMainHandItem().getItem() instanceof HammerItem ? player.getMainHandItem() : player.getOffhandItem();
			if (SluiceModRecipeSerializers.getHammerDrops(player.level, hammer, new ItemStack(player.level.getBlockState(event.getPos()).getBlock())).size() > 0) {
				event.setCanceled(true);
				System.out.println("Rejected event");
			}
		}
	}
}
