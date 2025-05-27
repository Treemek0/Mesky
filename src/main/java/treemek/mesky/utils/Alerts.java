package treemek.mesky.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.alerts.AlertElement;
import treemek.mesky.handlers.gui.alerts.AlertPosition;
import treemek.mesky.handlers.soundHandler.Sound;
import treemek.mesky.handlers.soundHandler.SoundsHandler;
import treemek.mesky.utils.Waypoints.Waypoint;

public class Alerts extends GuiScreen {

	public static class Alert {
		private String triggerMessage;
        private String displayedMessage;
        private Float time; // [ms]
        public Boolean onlyParty;
        public Boolean ignorePlayers;
        public Boolean isEqual;
        public Boolean enabled;
        public Float[] position = new Float[] {50f,50f};
        public Float scale = 1f;
        public String sound;
        public Float volume;
        public Float pitch;
        
        public Alert(String trigger, String displayed, float time, boolean onlyParty, boolean ignorePlayers, boolean isEqual, Float[] position, Float scale, String sound, float volume, float pitch, boolean enabled) {
	        this.triggerMessage = trigger;
	        this.displayedMessage = displayed;
	        this.time = time;
	        this.onlyParty = onlyParty;
	        this.ignorePlayers = ignorePlayers;
	        this.isEqual = isEqual;
	        this.position = position;
	        this.scale = scale;
	        this.sound = sound;
	        this.volume = volume;
	        this.pitch = pitch;
	        this.enabled = enabled;
        }
        
        public void fixNulls() {
        	if(triggerMessage == null) triggerMessage = "";
        	if(displayedMessage == null) displayedMessage = "";
        	if(time == null) time = 1000f;
        	if(onlyParty == null) onlyParty = false;
        	if(ignorePlayers == null) ignorePlayers = false;
        	if(isEqual == null) isEqual = false;
        	if(enabled == null) enabled = true;
        	if(position == null) position = new Float[] {50f,50f};
        	if(scale == null) scale = 1f;
        	if(sound == null) sound = "minecraft:random.anvil_land";
        	if(volume == null) volume = 1f;
        	if(pitch == null) pitch = 1f;
        }
        
        public String getSound() {
        	return sound;
        }
        
        public String getTrigger() {
            return triggerMessage;
        }

        public String getDisplay() {
            return displayedMessage;
        }
        public float getTime() {
        	return time;
        }

		public boolean getOnlyParty() {
			return onlyParty;
		}
		
		public boolean getIgnorePlayers() {
        	return ignorePlayers;
        }
        public boolean getIsEqual() {
        	return isEqual;
        }
    }
	
	// alert renderinginfo
	public static class AlertRenderInfo {
        public String message;
        private float time; // [ms]
        private long startTime;
        public ResourceLocation location;
        public BufferedImage bufferedImage;
        public Float[] position;
        public float scale;
        
        public AlertRenderInfo(String displayed, float time, long startTime, ResourceLocation location, BufferedImage buffered, Float[] position, float scale) {
	        this.message = displayed;
	        this.time = time;
	        this.startTime = startTime;
	        this.location = location;
	        this.bufferedImage = buffered;
	        this.position = position;
	        this.scale = scale;
        }

    }
	
	// Method to add data
    public static void addAlert(String trigger, String display, float time, Float[] position, float scale) {
    	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	
    	alertsList.add(new Alert(trigger, display, time * 1000, false, false, false, position, scale, "minecraft:random.anvil_land", 1, 1, true));
		ConfigHandler.SaveAlert(alertsList);
    }
    
 // Method to add data
    public static void deleteAlert(int id) {
    	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Deleted: " + alertsList.get(id).getTrigger()));
		alertsList.remove(id);
        ConfigHandler.SaveAlert(alertsList);
		return;
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
		ConfigHandler.SaveAlert(alertsList);
    }
	
	public static List<Alert> alertsList = new ArrayList<>();

    // queue to render (but its list not queue lol)
    public static List<AlertRenderInfo> renderingQueue = new ArrayList<>();

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChat(ClientChatReceivedEvent event) {
		if(Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.isRemote && event.type == 0) {
			// Alerts
			if(event.isCanceled()) return;
			
			String message = event.message.getUnformattedText();
			String nickname = Minecraft.getMinecraft().thePlayer.getName();
			String nonColorMessage = StringUtils.stripControlCodes(message);
			String onlyNonColorMessage = nonColorMessage;
			boolean autor = false;
			
			if(nonColorMessage.contains(": ")){
				onlyNonColorMessage = nonColorMessage.substring(nonColorMessage.indexOf(":") + 1);
				String[] beforeMessage = nonColorMessage.split(":")[0].split(" ");
				
				for (String string : beforeMessage) {
					if(string.equals(nickname) || string.equals(nickname + ":")) autor = true;
				}
			}
			
			
			for(int i = 0; i < alertsList.size(); i++) {
				if(!alertsList.get(i).enabled) continue;
				if(alertsList.get(i).triggerMessage.equals("")) continue;
				if(alertsList.get(i).getIsEqual()) {
					if(onlyNonColorMessage.equals(alertsList.get(i).getTrigger())) {
						if(alertsList.get(i).getOnlyParty() && !nonColorMessage.startsWith("Party >")) return;
						if(alertsList.get(i).getIgnorePlayers() && nonColorMessage.contains(": ")) return;
						if(autor) return;
	
						try {
							registerAlert(alertsList.get(i));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else {
					if(message.contains(alertsList.get(i).getTrigger())) {
						if(alertsList.get(i).getOnlyParty() && !nonColorMessage.startsWith("Party >")) return;
						if(alertsList.get(i).getIgnorePlayers() && nonColorMessage.contains(": ")) return;
						if(autor) return; // ignores messages written by yourself
						
						try {
							registerAlert(alertsList.get(i));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	
	private void registerAlert(Alert alert) {
		String message = alert.getDisplay();
		
		if(isImage(alert.displayedMessage)) { // rendering images
			getTextureFromCache(alert); // image
		}else { // text
			SoundsHandler.playSound(alert.sound, alert.volume, alert.pitch);
			
		    if(!renderingQueue.contains(new AlertRenderInfo(message,  alert.getTime(), System.currentTimeMillis(), null, null,  alert.position, alert.scale))) {
		    	renderingQueue.add(new AlertRenderInfo(message,  alert.getTime(), System.currentTimeMillis(), null, null,  alert.position, alert.scale));
		    }
		}
	}
	
	public static void DisplayCustomAlert(String display, int dur, int soundIteration, Float[] position, float scale, ResourceLocation soundLocation, float pitch) {
		AlertRenderInfo info = new AlertRenderInfo(display, dur, System.currentTimeMillis(), null, null, position, scale);
		renderingQueue.add(info);
		
		if(soundLocation == null) return;
		for (int i = 0; i < soundIteration; i++) {
			 Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(soundLocation, pitch));
		}
	}
	
	public static void DisplayCustomAlert(String display, int dur, Float[] position, float scale) {
		AlertRenderInfo info = new AlertRenderInfo(display, dur, System.currentTimeMillis(), null, null, position, scale);
		renderingQueue.add(info);     
	}
	
	 @SubscribeEvent
	    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
		 // its done this way because u cant remove element from list while its iterating
		 Iterator<AlertRenderInfo> iterator = new ArrayList<>(renderingQueue).iterator();
		 while (iterator.hasNext()) {
			 	AlertRenderInfo info = iterator.next();
		        String alert = info.message;
		        
			    // Check if the delay duration has passed and delete alert from renderingQueue
		        if (System.currentTimeMillis() - info.startTime >= info.time) {
		            renderingQueue.remove(info);
		        }
			    
        		if(info.bufferedImage != null && info.location != null) {
        			renderAlertImage(event.resolution, info);
	        	}else {
	        		renderAlertText(event.resolution, info);
	        	}
			}
	    }

	    private void renderAlertText(ScaledResolution resolution, AlertRenderInfo info) {
	        Minecraft mc = Minecraft.getMinecraft();
	        FontRenderer fontRenderer = mc.fontRendererObj;

	        int posX = (int) (resolution.getScaledWidth() * ((float)(info.position[0])/100));
	        int posY = (int) (resolution.getScaledHeight() * ((float)(info.position[1])/100));
	        
	        RenderHandler.drawAlertText(ColorUtils.getColoredText(info.message), resolution, 0xffffff, info.scale, posX, posY);
	    }
	    
	    private void renderAlertImage(ScaledResolution resolution, AlertRenderInfo info) {
	        Minecraft mc = Minecraft.getMinecraft();
	        FontRenderer fontRenderer = mc.fontRendererObj;
	        
	        int posX = (int) (resolution.getScaledWidth() * ((float)(info.position[0])/100));
	        int posY = (int) (resolution.getScaledHeight() * ((float)(info.position[1])/100));
	        
            // Draw the texture on the screen
            int imgHeight = (int) (info.bufferedImage.getHeight() * (((double)resolution.getScaledHeight() / (double)info.bufferedImage.getHeight()) * info.scale));
            double ratio = (double)info.bufferedImage.getWidth() / (double)info.bufferedImage.getHeight() ;
            int imgWidth = (int) (imgHeight * ratio);
           
            // Calculate opacity based on elapsed time
            long elapsedTime = System.currentTimeMillis() - info.startTime;
            long maxOpacityTime = (long) Math.min(250, 0.25*info.time);
            long maxOpacityEndTime = (long) Math.max(info.time-250, 0.75*info.time);
            float opacity = RenderHandler.calculateOpacity(elapsedTime, (long)info.time, maxOpacityTime, maxOpacityEndTime);
            

            // Render the image
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, opacity);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableDepth();
            
            Minecraft.getMinecraft().renderEngine.bindTexture(info.location);
            drawModalRectWithCustomSizedTexture(posX - imgWidth / 2, posY - imgHeight / 2, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
	    }
	    
	    
	    
	   
	    

	    public void getTextureFromCache(Alert alert) {
	        if (!ImageCache.bufferedTextureCache.containsKey(alert.displayedMessage)) {
	            // If the texture is not in the cache, load it
	        	if(alert.displayedMessage.startsWith("https:") || alert.displayedMessage.startsWith("http:")) {
	        		ImageCache.downloadImageFromInternet(alert.displayedMessage, "Alerts " + EnumChatFormatting.DARK_AQUA + "('" + alert.triggerMessage + "')");
	        	}else {
	        		ImageCache.downloadImageFromFile(alert.displayedMessage, "Alerts " + EnumChatFormatting.DARK_AQUA + "('" + alert.triggerMessage + "')");
	        	}
	        }
	        
	        // Return the texture from the cache
	        BufferedImage buff = ImageCache.bufferedTextureCache.getOrDefault(alert.displayedMessage, null);
	        
	        if(buff != null) {
	        	ResourceLocation location = ImageCache.resourceLocationCache.getOrDefault(buff, null);
	        	SoundsHandler.playSound(alert.sound, alert.volume, alert.pitch);
	            
	            // adds alert to rendering queue so it can render
	            if(!renderingQueue.contains(new AlertRenderInfo(alert.displayedMessage, alert.time, System.currentTimeMillis(), location, buff, alert.position, alert.scale))) {
	            	renderingQueue.add(new AlertRenderInfo(alert.displayedMessage, alert.time, System.currentTimeMillis(), location, buff, alert.position, alert.scale));
	            }
	        }else {
	        	DisplayCustomAlert("Alert is still downloading...", 3000, new Float[] {50f, 50f}, 2);
	        }
	    }
	    
	    public static void putAllImagesToCache() {
	    	// downloads alert images while loading game so when you want to get them they dont have to download (it sometimes took 5s to receive alert before this)
	    	List<String> paths = new ArrayList();
	    	
	    	for (Alert alert : alertsList) {
				if(isImage(alert.displayedMessage)) {
					paths.add(alert.displayedMessage);
					if(!ImageCache.bufferedTextureCache.containsKey(alert.displayedMessage)) {
						if(alert.displayedMessage.startsWith("https:") || alert.displayedMessage.startsWith("http:")) {
							ImageCache.downloadImageFromInternet(alert.displayedMessage, "Alerts " + EnumChatFormatting.DARK_AQUA + "('" + alert.triggerMessage + "')");
			        	}else {
			        		ImageCache.downloadImageFromFile(alert.displayedMessage, "Alerts " + EnumChatFormatting.DARK_AQUA + "('" + alert.triggerMessage + "')");
			        	}
					}
				}
			}
	    	
	    	// deleting all unused alert images (while changing them or something idk)
	    	for (Map.Entry<String, BufferedImage> cache : ImageCache.bufferedTextureCache.entrySet()) {
	    	    String path = cache.getKey();
	    	    BufferedImage buff = cache.getValue();
	    	    
	    	    if(!paths.contains(path)) {
	    	    	ImageCache.bufferedTextureCache.remove(path);
	    	    	Minecraft.getMinecraft().getTextureManager().deleteTexture(ImageCache.resourceLocationCache.get(buff));
	    	    	ImageCache.resourceLocationCache.remove(buff);
	    	    }
	    	}
	    }
	    
	    public static void editAlertPositionAndScale(Float[] position, float scale, int i, List<AlertElement> alerts) {
	    	String alert = alerts.get(i).display.getText();
	    	
    		if(isImage(alert)) {
    			if(!ImageCache.bufferedTextureCache.containsKey(alert)) {
					if(alert.startsWith("https:") || alert.startsWith("http:")) {
						ImageCache.downloadImageFromInternet(alert, "Alerts " + EnumChatFormatting.DARK_AQUA + "('" + "edit" + "')");
		        	}else {
		        		ImageCache.downloadImageFromFile(alert, "Alerts " + EnumChatFormatting.DARK_AQUA + "(" + "edit" + ")");
		        	}
    			}
			}
    
		    // Return the texture from the cache
	        BufferedImage buff = ImageCache.bufferedTextureCache.getOrDefault(alert, null);
	        
	        if(buff != null) {
	        	ResourceLocation location = ImageCache.resourceLocationCache.getOrDefault(buff, null);
	        	
	        	AlertPosition.alertInfo = new AlertRenderInfo(alert, 0, 0, location, buff, position, scale);
	        	AlertPosition.alert = alerts.get(i);
	        	AlertPosition.alertsGUI = alerts;
	        	GuiHandler.GuiType = new AlertPosition();
	        }else {
	        	AlertPosition.alertInfo = new AlertRenderInfo(alert, 0, 0, null, null, position, scale);
	        	AlertPosition.alert =  alerts.get(i);
	        	AlertPosition.alertsGUI = alerts;
	        	GuiHandler.GuiType = new AlertPosition();
	        }

	    }
	    
	    
	    private static boolean isImage(String alert) {
	    	if((alert.contains(".png") || alert.contains(".jpg") || alert.contains(".jpeg") || alert.contains(".avif")) && (alert.contains("/") || alert.contains("\\"))) {
	    		return true;
	    	}
	    	
	    	return false;
	    }
}
