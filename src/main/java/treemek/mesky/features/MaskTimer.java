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
import treemek.mesky.config.GuiLocationConfig;
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
        ItemStack mask = Minecraft.getMinecraft().thePlayer.getCurrentArmor(3);
        
        if(message.contains(":")) return; // only from server not players
        
		if(message.contains("Bonzo's Mask") && message.contains("saved your life!") && SettingsConfig.BonzoTimer) {
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
				    	Alerts.DisplayCustomAlerts("Bonzo Mask", 1000, new int[] {50,50}, 4);
				    	return;
				    }
				}
			}
		}
		
		
		if(message.contains("Your Spirit Mask saved your life!") && SettingsConfig.SpiritTimer) {
			if (mask != null) {
				SpiritCooldownSeconds = 30;
				SpiritMaskActivated = true;
				Alerts.DisplayCustomAlerts("Spirit Mask", 1000, new int[] {50,50}, 4);
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
            renderBonzoMaskTimer(event.resolution, event.partialTicks);
        }
        if (SpiritMaskActivated) {
            renderSpiritMaskTimer(event.resolution, event.partialTicks);
        }
    }
	
	
	
	private void renderBonzoMaskTimer(ScaledResolution resolution, float partialTicks) {
		String cooldownTimer = Math.round(BonzoCooldownSeconds) + "s";
		
		// Calculate the position to render the timer
		float x = resolution.getScaledWidth() * (GuiLocationConfig.bonzoMaskTimer[0]/100);
        float y = resolution.getScaledHeight() * (GuiLocationConfig.bonzoMaskTimer[1]/100);
		
        RenderHandler.drawText(cooldownTimer, x + 17, y, 1, false, 0xFFFFFF);
        
		ResourceLocation textureLocation = new ResourceLocation(Reference.MODID, "textures/Bonzo_Head.png");
        Minecraft.getMinecraft().renderEngine.bindTexture(textureLocation);
        drawModalRectWithCustomSizedTexture((int)x, (int)y - 5, 0, 0, 17, 17, 17, 17);
        
        
	}
	
	private void renderSpiritMaskTimer(ScaledResolution resolution, float partialTicks) {
		String cooldownTimer = Math.round(SpiritCooldownSeconds) + "s";
		
		// Calculate the position to render the timer
		float x = resolution.getScaledWidth() * (GuiLocationConfig.spiritMaskTimer[0]/100);
        float y = resolution.getScaledHeight() * (GuiLocationConfig.spiritMaskTimer[1]/100);
		
		
        RenderHandler.drawText(cooldownTimer, x + 17, y, 1, false, 0xFFFFFF);
        
		ResourceLocation textureLocation = new ResourceLocation(Reference.MODID, "textures/Spirit_Mask.png");
        Minecraft.getMinecraft().renderEngine.bindTexture(textureLocation);
        drawModalRectWithCustomSizedTexture((int)x, (int)y - 5, 0, 0, 17, 17, 17, 17);
        
        
	}
}
