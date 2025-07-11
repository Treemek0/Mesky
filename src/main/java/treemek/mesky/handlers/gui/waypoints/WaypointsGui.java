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
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
import treemek.mesky.handlers.gui.macrowaypoints.MacroWaypointGroupElement;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.HypixelCheck;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.MacroWaypoints;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.MacroWaypoints.MacroWaypointGroup;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.Waypoint;
import treemek.mesky.utils.Waypoints.WaypointGroup;

public class WaypointsGui extends GuiScreen {
	public static GuiTextField region;
	private GuiButton saveButton;
	
	public static List<WaypointGroupElement> waypoints = new ArrayList<>();
	
	public ScrollBar scrollbar = new ScrollBar();
	CloseWarning closeWarning = new CloseWarning();
	ChangingRegionWarning changingRegionWarning = new ChangingRegionWarning();
	
	WaypointElement holdingElement;
	WaypointGroupElement holdingGroup;
	boolean wasOpenedBeforeHolding = false;
	
	int inputMargin;
	int inputHeight;
	private float scrollbar_startPosition;
	private float scrollbarBg_height;
	private float scrollbar_width;


	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		
		drawRect(0, height/4 - 1, width, height, new Color(33, 33, 33,255).getRGB());
		
		double widthOfCheckTexts = width / 20;
        double checksScale = RenderHandler.getTextScale("Players", widthOfCheckTexts); // "Players" is the longest in Alerts and i want all to have the same scale

		
		// Draw text fields first
		for (WaypointGroupElement waypointGroupElement : waypoints) {
			if(holdingGroup == waypointGroupElement) continue;
			drawRect(width / 20 - inputHeight-1, waypointGroupElement.yPosition + inputHeight + inputMargin/2 - 1, (int) (width*0.9 + inputHeight*2 + 1), waypointGroupElement.yPosition + waypointGroupElement.height, new Color(15, 15, 15,255).getRGB()); // waypoint list dark
			drawRect(width / 20 - inputHeight, waypointGroupElement.yPosition + inputHeight + inputMargin/2 - 1, (int) (width*0.9 + inputHeight*2), waypointGroupElement.yPosition + waypointGroupElement.height - 1, new Color(25, 25, 25,255).getRGB()); // waypoint list light

			if(waypointGroupElement.opened.isOpened()) {
				for (WaypointElement waypoint : waypointGroupElement.list) {
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
				    
				    waypoint.scale.drawButton(mc, mouseX, mouseY);
				    
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
			
			holdingElement.enabled.drawButton(mc, mouseX, mouseY);
			
			holdingElement.color.drawTextBox();
			holdingElement.scale.drawButton(mc, mouseX, mouseY);
			
			if(!holdingElement.enabled.isFull()) {
				drawRect(holdingElement.xPosition, holdingElement.yPosition-2, holdingElement.xPosition + holdingElement.getWidth(), holdingElement.yPosition + holdingElement.getHeight()+2, new Color(28, 28, 28, 180).getRGB());
			}
	    }
    	
		drawRect(0, 0, width, height/4 - 1, new Color(33, 33, 33,255).getRGB());
		
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
        

 	    
        int positionY = (int)((height / 4) - RenderHandler.getTextHeight(checksScale) - 3);

        RenderHandler.drawText("Scale", scale_X, positionY, checksScale, true, 0xffffff);
        RenderHandler.drawText("Name", name_X, positionY, checksScale, true, 0xffffff);
        RenderHandler.drawText("X", coords_X, positionY, checksScale, true, 0xffffff);
        RenderHandler.drawText("Y", coords_X + coords_width + 5, positionY, checksScale, true, 0xffffff);
        RenderHandler.drawText("Z", coords_X + (coords_width*2) + 10, positionY, checksScale, true, 0xffffff);
        
        scrollbar.updateScrollBar((int)Math.min(20, (width * 0.025)), (height - (height / 4) - 10), (int) (width*0.9 + inputHeight*2 + 5), height/4);
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
	    changingRegionWarning = new ChangingRegionWarning();
	    
        int checkX = (int)(width / 4);
        int buttonWidth = 20;
        int buttonHeight = 20;
        
        
        // Save button
        int mainButtonsY = Math.max(22, (height/15));
        saveButton = new MeskyButton(-1, (int)(width * 0.8f), mainButtonsY, (int)(width * 0.2f), 20, "Save");
        this.buttonList.add(saveButton);
        
        // New waypoint button
        this.buttonList.add(new MeskyButton(-2, 0, mainButtonsY, (int)(width * 0.2f), 20, "New group"));
        
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

		// its gonna be 0 btw
		int ScrollOffset = scrollbar.getOffset();
		
        int positionY = (int) (height / 4 + ScrollOffset);
		
        if(waypoints.isEmpty()) {
	        for (Entry<String, WaypointGroup> waypointGroup : Waypoints.GetLocationWaypoints().entrySet()) {
	        	List<WaypointElement> list = new ArrayList<>();
	        	
				String groupName = waypointGroup.getKey();
				List<Waypoint> groupList = waypointGroup.getValue().list;
				
				for (Waypoint waypoint : groupList) {
					int i = 1;
					
		        	// Position 0 for inputs + every input height and their bottom margin
		        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
		        	
		        	DeleteButton deleteButton = new DeleteButton(0, (int)(width * 0.85f), inputFullPosition, inputHeight, inputHeight, "");
		        	deleteButton.enabled = waypoint.enabled;
		        	
		        	CheckButton enabled = new CheckButton(0, (int)(width * 0.9f), inputFullPosition, inputHeight, inputHeight, "", waypoint.enabled);
		        	
		    		// Name text input
		        	TextField waypointName = new TextField(0, waypointName_X, inputFullPosition, width / 4, inputHeight);
		        	waypointName.setColoredField(true);
		            waypointName.setMaxStringLength(512);
		            waypointName.setCanLoseFocus(true);
		            waypointName.setText(waypoint.name);
		            
		            
		            // color text input
		            ColorPicker colorPicker = new ColorPicker(0, waypointColor_X, 0, inputHeight);	        	
		            colorPicker.setText(waypoint.color);
		            colorPicker.enabled = waypoint.enabled;
		            
		            // scale slider
		            Slider scale = new Slider(0, waypointScale_X, inputFullPosition, width / 8, inputHeight, "", 0.5f, 5, 0.1f);
		            scale.setValue(waypoint.scale);
		            
		            // X coordinate input
		            TextField waypointX = new TextField(2, coords_X, inputFullPosition, coord_Width, inputHeight);
		            waypointX.setMaxStringLength(16);
		            waypointX.setCanLoseFocus(true);
		            waypointX.setText(Float.toString(waypoint.coords[0]));
		            
		            // Y coordinate input
		            TextField waypointY = new TextField(3, coords_X + coord_Width + 5, inputFullPosition, coord_Width, inputHeight);
		            waypointY.setMaxStringLength(16);
		            waypointY.setCanLoseFocus(true);
		            waypointY.setText(Float.toString(waypoint.coords[1]));
		            
		            // Z coordinate input
		            TextField waypointZ = new TextField(4, coords_X + (width / 5) + 10, inputFullPosition, coord_Width, inputHeight);
		            waypointZ.setMaxStringLength(16);
		            waypointZ.setCanLoseFocus(true);
		            waypointZ.setText(Float.toString(waypoint.coords[2]));
		
		            
		            list.add(new WaypointElement(waypointName, colorPicker, scale, waypointX, waypointY, waypointZ, deleteButton, enabled, inputMargin));
				}
				 
				WaypointGroupElement group = new WaypointGroupElement(groupName, list, waypointGroup.getValue().world);
				group.xPosition = waypointName_X;
				group.setEnabled(waypointGroup.getValue().enabled);
				group.opened.setOpened(waypointGroup.getValue().opened);
				waypoints.add(group);
				
	        }
        }else {
        	for (WaypointGroupElement waypointGroupElement : waypoints) {
        		waypointGroupElement.xPosition = waypointName_X;
        		
        		List<WaypointElement> waypointList = waypointGroupElement.list;
        		for (int i = 0; i < waypointList.size(); i++) {
					WaypointElement waypoint = waypointList.get(i);

		        	// Position 0 for inputs + every input height and their bottom margin
		        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
		        	
		        	DeleteButton deleteButton = new DeleteButton(0 + (5*i), (int)(width * 0.85f), inputFullPosition, inputHeight, inputHeight, "");
		        	deleteButton.enabled = waypoint.enabled.isFull();
		        	
		        	CheckButton enabled = new CheckButton(0, (int)(width * 0.9f), inputFullPosition, inputHeight, inputHeight, "", waypoint.enabled.isFull());
		        	
		        	// color text input
		            ColorPicker colorPicker = new ColorPicker(0, waypointColor_X, 0, inputHeight);
		            colorPicker.setText(waypoint.color.getColorString());
		        	colorPicker.enabled = waypoint.enabled.isFull();
		            
		    		// Name text input
		        	TextField waypointName = new TextField(0, waypointName_X, inputFullPosition, width / 4, inputHeight);
		        	waypointName.setColoredField(true);
		            waypointName.setMaxStringLength(512);
		            waypointName.setCanLoseFocus(true);
		            waypointName.setText(waypoint.name.getText());
		            
		            // scale slider
		            Slider scale = new Slider(0, waypointScale_X, inputFullPosition, width / 8, inputHeight, "", 0.5f, 5, 0.1f);
		            scale.setValue(waypoint.scale.getValue());
		            
		            // X coordinate input
		            TextField waypointX = new TextField(2, coords_X, inputFullPosition, coord_Width, inputHeight);
		            waypointX.setMaxStringLength(16);
		            waypointX.setCanLoseFocus(true);
		            waypointX.setText(waypoint.x.getText());
		            
		            // Y coordinate input
		            TextField waypointY = new TextField(3, coords_X + coord_Width + 5, inputFullPosition, coord_Width, inputHeight);
		            waypointY.setMaxStringLength(16);
		            waypointY.setCanLoseFocus(true);
		            waypointY.setText(waypoint.y.getText());
		            
		            // Z coordinate input
		            TextField waypointZ = new TextField(4, coords_X + (width / 5) + 10, inputFullPosition, coord_Width, inputHeight);
		            waypointZ.setMaxStringLength(16);
		            waypointZ.setCanLoseFocus(true);
		            waypointZ.setText(waypoint.z.getText());
		
		            waypointList.set(i, new WaypointElement(waypointName, colorPicker, scale, waypointX, waypointY, waypointZ, deleteButton, enabled, inputMargin));
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
		if(button == saveButton) {
			// Save button
			SaveWaypoints();
			return;
		}
		
		if(button.id == -2) {
			// Add group button
			WaypointGroupElement group = new WaypointGroupElement("default", new ArrayList<>(), Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld));
			group.xPosition = width / 20 + inputHeight + 10 + width / 8 + 5;
            waypoints.add(0, group);
            return;
		}


		for (WaypointGroupElement group : waypoints) {
			if(button == (GuiButton)group.addWaypoint) {
				EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
				float x = Float.parseFloat(String.format("%.2f", player.posX).replace(",", "."));
				float y = Float.parseFloat(String.format("%.2f", player.posY).replace(",", "."));
				float z = Float.parseFloat(String.format("%.2f", player.posZ).replace(",", "."));
			
				int waypointColor_X = width / 20;
			    int waypointScale_X = waypointColor_X + inputHeight + 10;
			    int waypointName_X = waypointScale_X + width / 8 + 5;
				int coords_X = waypointName_X + (width/4) + 5;
				int coord_Width = width / 10;
				
				int topOfGroup = group.yPosition;
				
				DeleteButton deleteButton = new DeleteButton(0, (int)(width * 0.85f), 0, inputHeight, inputHeight, "");
				
				CheckButton enabled = new CheckButton(0, (int)(width * 0.9f), 0, inputHeight, inputHeight, "", true);
				
				// Name text input
				TextField waypointName = new TextField(0, waypointName_X, topOfGroup, width / 4, inputHeight);
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
			    
			    group.list.add(0, new WaypointElement(waypointName, colorPicker, scale, waypointX, waypointY, waypointZ, deleteButton, enabled, inputMargin));
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
				
				for (WaypointGroupElement waypointGroupElement : waypoints) {
					if(waypointGroupElement.nameField.textboxKeyTyped(typedChar, keyCode)) {
						waypointGroupElement.name = waypointGroupElement.nameField.getText();
					}
					
					for (WaypointElement waypoint : waypointGroupElement.list) {
						List<TextField> inputs = waypoint.getListOfTextFields();
	
						waypoint.color.keyTyped(typedChar, keyCode);
						
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
			
			if(region != null) {
				if (mouseX >= region.xPosition && mouseX <= region.xPosition + region.width && mouseY >= region.yPosition && mouseY <= region.yPosition + region.height) {
					region.mouseClicked(mouseX, mouseY, mouseButton);
				}else {
					region.setFocused(false);
				}
			}
			
			boolean isOpenedColorPicker = false; // made it like that because mouseClick also hides picker so it wouldnt hide if i would make return
			if (mouseY <= ((height / 4))) { // is above waypoints line
				for (WaypointGroupElement waypointGroupElement : waypoints) {
					for (WaypointElement waypoint : waypointGroupElement.list) {
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
				}
				
				if(!isOpenedColorPicker) {
					super.mouseClicked(mouseX, mouseY, mouseButton); // save and new group buttons
				}
				
				return;
        	}
			
			
			for (WaypointGroupElement waypointGroupElement : waypoints) {
				if(!waypointGroupElement.enabled.isFull()) continue;
				
				for (WaypointElement waypoint : waypointGroupElement.list) {
					if(waypoint.color.mouseClick(mouseX, mouseY, mouseButton)) isOpenedColorPicker = true;
				}
			}
			
			if(isOpenedColorPicker) {
				for (WaypointGroupElement waypointGroupElement : waypoints) {
					for (WaypointElement waypoint : waypointGroupElement.list) {
						for (TextField input : waypoint.getListOfTextFields()) {
							input.setCursorPositionZero();
							input.setFocused(false);
						}
					}
				}
			
				return;
			}
		
			
			// focusing input when clicked
			for (WaypointGroupElement waypointGroupElement : waypoints) {
				if (waypointGroupElement.enabled.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) { // enabled
					waypointGroupElement.enabled.playPressSound(Minecraft.getMinecraft().getSoundHandler());
					waypointGroupElement.delete.enabled = waypointGroupElement.enabled.isFull();
					waypointGroupElement.addWaypoint.enabled = waypointGroupElement.enabled.isFull();
					for (WaypointElement waypoint : waypointGroupElement.list) {
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
						for (WaypointElement waypoint : waypointGroupElement.list) {
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
				
				double groupNameScale = RenderHandler.getTextScale("Players", width / 20);
		        double groupNameWidth = RenderHandler.getTextWidth(waypointGroupElement.name, groupNameScale);
				double groupNameHeight = RenderHandler.getTextHeight(groupNameScale);
		        double groupNameY = waypointGroupElement.yPosition + ((inputHeight + inputMargin/2)/2 - RenderHandler.getTextHeight(groupNameScale)/2);
				
				if (mouseX >= waypointGroupElement.xPosition && mouseX <= waypointGroupElement.xPosition + groupNameWidth && mouseY >= groupNameY && mouseY <= groupNameY + groupNameHeight) {
					waypointGroupElement.nameField.mouseClicked(mouseX, mouseY, mouseButton);
					waypointGroupElement.nameField.setVisible(true);
				}else {
					if(waypointGroupElement.nameField.getVisible()) {
						if (mouseX >= waypointGroupElement.xPosition && mouseX <= waypointGroupElement.xPosition + width/4 && mouseY >= groupNameY && mouseY <= groupNameY + groupNameHeight) {
							waypointGroupElement.nameField.mouseClicked(mouseX, mouseY, mouseButton);
						}else {
							waypointGroupElement.nameField.setVisible(false);
						}
					}else {
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
					for (WaypointElement waypoint : waypointGroupElement.list) {
						boolean isAnythingPressed = false;
						
						List<TextField> inputs = waypoint.getListOfTextFields();
						if (mouseY <= ((height / 4))) {
							for (TextField input : inputs) {
								input.setCursorPositionZero();
								input.setFocused(false);
							}
			                 break;
			        	}
	
						if(waypoint.enabled.mousePressed(mc, mouseX, mouseY)) {
							waypoint.enabled.playPressSound(Minecraft.getMinecraft().getSoundHandler());
			                this.actionPerformed(waypoint.enabled);
			                waypoint.deleteButton.enabled = waypoint.enabled.isFull();
			                waypoint.color.enabled = waypoint.enabled.isFull();
			                return;
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
								
								waypoint.enabled.mousePressed(mc, mouseX, mouseY);
								
								for (GuiButton guibutton : waypoint.getListOfButtons()) {
						            if (guibutton.yPosition <= ((height / 4) - 20)) {
						                 continue;
						        	 }
						            
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
						}
						
						if(waypoint.isHovered(mouseX, mouseY)) {
							if (waypoint.yPosition + waypoint.getHeight() <= ((height / 4) - 20)) {
				                 continue;
				        	 }
							
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
		changingRegionWarning.mouseClicked(mouseX, mouseY, mouseButton, this);
	}
	
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
		if (holdingElement != null) {
			mouseY = Math.min(Math.max(mouseY - offsetY, height/4 - 1), height - holdingElement.getHeight());
            holdingElement.updateYposition(mouseY);
		}
		
		if (holdingGroup != null) {
			mouseY = Math.min(Math.max(mouseY - offsetY, height/4 - 1), height - holdingGroup.height);
            holdingGroup.updateYposition(mouseY, inputHeight, inputMargin, holdingElement);
		}

		for (WaypointGroupElement waypointGroupElement : waypoints) {
			for (WaypointElement waypoint : waypointGroupElement.list) {
				waypoint.color.mouseClickMoved(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
				waypoint.scale.mouseClickMoved(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
			}
		}
    }
	
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
		if (holdingElement != null) {
			WaypointElement tempHolding = holdingElement;
			
			WaypointGroupElement lastWaypointGroup = waypoints.get(waypoints.size()-1);
			
			if(mouseY > lastWaypointGroup.yPosition + lastWaypointGroup.height) {
                for (WaypointGroupElement group : waypoints) {
                	if(group.list.contains(tempHolding)) {
                		group.list.remove(tempHolding);
                		break;
                	}
                }
                
                
                Utils.debug("1");
				lastWaypointGroup.list.add(tempHolding);
			}
			
			for (WaypointGroupElement waypointGroupElement : waypoints) {
				if(waypointGroupElement.list.isEmpty()) {
					if (mouseY >= waypointGroupElement.yPosition && mouseY <= waypointGroupElement.yPosition + waypointGroupElement.height) {
			                for (WaypointGroupElement group : waypoints) {
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
	                for (WaypointGroupElement group : waypoints) {
	                	if(group.list.contains(tempHolding)) {
	                		group.list.remove(tempHolding);
	                		break;
	                	}
	                }
	                Utils.debug("3");
					waypointGroupElement.list.add(0, tempHolding);
				}
			
				for (int i = 0; i < waypointGroupElement.list.size(); i++) {
					WaypointElement element = waypointGroupElement.list.get(i);
				
					if(!waypointGroupElement.opened.isOpened()) continue;
					if(tempHolding == element) continue;
					if(element.isHovered(mouseX, mouseY)) { // go beetwen elements
						int elementIndex = i;
						int holdingIndex = waypointGroupElement.list.indexOf(tempHolding);
						int sidePlus = (element.isHigherHalfHovered(mouseX, mouseY))?-1:0;
	
			            if (!waypointGroupElement.list.contains(tempHolding)) { // from one group to another
			                for (WaypointGroupElement group : waypoints) {
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
			WaypointGroupElement tempHolding = holdingGroup;
			tempHolding.opened.setOpened(wasOpenedBeforeHolding);
			
			for (int i = 0; i < waypoints.size(); i++) {
				WaypointGroupElement element = waypoints.get(i);
			
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
		
		for (WaypointGroupElement waypointGroupElement : waypoints) {
			for (WaypointElement waypoint : waypointGroupElement.list) {
				waypoint.color.mouseReleased(mouseX, mouseY);
				waypoint.scale.mouseReleased(mouseX, mouseY);
			}
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
		for (WaypointGroupElement waypointGroupElement : waypoints) {
			for (WaypointElement waypoint : waypointGroupElement.list) {
				List<TextField> inputs = waypoint.getListOfTextFields();
				
				for (TextField input : inputs) {
					input.updateCursorCounter();
				}
				
				waypoint.color.updateCursorCounter();
			}
		}
		
		if(region != null) {
			region.updateCursorCounter();
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private boolean nameExists(String name, WaypointGroupElement currentGroup, Map<String, WaypointGroup> waypointsList) {
	    return waypointsList.containsKey(name) || waypoints.stream()
	        .anyMatch(g -> g != currentGroup && g.name.equals(name));
	}
	
	private void SaveWaypoints() {
		Location.checkTabLocation();
		// its to prevent removing waypoints from other regions, so i just remove waypoints from my region and add them updated
		Map<String, WaypointGroup> waypointsList = Waypoints.GetWaypointsWithoutLocation();
		boolean isError = false;
		
		for (WaypointGroupElement group : waypoints) {
			String originalName = group.name;
	    	String finalName = originalName;
	    	int suffix = 1;

	    	while (nameExists(finalName, group, waypointsList)) {
	    	    finalName = originalName + suffix;
	    	    suffix++;
	    	}
	    	
	    	group.changeName(finalName);
			
			List<Waypoint> groupList = new ArrayList<>();
			for (int j = group.list.size() - 1; j >= 0; j--) {
				WaypointElement waypoint = group.list.get(j);
	
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
		        
		    	groupList.add(0, new Waypoint(name, color, x, y, z, Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld), (float) scale, enabled));
			}
			
			waypointsList.computeIfAbsent(finalName + " %" + group.world, k -> new WaypointGroup(new ArrayList<>(), group.world, group.enabled.isFull(), group.opened.isOpened())).list.addAll(0, groupList);
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
        int contentHeight = waypoints.stream().mapToInt(group -> group.height + inputHeight).sum(); // sums height and inputheight
		scrollbar.updateContentHeight(contentHeight);
		int ScrollOffset = scrollbar.getOffset();
		
		int positionY = (int) (height / 4 + 2 + ScrollOffset);

		for (WaypointGroupElement waypointGroupElement : waypoints) {
			if(waypointGroupElement != holdingGroup) {
				waypointGroupElement.updateYpositionSmooth(positionY, inputHeight, inputMargin, holdingElement);
			}
			positionY += waypointGroupElement.height + inputHeight;
		}
	}
	
	public void snapToWaypointY() {
	//	scrollbar.updateMaxBottomScroll(((waypoints.size() * (inputHeight + inputMargin))) - (height - (height/3)));
        int contentHeight = waypoints.stream().mapToInt(group -> group.height + inputHeight).sum(); // sums height and inputheight
		scrollbar.updateContentHeight(contentHeight);
		int ScrollOffset = scrollbar.getOffset();
		
		int positionY = (int) (height / 4 + 2 + ScrollOffset);
		
		for (WaypointGroupElement waypointGroupElement : waypoints) {
			if(waypointGroupElement != holdingGroup) {
				waypointGroupElement.updateYposition(positionY, inputHeight, inputMargin, holdingElement);
			}
			
			positionY += waypointGroupElement.height + inputHeight;
			
			waypointGroupElement.nameField.setVisible(false);
		}
	}
	
	public boolean isChanged() {
		Location.checkTabLocation();
		Map<String, WaypointGroup> currentWaypoints = Waypoints.GetLocationWaypoints();
		if(currentWaypoints.size() != waypoints.size()) return true;
		
		for (WaypointGroupElement group : waypoints) {
			WaypointGroup waypointFromCurrent = currentWaypoints.get(group.name + " %" + group.world);
			List<Waypoint> storedList = waypointFromCurrent.list;
			if (storedList == null || storedList.size() != group.list.size()) return true;
			if(group.enabled.isFull() !=  waypointFromCurrent.enabled) return true;
			if(group.opened.isOpened() !=  waypointFromCurrent.opened) return true;
			
			for (int i = 0; i < group.list.size(); i++) {
				WaypointElement waypoint = group.list.get(i);
				Waypoint current = storedList.get(i);

				String name = waypoint.name.getText();
				String color = waypoint.color.getColorString().replace("#", "");
				float scale = (float) waypoint.scale.getValue();
				boolean enabled = waypoint.enabled.isFull();

				try {
					Color.decode("#" + color);
					float x = Float.parseFloat(waypoint.x.getText());
					float y = Float.parseFloat(waypoint.y.getText());
					float z = Float.parseFloat(waypoint.z.getText());

					if (!name.equals(current.name) ||
					    !color.equalsIgnoreCase(ColorUtils.fixColor(current.color)) ||
					    enabled != current.enabled ||
					    Float.compare(current.scale, scale) != 0 ||
					    Float.compare(current.coords[0], x) != 0 ||
					    Float.compare(current.coords[1], y) != 0 ||
					    Float.compare(current.coords[2], z) != 0) {
						return true;
					}
				} catch (NumberFormatException e) {
					return true;
				}
			}
		}

		// Check for extra groups in stored data not present in UI
		if (currentWaypoints.size() != waypoints.size()) return true;

		return false;
	}

	
	@Override
    public void onGuiClosed() {
		holdingElement = null;
		region = null;
		waypoints.clear();
	}
	
}