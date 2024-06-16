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
import java.util.List;
import java.util.Map;

import com.google.gson.*;

import net.minecraftforge.common.config.Configuration;
import scala.util.parsing.input.Reader;
import treemek.mesky.Mesky;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Alerts.Alert;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.ChatFunctions;
import treemek.mesky.utils.FriendsLocations;
import treemek.mesky.utils.Waypoints.Waypoint;

public class ConfigHandler {
    private static Configuration config = new Configuration(new File(Mesky.configDirectory + "/mesky/meskySettings.cfg"));
	
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


            // Alerts
            Object[] alerts = initJsonArray(directory + "/mesky/utils/meskyAlerts.json", Alerts.Alert[].class);
            if (alerts != null) Alerts.alertsList = new ArrayList<>(Arrays.asList((Alerts.Alert[]) alerts));
        
            Alerts.putAllImagesToCache();
            
            // Chat Functions
            Object[] chatFunctions = initJsonArray(directory + "/mesky/utils/meskyChatFunctions.json", ChatFunctions.ChatFunction[].class);
            if (chatFunctions != null) ChatFunctions.chatFunctionsList = new ArrayList<>(Arrays.asList((ChatFunctions.ChatFunction[]) chatFunctions));
        
            // Waypoints
            Object[] waypoints = initJsonArray(directory + "/mesky/utils/meskyWaypoints.json", Waypoints.Waypoint[].class);
            if (waypoints != null) Waypoints.waypointsList = new ArrayList<>(Arrays.asList((Waypoints.Waypoint[]) waypoints));
            
            
            loadSettingsLocations();
            loadSettings();
            
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
    
    // !!!
    // Saving friends locations is in FriendsLocations file
    // !!!

    
    public static void saveSettingsLocations() {
    	// Create an instance of Gson
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

     // Create an instance of GuiLocationConfig
        GuiLocationConfig config = new GuiLocationConfig();
        
     // Create a JSON object containing the float arrays
        JsonObject json = new JsonObject();
        json.add("fishingTimer", gson.toJsonTree(GuiLocationConfig.fishingTimer));
        json.add("bonzoMaskTimer", gson.toJsonTree(GuiLocationConfig.bonzoMaskTimer));
        json.add("spiritMaskTimer", gson.toJsonTree(GuiLocationConfig.spiritMaskTimer));
        // Ensure the directory exists
        new File(Mesky.configDirectory + "/mesky/gui/").mkdirs();

        // Write the JSON string to a file
        try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/gui/guiLocations.json")) {
        	gson.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void loadSettingsLocations() throws FileNotFoundException {
    	// Create an instance of Gson
        Gson gson = new Gson();

        try {
            // Read JSON data from the file
            FileReader reader = new FileReader(Mesky.configDirectory + "/mesky/gui/guiLocations.json");

         // Deserialize JSON data into a JsonObject
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            GuiLocationConfig.fishingTimer = gson.fromJson(json.get("fishingTimer"), float[].class);
            GuiLocationConfig.bonzoMaskTimer = gson.fromJson(json.get("bonzoMaskTimer"), float[].class);
            GuiLocationConfig.spiritMaskTimer =gson.fromJson(json.get("spiritMaskTimer"), float[].class);

            // Close the reader
            reader.close();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            // Handle other IO exceptions
            e.printStackTrace();
        }
    }
    


    private static void loadSettings(){
    	config.load();
    	SettingsConfig.BlockFlowerPlacing = config.getBoolean("BlockFlowerPlacing", Configuration.CATEGORY_GENERAL, false, "Block Flower from Placing");
        SettingsConfig.FishingTimer = config.getBoolean("FishingTimer", Configuration.CATEGORY_GENERAL, false, "FishingTimer");
        SettingsConfig.BonzoTimer = config.getBoolean("BonzoTimer", Configuration.CATEGORY_GENERAL, false, "BonzoMask Timer");
        SettingsConfig.SpiritTimer = config.getBoolean("SpiritTimer", Configuration.CATEGORY_GENERAL, false, "SpiritMask Timer");
        SettingsConfig.GhostBlocks = config.getBoolean("GhostBlocks", Configuration.CATEGORY_GENERAL, false, "Ghost Blocks");
        SettingsConfig.GhostPickaxe = config.getBoolean("GhostPickaxe", Configuration.CATEGORY_GENERAL, false, "Ghost Pickaxe");
        SettingsConfig.HidePlayers = config.getBoolean("HidePlayers", Configuration.CATEGORY_GENERAL, false, "Hides Players");
        SettingsConfig.GhostPickaxeSlot = config.getInt("GhostPickaxeSlot", Configuration.CATEGORY_GENERAL, 5, 0, 36, "Ghost Pickaxe Slot");
        SettingsConfig.AntyGhostBlocks = config.getBoolean("AntyGhostBlocks", Configuration.CATEGORY_GENERAL, false, "Removes Ghost Blocks");
        SettingsConfig.CoordsDetection = config.getBoolean("CoordsDetection", Configuration.CATEGORY_GENERAL, true, "Coords Detection");
        SettingsConfig.NickMentionDetection = config.getBoolean("NickMentionDetection", Configuration.CATEGORY_GENERAL, false, "Username mentioning detection");
        SettingsConfig.JawbusDetection = config.getBoolean("JawbusDetection", Configuration.CATEGORY_GENERAL, false, "");
        
        // cosmetics
        CosmeticHandler.WingsType = config.getInt("WingsType", Configuration.CATEGORY_GENERAL, 0, 0, 10, "Type of wings (0 == null)");
        CosmeticHandler.HatType = config.getInt("HatType", Configuration.CATEGORY_GENERAL, 0, 0, 10, "Type of hat (0 == null)");
        
        
    }

    public static void saveSettings(){
      // features
    	config.get(Configuration.CATEGORY_GENERAL, "BlockFlowerPlacing", false, "Block Flower from Placing").set(SettingsConfig.BlockFlowerPlacing);
        config.get(Configuration.CATEGORY_GENERAL, "FishingTimer", false, "FishingTimer").set(SettingsConfig.FishingTimer);
        config.get(Configuration.CATEGORY_GENERAL, "BonzoTimer", false, "BonzoMask Timer").set(SettingsConfig.BonzoTimer);
        config.get(Configuration.CATEGORY_GENERAL, "SpiritTimer", false, "SpiritMask Timer").set(SettingsConfig.SpiritTimer);
        config.get(Configuration.CATEGORY_GENERAL, "GhostBlocks", false, "Ghost Blocks").set(SettingsConfig.GhostBlocks);
        config.get(Configuration.CATEGORY_GENERAL, "GhostPickaxe", false, "Ghost Pickaxe").set(SettingsConfig.GhostPickaxe);
        config.get(Configuration.CATEGORY_GENERAL, "HidePlayers", false, "Hides Players").set(SettingsConfig.HidePlayers);
        config.get(Configuration.CATEGORY_GENERAL, "GhostPickaxeSlot", 5, "Ghost Pickaxe Slot").set(SettingsConfig.GhostPickaxeSlot);
        config.get(Configuration.CATEGORY_GENERAL, "AntyGhostBlocks", false, "Removes Ghost Blocks").set(SettingsConfig.AntyGhostBlocks);
        config.get(Configuration.CATEGORY_GENERAL, "CoordsDetection", false, "Coords Detection").set(SettingsConfig.CoordsDetection);
        config.get(Configuration.CATEGORY_GENERAL, "NickMentionDetection", false, "Username mentioning detection").set(SettingsConfig.NickMentionDetection);
        config.get(Configuration.CATEGORY_GENERAL, "JawbusDetection", false, "").set(SettingsConfig.JawbusDetection);
        
        // cosmetics
        config.get(Configuration.CATEGORY_GENERAL, "WingsType", 0, "Type of wings (0 == null)").set(CosmeticHandler.WingsType);
        config.get(Configuration.CATEGORY_GENERAL, "HatType", 0, "Type of hat (0 == null)").set(CosmeticHandler.HatType);
         
        config.save();
    }
	
}
