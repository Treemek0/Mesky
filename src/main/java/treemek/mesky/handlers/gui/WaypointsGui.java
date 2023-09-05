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
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.Waypoint;

public class WaypointsGui extends GuiScreen {
	ArrayList<GuiTextField> waypointNameFields;
	ArrayList<GuiTextField> coordsFields;
	int Xplace;
	int Xcoords = Xplace + (width/4) + 5;
	int coordWidth;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
        double scale = 3;
        int textLength = mc.fontRendererObj.getStringWidth("Waypoints");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) ((height / 4) / scale);

        RenderHandler.drawText("Waypoints", titleX, titleY, scale, true, 0x3e91b5);
        
        
        int infoY = (int)((height / 3) - 15);
        RenderHandler.drawText("Name", width / 6, infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("X", Xcoords, infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Y", Xcoords + coordWidth + 5, infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Z", Xcoords + (coordWidth*2) + 10, infoY, 1, true, 0x7a7a7a);
        
        
        for (GuiTextField input : waypointNameFields) {
			input.drawTextBox();
		}
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    
	    waypointNameFields = new ArrayList<GuiTextField>();
	    coordsFields = new ArrayList<GuiTextField>();
	    
        int checkX = (int)(width / 4);
        int positionY = height / 3;
        int buttonWidth = 20;
        int buttonHeight = 20;
        
        this.buttonList.add(new GuiButton(-1, (int)(width * 0.8f), (height/15), (int)(width * 0.2f), 20, "Save"));
        this.buttonList.add(new GuiButton(-2, 0, (height/15), (int)(width * 0.2f), 20, "New waypoint"));
        
        for (int i = 0; i < Waypoints.waypointsList.size(); i++) {
        	if(Waypoints.waypointsList.get(i).getWorld().equals(Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName())) {
	        	this.buttonList.add(new DeleteButton(0 + (4*i), (int)(width * 0.8f), positionY + (30 * i), 20, 20, ""));
	        	
	        	Xplace = width / 6;
	        	GuiTextField waypointName = new GuiTextField(1 + (4 * i), this.fontRendererObj, Xplace, positionY + (30 * i), width / 4, 20);
	            waypointName.setMaxStringLength(30);
	            waypointName.setCanLoseFocus(true);
	            waypointName.setText(Waypoints.waypointsList.get(i).getName());
	            waypointNameFields.add(waypointName);
	            
	            coordWidth = width / 10;
	            Xcoords = Xplace + waypointName.width + 15;
	            
	            GuiTextField waypointX = new GuiTextField(1 + (4 * i), this.fontRendererObj, Xcoords, positionY + (30 * i), coordWidth, 20);
	            waypointX.setMaxStringLength(16);
	            waypointX.setCanLoseFocus(true);
	            waypointX.setText(Float.toString(Waypoints.waypointsList.get(i).getCoords()[0]));
	            waypointNameFields.add(waypointX);
	            coordsFields.add(waypointX);
	            
	            GuiTextField waypointY = new GuiTextField(2 + (4 * i), this.fontRendererObj, Xcoords + coordWidth + 5, positionY + (30 * i), coordWidth, 20);
	            waypointY.setMaxStringLength(16);
	            waypointY.setCanLoseFocus(true);
	            waypointY.setText(Float.toString(Waypoints.waypointsList.get(i).getCoords()[1]));
	            waypointNameFields.add(waypointY);
	            coordsFields.add(waypointY);
	            
	            GuiTextField waypointZ = new GuiTextField(3 + (4 * i), this.fontRendererObj, Xcoords + (width / 5) + 10, positionY + (30 * i), coordWidth, 20);
	            waypointZ.setMaxStringLength(16);
	            waypointZ.setCanLoseFocus(true);
	            waypointZ.setText(Float.toString(Waypoints.waypointsList.get(i).getCoords()[0]));
	            waypointNameFields.add(waypointZ);
	            coordsFields.add(waypointZ);
        	}
        }	
	}
	
	@Override
    protected void actionPerformed(GuiButton button) {
		if(button.id == -1) {
			SaveWaypoints();
			return;
		}
		if(button.id == -2) {
			Waypoints.addWaypoint("Name", 0, 0, 0);;
			 buttonList.clear();
            waypointNameFields.clear();
            coordsFields.clear();
            initGui();
            return;
		}
        for (GuiButton guiButton : buttonList) {
			if(guiButton.id == button.id) {
				int listId = (button.id > 0)?(button.id/4):0;
				Waypoints.deleteWaypoint(listId);

	            // Clear existing buttons and text fields
	            buttonList.clear();
	            waypointNameFields.clear();
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
		
		for (GuiTextField input : waypointNameFields) {
			if(input.isFocused()) {
				// Backspace / leftArrow / rightArrow / . / delete
				if(coordsFields.contains(input) && keyCode != 14 && keyCode != 203 && keyCode != 205 && keyCode != 52 && keyCode != 211) {
					try {
						// coords
		                float coord = Integer.parseInt(String.valueOf(typedChar));
		                input.textboxKeyTyped(typedChar, keyCode);
		                
		            } catch (NumberFormatException ex) {
		            	return;
		            }
				}else {
					if(keyCode == 52 && input.getText().contains(".") && coordsFields.contains(input)) {
						return;
						// dis-allow more than one "." in coords 
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
		for (GuiTextField input : waypointNameFields) {
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
		for (GuiTextField input : waypointNameFields) {
			input.updateCursorCounter();
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void SaveWaypoints() {
		List<Waypoint> waypointsList = new ArrayList<Waypoint>();
	    for (int i = 0; i < waypointNameFields.size(); i += 4) {
	        String name = waypointNameFields.get(i).getText();
	        float x = 0, y = 0, z = 0;
	        try {
	            x = Float.parseFloat(waypointNameFields.get(i + 1).getText());
	            y = Float.parseFloat(waypointNameFields.get(i + 2).getText());
	            z = Float.parseFloat(waypointNameFields.get(i + 3).getText());
	            
	        } catch (NumberFormatException e) {
	            System.out.println(e);
	            continue; // Skip this iteration if there's a parsing error
	        }
	        waypointsList.add(new Waypoint(name, x, y, z, Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName()));
	    }
	    Waypoints.waypointsList = waypointsList;
	}
}
