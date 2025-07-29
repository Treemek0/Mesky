package treemek.mesky.handlers.gui.elements.textFields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.Utils;

@SideOnly(Side.CLIENT)
public class TextField
{
    private final int id;
    private final FontRenderer fontRendererInstance;
    public int xPosition;
    public int yPosition;
    /** The width of this text field. */
    public int width;
    public int height;
    /** Has the current text being edited on the textbox. */
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;
    /** if true the textbox can lose focus by clicking elsewhere on the screen */
    private boolean canLoseFocus = true;
    /** If this value is true along with isEnabled, keyTyped will process the keys. */
    private boolean isFocused;
    /** If this value is true along with isFocused, keyTyped will process the keys. */
    private boolean isEnabled = true;
    /** The current character index that should be used as start of the rendered text. */
    private int lineScrollOffset;
    private int cursorPosition = 0;
    /** other selection position, maybe the same as the cursor */
    private int selectionEnd;
    private int enabledColor = 14737632;
    private int disabledColor = 7368816;
    /** True if this textbox is visible */
    private boolean visible = true;
    private boolean tabToggled = false;
    
    private boolean showColors = false;
    
    private GuiPageButtonList.GuiResponder field_175210_x;
    private Predicate<String> field_175209_y = Predicates.<String>alwaysTrue();

    public float scaleFactor;
	int defaultFontHeight;
    
	public void setColoredField(boolean b) {
		showColors = b;
	}
	
	public boolean isColoredField() {
		return showColors;
	}
	
    public TextField(int componentId, int x, int y, int width, int height)
    {
        this.id = componentId;
        this.fontRendererInstance = Minecraft.getMinecraft().fontRendererObj;
        this.xPosition = x;
        this.yPosition = y;
        this.width = width;
        this.height = height;
        
        defaultFontHeight = fontRendererInstance.FONT_HEIGHT;
		scaleFactor = (float) RenderHandler.getTextScale(height/2.2);
    }
    
    public void update(int x, int y, int width, int height) {
        this.xPosition = x;
        this.yPosition = y;
        this.width = width;
        this.height = height;
        
        defaultFontHeight = fontRendererInstance.FONT_HEIGHT;
		scaleFactor = (float) RenderHandler.getTextScale(height/2.2);
    }

    public void func_175207_a(GuiPageButtonList.GuiResponder p_175207_1_)
    {
        this.field_175210_x = p_175207_1_;
    }

    /**
     * Increments the cursor counter
     */
    public void updateCursorCounter()
    {
        ++this.cursorCounter;
    }

    /**
     * Sets the text of the textbox
     */
    public void setText(String text)
    {
        if (this.field_175209_y.apply(text))
        {
            if (text.length() > this.maxStringLength)
            {
                this.text = text.substring(0, this.maxStringLength);
            }
            else
            {
                this.text = text;
            }

            setCursorPosition(Math.min(text.length(), cursorPosition));
        }
    }

    /**
     * Returns the contents of the textbox
     */
    public String getText()
    {
        return this.text;
    }
    
    public int getStartOfSelection() {
    	return this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
    }
    
    public int getEndOfSelection() {
    	return this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
    }
    
    public boolean isTextSelected() {
    	return cursorPosition != selectionEnd;
    }
    
    /**
     * returns the text between the cursor and selectionEnd
     */
    public String getSelectedText()
    {
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(i, j);
    }

    public void func_175205_a(Predicate<String> p_175205_1_)
    {
        this.field_175209_y = p_175205_1_;
    }

    /**
     * replaces selected text, or inserts text at the position on the cursor
     */
    public void writeText(String p_146191_1_)
    {
        String s = "";
        String s1 = ChatAllowedCharacters.filterAllowedCharacters(p_146191_1_);
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int k = this.maxStringLength - this.text.length() - (i - j);
        int l = 0;

        if (this.text.length() > 0)
        {
            s = s + this.text.substring(0, i);
        }

        if (k < s1.length())
        {
            s = s + s1.substring(0, k);
            l = k;
        }
        else
        {
            s = s + s1;
            l = s1.length();
        }

        if (this.text.length() > 0 && j < this.text.length())
        {
            s = s + this.text.substring(j);
        }

        if (this.field_175209_y.apply(s))
        {
            this.text = s;
            this.moveCursorBy(i - this.selectionEnd + l);

            if (this.field_175210_x != null)
            {
                this.field_175210_x.func_175319_a(this.id, this.text);
            }
        }
    }

    /**
     * Deletes the specified number of words starting at the cursor position. Negative numbers will delete words left of
     * the cursor.
     */
    public void deleteWords(int p_146177_1_)
    {
        if (this.text.length() != 0)
        {
            if (this.selectionEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                this.deleteFromCursor(this.getNthWordFromCursor(p_146177_1_) - this.cursorPosition);
            }
        }
    }

    /**
     * delete the selected text, otherwsie deletes characters from either side of the cursor. params: delete num
     */
    public void deleteFromCursor(int p_146175_1_)
    {
        if (this.text.length() != 0)
        {
            if (this.selectionEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                boolean flag = p_146175_1_ < 0;
                int i = flag ? this.cursorPosition + p_146175_1_ : this.cursorPosition;
                int j = flag ? this.cursorPosition : this.cursorPosition + p_146175_1_;
                String s = "";

                if (i >= 0)
                {
                    s = this.text.substring(0, i);
                }

                if (j < this.text.length())
                {
                    s = s + this.text.substring(j);
                }

                if (this.field_175209_y.apply(s))
                {
                    this.text = s;

                    if (flag)
                    {
                        this.moveCursorBy(p_146175_1_);
                    }

                    if (this.field_175210_x != null)
                    {
                        this.field_175210_x.func_175319_a(this.id, this.text);
                    }
                }
            }
        }
    }

    public int getId()
    {
        return this.id;
    }

    /**
     * see @getNthNextWordFromPos() params: N, position
     */
    public int getNthWordFromCursor(int p_146187_1_)
    {
        return this.getNthWordFromPos(p_146187_1_, this.getCursorPosition());
    }

    /**
     * gets the position of the nth word. N may be negative, then it looks backwards. params: N, position
     */
    public int getNthWordFromPos(int n, int startPos) {
        return findNthWord(n, startPos, true);
    }

    public int findNthWord(int n, int startPos, boolean skipSpaces) {
        int currentPos = startPos;
        boolean movingBackwards = n < 0;
        int wordsToMove = Math.abs(n);

        for (int i = 0; i < wordsToMove; i++) {
            if (!movingBackwards) {
                // Moving forwards
                int textLength = this.text.length();
                currentPos = this.text.indexOf(' ', currentPos);

                if (currentPos == -1) {
                    // If no more spaces, move to the end of the text
                    currentPos = textLength;
                } else {
                    // Optionally skip over multiple spaces
                    while (skipSpaces && currentPos < textLength && this.text.charAt(currentPos) == ' ') {
                        currentPos++;
                    }
                }
            } else {
                // Moving backwards
                while (skipSpaces && currentPos > 0 && this.text.charAt(currentPos - 1) == ' ') {
                    currentPos--;
                }

                while (currentPos > 0 && this.text.charAt(currentPos - 1) != ' ') {
                    currentPos--;
                }
            }
        }

        return currentPos;
    }

    /**
     * Moves the text cursor by a specified number of characters and clears the selection
     */
    public void moveCursorBy(int p_146182_1_)
    {
        this.setCursorPosition(this.selectionEnd + p_146182_1_);
    }

    /**
     * sets the position of the cursor to the provided index
     */
    public void setCursorPosition(int p_146190_1_)
    {
        int i = this.text.length();
        this.cursorPosition = MathHelper.clamp_int(p_146190_1_, 0, i);
        this.setSelectionPos(this.cursorPosition);
    }

    /**
     * sets the cursors position to the beginning
     */
    public void setCursorPositionZero()
    {
        this.setCursorPosition(0);
    }

    /**
     * sets the cursors position to after the text
     */
    public void setCursorPositionEnd()
    {
        this.setCursorPosition(this.text.length());
    }

    /**
     * Call this method from your GuiScreen to process the keys into the textbox
     */
    public boolean textboxKeyTyped(char typedChar, int keyCode)
    {
        if (!this.isFocused)
        {
            return false;
        }
        else if (GuiScreen.isKeyComboCtrlA(keyCode))
        {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        }
        else if (GuiScreen.isKeyComboCtrlC(keyCode))
        {
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
        }
        else if (GuiScreen.isKeyComboCtrlV(keyCode))
        {
            if (this.isEnabled)
            {
                this.writeText(GuiScreen.getClipboardString());
            }

            return true;
        }
        else if (GuiScreen.isKeyComboCtrlX(keyCode))
        {
            GuiScreen.setClipboardString(this.getSelectedText());

            if (this.isEnabled)
            {
                this.writeText("");
            }

            return true;
        }
        else if (isKeyComboCtrl7(keyCode))
        {
            if (this.isEnabled)
            {
                this.writeText("<&>");
                this.moveCursorBy(-1);
            }

            return true;
        }
        else if(keyCode == Keyboard.KEY_TAB) {
        	tabToggled = !tabToggled;
        	return true;
        }
        else
        {
            switch (keyCode)
            {
                case 14:

                    if (GuiScreen.isCtrlKeyDown())
                    {
                        if (this.isEnabled)
                        {
                            this.deleteWords(-1);
                        }
                    }
                    else if (this.isEnabled)
                    {
                        this.deleteFromCursor(-1);
                    }

                    return true;
                case 199:

                    if (GuiScreen.isShiftKeyDown())
                    {
                        this.setSelectionPos(0);
                    }
                    else
                    {
                        this.setCursorPositionZero();
                    }

                    return true;
                case 203:

                    if (GuiScreen.isShiftKeyDown())
                    {
                        if (GuiScreen.isCtrlKeyDown())
                        {
                            this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                        }
                        else
                        {
                            this.setSelectionPos(this.getSelectionEnd() - 1);
                        }
                    }
                    else if (GuiScreen.isCtrlKeyDown())
                    {
                        this.setCursorPosition(this.getNthWordFromCursor(-1));
                    }
                    else
                    {
                    	if(cursorPosition != selectionEnd) {
                    		setCursorPosition(getStartOfSelection());
                    	}else {
                    		this.moveCursorBy(-1);
                    	}
                    }

                    return true;
                case 205:

                    if (GuiScreen.isShiftKeyDown())
                    {
                        if (GuiScreen.isCtrlKeyDown())
                        {
                            this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                        }
                        else
                        {
                            this.setSelectionPos(this.getSelectionEnd() + 1);
                        }
                    }
                    else if (GuiScreen.isCtrlKeyDown())
                    {
                        this.setCursorPosition(this.getNthWordFromCursor(1));
                    }
                    else
                    {
                    	if(cursorPosition != selectionEnd) {
                    		setCursorPosition(getEndOfSelection());
                    	}else {
                    		this.moveCursorBy(1);
                    	}
                    }

                    return true;
                case 207:

                    if (GuiScreen.isShiftKeyDown())
                    {
                        this.setSelectionPos(this.text.length());
                    }
                    else
                    {
                        this.setCursorPositionEnd();
                    }

                    return true;
                case 211:

                    if (GuiScreen.isCtrlKeyDown())
                    {
                        if (this.isEnabled)
                        {
                            this.deleteWords(1);
                        }
                    }
                    else if (this.isEnabled)
                    {
                        this.deleteFromCursor(1);
                    }

                    return true;
                default:

                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar))
                    {
                        if (this.isEnabled)
                        {
                            this.writeText(Character.toString(typedChar));
                        }

                        return true;
                    }
                    else
                    {
                        return false;
                    }
            }
        }
    }

    /**
     * Args: x, y, buttonClicked
     */
    public void mouseClicked(int mouseX_, int mouseY_, int buttonId_)
    {
        boolean flag = mouseX_ >= this.xPosition && mouseX_ < this.xPosition + this.width && mouseY_ >= this.yPosition && mouseY_ < this.yPosition + this.height;
        
        if (this.canLoseFocus)
        {
            this.setFocused(flag);
        }

        if (this.isFocused && flag && buttonId_ == 0)
        {
            int i = mouseX_ - this.xPosition - 4;

            String s = RenderHandler.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.width - 8, false, scaleFactor);
            this.setCursorPosition(RenderHandler.trimStringToWidth(s, i, false, scaleFactor).length() + this.lineScrollOffset);
        }
    }

    
    /**
     * Draws the textbox
     */
    public void drawTextBox()
    {
        if (this.getVisible())
        {
            Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -6250336);
            Gui.drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1, this.yPosition + this.height - 1, -16777216);
            
            int i = this.isEnabled ? this.enabledColor : this.disabledColor;

            boolean colored = (!isFocused || tabToggled) && showColors;
            
            List<Pair> colorSetters = new ArrayList<>();
            
            if(colored) {
	            Matcher matcher = Reference.COLOR_PATTERN.matcher(this.text);
	            StringBuilder renderedText = new StringBuilder();
	
	            while (matcher.find()) {
	            	EnumChatFormatting color = ColorUtils.getColorFromCode(matcher.group());
	            	int index = (ColorUtils.isEnumColor(color) || color == EnumChatFormatting.RESET)? matcher.start() : matcher.end();
	            	colorSetters.add(new Pair(index, color));
	            }
            }
            
            int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String s = RenderHandler.trimStringToWidth(this.text.substring(this.lineScrollOffset), getWritableWidth(), false, scaleFactor);
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused && this.cursorCounter / 10 % 2 == 0 && flag;
            
            int l =  this.xPosition + 4;
            int i1 = this.yPosition + (this.height/2) - ((int)(defaultFontHeight * scaleFactor)/2);
            double j1 = l;

            List<EnumChatFormatting> lastColorBeforeCursor = new ArrayList<>();
            
            if (s.length() > 0) {
                String s1 = flag ? s.substring(0, j) : s;
                
                if(colored) {
	                List<EnumChatFormatting> lastColor = new ArrayList<>();
	                int add = 0; // when adding color to s1, index is being offset by 2, because color code added to string has 2 chars
	
	                for (int m = 0; m < colorSetters.size(); m++) {
	                    int index = colorSetters.get(m).i;
	                    EnumChatFormatting color = colorSetters.get(m).color;
	
	                    if (index < lineScrollOffset) {
	                        if(ColorUtils.isEnumColor(color)) lastColor.clear();
	                    	lastColor.add(color); // if the color is before the visible area, set it as the lastColor
	                        
	                        if(index - lineScrollOffset <= j) {
	                        	if(ColorUtils.isEnumColor(color)) lastColorBeforeCursor.clear();
	                        	lastColorBeforeCursor.add(color);
	                        }
	                    } else if (index - lineScrollOffset < s1.length()) {
	                        int visibleIndex = MathHelper.clamp_int(index - lineScrollOffset + add, 0, s1.length());
	                        
	                        s1 = s1.substring(0, visibleIndex) + color + s1.substring(visibleIndex);
	                        add += color.toString().length();
	                        
	                        if(index - lineScrollOffset <= j) {
	                        	if(ColorUtils.isEnumColor(color)) lastColorBeforeCursor.clear();
	                        	lastColorBeforeCursor.add(color);
	                        }
	                    }
	                }
	
	                s1 = applyListOfChatFormattingsToStart(s1, lastColor); // apply the last color at the start of the visible string
                }
                RenderHandler.drawText(s1, (float) l, (float) i1, scaleFactor, true, i);
                j1 = l + (int) (fontRendererInstance.getStringWidth(s1) * scaleFactor) + 0.3f;
            }

            boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            double k1 = j1;

            if (!flag)
            {
                k1 = j > 0 ? l + this.width : l;
            }
            else if (flag2)
            {
                k1 = j1 - 1;
                --j1;
            }

            if (s.length() > 0 && flag && j < s.length()) {
                String t = s.substring(j);
                
                if(colored) {
	                int add = 0; 
	
	                for (int m = 0; m < colorSetters.size(); m++) {
	                    int index = colorSetters.get(m).i; // Original index
	                    EnumChatFormatting color = colorSetters.get(m).color;
	
	                    int adjustedIndex = index - lineScrollOffset;
	
	                    if (adjustedIndex >= j && adjustedIndex - j < t.length()) {
	                        int visibleIndex = MathHelper.clamp_int(adjustedIndex - j + add, 0, t.length());
	
	                        t = t.substring(0, visibleIndex) + color + t.substring(visibleIndex);
	                        add += color.toString().length();
	                    }
	                }
	
	                t = applyListOfChatFormattingsToStart(t, lastColorBeforeCursor);
                }
                RenderHandler.drawText(t, (float) j1 + 0.7f, (float) i1, scaleFactor, true, i);
            }


            
            if (k != j)
            {
                if (k > s.length())
                {
                    k = s.length();
                }
            	
                int l1 = (int) (l + this.fontRendererInstance.getStringWidth(s.substring(0, k)) * scaleFactor);
                this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + this.fontRendererInstance.FONT_HEIGHT * scaleFactor);
                
                if (flag1)
                {
                	RenderHandler.drawRect(k1, i1 - 1, k1 + scaleFactor, (int) (i1 + this.fontRendererInstance.FONT_HEIGHT * scaleFactor), -3092272);
                }
            }else {
	            if (flag1)
	            {
	                if (flag2)
	                {
	                    RenderHandler.drawRect(k1, i1 - 1, k1 + scaleFactor, (int) (i1 + this.fontRendererInstance.FONT_HEIGHT * scaleFactor), -3092272);
	                }
	                else
	                {
	                    RenderHandler.drawText("_", (float)k1, (float)i1, scaleFactor, true, i);
	                }
	            }
            }
            
            GL11.glColor3f(1, 1, 1);
            GlStateManager.color(1, 1, 1, 1);
        }
    }
    
    private String applyListOfChatFormattingsToStart(String text, List<EnumChatFormatting> list) {
    	for (int i = list.size() - 1; i >= 0; i--) {
    		text = list.get(i) + text;
		}
    	
    	return text;
    }
    
    /**
     *  White selection rectangle
     */
    private void drawCursorVertical(double x, double y, double end_x, double end_y)
    {
        if (x < end_x)
        {
            double i = x;
            x = end_x;
            end_x = i;
        }

        if (y < end_y)
        {
            double j = y;
            y = end_y;
            end_y = j;
        }

        if (end_x > this.xPosition + this.width)
        {
            end_x = this.xPosition + this.width;
        }

        if (x > this.xPosition + this.width)
        {
            x = this.xPosition + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)x, (double)end_y, 0.0D).endVertex();
        worldrenderer.pos((double)end_x, (double)end_y, 0.0D).endVertex();
        worldrenderer.pos((double)end_x, (double)y, 0.0D).endVertex();
        worldrenderer.pos((double)x, (double)y, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setMaxStringLength(int p_146203_1_)
    {
        this.maxStringLength = p_146203_1_;

        if (this.text.length() > p_146203_1_)
        {
            this.text = this.text.substring(0, p_146203_1_);
        }
    }

    /**
     * returns the maximum number of character that can be contained in this textbox
     */
    public int getMaxStringLength()
    {
        return this.maxStringLength;
    }

    /**
     * returns the current position of the cursor
     */
    public int getCursorPosition()
    {
        return this.cursorPosition;
    }

    /**
     * Sets the text colour for this textbox (disabled text will not use this colour)
     */
    public void setTextColor(int p_146193_1_)
    {
        this.enabledColor = p_146193_1_;
    }

    public void setDisabledTextColour(int p_146204_1_)
    {
        this.disabledColor = p_146204_1_;
    }

    /**
     * Sets focus to this gui element
     */
    public void setFocused(boolean b)
    {
        if (b && !this.isFocused)
        {
            this.cursorCounter = 0;
        }
        
        if(!b) {
        	setCursorPositionZero();
        	tabToggled = false;
        }

        this.isFocused = b;
    }

    /**
     * Getter for the focused field
     */
    public boolean isFocused()
    {
        return this.isFocused;
    }

    public void setEnabled(boolean p_146184_1_)
    {
        this.isEnabled = p_146184_1_;
    }

    /**
     * the side of the selection that is not the cursor, may be the same as the cursor
     */
    private int getSelectionEnd()
    {
        return this.selectionEnd;
    }

    /**
     * returns the width of the textbox depending on if background drawing is enabled
     */
    public int getWritableWidth()
    {
         return this.width - 8;
    }

    /**
     * Sets the position of the selection anchor (i.e. position the selection was started at)
     */
    public void setSelectionPos(int p_146199_1_)
    {
        int i = this.text.length();

        if (p_146199_1_ > i)
        {
            p_146199_1_ = i;
        }

        if (p_146199_1_ < 0)
        {
            p_146199_1_ = 0;
        }

        this.selectionEnd = p_146199_1_;
        
        if (this.fontRendererInstance != null)
        {
            if (this.lineScrollOffset > i)
            {
                this.lineScrollOffset = i;
            }

            int j = this.getWritableWidth();
            String s = RenderHandler.trimStringToWidth(this.text.substring(this.lineScrollOffset), j, false, scaleFactor);
            int k = s.length() + this.lineScrollOffset;

            if (p_146199_1_ == this.lineScrollOffset)
            {
                this.lineScrollOffset -= RenderHandler.trimStringToWidth(this.text, j, true, scaleFactor).length();
            }

            if (p_146199_1_ > k)
            {
                this.lineScrollOffset += p_146199_1_ - k;
            }
            else if (p_146199_1_ <= this.lineScrollOffset)
            {
                this.lineScrollOffset -= this.lineScrollOffset - p_146199_1_;
            }

            this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, i);
        }
    }

    /**
     * if true the textbox can lose focus by clicking elsewhere on the screen
     */
    public void setCanLoseFocus(boolean p_146205_1_)
    {
        this.canLoseFocus = p_146205_1_;
    }

    /**
     * returns true if this textbox is visible
     */
    public boolean getVisible()
    {
        return this.visible;
    }

    /**
     * Sets whether or not this textbox is visible
     */
    public void setVisible(boolean p_146189_1_)
    {
        this.visible = p_146189_1_;
        if(!visible) setFocused(false);
    }
    
    public void setSelection(int start, int end) {
    	setSelectionPos(start);
    	setCursorPosition(end);
    }
    
    public void selectAll() {
    	setSelectionPos(0);
    	setCursorPosition(getText().length());
    }
    
    // color key combo
    public static boolean isKeyComboCtrl7(int key)
    {
        return key == Keyboard.KEY_7 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
    }
    
    private class Pair{
    	int i;
    	EnumChatFormatting color;
    	
    	private Pair(int i, EnumChatFormatting color) {
    		this.i = i;
    		this.color = color;
    	}
    }
}
