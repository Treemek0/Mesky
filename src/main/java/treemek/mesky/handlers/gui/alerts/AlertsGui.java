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
import treemek.mesky.handlers.gui.buttons.CheckButton;
import treemek.mesky.handlers.gui.buttons.DeleteButton;
import treemek.mesky.handlers.gui.buttons.EditButton;
import treemek.mesky.handlers.gui.buttons.MeskyButton;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Alerts.Alert;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.Waypoint;

public class AlertsGui extends GuiScreen {
	ArrayList<GuiTextField> alertsFields;
	ArrayList<GuiTextField> timeFields;

	List<GuiButton> deleteButtonList = Lists.<GuiButton>newArrayList();
	List<GuiButton> editButtonList = Lists.<GuiButton>newArrayList();
	List<GuiButton> checksButtonList = Lists.<GuiButton>newArrayList();
	private GuiButton selectedButton; // something from GuiScreen for mouseClicked()
	float ScrollOffset = 0;
	float maxBottomScroll;
	// old input is saved because, when saving and other things region is cleared and i can't just get text from current location, since you can change region text while not being in this location
	String oldRegion;
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
	    for (GuiTextField input : alertsFields) {
	        input.drawTextBox();
	    }

	    // Reset color and blending state before drawing buttons
	    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

	    // Draw delete buttons
	    for (GuiButton button : deleteButtonList) {
	        button.drawButton(mc, mouseX, mouseY);
	    }
	    
	 // Draw delete buttons
	    for (GuiButton button : editButtonList) {
	        button.drawButton(mc, mouseX, mouseY);
	    }
	    
	    // Draw delete buttons
	    for (GuiButton button : checksButtonList) {
	        button.drawButton(mc, mouseX, mouseY);
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
        
        if(maxBottomScroll != 0) { // dont render if doesnt needed
	        // Scrollbar background
	        scrollbar_startPosition = height/3;
	        scrollbarBg_height = (height - (height / 3) - 10); // scrollbar end position is just scrollbar background height
	        
	        scrollbar_width = (int) Math.min(20, (width * 0.025));
	        
	        // Scrollbar
	        int scrollbar_height = (int) Math.max(scrollbar_width * 2.857, Math.abs((scrollbarBg_height - (height / 20)) / Math.max(1, Math.abs(maxBottomScroll) / 10)));
	        
	        int scrollbar_endPosition = height - 10 - scrollbar_height;

	        float scrollbar_percent = (maxBottomScroll != 0)?ScrollOffset / maxBottomScroll:0; // if maxBottom scroll is 0 then it cant be divided because x/0 = NaN
	        int scrollbar_positionY = (int)(scrollbar_startPosition + (scrollbar_percent * (scrollbar_endPosition - scrollbar_startPosition)));
	        scrollbar_positionY = (int) Math.max(scrollbar_startPosition, Math.min(scrollbar_positionY, scrollbar_endPosition)); // scrollbar cant go past start and end positions (its because of bugs when changing resolution)
	       
        	drawRect((int)(width * 0.95), (int)scrollbar_startPosition, (int)((width * 0.95) + scrollbar_width), (int)(scrollbar_startPosition + scrollbarBg_height), new Color(8, 7, 10, 150).getRGB());
        	
        	ResourceLocation scrollbar = new ResourceLocation(Reference.MODID, "/gui/scrollbar.png");
        	mc.getTextureManager().bindTexture(scrollbar);
        	drawModalRectWithCustomSizedTexture((int)(width * 0.95), scrollbar_positionY, 0, 0, (int) scrollbar_width, scrollbar_height, scrollbar_width, scrollbar_height);
        }
        
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    
	    alertsFields = new ArrayList<GuiTextField>();
	    deleteButtonList.clear();
	    checksButtonList.clear();
	    editButtonList.clear();
	    
	    inputHeight = ((height / 25) < 12)?12:(height / 25);
		inputMargin = ((height / 40) < 5)?5:(height / 40);
	    
		this.fontRendererObj.FONT_HEIGHT = (int)(height / 56.5);
		
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
 		maxBottomScroll = Math.min(0, -(((Alerts.alertsList.size() * (inputHeight + inputMargin))) - (height - (height/3))));
 		ScrollOffset = Math.max(ScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
 		
        
        for (int i = 0; i < Alerts.alertsList.size(); i++) {
        	// Position 0 for inputs + every input height and their bottom margin
        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
        	
        	// Check if any part of the text field is within the visible area (stop rendering inputs that go over text)
            if (inputFullPosition <= ((height / 3) - 20)) {
                continue;
            }
        	
        	// Delete button
        	this.deleteButtonList.add(new DeleteButton(0 + (10*i), (int)(width * 0.9f), inputFullPosition, inputHeight, inputHeight, ""));

        	editButtonList.add(new EditButton(5 + (10*i), (int)(width * 0.85f), inputFullPosition, inputHeight, inputHeight, ""));
        	
        	// onlyParty button
        	this.checksButtonList.add(new CheckButton(1 + (10*i), (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", Alerts.alertsList.get(i).getOnlyParty()));
        	
        	// ignorePlayers button
        	this.checksButtonList.add(new CheckButton(2 + (10*i), (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", Alerts.alertsList.get(i).getIgnorePlayers()));
        	
        	// isEqual button
        	this.checksButtonList.add(new CheckButton(3 + (10*i), (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", Alerts.alertsList.get(i).getIsEqual()));
        	
        	// trigger text input
        	GuiTextField alertTrigger = new GuiTextField(1, this.fontRendererObj, trigger_X, inputFullPosition, width / 4, inputHeight);
        	alertTrigger.setMaxStringLength(512);
            alertTrigger.setCanLoseFocus(true);
            alertTrigger.setText(Alerts.alertsList.get(i).getTrigger());
            alertsFields.add(alertTrigger);
            
            // display text input
            GuiTextField alertDisplay = new GuiTextField(1, this.fontRendererObj, display_X, inputFullPosition, width / 4, inputHeight);
            alertDisplay.setMaxStringLength(512);
            alertDisplay.setCanLoseFocus(true);
            alertDisplay.setText(Alerts.alertsList.get(i).getDisplay());
            alertsFields.add(alertDisplay);

            // time text input
            GuiTextField alertTime = new GuiTextField(2, this.fontRendererObj, time_X + 10, inputFullPosition, width / 10, inputHeight);
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
			Alerts.addAlert("", "", 1, new int[] {50,50}, 1);
	        int trigger_X = width / 6;
	    	int display_X = trigger_X + (width / 4) + 10;
	    	int time_X = display_X + (width / 4);
			this.fontRendererObj.FONT_HEIGHT = (int)(height / 56.5);
			int i = (alertsFields.size() / 3);
			int positionY = (int) (height / 3 + ScrollOffset);
			int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
        	
			// I did that instead of just initGui() so that it doesnt change back other inputs if theyre not saved
			maxBottomScroll = Math.min(0, -(((Alerts.alertsList.size() * (inputHeight + inputMargin))) - (height - (height/3))));
    		ScrollOffset = Math.max(ScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
			
    		deleteButtonList.add(new DeleteButton(0 + (10*i), (int)(width * 0.9f), inputFullPosition, inputHeight, inputHeight, ""));
    		editButtonList.add(new EditButton(5 + (10*i), (int)(width * 0.85f), inputFullPosition, inputHeight, inputHeight, ""));
        	
        	this.checksButtonList.add(new CheckButton(1 + (10*i), (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", false));
        	
        	this.checksButtonList.add(new CheckButton(2 + (10*i), (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", false));

        	this.checksButtonList.add(new CheckButton(3 + (10*i), (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", false));
        	
        	// trigger text input
        	GuiTextField alertTrigger = new GuiTextField(1, this.fontRendererObj, trigger_X, inputFullPosition, width / 4, inputHeight);
        	alertTrigger.setMaxStringLength(512);
            alertTrigger.setCanLoseFocus(true);
            alertTrigger.setText("");
            alertsFields.add(alertTrigger);
            
            // display text input
            GuiTextField alertDisplay = new GuiTextField(1, this.fontRendererObj, display_X, inputFullPosition, width / 4, inputHeight);
            alertDisplay.setMaxStringLength(512);
            alertDisplay.setCanLoseFocus(true);
            alertDisplay.setText("");
            alertsFields.add(alertDisplay);

            // time text input
            GuiTextField alertTime = new GuiTextField(2, this.fontRendererObj, time_X + 10, inputFullPosition, width / 10, inputHeight);
            alertTime.setMaxStringLength(16);
            alertTime.setCanLoseFocus(true);
            alertTime.setText("1");
            alertsFields.add(alertTime);
            return;
		}
		
		
		// Every delete button
        for (GuiButton guiButton : deleteButtonList) {
			if(guiButton.id == button.id) {
				// Removing alert from list
				int listId = button.id/10;
				Alerts.deleteAlert(listId);
	            alertsFields.remove(listId*3); // removing trigger
	            alertsFields.remove(listId*3); // removing display
	            alertsFields.remove(listId*3); // removing time
	            checksButtonList.remove(listId*3);
	            checksButtonList.remove(listId*3);
	            checksButtonList.remove(listId*3);
	            deleteButtonList.remove(listId);
	            editButtonList.remove(listId);
	            ScrollOffset += 0;
	            
	            // putting all inputs and buttons in place so theres no blank spot
	            maxBottomScroll = Math.min(0, -(((Alerts.alertsList.size() * (inputHeight + inputMargin))) - (height - (height/3))));
	    		ScrollOffset = Math.max(ScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
	    		int positionY = (int) (height / 3 + ScrollOffset);
	    		List<GuiButton> oldChecksList = new ArrayList<>(checksButtonList);
	    		deleteButtonList.clear();
	    		checksButtonList.clear();
	    		editButtonList.clear();
	    		for (int i = 0; i < alertsFields.size(); i+=3) {
	    			int whichAlert = i/3;
	    			int inputFullPosition = positionY + ((inputHeight + inputMargin) * whichAlert);
	    			alertsFields.get(i).yPosition = inputFullPosition;
	    			alertsFields.get(i+1).yPosition = inputFullPosition;
	    			alertsFields.get(i+2).yPosition = inputFullPosition;
	    			DeleteButton deleteButton = new DeleteButton(0 + (10*whichAlert), (int)(width * 0.9f), inputFullPosition, inputHeight, inputHeight, "");
	            	deleteButtonList.add(deleteButton);
	            	
	            	editButtonList.add(new EditButton(5 + (10*whichAlert), (int)(width * 0.85f), inputFullPosition, inputHeight, inputHeight, ""));
	            	
	            	int trigger_X = width/6;
	            	// np. i=3 so wchichAlert = 1, id = 11, oldChecksList.get(3) [because there are 3 checks in one alerts, so first one in second alert is 3]
	            	checksButtonList.add(new CheckButton(1 + (10*whichAlert), (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", ((CheckButton)oldChecksList.get(whichAlert*3)).isFull()));
	            	
	            	checksButtonList.add(new CheckButton(2 + (10*whichAlert), (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", ((CheckButton)oldChecksList.get((whichAlert*3)+1)).isFull()));
				
	            	checksButtonList.add(new CheckButton(3 + (10*whichAlert), (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", ((CheckButton)oldChecksList.get((whichAlert*3)+2)).isFull()));
	    		}
	    		oldChecksList.clear();
	            return;
			}
		}
        
        for (GuiButton guiButton : editButtonList) { // edit position and scale
        	if(guiButton.id == button.id) {
        		int listId = button.id/10;
        		SaveAlerts();
        		int[] position = (Alerts.alertsList.get(listId).position != null)?Alerts.alertsList.get(listId).position : new int[] {50,50};
        		Float scale = (Alerts.alertsList.get(listId).scale != null)?Alerts.alertsList.get(listId).scale : 1;
        		Alerts.editAlertPositionAndScale(alertsFields.get((listId*3)+1).getText(), position, Alerts.alertsList.get(listId).scale, listId);
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
		
		// some shit from GuiScreen probably (just override)
		if (mouseButton == 0)
        {
            for (int i = 0; i < this.deleteButtonList.size(); ++i)
            {
                GuiButton guibutton = (GuiButton)this.deleteButtonList.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
                {
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.deleteButtonList);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.button;
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                    if (this.equals(this.mc.currentScreen))
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.button, this.deleteButtonList));
                }
            }
        }
		
		// some shit from GuiScreen probably (just override)
		if (mouseButton == 0)
        {
            for (int i = 0; i < this.checksButtonList.size(); ++i)
            {
                GuiButton guibutton = (GuiButton)this.checksButtonList.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
                {
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.checksButtonList);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.button;
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                    if (this.equals(this.mc.currentScreen))
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.button, this.checksButtonList));
                }
            }
        }
		
		// some shit from GuiScreen probably (just override)
				if (mouseButton == 0)
		        {
		            for (int i = 0; i < this.editButtonList.size(); ++i)
		            {
		                GuiButton guibutton = (GuiButton)this.editButtonList.get(i);

		                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
		                {
		                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.editButtonList);
		                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
		                        break;
		                    guibutton = event.button;
		                    this.selectedButton = guibutton;
		                    guibutton.playPressSound(this.mc.getSoundHandler());
		                    this.actionPerformed(guibutton);
		                    if (this.equals(this.mc.currentScreen))
		                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.button, this.editButtonList));
		                }
		            }
		        }
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        int SCROLL_SPEED = height / 50;
        
        if (scroll != 0) {
        	if(ScrollOffset < maxBottomScroll && scroll < 0) return;
            ScrollOffset -= scroll > 0 ? -SCROLL_SPEED : SCROLL_SPEED;
            
            ScrollOffset = Math.min(0, ScrollOffset); // cant go over 0, so you cant scroll up when at first waypoint 
            // This is so you cant scroll limitless, it takes every waypoint height with their margin and removes visible inputs height so you can scroll max to how much of inputs isnt visible
    		maxBottomScroll = Math.min(0, -(((Alerts.alertsList.size() * (inputHeight + inputMargin))) - (height - (height/3))));
    		ScrollOffset = Math.max(ScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
            
    		int positionY = (int) (height / 3 + ScrollOffset);
    		List<GuiButton> oldChecksList = new ArrayList<>(checksButtonList);
    		deleteButtonList.clear();
    		checksButtonList.clear();
    		editButtonList.clear();
    		for (int i = 0; i < alertsFields.size(); i+=3) {
    			int whichAlert = i/3;
    			int inputFullPosition = positionY + ((inputHeight + inputMargin) * whichAlert);
    			alertsFields.get(i).yPosition = inputFullPosition;
    			alertsFields.get(i+1).yPosition = inputFullPosition;
    			alertsFields.get(i+2).yPosition = inputFullPosition;
    			DeleteButton deleteButton = new DeleteButton(0 + (10*whichAlert), (int)(width * 0.9f), inputFullPosition, inputHeight, inputHeight, "");
            	deleteButtonList.add(deleteButton);
            	
            	editButtonList.add(new EditButton(5 + (10*whichAlert), (int)(width * 0.85f), inputFullPosition, inputHeight, inputHeight, ""));
            	
            	int trigger_X = width/6;
            	checksButtonList.add(new CheckButton(1 + (10*whichAlert), (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", ((CheckButton)oldChecksList.get(whichAlert*3)).isFull()));
            	
            	checksButtonList.add(new CheckButton(2 + (10*whichAlert), (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", ((CheckButton)oldChecksList.get((whichAlert*3)+1)).isFull()));
			
            	checksButtonList.add(new CheckButton(3 + (10*whichAlert), (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", ((CheckButton)oldChecksList.get((whichAlert*3)+2)).isFull()));
    		}
    		oldChecksList.clear();
        }
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
	    	alertsFields.get(i + 2).setTextColor(14737632);
	    	
	        String trigger = alertsFields.get(i).getText();
	        String display = alertsFields.get(i+1).getText();
	        int[] position = Alerts.alertsList.get(i/3).position;
	        float scale = Alerts.alertsList.get(i/3).scale;
	        float time = 0;
	        try {
	            time = Float.parseFloat(alertsFields.get(i + 2).getText()) * 1000;
	        } catch (NumberFormatException e) {
	            System.out.println(e);
	            alertsFields.get(i + 2).setTextColor(11217193);
	            saveButton.packedFGColour = 14258834;
	            return;
	        }
	        boolean onlyParty = ((CheckButton)checksButtonList.get((i/3)*3)).isFull();
	        boolean ignorePlayers = ((CheckButton)checksButtonList.get(((i/3)*3)+1)).isFull();
	        boolean isEqual = ((CheckButton)checksButtonList.get((((i/3)*3))+2)).isFull();
	        alertsList.add(new Alert(trigger, display, time, onlyParty, ignorePlayers, isEqual, position, scale));
	    }
	    
	    saveButton.packedFGColour = 11131282;
	    Alerts.alertsList = alertsList;
	    Alerts.putAllImagesToCache();
	    ConfigHandler.SaveAlert(alertsList);
	}
}
