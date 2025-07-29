package treemek.mesky.handlers.gui.sounds;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.ScrollBar;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.FileSelectorButton;
import treemek.mesky.handlers.gui.elements.buttons.PlayButton;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.handlers.soundHandler.SoundsHandler;
import treemek.mesky.utils.Utils;

public class SoundGui extends GuiScreen{
	
	FileSelectorButton importSound;
	TextField field;
	
	String oldText = "";
	
	List<SoundElement> soundElements = new ArrayList<>();
	
	ScrollBar scrollbar = new ScrollBar();
	private TextField importField;
	private GuiButton importButton;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int inputHeight = Math.max(20, height / 25);
		
		RenderHandler.drawRect(0, inputHeight*2, width, height, new Color(33, 33, 33,255).getRGB());
		
		for (SoundElement element : soundElements) {
			RenderHandler.drawRect(element.field.xPosition - 5, element.field.yPosition - 3, element.delete.xPosition + element.delete.width + 5, element.field.yPosition + element.field.height + 3, 0x30000000);
			element.drawElement(mouseX, mouseY);
		}
		
		RenderHandler.drawRect(0, 0, width, inputHeight*2, new Color(33, 33, 33,255).getRGB());
		
		if(!field.getText().equals(oldText)) {
			SoundsHandler.importSoundToResources(new File(field.getText()));
			oldText = "";
			field.setText("");
			initGui();
		}
		
		importSound.drawButton(mc, mouseX, mouseY);
		
		scrollbar.updateScrollBar((int)(width*0.03f), height - inputHeight*2, (int)(width*0.95f), inputHeight*2);
		scrollbar.updateVisibleHeight(height - importSound.height*2);
		
		if(!soundElements.isEmpty()) {
			scrollbar.updateContentHeight(soundElements.get(soundElements.size()-1).field.yPosition + inputHeight);
		}else {
			scrollbar.updateVisibleHeight(0);
		}
		scrollbar.drawScrollBar();
		
		snapToY();
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
		soundElements.clear();
		
		int inputHeight = Math.max(20, height / 25);
		
		field = new TextField(0, 0, 0, 0, 0);
		field.setMaxStringLength(512);
		field.setText(oldText);
		
		importSound = new FileSelectorButton(0, 0, 0, inputHeight*5, inputHeight, field, "Import .ogg sound");
		importSound.setFilter(".ogg");
	
		int y = importSound.height * 2;
		int x = 1;
		
		SoundsHandler.reloadSounds();
		for (File sound : SoundsHandler.getAllImportedSounds()) {
			if(x + inputHeight*7 + 20 > width*0.89f) {
				y += inputHeight+6;
				x = 1;
			}
			
			TextField eField = new TextField(0, x, y, inputHeight*5, inputHeight);
			
			DeleteButton delete = new DeleteButton(0, x + inputHeight*6 + 20, y, inputHeight, inputHeight, "");
			
			PlayButton play = new PlayButton(0, x + inputHeight*5 + 10, y, inputHeight);
			
			soundElements.add(new SoundElement(play, delete, eField, sound));
			x+=inputHeight*8 + 20;
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		importSound.mousePressed(mc, mouseX, mouseY);
		
		Iterator<SoundElement> iterator = soundElements.iterator();
		boolean deleted = false;
		
		while (iterator.hasNext()) {
		    SoundElement soundElement = iterator.next();
			soundElement.field.mouseClicked(mouseX, mouseY, mouseButton);
			
			if(soundElement.play.mousePressed(mc, mouseX, mouseY)) {
				SoundsHandler.playSound(soundElement.field.getText());
			}
			
			if(soundElement.delete.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY) && soundElement.isDeletable) {
				if(SoundsHandler.deleteSound(soundElement.soundPath)){
					iterator.remove();
					deleted = true;
				}
			}
		}
		
		if(deleted) {
			initGui();
		}
	}
	
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		
		if(keyCode != 203 && keyCode != 205) {
			if(!GuiScreen.isKeyComboCtrlA(keyCode) && !GuiScreen.isKeyComboCtrlC(keyCode)) {
				return;
			}
		}

		for (SoundElement soundElement : soundElements) {
			soundElement.keyTyped(typedChar, keyCode);
		}
	}
	
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		
		scrollbar.handleMouseInput();
	}
	
	private void snapToY() {
		int offsetY = scrollbar.getOffset();
		
		int inputHeight = Math.max(20, height / 25);
		int y = importSound.height * 2 + offsetY;
		int x = 1;
		
		for (SoundElement element : soundElements) {
			if(x + inputHeight*7 + 20 > width*0.89f) {
				y += inputHeight+6;
				x = 1;
			}
			
			element.updateY(y);
			
			x+=inputHeight*8 + 20;
		}
	}
	
	@Override
	public void onGuiClosed() {
		SoundsHandler.reloadSounds();
		super.onGuiClosed();
	}
}
