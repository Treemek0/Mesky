package treemek.mesky.handlers.gui.settings;

import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.SettingColorPicker;
import treemek.mesky.handlers.gui.elements.buttons.FoldableSettingButton;
import treemek.mesky.handlers.gui.elements.buttons.SettingButton;
import treemek.mesky.handlers.gui.elements.sliders.SettingSlider;
import treemek.mesky.handlers.gui.elements.textFields.SettingTextField;

public class SubCategory extends GuiScreen{
	String name = null;
	List<Object> list;
	public int subHeight = 0;
	public int y;

	public SubCategory(String name, List<Object> list) {
		this.name = name;
		this.list = list;
	}

	public SubCategory(List<Object> list) {
		this.list = list;
	}
	
	public void drawSubCategory(int y, int width, int height) {
		this.y = y;
		
		Minecraft mc = Minecraft.getMinecraft();
		int x = width/3;
		int newSubHeight = 0;
		
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) ((height/20) / defaultFontHeight) / 2;
		
		if(name != null) {
			RenderHandler.drawText(name, x, y, scaleFactor, true, 0x505050);
			newSubHeight += (scaleFactor * defaultFontHeight) + 3;
		}
		
		if(list.size() > 0) {
			int subEndY = (int)(y + newSubHeight);
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i) instanceof GuiButton) {
					GuiButton button = (GuiButton) list.get(i);
					
					button.drawButton(mc, x, subEndY);
					if(list.get(i) instanceof FoldableSettingButton) {
						subEndY += ((FoldableSettingButton)list.get(i)).foldHeight + 2;
					}
					if(list.get(i) instanceof SettingButton) {
						subEndY += ((SettingButton)list.get(i)).allHeight + 2;
					}
					if(list.get(i) instanceof SettingSlider) {
						subEndY += ((SettingSlider)list.get(i)).allHeight + 2;
					}
				}
				
				if(list.get(i) instanceof GuiTextField) {
					if(list.get(i) instanceof SettingTextField) {
		           		 SettingTextField input = (SettingTextField)this.list.get(i);
							
						input.drawTextField(x, subEndY);
						subEndY += input.height + 6;
					}
					
					if(list.get(i) instanceof SettingColorPicker) {
		           		 SettingColorPicker input = (SettingColorPicker)this.list.get(i);
		           		 	GL11.glPushMatrix();
		                    GL11.glDisable(GL11.GL_DEPTH_TEST);
							input.drawTextField(x, subEndY);
							GL11.glPopMatrix();
							subEndY += input.height + 6;
						}
				}
			}
			
			newSubHeight += subEndY;
		}
		subHeight = newSubHeight - y;
	}
	
	
	@Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (mouseButton == 0)
        {
            for (int i = 0; i < this.list.size(); ++i)
            {
            	if(list.get(i) instanceof GuiButton) {
            		if(list.get(i) instanceof FoldableSettingButton) {
                		FoldableSettingButton guiButton = (FoldableSettingButton)list.get(i);
                		
                		if (guiButton.mouseClicked(mouseX, mouseY, mouseButton))
                        {
                            guiButton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
                            SettingsGUI.buttonClicked(guiButton); // here is function to buttonClicked maybe for future searching XD
                        }
            		}else {
            		
		                GuiButton guibutton = (GuiButton)this.list.get(i);
		                
		                if(this.list.get(i) instanceof SettingSlider) {
		                	((SettingSlider)this.list.get(i)).mouseClicked(mouseX, mouseY, mouseButton);
		                }else {
			                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
			                {
			                    guibutton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
			                    SettingsGUI.buttonClicked(guibutton); // here is function to buttonClicked maybe for future searching XD
			                }
		                }
            		}
	            }
            	
            	if(list.get(i) instanceof GuiTextField) {
            		if(list.get(i) instanceof SettingTextField) {
	            		 SettingTextField input = (SettingTextField)this.list.get(i);
	            		 
	            		 if (input.isHovered(mouseX, mouseY)) {
	     					input.mouseClicked(mouseX, mouseY, mouseButton);
	     				}else {
	     					input.setCursorPositionZero();
	     					input.setFocused(false);
	     				}
            		}
            		
            		if(list.get(i) instanceof SettingColorPicker) {
            			 SettingColorPicker input = (SettingColorPicker)this.list.get(i);
            			 input.mouseClick(mouseX, mouseY, mouseButton);
            		}
            	}
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
	
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		for (int i = 0; i < this.list.size(); ++i)
        {
			if(list.get(i) instanceof SettingSlider) { // SettingSlider not GuiButton is because GuiButton doesnt have that function and its custom made lol
				((SettingSlider)list.get(i)).mouseDragged(mc, mouseX, mouseY);
			}
			
			if(list.get(i) instanceof FoldableSettingButton) {
				((FoldableSettingButton)list.get(i)).mouseDragged(mc, mouseX, mouseY);
			}
			
			if(list.get(i) instanceof SettingColorPicker) {
				((SettingColorPicker)list.get(i)).mouseClickMoved(mouseX, mouseY);
			}
        }
	}
	
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
		for (int i = 0; i < this.list.size(); ++i)
        {
			if(list.get(i) instanceof GuiButton) {
				((GuiButton)list.get(i)).mouseReleased(mouseX, mouseY);
			}
			
			if(list.get(i) instanceof SettingColorPicker) {
				((SettingColorPicker)list.get(i)).mouseReleased(mouseX, mouseY);
			}
        }
    }
	
	protected void keyTyped(char typedChar, int keyCode) {
		for (int i = 0; i < this.list.size(); ++i)
        {
			if(list.get(i) instanceof SettingTextField) {
				SettingTextField input = (SettingTextField)this.list.get(i);
				if(input.isFocused()) {
					input.keyTyped(typedChar, keyCode);
				}
			}
			
			if(list.get(i) instanceof SettingColorPicker) {
       			SettingColorPicker input = (SettingColorPicker)this.list.get(i);
				input.keyTyped(typedChar, keyCode);
			}
			
			if(list.get(i) instanceof FoldableSettingButton) {
				((FoldableSettingButton)list.get(i)).keyTyped(typedChar, keyCode);
			}
			
			if(list.get(i) instanceof SettingSlider) {
				((SettingSlider)list.get(i)).keyTyped(typedChar, keyCode);
			}
        }
	}
	
}
