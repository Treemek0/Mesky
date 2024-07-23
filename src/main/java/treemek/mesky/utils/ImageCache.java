package treemek.mesky.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
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
	   
		public static void deleteFromImageCache(String path) {
			if(bufferedTextureCache.containsKey(path)) {
				BufferedImage buff = bufferedTextureCache.get(path);
				resourceLocationCache.remove(buff);
				bufferedTextureCache.remove(path);
			}
		}
		
		public static void removeImageFolderFromImageCache(String folderPath) {
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
		
		 public static void findImageFromFiles(final String path, final String errorWhere) {
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
}
