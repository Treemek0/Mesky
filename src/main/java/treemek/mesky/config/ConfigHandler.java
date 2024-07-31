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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import net.minecraftforge.common.config.Configuration;
import scala.util.parsing.input.Reader;
import treemek.mesky.Mesky;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.handlers.gui.cosmetics.CustomCapeGui;
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
                	setAllVariablesFromSave(SettingsConfig.class, settings);
                	setAllVariablesFromSave(CosmeticHandler.class, settings);
	                
	                if(CosmeticHandler.CapeType.number == 5) CustomCapeGui.iterateImagesInFolder(CosmeticHandler.CustomCapeTexture.text, "CustomCape");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }     
    }
    
    public static void saveSettings(){
    	Map<String, Setting> settings = new LinkedHashMap<>(); // linked so it would be in order
    	
    	settings.putAll(saveAllVariablesFromClass(SettingsConfig.class));
    	settings.putAll(saveAllVariablesFromClass(CosmeticHandler.class));

    	new File(Mesky.configDirectory + "/mesky/").mkdirs();
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/meskySettings.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(settings, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private static void setAllVariablesFromSave(Class c, Map<String, Setting> settings) {
    	try {	
    		Field[] fields = c.getDeclaredFields();;
	        for (Field field : fields) {
	        	if(field.getType() == Setting.class) {
	        		field.setAccessible(true);
	        		Setting setting = (Setting) field.get(null);
	                if (setting != null) {
	                    String fieldName = field.getName();

	                    setting = safelyGetInfo(settings.get(fieldName), setting);
	                }
	        	}
	        }
        } catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
    }
    
    private static Map<String, Setting> saveAllVariablesFromClass(Class c) {
    	Map<String, Setting> settings = new LinkedHashMap<>();
    	
    	try {	
    		Field[] fields = c.getDeclaredFields();;
	        for (Field field : fields) {
	        	if(field.getType() == Setting.class) {
	        		field.setAccessible(true);
	        		Setting setting = (Setting) field.get(null);
	                if (setting != null) {
	                    String fieldName = field.getName();

	                    settings.put(fieldName, setting);
	                }
	        	}
	        }
        } catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
    	
    	return settings;
    }
    
    private static Setting safelyGetInfo(Setting objectFromFile, Setting oldSetting) {
    	if(objectFromFile != null) {
    		for (Field field : Setting.class.getDeclaredFields()) { // its so when i update setting class then users wont have to delete config file
                field.setAccessible(true);
                try {
                    Object newValue = field.get(objectFromFile);
                    if (newValue != null) {
                        field.set(oldSetting, newValue); // this sets variable in oldSetting to new one if isnt null
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
    		
            return oldSetting; // after iterating thru all variables returns
    	}else {
    		return oldSetting;
    	}
    }

    
	
}
