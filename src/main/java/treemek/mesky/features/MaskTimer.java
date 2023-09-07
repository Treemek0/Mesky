package treemek.mesky.features;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.Utils;

public class MaskTimer extends GuiScreen {

	float BonzoCooldownSeconds = 0;
	boolean BonzoMaskActivated = false;
	float SpiritCooldownSeconds = 0;
	boolean SpiritMaskActivated = false;
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onChat(ClientChatReceivedEvent event) {
		
        String message = event.message.getUnformattedText();
        ItemStack mask = Minecraft.getMinecraft().thePlayer.getCurrentArmor(3);
        
        if(message.contains(":")) return; // only from server not players
        
		if(message.contains("Bonzo's Mask") && message.contains("saved your life!")) {
			if (mask != null) {
				String bonzoLore = StringUtils.join(Utils.getItemLore(mask), " ");
				
					String bonzoLoreSplit [] = bonzoLore.split(" ");
					for (int i = bonzoLoreSplit.length; i == 0; i--){
					    if (bonzoLoreSplit[i].equalsIgnoreCase("Cooldown:")) {
					       BonzoCooldownSeconds = Integer.parseInt(bonzoLoreSplit[i-1]);
					       BonzoMaskActivated = true;
					       break;
					}
					
                }
			}
		}
		
		if(message.contains("Your Spirit Mask saved your life!")) {
			System.out.println(mask.getDisplayName());
			if (mask != null) {
				SpiritCooldownSeconds = 30;
				SpiritMaskActivated = true;
			}
		}
	}
	
	@SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
		if(SpiritCooldownSeconds > 0) {
			SpiritCooldownSeconds -= 0.05;
		}
		if(BonzoCooldownSeconds > 0) {
			BonzoCooldownSeconds -= 0.05;
		}
	}
	
	@SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (BonzoMaskActivated) {
            renderBonzoMaskTimer(event.resolution, event.partialTicks);
            if(BonzoCooldownSeconds <= 0) {
            	BonzoMaskActivated = false;
            }
        }
        if (SpiritMaskActivated) {
            renderSpiritMaskTimer(event.resolution, event.partialTicks);
            if(SpiritCooldownSeconds <= 0) {
            	SpiritMaskActivated = false;
            }
        }
    }
	
	private void renderBonzoMaskTimer(ScaledResolution resolution, float partialTicks) {
		String cooldownTimer = Math.round(BonzoCooldownSeconds) + "s";
		
		// Calculate the position to render the timer
        int x = resolution.getScaledWidth() / 10;
        int y = resolution.getScaledHeight() / 3 - 10;
		RenderHandler.drawText(cooldownTimer, x, y, 1f, false, 0xFFFFFF);
		
		int textWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(cooldownTimer);
		
		ResourceLocation textureLocation = new ResourceLocation(Reference.MODID, "textures/Bonzo_Head.png");
        Minecraft.getMinecraft().renderEngine.bindTexture(textureLocation);
        drawModalRectWithCustomSizedTexture((int)(x - textWidth), y - 5, 0, 0, 12, 17, 12, 17);
        
	}
	
	private void renderSpiritMaskTimer(ScaledResolution resolution, float partialTicks) {
		String cooldownTimer = Math.round(SpiritCooldownSeconds) + "s";
		
		// Calculate the position to render the timer
        int x = resolution.getScaledWidth() / 10;
        int y = resolution.getScaledHeight() / 3 - 20;
		RenderHandler.drawText(cooldownTimer, x, y, 1f, false, 0xFFFFFF);
		
		int textWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(cooldownTimer);
		
		ResourceLocation textureLocation = new ResourceLocation(Reference.MODID, "textures/Spirit_Mask.png");
        Minecraft.getMinecraft().renderEngine.bindTexture(textureLocation);
        drawModalRectWithCustomSizedTexture((int)(x - textWidth), y - 5, 0, 0, 12, 17, 12, 17);
        
	}
}
