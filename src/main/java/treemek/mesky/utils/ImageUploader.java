package treemek.mesky.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.util.ResourceLocation;
import treemek.mesky.Mesky;
import treemek.mesky.cosmetics.CosmeticHandler;

public class ImageUploader {
	static File IMAGES_DIR = new File(Mesky.configDirectory, "/mesky/images");
	
	public static void downloadImagesFromFolderToCache() {
        Path folder = Paths.get(Mesky.configDirectory + "/mesky/images");

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.{png,jpg,jpeg}")) {
            for (Path entry : stream) {
            	String fileName = entry.getFileName().toString().substring(0, entry.getFileName().toString().indexOf("."));
        		ImageCache.downloadImageFromFile(entry.toAbsolutePath().toString(), fileName, "ImageUploader");
            }
        } catch (IOException e) {
            Utils.writeError(e);
        }
	}
	
	public static void uploadImage(File file) {
		if(!IMAGES_DIR.exists()) IMAGES_DIR.mkdir();
		
		if(!file.getName().endsWith(".png") && !file.getName().endsWith(".jpg") && !file.getName().endsWith(".jpeg")) return;
		File destFile = new File(IMAGES_DIR, file.getName());
		
		try {
            Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            String fileName = destFile.getName().toString().substring(0, destFile.getName().toString().indexOf("."));
    		ImageCache.downloadImageFromFile(destFile.getAbsolutePath().toString(), fileName, "ImageUploader");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static List<File> getAllImportedImages(){
		List<File> images = new ArrayList<>();

        if (IMAGES_DIR.isDirectory()) {
            File[] files = IMAGES_DIR.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"));

            if (files != null) {
                for (File file : files) {
                    images.add(file);
                }
            }
        }
		 
		 return images;
	}
	
	public static boolean isCustomImage(String image) {
		if(image.contains(":")) {
			int s = image.indexOf(":");
			
			String mother = image.substring(0, s);
			if(mother.equalsIgnoreCase("mesky")) return true;
		}
		
		return false;
	}
	
	public static ResourceLocation getResourceLocation(String image) {
		if(image.contains(":")) {
			int s = image.indexOf(":");
			
			String child = image.substring(s+1);
			
			ResourceLocation location = ImageCache.getLocationOfPath(child);
			if(location.getResourcePath().length() > 0) {
				return location;
			}
		}
		
		return null;
	}
}
