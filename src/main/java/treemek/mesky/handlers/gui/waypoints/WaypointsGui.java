package treemek.mesky.handlers.gui.waypoints;

import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.ibm.icu.util.RangeValueIterator.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiScrollingList;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.alerts.AlertElement;
import treemek.mesky.handlers.gui.elements.ColorPicker;
import treemek.mesky.handlers.gui.elements.ScrollBar;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.EditButton;
import treemek.mesky.handlers.gui.elements.buttons.MeskyButton;
import treemek.mesky.handlers.gui.elements.sliders.Slider;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.handlers.gui.elements.warnings.ChangingRegionWarning;
import treemek.mesky.handlers.gui.elements.warnings.CloseWarning;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.HypixelCheck;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.MacroWaypoints;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.Waypoint;

public class WaypointsGui extends GuiScreen {
	public static GuiTextField region;
	private GuiButton saveButton;
	
	public static List<WaypointElement> waypoints = new ArrayList<>();
	
	public ScrollBar scrollbar = new ScrollBar();
	CloseWarning closeWarning = new CloseWarning();
	ChangingRegionWarning changingRegionWarning = new ChangingRegionWarning();
	
	WaypointElement holdingElement;
	
	int inputMargin;
	int inputHeight;
	private float scrollbar_startPosition;
	private float scrollbarBg_height;
	private float scrollbar_width;


	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		
		drawRect(0, height/3 - 1, width, height, new Color(33, 33, 33,255).getRGB());
		
		// Draw text fields first
		for (int i = 0; i < waypoints.size(); i++) {
			WaypointElement waypoint = waypoints.get(i);
			if(waypoint == holdingElement) continue;
			
			if (waypoint.yPosition + waypoint.getHeight() <= ((height / 3))) {
                continue;
       	 	}
			
			List<TextField> inputs = waypoint.getListOfTextFields();
			
			for (TextField input : inputs) {
				input.drawTextBox();
			}
			
			waypoint.color.drawTextBox();
			
			// Reset color and blending state before drawing buttons
		    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		    GlStateManager.enableBlend();
		    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		    
		    for (GuiButton button : waypoints.get(i).getListOfButtons()) {	
				button.drawButton(mc, mouseX, mouseY);
			}
		    
		    waypoints.get(i).scale.drawButton(mc, mouseX, mouseY);
		    
		    if(!waypoint.enabled.isFull()) {
				drawRect(waypoint.xPosition, waypoint.yPosition-2, waypoint.xPosition + waypoint.getWidth(), waypoint.yPosition + waypoint.getHeight()+2, new Color(33, 33, 33, 180).getRGB());
			}
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
				button.drawButton(mc, mouseX, mouseY);
			}
			
			holdingElement.color.drawTextBox();
			holdingElement.scale.drawButton(mc, mouseX, mouseY);
			
			if(!holdingElement.enabled.isFull()) {
				drawRect(holdingElement.xPosition, holdingElement.yPosition-2, holdingElement.xPosition + holdingElement.getWidth(), holdingElement.yPosition + holdingElement.getHeight()+2, new Color(28, 28, 28, 180).getRGB());
			}
	    }
    	
		drawRect(0, 0, width, height/3 - 1, new Color(33, 33, 33,255).getRGB());
		
		float scale = (float) ((height*0.1f) / mc.fontRendererObj.FONT_HEIGHT) / 2;

        int textLength = mc.fontRendererObj.getStringWidth("Waypoints");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) (height * 0.05f);

        RenderHandler.drawText("Waypoints", titleX, titleY, scale, true, 0x3e91b5);
        
        if(HypixelCheck.isOnHypixel()) {
        	region.drawTextBox(); // Region input should be only visible on Hypixel (its in right upper corner)
    	}
        
        int scale_X = width / 20 + inputHeight + 10;
        int name_X = scale_X + width / 8 + 5;
    	int coords_X = name_X + (width/4) + 5;
    	int coords_width = width / 10;
        

 	    double widthOfCheckTexts = width / 20;
        
        double checksScale = RenderHandler.getTextScale("Players", widthOfCheckTexts); // "Players" is the longest in Alerts and i want all to have the same scale

        int positionY = (int)((height / 3) - RenderHandler.getTextHeight(checksScale) - 3);

        RenderHandler.drawText("Scale", scale_X, positionY, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("Name", name_X, positionY, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("X", coords_X, positionY, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("Y", coords_X + coords_width + 5, positionY, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("Z", coords_X + (coords_width*2) + 10, positionY, checksScale, true, 0x7a7a7a);
        
        scrollbar.updateScrollBar((int)Math.min(20, (width * 0.025)), (height - (height / 3) - 10), (int)(width * 0.95), height/3);
        if(scrollbar.isScrolling()) {
        	snapToWaypointY();
        }else {
        	updateWaypointsY();
        }
        scrollbar.drawScrollBar();
       
	    super.drawScreen(mouseX, mouseY, partialTicks);
	    
	    changingRegionWarning.drawElement(mc, mouseX, mouseY);
        closeWarning.drawElement(mc, mouseX, mouseY);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    buttonList.clear();
	    closeWarning = new CloseWarning();
	    changingRegionWarning = new ChangingRegionWarning();
	    
        int checkX = (int)(width / 4);
        int buttonWidth = 20;
        int buttonHeight = 20;
        
        
        // Save button
        int mainButtonsY = Math.max(22, (height/15));
        saveButton = new MeskyButton(-1, (int)(width * 0.8f), mainButtonsY, (int)(width * 0.2f), 20, "Save");
        this.buttonList.add(saveButton);
        
        // New waypoint button
        this.buttonList.add(new MeskyButton(-2, 0, mainButtonsY, (int)(width * 0.2f), 20, "New waypoint"));
        
        // Updating location from tab
        Location.checkTabLocation();
        
        // Region text input
        if(region == null) {
	        region = new GuiTextField(-1, this.fontRendererObj, (int)(width * 0.8f), 1, width / 4, 20);
	        region.setMaxStringLength(30);
	        region.setCanLoseFocus(true);
	        region.setText(Locations.currentLocationText);
        }else {
        	region.xPosition = (int)(width * 0.8f);
        	region.yPosition = 1;
        	region.width = width / 4;
        	region.height = 20;
        }
        
		inputHeight = ((height / 25) < 12)?12:(height / 25);
		inputMargin = ((height / 40) < 5)?5:(height / 40);
        
        int waypointColor_X = width / 20;
        int waypointScale_X = waypointColor_X + inputHeight + 10;
        int waypointName_X = waypointScale_X + width / 8 + 5;
		int coords_X = waypointName_X + (width/4) + 5;
		int coord_Width = width / 10;

		
		scrollbar.updateVisibleHeight(height - (height/3));
		scrollbar.updateContentHeight((Waypoints.GetLocationWaypoints().size() * (inputHeight + inputMargin)));
		int ScrollOffset = scrollbar.getOffset(); // so scrolloffset doesnt go below maxbottomscroll
		
        int positionY = (int) (height / 3 + ScrollOffset);
		
        if(waypoints.isEmpty()) {
	        for (int i = 0; i < Waypoints.GetLocationWaypoints().size(); i++) {
	        	// Position 0 for inputs + every input height and their bottom margin
	        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
	        	
	        	DeleteButton deleteButton = new DeleteButton(0 + (5*i), (int)(width * 0.85f), inputFullPosition, inputHeight, inputHeight, "");
	        	deleteButton.enabled = Waypoints.GetLocationWaypoints().get(i).enabled;
	        	
	        	CheckButton enabled = new CheckButton(0, (int)(width * 0.9f), inputFullPosition, inputHeight, inputHeight, "", Waypoints.GetLocationWaypoints().get(i).enabled);
	        	
	    		// Name text input
	        	TextField waypointName = new TextField(0, waypointName_X, inputFullPosition, width / 4, inputHeight);
	        	waypointName.setColoredField(true);
	            waypointName.setMaxStringLength(512);
	            waypointName.setCanLoseFocus(true);
	            waypointName.setText(Waypoints.GetLocationWaypoints().get(i).name);
	            
	            
	            // color text input
	            ColorPicker colorPicker = new ColorPicker(0, waypointColor_X, 0, inputHeight);	        	
	            colorPicker.setText(Waypoints.GetLocationWaypoints().get(i).color);
	            colorPicker.enabled = Waypoints.GetLocationWaypoints().get(i).enabled;
	            
	            // scale slider
	            Slider scale = new Slider(0, waypointScale_X, inputFullPosition, width / 8, inputHeight, "", 0.5f, 5, 0.1f);
	            scale.setValue(Waypoints.GetLocationWaypoints().get(i).scale);
	            
	            // X coordinate input
	            TextField waypointX = new TextField(2, coords_X, inputFullPosition, coord_Width, inputHeight);
	            waypointX.setMaxStringLength(16);
	            waypointX.setCanLoseFocus(true);
	            waypointX.setText(Float.toString(Waypoints.GetLocationWaypoints().get(i).coords[0]));
	            
	            // Y coordinate input
	            TextField waypointY = new TextField(3, coords_X + coord_Width + 5, inputFullPosition, coord_Width, inputHeight);
	            waypointY.setMaxStringLength(16);
	            waypointY.setCanLoseFocus(true);
	            waypointY.setText(Float.toString(Waypoints.GetLocationWaypoints().get(i).coords[1]));
	            
	            // Z coordinate input
	            TextField waypointZ = new TextField(4, coords_X + (width / 5) + 10, inputFullPosition, coord_Width, inputHeight);
	            waypointZ.setMaxStringLength(16);
	            waypointZ.setCanLoseFocus(true);
	            waypointZ.setText(Float.toString(Waypoints.GetLocationWaypoints().get(i).coords[2]));
	
	            
	            waypoints.add(new WaypointElement(waypointName, colorPicker, scale, waypointX, waypointY, waypointZ, deleteButton, enabled, inputMargin));
	        }
        }else {
        	for (int i = 0; i < waypoints.size(); i++) {
	        	// Position 0 for inputs + every input height and their bottom margin
	        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
	        	
	        	DeleteButton deleteButton = new DeleteButton(0 + (5*i), (int)(width * 0.85f), inputFullPosition, inputHeight, inputHeight, "");
	        	deleteButton.enabled = waypoints.get(i).enabled.isFull();
	        	
	        	CheckButton enabled = new CheckButton(0, (int)(width * 0.9f), inputFullPosition, inputHeight, inputHeight, "", waypoints.get(i).enabled.isFull());
	        	
	        	// color text input
	            ColorPicker colorPicker = new ColorPicker(0, waypointColor_X, 0, inputHeight);
	            colorPicker.setText(waypoints.get(i).color.getColorString());
	        	colorPicker.enabled = waypoints.get(i).enabled.isFull();
	            
	    		// Name text input
	        	TextField waypointName = new TextField(0, waypointName_X, inputFullPosition, width / 4, inputHeight);
	        	waypointName.setColoredField(true);
	            waypointName.setMaxStringLength(512);
	            waypointName.setCanLoseFocus(true);
	            waypointName.setText(waypoints.get(i).name.getText());
	            
	            // scale slider
	            Slider scale = new Slider(0, waypointScale_X, inputFullPosition, width / 8, inputHeight, "", 0.5f, 5, 0.1f);
	            scale.setValue(waypoints.get(i).scale.getValue());
	            
	            // X coordinate input
	            TextField waypointX = new TextField(2, coords_X, inputFullPosition, coord_Width, inputHeight);
	            waypointX.setMaxStringLength(16);
	            waypointX.setCanLoseFocus(true);
	            waypointX.setText(waypoints.get(i).x.getText());
	            
	            // Y coordinate input
	            TextField waypointY = new TextField(3, coords_X + coord_Width + 5, inputFullPosition, coord_Width, inputHeight);
	            waypointY.setMaxStringLength(16);
	            waypointY.setCanLoseFocus(true);
	            waypointY.setText(waypoints.get(i).y.getText());
	            
	            // Z coordinate input
	            TextField waypointZ = new TextField(4, coords_X + (width / 5) + 10, inputFullPosition, coord_Width, inputHeight);
	            waypointZ.setMaxStringLength(16);
	            waypointZ.setCanLoseFocus(true);
	            waypointZ.setText(waypoints.get(i).z.getText());
	
	            
	            waypoints.set(i, new WaypointElement(waypointName, colorPicker, scale, waypointX, waypointY, waypointZ, deleteButton, enabled, inputMargin));
	        }
        }
        
        snapToWaypointY();
	}
	
	@Override
    protected void actionPerformed(GuiButton button) {
		if(button == saveButton) {
			// Save button
			SaveWaypoints();
			return;
		}
		
		if(button.id == -2) {
			// Add waypoint button
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			float x = Float.parseFloat(String.format("%.2f", player.posX).replace(",", "."));
			float y = Float.parseFloat(String.format("%.2f", player.posY).replace(",", "."));
			float z = Float.parseFloat(String.format("%.2f", player.posZ).replace(",", "."));

			int waypointColor_X = width / 20;
	        int waypointScale_X = waypointColor_X + inputHeight + 10;
	        int waypointName_X = waypointScale_X + width / 8 + 5;
			int coords_X = waypointName_X + (width/4) + 5;
			int coord_Width = width / 10;
			
			int topOfWaypoints = height/3 - inputHeight - inputMargin;
    		
        	DeleteButton deleteButton = new DeleteButton(0, (int)(width * 0.85f), 0, inputHeight, inputHeight, "");
        	
        	CheckButton enabled = new CheckButton(0, (int)(width * 0.9f), 0, inputHeight, inputHeight, "", true);
        	
    		// Name text input
        	TextField waypointName = new TextField(0, waypointName_X, topOfWaypoints, width / 4, inputHeight);
        	waypointName.setColoredField(true);
            waypointName.setMaxStringLength(512);
            waypointName.setCanLoseFocus(true);
            waypointName.setText("Name");
            
            ColorPicker colorPicker = new ColorPicker(0, waypointColor_X, 0, inputHeight);
            colorPicker.setText("ffffff");
            
            // scale slider
            Slider scale = new Slider(0, waypointScale_X, 0, width / 8, inputHeight, "", 0.5f, 5, 0.1f);
            scale.setValue(1);
            
            // X coordinate input
            TextField waypointX = new TextField(2, coords_X, 0, coord_Width, inputHeight);
            waypointX.setMaxStringLength(16);
            waypointX.setCanLoseFocus(true);
            waypointX.setText(Float.toString(x));
            
            // Y coordinate input
            TextField waypointY = new TextField(3, coords_X + coord_Width + 5, 0, coord_Width, inputHeight);
            waypointY.setMaxStringLength(16);
            waypointY.setCanLoseFocus(true);
            waypointY.setText(Float.toString(y));

        	// Z coordinate input
            TextField waypointZ = new TextField(4, coords_X + (width / 5) + 10, 0, coord_Width, inputHeight);
            waypointZ.setMaxStringLength(16);
            waypointZ.setCanLoseFocus(true);
            waypointZ.setText(Float.toString(z));

            waypoints.add(0, new WaypointElement(waypointName, colorPicker, scale, waypointX, waypointY, waypointZ, deleteButton, enabled, inputMargin));
            return;
		}
		
        for (int i = 0; i < waypoints.size(); i++) {    	
			WaypointElement element = waypoints.get(i);
        	GuiButton guiButton = element.deleteButton;
        	
			if(guiButton == button) {
	            waypoints.remove(i);
	            return;
			}
		}
    }
	
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		try {
			if(keyCode == 1) {
				ColorPicker.turnOff();
				CloseGui();
				return;
			}
			if(!closeWarning.showElement && !changingRegionWarning.showElement) {
				if(region != null) {
					if(region.isFocused()) {	
						if(keyCode != 203 && keyCode != 205 && keyCode != 29 && keyCode != 42 && keyCode != 54 && keyCode != 56 && keyCode != 184) {
							if(isChanged()) {
								changingRegionWarning.typedChar = typedChar;
								changingRegionWarning.keyCode = keyCode;
								changingRegionWarning.changeElementActive(true);
							}else {
								region.textboxKeyTyped(typedChar, keyCode);
								waypoints.clear();
								initGui();
							}
						}else {
							region.textboxKeyTyped(typedChar, keyCode);
						}
					}
				}
				
				for (int i = 0; i < waypoints.size(); i++) {
					WaypointElement waypoint = waypoints.get(i);
					List<TextField> inputs = waypoint.getListOfTextFields();

					waypoints.get(i).color.keyTyped(typedChar, keyCode);
					
					for (TextField input : inputs) {
						if(input.isFocused()) {
							if(input.getId() == 2 || input.getId() == 3 || input.getId() == 4) {
								// Backspace / leftArrow / rightArrow / . / delete
								if(keyCode == 14 || keyCode == 203 || keyCode == 205 || keyCode == 211|| keyCode == Keyboard.KEY_MINUS) input.textboxKeyTyped(typedChar, keyCode);
								
								// disallows more than one "." in coords 
								if(keyCode == 52 && !input.getText().contains(".")) input.textboxKeyTyped(typedChar, keyCode);
									
								// CTRL + A/C/V
								if((keyCode == Keyboard.KEY_A || keyCode == Keyboard.KEY_C || keyCode == Keyboard.KEY_V) && isCtrlKeyDown()) input.textboxKeyTyped(typedChar, keyCode);
								
								try {
					                float isNumber = Integer.parseInt(String.valueOf(typedChar));
					                input.textboxKeyTyped(typedChar, keyCode);
								} catch (NumberFormatException ex) { return; }
			
									
							}else{
								input.textboxKeyTyped(typedChar, keyCode);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	int offsetY = 0;
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(!closeWarning.showElement && !changingRegionWarning.showElement) {
			
			saveButton.packedFGColour = 14737632;
			
			boolean isOpenedColorPicker = false; // made it like that because mouseClick also hides picker so it wouldnt hide if i would make return
			for (int i = 0; i < waypoints.size(); i++) {
				if(waypoints.get(i).color.mouseClick(mouseX, mouseY, mouseButton)) isOpenedColorPicker = true;
			}
			
			if(isOpenedColorPicker) {
				for (int i = 0; i < waypoints.size(); i++) {
					for (TextField input : waypoints.get(i).getListOfTextFields()) {
						input.setCursorPositionZero();
						input.setFocused(false);
					}
				}
			
				return;
			}
		
			
			// focusing input when clicked
			for (int i = 0; i < waypoints.size(); i++) {
				WaypointElement waypoint = waypoints.get(i);
				boolean isAnythingPressed = false;
				
				List<TextField> inputs = waypoint.getListOfTextFields();
				if (mouseY <= ((height / 3))) {
					for (TextField input : inputs) {
						input.setCursorPositionZero();
						input.setFocused(false);
					}
	                 break;
	        	}
				
				if(waypoint.enabled.isFull()) {
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
						if(waypoint.scale.mousePressed(mc, mouseX, mouseY)) {
							isAnythingPressed = true;
						};
						
						
						for (GuiButton guibutton : waypoint.getListOfButtons()) {
				            if (guibutton.yPosition <= ((height / 3) - 20)) {
				                 continue;
				        	 }
				            
				            if (guibutton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
				            {
				            	isAnythingPressed = true;
				                guibutton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
				                this.actionPerformed(guibutton);
				            }
						}
						
			            waypoint.deleteButton.enabled = waypoint.enabled.isFull();
			            waypoint.color.enabled = waypoint.enabled.isFull();
			        }
				}else {
					if(waypoint.enabled.mousePressed(mc, mouseX, mouseY)) {
	            		isAnythingPressed = true;
	            		waypoint.enabled.playPressSound(Minecraft.getMinecraft().getSoundHandler());
		                this.actionPerformed(waypoint.enabled);
		                waypoint.deleteButton.enabled = waypoint.enabled.isFull();
		                waypoint.color.enabled = waypoint.enabled.isFull();
					};
				}
				
				if(waypoint.isHovered(mouseX, mouseY)) {
					if (waypoint.yPosition + waypoint.getHeight() <= ((height / 3) - 20)) {
		                 continue;
		        	 }
					
					if(!isAnythingPressed) {
						holdingElement = waypoint;
		                offsetY = mouseY - waypoint.yPosition;
					}
				}
			}
			
			if(region != null) {
				if (mouseX >= region.xPosition && mouseX <= region.xPosition + region.width && mouseY >= region.yPosition && mouseY <= region.yPosition + region.height) {
					region.mouseClicked(mouseX, mouseY, mouseButton);
				}else {
					region.setFocused(false);
				}
			}
			
	
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
		
		closeWarning.mouseClicked(mouseX, mouseY, mouseButton);
		changingRegionWarning.mouseClicked(mouseX, mouseY, mouseButton, this);
	}
	
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
		if (holdingElement != null) {
			mouseY = Math.min(Math.max(mouseY - offsetY, height/3 - 1), height - holdingElement.getHeight());
            holdingElement.updateYposition(mouseY);
		}

		for (int i = 0; i < waypoints.size(); i++) {
			waypoints.get(i).color.mouseClickMoved(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
			waypoints.get(i).scale.mouseClickMoved(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		}
    }
	
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
		if (holdingElement != null) {
            
			for (int i = 0; i < waypoints.size(); i++) {
				WaypointElement element = waypoints.get(i);
				
				if(holdingElement == element) continue;
				if(element.isHovered(mouseX, mouseY)) {
					int elementIndex = i;
					int holdingIndex = waypoints.indexOf(holdingElement);
					int sidePlus = (element.isHigherHalfHovered(mouseX, mouseY))?-1:0;

					waypoints.remove(holdingElement);
					waypoints.add(waypoints.indexOf(element)+1+sidePlus, holdingElement);
					holdingElement = null;
					break;
				}else if(mouseY > waypoints.get(waypoints.size()-1).yPosition + waypoints.get(waypoints.size()-1).elementHeight) {
					waypoints.remove(holdingElement);
					waypoints.add(waypoints.size(), holdingElement);
					holdingElement = null;
					break;
				}
			}
			holdingElement = null;
		}
		
		
		for (int i = 0; i < waypoints.size(); i++) {
			waypoints.get(i).color.mouseReleased(mouseX, mouseY);
			waypoints.get(i).scale.mouseReleased(mouseX, mouseY);
		}
    }
	
	@Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        
        if (!closeWarning.showElement && !changingRegionWarning.showElement) {
        	scrollbar.handleMouseInput();
        	
        	if(Mouse.getEventDWheel() > 0) {
        		snapToWaypointY();
        	}
        }
    }
	
	@Override
	public void updateScreen() {
		for (int i = 0; i < waypoints.size(); i++) {
			WaypointElement waypoint = waypoints.get(i);
			List<TextField> inputs = waypoint.getListOfTextFields();
			
			for (TextField input : inputs) {
				input.updateCursorCounter();
			}
			
			waypoints.get(i).color.updateCursorCounter();
		}
		
		if(region != null) {
			region.updateCursorCounter();
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private void SaveWaypoints() {
		Location.checkTabLocation();
		// its to prevent removing waypoints from other regions, so i just remove waypoints from my region and add them updated
		List<Waypoint> waypointsList = Waypoints.GetWaypointsWithoutLocation();
		boolean isError = false;
		
		for (int i = waypoints.size() - 1; i >= 0; i--) {
			WaypointElement waypoint = waypoints.get(i);

	    	waypoint.color.setTextColor(14737632);
	    	waypoint.x.setTextColor(14737632);
	    	waypoint.y.setTextColor(14737632);
	    	waypoint.z.setTextColor(14737632);
	    	
	        String name = waypoint.name.getText();
	        String color = waypoint.color.getColorString().replace("#", ""); 
	        double scale = waypoint.scale.getValue();
	        boolean enabled = waypoint.enabled.isFull();
	        
	        if(color.length() == 0) color = "ffffff";
	        
	        try {
	        	Color.decode("#" + color);
	        } catch (NumberFormatException e) {
	        	waypoint.color.setTextColor(11217193);
	            isError = true;
	        }
	        
	        float x = 0, y = 0, z = 0;
	        try {
	            x = Float.parseFloat(waypoint.x.getText());
	        } catch (NumberFormatException e) { waypoint.x.setTextColor(11217193); isError = true; }
	        
	        try {
	            y = Float.parseFloat(waypoint.y.getText());
	        } catch (NumberFormatException e) { waypoint.y.setTextColor(11217193); isError = true; }
	        
	        try {
	        	z = Float.parseFloat(waypoint.z.getText()); 
	        } catch (NumberFormatException e) { waypoint.z.setTextColor(11217193); isError = true; }
	        
	        if(isError) {
	        	saveButton.packedFGColour = 14258834;
	        	return; // skip
	        }
	        
	    	waypointsList.add(0, new Waypoint(name, color, x, y, z, Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld), (float) scale, enabled));
	        
	    }
	    saveButton.packedFGColour = 11131282;
	    Waypoints.waypointsList = waypointsList;
	    ConfigHandler.SaveWaypoint(waypointsList);
	}
	
	private void CloseGui() {
		if(closeWarning.showElement == true) return;

		if(isChanged()) {
			changingRegionWarning.changeElementActive(false);
			closeWarning.changeElementActive(true);
		}else {
			Minecraft.getMinecraft().thePlayer.closeScreen();
		}
	}
	
	public void updateWaypointsY() {
		//scrollbar.updateMaxBottomScroll(((waypoints.size() * (inputHeight + inputMargin))) - (height - (height/3)));
		scrollbar.updateContentHeight((waypoints.size() * (inputHeight + inputMargin)));
		int ScrollOffset = scrollbar.getOffset();
		
		int positionY = (int) (height / 3 + ScrollOffset);
		
		for (int i = 0; i < waypoints.size(); i++) {
			WaypointElement element = waypoints.get(i);
			if(element == holdingElement) continue;
			int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
			
			int l = (int) Math.signum((inputFullPosition - element.yPosition));
			if(Math.abs(inputFullPosition - element.yPosition) > element.getHeight()*2) l *= 4;
		    element.updateYposition(element.yPosition + l);
		}
	}
	
	public void snapToWaypointY() {
	//	scrollbar.updateMaxBottomScroll(((waypoints.size() * (inputHeight + inputMargin))) - (height - (height/3)));
		scrollbar.updateContentHeight((waypoints.size() * (inputHeight + inputMargin)));
		int ScrollOffset = scrollbar.getOffset();
		
		int positionY = (int) (height / 3 + ScrollOffset);
		
		for (int i = 0; i < waypoints.size(); i++) {
			if(waypoints.get(i) == holdingElement) continue;
			int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
			
			waypoints.get(i).updateYposition(inputFullPosition);
		}
	}
	
	public boolean isChanged() {
		Location.checkTabLocation();
		List<Waypoint> waypointsList = Waypoints.GetLocationWaypoints();
		boolean isntEqual = false;
		
		if(waypoints.size() != waypointsList.size()) {
			 isntEqual = true;
		}else {
			for (int i = waypoints.size() - 1; i >= 0; i--) {
				WaypointElement waypoint = waypoints.get(i);
		    	
		        String name = waypoint.name.getText();
		        String color = waypoint.color.getColorString().replace("#", "");
		        float scale = (float) waypoint.scale.getValue();
		        boolean enabled = waypoint.enabled.isFull();
		        
		        float x = 0, y = 0, z = 0;
		        try {
		        	Color.decode("#" + color);
		            x = Float.parseFloat(waypoint.x.getText());
		            y = Float.parseFloat(waypoint.y.getText());
		        	z = Float.parseFloat(waypoint.z.getText()); 
		        } catch (NumberFormatException e) { isntEqual = true; break; }
	
		        if(Float.compare(waypointsList.get(i).scale, scale) != 0) { isntEqual = true; break; }
		        if(Float.compare(waypointsList.get(i).coords[0], x) != 0) { isntEqual = true; break; }
		        if(Float.compare(waypointsList.get(i).coords[1], y) != 0) { isntEqual = true; break; }
		        if(Float.compare(waypointsList.get(i).coords[2], z) != 0) { isntEqual = true; break; }
		        if(!name.equals(waypointsList.get(i).name)) { isntEqual = true; break; }
		        if(enabled != waypointsList.get(i).enabled) { isntEqual = true; break; }
		        if(!color.equalsIgnoreCase(ColorUtils.fixColor(waypointsList.get(i).color))) { isntEqual = true; break; }
		    }
		}
		
		return isntEqual;
	}
	
	@Override
    public void onGuiClosed() {
		holdingElement = null;
		region = null;
		waypoints.clear();
	}
	
}
