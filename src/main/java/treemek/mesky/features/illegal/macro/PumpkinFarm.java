package treemek.mesky.features.illegal.macro;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import treemek.mesky.config.SettingsConfig;

public class PumpkinFarm {

	public static final KeyBinding L_KEY = new KeyBinding("macro L", 203, "Mesky");
	public static final KeyBinding R_KEY = new KeyBinding("macro R", 205, "Mesky");
	
	boolean rightMacro = false;
	boolean leftMacro = false;
	
	int forward = Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode();
	int right = Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode();
	int left = Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode();
	int leftClick = Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode();
	
	
	
	@SubscribeEvent
	public void onKeyPress(InputEvent.KeyInputEvent event) {
		
       if(L_KEY.isPressed()) {
    	   if(leftMacro) TurnOff();
    	   leftMacro = (leftMacro)?false:true;
    	   rightMacro = false;
       }
       if(R_KEY.isPressed()) {
    	   if(rightMacro) TurnOff();
    	   rightMacro = (rightMacro)?false:true;
    	   leftMacro = false;
       }
    }
	
	@SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
		if(leftMacro) {
			LeftSide();
		}else if(rightMacro) {
			RightSide();
		}
	}
	
	private void TurnOff(){
		KeyBinding.setKeyBindState(right, false);
		KeyBinding.setKeyBindState(forward, false);
		KeyBinding.setKeyBindState(left, false);
		KeyBinding.setKeyBindState(leftClick, false);
	}
	
	
	private void RightSide() {
	    KeyBinding.setKeyBindState(right, true);
		KeyBinding.setKeyBindState(forward, true);
		KeyBinding.setKeyBindState(left, false);
		KeyBinding.setKeyBindState(leftClick, true);
	}

	private void LeftSide() {
	    KeyBinding.setKeyBindState(left, true);
		KeyBinding.setKeyBindState(forward, true);
		KeyBinding.setKeyBindState(right, false);
		KeyBinding.setKeyBindState(leftClick, true);
	}

	
}
