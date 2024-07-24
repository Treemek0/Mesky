package treemek.mesky.utils;

import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import treemek.mesky.handlers.gui.elements.ScrollBar;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;

public class Locations {

	public static Location currentLocation = Location.NONE;
	public static String currentLocationText = " ";
	
	public enum Location {
		OUTSIDEHYPIXEL("Outside Hypixel"),
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
	    
	
	    public static Location getEnumLocation(String text) {
	        for (Location location : Location.values()) {
	            if (location.text.equalsIgnoreCase(text)) { 
	            	currentLocationText = text;
	            	return location;
	            }
	        }
	        return NONE;
	    }
	
	    public static Location checkTabLocation() {
			if (HypixelCheck.isOnHypixel()) {
				Collection<NetworkPlayerInfo> players = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
				for (NetworkPlayerInfo player : players) {
					if (player == null || player.getDisplayName() == null) continue;
					String text = player.getDisplayName().getUnformattedText();
					if (text.startsWith("Area: ") || text.startsWith("Dungeon: ")) {
						currentLocation = Location.getEnumLocation(text.substring(text.indexOf(":") + 2));
						return currentLocation;
					}
				}
				
				currentLocationText = "";
				currentLocation = Location.NONE;
				return Location.NONE;
			}
			
			currentLocationText = "";
			currentLocation = Location.OUTSIDEHYPIXEL;
			return Location.OUTSIDEHYPIXEL;
		}
	    
	}
	
	public static Location getLocation() {
		return Location.checkTabLocation();
	}
	
	
	 public static String getRegion() {
		Minecraft mc = Minecraft.getMinecraft();
		List<String> lines = Utils.getScoreboardLines(false);
		 for (String line : lines)
		 {
			if(line.contains(String.valueOf('\u23E3'))) {
				String region = line.substring(line.indexOf('\u23E3') + 1);
				region = Utils.removeEmojisFromString(region).trim();
				return region;
			}
		 }
		    
		 return "";
	  }

	 @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
    	if (event.world.isRemote && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (player == Minecraft.getMinecraft().thePlayer) {
            	Location.checkTabLocation();
            }
        }
    }

}
