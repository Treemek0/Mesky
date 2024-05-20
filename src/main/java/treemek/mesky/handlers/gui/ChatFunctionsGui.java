package treemek.mesky.handlers.gui;

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
import treemek.mesky.handlers.gui.buttons.CheckButton;
import treemek.mesky.handlers.gui.buttons.DeleteButton;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.ChatFunctions;
import treemek.mesky.utils.ChatFunctions.ChatFunction;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.Waypoint;

public class ChatFunctionsGui extends GuiScreen {
	ArrayList<GuiTextField> chatFunctionFields;
	
	List<GuiButton> deleteButtonList = Lists.<GuiButton>newArrayList();
	List<GuiButton> checksButtonList = Lists.<GuiButton>newArrayList();
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
	private GuiButton saveButton;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
drawDefaultBackground();
        
		drawRect(0, height/3 - 1, width, height, new Color(33, 33, 33,255).getRGB());
		
		// Draw text fields first
	    for (GuiTextField input : chatFunctionFields) {
	        input.drawTextBox();
	    }

	    // Reset color and blending state before drawing buttons
	    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

	    // Draw delete buttons
	    for (GuiButton button : deleteButtonList) {
	        button.drawButton(mc, mouseX, mouseY);
	    }
	    
	    // Draw delete buttons
	    for (GuiButton button : checksButtonList) {
	        button.drawButton(mc, mouseX, mouseY);
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
        RenderHandler.drawText("Only", (trigger_X * 0.1), infoY - mc.fontRendererObj.FONT_HEIGHT*1.2, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Party", (trigger_X * 0.1), infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Ignore", (trigger_X * 0.4), infoY - mc.fontRendererObj.FONT_HEIGHT*1.2, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Players", (trigger_X * 0.4), infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Equal", (trigger_X * 0.7), infoY, 1, true, 0x7a7a7a);
        
        RenderHandler.drawText("Trigger", trigger_X, infoY, 1, true, 0x7a7a7a);
        RenderHandler.drawText("Function", display_X, infoY, 1, true, 0x7a7a7a);
        
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
	    
	    chatFunctionFields = new ArrayList<GuiTextField>();
	    deleteButtonList.clear();
	    checksButtonList.clear();
	    
	    inputHeight = ((height / 25) < 12)?12:(height / 25);
		inputMargin = ((height / 40) < 5)?5:(height / 40);
	    
		this.fontRendererObj.FONT_HEIGHT = (int)(height / 56.5);
	    
        int positionY = height / 3;
        int buttonWidth = 20;
        int buttonHeight = 20;
        int trigger_X = width / 5;
    	int function_X = trigger_X + (width / 3) + 10;	
    	
        // Save button
    	saveButton = new GuiButton(-1, (int)(width * 0.8f), (height/15), (int)(width * 0.2f), 20, "Save");
        this.buttonList.add(saveButton);
        
        // New function button
        this.buttonList.add(new GuiButton(-2, 0, (height/15), (int)(width * 0.2f), 20, "New function"));
        
        // This is so you cant scroll limitless, it takes every waypoint height with their margin and removes visible inputs height so you can scroll max to how much of inputs isnt visible
  		maxBottomScroll = Math.min(0, -(((ChatFunctions.chatFunctionsList.size() * (inputHeight + inputMargin))) - (height - (height/3))));
  		ScrollOffset = Math.max(ScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
      		
        
        for (int i = 0; i < ChatFunctions.chatFunctionsList.size(); i++) {
        	// Position 0 for inputs + every input height and their bottom margin
        	int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
        	
        	// Check if any part of the text field is within the visible area (stop rendering inputs that go over text)
            if (inputFullPosition <= ((height / 3) - 20)) {
                continue;
            }
        	
        	
        	// Delete function button
        	this.deleteButtonList.add(new DeleteButton(0 + (10*i), (int)(function_X + (width/3) + 10), inputFullPosition, inputHeight, inputHeight, ""));

        	// Ignore players check button
        	this.checksButtonList.add(new CheckButton(2 + (10*i), (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", ChatFunctions.chatFunctionsList.get(i).getOnlyParty()));
        	
        	// Only party messages check button
        	this.checksButtonList.add(new CheckButton(1 + (10*i), (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", ChatFunctions.chatFunctionsList.get(i).getIgnorePlayers()));
        	
        	// If message must be equal check button
        	this.checksButtonList.add(new CheckButton(3 + (10*i), (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", ChatFunctions.chatFunctionsList.get(i).getIsEqual()));
        	
        	// trigger text input
        	GuiTextField trigger = new GuiTextField(1, this.fontRendererObj, trigger_X, inputFullPosition, width / 3, inputHeight);
            trigger.setMaxStringLength(128);
            trigger.setCanLoseFocus(true);
            trigger.setText(ChatFunctions.chatFunctionsList.get(i).getTrigger());
            chatFunctionFields.add(trigger);
            
            // function text input
            GuiTextField function = new GuiTextField(2, this.fontRendererObj, function_X, inputFullPosition, width / 3, inputHeight);
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
			int trigger_X = width / 5;
	    	int function_X = trigger_X + (width / 3) + 10;	
			this.fontRendererObj.FONT_HEIGHT = (int)(height / 56.5);
			int i = (chatFunctionFields.size() / 2);
			int positionY = (int) (height / 3 + ScrollOffset);
			int inputFullPosition = positionY + ((inputHeight + inputMargin) * i);
        	
			// I did that instead of just initGui() so that it doesnt change back other inputs if theyre not saved
			maxBottomScroll = Math.min(0, -(((ChatFunctions.chatFunctionsList.size() * (inputHeight + inputMargin))) - (height - (height/3))));
    		ScrollOffset = Math.max(ScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
    		
    		DeleteButton deleteButton = new DeleteButton(0 + (10*i), (int)(function_X + (width/3) + 10), inputFullPosition, inputHeight, inputHeight, "");
        	deleteButtonList.add(deleteButton);
        	
        	this.checksButtonList.add(new CheckButton(1 + (10*i), (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", false));
        	
        	this.checksButtonList.add(new CheckButton(2 + (10*i), (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", false));

        	this.checksButtonList.add(new CheckButton(3 + (10*i), (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", false));
        	
        	// trigger text input
        	GuiTextField trigger = new GuiTextField(1, this.fontRendererObj, trigger_X, inputFullPosition, width / 3, inputHeight);
            trigger.setMaxStringLength(128);
            trigger.setCanLoseFocus(true);
            trigger.setText("");
            chatFunctionFields.add(trigger);
            
            // function text input
            GuiTextField function = new GuiTextField(2, this.fontRendererObj, function_X, inputFullPosition, width / 3, inputHeight);
            function.setMaxStringLength(128);
            function.setCanLoseFocus(true);
            function.setText("/");
            chatFunctionFields.add(function);
		}
		
		// Every delete button
        for (GuiButton guiButton : deleteButtonList) {
			if(guiButton.id == button.id) {
				// Removing alert from list
				int listId = button.id/10;
				Alerts.deleteAlert(listId);
	            chatFunctionFields.remove(listId*2); // removing trigger
	            chatFunctionFields.remove(listId*2); // removing display
	            checksButtonList.remove(listId*3);
	            checksButtonList.remove(listId*3);
	            checksButtonList.remove(listId*3);
	            deleteButtonList.remove(listId);
	            ScrollOffset += 0;
	            
	            // putting all inputs and buttons in place so theres no blank spot
	            maxBottomScroll = Math.min(0, -(((ChatFunctions.chatFunctionsList.size() * (inputHeight + inputMargin))) - (height - (height/3))));
	    		ScrollOffset = Math.max(ScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
	    		int positionY = (int) (height / 3 + ScrollOffset);
	    		List<GuiButton> oldChecksList = new ArrayList<>(checksButtonList);
	    		deleteButtonList.clear();
	    		checksButtonList.clear();
	    		
	    		for (int i = 0; i < chatFunctionFields.size(); i+=2) {
	    			int whichAlert = i/2;
	    			int inputFullPosition = positionY + ((inputHeight + inputMargin) * whichAlert);
	    			int trigger_X = width / 5;
	    	    	int function_X = trigger_X + (width / 3) + 10;	
	    			chatFunctionFields.get(i).yPosition = inputFullPosition;
	    			chatFunctionFields.get(i+1).yPosition = inputFullPosition;
	    			DeleteButton deleteButton = new DeleteButton(0 + (10*whichAlert), (int)(function_X + (width/3) + 10), inputFullPosition, inputHeight, inputHeight, "");
	            	deleteButtonList.add(deleteButton);
	            	
	            	// np. i=3 so wchichAlert = 1, id = 11, oldChecksList.get(3) [because there are 3 checks in one alerts, so first one in second alert is 3]
	            	checksButtonList.add(new CheckButton(1 + (10*whichAlert), (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", ((CheckButton)oldChecksList.get(whichAlert*3)).isFull()));
	            	
	            	checksButtonList.add(new CheckButton(2 + (10*whichAlert), (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", ((CheckButton)oldChecksList.get((whichAlert*3)+1)).isFull()));
				
	            	checksButtonList.add(new CheckButton(3 + (10*whichAlert), (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", ((CheckButton)oldChecksList.get((whichAlert*3)+2)).isFull()));
	    		}
	    		oldChecksList.clear();
	            return;
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
				if(input.getId() == 2) {
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
		
		// some shit from GuiScreen probably (just override)
		if (mouseButton == 0)
        {
            for (int i = 0; i < this.checksButtonList.size(); ++i)
            {
                GuiButton guibutton = (GuiButton)this.checksButtonList.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
                {
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.checksButtonList);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.button;
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                    if (this.equals(this.mc.currentScreen))
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.button, this.checksButtonList));
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
    		maxBottomScroll = Math.min(0, -(((ChatFunctions.chatFunctionsList.size() * (inputHeight + inputMargin))) - (height - (height/3))));
    		ScrollOffset = Math.max(ScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
            
    		int positionY = (int) (height / 3 + ScrollOffset);
    		List<GuiButton> oldChecksList = new ArrayList<>(checksButtonList);
    		deleteButtonList.clear();
    		checksButtonList.clear();
    		
    		for (int i = 0; i < chatFunctionFields.size(); i+=2) {
    			int whichAlert = i/2;
    			int inputFullPosition = positionY + ((inputHeight + inputMargin) * whichAlert);
    			int trigger_X = width / 5;
    	    	int function_X = trigger_X + (width / 3) + 10;	
    			chatFunctionFields.get(i).yPosition = inputFullPosition;
    			chatFunctionFields.get(i+1).yPosition = inputFullPosition;
    			DeleteButton deleteButton = new DeleteButton(0 + (10*whichAlert), (int)(function_X + (width/3) + 10), inputFullPosition, inputHeight, inputHeight, "");
            	deleteButtonList.add(deleteButton);
            	
            	checksButtonList.add(new CheckButton(1 + (10*whichAlert), (int) (trigger_X * 0.1), inputFullPosition, inputHeight, inputHeight, "", ((CheckButton)oldChecksList.get(whichAlert*3)).isFull()));
            	
            	checksButtonList.add(new CheckButton(2 + (10*whichAlert), (int) (trigger_X * 0.4), inputFullPosition, inputHeight, inputHeight, "", ((CheckButton)oldChecksList.get((whichAlert*3)+1)).isFull()));
			
            	checksButtonList.add(new CheckButton(3 + (10*whichAlert), (int) (trigger_X * 0.7), inputFullPosition, inputHeight, inputHeight, "", ((CheckButton)oldChecksList.get((whichAlert*3)+2)).isFull()));
    		}
    		oldChecksList.clear();
        }
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
	        boolean onlyParty = ((CheckButton)checksButtonList.get(i/2)).isFull();
	        boolean ignorePlayers = ((CheckButton)checksButtonList.get((i/2)+1)).isFull();
	        boolean isEqual = ((CheckButton)checksButtonList.get((i/2)+2)).isFull();
	        
	        chatFunctionsList.add(new ChatFunction(trigger, function, onlyParty, ignorePlayers, isEqual));
	    }
	    saveButton.packedFGColour = 11131282;
	    ChatFunctions.chatFunctionsList = chatFunctionsList;
	    ConfigHandler.SaveChatFunction(chatFunctionsList);
	}
}