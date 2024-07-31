package treemek.mesky.utils.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CameraManager {
	
    static double defaultDistance = 4;
	static CameraSetup camera;
	
	static boolean lockCamera;
	private static float cameraYaw;
	private static float cameraPitch;
	
	@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void cameraSetup(EntityViewRenderEvent.CameraSetup e) throws IllegalArgumentException, IllegalAccessException {
    	camera = e;
    	
    	if(lockCamera) {
    		e.yaw = cameraYaw;
            e.pitch = cameraPitch;
            changeCameraCollisionsToCameraRotation();
    	}
    }
	
	public static void lockCamera(boolean t, Float yaw, Float pitch) {
		if(yaw == null) {
			cameraYaw = Minecraft.getMinecraft().thePlayer.rotationYaw + 180;
		}else {
			cameraYaw = yaw;
		}
		
		if(pitch == null) {
			cameraPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;
		}else {
			cameraPitch = pitch;
		}
		lockCamera = t;
	}
	
	public static float getYaw() {
		return camera.yaw;
	}
    
	public static float getPitch() {
		return camera.pitch;
	}
	
    public static double getCameraDistance() {
    	if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) return 0;
    	
    	Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
    	float f = entity.getEyeHeight();
    	double d0 = entity.posX;
        double d1 = entity.posY + (double)f;
        double d2 = entity.posZ;
    	
        double d3 = defaultDistance;
        
        float f1 = Minecraft.getMinecraft().thePlayer.rotationYaw;
        float f2 = Minecraft.getMinecraft().thePlayer.rotationPitch;

        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2)
        {
            f2 += 180.0F;
        }

        double d4 = (double)(-MathHelper.sin(f1 / 180.0F * (float)Math.PI) * MathHelper.cos(f2 / 180.0F * (float)Math.PI)) * d3;
        double d5 = (double)(MathHelper.cos(f1 / 180.0F * (float)Math.PI) * MathHelper.cos(f2 / 180.0F * (float)Math.PI)) * d3;
        double d6 = (double)(-MathHelper.sin(f2 / 180.0F * (float)Math.PI)) * d3;

        for (int i = 0; i < 8; ++i)
        {
            float f3 = (float)((i & 1) * 2 - 1);
            float f4 = (float)((i >> 1 & 1) * 2 - 1);
            float f5 = (float)((i >> 2 & 1) * 2 - 1);
            f3 = f3 * 0.1F;
            f4 = f4 * 0.1F;
            f5 = f5 * 0.1F;
            MovingObjectPosition movingobjectposition = Minecraft.getMinecraft().theWorld.rayTraceBlocks(new Vec3(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), new Vec3(d0 - d4 + (double)f3 + (double)f5, d1 - d6 + (double)f4, d2 - d5 + (double)f5));

            if (movingobjectposition != null)
            {
                double d7 = movingobjectposition.hitVec.distanceTo(new Vec3(d0, d1, d2));

                if (d7 < d3)
                {
                    d3 = d7;
                }
            }
        }
        
    	
        
        return d3;
    }
    
    public static double getCameraDistanceUsingCameraRotation() {
    	if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) return 0;
    	
    	Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
    	float f = entity.getEyeHeight();
    	double d0 = entity.posX;
        double d1 = entity.posY + (double)f;
        double d2 = entity.posZ;
    	
        double d3 = defaultDistance;
        
        float f1 = camera.yaw + 180;
        float f2 = camera.pitch;

        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2)
        {
            f2 += 180.0F;
        }

        double d4 = (double)(-MathHelper.sin(f1 / 180.0F * (float)Math.PI) * MathHelper.cos(f2 / 180.0F * (float)Math.PI)) * d3;
        double d5 = (double)(MathHelper.cos(f1 / 180.0F * (float)Math.PI) * MathHelper.cos(f2 / 180.0F * (float)Math.PI)) * d3;
        double d6 = (double)(-MathHelper.sin(f2 / 180.0F * (float)Math.PI)) * d3;

        for (int i = 0; i < 8; ++i)
        {
            float f3 = (float)((i & 1) * 2 - 1);
            float f4 = (float)((i >> 1 & 1) * 2 - 1);
            float f5 = (float)((i >> 2 & 1) * 2 - 1);
            f3 = f3 * 0.1F;
            f4 = f4 * 0.1F;
            f5 = f5 * 0.1F;
            MovingObjectPosition movingobjectposition = Minecraft.getMinecraft().theWorld.rayTraceBlocks(new Vec3(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), new Vec3(d0 - d4 + (double)f3 + (double)f5, d1 - d6 + (double)f4, d2 - d5 + (double)f5));

            if (movingobjectposition != null)
            {
                double d7 = movingobjectposition.hitVec.distanceTo(new Vec3(d0, d1, d2));

                if (d7 < d3)
                {
                    d3 = d7;
                }
            }
        }

        return d3;
    }
    
    public void changeCameraCollisionsToCameraRotation() {
    	double d3 = getCameraDistance();
    	
    	// coming back to 0 translation
    	if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) d3 *= -1;
        GlStateManager.translate(0.0F, 0.0F, d3);
        System.out.println(d3);
        
        // translating to camera colission
        double d4 = getCameraDistanceUsingCameraRotation();
        if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) d4 *= -1;
        GlStateManager.translate(0.0F, 0.0F, -d4);
        System.out.println(-d4);
    }
}
