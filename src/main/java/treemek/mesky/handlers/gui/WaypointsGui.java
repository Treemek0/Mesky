package treemek.mesky.handlers.gui;

import java.io.IOException;
import org.lwjgl.input.Keyboard;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.buttons.CheckButton;
import treemek.mesky.handlers.gui.buttons.DeleteButton;
import treemek.mesky.utils.HypixelCheck;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.Waypoint;

public class WaypointsGui extends GuiScreen {
	ArrayList<GuiTextField> allFields;
	ArrayList<GuiTextField> coordsFields;
	public static GuiTextField region;
	String oldRegion;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
        double scale = 3;
        int textLength = mc.fontRendererObj.getStringWidth("Waypoints");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) ((height / 4) / scale);

        RenderHandler.drawText("Waypoints", titleX, titleY, scale, true, 0x3e91b5);
        
        int waypointName_X = width / 6;
    	int coords_X = waypointName_X + (width/4) + 15;
    	int coord_Width = width / 10;
        
        int infoY = (int)((height / 3) - 15);
        RenderHandler.drawText("Name", waypointName_X, infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("X", coords_X, infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Y", coords_X + coord_Width + 5, infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Z", coords_X + (coord_Width*2) + 10, infoY, 1, true, 0x7a7a7a);
        
        
        for (GuiTextField input : allFields) {
        	if(input.getId() == -3 && !HypixelCheck.isOnHypixel()) {
        		continue;
        		// region should be only visible on hypixel
        	}
			input.drawTextBox();
		}
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    allFields = new ArrayList<GuiTextField>();
	    coordsFields = new ArrayList<GuiTextField>();
	    
        int checkX = (int)(width / 4);
        int positionY = height / 3;
        int buttonWidth = 20;
        int buttonHeight = 20;
        
        this.buttonList.add(new GuiButton(-1, (int)(width * 0.8f), (height/15), (int)(width * 0.2f), 20, "Save"));
        this.buttonList.add(new GuiButton(-2, 0, (height/15), (int)(width * 0.2f), 20, "New waypoint"));
        
        Location.checkTabLocation();
        
        region = new GuiTextField(-3, this.fontRendererObj, (int)(width * 0.8f), 1, width / 4, 20);
        region.setMaxStringLength(30);
        region.setCanLoseFocus(true);
        if(oldRegion == null) {
        	region.setText(Locations.currentLocationText);
        }else {
        	region.setText(oldRegion);
        }
        allFields.add(region);
        
        
        for (int i = 0; i < Waypoints.GetLocationWaypoints().size(); i++) {
    		int waypointName_X = width / 6;
    		int coords_X = waypointName_X + (width/4) + 15;
    		int coord_Width = width / 10;
    		
    		this.buttonList.add(new DeleteButton(0 + (4*i), (int)(width * 0.8f), positionY + (30 * i), 20, 20, ""));
        	
    		// each waypoint name text
        	GuiTextField waypointName = new GuiTextField(1 + (4 * i), this.fontRendererObj, waypointName_X, positionY + (30 * i), width / 4, 20);
            waypointName.setMaxStringLength(30);
            waypointName.setCanLoseFocus(true);
            waypointName.setText(Waypoints.GetLocationWaypoints().get(i).getName());
            allFields.add(waypointName);
            
            // each X waypoint coord
            GuiTextField waypointX = new GuiTextField(1 + (4 * i), this.fontRendererObj, coords_X, positionY + (30 * i), coord_Width, 20);
            waypointX.setMaxStringLength(16);
            waypointX.setCanLoseFocus(true);
            waypointX.setText(Float.toString(Waypoints.GetLocationWaypoints().get(i).getCoords()[0]));
            allFields.add(waypointX);
            coordsFields.add(waypointX);
            
            // each Y waypoint coord
            GuiTextField waypointY = new GuiTextField(2 + (4 * i), this.fontRendererObj, coords_X + coord_Width + 5, positionY + (30 * i), coord_Width, 20);
            waypointY.setMaxStringLength(16);
            waypointY.setCanLoseFocus(true);
            waypointY.setText(Float.toString(Waypoints.GetLocationWaypoints().get(i).getCoords()[1]));
            allFields.add(waypointY);
            coordsFields.add(waypointY);
            
            // each Z waypoint coord
            GuiTextField waypointZ = new GuiTextField(3 + (4 * i), this.fontRendererObj, coords_X + (width / 5) + 10, positionY + (30 * i), coord_Width, 20);
            waypointZ.setMaxStringLength(16);
            waypointZ.setCanLoseFocus(true);
            waypointZ.setText(Float.toString(Waypoints.GetLocationWaypoints().get(i).getCoords()[2]));
            allFields.add(waypointZ);
            coordsFields.add(waypointZ);
    	
        }	
	}
	
	@Override
    protected void actionPerformed(GuiButton button) {
		if(button.id == -1) {
			// Save button
			SaveWaypoints();
			return;
		}
		
		if(button.id == -2) {
			// Add weaypoint button
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			Waypoints.addWaypoint("Name", (int)player.posX, (int)player.posY, (int)player.posZ);;
			buttonList.clear();
			oldRegion = region.getText();
            allFields.clear();
            coordsFields.clear();
            initGui();
            return;
		}
		
        for (GuiButton guiButton : buttonList) {
			if(guiButton.id == button.id) {
				int listId = (button.id > 0)?(button.id/4):0;
				Waypoints.deleteWaypointFromLocation(listId);

	            // Clear existing buttons and text fields
	            buttonList.clear();
	            oldRegion = region.getText();
	            allFields.clear();
	            coordsFields.clear();

	            // Reinitialize the GUI with updated data
	            initGui();
	            return;
			}
		}
    }
	
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		// TODO Auto-generated method stub
		System.out.println(keyCode);
		if(keyCode == 1) {
			Minecraft.getMinecraft().thePlayer.closeScreen();
			return;
		}
		
		for (GuiTextField input : allFields) {
			if(input.isFocused()) {
				if(coordsFields.contains(input)) {
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

						
				}else{
					// name
					input.textboxKeyTyped(typedChar, keyCode);
					if(input.getId() == -3 && keyCode != 203 && keyCode != 205 && keyCode != 29 && keyCode != 42 && keyCode != 54 && keyCode != 56 && keyCode != 184) {
						oldRegion = region.getText();
			
						// it loses focus when typing because of initGui and im too lazy to find other way
						region.setCanLoseFocus(false);
						buttonList.clear();
						initGui();
						region.setCanLoseFocus(true);
						region.setFocused(true);
						
					}
				}
			}
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		// focusing
		for (GuiTextField input : allFields) {
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
		// sam nawet nwm
		for (GuiTextField input : allFields) {
			input.updateCursorCounter();
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void SaveWaypoints() {
		Location.checkTabLocation();
		// it to prevent removing waypoints from other regions, so i just remove waypoints from my region and add them updated
		List<Waypoint> waypointsList = Waypoints.GetWaypointsWithoutLocation();
		
	    for (int i = 1; i < allFields.size() - 1; i += 4) {
	    	
	        String name = allFields.get(i).getText();
	        float x = 0, y = 0, z = 0;
	        
	        try {
	            x = Float.parseFloat(allFields.get(i + 1).getText());
	            y = Float.parseFloat(allFields.get(i + 2).getText());
	            z = Float.parseFloat(allFields.get(i + 3).getText());
	            
	        } catch (NumberFormatException e) {
	            System.out.println(e);
	            continue; // Skip this iteration if there's a parsing error
	        }
	        
	        if(!HypixelCheck.isOnHypixel()) {
	    		waypointsList.add(0, new Waypoint(name, x, y, z, Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName()));
	    	}else{
	    		if(Locations.currentLocationText != null) {
	    			waypointsList.add(0, new Waypoint(name, x, y, z, region.getText()));
	    		}
	    	}
	        
	    }
	    Waypoints.waypointsList = waypointsList;
	    ConfigHandler.SaveWaypoint(waypointsList);
	}
}
