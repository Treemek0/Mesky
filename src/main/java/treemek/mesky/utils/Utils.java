package treemek.mesky.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Utils {

	
	// Taken from SkyblockAddons
		public static List<String> getItemLore(ItemStack itemStack) {
			final int NBT_INTEGER = 3;
			final int NBT_STRING = 8;
			final int NBT_LIST = 9;
			final int NBT_COMPOUND = 10;

			if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("display", NBT_COMPOUND)) {
				NBTTagCompound display = itemStack.getTagCompound().getCompoundTag("display");

				if (display.hasKey("Lore", NBT_LIST)) {
					NBTTagList lore = display.getTagList("Lore", NBT_STRING);

					List<String> loreAsList = new ArrayList<>();
					for (int lineNumber = 0; lineNumber < lore.tagCount(); lineNumber++) {
						loreAsList.add(lore.getStringTagAt(lineNumber));
					}

					return Collections.unmodifiableList(loreAsList);
				}
			}

			return Collections.emptyList();
		}
		
		
		 public static String getNameFromUUID(String uuid) throws Exception {
			 
		 	StringBuilder result = new StringBuilder();
	        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
	            for (String line; (line = reader.readLine()) != null; ) {
	                result.append(line);
	            }
	        }
	        
	        String jsonString = result.toString();
	        // Split the JSON string by "name":""
	      	String[] parts = jsonString.split("\"name\"\\s*:\\s*\"");
	      	if (parts.length >= 2) {
	            String name = parts[1]; // Take the second part after the split
	            int endIndex = name.indexOf('"'); // Find the ending quote
	            if (endIndex != -1) {
	                return name.substring(0, endIndex); // Extract the name
	            }
	      	}
	        return null; // Return null if name extraction fails
		}
}
