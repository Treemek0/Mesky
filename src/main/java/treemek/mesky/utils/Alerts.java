package treemek.mesky.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Reference;
import treemek.mesky.handlers.Config;
import treemek.mesky.utils.Waypoints.Waypoint;

public class Alerts {

	public static class Alert {
		private String triggerMessage;
        private String displayedMessage;
        private float Time;
        
        public Alert(String trigger, String displayed, float time) {
	        this.triggerMessage = trigger;
	        this.displayedMessage = displayed;
	        this.Time = time;
        }
        
        public String getTrigger() {
            return triggerMessage;
        }

        public String getDisplay() {
            return displayedMessage;
        }
        public float getTime() {
        	return Time;
        }
    }
	
	// Method to add data
    public static void addAlert(String trigger, String display, float time) {
    	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	for (int i = 0; i < alertsList.size(); i++) {
            if (alertsList.get(i).getTrigger().equals(trigger)) {
            	alertsList.set(i, new Alert(trigger, display, time * 1000));
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Updated trigger: " + trigger));
        		player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Display: " + display));
        		player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Time: " + time + 's'));
        		Config.SaveAlert(alertsList);
            	return;
            }
    	}
    	
    	alertsList.add(new Alert(trigger, display, time * 1000));
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added: "));
		player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Trigger: " + trigger));
		player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Display: " + display));
		player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Time: " + time + 's'));
		Config.SaveAlert(alertsList);
    }
    
 // Method to add data
    public static void deleteAlert(String trigger) {
    	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	for (int i = 0; i < alertsList.size(); i++) {
    		if(alertsList.get(i).getTrigger().equals(trigger)) {
    			alertsList.remove(i);
    	        player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Deleted: " + trigger));
    			Config.SaveAlert(alertsList);
    			return;
    		}
		}
    	player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "There are no alerts with trigger: " + trigger));
    }
    
 // Method to add data
    public static void showAlert() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Alert list:"));
        for (Alert alert : alertsList) {
        	player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.DARK_AQUA + "Trigger: " + EnumChatFormatting.WHITE + alert.getTrigger()));
        	player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.DARK_AQUA + "Display: " + EnumChatFormatting.WHITE + alert.getDisplay()));
        	player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.DARK_AQUA + "Time: " + EnumChatFormatting.WHITE + alert.getTime()/20f + "s"));
        	player.addChatMessage(new ChatComponentText(""));
        }
		Config.SaveAlert(alertsList);
    }
	
	public static String appearedAlert = null;
	private float alertDisplayDuration = 1000;
	public static long alertDisplayTime = 0;
	// we must setup saving it in file
	
	public static List<Alert> alertsList = new ArrayList<>();
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onChat(ClientChatReceivedEvent event) {
		// Alerts
		for(int i = 0; i < Alerts.alertsList.size(); i++) {
			if(event.message.getUnformattedText().contains(Alerts.alertsList.get(i).getTrigger())) {
				Alerts.appearedAlert = Alerts.alertsList.get(i).getDisplay();
				Alerts.alertDisplayTime = System.currentTimeMillis();
				alertDisplayDuration = Alerts.alertsList.get(i).getTime();
				ResourceLocation soundLocation = new ResourceLocation("minecraft", "random.anvil_land");
    	        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(soundLocation));
			}
		}
	}
	
	
	 @SubscribeEvent
	    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
	        if (appearedAlert != null) {
	            renderAlert(event.resolution);
	        }
	    }

	    private void renderAlert(ScaledResolution resolution) {
	        Minecraft mc = Minecraft.getMinecraft();
	        FontRenderer fontRenderer = mc.fontRendererObj;

	        int posX = resolution.getScaledWidth() / 2;
	        int posY = resolution.getScaledHeight() / 2;
	        
	        Rendering.drawTitle(appearedAlert, resolution, 0xf54245);
	        
	     // Check if the delay duration has passed
	        if (System.currentTimeMillis() - alertDisplayTime >= alertDisplayDuration) {
	        	appearedAlert = null; // Reset appearedAlert after the delay
	            
	        }
	       
	    }
	    
}
