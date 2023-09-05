package treemek.mesky.features;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.utils.HypixelCheck;

public class BlockFlowerPlacing {
	
	@SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
		if(SettingsConfig.BlockFlowerPlacing) {
	        if(Minecraft.getMinecraft().thePlayer != event.entityPlayer || !HypixelCheck.isOnHypixel) return;
	        ItemStack item = event.entityPlayer.getHeldItem();
	
	        if(item != null && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
	                if(item.getDisplayName().contains("Flower of Truth")) {
	                    event.setCanceled(true);
	                }
	                if(item.getDisplayName().contains("Spirit Sceptre")) {
	                    event.setCanceled(true);
	                }
	        }
	    }
	}
}
	
