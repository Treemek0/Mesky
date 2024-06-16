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
import treemek.mesky.handlers.gui.alerts.AlertPosition;
import treemek.mesky.utils.Waypoints.Waypoint;

public class Alerts extends GuiScreen {

	public static class Alert {
		private String triggerMessage;
        private String displayedMessage;
        private float time; // [ms]
        private boolean onlyParty;
        public boolean ignorePlayers;
        public boolean isEqual; // will be added but it requires fixing all code in AlertGui (and i dont have time for that rn)
        public int[] position = new int[] {50,50};
        public Float scale = 1f;
        
        public Alert(String trigger, String displayed, float time, boolean onlyParty, boolean ignorePlayers, boolean isEqual, int[] position, Float scale) {
	        this.triggerMessage = trigger;
	        this.displayedMessage = displayed;
	        this.time = time;
	        this.onlyParty = onlyParty;
	        this.ignorePlayers = ignorePlayers;
	        this.isEqual = isEqual;
	        this.position = position;
	        this.scale = scale;
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
        public int[] position;
        public float scale;
        
        public AlertRenderInfo(String displayed, float time, long startTime, ResourceLocation location, BufferedImage buffered, int[] position, float scale) {
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
    public static void addAlert(String trigger, String display, float time, int[] position, float scale) {
    	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	
    	alertsList.add(new Alert(trigger, display, time * 1000, false, false, false, position, scale));
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
	
	 // Texture cache to store image path -> texture ID mappings
    static Map<String, BufferedImage> bufferedTextureCache = new ConcurrentHashMap<>();
    static Map<BufferedImage, ResourceLocation> resourceLocationCache = new ConcurrentHashMap<>();
    
    // queue to render (but its list not queue lol)
    public static List<AlertRenderInfo> renderingQueue = new ArrayList<>();

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChat(ClientChatReceivedEvent event) {
		if(Minecraft.getMinecraft().theWorld.isRemote && event.type == 0) {
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
				if(alertsList.get(i).triggerMessage.equals("")) continue;
				if(alertsList.get(i).getIsEqual()) {
					if(onlyNonColorMessage.equals(alertsList.get(i).getTrigger())) {
						if(alertsList.get(i).getOnlyParty() && !nonColorMessage.startsWith("Party >")) return;
						if(alertsList.get(i).getIgnorePlayers() && nonColorMessage.contains(": ")) return;
						if(autor) return;
	
						registerAlert(i);
					}
				}else {
					if(message.contains(alertsList.get(i).getTrigger())) {
						if(alertsList.get(i).getOnlyParty() && !nonColorMessage.startsWith("Party >")) return;
						if(alertsList.get(i).getIgnorePlayers() && nonColorMessage.contains(": ")) return;
						if(autor) return; // ignores messages written by yourself
						
						registerAlert(i);
					}
				}
			}
		}
	}
	
	
	private void registerAlert(int i) {
		String message = Alerts.alertsList.get(i).getDisplay();
		
		if((message.endsWith(".png") || message.endsWith(".jpg") || message.endsWith(".jpeg")) && (message.contains("/") || message.contains("\\"))) { // rendering images
			getTextureFromCache(Alerts.alertsList.get(i)); // image
		}else { // text
			ResourceLocation soundLocation = new ResourceLocation("minecraft", "random.anvil_land");
		    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(soundLocation));
		    if(!renderingQueue.contains(new AlertRenderInfo(message, Alerts.alertsList.get(i).getTime(), System.currentTimeMillis(), null, null, Alerts.alertsList.get(i).position, Alerts.alertsList.get(i).scale))) {
		    	renderingQueue.add(new AlertRenderInfo(message, Alerts.alertsList.get(i).getTime(), System.currentTimeMillis(), null, null, Alerts.alertsList.get(i).position, Alerts.alertsList.get(i).scale));
		    }
		}
	}
	
	
	public static void DisplayCustomAlerts(String display, int dur, int[] position, float scale) {
		AlertRenderInfo info = new AlertRenderInfo(display, dur, System.currentTimeMillis(), null, null, position, scale);
		renderingQueue.add(info);
		ResourceLocation soundLocation = new ResourceLocation("minecraft", "random.anvil_land");
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(soundLocation));
	}
	
	 @SubscribeEvent
	    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
		 // its done this way because u cant remove element from list while its iterating
		 Iterator<AlertRenderInfo> iterator = renderingQueue.iterator();
		 while (iterator.hasNext()) {
			 	AlertRenderInfo info = iterator.next();
		        String alert = info.message;
		        
			    // Check if the delay duration has passed and delete alert from renderingQueue
		        if (System.currentTimeMillis() - info.startTime >= info.time) {
		            iterator.remove();
		        }
			    
	        	if((alert.endsWith(".png") || alert.endsWith(".jpg") || alert.endsWith(".jpeg")) && (alert.contains("/") || alert.contains("\\"))) { // rendering images
	        		if(info.bufferedImage != null && info.location != null) {
	        			renderAlertImage(event.resolution, info);
	        		}
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
	        
	        RenderHandler.drawAlertText(info.message, resolution, 0xf54245, info.scale, posX, posY);
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
            
            Minecraft.getMinecraft().renderEngine.bindTexture(info.location);
            drawModalRectWithCustomSizedTexture(posX - imgWidth / 2, posY - imgHeight / 2, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
	    }
	    
	    private static void downloadImageFromInternet(final String urlString, final String errorWhere) {
	        new Thread(new Runnable() {
	        	private BufferedImage bufferedimage;
	        	private ResourceLocation resourceLocation;
	        	
	            public void run() {
	                try {
	                    // Download the image
	                    URL url = new URL(urlString);
	                    InputStream inputStream = url.openStream();
	                    bufferedimage = ImageIO.read(inputStream);
	                    // Switch back to the main thread for OpenGL operations
	                    Minecraft.getMinecraft().addScheduledTask(new Runnable() {
							public void run() {
	                            try {
	                            	resourceLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("custom_image", new DynamicTexture(bufferedimage));
			                    	bufferedTextureCache.put(urlString, bufferedimage);
	                            	resourceLocationCache.put(bufferedimage, resourceLocation);
	                            } catch (Exception e) {
	                            	if(Minecraft.getMinecraft().thePlayer != null) {
	                            		if(e.getLocalizedMessage() == null) {
	                            			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]" + EnumChatFormatting.WHITE + " in " + EnumChatFormatting.AQUA + errorWhere + EnumChatFormatting.WHITE + ": " + EnumChatFormatting.BOLD.RED + "There was a problem with downloading this image (BufferedImage is null)"));
	                            		}else {
	                            			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]" + EnumChatFormatting.WHITE + " in " + EnumChatFormatting.AQUA + errorWhere + EnumChatFormatting.WHITE + ": " + EnumChatFormatting.BOLD.RED + e.getLocalizedMessage()));
	                            		}
	                            	}
                            	}
	                        }
	                    });
	                } catch (Exception e) {
	                	if(Minecraft.getMinecraft().thePlayer != null) {
                    		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]" + EnumChatFormatting.WHITE + " in " + EnumChatFormatting.AQUA + errorWhere + EnumChatFormatting.WHITE + ": " + EnumChatFormatting.BOLD.RED + e.getLocalizedMessage()));
                    		e.printStackTrace();
	                	}	             
                	}
	            }
	        }).start();
	    }
	    
	    private static void findImageFromFiles(final String path, final String errorWhere) {
	    	new Thread(new Runnable() {
	            private BufferedImage bufferedimage;
	            private ResourceLocation resourceLocation;
	            
				public void run() {
			    	try {
			    		File imageFile = new File(path);
			    		try (InputStream inputStream = new BufferedInputStream(new FileInputStream(imageFile))) {
		                    bufferedimage = ImageIO.read(inputStream);
		                }
			    		
			            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
			                public void run() {
			                    try {
			                    	resourceLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("custom_image", new DynamicTexture(bufferedimage));
			                    	bufferedTextureCache.put(path, bufferedimage);
	                            	resourceLocationCache.put(bufferedimage, resourceLocation);
			                    }catch (Exception e) {
			                    	if(Minecraft.getMinecraft().thePlayer != null) {
	                            		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]" + EnumChatFormatting.WHITE + " in " + EnumChatFormatting.AQUA + errorWhere + EnumChatFormatting.WHITE + ": " + EnumChatFormatting.BOLD.RED + e.getLocalizedMessage()));
	                            	}
			                    }
			                }
			            });
			        } catch (Exception e) {
			        	if(Minecraft.getMinecraft().thePlayer != null) {
                    		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]" + EnumChatFormatting.WHITE + " in " + EnumChatFormatting.AQUA + errorWhere + EnumChatFormatting.WHITE + ": " + EnumChatFormatting.BOLD.RED + e.getLocalizedMessage()));
                    	}
			        }
	            }
	    	}).start();
	    }
	    

	    public void getTextureFromCache(Alert alert) {
	        if (!bufferedTextureCache.containsKey(alert.displayedMessage)) {
	            // If the texture is not in the cache, load it
	        	if(alert.displayedMessage.contains("https://")) {
	        		downloadImageFromInternet(alert.displayedMessage, "Alerts " + EnumChatFormatting.DARK_AQUA + "('" + alert.triggerMessage + "')");
	        	}else {
	        		findImageFromFiles(alert.displayedMessage, "Alerts " + EnumChatFormatting.DARK_AQUA + "('" + alert.triggerMessage + "')");
	        	}
	        }
	        // Return the texture from the cache
	        BufferedImage buff = bufferedTextureCache.getOrDefault(alert.displayedMessage, null);
	        
	        if(buff != null) {
	        	ResourceLocation location = resourceLocationCache.getOrDefault(buff, null);
		        ResourceLocation soundLocation = new ResourceLocation("minecraft", "random.anvil_land");
	            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(soundLocation));
	            
	            // adds alert to rendering queue so it can render
	            if(!renderingQueue.contains(new AlertRenderInfo(alert.displayedMessage, alert.time, System.currentTimeMillis(), location, buff, alert.position, alert.scale))) {
	            	renderingQueue.add(new AlertRenderInfo(alert.displayedMessage, alert.time, System.currentTimeMillis(), location, buff, alert.position, alert.scale));
	            }
	        }else {
	        	DisplayCustomAlerts("Alert is still downloading...", 3000, new int[] {50, 50}, 2);
	        }
	    }
	    
	    public static void putAllImagesToCache() {
	    	// downloads images while loading game so when you want to get them they dont have to download (it sometimes took 5s to receive alert before this)
	    	List<String> paths = new ArrayList();
	    	
	    	for (Alert alert : alertsList) {
				if((alert.displayedMessage.endsWith(".png") || alert.displayedMessage.endsWith(".jpg") || alert.displayedMessage.endsWith(".jpeg")) && (alert.displayedMessage.contains("/") || alert.displayedMessage.contains("\\"))) {
					paths.add(alert.displayedMessage);
					if(!bufferedTextureCache.containsKey(alert.displayedMessage)) {
						if(alert.displayedMessage.contains("https://")) {
			        		downloadImageFromInternet(alert.displayedMessage, "Alerts " + EnumChatFormatting.DARK_AQUA + "('" + alert.triggerMessage + "')");
			        	}else {
			        		findImageFromFiles(alert.displayedMessage, "Alerts " + EnumChatFormatting.DARK_AQUA + "('" + alert.triggerMessage + "')");
			        	}
					}
				}
			}
	    	
	    	// deleting all unused images (while changing them or something idk)
	    	for (Map.Entry<String, BufferedImage> cache : bufferedTextureCache.entrySet()) {
	    	    String path = cache.getKey();
	    	    BufferedImage buff = cache.getValue();
	    	    
	    	    if(!paths.contains(path)) {
	    	    	bufferedTextureCache.remove(path);
	    	    	Minecraft.getMinecraft().getTextureManager().deleteTexture(resourceLocationCache.get(buff));
	    	    	resourceLocationCache.remove(buff);
	    	    }
	    	}
	    }
	    
	    public static void editAlertPositionAndScale(String alert, int[] position, float scale, int i) {
    		if((alert.endsWith(".png") || alert.endsWith(".jpg") || alert.endsWith(".jpeg")) && (alert.contains("/") || alert.contains("\\"))) {
    			if(!bufferedTextureCache.containsKey(alert)) {
					if(alert.contains("https://")) {
		        		downloadImageFromInternet(alert, "Alerts " + EnumChatFormatting.DARK_AQUA + "('" + "edit" + "')");
		        	}else {
		        		findImageFromFiles(alert, "Alerts " + EnumChatFormatting.DARK_AQUA + "(" + "edit" + ")");
		        	}
    			}
			}
    
		    // Return the texture from the cache
	        BufferedImage buff = bufferedTextureCache.getOrDefault(alert, null);
	        
	        if(buff != null) {
	        	ResourceLocation location = resourceLocationCache.getOrDefault(buff, null);
	        	
	        	AlertPosition.alertInfo = new AlertRenderInfo(alert, 0, 0, location, buff, position, scale);
	        	AlertPosition.alert = alertsList.get(i);
	        	GuiHandler.GuiType = 8;
	        }else {
	        	AlertPosition.alertInfo = new AlertRenderInfo(alert, 0, 0, null, null, position, scale);
	        	AlertPosition.alert = alertsList.get(i);
	        	GuiHandler.GuiType = 8;
	        }

	    }
	    
}
