package treemek.mesky.handlers.gui.chatfunctions;

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
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Chat;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.alerts.AlertElement;
import treemek.mesky.handlers.gui.elements.ScrollBar;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.EditButton;
import treemek.mesky.handlers.gui.elements.buttons.MeskyButton;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.handlers.gui.elements.warnings.CloseWarning;
import treemek.mesky.handlers.gui.waypoints.WaypointElement;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.ChatFunctions;
import treemek.mesky.utils.ChatFunctions.ChatFunction;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Alerts.Alert;
import treemek.mesky.utils.Waypoints.Waypoint;

public class ChatFunctionsGui extends GuiScreen {
	
	List<ChatFunctionElement> chatFunctions = new ArrayList<>();
	
	CloseWarning closeWarning = new CloseWarning();
	ScrollBar scrollbar = new ScrollBar();

	int inputMargin;
	int inputHeight;
	private float scrollbar_startPosition;
	private float scrollbarBg_height;
	private float scrollbar_width;
	private GuiButton saveButton;


	private ChatFunctionElement holdingElement;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
		drawRect(0, height/3 - 1, width, height, new Color(33, 33, 33,255).getRGB());
		
		// Draw text fields first
		for (int i = 0; i < chatFunctions.size(); i++) {
			ChatFunctionElement chatFunction = chatFunctions.get(i);
			if(chatFunction == holdingElement) continue;
			
			if (chatFunction.yPosition + chatFunction.getHeight() <= ((height / 3))) {
                continue;
       	 	}
			
			for (TextField input : chatFunction.getListOfTextFields()) {
				input.drawTextBox();
			}
			
			// Reset color and blending state before drawing buttons
		    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		    GlStateManager.enableBlend();
		    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
			
		    for (GuiButton button : chatFunction.getListOfButtons()) {
				button.drawButton(mc, mouseX, mouseY);
			}
		    
			if(!chatFunction.enabled.isFull()) {
				drawRect(chatFunction.xPosition, chatFunction.yPosition-2, chatFunction.xPosition + chatFunction.getWidth(), chatFunction.yPosition + chatFunction.getHeight()+2, new Color(33, 33, 33, 180).getRGB());
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
			
			if(!holdingElement.enabled.isFull()) {
				drawRect(holdingElement.xPosition, holdingElement.yPosition-2, holdingElement.xPosition + holdingElement.getWidth(), holdingElement.yPosition + holdingElement.getHeight()+2, new Color(28, 28, 28, 180).getRGB());
			}
	    }
	    
		drawRect(0, 0, width, height/3 - 1, new Color(33, 33, 33,255).getRGB());
		
        
		float scale = (float) ((height*0.1f) / mc.fontRendererObj.FONT_HEIGHT) / 2;

        int textLength = mc.fontRendererObj.getStringWidth("Chat Functions");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) (height * 0.05f);

        RenderHandler.drawText("Chat Functions", titleX, titleY, scale, true, 0x3e91b5);
        
    	int trigger_X = width / 5;
    	int display_X = trigger_X + (width / 3) + 10;
        
	   double widthOfCheckTexts = width / 20;
       
       double checksScale = RenderHandler.getTextScale("Players", widthOfCheckTexts); // "Players" is the longest and i want all to have the same scale

       int positionY = (int)((height / 3) - RenderHandler.getTextHeight(checksScale) - 3);
       
       RenderHandler.drawText("Only", (trigger_X * 0.1) + inputHeight/2 - RenderHandler.getTextWidth("Only", checksScale)/2, positionY - RenderHandler.getTextHeight(checksScale) - 2, checksScale, true, 0x7a7a7a);
       RenderHandler.drawText("Party", (trigger_X * 0.1) + inputHeight/2 - RenderHandler.getTextWidth("Party", checksScale)/2, positionY, checksScale, true, 0x7a7a7a);
       
       RenderHandler.drawText("Ignore", (trigger_X * 0.4) + inputHeight/2 - RenderHandler.getTextWidth("Ignore", checksScale)/2, positionY - RenderHandler.getTextHeight(checksScale) - 2, checksScale, true, 0x7a7a7a);
       RenderHandler.drawText("Players", (trigger_X * 0.4) + inputHeight/2 - RenderHandler.getTextWidth("Players", checksScale)/2, positionY, checksScale, true, 0x7a7a7a);
       
       RenderHandler.drawText("Equal", (trigger_X * 0.7) + inputHeight/2 - RenderHandler.getTextWidth("Equal", checksScale)/2, positionY, checksScale, true, 0x7a7a7a);
       
        RenderHandler.drawText("Trigger", trigger_X, positionY, checksScale, true, 0x7a7a7a);
        RenderHandler.drawText("Function", display_X, positionY, checksScale, true, 0x7a7a7a);
        
        
        scrollbar.updateScrollBar((int)Math.min(20, (width * 0.025)), (height - (height / 3) - 10), (int)(width * 0.95), height/3);
        if(scrollbar.isScrolling()) {
        	snapToChatFunctionsY();
        }else {
        	updateChatFunctionsY();
        }
        scrollbar.drawScrollBar();
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	    
	    closeWarning.drawElement(mc, mouseX, mouseY);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    closeWarning = new CloseWarning();
	    buttonList.clear();
	    
	    inputHeight = ((height / 25) < 12)?12:(height / 25);
		inputMargin = ((height / 40) < 5)?5:(height / 40);
	    
        int positionY = height / 3;
        int buttonWidth = 20;
        int buttonHeight = 20;
        int trigger_X = width / 5;
    	int function_X = trigger_X + (width / 3) + 10;	
    	
        // Save button
    	saveButton = new MeskyButton(-1, (int)(width * 0.8f), (height/15), (int)(width * 0.2f), 20, "Save");
        this.buttonList.add(saveButton);
        
        // New function button
        this.buttonList.add(new MeskyButton(-2, 0, (height/15), (int)(width * 0.2f), 20, "New function"));
        
        // This is so you cant scroll limitless, it takes every waypoint height with their margin and removes visible inputs height so you can scroll max to how much of inputs isnt visible
  		scrollbar.updateVisibleHeight(height - (height/3));
		scrollbar.updateContentHeight((ChatFunctions.chatFunctionsList.size() * (inputHeight + inputMargin)));
		
  		int ScrollOffset = scrollbar.getOffset();
      		
        if(chatFunctions.isEmpty()) {
	        for (int i = 0; i < ChatFunctions.chatFunctionsList.size(); i++) {
	        	// Position 0 for inputs + every input height and their bottom margin
	        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
	        	
	        	
	        	// Delete function button
	            DeleteButton deleteButton = new DeleteButton(0, (int)(function_X + (width/4) + 10), inputFullPosition, inputHeight, inputHeight, "");
	            deleteButton.enabled = ChatFunctions.chatFunctionsList.get(i).isEnabled();
	            
	            CheckButton enabled = new CheckButton(0, (int)(function_X + (width/4) + inputHeight + 20), inputFullPosition, inputHeight, inputHeight, "", ChatFunctions.chatFunctionsList.get(i).isEnabled());            
	            
	        	// Only party messages check button
	            CheckButton onlyParty = new CheckButton(1, (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", ChatFunctions.chatFunctionsList.get(i).getOnlyParty());
	        	
	        	// Ignore players check button
	            CheckButton ignorePlayers = new CheckButton(2, (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", ChatFunctions.chatFunctionsList.get(i).getIgnorePlayers());
	        	
	        	// If message must be equal check button
	            CheckButton isEqual = new CheckButton(3, (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", ChatFunctions.chatFunctionsList.get(i).getIsEqual());
	        	
	        	// trigger text input
	        	TextField trigger = new TextField(1, trigger_X, inputFullPosition, width / 3, inputHeight);
	            trigger.setMaxStringLength(128);
	            trigger.setCanLoseFocus(true);
	            trigger.setText(ChatFunctions.chatFunctionsList.get(i).getTrigger());
	            
	            // function text input
	            TextField function = new TextField(2, function_X, inputFullPosition, width / 4, inputHeight);
	            function.setMaxStringLength(128);
	            function.setCanLoseFocus(true);
	            function.setText(ChatFunctions.chatFunctionsList.get(i).getFunction());
	
	            chatFunctions.add(new ChatFunctionElement(trigger, function, deleteButton, onlyParty, ignorePlayers, isEqual, enabled, inputMargin));
	        }
        }else {
        	for (int i = 0; i < chatFunctions.size(); i++) {
	        	// Position 0 for inputs + every input height and their bottom margin
	        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
	        	
	        	
	        	
	        	// Delete function button
	            DeleteButton deleteButton = new DeleteButton(0, (int)(function_X + (width/4) + 10), inputFullPosition, inputHeight, inputHeight, "");
	            deleteButton.enabled = chatFunctions.get(i).enabled.isFull();
	            
	            CheckButton enabled = new CheckButton(0, (int)(function_X + (width/4) + inputHeight + 20), inputFullPosition, inputHeight, inputHeight, "", chatFunctions.get(i).enabled.isFull());
	            
	        	// Only party messages check button
	            CheckButton onlyParty = new CheckButton(1, (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", chatFunctions.get(i).onlyParty.isFull());
	        	
	        	// Ignore players check button
	            CheckButton ignorePlayers = new CheckButton(2, (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", chatFunctions.get(i).ignorePlayers.isFull());
	        	
	        	// If message must be equal check button
	            CheckButton isEqual = new CheckButton(3, (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", chatFunctions.get(i).isEqual.isFull());
	        	
	        	// trigger text input
	        	TextField trigger = new TextField(1, trigger_X, inputFullPosition, width / 3, inputHeight);
	            trigger.setMaxStringLength(128);
	            trigger.setCanLoseFocus(true);
	            trigger.setText(chatFunctions.get(i).trigger.getText());
	            
	            // function text input
	            TextField function = new TextField(2, function_X, inputFullPosition, width / 4, inputHeight);
	            function.setMaxStringLength(128);
	            function.setCanLoseFocus(true);
	            function.setText(chatFunctions.get(i).function.getText());
	
	            chatFunctions.set(i, new ChatFunctionElement(trigger, function, deleteButton, onlyParty, ignorePlayers, isEqual, enabled, inputMargin));
	        }
        }
        
        snapToChatFunctionsY();
	}
	
	@Override
    protected void actionPerformed(GuiButton button) {
		if(button.id == -1) {
			// Save button
			SaveChatFunctions();
			return;
		}
		if(button.id == -2) {
			// New function button
			int trigger_X = width / 5;
	    	int function_X = trigger_X + (width / 3) + 10;	

	    	int topOfWaypoints = height/3 - inputHeight - inputMargin;
	    	
    		DeleteButton deleteButton = new DeleteButton(0, (int)(function_X + (width/4) + 10), 0, inputHeight, inputHeight, "");
    		
    		CheckButton enabled = new CheckButton(0, (int)(function_X + (width/4) + inputHeight + 20), 0, inputHeight, inputHeight, "", true);
    		
        	// Only party messages check button
            CheckButton onlyParty = new CheckButton(1, (int) (trigger_X * 0.1), 0, inputHeight, inputHeight, "", false);
        	
        	// Ignore players check button
            CheckButton ignorePlayers = new CheckButton(2, (int) (trigger_X * 0.4), 0, inputHeight, inputHeight, "", false);
        	
        	// If message must be equal check button
            CheckButton isEqual = new CheckButton(3, (int) (trigger_X * 0.7), 0, inputHeight, inputHeight, "", false);
        	
        	
        	// trigger text input
        	TextField trigger = new TextField(1, trigger_X, topOfWaypoints, width / 3, inputHeight);
            trigger.setMaxStringLength(128);
            trigger.setCanLoseFocus(true);
            trigger.setText("");
            
            // function text input
            TextField function = new TextField(2, function_X, 0, width / 4, inputHeight);
            function.setMaxStringLength(128);
            function.setCanLoseFocus(true);
            function.setText("/");
            
           chatFunctions.add(0, new ChatFunctionElement(trigger, function, deleteButton, onlyParty, ignorePlayers, isEqual, enabled, inputMargin));
		}
		
		// Every delete button
		for (int i = 0; i < chatFunctions.size(); i++) {
			ChatFunctionElement chatFunction = chatFunctions.get(i);
        	GuiButton deleteButton = chatFunction.deleteButton;
        	
			if(deleteButton == button) {
				chatFunctions.remove(i);
	            return;
			}
		}
    }
	
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == 1) {
			CloseGui();
			return;
		}
		
		if(!closeWarning.showElement) {
			// Every delete button
			for (int i = 0; i < chatFunctions.size(); i++) {
				ChatFunctionElement chatFunction = chatFunctions.get(i);
				for (TextField input : chatFunction.getListOfTextFields()) {
					if(input.isFocused()) {
						int cursor = input.getCursorPosition();
						
						// User can't type "/", because function must have only one slash
						if(keyCode == Keyboard.KEY_SLASH && !isShiftKeyDown()) return;
						input.textboxKeyTyped(typedChar, keyCode);
						
						// if function input doesn't have "/" then we add it
						if(input.getId() == 2) {
							if(!input.getText().contains("/")) {
								input.setText("/" + input.getText());
								input.setCursorPosition(cursor);
							}
						}
					}
				}
			}
		}
	}
	
	float offsetY = 0;
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(!closeWarning.showElement) {
			// focusing on input if clicked
			
			saveButton.packedFGColour = 14737632;
			
			for (int i = 0; i < chatFunctions.size(); i++) {
				ChatFunctionElement chatFunction = chatFunctions.get(i);
				
				if (mouseY <= ((height / 3))) {
					List<TextField> inputs = chatFunction.getListOfTextFields();
					for (TextField input : inputs) {
						input.setCursorPositionZero();
						input.setFocused(false);
					}
	                 break;
	        	 }
				
				boolean isAnythingPressed = false;
				
				if(chatFunction.enabled.isFull()) {
					for (TextField input : chatFunction.getListOfTextFields()) {
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
			            for (GuiButton guiButton : chatFunction.getListOfButtons()) {
			            	if (guiButton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY) && guiButton.enabled)
				            {
			            		isAnythingPressed = true;
				                guiButton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
				                this.actionPerformed(guiButton);
				            }
						}
			            
			            chatFunction.deleteButton.enabled = chatFunction.enabled.isFull();
			        }
				}else {
					if(chatFunction.enabled.mousePressed(mc, mouseX, mouseY)) {
	            		isAnythingPressed = true;
	            		chatFunction.enabled.playPressSound(Minecraft.getMinecraft().getSoundHandler());
		                this.actionPerformed(chatFunction.enabled);
		                chatFunction.deleteButton.enabled = chatFunction.enabled.isFull();
					};
				}
				
				
				if(chatFunction.isHovered(mouseX, mouseY)) {
					if(!isAnythingPressed) {
						holdingElement = chatFunction;
						offsetY = mouseY - chatFunction.yPosition;
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
			mouseY = (int) Math.min(Math.max(mouseY - offsetY, height/3 - 1), height - holdingElement.getHeight());
            holdingElement.updateYposition(mouseY);
		}
    }
	
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
		if (holdingElement != null) {
            
			for (int i = 0; i < chatFunctions.size(); i++) {
				ChatFunctionElement element = chatFunctions.get(i);
				
				if(holdingElement == element) continue;
				if(element.isHovered(mouseX, mouseY)) {
					int elementIndex = i;
					int holdingIndex = chatFunctions.indexOf(holdingElement);
					int sidePlus = (element.isHigherHalfHovered(mouseX, mouseY))?-1:0;

					chatFunctions.remove(holdingElement);
					chatFunctions.add(chatFunctions.indexOf(element)+1+sidePlus, holdingElement);
				}else if(mouseY > chatFunctions.get(chatFunctions.size()-1).yPosition + chatFunctions.get(chatFunctions.size()-1).elementHeight) {
					chatFunctions.remove(holdingElement);
					chatFunctions.add(chatFunctions.size(), holdingElement);
					holdingElement = null;
					break;
				}
			}
			holdingElement = null;
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
		for (int i = 0; i < chatFunctions.size(); i++) {
			ChatFunctionElement chatFunction = chatFunctions.get(i);
			for (TextField input : chatFunction.getListOfTextFields()) {
				input.updateCursorCounter();
			}
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private void SaveChatFunctions() {
		// Saving chat functions to file
		List<ChatFunction> chatFunctionsList = new ArrayList<ChatFunction>();
		// idk why i did it like this with chatFunctionFields.size(), but because of it we have to do "i +=2" since we have 2 input fields per ChatFunction
	    for (int i = 0; i < chatFunctions.size(); i ++) {
	    	ChatFunctionElement chatFunction = chatFunctions.get(i);
	    	
	        String trigger = chatFunction.trigger.getText();
	        String function = chatFunction.function.getText();
	        boolean onlyParty = chatFunction.onlyParty.isFull();
	        boolean ignorePlayers = chatFunction.ignorePlayers.isFull();
	        boolean isEqual = chatFunction.isEqual.isFull();
	        boolean enabled = chatFunction.enabled.isFull();
	        
	        chatFunctionsList.add(new ChatFunction(trigger, function, onlyParty, ignorePlayers, isEqual, enabled));
	    }
	    
	    saveButton.packedFGColour = 11131282;
	    ChatFunctions.chatFunctionsList = chatFunctionsList;
	    ConfigHandler.SaveChatFunction(chatFunctionsList);
	}
	
	private void CloseGui() {
		if(closeWarning.showElement == true) return;
		
		Location.checkTabLocation();
		// its to prevent removing waypoints from other regions, so i just remove waypoints from my region and add them updated
		List<ChatFunction> functionsList = ChatFunctions.chatFunctionsList;
		boolean isntEqual = false;
		
		if(chatFunctions.size() != functionsList.size()) {
			 isntEqual = true;
		}else {
			for (int i = chatFunctions.size() - 1; i >= 0; i--) {
				ChatFunctionElement chatFunction = chatFunctions.get(i);
		    	
		        String trigger = chatFunction.trigger.getText();
		        String function = chatFunction.function.getText(); 
	
		        if(!trigger.equals(functionsList.get(i).getTrigger())) { isntEqual = true; break; }
		        if(!function.equals(functionsList.get(i).getFunction())) { isntEqual = true; break; }
		        if(chatFunction.ignorePlayers.isFull() != functionsList.get(i).ignorePlayers) { isntEqual = true; break; }
		        if(chatFunction.isEqual.isFull() != functionsList.get(i).isEqual) { isntEqual = true; break; }
		        if(chatFunction.onlyParty.isFull() != functionsList.get(i).onlyParty) { isntEqual = true; break; }
		        if(chatFunction.enabled.isFull() != functionsList.get(i).enabled) { isntEqual = true; break; }
			}
		}
		
		if(isntEqual) {
			closeWarning.changeElementActive(true);
		}else {
			Minecraft.getMinecraft().thePlayer.closeScreen();
		}
	}
	
	public void updateChatFunctionsY() {
		scrollbar.updateContentHeight((chatFunctions.size() * (inputHeight + inputMargin)));
		int ScrollOffset = scrollbar.getOffset();
		
		int positionY = (int) (height / 3 + ScrollOffset);
		
		for (int i = 0; i < chatFunctions.size(); i++) {
			ChatFunctionElement element = chatFunctions.get(i);
			if(element == holdingElement) continue;
			int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
			
			double l = Math.signum((inputFullPosition - element.yPositionD)) * (240f/Minecraft.getDebugFPS());
			if (Math.abs(inputFullPosition - element.yPositionD) > element.getHeight() * 2) l *= 4;
			
			if(Math.abs(l) > Math.abs(element.yPositionD - inputFullPosition)) {
				element.updateYposition(inputFullPosition);
			}else {
				element.updateYposition(element.yPositionD + l);
			}
		}
	}
	
	public void snapToChatFunctionsY() {
		scrollbar.updateContentHeight((chatFunctions.size() * (inputHeight + inputMargin)));
		int ScrollOffset = scrollbar.getOffset();
		
		int positionY = (int) (height / 3 + ScrollOffset);
		
		for (int i = 0; i < chatFunctions.size(); i++) {
			if(chatFunctions.get(i) == holdingElement) continue;
			int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
			chatFunctions.get(i).updateYposition(inputFullPosition);
		}
	}
	
	@Override
    public void onGuiClosed() {
		holdingElement = null;
		chatFunctions.clear();
	}
}