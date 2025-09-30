package treemek.mesky.handlers.gui.settings;

import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.SettingColorPicker;
import treemek.mesky.handlers.gui.elements.buttons.FoldableSettingButton;
import treemek.mesky.handlers.gui.elements.buttons.SettingListBox;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Utils.smoothFunction;

public class Category extends Gui{
	public String name;
	public int y;
	public List<SubCategory> list;
	public int subBottomY = 0;
	
	public Category(String name, List<SubCategory> list) {
		this.name = name;
		this.list = list;
	}
	
	public smoothFunction animationForText = new smoothFunction(1, 1.02, 1, 0.05, false);
	public void drawCategory(int width, int height, int y, int contentOffset, boolean isOpened) {
		this.y = y;
		Minecraft mc = Minecraft.getMinecraft();
		animationForText.next();
		
		int x = (int)(width * 0.1f);
		int categoryHeight = (int) (height * 0.075f);
		int categoryWidth =  (int) (width * 0.2f);
		
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) ((float) (categoryHeight / defaultFontHeight) / 2 * animationForText.getCurrent());
		
		float scaledFontHeight = defaultFontHeight * scaleFactor;
		
		if(isOpened) {
			drawRect(x, y, x + categoryWidth, y + categoryHeight, 0x85151515);
			drawRect((int)(x-1*scaleFactor), y+1, x, y + categoryHeight-1, 0x85FFFFFF);
			RenderHandler.drawText(name, x + 6*scaleFactor, y + (categoryHeight/2) - scaledFontHeight/2, scaleFactor, true, 0xd9d9d9);
			drawCategorySubs(width, height, contentOffset);
		}else {
			drawRect(x, y, x + categoryWidth, y + categoryHeight, 0x15212121);
			RenderHandler.drawText(name, x + 4*scaleFactor, y + (categoryHeight/2) - scaledFontHeight/2, scaleFactor, true, 0xb3b3b3);
		}
	}
	
	
	private void drawCategorySubs(int width, int height, int offset) {
		int x = (int) (width * 0.1f);
		int categoryWidth =  (int) (width * 0.2f);
		
		drawRect(x + categoryWidth, (int)(height * 0.15f), (int)(width*0.9f), height, 0x85151515);
		
		int screenHeight = Display.getHeight(); // something with GL11.glViewport or someshit that it has different sizes
		int screenWidth = Display.getWidth();
		int topOfTheRectFromBottom = screenHeight - (int)(screenHeight * 0.15f);
		
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(0, 0, screenWidth, topOfTheRectFromBottom);
		for (int i = 0; i < list.size(); i++) {
			int y;
			if(i==0) {
				y = (int)(height * 0.20f) + offset;
			}else{
				y = list.get(i-1).y + list.get(i-1).subHeight + (height/30);
			}
			
			list.get(i).drawSubCategory(y, width, height);
		}
		
		for (int i = 0; i < list.size(); i++) { // drawing elements on top like this because Z level doesnt work for some reason 
			SubCategory sub = list.get(i);
			for (Object ob : sub.list) {
				if(ob instanceof SettingColorPicker) {
					((SettingColorPicker)ob).drawPickerOpened();
				}
				
				if(ob instanceof SettingListBox) {
					((SettingListBox)ob).drawOpenedList(); // because it has to be on top and for some reason it didnt worked like everything else
				}
				
				if(ob instanceof FoldableSettingButton) {
					((FoldableSettingButton)ob).drawElementOnTop(); 
				}
			}
		}
		
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	
}
