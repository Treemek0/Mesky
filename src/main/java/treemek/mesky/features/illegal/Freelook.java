package treemek.mesky.features.illegal;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Freelook {
	public static boolean cameraToggled = false;
    public static float cameraYaw;
    public static float cameraPitch;
	
    public static final KeyBinding KEY = new KeyBinding("Freelook", Keyboard.KEY_LMENU, "Mesky");
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent e) {
        if(Keyboard.isKeyDown(KEY.getKeyCode()) && !cameraToggled){
            cameraYaw = Minecraft.getMinecraft().thePlayer.rotationYaw + 180;
            cameraPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;
            cameraToggled = true;
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
    public void cameraSetup(EntityViewRenderEvent.CameraSetup e) {
        if (cameraToggled) {
            e.yaw = cameraYaw;
            e.pitch = cameraPitch;
        }
    }
}
