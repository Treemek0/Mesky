package treemek.mesky.config;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import net.minecraftforge.common.config.Configuration;
import scala.util.parsing.input.Reader;
import treemek.mesky.Mesky;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Alerts.Alert;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.MacroWaypoints;
import treemek.mesky.utils.ChatFunctions;
import treemek.mesky.utils.FriendsLocations;
import treemek.mesky.utils.Waypoints.Waypoint;

public class ConfigHandler {
	
    public static void createNewJsonObject(String file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(new JsonObject().toString());
        fileWriter.close();
    }

    public static void createNewJsonArray(String file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(new JsonArray().toString());
        fileWriter.close();
    }

    public static JsonObject initJsonObject(String file) throws IOException {
        if (!(new File(file).exists())) createNewJsonObject(file);
        try {
            return new JsonParser().parse(new FileReader(file)).getAsJsonObject();
        } catch (IllegalStateException | JsonSyntaxException corrupted) {
            corrupted.printStackTrace();
            System.out.println("Recreating " + file);
            createNewJsonObject(file);
            return new JsonParser().parse(new FileReader(file)).getAsJsonObject();
        }
    }

    public static <T> Object[] initJsonArray(String file, Class<T> clazz) throws IOException {
        if (!(new File(file).exists())) createNewJsonArray(file);
        Object[] arr = null;
        try {
            arr = new Gson().fromJson(new FileReader(file), (Type) clazz);
        } catch (JsonSyntaxException corrupted) {
            corrupted.printStackTrace();
            System.out.println("Recreating " + file);
            createNewJsonArray(file);
        }
        return arr;
    }

    public static void reloadConfig() {
        try {
            File directory = new File(Mesky.configDirectory);
            if (!directory.exists()) directory.mkdir();

            loadSettings();

            // Alerts
            if(new File(directory + "/mesky/utils/meskyAlerts.json").exists()) {
	            Object[] alerts = initJsonArray(directory + "/mesky/utils/meskyAlerts.json", Alerts.Alert[].class);
	            if (alerts != null) Alerts.alertsList = new ArrayList<>(Arrays.asList((Alerts.Alert[]) alerts));
	        
	        	Alerts.putAllImagesToCache();
            }
            // Chat Functions
            if(new File(directory + "/mesky/utils/meskyChatFunctions.json").exists()) {
	            Object[] chatFunctions = initJsonArray(directory + "/mesky/utils/meskyChatFunctions.json", ChatFunctions.ChatFunction[].class);
	            if (chatFunctions != null) ChatFunctions.chatFunctionsList = new ArrayList<>(Arrays.asList((ChatFunctions.ChatFunction[]) chatFunctions));
            }
            
            // Waypoints
            if(new File(directory + "/mesky/utils/meskyWaypoints.json").exists()) {
	            Object[] waypoints = initJsonArray(directory + "/mesky/utils/meskyWaypoints.json", Waypoints.Waypoint[].class);
	            if (waypoints != null) Waypoints.waypointsList = new ArrayList<>(Arrays.asList((Waypoints.Waypoint[]) waypoints));
            }
            
            // Macro Waypoints
            if(new File(directory + "/mesky/utils/meskyMacroWaypoints.json").exists()) {
	            Object[] waypoints = initJsonArray(directory + "/mesky/utils/meskyMacroWaypoints.json", MacroWaypoints.MacroWaypoint[].class);
	            if (waypoints != null) MacroWaypoints.waypointsList = new ArrayList<>(Arrays.asList((MacroWaypoints.MacroWaypoint[]) waypoints));
            }

            
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

	public static void SaveAlert(List<Alerts.Alert> alerts) {
    	// saves correctly
		new File(Mesky.configDirectory + "/mesky/utils/").mkdirs();
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/utils/meskyAlerts.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(alerts, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
	public static void SaveChatFunction(List<ChatFunctions.ChatFunction> chatFunctions) {
    	// saves correctly
		new File(Mesky.configDirectory + "/mesky/utils/").mkdirs();
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/utils/meskyChatFunctions.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(chatFunctions, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
	
    public static void SaveWaypoint(List<Waypoints.Waypoint> waypoints) {
    	// saves correctly
    	new File(Mesky.configDirectory + "/mesky/utils/").mkdirs();
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/utils/meskyWaypoints.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(waypoints, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void SaveMacroWaypoint(List<MacroWaypoints.MacroWaypoint> waypoints) {
    	// saves correctly
    	new File(Mesky.configDirectory + "/mesky/utils/").mkdirs();
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/utils/meskyMacroWaypoints.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(waypoints, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    // !!!
    // Saving friends locations is in FriendsLocations file
    // !!!


    private static void loadSettings(){
    	File file = new File(Mesky.configDirectory + "/mesky/meskySettings.json");
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Setting>>(){}.getType();
                Map<String, Setting> settings = gson.fromJson(reader, type);

                if(settings != null) {
	                SettingsConfig.BlockFlowerPlacing = safelyGetInfo(settings.get("BlockFlowerPlacing"), SettingsConfig.BlockFlowerPlacing);
	                SettingsConfig.FishingTimer = safelyGetInfo(settings.get("FishingTimer"), SettingsConfig.FishingTimer);
	                SettingsConfig.BonzoTimer = safelyGetInfo(settings.get("BonzoTimer"), SettingsConfig.BonzoTimer);
	                SettingsConfig.SpiritTimer = safelyGetInfo(settings.get("SpiritTimer"), SettingsConfig.SpiritTimer);
	                SettingsConfig.GhostBlocks = safelyGetInfo(settings.get("GhostBlocks"), SettingsConfig.GhostBlocks);
	                SettingsConfig.GhostPickaxe = safelyGetInfo(settings.get("GhostPickaxe"), SettingsConfig.GhostPickaxe);
	                SettingsConfig.HidePlayers = safelyGetInfo(settings.get("HidePlayers"), SettingsConfig.HidePlayers);
	                SettingsConfig.GhostPickaxeSlot = safelyGetInfo(settings.get("GhostPickaxeSlot"), SettingsConfig.GhostPickaxeSlot);
	                SettingsConfig.AntyGhostBlocks = safelyGetInfo(settings.get("AntyGhostBlocks"), SettingsConfig.AntyGhostBlocks);
	                SettingsConfig.CoordsDetection = safelyGetInfo(settings.get("CoordsDetection"), SettingsConfig.CoordsDetection);
	                SettingsConfig.NickMentionDetection = safelyGetInfo(settings.get("NickMentionDetection"), SettingsConfig.NickMentionDetection);
	                SettingsConfig.JawbusDetection = safelyGetInfo(settings.get("JawbusDetection"), SettingsConfig.JawbusDetection);
	                SettingsConfig.AutoFish = safelyGetInfo(settings.get("AutoFish"), SettingsConfig.AutoFish);
	                SettingsConfig.KillSeaCreatures = safelyGetInfo(settings.get("KillSeaCreatures"), SettingsConfig.KillSeaCreatures);
	                SettingsConfig.AutoThrowHook = safelyGetInfo(settings.get("AutoThrowHook"), SettingsConfig.AutoThrowHook);
	                
	                // cosmetics
	                CosmeticHandler.AuraType = safelyGetInfo(settings.get("AuraType"), CosmeticHandler.AuraType);
	                CosmeticHandler.HatType = safelyGetInfo(settings.get("HatType"), CosmeticHandler.HatType);
	                CosmeticHandler.WingsType = safelyGetInfo(settings.get("WingsType"), CosmeticHandler.WingsType);
	                CosmeticHandler.PetType = safelyGetInfo(settings.get("PetType"), CosmeticHandler.PetType);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }     
    }
    
    private static Setting safelyGetInfo(Setting objectFromFile, Setting oldSetting) {
    	if(objectFromFile != null) {
    		return objectFromFile;
    	}else {
    		return oldSetting;
    	}
    }

    public static void saveSettings(){
      // features
    	Map<String, Setting> settings = new HashMap<>();
    	
    	settings.put("BlockFlowerPlacing", SettingsConfig.BlockFlowerPlacing);
    	settings.put("FishingTimer", SettingsConfig.FishingTimer);
    	settings.put("BonzoTimer", SettingsConfig.BonzoTimer);
    	settings.put("SpiritTimer", SettingsConfig.SpiritTimer);
    	settings.put("GhostBlocks", SettingsConfig.GhostBlocks);
    	settings.put("GhostPickaxe", SettingsConfig.GhostPickaxe);
    	settings.put("HidePlayers", SettingsConfig.HidePlayers);
    	settings.put("GhostPickaxeSlot", SettingsConfig.GhostPickaxeSlot);
    	settings.put("AntyGhostBlocks", SettingsConfig.AntyGhostBlocks);
    	settings.put("CoordsDetection", SettingsConfig.CoordsDetection);
    	settings.put("NickMentionDetection", SettingsConfig.NickMentionDetection);
    	settings.put("JawbusDetection", SettingsConfig.JawbusDetection);
        settings.put("AutoFish", SettingsConfig.AutoFish);
        settings.put("KillSeaCreatures", SettingsConfig.KillSeaCreatures);
        settings.put("AutoThrowHook", SettingsConfig.AutoThrowHook);
        
    	//cosmetics
    	settings.put("AuraType", CosmeticHandler.AuraType);
    	settings.put("HatType", CosmeticHandler.HatType);
    	settings.put("WingsType", CosmeticHandler.WingsType);
    	settings.put("PetType", CosmeticHandler.PetType);
        
    	new File(Mesky.configDirectory + "/mesky/").mkdirs();
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/meskySettings.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(settings, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
	
}
