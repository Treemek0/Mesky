package treemek.mesky.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.ItemsHandler;
import treemek.mesky.handlers.RenderHandler;

public class InventoryButtons {
    static int xSize = 176;
    static int ySize = 166;
	
	public static class InventoryButton {
		public String itemString;
		public int imageBackgroundID;
		public String command;
		
		private transient ItemStack item;
		
		public transient boolean wasHovered = false;
		public transient long lastStartedHovering = 0;
		
		public InventoryButton(String item, int imageBackgroundID, String command) {
			this.itemString = item;
			this.imageBackgroundID = imageBackgroundID;
			this.command = command;
			updateItem();
		}
		
		public void updateItem() {
			item = getItem(this);
		}
		
		public void executeCommand() {
			Utils.executeCommand(command);
		}

		public boolean isHovered(Parameters p, int mouseX, int mouseY) {
			return mouseX > guiLeft + p.x && mouseX < guiLeft + p.x + p.width && mouseY > guiTop + p.y && mouseY < guiTop + p.y + p.height;
		}
		
		public boolean shouldRenderToolkit(boolean isHovered) {
			if(isHovered) {
				if(!wasHovered) {
					wasHovered = true;
					lastStartedHovering = System.currentTimeMillis();
				}else {
					if(System.currentTimeMillis() - lastStartedHovering > 1000) {
						return true;
					}
				}
			}else {
				wasHovered = false;
			}
			
			return false;
		}
	}
	
	public static Map<Integer, InventoryButton> buttons = new HashMap<>();
	public static int guiLeft;
	public static int guiTop;
	
	public static final int buttonSize = 18;
	
	public static class Parameters {
		public int x;
		public int y;
		public int width;
		public int height;
		
		public Parameters(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}
	
	public static Parameters getInventoryButtonPosition(int id) {
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        guiLeft = (sr.getScaledWidth() - xSize) / 2;
        guiTop = (sr.getScaledHeight() - ySize) / 2;
        
		if(id <= 8) {
			return new Parameters(7 + id * buttonSize, -buttonSize - 2, buttonSize, buttonSize); // above inv
		}
		
		if(id > 8 && id <= 12) {
			return new Parameters(-3 + (id-4) * buttonSize, 5, buttonSize, buttonSize); // above crafting slots
		}
		
		if(id > 12 && id <= 16) {
			return new Parameters(-3 + (id-8) * buttonSize, 8 + buttonSize*3, buttonSize, buttonSize); // under crafting slots
		}
		
		if(id > 16 && id <= 25) {
			return new Parameters(-buttonSize - 1, (id-17)*buttonSize, buttonSize, buttonSize); // left inv
		}
		
		if(id > 25 && id <= 34) {
			return new Parameters(xSize + 1, (id-26)*buttonSize, buttonSize, buttonSize); // right inv
		}
		
		if(id > 34 && id <= 43) {
			return new Parameters(7 + (id-35) * buttonSize, ySize + 1, buttonSize, buttonSize); // bottom inv
		}
		
		if(id > 43 && id <= 46) {
			return new Parameters(-guiLeft + (id-44) * buttonSize, -guiTop, buttonSize, buttonSize); // top-left screen
		}
		
		if(id > 46 && id <= 49) {
			return new Parameters(guiLeft + xSize - (id-46) * buttonSize, -guiTop, buttonSize, buttonSize); // top-right screen
		}
		
		if(id > 49 && id <= 52) {
			return new Parameters(-guiLeft + (id-50) * buttonSize, guiTop + ySize - buttonSize, buttonSize, buttonSize); // bottom-left screen
		}
		
		if(id > 52 && id <= 55) {
			return new Parameters(guiLeft + xSize - (id-52) * buttonSize, guiTop + ySize - buttonSize, buttonSize, buttonSize); // bottom-right screen
		}
		
		
		return new Parameters(0,0,0,0);
	}
	
	public static void drawBackground(int imageBackgroundID, Parameters p, boolean isHovered) {
		float subToColor = isHovered ? 0.3f : 0f;
		
		if(imageBackgroundID == 1) {
			GL11.glColor4f(1 - subToColor, 1 - subToColor, 1 - subToColor, 1f);
			ResourceLocation texture = new ResourceLocation(Reference.MODID, "gui/slot.png");
	        RenderHandler.drawImage(p.x, p.y, p.width, p.height, texture);
	        GL11.glColor4f(1, 1, 1, 1);
			return;
		}
		
		if(imageBackgroundID == 2) {
			GL11.glColor4f(0.5f - subToColor, 0.5f - subToColor, 0.5f - subToColor, 1f);
			ResourceLocation texture = new ResourceLocation(Reference.MODID, "gui/slot.png");
	        RenderHandler.drawImage(p.x, p.y, p.width, p.height, texture);
	        GL11.glColor4f(1, 1, 1, 1);
			return;
		}
		
		if(imageBackgroundID == 3) {
			GL11.glColor4f(1 - subToColor, 1 - subToColor, 1 - subToColor, 0.3f);
			GlStateManager.enableBlend();
			ResourceLocation texture = new ResourceLocation(Reference.MODID, "gui/slot.png");
	        RenderHandler.drawImage(p.x, p.y, p.width, p.height, texture);
	        GlStateManager.disableBlend();
	        GL11.glColor4f(1, 1, 1, 1);
			return;
		}
		
		if(imageBackgroundID == 4) {
			GL11.glColor4f(1 - subToColor, 1 - subToColor, 1 - subToColor, 1f);
			ResourceLocation texture = new ResourceLocation("textures/gui/container/inventory.png");
	        RenderHandler.drawImage(p.x, p.y, 7, 83, p.width, p.height, 256, 256, texture);
	        GL11.glColor4f(1, 1, 1, 1);
			return;
		}
		
		if(imageBackgroundID == 5) {
			RenderHandler.drawLine2D(p.x + 1, p.y + 1, p.x + p.width - 1, p.y + 1, 2, 0xFF000000); // top left->right
			RenderHandler.drawLine2D(p.x + 1, p.y + p.height - 1, p.x + p.width - 1, p.y + p.height - 1, 2, 0xFF000000); // right top->bottom
			RenderHandler.drawLine2D(p.x + 1, p.y + p.height - 1, p.x + 1, p.y + 1, 2, 0xFF000000); // bottom right->left
			RenderHandler.drawLine2D(p.x + p.width - 1, p.y + p.height - 1, p.x + p.width - 1, p.y + 1, 2, 0xFF000000); // left bottom->top
			return;
		}
	}
	
	public static ItemStack getItem(InventoryButton button) {
		String itemString = button.itemString;
		Integer color = null;
	    String owner = null;
	    String type = null;
		
		if(itemString.contains("[")) {
			int startIndex = itemString.indexOf("[");
			
			if(itemString.contains("]")) {
				int endIndex = itemString.indexOf("]");
				
				String attr = itemString.substring(startIndex+1, endIndex);
				if(attr.contains("hex=")) {
					try {
						String hex = attr.substring(attr.indexOf("hex=")+4);
						color = Integer.parseInt(hex, 16);
					} catch (Exception e) {}
				}else if (attr.contains("skin=")) {
		            owner = attr.substring(attr.indexOf("skin=") + 5);
		        }else if (attr.contains("type=")) {
		            type = attr.substring(attr.indexOf("type=") + 5);
		        }
			}
			
			itemString = itemString.substring(0, startIndex);
		}
		
		ResourceLocation rl = new ResourceLocation(itemString);
        Item item = Item.itemRegistry.getObject(rl);
        if(item == null) return null;
        ItemStack stack = new ItemStack(item);
        
        if(color != null) {
    		if(item instanceof ItemArmor && ((ItemArmor)item).getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
    			NBTTagCompound display = new NBTTagCompound();
    			display.setInteger("color", color);
    			stack.setTagInfo("display", display);
    		}else {
    			if(color != null && item instanceof ItemBlock) {
    			    Block block = ((ItemBlock)item).getBlock();
    			    
    			    if(block == Blocks.stained_glass || block == Blocks.stained_glass_pane || block instanceof BlockBanner || block == Blocks.wool || block == Blocks.carpet || block == Blocks.stained_hardened_clay) {
	    			    int meta = ColorUtils.getWoolMetaFromRGB(color);
	
	    			    if(block instanceof BlockBanner) meta = 15 - meta; // invert
	
	    			    stack.setItemDamage(meta);
    			    }
    			}
    		}
    	}
        
        if(type != null) {
        	if(item == Items.potionitem){
        		int meta = 0;
                
                try {
					int typeInt = Integer.parseInt(type);
					meta = typeInt;
				} catch (Exception e) {
					Integer typeInt = ItemsHandler.getPotionMetadata(type);
					if(typeInt != null) {
						meta = typeInt;
					}
				}
                
                stack.setItemDamage(meta);
        	}
        	
        	if(item == Items.spawn_egg) {
        		int meta = 0;
                
                try {
					int typeInt = Integer.parseInt(type);
					meta = typeInt;
				} catch (Exception e) {
					Integer typeInt = ItemsHandler.getSpawnEggType(type);
					if(typeInt != null) {
						meta = typeInt;
					}
				}
                
                stack.setItemDamage(meta);
        	}
        }
        
        if (owner != null && item == Items.skull) {
            stack.setItemDamage(3); // 3 = player head

            GameProfile profile = new GameProfile(null, owner);
            profile = TileEntitySkull.updateGameprofile(profile); // resolves skin once

            NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
            NBTTagCompound skullOwner = new NBTTagCompound();
            NBTUtil.writeGameProfile(skullOwner, profile);
            tag.setTag("SkullOwner", skullOwner);
            stack.setTagCompound(tag);
        }
        
        return stack;
	}

	public static void renderButton(InventoryButton invButton, Parameters p, boolean isHovered) {
		
		InventoryButtons.drawBackground(invButton.imageBackgroundID, p, isHovered);
        
        if(invButton.item == null) {
        	if(!invButton.itemString.contains("mesky:")) {
        		invButton.updateItem();
        	}
        }
        
        ItemStack item = invButton.item;
        if(item == null) {
        	if(ImageUploader.isCustomImage(invButton.itemString)) {
        		ResourceLocation texture = ImageUploader.getResourceLocation(invButton.itemString);
        		
        		if(texture != null) RenderHandler.drawImage(p.x + 2, p.y + 2, p.width - 4, p.height - 4, texture);
        	}
        }else {
        	int itemSize = 12;
        	
	        if(item.getItem() == Items.skull) {
	        	itemSize = 14;
	        }
	        
	        int x = p.x + (p.width - itemSize)/2;
	        int y = p.y + (p.width - itemSize)/2;
	        
	        GlStateManager.pushMatrix();
	        GlStateManager.enableDepth();
	        
	        GlStateManager.translate(x, y, 150);
	        GlStateManager.scale(itemSize/16f, itemSize/16f, 1f);
	        RenderHelper.enableGUIStandardItemLighting();
	        GlStateManager.depthMask(true);
	        
	        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item, 0, 0);
	        GlStateManager.disableDepth();
	        GlStateManager.depthMask(false);
	        RenderHelper.disableStandardItemLighting();
	        GlStateManager.disableRescaleNormal();
	        GlStateManager.disableDepth();
	        GlStateManager.popMatrix();
        }
	}
}
