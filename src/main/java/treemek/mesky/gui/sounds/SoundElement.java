package treemek.mesky.handlers.gui.sounds;

import java.io.File;

import net.minecraft.client.Minecraft;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.PlayButton;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.handlers.soundHandler.SoundsHandler;

public class SoundElement {
	File soundPath;
	DeleteButton delete;
	PlayButton play;
	TextField field;
	
	boolean isDeletable = true;
	
	public SoundElement(PlayButton play, DeleteButton delete, TextField field, File soundPath) {
		this.play = play;
		this.delete = delete;
		this.field = field;
		field.setText("mesky:"+ soundPath.getName().replace(".ogg", ""));
		
		if(field.getText().equals("mesky:alarm")) isDeletable = false;
		
		this.soundPath = soundPath;
	}
	
	public void drawElement(int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		
		field.drawTextBox();
		play.drawButton(mc, mouseX, mouseY);
		
		if(isDeletable) {
			delete.drawButton(mc, mouseX, mouseY);
		}
	}
	
	public void updateY(int y) {
		delete.yPosition = y;
		play.yPosition = y;
		field.yPosition = y;
	}
	
	public void updateFileName() {
		soundPath.renameTo(new File(field.getText().replace("mesky:", "")));
	}

	public void keyTyped(char typedChar, int keyCode) {
		field.textboxKeyTyped(typedChar, keyCode);
	}
}
