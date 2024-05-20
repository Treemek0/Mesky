package treemek.mesky.features;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.cosmetics.CosmeticHandler;

public class HidePlayers {
	
	static List<Entity> playerList = new ArrayList<Entity>();
	
	@SubscribeEvent
    public void onPreRenderLiving(RenderLivingEvent.Pre event) {
	
        if (event.entity instanceof EntityPlayer && SettingsConfig.HidePlayers) {
        	if(event.entity.getName().equals(Minecraft.getMinecraft().thePlayer.getName())) return;
        	if(!playerList.contains(event.entity)) playerList.add(event.entity);
            // Cancel hitbox rendering for player entities
        	event.entity.height = 0;
        	event.entity.width = 0;
            event.setCanceled(true);
        }
    }
	
	public static void resetHeight() {
		for (Entity player : playerList) {
			player.height = 1.8f;
			player.width = 0.6f;
		}
		
	}
	
	
}


