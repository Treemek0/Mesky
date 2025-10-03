package treemek.mesky.utils.chat.customChat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.Utils;

public class CustomChat extends GuiScreen {
    private final Minecraft mc;
    public List<ChatLine> messages = new ArrayList<>();
    public List<ChatLine> wrappedLines = new ArrayList<>();
    
    public List<ChatLine> preview_messages = new ArrayList<>();
    public List<ChatLine> preview_wrappedLines = new ArrayList<>();
    
    int maxMessages = 25;
    public boolean rightPacing = false;
    
    private double xP = 50;
    private double yP = 50;
    
    public int x = 0;
    public int y = 0;
	
	private float chatWidthScale = 1;
	private float chatHeightScale = 1;
	private float textScale = 1;
	
	float scales = 1;
	
	boolean isOpened = false;
	
	public long fadeStart = 3000; // ms after which fading starts
	public long fadeDuration = 1000; // fade out over 1 second
	
	private int scrollPos;

	
    public CustomChat(Minecraft mcIn)
    {
        this.mc = mcIn;
        
        preview_messages.clear();
        
	     // Co-op chat
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.GRAY + "[Co-op] " + EnumChatFormatting.GREEN + "Treemek" + EnumChatFormatting.GRAY + ": anyone got spare wood?"
	     ), 0));
	
	     // Collection unlock
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.YELLOW + "Wheat III " + EnumChatFormatting.GRAY + "(" + EnumChatFormatting.GREEN + "32/100" + EnumChatFormatting.GRAY + ")"
	     ), 0));
	
	     // Farming XP gain
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.GOLD + " Farming XP " + EnumChatFormatting.GREEN + "+15"
	     ), 0));
	
	     // Item pickup
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.GRAY + "You received " + EnumChatFormatting.AQUA + "Enchanted Cobblestone"
	     ), 0));
	
	     // Slayer start
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.DARK_PURPLE + "Slayer Quest Started! " + EnumChatFormatting.GRAY + "(Revenant Horror I)"
	     ), 0));
	
	     // Damage dealt
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.RED + "-" + EnumChatFormatting.DARK_RED + "152 " + EnumChatFormatting.RED + "Critical Damage"
	     ), 0));
	
	     // Player chat with rank
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.GRAY + "[" + EnumChatFormatting.GREEN + "VIP" + EnumChatFormatting.GRAY + "] " +
	         EnumChatFormatting.GREEN + "Player" + EnumChatFormatting.GRAY + ": anyone selling cane?"
	     ), 0));
	
	     // Bank deposit
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.LIGHT_PURPLE + "You deposited " + EnumChatFormatting.GOLD + "1,500 coins " +
	         EnumChatFormatting.LIGHT_PURPLE + "into the Bank!"
	     ), 0));
	
	     // Mana error
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.RED + "Not enough Mana!"
	     ), 0));
	     
	  // Skill level up
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.AQUA + "SKILL LEVEL UP " + EnumChatFormatting.GRAY + "(" +
	         EnumChatFormatting.GREEN + "Mining V" + EnumChatFormatting.GRAY + ")"
	     ), 0));

	     // Rare drop
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.DARK_PURPLE + "RARE DROP! " + EnumChatFormatting.LIGHT_PURPLE + "Summoning Eye"
	     ), 0));

	     // Dungeon teammate death
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.RED + " " + EnumChatFormatting.GRAY + "Your teammate " +
	         EnumChatFormatting.GREEN + "Treemek " + EnumChatFormatting.GRAY + "has died!"
	     ), 0));

	     // Pet found
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.LIGHT_PURPLE + "You found a " + EnumChatFormatting.AQUA + "Rare Griffin Pet!"
	     ), 0));

	     // Auction bid
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.YELLOW + "Bid of " + EnumChatFormatting.GOLD + "500,000 coins " +
	         EnumChatFormatting.YELLOW + "placed on your " + EnumChatFormatting.AQUA + "Aspect of the End"
	     ), 0));

	     // Slayer boss slain
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.DARK_PURPLE + "Boss defeated! " + EnumChatFormatting.GRAY +
	         "(Revenant Horror II)"
	     ), 0));

	     // Jerry Festival event
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.GREEN + "Jerry's Workshop Event " +
	         EnumChatFormatting.GRAY + "has started!"
	     ), 0));

	     // Fishing treasure
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.AQUA + "You fished up a " + EnumChatFormatting.LIGHT_PURPLE + "Sea Emperor!"
	     ), 0));

	     // Bazaar purchase
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.GREEN + "Bought " + EnumChatFormatting.AQUA + "160 Enchanted Sugar Cane " +
	         EnumChatFormatting.GREEN + "for " + EnumChatFormatting.GOLD + "12,000,000 coins"
	     ), 0));

	     // Dungeon blessing
	     preview_messages.add(new ChatLine(new ChatComponentText(
	         EnumChatFormatting.LIGHT_PURPLE + "You found " + EnumChatFormatting.AQUA + "Blessing of Wisdom " +
	         EnumChatFormatting.GRAY + "(+" + EnumChatFormatting.GREEN + "100 Intelligence" + EnumChatFormatting.GRAY + ")"
	     ), 0));
	}

    public void changePosition(double x, double y) {
    	this.xP = x;
    	this.yP = y;
    }
    
    public void changeSize(float widthSize, float heightSize) {
    	chatWidthScale = widthSize;
    	chatHeightScale = heightSize;
    }
    
	public void setTextScale(float scale) {
		textScale = scale;
	}
    
    public boolean isOpened() {
    	return isOpened;
    }
    
    public int getLineHeight() {
    	return (int) Math.max(4, 9 * textScale);
    }
    
    public void update(ScaledResolution sr) {
    	if(width != sr.getScaledWidth() || height != sr.getScaledHeight()) {
    		width = sr.getScaledWidth();
    		height = sr.getScaledHeight();
    		wrapMessages();
    	}
    	
    	if(chatHeightScale * chatWidthScale * textScale != scales) {
    		scales = chatHeightScale * chatWidthScale * textScale;
    		wrapMessages(); // changed scale
    	}
    	
    	x = (int) ((xP / 100) * width);
    	y = (int) ((yP / 100) * height);
    }
    
    public void drawChat() {
    	ScaledResolution sr = new ScaledResolution(mc);
    	
    	update(sr);
    	
    	int w = getChatWidth(width); // already scalled
    	int h = getMaxChatHeight(height); // already scalled
    	
    	int lineHeight = getLineHeight();
    	int visibleMessageLines = getVisibleLinesCount(h);
    	double scale = RenderHandler.getTextScale(lineHeight-2);
    	
    	int linesDrawn = 0;
    	for (int i = scrollPos; i < scrollPos + Math.min(visibleMessageLines+1, wrappedLines.size()); i++) {
			ChatLine message = wrappedLines.get(i);
			linesDrawn++;
			
			int alpha = 255;
			
			if(!isOpened()) {
				long currentTime = System.currentTimeMillis();
				long elapsed = currentTime - message.createdTime; // millis since message was created
				
				if (elapsed > fadeStart) {
			        float fadeProgress = Math.min(1.0f, (float)(elapsed - fadeStart) / fadeDuration);
			        alpha = (int)((1.0f - fadeProgress) * 255);
			    }
			    
			    if (elapsed > fadeStart + fadeDuration || alpha < 10) {
			        continue;
			    }
			}

			// === DRAWING CHAT LINE ===
			int lineY = y - (linesDrawn-1) * lineHeight;

			RenderHandler.drawRect(x, lineY - lineHeight, x + w, lineY, ((int)(alpha * SettingsConfig.customChatOpacity.number) << 24) | 0x000000);
			
			int textX = (int) (rightPacing ? x + w - RenderHandler.getTextWidth(message.message.getFormattedText(), scale) : x + 1);
			
			
			
			RenderHandler.drawText(message.message.getFormattedText(), textX, lineY - lineHeight + 1, scale, true, (alpha << 24) | (0xFFFFFF & 0xFFFFFF));
			
			if(linesDrawn >= visibleMessageLines) break;
		}
    	
    	if(isOpened()) {
	    	IChatComponent hoveredComp = getChatComponent(Mouse.getX(), Mouse.getY());
	    	if(hoveredComp != null && hoveredComp.getChatStyle().getChatHoverEvent() != null) {
	    		int m_x = Mouse.getX() * sr.getScaledWidth() / this.mc.displayWidth;
	    		int m_y = (height) - Mouse.getY() * sr.getScaledHeight() / this.mc.displayHeight;
	    		handleComponentHover(hoveredComp, m_x, m_y);
	    	}
    	}
    }
    
    public void drawPreviewChat(Integer customX, Integer customY) {
    	ScaledResolution sr = new ScaledResolution(mc);
    	
    	update(sr);
    	
    	int cX, cY;
    	
    	if(customX != null && customY != null) {
    		cX = customX;
    		cY = customY;
    	}else {
    		cX = x;
    		cY = y;
    	}
    	
    	int w = getChatWidth(width); // already scalled
    	int h = getMaxChatHeight(height); // already scalled
    	
    	int lineHeight = getLineHeight();
    	int visibleMessageLines = getVisibleLinesCount(h);
    	double scale = RenderHandler.getTextScale(lineHeight-2);
    	
    	int linesDrawn = 0;
    	for (int i = 0; i <= visibleMessageLines; i++) {
    		ChatLine message;
    	    if (i < preview_wrappedLines.size()) {
    	        message = preview_wrappedLines.get(i);
    	    } else {
    	        message = preview_wrappedLines.get(i % preview_wrappedLines.size());
    	    }
    		
			linesDrawn++;
			
			int alpha = 255;
			
			// === DRAWING CHAT LINE ===
			int lineY = cY - (linesDrawn-1) * lineHeight;
			
			RenderHandler.drawRect(cX, lineY - lineHeight, cX + w, lineY, ((int)(alpha * SettingsConfig.customChatOpacity.number) << 24) | 0x000000);
			
			int textX = (int) (rightPacing ? cX + w - RenderHandler.getTextWidth(message.message.getFormattedText(), scale) : cX + 1);
			
			String text = rightPacing ? swapWhitespace(message.message.getFormattedText()) : message.message.getFormattedText();
			
			RenderHandler.drawText(text, textX, lineY - lineHeight + 1, scale, true, (alpha << 24) | (0xFFFFFF & 0xFFFFFF));
			
			if(linesDrawn >= visibleMessageLines) break;
		}
    	
    	if(isOpened()) {
	    	IChatComponent hoveredComp = getChatComponent(Mouse.getX(), Mouse.getY());
	    	if(hoveredComp != null && hoveredComp.getChatStyle().getChatHoverEvent() != null) {
	    		int m_x = Mouse.getX() * sr.getScaledWidth() / this.mc.displayWidth;
	    		int m_y = (height) - Mouse.getY() * sr.getScaledHeight() / this.mc.displayHeight;
	    		handleComponentHover(hoveredComp, m_x, m_y);
	    	}
    	}
    }
    
    public int getChatWidth(int screenWidth) {
    	return (int) ((screenWidth / 4) * getWidthChatScale());
    }
    
    public int getMaxChatHeight(int screenHeight) {
    	return (int) ((screenHeight / 5) * getHeightChatScale());
    }
    
    public int getVisibleChatHeight(int screenHeight) {
    	int drawnLinesCount = Math.min(wrappedLines.size(), getVisibleLinesCount(getMaxChatHeight(height)));
        int cHeight = drawnLinesCount * getLineHeight();
        
        return cHeight;
    }
    
    public int getVisiblePreviewChatHeight(int screenHeight) {
    	int drawnLinesCount = getVisibleLinesCount(getMaxChatHeight(height));
        int cHeight = drawnLinesCount * getLineHeight();
        
        return cHeight;
    }
    
    public float getWidthChatScale() {
    	return chatWidthScale;
    }
    
    public float getHeightChatScale() {
    	return chatHeightScale;
    }
    
    public int getVisibleLinesCount(int chatHeight) {
    	return (int) ((float)chatHeight / getLineHeight());
    }
    
    public void addChatMessage(IChatComponent comp) {
    	Utils.writeToConsole("[SCHAT] " + comp.getFormattedText());
    	messages.add(new ChatLine(comp));
    	
    	int w = getChatWidth(width);
    	double scale = RenderHandler.getTextScale(getLineHeight()-2);
    	List<IChatComponent> lines = splitComponentToWidth(comp, w - 2, (float) scale);
		
		for (IChatComponent line : lines) {
			wrappedLines.add(0, new ChatLine(line));
		}
		
		if(messages.size() > maxMessages) {
			messages.remove(0);
		}
    }
    
	public void addChatMessage(String string) {
		ChatComponentText comp = new ChatComponentText(string);
		addChatMessage(comp);
	}
    
    public void openChat() {
    	if(!isOpened) {
    		isOpened = true;
    		Minecraft.getMinecraft().displayGuiScreen(this);
    	}
    }
    
    public void closeChat() {
    	if(isOpened) {
    		Minecraft.getMinecraft().displayGuiScreen(null);
    	}
    }
    
    public void clearChat() {
    	messages.clear();
    	wrappedLines.clear();
    }
    
    @Override
    public void onGuiClosed() {
    	isOpened = false;
    	super.onGuiClosed();
    }
    
    public void wrapMessages() {
    	wrappedLines.clear();
    	preview_wrappedLines.clear();
    	scrollPos = 0;

    	int w = getChatWidth(width);
    	double scale = RenderHandler.getTextScale(getLineHeight()-2);
    	
    	for (ChatLine message : messages) {
    		List<IChatComponent> lines = splitComponentToWidth(message.message, w - 2, (float) scale);
    		
    		for (IChatComponent line : lines) {
				wrappedLines.add(0, new ChatLine(line, message.createdTime));
			}
		}
    	
    	for (ChatLine message : preview_messages) {
    		List<IChatComponent> lines = splitComponentToWidth(message.message, w - 2, (float) scale);
    		
    		for (IChatComponent line : lines) {
				preview_wrappedLines.add(0, new ChatLine(line, message.createdTime));
			}
		}
    }
    
    private IChatComponent getChatComponent(int mouseX, int mouseY) {
    	ScaledResolution sr = new ScaledResolution(mc);
    	
    	int scaleFactor = sr.getScaleFactor();
    	int m_x = mouseX / scaleFactor;
        int m_y = height - (mouseY / scaleFactor);
        
        int lineHeight = getLineHeight();
        int drawnLinesCount = Math.min(wrappedLines.size(), getVisibleLinesCount(getMaxChatHeight(height)));
        int cHeight = drawnLinesCount * lineHeight;
        int cWidth = getChatWidth(width);
        
        if(m_x >= x && m_x <= x + cWidth && m_y <= y && m_y >= y - cHeight) {
        	int chatY = y - m_y;
        	
        	int textStartX = x;
        	
        	int i = drawnLinesCount - 1 - (cHeight - chatY) / lineHeight;
        	i = MathHelper.clamp_int(i, 0, drawnLinesCount-1);
        	
        	ChatLine line = wrappedLines.get(i+scrollPos);
        	
        	double scale = RenderHandler.getTextScale(lineHeight-2);
        	
        	if(rightPacing) textStartX = (int) (x + cWidth - RenderHandler.getTextWidth(swapWhitespace(line.message.getFormattedText()), scale));
        	
        	if(m_x < textStartX) return null;
        	
        	int x_Position = ColorUtils.removeTextFormatting(RenderHandler.trimStringToWidth(line.message.getFormattedText(), m_x + 1 - textStartX, false, scale)).length();
        	
        	if(x_Position == ColorUtils.removeTextFormatting(line.message.getFormattedText()).length()) return null;
        	
        	for (IChatComponent comp : line.message.getSiblings()) {
        		String m = ColorUtils.removeTextFormatting(comp.getUnformattedText());
        		
				if(m.length() < x_Position) {
					x_Position -= m.length();
				}else {
					return comp;
				}
			}
        }
        
        return null;
    }
    
    public void scroll(int amount)
    {
        this.scrollPos += amount;
        int i = this.wrappedLines.size();

        int lineCount = getVisibleLinesCount(getMaxChatHeight(height));
        
        if (this.scrollPos > i - lineCount)
        {
            this.scrollPos = i - lineCount;
        }

        if (this.scrollPos <= 0)
        {
            this.scrollPos = 0;
        }
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	if (mouseButton == 0)
        {
            IChatComponent ichatcomponent = getChatComponent(Mouse.getX(), Mouse.getY());

            if (this.handleComponentClick(ichatcomponent))
            {
                return;
            }
        }
    	
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0)
        {
            if (i > 1)
            {
                i = 1;
            }

            if (i < -1)
            {
                i = -1;
            }

            if (!isShiftKeyDown())
            {
                i *= 7;
            }

            scroll(i);
        }
    }
    
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    
    
    public static List<IChatComponent> splitComponentToWidth(IChatComponent component, int width, float scale) {
        List<IChatComponent> lines = new ArrayList<>();
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;

        int lineWidth = 0;
        IChatComponent currentLine = new ChatComponentText("");

        List<IChatComponent> siblings = Lists.newArrayList(component);
        
        for (int i = 0; i < siblings.size(); i++) {
            IChatComponent sibling = siblings.get(i);
            String text = sibling.getUnformattedTextForChat();
            ChatStyle style = sibling.getChatStyle().createShallowCopy();

            while (!text.isEmpty()) {
                int available = width - lineWidth;

                // trim string to fit remaining width
                String fit = trimStringToWidthPreserveWords(text, available, scale, font);
                String lastFormatting = FontRenderer.getFormatFromString(fit);
                String remainder = text.substring(fit.length());
                if(fit.endsWith("\n")) {
                	fit = fit.substring(0, fit.length() - 1);
                	
                	ChatComponentText part = new ChatComponentText(fit);
                    part.setChatStyle(style);
                    currentLine.appendSibling(part);
                	
                	lines.add(currentLine);
                    currentLine = new ChatComponentText("");
                    lineWidth = 0;

                    // keep formatting for next line
                    remainder = remainder.startsWith(" ") ? remainder.substring(1) : remainder;
                    text = lastFormatting + remainder; // skip leading spaces
                    continue;
                }
                
                ChatComponentText part = new ChatComponentText(fit);
                part.setChatStyle(style);
                currentLine.appendSibling(part);

                lineWidth += font.getStringWidth(fit) * scale;

                if (!remainder.isEmpty()) {
                    // wrap line
                    lines.add(currentLine);
                    currentLine = new ChatComponentText("");
                    lineWidth = 0;

                    // keep formatting for next line
                    remainder = remainder.startsWith(" ") ? remainder.substring(1) : remainder;
                    text = lastFormatting + remainder; // skip leading spaces
                } else {
                    text = "";
                }
            }
        }

        if (currentLine.getSiblings().size() > 0) {
            lines.add(currentLine);
        }

        return lines;
    }

    /** Trims a string to fit width but never splits words in half */
    private static String trimStringToWidthPreserveWords(String text, int width, float scale, FontRenderer font) {
        int len = text.length();
        float w = 0;

        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            w += font.getCharWidth(c) * scale;

            if (w > width) {
            	int ni = text.substring(0, i).indexOf(10);
            	if(ni != -1) {
            		return text.substring(0, ni + 1);
            	}
            	
                // backtrack to last space if possible
                int lastSpace = text.substring(0, i).lastIndexOf(' ');
                if (lastSpace >= 0) {
                	return text.substring(0, lastSpace);
                }
                
                return text.substring(0, i); // force cut
            }
        }

        int ni = text.indexOf(10);
    	if(ni != -1) {
    		return text.substring(0, ni + 1);
    	}
    	
        return text;
    }
    
    public static String swapWhitespace(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        int start = 0;
        int end = text.length() - 1;

        // count leading whitespace
        while (start < text.length() && Character.isWhitespace(text.charAt(start))) {
            start++;
        }

        // count trailing whitespace
        while (end >= 0 && Character.isWhitespace(text.charAt(end))) {
            end--;
        }

        String leading = text.substring(0, start);          // all left spaces
        String middle  = text.substring(start, end + 1);    // actual text
        String trailing = text.substring(end + 1);          // all right spaces

        // swap: right to left, left to right
        return trailing + middle + leading;
    }
    
    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
    	super.onResize(mcIn, w, h);

    	wrapMessages();
    }
}
