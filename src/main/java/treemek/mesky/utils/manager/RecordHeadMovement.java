package treemek.mesky.utils.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.google.gson.GsonBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.Mesky;
import treemek.mesky.utils.RotationUtils;

public class RecordHeadMovement {
	
	public static final KeyBinding HeadRecorder = new KeyBinding("HeadRecorder", Keyboard.KEY_F7, "Mesky");
	public static final KeyBinding HeadPlayer = new KeyBinding("HeadPlayer", Keyboard.KEY_F8, "Mesky");
	public boolean isRecording = false;
	public List<float[]> recordedAngles = new ArrayList<>();
	public float[] startedRotation;
	private int currentFrame = -1;
	float[] currentStartedRotation;
	private int tickCounter = 0;

	@SideOnly(Side.CLIENT)
    @SubscribeEvent
	public void onKey(InputEvent.KeyInputEvent e) throws InterruptedException {
		if (Minecraft.getMinecraft().thePlayer == null) return;
		
		if (HeadRecorder.isPressed() && !isRecording) {
			recordedAngles.clear();
			startedRotation = new float[] {Minecraft.getMinecraft().thePlayer.rotationYaw, Minecraft.getMinecraft().thePlayer.rotationPitch};
			isRecording = true;
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Started recording"));
		}
		
		if (HeadPlayer.isPressed()) {
			//RotationUtils.replayMovement(RotationUtils.readMovementFromFile(new File(Mesky.configDirectory + "/mesky/recordings/FishingRotation1.json")));
			RotationUtils.rotateCurveTo(0, -Minecraft.getMinecraft().thePlayer.rotationPitch + 90, 0.5f, true);
		}
	}
	
	@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e){
        if(!HeadRecorder.isKeyDown()) {
        	if(isRecording) {
        		isRecording = false;
        		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Stoped recording"));
        		saveToFile();
        	}
        }
    }

	@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
			if (event.phase == TickEvent.Phase.START) {
				if(Minecraft.getMinecraft().theWorld.isRemote) {
					if (isRecording) {
						recordedAngles.add(new float[] {Minecraft.getMinecraft().thePlayer.rotationYaw - startedRotation[0], Minecraft.getMinecraft().thePlayer.rotationPitch - startedRotation[1]});
					}
				}
			}
		}
	}
	
	private void turnOffWithDelay() {
		new Thread(() -> {
            try {
            	Thread.sleep(2000);
            	isRecording = false;
            	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Stopped recording"));
            	
            	saveToFile();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
	}

	
	private void saveToFile() {
		new File(Mesky.configDirectory + "/mesky/recordings/").mkdirs();
    	try (FileWriter writer = new FileWriter(Mesky.configDirectory + "/mesky/recordings/recordedHeadMovements" + System.currentTimeMillis() + ".json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(recordedAngles, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
}
