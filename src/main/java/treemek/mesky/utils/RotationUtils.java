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
import net.minecraft.util.Vec3i;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.features.illegal.Freelook;
import treemek.mesky.utils.MovementUtils.Task;
import treemek.mesky.utils.manager.CameraManager;

public class RotationUtils {
	private static List<float[]> recordedAngles;
	private static int currentFrame = -1;
	private static float[] currentStartedRotation;
	private int tickCounter = 0;
	private static Queue<Rotation> rotationQueue = new LinkedList<>();
	
	private static float lockedRotationYaw;
	private static float lockedRotationPitch;
	private static boolean lockRotation = false;
	
	private static class Rotation {
		public List<float[]> list;
		public Task task;
		
		private Rotation(List<float[]> list) {
			this.list = list;
		}
		
		private Rotation(Task task) {
			this.task = task;
		}
	}
	
	public static boolean isListEmpty() {
		return rotationQueue.isEmpty();
	}
	
	public static boolean isPlayerRotating() {
		return !rotationQueue.isEmpty() || currentFrame != -1;
	}
	
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
	
	public static void addTask(Task task) {
		rotationQueue.add(new Rotation(task));
	}
	
	private static void replayMovement(List<float[]> movement) {
		currentStartedRotation = new float[] {Utils.normalizeAngle(Minecraft.getMinecraft().thePlayer.rotationYaw), Utils.clampPitch(Minecraft.getMinecraft().thePlayer.rotationPitch)};
		recordedAngles = movement;
		currentFrame = 0;
	};
	
	public static void skipCycle() {
		currentFrame = -1;
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderPlayer(RenderPlayerEvent.Post event) {
		if(lockRotation && event.entity.equals(Minecraft.getMinecraft().thePlayer)) { // its because only here isnt always working, and only renderPlayer doesnt work when in first person
			event.entity.rotationYaw = lockedRotationYaw;
			event.entity.rotationPitch = lockedRotationPitch;
			
			event.entity.prevRotationYaw = lockedRotationYaw;
			event.entity.prevRotationPitch = lockedRotationPitch;
		}
	}

	@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			
			if (event.phase == TickEvent.Phase.START) {	
				if(Minecraft.getMinecraft().theWorld.isRemote) {
					if (currentFrame >= 0 && !recordedAngles.isEmpty()) {
						float[] current = recordedAngles.get(currentFrame);
						
						lockedRotationYaw = currentStartedRotation[0] + current[0];
						lockedRotationPitch = currentStartedRotation[1] + current[1];
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
		
			if(!rotationQueue.isEmpty()) {
				if(currentFrame == -1) {
					Rotation rotation = rotationQueue.poll();
					if(rotation.list != null) {
						replayMovement(rotation.list);
					}
					
					if(rotation.task != null) {
						rotation.task.execute();
					}
				}
			}
		}
	}
	
	public static void addToRotation(float yaw, float pitch) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		lockedRotationYaw = player.rotationYaw + yaw;
		lockedRotationPitch = player.rotationPitch + pitch;
		player.rotationYaw = player.rotationYaw + yaw;
		player.rotationPitch = player.rotationPitch + pitch;
	}
	
	public static void rotateStraight(float yaw, float pitch, float seconds, boolean addNoise) {
		float[] goalRotation = new float[] {Utils.normalizeAngle(yaw), Utils.clampPitch(pitch)};
		
		int ticksInSeconds = Minecraft.getMinecraft().getDebugFPS();
		float ticksReq = (float)ticksInSeconds * seconds;
		
		List<float[]> angles = new ArrayList<>();
		for (float i = 0; i < ticksReq; i++) {
			float interpolatedYaw = Utils.getPrecentAverage(0, goalRotation[0], i/ticksReq);
			float interpolatedPitch = Utils.getPrecentAverage(0, goalRotation[1], i/ticksReq);
			
			float oneBeforeYaw = (angles.size()>1)?angles.get(angles.size()-1)[0]:interpolatedYaw;
	        float oneBeforePitch = (angles.size()>1)?angles.get(angles.size()-1)[1]:interpolatedPitch;
			
			if(addNoise) {
				if(Math.round(interpolatedYaw) != Math.round(goalRotation[0]) && Math.round(interpolatedYaw) != Math.round(oneBeforeYaw)) {
	        		 interpolatedYaw += Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/2);
	        	}
	        	if(Math.round(interpolatedPitch) != Math.round(goalRotation[1]) && Math.round(interpolatedPitch) != Math.round(oneBeforePitch)) {
	        		interpolatedPitch +=  Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/4);
	        	 }
	        }
			
			interpolatedYaw = Utils.normalizeAngle(interpolatedYaw);
	        interpolatedPitch = Utils.clampPitch(interpolatedPitch);
			
			angles.add(new float[] {interpolatedYaw, interpolatedPitch});
		}
		
		angles.add(goalRotation);
		
		addToRotationQueue(angles);
	}
	
	public static void rotateCurve(float yaw, float pitch, float seconds, boolean addNoise) {
		float[] goalRotation = new float[] {Utils.normalizeAngle(yaw), Utils.clampPitch(pitch)};
		
		int ticksInSeconds = Minecraft.getMinecraft().getDebugFPS();
		float ticksReq = (float)ticksInSeconds * seconds;
		
		float[] controlPoint = new float[] {Utils.normalizeAngle(((0 + goalRotation[0]) / 2) + Utils.getRandomizedMinusOrPlus(goalRotation[1]/6)), Utils.clampPitch(((0 + goalRotation[1]) / 2) + Utils.getRandomizedMinusOrPlus(goalRotation[0]/12))};
		
		List<float[]> angles = new ArrayList<>();
		for (float i = 0; i < ticksReq; i++) {
			float interpolatedYaw = Utils.bezier(0, controlPoint[0], goalRotation[0], i/ticksReq);
	        float interpolatedPitch = Utils.bezier(0, controlPoint[1], goalRotation[1], i/ticksReq);
	        
	        float oneBeforeYaw = (angles.size()>1)?angles.get(angles.size()-1)[0]:interpolatedYaw;
	        float oneBeforePitch = (angles.size()>1)?angles.get(angles.size()-1)[1]:interpolatedPitch;
	        
	        if(addNoise) {
	        	if(Math.round(interpolatedYaw) != Math.round(goalRotation[0]) && Math.round(interpolatedYaw) != Math.round(oneBeforeYaw)) {
	        		 interpolatedYaw += Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/2);
	        	}
	        	if(Math.round(interpolatedPitch) != Math.round(goalRotation[1]) && Math.round(interpolatedPitch) != Math.round(oneBeforePitch)) {
	        		interpolatedPitch +=  Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/4);
	        	 }
	        }
	        
	        interpolatedYaw = Utils.normalizeAngle(interpolatedYaw);
	        interpolatedPitch = Utils.clampPitch(interpolatedPitch);
	        
	        angles.add(new float[] {interpolatedYaw, interpolatedPitch});
		}
		
		angles.add(goalRotation);
		
		addToRotationQueue(angles);
	}
	
	public static void rotateBezierCurve(float yaw, float pitch, float controlPointYaw, float controlPointPitch, float seconds, boolean addNoise) {
		float[] goalRotation = new float[] {Utils.normalizeAngle(yaw), Utils.clampPitch(pitch)};
		
		int ticksInSeconds = Minecraft.getMinecraft().getDebugFPS();
		float ticksReq = (float)ticksInSeconds * seconds;
		
		float[] controlPoint = new float[] {Utils.normalizeAngle(controlPointYaw), Utils.clampPitch(controlPointPitch)};
		
		List<float[]> angles = new ArrayList<>();
		for (float i = 0; i < ticksReq; i++) {
			float interpolatedYaw = Utils.bezier(0, controlPoint[0], goalRotation[0], i/ticksReq);
	        float interpolatedPitch = Utils.bezier(0, controlPoint[1], goalRotation[1], i/ticksReq);
	        
	        float oneBeforeYaw = (angles.size()>1)?angles.get(angles.size()-1)[0]:interpolatedYaw;
	        float oneBeforePitch = (angles.size()>1)?angles.get(angles.size()-1)[1]:interpolatedPitch;
	        
	        if(addNoise) {
	        	if(Math.round(interpolatedYaw) != Math.round(goalRotation[0]) && Math.round(interpolatedYaw) != Math.round(oneBeforeYaw)) {
	        		 interpolatedYaw += Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/2);
	        	}
	        	if(Math.round(interpolatedPitch) != Math.round(goalRotation[1]) && Math.round(interpolatedPitch) != Math.round(oneBeforePitch)) {
	        		interpolatedPitch +=  Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/4);
	        	 }
	        }
	        
	        interpolatedYaw = Utils.normalizeAngle(interpolatedYaw);
	        interpolatedPitch = Utils.clampPitch(interpolatedPitch);
	        
	        angles.add(new float[] {interpolatedYaw, interpolatedPitch});
		}
		
		angles.add(goalRotation);
		
		addToRotationQueue(angles);
	}
	
	public static void rotateDoubleBezierCurve(float yaw, float pitch, float controlPointYaw1, float controlPointPitch1, float controlPointYaw2, float controlPointPitch2, float seconds, boolean addNoise) {
		float[] goalRotation = new float[] {Utils.normalizeAngle(yaw), Utils.clampPitch(pitch)};

	    int ticksInSeconds = Minecraft.getMinecraft().getDebugFPS();
	    float ticksReq = (float)ticksInSeconds * seconds;

	    float[] controlPoint1 = new float[] {Utils.normalizeAngle(controlPointYaw1), Utils.clampPitch(controlPointPitch1)};
	    float[] controlPoint2 = new float[] {Utils.normalizeAngle(controlPointYaw2), Utils.clampPitch(controlPointPitch2)};

	    List<float[]> angles = new ArrayList<>();
	    for (float i = 0; i <= ticksReq; i++) {
	        float t = i / ticksReq;
	        float interpolatedYaw = Utils.cubicBezier(0, controlPoint1[0], controlPoint2[0], goalRotation[0], t);
	        float interpolatedPitch = Utils.cubicBezier(0, controlPoint1[1], controlPoint2[1], goalRotation[1], t);

	        float oneBeforeYaw = (angles.size()>1)?angles.get(angles.size()-1)[0]:interpolatedYaw;
	        float oneBeforePitch = (angles.size()>1)?angles.get(angles.size()-1)[1]:interpolatedPitch;
	        
	        if(addNoise) {
	        	if(Math.round(interpolatedYaw) != Math.round(goalRotation[0]) && Math.round(interpolatedYaw) != Math.round(oneBeforeYaw)) {
	        		 interpolatedYaw += Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/2);
	        	}
	        	if(Math.round(interpolatedPitch) != Math.round(goalRotation[1]) && Math.round(interpolatedPitch) != Math.round(oneBeforePitch)) {
	        		interpolatedPitch +=  Utils.getRandomizedMinusOrPlus(new Random().nextFloat()/4);
	        	 }
	        }
	        
	        interpolatedYaw = Utils.normalizeAngle(interpolatedYaw);
	        interpolatedPitch = Utils.clampPitch(interpolatedPitch);
	        
	        angles.add(new float[] {interpolatedYaw, interpolatedPitch});
	    }

	    angles.add(goalRotation);
	    
	    addToRotationQueue(angles);
	}
	
	public static void rotateCurveToWithControlableNoise(float yaw, float pitch, float seconds, float noiseLevel) {
		float[] goalRotation = new float[] {Utils.normalizeAngle(yaw), Utils.clampPitch(pitch)};
		
		int ticksInSeconds = Minecraft.getMinecraft().getDebugFPS();
		float ticksReq = (float)ticksInSeconds * seconds;
		
		
		float[] controlPoint = new float[] {Utils.normalizeAngle(((0 + goalRotation[0]) / 2) + Utils.getRandomizedMinusOrPlus(goalRotation[1]/6)), Utils.clampPitch(((0 + goalRotation[1]) / 2) + Utils.getRandomizedMinusOrPlus(goalRotation[0]/12))};
		
		List<float[]> angles = new ArrayList<>();
		for (float i = 0; i < ticksReq; i++) {
			float interpolatedYaw = Utils.bezier(0, controlPoint[0], goalRotation[0], i/ticksReq);
	        float interpolatedPitch = Utils.bezier(0, controlPoint[1], goalRotation[1], i/ticksReq);

	        float oneBeforeYaw = (angles.size()>1)?angles.get(angles.size()-1)[0]:interpolatedYaw;
	        float oneBeforePitch = (angles.size()>1)?angles.get(angles.size()-1)[1]:interpolatedPitch;
	        
	        if(noiseLevel > 0) {
	        	if(Math.round(interpolatedYaw) != Math.round(goalRotation[0]) && Math.round(interpolatedYaw) != Math.round(oneBeforeYaw)) {
	        		 interpolatedYaw += Utils.getRandomizedMinusOrPlus(new Random().nextFloat() * noiseLevel);
	        	}
	        	if(Math.round(interpolatedPitch) != Math.round(goalRotation[1]) && Math.round(interpolatedPitch) != Math.round(oneBeforePitch)) {
	        	interpolatedPitch +=  Utils.getRandomizedMinusOrPlus(new Random().nextFloat() * noiseLevel/2);
	        	 }
	        }
	        
	        interpolatedYaw = Utils.normalizeAngle(interpolatedYaw);
	        interpolatedPitch = Utils.clampPitch(interpolatedPitch);
	        
	        angles.add(new float[] {interpolatedYaw, interpolatedPitch});
		}
		
		angles.add(goalRotation);
		
		addToRotationQueue(angles);
	}
	
	public static float getNeededYawFromMinecraftRotation(float yaw) {
		if(Minecraft.getMinecraft().thePlayer != null) {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			float neededYaw = yaw -player.rotationYaw;
			
			return Utils.normalizeAngle(neededYaw);
		}
		return 0;
	}
	
	public static float getNeededPitchFromMinecraftRotation(float pitch) {
		if(Minecraft.getMinecraft().thePlayer != null) {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			float neededPitch = -player.rotationPitch + pitch;
			
			return Utils.clampPitch(neededPitch);
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
	
	public static float[] getPlayerRotationToLookAtVector(Vec3i vector) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

		double deltaX = vector.getX() - player.posX;
		double deltaY = vector.getY() - (player.posY + player.getEyeHeight());
		double deltaZ = vector.getZ() - player.posZ;
		
		double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
	    float yaw = (float)(Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90;
	    float pitch = (float)(-Math.atan2(deltaY, distanceXZ) * (180 / Math.PI));
	    
	    return new float[]{yaw, pitch};
	}
	
	public static float[] getPlayerRotationToLookAtBlockCenter(Vec3i vector) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

		double deltaX = vector.getX() + 0.5f - player.posX;
		double deltaY = vector.getY() - (player.posY + player.getEyeHeight());
		double deltaZ = vector.getZ() + 0.5f - player.posZ;
		
		double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
	    float yaw = (float)(Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90;
	    float pitch = (float)(-Math.atan2(deltaY, distanceXZ) * (180 / Math.PI));
	    
	    return new float[]{yaw, pitch};
	}
	
	public static float[] getPlayerRotationToLookAtVector(double x, double y, double z) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

		double deltaX = x - player.posX;
		double deltaY = y - (player.posY + player.getEyeHeight());
		double deltaZ = z - player.posZ;
		
		double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
	    float yaw = (float)(Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90;
	    float pitch = (float)(-Math.atan2(deltaY, distanceXZ) * (180 / Math.PI));
	    
	    return new float[]{yaw, pitch};
	}
	
	public static float[] getPlayerRotationToLookAtVectorWithCrouching(double x, double y, double z) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

		double deltaX = x - player.posX;
		double deltaY = y - (player.posY + 1.52);
		double deltaZ = z - player.posZ;
		
		double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
	    float yaw = (float)(Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90;
	    float pitch = (float)(-Math.atan2(deltaY, distanceXZ) * (180 / Math.PI));
	    
	    return new float[]{yaw, pitch};
	}
	
	public static void addToRotationQueue(List<float[]> angles) {
		rotationQueue.add(new Rotation(angles));
	}
	
	public static void lockRotation(boolean b, Float yaw, Float pitch) {
		if(yaw == null) {
			lockedRotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
		}else {
			lockedRotationYaw = yaw;
		}
		
		if(pitch == null) {
			lockedRotationPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;
		}else {
			lockedRotationPitch = pitch;
		}
		
		CameraManager.lockCamera(b, null, null);
		
		lockRotation = b;
	}
	
	
	// stoping rotations if player movement from mouse
	@SubscribeEvent
    public void onMouseMoved(MouseEvent event) {
		if(currentFrame != -1 && !CameraManager.lockCamera) {
			if (event.dx != 0 || event.dy != 0) {
				if(MacroWaypoints.MacroActive != null && MacroWaypoints.MacroActive.boundingBox.intersectsWith(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox())) return;
				clearAllRotations();
				Utils.debug("cleared rotation " + MacroWaypoints.MacroActive);
			}
		}
		
		if(lockRotation) {
	        if (event.dx != 0 || event.dy != 0) {
	            float sensitivity = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
	            float scaledSensitivity = sensitivity * sensitivity * sensitivity * 8.0F;

	            float mouseXMovement = event.dx * scaledSensitivity * 0.15F;
	            float mouseYMovement = event.dy * scaledSensitivity * 0.15F;
				
				CameraManager.addRotation(mouseXMovement, mouseYMovement);
	        }
		}
    }
	
	@SubscribeEvent
    public void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
		if(lockRotation && event.entityPlayer == Minecraft.getMinecraft().thePlayer) {
			Minecraft.getMinecraft().thePlayer.rotationYaw = lockedRotationYaw;
			Minecraft.getMinecraft().thePlayer.rotationPitch = lockedRotationPitch;
		}
	}
	
	public static void clearAllRotations() {
		currentFrame = -1;
		rotationQueue.clear();
	}
}
	
