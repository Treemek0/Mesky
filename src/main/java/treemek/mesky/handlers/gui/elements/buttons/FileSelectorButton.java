package treemek.mesky.handlers.gui.elements.buttons;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;

public class FileSelectorButton extends GuiButton{

	GuiTextField resultField;
	
	public FileSelectorButton(int buttonId, int x, int y, int size, GuiTextField resultTextField) {
		super(buttonId, x, y, size, size, "");
		resultField = resultTextField;
	}

	ResourceLocation folder = new ResourceLocation(Reference.MODID, "gui/folder.png");
	ResourceLocation folder_hovered = new ResourceLocation(Reference.MODID, "gui/folder_hovered.png");
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if(super.mousePressed(mc, mouseX, mouseY)) {
			mc.renderEngine.bindTexture(folder_hovered);
     		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
		}else {
			mc.renderEngine.bindTexture(folder);
	     	drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
		}
	}
	
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean isHovered = super.mousePressed(mc, mouseX, mouseY);
		
		if(isHovered) {
			openFileChooser();
		}
		
		return isHovered;
	}
	
	private void openFileChooser() {
	    // Ensure the GUI thread is used to open the file dialog
	    Minecraft.getMinecraft().addScheduledTask(() -> {
	        FileDialog fileDialog = new FileDialog((java.awt.Frame) null, "Select \"cape0\" File", FileDialog.LOAD);
	        fileDialog.setVisible(true);

	        String directory = fileDialog.getDirectory();
	        if (directory != null) {
	            setFolderPath(directory.substring(0, directory.length()-1));
	        }
	    });
	}

    private void setFolderPath(String path) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            resultField.setText(path);
        });
    }
	
}
