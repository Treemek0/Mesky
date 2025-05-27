package treemek.mesky.handlers.gui.elements.buttons;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.MovementUtils.Task;

public class FileSelectorButton extends GuiButton{

	TextField resultField;
	List<String> filter = new ArrayList<>();
	String buttonText;
	boolean saveDirectory = false;
	Task task;
	
	public FileSelectorButton(int buttonId, int x, int y, int size, TextField pathField) {
		super(buttonId, x, y, size, size, "");
		resultField = pathField;
	}
	
	public FileSelectorButton(int buttonId, int x, int y, int width, int height, TextField pathField, String buttonText) {
		super(buttonId, x, y, width,  Math.max(10, height), "");
		resultField = pathField;
		this.buttonText = buttonText;
	}
	
	public void setFilter(List<String> ex) {
		filter = ex;
	}
	
	public void setFilter(String ex) {
		List<String> newFilter = new ArrayList<>();
		newFilter.add(ex);
		filter = newFilter;
	}
	
	public void setOnlyDirectories(boolean b) {
		saveDirectory = b;
	}

	ResourceLocation folder = new ResourceLocation(Reference.MODID, "gui/folder.png");
	ResourceLocation folder_hovered = new ResourceLocation(Reference.MODID, "gui/folder_hovered.png");
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		GL11.glColor3f(1, 1, 1);
		
		if(buttonText == null) {
			if(super.mousePressed(mc, mouseX, mouseY)) {
				mc.renderEngine.bindTexture(folder_hovered);
	     		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
			}else {
				mc.renderEngine.bindTexture(folder);
		     	drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
			}
		}else {
			double scale = Math.min(RenderHandler.getTextScale(height - 10), RenderHandler.getTextScale(buttonText, width - height - 3));
			double textY = yPosition + height/2 - RenderHandler.getTextHeight(scale)/2;
			
			drawButtonForegroundLayer(xPosition, yPosition, super.mousePressed(mc, mouseX, mouseY));
			
			RenderHandler.drawText(buttonText, xPosition + 3, textY, scale, true, 0xFFFFFF);
			
			int size = height - 6;
			mc.renderEngine.bindTexture(folder);
	     	drawModalRectWithCustomSizedTexture(xPosition + width - height + 3, yPosition + 3, 0, 0, size, size, size, size);
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
	    SwingUtilities.invokeLater(() -> {
	        FileDialog fileDialog = new FileDialog((Frame) null, "Select File", FileDialog.LOAD);
	        fileDialog.setDirectory(resultField.getText());
	        
	        // Create a filter string for file types
            StringBuilder filterString = new StringBuilder();
            if (!filter.isEmpty()) {
                for (String ext : filter) {
                    if (filterString.length() > 0) {
                        filterString.append(";");
                    }
                    filterString.append("*").append(ext);
                }
            } else {
                filterString.append("*.*"); // Allow all files if the list is empty
            }

            fileDialog.setFile(filterString.toString()); // Apply the filter
	        
	        fileDialog.setVisible(true);

	        String directory = fileDialog.getDirectory();
	        String fileName = fileDialog.getFile();

	        if (directory != null && fileName != null) {
	            Path fullPath = Paths.get(directory, fileName);

	            if (saveDirectory) {
	                Path parentDirectory = fullPath.getParent();
	                if (parentDirectory != null) {
	                    setPath(parentDirectory.toString());
	                }
	            } else {
	                setPath(fullPath.toString());
	            }
	        }
	    });
	}

	private void setPath(String path) {
	    Utils.debug("Setting path: " + path);

	    Minecraft.getMinecraft().addScheduledTask(() -> {
	        resultField.setText(path);
	    });
	}
	

	public void drawButtonForegroundLayer(int x, int y, boolean isHovered) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(buttonTextures);
		
        int i = this.getHoverState(isHovered);
		this.drawTexturedModalRect(x, y, 0, 46 + i * 20, this.width / 2, this.height);
        this.drawTexturedModalRect(x + this.width / 2, y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
	}
}
