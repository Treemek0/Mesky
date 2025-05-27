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
import treemek.mesky.utils.Utils;

public class HidePlayers {
	
	static List<Entity> playerList = new ArrayList<Entity>();
	
	@SubscribeEvent
    public void onPreRenderLiving(RenderLivingEvent.Pre event) {
        if (event.entity instanceof EntityPlayer && SettingsConfig.HidePlayers.isOn) {
        	if(event.entity.getName().equals(Minecraft.getMinecraft().thePlayer.getName())) return;
        	if(Utils.isNPC(event.entity)) return;
        	if(!playerList.contains(event.entity)) playerList.add(event.entity);
            // Cancel hitbox rendering for player entities
        	setSize(event.entity, 0, 0);
            event.setCanceled(true);
        }
    }
	
	public static void resetPlayersSize() {
		for (Entity player : playerList) {
			setSize(player, 1.8f, 0.6f);
		}
	}
	
	public static void setSize(Entity entity, float width, float height) {
		entity.height = height;
		entity.width = width;
		entity.setEntityBoundingBox(new AxisAlignedBB(entity.getEntityBoundingBox().minX, entity.getEntityBoundingBox().minY, entity.getEntityBoundingBox().minZ, entity.getEntityBoundingBox().minX + (double)entity.width, entity.getEntityBoundingBox().minY + (double)entity.height, entity.getEntityBoundingBox().minZ + (double)entity.width));

	}
	
	
}


