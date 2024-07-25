package treemek.mesky.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import treemek.mesky.Mesky;
import treemek.mesky.handlers.gui.waypoints.WaypointsGui;
import treemek.mesky.utils.Locations.Location;

public class Utils {
	private static final Logger logger = LogManager.getLogger();
	
	// Taken from SkyblockAddons
	public static List<String> getItemLore(ItemStack itemStack) {
	final int NBT_INTEGER = 3;
	final int NBT_STRING = 8;
	final int NBT_LIST = 9;
	final int NBT_COMPOUND = 10;

	if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("display", NBT_COMPOUND)) {
		NBTTagCompound display = itemStack.getTagCompound().getCompoundTag("display");

		if (display.hasKey("Lore", NBT_LIST)) {
			NBTTagList lore = display.getTagList("Lore", NBT_STRING);

			List<String> loreAsList = new ArrayList<>();
			for (int lineNumber = 0; lineNumber < lore.tagCount(); lineNumber++) {
				loreAsList.add(lore.getStringTagAt(lineNumber));
			}

			return Collections.unmodifiableList(loreAsList);
		}
	}

	return Collections.emptyList();
}


	 public static String getNameFromUUID(String uuid) throws Exception {
		 
	 	StringBuilder result = new StringBuilder();
	    URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("GET");
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
	        for (String line; (line = reader.readLine()) != null; ) {
	            result.append(line);
	        }
	    }
	    
	    String jsonString = result.toString();
	    // Split the JSON string by "name":""
	  	String[] parts = jsonString.split("\"name\"\\s*:\\s*\"");
	  	if (parts.length >= 2) {
	        String name = parts[1]; // Take the second part after the split
	        int endIndex = name.indexOf('"'); // Find the ending quote
	        if (endIndex != -1) {
	            return name.substring(0, endIndex); // Extract the name
	        }
	  	}
	    return null; // Return null if name extraction fails
	}
	 
	 
	 public static boolean containsWord(List<String> list, String word) {
	        return list.stream().anyMatch(sentence -> sentence.contains(word));
	    }

	 public static List<String> getScoreboardLines(boolean withColors) {
	        List<String> lines = new ArrayList<>();
	        Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
	        if (scoreboard == null) return lines;

	        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);

	        if (objective == null) return lines;

	        Collection<Score> scores = scoreboard.getSortedScores(objective);
	        List<Score> list = Lists.newArrayList(scores.stream()
	                .filter(input -> input != null && input.getPlayerName() != null && !input.getPlayerName().startsWith("#"))
	                .collect(Collectors.toList()));

	        if (list.size() > 15) {
	            scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
	        } else {
	            scores = list;
	        }

	        for (Score score : scores) {
	            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
	            String line = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
	            if(!withColors) line = StringUtils.stripControlCodes(line);
	            lines.add(line);
	        }

	        return lines;
	    }
	 
	 public static String removeEmojisFromString(String s) {
		 return s.replaceAll("[^\\x00-\\x7F]", "");
	 }
	 
	 public static float normalizeAngle(float angle) {
		    angle = angle % 360.0F;
		    while(angle > 180 || angle < -180) {
			    if (angle >= 180.0F) {
			        angle -= 360.0F;
			    }
			    if (angle < -180.0F) {
			        angle += 360.0F;
			    }
		    }
		    return angle;
	 }
	 
	 // if bPercent is 0.1 then a=90% and b=10%
	 public static float getPrecentAverage(float a, float b, float bPercent) {
		 float aPercent = 1-bPercent;
		 return	(a*aPercent)+(b*bPercent);
	 }
	 
	 public static float bezier(float startPoint, float controlPoint, float goalPoint, float percentOfCurve) {
	    float u = 1 - percentOfCurve;
	    return (u * u * startPoint) + (2 * u * percentOfCurve * controlPoint) + (percentOfCurve * percentOfCurve * goalPoint);
	}
	 
	 public static float getRandomizedMinusOrPlus(float number) {
		 boolean plus = new Random().nextBoolean();
		 return (plus)?number:-number;
	 }


	 public static float cubicBezier(float startPoint, float controlPoint1, float controlPoint2, float goalPoint, float percentOfCurve) {
		    float u = 1 - percentOfCurve;
		    float tt = percentOfCurve * percentOfCurve;
		    float uu = u * u;
		    float uuu = uu * u;
		    float ttt = tt * percentOfCurve;

		    float p = uuu * startPoint; // (1-t)^3 * P0
		    p += 3 * uu * percentOfCurve * controlPoint1; // 3 * (1-t)^2 * t * P1
		    p += 3 * u * tt * controlPoint2; // 3 * (1-t) * t^2 * P2
		    p += ttt * goalPoint; // t^3 * P3

		    return p;
	}
	 
	 public static String fixColor(String color) {
    	if(color == null || color.length() == 0) {
    		return "ffffff";
    	}
    	
    	color = color.replace("#", "");
    	
    	if(color.length() > 8) {
    		color = color.substring(0, 8);
    	}
    	
    	try {
			Color.decode("#" + color);
			return color;
		} catch (Exception e) {
			System.out.println(e);
			color = "ffffff";
			return color;
		}
    }
	 
	 public static int InvertColor(int color) {
		 int r = (color >> 16) & 0xFF;
	        int g = (color >> 8) & 0xFF;
	        int b = color & 0xFF;
	        
	        // Invert each component
	        int invertedR = 255 - r;
	        int invertedG = 255 - g;
	        int invertedB = 255 - b;
	        
	        // Reassemble the color
	        return (invertedR << 16) | (invertedG << 8) | invertedB;
	 }
	 
	 public static int getColorInt(String color) {
		 color = color.replace("#", "");
		 
		 if(color.length() > 6) {
    		color = color.substring(0, 6);
		 }
		 
		 try {
			Color c = Color.decode("#" + color.replace("#", ""));
			return c.getRGB();
		} catch (Exception e) {
			return 0xFFFFFFFF;
		}
		 
		 
		 
	 }
	 
	 
	 public static Vec3 getBlockLookingAt(double reachDistance) {
	 	Minecraft mc = Minecraft.getMinecraft();
        Vec3 playerPos = mc.thePlayer.getPositionEyes(1.0f);
        Vec3 lookVec = mc.thePlayer.getLook(1.0f);
        Vec3 reachVec = playerPos.addVector(lookVec.xCoord * reachDistance, lookVec.yCoord * reachDistance, lookVec.zCoord * reachDistance);

        MovingObjectPosition rayTraceResult = mc.theWorld.rayTraceBlocks(playerPos, reachVec, false, false, true);

        if (rayTraceResult != null && rayTraceResult.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            return rayTraceResult.hitVec;
        }
        return null;
    }
	 
	 public static String getWorldIdentifier(WorldClient world) {
		 Minecraft mc = Minecraft.getMinecraft();
        if (mc.isSingleplayer()) {
        	
            return mc.getIntegratedServer().getWorldName() + "_" + world.provider.getDimensionName();  // Singleplayer world name
        } else if(HypixelCheck.isOnHypixel()){
        	Location.checkTabLocation();
        	return Locations.currentLocationText;
        }else {
            ServerData serverData = mc.getCurrentServerData();
            if (serverData != null) {
                return serverData.serverIP + "_" + world.provider.getDimensionName();  // Multiplayer server IP
            }
        }
        return "unknown";
    }
	 
	 public static String getWorldIdentifierWithRegionTextField(WorldClient world) {
		 Minecraft mc = Minecraft.getMinecraft();
        if (mc.isSingleplayer()) {
            return mc.getIntegratedServer().getWorldName() + "_" + world.provider.getDimensionName();  // Singleplayer world name
        } else if(HypixelCheck.isOnHypixel()){
        	if(mc.currentScreen.getClass() == WaypointsGui.class) {
        		if(WaypointsGui.region != null && WaypointsGui.region.getText() != null) {
        			return WaypointsGui.region.getText();
        		}else {
        			Location.checkTabLocation();
    	        	return Locations.currentLocationText;
        		}
        	}else {
	        	Location.checkTabLocation();
	        	return Locations.currentLocationText;
        	}
        }else {
            ServerData serverData = mc.getCurrentServerData();
            if (serverData != null) {
                return serverData.serverIP + "_" + world.provider.getDimensionName();  // Multiplayer server IP
            }
        }
        return "unknown";
    }
	 
 	// Smooth transition from 1 to 0 as angle moves away from A to B
    public static double influenceBasedOnDistanceFromAToB(double number, double A, double B) {
    	number = Math.abs(number);
    	A = Math.abs(A);
    	B = Math.abs(B);
    	
    	if (number <= A) return 1.0;
        if (number >= B) return 0.0;
        double x = (number - A) / (B - A); // Normalize to [0, 1]
        return 1.0 / (1.0 + Math.exp(10 * (x - 0.5))); // Adjust the steepness of the transition with a factor (10)
    }
    
    public static float getPlayerRidingWithoutControlRotation(EntityLivingBase ridingEntity,  float partialTicks) {
		// from RendererLivingEntity doRender()
		
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        float yawHeadRotation = interpolate360(player.prevRotationYawHead, player.rotationYawHead, partialTicks);
        
		float riddenEntityRotation = interpolate360(ridingEntity.prevRenderYawOffset, ridingEntity.renderYawOffset, partialTicks);
        float diff = yawHeadRotation - riddenEntityRotation;
        float f3 = MathHelper.wrapAngleTo180_float(diff);

        if (f3 < -85.0F)
        {
            f3 = -85.0F;
        }

        if (f3 >= 85.0F)
        {
            f3 = 85.0F;
        }

        riddenEntityRotation = yawHeadRotation - f3;

        if (f3 * f3 > 2500.0F)
        {
            riddenEntityRotation += f3 * 0.2F;
        }
        
        
        return riddenEntityRotation;
	}
    
    public static Entity getEntityLookedAt(double range) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if(player == null) return null;
        Vec3 eyePosition = player.getPositionEyes(1.0F);
        Vec3 lookVector = player.getLook(1.0F);
        Vec3 traceEnd = eyePosition.addVector(lookVector.xCoord * range, lookVector.yCoord * range, lookVector.zCoord * range);

        // Ray trace for blocks first
        MovingObjectPosition blockHit = mc.theWorld.rayTraceBlocks(eyePosition, traceEnd);
        Vec3 hitVec = blockHit != null ? blockHit.hitVec : traceEnd;

        Entity pointedEntity = null;
        double minDistance = range;

        List<Entity> entities = mc.theWorld.getEntitiesWithinAABBExcludingEntity(player,
            player.getEntityBoundingBox()
            .addCoord(lookVector.xCoord * range, lookVector.yCoord * range, lookVector.zCoord * range)
            .expand(1.0D, 1.0D, 1.0D)
        );

        for (Entity entity : entities) {
            if (entity.canBeCollidedWith()) {
                float collisionBorderSize = entity.getCollisionBorderSize();
                AxisAlignedBB entityBB = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                MovingObjectPosition entityHit = entityBB.calculateIntercept(eyePosition, hitVec);

                if (entityBB.isVecInside(eyePosition)) {
                    if (0.0D < minDistance || minDistance == 0.0D) {
                        pointedEntity = entity;
                        minDistance = 0.0D;
                    }
                } else if (entityHit != null) {
                    double distanceToHit = eyePosition.distanceTo(entityHit.hitVec);

                    if (distanceToHit < minDistance || minDistance == 0.0D) {
                        if (entity == player.ridingEntity && !entity.canRiderInteract()) {
                            if (minDistance == 0.0D) {
                                pointedEntity = entity;
                            }
                        } else {
                            pointedEntity = entity;
                            minDistance = distanceToHit;
                        }
                    }
                }
            }
        }

        return pointedEntity;
    }
    
	public static float interpolate360(float yaw1, float yaw2, float percent)
	{
		float f = (yaw1 + (yaw2 - yaw1) * percent);
		while(f < 0 || f > 360) {
			if (f < 0)
			{
				f += 360;
			}
			if(f > 360) {
				f -= 360;
			}
		}

		return f;
	}
	
	public static String getCurrentTime() {
        LocalTime now = LocalTime.now();

        String timeFormatted = now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();
        return timeFormatted;
	}
	
	public static void writeError(Exception e) {
		e.printStackTrace();
		writeMinecraftMessage(EnumChatFormatting.RED + "[" + getCurrentTime() + "] " + e);
	}
	
	public static void writeError(String e) {
		writeToConsole(e);
		writeMinecraftMessage(EnumChatFormatting.RED + "[" + getCurrentTime() + "] " + e);
	}
	
	public static void writeMinecraftMessage(String a) {
		if(Minecraft.getMinecraft().thePlayer != null) {
			if(a == null) a = "";
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(a));
		}
	}
	
	public static boolean systemUsesRightSlashes() {
		if(Mesky.configDirectory.contains("/")) {
			return true;
		}else {
			return false;
		}
	}
	
	
	
	public static void writeToConsole(String a) {
		logger.info("[Mesky]: " + a);
	}
}


