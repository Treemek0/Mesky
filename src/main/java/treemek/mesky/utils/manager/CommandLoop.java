package treemek.mesky.utils.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import treemek.mesky.utils.Utils;

public class CommandLoop {
	public static class Loop {
		long time = 0;
		String command = "";
		
		long lastTime = 0;
		
		public Loop(long time, String command) {
			this.time = time;
			this.command = command;
		}
	}
	
	private static Loop current;
	
	@SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
		if(current == null) return;
		if(Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null) return;
		if (event.phase == TickEvent.Phase.START) {
			if(System.currentTimeMillis() - current.lastTime >= current.time) {
				current.lastTime = System.currentTimeMillis();
				
				Utils.executeCommand(current.command);
			}
		}
	}
	
	public static void setCurrentLoop(Loop loop) {
		current = loop;
	}
	
    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
    	if(current == null) return;
    	if (event.world.isRemote && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (player == Minecraft.getMinecraft().thePlayer) {
            	current = null;
            	Utils.addMinecraftMessageWithPrefix("Changing worlds detected, command loop set to null");
            }
        }
    }
}
