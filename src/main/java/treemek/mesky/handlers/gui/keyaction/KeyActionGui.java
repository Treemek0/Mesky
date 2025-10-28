package treemek.mesky.handlers.gui.keyaction;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.ButtonWithToolkit;
import treemek.mesky.handlers.gui.elements.ColorPicker;
import treemek.mesky.handlers.gui.elements.ScrollBar;
import treemek.mesky.handlers.gui.elements.buttons.AddButton;
import treemek.mesky.handlers.gui.elements.buttons.SaveButton;
import treemek.mesky.handlers.gui.elements.warnings.CloseWarning;
import treemek.mesky.utils.KeyActions;
import treemek.mesky.utils.KeyActions.KeyAction;
import treemek.mesky.utils.Keybind;
import treemek.mesky.utils.Utils;

public class KeyActionGui extends GuiScreen{
	
	static List<KeyActionElement> list = new ArrayList<>();
	
	AddButton addAction;
	SaveButton save;
	
	CloseWarning closeWarning;
	
	ScrollBar scrollbar;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawRect(0, height/4, width, height, new Color(33, 33, 33,255).getRGB());
		
		for (KeyActionElement keyActionElement : list) {
			keyActionElement.draw();
		}
		
		for (KeyActionElement keyActionElement : list) {
			if (keyActionElement.keybindBtn instanceof ButtonWithToolkit && ((ButtonWithToolkit) keyActionElement.keybindBtn).shouldShowTooltip()) {
		    	RenderHandler.drawToolkit(keyActionElement.keybindBtn, mouseX, mouseY);
		    }
		}
		
		drawRect(0, 0, width, height/4, new Color(33, 33, 33,255).getRGB());
		
		addAction.drawButton(mc, mouseX, mouseY);
		save.drawButton(mc, mouseX, mouseY);
		
		int startY = height/4;
		
		float scale = (float) ((height*0.1f) / mc.fontRendererObj.FONT_HEIGHT) / 2;
		
		RenderHandler.drawText("Key Actions", width/2 - RenderHandler.getTextWidth("Key Actions", scale)/2, height * 0.05f, scale, true, 0x3e91b5);
		
		double widthOfCheckTexts = width / 20;
        double checksScale = RenderHandler.getTextScale("Players", widthOfCheckTexts); // "Players" is the longest in Alerts and i want all to have the same scale

        int elementWidth = width - (width/12)*2;
        int keybindWidth = Math.min(elementWidth/6, 110);
        
		int keybindX = width/12 + elementWidth/8 + 5;
        int commandX = keybindX + keybindWidth + 15;
		
        double textHeight = RenderHandler.getTextHeight(checksScale);
        RenderHandler.drawText("Keybind", keybindX, startY-textHeight - 4, checksScale, true, 0xffffff);
        RenderHandler.drawText("Command", commandX, startY-textHeight - 4, checksScale, true, 0xffffff);
		
		
		
		if(addAction.shouldShowTooltip()){
			RenderHandler.drawToolkit(addAction, mouseX, mouseY);
		}
		
		scrollbar.updateScrollBar((int)Math.min(20, (width * 0.025)), height - startY, width - 10, startY);
		if(scrollbar.isScrolling()) {
        	snapToYPositions();
        }else {
        	updateYPositions();
        }
		
		int contentSize = (int) (list.size()*(KeyActionElement.height*1.2f));
		scrollbar.updateVisibleHeight(height - height/4);
		scrollbar.updateContentHeight(contentSize);
		scrollbar.drawScrollBar();
		
		closeWarning.drawElement(mc, mouseX, mouseY);
	}
	
	@Override
	public void initGui() {
		int startY = height/4;
		
		scrollbar = new ScrollBar();
		
		closeWarning = new CloseWarning( () -> { return saveChanges(); } );
		
        float mainButtonsScale = (float) ((height*0.1f) / mc.fontRendererObj.FONT_HEIGHT) / 2;
        int mainButtonsSize = (int) RenderHandler.getTextHeight(mainButtonsScale);
        int mainButtonsY = (int) (height * 0.05f);
		
		addAction = new AddButton(-2, (int)(width * 0.1f) - mainButtonsSize/2, mainButtonsY, mainButtonsSize, mainButtonsSize, "New action");
		save = new SaveButton(-1, (int)(width * 0.9f) - mainButtonsSize/2, mainButtonsY, mainButtonsSize ,mainButtonsSize, "Save");
		
		List<KeyAction> actions = KeyActions.getActions();
		
		if(list.isEmpty()) {
			for (int i = 0; i < actions.size(); i++) {
				list.add(new KeyActionElement(startY, i, actions.get(i).keybind, actions.get(i).command));
			}
		}else {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).updateData(startY, i);
			}
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(addAction.mousePressed(mc, mouseX, mouseY)) {
			addAction.playPressSound(Minecraft.getMinecraft().getSoundHandler());
			int startY = height/4;
			list.add(0, new KeyActionElement(startY - KeyActionElement.height, 0, new Keybind(Keyboard.KEY_0), "/"));
		}
		
		closeWarning.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(save.mousePressed(mc, mouseX, mouseY)) {
			save.playPressSound(Minecraft.getMinecraft().getSoundHandler());
			save.packedFGColour = new Color(200, 255, 200).getRGB();
			saveChanges();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			KeyActionElement keyActionElement = (KeyActionElement) iterator.next();
			
			keyActionElement.mousePressed(mouseX, mouseY);
			if(keyActionElement.delete.mousePressed(mc, mouseX, mouseY)) {
				iterator.remove();
			}
		}
	}
	
	public void updateYPositions() {
		int offset = scrollbar.getOffset();
		
		int startY = height/4 + offset;
		
		for (int i = 0; i < list.size(); i++) {
			KeyActionElement action = list.get(i);
			
			action.smoothlyUpdateY(startY, i);;
		}
	}
	
	public void snapToYPositions() {
		int offset = scrollbar.getOffset();
		
		int startY = height/4 + offset;
		
		for (int i = 0; i < list.size(); i++) {
			list.get(i).updateData(startY, i);
		}
	}
	
	@Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        
        if (!closeWarning.showElement) {
        	scrollbar.handleMouseInput();
        	
        	if(Mouse.getEventDWheel() > 0) {
        		snapToYPositions();
        	}
        }
    }
	
	private boolean saveChanges() {
		List<KeyAction> newList = new ArrayList<>();
		
		for (KeyActionElement keyActionElement : list) {
			Keybind keybind = keyActionElement.keybindBtn.getKeybind();
			String command = keyActionElement.command.getText();
			
			newList.add(new KeyAction(keybind, command));
		}
		
		KeyActions.actions = newList;
		save.packedFGColour = 11131282;
		ConfigHandler.SaveKeyActions(KeyActions.actions);
		return true;
	}
	
	
	private boolean isChanged() {
		if(list.size() != KeyActions.actions.size()) return true;
		
		for (int i = 0; i < list.size(); i++) {
			KeyAction action = KeyActions.actions.get(i);
			KeyActionElement actionElement = list.get(i);
		
			if(!actionElement.keybindBtn.getKeybind().getKeysAsString().equals(action.keybind.getKeysAsString())) return true;
			if(!actionElement.command.getText().equals(action.command)) return true;
		}

		return false;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == 1) {
			ColorPicker.turnOff();
			CloseGui();
			return;
		}
		
		for (KeyActionElement keyActionElement : list) {
			keyActionElement.keyTyped(typedChar, keyCode);
		}
	}
	
	private void CloseGui() {
		if(closeWarning.showElement) {
			closeWarning.changeElementActive(false);
		}else if(isChanged()) {
			closeWarning.changeElementActive(true);
		}else {
			Minecraft.getMinecraft().thePlayer.closeScreen();	
		}
	}

	@Override
	public void updateScreen() {
		for (KeyActionElement keyActionElement : list) {
			keyActionElement.updateScreen();
		}
	}
	
	@Override
    public void onGuiClosed() {
		list.clear();
	}
}
