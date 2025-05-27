package treemek.mesky.features;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import treemek.mesky.Reference;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Utils;

public class MaskTimer extends GuiScreen {

	float BonzoCooldownSeconds = 0;
	boolean BonzoMaskActivated = false;
	float SpiritCooldownSeconds = 0;
	boolean SpiritMaskActivated = false;
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onChat(ClientChatReceivedEvent event) {
		
        String message = net.minecraft.util.StringUtils.stripControlCodes(event.message.getUnformattedText());
        if(Minecraft.getMinecraft().thePlayer == null) return;
        ItemStack mask = Minecraft.getMinecraft().thePlayer.getCurrentArmor(3);
        
        if(message.contains(":")) return; // only from server not players
        
		if(message.contains("Bonzo's Mask") && message.contains("saved your life!") && SettingsConfig.BonzoTimer.isOn) {
			if(mask != null) {
				String bonzoLore = StringUtils.join(Utils.getItemLore(mask), " ");
				String[] bonzoLoreSplit = bonzoLore.split(" ");
				for (int i = 0; i < bonzoLoreSplit.length; i++){
				    if (bonzoLoreSplit[i].contains("Cooldown:")) {
				    	String seconds = net.minecraft.util.StringUtils.stripControlCodes(bonzoLoreSplit[i+1].substring(0, bonzoLoreSplit[i+1].length() - 1));
				    	
				    	mc.thePlayer.addChatMessage(new ChatComponentText(seconds));
				    	try {
				    		BonzoCooldownSeconds = Integer.parseInt(seconds);
						} catch (Exception e) {
							mc.thePlayer.addChatMessage(new ChatComponentText(e.toString()));
						}
				    	
				    	BonzoMaskActivated = true;
				    	Alerts.DisplayCustomAlert("Bonzo Mask", 1000, 3, new Float[] {50f,50f}, 4, new ResourceLocation("minecraft", "random.anvil_land"), 1);
				    	return;
				    }
				}
			}
		}
		
		
		if(message.contains("Your Spirit Mask saved your life!") && SettingsConfig.SpiritTimer.isOn) {
			if (mask != null) {
				SpiritCooldownSeconds = 30;
				SpiritMaskActivated = true;
				Alerts.DisplayCustomAlert("Spirit Mask", 1000, 3, new Float[] {50f,50f}, 4, new ResourceLocation("minecraft", "random.anvil_land"), 1);
			}else {
				Utils.writeError("Somehow you had activated Spirit Mask ability without having it on");
			}
		}
	}
	
	@SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.START) {
			if(SpiritCooldownSeconds > 0) {
				SpiritCooldownSeconds -= 0.05;
			}else {
				SpiritMaskActivated = false;
			}
			if(BonzoCooldownSeconds > 0) {
				BonzoCooldownSeconds -= 0.05;
			}else {
				BonzoMaskActivated = false;
			}
		}
	}
	
	@SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (BonzoMaskActivated) {
            renderBonzoMaskTimer(BonzoCooldownSeconds);
        }
        if (SpiritMaskActivated) {
            renderSpiritMaskTimer(SpiritCooldownSeconds);
        }
    }
	
	
	
	public static void renderBonzoMaskTimer(Float text) {
		ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
		
		String cooldownTimer = Math.round(text) + "s";
		
		// Calculate the position to render the timer
		Float scale = (float) (SettingsConfig.BonzoTimer.scale * RenderHandler.getResolutionScale());
		float x = resolution.getScaledWidth() * (SettingsConfig.BonzoTimer.position[0]/100);
        float y = resolution.getScaledHeight() * (SettingsConfig.BonzoTimer.position[1]/100);
        
        float textY = y + ((8.5f*scale) - (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * scale)/2);
        RenderHandler.drawText(cooldownTimer, x + (17*scale), textY, scale, true, 0xFFFFFF);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Reference.MODID, "textures/Bonzo_Head.png"));
        drawModalRectWithCustomSizedTexture((int)x, (int)y, 0, 0, (int)(17*scale), (int)(17*scale), (int)(17*scale), (int)(17*scale));
	}
	
	public static void renderSpiritMaskTimer(Float text) {
		ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
		
		String cooldownTimer = Math.round(text) + "s";
		
		// Calculate the position to render the timer
		Float scale = (float) (SettingsConfig.SpiritTimer.scale * RenderHandler.getResolutionScale());
		float x = resolution.getScaledWidth() * (SettingsConfig.SpiritTimer.position[0]/100);
        float y = resolution.getScaledHeight() * (SettingsConfig.SpiritTimer.position[1]/100);
		
        float textY = y + ((8.5f*scale) - (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * scale)/2);
        RenderHandler.drawText(cooldownTimer, x + (17*scale), textY, scale, true, 0xFFFFFF);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Reference.MODID, "textures/Spirit_Mask.png"));
        drawModalRectWithCustomSizedTexture((int)x, (int)y, 0, 0, (int)(17*scale), (int)(17*scale), (int)(17*scale), (int)(17*scale));
	}
}
