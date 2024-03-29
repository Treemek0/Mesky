package treemek.mesky.handlers.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.StringUtils;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.buttons.CheckButton;
import treemek.mesky.handlers.gui.buttons.DeleteButton;
import treemek.mesky.utils.ChatFunctions;
import treemek.mesky.utils.ChatFunctions.ChatFunction;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.Waypoint;

public class ChatFunctionsGui extends GuiScreen {
	ArrayList<GuiTextField> chatFunctionFields;
	ArrayList<Boolean> chatFunctionOnlyParty;
	ArrayList<Boolean> chatFunctionIgnorePlayers;
	ArrayList<Boolean> chatFunctionIsEqual;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
        double scale = 3;
        int textLength = mc.fontRendererObj.getStringWidth("Chat Functions");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) ((height / 4) / scale);

        RenderHandler.drawText("Chat Functions", titleX, titleY, scale, true, 0x3e91b5);
        
    	int trigger_X = width / 5;
    	int display_X = trigger_X + (width / 3) + 10;
        
        int infoY = (int)((height / 3) - 15);
        RenderHandler.drawText("Ignore", (trigger_X * 0.1), infoY - mc.fontRendererObj.FONT_HEIGHT*1.2, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Players", (trigger_X * 0.1), infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Only", (trigger_X * 0.4), infoY - mc.fontRendererObj.FONT_HEIGHT*1.2, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Party", (trigger_X * 0.4), infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Equal", (trigger_X * 0.75), infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Trigger", trigger_X, infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Function", display_X, infoY, 1, true, 0x7a7a7a);
        
        
        for (GuiTextField input : chatFunctionFields) {
			input.drawTextBox();
		}
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    
	    chatFunctionFields = new ArrayList<GuiTextField>();
	    chatFunctionOnlyParty = new ArrayList<Boolean>();
	    chatFunctionIgnorePlayers = new ArrayList<Boolean>();
	    chatFunctionIsEqual = new ArrayList<Boolean>();
	    
        int positionY = height / 3;
        int buttonWidth = 20;
        int buttonHeight = 20;
        int alertTrigger_X = width / 5;
    	int alertDisplay_X = alertTrigger_X + (width / 3) + 10;	
    	
        
        // Save button
        this.buttonList.add(new GuiButton(-1, (int)(width * 0.8f), (height/15), (int)(width * 0.2f), 20, "Save"));
        
        // New function button
        this.buttonList.add(new GuiButton(-2, 0, (height/15), (int)(width * 0.2f), 20, "New function"));
        
        for (int i = 0; i < ChatFunctions.chatFunctionsList.size(); i++) {
        	// Delete function button
        	this.buttonList.add(new DeleteButton(0 + (4*i), (int)(alertDisplay_X + (width/3) + 10), positionY + (30 * i), 20, 20, ""));

        	// Ignore players check button
        	chatFunctionIgnorePlayers.add(ChatFunctions.chatFunctionsList.get(i).getIgnorePlayers());
        	this.buttonList.add(new CheckButton(2 + (4*i), (int) (alertTrigger_X * 0.1), positionY + (30*i), buttonWidth, buttonHeight, "", chatFunctionIgnorePlayers.get(i)));
        	
        	// Only party messages check button
        	chatFunctionOnlyParty.add(ChatFunctions.chatFunctionsList.get(i).getOnlyParty());
        	this.buttonList.add(new CheckButton(1 + (4*i), (int) (alertTrigger_X * 0.4), positionY + (30*i), buttonWidth, buttonHeight, "", chatFunctionOnlyParty.get(i)));
        	
        	// If message must be equal check button
        	chatFunctionIsEqual.add(ChatFunctions.chatFunctionsList.get(i).getIsEqual());
        	this.buttonList.add(new CheckButton(3 + (4*i), (int) (alertTrigger_X * 0.75), positionY + (30*i), buttonWidth, buttonHeight, "", chatFunctionIsEqual.get(i)));
        	
        	// trigger text input
        	GuiTextField trigger = new GuiTextField(1 + (4 * i), this.fontRendererObj, alertTrigger_X, positionY + (30 * i), width / 3, 20);
            trigger.setMaxStringLength(128);
            trigger.setCanLoseFocus(true);
            trigger.setText(ChatFunctions.chatFunctionsList.get(i).getTrigger());
            chatFunctionFields.add(trigger);
            
            // function text input
            GuiTextField function = new GuiTextField(2 + (4 * i), this.fontRendererObj, alertDisplay_X, positionY + (30 * i), width / 3, 20);
            function.setMaxStringLength(128);
            function.setCanLoseFocus(true);
            function.setText(ChatFunctions.chatFunctionsList.get(i).getFunction());
            chatFunctionFields.add(function);
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
			ChatFunctions.addChatFunction("", "/", true, false, false);
			// Clearing all lists because in initGui() it will add them again
			buttonList.clear();
			chatFunctionFields.clear();
            initGui();
            return;
		}
		
		// Every chat function buttons
		// Button have IDs, delete button have "0 + (4*i)" where "i" is chat function so first function have delete button with id0, second one have it with id4
		// thats why to recognize what button it is i used "button.id % 4 == 0" to check if it can be divided with 4, because other buttons have IDs like 1, 2, 3, 5, etc.
        for (GuiButton guiButton : buttonList) {
			if(guiButton.id == button.id) {
				if(button.id % 4 == 0) {
					// DELETE BUTTON
					int listId = (button.id > 0)?(button.id/4):0;
					ChatFunctions.deleteChatFunction(listId);
	
		            buttonList.clear();
		            chatFunctionFields.clear();
		            initGui();
		            return;
				}else if((button.id - 1) % 4 == 0) {
					// ONLYPARTY BUTTON
					int i = (int)((button.id - 1) / 4);
					chatFunctionOnlyParty.set(i, !chatFunctionOnlyParty.get(i));
				}else if((button.id - 2) % 4 == 0) {
					// IGNOREPLAYERS BUTTON
					int i = (int)((button.id - 2) / 4);
					chatFunctionIgnorePlayers.set(i, !chatFunctionIgnorePlayers.get(i));
				}else if((button.id - 3) % 4 == 0) {
					// ISEQUAL BUTTON
					int i = (int)((button.id - 3) / 4);
					chatFunctionIsEqual.set(i, !chatFunctionIsEqual.get(i));
				}
			}
		}
        
        
    }
	
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == 1) {
			// ESC
			Minecraft.getMinecraft().thePlayer.closeScreen();
			return;
		}
		
		for (GuiTextField input : chatFunctionFields) {
			if(input.isFocused()) {
				int cursor = input.getCursorPosition();
				
				// User can't type "/", because function must have only one slash
				if(keyCode == Keyboard.KEY_SLASH && !isShiftKeyDown()) return;
				input.textboxKeyTyped(typedChar, keyCode);
				
				// if function input doesn't have "/" then we add it
				if(input.getId() % 2 == 0) {
					if(!input.getText().contains("/")) {
						input.setText("/" + input.getText());
						input.setCursorPosition(cursor);
					}
				}
			}
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		// focusing on input if clicked
		for (GuiTextField input : chatFunctionFields) {
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
		for (GuiTextField input : chatFunctionFields) {
			input.updateCursorCounter();
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
	    for (int i = 0; i < chatFunctionFields.size(); i += 2) {
	        String trigger = chatFunctionFields.get(i).getText();
	        String function = chatFunctionFields.get(i+1).getText();
	        boolean onlyParty = chatFunctionOnlyParty.get(i/2);
	        boolean ignorePlayers = chatFunctionIgnorePlayers.get(i/2);
	        boolean isEqual = chatFunctionIsEqual.get(i/2);
	        
	        chatFunctionsList.add(new ChatFunction(trigger, function, onlyParty, ignorePlayers, isEqual));
	    }
	    ChatFunctions.chatFunctionsList = chatFunctionsList;
	    ConfigHandler.SaveChatFunction(chatFunctionsList);
	}
}
