package treemek.mesky.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import treemek.mesky.Commands;
import treemek.mesky.features.FishingTimer;
import treemek.mesky.handlers.gui.GUI;

public class EventHandler{
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onChat(ClientChatReceivedEvent event) {
		
	}
	
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		// for opening gui, because i have to wait 1 tick before opening it
		if (Commands.opengui == true) {
            Minecraft.getMinecraft().displayGuiScreen(new GUI());
            Commands.opengui = false;
		}
	}
	
	
}
