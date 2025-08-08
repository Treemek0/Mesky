package treemek.mesky.listeners;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.handlers.gui.warp.fasttravel.WarpGui;

public class WarpErrorListener {
	
	@SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
		if(Minecraft.getMinecraft().currentScreen != null) {
			if(Minecraft.getMinecraft().currentScreen instanceof WarpGui) {
				if(Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.isRemote && event.type == 0) {
					String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
					
					if(message.contains(":")) return;
					
					if(message.contains("Couldn't warp you! Try again later.")) {
						WarpGui.popup.showPopup("Teleport cooldown. Try again later.");
						event.setCanceled(true);
					}
					
					if(message.contains("You haven't unlocked this fast travel destination!")) {
						WarpGui.popup.showPopup("Teleport not unlocked", 1500);
						event.setCanceled(true);
					}
					
					if(message.contains("You need to have visited this island at least once before fast traveling to it!")) {
						WarpGui.popup.showPopup("Island haven't been visited. Can't teleport", 1500);
						event.setCanceled(true);
					}
				}
			}
		}
	}
}
