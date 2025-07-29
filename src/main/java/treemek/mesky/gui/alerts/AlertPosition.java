package treemek.mesky.handlers.gui.alerts;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.buttons.MeskyButton;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Alerts.Alert;
import treemek.mesky.utils.Alerts.AlertRenderInfo;
import treemek.mesky.utils.ColorUtils;

public class AlertPosition extends GuiScreen{

	public static AlertRenderInfo alertInfo;
	public static AlertElement alert;
	public static List<AlertElement> alertsGUI;
	public static List<Alert> alertsList;
	private boolean currentlyDragged = false;
	float offsetX = 0;
	float offsetY = 0;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = mc.fontRendererObj;
        ScaledResolution resolution = new ScaledResolution(mc);
        
        int posX = (int) (resolution.getScaledWidth() * ((float)(alertInfo.position[0])/100));
        int posY = (int) (resolution.getScaledHeight() * ((float)(alertInfo.position[1])/100));
        
		if(alertInfo != null) {
    		if(alertInfo.bufferedImage != null && alertInfo.location != null) {     
                int imgHeight = (int) (alertInfo.bufferedImage.getHeight() * (((double)resolution.getScaledHeight() / (double)alertInfo.bufferedImage.getHeight()) * alertInfo.scale));
                double ratio = (double)alertInfo.bufferedImage.getWidth() / (double)alertInfo.bufferedImage.getHeight() ;
        		int imgWidth = (int) (imgHeight * ratio);
                Minecraft.getMinecraft().renderEngine.bindTexture(alertInfo.location);
                
                if ((mouseX >= posX - (imgWidth/2) && mouseX <= posX + (imgWidth/2) && mouseY >= posY - (imgHeight/2) && mouseY <= posY + (imgHeight/2)) || currentlyDragged) { // hovered
                	GlStateManager.color(0.35F, 0.35F, 0.35F, 1);
                	drawModalRectWithCustomSizedTexture(posX - imgWidth / 2, posY - imgHeight / 2, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
	            }else {
	            	drawModalRectWithCustomSizedTexture(posX - imgWidth / 2, posY - imgHeight / 2, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
	            }
                    
        	}else {
        		float TextWidth = 0;
            	float TextHeight = 0;
            	
            	for (String a : alertInfo.message.split("<br>")) {
					TextHeight += fontRenderer.FONT_HEIGHT * alert.scale;
					float w = fontRenderer.getStringWidth(a) * alert.scale;
					if(TextWidth < w) {
						TextWidth = w;
					}
				}

            	// Gl11.color doesnt work lmao so no hover
            	RenderHandler.drawAlertText(ColorUtils.getColoredText(alertInfo.message), resolution, 0xffffff, alertInfo.scale, posX, posY);
        	}
			
			if(currentlyDragged) {
	            float scale = (float) (Math.round(alertInfo.scale * 100.0) / 100.0); 
				int fontHeight = fontRendererObj.FONT_HEIGHT;
	            
				RenderHandler.drawText("x: " + posX + ", y: " + posY, posX, posY - (2*fontHeight) - 10, 0.7, true, 0xffffff);
				RenderHandler.drawText("scale: " + scale, posX, posY - fontHeight - 5, 0.7, true, 0xffffff);
			}
		}
    	
    	
    	
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(alertInfo != null) {
			ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
			
            if(alertInfo.bufferedImage != null) { // image
            	int imgHeight = (int) (alertInfo.bufferedImage.getHeight() * (((double)resolution.getScaledHeight() / (double)alertInfo.bufferedImage.getHeight()) * alertInfo.scale));
                double ratio = (double)alertInfo.bufferedImage.getWidth() / (double)alertInfo.bufferedImage.getHeight() ;
                int imgWidth = (int) (imgHeight * ratio);
            	
                float x = resolution.getScaledWidth() * ((float)alertInfo.position[0] / 100);
                float y = resolution.getScaledHeight() * ((float)alertInfo.position[1] / 100);
	            if (mouseX >= x - (imgWidth/2) && mouseX <= x + (imgWidth/2) && mouseY >= y - (imgHeight/2) && mouseY <= y + (imgHeight/2)) {
	                currentlyDragged = true;
	                offsetX = mouseX - x;
	                offsetY = mouseY - y;
	            }
            }else { // text
            	FontRenderer fontRenderer = mc.fontRendererObj;
            	float TextWidth = 0;
            	float TextHeight = 0;
            	
            	for (String a : alertInfo.message.split("<br>")) {
					TextHeight += fontRenderer.FONT_HEIGHT * alert.scale;
					float w = fontRenderer.getStringWidth(a) * alert.scale;
					if(TextWidth < w) {
						TextWidth = w;
					}
				}
            	
            	float x = width * ((float)alertInfo.position[0] / 100);
            	float y = height * ((float)alertInfo.position[1] / 100);
            	
            	if (mouseX >= (x - (TextWidth/2)) && mouseX <= (x + (TextWidth/2)) && mouseY >= (y - (2*alertInfo.scale)) && mouseY <= (y + (TextHeight) + (1*alertInfo.scale))) {
	                currentlyDragged = true;
	                offsetX = mouseX - x;
	                offsetY = mouseY - y;
	            }
            }
		}
    }
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
		if (currentlyDragged) {
        	float TextHeight = 0;
        	
        	for (String a : alertInfo.message.split("<br>")) TextHeight += mc.fontRendererObj.FONT_HEIGHT * alert.scale;
			
			float x = ((mouseX - offsetX) / (float) width) * 100;
			float y = ((Math.min(height - TextHeight/2, Math.max(-TextHeight/2, mouseY - offsetY))) / (float) height) * 100;
			
			x = Math.max(0, Math.min(x, 100)); // it renders from center on X axis
			
            Float[] newPosition = {x, y};
            alertInfo.position = newPosition;
		}
    }
	
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if(currentlyDragged) {
        	currentlyDragged = false;
        	AlertElement newAlert = alertsGUI.get(alertsGUI.indexOf(alert));
        	newAlert.position = alertInfo.position;
        	alertsGUI.set(alertsGUI.indexOf(alert), newAlert);
        }
    }
	
	
	@Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        AlertElement newAlert = alertsGUI.get(alertsGUI.indexOf(alert));
        
        if (scroll != 0 && currentlyDragged) {
        	float SCROLL_SPEED = (newAlert.scale < 3f)?0.05f:0.1f;
        	newAlert.scale -= scroll > 0 ? -SCROLL_SPEED : SCROLL_SPEED;
        }
        
        if(alertInfo.bufferedImage != null && newAlert.scale < 0.05f) { // image
        	newAlert.scale = 0.05f;
        }
        
        if(alertInfo.bufferedImage == null && newAlert.scale < 1f) { // image
        	newAlert.scale = 1f;
        }
        
        newAlert.scale = (float)Math.round(newAlert.scale * 100) / 100;
        alertsGUI.set(alertsGUI.indexOf(alert), newAlert);
        alertInfo.scale = newAlert.scale;
	}
	
	@Override
    public void onGuiClosed() {
		alertInfo = null;
		alert = null;
		currentlyDragged = false;
		GuiHandler.GuiType = new AlertsGui();
		AlertsGui.alerts = alertsGUI;
    }
	
	@Override
	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return false;
	}
}
