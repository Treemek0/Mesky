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
import treemek.mesky.handlers.gui.buttons.MeskyButton;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Alerts.Alert;
import treemek.mesky.utils.Alerts.AlertRenderInfo;

public class AlertPosition extends GuiScreen{

	public static AlertRenderInfo alertInfo;
	public static Alert alert;
	public static List<Alert> alertsList;
	private boolean currentlyDragged = false;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = mc.fontRendererObj;
        ScaledResolution resolution = new ScaledResolution(mc);
        
        int posX = (int) (resolution.getScaledWidth() * ((float)(alertInfo.position[0])/100));
        int posY = (int) (resolution.getScaledHeight() * ((float)(alertInfo.position[1])/100));
        
		if(alertInfo != null) {
			if((alertInfo.message.endsWith(".png") || alertInfo.message.endsWith(".jpg") || alertInfo.message.endsWith(".jpeg")) && (alertInfo.message.contains("/") || alertInfo.message.contains("\\"))) { // rendering images
        		if(alertInfo.bufferedImage != null && alertInfo.location != null) {
        	        
                    // Draw the texture on the screen
                    int imgHeight = (int) (alertInfo.bufferedImage.getHeight() * (((double)resolution.getScaledHeight() / (double)alertInfo.bufferedImage.getHeight()) * alertInfo.scale));
                    double ratio = (double)alertInfo.bufferedImage.getWidth() / (double)alertInfo.bufferedImage.getHeight() ;
            		int imgWidth = (int) (imgHeight * ratio);
                    Minecraft.getMinecraft().renderEngine.bindTexture(alertInfo.location);
                    

                    if ((mouseX >= posX - (imgWidth/2) && mouseX <= posX + (imgWidth/2) && mouseY >= posY - (imgHeight/2) && mouseY <= posY + (imgHeight/2)) || currentlyDragged) { // hovered
                    	GlStateManager.color(0.5F, 0.5F, 0.5F, 1);
                    	drawModalRectWithCustomSizedTexture(posX - imgWidth / 2, posY - imgHeight / 2, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
    	            }else {
    	            	drawModalRectWithCustomSizedTexture(posX - imgWidth / 2, posY - imgHeight / 2, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
    	            }
                    
        		}
        	}else {
        		float TextWidth = fontRenderer.getStringWidth(alertInfo.message) * alert.scale;
            	float TextHeight = fontRenderer.FONT_HEIGHT * alert.scale;
            	
            	if ((mouseX >= (posX - (TextWidth/2)) && mouseX <= (posX + (TextWidth/2)) && mouseY >= (posY - 1) && mouseY <= (posY + (TextHeight*1.5)) + (1*alertInfo.scale)) || currentlyDragged) {
            		RenderHandler.drawAlertText(alertInfo.message, resolution, 0xa93234, alertInfo.scale, posX, posY);
	            }else {
	            	RenderHandler.drawAlertText(alertInfo.message, resolution, 0xf54245, alertInfo.scale, posX, posY);
	            }
        		
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
            	
                int x = (int) (resolution.getScaledWidth() * ((float)alertInfo.position[0] / 100));
                int y = (int) (resolution.getScaledHeight() * ((float)alertInfo.position[1] / 100));
	            if (mouseX >= x - (imgWidth/2) && mouseX <= x + (imgWidth/2) && mouseY >= y - (imgHeight/2) && mouseY <= y + (imgHeight/2)) {
	                currentlyDragged = true;
	            }
            }else { // text
            	FontRenderer fontRenderer = mc.fontRendererObj;
            	float TextWidth = fontRenderer.getStringWidth(alertInfo.message) * alert.scale;
            	float TextHeight = fontRenderer.FONT_HEIGHT * alert.scale;
            	
            	int x = (int) (width * ((float)alertInfo.position[0] / 100));
            	int y = (int) (height * ((float)alertInfo.position[1] / 100));
            	
            	if (mouseX >= (x - (TextWidth/2)) && mouseX <= (x + (TextWidth/2)) && mouseY >= (y + (1*alertInfo.scale)) && mouseY <= (y + (TextHeight) + (1*alertInfo.scale)) + 1) {
	                currentlyDragged = true;
	            }
            }
		}
    }
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
		if (currentlyDragged) {
            int[] newPosition = { (int) ((mouseX / (float) width) * 100), (int) ((mouseY / (float) height) * 100) };
            alertInfo.position = newPosition;
		}
    }
	
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if(currentlyDragged) {
        	currentlyDragged = false;
        	Alert newAlert = alert;
            newAlert.position = alertInfo.position;
        	Alerts.alertsList.set(Alerts.alertsList.indexOf(alert), newAlert);
        }
    }
	
	
	@Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        float SCROLL_SPEED = 0.05f;
        
        if (scroll != 0 && currentlyDragged) {
        	Alert newAlert = alert;
        	newAlert.scale -= scroll > 0 ? -SCROLL_SPEED : SCROLL_SPEED;
        	alertInfo.scale = newAlert.scale;
            Alerts.alertsList.set(Alerts.alertsList.indexOf(alert), newAlert);
        }
        
        if(alertInfo.bufferedImage != null && alertInfo.scale < 0.05f) { // image
        	Alert newAlert = alert;
        	newAlert.scale = 0.05f;
        	alertInfo.scale = newAlert.scale;
            Alerts.alertsList.set(Alerts.alertsList.indexOf(alert), newAlert);
        }
        
        if(alertInfo.bufferedImage == null && alertInfo.scale < 1f) { // image
        	Alert newAlert = alert;
        	newAlert.scale = 1f;
        	alertInfo.scale = newAlert.scale;
            Alerts.alertsList.set(Alerts.alertsList.indexOf(alert), newAlert);
        }
        
	}
	
	@Override
    public void onGuiClosed() {
		alertInfo = null;
		alert = null;
		currentlyDragged = false;
		GuiHandler.GuiType = 4;
		ConfigHandler.SaveAlert(Alerts.alertsList);
    }
	
	@Override
	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return false;
	}
}
