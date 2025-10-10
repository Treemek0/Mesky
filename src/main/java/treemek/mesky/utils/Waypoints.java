package treemek.mesky.utils;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.brotli.dec.BrotliInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.waypoints.WaypointsGui;
import treemek.mesky.utils.Alerts.Alert;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.Waypoints.Waypoint;

public class Waypoints {

	// <String> name, <World> world, <int[]> coords
	public static Map<String, WaypointGroup> waypointsList = new LinkedHashMap<>();
	public static List<TemporaryWaypoint> temporaryWaypointsList = new ArrayList<TemporaryWaypoint>();
	public static List<TouchWaypoint> touchWaypointsList = new ArrayList<TouchWaypoint>();

	// make waypointsList hashmap with WaypointGroup instead list because enabled
	public static class WaypointGroup {
		public List<Waypoint> list;
		public boolean enabled;
		public boolean opened; // only for gui
		public String world;
		
		public WaypointGroup(List<Waypoint> list, String world, boolean enabled, boolean opened) {
			this.list = list;
			this.world = world;
			this.enabled = enabled;
			this.opened = opened;
		}
	}
	
    // Waypoint class definition
    public static class Waypoint {
        public String name;
        public float[] coords;
        public String world;
        public String color;
        public Float scale;
        public Boolean enabled;

        public Waypoint(String name, String color, float x, float y, float z, String world, float scale, boolean enabled) {
            this.name = name;
            this.coords = new float[]{x, y, z};
            this.world = world;
            this.color = ColorUtils.fixColor(color);
            this.scale = scale;
            this.enabled = enabled;
        }
        
        public void fixNulls() {
        	if(name == null) name = " ";
        	if(coords == null) coords = new float[] {0,0,0};
        	if(world == null) world = " ";
        	if(enabled == null) enabled = true;
        	if(color == null) color = "ffffff";
        	if(scale == null) scale = 1f;
        }
    }
    
    public static class TemporaryWaypoint {
        public String name;
        public float[] coords;
        public String world;
        public String color;
        public float scale;
        public long startTime;
        public long lifeTime;

        public TemporaryWaypoint(String name, String color, float x, float y, float z, float scale, long startTime, long lifeTime) {
            this.name = name;
            this.coords = new float[]{x, y, z};
            this.scale = scale;
            this.color = ColorUtils.fixColor(color);
            this.startTime = startTime;
            this.lifeTime = lifeTime;
        } 
    }
    
    public static class TouchWaypoint {
        public String name;
        public float[] coords;
        public String color;
        public float scale;
		public float touchRadius;
		public Long startTime;
        public Long lifeTime;

        public TouchWaypoint(String name, String color, float x, float y, float z, float scale, float touchRadius, Long lifeTime) {
            this.name = name;
            this.coords = new float[]{x, y, z};
            this.scale = scale;
            this.color = ColorUtils.fixColor(color);
            this.touchRadius = touchRadius;
            if(lifeTime != null) {
	            this.startTime = System.currentTimeMillis();
	            this.lifeTime = lifeTime;
            }
        }
        
    }

    public static Waypoint addWaypoint(String name, String color, float x, float y, float z, float scale) {
    	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	color = color.replace("#", "");
    	
    	String world = Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld);
    	Waypoint waypoint;
		waypoint = new Waypoint(name, color, x, y, z, world, scale, true);
		waypointsList.computeIfAbsent("default", k -> new WaypointGroup(new ArrayList<>(), world, true, true)).list.add(0, waypoint);
		ConfigHandler.SaveWaypoint(waypointsList);
		return waypoint;
    }
    
    public static Waypoint addWaypoint(String name, String color, float x, float y, float z, float scale, String group) {
    	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	color = color.replace("#", "");
    	
    	String world = Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld);
    	Waypoint waypoint;
		waypoint = new Waypoint(name, color, x, y, z, world, scale, true);
		waypointsList.computeIfAbsent(group, k -> new WaypointGroup(new ArrayList<>(), world, true, true)).list.add(0, waypoint);
		ConfigHandler.SaveWaypoint(waypointsList);
		return waypoint;
    }
    
    public static TemporaryWaypoint addTemporaryWaypoint(String name, String color, float x, float y, float z, float scale, long lifeTime) {
    	TemporaryWaypoint waypoint = new TemporaryWaypoint(name, color, x, y, z, scale, System.currentTimeMillis(), lifeTime);
    	temporaryWaypointsList.add(waypoint);
		return waypoint;
    }
    
    public static TouchWaypoint addTouchWaypoint(String name, String color, float x, float y, float z, float scale, float touchRadius, Long lifeTime) {
    	TouchWaypoint waypoint = new TouchWaypoint(name, color, x, y, z, scale, touchRadius, lifeTime);
    	touchWaypointsList.add(waypoint);
		return waypoint;
    }
    
    public static void deleteWaypoint(String group, int number) {
        List<Waypoint> list = waypointsList.get(group).list;
        
        if (list != null && number >= 0 && number < list.size()) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Deleted waypoint: " + EnumChatFormatting.GOLD + list.get(number).name));
            list.remove(number);
            ConfigHandler.SaveWaypoint(waypointsList);
        }
    }
   
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (!waypointsList.isEmpty()) {
        	for (WaypointGroup group : waypointsList.values()) {
        		if(!group.enabled) continue;
        		
        	    for (Waypoint waypoint : group.list) {
	            	if(!waypoint.enabled) continue;
	            	if(waypoint.world.equals(Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld))) {
	                    waypoint.color = ColorUtils.fixColor(waypoint.color);
	                    RenderHandler.draw3DWaypointString(ColorUtils.getColoredText(waypoint.name), waypoint.color, waypoint.coords, event.partialTicks, waypoint.scale);  
	                    
	                    try {
	                    	BlockPos waypointPos = new BlockPos(waypoint.coords[0], waypoint.coords[1], waypoint.coords[2]);
	                    	float x = (float) Math.floor(waypointPos.getX());
	                        float y = (float) Math.floor(waypointPos.getY());
	                        float z = (float) Math.floor(waypointPos.getZ());
	                    	AxisAlignedBB aabb = new AxisAlignedBB(x + 1.001, y + 1.001, z + 1.001, x - 0.001, y - 0.001, z - 0.001);
	                    	RenderHandler.draw3DBox(aabb, waypoint.color, event.partialTicks);
	                    	RenderHandler.draw3DImage(x + 0.5f, y + 0.5f, z + 0.5f, 0, 0, 0.5f, 0.5f, new ResourceLocation(Reference.MODID, "gui/marker.png"), ColorUtils.getColorInt(waypoint.color), false, event.partialTicks);
						} catch (Exception e) {
							e.printStackTrace();
						}
	                }
        	    }
            }
        }
        if (!temporaryWaypointsList.isEmpty()) {
        	Iterator<TemporaryWaypoint> iterator = temporaryWaypointsList.iterator();
        	while (iterator.hasNext()) {
        	    TemporaryWaypoint waypoint = iterator.next();
                waypoint.color = ColorUtils.fixColor(waypoint.color);
        		RenderHandler.draw3DWaypointString(ColorUtils.getColoredText(waypoint.name), waypoint.color, waypoint.coords, event.partialTicks, waypoint.scale);  
                
                try {
                	BlockPos waypointPos = new BlockPos(waypoint.coords[0], waypoint.coords[1], waypoint.coords[2]);
                	float x = (float) Math.floor(waypointPos.getX());
                    float y = (float) Math.floor(waypointPos.getY());
                    float z = (float) Math.floor(waypointPos.getZ());
                	AxisAlignedBB aabb = new AxisAlignedBB(x + 1.001, y + 1.001, z + 1.001, x - 0.001, y - 0.001, z - 0.001);
                	RenderHandler.draw3DBox(aabb, waypoint.color, event.partialTicks);
                	RenderHandler.draw3DImage(x + 0.5f, y + 0.5f, z + 0.5f, 0, 0, 0.5f, 0.5f, new ResourceLocation(Reference.MODID, "gui/clock.png"), ColorUtils.getColorInt(waypoint.color), false, event.partialTicks);
				} catch (Exception e) {
					e.printStackTrace();
				}
               
                if (System.currentTimeMillis() - waypoint.startTime >= waypoint.lifeTime) {
		            iterator.remove();
		        }
            }
        }
        
        if (!touchWaypointsList.isEmpty()) {
        	Iterator<TouchWaypoint> iterator = touchWaypointsList.iterator();
        	while (iterator.hasNext()) {
        	    TouchWaypoint waypoint = iterator.next();
                waypoint.color = ColorUtils.fixColor(waypoint.color);
        		RenderHandler.draw3DWaypointString(ColorUtils.getColoredText(waypoint.name), waypoint.color, waypoint.coords, event.partialTicks, waypoint.scale);  
                
                try {
                	BlockPos waypointPos = new BlockPos(waypoint.coords[0], waypoint.coords[1], waypoint.coords[2]);
                	float x = (float) Math.floor(waypointPos.getX());
                    float y = (float) Math.floor(waypointPos.getY());
                    float z = (float) Math.floor(waypointPos.getZ());
                	AxisAlignedBB aabb = new AxisAlignedBB(x + 1.001, y + 1.001, z + 1.001, x - 0.001, y - 0.001, z - 0.001);
                	RenderHandler.draw3DBox(aabb, waypoint.color, event.partialTicks);
                	RenderHandler.draw3DImage(x + 0.5f, y + 0.5f, z + 0.5f, 0, 0, 0.5f, 0.5f, new ResourceLocation(Reference.MODID, "gui/touch.png"), ColorUtils.getColorInt(waypoint.color), false, event.partialTicks);
				} catch (Exception e) {
					e.printStackTrace();
				}
               
                if (Minecraft.getMinecraft().thePlayer.getDistance(waypoint.coords[0], waypoint.coords[1], waypoint.coords[2]) <= waypoint.touchRadius) {
		            iterator.remove();
		        }
                
                if(waypoint.lifeTime != null) {
	                if (System.currentTimeMillis() - waypoint.startTime >= waypoint.lifeTime) {
			            iterator.remove();
			        }
                }
            }
        }
    }
    
    public static Map<String, WaypointGroup> GetLocationWaypoints() {
        Map<String, WaypointGroup> groupedWaypoints = new LinkedHashMap<>();
        Location.checkTabLocation();
        String currentWorld = Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld); // Ensure Utils and getWorldIdentifierWithRegionTextField exist

        for (Entry<String, WaypointGroup> entry : waypointsList.entrySet()) {
        	if(!entry.getValue().world.equals(currentWorld)) continue;
        	
            List<Waypoint> filteredWaypoints = new ArrayList<>();
            for (Waypoint waypoint : entry.getValue().list) {
                if (waypoint.world.equals(currentWorld)) {
                    filteredWaypoints.add(new Waypoint(waypoint.name, waypoint.color, waypoint.coords[0], waypoint.coords[1], waypoint.coords[2], waypoint.world, waypoint.scale, waypoint.enabled));
                }
            }

            if (!filteredWaypoints.isEmpty() || entry.getValue().list.isEmpty()) {
                WaypointGroup newGroup = new WaypointGroup(filteredWaypoints, entry.getValue().world, entry.getValue().enabled, entry.getValue().opened);
                groupedWaypoints.put(entry.getKey(), newGroup);
            }
        }

        return groupedWaypoints;
    }

    public static Map<String, WaypointGroup> GetWaypointsWithoutLocation() {
        Map<String, WaypointGroup> groupedWaypoints = new LinkedHashMap<>();
        String currentWorld = Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld);

        for (Entry<String, WaypointGroup> entry : waypointsList.entrySet()) {
        	if(entry.getValue().world.equals(currentWorld)) continue;
        	
            List<Waypoint> filteredWaypoints = new ArrayList<>();
            for (Waypoint waypoint : entry.getValue().list) {
                if (!waypoint.world.equals(currentWorld)) {
                    filteredWaypoints.add(new Waypoint(waypoint.name, waypoint.color, waypoint.coords[0], waypoint.coords[1], waypoint.coords[2], waypoint.world, waypoint.scale, waypoint.enabled));
                }
            }

            if (!filteredWaypoints.isEmpty()) {
                WaypointGroup newGroup = new WaypointGroup(filteredWaypoints, entry.getValue().world, entry.getValue().enabled, entry.getValue().opened);
                groupedWaypoints.put(entry.getKey(), newGroup);
            }
        }

        return groupedWaypoints;
    }
    
    
    public static LinkedHashMap<String, WaypointGroup> importSkytilsWaypoints(String input) throws IOException {
        if (!input.startsWith("<Skytils-Waypoint-Data>(V")) {
        	Utils.writeError("Invalid Skytils format");
        	return null;
        }
           

        int version = Integer.parseInt(input.substring(input.indexOf('V') + 1, input.indexOf(')')));
        String base64 = input.substring(input.indexOf(':') + 1);
        byte[] decoded = Base64.getDecoder().decode(base64);

        InputStream decompressed;
        if (version == 1) {
            decompressed = new GZIPInputStream(new ByteArrayInputStream(decoded));
        } else if (version == 2) {
        	decompressed = new BrotliInputStream(new ByteArrayInputStream(decoded));
        } else {
        	Utils.writeError("Unknown Skytils waypoint version: " + version);
            return null;
        }

        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = decompressed.read(buffer)) != -1) {
            sb.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
        }

        String json = sb.toString();
        JsonObject root = new Gson().fromJson(json, JsonObject.class);
        JsonArray categories = root.getAsJsonArray("categories");

        LinkedHashMap<String, WaypointGroup> groups = new LinkedHashMap<>();

        for (JsonElement catEl : categories) {
            JsonObject cat = catEl.getAsJsonObject();
            String categoryName = cat.has("name") ? cat.get("name").getAsString() : null;
            JsonArray wps = cat.getAsJsonArray("waypoints");
            String worldRaw = cat.has("island") ? cat.get("island").getAsString() : "Unknown";
            String world = Utils.convertSkytilsRegionToLocation(worldRaw);

            List<Waypoint> list = new ArrayList<>();

            for (JsonElement wpEl : wps) {
                JsonObject wp = wpEl.getAsJsonObject();
                String name = wp.has("name") ? wp.get("name").getAsString() : "Unnamed";
                int x = wp.get("x").getAsInt();
                int y = wp.get("y").getAsInt();
                int z = wp.get("z").getAsInt();
                String color = wp.has("color") ? wp.get("color").getAsString().replace("#", "") : "ffffff";

                list.add(new Waypoint(name, color, x, y, z, world, 1f, true));
            }

            String key = (categoryName != null ? categoryName : "skytils") + " %" + world;
            groups.put(key, new WaypointGroup(list, world, true, true));
        }

        return groups;
    }
    
    public static LinkedHashMap<String, WaypointGroup> importSkytilsWaypoints(String input, String island) throws IOException {
        if (!input.startsWith("<Skytils-Waypoint-Data>(V")) {
        	Utils.writeError("Invalid Skytils format");
        	return null;
        }
           

        int version = Integer.parseInt(input.substring(input.indexOf('V') + 1, input.indexOf(')')));
        String base64 = input.substring(input.indexOf(':') + 1);
        byte[] decoded = Base64.getDecoder().decode(base64);

        InputStream decompressed;
        if (version == 1) {
            decompressed = new GZIPInputStream(new ByteArrayInputStream(decoded));
        } else if (version == 2) {
        	decompressed = new BrotliInputStream(new ByteArrayInputStream(decoded));
        } else {
        	Utils.writeError("Unknown Skytils waypoint version: " + version);
            return null;
        }

        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = decompressed.read(buffer)) != -1) {
            sb.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
        }

        String json = sb.toString();
        JsonObject root = new Gson().fromJson(json, JsonObject.class);
        JsonArray categories = root.getAsJsonArray("categories");

        LinkedHashMap<String, WaypointGroup> groups = new LinkedHashMap<>();

        for (JsonElement catEl : categories) {
            JsonObject cat = catEl.getAsJsonObject();
            String categoryName = cat.has("name") ? cat.get("name").getAsString() : null;
            JsonArray wps = cat.getAsJsonArray("waypoints");
            String world = island;

            List<Waypoint> list = new ArrayList<>();

            for (JsonElement wpEl : wps) {
                JsonObject wp = wpEl.getAsJsonObject();
                String name = wp.has("name") ? wp.get("name").getAsString() : "Unnamed";
                int x = wp.get("x").getAsInt();
                int y = wp.get("y").getAsInt();
                int z = wp.get("z").getAsInt();
                String color = wp.has("color") ? wp.get("color").getAsString().replace("#", "") : "ffffff";

                list.add(new Waypoint(name, color, x, y, z, world, 1f, true));
            }

            String key = (categoryName != null ? categoryName : "skytils") + " %" + world;
            groups.put(key, new WaypointGroup(list, world, true, true));
        }

        return groups;
    }

    public static String exportCurrentLocationWaypointsSkytilsFormat() throws IOException {
        Map<String, WaypointGroup> currentLocationWaypoints = GetLocationWaypoints();

        // Build a Skytils-like JSON structure from your waypoints
        JsonArray categories = new JsonArray();

        for (Map.Entry<String, WaypointGroup> entry : currentLocationWaypoints.entrySet()) {
            WaypointGroup group = entry.getValue();

            JsonObject category = new JsonObject();
            String categoryName = entry.getKey().substring(0, entry.getKey().lastIndexOf(" %" + group.world));
            category.addProperty("name", categoryName);

            JsonArray waypoints = new JsonArray();
            for (Waypoint wp : group.list) {
                JsonObject jsonWp = new JsonObject();
                jsonWp.addProperty("name", wp.name);
                jsonWp.addProperty("x", (int) wp.coords[0]);
                jsonWp.addProperty("y", (int) wp.coords[1]);
                jsonWp.addProperty("z", (int) wp.coords[2]);
                jsonWp.addProperty("color", "#" + wp.color);
                waypoints.add(jsonWp);
            }
            category.add("waypoints", waypoints);
            category.addProperty("island", (HypixelCheck.isOnHypixel())?group.world.toLowerCase().replace(" ", "_"):group.world); // skytils style

            categories.add(category);
        }

        JsonObject root = new JsonObject();
        root.add("categories", categories);

        String jsonString = root.toString();

        // Compress with GZIP
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
            gzipStream.write(jsonString.getBytes("UTF-8"));
        }

        // Base64 encode
        String base64 = Base64.getEncoder().encodeToString(byteStream.toByteArray());

        // Version 1 = GZIP compression in Skytils format
        return "<Skytils-Waypoint-Data>(V1):" + base64;
    }
    
    public static String exportWaypointsSkytilsFormat(Map<String, WaypointGroup> givenWaypoints) throws IOException {
        // Build a Skytils-like JSON structure from your waypoints
        JsonArray categories = new JsonArray();

        for (Map.Entry<String, WaypointGroup> entry : givenWaypoints.entrySet()) {
            WaypointGroup group = entry.getValue();

            JsonObject category = new JsonObject();
            String categoryName = entry.getKey().substring(0, entry.getKey().lastIndexOf(" %" + group.world));
            category.addProperty("name", categoryName);

            JsonArray waypoints = new JsonArray();
            for (Waypoint wp : group.list) {
                JsonObject jsonWp = new JsonObject();
                jsonWp.addProperty("name", wp.name);
                jsonWp.addProperty("x", (int) wp.coords[0]);
                jsonWp.addProperty("y", (int) wp.coords[1]);
                jsonWp.addProperty("z", (int) wp.coords[2]);
                jsonWp.addProperty("color", "#" + wp.color);
                waypoints.add(jsonWp);
            }
            category.add("waypoints", waypoints);
            category.addProperty("island", (HypixelCheck.isOnHypixel())?group.world.toLowerCase().replace(" ", "_"):group.world); // skytils style

            categories.add(category);
        }

        JsonObject root = new JsonObject();
        root.add("categories", categories);

        String jsonString = root.toString();

        // Compress with GZIP
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
            gzipStream.write(jsonString.getBytes("UTF-8"));
        }

        // Base64 encode
        String base64 = Base64.getEncoder().encodeToString(byteStream.toByteArray());

        // Version 1 = GZIP compression in Skytils format
        return "<Skytils-Waypoint-Data>(V1):" + base64;
    }
	
    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
    	if (event.world.isRemote && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (player == Minecraft.getMinecraft().thePlayer) {
            	temporaryWaypointsList.clear();
        		touchWaypointsList.clear();
            }
        }
    }
    
    @SubscribeEvent
    public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
    	temporaryWaypointsList.clear();
		touchWaypointsList.clear();
    }
}