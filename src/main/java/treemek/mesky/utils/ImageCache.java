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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class ImageCache {
	public static class Cache {
		public BufferedImage buffer;
		public ResourceLocation location;
		
		public Cache(BufferedImage bufferedimage, ResourceLocation resourceLocation) {
			this.buffer = bufferedimage;
			this.location = resourceLocation;
		}
		
		public boolean isNulled() {
			return buffer == null || location == null;
		}
	}
	
	// Texture cache to store image path -> texture ID mappings
		public static Map<String, Cache> bufferedCache = new ConcurrentHashMap<>();
	   
		public static ResourceLocation getLocationOfPath(String path) {
			Cache cache = bufferedCache.get(path);
			if(cache != null && cache.location != null) {
				return cache.location;
			}
			
			return new ResourceLocation("");
		}
		
		public static void deleteFromImageCache(String path) {
			if(bufferedCache.containsKey(path)) {
				bufferedCache.remove(path);
			}
		}
		
		public static void removeFolderImagesFromCache(String folderPath) {
	        Iterator<Entry<String, Cache>> iterator = bufferedCache.entrySet().iterator();
	        
	        while (iterator.hasNext()) {
	            Entry<String, Cache> entry = iterator.next();
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
			                    	bufferedCache.put(urlString, new Cache(bufferedimage, resourceLocation));
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
			                    	bufferedCache.put(path, new Cache(bufferedimage, resourceLocation));
			                    }catch (Exception e) {
			                    	if(Minecraft.getMinecraft().thePlayer != null) {
	                            		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]" + EnumChatFormatting.WHITE + " Error in " + EnumChatFormatting.AQUA + errorWhere + EnumChatFormatting.WHITE + ": " + EnumChatFormatting.BOLD.RED + e.getLocalizedMessage()));
	                            	}
			                    }
			                }
			            });
			        } catch (Exception e) {
			        	if(Minecraft.getMinecraft().thePlayer != null) {
	             		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]" + EnumChatFormatting.WHITE + " Error in " + EnumChatFormatting.AQUA + errorWhere + EnumChatFormatting.WHITE + ": " + EnumChatFormatting.BOLD.RED + e.getLocalizedMessage()));
	             	}
			        }
	            }
	    	}).start();
	    }
		 
		 public static void downloadImageFromFile(final String path, final String customName, final String errorWhere) {
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
				                    	resourceLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(customName, new DynamicTexture(bufferedimage));
				                    	bufferedCache.put(customName, new Cache(bufferedimage, resourceLocation));
				                    }catch (Exception e) {
				                    	if(Minecraft.getMinecraft().thePlayer != null) {
		                            		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]" + EnumChatFormatting.WHITE + " Error in " + EnumChatFormatting.AQUA + errorWhere + EnumChatFormatting.WHITE + ": " + EnumChatFormatting.BOLD.RED + e.getLocalizedMessage()));
		                            	}
				                    }
				                }
				            });
				        } catch (Exception e) {
				        	if(Minecraft.getMinecraft().thePlayer != null) {
		             		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]" + EnumChatFormatting.WHITE + " Error in " + EnumChatFormatting.AQUA + errorWhere + EnumChatFormatting.WHITE + ": " + EnumChatFormatting.BOLD.RED + e.getLocalizedMessage()));
		             	}
				        }
		            }
		    	}).start();
		    }
}
