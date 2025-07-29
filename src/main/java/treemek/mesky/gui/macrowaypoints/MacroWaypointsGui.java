package treemek.mesky.handlers.gui.macrowaypoints;

import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
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
import treemek.mesky.handlers.gui.GUI;
import treemek.mesky.handlers.gui.chatfunctions.ChatFunctionElement;
import treemek.mesky.handlers.gui.elements.ButtonWithToolkit;
import treemek.mesky.handlers.gui.elements.ColorPicker;
import treemek.mesky.handlers.gui.elements.ScrollBar;
import treemek.mesky.handlers.gui.elements.buttons.AddButton;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.MeskyButton;
import treemek.mesky.handlers.gui.elements.buttons.SaveButton;
import treemek.mesky.handlers.gui.elements.sliders.Slider;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.handlers.gui.elements.warnings.CloseWarning;
import treemek.mesky.handlers.gui.waypoints.WaypointElement;
import treemek.mesky.handlers.gui.waypoints.WaypointGroupElement;
import treemek.mesky.handlers.gui.elements.buttons.MacroButton;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.ChatFunctions;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.HypixelCheck;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.MacroWaypoints;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.MacroWaypoints.MacroWaypoint;
import treemek.mesky.utils.MacroWaypoints.MacroWaypointGroup;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.ChatFunctions.ChatFunction;
import treemek.mesky.utils.Waypoints.Waypoint;
import treemek.mesky.utils.Waypoints.WaypointGroup;

public class MacroWaypointsGui extends GuiScreen {
	private GuiButton saveButton;
	
	public static List<MacroWaypointGroupElement> waypoints = new ArrayList<>();
	
	CloseWarning closeWarning = new CloseWarning();
	ScrollBar scrollbar = new ScrollBar();
	
	int inputMargin;
	int inputHeight;
	private float scrollbar_startPosition;
	private float scrollbarBg_height;
	private float scrollbar_width;

	private MacroWaypointElement holdingElement;

	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
		drawRect(0, height/4 - 1, width, height, new Color(33, 33, 33,255).getRGB());
		
		double widthOfCheckTexts = width / 20;
        double checksScale = RenderHandler.getTextScale("Players", widthOfCheckTexts); // "Players" is the longest in Alerts and i want all to have the same scale
		
		for (MacroWaypointGroupElement waypointGroupElement : waypoints) {
			if(holdingGroup == waypointGroupElement) continue;
			drawRect(width / 20 - inputHeight-1, waypointGroupElement.yPosition + inputHeight + inputMargin/2 - 1, (int) (width*0.9 + inputHeight*2 + 1), waypointGroupElement.yPosition + waypointGroupElement.height, new Color(15, 15, 15,255).getRGB()); // waypoint list dark
			drawRect(width / 20 - inputHeight, waypointGroupElement.yPosition + inputHeight + inputMargin/2 - 1, (int) (width*0.9 + inputHeight*2), waypointGroupElement.yPosition + waypointGroupElement.height - 1, new Color(25, 25, 25,255).getRGB()); // waypoint list light

			if(waypointGroupElement.opened.isOpened()) {
				for (MacroWaypointElement waypoint : waypointGroupElement.list) {
					if(waypoint == holdingElement) continue;
					
					if (waypoint.yPosition + waypoint.getHeight() <= ((height / 4))) {
		                continue;
		       	 	}
					
					List<TextField> inputs = waypoint.getListOfTextFields();
					
					for (TextField input : inputs) {
						input.drawTextBox();
					}
					
					waypoint.color.drawTextBox();
					
					// Reset color and blending state before drawing buttons
				    resetColor();
				    
				    for (GuiButton button : waypoint.getListOfButtons()) {	
						button.drawButton(mc, mouseX, mouseY);
					}
				    
				    if(!waypoint.enabled.isFull() && waypointGroupElement.enabled.isFull()) {
						drawRect(waypoint.xPosition, waypoint.yPosition-2, waypoint.xPosition + waypoint.getWidth(), waypoint.yPosition + waypoint.getHeight()+2, new Color(33, 33, 33, 180).getRGB());
					}
				    
				    resetColor();
				    waypoint.enabled.drawButton(mc, mouseX, mouseY);
				}
			}
			
			drawRect(width / 20 - inputHeight-1, waypointGroupElement.yPosition - 1, (int) (width*0.9 + inputHeight*2 + 1), waypointGroupElement.yPosition + inputHeight + inputMargin/2 + 1, new Color(15, 15, 15,255).getRGB());
			if(waypointGroupElement.name != null && !waypointGroupElement.nameField.getVisible()) RenderHandler.drawText(ColorUtils.getColoredText(waypointGroupElement.name), waypointGroupElement.xPosition, waypointGroupElement.yPosition + ((inputHeight + inputMargin/2)/2 - RenderHandler.getTextHeight(checksScale)/2), checksScale, true, 0xb0aeae);
			waypointGroupElement.addWaypoint.drawButton(mc, mouseX, mouseY);
			waypointGroupElement.delete.drawButton(mc, mouseX, mouseY);
			waypointGroupElement.nameField.drawTextBox();
			
			if(!waypointGroupElement.enabled.isFull()) drawRect(width / 20 - inputHeight-1, waypointGroupElement.yPosition - 1, (int) (width*0.9 + inputHeight*2 + 1), waypointGroupElement.yPosition + waypointGroupElement.height, new Color(33, 33, 33, 180).getRGB());
			resetColor();
			waypointGroupElement.enabled.drawButton(mc, mouseX, mouseY);
			waypointGroupElement.move.drawButton(mc, mouseX, mouseY);
			waypointGroupElement.opened.drawButton(mc, mouseX, mouseY);
	    }

		if(holdingGroup != null) {
			drawRect(width / 20 - inputHeight-1, holdingGroup.yPosition + inputHeight + inputMargin/2 - 1, (int) (width*0.9 + inputHeight*2 + 1), holdingGroup.yPosition + holdingGroup.height, new Color(15, 15, 15,255).getRGB());
			drawRect(width / 20 - inputHeight, holdingGroup.yPosition + inputHeight + inputMargin/2 - 1, (int) (width*0.9 + inputHeight*2), holdingGroup.yPosition + holdingGroup.height - 1, new Color(25, 25, 25,255).getRGB());
			
			drawRect(width / 20 - inputHeight-1, holdingGroup.yPosition - 1, (int) (width*0.9 + inputHeight*2 + 1), holdingGroup.yPosition + inputHeight + inputMargin/2 - 1, new Color(15, 15, 15,255).getRGB());
			if(holdingGroup.name != null) RenderHandler.drawText(ColorUtils.getColoredText(holdingGroup.name), holdingGroup.xPosition, holdingGroup.yPosition + ((inputHeight + inputMargin/2)/2 - RenderHandler.getTextHeight(checksScale)/2), checksScale, true, 0xb0aeae);
			holdingGroup.addWaypoint.drawButton(mc, mouseX, mouseY);
			holdingGroup.delete.drawButton(mc, mouseX, mouseY);
			
			if(!holdingGroup.enabled.isFull()) drawRect(width / 20 - inputHeight-1, holdingGroup.yPosition - 1, (int) (width*0.9 + inputHeight*2 + 1), holdingGroup.yPosition + holdingGroup.height, new Color(33, 33, 33, 180).getRGB());
			resetColor();
			holdingGroup.move.drawButton(mc, mouseX, mouseY);
			holdingGroup.enabled.drawButton(mc, mouseX, mouseY);
			holdingGroup.opened.drawButton(mc, mouseX, mouseY);
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
			
			if(!holdingElement.enabled.isFull()) {
				drawRect(holdingElement.xPosition, holdingElement.yPosition-2, holdingElement.xPosition + holdingElement.getWidth(), holdingElement.yPosition + holdingElement.getHeight()+2, new Color(28, 28, 28, 180).getRGB());
			}
	    }
	    
	    drawRect(0, 0, width, height/4 - 1, new Color(33, 33, 33,255).getRGB());
		
	    // Toolkit
    	if(holdingElement == null && holdingGroup == null) {
	    	for (MacroWaypointGroupElement waypointGroupElement : waypoints) {
	    		if(waypointGroupElement.opened.isOpened()) {
					for (MacroWaypointElement waypoint : waypointGroupElement.list) {
				    	for (GuiButton button : waypoint.getListOfButtons()) {
				    	    if (button instanceof ButtonWithToolkit && ((ButtonWithToolkit) button).shouldShowTooltip()) {
				    	    	RenderHandler.drawToolkit(button, mouseX, mouseY);
				    	    }
				    	}
					}
	    		}
	    		
	    		if(waypointGroupElement.addWaypoint.shouldShowTooltip()) {
	    			RenderHandler.drawToolkit(waypointGroupElement.addWaypoint, mouseX, mouseY);
	    		}
	    		
	    		if(waypointGroupElement.delete.shouldShowTooltip()) {
	    			RenderHandler.drawToolkit(waypointGroupElement.delete, mouseX, mouseY);
	    		}
	    		
	    		if(waypointGroupElement.move.shouldShowTooltip()) {
	    			RenderHandler.drawToolkit(waypointGroupElement.move, mouseX, mouseY);
	    		}
	    	}
    	}
	    
		float scale = (float) ((height*0.1f) / mc.fontRendererObj.FONT_HEIGHT) / 2;

        int textLength = mc.fontRendererObj.getStringWidth("Macro Waypoints");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) (height * 0.05f);

        RenderHandler.drawText("Macro Waypoints", titleX, titleY, scale, true, 0x3e91b5);
        
        int color_X = width / 20;
        int name_X = color_X + inputHeight*2 + 5;
    	int coords_X = name_X + (width/4) + 10;
    	int coords_width = width / 15;

    	int positionY = (int)((height / 4) - RenderHandler.getTextHeight(checksScale) - 3);

        RenderHandler.drawText("Color", color_X, positionY, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("Name", name_X, positionY, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("X", coords_X, positionY, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("Y", coords_X + (coords_width+5), positionY, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("Z", coords_X + ((coords_width+5)*2), positionY, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("Yaw", coords_X + ((coords_width+5)*3), positionY, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("Pitch", coords_X + ((coords_width+5)*4), positionY, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("Noise", coords_X + ((coords_width+5)*5), positionY, checksScale, true, 0x7a7a7a);
        
        scrollbar.updateScrollBar((int)Math.min(20, (width * 0.025)), (height - (height / 4) - 10), (int) (width*0.9 + inputHeight*2 + 5), height/4);
        if(scrollbar.isScrolling()) {
        	snapToWaypointY();
        }else {
        	updateWaypointsY();
        }
        scrollbar.drawScrollBar();
       
	    super.drawScreen(mouseX, mouseY, partialTicks);
	    
	    if(holdingElement == null && holdingGroup == null) {
    		for (GuiButton guiButton : buttonList) {
    			if (guiButton instanceof ButtonWithToolkit && ((ButtonWithToolkit) guiButton).shouldShowTooltip()) {
	    	    	RenderHandler.drawToolkit(guiButton, mouseX, mouseY);
	    	    }
			}
	    }
	    
	    closeWarning.drawElement(mc, mouseX, mouseY);
	}
	
	private void resetColor() { // because drawRect destroys some logic
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); // Ensure full alpha here for the texture
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    buttonList.clear();
	    closeWarning = new CloseWarning();
	    
        int checkX = (int)(width / 4);
        int buttonWidth = 20;
        int buttonHeight = 20;
        
        float mainButtonsScale = (float) ((height*0.1f) / mc.fontRendererObj.FONT_HEIGHT) / 2;
        int mainButtonsSize = (int) RenderHandler.getTextHeight(mainButtonsScale);
        int mainButtonsY = (int) (height * 0.05f);
        
        // Save button
        saveButton = new SaveButton(-1, (int)(width * 0.9f) - mainButtonsSize/2, mainButtonsY, mainButtonsSize ,mainButtonsSize, "Save");
        this.buttonList.add(saveButton);
        
        // New waypoint button
        this.buttonList.add(new AddButton(-2, (int)(width * 0.1f) - mainButtonsSize/2, mainButtonsY, mainButtonsSize, mainButtonsSize, "New group"));
        
        // Updating location from tab
        Location.checkTabLocation();
        
        inputHeight = ((height / 25) < 12)?12:(height / 25);
        inputMargin = ((height / 40) < 5)?5:(height / 40);
        
        int spaceBetween = width/80;
        
        int waypointColor_X = width / 20;
        int waypointName_X = waypointColor_X + inputHeight*2 + 5;
		int coords_X = waypointName_X + (width/4) + spaceBetween*2;
		int coord_Width = width / 17;
		int waypointHeight = inputHeight*2 + 5;
		int deleteX = (int) (coords_X + ((coord_Width+spaceBetween)*6) + spaceBetween*1.5f);
		
		int macroButtonSize = Math.min(inputHeight, (width / 4)/6) - 3;
		
		scrollbar.updateVisibleHeight(height - (height/3));
		scrollbar.updateContentHeight((MacroWaypoints.GetLocationWaypoints().size() * (waypointHeight + inputMargin)));
		int ScrollOffset = scrollbar.getOffset(); // so scrolloffset doesnt go below maxbottomscroll
		
        int positionY = (int) (height / 3 + ScrollOffset);
		
        if(waypoints.isEmpty()) {
	        for (Entry<String, MacroWaypointGroup> waypointGroup : MacroWaypoints.GetLocationWaypoints().entrySet()) {
	        	List<MacroWaypointElement> list = new ArrayList<>();
	        	
				String groupName = waypointGroup.getKey();
				List<MacroWaypoint> groupList = waypointGroup.getValue().list;
				
				for (MacroWaypoint macroWaypoint : groupList) {
		        	// Position 0 for inputs + every input height and their bottom margin
		        	int inputFullPosition = positionY + ((waypointHeight + inputMargin) * 1);
		        	
		        	DeleteButton deleteButton = new DeleteButton(0 + (5), deleteX, inputFullPosition, inputHeight, inputHeight, "Delete waypoint");
		        	deleteButton.enabled = macroWaypoint.enabled;
		        	
		        	CheckButton enabled = new CheckButton(0, (int) (deleteX + inputHeight + spaceBetween*1.5f), inputFullPosition, inputHeight, inputHeight, "", macroWaypoint.enabled);
		        	
		    		// Name text input
		        	TextField waypointName = new TextField(0, waypointName_X, inputFullPosition, width / 4, inputHeight);
		        	waypointName.setColoredField(true);
		            waypointName.setMaxStringLength(512);
		            waypointName.setCanLoseFocus(true);
		            waypointName.setText(macroWaypoint.waypoint.name);
		            
		            
		            // color text input
		            ColorPicker colorPicker = new ColorPicker(0, waypointColor_X, 0, inputHeight);	        	
		            colorPicker.setText(macroWaypoint.waypoint.color);
		            colorPicker.enabled = macroWaypoint.enabled;
		            
		            // X coordinate input
		            TextField waypointX = new TextField(2, coords_X, inputFullPosition, coord_Width, inputHeight);
		            waypointX.setMaxStringLength(16);
		            waypointX.setCanLoseFocus(true);
		            waypointX.setText(Float.toString(macroWaypoint.waypoint.coords[0]));
		            
		            // Y coordinate input
		            TextField waypointY = new TextField(2, coords_X + coord_Width + spaceBetween, inputFullPosition, coord_Width, inputHeight);
		            waypointY.setMaxStringLength(16);
		            waypointY.setCanLoseFocus(true);
		            waypointY.setText(Float.toString(macroWaypoint.waypoint.coords[1]));
		            
		            // Z coordinate input
		            TextField waypointZ = new TextField(2, coords_X + ((coord_Width+spaceBetween)*2), inputFullPosition, coord_Width, inputHeight);
		            waypointZ.setMaxStringLength(16);
		            waypointZ.setCanLoseFocus(true);
		            waypointZ.setText(Float.toString(macroWaypoint.waypoint.coords[2]));
		            
		            TextField yaw = new TextField(2, coords_X + ((coord_Width+spaceBetween)*3), inputFullPosition, coord_Width, inputHeight);
		            yaw.setMaxStringLength(16);
		            yaw.setCanLoseFocus(true);
		            String yawF = (macroWaypoint.yaw != null)?Float.toString(macroWaypoint.yaw):"";
					yaw.setText(yawF);
		            
		            TextField pitch = new TextField(2, coords_X + ((coord_Width+spaceBetween)*4), inputFullPosition, coord_Width, inputHeight);
		            pitch.setMaxStringLength(16);
		            pitch.setCanLoseFocus(true);
		            String pitchF = (macroWaypoint.pitch != null)?Float.toString(macroWaypoint.pitch):"";
		            pitch.setText(pitchF);
		
		            TextField noiseLevel = new TextField(2, coords_X + ((coord_Width+spaceBetween)*5), inputFullPosition, coord_Width, inputHeight);
		            noiseLevel.setMaxStringLength(16);
		            noiseLevel.setCanLoseFocus(true);
		            noiseLevel.setText(Float.toString(macroWaypoint.noiseLevel));
		            
		            TextField function = new TextField(-1, coords_X, inputFullPosition + inputHeight+5, width / 4, inputHeight);
		            function.setMaxStringLength(128);
		            function.setCanLoseFocus(true);
		            function.setText(macroWaypoint.function);
		            
		            MacroButton leftClick = new MacroButton(0, waypointName_X, inputFullPosition + inputHeight+5, macroButtonSize, macroButtonSize, "", macroWaypoint.leftClick);
		            MacroButton rightClick = new MacroButton(1, waypointName_X + (macroButtonSize+spaceBetween), inputFullPosition + inputHeight+5, macroButtonSize, macroButtonSize, "", macroWaypoint.rightClick);
		            MacroButton left = new MacroButton(2, waypointName_X + (macroButtonSize+spaceBetween)*2, inputFullPosition + inputHeight+5, macroButtonSize, macroButtonSize, "", macroWaypoint.left);
		            MacroButton right = new MacroButton(3, waypointName_X + (macroButtonSize+spaceBetween)*3, inputFullPosition + inputHeight+5, macroButtonSize, macroButtonSize, "", macroWaypoint.right);
		            MacroButton back = new MacroButton(4, waypointName_X + (macroButtonSize+spaceBetween)*4, inputFullPosition + inputHeight+5, macroButtonSize, macroButtonSize, "", macroWaypoint.back);
		            MacroButton forward = new MacroButton(5, waypointName_X + (macroButtonSize+spaceBetween)*5, inputFullPosition + inputHeight+5, macroButtonSize, macroButtonSize, "", macroWaypoint.forward);
		            MacroButton sneak = new MacroButton(6, waypointName_X + (macroButtonSize+spaceBetween)*6, inputFullPosition + inputHeight+5, macroButtonSize, macroButtonSize, "", macroWaypoint.sneak);
		            
		            list.add(new MacroWaypointElement(waypointName, colorPicker, waypointX, waypointY, waypointZ, yaw, pitch, left, right, back, forward, leftClick, rightClick, sneak, noiseLevel, function, deleteButton, enabled, inputMargin));
				}
				
				MacroWaypointGroupElement group = new MacroWaypointGroupElement(groupName, list, waypointGroup.getValue().world);
				group.xPosition = waypointName_X;
				group.setEnabled(waypointGroup.getValue().enabled);
				group.opened.setOpened(waypointGroup.getValue().opened);
				waypoints.add(group);
	        }
        }else {
        	for (MacroWaypointGroupElement waypointGroupElement : waypoints) {
        		waypointGroupElement.xPosition = waypointName_X;
        		
        		List<MacroWaypointElement> waypointList = waypointGroupElement.list;
        		for (int i = 0; i < waypointList.size(); i++) {
					MacroWaypointElement macroWaypoint = waypointList.get(i);
		        	
					// Position 0 for inputs + every input height and their bottom margin
		        	int inputFullPosition = positionY + ((waypointHeight + inputMargin) * i);
		        	
		        	DeleteButton deleteButton = new DeleteButton(0 + (5*i), deleteX, inputFullPosition, inputHeight, inputHeight, "Delete waypoint");
		        	deleteButton.enabled = macroWaypoint.enabled.isFull();
		        	
		        	CheckButton enabled = new CheckButton(0, (int) (deleteX + inputHeight + spaceBetween*1.5f), inputFullPosition, inputHeight, inputHeight, "", macroWaypoint.enabled.isFull());
		        	
		    		// Name text input
		        	TextField waypointName = new TextField(0, waypointName_X, inputFullPosition, width / 4, inputHeight);
		        	waypointName.setColoredField(true);
		            waypointName.setMaxStringLength(512);
		            waypointName.setCanLoseFocus(true);
		            waypointName.setText(macroWaypoint.name.getText());
		            
		            
		            // color text input
		            ColorPicker colorPicker = new ColorPicker(0, waypointColor_X, 0, inputHeight);
		            colorPicker.setText(macroWaypoint.color.getColorString());
		            colorPicker.enabled = macroWaypoint.enabled.isFull();
		            
		            // X coordinate input
		            TextField waypointX = new TextField(2, coords_X, inputFullPosition, coord_Width, inputHeight);
		            waypointX.setMaxStringLength(16);
		            waypointX.setCanLoseFocus(true);
		            waypointX.setText(macroWaypoint.x.getText());
		            
		            // Y coordinate input
		            TextField waypointY = new TextField(2, coords_X + coord_Width + spaceBetween, inputFullPosition, coord_Width, inputHeight);
		            waypointY.setMaxStringLength(16);
		            waypointY.setCanLoseFocus(true);
		            waypointY.setText(macroWaypoint.y.getText());
		            
		            // Z coordinate input
		            TextField waypointZ = new TextField(2, coords_X + ((coord_Width+spaceBetween)*2), inputFullPosition, coord_Width, inputHeight);
		            waypointZ.setMaxStringLength(16);
		            waypointZ.setCanLoseFocus(true);
		            waypointZ.setText(macroWaypoint.z.getText());
		            
		            TextField yaw = new TextField(2, coords_X + ((coord_Width+spaceBetween)*3), inputFullPosition, coord_Width, inputHeight);
		            yaw.setMaxStringLength(16);
		            yaw.setCanLoseFocus(true);
					yaw.setText(macroWaypoint.yaw.getText());
		            
		            TextField pitch = new TextField(2, coords_X + ((coord_Width+spaceBetween)*4), inputFullPosition, coord_Width, inputHeight);
		            pitch.setMaxStringLength(16);
		            pitch.setCanLoseFocus(true);
		            pitch.setText(macroWaypoint.pitch.getText());
		
		            TextField noiseLevel = new TextField(2, coords_X + ((coord_Width+spaceBetween)*5), inputFullPosition, coord_Width, inputHeight);
		            noiseLevel.setMaxStringLength(16);
		            noiseLevel.setCanLoseFocus(true);
		            noiseLevel.setText(macroWaypoint.noiseLevel.getText());
		            
		            TextField function = new TextField(-1, coords_X, inputFullPosition + inputHeight+5, width / 4, inputHeight);
		            function.setMaxStringLength(128);
		            function.setCanLoseFocus(true);
		            function.setText(macroWaypoint.function.getText());
		            
		            MacroButton leftClick = new MacroButton(0, waypointName_X, inputFullPosition + inputHeight+5, macroButtonSize, macroButtonSize, "", macroWaypoint.leftClick.isFull);
		            MacroButton rightClick = new MacroButton(1, waypointName_X + (macroButtonSize+spaceBetween), inputFullPosition + inputHeight+5, macroButtonSize, macroButtonSize, "", macroWaypoint.rightClick.isFull);
		            MacroButton left = new MacroButton(2, waypointName_X + (macroButtonSize+spaceBetween)*2, inputFullPosition + inputHeight+5, macroButtonSize, macroButtonSize, "", macroWaypoint.left.isFull);
		            MacroButton right = new MacroButton(3, waypointName_X + (macroButtonSize+spaceBetween)*3, inputFullPosition + inputHeight+5, macroButtonSize, macroButtonSize, "", macroWaypoint.right.isFull);
		            MacroButton back = new MacroButton(4, waypointName_X + (macroButtonSize+spaceBetween)*4, inputFullPosition + inputHeight+5, macroButtonSize, macroButtonSize, "", macroWaypoint.back.isFull);
		            MacroButton forward = new MacroButton(5, waypointName_X + (macroButtonSize+spaceBetween)*5, inputFullPosition + inputHeight+5, macroButtonSize, macroButtonSize, "", macroWaypoint.forward.isFull);
		            MacroButton sneak = new MacroButton(6, waypointName_X + (macroButtonSize+spaceBetween)*6, inputFullPosition + inputHeight+5, macroButtonSize, macroButtonSize, "", macroWaypoint.sneak.isFull);
		            
		            waypointList.set(i, new MacroWaypointElement(waypointName, colorPicker, waypointX, waypointY, waypointZ, yaw, pitch, left, right, back, forward, leftClick, rightClick, sneak, noiseLevel, function, deleteButton, enabled, inputMargin));
        		}
        	}
        }
        
        snapToWaypointY();
        scrollbar.updateVisibleHeight(height - (height/4));
        int contentHeight = waypoints.stream().mapToInt(group -> group.height + inputHeight).sum(); // sums height and inputheight
		scrollbar.updateContentHeight(contentHeight);
	}
	
	@Override
    protected void actionPerformed(GuiButton button) {
		if(button.id == -1 && button == saveButton) {
			// Save button
			SaveWaypoints();
			return;
		}
		
		if(button.id == -2) {
			// Add group button
			MacroWaypointGroupElement group = new MacroWaypointGroupElement("default", new ArrayList<>(), Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld));
			group.xPosition = width / 20 + inputHeight*2 + 5;
            waypoints.add(0, group);
            return;
		}
		
		for (MacroWaypointGroupElement group : waypoints) {
			if(button == (GuiButton)group.addWaypoint) {
				EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
				float x = Float.parseFloat(String.format("%.2f", player.posX).replace(",", "."));
				float y = Float.parseFloat(String.format("%.2f", player.posY).replace(",", "."));
				float z = Float.parseFloat(String.format("%.2f", player.posZ).replace(",", "."));
			
				float yawF = Float.parseFloat(String.format("%.2f", player.rotationYaw).replace(",", "."));
				float pitchF = Float.parseFloat(String.format("%.2f", player.rotationPitch).replace(",", "."));
				
				int spaceBetween = width/80;
		        
				int waypointColor_X = width / 20;
		        int waypointName_X = waypointColor_X + inputHeight*2 + 5;
				int coords_X = waypointName_X + (width/4) + spaceBetween*2;
				int coord_Width = width / 17;
				int waypointHeight = inputHeight*2 + 5;
				int deleteX = (int) (coords_X + ((coord_Width+spaceBetween)*6) + spaceBetween*1.5f);
				
				int macroButtonSize = Math.min(inputHeight, (width / 4)/6) - 3;
				
				int topOfGroup = group.yPosition;
				
				
				
	        	DeleteButton deleteButton = new DeleteButton(0 + (5*1), deleteX, topOfGroup, inputHeight, inputHeight, "Delete waypoint");
	        	deleteButton.enabled = true;
	        	
	        	CheckButton enabled = new CheckButton(0, (int) (deleteX + inputHeight + spaceBetween*1.5f), topOfGroup, inputHeight, inputHeight, "", true);
	        	
	    		// Name text input
	        	TextField waypointName = new TextField(0, waypointName_X, topOfGroup, width / 4, inputHeight);
	        	waypointName.setColoredField(true);
	            waypointName.setMaxStringLength(512);
	            waypointName.setCanLoseFocus(true);
	            waypointName.setText("Name");
	            
	            
	            // color text input
	            ColorPicker colorPicker = new ColorPicker(0, waypointColor_X, 0, inputHeight);
	            colorPicker.setText("ffffff");
	            colorPicker.enabled = true;
	            
	            // X coordinate input
	            TextField waypointX = new TextField(2, coords_X, topOfGroup, coord_Width, inputHeight);
	            waypointX.setMaxStringLength(16);
	            waypointX.setCanLoseFocus(true);
	            waypointX.setText(Float.toString(x));
	            
	            // Y coordinate input
	            TextField waypointY = new TextField(2, coords_X + coord_Width + spaceBetween, topOfGroup, coord_Width, inputHeight);
	            waypointY.setMaxStringLength(16);
	            waypointY.setCanLoseFocus(true);
	            waypointY.setText(Float.toString(y));
	            
	            // Z coordinate input
	            TextField waypointZ = new TextField(2, coords_X + ((coord_Width+spaceBetween)*2), topOfGroup, coord_Width, inputHeight);
	            waypointZ.setMaxStringLength(16);
	            waypointZ.setCanLoseFocus(true);
	            waypointZ.setText(Float.toString(z));
				
			    TextField yaw = new TextField(2, coords_X + ((coord_Width+spaceBetween)*3), 0, coord_Width, inputHeight);
	            yaw.setMaxStringLength(16);
	            yaw.setCanLoseFocus(true);
	            yaw.setText(Float.toString(yawF));
	            
	            TextField pitch = new TextField(2, coords_X + ((coord_Width+spaceBetween)*4), 0, coord_Width, inputHeight);
	            pitch.setMaxStringLength(16);
	            pitch.setCanLoseFocus(true);
	            pitch.setText(Float.toString(pitchF));

	            TextField noiseLevel = new TextField(2, coords_X + ((coord_Width+spaceBetween)*5), 0, coord_Width, inputHeight);
	            noiseLevel.setMaxStringLength(16);
	            noiseLevel.setCanLoseFocus(true);
	            noiseLevel.setText("1");
	            
	            TextField function = new TextField(-1,  coords_X, 0 + inputHeight+5, width / 4, inputHeight);
	            function.setMaxStringLength(128);
	            function.setCanLoseFocus(true);
	            function.setText("/");
	            
	            MacroButton leftClick = new MacroButton(0, waypointName_X, 0 + inputHeight+5, macroButtonSize, macroButtonSize, "", false);
	            MacroButton rightClick = new MacroButton(1, waypointName_X + (macroButtonSize+spaceBetween), 0 + inputHeight, macroButtonSize, macroButtonSize, "", false);
	            MacroButton left = new MacroButton(2, waypointName_X + (macroButtonSize+spaceBetween)*2, 0 + inputHeight, macroButtonSize, macroButtonSize, "", false);
	            MacroButton right = new MacroButton(3, waypointName_X + (macroButtonSize+spaceBetween)*3, 0 + inputHeight, macroButtonSize, macroButtonSize, "", false);
	            MacroButton back = new MacroButton(4, waypointName_X + (macroButtonSize+spaceBetween)*4, 0 + inputHeight, macroButtonSize, macroButtonSize, "", false);
	            MacroButton forward = new MacroButton(5, waypointName_X + (macroButtonSize+spaceBetween)*5, 0 + inputHeight, macroButtonSize, macroButtonSize, "", false);
	            MacroButton sneak = new MacroButton(6, waypointName_X + (macroButtonSize+spaceBetween)*6, 0 + inputHeight, macroButtonSize, macroButtonSize, "", false);
	            
			    
	            group.list.add(0, new MacroWaypointElement(waypointName, colorPicker, waypointX, waypointY, waypointZ, yaw, pitch, left, right, back, forward, leftClick, rightClick, sneak, noiseLevel, function, deleteButton, enabled, inputMargin));
				return;
			}
			
			
			
			if(button == (GuiButton)group.delete) {
				waypoints.remove(group);
				return;
			}
			
			group.list.removeIf(waypointElement -> waypointElement.deleteButton == button);
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
			
			if(!closeWarning.showElement) {
				for (MacroWaypointGroupElement waypointGroupElement : waypoints) {
					if(waypointGroupElement.nameField.textboxKeyTyped(typedChar, keyCode)) {
						waypointGroupElement.name = waypointGroupElement.nameField.getText();
					}
					
					for (MacroWaypointElement waypoint : waypointGroupElement.list) {
						List<TextField> inputs = waypoint.getListOfTextFields();
						
						waypoint.color.keyTyped(typedChar, keyCode);
						
						for (TextField input : inputs) {
							if(input.isFocused()) {
								if(input.getId() == 2) {
									// Backspace / leftArrow / rightArrow / . / delete
									if(keyCode == 14 || keyCode == 203 || keyCode == 205 || keyCode == 211 || keyCode == Keyboard.KEY_MINUS) input.textboxKeyTyped(typedChar, keyCode);
									
									// disallows more than one "." in coords 
									if(keyCode == 52 && !input.getText().contains(".")) input.textboxKeyTyped(typedChar, keyCode);
										
									// CTRL + A/C/V
									if((keyCode == Keyboard.KEY_A || keyCode == Keyboard.KEY_C || keyCode == Keyboard.KEY_V) && isCtrlKeyDown()) input.textboxKeyTyped(typedChar, keyCode);
									
									try {
						                float isNumber = Integer.parseInt(String.valueOf(typedChar));
						                input.textboxKeyTyped(typedChar, keyCode);
									} catch (NumberFormatException ex) { return; }
				
								}else if(input.getId() == -1) {
									if(keyCode == Keyboard.KEY_SLASH && !isShiftKeyDown()) return;
									int cursor = input.getCursorPosition();
									
									input.textboxKeyTyped(typedChar, keyCode);
									
									if(!input.getText().startsWith("/")) {
										input.setText("/" + input.getText());
										input.setCursorPosition(cursor);
									}
								}else{
									input.textboxKeyTyped(typedChar, keyCode);
								}
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
	private MacroWaypointGroupElement holdingGroup;
	private boolean wasOpenedBeforeHolding;

	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(!closeWarning.showElement) {
			
			saveButton.packedFGColour = 14737632;
			
			boolean isOpenedColorPicker = false; // made it like that because mouseClick also hides picker so it wouldnt hide if i would make return
			
			if (mouseY <= ((height / 4))) { // is above waypoints line
				for (MacroWaypointGroupElement waypointGroupElement : waypoints) {
					for (MacroWaypointElement waypoint : waypointGroupElement.list) {
						if(waypoint.color.isOpened()) {
							isOpenedColorPicker = true;
							waypoint.color.mouseClick(mouseX, mouseY, mouseButton);
						}else {
							List<TextField> inputs = waypoint.getListOfTextFields();
							
							for (TextField input : inputs) {
								input.setCursorPositionZero();
								input.setFocused(false);
							}
						}
					}
					
					waypointGroupElement.nameField.setCursorPositionZero();
					waypointGroupElement.nameField.setFocused(false);
					waypointGroupElement.nameField.setVisible(false);
				}
				
				if(!isOpenedColorPicker) {
					super.mouseClicked(mouseX, mouseY, mouseButton);
				}
				
				return;
        	}
			
			for (MacroWaypointGroupElement waypointGroupElement : waypoints) {
				if(!waypointGroupElement.enabled.isFull()) continue;
				
				for (MacroWaypointElement waypoint : waypointGroupElement.list) {
					
					if(waypoint.color.mouseClick(mouseX, mouseY, mouseButton)) {
						isOpenedColorPicker = true;
					}
				}
			}

			if(isOpenedColorPicker) {
				for (MacroWaypointGroupElement waypointGroupElement : waypoints) {
					for (MacroWaypointElement waypoint : waypointGroupElement.list) {
						//Utils.debug(waypoint.color.isOpened() + " " + waypoint.name.getText());
						
						for (TextField input : waypoint.getListOfTextFields()) {
							input.setCursorPositionZero();
							input.setFocused(false);
						}
					}
				}
				return;
			}
			
			// focusing input when clicked
			for (MacroWaypointGroupElement waypointGroupElement : waypoints) {
				if (waypointGroupElement.enabled.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) { // enabled
					waypointGroupElement.enabled.playPressSound(Minecraft.getMinecraft().getSoundHandler());
					waypointGroupElement.delete.enabled = waypointGroupElement.enabled.isFull();
					waypointGroupElement.addWaypoint.enabled = waypointGroupElement.enabled.isFull();
					for (MacroWaypointElement waypoint : waypointGroupElement.list) {
						waypoint.deleteButton.enabled = waypointGroupElement.enabled.isFull();
					}
	                return;
	            }
				
				if (waypointGroupElement.opened.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
	            {
					waypointGroupElement.opened.playPressSound(Minecraft.getMinecraft().getSoundHandler());
					waypointGroupElement.switchOpened();
					snapToWaypointY();
	                return;
	            }
				
				if (waypointGroupElement.move.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
	            {
					waypointGroupElement.move.playPressSound(Minecraft.getMinecraft().getSoundHandler());
					holdingGroup = waypointGroupElement;
					offsetY = mouseY - waypointGroupElement.yPosition;
					wasOpenedBeforeHolding = waypointGroupElement.opened.isOpened();
					waypointGroupElement.opened.setOpened(false);
	                return;
	            }
				
				if(!waypointGroupElement.enabled.isFull()) {
					if(waypointGroupElement.opened.isOpened()) {
						for (MacroWaypointElement waypoint : waypointGroupElement.list) {
							if(waypoint.isHovered(mouseX, mouseY)) {
								if (waypoint.yPosition + waypoint.getHeight() <= ((height / 4) - 20)) {
					                 continue;
								}
							
								holdingElement = waypoint;
				                offsetY = mouseY - waypoint.yPosition;
							}
						}
					}
					
					continue;
				}
				
				if (mouseX >= waypointGroupElement.nameField.xPosition && mouseX <= waypointGroupElement.nameField.xPosition + waypointGroupElement.nameField.width && mouseY >= waypointGroupElement.nameField.yPosition && mouseY <= waypointGroupElement.nameField.yPosition + waypointGroupElement.nameField.height) {
					waypointGroupElement.nameField.mouseClicked(mouseX, mouseY, mouseButton);
					waypointGroupElement.nameField.setVisible(true);
				}else {
					if(waypointGroupElement.nameField.getVisible()) {
						waypointGroupElement.nameField.setVisible(false);
					}
				}
				
				if (waypointGroupElement.addWaypoint.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
	            {
					waypointGroupElement.addWaypoint.playPressSound(Minecraft.getMinecraft().getSoundHandler());
	                this.actionPerformed(waypointGroupElement.addWaypoint);
	                return;
	            }
				
				if (waypointGroupElement.delete.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
	            {
					waypointGroupElement.delete.playPressSound(Minecraft.getMinecraft().getSoundHandler());
	                this.actionPerformed(waypointGroupElement.delete);
	                return;
	            }
				
				if(waypointGroupElement.opened.isOpened()) {
					for (MacroWaypointElement waypoint : waypointGroupElement.list) {
						if (mouseY <= ((height / 3))) {
							List<TextField> inputs = waypoint.getListOfTextFields();
							for (TextField input : inputs) {
								input.setCursorPositionZero();
								input.setFocused(false);
							}
							
			                 break;
			        	 }
						
						boolean isAnythingPressed = false;
						
						if(waypoint.enabled.isFull()) {
							List<TextField> inputs = waypoint.getListOfTextFields();
							
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
								for (GuiButton guibutton : waypoint.getListOfButtons()) {
						            if (guibutton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
						            {
						            	isAnythingPressed = true;
						                guibutton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
						                this.actionPerformed(guibutton);
						                return;
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
							if(!isAnythingPressed) {
								holdingElement = waypoint;
								offsetY = mouseY - waypoint.yPosition;
							}
						}
					}
				}
			}
		}
		
		closeWarning.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
		if (holdingElement != null) {
			mouseY = Math.min(Math.max(mouseY - offsetY, height/4 - 1), height - holdingElement.getHeight());
            holdingElement.updateYposition(mouseY, inputHeight);
		}
		
		if (holdingGroup != null) {
			mouseY = Math.min(Math.max(mouseY - offsetY, height/4 - 1), height - holdingGroup.height);
            holdingGroup.updateYposition(mouseY, inputHeight, inputMargin, holdingElement);
		}

		for (MacroWaypointGroupElement waypointGroupElement : waypoints) {
			for (MacroWaypointElement waypoint : waypointGroupElement.list) {
				waypoint.color.mouseClickMoved(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
			}
		}
    }
	
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
		if (holdingElement != null) {
			MacroWaypointElement tempHolding = holdingElement;
			
			MacroWaypointGroupElement lastWaypointGroup = waypoints.get(waypoints.size()-1);
			
			if(mouseY > lastWaypointGroup.yPosition + lastWaypointGroup.height) {
                for (MacroWaypointGroupElement group : waypoints) {
                	if(group.list.contains(tempHolding)) {
                		group.list.remove(tempHolding);
                		break;
                	}
                }
                
                
                Utils.debug("1");
				lastWaypointGroup.list.add(tempHolding);
			}
			
			for (MacroWaypointGroupElement waypointGroupElement : waypoints) {
				if(waypointGroupElement.list.isEmpty()) {
					if (mouseY >= waypointGroupElement.yPosition && mouseY <= waypointGroupElement.yPosition + waypointGroupElement.height) {
			                for (MacroWaypointGroupElement group : waypoints) {
			                	if(group.list.contains(tempHolding)) {
			                		group.list.remove(tempHolding);
			                		break;
			                	}
			                }
			                Utils.debug("2");
							waypointGroupElement.list.add(tempHolding);
							break;
					}
				}
				
				if (mouseY >= waypointGroupElement.yPosition && mouseY <= waypointGroupElement.yPosition + inputHeight + inputMargin/2) {
	                for (MacroWaypointGroupElement group : waypoints) {
	                	if(group.list.contains(tempHolding)) {
	                		group.list.remove(tempHolding);
	                		break;
	                	}
	                }
	                Utils.debug("3");
					waypointGroupElement.list.add(0, tempHolding);
				}
			
				for (int i = 0; i < waypointGroupElement.list.size(); i++) {
					MacroWaypointElement element = waypointGroupElement.list.get(i);
				
					if(!waypointGroupElement.opened.isOpened()) continue;
					if(tempHolding == element) continue;
					if(element.isHovered(mouseX, mouseY)) { // go beetwen elements
						int elementIndex = i;
						int holdingIndex = waypointGroupElement.list.indexOf(tempHolding);
						int sidePlus = (element.isHigherHalfHovered(mouseX, mouseY))?-1:0;
	
			            if (!waypointGroupElement.list.contains(tempHolding)) { // from one group to another
			                for (MacroWaypointGroupElement group : waypoints) {
			                	if(group.list.contains(tempHolding)) {
			                		group.list.remove(tempHolding);
			                		break;
			                	}
			                }
			            }else {
			            	waypointGroupElement.list.remove(tempHolding);
			            }
			            
			            int index = Math.max(0, waypointGroupElement.list.indexOf(element)+1+sidePlus);
			            Utils.debug("4");
						waypointGroupElement.list.add(index, tempHolding);
						break;
					}
				}
			}
			
			holdingElement = null;
		}
		
		if (holdingGroup != null) {
			MacroWaypointGroupElement tempHolding = holdingGroup;
			tempHolding.opened.setOpened(wasOpenedBeforeHolding);
			
			for (int i = 0; i < waypoints.size(); i++) {
				MacroWaypointGroupElement element = waypoints.get(i);
			
				if(tempHolding == element) continue;
				if(element.isHovered(mouseX, mouseY)) { // go beetwen elements
					int elementIndex = i;
					int holdingIndex = waypoints.indexOf(tempHolding);
					int sidePlus = (element.isHigherHalfHovered(mouseX, mouseY))?-1:0;

	            	waypoints.remove(tempHolding);
		            
		            int index = Math.max(0, waypoints.indexOf(element)+1+sidePlus);
		            
		            waypoints.add(index, tempHolding);
					break;
				}else if(mouseY > waypoints.get(waypoints.size()-1).yPosition + waypoints.get(waypoints.size()-1).height) {
					waypoints.remove(tempHolding);

					waypoints.add(tempHolding);
					break;
				}
			}
			
			holdingGroup = null;
		}
		
		for (MacroWaypointGroupElement waypointGroupElement : waypoints) {
			for (MacroWaypointElement waypoint : waypointGroupElement.list) {
				waypoint.color.mouseReleased(mouseX, mouseY);
			}
		}
    }
	
	@Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        if (!closeWarning.showElement) {
        	scrollbar.handleMouseInput();
        	
        	if(Mouse.getEventDWheel() > 0) {
        		snapToWaypointY();
        	}
        }
    }
	
	@Override
	public void updateScreen() {
		for (MacroWaypointGroupElement waypointGroupElement : waypoints) {
			for (MacroWaypointElement waypoint : waypointGroupElement.list) {
				List<TextField> inputs = waypoint.getListOfTextFields();
				
				for (TextField input : inputs) {
					input.updateCursorCounter();
				}
				
				waypoint.color.updateCursorCounter();
			}
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private boolean nameExists(String name, MacroWaypointGroupElement currentGroup, Map<String, MacroWaypointGroup> waypointsList) {
	    return waypointsList.containsKey(name) || waypoints.stream()
	        .anyMatch(g -> g != currentGroup && g.name.equals(name));
	}
	
	private void SaveWaypoints() {
	    Location.checkTabLocation();
	    Map<String, MacroWaypointGroup> waypointsList = MacroWaypoints.GetWaypointsWithoutLocation();
	    boolean isError = false;

	    for (MacroWaypointGroupElement group : waypoints) {
	    	String originalName = group.name;
	    	String finalName = originalName;
	    	int suffix = 1;

	    	while (nameExists(finalName, group, waypointsList)) {
	    	    finalName = originalName + suffix;
	    	    suffix++;
	    	}
	    	
	    	group.changeName(finalName);


	        List<MacroWaypoint> groupList = new ArrayList<>();
	        for (int j = group.list.size() - 1; j >= 0; j--) {
	            MacroWaypointElement waypoint = group.list.get(j);

	            waypoint.color.setTextColor(14737632);
	            waypoint.x.setTextColor(14737632);
	            waypoint.y.setTextColor(14737632);
	            waypoint.z.setTextColor(14737632);

	            String name = waypoint.name.getText();
	            String function = waypoint.function.getText();
	            String color = waypoint.color.getColorString().replace("#", "");
	            boolean enabled = waypoint.enabled.isFull();

	            try {
	                Color.decode("#" + color);
	            } catch (NumberFormatException e) {
	                waypoint.color.setTextColor(11217193);
	                isError = true;
	            }

	            float x = 0, y = 0, z = 0, noiseLevel = 0f;
	            Float yaw = 0f, pitch = 0f;

	            try { x = Float.parseFloat(waypoint.x.getText()); }
	            catch (NumberFormatException e) { waypoint.x.setTextColor(11217193); isError = true; }

	            try { y = Float.parseFloat(waypoint.y.getText()); }
	            catch (NumberFormatException e) { waypoint.y.setTextColor(11217193); isError = true; }

	            try { z = Float.parseFloat(waypoint.z.getText()); }
	            catch (NumberFormatException e) { waypoint.z.setTextColor(11217193); isError = true; }

	            try { yaw = Float.parseFloat(waypoint.yaw.getText()); }
	            catch (NumberFormatException e) { yaw = null; }

	            try { pitch = Float.parseFloat(waypoint.pitch.getText()); }
	            catch (NumberFormatException e) { pitch = null; }

	            try { noiseLevel = Float.parseFloat(waypoint.noiseLevel.getText()); }
	            catch (NumberFormatException e) { waypoint.noiseLevel.setTextColor(11217193); isError = true; }

	            boolean leftClick = waypoint.leftClick.isFull;
	            boolean rightClick = waypoint.rightClick.isFull;
	            boolean left = waypoint.left.isFull;
	            boolean right = waypoint.right.isFull;
	            boolean back = waypoint.back.isFull;
	            boolean forward = waypoint.forward.isFull;
	            boolean sneak = waypoint.sneak.isFull;

	            if (isError) {
	                saveButton.packedFGColour = 14258834;
	                return;
	            }

	            groupList.add(0, new MacroWaypoint(
	                new Waypoint(name, color, x, y, z,
	                Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld), 1, true),
	                yaw, pitch, left, right, back, forward, leftClick, rightClick, sneak, noiseLevel, function, enabled));
	        }

	        waypointsList.computeIfAbsent(finalName + " %" + group.world,
	            k -> new MacroWaypointGroup(new ArrayList<>(), group.world, group.enabled.isFull(), group.opened.isOpened()))
	            .list.addAll(0, groupList);
	    }

	    saveButton.packedFGColour = 11131282;
	    MacroWaypoints.waypointsList = waypointsList;
	    MacroWaypoints.doneMacro = waypointsList.values().stream()
	        .flatMap(group -> group.list.stream())
	        .collect(Collectors.toCollection(ArrayList::new));

	    ConfigHandler.SaveMacroWaypoint(waypointsList);
	}


	
	private void CloseGui() {
		if(closeWarning.showElement == true) return;

		if(isChanged()) {
			closeWarning.changeElementActive(true);
		}else {
			Minecraft.getMinecraft().thePlayer.closeScreen();
		}
	}
	
	private boolean isChanged() {
	    Location.checkTabLocation();
	    Map<String, MacroWaypointGroup> currentWaypoints = MacroWaypoints.GetLocationWaypoints();
	    if (currentWaypoints.size() != waypoints.size()) return true;

	    try {
		    for (MacroWaypointGroupElement group : waypoints) {
		        MacroWaypointGroup storedGroup = currentWaypoints.get(group.name + " %" + group.world);
		        if (storedGroup == null || storedGroup.list.size() != group.list.size()) return true;
		        if (group.enabled.isFull() != storedGroup.enabled) return true;
		        if (group.opened.isOpened() != storedGroup.opened) return true;
	
		        for (int i = 0; i < group.list.size(); i++) {
		            MacroWaypointElement waypoint = group.list.get(i);
		            MacroWaypoint current = storedGroup.list.get(i);
	
		            String name = waypoint.name.getText();
		            String color = waypoint.color.getColorString().replace("#", "");
		            String function = waypoint.function.getText();
		            boolean enabled = waypoint.enabled.isFull();
	
		            boolean leftClick = waypoint.leftClick.isFull;
		            boolean rightClick = waypoint.rightClick.isFull;
		            boolean left = waypoint.left.isFull;
		            boolean right = waypoint.right.isFull;
		            boolean back = waypoint.back.isFull;
		            boolean forward = waypoint.forward.isFull;
		            boolean sneak = waypoint.sneak.isFull;
	
		            try {
		                Color.decode("#" + color);
		                float x = Float.parseFloat(waypoint.x.getText());
		                float y = Float.parseFloat(waypoint.y.getText());
		                float z = Float.parseFloat(waypoint.z.getText());
		                float noiseLevel = Float.parseFloat(waypoint.noiseLevel.getText());
	
		                Float yaw = null, pitch = null;
		                try { yaw = Float.parseFloat(waypoint.yaw.getText()); } catch (NumberFormatException ignored) {}
		                try { pitch = Float.parseFloat(waypoint.pitch.getText()); } catch (NumberFormatException ignored) {}
	
		                Waypoint wp = current.waypoint;
		                if (!name.equals(wp.name) ||
		                    !color.equalsIgnoreCase(wp.color) ||
		                    !function.equals(current.function) ||
		                    enabled != current.enabled ||
		                    leftClick != current.leftClick ||
		                    rightClick != current.rightClick ||
		                    left != current.left ||
		                    right != current.right ||
		                    back != current.back ||
		                    forward != current.forward ||
		                    sneak != current.sneak ||
		                    Float.compare(current.noiseLevel, noiseLevel) != 0 ||
		                    Float.compare(wp.coords[0], x) != 0 ||
		                    Float.compare(wp.coords[1], y) != 0 ||
		                    Float.compare(wp.coords[2], z) != 0 ||
		                    !floatEqualsNullable(current.yaw, yaw) ||
		                    !floatEqualsNullable(current.pitch, pitch)) {
		                    return true;
		                }
		            } catch (NumberFormatException e) {
		                return true;
		            }
		        }
		    }
	
		    return false;
	    } catch (Exception e) {
			Utils.writeError(e);
			return true;
		}
	}

	private boolean floatEqualsNullable(Float a, Float b) {
	    return a == null ? b == null : b != null && Float.compare(a, b) == 0;
	}

	
	public void updateWaypointsY() {
	    int contentHeight = waypoints.stream().mapToInt(group -> group.height + inputHeight).sum();
	    scrollbar.updateContentHeight(contentHeight);
	    int scrollOffset = scrollbar.getOffset();

	    int positionY = (int) (height / 4 + 2 + scrollOffset);

	    for (MacroWaypointGroupElement group : waypoints) {
	        if (group != holdingGroup) {
	            group.updateYpositionSmooth(positionY, inputHeight, inputMargin, holdingElement);
	        }
	        positionY += group.height + inputHeight;
	    }
	}

	public void snapToWaypointY() {
	    int contentHeight = waypoints.stream().mapToInt(group -> group.height + inputHeight).sum();
	    scrollbar.updateContentHeight(contentHeight);
	    int scrollOffset = scrollbar.getOffset();

	    int positionY = (int) (height / 4 + 2 + scrollOffset);

	    for (MacroWaypointGroupElement group : waypoints) {
	        if (group != holdingGroup) {
	            group.updateYposition(positionY, inputHeight, inputMargin, holdingElement);
	        }
	        positionY += group.height + inputHeight;
	        group.nameField.setVisible(false);
	    }
	}

	
	@Override
    public void onGuiClosed() {
		holdingElement = null;
		waypoints.clear();
	}

}
