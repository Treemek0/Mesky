package treemek.mesky.handlers.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Keyboard;

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

	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
        double scale = 3;
        int textLength = mc.fontRendererObj.getStringWidth("Alerts");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) ((height / 4) / scale);

        RenderHandler.drawText("Alerts", titleX, titleY, scale, true, 0x3e91b5);
        
    	int trigger_X = width / 6;
    	int display_X = trigger_X + (width / 4) + 10;
    	int time_X = display_X + (width / 4);
        
        int positionY = (int)((height / 3) - 15);
        RenderHandler.drawText("Trigger", width / 6, positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Display", display_X, positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Time [seconds]", time_X + 10, positionY, 1, true, 0x7a7a7a);
        
        
        for (GuiTextField input : alertsFields) {
			input.drawTextBox();
		}
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    
	    alertsFields = new ArrayList<GuiTextField>();
	    
        int positionY = height / 3;
        int buttonWidth = 20;
        int buttonHeight = 20;
        int trigger_X = width / 6;
    	int display_X = trigger_X + (width / 4) + 10;
    	int time_X = display_X + (width / 4);
        
        // Save button
        this.buttonList.add(new GuiButton(-1, (int)(width * 0.8f), (height/15), (int)(width * 0.2f), 20, "Save"));
        
        // New Alert button
        this.buttonList.add(new GuiButton(-2, 0, (height/15), (int)(width * 0.2f), 20, "New alert"));
        
        for (int i = 0; i < Alerts.alertsList.size(); i++) {
        	// Delete button
        	this.buttonList.add(new DeleteButton(0 + (4*i), (int)(width * 0.8f), positionY + (30 * i), 20, 20, ""));

        	// trigger text input
        	GuiTextField alertTrigger = new GuiTextField(1, this.fontRendererObj, trigger_X, positionY + (30 * i), width / 4, 20);
            alertTrigger.setMaxStringLength(128);
            alertTrigger.setCanLoseFocus(true);
            alertTrigger.setText(Alerts.alertsList.get(i).getTrigger());
            alertsFields.add(alertTrigger);
            
            // display text input
            GuiTextField alertDisplay = new GuiTextField(1, this.fontRendererObj, display_X, positionY + (30 * i), width / 4, 20);
            alertDisplay.setMaxStringLength(35);
            alertDisplay.setCanLoseFocus(true);
            alertDisplay.setText(Alerts.alertsList.get(i).getDisplay());
            alertsFields.add(alertDisplay);

            // time text input
            GuiTextField alertTime = new GuiTextField(2, this.fontRendererObj, time_X + 10, positionY + (30 * i), width / 10, 20);
            alertTime.setMaxStringLength(16);
            alertTime.setCanLoseFocus(true);
            alertTime.setText(Float.toString(Alerts.alertsList.get(i).getTime() / 1000));
            alertsFields.add(alertTime);
        }
	}
	
	@Override
    protected void actionPerformed(GuiButton button) {
		if(button.id == -1) {
			// Save button
			SaveAlerts();
			return;
		}
		if(button.id == -2) {
			// New alert button
			Alerts.addAlert("", "", 1);
			// Clearing all lists because in initGui() it will add them again
			buttonList.clear();
            alertsFields.clear();
            initGui();
            return;
		}
		
		// Every other button
        for (GuiButton guiButton : buttonList) {
			if(guiButton.id == button.id) {
				// Removing alert from list
				int listId = button.id/4;
				Alerts.deleteAlert(listId);
	            buttonList.clear();
	            alertsFields.clear();
	            initGui();
	            return;
			}
		}
    }
	
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == 1) {
			// Esc
			Minecraft.getMinecraft().thePlayer.closeScreen();
			return;
		}
		
		for (GuiTextField input : alertsFields) {
			if(input.isFocused()) {
				// Numerical (time input)
				if(input.getId() == 2) {
					// Backspace / leftArrow / rightArrow / . / delete
					if(keyCode == 14 || keyCode == 203 || keyCode == 205 || keyCode == 211) input.textboxKeyTyped(typedChar, keyCode);
					
					// disallows more than one "." in coords 
					if(keyCode == 52 && !input.getText().contains(".")) input.textboxKeyTyped(typedChar, keyCode);
						
					// CTRL + A/C/V
					if((keyCode == Keyboard.KEY_A || keyCode == Keyboard.KEY_C || keyCode == Keyboard.KEY_V) && isCtrlKeyDown()) input.textboxKeyTyped(typedChar, keyCode);
					
					try {
		                float isNumber = Integer.parseInt(String.valueOf(typedChar));
		                input.textboxKeyTyped(typedChar, keyCode);
					} catch (NumberFormatException ex) { return; }

				}else {
					// Trigger and display inputs
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
		return false;
	}
	
	private void SaveAlerts() {
		List<Alert> alertsList = new ArrayList<Alert>();
		// idk why i did it like this with alertsFields.size(), but because of it we have to do "i +=3" since we have 3 input fields per alert
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
	    ConfigHandler.SaveAlert(alertsList);
	}
}
