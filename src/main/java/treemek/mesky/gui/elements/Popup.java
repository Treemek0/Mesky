package treemek.mesky.handlers.gui.elements;

import java.util.LinkedList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import java.util.Queue;
import java.util.LinkedList;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.Utils;

public class Popup {
	public class PopupElement {
		String text = "";
		long time = 0;
				
		private PopupElement(String text, long time) {
			this.text = text;
			this.time = time;
		}
	}
	
	private int scale = 1;
	private long showTime = 1000;
	private long SHOW_INTERVAL = 0;
	
	public Popup(long showTime) {
		this.showTime = showTime;
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		width = Math.min(150, Math.max(60, sr.getScaledWidth() / 4));
		height = width / 3;
		
		x = sr.getScaledWidth();
	}
	
	private boolean isShown = false;
	private boolean isHiding = false;
	Queue<PopupElement> popupText = new LinkedList<>();
	private PopupElement currentPopup = null;
	private double x = 0;
	private double width = 0;
	private double height = 0;
	
	private ResourceLocation popupLocation = new ResourceLocation(Reference.MODID, "gui/popup.png");
	
	public void drawPopup() {
		if(isShown) {
			if(currentPopup == null) {
				currentPopup= popupText.poll();
				
				if(currentPopup == null) {
					isShown = false;
					isHiding = false;
					return;
				}
			}
			
			RenderHandler.drawImage((int) x, 0, (int) width, (int) height, popupLocation);
			
			double textScale = Math.min(RenderHandler.getTextScale(currentPopup.text, width*0.8f), RenderHandler.getTextScale(height/2));
			double textWidth = RenderHandler.getTextWidth(currentPopup.text, textScale);
			double textHeight = RenderHandler.getTextHeight(textScale);
			
			RenderHandler.drawText(currentPopup.text, x + width/2 - textWidth/2, height/2 - textHeight/2, textScale, true, 0xFFFFFF);
			
			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
			
			if(!isHiding) {
				if(x > sr.getScaledWidth() - width) {
					x -= width / 100 * (240f/Minecraft.getDebugFPS());
					SHOW_INTERVAL = System.currentTimeMillis();
				}else {
					if(System.currentTimeMillis() - SHOW_INTERVAL >= currentPopup.time) {
						isHiding = true;
					}
				}
			}
			
			if(isHiding) {
				if(x < sr.getScaledWidth()) {
					x += width / 100 * (240f/Minecraft.getDebugFPS());
				}else {
					isHiding = false;
					currentPopup = popupText.poll();
					
					if(currentPopup == null) {
						isShown = false;
					}
				}
			}
		}
	}
	
	public void showPopup(String text) {
		isShown = true;
		popupText.add(new PopupElement(text, showTime));
	}
	
	public void showPopup(String text, long time) {
		isShown = true;
		popupText.add(new PopupElement(text, time));
	}
	
	public void resetPopups() {
		popupText.clear();
	}
}
