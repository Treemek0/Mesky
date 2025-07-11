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
import treemek.mesky.handlers.soundHandler.SoundsHandler;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Alerts.Alert;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.MacroWaypoints;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.MacroWaypoints.MacroWaypoint;
import treemek.mesky.utils.MacroWaypoints.MacroWaypointGroup;
import treemek.mesky.utils.MiningUtils;
import treemek.mesky.utils.MiningUtils.MiningPath;
import treemek.mesky.utils.ChatFunctions;
import treemek.mesky.utils.ChatFunctions.ChatFunction;
import treemek.mesky.utils.FriendsLocations;
import treemek.mesky.utils.Waypoints.Waypoint;
import treemek.mesky.utils.Waypoints.WaypointGroup;

public class ConfigHandler {
	
	static File directory = new File(Mesky.configDirectory);

    public static void createNewJsonArray(String file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(new JsonArray().toString());
        fileWriter.close();
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
        if (!directory.exists()) directory.mkdir();

		reloadSettings();
		reloadAlerts();
		reloadChatFunctions();
		reloadWaypoints();
		reloadMacroWaypoints();
		reloadMiningPaths();
		SoundsHandler.reloadSounds();
    }

    public static void reloadAlerts() {
        try {
	    	if(new File(directory + "/mesky/utils/meskyAlerts.json").exists()) {
	            Object[] alerts = initJsonArray(directory + "/mesky/utils/meskyAlerts.json", Alerts.Alert[].class);
	            if (alerts != null) {
	            	 ArrayList<Alert> list = new ArrayList<>(Arrays.asList((Alerts.Alert[]) alerts));
	            	 
	            	 for (Alert alert : list) {
						alert.fixNulls(); // fixing nulls (when adding new features or if someone do something with file)
					}
	            	 
	            	 Alerts.alertsList = list;
	            	 Alerts.putAllImagesToCache();
	            }
	        }
        } catch (Exception e) {
			Utils.writeError(e);
		}
    }
    
    public static void reloadChatFunctions() {
    	try {
	        if(new File(directory + "/mesky/utils/meskyChatFunctions.json").exists()) {
	            Object[] chatFunctions = initJsonArray(directory + "/mesky/utils/meskyChatFunctions.json", ChatFunctions.ChatFunction[].class);
	            if (chatFunctions != null) {
	            	 ArrayList<ChatFunction> list = new ArrayList<>(Arrays.asList((ChatFunctions.ChatFunction[]) chatFunctions));
	            	 
	            	 for (ChatFunction chatFunction : list) {
						chatFunction.fixNulls();
	            	 }
	            	 
	            	 ChatFunctions.chatFunctionsList = list;
	            }
	        }
    	} catch (Exception e) {
			Utils.writeError(e);
		}
    }
    

    
    public static void reloadWaypoints() {
        try {
            File file = new File(directory + "/mesky/utils/meskyWaypoints.json");
            if (!file.exists()) return;

            JsonElement json = new JsonParser().parse(new FileReader(file));
            Map<String, WaypointGroup> tempLoadedGroups = new LinkedHashMap<>();

            if (json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    String key = entry.getKey();

                    WaypointGroup loadedGroup = new Gson().fromJson(entry.getValue(), WaypointGroup.class);

                    if (loadedGroup != null && loadedGroup.list != null) {
                        for (Waypoint w : loadedGroup.list) {
                            if (w != null) {
                               w.fixNulls();
                            }
                        }
                        
                        if(loadedGroup.world == null) {
                        	if(!loadedGroup.list.isEmpty()) {
                        		loadedGroup.world = loadedGroup.list.get(0).world;
                        		
                        		key += " %" + loadedGroup.world;
                        	}
                        }
                    }
                    
                    tempLoadedGroups.put(key, loadedGroup);
                }
            } else if (json.isJsonArray()) { // legacy
                Waypoint[] flatWaypoints = new Gson().fromJson(json, Waypoint[].class);
                List<Waypoint> list = new ArrayList<>(Arrays.asList(flatWaypoints));

                for (Waypoint w : list) w.fixNulls();

                String world = null;
                
                if(!list.isEmpty()) {
            		world = list.get(0).world;
            	}
                
                tempLoadedGroups.put("default %" + world, new WaypointGroup(list, world, true, true));
            }
            
            Waypoints.waypointsList = tempLoadedGroups;

        } catch (Exception e) {
            Utils.writeError(e);
        }
    }

    
    public static void reloadMacroWaypoints() {
    	try {
    		File file = new File(directory + "/mesky/utils/meskyMacroWaypoints.json");
    		if (!file.exists()) return;
    		
            JsonElement json = new JsonParser().parse(new FileReader(file));
            Map<String, MacroWaypointGroup> tempLoadedGroups = new LinkedHashMap<>();

            if (json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    String key = entry.getKey();

                    MacroWaypointGroup loadedGroup = new Gson().fromJson(entry.getValue(), MacroWaypointGroup.class);

                    if (loadedGroup != null && loadedGroup.list != null) {
                        for (MacroWaypoint w : loadedGroup.list) {
                            if (w != null) {
                               w.fixNulls();
                            }
                        }
                        
                        if(loadedGroup.world == null) {
                        	if(!loadedGroup.list.isEmpty()) {
                        		loadedGroup.world = loadedGroup.list.get(0).waypoint.world;
                        		
                        		key += " %" + loadedGroup.world;
                        	}
                        }
                    }
                    
                    
                    
                    tempLoadedGroups.put(key, loadedGroup);
                }
            } else if (json.isJsonArray()) { // legacy
                MacroWaypoint[] flatWaypoints = new Gson().fromJson(json, MacroWaypoint[].class);
                List<MacroWaypoint> list = new ArrayList<>(Arrays.asList(flatWaypoints));

                for (MacroWaypoint w : list) w.fixNulls();

                String world = null;
                
            	if(!list.isEmpty()) {
            		world = list.get(0).waypoint.world;
            	}
                
                tempLoadedGroups.put("default %" + world, new MacroWaypointGroup(list, world, true, true));
            }

            MacroWaypoints.waypointsList = tempLoadedGroups;

        } catch (Exception e) {
            Utils.writeError(e);
        }
    }
    
    public static void reloadMiningPaths() {
    	try {
    		if(new File(directory + "/mesky/utils/meskyMiningPaths.json").exists()) {
	            Object[] paths = initJsonArray(directory + "/mesky/utils/meskyMiningPaths.json", MiningUtils.MiningPath[].class);
	            if (paths != null) {
	            	ArrayList<MiningPath> list = new ArrayList<>(Arrays.asList((MiningUtils.MiningPath[]) paths));
	            	
	            	MiningUtils.miningPaths = list;
	            }
            }
    	} catch (Exception e) {
			Utils.writeError(e);
		}
    }
    
	public static void SaveAlert(List<Alerts.Alert> alerts) {
		new File(Mesky.configDirectory + "/mesky/utils/").mkdirs();
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/utils/meskyAlerts.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(alerts, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
	public static void SaveChatFunction(List<ChatFunctions.ChatFunction> chatFunctions) {
		new File(Mesky.configDirectory + "/mesky/utils/").mkdirs();
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/utils/meskyChatFunctions.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(chatFunctions, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
	
    public static void SaveWaypoint(Map<String, WaypointGroup> waypointsList) {
    	new File(Mesky.configDirectory + "/mesky/utils/").mkdirs();
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/utils/meskyWaypoints.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(waypointsList, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void SaveMiningPaths(List<MiningUtils.MiningPath> paths) {
    	new File(Mesky.configDirectory + "/mesky/utils/").mkdirs();
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/utils/meskyMiningPaths.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(paths, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void SaveMacroWaypoint(Map<String, MacroWaypointGroup> waypointsList) {
    	new File(Mesky.configDirectory + "/mesky/utils/").mkdirs();
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/utils/meskyMacroWaypoints.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(waypointsList, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    // !!!
    // Saving friends locations is in FriendsLocations file
    // !!!

    public static void reloadSettings(){
    	File file = new File(Mesky.configDirectory + "/mesky/meskySettings.json");
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Setting>>(){}.getType();
                Map<String, Setting> settings = gson.fromJson(reader, type);

                if(settings != null) {
                	loadSettingsFromFileToClass(SettingsConfig.class, settings);
                	loadSettingsFromFileToClass(CosmeticHandler.class, settings);
	                
	                if(CosmeticHandler.CapeType.number == 5) CustomCapeGui.iterateImagesInFolder(CosmeticHandler.CustomCapeTexture.text, "CustomCape");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }     
    }
    
    public static void saveSettings(){
    	Map<String, Setting> settings = new LinkedHashMap<>(); // linked so it would be in order
    	
    	settings.putAll(saveAllSettingsFromClass(SettingsConfig.class));
    	settings.putAll(saveAllSettingsFromClass(CosmeticHandler.class));

    	new File(Mesky.configDirectory + "/mesky/").mkdirs();
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/meskySettings.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(settings, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private static void loadSettingsFromFileToClass(Class c, Map<String, Setting> settings) {
    	try {	
    		Field[] fields = c.getDeclaredFields();
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
    
    private static Map<String, Setting> saveAllSettingsFromClass(Class c) {
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
