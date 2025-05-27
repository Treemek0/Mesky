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
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.utils.RotationUtils;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.manager.CameraManager;

@SideOnly(Side.CLIENT)
public class Freelook {
	public static boolean cameraToggled = false;
    
    public static final KeyBinding KEY = new KeyBinding("Freelook", Keyboard.KEY_LMENU, "Mesky");
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent e) throws IllegalArgumentException, IllegalAccessException {
        boolean isPressed = Keyboard.getEventKeyState();
        int k = Keyboard.getEventKey();
        
    	if(isPressed) {
	    	if(k == KEY.getKeyCode() && SettingsConfig.FreeLookToogle.isOn && cameraToggled) {
	        	cameraToggled = false;
	        	CameraManager.lockCamera(false, null, null);
	        	RotationUtils.lockRotation(false, null, null);
	            Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
	            return;
	        }
	    	
	    	if(k == KEY.getKeyCode() && !cameraToggled && SettingsConfig.FreeLook.isOn){
	        	if(SettingsConfig.FreeRotate.isOn) {
		        	cameraToggled = true;
		            CameraManager.lockCamera(true, null, null);
		            Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
	        	}else {
	        		cameraToggled = true;
	        		RotationUtils.lockRotation(true, null, null);
	        		Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
	        	}
	        }
    	}else {
    		if(k == KEY.getKeyCode() && cameraToggled && !SettingsConfig.FreeLookToogle.isOn) {
	        	cameraToggled = false;
	        	CameraManager.lockCamera(false, null, null);
	        	RotationUtils.lockRotation(false, null, null);
	            Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
	            return;
    		}
    	}
    }
}
