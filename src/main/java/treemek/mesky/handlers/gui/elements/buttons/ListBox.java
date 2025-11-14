package treemek.mesky.handlers.gui.elements.buttons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.Waypoint;

public class ListBox extends GuiButton{
	public static class Option{
		private String visibleText;
		private String argument;
		public float currentTextPos = 0;
		public boolean reverseTextPos = false;
		public EnumChatFormatting textColor = EnumChatFormatting.RESET;
		
		public Option(String visibleText, String argument) {
			this.visibleText = visibleText;
			this.argument = argument;
		}
		
		public Option(String visibleText, String argument, EnumChatFormatting textColor) {
			this.visibleText = visibleText;
			this.argument = argument;
			this.textColor  = textColor;
		}
		
		public String getVisibleText() {
			return visibleText;
		}
		
		public String getArgument() {
			return argument;
		}
	}
	
	int currentOption = 0;
	List<Option> options;
	boolean opened = false;
	public int endY = 0;
	
	Option custom;
	TextField customField;
	
	ResourceLocation arrowRight = new ResourceLocation(Reference.MODID, "gui/arrow_right.png");
	ResourceLocation arrowDown = new ResourceLocation(Reference.MODID, "gui/arrow_down.png");
	
	public ListBox(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, List<Option> options, String current) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.options = options;
		
		Optional<Option> foundOption = options.stream().filter(option -> option.getArgument().equals(current)).findFirst();
		
		if(foundOption.isPresent()) {
			currentOption = options.indexOf(foundOption.get());
		}
	}
	
	public ListBox(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, List<Option> options, String customText, String current) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.options = options;
		this.custom = new Option(customText, customText);
		this.customField = new TextField(0, x+1, y+1, width-height, height-2);
		customField.setText(custom.getVisibleText());
		
		Optional<Option> foundOption = options.stream().filter(option -> option.getArgument().equals(current)).findFirst();
		
		if(foundOption.isPresent()) {
			currentOption = options.indexOf(foundOption.get());
		}else {
			setCustom(current);
			currentOption = options.size();
		}
	}
	
	public boolean isCurrentCustomOption() {
		return currentOption >= options.size();
	}
	
	public void setCustom(String text) {
		custom = new Option(text, text);
		if(!customField.getText().equals(text)) {
			customField.setText(text);
		}
	}
	
	public boolean isOpened() {
		return opened;
	}
	
	public void closeList() {
		opened = false;
	}
	
	public String getCurrentArgument() {
		if(isCurrentCustomOption()) {
			return custom.getArgument();
		}else {
			return options.get(currentOption).getArgument();
		}
	}
	
	
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if(customField != null) customField.yPosition = yPosition + 1;
		Option current;
		
		if(mouseX >= this.xPosition && mouseY >= yPosition && mouseX < this.xPosition + this.width && mouseY < yPosition + height) {
			GL11.glColor3f(0.6f, 0.6f, 0.6f);
			drawButtonForegroundLayer(xPosition, yPosition);
			GL11.glColor3f(1f, 1f, 1f);
		}else {
			drawButtonForegroundLayer(xPosition, yPosition);
		}
		
		double scale = RenderHandler.getTextScale(height/2.5);
		double textY = yPosition + height/2 - RenderHandler.getTextHeight(scale)/2;
		
		if(isCurrentCustomOption()) {
			current = custom;
			
			customField.drawTextBox();
		}else {
			current = options.get(currentOption);
			
			String reverseText = RenderHandler.trimStringToWidth(current.getVisibleText(), width - height - 4, true, scale);
			float textStartP = Math.max(0, current.currentTextPos);
			int maxTextPos = current.getVisibleText().length() - reverseText.length();
			if(textStartP > maxTextPos) textStartP = maxTextPos;
			
			String currentText = RenderHandler.trimStringToWidth(current.getVisibleText().substring((int) textStartP), (int) (width - height - 4), false, scale);
			RenderHandler.drawText(currentText, xPosition + 5, textY, scale, true, 0xFFFFFF);
			
			updateTextPos(current, maxTextPos);
		}

		if(opened){		
			int arrowSize = height/2;
			RenderHandler.drawImage((int)(xPosition + width - height/2 - arrowSize/2 - 1), yPosition + height/2 - arrowSize/2, arrowSize, arrowSize, arrowDown);
			
			int y = yPosition;
			
			for (Option option : options) {
				if(option == current) continue;
				
				y += height;
				textY += height;
				
				if(mouseX >= this.xPosition && mouseY >= y && mouseX < this.xPosition + this.width && mouseY < y + height) {
					GL11.glColor3f(0.1f, 0.1f, 0.1f);
				}else {
					GL11.glColor3f(0.3f, 0.3f, 0.3f);
				}
				
				drawButtonForegroundLayer(xPosition, y);
				GL11.glColor3f(1,1,1);
				
				String reverseText = RenderHandler.trimStringToWidth(option.getVisibleText(), width - 14, true, (float) scale);
				float textStartP = Math.max(0, option.currentTextPos);
				int maxTextPos = option.getVisibleText().length() - reverseText.length();
				if(textStartP > maxTextPos) textStartP = maxTextPos;
				
				String currentText = RenderHandler.trimStringToWidth(option.getVisibleText().substring((int) textStartP), width - 12, false, (float) scale);
				RenderHandler.drawText(currentText, xPosition + 5, textY, scale, true, 0xFFFFFF);
				
				updateTextPos(option, maxTextPos);
			}
			
			if(custom != null && !isCurrentCustomOption()) {
				
				y += height;
				textY += height;
				
				if(mouseX >= this.xPosition && mouseY >= y && mouseX < this.xPosition + this.width && mouseY < y + height) {
					GL11.glColor3f(0.2f, 0.2f, 0.2f);
				}else {
					GL11.glColor3f(0.4f, 0.4f, 0.4f);
				}
				
				drawButtonForegroundLayer(xPosition, y);
				GL11.glColor3f(1,1,1);
				
				String customText = (!custom.getVisibleText().isEmpty())? "\"" + custom.getVisibleText() + "\"" : "\" \"";
				
				String reverseText = RenderHandler.trimStringToWidth(customText, width - 14, true, (float) scale);
				float textStartP = Math.max(0, custom.currentTextPos);
				int maxTextPos = customText.length() - reverseText.length();
				if(textStartP > maxTextPos) textStartP = maxTextPos;
				
				String currentText = RenderHandler.trimStringToWidth(customText.substring((int) textStartP), width - 12, false, (float) scale);
				RenderHandler.drawText(currentText, xPosition + 5, textY, scale, true, 0x97bc98);
				
				updateTextPos(custom, maxTextPos);
			}
			
			endY = y + height;
			
			GL11.glColor3f(1,1,1);
		}else {
			int arrowSize = height/2;
			RenderHandler.drawImage((int)(xPosition + width - height/2 - arrowSize/2 - 1), yPosition + height/2 - arrowSize/2, arrowSize, arrowSize, arrowRight);
			endY = yPosition + height;
		}
	}
	
	private void updateTextPos(Option opt, int maxTextPos) {
		if(!opt.reverseTextPos) {
			opt.currentTextPos+= 0.03f;
		}else {
			opt.currentTextPos-= 0.06f;
		}
		
		if(opt.currentTextPos >= maxTextPos + 10) {
			opt.reverseTextPos = true;
		}
		
		if(opt.currentTextPos <= -10) {
			opt.reverseTextPos = false;
		}
	}
	
	Integer oldMouseX;
	int b = 0;
	
	public boolean mousePressed(int mouseX, int mouseY, int buttonId) {
		Minecraft mc = Minecraft.getMinecraft();
		
		if(customField != null) {
			if(isCurrentCustomOption()) {
				customField.mouseClicked(mouseX, mouseY, buttonId);
			}else {
				customField.setFocused(false);
			}
		}
		
		if(!opened) {
			if(super.mousePressed(mc, mouseX, mouseY)) {
				playPressSound(Minecraft.getMinecraft().getSoundHandler());
				
				if(!isCurrentCustomOption()) {
					opened = true;
				}else {
					if(mouseX >= this.xPosition + width - height && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + height) {
						opened = true;
						return true;
					}
					
					if(oldMouseX == null) oldMouseX = mouseX;
					if(oldMouseX == mouseX) {
						b++;
						
						if(b >= 2) {
							opened = true;
							b = 0;
						}
					}else {
						b = 1;
					}
					
					oldMouseX = mouseX;
				}
				
				return true;
			}
			
			b = 0;
			return false;
		}else {
			if(super.mousePressed(mc, mouseX, mouseY)) {
				playPressSound(Minecraft.getMinecraft().getSoundHandler());
				opened = false;
				return true;
			}
			
			int optionsTabHeight = (custom == null)? (options.size() - 1) * height : options.size() * height;
			if(this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition + height && mouseX < this.xPosition + this.width && mouseY < this.yPosition + height + optionsTabHeight) {
				playPressSound(Minecraft.getMinecraft().getSoundHandler());
				
				int optionIndex = (mouseY - this.yPosition + height) / height - 1;
				
				if(currentOption >= optionIndex) {
					currentOption = optionIndex - 1;
				}else {
					currentOption = optionIndex;
				}
				
				opened = false;
				return true;
			}
			
			opened = false;
			return false;
		}
	}
	
	public void textboxKeyTyped(char typedChar, int keyCode){
		customField.textboxKeyTyped(typedChar, keyCode);
		setCustom(customField.getText());
    }
	
	
	@Override
	public void drawButtonForegroundLayer(int x, int y) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(buttonTextures);
	
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale((float) width / 200f, (float) height / 20f, 1f);
        drawTexturedModalRect(0, 0, 0, 46 + 20, 200, 20);
        
        GlStateManager.popMatrix();
	}
}
