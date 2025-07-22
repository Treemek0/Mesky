package treemek.mesky.handlers.gui.alerts;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import treemek.mesky.handlers.gui.elements.ButtonWithToolkit;
import treemek.mesky.handlers.gui.elements.ScrollBar;
import treemek.mesky.handlers.gui.elements.buttons.AddButton;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.EditButton;
import treemek.mesky.handlers.gui.elements.buttons.ListBox;
import treemek.mesky.handlers.gui.elements.buttons.MeskyButton;
import treemek.mesky.handlers.gui.elements.buttons.SaveButton;
import treemek.mesky.handlers.gui.elements.sliders.Slider;
import treemek.mesky.handlers.gui.elements.buttons.ListBox.Option;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.handlers.gui.elements.warnings.CloseWarning;
import treemek.mesky.handlers.gui.waypoints.WaypointElement;
import treemek.mesky.handlers.gui.waypoints.WaypointGroupElement;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.HypixelCheck;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Alerts.Alert;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.Waypoint;

public class AlertsGui extends GuiScreen {

	public static List<AlertElement> alerts = new ArrayList<>();
	
	ScrollBar scrollbar = new ScrollBar();
	AlertElement holdingElement;
	CloseWarning closeWarning = new CloseWarning();
	
	int inputMargin = 0;
	int inputHeight = 0;
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
			
			if (alert.yPosition + alert.getHeight() <= ((height / 3))) {
                continue;
       	 	}
			
			List<TextField> inputs = alert.getListOfTextFields();
			
			for (TextField input : inputs) {
				input.drawTextBox();
			}
			
			// Reset color and blending state before drawing buttons
		    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		    GlStateManager.enableBlend();
		    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		    
		    for (GuiButton button : alert.getListOfButtons()) {
				if(button instanceof EditButton) {
					if(alert.display.getText().length() > 0 && alert.enabled.isFull()) { // if theres nothing to display then theres no need to edit
						button.enabled = true;
					}else {
						button.enabled = false;
					}
				}
					
				button.drawButton(mc, mouseX, mouseY);
			}
	    }
		
		for (int i = alerts.size()-1; i >= 0; i--) {
			AlertElement alert = alerts.get(i);
			if(alert == holdingElement) continue;

			alert.sound.drawButton(mc, mouseX, mouseY);
			
			if(!alert.enabled.isFull()) {
				drawRect(alert.xPosition, alert.yPosition-2, alert.xPosition + alert.getWidth(), alert.yPosition + alert.getHeight()+2, new Color(33, 33, 33, 180).getRGB());
			}
			
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		    GlStateManager.enableBlend();
		    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		    
		}
		
	    if(holdingElement != null) {
	    	drawRect(holdingElement.xPosition, holdingElement.yPosition - inputMargin/2, holdingElement.xPosition + holdingElement.getWidth(), holdingElement.yPosition + holdingElement.getHeight() + inputMargin/2, new Color(28, 28, 28,255).getRGB());
	    	
	    	List<TextField> inputs = holdingElement.getListOfTextFields();
			for (TextField input : inputs) {
				input.drawTextBox();
			}
			
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		    GlStateManager.enableBlend();
		    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
			
			for (GuiButton button : holdingElement.getListOfButtons()) {
				if(button instanceof EditButton) {
					if(holdingElement.display.getText().length() > 0 && holdingElement.enabled.isFull()) { // if theres nothing to display then theres no need to edit
						button.enabled = true;
					}else {
						button.enabled = false;
					}
				}
					
				button.drawButton(mc, mouseX, mouseY);
			}
			
			holdingElement.sound.drawButton(mc, mouseX, mouseY);
			
			if(!holdingElement.enabled.isFull()) {
				drawRect(holdingElement.xPosition, holdingElement.yPosition-2, holdingElement.xPosition + holdingElement.getWidth(), holdingElement.yPosition + holdingElement.getHeight()+2, new Color(28, 28, 28, 180).getRGB());
			}
	    }
	    
		drawRect(0, 0, width, height/3 - 1, new Color(33, 33, 33,255).getRGB());
		
		// Toolkit
    	if(holdingElement == null) {
			for (AlertElement alert : alerts) {
		    	for (GuiButton button : alert.getListOfButtons()) {
		    	    if (button instanceof ButtonWithToolkit && ((ButtonWithToolkit) button).shouldShowTooltip()) {
		    	    	RenderHandler.drawToolkit(button, mouseX, mouseY);
		    	    }
		    	}
			}
    	}
		
		float scale = (float) ((height*0.1f) / mc.fontRendererObj.FONT_HEIGHT) / 2;

        int textLength = mc.fontRendererObj.getStringWidth("Alerts");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) (height * 0.05f);

        RenderHandler.drawText("Alerts", titleX, titleY, scale, true, 0x3e91b5);
        
        int spaceBetween = width/60;
        
        int trigger_X = width / 6;
    	int display_X = trigger_X + (width / 4) + spaceBetween;
    	int time_X = display_X + (width / 4);
        
        
        double widthOfCheckTexts = width / 20;
        
        double checksScale = RenderHandler.getTextScale("Players", widthOfCheckTexts); // "Players" is the longest and i want all to have the same scale

        int positionY = (int)((height / 3) - RenderHandler.getTextHeight(checksScale) - 3);
        
        RenderHandler.drawText("Only", (trigger_X * 0.1) + inputHeight/2 - RenderHandler.getTextWidth("Only", checksScale)/2, positionY - RenderHandler.getTextHeight(checksScale) - 2, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("Party", (trigger_X * 0.1) + inputHeight/2 - RenderHandler.getTextWidth("Party", checksScale)/2, positionY, checksScale, true, 0x7a7a7a);
        
        RenderHandler.drawText("Ignore", (trigger_X * 0.4) + inputHeight/2 - RenderHandler.getTextWidth("Ignore", checksScale)/2, positionY - RenderHandler.getTextHeight(checksScale) - 2, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("Players", (trigger_X * 0.4) + inputHeight/2 - RenderHandler.getTextWidth("Players", checksScale)/2, positionY, checksScale, true, 0x7a7a7a);
        
        RenderHandler.drawText("Equal", (trigger_X * 0.7) + inputHeight/2 - RenderHandler.getTextWidth("Equal", checksScale)/2, positionY, checksScale, true, 0x7a7a7a);
        
        RenderHandler.drawText("Trigger", width / 6, positionY, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("Display", display_X, positionY, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("Time [s]", time_X + 10, positionY, checksScale, true, 0x7a7a7a);
        
        scrollbar.updateScrollBar((int) Math.min(20, (width * 0.025)), (height - (height / 3) - 10), (int)(width * 0.95), height/3);
        if(scrollbar.isScrolling()) {
        	snapToAlertsY();
        }else {
        	updateAlertsY();
        }
        scrollbar.drawScrollBar();
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	    
	    if(holdingElement == null) {
    		for (GuiButton guiButton : buttonList) {
    			if (guiButton instanceof ButtonWithToolkit && ((ButtonWithToolkit) guiButton).shouldShowTooltip()) {
	    	    	RenderHandler.drawToolkit(guiButton, mouseX, mouseY);
	    	    }
			}
	    }
	    
	    closeWarning.drawElement(mc, mouseX, mouseY);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    buttonList.clear();
	    closeWarning = new CloseWarning();
	    
		inputHeight = ((height / 25) < 12)?12:(height / 25);
		inputMargin = ((height / 40) < 5)?5:(height / 40);
		
		int spaceBetween = width/60;
		
        int positionY = height / 3;
        int trigger_X = width / 6;
    	int display_X = trigger_X + (width / 4) + spaceBetween;
    	int time_X = display_X + (width / 4);
    	int editX = (int) (time_X + width/12 + spaceBetween + 10);
    	
        float mainButtonsScale = (float) ((height*0.1f) / mc.fontRendererObj.FONT_HEIGHT) / 2;
        int mainButtonsSize = (int) RenderHandler.getTextHeight(mainButtonsScale);
        int mainButtonsY = (int) (height * 0.05f);
        
        // Save button
        saveButton = new SaveButton(-1, (int)(width * 0.9f) - mainButtonsSize/2, mainButtonsY, mainButtonsSize ,mainButtonsSize, "Save");
        this.buttonList.add(saveButton);
        
        // New waypoint button
        this.buttonList.add(new AddButton(-2, (int)(width * 0.1f) - mainButtonsSize/2, mainButtonsY, mainButtonsSize, mainButtonsSize, "New alert"));
        
    	// This is so you cant scroll limitless, it takes every waypoint height with their margin and removes visible inputs height so you can scroll max to how much of inputs isnt visible
        scrollbar.updateVisibleHeight(height - (height/3));
        scrollbar.updateContentHeight((Alerts.alertsList.size() * (inputHeight + inputMargin)));
        //	scrollbar.updateMaxBottomScroll(((Alerts.alertsList.size() * (inputHeight + inputMargin))) - (height - (height/3)));
 		int ScrollOffset = scrollbar.getOffset();
 		
 		if(alerts.isEmpty()) {	
	        for (int i = 0; i < Alerts.alertsList.size(); i++) {
	        	// Position 0 for inputs + every input height and their bottom margin
	        	int inputFullPosition = positionY + ((inputHeight*2 + 5 + inputMargin) * i);
	        	
	        	CheckButton enabled = new CheckButton(0, editX + (spaceBetween/2 + inputHeight)*2, inputFullPosition, inputHeight, inputHeight, "", Alerts.alertsList.get(i).enabled);
	        	
	        	// Delete button
	        	DeleteButton deleteButton = new DeleteButton(0, editX + spaceBetween/2 + inputHeight, inputFullPosition, inputHeight, inputHeight, "Delete alert");
	        	deleteButton.enabled = Alerts.alertsList.get(i).enabled;
	        	
	        	EditButton editButton = new EditButton(1, editX, inputFullPosition, inputHeight, inputHeight, "Edit position");
	        	editButton.enabled = Alerts.alertsList.get(i).enabled;
	        	
	        	// onlyParty button
	        	CheckButton onlyParty = new CheckButton(2, (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", Alerts.alertsList.get(i).getOnlyParty());
	        	
	        	// ignorePlayers button
	        	CheckButton ignorePlayers = new CheckButton(3, (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", Alerts.alertsList.get(i).getIgnorePlayers());
	        	
	        	// isEqual button
	        	CheckButton isEqual = new CheckButton(4, (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", Alerts.alertsList.get(i).getIsEqual());
	        	
	        	// trigger text input
	        	TextField alertTrigger = new TextField(1, trigger_X, inputFullPosition, width / 4, inputHeight);
	        	alertTrigger.setMaxStringLength(512);
	            alertTrigger.setCanLoseFocus(true);
	            alertTrigger.setText(Alerts.alertsList.get(i).getTrigger());
	            alertTrigger.setCursorPositionZero();
	            
	            // display text input
	            TextField alertDisplay = new TextField(1, display_X, inputFullPosition, width / 4, inputHeight);
	            alertDisplay.setColoredField(true);
	            alertDisplay.setMaxStringLength(512);
	            alertDisplay.setCanLoseFocus(true);
	            alertDisplay.setText(Alerts.alertsList.get(i).getDisplay());
	            alertDisplay.setCursorPositionZero();
	
	            // time text input
	            TextField alertTime = new TextField(2, time_X + 10, inputFullPosition, width / 12, inputHeight);
	            alertTime.setMaxStringLength(16);
	            alertTime.setCanLoseFocus(true);
	            alertTime.setText(Float.toString(Alerts.alertsList.get(i).getTime() / 1000));
	            alertTime.setCursorPositionZero();
	            
	            ListBox sound = new ListBox(0, trigger_X, 0, width/8, inputHeight, "Sound", new ArrayList<>(Arrays.asList(new Option("minecraft:random.anvil_land", "minecraft:random.anvil_land"), new Option("minecraft:random.explode", "minecraft:random.explode"), new Option("mesky:alarm", "mesky:alarm"), new Option("mesky:error", "mesky:error"), new Option("mesky:levelup", "mesky:levelup"), new Option("mesky:bop", "mesky:bop"))), "", Alerts.alertsList.get(i).sound);
	            
	            Slider volume = new Slider(0, trigger_X + width/8 + 5, 0, width/8, (int)inputHeight, "Volume", 0.1f, 1, 0.1f);
	            volume.setValue(Alerts.alertsList.get(i).volume);
	            
	            Slider pitch = new Slider(0, display_X, 0, width/8, (int)inputHeight, "Pitch", 0.5f, 2, 0.1f);
	            pitch.setValue(Alerts.alertsList.get(i).pitch);
	            
	            alerts.add(new AlertElement(alertTrigger, alertDisplay, alertTime, sound, volume, pitch, deleteButton, editButton, onlyParty, ignorePlayers, isEqual, Alerts.alertsList.get(i).position, Alerts.alertsList.get(i).scale, enabled, inputMargin));
	        }
 		}else {
	 		for (int i = 0; i < alerts.size(); i++) {
	        	// Position 0 for inputs + every input height and their bottom margin
	 			int inputFullPosition = positionY + ((inputHeight*2 + 5 + inputMargin) * i);
	        	
	        	// Delete button
	        	DeleteButton deleteButton = new DeleteButton(0, editX + (spaceBetween/2 + inputHeight), inputFullPosition, inputHeight, inputHeight, "Delete alert");
	        	deleteButton.enabled = alerts.get(i).enabled.isFull();
	        	
	        	EditButton editButton = new EditButton(1, editX, inputFullPosition, inputHeight, inputHeight, "Edit position");
	        	editButton.enabled = alerts.get(i).enabled.isFull();
	        	
	        	CheckButton enabled = new CheckButton(0, editX + (spaceBetween/2 + inputHeight)*2, inputFullPosition, inputHeight, inputHeight, "", alerts.get(i).enabled.isFull());
	        	
	        	// onlyParty button
	        	CheckButton onlyParty = new CheckButton(2, (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", alerts.get(i).onlyParty.isFull());
	        	
	        	// ignorePlayers button
	        	CheckButton ignorePlayers = new CheckButton(3, (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", alerts.get(i).ignorePlayers.isFull());
	        	
	        	// isEqual button
	        	CheckButton isEqual = new CheckButton(4, (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", alerts.get(i).isEqual.isFull());
	        	
	        	// trigger text input
	        	TextField alertTrigger = new TextField(1, trigger_X, inputFullPosition, width / 4, inputHeight);
	        	alertTrigger.setMaxStringLength(512);
	            alertTrigger.setCanLoseFocus(true);
	            alertTrigger.setText(alerts.get(i).trigger.getText());
	            alertTrigger.setCursorPositionZero();
	            
	            // display text input
	            TextField alertDisplay = new TextField(1, display_X, inputFullPosition, width / 4, inputHeight);
	            alertDisplay.setColoredField(true);
	            alertDisplay.setMaxStringLength(512);
	            alertDisplay.setCanLoseFocus(true);
	            alertDisplay.setText(alerts.get(i).display.getText());
	            alertDisplay.setCursorPositionZero();
	
	            // time text input
	            TextField alertTime = new TextField(2, time_X + 10, inputFullPosition, width / 12, inputHeight);
	            alertTime.setMaxStringLength(16);
	            alertTime.setCanLoseFocus(true);
	            alertTime.setText(alerts.get(i).time.getText());
	            alertTime.setCursorPositionZero();
	            
	            ListBox sound = new ListBox(0, trigger_X, 0, width/8, inputHeight, "Sound", new ArrayList<>(Arrays.asList(new Option("minecraft:random.anvil_land", "minecraft:random.anvil_land"),  new Option("minecraft:random.explode", "minecraft:random.explode"), new Option("mesky:alarm", "mesky:alarm"), new Option("mesky:error", "mesky:error"), new Option("mesky:levelup", "mesky:levelup"), new Option("mesky:bop", "mesky:bop"))), "", alerts.get(i).sound.getCurrentArgument());
	            
	            Slider volume = new Slider(0, trigger_X + width/8 + 5, 0, width/8, (int)inputHeight, "Volume", 0.1f, 1, 0.1f);
	            volume.setValue(alerts.get(i).volume.getValue());
	            
	            Slider pitch = new Slider(0, display_X, 0, width/8, (int)inputHeight, "Pitch", 0.5f, 2, 0.1f);
	            pitch.setValue(alerts.get(i).pitch.getValue());
	            
	            alerts.set(i, new AlertElement(alertTrigger, alertDisplay, alertTime, sound, volume, pitch, deleteButton, editButton, onlyParty, ignorePlayers, isEqual, alerts.get(i).position, alerts.get(i).scale, enabled, inputMargin));
	        }
 		}
 		snapToAlertsY();
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
			snapToAlertsY();
			int spaceBetween = width/60;
			
			int positionY = height / 3;
	        int trigger_X = width / 6;
	    	int display_X = trigger_X + (width / 4) + spaceBetween;
	    	int time_X = display_X + (width / 4);
	    	int editX = (int) (time_X + width/12 + spaceBetween*1.5f);
	    	
	    	int topOfWaypoints = height/3 - inputHeight*2 - inputMargin;
			
	    	
			DeleteButton deleteButton = new DeleteButton(0, editX + spaceBetween/2 + inputHeight, 0, inputHeight, inputHeight, "Delete alert");
			
			EditButton editButton = new EditButton(1, editX, 0, inputHeight, inputHeight, "Edit position");
        	
			CheckButton enabled = new CheckButton(0, editX + (spaceBetween/2 + inputHeight)*2, 0, inputHeight, inputHeight, "", true);
			
			CheckButton onlyParty = new CheckButton(2, (int) (trigger_X * 0.1), 0, inputHeight, inputHeight, "", false);
        	
			CheckButton ignorePlayers = new CheckButton(3, (int) (trigger_X * 0.4), 0, inputHeight, inputHeight, "", false);

			CheckButton isEqual = new CheckButton(4, (int) (trigger_X * 0.7), 0, inputHeight, inputHeight, "", false);
        	
        	// trigger text input
        	TextField alertTrigger = new TextField(1, trigger_X, topOfWaypoints, width / 4, inputHeight);
        	alertTrigger.setMaxStringLength(512);
            alertTrigger.setCanLoseFocus(true);
            alertTrigger.setText("");
            
            // display text input
            TextField alertDisplay = new TextField(1, display_X, 0, width / 4, inputHeight);
            alertDisplay.setColoredField(true);
            alertDisplay.setMaxStringLength(512);
            alertDisplay.setCanLoseFocus(true);
            alertDisplay.setText("");

            // time text input
            TextField alertTime = new TextField(2, time_X + 10, 0, width / 12, inputHeight);
            alertTime.setMaxStringLength(16);
            alertTime.setCanLoseFocus(true);
            alertTime.setText("1");
            
            ListBox sound = new ListBox(0, trigger_X, 0, width/8, inputHeight, "Sound", new ArrayList<>(Arrays.asList(new Option("minecraft:random.anvil_land", "minecraft:random.anvil_land"), new Option("minecraft:random.explode", "minecraft:random.explode"), new Option("mesky:alarm", "mesky:alarm"), new Option("mesky:error", "mesky:error"), new Option("mesky:levelup", "mesky:levelup"), new Option("mesky:bop", "mesky:bop"))), "", "minecraft:random.anvil_land");
            
            Slider volume = new Slider(0, trigger_X + width/8 + 5, 0, width/8, (int)inputHeight, "Volume", 0.1f, 1, 0.1f);
            volume.setValue(1);
            
            Slider pitch = new Slider(0, display_X, 0, width/8, (int)inputHeight, "Pitch", 0.5f, 2, 0.1f);
            pitch.setValue(1);
            
            alerts.add(0, new AlertElement(alertTrigger, alertDisplay, alertTime, sound, volume, pitch, deleteButton, editButton, onlyParty, ignorePlayers, isEqual, new Float[] {50f,50f}, 1f, enabled, inputMargin));
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
        		Float[] position = (alert.position != null)?alert.position : new Float[] {50f,50f};
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
				List<TextField> inputs = alert.getListOfTextFields();
				
				alert.sound.textboxKeyTyped(typedChar, keyCode);
				
				for (TextField input : inputs) {
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
	
	int offsetY = 0;
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(!closeWarning.showElement) {
			
			saveButton.packedFGColour = 14737632;
			
			for (int i = 0; i < alerts.size(); i++) {
				AlertElement alert = alerts.get(i);
				boolean isAnythingPressed = false;
				
				if (mouseY <= ((height / 3))) {
					List<TextField> inputs = alert.getListOfTextFields();
					for (TextField input : inputs) {
						input.setCursorPositionZero();
						input.setFocused(false);
					}
					
	                 break;
	        	 }
				
				if(alert.enabled.isFull()) {
					if(alert.sound.mousePressed(mouseX, mouseY, mouseButton)) {
						for (int j = 0; j < alerts.size(); j++) {
							if(alerts.get(j) == alert) continue;
							alerts.get(j).sound.closeList();
						}
						
						List<TextField> inputs = alert.getListOfTextFields();
						for (TextField input : inputs) {
							input.setCursorPositionZero();
							input.setFocused(false);
						}
						
						return;
					}
					
					List<TextField> inputs = alert.getListOfTextFields();
					for (TextField input : inputs) {
						if (mouseX >= input.xPosition && mouseX <= input.xPosition + input.width && mouseY >= input.yPosition && mouseY <= input.yPosition + input.height) {
							input.mouseClicked(mouseX, mouseY, mouseButton);
							isAnythingPressed = true;
						}else {
							input.setCursorPositionZero();
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
			            
			            alert.deleteButton.enabled = alert.enabled.isFull();
			            alert.editButton.enabled = alert.enabled.isFull(); 
			        }
				}else {
					if(alert.enabled.mousePressed(mc, mouseX, mouseY)) {
	            		isAnythingPressed = true;
	            		alert.enabled.playPressSound(Minecraft.getMinecraft().getSoundHandler());
		                this.actionPerformed(alert.enabled);
		                alert.deleteButton.enabled = alert.enabled.isFull();
			            alert.editButton.enabled = alert.enabled.isFull(); 
					};
				}
				
				if(alert.isHovered(mouseX, mouseY)) {
					if(!isAnythingPressed) {
						holdingElement = alert;
						offsetY = mouseY - alert.yPosition;
					}
				}
			}
			
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
		
		closeWarning.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
		if (holdingElement != null) {
			mouseY = Math.min(Math.max(mouseY - offsetY, height/3 - 1), height - holdingElement.getHeight());
            holdingElement.updateYposition(mouseY, inputHeight);
		}else {
			for (int i = 0; i < alerts.size(); i++) {
				alerts.get(i).volume.mouseClickMoved(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
				alerts.get(i).pitch.mouseClickMoved(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
			}
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
					int sidePlus = (element.isHigherHalfHovered(mouseX, mouseY))?-1:0;

					alerts.remove(holdingElement);
					alerts.add(alerts.indexOf(element)+1+sidePlus, holdingElement);
				}else if(mouseY > alerts.get(alerts.size()-1).yPosition + alerts.get(alerts.size()-1).getHeight()) {
					alerts.remove(holdingElement);
					alerts.add(alerts.size(), holdingElement);
					holdingElement = null;
					break;
				}
			}
			holdingElement = null;
		}else {
			for (int i = 0; i < alerts.size(); i++) {
				alerts.get(i).volume.mouseReleased(mouseX, mouseY);
				alerts.get(i).pitch.mouseReleased(mouseX, mouseY);
			}
		}
    }
	
	@Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if(!closeWarning.showElement) {
	        scrollbar.handleMouseInput();
        }
    }
	
	@Override
	public void updateScreen() {
		for (int i = 0; i < alerts.size(); i++) {
			AlertElement alert = alerts.get(i);
			List<TextField> inputs = alert.getListOfTextFields();
			
			for (TextField input : inputs) {
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
	        String sound = alert.sound.getCurrentArgument();
	        double volume = alert.volume.getValue();
	        double pitch = alert.pitch.getValue();
	        
	        Float[] position = alerts.get(i).position;
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
	        boolean enabled = ((CheckButton)alert.enabled).isFull();
	        alertsList.add(new Alert(trigger, display, time, onlyParty, ignorePlayers, isEqual, position, scale, sound, (float)volume, (float)pitch, enabled));
	    }
	    
	    saveButton.packedFGColour = 11131282;
	    Alerts.alertsList = alertsList;
	    Alerts.putAllImagesToCache();
	    ConfigHandler.SaveAlert(alertsList);
	}
	
	public void updateAlertsY() {
		if(!alerts.isEmpty()) {
			if(alerts.get(alerts.size() - 1).sound.isOpened()){
				scrollbar.updateContentHeight(((alerts.size() * (inputHeight*2 + 5 + inputMargin))) + (alerts.get(alerts.size() - 1).sound.endY - alerts.get(alerts.size() - 1).sound.yPosition));
			}else {
				scrollbar.updateContentHeight((alerts.size() * (inputHeight*2 + 5 + inputMargin)));
			}
		}else {
			scrollbar.updateContentHeight((alerts.size() * (inputHeight*2 + 5 + inputMargin)));
		}
		
		int ScrollOffset = scrollbar.getOffset();
		
		int positionY = (int) (height / 3 + ScrollOffset);
		
		for (int i = 0; i < alerts.size(); i++) {
			AlertElement element = alerts.get(i);
			if(element == holdingElement) continue;
			int inputFullPosition = positionY + ((inputHeight*2 + 5 + inputMargin) * i);

			double l = Math.signum((inputFullPosition - element.yPositionD)) * (240f/Minecraft.getDebugFPS());
			if (Math.abs(inputFullPosition - element.yPositionD) > element.getHeight() * 2) l *= 4;
			
			if(Math.abs(l) > Math.abs(element.yPositionD - inputFullPosition)) {
				element.updateYposition(inputFullPosition, inputHeight);
			}else {
				element.updateYposition(element.yPositionD + l, inputHeight);
			}
		}
	}
	
	public void snapToAlertsY() {
		if(!alerts.isEmpty()) {
			if(alerts.get(alerts.size() - 1).sound.isOpened() || (alerts.size() >= 2 && alerts.get(alerts.size() - 2).sound.isOpened())){
				scrollbar.updateContentHeight(((alerts.size() * (inputHeight*2 + 5 + inputMargin))) + (alerts.get(alerts.size() - 1).sound.endY - alerts.get(alerts.size() - 1).sound.yPosition));
			}else {
				scrollbar.updateContentHeight((alerts.size() * (inputHeight*2 + 5 + inputMargin)));
			}
		}else {
			scrollbar.updateContentHeight((alerts.size() * (inputHeight*2 + 5 + inputMargin)));
		}
		int ScrollOffset = scrollbar.getOffset();
		
		int positionY = (int) (height / 3 + ScrollOffset);
		
		for (int i = 0; i < alerts.size(); i++) {
			if(alerts.get(i) == holdingElement) continue;
			int inputFullPosition = positionY + ((inputHeight*2 + 5 + inputMargin) * i);
			alerts.get(i).updateYposition(inputFullPosition, inputHeight);
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
		        
		        Float[] position = alert.position;
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
		        if(alert.ignorePlayers.isFull() != alertsList.get(i).ignorePlayers) { isntEqual = true; break; }
		        if(alert.isEqual.isFull() != alertsList.get(i).isEqual) { isntEqual = true; break; }
		        if(alert.onlyParty.isFull() != alertsList.get(i).onlyParty) { isntEqual = true; break; }
		        if(alert.enabled.isFull() != alertsList.get(i).enabled) { isntEqual = true; break; }
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
