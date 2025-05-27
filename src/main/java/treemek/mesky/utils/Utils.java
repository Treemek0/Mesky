package treemek.mesky.utils;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import treemek.mesky.Mesky;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.handlers.gui.waypoints.WaypointsGui;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.manager.PartyManager;

public class Utils {
	private static final Logger logger = LogManager.getLogger();
	
	public static class smoothFunction {
		double START = 0;
		double MIDDLE;
		double CURRENT = 0;
		double FINAL = 0;
		double addHowManyPercent = 0.1;
		boolean enabled = true;
		
		public smoothFunction(double START, double FINAL, double addHowManyPrecent, boolean enabled) {
			this.START = START;
			this.MIDDLE = (FINAL + START)/2;
			this.CURRENT = START;
			this.FINAL = FINAL;
			this.addHowManyPercent = addHowManyPrecent;
			this.enabled = enabled;
		}
		
		public smoothFunction(double START, double MIDDLE, double FINAL, double addHowManyPrecent, boolean enabled) {
			this.START = START;
			this.MIDDLE = MIDDLE;
			this.CURRENT = START;
			this.FINAL = FINAL;
			this.addHowManyPercent = addHowManyPrecent;
			this.enabled = enabled;
		}
		
		boolean passedMiddle = false; 
		public void next() {
	        if (!enabled) return;

	        // Determine the increment based on the position of CURRENT relative to MIDDLE
	        double increment;
	        if (CURRENT < MIDDLE && !passedMiddle) {
	            // Transition towards MIDDLE
	            increment = (MIDDLE - START) * addHowManyPercent;
	        } else {
	            // Transition towards FINAL
	        	passedMiddle = true;
	            increment = (FINAL - MIDDLE) * addHowManyPercent;
	        }

	        // Adjust CURRENT by the calculated increment
	        CURRENT += increment;

	        // Determine the direction of the transition
	        boolean isIncreasing = (CURRENT < FINAL);
	        
	        Utils.debug(CURRENT + " " + isIncreasing + " " + FINAL);
	        // Check if CURRENT has reached or surpassed FINAL
	        if (passedMiddle && Math.abs(FINAL - CURRENT) <= Math.abs((FINAL - MIDDLE) * addHowManyPercent)) {
	            CURRENT = FINAL;
	            setEnabled(false);
	            Utils.debug("rip");
	            return; // No further processing if enabled is false
	        }
	        
	        // Ensure CURRENT does not exceed FINAL or drop below FINAL
	        if (isIncreasing) {
	            if (CURRENT > FINAL) {
	                CURRENT = FINAL; // Clamp to FINAL for increasing
	            }
	        } else {
	            if (CURRENT < FINAL) {
	                CURRENT = FINAL; // Clamp to FINAL for decreasing
	            }
	        }
	    }
		
		public double getCurrent() {
			if(!enabled) return START;
			return CURRENT;
		}
		
		public void setEnabled(boolean b) {
			enabled = b;
		}
		
		public void reset() {
			CURRENT = START;
			passedMiddle = false;
		}
	}
	
	private static Map<String, Integer> mapTextureCounters = new HashMap<>();
    public static ResourceLocation getDynamicLocation(String name)
    {
        Integer integer = (Integer)mapTextureCounters.get(name);

        if (integer == null)
        {
            integer = Integer.valueOf(1);
        }
        else
        {
            integer = Integer.valueOf(integer.intValue() + 1);
        }

        mapTextureCounters.put(name, integer);
        ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", new Object[] {name, integer}));
        return resourcelocation;
    }
	
	
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

	public static String generateRandomString(int LENGTH) {
        StringBuilder sb = new StringBuilder(LENGTH);
        Random random = new Random();
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

	public static String generateRandomHexString(int LENGTH) {
        StringBuilder sb = new StringBuilder(LENGTH);
        Random random = new Random();
        String CHARACTERS = "ABCDEFabcdef0123456789";
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
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
	 
	 public static float clampPitch(float pitch) {
		 return Math.max(-90, Math.min(90, pitch));
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
	 
	 public static String getWorldIdentifier(WorldClient world) {
		 Minecraft mc = Minecraft.getMinecraft();
        if (mc.isSingleplayer()) {
            return mc.getIntegratedServer().getFolderName() + "_" + world.provider.getDimensionName();  // Singleplayer world name
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
            return mc.getIntegratedServer().getFolderName() + "_" + world.provider.getDimensionName();  // Singleplayer world name
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
	
	public static String getSkyblockId(ItemStack itemStack) {
	    if (itemStack.hasTagCompound()) {
	        NBTTagCompound tagCompound = itemStack.getTagCompound();
	        
	        // Check for the "ExtraAttributes" compound tag
	        if (tagCompound.hasKey("ExtraAttributes", 10)) { // 10 represents an NBT compound tag
	            NBTTagCompound extraAttributes = tagCompound.getCompoundTag("ExtraAttributes");

	            // Get the item ID from the "id" tag within "ExtraAttributes"
	            if (extraAttributes.hasKey("id", 8)) { // 8 represents an NBT string
	                return extraAttributes.getString("id");
	            }
	        }
	    }
	    return null; // Return null if the item ID is not found
	}
	
	public static boolean isNPC(Entity entity) {
        if (!(entity instanceof EntityOtherPlayerMP) || !HypixelCheck.isOnHypixel()) {
            return false;
        }

        EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

        return entity.getUniqueID().version() == 2 && entityLivingBase.getHealth() == 20.0F && !entityLivingBase.isPlayerSleeping();
    }
	
	public static boolean isSolid(BlockPos p) {
		IBlockState state = getBlockState(p);
		Block block = state.getBlock();
		if(block instanceof BlockSkull) return true;
		if(block == Blocks.standing_banner || block == Blocks.wall_banner || block == Blocks.standing_sign || block == Blocks.wall_sign || block == Blocks.heavy_weighted_pressure_plate || block == Blocks.light_weighted_pressure_plate || block == Blocks.stone_pressure_plate || block == Blocks.wooden_pressure_plate) return false;
		if(block == Blocks.snow_layer && state.getValue(BlockSnow.LAYERS) > 1) return true;
		return block.getMaterial().isSolid();
	}
	
	public static double getBlockHeight(BlockPos p) {
		IBlockState state = getBlockState(p);
		Block block = state.getBlock();
		if(block == Blocks.snow_layer) {
			return (state.getValue(BlockSnow.LAYERS)-1)*0.125;
		}
		return block.getBlockBoundsMaxY() - block.getBlockBoundsMinY();
	}

	public static IBlockState getBlockState(BlockPos p) {
		return Minecraft.getMinecraft().theWorld.getBlockState(p);
	}
	
	public static double getJumpHeight() {
	    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

	    double motionY = 0.42F;

	    if (player.isPotionActive(Potion.jump)) {
	        motionY += (double)((float)(player.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
	    }

	    // Minecraft gravity constant
	    double gravity = -0.08;
	    double drag = 0.98;  // The drag applied every tick
	    double height = 0.0;

	    while (motionY > 0) {
	        height += motionY;
	        motionY = motionY * drag + gravity;
	    }

	    return height;
	}
	
	public static boolean systemUsesRightSlashes() {
		if(Mesky.configDirectory.contains("/")) {
			return true;
		}else {
			return false;
		}
	}
	
	// raycasts to center of blocks and its corners (if block not visible then null)
	public static double[] getRaycastToBlock(BlockPos targetPos, boolean forceShift) {
	    Minecraft mc = Minecraft.getMinecraft();
	    EntityPlayerSP player = mc.thePlayer;

	    Vec3 eyePosition = player.getPositionEyes(1.0f);
	    
	    if(!player.isSneaking() && forceShift) {
	    	eyePosition = eyePosition.addVector(0, -0.08, 0);
	    }
	    
	    // Check visibility from the center
	    if (raycastToBlock(eyePosition, targetPos.getX() + 0.5,  targetPos.getY() + 0.5,  targetPos.getZ() + 0.5, targetPos)) {
	        return new double[] {targetPos.getX() + 0.5,  targetPos.getY() + 0.5,  targetPos.getZ() + 0.5};
	    }

	    double[][] corners = {
	        {0.1, 0.1, 0.1}, {0.9, 0.1, 0.1}, {0.1, 0.1, 0.9}, {0.9, 0.1, 0.9},
	        {0.1, 0.9, 0.1}, {0.9, 0.9, 0.1}, {0.1, 0.9, 0.9}, {0.9, 0.9, 0.9}
	    };

	    // Check visibility from each corner
	    for (double[] corner : corners) {
	        if (raycastToBlock(eyePosition, targetPos.getX() + corner[0], targetPos.getY() + corner[1], targetPos.getZ() + corner[2], targetPos)) {
	            return new double[] {targetPos.getX() + corner[0], targetPos.getY() + corner[1], targetPos.getZ() + corner[2]};
	        }
	    }

	    return null;
	}
	
	// check if raycast to coordinates returns block given
	private static boolean raycastToBlock(Vec3 eyePosition, double X, double Y, double Z, BlockPos targetPos) {
	    Minecraft mc = Minecraft.getMinecraft();

	    // Convert target block position to Vec3 (with offsets)
	    Vec3 targetVec = new Vec3(X, Y, Z);

	    // Calculate direction vector from the player's eyes to the target point
	    Vec3 direction = targetVec.subtract(eyePosition).normalize();

	    // Calculate the maximum distance from the player's eyes to the target point
	    double maxDistance = eyePosition.distanceTo(targetVec);

	    // Scale the direction vector to the maximum distance
	    Vec3 scaledDirection = new Vec3(direction.xCoord * maxDistance, direction.yCoord * maxDistance, direction.zCoord * maxDistance);

	    // Perform the raycast
	    MovingObjectPosition result = mc.theWorld.rayTraceBlocks(eyePosition, eyePosition.addVector(scaledDirection.xCoord, scaledDirection.yCoord, scaledDirection.zCoord), false, false, false);

	    // Check if the ray hit the target block
	    return result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && result.getBlockPos().equals(targetPos);
	}
	
	
	public static BlockPos getBlockLookingAt(double maxDistance) {
	    Minecraft mc = Minecraft.getMinecraft();
	    EntityPlayerSP player = mc.thePlayer;

	    // Get the player's eye position and look direction
	    Vec3 eyePosition = player.getPositionEyes(1.0f);
	    Vec3 lookVector = player.getLook(1.0f);

	    // Scale the look vector to the maximum distance
	    Vec3 targetVec = eyePosition.addVector(lookVector.xCoord * maxDistance, lookVector.yCoord * maxDistance, lookVector.zCoord * maxDistance);

	    // Perform the ray trace to find the block the player is looking at
	    MovingObjectPosition result = mc.theWorld.rayTraceBlocks(eyePosition, targetVec, false, false, false);

	    // Check if the ray hit a block
	    if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
	        return result.getBlockPos(); // Return the position of the block
	    }

	    return null; // Return null if no block was hit
	}
	
	public static BlockPos getBlockPos(double x, double y, double z) {
		return new BlockPos(Math.floor(x), Math.floor(y), Math.floor(z));
	}
	
	public static BlockPos getBlockPos(double[] c) {
		if(c.length < 3) return new BlockPos(0,0,0);
		double x = c[0];
		double y = c[1];
		double z = c[2];
		return new BlockPos(Math.floor(x), Math.floor(y), Math.floor(z));
	}

	public static boolean isHexadecimal(String input) {
        return input.matches("^[0-9A-Fa-f]+$");
    }
	
	public static BlockPos playerPosition() {
		if(Minecraft.getMinecraft().thePlayer == null) return new BlockPos(0,0,0);
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
	}
	
	public static float[] precisePlayerPosition() {
		if(Minecraft.getMinecraft().thePlayer == null) return new float[] {0,0,0};
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		return new float[] {Math.round(player.posX*10)/10f, Math.round(player.posY*10)/10f, Math.round(player.posZ*10)/10f};
	}
	
	public static float distance(float x, float y, float z, float dx, float dy, float dz) {
		float d0 = x - dx;
        float d1 = y - dy;
        float d2 = z - dz;
        return d0 * d0 + d1 * d1 + d2 * d2;
	}
	
	public static double distance(double x, double y, double z, double dx, double dy, double dz) {
		double d0 = x - dx;
        double d1 = y - dy;
        double d2 = z - dz;
        return d0 * d0 + d1 * d1 + d2 * d2;
	}
	
	public static float distanceSqrt(float x, float y, float z, float dx, float dy, float dz) {
		float d = distance(x, y, z, dx, dy, dz);
		return (float) Math.sqrt(d);
	}
	

	public static double distanceSqrt(double x, double y, double z, double dx, double dy, double dz) {
		double d = distance(x, y, z, dx, dy, dz);
		return Math.sqrt(d);
	}
	
	public static void copyToClipboard(String text) {
		StringSelection stringSelection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}
	
	public static boolean isPlayerVisible(EntityPlayer targetPlayer) {
	    Minecraft mc = Minecraft.getMinecraft();
	    EntityPlayer localPlayer = mc.thePlayer;

	    if (localPlayer == null || targetPlayer == null || targetPlayer == localPlayer) {
	        return false;
	    }

	    // Get the eye position and direction of the local player
	    Vec3 localPlayerEyePos = localPlayer.getPositionEyes(1.0f);
	    Vec3 lookVec = localPlayer.getLook(1.0f);
	    
	    // Calculate vector to the target player
	    Vec3 targetVec = new Vec3(targetPlayer.posX, targetPlayer.posY + targetPlayer.getEyeHeight(), targetPlayer.posZ);
	    Vec3 toTarget = targetVec.subtract(localPlayerEyePos).normalize();

	    // Check if the target player is within the view angle
	    double dotProduct = lookVec.dotProduct(toTarget);
	    double angle = Math.acos(dotProduct) * (180.0 / Math.PI);

	    float fov = Minecraft.getMinecraft().gameSettings.fovSetting;
	    if (angle < fov*0.7) { 
	        MovingObjectPosition result = mc.theWorld.rayTraceBlocks(localPlayerEyePos, targetVec);
	        return result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS || result.entityHit == targetPlayer;
	    }

	    return false;
	}
	
	public static EntityPlayer isAnyPlayerVisible() {
		for (EntityPlayer targetPlayer : Minecraft.getMinecraft().theWorld.playerEntities) {
			if(targetPlayer == Minecraft.getMinecraft().thePlayer) continue;
			// ignore goblins if(targetPlayer.)
			
			if(isPlayerVisible(targetPlayer)) {
				Utils.debug(targetPlayer + " is visible, with name: " + targetPlayer.getName());
				return targetPlayer;
			}
		}
		
		return null;
	}
	
	public static EntityPlayer isAnyPlayerVisibleBesideNames(String[] names) {
		for (EntityPlayer targetPlayer : Minecraft.getMinecraft().theWorld.playerEntities) {
			if(targetPlayer == Minecraft.getMinecraft().thePlayer) continue;
			
			if(isPlayerVisible(targetPlayer)) {
				if(Arrays.asList(names).contains(targetPlayer.getName().trim())) continue;
				if(targetPlayer.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) < 9) {
					Utils.debug(targetPlayer + " is visible, with name: " + targetPlayer.getName());
					return targetPlayer;
				}
			}
		}
		
		return null;
	}
	
	public static EntityPlayer isAnyPlayerVisibleBesideList(List<EntityPlayer> list) {
		for (EntityPlayer targetPlayer : Minecraft.getMinecraft().theWorld.playerEntities) {
			if(targetPlayer == Minecraft.getMinecraft().thePlayer) continue;
			if(list.contains(targetPlayer)) continue;
			
			if(isPlayerVisible(targetPlayer)) {
				if(targetPlayer.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) < 9) {
					Utils.debug(targetPlayer + " is visible");
					return targetPlayer;
				}
			}
		}
		
		return null;
	}
	
	public static EntityPlayer isAnyPlayerVisibleBesideListAndNames(List<EntityPlayer> list, String[] names) {
		for (EntityPlayer targetPlayer : Minecraft.getMinecraft().theWorld.playerEntities) {
			if(targetPlayer == Minecraft.getMinecraft().thePlayer) continue;
			if(list.contains(targetPlayer)) continue;
			
			if(isPlayerVisible(targetPlayer)) {
				if(Arrays.asList(names).contains(targetPlayer.getName().trim())) continue;
				if(targetPlayer.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) < 9) {
					Utils.debug(targetPlayer + " is visible");
					return targetPlayer;
				}
			}
		}
		
		return null;
	}
	
	public static List<double[]> sortBlocksByProximity(List<double[]> blocks, double[] currentblock) {
	    if (blocks.isEmpty()) return blocks;

	    List<double[]> sorted = new ArrayList<>();
	    Set<Integer> visited = new HashSet<>();

	    // Find and add the starting block (closest to currentblock)
	    int startIdx = 0;
	    double minStartDist = Double.MAX_VALUE;
	    for (int i = 0; i < blocks.size(); i++) {
	        double dist = distance(blocks.get(i)[0], blocks.get(i)[1], blocks.get(i)[2], currentblock[0], currentblock[1], currentblock[2]);
	        if (dist < minStartDist) {
	            minStartDist = dist;
	            startIdx = i;
	        }
	    }

	    sorted.add(blocks.get(startIdx));
	    visited.add(startIdx);

	    while (visited.size() < blocks.size()) {
	        double[] last = sorted.get(sorted.size() - 1);
	        double minDist = Double.MAX_VALUE;
	        int nextIdx = -1;

	        for (int i = 0; i < blocks.size(); i++) {
	            if (visited.contains(i)) continue;
	            double[] b = blocks.get(i);
	            double dist = distance(last[0], last[1], last[2], b[0], b[1], b[2]);
	            if (dist < minDist) {
	                minDist = dist;
	                nextIdx = i;
	            }
	        }

	        if (nextIdx != -1) {
	            sorted.add(blocks.get(nextIdx));
	            visited.add(nextIdx);
	        }
	    }

	    // Add waypoints
	    if(Mesky.debug) {
		    for (int i = 0; i < sorted.size(); i++) {
		        double[] b = sorted.get(i);
		        Waypoints.addTemporaryWaypoint(i + "", "000000", (float) b[0], (float) b[1], (float) b[2], 1, 1000);
		    }
	    }

	    return sorted;
	}

	
	public static float getYawDifference(float yaw1, float yaw2) {
	    float difference = yaw1 - yaw2;

	    // Normalize the difference to be within the range -180 to 180 degrees
	    while (difference > 180) {
	        difference -= 360;
	    }
	    while (difference < -180) {
	        difference += 360;
	    }

	    return Math.abs(difference);
	}
	
	public static void addToMinecraftAndWriteToConsole(String a) {
		writeToConsole(a);
		addMinecraftMessage(a);
	}
	
	public static void write(String a) {
		addToMinecraftWithPrefixAndWriteToConsole(a);
	}
	
	public static void executeCommand(String a) {
		if(Minecraft.getMinecraft().thePlayer != null) {
			if(a == null) a = "/";
			if(!a.startsWith("/")) a = "/" + a;
			if (net.minecraftforge.client.ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().thePlayer, a) != 0) return;
			
			Minecraft.getMinecraft().thePlayer.sendChatMessage(a);
		}
	}
	
	public static void addToMinecraftWithPrefixAndWriteToConsole(String a) {
		writeToConsole(a);
		addMinecraftMessageWithPrefix(a);
	}
	
	public static void debug(String a) {
		if(Mesky.debug) {
		    StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
		    
		    String className = caller.getClassName();
		    String methodName = caller.getMethodName();
		    int lineNumber = caller.getLineNumber();
		    
			writeToConsole("(" + className + ", " + methodName + "() " + "[" + lineNumber + "]" + "): " + a);
			addMinecraftMessageWithPrefix("[DEBUG] " + a);
		}
	}
	
	public static void writeToConsole(String a) {
		logger.info("[Mesky]: " + a);
	}
	
	
	public static void writeError(Exception e) {
		e.printStackTrace();
		addMinecraftMessage(EnumChatFormatting.RED + "[" + getCurrentTime() + "] " + e);
	}
	
	public static void writeError(String e) {
		writeToConsole(e);
		addMinecraftMessage(EnumChatFormatting.RED + "[" + getCurrentTime() + "] " + e);
	}
	
	public static void addMinecraftMessage(String a) {
		if(Minecraft.getMinecraft().thePlayer != null) {
			if(a == null) a = "";
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(a));
		}
	}
	
	public static void addMinecraftMessageWithPrefix(String a) {
		if(Minecraft.getMinecraft().thePlayer != null) {
			if(a == null) a = "";
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + a));
		}
	}
	
	public static void addMinecraftMessage(ChatComponentText comp) {
		if(Minecraft.getMinecraft().thePlayer != null) {
			if(comp == null) comp = new ChatComponentText("");
			Minecraft.getMinecraft().thePlayer.addChatMessage(comp);
		}
	}
	
	public static void addMinecraftMessageWithPrefix(ChatComponentText comp) {
		if(Minecraft.getMinecraft().thePlayer != null) {
			if(comp == null) comp = new ChatComponentText("");
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE).appendSibling(comp));
		}
	}
	
	public static void writeToPartyMinecraft(String a) {
		if(Minecraft.getMinecraft().thePlayer != null) {
			if(a == null) a = "";
			if(!PartyManager.isInParty) {
				debug("Can't write to party, because you aren't in one");
				return;
			}
			Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc " + a);
		}
	}
	
	public static void sendDiscordWebhook(String webhookUrl, String content) {
		if(webhookUrl == null) return;
		
	    new Thread(() -> {
	        try {
	            URL url = new URL(webhookUrl);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	            connection.setRequestMethod("POST");
	            connection.setRequestProperty("Content-Type", "application/json");
	            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
	            connection.setDoOutput(true);

	            String payload = "{\"content\": \"" + content.replace("\"", "\\\"") + "\"}";

	            try (OutputStream os = connection.getOutputStream()) {
	                os.write(payload.getBytes(StandardCharsets.UTF_8));
	            }

	            int responseCode = connection.getResponseCode();
	            System.out.println("Discord webhook response: " + responseCode);

	            connection.getInputStream().close();
	            connection.disconnect();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }).start();
	}

	public static void sendDiscordWebhook(String content) {
		String webhookUrl = SettingsConfig.DiscordWebHook.text;
		if(webhookUrl == null) return;
		
	    new Thread(() -> {
	        try {
	            URL url = new URL(webhookUrl);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	            connection.setRequestMethod("POST");
	            connection.setRequestProperty("Content-Type", "application/json");
	            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
	            connection.setDoOutput(true);

	            String payload = "{\"content\": \"" + content.replace("\"", "\\\"") + "\"}";

	            try (OutputStream os = connection.getOutputStream()) {
	                os.write(payload.getBytes(StandardCharsets.UTF_8));
	            }

	            int responseCode = connection.getResponseCode();
	            System.out.println("Discord webhook response: " + responseCode);

	            connection.getInputStream().close();
	            connection.disconnect();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }).start();
	}
}


