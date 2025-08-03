package treemek.mesky.utils.manager;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.utils.Utils;

public class CameraManager {
	
    static double defaultDistance = 4;
	static CameraSetup camera;
	
	public static boolean lockCamera;
	private static float cameraYaw;
	private static float cameraPitch;
	
	private static float targetCameraYaw;
	private static float targetCameraPitch;
	
	@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
		if(lockCamera) {
			Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
			
			if(targetCameraYaw != cameraYaw) {
				cameraYaw += (targetCameraYaw - cameraYaw) / 5;
				
				if(Math.abs(targetCameraYaw - cameraYaw) < 0.2f) {
					cameraYaw = targetCameraYaw;
				}
			}
			
			if(targetCameraPitch != cameraPitch) {
				cameraPitch += (targetCameraPitch - cameraPitch) / 5;
				
				if(Math.abs(targetCameraPitch - cameraPitch) < 0.2f) {
					cameraPitch = targetCameraPitch;
				}
			}
		}
	}
	
	public static void lockCamera(boolean t, Float yaw, Float pitch) {
		if(yaw == null) {
			cameraYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
		}else {
			cameraYaw = yaw;
		}
		
		if(pitch == null) {
			cameraPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;
		}else {
			cameraPitch = pitch;
		}
		
		targetCameraYaw = cameraYaw;
		targetCameraPitch = cameraPitch;
		lockCamera = t;
	}
	
	public static void addRotation(float yaw, float pitch) {
		targetCameraYaw += yaw;
		targetCameraPitch += pitch;
		
		if(SettingsConfig.FreeLookClampAngles.isOn) {
			targetCameraPitch = MathHelper.clamp_float(targetCameraPitch, -90, 90);
		}
	}
	
	public static float getYaw() {
		return cameraYaw;
	}
    
	public static float getPitch() {
		return cameraPitch;
	}
}
