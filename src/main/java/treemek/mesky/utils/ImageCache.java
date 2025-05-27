package treemek.mesky.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class ImageCache {
	// Texture cache to store image path -> texture ID mappings
		public static Map<String, BufferedImage> bufferedTextureCache = new ConcurrentHashMap<>();
		public static Map<BufferedImage, ResourceLocation> resourceLocationCache = new ConcurrentHashMap<>();
	   
		public static ResourceLocation getLocationOfPath(String path) {
			BufferedImage buff = bufferedTextureCache.get(path);
			if(buff != null) {
				return resourceLocationCache.getOrDefault(buff, new ResourceLocation(""));
			}
			
			return new ResourceLocation("");
		}
		
		public static void deleteFromImageCache(String path) {
			if(bufferedTextureCache.containsKey(path)) {
				BufferedImage buff = bufferedTextureCache.get(path);
				resourceLocationCache.remove(buff);
				bufferedTextureCache.remove(path);
			}
		}
		
		public static void removeFolderImagesFromCache(String folderPath) {
	        Iterator<Map.Entry<String, BufferedImage>> iterator = bufferedTextureCache.entrySet().iterator();
	        
	        while (iterator.hasNext()) {
	            Map.Entry<String, BufferedImage> entry = iterator.next();
	            String key = entry.getKey();
	            
	            if (key.startsWith(folderPath)) {
	                iterator.remove();
	            }
	        }
	    }
		
		public static void downloadImageFromInternet(final String urlString, final String errorWhere) {
	        new Thread(new Runnable() {
	        	private BufferedImage bufferedimage;
	        	private ResourceLocation resourceLocation;
	        	
	            public void run() {
	                try {
	                    URL url = new URL(urlString);
	                    URLConnection connection = url.openConnection();
	                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
	                    InputStream inputStream = connection.getInputStream();
	                    bufferedimage = ImageIO.read(inputStream);

	                    Minecraft.getMinecraft().addScheduledTask(new Runnable() {
							public void run() {
	                            try {
	                            	resourceLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(urlString, new DynamicTexture(bufferedimage));
			                    	bufferedTextureCache.put(urlString, bufferedimage);
	                            	resourceLocationCache.put(bufferedimage, resourceLocation);
	                            } catch (Exception e) {
	                            	if(Minecraft.getMinecraft().thePlayer != null) {
	                            		if(e.getLocalizedMessage() == null) {
	                            			Utils.addMinecraftMessageWithPrefix("Error in " + EnumChatFormatting.AQUA + errorWhere + EnumChatFormatting.WHITE + ": " + EnumChatFormatting.BOLD.RED + "There was a problem with downloading this image (BufferedImage is null)");
                            			}else {
	                            			Utils.addMinecraftMessageWithPrefix("Error in " + EnumChatFormatting.AQUA + errorWhere + EnumChatFormatting.WHITE + ": ");
	                                		Utils.addMinecraftMessage(EnumChatFormatting.BOLD.RED + e.getLocalizedMessage());
	                                	}
	                            	}
	                        	}
	                        }
	                    });
	                } catch (Exception e) {
                		Utils.addMinecraftMessageWithPrefix("Error in " + EnumChatFormatting.AQUA + errorWhere + EnumChatFormatting.WHITE + ": ");
                		Utils.addMinecraftMessage(EnumChatFormatting.BOLD.RED + e.getLocalizedMessage());
                		e.printStackTrace();
	                }
	            }
	        }).start();
	    }
		
		 public static void downloadImageFromFile(final String path, final String errorWhere) {
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
			                    	resourceLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(path, new DynamicTexture(bufferedimage));
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
}
