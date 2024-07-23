package treemek.mesky.handlers.gui.waypoints;

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
import java.util.Iterator;
import java.util.List;

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
import treemek.mesky.handlers.gui.elements.ChangingRegionWarning;
import treemek.mesky.handlers.gui.elements.CloseWarning;
import treemek.mesky.handlers.gui.elements.ScrollBar;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.EditButton;
import treemek.mesky.handlers.gui.elements.buttons.MeskyButton;
import treemek.mesky.utils.HypixelCheck;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.Waypoint;

public class WaypointsGui extends GuiScreen {
	public static GuiTextField region;
	private GuiButton saveButton;
	
	public static List<WaypointElement> waypoints = new ArrayList<>();
	
	public ScrollBar scrollbar = new ScrollBar(0,0,0,0,0);
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
			
			List<GuiTextField> inputs = waypoint.getListOfTextFields();
			
			for (GuiTextField input : inputs) {
				input.drawTextBox();
			}
			
			int radius = waypoint.color.height;
			RenderHandler.drawCircle(waypoint.color.xPosition - radius - 2, waypoint.color.yPosition, radius, Utils.getColorInt(waypoint.color.getText()));
	    }

	    // Reset color and blending state before drawing buttons
	    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

	    // Draw delete buttons
	    for (int i = 0; i < waypoints.size(); i++) {
	    	if(waypoints.get(i) == holdingElement) continue;
	    	
	    	if (waypoints.get(i).yPosition + waypoints.get(i).getHeight() <= ((height / 3))) {
                continue;
       	 	}
	    	
	    	for (GuiButton button : waypoints.get(i).getListOfButtons()) {	
				button.drawButton(mc, mouseX, mouseY);
			}
	    }
	    
	    
    	if(holdingElement != null) {
	    
	    	drawRect(holdingElement.xPosition, holdingElement.yPosition, holdingElement.xPosition + holdingElement.getWidth(), holdingElement.yPosition + holdingElement.getHeight(), new Color(28, 28, 28,255).getRGB());
	    	
	    	List<GuiTextField> inputs = holdingElement.getListOfTextFields();
			for (GuiTextField input : inputs) {
				input.drawTextBox();
			}
			
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		    GlStateManager.enableBlend();
		    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
			
			for (GuiButton button : holdingElement.getListOfButtons()) {	
				button.drawButton(mc, mouseX, mouseY);
			}
			
			int radius = holdingElement.color.height;
			RenderHandler.drawCircle(holdingElement.color.xPosition - radius - 2, holdingElement.color.yPosition, radius, Utils.getColorInt(holdingElement.color.getText()));
	    }
	    
		drawRect(0, 0, width, height/3 - 1, new Color(33, 33, 33,255).getRGB());
		
        double scale = 3;
        int textLength = mc.fontRendererObj.getStringWidth("Waypoints");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) ((height / 4) / scale);

        RenderHandler.drawText("Waypoints", titleX, titleY, scale, true, 0x3e91b5);
        
        if(HypixelCheck.isOnHypixel()) {
        	region.drawTextBox(); // Region input should be only visible on Hypixel (its in right upper corner)
    	}
        
        int color_X = width / 20;
        int name_X = color_X + width / 8;
    	int coords_X = name_X + (width/4) + 15;
    	int coords_width = width / 10;
        
        int positionY = (int)((height / 3) - 15);
        RenderHandler.drawText("Hex Color", color_X, positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Name", name_X, positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("X", coords_X, positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Y", coords_X + coords_width + 5, positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Z", coords_X + (coords_width*2) + 10, positionY, 1, true, 0x7a7a7a);
        
        scrollbar.updateScrollBar((int)Math.min(20, (width * 0.025)), (height - (height / 3) - 10), (int)(width * 0.95), height/3);
        updateWaypointsY();
        scrollbar.renderScrollBar();
       
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
        
        int waypointColor_X = width / 20;
        int waypointName_X = waypointColor_X + width / 8;
		int coords_X = waypointName_X + (width/4) + 15;
		int coord_Width = width / 10;
		inputHeight = ((height / 25) < 12)?12:(height / 25);
		inputMargin = ((height / 40) < 5)?5:(height / 40);
		
		scrollbar.updateMaxBottomScroll(((Waypoints.GetLocationWaypoints().size() * (inputHeight + inputMargin))) - (height - (height/3)));
		int ScrollOffset = scrollbar.getOffset(); // so scrolloffset doesnt go below maxbottomscroll
		
        int positionY = (int) (height / 3 + ScrollOffset);
		
        if(waypoints.isEmpty()) {
	        for (int i = 0; i < Waypoints.GetLocationWaypoints().size(); i++) {
	        	// Position 0 for inputs + every input height and their bottom margin
	        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
	        	
	        	DeleteButton deleteButton = new DeleteButton(0 + (5*i), (int)(width * 0.8f), inputFullPosition, inputHeight, inputHeight, "");
	        	
	        	
	        	
	    		// Name text input
	        	GuiTextField waypointName = new GuiTextField(0, this.fontRendererObj, waypointName_X, inputFullPosition, width / 4, inputHeight);
	            waypointName.setMaxStringLength(32);
	            waypointName.setCanLoseFocus(true);
	            waypointName.setText(Waypoints.GetLocationWaypoints().get(i).name);
	            
	            
	            // color text input
	        	GuiTextField waypointColor = new GuiTextField(1, this.fontRendererObj, waypointColor_X, inputFullPosition, width / 10, inputHeight);
	            waypointColor.setMaxStringLength(12);
	            waypointColor.setCanLoseFocus(true);
	            waypointColor.setText(Waypoints.GetLocationWaypoints().get(i).color);
	        	
	            
	            // X coordinate input
	            GuiTextField waypointX = new GuiTextField(2, this.fontRendererObj, coords_X, inputFullPosition, coord_Width, inputHeight);
	            waypointX.setMaxStringLength(16);
	            waypointX.setCanLoseFocus(true);
	            waypointX.setText(Float.toString(Waypoints.GetLocationWaypoints().get(i).coords[0]));
	            
	            // Y coordinate input
	            GuiTextField waypointY = new GuiTextField(3, this.fontRendererObj, coords_X + coord_Width + 5, inputFullPosition, coord_Width, inputHeight);
	            waypointY.setMaxStringLength(16);
	            waypointY.setCanLoseFocus(true);
	            waypointY.setText(Float.toString(Waypoints.GetLocationWaypoints().get(i).coords[1]));
	            
	            // Z coordinate input
	            GuiTextField waypointZ = new GuiTextField(4, this.fontRendererObj, coords_X + (width / 5) + 10, inputFullPosition, coord_Width, inputHeight);
	            waypointZ.setMaxStringLength(16);
	            waypointZ.setCanLoseFocus(true);
	            waypointZ.setText(Float.toString(Waypoints.GetLocationWaypoints().get(i).coords[2]));
	
	            
	            waypoints.add(new WaypointElement(waypointName, waypointColor, waypointX, waypointY, waypointZ, deleteButton));
	        }
        }else {
        	for (int i = 0; i < waypoints.size(); i++) {
	        	// Position 0 for inputs + every input height and their bottom margin
	        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
	        	
	        	DeleteButton deleteButton = new DeleteButton(0 + (5*i), (int)(width * 0.8f), inputFullPosition, inputHeight, inputHeight, "");
	        	
	        	
	        	
	    		// Name text input
	        	GuiTextField waypointName = new GuiTextField(0, this.fontRendererObj, waypointName_X, inputFullPosition, width / 4, inputHeight);
	            waypointName.setMaxStringLength(32);
	            waypointName.setCanLoseFocus(true);
	            waypointName.setText(waypoints.get(i).name.getText());
	            
	            
	            // color text input
	        	GuiTextField waypointColor = new GuiTextField(1, this.fontRendererObj, waypointColor_X, inputFullPosition, width / 10, inputHeight);
	            waypointColor.setMaxStringLength(12);
	            waypointColor.setCanLoseFocus(true);
	            waypointColor.setText(waypoints.get(i).color.getText());
	        	
	            
	            // X coordinate input
	            GuiTextField waypointX = new GuiTextField(2, this.fontRendererObj, coords_X, inputFullPosition, coord_Width, inputHeight);
	            waypointX.setMaxStringLength(16);
	            waypointX.setCanLoseFocus(true);
	            waypointX.setText(waypoints.get(i).x.getText());
	            
	            // Y coordinate input
	            GuiTextField waypointY = new GuiTextField(3, this.fontRendererObj, coords_X + coord_Width + 5, inputFullPosition, coord_Width, inputHeight);
	            waypointY.setMaxStringLength(16);
	            waypointY.setCanLoseFocus(true);
	            waypointY.setText(waypoints.get(i).y.getText());
	            
	            // Z coordinate input
	            GuiTextField waypointZ = new GuiTextField(4, this.fontRendererObj, coords_X + (width / 5) + 10, inputFullPosition, coord_Width, inputHeight);
	            waypointZ.setMaxStringLength(16);
	            waypointZ.setCanLoseFocus(true);
	            waypointZ.setText(waypoints.get(i).z.getText());
	
	            
	            waypoints.set(i, new WaypointElement(waypointName, waypointColor, waypointX, waypointY, waypointZ, deleteButton));
	        }
        }
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
	        int waypointName_X = waypointColor_X + width / 8;
			int coords_X = waypointName_X + (width/4) + 15;
			int coord_Width = width / 10;
    		
        	DeleteButton deleteButton = new DeleteButton(0, (int)(width * 0.8f), 0, inputHeight, inputHeight, "");
        	
    		// Name text input
        	GuiTextField waypointName = new GuiTextField(0, this.fontRendererObj, waypointName_X, 0, width / 4, inputHeight);
            waypointName.setMaxStringLength(32);
            waypointName.setCanLoseFocus(true);
            waypointName.setText("Name");
            
            // color text input
        	GuiTextField waypointColor = new GuiTextField(1, this.fontRendererObj, waypointColor_X, 0, width / 10, inputHeight);
            waypointColor.setMaxStringLength(12);
            waypointColor.setCanLoseFocus(true);
            waypointColor.setText("ffffff");
            
            
            // X coordinate input
            GuiTextField waypointX = new GuiTextField(2, this.fontRendererObj, coords_X, 0, coord_Width, inputHeight);
            waypointX.setMaxStringLength(16);
            waypointX.setCanLoseFocus(true);
            waypointX.setText(Float.toString(x));
            
            // Y coordinate input
            GuiTextField waypointY = new GuiTextField(3, this.fontRendererObj, coords_X + coord_Width + 5, 0, coord_Width, inputHeight);
            waypointY.setMaxStringLength(16);
            waypointY.setCanLoseFocus(true);
            waypointY.setText(Float.toString(y));

        	// Z coordinate input
            GuiTextField waypointZ = new GuiTextField(4, this.fontRendererObj, coords_X + (width / 5) + 10, 0, coord_Width, inputHeight);
            waypointZ.setMaxStringLength(16);
            waypointZ.setCanLoseFocus(true);
            waypointZ.setText(Float.toString(z));

            waypoints.add(0, new WaypointElement(waypointName, waypointColor, waypointX, waypointY, waypointZ, deleteButton));
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
					List<GuiTextField> inputs = waypoint.getListOfTextFields();
					
					for (GuiTextField input : inputs) {
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
	
	
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		closeWarning.mouseClicked(mouseX, mouseY, mouseButton);
		changingRegionWarning.mouseClicked(mouseX, mouseY, mouseButton, this);
		
		if(!closeWarning.showElement && !changingRegionWarning.showElement) {
			// focusing input when clicked
			for (int i = 0; i < waypoints.size(); i++) {
				WaypointElement waypoint = waypoints.get(i);
				boolean isAnythingPressed = false;
				List<GuiTextField> inputs = waypoint.getListOfTextFields();
				
				if (mouseY <= ((height / 3))) {
	                 break;
	        	 }
				
				for (GuiTextField input : inputs) {
					
					
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
		            GuiButton guibutton = waypoint.deleteButton;
		            
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
				
				if(waypoint.isHovered(mouseX, mouseY)) {
					if (waypoint.yPosition + waypoint.getHeight() <= ((height / 3) - 20)) {
		                 continue;
		        	 }
					
					if(!isAnythingPressed) {
						holdingElement = waypoint;
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
            
			for (int i = 0; i < waypoints.size(); i++) {
				WaypointElement element = waypoints.get(i);
				
				if(holdingElement == element) continue;
				if(element.isHovered(mouseX, mouseY)) {
					int elementIndex = i;
					int holdingIndex = waypoints.indexOf(holdingElement);
					
					waypoints.remove(holdingElement);
					if(elementIndex > holdingIndex) {
						waypoints.add(waypoints.indexOf(element)+1, holdingElement);
						break;
					}else {
						waypoints.add(waypoints.indexOf(element), holdingElement);
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
        
        if (scroll != 0 && !closeWarning.showElement && !changingRegionWarning.showElement) {
        	scrollbar.handleMouseInput(scroll);
        }
    }
	
	@Override
	public void updateScreen() {
		for (int i = 0; i < waypoints.size(); i++) {
			WaypointElement waypoint = waypoints.get(i);
			List<GuiTextField> inputs = waypoint.getListOfTextFields();
			
			for (GuiTextField input : inputs) {
				input.updateCursorCounter();
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
	        String color = waypoint.color.getText().replace("#", ""); 
	        
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
	        
	    	waypointsList.add(0, new Waypoint(name, color, x, y, z, Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld)));
	        
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
		scrollbar.updateMaxBottomScroll(((waypoints.size() * (inputHeight + inputMargin))) - (height - (height/3)));
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
		        String color = waypoint.color.getText().replace("#", ""); 
		        
		        float x = 0, y = 0, z = 0;
		        try {
		        	Color.decode("#" + color);
		            x = Float.parseFloat(waypoint.x.getText());
		            y = Float.parseFloat(waypoint.y.getText());
		        	z = Float.parseFloat(waypoint.z.getText()); 
		        } catch (NumberFormatException e) { isntEqual = true; break; }
	
		        if(Float.compare(waypointsList.get(i).coords[0], x) != 0) { isntEqual = true; break; }
		        if(Float.compare(waypointsList.get(i).coords[1], y) != 0) { isntEqual = true; break; }
		        if(Float.compare(waypointsList.get(i).coords[2], z) != 0) { isntEqual = true; break; }
		        if(!name.equals(waypointsList.get(i).name)) { isntEqual = true; break; }
		        if(!color.equals(waypointsList.get(i).color)) { isntEqual = true; break; }
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
