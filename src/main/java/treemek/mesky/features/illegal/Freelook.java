package treemek.mesky.features.illegal;

import java.lang.reflect.Field;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.config.SettingsConfig;

@SideOnly(Side.CLIENT)
public class Freelook {
	public static boolean cameraToggled = false;
    public static float cameraYaw;
    public static float cameraPitch;
	
    double defaultDistance = 4;
    
    double cameraDistance;
    
    public static final KeyBinding KEY = new KeyBinding("Freelook", Keyboard.KEY_LMENU, "Mesky");

    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent e) throws IllegalArgumentException, IllegalAccessException {
        if(KEY != null && Keyboard.isKeyDown(KEY.getKeyCode()) && !cameraToggled && SettingsConfig.FreeLook.isOn){
            cameraYaw = Minecraft.getMinecraft().thePlayer.rotationYaw + 180;
            cameraPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;
            cameraToggled = true;
            cameraDistance = getCameraDistance();
            Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e){
        if(!Keyboard.isKeyDown(Keyboard.KEY_LMENU) && cameraToggled) {
            cameraToggled = false;
            Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void cameraSetup(EntityViewRenderEvent.CameraSetup e) throws IllegalArgumentException, IllegalAccessException {
        if (cameraToggled) {
            e.yaw = cameraYaw;
            e.pitch = cameraPitch;
            setUpCameraDistance(cameraDistance, e.renderPartialTicks);
        }
    }
    
    public double getCameraDistance() {
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
    
    public void setUpCameraDistance(double cameraDistance, double renderPartialTicks) {
    	double d3 = getCameraDistance();
    	
    	
        GlStateManager.translate(0.0F, 0.0F, (float)(d3-cameraDistance));
    }
}
