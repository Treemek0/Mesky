package treemek.mesky.handlers.gui.elements;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.utils.Waypoints;

public class ScrollBar extends Gui{
	private int maxBottomScroll; // how much you can scroll down (whole area height to scroll)
	private float ScrollOffset;
    private float targetScrollOffset;
	private int wholeSize;
	public int scrollbarHeight;
	public int scrollbarWidth;
	public int x;
	public int y;
	
	private int SCROLL_SPEED = SettingsConfig.ScrollbarSpeed.number.intValue();
	private float SMOOTH_SCROLLING_SPEED = SettingsConfig.ScrollbarSmoothness.number.floatValue();
	
	public ScrollBar(int maxBottomScroll, int width, int height, int x, int y) {
		this.maxBottomScroll = maxBottomScroll;
		this.scrollbarHeight = height;
		this.scrollbarWidth = width;
		this.x = x;
		this.y = y;
		this.ScrollOffset = 0;
        this.targetScrollOffset = 0;
	}
	
	public ScrollBar() {
		// if dont want to give 0,0,0,0
		this.ScrollOffset = 0;
		this.targetScrollOffset = 0;
		this.maxBottomScroll = 0;
	}
	
	// Scrolling
    public void handleMouseInput(int scroll) throws IOException {      
        if (scroll != 0) {
        	if(targetScrollOffset < maxBottomScroll && scroll < 0) return;
            targetScrollOffset -= scroll > 0 ? -SCROLL_SPEED : SCROLL_SPEED;
            targetScrollOffset = Math.min(0, targetScrollOffset); // cant go over 0, so you cant scroll up when at first waypoint 
            // This is so you cant scroll limitless, it takes every waypoint height with their margin and removes visible inputs height so you can scroll max to how much of inputs isnt visible
            targetScrollOffset = Math.max(targetScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
        }
    }
    
    // Length of scrollable space (contentHeight - visibleHeight) [must be positive]
    public void updateMaxBottomScroll(int maxBottomScroll) {
        maxBottomScroll = Math.min(-maxBottomScroll, 0);
    	this.maxBottomScroll = maxBottomScroll;
        targetScrollOffset = Math.max(targetScrollOffset, maxBottomScroll);
    }
    
    // Update scrollbar position to click
    public int updateOffsetToMouseClick(int mouseY){
    	float precentOfScrollbar = (float)(mouseY - y) / ((y + scrollbarHeight) - y);
		targetScrollOffset = (int) ((float)maxBottomScroll * precentOfScrollbar);
		targetScrollOffset = Math.max(targetScrollOffset, maxBottomScroll);
		return (int) ScrollOffset;
    }
    
    public int getOffset() {
    	ScrollOffset = Math.max(ScrollOffset, maxBottomScroll);
    	return (int) ScrollOffset;
    }
    
    // Update scrollbar width, height and position
    public void updateScrollBar(int width, int height, int x, int y) {
    	this.scrollbarHeight = height;
		this.scrollbarWidth = width;
		this.x = x;
		this.y = y;
    }
    
    public void renderScrollBar() {	
    	if(maxBottomScroll != 0) { // dont render if doesnt needed
    		if((targetScrollOffset != ScrollOffset)) updateScrollOffset();
    		
	        int scrollbar_height = (int) Math.max(scrollbarWidth * 2.857, Math.abs((scrollbarHeight - (scrollbarHeight / 20)) / Math.max(1, Math.abs(maxBottomScroll) / 15)));
	        
	        int scrollbarBG_endPosition = y + scrollbarHeight - scrollbar_height;

	        float scrollbar_percent = (maxBottomScroll != 0)?ScrollOffset / maxBottomScroll:0; // if maxBottom scroll is 0 then it cant be divided because x/0 = NaN
	        int scrollbar_positionY = (int)(y + (scrollbar_percent * (scrollbarBG_endPosition - y)));
	        scrollbar_positionY = (int) Math.max(y, Math.min(scrollbar_positionY, scrollbarBG_endPosition)); // scrollbar cant go past start and end positions (its because of bugs when changing resolution)
	       
        	drawRect(x, y, x + scrollbarWidth, y + scrollbarHeight, new Color(8, 7, 10, 150).getRGB());
        	
        	ResourceLocation scrollbar = new ResourceLocation(Reference.MODID, "gui/scrollbar.png");
        	Minecraft.getMinecraft().getTextureManager().bindTexture(scrollbar);
        	drawModalRectWithCustomSizedTexture(x, scrollbar_positionY, 0, 0, (int) scrollbarWidth, scrollbar_height, scrollbarWidth, scrollbar_height);
        }
    }
    
    // the same thing as renderScrollBar() just different name because yes
    public void drawScrollBar() {
    	renderScrollBar();
    }
    
    // Smooth function
    private void updateScrollOffset() {
    	if (Math.abs(targetScrollOffset - ScrollOffset) * SMOOTH_SCROLLING_SPEED < (SCROLL_SPEED * SMOOTH_SCROLLING_SPEED)/25) {
            ScrollOffset = targetScrollOffset; // Snap to target
    	}
    	
        ScrollOffset += (targetScrollOffset - ScrollOffset) * SMOOTH_SCROLLING_SPEED;
    }
    
}
