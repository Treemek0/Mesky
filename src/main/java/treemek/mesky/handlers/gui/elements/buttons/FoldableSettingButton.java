package treemek.mesky.handlers.gui.elements.buttons;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import scala.collection.parallel.ParIterableLike.Fold;
import treemek.mesky.Reference;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.SettingColorPicker;
import treemek.mesky.handlers.gui.elements.sliders.SettingSlider;
import treemek.mesky.handlers.gui.elements.textFields.SettingTextField;
import treemek.mesky.handlers.gui.settings.SettingsGUI;

public class FoldableSettingButton extends GuiButton{

	int color = 0x3e91b5;
	String buttonText;
	public Setting setting;
	public int foldHeight;
	public List<Object> hiddenObjects;
	boolean illegal = false;
	
	public FoldableSettingButton(int buttonId, int height, String buttonText, Setting ghostPickaxe, List<Object> hiddenObjects) {
		super(buttonId, 0, 0, height*2, height, buttonText);
		this.buttonText = buttonText;
		this.hiddenObjects = hiddenObjects;
		this.setting = ghostPickaxe;
		
		for (Object guiButton : hiddenObjects) {
			if(guiButton instanceof GuiButton) {
				((GuiButton)guiButton).height *= 0.9;
				((GuiButton)guiButton).width *= 0.9;
			}
			
			if(guiButton instanceof GuiTextField) {
				((GuiTextField)guiButton).height *= 0.9;
				((GuiTextField)guiButton).width *= 0.9;
			}
		}
	}
	
	public FoldableSettingButton(int buttonId, int height, String buttonText, Setting ghostPickaxe, List<Object> hiddenObjects, boolean illegal) {
		super(buttonId, 0, 0, height*2, height, buttonText);
		this.buttonText = buttonText;
		this.hiddenObjects = hiddenObjects;
		this.setting = ghostPickaxe;
		this.illegal = illegal;
		
		for (Object guiButton : hiddenObjects) {
			if(guiButton instanceof GuiButton) {
				((GuiButton)guiButton).height *= 0.9;
				((GuiButton)guiButton).width *= 0.9;
			}
			
			if(guiButton instanceof GuiTextField) {
				((GuiTextField)guiButton).height *= 0.9;
				((GuiTextField)guiButton).width *= 0.9;
			}
		}
	}
	
	ResourceLocation off = new ResourceLocation(Reference.MODID, "gui/off-switch.png");
	ResourceLocation on = new ResourceLocation(Reference.MODID, "gui/on-switch.png");
	
	ResourceLocation warning = new ResourceLocation(Reference.MODID, "gui/warning.png");
	
	public boolean isFull() {
		return setting.isOn;
	}
	
	public void changeFull() {
         setting.isOn = !setting.isOn;
	}
	
	@Override
	public void drawButton(Minecraft mc, int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
		
		if(isFull()) {
			mc.renderEngine.bindTexture(on);
		}else {
			mc.renderEngine.bindTexture(off);
		}
		
		ScaledResolution resolution = new ScaledResolution(mc);
		
		drawModalRectWithCustomSizedTexture(xPosition, yPosition + height/4, 0, 0, width/2, height/2, width/2, height/2);
		
		if(illegal) {
			mc.renderEngine.bindTexture(warning);
			drawModalRectWithCustomSizedTexture((int) (xPosition - height*0.75f), yPosition + height/4, 0, 0, height/2, height/2, height/2, height/2);
		}
		
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) (height / defaultFontHeight) / 2;
		
		float textY = y + ((height / 2) - ((defaultFontHeight * scaleFactor) / 2));
		
		String text = RenderHandler.trimWordsToWidth(buttonText, (int) (resolution.getScaledWidth()*0.9f - x - (width*1.30) - 10), false, scaleFactor);
		
		foldHeight = height;
		
		if(text.equals(buttonText)) {
			RenderHandler.drawText(buttonText, x + width/2 + 5, textY, scaleFactor, true, color);
		}else {
			String oldText = buttonText;
			int newHeight = foldHeight;
			while (!text.equals(oldText)) {
				RenderHandler.drawText(text, x + width/2 + 5, textY, scaleFactor, true, color);
				oldText = oldText.substring(text.length());
				text = RenderHandler.trimWordsToWidth(oldText, (int) (resolution.getScaledWidth()*0.9f - x - (width*1.30) - 10), false, scaleFactor);
				
				if(text.split(" ").length == 1 || text.length() == 0) {
					String newText = RenderHandler.trimStringToWidth(oldText, (int) (resolution.getScaledWidth()*0.9f - x - (width*1.30) - 10), false, scaleFactor);
					
					if(newText.length() > 0) {
						text = newText;
					}else {
						break;
					}
				}
				
				textY += height + 1;
				newHeight += height + 1;
			}

			RenderHandler.drawText(text, x + width/2 + 5, textY, scaleFactor, true, color);
			
			foldHeight = newHeight;
		}
		
		if(isFull()) {
			int xOffset = width/2;
			
			for (int i = 0; i < hiddenObjects.size(); i++) {
				if(hiddenObjects.get(i) instanceof GuiButton) {
					GuiButton button = (GuiButton) hiddenObjects.get(i);
					button.drawButton(mc, xPosition + xOffset, yPosition + foldHeight + 2);
					
					if(hiddenObjects.get(i) instanceof FoldableSettingButton) {
						foldHeight += ((FoldableSettingButton)hiddenObjects.get(i)).foldHeight + 2;
					}
					if(hiddenObjects.get(i) instanceof SettingButton) {
						foldHeight += ((SettingButton)hiddenObjects.get(i)).allHeight + 2;
					}
					if(hiddenObjects.get(i) instanceof SettingSlider) {
						foldHeight += ((SettingSlider)hiddenObjects.get(i)).allHeight + 2;
					}
				}
				if(hiddenObjects.get(i) instanceof GuiTextField) {
					if(hiddenObjects.get(i) instanceof SettingTextField) {
						SettingTextField input = (SettingTextField) hiddenObjects.get(i);
						input.drawTextField(xPosition + xOffset, yPosition + foldHeight + 2);
						foldHeight += input.height + 6;
					}
					
					if(hiddenObjects.get(i) instanceof SettingColorPicker) {
						SettingColorPicker input = (SettingColorPicker) hiddenObjects.get(i);
						input.drawTextField(xPosition + xOffset, yPosition + foldHeight + 2);
						foldHeight += input.height + 2;
					}
				}
			}
		}
	}

	public void drawElementOnTop() {
		for (Object object : hiddenObjects) {
			if(object instanceof SettingColorPicker) {
				((SettingColorPicker)object).drawPickerOpened(); // because it has to be on top and for some reason it didnt worked like everything else
			}
			
			if(object instanceof FoldableSettingButton) {
				((FoldableSettingButton)object).drawElementOnTop();; // because it has to be on top and for some reason it didnt worked like everything else
			}
		}
	}
	
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		Minecraft mc = Minecraft.getMinecraft();
		
		if (mouseX >= this.xPosition && mouseX <= this.xPosition + width && mouseY >= this.yPosition && mouseY <= this.yPosition + height) {
			changeFull();
		}
		
		for (int j = 0; j < hiddenObjects.size(); ++j)
        {
    		if(hiddenObjects.get(j) instanceof GuiButton) {
    			if(hiddenObjects.get(j) instanceof FoldableSettingButton) {
            		FoldableSettingButton guiButton = (FoldableSettingButton)hiddenObjects.get(j);
            		
            		if (guiButton.mouseClicked(mouseX, mouseY, mouseButton))
                    {
                        guiButton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
                        SettingsGUI.buttonClicked(guiButton); // here is function to buttonClicked maybe for future searching XD
                    }
        		}else {
	        		GuiButton guiButton = (GuiButton)hiddenObjects.get(j);
	        		
	        		if(hiddenObjects.get(j) instanceof SettingSlider) {
	                	((SettingSlider)hiddenObjects.get(j)).mouseClicked(mouseX, mouseY, mouseButton);
	                }else {
		                if (guiButton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
		                {
		                    guiButton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
		                    SettingsGUI.buttonClicked(guiButton); // here is function to buttonClicked maybe for future searching XD
		                }
	                }
        		}
    		}
    		
    		if(hiddenObjects.get(j) instanceof GuiTextField) {
    			if(hiddenObjects.get(j) instanceof SettingTextField) {
	    			SettingTextField input = (SettingTextField)hiddenObjects.get(j);
	    			
	    			if (input.isHovered(mouseX, mouseY)) {
	 					input.mouseClicked(mouseX, mouseY, mouseButton);
	 				}else {
	 					input.setCursorPositionZero();
	 					input.setFocused(false);
	 				}
    			}
    			
    			if(hiddenObjects.get(j) instanceof SettingColorPicker) {
    				SettingColorPicker input = (SettingColorPicker)hiddenObjects.get(j);
 					input.mouseClick(mouseX, mouseY, mouseButton);
    			}
    		}
        }
		
		return super.mousePressed(mc, mouseX, mouseY);
	}
	
	
	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		List<Object> foldableList = hiddenObjects;
		for (int j = 0; j < foldableList.size(); ++j){
			if(foldableList.get(j) instanceof GuiButton) {
				((GuiButton)foldableList.get(j)).mouseReleased(mouseX, mouseY);;
			}
			
			if(foldableList.get(j) instanceof SettingColorPicker) {
				((SettingColorPicker)foldableList.get(j)).mouseReleased(mouseX, mouseY);
			}
		}
	}
	
	@Override
	public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
		List<Object> foldableList = hiddenObjects;
		for (int j = 0; j < foldableList.size(); ++j){
			if(foldableList.get(j) instanceof SettingSlider) {
				((SettingSlider)foldableList.get(j)).mouseDragged(mc, mouseX, mouseY);
			}
			
			if(foldableList.get(j) instanceof FoldableSettingButton) {
				((FoldableSettingButton)foldableList.get(j)).mouseDragged(mc, mouseX, mouseY);
			}
			
			if(foldableList.get(j) instanceof SettingColorPicker) {
				((SettingColorPicker)foldableList.get(j)).mouseClickMoved(mouseX, mouseY);
			}
		}
	}
	
	public void keyTyped(char typedChar, int keyCode) {
		List<Object> foldableList = hiddenObjects;
		for (int j = 0; j < foldableList.size(); ++j){
			if(foldableList.get(j) instanceof GuiTextField) {
				if(foldableList.get(j) instanceof SettingTextField) {
					SettingTextField input = (SettingTextField)foldableList.get(j);
					
					if(input.isFocused()) {
						input.keyTyped(typedChar, keyCode);
					}
				}
				
				if(foldableList.get(j) instanceof SettingColorPicker) {
					SettingColorPicker input = (SettingColorPicker)foldableList.get(j);
					input.keyTyped(typedChar, keyCode);
				}
			}
			
			if(foldableList.get(j) instanceof FoldableSettingButton) {
				((FoldableSettingButton)foldableList.get(j)).keyTyped(typedChar, keyCode);
			}
			
			if(foldableList.get(j) instanceof SettingSlider) {
				((SettingSlider)foldableList.get(j)).keyTyped(typedChar, keyCode);
			}
        }
	}
	
}
