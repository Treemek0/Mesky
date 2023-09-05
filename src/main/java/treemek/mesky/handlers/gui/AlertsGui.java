package treemek.mesky.handlers.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.buttons.CheckButton;
import treemek.mesky.handlers.gui.buttons.DeleteButton;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Alerts.Alert;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.Waypoint;

public class AlertsGui extends GuiScreen {
	ArrayList<GuiTextField> alertsFields;
	ArrayList<GuiTextField> timeFields;
	int Xplace;
	int displayX = Xplace + (width/4) + 5;
	int timeX;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
        double scale = 3;
        int textLength = mc.fontRendererObj.getStringWidth("Alerts");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) ((height / 4) / scale);

        RenderHandler.drawText("Alerts", titleX, titleY, scale, true, 0x3e91b5);
        
        
        int infoY = (int)((height / 3) - 15);
        RenderHandler.drawText("Trigger", width / 6, infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Display", displayX, infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Time [seconds]", timeX + 10, infoY, 1, true, 0x7a7a7a);
        
        
        for (GuiTextField input : alertsFields) {
			input.drawTextBox();
		}
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    
	    alertsFields = new ArrayList<GuiTextField>();
	    timeFields = new ArrayList<GuiTextField>();
	    
        int checkX = (int)(width / 4);
        int positionY = height / 3;
        int buttonWidth = 20;
        int buttonHeight = 20;
        
        this.buttonList.add(new GuiButton(-1, (int)(width * 0.8f), (height/15), (int)(width * 0.2f), 20, "Save"));
        this.buttonList.add(new GuiButton(-2, 0, (height/15), (int)(width * 0.2f), 20, "New alert"));
        
        for (int i = 0; i < Alerts.alertsList.size(); i++) {
	        	this.buttonList.add(new DeleteButton(0 + (4*i), (int)(width * 0.8f), positionY + (30 * i), 20, 20, ""));
	        	
	        	Xplace = width / 6;
	        	GuiTextField alertTrigger = new GuiTextField(1 + (4 * i), this.fontRendererObj, Xplace, positionY + (30 * i), width / 4, 20);
	            alertTrigger.setMaxStringLength(30);
	            alertTrigger.setCanLoseFocus(true);
	            alertTrigger.setText(Alerts.alertsList.get(i).getTrigger());
	            alertsFields.add(alertTrigger);
	            
	            
	            displayX = Xplace + alertTrigger.width + 10;
	            
	            GuiTextField alertDisplay = new GuiTextField(1 + (4 * i), this.fontRendererObj, displayX, positionY + (30 * i), width / 4, 20);
	            alertDisplay.setMaxStringLength(16);
	            alertDisplay.setCanLoseFocus(true);
	            alertDisplay.setText(Alerts.alertsList.get(i).getDisplay());
	            alertsFields.add(alertDisplay);
	            
	            timeX = displayX + alertDisplay.width;
	            
	            GuiTextField alertTime = new GuiTextField(2 + (4 * i), this.fontRendererObj, timeX + 10, positionY + (30 * i), width / 10, 20);
	            alertTime.setMaxStringLength(16);
	            alertTime.setCanLoseFocus(true);
	            alertTime.setText(Float.toString(Alerts.alertsList.get(i).getTime() / 1000));
	            alertsFields.add(alertTime);
	            timeFields.add(alertTime);
	           
        }
	}
	
	@Override
    protected void actionPerformed(GuiButton button) {
		if(button.id == -1) {
			SaveAlerts();
			return;
		}
		if(button.id == -2) {
			Alerts.addAlert("", "", 1);
			buttonList.clear();
            alertsFields.clear();
            timeFields.clear();
            initGui();
            return;
		}
        for (GuiButton guiButton : buttonList) {
			if(guiButton.id == button.id) {
				int listId = (button.id > 0)?(button.id/4):0;
				Alerts.deleteAlert(listId);

	            // Clear existing buttons and text fields
	            buttonList.clear();
	            alertsFields.clear();
	            timeFields.clear();

	            // Reinitialize the GUI with updated data
	            initGui();
	            return;
			}
		}
    }
	
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		// TODO Auto-generated method stub
		if(keyCode == 1) {
			Minecraft.getMinecraft().thePlayer.closeScreen();
			return;
		}
		
		for (GuiTextField input : alertsFields) {
			if(input.isFocused()) {
				// Backspace / leftArrow / rightArrow / . / delete
				if(timeFields.contains(input) && keyCode != 14 && keyCode != 203 && keyCode != 205 && keyCode != 52 && keyCode != 211) {
					try {
						// coords
		                float time = Integer.parseInt(String.valueOf(typedChar));
		                input.textboxKeyTyped(typedChar, keyCode);
		                
		            } catch (NumberFormatException ex) {
		            	return;
		            }
				}else {
					if(keyCode == 52 && input.getText().contains(".") && timeFields.contains(input)) {
						return;
						// dis-allow more than one "." in time 
					}
					// name
					input.textboxKeyTyped(typedChar, keyCode);
				}
			}
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for (GuiTextField input : alertsFields) {
			if (mouseX >= input.xPosition && mouseX <= input.xPosition + input.width && mouseY >= input.yPosition && mouseY <= input.yPosition + input.height) {
				input.mouseClicked(mouseX, mouseY, mouseButton);
			}else {
				input.setFocused(false);
			}
		}
		
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void updateScreen() {
		for (GuiTextField input : alertsFields) {
			input.updateCursorCounter();
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void SaveAlerts() {
		List<Alert> alertsList = new ArrayList<Alert>();
	    for (int i = 0; i < alertsFields.size(); i += 3) {
	        String trigger = alertsFields.get(i).getText();
	        String display = alertsFields.get(i+1).getText();
	        float time = 0;
	        try {
	            time = Float.parseFloat(alertsFields.get(i + 2).getText()) * 1000;
	        } catch (NumberFormatException e) {
	            System.out.println(e);
	            continue; // Skip this iteration if there's a parsing error
	        }
	        alertsList.add(new Alert(trigger, display, time));
	    }
	    Alerts.alertsList = alertsList;
	}
}
