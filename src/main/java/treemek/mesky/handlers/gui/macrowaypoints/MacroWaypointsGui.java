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
import java.util.Iterator;
import java.util.List;

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
import treemek.mesky.handlers.gui.chatfunctions.ChatFunctionElement;
import treemek.mesky.handlers.gui.elements.CloseWarning;
import treemek.mesky.handlers.gui.elements.ColorPicker;
import treemek.mesky.handlers.gui.elements.ScrollBar;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.MeskyButton;
import treemek.mesky.handlers.gui.waypoints.WaypointElement;
import treemek.mesky.handlers.gui.elements.buttons.MacroButton;
import treemek.mesky.utils.ChatFunctions;
import treemek.mesky.utils.HypixelCheck;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.MacroWaypoints;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.MacroWaypoints.MacroWaypoint;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.ChatFunctions.ChatFunction;
import treemek.mesky.utils.Waypoints.Waypoint;

public class MacroWaypointsGui extends GuiScreen {
	private GuiButton saveButton;
	
	public static List<MacroWaypointElement> waypoints = new ArrayList<>();
	
	CloseWarning closeWarning = new CloseWarning();
	ScrollBar scrollbar = new ScrollBar(0,0,0,0,0);
	
	int inputMargin;
	int inputHeight;
	private float scrollbar_startPosition;
	private float scrollbarBg_height;
	private float scrollbar_width;

	private MacroWaypointElement holdingElement;

	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
		drawRect(0, height/3 - 1, width, height, new Color(33, 33, 33,255).getRGB());
		
		// Draw text fields first
		for (int i = 0; i < waypoints.size(); i++) {
			MacroWaypointElement waypoint = waypoints.get(i);
			if(waypoint == holdingElement) continue;
			
			if (waypoint.yPosition + waypoint.getHeight() <= ((height / 3))) {
                continue;
       	 	}
			
			List<GuiTextField> inputs = waypoint.getListOfTextFields();
			
			for (GuiTextField input : inputs) {
				input.drawTextBox();
			}
			
			waypoint.color.drawTextBox();
	    }

	    // Reset color and blending state before drawing buttons
	    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

	    // Draw delete buttons
	    for (int i = 0; i < waypoints.size(); i++) {
	    	MacroWaypointElement waypoint = waypoints.get(i);
	    	if(waypoint == holdingElement) continue;
	    	
	    	if (waypoint.yPosition + waypoint.getHeight() <= ((height / 3))) {
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
			
			holdingElement.color.drawTextBox();
	    }
	    
		drawRect(0, 0, width, height/3 - 1, new Color(33, 33, 33,255).getRGB());
		
        double scale = 3;
        int textLength = mc.fontRendererObj.getStringWidth("Macro Waypoints");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) ((height / 4) / scale);

        RenderHandler.drawText("Macro Waypoints", titleX, titleY, scale, true, 0x3e91b5);
        
        int color_X = width / 20;
        int name_X = color_X + width / 10 + 5;
    	int coords_X = name_X + (width/4) + 10;
    	int coords_width = width / 15;
        
        int positionY = (int)((height / 3) - 15);
        RenderHandler.drawText("Hex Color", color_X, positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Name", name_X, positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("X", coords_X, positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Y", coords_X + (coords_width+5), positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Z", coords_X + ((coords_width+5)*2), positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Yaw", coords_X + ((coords_width+5)*3), positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Pitch", coords_X + ((coords_width+5)*4), positionY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Noise", coords_X + ((coords_width+5)*5), positionY, 1, true, 0x7a7a7a);
        
        scrollbar.updateScrollBar((int)Math.min(20, (width * 0.025)), (height - (height / 3) - 10), (int)(width * 0.95), height/3);
        updateWaypointsY();
        scrollbar.renderScrollBar();
       
	    super.drawScreen(mouseX, mouseY, partialTicks);
	    
	    closeWarning.drawElement(mc, mouseX, mouseY);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    buttonList.clear();
	    closeWarning = new CloseWarning();
	    
        int checkX = (int)(width / 4);
        int buttonWidth = 20;
        int buttonHeight = 20;
        
        
        // Save button
        saveButton = new MeskyButton(-1, (int)(width * 0.8f), (height/15), (int)(width * 0.2f), 20, "Save");
        this.buttonList.add(saveButton);
        
        // New waypoint button
        this.buttonList.add(new MeskyButton(-2, 0, (height/15), (int)(width * 0.2f), 20, "New waypoint"));
        
        // Updating location from tab
        Location.checkTabLocation();
        

        int waypointColor_X = width / 20;
        int waypointName_X = waypointColor_X + width / 10 + 5;
		int coords_X = waypointName_X + (width/4) + 10;
		int coord_Width = width / 15;
		inputHeight = ((height / 25) < 12)?12:(height / 25);
		int waypointHeight = inputHeight*2 + 5;
		inputMargin = ((height / 40) < 5)?5:(height / 40);
		
		scrollbar.updateMaxBottomScroll(((MacroWaypoints.GetLocationWaypoints().size() * (waypointHeight + inputMargin))) - (height - (height/3)));
		int ScrollOffset = scrollbar.getOffset(); // so scrolloffset doesnt go below maxbottomscroll
		
        int positionY = (int) (height / 3 + ScrollOffset);
		
        if(waypoints.isEmpty()) {
	        for (int i = 0; i < MacroWaypoints.GetLocationWaypoints().size(); i++) {
	        	// Position 0 for inputs + every input height and their bottom margin
	        	int inputFullPosition = positionY + ((waypointHeight + inputMargin) * i);
	        	
	        	DeleteButton deleteButton = new DeleteButton(0 + (5*i), (int)(width * 0.9f), inputFullPosition, inputHeight, inputHeight, "");
	        	
	        	
	        	
	    		// Name text input
	        	GuiTextField waypointName = new GuiTextField(0, this.fontRendererObj, waypointName_X, inputFullPosition, width / 4, inputHeight);
	            waypointName.setMaxStringLength(128);
	            waypointName.setCanLoseFocus(true);
	            waypointName.setText(MacroWaypoints.GetLocationWaypoints().get(i).waypoint.name);
	            
	            
	            // color text input
	            ColorPicker colorPicker = new ColorPicker(0, waypointColor_X, 0, inputHeight);	        	
	            colorPicker.setText(MacroWaypoints.GetLocationWaypoints().get(i).waypoint.color);
	        	
	            
	            // X coordinate input
	            GuiTextField waypointX = new GuiTextField(2, this.fontRendererObj, coords_X, inputFullPosition, coord_Width, inputHeight);
	            waypointX.setMaxStringLength(16);
	            waypointX.setCanLoseFocus(true);
	            waypointX.setText(Float.toString(MacroWaypoints.GetLocationWaypoints().get(i).waypoint.coords[0]));
	            
	            // Y coordinate input
	            GuiTextField waypointY = new GuiTextField(2, this.fontRendererObj, coords_X + coord_Width + 5, inputFullPosition, coord_Width, inputHeight);
	            waypointY.setMaxStringLength(16);
	            waypointY.setCanLoseFocus(true);
	            waypointY.setText(Float.toString(MacroWaypoints.GetLocationWaypoints().get(i).waypoint.coords[1]));
	            
	            // Z coordinate input
	            GuiTextField waypointZ = new GuiTextField(2, this.fontRendererObj, coords_X + ((coord_Width+5)*2), inputFullPosition, coord_Width, inputHeight);
	            waypointZ.setMaxStringLength(16);
	            waypointZ.setCanLoseFocus(true);
	            waypointZ.setText(Float.toString(MacroWaypoints.GetLocationWaypoints().get(i).waypoint.coords[2]));
	            
	            GuiTextField yaw = new GuiTextField(2, this.fontRendererObj, coords_X + ((coord_Width+5)*3), inputFullPosition, coord_Width, inputHeight);
	            yaw.setMaxStringLength(16);
	            yaw.setCanLoseFocus(true);
	            String yawF = (MacroWaypoints.GetLocationWaypoints().get(i).yaw != null)?Float.toString(MacroWaypoints.GetLocationWaypoints().get(i).yaw):"";
				yaw.setText(yawF);
	            
	            GuiTextField pitch = new GuiTextField(2, this.fontRendererObj, coords_X + ((coord_Width+5)*4), inputFullPosition, coord_Width, inputHeight);
	            pitch.setMaxStringLength(16);
	            pitch.setCanLoseFocus(true);
	            String pitchF = (MacroWaypoints.GetLocationWaypoints().get(i).pitch != null)?Float.toString(MacroWaypoints.GetLocationWaypoints().get(i).pitch):"";
	            pitch.setText(pitchF);
	
	            GuiTextField noiseLevel = new GuiTextField(2, this.fontRendererObj, coords_X + ((coord_Width+5)*5), inputFullPosition, coord_Width, inputHeight);
	            noiseLevel.setMaxStringLength(16);
	            noiseLevel.setCanLoseFocus(true);
	            noiseLevel.setText(Float.toString(MacroWaypoints.GetLocationWaypoints().get(i).noiseLevel));
	            
	            GuiTextField function = new GuiTextField(-1, this.fontRendererObj, coords_X, inputFullPosition + inputHeight+5, width / 4, inputHeight);
	            function.setMaxStringLength(128);
	            function.setCanLoseFocus(true);
	            function.setText(MacroWaypoints.GetLocationWaypoints().get(i).function);
	            
	            MacroButton leftClick = new MacroButton(0, waypointName_X, inputFullPosition + inputHeight+5, inputHeight, inputHeight, "", MacroWaypoints.GetLocationWaypoints().get(i).leftClick);
	            MacroButton rightClick = new MacroButton(1, waypointName_X + (inputHeight+5), inputFullPosition + inputHeight+5, inputHeight, inputHeight, "", MacroWaypoints.GetLocationWaypoints().get(i).rightClick);
	            MacroButton left = new MacroButton(2, waypointName_X + (inputHeight+5)*2, inputFullPosition + inputHeight+5, inputHeight, inputHeight, "", MacroWaypoints.GetLocationWaypoints().get(i).left);
	            MacroButton right = new MacroButton(3, waypointName_X + (inputHeight+5)*3, inputFullPosition + inputHeight+5, inputHeight, inputHeight, "", MacroWaypoints.GetLocationWaypoints().get(i).right);
	            MacroButton back = new MacroButton(4, waypointName_X + (inputHeight+5)*4, inputFullPosition + inputHeight+5, inputHeight, inputHeight, "", MacroWaypoints.GetLocationWaypoints().get(i).back);
	            MacroButton forward = new MacroButton(5, waypointName_X + (inputHeight+5)*5, inputFullPosition + inputHeight+5, inputHeight, inputHeight, "", MacroWaypoints.GetLocationWaypoints().get(i).forward);
	            
	            waypoints.add(new MacroWaypointElement(waypointName, colorPicker, waypointX, waypointY, waypointZ, yaw, pitch, left, right, back, forward, leftClick, rightClick, noiseLevel, function, deleteButton));
	        }	
        }else {
        	for (int i = 0; i < waypoints.size(); i++) {
	        	// Position 0 for inputs + every input height and their bottom margin
	        	int inputFullPosition = positionY + ((waypointHeight + inputMargin) * i);
	        	
	        	DeleteButton deleteButton = new DeleteButton(0 + (5*i), (int)(width * 0.9f), inputFullPosition, inputHeight, inputHeight, "");
	        	
	        	
	        	
	    		// Name text input
	        	GuiTextField waypointName = new GuiTextField(0, this.fontRendererObj, waypointName_X, inputFullPosition, width / 4, inputHeight);
	            waypointName.setMaxStringLength(128);
	            waypointName.setCanLoseFocus(true);
	            waypointName.setText(waypoints.get(i).name.getText());
	            
	            
	            // color text input
	            ColorPicker colorPicker = new ColorPicker(0, waypointColor_X, 0, inputHeight);
	            colorPicker.setText(waypoints.get(i).color.getColorString());
	        	
	            
	            // X coordinate input
	            GuiTextField waypointX = new GuiTextField(2, this.fontRendererObj, coords_X, inputFullPosition, coord_Width, inputHeight);
	            waypointX.setMaxStringLength(16);
	            waypointX.setCanLoseFocus(true);
	            waypointX.setText(waypoints.get(i).x.getText());
	            
	            // Y coordinate input
	            GuiTextField waypointY = new GuiTextField(2, this.fontRendererObj, coords_X + coord_Width + 5, inputFullPosition, coord_Width, inputHeight);
	            waypointY.setMaxStringLength(16);
	            waypointY.setCanLoseFocus(true);
	            waypointY.setText(waypoints.get(i).y.getText());
	            
	            // Z coordinate input
	            GuiTextField waypointZ = new GuiTextField(2, this.fontRendererObj, coords_X + ((coord_Width+5)*2), inputFullPosition, coord_Width, inputHeight);
	            waypointZ.setMaxStringLength(16);
	            waypointZ.setCanLoseFocus(true);
	            waypointZ.setText(waypoints.get(i).z.getText());
	            
	            GuiTextField yaw = new GuiTextField(2, this.fontRendererObj, coords_X + ((coord_Width+5)*3), inputFullPosition, coord_Width, inputHeight);
	            yaw.setMaxStringLength(16);
	            yaw.setCanLoseFocus(true);
				yaw.setText(waypoints.get(i).yaw.getText());
	            
	            GuiTextField pitch = new GuiTextField(2, this.fontRendererObj, coords_X + ((coord_Width+5)*4), inputFullPosition, coord_Width, inputHeight);
	            pitch.setMaxStringLength(16);
	            pitch.setCanLoseFocus(true);
	            pitch.setText(waypoints.get(i).pitch.getText());
	
	            GuiTextField noiseLevel = new GuiTextField(2, this.fontRendererObj, coords_X + ((coord_Width+5)*5), inputFullPosition, coord_Width, inputHeight);
	            noiseLevel.setMaxStringLength(16);
	            noiseLevel.setCanLoseFocus(true);
	            noiseLevel.setText(waypoints.get(i).noiseLevel.getText());
	            
	            GuiTextField function = new GuiTextField(-1, this.fontRendererObj, coords_X, inputFullPosition + inputHeight+5, width / 4, inputHeight);
	            function.setMaxStringLength(128);
	            function.setCanLoseFocus(true);
	            function.setText(waypoints.get(i).function.getText());
	            
	            MacroButton leftClick = new MacroButton(0, waypointName_X, inputFullPosition + inputHeight+5, inputHeight, inputHeight, "", waypoints.get(i).leftClick.isFull);
	            MacroButton rightClick = new MacroButton(1, waypointName_X + (inputHeight+5), inputFullPosition + inputHeight+5, inputHeight, inputHeight, "", waypoints.get(i).rightClick.isFull);
	            MacroButton left = new MacroButton(2, waypointName_X + (inputHeight+5)*2, inputFullPosition + inputHeight+5, inputHeight, inputHeight, "", waypoints.get(i).left.isFull);
	            MacroButton right = new MacroButton(3, waypointName_X + (inputHeight+5)*3, inputFullPosition + inputHeight+5, inputHeight, inputHeight, "", waypoints.get(i).right.isFull);
	            MacroButton back = new MacroButton(4, waypointName_X + (inputHeight+5)*4, inputFullPosition + inputHeight+5, inputHeight, inputHeight, "", waypoints.get(i).back.isFull);
	            MacroButton forward = new MacroButton(5, waypointName_X + (inputHeight+5)*5, inputFullPosition + inputHeight+5, inputHeight, inputHeight, "", waypoints.get(i).forward.isFull);
	            
	            waypoints.set(i, new MacroWaypointElement(waypointName, colorPicker, waypointX, waypointY, waypointZ, yaw, pitch, left, right, back, forward, leftClick, rightClick, noiseLevel, function, deleteButton));

	        }	
        }
	}
	
	@Override
    protected void actionPerformed(GuiButton button) {
		if(button.id == -1 && button == saveButton) {
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
			float yawF = Float.parseFloat(String.format("%.2f", player.rotationYaw).replace(",", "."));
			float pitchF = Float.parseFloat(String.format("%.2f", player.rotationPitch).replace(",", "."));
			
			int waypointColor_X = width / 20;
	        int waypointName_X = waypointColor_X + width / 10 + 5;
			int coords_X = waypointName_X + (width/4) + 10;
			int coord_Width = width / 15;
    		
        	DeleteButton deleteButton = new DeleteButton(0, (int)(width * 0.9f), 0, inputHeight, inputHeight, "");
        	
    		// Name text input
        	GuiTextField waypointName = new GuiTextField(0, this.fontRendererObj, waypointName_X, 0, width / 4, inputHeight);
            waypointName.setMaxStringLength(32);
            waypointName.setCanLoseFocus(true);
            waypointName.setText("Name");
            
            // color text input
            ColorPicker colorPicker = new ColorPicker(0, waypointColor_X, 0, inputHeight);
            colorPicker.setText("ffffff");
            
            // X coordinate input
            GuiTextField waypointX = new GuiTextField(2, this.fontRendererObj, coords_X, 0, coord_Width, inputHeight);
            waypointX.setMaxStringLength(16);
            waypointX.setCanLoseFocus(true);
            waypointX.setText(Float.toString(x));
            
            // Y coordinate input
            GuiTextField waypointY = new GuiTextField(2, this.fontRendererObj, coords_X + coord_Width + 5, 0, coord_Width, inputHeight);
            waypointY.setMaxStringLength(16);
            waypointY.setCanLoseFocus(true);
            waypointY.setText(Float.toString(y));

        	// Z coordinate input
            GuiTextField waypointZ = new GuiTextField(2, this.fontRendererObj, coords_X + ((coord_Width+5)*2), 0, coord_Width, inputHeight);
            waypointZ.setMaxStringLength(16);
            waypointZ.setCanLoseFocus(true);
            waypointZ.setText(Float.toString(z));
            
            GuiTextField yaw = new GuiTextField(2, this.fontRendererObj, coords_X + ((coord_Width+5)*3), 0, coord_Width, inputHeight);
            yaw.setMaxStringLength(16);
            yaw.setCanLoseFocus(true);
            yaw.setText(Float.toString(yawF));
            
            GuiTextField pitch = new GuiTextField(2, this.fontRendererObj, coords_X + ((coord_Width+5)*4), 0, coord_Width, inputHeight);
            pitch.setMaxStringLength(16);
            pitch.setCanLoseFocus(true);
            pitch.setText(Float.toString(pitchF));

            GuiTextField noiseLevel = new GuiTextField(2, this.fontRendererObj, coords_X + ((coord_Width+5)*5), 0, coord_Width, inputHeight);
            noiseLevel.setMaxStringLength(16);
            noiseLevel.setCanLoseFocus(true);
            noiseLevel.setText("1");
            
            GuiTextField function = new GuiTextField(-1, this.fontRendererObj, coords_X, 0 + inputHeight+5, width / 4, inputHeight);
            function.setMaxStringLength(16);
            function.setCanLoseFocus(true);
            function.setText("/");
            
            MacroButton leftClick = new MacroButton(0, waypointName_X, 0 + inputHeight+5, inputHeight, inputHeight, "", false);
            MacroButton rightClick = new MacroButton(1, waypointName_X + (inputHeight+5), 0 + inputHeight, inputHeight, inputHeight, "", false);
            MacroButton left = new MacroButton(2, waypointName_X + (inputHeight+5)*2, 0 + inputHeight, inputHeight, inputHeight, "", false);
            MacroButton right = new MacroButton(3, waypointName_X + (inputHeight+5)*3, 0 + inputHeight, inputHeight, inputHeight, "", false);
            MacroButton back = new MacroButton(4, waypointName_X + (inputHeight+5)*4, 0 + inputHeight, inputHeight, inputHeight, "", false);
            MacroButton forward = new MacroButton(5, waypointName_X + (inputHeight+5)*5, 0 + inputHeight, inputHeight, inputHeight, "", false);
            
            waypoints.add(0, new MacroWaypointElement(waypointName, colorPicker, waypointX, waypointY, waypointZ, yaw, pitch, left, right, back, forward, leftClick, rightClick, noiseLevel, function, deleteButton));
            return;
		}
		
        for (int i = 0; i < waypoints.size(); i++) {
			MacroWaypointElement element = waypoints.get(i);
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
			
			if(!closeWarning.showElement) {
				for (int i = 0; i < waypoints.size(); i++) {
					MacroWaypointElement waypoint = waypoints.get(i);
					List<GuiTextField> inputs = waypoint.getListOfTextFields();
					
					waypoints.get(i).color.keyTyped(typedChar, keyCode);
					
					for (GuiTextField input : inputs) {
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
								if(keyCode == Keyboard.KEY_BACK && input.getCursorPosition() == 1) return;
								if(keyCode != Keyboard.KEY_RIGHT && input.getCursorPosition() == 0) return;
								input.textboxKeyTyped(typedChar, keyCode);
								
								if(!input.getText().startsWith("/")) {
									input.setText("/" + input.getText());
								}
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
		
		if(!closeWarning.showElement) {
			
			boolean isOpenedColorPicker = false; // made it like that because mouseClick also hides picker so it wouldnt hide if i would make return
			for (int i = 0; i < waypoints.size(); i++) {
				if(waypoints.get(i).color.mouseClick(mouseX, mouseY, mouseButton)) isOpenedColorPicker = true;
			}

			if(isOpenedColorPicker) {
				for (int i = 0; i < waypoints.size(); i++) {
					for (GuiTextField input : waypoints.get(i).getListOfTextFields()) {
						input.setCursorPositionZero();
						input.setFocused(false);
					}
				}
			
				return;
			}
			
			// focusing input when clicked
			for (int i = 0; i < waypoints.size(); i++) {
				MacroWaypointElement waypoint = waypoints.get(i);
				
				if (mouseY <= ((height / 3))) {
	                 break;
	        	 }
				
				boolean isAnythingPressed = false;
				List<GuiTextField> inputs = waypoint.getListOfTextFields();
				
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
					for (GuiButton guibutton : waypoint.getListOfButtons()) {
			            if (guibutton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
			            {
			            	isAnythingPressed = true;
			                guibutton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
			                this.actionPerformed(guibutton);
			            }
					}
		        }
				
				if(waypoint.isHovered(mouseX, mouseY)) {
					if(!isAnythingPressed) {
						holdingElement = waypoint;
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
            holdingElement.updateYposition(mouseY, inputHeight);
		}
		
		for (int i = 0; i < waypoints.size(); i++) {
			waypoints.get(i).color.mouseClickMoved(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		}
    }
	
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
		if (holdingElement != null) {
            
			for (int i = 0; i < waypoints.size(); i++) {
				MacroWaypointElement element = waypoints.get(i);
				
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
		
		for (int i = 0; i < waypoints.size(); i++) {
			waypoints.get(i).color.mouseReleased(mouseX, mouseY);
		}
    }
	
	@Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        
        if (scroll != 0 && !closeWarning.showElement) {
        	scrollbar.handleMouseInput(scroll);
        }
    }
	
	@Override
	public void updateScreen() {
		for (int i = 0; i < waypoints.size(); i++) {
			MacroWaypointElement waypoint = waypoints.get(i);
			List<GuiTextField> inputs = waypoint.getListOfTextFields();
			
			for (GuiTextField input : inputs) {
				input.updateCursorCounter();
			}
			
			waypoint.color.updateCursorCounter();
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private void SaveWaypoints() {
		Location.checkTabLocation();
		// its to prevent removing waypoints from other regions, so i just remove waypoints from my region and add them updated
		List<MacroWaypoint> waypointsList = MacroWaypoints.GetWaypointsWithoutLocation();
		boolean isError = false;
		
		for (int i = waypoints.size() - 1; i >= 0; i--) {
			MacroWaypointElement waypoint = waypoints.get(i);

	    	waypoint.color.setTextColor(14737632);
	    	waypoint.x.setTextColor(14737632);
	    	waypoint.y.setTextColor(14737632);
	    	waypoint.z.setTextColor(14737632);
	    	
	        String name = waypoint.name.getText();
	        String function = waypoint.function.getText();
	        String color = waypoint.color.getColorString().replace("#", ""); 
	        try {
	        	Color.decode("#" + color);
	        } catch (NumberFormatException e) {
	        	waypoint.color.setTextColor(11217193);
	            isError = true;
	        }
	        
	        float x = 0, y = 0, z = 0, noiseLevel = 0;
	        Float yaw = 0f, pitch = 0f;
	        try {
	            x = Float.parseFloat(waypoint.x.getText());
	        } catch (NumberFormatException e) { waypoint.x.setTextColor(11217193); isError = true; }
	        
	        try {
	            y = Float.parseFloat(waypoint.y.getText());
	        } catch (NumberFormatException e) { waypoint.y.setTextColor(11217193); isError = true; }
	        
	        try {
	        	z = Float.parseFloat(waypoint.z.getText()); 
	        } catch (NumberFormatException e) { waypoint.z.setTextColor(11217193); isError = true; }
	        
	        try {
	        	yaw = Float.parseFloat(waypoint.yaw.getText()); 
	        } catch (NumberFormatException e) { yaw = null; }
	        
	        try {
	        	pitch = Float.parseFloat(waypoint.pitch.getText()); 
	        } catch (NumberFormatException e) { pitch = null; }
	        
	        try {
	        	noiseLevel = Float.parseFloat(waypoint.noiseLevel.getText()); 
	        } catch (NumberFormatException e) { waypoint.noiseLevel.setTextColor(11217193); isError = true; }
	        
	        boolean leftClick = waypoint.leftClick.isFull;
	        boolean rightClick = waypoint.rightClick.isFull;
	        boolean left = waypoint.left.isFull;
	        boolean right = waypoint.right.isFull;
	        boolean back = waypoint.back.isFull;
	        boolean forward = waypoint.forward.isFull;
	        
	        if(isError) {
	        	saveButton.packedFGColour = 14258834;
	        	return; // skip
	        }
	        
	        waypointsList.add(0, new MacroWaypoint(new Waypoint(name, color, x, y, z, Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld)), yaw, pitch, left, right, back, forward, leftClick, rightClick, noiseLevel, function));
	    	
	        
	    }
	    saveButton.packedFGColour = 11131282;
	    MacroWaypoints.waypointsList = waypointsList;
	    MacroWaypoints.doneMacro = new ArrayList<>(waypointsList);
	    ConfigHandler.SaveMacroWaypoint(waypointsList);
	}
	
	private void CloseGui() {
		if(closeWarning.showElement == true) return;
		
		Location.checkTabLocation();
		// its to prevent removing waypoints from other regions, so i just remove waypoints from my region and add them updated
		List<MacroWaypoint> waypointsList = MacroWaypoints.GetLocationWaypoints();
		boolean isntEqual = false;
		
		if(waypoints.size() != waypointsList.size()) {
			 isntEqual = true;
		}else {
			for (int i = waypoints.size() - 1; i >= 0; i--) {
				MacroWaypointElement macroWaypoint = waypoints.get(i);
		    	
				String name = macroWaypoint.name.getText();
		        String color = macroWaypoint.color.getColorString();
		        String function = macroWaypoint.function.getText();
		        
		        boolean leftClick = macroWaypoint.leftClick.isFull;
		        boolean rightClick = macroWaypoint.rightClick.isFull;
		        boolean left = macroWaypoint.left.isFull;
		        boolean right = macroWaypoint.right.isFull;
		        boolean back = macroWaypoint.back.isFull;
		        boolean forward = macroWaypoint.forward.isFull;
		        
		        float x = 0, y = 0, z = 0, noiseLevel = 0f;
		        Float yaw = 0f, pitch = 0f;
		        try {
		        	Color.decode("#" + color);
		            x = Float.parseFloat(macroWaypoint.x.getText());
		            y = Float.parseFloat(macroWaypoint.y.getText());
		        	z = Float.parseFloat(macroWaypoint.z.getText()); 
		        	noiseLevel = Float.parseFloat(macroWaypoint.noiseLevel.getText());
		        } catch (NumberFormatException e) { isntEqual = true; break; }
	
		        try {
		        	yaw = Float.parseFloat(macroWaypoint.yaw.getText()); 
		        } catch (NumberFormatException e) { yaw = null; }
		        
		        try {
		        	pitch = Float.parseFloat(macroWaypoint.pitch.getText()); 
		        } catch (NumberFormatException e) { pitch = null; }
		        
		        if(Float.compare(waypointsList.get(i).waypoint.coords[0], x) != 0) { isntEqual = true; break; }
		        if(Float.compare(waypointsList.get(i).waypoint.coords[1], y) != 0) { isntEqual = true; break; }
		        if(Float.compare(waypointsList.get(i).waypoint.coords[2], z) != 0) { isntEqual = true; break; }
		        if(yaw != null && waypointsList.get(i).yaw != null) {
		        	if(Float.compare(waypointsList.get(i).yaw, yaw) != 0) { isntEqual = true; break; }
		        }else {
		        	if(waypointsList.get(i).yaw != yaw) { isntEqual = true; break; }
		        }
		        if(pitch != null && waypointsList.get(i).pitch != null) {
		        	if(Float.compare(waypointsList.get(i).pitch, pitch) != 0) { isntEqual = true; break; }
		        }else {
		        	if(waypointsList.get(i).pitch != pitch) { isntEqual = true; break; }
		        }
		        if(Float.compare(waypointsList.get(i).noiseLevel, noiseLevel) != 0) { isntEqual = true; break; }
		        if(!name.equals(waypointsList.get(i).waypoint.name)) { isntEqual = true; break; }
		        if(!color.equalsIgnoreCase(waypointsList.get(i).waypoint.color)) { isntEqual = true; break; }
		        if(!function.equals(waypointsList.get(i).function)) { isntEqual = true; break; }
		        if(leftClick != waypointsList.get(i).leftClick) { isntEqual = true; break; }
		        if(rightClick != waypointsList.get(i).rightClick) { isntEqual = true; break; }
		        if(left != waypointsList.get(i).left) { isntEqual = true; break; }
		        if(right != waypointsList.get(i).right) { isntEqual = true; break; }
		        if(back != waypointsList.get(i).back) { isntEqual = true; break; }
		        if(forward != waypointsList.get(i).forward) { isntEqual = true; break; }
			}
		}
		
		if(isntEqual) {
			closeWarning.changeElementActive(true);
		}else {
			Minecraft.getMinecraft().thePlayer.closeScreen();
		}
	}
	
	public void updateWaypointsY() {
		int waypointHeight = inputHeight*2 + 5;
		scrollbar.updateMaxBottomScroll(((waypoints.size() * (waypointHeight + inputMargin))) - (height - (height/3)));
		int ScrollOffset = scrollbar.getOffset();
		
		int positionY = (int) (height / 3 + ScrollOffset);
		
		for (int i = 0; i < waypoints.size(); i++) {
			if(waypoints.get(i) == holdingElement) continue;
			int inputFullPosition = positionY + ((waypointHeight + inputMargin) * i);
			waypoints.get(i).updateYposition(inputFullPosition, inputHeight);
		}
	}
	
	@Override
    public void onGuiClosed() {
		holdingElement = null;
		waypoints.clear();
	}

}
