package treemek.mesky.handlers.gui.settings;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import treemek.mesky.handlers.RenderHandler;

public class Category extends Gui{
	public String name;
	public int y;
	public List<SubCategory> list;
	public boolean isOpened;
	
	public Category(String name, List<SubCategory> list, boolean isOpened) {
		this.name = name;
		this.list = list;
		this.isOpened = isOpened;
	}
	
	public void drawCategory(int width, int height, int y) {
		this.y = y;
		Minecraft mc = Minecraft.getMinecraft();
		
		int x = (int)(width * 0.1f);
		int categoryHeight = (int) (height * 0.075f);
		int categoryWidth =  (int) (width * 0.2f);
		
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) (categoryHeight / defaultFontHeight) / 2;
		
		float scaledFontHeight = defaultFontHeight * scaleFactor;
		
		if(isOpened) {
			drawRect(x, y, x + categoryWidth, y + categoryHeight, 0x85151515);
			RenderHandler.drawText(name, x + 5, y + (categoryHeight/2) - scaledFontHeight/2, scaleFactor, true, 0xd9d9d9);
			drawCategorySubs(width, height);
		}else {
			drawRect(x, y, x + categoryWidth, y + categoryHeight, 0x15212121);
			RenderHandler.drawText(name, x + 5, y + (categoryHeight/2) - scaledFontHeight/2, scaleFactor, true, 0xb3b3b3);
		}
	}
	
	
	private void drawCategorySubs(int width, int height) {
		int x = (int) (width * 0.1f);
		int categoryWidth =  (int) (width * 0.2f);
		
		drawRect(x + categoryWidth, (int)(height * 0.15f), (int)(width*0.9f), height, 0x85151515);
		
		for (int i = 0; i < list.size(); i++) {
			int y;
			if(i==0) {
				y = (int)(height * 0.2f);
			}else{
				y = list.get(i-1).y + list.get(i-1).subHeight + (height/15);
			}
			
			list.get(i).drawSubCategory(y, width, height);
		}
	}
	
	
}
