package treemek.mesky.config;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.*;

import net.minecraftforge.common.config.Configuration;
import treemek.mesky.Mesky;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Alerts.Alert;
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
            File directory = new File(Mesky.configDirectory + "/mesky");
            if (!directory.exists()) directory.mkdir();


            // Alerts
            Object[] alerts = initJsonArray(directory + "/meskyAlerts.json", Alerts.Alert[].class);
            if (alerts != null) Alerts.alertsList = new ArrayList<>(Arrays.asList((Alerts.Alert[]) alerts));
        
            // Waypoints
            Object[] waypoints = initJsonArray(directory + "/meskyWaypoints.json", Waypoints.Waypoint[].class);
            if (waypoints != null) Waypoints.waypointsList = new ArrayList<>(Arrays.asList((Waypoints.Waypoint[]) waypoints));
            
            loadSettings();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

	public static void SaveAlert(List<Alerts.Alert> alerts) {
    	// saves correctly
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/meskyAlerts.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(alerts, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void SaveWaypoint(List<Waypoints.Waypoint> waypoints) {
    	// saves correctly
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/meskyWaypoints.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(waypoints, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
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
    
        // cosmetics
        CosmeticHandler.WingsType = config.getInt("WingsType", Configuration.CATEGORY_GENERAL, 0, 0, 10, "Type of wings (0 == null)");
    }

    public static void saveSettings(){
      // features
    	config.get(Configuration.CATEGORY_GENERAL, "BlockFlowerPlacing", false, "Block Flower from Placing").set(SettingsConfig.BlockFlowerPlacing);
        config.get(Configuration.CATEGORY_GENERAL, "FishingTimer", false, "FishingTimer").set(SettingsConfig.FishingTimer);
        config.get(Configuration.CATEGORY_GENERAL, "BonzoTimer", false, "BonzoMask Timer").set(SettingsConfig.BonzoTimer);
        config.get(Configuration.CATEGORY_GENERAL, "SpiritTimer", false, "SpiritMask Timer").set(SettingsConfig.SpiritTimer);
        config.get(Configuration.CATEGORY_GENERAL, "GhostBlocks", false, "Ghost Blocks").set(SettingsConfig.GhostBlocks);
        config.get(Configuration.CATEGORY_GENERAL, "GhostPickaxe", false, "Ghost Pickaxe").set(SettingsConfig.GhostPickaxe);
        
        // cosmetics
        config.get(Configuration.CATEGORY_GENERAL, "WingsType", 0, "Type of wings (0 == null)").set(CosmeticHandler.WingsType);
        
        
        config.save();
    }
	
}
