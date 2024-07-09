package treemek.mesky.handlers.gui.settings;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.buttons.FoldableSettingButton;
import treemek.mesky.handlers.gui.elements.buttons.SettingButton;
import treemek.mesky.handlers.gui.elements.textFields.SettingTextField;

public class SubCategory extends GuiScreen{
	String name;;
	List<Object> list;
	public int subHeight = 0;
	public int y;

	public SubCategory(String name, List<Object> list) {
		this.name = name;
		this.list = list;
	}

	public void drawSubCategory(int y, int width, int height) {
		this.y = y;
		
		Minecraft mc = Minecraft.getMinecraft();
		int x = width/3;
		int newSubHeight = 0;
		
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) ((height/20) / defaultFontHeight) / 2;
		
		RenderHandler.drawText(name, x, y, scaleFactor, true, 0x505050);
		newSubHeight += scaleFactor;
		
		if(list.size() > 0) {
			int subEndY = (int)(y + defaultFontHeight * scaleFactor + 5);
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i) instanceof GuiButton) {
					GuiButton button = (GuiButton) list.get(i);
					
					button.drawButton(mc, x, subEndY);
					if(list.get(i) instanceof FoldableSettingButton) {
						subEndY += ((FoldableSettingButton)list.get(i)).foldHeight + 2;
					}else {
						subEndY += button.height + 2;
					}
				}
				
				if(list.get(i) instanceof GuiTextField) {
           		 SettingTextField input = (SettingTextField)this.list.get(i);
					
					input.drawTextField(x, subEndY);
					subEndY += input.height + 5;
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
	                GuiButton guibutton = (GuiButton)this.list.get(i);
	
	                if(guibutton instanceof FoldableSettingButton) {
	                	for (int j = 0; j < ((FoldableSettingButton)guibutton).hiddenObjects.size(); ++j)
	                    {
	                		if(((FoldableSettingButton)guibutton).hiddenObjects.get(j) instanceof GuiButton) {
		                		GuiButton guiButton = (GuiButton)((FoldableSettingButton)guibutton).hiddenObjects.get(j);
		                		
		                		if (guiButton.mousePressed(this.mc, mouseX, mouseY))
		                        {
		                            guiButton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
		                            SettingsGUI.buttonClicked(guiButton); // here is function to buttonClicked maybe for future searching XD
		                        }
	                		}
	                		
	                		if(((FoldableSettingButton)guibutton).hiddenObjects.get(j) instanceof GuiTextField) {
	                			SettingTextField input = (SettingTextField)((FoldableSettingButton)guibutton).hiddenObjects.get(j);
	                			
	                			if (input.isHovered(mouseX, mouseY)) {
	             					input.mouseClicked(mouseX, mouseY, mouseButton);
	             				}else {
	             					input.setSelectionPos(input.getSelectionEnd());
	             					input.setFocused(false);
	             				}
	                		}
	                    }
	                }
	                
	                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
	                {
	                    guibutton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
	                    SettingsGUI.buttonClicked(guibutton); // here is function to buttonClicked maybe for future searching XD
	                }
	            }
            	
            	if(list.get(i) instanceof GuiTextField) {
            		 SettingTextField input = (SettingTextField)this.list.get(i);
            		 
            		 if (input.isHovered(mouseX, mouseY)) {
     					input.mouseClicked(mouseX, mouseY, mouseButton);
     				}else {
     					input.setSelectionPos(input.getSelectionEnd());
     					input.setFocused(false);
     				}
            	}
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
	
	protected void keyTyped(char typedChar, int keyCode) {
		for (int i = 0; i < this.list.size(); ++i)
        {
			if(list.get(i) instanceof GuiTextField) {
				SettingTextField input = (SettingTextField)this.list.get(i);
				if(input.isFocused()) {
					input.keyTyped(typedChar, keyCode);
				}
			}
			
			if(list.get(i) instanceof FoldableSettingButton) {
				List<Object> foldableList = ((FoldableSettingButton)list.get(i)).hiddenObjects;
				for (int j = 0; j < foldableList.size(); ++j){
					if(foldableList.get(j) instanceof GuiTextField) {
						SettingTextField input = (SettingTextField)foldableList.get(j);
						
						if(input.isFocused()) {
							input.keyTyped(typedChar, keyCode);
						}
					}
                }
			}
        }
	}
	
}
