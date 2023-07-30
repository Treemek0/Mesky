package treemek.mesky.handlers;

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

import treemek.mesky.Mesky;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Waypoints;

public class Config {

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
        
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void SaveAlert(List<Alerts.Alert> alerts) {
    	// saves correctly
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/meskyAlerts.json")) {
            new GsonBuilder().create().toJson(alerts, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void SaveWaypoint(List<Waypoints.Waypoint> waypoints) {
    	// saves correctly
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/meskyWaypoints.json")) {
            new GsonBuilder().create().toJson(waypoints, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
	
}
