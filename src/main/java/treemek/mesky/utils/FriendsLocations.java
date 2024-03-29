package treemek.mesky.utils;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Mesky;
import treemek.mesky.utils.Locations.Location;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class FriendsLocations {
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onChat(ClientChatReceivedEvent event) {
		String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
		
		if(message.contains(": ")) return;
		if(message.startsWith("You are now friends with")){
		String HypixelNickname = message.substring(25);
		String nick = HypixelNickname;
		if(HypixelNickname.contains("[") && HypixelNickname.contains("]")) {
			nick = HypixelNickname.substring(HypixelNickname.indexOf("]") + 2);
		}
		
		Location.checkTabLocation();
		setLocationForPlayer(nick, Locations.currentLocation);
		}
		
		if(message.endsWith("removed you from their friends list!")){
			String HypixelNickname = message.substring(0, message.length() - 37);
			String nick = HypixelNickname;
			if(HypixelNickname.contains("[") && HypixelNickname.contains("]")) {
				nick = HypixelNickname.substring(HypixelNickname.indexOf("]") + 2);
			}
			
			removePlayer(nick);
		}
		
		if(message.startsWith("You removed") && message.endsWith("from your friends list!")){
			String HypixelNickname = message.substring(12, message.length() - 24);
			String nick = HypixelNickname;
			if(HypixelNickname.contains("[") && HypixelNickname.contains("]")) {
				nick = HypixelNickname.substring(HypixelNickname.indexOf("]") + 2);
			}
			
			removePlayer(nick);
		}
		
	}
	
	public static void removePlayer(String nick) {
		Map<String, Location> playerLocationMap = loadPlayerLocationsFromFile();
        playerLocationMap.remove(nick);
        savePlayerLocationsToFile(playerLocationMap);
	}

	// Method to update or set the location for a player
    public static void setLocationForPlayer(String playerNickname, Location location) {
    	Map<String, Location> playerLocationMap = loadPlayerLocationsFromFile();
        playerLocationMap.put(playerNickname, location);
        savePlayerLocationsToFile(playerLocationMap);
    }

    // Method to retrieve the location for a player
    public static Location getLocationForPlayer(String playerNickname) {
    	Map<String, Location> playerLocationMap = loadPlayerLocationsFromFile();
    	if(playerLocationMap == null) return Location.NONE;
    	if(playerLocationMap.get(playerNickname) == null) return Location.NONE;
    	
    	return playerLocationMap.get(playerNickname);
    }

    
    public static Map<String, Location> loadPlayerLocationsFromFile() {
        File file = new File(Mesky.configDirectory + "/mesky/utils/meskyFriendsLocations.json");
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<String, Location>>() {}.getType();
                Map<String, Location> loadedLocations = new Gson().fromJson(reader, type);
                if (loadedLocations != null) {
                    return loadedLocations;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		return null;
    }
    
 // Method to save player locations to a JSON file
    public static void savePlayerLocationsToFile(Map<String, Location> playerLocationMap) {
    	new File(Mesky.configDirectory + "/mesky/utils/").mkdirs();
        try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/utils/meskyFriendsLocations.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(playerLocationMap, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
