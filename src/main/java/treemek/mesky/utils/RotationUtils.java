package treemek.mesky.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.config.SettingsConfig.Setting;

public class RotationUtils {
	private static List<float[]> recordedAngles;
	private static int currentFrame = -1;
	private static float[] currentStartedRotation;
	private int tickCounter = 0;
	private static Queue<List<float[]>> rotationQueue = new LinkedList<>();
	
	public static List<float[]> readMovementFromFile(File file){
		try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<float[]>>(){}.getType();
            List<float[]> movement = gson.fromJson(reader, type);

            if(movement != null) {
            	return movement;
            }else {
            	return null;
            }
    	} catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
	}
	
	private static void replayMovement(List<float[]> movement) {
		recordedAngles = movement;
		currentStartedRotation = new float[] {Utils.normalizeAngle(Minecraft.getMinecraft().thePlayer.rotationYaw), Utils.normalizeAngle(Minecraft.getMinecraft().thePlayer.rotationPitch)};
		currentFrame = 0;
	}
	
	
	@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
			if (event.phase == TickEvent.Phase.START) {	
				if(Minecraft.getMinecraft().theWorld.isRemote) {
					if (currentFrame >= 0 && !recordedAngles.isEmpty()) {
						float[] current = recordedAngles.get(currentFrame);
						EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
						player.setPositionAndRotation(player.posX, player.posY, player.posZ, currentStartedRotation[0] + current[0], currentStartedRotation[1] + current[1]);
						
						tickCounter++;
						
						if (tickCounter >= 2) {
							currentFrame++;
							tickCounter = 0;
						}
						
						if (currentFrame >= recordedAngles.size()) {
							currentFrame = -1; // Stop replaying
							tickCounter = 0;
						}
					}
				}
			}
		}
		
		if(!rotationQueue.isEmpty()) {
			if(currentFrame == -1) {
				replayMovement(rotationQueue.poll());
			}
		}
	}
	
	public static void rotateStraightTo(float yaw, float pitch, float seconds, boolean addNoise) {
		float[] goalRotation = new float[] {Utils.normalizeAngle(yaw), Utils.normalizeAngle(pitch)};
		
		int ticksInSeconds = Minecraft.getMinecraft().getDebugFPS();
		float ticksReq = (float)ticksInSeconds * seconds;
		
		List<float[]> angles = new ArrayList<>();
		for (float i = 0; i < ticksReq; i++) {
			float interpolatedYaw = Utils.getPrecentAverage(0, goalRotation[0], i/ticksReq);
			float interpolatedPitch = Utils.getPrecentAverage(0, goalRotation[1], i/ticksReq);
			
			if(addNoise) {
	        	if(Math.round(interpolatedYaw) != Math.round(goalRotation[0])) {
	        		 interpolatedYaw += Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/2);
	        	}
	        	if(Math.round(interpolatedPitch) != Math.round(goalRotation[1])) {
	        	interpolatedPitch +=  Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/4);
	        	 }
	        }
			
			interpolatedYaw = Utils.normalizeAngle(interpolatedYaw);
	        interpolatedPitch = Utils.normalizeAngle(interpolatedPitch);
			
			angles.add(new float[] {interpolatedYaw, interpolatedPitch});
		}
		
		addToRotationQueue(angles);
	}
	
	public static void rotateCurveTo(float yaw, float pitch, float seconds, boolean addNoise) {
		float[] goalRotation = new float[] {Utils.normalizeAngle(yaw), Utils.normalizeAngle(pitch)};
		
		int ticksInSeconds = Minecraft.getMinecraft().getDebugFPS();
		float ticksReq = (float)ticksInSeconds * seconds;
		
		float[] controlPoint = new float[] {Utils.normalizeAngle(Utils.getRandomizedMinusOrPlus(((0 + goalRotation[0]) / 2) + goalRotation[1]/4)), Utils.normalizeAngle(((0 + goalRotation[1]) / 2) + goalRotation[0]/10)};
		
		List<float[]> angles = new ArrayList<>();
		for (float i = 0; i < ticksReq; i++) {
			float interpolatedYaw = Utils.bezier(0, controlPoint[0], goalRotation[0], i/ticksReq);
	        float interpolatedPitch = Utils.bezier(0, controlPoint[1], goalRotation[1], i/ticksReq);
	        
	        if(addNoise) {
	        	if(Math.round(interpolatedYaw) != Math.round(goalRotation[0])) {
	        		 interpolatedYaw += Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/2);
	        	}
	        	if(Math.round(interpolatedPitch) != Math.round(goalRotation[1])) {
	        	interpolatedPitch +=  Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/4);
	        	 }
	        }
	        
	        interpolatedYaw = Utils.normalizeAngle(interpolatedYaw);
	        interpolatedPitch = Utils.normalizeAngle(interpolatedPitch);
	        
	        angles.add(new float[] {interpolatedYaw, interpolatedPitch});
		}
		
		addToRotationQueue(angles);
	}
	
	public static void rotateBezierCurveTo(float yaw, float pitch, float controlPointYaw, float controlPointPitch, float seconds, boolean addNoise) {
		float[] goalRotation = new float[] {Utils.normalizeAngle(yaw), Utils.normalizeAngle(pitch)};
		
		int ticksInSeconds = Minecraft.getMinecraft().getDebugFPS();
		float ticksReq = (float)ticksInSeconds * seconds;
		
		float[] controlPoint = new float[] {Utils.normalizeAngle(controlPointYaw), Utils.normalizeAngle(controlPointPitch)};
		
		List<float[]> angles = new ArrayList<>();
		for (float i = 0; i < ticksReq; i++) {
			float interpolatedYaw = Utils.bezier(0, controlPoint[0], goalRotation[0], i/ticksReq);
	        float interpolatedPitch = Utils.bezier(0, controlPoint[1], goalRotation[1], i/ticksReq);
	        
	        if(addNoise) {
	        	if(Math.round(interpolatedYaw) != Math.round(goalRotation[0])) {
	        		 interpolatedYaw += Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/2);
	        	}
	        	if(Math.round(interpolatedPitch) != Math.round(goalRotation[1])) {
	        	interpolatedPitch +=  Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/4);
	        	 }
	        }
	        
	        interpolatedYaw = Utils.normalizeAngle(interpolatedYaw);
	        interpolatedPitch = Utils.normalizeAngle(interpolatedPitch);
	        
	        angles.add(new float[] {interpolatedYaw, interpolatedPitch});
		}
		
		addToRotationQueue(angles);
	}
	
	public static void rotateDoubleBezierCurveTo(float yaw, float pitch, float controlPointYaw1, float controlPointPitch1, float controlPointYaw2, float controlPointPitch2, float seconds, boolean addNoise) {
		float[] goalRotation = new float[] {Utils.normalizeAngle(yaw), Utils.normalizeAngle(pitch)};

	    int ticksInSeconds = Minecraft.getMinecraft().getDebugFPS();
	    float ticksReq = (float)ticksInSeconds * seconds;

	    float[] controlPoint1 = new float[] {Utils.normalizeAngle(controlPointYaw1), Utils.normalizeAngle(controlPointPitch1)};
	    float[] controlPoint2 = new float[] {Utils.normalizeAngle(controlPointYaw2), Utils.normalizeAngle(controlPointPitch2)};

	    List<float[]> angles = new ArrayList<>();
	    for (float i = 0; i <= ticksReq; i++) {
	        float t = i / ticksReq;
	        float interpolatedYaw = Utils.cubicBezier(0, controlPoint1[0], controlPoint2[0], goalRotation[0], t);
	        float interpolatedPitch = Utils.cubicBezier(0, controlPoint1[1], controlPoint2[1], goalRotation[1], t);

	        if(addNoise) {
	        	if(Math.round(interpolatedYaw) != Math.round(goalRotation[0])) {
	        		 interpolatedYaw += Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/2);
	        	}
	        	if(Math.round(interpolatedPitch) != Math.round(goalRotation[1])) {
	        	interpolatedPitch +=  Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/4);
	        	 }
	        }
	        
	        interpolatedYaw = Utils.normalizeAngle(interpolatedYaw);
	        interpolatedPitch = Utils.normalizeAngle(interpolatedPitch);
	        
	        angles.add(new float[] {interpolatedYaw, interpolatedPitch});
	    }

	    addToRotationQueue(angles);
	}
	
	public static void rotateCurveToWithControlableNoise(float yaw, float pitch, float seconds, float noiseLevel) {
		float[] goalRotation = new float[] {Utils.normalizeAngle(yaw), Utils.normalizeAngle(pitch)};
		
		int ticksInSeconds = Minecraft.getMinecraft().getDebugFPS();
		float ticksReq = (float)ticksInSeconds * seconds;
		
		float[] controlPoint = new float[] {Utils.normalizeAngle(Utils.getRandomizedMinusOrPlus(((0 + goalRotation[0]) / 2) + goalRotation[1]/4)), Utils.normalizeAngle(((0 + goalRotation[1]) / 2) + goalRotation[0]/10)};
		
		List<float[]> angles = new ArrayList<>();
		for (float i = 0; i < ticksReq; i++) {
			float interpolatedYaw = Utils.bezier(0, controlPoint[0], goalRotation[0], i/ticksReq);
	        float interpolatedPitch = Utils.bezier(0, controlPoint[1], goalRotation[1], i/ticksReq);
	        
	       
	        if(noiseLevel > 0) {
	        	if(Math.round(interpolatedYaw) != Math.round(goalRotation[0])) {
	        		 interpolatedYaw += Utils.getRandomizedMinusOrPlus(new Random().nextFloat() * noiseLevel);
	        	}
	        	if(Math.round(interpolatedPitch) != Math.round(goalRotation[1])) {
	        	interpolatedPitch +=  Utils.getRandomizedMinusOrPlus(new Random().nextFloat() * noiseLevel/2);
	        	 }
	        }
	        
	        interpolatedYaw = Utils.normalizeAngle(interpolatedYaw);
	        interpolatedPitch = Utils.normalizeAngle(interpolatedPitch);
	        
	        angles.add(new float[] {interpolatedYaw, interpolatedPitch});
		}
		
		addToRotationQueue(angles);
	}
	
	public static float getNeededYawFromMinecraftRotation(float yaw) {
		if(Minecraft.getMinecraft().thePlayer != null) {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			float neededYaw = -player.rotationYaw + yaw;
			
			return Utils.normalizeAngle(neededYaw);
		}
		return 0;
	}
	
	public static float getNeededPitchFromMinecraftRotation(float pitch) {
		if(Minecraft.getMinecraft().thePlayer != null) {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			float neededPitch = -player.rotationPitch + pitch;
			
			return neededPitch;
		}
		
		return 0;
	}
	
	public static float[] getPlayerRotationToLookAtEntity(Entity entity) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

		double deltaX = entity.posX - player.posX;
		double deltaY = entity.posY + (entity.getEyeHeight() / 2.0) - (player.posY + player.getEyeHeight());
		double deltaZ = entity.posZ - player.posZ;
		
		double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
	    float yaw = (float)(Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90;
	    float pitch = (float)(-Math.atan2(deltaY, distanceXZ) * (180 / Math.PI));
	    
	    return new float[]{yaw, pitch};
	}
	
	public static float[] getPlayerRotationToLookAtVector(Vec3 vector) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

		double deltaX = vector.xCoord - player.posX;
		double deltaY = vector.yCoord - (player.posY + player.getEyeHeight());
		double deltaZ = vector.zCoord - player.posZ;
		
		double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
	    float yaw = (float)(Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90;
	    float pitch = (float)(-Math.atan2(deltaY, distanceXZ) * (180 / Math.PI));
	    
	    return new float[]{yaw, pitch};
	}
	
	public static void addToRotationQueue(List<float[]> angles) {
		rotationQueue.add(angles);
	}
	
	public static void clearAllRotations() {
		rotationQueue.clear();
		currentFrame = -1;
	}
}
	
