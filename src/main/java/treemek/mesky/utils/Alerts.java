package treemek.mesky.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Reference;

public class Alerts {

	public static List<String[]> alerts = new ArrayList<String[]>();
	public static String appearedAlert = null;
	private final long alertDisplayDuration = 1000;
	public static long alertDisplayTime = 0;
	// we must setup saving it in file
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onChat(ClientChatReceivedEvent event) {
		// Alerts
		for(int i = 0; i < Alerts.alerts.size(); i++) {
			if(event.message.getUnformattedText().contains(Alerts.alerts.get(i)[0])) {
				Alerts.appearedAlert = Alerts.alerts.get(i)[1];
				Alerts.alertDisplayTime = System.currentTimeMillis();
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
	        
	        System.out.println(appearedAlert + " Sended");
	        
//	        int textWidth = fontRenderer.getStringWidth(appearedAlert);
//	        fontRenderer.drawStringWithShadow(appearedAlert, posX - textWidth / 2, posY, 0xf03d30);
	        
	        Rendering.drawTitle(appearedAlert, resolution, 0xf54245);
	        
	        
	        
	     // Check if the delay duration has passed
	        if (System.currentTimeMillis() - alertDisplayTime >= alertDisplayDuration) {
	        	appearedAlert = null; // Reset appearedAlert after the delay
	            
	        }
	       
	    }
	    
}
