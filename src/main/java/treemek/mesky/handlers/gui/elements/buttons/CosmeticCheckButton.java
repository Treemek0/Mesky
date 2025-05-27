package treemek.mesky.handlers.gui.elements.buttons;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.Utils;

public class CosmeticCheckButton extends GuiButton{
	public static class Skin {
		public ResourceLocation skin;
		public List<Integer> skinColors;
		
		public Skin(ResourceLocation skin, List<Integer> skinColors) {
			this.skin = skin;
			this.skinColors = skinColors;
		}
		
		public Skin(ResourceLocation skin, Integer skinColor) {
			this.skin = skin;
			this.skinColors =  new ArrayList<>(Arrays.asList(skinColor));
		}
	}
	
	String buttonText;
	int checkSize;
	public Setting setting;
	Map<Integer, Skin> skins = new HashMap<>();
	List<Setting> cosmeticsToTurnOff = new ArrayList<>();
	int id = 0;
	
	
	public CosmeticCheckButton(int buttonId, int x, int y, int width, int height, String buttonText, Setting setting, int cosmeticId, ResourceLocation image, int checkSize) {
		super(buttonId, x, y, width, height, buttonText);
		this.xPosition = x;
		this.yPosition = y;
		this.buttonText = buttonText;
		this.setting = setting;
		Map<Integer, Skin> skins = new HashMap<Integer, Skin>();
		skins.put(cosmeticId, new Skin(image, 0x000000));
		
		this.skins = skins;
		id = cosmeticId;
		this.checkSize = checkSize;
	}
	
	public CosmeticCheckButton(int buttonId, int x, int y, int width, int height, String buttonText, Setting setting, int defaultId, Map<Integer, Skin> skins, int checkSize) {
		super(buttonId, x, y, width, height, buttonText);
		this.xPosition = x;
		this.yPosition = y;
		this.buttonText = buttonText;
		this.setting = setting;
		this.checkSize = checkSize;
		this.skins = skins;
		id = !skins.containsKey(setting.number.intValue()) ? defaultId : setting.number.intValue();
	}
	
	
	ResourceLocation empty = new ResourceLocation(Reference.MODID, "gui/check-empty.png");
	ResourceLocation check = new ResourceLocation(Reference.MODID, "gui/check.png");
	
	public boolean isFull() {
		return skins.containsKey(setting.number.intValue());
	}
	
	public void changeFull() {
		if(isFull()) {
			id = setting.number.intValue();
			setting.number = 0D;
		}else {
			setting.number = (double) id;
			
			for (Setting sett : cosmeticsToTurnOff) {
				sett.number = 0D;
			}
		}
		
		ConfigHandler.saveSettings();
	}
	
	public void setCosmeticsToTurnOff(List<Setting> list) {
		cosmeticsToTurnOff = list;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        
        int left = xPosition;
        int top = yPosition;
		int right = xPosition + width;
        int bottom = yPosition + height;
        
        drawRect(left-1, top-1, right+1, bottom+1, new Color(1, 1, 1, 255).getRGB());
        
		drawRect(left, top, right, bottom, new Color(28, 28, 28, 255).getRGB());
		
		// Reset color and blending state before drawing buttons
	    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

		
		if(isFull()) {
			mc.renderEngine.bindTexture(check);
		}else {
			mc.renderEngine.bindTexture(empty);
		}
		// drawing button
		int checkX = xPosition + (width/2) - (checkSize/2);
		int checkY = (int)(yPosition + (height*0.1));
		drawModalRectWithCustomSizedTexture(checkX, checkY, 0, 0, checkSize, checkSize, checkSize, checkSize);
		
		
		// drawing image
		ResourceLocation image = skins.get(id).skin;
		mc.renderEngine.bindTexture(image);
		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
		
		int x = xPosition + width - (width/10) - 3;
		int y = yPosition + 2;
		int size = width/8;
		for (Map.Entry<Integer, Skin> skin : skins.entrySet()) {
			Integer i = skin.getKey();
			Skin s = skin.getValue();
			
			if(id == i) continue;
			
			mc.renderEngine.bindTexture(s.skin);
			//drawModalRectWithCustomSizedTexture(x, y, 0, 0, size, size, size, size);
			drawMultiColorRect(x, y, size, s.skinColors);
			y+= size + 3;
		}
	}
	
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean b = super.mousePressed(mc, mouseX, mouseY);
		
		if(b) {
			int x = xPosition + width - (width/10) - 3;
			int y = yPosition + 2;
			int size = width/8;
			for (Map.Entry<Integer, Skin> skin : skins.entrySet()) {
				Integer i = skin.getKey();
				if(id == i) continue;
				
				if(mouseX >= x && mouseY >= y && mouseX < x + size && mouseY < y + size) {
					if(isFull()) {
						id = i;
						setting.number = i.doubleValue();
					}else {
						id = i;
					}
					return true;
				}else {
					y += size + 3;
				}
			}
			
			changeFull();
		}
		return b;
	}
	
	
	private void drawMultiColorRect(int x, int y, int size, List<Integer> colors) {
	    int colorCount = colors.size();
	    
	    // Early exit for empty color list
	    if (colorCount == 0) return;
	    
	    RenderHandler.drawRect(x - 1, y - 1, x + size + 1, y + size + 1, 0xFF000000);
	    
	    // 2 colors: Split vertically
	    if (colorCount == 2) {
	        double sectionWidth = (double) size / colorCount;
	        for (int i = 0; i < colorCount; i++) {
	            int color = ColorUtils.ensureAlpha(colors.get(i));
	            RenderHandler.drawRect((int) (x + i * sectionWidth), y, (int) (x + (i + 1) * sectionWidth), y + size, color);
	        }
	    } 
	    
	    // 3 colors: 3 vertical sections, last section covering full height at the bottom
	    else if (colorCount == 3) {
	        double sectionWidth = (double) size / 2;
	        for (int i = 0; i < 2; i++) { // Draw first two vertically
	            int color = ColorUtils.ensureAlpha(colors.get(i));
	            RenderHandler.drawRect((int) (x + i * sectionWidth), y, (int) (x + (i + 1) * sectionWidth), y + size, color);
	        }
	        // Draw the third color at the bottom covering full width
	        int color = ColorUtils.ensureAlpha(colors.get(2));
	        RenderHandler.drawRect(x, (int) (y + size * 0.5), x + size, y + size, color);
	    } 
	    
	    // 4 colors: 2x2 grid (split both vertically and horizontally)
	    else if (colorCount == 4) {
	        double sectionWidth = (double) size / 2;
	        double sectionHeight = (double) size / 2;
	        for (int i = 0; i < 2; i++) {
	            for (int j = 0; j < 2; j++) {
	                int index = i * 2 + j;
	                int color = ColorUtils.ensureAlpha(colors.get(index));
	                RenderHandler.drawRect(
	                    (int) (x + j * sectionWidth), 
	                    (int) (y + i * sectionHeight), 
	                    (int) (x + (j + 1) * sectionWidth), 
	                    (int) (y + (i + 1) * sectionHeight), 
	                    color
	                );
	            }
	        }
	    } 
	    
	    // For 5+ colors, continue dividing logically
	    else {
	        double sectionWidth = (double) size / Math.ceil(Math.sqrt(colorCount));
	        double sectionHeight = (double) size / Math.ceil(Math.sqrt(colorCount));
	        
	        int currentX = x;
	        int currentY = y;
	        
	        for (int i = 0; i < colorCount; i++) {
	            int color = ColorUtils.ensureAlpha(colors.get(i));
	            
	            // Draw the section
	            RenderHandler.drawRect(currentX, currentY, (int) (currentX + sectionWidth), (int) (currentY + sectionHeight), color);
	            
	            // Move horizontally
	            currentX += sectionWidth;
	            
	            // If out of bounds horizontally, reset x and move vertically
	            if (currentX >= x + size) {
	                currentX = x;
	                currentY += sectionHeight;
	            }
	        }
	    }
	}
	
}
