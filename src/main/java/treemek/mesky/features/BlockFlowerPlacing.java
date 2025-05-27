package treemek.mesky.features;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.utils.HypixelCheck;
import treemek.mesky.utils.Utils;

public class BlockFlowerPlacing {
	
	@SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
		if(SettingsConfig.BlockFlowerPlacing.isOn) {
	        if(Minecraft.getMinecraft().thePlayer != event.entityPlayer || !HypixelCheck.isOnHypixel()) return;
	        ItemStack item = event.entityPlayer.getHeldItem();
	
	        if(item != null && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
	        	String id = Utils.getSkyblockId(item);
	        	
	        	if(id == null) return;
	        	
                if(id.equalsIgnoreCase("FLOWER_OF_TRUTH")) {
                    event.setCanceled(true);
                }
                
                if(id.equalsIgnoreCase("BAT_WAND")) {
                    event.setCanceled(true);
                }
                
                if(id.equalsIgnoreCase("BOUQUET_OF_LIES")) {
                    event.setCanceled(true);
                }
	                
	        }
	    }
	}
}
	
