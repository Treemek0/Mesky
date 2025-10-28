package treemek.mesky;

import java.io.StringWriter;
import javax.imageio.spi.ImageReaderSpi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import scala.Char;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.FishingTimer;
import treemek.mesky.features.SeaCreaturesDetection;
import treemek.mesky.features.ShardsProfit;
import treemek.mesky.features.ShardsProfit.Shard;
import treemek.mesky.features.ShardsProfit.ShardsWrapper;
import treemek.mesky.features.illegal.AutoFish;
import treemek.mesky.features.illegal.EntityDetector;
import treemek.mesky.features.illegal.JawbusDetector;
import treemek.mesky.handlers.ApiHandler;
import treemek.mesky.handlers.ApiHandler.ApiType;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.handlers.RecipeHandler;
import treemek.mesky.handlers.api.Auction;
import treemek.mesky.handlers.api.Bazaar;
import treemek.mesky.handlers.api.Bazaar.BuyType;
import treemek.mesky.handlers.api.Bazaar.SellType;
import treemek.mesky.handlers.gui.GUI;
import treemek.mesky.handlers.gui.GuiLocations;
import treemek.mesky.handlers.gui.TestGui;
import treemek.mesky.handlers.gui.keyaction.KeyActionGui;
import treemek.mesky.handlers.gui.sounds.SoundGui;
import treemek.mesky.handlers.gui.warp.fasttravel.WarpGui;
import treemek.mesky.handlers.gui.warp.rift.RiftWarpGui;
import treemek.mesky.handlers.gui.warp.rift.RiftWarpGui.RiftStack;
import treemek.mesky.handlers.soundHandler.SoundsHandler;
import treemek.mesky.listeners.GuiOpenListener.FastTravel;
import treemek.mesky.listeners.GuiOpenListener.PadLock;
import treemek.mesky.listeners.GuiOpenListener.RiftTravel;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.FriendsLocations;
import treemek.mesky.utils.ImageCache;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.MacroWaypoints;
import treemek.mesky.utils.MacroWaypoints.MacroWaypoint;
import treemek.mesky.utils.MiningUtils;
import treemek.mesky.utils.MiningUtils.MiningPath;
import treemek.mesky.utils.MiningUtils.MiningType;
import treemek.mesky.utils.MovementUtils;
import treemek.mesky.utils.MovementUtils.Movement;
import treemek.mesky.utils.PathfinderUtils;
import treemek.mesky.utils.RotationUtils;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.Waypoint;
import treemek.mesky.utils.Waypoints.WaypointGroup;
import treemek.mesky.utils.chat.CoordsDetector;
import treemek.mesky.utils.manager.CommandLoop;
import treemek.mesky.utils.manager.CommandLoop.Loop;
import treemek.mesky.utils.manager.PartyManager;

public class Commands extends CommandBase{
	
	 List<String> commands = new ArrayList<>(Arrays.asList(
	            "commands", "gui", "reload", "waypoint", "macrowaypoint", "tempwaypoint",
	            "touchwaypoint", "mark", "friend", "setfriend", "hideplayers", "find", 
	            "findclear", "whatentity", "region", "path", "pathxray", "pathtowaypoint",
	            "pathtomacrowaypoint", "clearpath", "flyingpath", "flyingpathtowaypoint",
	            "moveto", "movetowaypoint", "stopmoving", "lookat", "id", "sharks", 
	            "copy", "sounds", "colors", "miningmacro", " mining", "miningspeed",
	            "setwebhook", "aotv", "rotate", "delay", "and", "loop", "importskytilsfromclipboard", "craftprofit", "craftitemprofit"
	        ));
	
	 private Thread craftProfitThread;
	 
	@Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0) {
		// instead of message there should be gui with settings
			GuiHandler.GuiType = new GUI();
		}
		
		// Command arguments ./mesky [args]
    	if(args.length > 0){
    		String command = args[0].toLowerCase();
    		
    		if(command.equals("commands") || command.equals("help")) {
    			Utils.addMinecraftMessage("");
    			Utils.addMinecraftMessage("If you want to know how to use command just type '/mesky <command>' and it will tell you ( <> - are must, {} - are optional");
    			Utils.addMinecraftMessageWithPrefix("List of command prefixes: '/mesky " + EnumChatFormatting.GOLD + "<prefix>" + EnumChatFormatting.WHITE + "'");
    			
    			String commandsList = " ";
    			for (String string : commands) {
					commandsList += ColorUtils.getRandomMinecraftColor() + string + EnumChatFormatting.WHITE + ", ";
				}
    			
    			Utils.addMinecraftMessage( commandsList);
    			Utils.addMinecraftMessage("");
    			return;
    		}
    		if(command.equals("playsound")) {
    			if(args.length > 1) {
    				SoundsHandler.playSound(args[1]);
    			}
    			return;
    		}
    		if(command.equals("craftprofit")) {
    			if(args.length > 3) {
    				try {
    					Float tax = Math.max(1, Math.min(1.25f, Float.parseFloat(args[1])));

	    				BuyType buy = null;
	    				SellType sell = null;
	    				
	    				if(args[2].equalsIgnoreCase("order")) {
	    					buy = BuyType.ORDER_BUY;
	    				}else if(args[2].equalsIgnoreCase("insta")) {
	    					buy = BuyType.INSTA_BUY;
	    				}else {
	    					Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky craftprofit <bazaar tax> <buy \"insta\"/\"order\"> <sell \"insta\"/\"order\"> {minProfit}");
	    				}
	    				
	    				if(args[3].equalsIgnoreCase("order")) {
	    					sell = SellType.ORDER_SELL;
	    				}else if(args[3].equalsIgnoreCase("insta")) {
	    					sell = SellType.INSTA_SELL;
	    				}else {
	    					Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky craftprofit <bazaar tax> <buy \"insta\"/\"order\"> <sell \"insta\"/\"order\"> {minProfit}");
	    				}
	    				
	    				final BuyType buyType = buy;
	    				final SellType sellType = sell;
	    				
	    				Long minProfit = 0L;
	    				if(args.length > 4) minProfit = Long.parseLong(args[4]);
	    				
	    				final Long minProfitFinal = minProfit;
	    				
	    				if(craftProfitThread != null && craftProfitThread.isAlive()) {
	    					Utils.addMinecraftMessageWithPrefix("Stopping searching for new craft profits");
	    					craftProfitThread.interrupt();
	    				}
	    				
	    				Utils.addMinecraftMessageWithPrefix("Looking for best crafting offers with: Tax: " + tax + ", Buying: " + buy.code + ", Selling: " + sell.code + ", Min Profit: " + Utils.formatNumber(minProfitFinal.floatValue()));
	    				
	    				craftProfitThread = new Thread(() -> {
	    					Bazaar.getHighestProfitCrafts(tax, buyType, sellType, minProfitFinal);
	    				});
	    				
	    				craftProfitThread.start();
					} catch (Exception e) {
						Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky craftprofit <bazaar tax> <buy \"insta\"/\"order\"> <sell \"insta\"/\"order\"> {minProfit}");
						Utils.writeError(e);
					}
    			}else {
    				Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky craftprofit <bazaar tax> <buy \"insta\"/\"order\"> <sell \"insta\"/\"order\"> {minProfit}");
    			}
    			
    			return;
    		}
    		if(command.equals("warp")) {
    			Map<FastTravel, PadLock> map = new HashMap<>();
    			
    			for (FastTravel string : FastTravel.values()) {
					map.put(string, PadLock.UNLOCKED);
				}
    			
    			GuiHandler.GuiType = new WarpGui(map);
    		}
    		if(command.equals("itemcraftprofit")) {
    			if(args.length > 4) {
    				try {
    					String id = args[1].toUpperCase().replace(" ", "_");
    					Float tax = Math.max(1, Math.min(1.25f, Float.parseFloat(args[2])));
    					
	    				BuyType buy = null;
	    				SellType sell = null;
	    				
	    				if(args[3].equalsIgnoreCase("order")) {
	    					buy = BuyType.ORDER_BUY;
	    				}else if(args[3].equalsIgnoreCase("insta")) {
	    					buy = BuyType.INSTA_BUY;
	    				}else {
	    					Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky craftprofit <item ID> <bazaar tax> <buy \"insta\"/\"order\"> <sell \"insta\"/\"order\">");
	    				}
	    				
	    				if(args[4].equalsIgnoreCase("order")) {
	    					sell = SellType.ORDER_SELL;
	    				}else if(args[4].equalsIgnoreCase("insta")) {
	    					sell = SellType.INSTA_SELL;
	    				}else {
	    					Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky itemcraftprofit <item ID> <bazaar tax> <buy \"insta\"/\"order\"> <sell \"insta\"/\"order\">");
	    				}
	    				
	    				final BuyType buyType = buy;
	    				final SellType sellType = sell;

	    				if(craftProfitThread != null && craftProfitThread.isAlive()) {
	    					Utils.addMinecraftMessageWithPrefix("Stopping searching for new craft profits");
	    					craftProfitThread.interrupt();
	    				}
	    				
	    				Utils.addMinecraftMessageWithPrefix("Looking for crafting profit for " + id + " with: Tax: " + tax + ", Buying: " + buy.code + ", Selling: " + sell.code);
	    				
	    				craftProfitThread = new Thread(() -> {
	    					Bazaar.clearCraftCostMap();
	    					JsonObject bazaar = Bazaar.getBaazar();
	    					if (bazaar == null || !bazaar.has("products")) {
	    						Utils.addMinecraftMessageWithPrefix("There was a problem with reading bazaar data. Try again later");
	    					}else {
	    						float[] craft = Bazaar.getCraftProfit(id, bazaar, tax, buyType, sellType, true);
	    						
	    						if(craft[0] == 0 && craft[1] == 0 && craft[2] == 0 && craft[3] == 0 && craft[4] == 0) {
	    							Utils.addMinecraftMessage(EnumChatFormatting.RED + "There's was a problem with finding craft cost of " + id);
	    						}else {
		    						String message = EnumChatFormatting.AQUA + id + EnumChatFormatting.GOLD + " | Craft cost: " + Utils.formatNumber(craft[2])
		    			   			 + EnumChatFormatting.YELLOW + " | Sell price: " + Utils.formatNumber(craft[1])
		    			   			 + EnumChatFormatting.BLUE + " | Profit: " + Utils.formatNumber(craft[0]);
		    			
		    						ChatComponentText chatText = new ChatComponentText(message);
		    				        ChatStyle style = new ChatStyle();
		    				        style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bz " + id));
		    				        chatText.setChatStyle(style);
		    				
		    				        Utils.addMinecraftMessage(chatText);
	    						}
	    					}
	    				});
	    				
	    				craftProfitThread.start();
					} catch (Exception e) {
						Utils.addMinecraftMessageWithPrefix("Bazaar tax you provided isn't a number. Correct way of using this command /mesky craftprofit <item ID> <bazaar tax> <buy \"insta\"/\"order\"> <sell \"insta\"/\"order\">");
					}
    			}else {
    				Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky craftprofit <item ID> <bazaar tax> <buy \"insta\"/\"order\"> <sell \"insta\"/\"order\">");
    			}
    			
    			return;
    		}
    		if(command.equals("map")) {
    			Utils.saveNearbyMaps(Minecraft.getMinecraft().thePlayer);
    			return;
    		}
    		if(command.equals("keyactions")) {
    			GuiHandler.GuiType = new KeyActionGui();
    		}
    		if(command.equals("loop")) {
    			if(args.length > 2) {
    				try {
    					long milis = parseLong(args[1]);
    					
    					if(milis < 500) {
    						Utils.addMinecraftMessageWithPrefix("Time should be 500ms and above. " + milis + " is lower than 500");
    						return;
    					}
    					
    					String loopedCommand = connectArgs(args, 2, args.length);
    					
    					if(loopedCommand.contains("mesky loop")) {
    						Utils.addMinecraftMessageWithPrefix("You can't loop a loop");
    						return;
    					}
    					
    					Utils.addMinecraftMessageWithPrefix("Setting up loop (" + milis + "ms) for command: " + EnumChatFormatting.GOLD + loopedCommand);
    					Utils.addMinecraftMessageWithPrefix("To stop it use /mesky resetloop");
    					
    					CommandLoop.setCurrentLoop(new Loop(milis, loopedCommand));
					} catch (Exception e) {
						Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky loop <time [ms]> <command>");
					}
    			}else {
    				Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky loop <time [ms]> <command>");
    			}
    			
    			return;
    		}
    		if(command.equals("resetloop")) {
    			CommandLoop.setCurrentLoop(null);
    			Utils.addMinecraftMessageWithPrefix("Command loop is set to null");
    		}
    		if(command.equals("isplayervisible")) {
    			Utils.addMinecraftMessageWithPrefix("Visible player: " + Utils.isAnyPlayerVisible());
    			return;
    		}
    		if(command.equals("delay")) {
    			if(args.length > 2) {
    				try {
    					long milis = parseLong(args[1]);
    					String delayedCommand = connectArgs(args, 2, args.length);
    					
    					Utils.addMinecraftMessageWithPrefix("Setting up delay for command: " + EnumChatFormatting.GOLD + delayedCommand);
    					
    					MovementUtils.addCommand(new Movement(() -> {
    						Utils.executeCommand(connectArgs(args, 2, args.length));
    					}, milis));
					} catch (Exception e) {
						Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky delay <time [ms]> <command>");
					}
    			}else {
    				Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky delay <time [ms]> <command>");
    			}
    			
    			return;
    		}
    		if(command.equals("and")) { // /mesky and /mesky mining & /mesky delay 5000 /mesky and /mesky stopmining & /mesky aotv
    			if(args.length > 3) {
    				int index = -1;
    				for (int i = 0; i < args.length; i++) {
    				    if (args[i].equals("&")) {
    				        index = i;
    				        break;
    				    }
    				}

    				if (index > 1 && index < args.length) {
    				    Utils.executeCommand(connectArgs(args, 1, index));
    				    Utils.executeCommand(connectArgs(args, index+1, args.length));
    				}else {
    					Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky and <command> & <command>");
    				}
    			}else {
    				Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky and <command> & <command>");
    			}
    			
    			return;
    		}
    		if(command.equals("test")){

    		}
    		if(command.equals("rotate")) {
    			if(args.length > 2) {
    				try {
						Float pitch = Float.parseFloat(args[1]);
						Float yaw = Float.parseFloat(args[2]);
						Float seconds = 1f;
						if(args.length > 3) seconds = Float.parseFloat(args[2]);
						RotationUtils.rotateStraight(pitch, yaw, seconds, false);
					} catch (Exception e) {
						Utils.writeError(e);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky rotate <pitch> <yaw> «seconds»"));
					}
    				
    			}
    			return;
    		}
    		if(command.equals("colors")) {
    		    Utils.addMinecraftMessageWithPrefix("To show formatting while typing on textField press Tab.");
    		    Utils.addMinecraftMessage("Text formatting:");
    		    
    		    // Color options
    		    Utils.addMinecraftMessage(EnumChatFormatting.BLACK + "<&0> Black");
    		    Utils.addMinecraftMessage(EnumChatFormatting.DARK_BLUE + "<&1> Dark Blue");
    		    Utils.addMinecraftMessage(EnumChatFormatting.DARK_GREEN + "<&2> Dark Green");
    		    Utils.addMinecraftMessage(EnumChatFormatting.DARK_AQUA + "<&3> Dark Aqua");
    		    Utils.addMinecraftMessage(EnumChatFormatting.DARK_RED + "<&4> Dark Red");
    		    Utils.addMinecraftMessage(EnumChatFormatting.DARK_PURPLE + "<&5> Dark Purple");
    		    Utils.addMinecraftMessage(EnumChatFormatting.GOLD + "<&6> Gold");
    		    Utils.addMinecraftMessage(EnumChatFormatting.GRAY + "<&7> Gray");
    		    Utils.addMinecraftMessage(EnumChatFormatting.DARK_GRAY + "<&8> Dark Gray");
    		    Utils.addMinecraftMessage(EnumChatFormatting.BLUE + "<&9> Blue");
    		    Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "<&a> Green");
    		    Utils.addMinecraftMessage(EnumChatFormatting.AQUA + "<&b> Aqua");
    		    Utils.addMinecraftMessage(EnumChatFormatting.RED + "<&c> Red");
    		    Utils.addMinecraftMessage(EnumChatFormatting.LIGHT_PURPLE + "<&d> Light Purple");
    		    Utils.addMinecraftMessage(EnumChatFormatting.YELLOW + "<&e> Yellow");
    		    Utils.addMinecraftMessage(EnumChatFormatting.WHITE + "<&f> White");

    		    // Additional formatting options
    		    Utils.addMinecraftMessage("<&k> " + EnumChatFormatting.OBFUSCATED + "Obfuscated");
    		    Utils.addMinecraftMessage("<&m> " + EnumChatFormatting.STRIKETHROUGH + "Strikethrough");
    		    Utils.addMinecraftMessage("<&n> " + EnumChatFormatting.UNDERLINE + "Underline");
    		    Utils.addMinecraftMessage(EnumChatFormatting.RESET + "<&r> Reset to Default");
    		    
    		    return;
    		}
    		if(command.equals("stopmining")) {
    			MiningUtils.stopMining();
    			return;
    		}
    		if(command.equals("mining")) {
    			if(args.length > 1) {
    				String miningType = args[1];
    				try {
    				    MiningType type = MiningType.valueOf(miningType.toUpperCase());
    				    MovementUtils.addMovement(new Movement(() -> MiningUtils.startMining(type), 1000));
    				} catch (IllegalArgumentException e) {
    				    Utils.addMinecraftMessageWithPrefix("Invalid mining type: " + miningType);
    				}
    			}else {
    				Utils.addMinecraftMessageWithPrefix("You have to specify mining type: ");
    				Utils.addMinecraftMessage(EnumChatFormatting.AQUA + "MITHRIL, GEMSTONE, GOLD, DIAMOND, IRON, COAL, REDSTONE, LAPIS");
    			}
    			
    			return;
    		}
    		if(command.equals("miningspeed")) {
    			if(args.length > 1) {
    				try {
						int speed = Integer.parseInt(args[1]);
						SettingsConfig.MiningSpeed.number = (double) speed;
						ConfigHandler.saveSettings();
					} catch (Exception e) {
						Utils.writeError(e);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky miningspeed <speed>"));
					}	
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky miningspeed <speed>"));
    			}
    			return;

    		}
    		if(command.equals("downloadtocache")) {
    			if(Mesky.debug) {
    				if(args.length > 1) {
    					String path = args[1];
    					ImageCache.downloadImageFromFile(path, "downloadtocache");
    				}
    			}
    		}
    		if(command.equals("sounds")) {
    			GuiHandler.GuiType = new SoundGui();
    			return;
    		}
    		if(command.equals("gui")) {
    			GuiHandler.GuiType = new GuiLocations();
    			return;
    		}
    		if(command.equals("testgui")) {
    			if(Mesky.debug) {
    				GuiHandler.GuiType = new TestGui();
    			}
    		}
    		if(command.equals("isinparty")) {
    			Utils.addMinecraftMessage(PartyManager.isInParty() + "");
    			return;
    		}
    		if(command.equals("copy")) {
    			String text = connectArgs(args, 1, args.length);
    			Utils.addMinecraftMessageWithPrefix("Copied: " + text);
    			Utils.copyToClipboard(text);
    	        return;
    		}
    		if(command.equals("copy0")) { // copy without debug info
    			String text = connectArgs(args, 1, args.length);
    			Utils.copyToClipboard(text);
    	        return;
    		}
    		if(command.equals("shards")) {
    			//ShardsProfit.getShardsProfit();
				
				return;
    		}
    		if(command.equals("auction")) {
    			if(args.length > 1) {
    				try {
    					float minProfit = Float.parseFloat(args[1]);
    					
    					Auction.findProfitAuctions(minProfit);
					} catch (Exception e) {
						Utils.addMinecraftMessageWithPrefix(args[1] + " isn't a number. Correct way of using this command: /mesky auction <min profit>");
					}
    			}else {
    				Utils.addMinecraftMessageWithPrefix("Correct way of using this command: /mesky auction <min profit>");
    			}
				
				return;
    		}
    		if(command.equals("id")) {
    			Utils.addMinecraftMessage(Utils.getSkyblockId(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem()));
    			return;
    		}
    		if(command.equals("isnpc")) {
    			Utils.addMinecraftMessage("isNPC: " + Utils.isNPC(Utils.getEntityLookedAt(50)));
    			return;
    		}
    		if(command.equals("debug")) {
    			Mesky.debug = !Mesky.debug;
    			Utils.addMinecraftMessageWithPrefix("Debug is now set to: " + Mesky.debug);
    			return;
    		}
    		if (command.equals("importskytilsfromclipboard")) {
    		    String hash = GuiScreen.getClipboardString();
    		    try {
    		        LinkedHashMap<String, WaypointGroup> waypoints = Waypoints.importSkytilsWaypoints(hash);

    		        if(waypoints != null && !waypoints.isEmpty()) {
    		        	Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.GREEN + "" + '\u2713' + EnumChatFormatting.DARK_GREEN + " Imported Skytils waypoints:");
    		        	
    		        	for (Map.Entry<String, WaypointGroup> entry : waypoints.entrySet()) {
    		                String key = entry.getKey();
    		                WaypointGroup group = entry.getValue();
	    		   
	        		        Utils.addMinecraftMessage('\u25B6' + " " + EnumChatFormatting.DARK_GREEN + "Waypoint group: " + EnumChatFormatting.DARK_AQUA + key);
	    		            
	    		            Waypoints.waypointsList.compute(key, (k, existingGroup) -> {
	    		                if (existingGroup == null) return group;
	    		                for (Waypoint waypoint : existingGroup.list) {
	    		                	Utils.addMinecraftMessage('\u21AA' + " " + EnumChatFormatting.DARK_AQUA + waypoint.name + EnumChatFormatting.GOLD + " [" + waypoint.coords[0] + ", " + waypoint.coords[1] + ", " + waypoint.coords[2] + "]");
								}
	    		                
	    		                existingGroup.list.addAll(group.list);
	    		                return existingGroup;
	    		            });
	    		        }
    		        	
    		        	ConfigHandler.SaveWaypoint(Waypoints.waypointsList);
    		        }else {
    		        	Utils.writeError("Something went wrong with importing your waypoints. Check if the string is correct.");
    		        }
    		    } catch (IOException e) {
    		        Utils.writeError("Failed to import Skytils waypoints: " + e.getMessage());
    		    }
    		    
    		    return;
    		}
    		if(command.equals("reload")) {
    			if(args.length > 1) {
    				String type = args[1].toLowerCase();
    				switch (type) {
	    				case "settings": {
	    				    try {
	    				        ConfigHandler.reloadSettings();
	    				        Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded settings " + EnumChatFormatting.GREEN + '\u2713');
	    				    } catch (FileNotFoundException e) {
	    				        Utils.writeError("There was a problem with reloading settings: " + e.getMessage());
	    				    }
	    				    break;
	    				}
	    				case "sounds": {
	    				    SoundsHandler.reloadSounds();
	    				    Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded sounds " + EnumChatFormatting.GREEN + '\u2713');
	    				    break;
	    				}
	    				case "alerts": {
	    				    try {
	    				        ConfigHandler.reloadAlerts();
	    				        Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded alerts " + EnumChatFormatting.GREEN + '\u2713');
	    				    } catch (IOException e) {
	    				        Utils.writeError("There was a problem with reloading alerts: " + e.getMessage());
	    				    }
	    				    break;
	    				}
	    				case "chatfunctions": {
	    				    try {
	    				        ConfigHandler.reloadChatFunctions();
	    				        Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded chatFunctions " + EnumChatFormatting.GREEN + '\u2713');
	    				    } catch (IOException e) {
	    				        Utils.writeError("There was a problem with reloading chatFunctions: " + e.getMessage());
	    				    }
	    				    break;
	    				}
	    				case "waypoints": {
	    				    try {
	    				        ConfigHandler.reloadWaypoints();
	    				        Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded waypoints " + EnumChatFormatting.GREEN + '\u2713');
	    				    } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
	    				        Utils.writeError("There was a problem with reloading waypoints: " + e.getMessage());
	    				    }
	    				    break;
	    				}
	    				case "macrowaypoints": {
	    				    try {
	    				        ConfigHandler.reloadMacroWaypoints();
	    				        Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded macroWaypoints " + EnumChatFormatting.GREEN + '\u2713');
	    				    } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
	    				        Utils.writeError("There was a problem with reloading macroWaypoints: " + e.getMessage());
	    				    }
	    				    break;
	    				}
	    				case "miningPaths": {
	    				    try {
	    				        ConfigHandler.reloadMiningPaths();
	    				        Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.DARK_GREEN + "Reloaded miningPaths " + EnumChatFormatting.GREEN + '\u2713');
	    				    } catch (IOException e) {
	    				        Utils.writeError("There was a problem with reloading miningPaths: " + e.getMessage());
	    				    }
	    				    break;
	    				}
	    				default: {
	    				    Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.YELLOW + "There's nothing to reload named: " + type);
	    				}

					}
    			}else {
	    			ConfigHandler.reloadConfig();
	    			Utils.addMinecraftMessageWithPrefix("Finished reloading.");
    			}
    			
    			return;
    		}
    		if(command.equals("locate")) {
    			if(args.length > 1) {
    				String name = args[1];
    				if(Locations.currentLocation == Location.CRIMSON_ISLE && SettingsConfig.JawbusPlayerDeathDetection.isOn) {
    					BlockPos playerLocation = JawbusDetector.getPlayerLastSeenLocation(name);
    					
    	                ChatStyle temp = new ChatStyle();
    	                temp.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mesky tempwaypoint " + name + " E66758 " + playerLocation.getX() + " " + playerLocation.getY() + " " + playerLocation.getZ() + " " + SettingsConfig.MarkWaypointTime.number.intValue()));
    	                temp.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Create a temporary waypoint (3min)")));
    	                temp.setColor(EnumChatFormatting.RED); // Set the color of the button text
    	                
    	                ChatComponentText clickableMessage = new ChatComponentText(" [Quick mark]");
    	                clickableMessage.setChatStyle(temp); 
    	                
    	                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + name + EnumChatFormatting.AQUA + " last rendered position: " + EnumChatFormatting.GOLD + playerLocation.getX() + " " + playerLocation.getY() + " " + playerLocation.getZ()).appendSibling(clickableMessage));
    				}else {
    					Utils.addMinecraftMessageWithPrefix("This command only works when on Crimson Isle and while JawbusPlayerDeathDetection is on");
    				}
    			}else {
    				Utils.addMinecraftMessageWithPrefix("Correct way of using this command: /mesky locate <player>");
    			}
    			return;
    		}
    		if(command.equals("sharks")) {
    			if(SettingsConfig.SharkCounter.isOn) {
	    			int allCounter = SeaCreaturesDetection.gwsCounter + SeaCreaturesDetection.tigerCounter + SeaCreaturesDetection.blueCounter + SeaCreaturesDetection.nurseCounter;
					Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.BOLD.AQUA + "This Fishing festival you fished-up: " + EnumChatFormatting.DARK_AQUA + allCounter + " sharks");
					if(SeaCreaturesDetection.gwsCounter > 0) Utils.addMinecraftMessage(EnumChatFormatting.GOLD + "@# Great White Sharks: " + SeaCreaturesDetection.gwsCounter);
					if(SeaCreaturesDetection.tigerCounter > 0) Utils.addMinecraftMessage(EnumChatFormatting.DARK_PURPLE + "@# Tiger Sharks: " + SeaCreaturesDetection.tigerCounter);
					if(SeaCreaturesDetection.blueCounter > 0) Utils.addMinecraftMessage(EnumChatFormatting.BLUE + "@# Blue Sharks: " + SeaCreaturesDetection.blueCounter);
					if(SeaCreaturesDetection.nurseCounter > 0) Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "@# Nurse Sharks: " + SeaCreaturesDetection.nurseCounter);
    			}else {
    				Utils.addMinecraftMessageWithPrefix("Shark counter disabled, turn it on to count your sharks");
    			}
				return;
    		}
			if(command.equals("waypoint")) {
    			if(args.length >= 7) {
    				String wholeCommand = buildString(args, 1);
    				int startOfName = wholeCommand.indexOf("\"");
    				if(startOfName == -1) {
    					Utils.addMinecraftMessageWithPrefix("You forgot to use \" \" in name");
    					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky waypoint \"<name>\" <hex_color> <x> <y> <z> <scale>"));
    					return;
    				}
    				String name = wholeCommand.substring(startOfName + 1, wholeCommand.lastIndexOf("\""));
					try {
						String[] argsAfterName = wholeCommand.substring(wholeCommand.lastIndexOf("\"")+1).trim().split(" ");
						
						String color = argsAfterName[0].replace("#", "");
						Float x = Float.parseFloat(argsAfterName[1]);
						Float y = Float.parseFloat(argsAfterName[2]);
						Float z = Float.parseFloat(argsAfterName[3]);
						Float scale = Float.parseFloat(argsAfterName[4]);
						Waypoints.addWaypoint(name, color, x, y, z, scale);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added waypoint: " + EnumChatFormatting.DARK_PURPLE + ColorUtils.getColoredText(name) + " " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
					} catch (Exception e) {
						Utils.writeError(e);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky waypoint \"<name>\" <hex_color> <x> <y> <z> <scale>"));
					}
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky waypoint \"<name>\" <hex_color> <x> <y> <z> <scale>"));
    			}
    			
    			return;
    		}
			if(command.equals("macrowaypoint")) {
    			if(args.length >= 11) {
    				EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    				
    				String wholeCommand = buildString(args, 1);
    				int startOfName = wholeCommand.indexOf("\"");
    				if(startOfName == -1) {
    					Utils.addMinecraftMessageWithPrefix("You forgot to use \" \" in name");
    					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky waypoint \"<name>\" <hex_color> <noiseLevel> <leftClick true/false> <rightClick true/false> <leftMove true/false> <rightMove true/false> <backMove true/false> <forwardMove true/false> <sneak true/false>"));
    					return;
    				}
    				String name = wholeCommand.substring(startOfName + 1, wholeCommand.lastIndexOf("\""));
					try {
						String[] argsAfterName = wholeCommand.substring(wholeCommand.lastIndexOf("\"")+1).trim().split(" ");
						
						String color = argsAfterName[0].replace("#", "");
						Float x = (float) player.posX;
						Float y = (float) player.posY + 0.5f;
						Float z = (float) player.posZ;
						Float yaw = player.rotationYaw;
						Float pitch = player.rotationPitch;
						Float noiseLevel = Float.parseFloat(argsAfterName[1]);
						Boolean leftClick = Boolean.parseBoolean(argsAfterName[2]);
						Boolean rightClick = Boolean.parseBoolean(argsAfterName[3]);
						Boolean left = Boolean.parseBoolean(argsAfterName[4]);
						Boolean right = Boolean.parseBoolean(argsAfterName[5]);
						Boolean back = Boolean.parseBoolean(argsAfterName[6]);
						Boolean forward = Boolean.parseBoolean(argsAfterName[7]);
						Boolean sneak = Boolean.parseBoolean(argsAfterName[8]);
						MacroWaypoints.addMacroWaypoint(name, color, x, y, z, yaw, pitch, left, right, back, forward, leftClick, rightClick, sneak, noiseLevel);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added macro waypoint: " + EnumChatFormatting.DARK_PURPLE + ColorUtils.getColoredText(name) + " " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
					} catch (Exception e) {
						Utils.writeError(e);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky waypoint \"<name>\" <hex_color> <noiseLevel> <leftClick true/false> <rightClick true/false> <leftMove true/false> <rightMove true/false> <backMove true/false> <forwardMove true/false> <sneak true/false>"));
					}
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky waypoint \"<name>\" <hex_color> <noiseLevel> <leftClick true/false> <rightClick true/false> <leftMove true/false> <rightMove true/false> <backMove true/false> <forwardMove true/false> <sneak true/false>"));
    			}
    			
    			return;
    		}
			if(command.equals("tempwaypoint")) {
    			if(args.length >= 7) {
    				String wholeCommand = buildString(args, 1);
    				int startOfName = wholeCommand.indexOf("\"");
    				if(startOfName == -1) {
    					Utils.addMinecraftMessageWithPrefix("You forgot to use \" \" in name");
        				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky tempwaypoint \"<name>\" <hex_color> <x> <y> <z> <time [s]>"));
    					return;
    				}
    				String name = wholeCommand.substring(startOfName + 1, wholeCommand.lastIndexOf("\""));
					try {
						String[] argsAfterName = wholeCommand.substring(wholeCommand.lastIndexOf("\"")+1).trim().split(" ");
						
						String color = argsAfterName[0].replace("#", "");
						Float x = Float.parseFloat(argsAfterName[1]);
						Float y = Float.parseFloat(argsAfterName[2]);
						Float z = Float.parseFloat(argsAfterName[3]);
						Long time = Long.parseLong(argsAfterName[4]) * 1000;
						Waypoints.addTemporaryWaypoint(name, color, x, y, z, 2, time);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added temporary waypoint (" + time/1000 + "s): " + EnumChatFormatting.DARK_PURPLE + ColorUtils.getColoredText(name) + " " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
					} catch (Exception e) {
						Utils.writeError(e);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky tempwaypoint \"<name>\" <hex_color> <x> <y> <z> <time [s]>"));
					}
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky tempwaypoint \"<name>\" <hex_color> <x> <y> <z> <time [s]>"));
    			}
    			
    			return;
    		}
			if(command.equals("touchwaypoint")) {
    			if(args.length >= 7) {
    				String wholeCommand = buildString(args, 1);
    				int startOfName = wholeCommand.indexOf("\"");
    				if(startOfName == -1) {
    					Utils.addMinecraftMessageWithPrefix("You forgot to use \" \" in name");
    					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky touchwaypoint \"<name>\" <hex_color> <x> <y> <z> <touch radius>"));
    					return;
    				}
    				String name = wholeCommand.substring(startOfName + 1, wholeCommand.lastIndexOf("\""));
					try {
						String[] argsAfterName = wholeCommand.substring(wholeCommand.lastIndexOf("\"")+1).trim().split(" ");
						
						String color = argsAfterName[0].replace("#", "");
						Float x = Float.parseFloat(argsAfterName[1]);
						Float y = Float.parseFloat(argsAfterName[2]);
						Float z = Float.parseFloat(argsAfterName[3]);
						Float radius = Float.parseFloat(argsAfterName[4]);
						Long lifeTime = null;
						if(argsAfterName.length > 5) {
							lifeTime = Long.parseLong(argsAfterName[5]) * 1000L;
						}
						Waypoints.addTouchWaypoint(name, color, x, y, z, 2, radius, lifeTime);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added touch waypoint (" + radius + "m): " + EnumChatFormatting.DARK_PURPLE + ColorUtils.getColoredText(name) + " " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
					} catch (Exception e) {
						Utils.writeError(e);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky touchwaypoint \"<name>\" <hex_color> <x> <y> <z> <touch radius>"));
					}
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky touchwaypoint \"<name>\" <hex_color> <x> <y> <z> <touch radius>"));
    			}
    			
    			return;
    		}
			if(command.equals("mark")) {
    			if(args.length > 4) {
    				String wholeCommand = buildString(args, 1);
    				int startOfName = wholeCommand.indexOf("\"");
    				if(startOfName == -1) {
    					Utils.addMinecraftMessageWithPrefix("You forgot to use \" \" in name");
    					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky mark \"<name>\" <x> <y> <z> {hex_color}"));
    					return;
    				}
    				String name = wholeCommand.substring(startOfName + 1, wholeCommand.lastIndexOf("\""));
					try {
						String[] argsAfterName = wholeCommand.substring(wholeCommand.lastIndexOf("\"")+1).trim().split(" ");
						
						Float x = Float.parseFloat(argsAfterName[0]);
						Float y = Float.parseFloat(argsAfterName[1]);
						Float z = Float.parseFloat(argsAfterName[2]);
						String color = Utils.generateRandomHexString(6);
						if(argsAfterName.length > 3) color = argsAfterName[3].replace("#", "");
						Float radius = SettingsConfig.MarkWaypointRadius.number.floatValue();
						Long lifeTime = SettingsConfig.MarkWaypointTime.number.longValue();
						Waypoints.addTouchWaypoint(name, color, x, y, z, 2, radius, lifeTime * 1000L);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added mark (" + radius + "m, " + lifeTime + "s): " + ColorUtils.getClosestMinecraftColor(color) + ColorUtils.getColoredText(name) + " " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
					} catch (Exception e) {
						Utils.writeError(e);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky mark \"<name>\" <x> <y> <z> {hex_color}"));
					}
    			}else {
    				if(args.length > 3) {
    					try {
	    					Float x = Float.parseFloat(args[1]);
							Float y = Float.parseFloat(args[2]);
							Float z = Float.parseFloat(args[3]);
							String name = Utils.generateRandomString(6);
							String color = Utils.generateRandomHexString(6);
							Float radius = SettingsConfig.MarkWaypointRadius.number.floatValue();
							Long lifeTime = SettingsConfig.MarkWaypointTime.number.longValue();
							Waypoints.addTouchWaypoint(name, color, x, y, z, 2, radius, lifeTime * 1000L);
							Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added mark (" + radius + "m, " + lifeTime + "s): " +  ColorUtils.getClosestMinecraftColor(color) + ColorUtils.getColoredText(name) + " " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
    					} catch (Exception e) {
    						Utils.writeError(e);
    						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky mark <x> <y> <z>"));
    					}
    				}else {
    					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: "));
    					Utils.addMinecraftMessage("/mesky mark \"<name>\" <x> <y> <z> {hex_color}");
    					Utils.addMinecraftMessage("or");
    					Utils.addMinecraftMessage("/mesky mark <x> <y> <z>");
    				}
    			}
    			
    			return;
    		}
			if(command.equals("friend")) {
    			if(args.length >= 2) {
	    			String nick = args[1];
					try {
						FriendsLocations.getInfoForPlayer(nick);
					} catch (Exception e) {
						e.printStackTrace();
					}
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky friend <nick>"));
    			}
    			
    			return;
			}
			if(command.equals("setfriend")) {
				if(args.length >= 9) {
					String name = args[1];
					String location = args[5];
					int day = 0, month = 0, year = 0, x = 0, y = 0, z = 0;
					try {
						day = Integer.parseInt(args[2]);
					} catch (Exception e) {
						Utils.writeError(args[2] + " isn't a number (Day)");
					}
					try {
						month = Integer.parseInt(args[3]);
					} catch (Exception e) {
						Utils.writeError(args[3] + " isn't a number (Month)");
					}
					try {
						year = Integer.parseInt(args[4]);
					} catch (Exception e) {
						Utils.writeError(args[4] + " isn't a number (Year)");
					}
					try {
						x = Integer.parseInt(args[6]);
					} catch (Exception e) {
						Utils.writeError(args[6] + " isn't a number (x)");
					}
					try {
						y = Integer.parseInt(args[7]);
					} catch (Exception e) {
						Utils.writeError(args[7] + " isn't a number (y)");
					}
					try {
						z = Integer.parseInt(args[8]);
					} catch (Exception e) {
						Utils.writeError(args[8] + " isn't a number (z)");
					}
					
					FriendsLocations.setCustomInfoForPlayer(name, day, month, year, location, x, y, z);
					Utils.addMinecraftMessage(EnumChatFormatting.AQUA + "Added " + name + " to friends list:");
					Utils.addMinecraftMessage(EnumChatFormatting.GOLD + "Date: " + day + " " + month + " " + year);
					Utils.addMinecraftMessage(EnumChatFormatting.GOLD + "Location: " + location);
					Utils.addMinecraftMessage(EnumChatFormatting.GOLD + "Coords: " + x + " " + y + " " + z);
				}else {
					Utils.addMinecraftMessage("Wrong use of command (/mesky setfriend <name> <day> <month (number)> <year> <location> <x> <y> <z>");
				}
				
				return;
			}
			if(command.equals("hideplayers")) {
				SettingsConfig.HidePlayers.isOn = !SettingsConfig.HidePlayers.isOn;
				return;
			}
			if(command.equals("find")) {
				if(args.length >= 3) {
					try {
						boolean onlyArmorStands = Boolean.parseBoolean(args[1]);
				        
						EntityDetector.getAllEntityFromType(connectArgs(args, 2, args.length), onlyArmorStands);
					} catch (Exception e) {
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command /mesky find <onlyArmorStands? <true>/<false>> <name of entity>"));
					}
					
				}else {
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command /mesky find <<true> = onlyArmorstands /<false> = withoutArmorstands> <name of entity>"));
				}
				
				return;
			}
			if(command.equals("findclear")) {
				EntityDetector.clearEntityWaypoints();
				return;
			}
			if(command.equals("whatentity")) {
				Utils.addMinecraftMessage(EnumChatFormatting.AQUA + "Looked at entity: ");
				Utils.addMinecraftMessage(EnumChatFormatting.GOLD + EntityDetector.whatEntity());
				return;
			}
			if(command.equals("region")) {
				Utils.addMinecraftMessageWithPrefix("Current region: " + Locations.getRegion());
				return;
			}
			if(command.equals("path")) {
				if(args.length > 3) {
					try {
						Float x = Float.parseFloat(args[1]);
						Float y = Float.parseFloat(args[2]);
						Float z = Float.parseFloat(args[3]);
						BlockPos pos = new BlockPos(x,y,z);
						PathfinderUtils.drawPathTo(pos, true, true);
					} catch (Exception e) {
						Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky path <x> <y> <z>");
					}
				}else {
					Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky path <x> <y> <z>");
				}
				
				return;
			}
			if(command.equals("aotv")) {
				if(args.length > 3) {
					try {
						Float x = Float.parseFloat(args[1]);
						Float y = Float.parseFloat(args[2]);
						Float z = Float.parseFloat(args[3]);
						BlockPos pos = new BlockPos(x,y,z);
						MovementUtils.useAOTVto(pos);
					} catch (Exception e) {
						Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky aotv <x> <y> <z>");
					}
				}else {
					Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky aotv <x> <y> <z>");
				}
				
				return;
			}
			if(command.equals("lookat")) {
				BlockPos p = Utils.getBlockLookingAt(Math.min(192, Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16));
				ChatComponentText message = new ChatComponentText("LookAt block: " + p.getX() + " " + p.getY() + " " + p.getZ());
				
				ChatStyle copy = new ChatStyle();
		        copy.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mesky copy " + p.getX() + " " + p.getY() + " " + p.getZ()));
		        copy.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Copies coords to clipboard")));
		        copy.setColor(EnumChatFormatting.DARK_GREEN); // Set the color of the button text
		        
		        ChatComponentText clickableCopy = new ChatComponentText(" [COPY]");
		        clickableCopy.setChatStyle(copy); 
		        
		        message.appendSibling(clickableCopy);
		        Utils.addMinecraftMessageWithPrefix(message);
				return;
			}
			if(command.equals("moveto")) {
				if(args.length > 3) {
					try {
						Float x = Float.parseFloat(args[1]);
						Float y = Float.parseFloat(args[2]);
						Float z = Float.parseFloat(args[3]);
						BlockPos pos = new BlockPos(x,y,z);
						MovementUtils.movePlayerTo(pos);
					} catch (Exception e) {
						Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky moveto <x> <y> <z>");
					}
				}else {
					Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky moveto <x> <y> <z>");
				}
				
				return;
			}
			if(command.equals("miningmacro")) {
				if(args.length > 1) {
					if(args[1].equalsIgnoreCase("paths")){
						String paths = "";
						for (int i = 0; i < MiningUtils.miningPaths.size(); i++) {
							paths += ", " + (i+3);
						}
						
						Utils.addMinecraftMessageWithPrefix("List of paths:");
						Utils.addMinecraftMessage("1, 2" + paths);
						return;
					}
					if(args[1].equalsIgnoreCase("changepath")){
						if(args.length > 2) {
							try {
								int path = Integer.parseInt(args[2]);
								path = Math.max(0, Math.min(MiningUtils.miningPaths.size() + 2, path));
								SettingsConfig.MiningMacroPath.number = (double) path;
								Utils.addMinecraftMessageWithPrefix("Changed path to: " + path);
							} catch (Exception e) {
								Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky miningmacro changepath <path number>");
							}
						}else {
							Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky miningmacro changepath <path number>");
						}
						return;
					}
					if(args[1].equalsIgnoreCase("showpath")){
						if(args.length > 2) {
							try {
								int path = Integer.parseInt(args[2]);
								Utils.addMinecraftMessageWithPrefix("Showing path: " + path);
								if(path > 2) {
									if(path-2 > MiningUtils.miningPaths.size()) {
										Utils.addMinecraftMessage(EnumChatFormatting.RED + "There's no path " + path);
										return;
									}
									
									MiningPath pathList = MiningUtils.miningPaths.get(path-3);
									List<BlockPos> pos = new ArrayList<>();
									for (double[] coords : pathList.coordinatesList) {
										Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "" + coords[0] + " " + coords[1] + " " + coords[2]);
										pos.add(new BlockPos(coords[0],coords[1],coords[2]));
									}
									
									PathfinderUtils.drawPath(pos, true);
								}else {
									if(path == 1) {
										Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "0 166 -11");
										Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "8 188 -6");
										Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "23 229 5");
									}
									if(path == 2) {
									    Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "0 166 -11");
									    Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "0 182 23");
									    Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "0 181 34");
									    Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "8 161 66");
									    Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "8 161 106");
									    Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "9 176 139");
									    Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "11 225 129");
									}
								}
								
							} catch (Exception e) {
								Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky miningmacro showpath <path number>");
							}
						}else {
							Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky miningmacro showpath <path number>");
						}
						return;
					}
					if(args[1].equalsIgnoreCase("addpath")){
						if(args.length > 4) {
							try {
								List<double[]> coordinatesList = new ArrayList<>();
								Utils.addMinecraftMessageWithPrefix("Adding path " + MiningUtils.miningPaths.size()+3);
								
								String coordsList = connectArgs(args, 2, args.length);
								for (String coords : coordsList.split(",")) {
									String[] coord = coords.trim().split(" ");
					        		Float x = null, y = null, z = null;
					                    try {
					                    	if(CoordsDetector.isNumeric(CoordsDetector.getCoordinateValue(coord[0]))) {
					                    		x = Float.parseFloat(CoordsDetector.getCoordinateValue(coord[0]));
					                    	}
				                    		if(CoordsDetector.isNumeric(CoordsDetector.getCoordinateValue(coord[1]))) {
					                    		y = Float.parseFloat(CoordsDetector.getCoordinateValue(coord[1]));	
					                    	}
				                    		if(CoordsDetector.isNumeric(CoordsDetector.getCoordinateValue(coord[2]))) {
					                    		z = Float.parseFloat(CoordsDetector.getCoordinateValue(coord[2]));
				                    		}
				                    		
				                    		if(x != null && y != null && z != null) {
				                    			coordinatesList.add(new double[] {x,y,z});
				                    			Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "x" + x + ", y" + y + ", z" + z);
				                    		}else {
				                    			Utils.addMinecraftMessage(EnumChatFormatting.RED + "Wrong coords: x" + x + ", y" + y + ", z" + z);
				                    		}
					                    } catch (Exception e) {
					                        // Handle parsing errors
					                    	e.printStackTrace();
					                        return;
					                    }
								}
								
								MiningUtils.miningPaths.add(new MiningPath(coordinatesList));
								ConfigHandler.SaveMiningPaths(MiningUtils.miningPaths);
							} catch (Exception e) {
								Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky miningmacro addpath <x> <y> <z>, <x> <y> <z>, ...");
							}
						}else {
							Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky miningmacro addpath <x> <y> <z>, <x> <y> <z>, ...");
						}
						return;
					}
					if(args[1].equalsIgnoreCase("removepath")){
						if(args.length > 2) {
							 try {
								 int path = Integer.parseInt(args[2]) - 3;
								 MiningUtils.miningPaths.remove(path);
								 Utils.addMinecraftMessageWithPrefix("Removed path: " + (path+3));
							 }catch (Exception e) {
								 Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky miningmacro removepath <path numer>");
							}
						}else {
							Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky miningmacro removepath <path numer>");
						}
						return;
					}
					
					Utils.addMinecraftMessageWithPrefix("Wrong argument " + args[1] + ", you should use one of these: changeepath, addpath, removepath");
					if(args[1].equalsIgnoreCase("start")) {
						if(args.length > 2) {
		    				String miningType = args[2];
		    				try {
		    				    MiningType type = MiningType.valueOf(miningType.toUpperCase());
		    				    MovementUtils.resetMovementsList();
		    					Utils.executeCommand("/warp forge");
		    					MiningUtils.miningmacroPath(SettingsConfig.MiningMacroPath.number.intValue());
		    				    MovementUtils.addMovement(new Movement(() -> MiningUtils.startMining(type), 1000));
		    				} catch (IllegalArgumentException e) {
		    				    Utils.addMinecraftMessageWithPrefix("Invalid mining type: " + miningType);
		    				}
		    			}else {
		    				Utils.addMinecraftMessageWithPrefix("You have to specify mining type (/mesky miningmacro start <type>): ");
		    				Utils.addMinecraftMessage(EnumChatFormatting.AQUA + "MITHRIL, GEMSTONE, GOLD, DIAMOND, IRON, COAL, REDSTONE, LAPIS");
		    			}
					}
				}else {
					Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.RED + "Correct way of using this command /mesky miningmacro <start / removepath / addpath / showpath / changepath / paths>");
				}
				return;
			}
			if(command.equals("movetowaypoint")) {
				if(args.length > 1) {
					String name = connectArgs(args, 1, args.length).toString();
					String currentWorldIdentifier = Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld);
					Optional<Waypoint> foundWaypoint = Waypoints.waypointsList.values().stream().map(waypointGroup -> waypointGroup.list).flatMap(List::stream).filter(waypoint -> waypoint.name.equals(name) && waypoint.world.equals(currentWorldIdentifier)).findFirst();
					
					if (foundWaypoint.isPresent()) {
					    Waypoint waypoint = foundWaypoint.get();
					    BlockPos pos = new BlockPos(waypoint.coords[0], waypoint.coords[1], waypoint.coords[2]);
					    MovementUtils.movePlayerTo(pos);
					}else {
						Utils.addMinecraftMessageWithPrefix("No waypoint found in this world with name: " + name);
					}
				}else {
					Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky movetowaypoint <waypoint_name>");
				}
				
				return;
			}
			if(command.equals("stopmoving")) {
				MovementUtils.stopMoving();
				return;
			}
			if(command.equals("flyingpath")) {
				if(args.length > 3) {
					try {
						Float x = Float.parseFloat(args[1]);
						Float y = Float.parseFloat(args[2]);
						Float z = Float.parseFloat(args[3]);
						BlockPos pos = new BlockPos(x,y,z);
						PathfinderUtils.drawPathTo(pos, true, false);
					} catch (Exception e) {
						Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky flyingpath <x> <y> <z>");
					}
				}else {
					Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky flyingpath <x> <y> <z>");
				}
				
				return;
			}
			if(command.equals("pathxray")) {
				if(args.length > 3) {
					try {
						Float x = Float.parseFloat(args[1]);
						Float y = Float.parseFloat(args[2]);
						Float z = Float.parseFloat(args[3]);
						BlockPos pos = new BlockPos(x,y,z);
						PathfinderUtils.drawPathTo(pos, false, true);
					} catch (Exception e) {
						Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky pathxray <x> <y> <z>");
					}
				}else {
					Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky pathxray <x> <y> <z>");
				}
				
				return;
			}
			if(command.equals("pathtowaypoint")) {
				if(args.length > 1) {
					String name = connectArgs(args, 1, args.length).toString();
					String currentWorldIdentifier = Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld);
					Optional<Waypoint> foundWaypoint = Waypoints.waypointsList.values().stream().map(waypointGroup -> waypointGroup.list).flatMap(List::stream).filter(waypoint -> waypoint.name.equals(name) && waypoint.world.equals(currentWorldIdentifier)).findFirst();
					
					if (foundWaypoint.isPresent()) {
					    Waypoint waypoint = foundWaypoint.get();
					    BlockPos pos = new BlockPos(waypoint.coords[0], waypoint.coords[1], waypoint.coords[2]);
					    PathfinderUtils.drawPathTo(pos, true, true);
					}else {
						Utils.addMinecraftMessageWithPrefix("No waypoint found in this world with name: " + name);
					}
				}else {
					Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky pathtowaypoint <waypoint_name>");
				}
				
				return;
			}
			if(command.equals("pathtomacrowaypoint")) {
				if(args.length > 1) {
					String name = connectArgs(args, 1, args.length).toString();
					String currentWorldIdentifier = Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld);
					Optional<MacroWaypoint> foundWaypoint = MacroWaypoints.waypointsList.values().stream().flatMap(group -> group.list.stream()).filter(waypoint -> waypoint.waypoint.name.equals(name) && waypoint.waypoint.world.equals(currentWorldIdentifier)).findFirst();

					if (foundWaypoint.isPresent()) {
					    MacroWaypoint waypoint = foundWaypoint.get();
					    BlockPos pos = new BlockPos(waypoint.waypoint.coords[0], waypoint.waypoint.coords[1], waypoint.waypoint.coords[2]);
					    PathfinderUtils.drawPathTo(pos, true, true);
					}else {
						Utils.addMinecraftMessageWithPrefix("No macroWaypoint found in this world with name: " + name);
					}
				}else {
					Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky pathtomacrowaypoint <waypoint_name>");
				}
				
				return;
			}
			if(command.equals("flyingpathtowaypoint")) {
				if(args.length > 1) {
					String name = connectArgs(args, 1, args.length).toString();
					String currentWorldIdentifier = Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld);
					Optional<Waypoint> foundWaypoint = Waypoints.waypointsList.values().stream().map(waypointGroup -> waypointGroup.list).flatMap(List::stream).filter(waypoint -> waypoint.name.equals(name) && waypoint.world.equals(currentWorldIdentifier)).findFirst();
				
					if (foundWaypoint.isPresent()) {
					    Waypoint waypoint = foundWaypoint.get();
					    BlockPos pos = new BlockPos(waypoint.coords[0], waypoint.coords[1], waypoint.coords[2]);
					    PathfinderUtils.drawPathTo(pos, true, false);
					}else {
						Utils.addMinecraftMessageWithPrefix("No waypoint found in this world with name: " + name);
					}
				}else {
					Utils.addMinecraftMessageWithPrefix("Correct way of using this command /mesky flyingpathtowaypoint <waypoint_name>");
				}
				
				return;
			}
			if(command.equals("clearpath")) {
				PathfinderUtils.clearDrawingPath();
				return;
			}
			
			if(command.equals("setwebhook")){
				if(args.length > 1) {
					String webhook = connectArgs(args, 1, args.length);
					SettingsConfig.DiscordWebHook.text = webhook;
				}else {
					SettingsConfig.DiscordWebHook.text = null;
				}
				
				ConfigHandler.saveSettings();
				return;
			}
			if(command.equals("writetowebhook")) {
				if(args.length > 1) {
					String text = connectArgs(args, 1, args.length);
					Utils.sendDiscordWebhook(text);
				}
			}
			
			// no command was found matching
			Utils.addMinecraftMessageWithPrefix("There's no command: " + EnumChatFormatting.GOLD + command);
			Utils.addMinecraftMessage(EnumChatFormatting.AQUA + "Try using " + EnumChatFormatting.GOLD + "/mesky commands");
    	}
    }

	
	
	
	public String connectArgs(String[] args, int start, int end) {
		StringBuilder combinedArgs = new StringBuilder();
		end = Math.min(args.length, end);
		start = Math.max(0, start);
		for (int i = start; i < end; i++) {
		    combinedArgs.append(args[i]);
		    if (i < args.length - 1) {
		        combinedArgs.append(" "); // Add space between words, but not after the last word
		    }
		}
		
		return combinedArgs.toString();
	}
	
	@Override
    public String getCommandName() {
        return "mesky";
    }
	
	@Override
	public List<String> getCommandAliases() {
		List<String> aliases = new ArrayList<String>();
		aliases.add("Mesky");
		aliases.add("MESKY");
		aliases.add("meksy");
		aliases.add("Meksy");
		aliases.add("MEKSY");
		return aliases;
	}
	
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return (args.length == 1 && !commands.contains(args[0])) ? getListOfStringsMatchingLastWord(args, commands) : null;
	}
	
	@Override
    public String getCommandUsage(ICommandSender sender) {
        return "/mesky";
    }
	 @Override
	 public boolean canCommandSenderUseCommand(ICommandSender sender) {
	     return true;
	 }
	 @Override
	 public int getRequiredPermissionLevel() {
	     return 0;
	 }
	
}
