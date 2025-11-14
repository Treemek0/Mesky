package treemek.mesky.handlers.gui.images;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.GUI;
import treemek.mesky.handlers.gui.elements.buttons.CustomButton;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.utils.ImageUploader;

public class ImageGalleryElement {
	ResourceLocation image;
	int x = 0, y = 0;
	int size = 80;
	int frameWidth = 100;
	int frameHeight = 120;
	
	public TextField name;
	public CustomButton delete;
	
	public String format;
	
	// add delete button (the best would be X on the top right of left)
	
	public ImageGalleryElement(ResourceLocation image, int i, String name, String format) {
		this.image = image;
		this.format = format;
		this.name = new TextField(0,0,0, (int)(frameWidth * 0.9f), 18);
		this.name.setText(name);
		
		this.delete = new CustomButton(0, 0, 0, 6, 6, "", new ResourceLocation(Reference.MODID, "gui/x.png"));
		
		int startY = 50;
		setPosition(startY, i);
	}
	
	public void setPosition(int startY, int i) {
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		
		int itemsPerRow = sr.getScaledWidth() / (frameWidth + 1);
		int row = i / itemsPerRow;
		int col = i % itemsPerRow;

		x = 1 + (frameWidth + 1) * col;
		y = startY + (frameHeight + 1) * row;
		
		name.xPosition = (int) (x + frameWidth*0.05f);
		name.yPosition = y + 20 + size;
		
		delete.xPosition = x + frameWidth - 8;
		delete.yPosition = y + 2;
	}
	
	public void draw(int mouseX, int mouseY) {
		RenderHandler.drawRectWithFrame(x, y, x + frameWidth, y + frameHeight, 0xFF202020, 1);
		if(image == null) {
			image = ImageUploader.getResourceLocation(name.getText());
		}else {
			RenderHandler.drawImage(x + frameWidth/2 - size/2, y + 10, size, size, image);
		}
		
		name.drawTextBox();
		
		GlStateManager.enableBlend();
		if(!delete.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) GL11.glColor4f(1, 1, 1, 0.5f);
		delete.drawButton(Minecraft.getMinecraft(), x, y);
		GL11.glColor4f(1, 1, 1, 1);
	}
	
	public void textboxKeyTyped(char typedChar, int keyCode) {
		if(keyCode != Keyboard.KEY_LEFT && keyCode != Keyboard.KEY_RIGHT) {
			if(!GUI.isKeyComboCtrlA(keyCode) && !GUI.isKeyComboCtrlC(keyCode)) return;
		}
		
		name.textboxKeyTyped(typedChar, keyCode);
	}
	
	public boolean mouseClicked(int mouseX, int mouseY) {
		if(delete.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
			return true;
		}
		
		return false;
	}
}
