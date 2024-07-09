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
import treemek.mesky.handlers.gui.elements.CloseWarning;
import treemek.mesky.handlers.gui.elements.ScrollBar;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.EditButton;
import treemek.mesky.handlers.gui.elements.buttons.MeskyButton;
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
	ScrollBar scrollbar = new ScrollBar(0,0,0,0,0);

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
			for (GuiTextField input : chatFunction.getListOfTextFields()) {
				input.drawTextBox();
			}
	    }

	    // Reset color and blending state before drawing buttons
	    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

	    for (int i = 0; i < chatFunctions.size(); i++) {
			ChatFunctionElement chatFunction = chatFunctions.get(i);
			if(chatFunction == holdingElement) continue;
			
			for (GuiButton button : chatFunction.getListOfButtons()) {
				button.drawButton(mc, mouseX, mouseY);
			}
	    }
		
	    if(holdingElement != null) {
	    	
	    	drawRect(holdingElement.xPosition, holdingElement.yPosition, holdingElement.xPosition + holdingElement.getWidth(), holdingElement.yPosition + holdingElement.getHeight(), new Color(33, 33, 33,255).getRGB());
	    	
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
	    }
	    
		drawRect(0, 0, width, height/3 - 1, new Color(33, 33, 33,255).getRGB());
		
        
        double scale = 3;
        int textLength = mc.fontRendererObj.getStringWidth("Chat Functions");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) ((height / 4) / scale);

        RenderHandler.drawText("Chat Functions", titleX, titleY, scale, true, 0x3e91b5);
        
    	int trigger_X = width / 5;
    	int display_X = trigger_X + (width / 3) + 10;
        
        int infoY = (int)((height / 3) - 15);
        RenderHandler.drawText("Only", (trigger_X * 0.1), infoY - fontRendererObj.FONT_HEIGHT*2, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Party", (trigger_X * 0.1), infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Ignore", (trigger_X * 0.4), infoY - fontRendererObj.FONT_HEIGHT*2, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Players", (trigger_X * 0.4), infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Equal", (trigger_X * 0.7), infoY, 1, true, 0x7a7a7a);
        
        RenderHandler.drawText("Trigger", trigger_X, infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Function", display_X, infoY, 1, true, 0x7a7a7a);
        
        
        scrollbar.updateScrollBar((int)Math.min(20, (width * 0.025)), (height - (height / 3) - 10), (int)(width * 0.95), height/3);
        updateChatFunctionsY();
        scrollbar.renderScrollBar();
        
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
  		scrollbar.updateMaxBottomScroll(Math.min(0, -(((ChatFunctions.chatFunctionsList.size() * (inputHeight + inputMargin))) - (height - (height/3)))));
  		int ScrollOffset = scrollbar.getOffset();
      		
        if(chatFunctions.isEmpty()) {
	        for (int i = 0; i < ChatFunctions.chatFunctionsList.size(); i++) {
	        	// Position 0 for inputs + every input height and their bottom margin
	        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
	        	
	        	// Check if any part of the text field is within the visible area (stop rendering inputs that go over text)
	            if (inputFullPosition <= ((height / 3) - 20)) {
	                continue;
	            }
	        	
	        	
	        	// Delete function button
	            DeleteButton deleteButton = new DeleteButton(0, (int)(function_X + (width/3) + 10), inputFullPosition, inputHeight, inputHeight, "");
	
	        	// Only party messages check button
	            CheckButton onlyParty = new CheckButton(1, (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", ChatFunctions.chatFunctionsList.get(i).getOnlyParty());
	        	
	        	// Ignore players check button
	            CheckButton ignorePlayers = new CheckButton(2, (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", ChatFunctions.chatFunctionsList.get(i).getIgnorePlayers());
	        	
	        	// If message must be equal check button
	            CheckButton isEqual = new CheckButton(3, (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", ChatFunctions.chatFunctionsList.get(i).getIsEqual());
	        	
	        	// trigger text input
	        	GuiTextField trigger = new GuiTextField(1, this.fontRendererObj, trigger_X, inputFullPosition, width / 3, inputHeight);
	            trigger.setMaxStringLength(128);
	            trigger.setCanLoseFocus(true);
	            trigger.setText(ChatFunctions.chatFunctionsList.get(i).getTrigger());
	            
	            // function text input
	            GuiTextField function = new GuiTextField(2, this.fontRendererObj, function_X, inputFullPosition, width / 3, inputHeight);
	            function.setMaxStringLength(128);
	            function.setCanLoseFocus(true);
	            function.setText(ChatFunctions.chatFunctionsList.get(i).getFunction());
	
	            chatFunctions.add(new ChatFunctionElement(trigger, function, deleteButton, onlyParty, ignorePlayers, isEqual));
	        }
        }else {
        	for (int i = 0; i < chatFunctions.size(); i++) {
	        	// Position 0 for inputs + every input height and their bottom margin
	        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
	        	
	        	// Check if any part of the text field is within the visible area (stop rendering inputs that go over text)
	            if (inputFullPosition <= ((height / 3) - 20)) {
	                continue;
	            }
	        	
	        	
	        	// Delete function button
	            DeleteButton deleteButton = new DeleteButton(0, (int)(function_X + (width/3) + 10), inputFullPosition, inputHeight, inputHeight, "");
	
	        	// Only party messages check button
	            CheckButton onlyParty = new CheckButton(1, (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", chatFunctions.get(i).onlyParty.isFull);
	        	
	        	// Ignore players check button
	            CheckButton ignorePlayers = new CheckButton(2, (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", chatFunctions.get(i).ignorePlayers.isFull);
	        	
	        	// If message must be equal check button
	            CheckButton isEqual = new CheckButton(3, (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", chatFunctions.get(i).isEqual.isFull);
	        	
	        	// trigger text input
	        	GuiTextField trigger = new GuiTextField(1, this.fontRendererObj, trigger_X, inputFullPosition, width / 3, inputHeight);
	            trigger.setMaxStringLength(128);
	            trigger.setCanLoseFocus(true);
	            trigger.setText(chatFunctions.get(i).trigger.getText());
	            
	            // function text input
	            GuiTextField function = new GuiTextField(2, this.fontRendererObj, function_X, inputFullPosition, width / 3, inputHeight);
	            function.setMaxStringLength(128);
	            function.setCanLoseFocus(true);
	            function.setText(chatFunctions.get(i).function.getText());
	
	            chatFunctions.set(i, new ChatFunctionElement(trigger, function, deleteButton, onlyParty, ignorePlayers, isEqual));
	        }
        }
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

	  		
    		DeleteButton deleteButton = new DeleteButton(0, (int)(function_X + (width/3) + 10), 0, inputHeight, inputHeight, "");

        	// Only party messages check button
            CheckButton onlyParty = new CheckButton(1, (int) (trigger_X * 0.1), 0, inputHeight, inputHeight, "", false);
        	
        	// Ignore players check button
            CheckButton ignorePlayers = new CheckButton(2, (int) (trigger_X * 0.4), 0, inputHeight, inputHeight, "", false);
        	
        	// If message must be equal check button
            CheckButton isEqual = new CheckButton(3, (int) (trigger_X * 0.7), 0, inputHeight, inputHeight, "", false);
        	
        	
        	// trigger text input
        	GuiTextField trigger = new GuiTextField(1, this.fontRendererObj, trigger_X, 0, width / 3, inputHeight);
            trigger.setMaxStringLength(128);
            trigger.setCanLoseFocus(true);
            trigger.setText("");
            
            // function text input
            GuiTextField function = new GuiTextField(2, this.fontRendererObj, function_X, 0, width / 3, inputHeight);
            function.setMaxStringLength(128);
            function.setCanLoseFocus(true);
            function.setText("/");
            
           chatFunctions.add(new ChatFunctionElement(trigger, function, deleteButton, onlyParty, ignorePlayers, isEqual));
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
				for (GuiTextField input : chatFunction.getListOfTextFields()) {
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
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		closeWarning.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(!closeWarning.showElement) {
			// focusing on input if clicked
			for (int i = 0; i < chatFunctions.size(); i++) {
				ChatFunctionElement chatFunction = chatFunctions.get(i);
				boolean isAnythingPressed = false;
				for (GuiTextField input : chatFunction.getListOfTextFields()) {
					if (mouseX >= input.xPosition && mouseX <= input.xPosition + input.width && mouseY >= input.yPosition && mouseY <= input.yPosition + input.height) {
						input.mouseClicked(mouseX, mouseY, mouseButton);
						isAnythingPressed = true;
					}else {
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
		            
		        }
				
				if(chatFunction.isHovered(mouseX, mouseY)) {
					if(!isAnythingPressed) {
						holdingElement = chatFunction;
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
					
					chatFunctions.remove(holdingElement);
					if(elementIndex > holdingIndex) {
						chatFunctions.add(chatFunctions.indexOf(element)+1, holdingElement);
						break;
					}else {
						chatFunctions.add(chatFunctions.indexOf(element), holdingElement);
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
        int SCROLL_SPEED = height / 50;
        
        if (scroll != 0 && !closeWarning.showElement) {
        	scrollbar.handleMouseInput(scroll);
        }
    }
	
	@Override
	public void updateScreen() {
		for (int i = 0; i < chatFunctions.size(); i++) {
			ChatFunctionElement chatFunction = chatFunctions.get(i);
			for (GuiTextField input : chatFunction.getListOfTextFields()) {
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
	        
	        chatFunctionsList.add(new ChatFunction(trigger, function, onlyParty, ignorePlayers, isEqual));
	    }
	    saveButton.packedFGColour = 11131282;
	    ChatFunctions.chatFunctionsList = chatFunctionsList;
	    ConfigHandler.SaveChatFunction(chatFunctionsList);
	}
	
	private void CloseGui() {
		if(closeWarning.showElement == true) return;
		
		Location.checkTabLocation();
		// its to prevent removing waypoints from other regions, so i just remove waypoints from my region and add them updated
		List<ChatFunction> alertsList = ChatFunctions.chatFunctionsList;
		boolean isntEqual = false;
		
		if(chatFunctions.size() != alertsList.size()) {
			 isntEqual = true;
		}else {
			for (int i = chatFunctions.size() - 1; i >= 0; i--) {
				ChatFunctionElement chatFunction = chatFunctions.get(i);
		    	
		        String trigger = chatFunction.trigger.getText();
		        String function = chatFunction.function.getText(); 
	
		        if(!trigger.equals(alertsList.get(i).getTrigger())) { isntEqual = true; break; }
		        if(!function.equals(alertsList.get(i).getFunction())) { isntEqual = true; break; }
		        if(chatFunction.ignorePlayers.isFull != alertsList.get(i).ignorePlayers) { isntEqual = true; break; }
		        if(chatFunction.isEqual.isFull != alertsList.get(i).isEqual) { isntEqual = true; break; }
		        if(chatFunction.onlyParty.isFull != alertsList.get(i).onlyParty) { isntEqual = true; break; }
			}
		}
		
		if(isntEqual) {
			closeWarning.changeElementActive(true);
		}else {
			Minecraft.getMinecraft().thePlayer.closeScreen();
		}
	}
	
	public void updateChatFunctionsY() {
		scrollbar.updateMaxBottomScroll(Math.min(0, -(((chatFunctions.size() * (inputHeight + inputMargin))) - (height - (height/3)))));
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