package treemek.mesky.utils;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class Locations {

	public static Location currentLocation = Location.NONE;
	public static String currentLocationText = " ";
	
	public enum Location {
	    NONE(" "),
	    CRIMSON_ISLE("Crimson Isle"),
	    CRYSTAL_HOLLOWS("Crystal Hollows"),
	    DEEP_CAVERNS("Deep Caverns"),
	    CATACOMBS("Catacombs"),
	    DUNGEON_HUB("Dungeon Hub"),
	    DWARVEN_MINES("Dwarven Mines"),
	    END("The End"),
	    FARMING_ISLANDS("The Farming Islands"),
	    GOLD_MINE("Gold Mine"),
	    HUB("Hub"),
	    INSTANCED("Instanced"),
	    JERRY_WORKSHOP("Jerry's Workshop"),
	    PRIVATE_ISLAND("Private Island"),
	    PARK("The Park"),
	    SPIDERS_DEN("Spider's Den"),
	    GARDEN("Garden"),
	    KUUDRA("Kuudra"),
	    RIFT("The Rift");
	
	 
		String text;
		
	
	    Location(String text) {
	        this.text = text;
	    }
	    
	
	    public static Location fromTab(String text) {
	        for (Location location : Location.values()) {
	            if (location.text.equalsIgnoreCase(text)) { 
	            	currentLocationText = text;
	            	return location;
	            }
	        }
	        return NONE;
	    }
	
	    public static void checkTabLocation() {
			if (HypixelCheck.isOnHypixel()) {
				Collection<NetworkPlayerInfo> players = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
				for (NetworkPlayerInfo player : players) {
					if (player == null || player.getDisplayName() == null) continue;
					String text = player.getDisplayName().getUnformattedText();
					if (text.startsWith("Area: ") || text.startsWith("Dungeon: ")) {
						currentLocation = Location.fromTab(text.substring(text.indexOf(":") + 2));
						return;
					}
				}
			}
			currentLocation = Location.NONE;
		}
	    
	}
	
	
	@SubscribeEvent
    public void onPlayerJoin(PlayerLoggedInEvent event) {
        Location.checkTabLocation();
    }

}
