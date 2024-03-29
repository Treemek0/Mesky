package treemek.mesky.handlers.gui;

import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

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
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiScrollingList;
import treemek.mesky.Reference;
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
	public static GuiTextField region;
	List<GuiButton> deleteButtonList = Lists.<GuiButton>newArrayList();
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
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
		drawRect(0, height/3 - 1, width, height, new Color(33, 33, 33,255).getRGB());
		
		for (GuiTextField input : allFields) {
			input.drawTextBox(); // drawing all inputs to screen
		}
		
		for (GuiButton button : deleteButtonList) {
			button.drawButton(mc, mouseX, mouseY); // drawing all delete buttons to screen (i had to separate it from drawScreen() because of order and its z levels (it had to be before next drawRect)
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
        
        
        int name_X = width / 6;
    	int coords_X = name_X + (width/4) + 15;
    	int coords_width = width / 10;
        
        int positionY = (int)((height / 3) - 15);
        RenderHandler.drawText("Name", name_X, positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("X", coords_X, positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Y", coords_X + coords_width + 5, positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Z", coords_X + (coords_width*2) + 10, positionY, 1, true, 0x7a7a7a);
        
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
	       
	        ResourceLocation scrollbar_background = new ResourceLocation(Reference.MODID, "/gui/scrollbar_background.png");
        	mc.getTextureManager().bindTexture(scrollbar_background);
        	//drawTexturedModalRect((int)(width * 0.9), scrollbar_startPosition, 0, 0, scrollbar_width, scrollbarBg_height);
        	drawRect((int)(width * 0.9), (int)scrollbar_startPosition, (int)((width * 0.9) + scrollbar_width), (int)(scrollbar_startPosition + scrollbarBg_height), new Color(8, 7, 10, 150).getRGB());
        	
        	ResourceLocation scrollbar = new ResourceLocation(Reference.MODID, "/gui/scrollbar.png");
        	mc.getTextureManager().bindTexture(scrollbar);
        	drawModalRectWithCustomSizedTexture((int)(width * 0.9), scrollbar_positionY, 0, 0, (int) scrollbar_width, scrollbar_height, scrollbar_width, scrollbar_height);
        }
        
        
        
       
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    allFields = new ArrayList<GuiTextField>();
	    deleteButtonList.clear();
	    
        int checkX = (int)(width / 4);
        int buttonWidth = 20;
        int buttonHeight = 20;
        int positionY = (int) (height / 3 + ScrollOffset);
        
        
        // Save button
        this.buttonList.add(new GuiButton(-1, (int)(width * 0.8f), (height/15), (int)(width * 0.2f), 20, "Save"));
        
        // New waypoint button
        this.buttonList.add(new GuiButton(-2, 0, (height/15), (int)(width * 0.2f), 20, "New waypoint"));
        
        // Updating location from tab
        Location.checkTabLocation();
        
        // Region text input
        region = new GuiTextField(-1, this.fontRendererObj, (int)(width * 0.8f), 1, width / 4, 20);
        region.setMaxStringLength(30);
        region.setCanLoseFocus(true);
        // if youre opening this gui then oldRegion is null and text is your currentLocation
        region.setText((oldRegion == null)?Locations.currentLocationText:oldRegion);
        
        
        int waypointName_X = width / 6;
		int coords_X = waypointName_X + (width/4) + 15;
		int coord_Width = width / 10;
		this.fontRendererObj.FONT_HEIGHT = (int)(height / 56.5);
		inputHeight = ((height / 25) < 12)?12:(height / 25);
		inputMargin = ((height / 40) < 5)?5:(height / 40);
		
		// This is so you cant scroll limitless, it takes every waypoint height with their margin and removes visible inputs height so you can scroll max to how much of inputs isnt visible
		maxBottomScroll = Math.min(0, -(((Waypoints.GetLocationWaypoints().size() * (inputHeight + inputMargin))) - (height - (height/3))));
		ScrollOffset = Math.max(ScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
		
		
        for (int i = 0; i < Waypoints.GetLocationWaypoints().size(); i++) {
        	// Position 0 for inputs + every input height and their bottom margin
        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
        	

        	
        	// Check if any part of the text field is within the visible area (stop rendering inputs that go over text)
            if (inputFullPosition <= ((height / 3) - 20)) {
                continue;
            }
        	
        	DeleteButton deleteButton = new DeleteButton(0 + (4*i), (int)(width * 0.8f), inputFullPosition, inputHeight, inputHeight, "");
        	deleteButtonList.add(deleteButton);
        	
        	
    		// Name text input
        	GuiTextField waypointName = new GuiTextField(0 + (4 * i), this.fontRendererObj, waypointName_X, inputFullPosition, width / 4, inputHeight);
            waypointName.setMaxStringLength(32);
            waypointName.setCanLoseFocus(true);
            waypointName.setText(Waypoints.GetLocationWaypoints().get(i).getName());
            allFields.add(waypointName);
            
            
            
            // X coordinate input
            GuiTextField waypointX = new GuiTextField(1 + (4 * i), this.fontRendererObj, coords_X, inputFullPosition, coord_Width, inputHeight);
            waypointX.setMaxStringLength(16);
            waypointX.setCanLoseFocus(true);
            waypointX.setText(Float.toString(Waypoints.GetLocationWaypoints().get(i).getCoords()[0]));
            allFields.add(waypointX);
            
            // Y coordinate input
            GuiTextField waypointY = new GuiTextField(2 + (4 * i), this.fontRendererObj, coords_X + coord_Width + 5, inputFullPosition, coord_Width, inputHeight);
            waypointY.setMaxStringLength(16);
            waypointY.setCanLoseFocus(true);
            waypointY.setText(Float.toString(Waypoints.GetLocationWaypoints().get(i).getCoords()[1]));
            allFields.add(waypointY);
            
            // Z coordinate input
            GuiTextField waypointZ = new GuiTextField(3 + (4 * i), this.fontRendererObj, coords_X + (width / 5) + 10, inputFullPosition, coord_Width, inputHeight);
            waypointZ.setMaxStringLength(16);
            waypointZ.setCanLoseFocus(true);
            waypointZ.setText(Float.toString(Waypoints.GetLocationWaypoints().get(i).getCoords()[2]));
            allFields.add(waypointZ);
    	
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
			// Add waypoint button
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			Waypoints.addWaypoint("Name", (int)player.posX, (int)player.posY, (int)player.posZ);
			int waypointName_X = width / 6;
			int coords_X = waypointName_X + (width/4) + 15;
			int coord_Width = width / 10;
			this.fontRendererObj.FONT_HEIGHT = (int)(height / 56.5);
			int i = (allFields.size() / 4);
			int positionY = (int) (height / 3 + ScrollOffset);
			int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
        	
			// I did that instead of just initGui() so that it doesnt change back other inputs if theyre not saved
			maxBottomScroll = Math.min(0, -(((Waypoints.GetLocationWaypoints().size() * (inputHeight + inputMargin))) - (height - (height/3))));
    		ScrollOffset = Math.max(ScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
			
        	DeleteButton deleteButton = new DeleteButton(0 + (4*i), (int)(width * 0.8f), inputFullPosition, inputHeight, inputHeight, "");
        	deleteButtonList.add(deleteButton);
        	
    		// Name text input
        	GuiTextField waypointName = new GuiTextField(0 + (4 * i), this.fontRendererObj, waypointName_X, inputFullPosition, width / 4, inputHeight);
            waypointName.setMaxStringLength(32);
            waypointName.setCanLoseFocus(true);
            waypointName.setText("Name");
            allFields.add(waypointName);

            // X coordinate input
            GuiTextField waypointX = new GuiTextField(1 + (4 * i), this.fontRendererObj, coords_X, inputFullPosition, coord_Width, inputHeight);
            waypointX.setMaxStringLength(16);
            waypointX.setCanLoseFocus(true);
            waypointX.setText(Integer.toString((int)player.posX));
            allFields.add(waypointX);
            
            // Y coordinate input
            GuiTextField waypointY = new GuiTextField(2 + (4 * i), this.fontRendererObj, coords_X + coord_Width + 5, inputFullPosition, coord_Width, inputHeight);
            waypointY.setMaxStringLength(16);
            waypointY.setCanLoseFocus(true);
            waypointY.setText(Integer.toString((int)player.posY));
            allFields.add(waypointY);
            
            // Z coordinate input
            GuiTextField waypointZ = new GuiTextField(3 + (4 * i), this.fontRendererObj, coords_X + (width / 5) + 10, inputFullPosition, coord_Width, inputHeight);
            waypointZ.setMaxStringLength(16);
            waypointZ.setCanLoseFocus(true);
            waypointZ.setText(Integer.toString((int)player.posZ));
            allFields.add(waypointZ);
    	
            return;
		}
		
        for (GuiButton guiButton : deleteButtonList) {
			if(guiButton.id == button.id) {
				int listId = button.id/4;
				Waypoints.deleteWaypointFromLocation(listId);
	            allFields.remove(button.id); // removing name
	            allFields.remove(button.id); // removing x
	            allFields.remove(button.id); // removing y
	            allFields.remove(button.id); // removing z
	            // i had to do them above like that so every has the same "button.id" because when i remove one the other goes down in list
	            deleteButtonList.remove(listId);
	            ScrollOffset += 0;
	            
	            // putting all inputs and buttons in place so theres no blank spot
	            maxBottomScroll = Math.min(0, -(((Waypoints.GetLocationWaypoints().size() * (inputHeight + inputMargin))) - (height - (height/3))));
	    		ScrollOffset = Math.max(ScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
	    		int positionY = (int) (height / 3 + ScrollOffset);
	    		deleteButtonList.clear();
	    		for (int i = 0; i < allFields.size(); i+=4) {
	    			int inputFullPosition = positionY + ((inputHeight + inputMargin) * (i/4));
	    			allFields.get(i).yPosition = inputFullPosition;
	    			allFields.get(i+1).yPosition = inputFullPosition;
	    			allFields.get(i+2).yPosition = inputFullPosition;
	    			allFields.get(i+3).yPosition = inputFullPosition;
	    			DeleteButton deleteButton = new DeleteButton(0 + (i), (int)(width * 0.8f), inputFullPosition, inputHeight, inputHeight, "");
	            	deleteButtonList.add(deleteButton);
				}
	            return;
			}
		}
    }
	
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		System.out.println(keyCode);
		if(keyCode == 1) {
			Minecraft.getMinecraft().thePlayer.closeScreen();
			return;
		}
		
		if(region.isFocused()) {
			region.textboxKeyTyped(typedChar, keyCode);
			if(keyCode != 203 && keyCode != 205 && keyCode != 29 && keyCode != 42 && keyCode != 54 && keyCode != 56 && keyCode != 184) {
				oldRegion = region.getText();
	
				// it loses focus when typing because of initGui and im too lazy to find other way
				
				// as i write it a while later idk what i meant with losing focus but this function updates gui to correct location while writing in region input
				region.setCanLoseFocus(false);
				buttonList.clear();
				initGui();
				region.setCanLoseFocus(true);
				region.setFocused(true);
				
			}
		}
		
		for (GuiTextField input : allFields) {
			if(input.isFocused()) {
				// numerical (coordinates inputs) [and because region input have id -1 it would count as coords input so i had to disable it]
				if((input.getId() - 1) % 4 == 0 || (input.getId() - 2) % 4 == 0 || (input.getId() - 3) % 4 == 0 && input.getId() != -1){
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
					// Name input
					input.textboxKeyTyped(typedChar, keyCode);
				}
			}
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		// focusing input when clicked
		for (GuiTextField input : allFields) {
			if (mouseX >= input.xPosition && mouseX <= input.xPosition + input.width && mouseY >= input.yPosition && mouseY <= input.yPosition + input.height) {
				input.mouseClicked(mouseX, mouseY, mouseButton);
			}else {
				input.setFocused(false);
			}
		}
		
		if (mouseX >= region.xPosition && mouseX <= region.xPosition + region.width && mouseY >= region.yPosition && mouseY <= region.yPosition + region.height) {
			region.mouseClicked(mouseX, mouseY, mouseButton);
		}else {
			region.setFocused(false);
		}
		

		if(mouseX >= width*0.9 && mouseX <= (width*0.9)+scrollbar_width && mouseY >= scrollbar_startPosition && mouseY <= scrollbar_startPosition + scrollbarBg_height) {
			// scroll to clicked area on scrollbar
			float precentOfScrollbar = ((mouseY - scrollbar_startPosition) / ((scrollbar_startPosition + scrollbarBg_height) - scrollbar_startPosition));
			ScrollOffset = maxBottomScroll * precentOfScrollbar;
			
			// changing position of fields and buttons
			int positionY = (int) (height / 3 + ScrollOffset);
    		deleteButtonList.clear();
    		
    		for (int i = 0; i < allFields.size(); i+=4) {
    			int inputFullPosition = positionY + ((inputHeight + inputMargin) * (i/4));
    			allFields.get(i).yPosition = inputFullPosition;
    			allFields.get(i+1).yPosition = inputFullPosition;
    			allFields.get(i+2).yPosition = inputFullPosition;
    			allFields.get(i+3).yPosition = inputFullPosition;
    			
    			DeleteButton deleteButton = new DeleteButton(0 + (i), (int)(width * 0.8f), inputFullPosition, inputHeight, inputHeight, "");
            	deleteButtonList.add(deleteButton);
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
    		maxBottomScroll = Math.min(0, -(((Waypoints.GetLocationWaypoints().size() * (inputHeight + inputMargin))) - (height - (height/3))));
    		ScrollOffset = Math.max(ScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
            
    		int positionY = (int) (height / 3 + ScrollOffset);
    		deleteButtonList.clear();
    		
    		for (int i = 0; i < allFields.size(); i+=4) {
    			int inputFullPosition = positionY + ((inputHeight + inputMargin) * (i/4));
    			allFields.get(i).yPosition = inputFullPosition;
    			allFields.get(i+1).yPosition = inputFullPosition;
    			allFields.get(i+2).yPosition = inputFullPosition;
    			allFields.get(i+3).yPosition = inputFullPosition;
    			
    			DeleteButton deleteButton = new DeleteButton(0 + (i), (int)(width * 0.8f), inputFullPosition, inputHeight, inputHeight, "");
            	deleteButtonList.add(deleteButton);
			}
        }
    }
	
	@Override
	public void updateScreen() {
		for (GuiTextField input : allFields) {
			input.updateCursorCounter();
		}
		region.updateCursorCounter();
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private void SaveWaypoints() {
		Location.checkTabLocation();
		// its to prevent removing waypoints from other regions, so i just remove waypoints from my region and add them updated
		List<Waypoint> waypointsList = Waypoints.GetWaypointsWithoutLocation();
		
		// idk why i did it like this with allFields.size(), but because of it we have to do "i -=4" since we have 4 input fields per waypoint
		// and i did "int i = allFields.size() - 4" so it start from the end because every time you pressed Save button the order was different
	    for (int i = allFields.size() - 4; i >= 0; i -= 4) {
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
