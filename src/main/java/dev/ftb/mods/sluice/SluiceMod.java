package dev.ftb.mods.sluice;

import dev.ftb.mods.sluice.block.SluiceModBlockEntities;
import dev.ftb.mods.sluice.block.SluiceModBlocks;
import dev.ftb.mods.sluice.integration.kubejs.KubeJSIntegration;
import dev.ftb.mods.sluice.item.HammerItem;
import dev.ftb.mods.sluice.item.SluiceModItems;
import dev.ftb.mods.sluice.recipe.SluiceModRecipeSerializers;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;


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
		Level level = player.level;
		if (player.getMainHandItem().getItem() instanceof HammerItem || player.getOffhandItem().getItem() instanceof HammerItem) {
			ItemStack hammer = player.getMainHandItem().getItem() instanceof HammerItem ? player.getMainHandItem() : player.getOffhandItem();
			BlockState blockState = player.level.getBlockState(event.getPos());
			List<ItemStack> hammerDrops = SluiceModRecipeSerializers.getHammerDrops(player.level, hammer, new ItemStack(blockState.getBlock()));
			if (hammerDrops.size() > 0) {
				event.setCanceled(true);

//				player.level.removeBlock(event.getPos(), false);

				ItemStack itemstack = player.getMainHandItem();
				ItemStack itemstack1 = itemstack.copy();
				boolean flag1 = blockState.canHarvestBlock(level, event.getPos(), player);
				itemstack.mineBlock(level, blockState, event.getPos(), player);
				if (itemstack.isEmpty() && !itemstack1.isEmpty()) {
					ForgeEventFactory.onPlayerDestroyItem(player, itemstack1, InteractionHand.MAIN_HAND);
				}

				boolean flag = level.removeBlock(event.getPos(), flag1);
				hammerDrops.forEach(e -> Containers.dropItemStack(level, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), e.copy()));
//				if (flag && flag1) {
//					BlockEntity tileentity = this.level.getBlockEntity(p_180237_1_);
//					blockState.getBlock().playerDestroy(level, player, event.getPos(), blockState, tileentity, itemstack1);
//				}

//				if (flag && exp > 0) {
//					blockstate.getBlock().popExperience(this.level, p_180237_1_, exp);
//				}
			}
		}
	}
}
