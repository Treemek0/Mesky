package treemek.mesky.utils;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Mesky;
import treemek.mesky.utils.Locations.Location;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.authlib.GameProfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class FriendsLocations {
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onChat(ClientChatReceivedEvent event) {
		if(!HypixelCheck.isOnHypixel()) return;
		String message = StringUtils.stripControlCodes(event.message.getUnformattedText());

		if(message.contains(": ")) return;
		if(message.contains("You are now friends with")){
			new Thread(() -> {
				try {
					String HypixelNickname = message.substring(message.indexOf("You are now") + 25);
					String nick = HypixelNickname;
					if(HypixelNickname.contains("[") && HypixelNickname.contains("]")) {
						nick = HypixelNickname.substring(HypixelNickname.indexOf("]") + 1);
					}
			
					nick = nick.trim();
					
					Utils.writeToConsole("Friends locations: detected nick: " + nick);
					String id = getUUIDFromNick(nick);
					if(id != null) {
						Utils.writeToConsole("Friends locations: extracted nick UUID: " + id);
		
		
						setInfoForPlayer(id, Locations.getLocation());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		}
		
		if(message.contains("removed you from their friends list!")){
			new Thread(() -> {
				try {
					String HypixelNickname = message.substring(0, message.indexOf("removed you from"));
					String nick = HypixelNickname;
					
					if(HypixelNickname.contains("[") && HypixelNickname.contains("]")) {
						nick = HypixelNickname.substring(HypixelNickname.indexOf("]") + 2);
					}
					
					nick = nick.trim();
					
					Utils.writeToConsole("Friends locations: detected nick in remove: " + nick);
					String id = getUUIDFromNick(nick);
					if(id != null) {
						Utils.writeToConsole("Friends locations: extracted nick UUID: " + id);
					
						removePlayer(id);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		}
		
		if(message.contains("You removed") && message.contains("from your friends list!")){
			new Thread(() -> {
				try {
					String HypixelNickname = message.substring(message.indexOf("You removed") + 12, message.indexOf("from your friends list!"));
					String nick = HypixelNickname;
					if(HypixelNickname.contains("[") && HypixelNickname.contains("]")) {
						nick = HypixelNickname.substring(HypixelNickname.indexOf("]") + 2);
					}
					
					nick = nick.trim();
					
					Utils.writeToConsole("Friends locations: detected nick in remove: " + nick);
					String id = getUUIDFromNick(nick);
					if(id != null) {
						Utils.writeToConsole("Friends locations: extracted nick UUID: " + id);
					
						removePlayer(id);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		}
		
	}
	public static void removePlayer(String uuid) {
		Map<String, String[]> playerLocationMap = loadPlayerLocationsFromFile();
		if(playerLocationMap.containsKey(uuid)) {
			playerLocationMap.remove(uuid);
			savePlayerLocationsToFile(playerLocationMap);
		}else {
			savePlayerLocationsToFile(playerLocationMap);
		}
	}
	
	public static void setCustomInfoForPlayer(String playerName, int day, int month, int year, String location, int x, int y, int z) {
		Map<String, String[]> playerLocationMap = loadPlayerLocationsFromFile();
		
		String playerUUID = getUUIDFromNick(playerName);
		if(playerUUID == null) return;
		
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	if(player != null) {
    		InventoryPlayer inventory = player.inventory;
    		ItemStack[] hotbar = inventory.mainInventory;
    		ItemStack[] armor = inventory.armorInventory;
		
			String slot1 = (hotbar[8] == null)?" ":(hotbar[0].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 1: ":"  1: ") + EnumChatFormatting.GOLD + hotbar[0].getDisplayName();
			String slot2 = (hotbar[8] == null)?" ":(hotbar[1].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 2: ":"  2: ") + EnumChatFormatting.GOLD + hotbar[1].getDisplayName();
			String slot3 = (hotbar[8] == null)?" ":(hotbar[2].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 3: ":"  3: ") + EnumChatFormatting.GOLD + hotbar[2].getDisplayName();
			String slot4 = (hotbar[8] == null)?" ":(hotbar[3].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 4: ":"  4: ") + EnumChatFormatting.GOLD + hotbar[3].getDisplayName();
			String slot5 = (hotbar[8] == null)?" ":(hotbar[4].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 5: ":"  5: ") + EnumChatFormatting.GOLD + hotbar[4].getDisplayName();
			String slot6 = (hotbar[8] == null)?" ":(hotbar[5].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 6: ":"  6: ") + EnumChatFormatting.GOLD + hotbar[5].getDisplayName();
			String slot7 = (hotbar[8] == null)?" ":(hotbar[6].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 7: ":"  7: ") + EnumChatFormatting.GOLD + hotbar[6].getDisplayName();
			String slot8 = (hotbar[7] == null)?" ":(hotbar[7].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 8: ":"  8: ") + EnumChatFormatting.GOLD + hotbar[7].getDisplayName();
			String slot9 = (hotbar[8] == null)?" ":(hotbar[8].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 9: ":"  9: ") + EnumChatFormatting.GOLD + hotbar[8].getDisplayName();
			String helmet = (armor[3] == null)?" ":armor[3].getDisplayName();
			String chestplate = (armor[2] == null)?" ":armor[2].getDisplayName();
			String leggins = (armor[1] == null)?" ":armor[1].getDisplayName();
			String boots = (armor[0] == null)?" ":armor[0].getDisplayName();
			
			String[] info = new String[] {
					EnumChatFormatting.LIGHT_PURPLE + "# Date: " + EnumChatFormatting.DARK_PURPLE + "Day: " + EnumChatFormatting.GOLD + day + EnumChatFormatting.DARK_PURPLE + ", Month: " + EnumChatFormatting.GOLD + month + " (" + Month.of(month) + ")" + EnumChatFormatting.DARK_PURPLE + ", Year: " + EnumChatFormatting.GOLD + year,
	    			EnumChatFormatting.LIGHT_PURPLE + "# Location: " + EnumChatFormatting.GOLD + location,
	    			EnumChatFormatting.LIGHT_PURPLE + "# Coordinates: " + EnumChatFormatting.GOLD + x + " " + y + " " + z,
	    			" ",
	    			EnumChatFormatting.LIGHT_PURPLE + "# Your hotbar: ",
	    			slot1,
	    			slot2,
	    			slot3,
	    			slot4,
	    			slot5,
	    			slot6,
	    			slot7,
	    			slot8,
	    			slot9,
	    			"  Helmet: " + EnumChatFormatting.GOLD + helmet,
	    			"  Chestplate: " + EnumChatFormatting.GOLD + chestplate,
	    			"  Leggins: " + EnumChatFormatting.GOLD + leggins,
	    			"  Boots: " + EnumChatFormatting.GOLD + boots,
				};
			
			if(playerLocationMap.containsKey(playerUUID)) {
	    		playerLocationMap.replace(playerUUID, info);
	    	}else {
	    		playerLocationMap.put(playerUUID, info);
	    	}
	        savePlayerLocationsToFile(playerLocationMap);
    	}
	}

	// Method to update or set the location for a player
    public static void setInfoForPlayer(String playerUUID, Location location) {
    	Map<String, String[]> playerLocationMap = loadPlayerLocationsFromFile();
    	
    	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	if(player != null) {
    		InventoryPlayer inventory = player.inventory;
    		ItemStack[] hotbar = inventory.mainInventory;
    		ItemStack[] armor = inventory.armorInventory;
    		
    		 LocalDate currentDate = LocalDate.now();

	        // Format the date as desired
    		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd, MMMM yyyy");
            String formattedDate = currentDate.format(formatter);
            
            
    		String slot1 = (hotbar[8] == null)?" ":(hotbar[0].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 1: ":"  1: ") + EnumChatFormatting.GOLD + hotbar[0].getDisplayName();
    		String slot2 = (hotbar[8] == null)?" ":(hotbar[1].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 2: ":"  2: ") + EnumChatFormatting.GOLD + hotbar[1].getDisplayName();
    		String slot3 = (hotbar[8] == null)?" ":(hotbar[2].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 3: ":"  3: ") + EnumChatFormatting.GOLD + hotbar[2].getDisplayName();
    		String slot4 = (hotbar[8] == null)?" ":(hotbar[3].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 4: ":"  4: ") + EnumChatFormatting.GOLD + hotbar[3].getDisplayName();
    		String slot5 = (hotbar[8] == null)?" ":(hotbar[4].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 5: ":"  5: ") + EnumChatFormatting.GOLD + hotbar[4].getDisplayName();
    		String slot6 = (hotbar[8] == null)?" ":(hotbar[5].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 6: ":"  6: ") + EnumChatFormatting.GOLD + hotbar[5].getDisplayName();
    		String slot7 = (hotbar[8] == null)?" ":(hotbar[6].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 7: ":"  7: ") + EnumChatFormatting.GOLD + hotbar[6].getDisplayName();
    		String slot8 = (hotbar[7] == null)?" ":(hotbar[7].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 8: ":"  8: ") + EnumChatFormatting.GOLD + hotbar[7].getDisplayName();
    		String slot9 = (hotbar[8] == null)?" ":(hotbar[8].getIsItemStackEqual(player.getCurrentEquippedItem())?"> 9: ":"  9: ") + EnumChatFormatting.GOLD + hotbar[8].getDisplayName();
    		String helmet = (armor[3] == null)?" ":armor[3].getDisplayName();
    		String chestplate = (armor[2] == null)?" ":armor[2].getDisplayName();
    		String leggins = (armor[1] == null)?" ":armor[1].getDisplayName();
    		String boots = (armor[0] == null)?" ":armor[0].getDisplayName();
    		
			String[] info = new String[] {
				EnumChatFormatting.LIGHT_PURPLE + "# Date: " + EnumChatFormatting.DARK_PURPLE + "Day: " + EnumChatFormatting.GOLD + currentDate.getDayOfMonth() + EnumChatFormatting.DARK_PURPLE + ", Month: " + EnumChatFormatting.GOLD + currentDate.getMonthValue() + " (" + currentDate.getMonth().toString().toLowerCase() + ")" + EnumChatFormatting.DARK_PURPLE + ", Year: " + EnumChatFormatting.GOLD + currentDate.getYear(),
    			EnumChatFormatting.LIGHT_PURPLE + "# Location: " + EnumChatFormatting.GOLD + location.text,
    			EnumChatFormatting.LIGHT_PURPLE + "# Coordinates: " + EnumChatFormatting.GOLD + Math.round(player.posX) + " " + Math.round(player.posY) + " " + Math.round(player.posZ),
    			" ",
    			EnumChatFormatting.LIGHT_PURPLE + "# Your hotbar: ",
    			slot1,
    			slot2,
    			slot3,
    			slot4,
    			slot5,
    			slot6,
    			slot7,
    			slot8,
    			slot9,
    			"  Helmet: " + EnumChatFormatting.GOLD + helmet,
    			"  Chestplate: " + EnumChatFormatting.GOLD + chestplate,
    			"  Leggins: " + EnumChatFormatting.GOLD + leggins,
    			"  Boots: " + EnumChatFormatting.GOLD + boots,
			};
	    	
	    	if(playerLocationMap.containsKey(playerUUID)) {
	    		playerLocationMap.replace(playerUUID, info);
	    	}else {
	    		playerLocationMap.put(playerUUID, info);
	    	}
	        savePlayerLocationsToFile(playerLocationMap);
    	}
    }

    // Method to retrieve the location for a player
    public static String[] getInfoForPlayer(String playerNickname) throws Exception {
    	Map<String, String[]> playerLocationMap = loadPlayerLocationsFromFile();
    	if(playerLocationMap == null) {
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "There's no location for this player."));
			return new String[] {"There's no location for this player."};
    	}
    	
    	String htmlJson = getHTML("https://api.mojang.com/users/profiles/minecraft/" + playerNickname);
		String id = extractIdFromJson(htmlJson);
    	
		if(!playerLocationMap.containsKey(id)) {
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "There's no location for this player."));
			return new String[] {"There's no location for this player."};
		}
		
		String[] info = playerLocationMap.get(id);
		Utils.addMinecraftMessage("");
		Utils.addMinecraftMessage(EnumChatFormatting.DARK_AQUA + "Details about when your friendship with " + EnumChatFormatting.AQUA + playerNickname + EnumChatFormatting.DARK_AQUA + " began: ");
		for (String string : info) {
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(string));
		}
		Utils.addMinecraftMessage("");
		
		return info;
    }

    

	public static Map<String, String[]> loadPlayerLocationsFromFile() {
        File file = new File(Mesky.configDirectory + "/mesky/utils/meskyFriendsLocations.json");
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<String, String[]>>() {}.getType();
                Map<String, String[]> loadedLocations = new Gson().fromJson(reader, type);
                if (loadedLocations != null) {
                    return loadedLocations;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		return new HashMap<String, String[]>();
    }
    
	// Method to save player locations to a JSON file
    public static void savePlayerLocationsToFile(Map<String, String[]> playerLocationMap) {
    	new File(Mesky.configDirectory + "/mesky/utils/").mkdirs();
        try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/utils/meskyFriendsLocations.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(playerLocationMap, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        }catch (FileNotFoundException e) {}
        return result.toString();
     }
    
 // Method to manually extract the "id" field from a JSON string
    public static String extractIdFromJson(String jsonString) {
        String idKey = "\"id\" : \"";
        int startIndex = jsonString.indexOf(idKey) + idKey.length();
        int endIndex = jsonString.indexOf("\",", startIndex);
        if(startIndex >= 0 && endIndex >= 0 && startIndex < jsonString.length() && endIndex < jsonString.length()) {
        	return jsonString.substring(startIndex, endIndex);
        }else {
        	return null;
        }
    }
    
    
    public static String getUUIDFromNick(String username) {
    	String htmlJson;
		try {
			htmlJson = getHTML("https://api.mojang.com/users/profiles/minecraft/" + username);
			if(htmlJson != null) {
				String id = extractIdFromJson(htmlJson);
				if(id == null) {
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "FriendsLocations: Error parsing UUID from: " + username));
				}
				return id;
			}else {
				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "FriendsLocations: Error parsing UUID from: " + username));
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
    }

}
