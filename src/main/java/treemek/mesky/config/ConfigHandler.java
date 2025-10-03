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

import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import scala.util.parsing.input.Reader;
import treemek.mesky.Mesky;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.features.LockSlot;
import treemek.mesky.handlers.ItemsHandler;
import treemek.mesky.handlers.RecipeHandler;
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
import treemek.mesky.utils.chat.ChatFilter;

import java.io.Writer;

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

        try {
            reloadSettings();
            Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded settings " + EnumChatFormatting.GREEN + '\u2713');
        } catch (FileNotFoundException e) {
            Utils.writeError("There was a problem with reloading settings: " + e.getMessage());
        }

        try {
            reloadAlerts();
            Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded alerts " + EnumChatFormatting.GREEN + '\u2713');
        } catch (IOException e) {
            Utils.writeError("There was a problem with reloading alerts: " + e.getMessage());
        }

        try {
            reloadChatFunctions();
            Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded chatFunctions " + EnumChatFormatting.GREEN + '\u2713');
        } catch (IOException e) {
            Utils.writeError("There was a problem with reloading chatFunctions: " + e.getMessage());
        }

        try {
            reloadWaypoints();
            Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded waypoints " + EnumChatFormatting.GREEN + '\u2713');
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
            Utils.writeError("There was a problem with reloading waypoints: " + e.getMessage());
        }

        try {
            reloadMacroWaypoints();
            Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded macroWaypoints " + EnumChatFormatting.GREEN + '\u2713');
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
            Utils.writeError("There was a problem with reloading macroWaypoints: " + e.getMessage());
        }

        try {
            reloadMiningPaths();
            Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded miningPaths " + EnumChatFormatting.GREEN + '\u2713');
        } catch (IOException e) {
            Utils.writeError("There was a problem with reloading miningPaths: " + e.getMessage());
        }

        try {
            reloadSlotLocks();
            Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded slotLocks " + EnumChatFormatting.GREEN + '\u2713');
        } catch (IOException e) {
            Utils.writeError("There was a problem with reloading slotLocks: " + e.getMessage());
        }

		RecipeHandler.reloadRecipes();
		ItemsHandler.reloadItemIdMapping();
        
		SoundsHandler.reloadSounds();
		Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded sounds " + EnumChatFormatting.GREEN + '\u2713');
    }

    public static void reloadAlerts() throws IOException {
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

    }
    
    public static void reloadChatFunctions() throws IOException {
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
    }

    public static void reloadSlotLocks() throws IOException {
        File file = new File(directory + "/mesky/utils/meskySlotLocks.json");
        if (file.exists()) {
            Map<String, Object> data = new Gson().fromJson(
                new FileReader(file),
                new TypeToken<Map<String, Object>>() {}.getType()
            );

            LockSlot.lockedSlots.clear();
            LockSlot.connectedSlots.clear();

            if (data.containsKey("lockedSlots")) {
                Map<String, Boolean> locked = new Gson().fromJson(
                    new Gson().toJson(data.get("lockedSlots")),
                    new TypeToken<Map<String, Boolean>>() {}.getType()
                );
                for (Map.Entry<String, Boolean> entry : locked.entrySet()) {
                    try {
                        LockSlot.lockedSlots.put(Integer.parseInt(entry.getKey()), entry.getValue());
                    } catch (NumberFormatException ignored) {}
                }
            }

            if (data.containsKey("connectedSlots")) {
                Map<String, String> connected = new Gson().fromJson(
                    new Gson().toJson(data.get("connectedSlots")),
                    new TypeToken<Map<String, String>>() {}.getType()
                );
                for (Map.Entry<String, String> entry : connected.entrySet()) {
                    try {
                        LockSlot.connectedSlots.put(Integer.parseInt(entry.getKey()), Integer.parseInt(entry.getValue()));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
    }


    
    public static void reloadWaypoints() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
        File file = new File(directory + "/mesky/utils/meskyWaypoints.json");
        if (!file.exists()) return;

        JsonElement json = new JsonParser().parse(new FileReader(file));
        Map<String, WaypointGroup> tempLoadedGroups = new LinkedHashMap<>();

        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                String originalKey = entry.getKey();
                WaypointGroup loadedGroup = new Gson().fromJson(entry.getValue(), WaypointGroup.class);

                if (loadedGroup != null && loadedGroup.list != null) {
                    // Fix nulls and group waypoints by world
                    Map<String, List<Waypoint>> byWorld = new HashMap<>();

                    for (Waypoint w : loadedGroup.list) {
                        if (w == null || w.world == null) continue;
                        w.fixNulls();
                        byWorld.computeIfAbsent(w.world, k -> new ArrayList<>()).add(w);
                    }

                    // Split into per-world groups
                    for (Map.Entry<String, List<Waypoint>> groupEntry : byWorld.entrySet()) {
                        String world = groupEntry.getKey();
                        List<Waypoint> worldList = groupEntry.getValue();

                        String newKey = (originalKey.endsWith(" %" + world)) ? originalKey : originalKey + " %" + world;
                        WaypointGroup newGroup = new WaypointGroup(worldList, world, loadedGroup.enabled, loadedGroup.opened);

                        tempLoadedGroups.put(newKey, newGroup);
                    }
                }
            }
        } else if (json.isJsonArray()) { // legacy
            Waypoint[] flatWaypoints = new Gson().fromJson(json, Waypoint[].class);

            Map<String, List<Waypoint>> byWorld = new HashMap<>();

            for (Waypoint w : flatWaypoints) {
                if (w == null || w.world == null) continue;
                w.fixNulls();
                byWorld.computeIfAbsent(w.world, k -> new ArrayList<>()).add(w);
            }

            for (Map.Entry<String, List<Waypoint>> entry : byWorld.entrySet()) {
                String world = entry.getKey();
                List<Waypoint> list = entry.getValue();

                tempLoadedGroups.put("default %" + world, new WaypointGroup(list, world, true, true));
            }
        }

        Waypoints.waypointsList = tempLoadedGroups;
    }

    public static void reloadMacroWaypoints() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
    		File file = new File(directory + "/mesky/utils/meskyMacroWaypoints.json");
    		if (!file.exists()) return;
    		
            JsonElement json = new JsonParser().parse(new FileReader(file));
            Map<String, MacroWaypointGroup> tempLoadedGroups = new LinkedHashMap<>();

            if (json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    String originalKey = entry.getKey();
                    MacroWaypointGroup loadedGroup = new Gson().fromJson(entry.getValue(), MacroWaypointGroup.class);

                    if (loadedGroup != null && loadedGroup.list != null) {
                        // Fix nulls and group waypoints by world
                        Map<String, List<MacroWaypoint>> byWorld = new HashMap<>();

                        for (MacroWaypoint w : loadedGroup.list) {
                            if (w == null || w.waypoint == null || w.waypoint.world == null) continue;
                            w.fixNulls();
                            byWorld.computeIfAbsent(w.waypoint.world, k -> new ArrayList<>()).add(w);
                        }

                        // Split into per-world groups
                        for (Map.Entry<String, List<MacroWaypoint>> groupEntry : byWorld.entrySet()) {
                            String world = groupEntry.getKey();
                            List<MacroWaypoint> worldList = groupEntry.getValue();

                            String newKey = (originalKey.endsWith(" %" + world))?originalKey:originalKey + " %" + world;
                            MacroWaypointGroup newGroup = new MacroWaypointGroup(worldList, world, loadedGroup.enabled, loadedGroup.opened);

                            tempLoadedGroups.put(newKey, newGroup);
                        }
                    }
                }
            }else if (json.isJsonArray()) { // legacy
	    	    MacroWaypoint[] flatWaypoints = new Gson().fromJson(json, MacroWaypoint[].class);
	
	    	    Map<String, List<MacroWaypoint>> byWorld = new HashMap<>();
	
	    	    for (MacroWaypoint w : flatWaypoints) {
	    	        if (w == null || w.waypoint == null || w.waypoint.world == null) continue;
	    	        w.fixNulls();
	    	        byWorld.computeIfAbsent(w.waypoint.world, k -> new ArrayList<>()).add(w);
	    	    }
	
	    	    for (Map.Entry<String, List<MacroWaypoint>> entry : byWorld.entrySet()) {
	    	        String world = entry.getKey();
	    	        List<MacroWaypoint> list = entry.getValue();
	
	    	        tempLoadedGroups.put("default %" + world, new MacroWaypointGroup(list, world, true, true));
	    	    }
    	}

            
            MacroWaypoints.waypointsList = tempLoadedGroups;
    }
    
    public static void reloadMiningPaths() throws IOException {
    		if(new File(directory + "/mesky/utils/meskyMiningPaths.json").exists()) {
	            Object[] paths = initJsonArray(directory + "/mesky/utils/meskyMiningPaths.json", MiningUtils.MiningPath[].class);
	            if (paths != null) {
	            	ArrayList<MiningPath> list = new ArrayList<>(Arrays.asList((MiningUtils.MiningPath[]) paths));
	            	
	            	MiningUtils.miningPaths = list;
	            }
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
	
    public static void saveSlotLocks(Map<Integer, Boolean> lockedSlots, Map<Integer, Integer> connectedSlots) {
    	new File(Mesky.configDirectory + "/mesky/utils/").mkdirs();
        File file = new File(directory + "/mesky/utils/meskySlotLocks.json");
        Map<String, Object> data = new LinkedHashMap<>();

        // Convert lockedSlots keys to String for JSON compatibility
        Map<String, Boolean> lockedSlotsStrKey = new LinkedHashMap<>();
        for (Map.Entry<Integer, Boolean> entry : lockedSlots.entrySet()) {
            lockedSlotsStrKey.put(entry.getKey().toString(), entry.getValue());
        }

        // Convert connectedSlots keys and values to String
        Map<String, String> connectedSlotsStrKey = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : connectedSlots.entrySet()) {
            connectedSlotsStrKey.put(entry.getKey().toString(), entry.getValue().toString());
        }

        data.put("lockedSlots", lockedSlotsStrKey);
        data.put("connectedSlots", connectedSlotsStrKey);

        try (Writer writer = new FileWriter(file)) {
            new Gson().toJson(data, writer);
        } catch (IOException e) {
			e.printStackTrace();
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

    public static void reloadSettings() throws FileNotFoundException{
    	File file = new File(Mesky.configDirectory + "/mesky/meskySettings.json");
        if (file.exists()) {
        	FileReader reader = new FileReader(file);
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Setting>>(){}.getType();
            Map<String, Setting> settings = gson.fromJson(reader, type);

            if(settings != null) {
            	loadSettingsFromFileToClass(SettingsConfig.class, settings);
            	loadSettingsFromFileToClass(CosmeticHandler.class, settings);
                
                if(CosmeticHandler.CapeType.number == 5) CustomCapeGui.iterateImagesInFolder(CosmeticHandler.CustomCapeTexture.text, "CustomCape");
                if(ChatFilter.chat != null) ChatFilter.chat.wrapMessages();
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
