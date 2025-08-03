package treemek.mesky.handlers.gui.elements.warnings;

import java.awt.Color;
import java.io.IOException;
import java.util.function.BooleanSupplier;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ChatComponentText;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.buttons.ColoredButton;

public class CloseWarning extends GuiScreen{
	private ColoredButton SaveAndClose;
	private ColoredButton CloseWithoutSaving;
	private GuiButton nevermind;
	public boolean showElement = false;
	
	private final BooleanSupplier saveFunction;
	
	public CloseWarning(BooleanSupplier saveFunction) {
		this.saveFunction = saveFunction;
		
		ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
		int buttonWidth = (int) Math.min(120, resolution.getScaledWidth_double()/2);
		int buttonHeight = 20;
		int yeahX = (int) (resolution.getScaledWidth() * 0.25f) - buttonWidth/2;
		SaveAndClose = new ColoredButton(0, yeahX, resolution.getScaledHeight()/2, buttonWidth, buttonHeight, "Save & Close", new Color(157f / 255f, 255f / 255f, 165f / 255f));
		SaveAndClose.enabled = false;
		
		int closeWithoutSavingX = (int) (resolution.getScaledWidth() * 0.75f) - buttonWidth/2;
		CloseWithoutSaving = new ColoredButton(0, closeWithoutSavingX, resolution.getScaledHeight()/2, buttonWidth, buttonHeight, "Close without saving", new Color(255f / 255f, 129f / 255f, 120f / 255f));
		CloseWithoutSaving.enabled = false;
		
		int nevermindX = (int) (resolution.getScaledWidth() * 0.50f) - buttonWidth/2;
		nevermind = new GuiButton(1, nevermindX, resolution.getScaledHeight()/2 + buttonHeight*2, buttonWidth, buttonHeight, "Nevermind");
		nevermind.enabled = false;
	}

	public void drawElement(Minecraft mc, int mouseX, int mouseY) {
		if(showElement) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, 100);
			ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
			
			drawRect(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), 0xea000000);
			
			RenderHandler.drawText("You didn't save changes", resolution.getScaledWidth()/2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("You didn't save changes"), resolution.getScaledHeight()*0.3f, 2, true, 0xFFFFFFFF);
			
			SaveAndClose.drawButton(mc, mouseX, mouseY);
			
			CloseWithoutSaving.drawButton(mc, mouseX, mouseY);
			
			GL11.glColor3f(1, 1, 1);
			nevermind.drawButton(mc, mouseX, mouseY);
			GL11.glPopMatrix();
		}
	}
	
	public void actionPerformed(GuiButton button) {
		if(button.equals(CloseWithoutSaving)) {
			Minecraft.getMinecraft().thePlayer.closeScreen();
		}
		
		if(button.equals(nevermind)) {
			changeElementActive(false);
		}
		
		if(button.equals(SaveAndClose)) {
			if(saveFunction.getAsBoolean()) {
				Minecraft.getMinecraft().thePlayer.closeScreen();
			}else {
				changeElementActive(false);
			}
		}
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0)
        {
            GuiButton guibutton = nevermind;
            if (guibutton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
            {
                guibutton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
                actionPerformed(guibutton);
            }
        }
		
		if (mouseButton == 0)
        {
            GuiButton guibutton = SaveAndClose;
            if (guibutton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
            {
                guibutton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
                actionPerformed(guibutton);
            }
        }
		
		if (mouseButton == 0)
        {
            GuiButton guibutton = CloseWithoutSaving;
            if (guibutton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
            {
                guibutton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
                actionPerformed(guibutton);
            }
        }
	}
	
	public void changeElementActive(boolean b) {
		showElement = b;
		nevermind.enabled = b;
		SaveAndClose.enabled = b;
		CloseWithoutSaving.enabled = b;
	}
}
