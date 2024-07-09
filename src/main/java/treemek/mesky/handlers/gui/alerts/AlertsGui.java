package treemek.mesky.handlers.gui.alerts;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.CloseWarning;
import treemek.mesky.handlers.gui.elements.ScrollBar;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.EditButton;
import treemek.mesky.handlers.gui.elements.buttons.MeskyButton;
import treemek.mesky.handlers.gui.waypoints.WaypointElement;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.HypixelCheck;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Alerts.Alert;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.Waypoint;

public class AlertsGui extends GuiScreen {

	public static List<AlertElement> alerts = new ArrayList<>();
	
	ScrollBar scrollbar = new ScrollBar(0,0,0,0,0);
	AlertElement holdingElement;
	CloseWarning closeWarning = new CloseWarning();
	
	int inputMargin;
	int inputHeight;
	private float scrollbar_startPosition;
	private float scrollbarBg_height;
	private float scrollbar_width;
	private GuiButton saveButton;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {     
		drawRect(0, height/3 - 1, width, height, new Color(33, 33, 33,255).getRGB());
		
		// Draw text fields first
		for (int i = 0; i < alerts.size(); i++) {
			AlertElement alert = alerts.get(i);
			if(alert == holdingElement) continue;
			
			List<GuiTextField> inputs = alert.getListOfTextFields();
			
			for (GuiTextField input : inputs) {
				input.drawTextBox();
			}
	    }

	    // Reset color and blending state before drawing buttons
	    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

	    // Draw delete buttons
	    for (int i = 0; i < alerts.size(); i++) {
			AlertElement alert = alerts.get(i);
			if(alert == holdingElement) continue;
			
			for (GuiButton button : alert.getListOfButtons()) {
				if(button instanceof EditButton) {
					if(alert.display.getText().length() > 0) { // if theres nothing to display then theres no need to edit
						button.enabled = true;
					}else {
						button.enabled = false;
					}
				}
					
				button.drawButton(mc, mouseX, mouseY);
			}
	        
	    }
		
	    if(holdingElement != null) {
	    	
	    	drawRect(holdingElement.xPosition, holdingElement.yPosition, holdingElement.xPosition + holdingElement.getWidth(), holdingElement.yPosition + holdingElement.getHeight(), new Color(33, 33, 33,255).getRGB());
	    	
	    	List<GuiTextField> inputs = holdingElement.getListOfTextFields();
			for (GuiTextField input : inputs) {
				input.drawTextBox();
			}
			
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		    GlStateManager.enableBlend();
		    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
			
			for (GuiButton button : holdingElement.getListOfButtons()) {
				if(button instanceof EditButton) {
					if(holdingElement.display.getText().length() > 0) { // if theres nothing to display then theres no need to edit
						button.enabled = true;
					}else {
						button.enabled = false;
					}
				}
					
				button.drawButton(mc, mouseX, mouseY);
			}
	    }
	    
		drawRect(0, 0, width, height/3 - 1, new Color(33, 33, 33,255).getRGB());
		
        double scale = 3;
        int textLength = mc.fontRendererObj.getStringWidth("Alerts");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) ((height / 4) / scale);

        RenderHandler.drawText("Alerts", titleX, titleY, scale, true, 0x3e91b5);
        
    	int trigger_X = width / 6;
    	int display_X = trigger_X + (width / 4) + 10;
    	int time_X = display_X + (width / 4);
        
        int positionY = (int)((height / 3) - 15);
        RenderHandler.drawText("Only", (trigger_X * 0.1), positionY - mc.fontRendererObj.FONT_HEIGHT*1.2, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Party", (trigger_X * 0.1), positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Ignore", (trigger_X * 0.4), positionY - mc.fontRendererObj.FONT_HEIGHT*1.2, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Players", (trigger_X * 0.4), positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Equal", (trigger_X * 0.7), positionY, 1, true, 0x7a7a7a);
        
        RenderHandler.drawText("Trigger", width / 6, positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Display", display_X, positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Time [seconds]", time_X + 10, positionY, 1, true, 0x7a7a7a);
        
        scrollbar.updateScrollBar((int) Math.min(20, (width * 0.025)), (height - (height / 3) - 10), (int)(width * 0.95), height/3);
        updateAlertsY();
        scrollbar.renderScrollBar();
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	    
	    closeWarning.drawElement(mc, mouseX, mouseY);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    buttonList.clear();
	    closeWarning = new CloseWarning();
	    
	    inputHeight = ((height / 25) < 12)?12:(height / 25);
		inputMargin = ((height / 40) < 5)?5:(height / 40);
		
        int positionY = height / 3;
        int trigger_X = width / 6;
    	int display_X = trigger_X + (width / 4) + 10;
    	int time_X = display_X + (width / 4);
        
        // Save button
    	saveButton = new MeskyButton(-1, (int)(width * 0.8f), (height/15), (int)(width * 0.2f), 20, "Save");
        this.buttonList.add(saveButton);
        
        // New Alert button
        this.buttonList.add(new MeskyButton(-2, 0, (height/15), (int)(width * 0.2f), 20, "New alert"));
        
    	// This is so you cant scroll limitless, it takes every waypoint height with their margin and removes visible inputs height so you can scroll max to how much of inputs isnt visible
 		scrollbar.updateMaxBottomScroll(Math.min(0, -(((Alerts.alertsList.size() * (inputHeight + inputMargin))) - (height - (height/3)))));
 		int ScrollOffset = scrollbar.getOffset();
 		
 		if(alerts.isEmpty()) {	
	        for (int i = 0; i < Alerts.alertsList.size(); i++) {
	        	// Position 0 for inputs + every input height and their bottom margin
	        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
	        	
	        	// Check if any part of the text field is within the visible area (stop rendering inputs that go over text)
	            if (inputFullPosition <= ((height / 3) - 20)) {
	                continue;
	            }
	        	
	        	// Delete button
	        	DeleteButton deleteButton = new DeleteButton(0, (int)(width * 0.9f), inputFullPosition, inputHeight, inputHeight, "");
	
	        	EditButton editButton = new EditButton(1, (int)(width * 0.85f), inputFullPosition, inputHeight, inputHeight, "");
	        	
	        	// onlyParty button
	        	CheckButton onlyParty = new CheckButton(2, (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", Alerts.alertsList.get(i).getOnlyParty());
	        	
	        	// ignorePlayers button
	        	CheckButton ignorePlayers = new CheckButton(3, (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", Alerts.alertsList.get(i).getIgnorePlayers());
	        	
	        	// isEqual button
	        	CheckButton isEqual = new CheckButton(4, (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", Alerts.alertsList.get(i).getIsEqual());
	        	
	        	// trigger text input
	        	GuiTextField alertTrigger = new GuiTextField(1, this.fontRendererObj, trigger_X, inputFullPosition, width / 4, inputHeight);
	        	alertTrigger.setMaxStringLength(512);
	            alertTrigger.setCanLoseFocus(true);
	            alertTrigger.setText(Alerts.alertsList.get(i).getTrigger());
	            
	            // display text input
	            GuiTextField alertDisplay = new GuiTextField(1, this.fontRendererObj, display_X, inputFullPosition, width / 4, inputHeight);
	            alertDisplay.setMaxStringLength(512);
	            alertDisplay.setCanLoseFocus(true);
	            alertDisplay.setText(Alerts.alertsList.get(i).getDisplay());
	
	            // time text input
	            GuiTextField alertTime = new GuiTextField(2, this.fontRendererObj, time_X + 10, inputFullPosition, width / 10, inputHeight);
	            alertTime.setMaxStringLength(16);
	            alertTime.setCanLoseFocus(true);
	            alertTime.setText(Float.toString(Alerts.alertsList.get(i).getTime() / 1000));
	
	            alerts.add(new AlertElement(alertTrigger, alertDisplay, alertTime, deleteButton, editButton, onlyParty, ignorePlayers, isEqual, Alerts.alertsList.get(i).position, Alerts.alertsList.get(i).scale));
	        }
 		}else {
	 		for (int i = 0; i < alerts.size(); i++) {
	        	// Position 0 for inputs + every input height and their bottom margin
	        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
	        	
	        	// Check if any part of the text field is within the visible area (stop rendering inputs that go over text)
	            if (inputFullPosition <= ((height / 3) - 20)) {
	                continue;
	            }
	        	
	        	// Delete button
	        	DeleteButton deleteButton = new DeleteButton(0, (int)(width * 0.9f), inputFullPosition, inputHeight, inputHeight, "");
	
	        	EditButton editButton = new EditButton(1, (int)(width * 0.85f), inputFullPosition, inputHeight, inputHeight, "");
	        	
	        	// onlyParty button
	        	CheckButton onlyParty = new CheckButton(2, (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", alerts.get(i).onlyParty.isFull);
	        	
	        	// ignorePlayers button
	        	CheckButton ignorePlayers = new CheckButton(3, (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", alerts.get(i).ignorePlayers.isFull);
	        	
	        	// isEqual button
	        	CheckButton isEqual = new CheckButton(4, (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", alerts.get(i).isEqual.isFull);
	        	
	        	// trigger text input
	        	GuiTextField alertTrigger = new GuiTextField(1, this.fontRendererObj, trigger_X, inputFullPosition, width / 4, inputHeight);
	        	alertTrigger.setMaxStringLength(512);
	            alertTrigger.setCanLoseFocus(true);
	            alertTrigger.setText(alerts.get(i).trigger.getText());
	            
	            // display text input
	            GuiTextField alertDisplay = new GuiTextField(1, this.fontRendererObj, display_X, inputFullPosition, width / 4, inputHeight);
	            alertDisplay.setMaxStringLength(512);
	            alertDisplay.setCanLoseFocus(true);
	            alertDisplay.setText(alerts.get(i).display.getText());
	
	            // time text input
	            GuiTextField alertTime = new GuiTextField(2, this.fontRendererObj, time_X + 10, inputFullPosition, width / 10, inputHeight);
	            alertTime.setMaxStringLength(16);
	            alertTime.setCanLoseFocus(true);
	            alertTime.setText(alerts.get(i).time.getText());
	            
	            alerts.set(i, new AlertElement(alertTrigger, alertDisplay, alertTime, deleteButton, editButton, onlyParty, ignorePlayers, isEqual, alerts.get(i).position, alerts.get(i).scale));
	        }
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
	        int trigger_X = width / 6;
	    	int display_X = trigger_X + (width / 4) + 10;
	    	int time_X = display_X + (width / 4);
			
			
			DeleteButton deleteButton = new DeleteButton(0, (int)(width * 0.9f), 0, inputHeight, inputHeight, "");
			
			EditButton editButton = new EditButton(1, (int)(width * 0.85f), 0, inputHeight, inputHeight, "");
        	
			CheckButton onlyParty = new CheckButton(2, (int) (trigger_X * 0.1), 0, inputHeight, inputHeight, "", false);
        	
			CheckButton ignorePlayers = new CheckButton(3, (int) (trigger_X * 0.4), 0, inputHeight, inputHeight, "", false);

			CheckButton isEqual = new CheckButton(4, (int) (trigger_X * 0.7), 0, inputHeight, inputHeight, "", false);
        	
        	// trigger text input
        	GuiTextField alertTrigger = new GuiTextField(1, this.fontRendererObj, trigger_X, 0, width / 4, inputHeight);
        	alertTrigger.setMaxStringLength(512);
            alertTrigger.setCanLoseFocus(true);
            alertTrigger.setText("");
            
            // display text input
            GuiTextField alertDisplay = new GuiTextField(1, this.fontRendererObj, display_X, 0, width / 4, inputHeight);
            alertDisplay.setMaxStringLength(512);
            alertDisplay.setCanLoseFocus(true);
            alertDisplay.setText("");

            // time text input
            GuiTextField alertTime = new GuiTextField(2, this.fontRendererObj, time_X + 10, 0, width / 10, inputHeight);
            alertTime.setMaxStringLength(16);
            alertTime.setCanLoseFocus(true);
            alertTime.setText("1");
            
            
            alerts.add(new AlertElement(alertTrigger, alertDisplay, alertTime, deleteButton, editButton, onlyParty, ignorePlayers, isEqual, new int[] {50,50}, 1f));
            return;
		}
		
		
		for (int i = 0; i < alerts.size(); i++) {
			AlertElement alert = alerts.get(i);
        	GuiButton deleteButton = alert.deleteButton;
        	GuiButton editButton = alert.editButton;
        	
			if(deleteButton == button) {
				// Removing alert from list
				alerts.remove(i);
	            return;
			}
			
			if(editButton == button && editButton.enabled) {
        		int[] position = (alert.position != null)?alert.position : new int[] {50,50};
        		Float scale = (alert.scale != null)?alert.scale : 1;
        		List<AlertElement> copiedList = new ArrayList<>(alerts);
        		Alerts.editAlertPositionAndScale(position, scale, i, copiedList);
			}
		}
        
			
    }
	
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == 1) {
			// Esc
			CloseGui();
			return;
		}
		
		if(!closeWarning.showElement) {
			for (int i = 0; i < alerts.size(); i++) {
				AlertElement alert = alerts.get(i);
				List<GuiTextField> inputs = alert.getListOfTextFields();
				
				for (GuiTextField input : inputs) {
					if(input.isFocused()) {
						if(input.getId() == 2) { // Numerical (time input)
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
			}
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		closeWarning.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(!closeWarning.showElement) {
			for (int i = 0; i < alerts.size(); i++) {
				AlertElement alert = alerts.get(i);
				boolean isAnythingPressed = false;
				List<GuiTextField> inputs = alert.getListOfTextFields();
				
				for (GuiTextField input : inputs) {
					if (mouseX >= input.xPosition && mouseX <= input.xPosition + input.width && mouseY >= input.yPosition && mouseY <= input.yPosition + input.height) {
						input.mouseClicked(mouseX, mouseY, mouseButton);
						isAnythingPressed = true;
					}else {
						input.setFocused(false);
					}
				}
				
				if (mouseButton == 0)
		        {
		            for (GuiButton guiButton : alert.getListOfButtons()) {
		            	if (guiButton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY) && guiButton.enabled)
			            {
		            		isAnythingPressed = true;
			                guiButton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
			                this.actionPerformed(guiButton);
			            }
					}
		            
		        }
				
				if(alert.isHovered(mouseX, mouseY)) {
					if(!isAnythingPressed) {
						holdingElement = alert;
					}
				}
			}
			
			if(mouseX >= scrollbar.x && mouseX <= scrollbar.x + scrollbar.scrollbarWidth && mouseY >= scrollbar.y && mouseY <= scrollbar.y + scrollbar.scrollbarHeight) {
				scrollbar.updateOffsetToMouseClick(mouseY);
			}
			
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
		if (holdingElement != null) {
			mouseY = Math.min(Math.max(mouseY, height/3 - 1), height - holdingElement.getHeight());
            holdingElement.updateYposition(mouseY);
		}
    }
	
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
		if (holdingElement != null) {
            
			for (int i = 0; i < alerts.size(); i++) {
				AlertElement element = alerts.get(i);
				
				if(holdingElement == element) continue;
				if(element.isHovered(mouseX, mouseY)) {
					int elementIndex = i;
					int holdingIndex = alerts.indexOf(holdingElement);
					
					alerts.remove(holdingElement);
					if(elementIndex > holdingIndex) {
						alerts.add(alerts.indexOf(element)+1, holdingElement);
						break;
					}else {
						alerts.add(alerts.indexOf(element), holdingElement);
						break;
					}
				}
			}
			holdingElement = null;
		}
    }
	
	@Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        int SCROLL_SPEED = height / 50;
        
        if (scroll != 0 && !closeWarning.showElement) {
        	scrollbar.handleMouseInput(scroll);
        }
    }
	
	@Override
	public void updateScreen() {
		for (int i = 0; i < alerts.size(); i++) {
			AlertElement alert = alerts.get(i);
			List<GuiTextField> inputs = alert.getListOfTextFields();
			
			for (GuiTextField input : inputs) {
				input.updateCursorCounter();
			}
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private void SaveAlerts() {
		List<Alert> alertsList = new ArrayList<Alert>();
		// idk why i did it like this with alertsFields.size(), but because of it we have to do "i +=3" since we have 3 input fields per alert
	    for (int i = 0; i < alerts.size(); i++) {
	    	AlertElement alert = alerts.get(i);
	    	
	    	alert.time.setTextColor(14737632);
	    	
	        String trigger = alert.trigger.getText();
	        String display = alert.display.getText();
	        
	        // TODO this shit also have to be independent
	        int[] position = alerts.get(i).position;
	        float scale = alerts.get(i).scale;
	        float time = 0;
	        try {
	            time = Float.parseFloat(alert.time.getText()) * 1000;
	        } catch (NumberFormatException e) {
	            System.out.println(e);
	            alert.time.setTextColor(11217193);
	            saveButton.packedFGColour = 14258834;
	            return;
	        }
	        boolean onlyParty = ((CheckButton)alert.onlyParty).isFull();
	        boolean ignorePlayers = ((CheckButton)alert.ignorePlayers).isFull();
	        boolean isEqual = ((CheckButton)alert.isEqual).isFull();
	        alertsList.add(new Alert(trigger, display, time, onlyParty, ignorePlayers, isEqual, position, scale));
	    }
	    
	    saveButton.packedFGColour = 11131282;
	    Alerts.alertsList = alertsList;
	    Alerts.putAllImagesToCache();
	    ConfigHandler.SaveAlert(alertsList);
	}
	
	public void updateAlertsY() {
		scrollbar.updateMaxBottomScroll(Math.min(0, -(((alerts.size() * (inputHeight + inputMargin))) - (height - (height/3)))));
		int ScrollOffset = scrollbar.getOffset();
		
		int positionY = (int) (height / 3 + ScrollOffset);
		
		for (int i = 0; i < alerts.size(); i++) {
			if(alerts.get(i) == holdingElement) continue;
			int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
			alerts.get(i).updateYposition(inputFullPosition);
		}
	}
	
	private void CloseGui() {
		if(closeWarning.showElement == true) return;
		
		Location.checkTabLocation();
		// its to prevent removing waypoints from other regions, so i just remove waypoints from my region and add them updated
		List<Alert> alertsList = Alerts.alertsList;
		boolean isntEqual = false;
		
		if(alerts.size() != alertsList.size()) {
			 isntEqual = true;
		}else {
			for (int i = alerts.size() - 1; i >= 0; i--) {
				AlertElement alert = alerts.get(i);
		    	
		        String trigger = alert.trigger.getText();
		        String display = alert.display.getText(); 
		        
		        int[] position = alert.position;
		        float scale = alert.scale;
		        float time = 0;
		        try {
		            time = Float.parseFloat(alert.time.getText()) * 1000;
		        } catch (NumberFormatException e) { isntEqual = true; break; }
	
		        if(Float.compare(alertsList.get(i).position[0], position[0]) != 0) { isntEqual = true; break; }
		        if(Float.compare(alertsList.get(i).position[1], position[1]) != 0) { isntEqual = true; break; }
		        if(Float.compare(alertsList.get(i).scale, scale) != 0) { isntEqual = true; break; }
		        if(Float.compare(alertsList.get(i).getTime(), time) != 0) { isntEqual = true; break; }
		        if(!trigger.equals(alertsList.get(i).getTrigger())) { isntEqual = true; break; }
		        if(!display.equals(alertsList.get(i).getDisplay())) { isntEqual = true; break; }
		        if(alert.ignorePlayers.isFull != alertsList.get(i).ignorePlayers) { isntEqual = true; break; }
		        if(alert.isEqual.isFull != alertsList.get(i).isEqual) { isntEqual = true; break; }
		        if(alert.onlyParty.isFull != alertsList.get(i).onlyParty) { isntEqual = true; break; }
			}
		}
		
		if(isntEqual) {
			closeWarning.changeElementActive(true);
		}else {
			Minecraft.getMinecraft().thePlayer.closeScreen();
		}
	}
	
	@Override
    public void onGuiClosed() {
		holdingElement = null;
		alerts.clear();
	}
}
