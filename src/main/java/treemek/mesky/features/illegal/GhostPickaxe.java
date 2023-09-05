package treemek.mesky.features.illegal;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.config.SettingsConfig;

public class GhostPickaxe {

	public static final KeyBinding GKEY = new KeyBinding("key.ghostPickaxe", Keyboard.KEY_G, "key.categories.mesky");
	public static int slotID = 5;
	boolean hasBeenClicked = false;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (GKEY.isPressed() && SettingsConfig.GhostPickaxe) {
            InventoryPlayer inventory = Minecraft.getMinecraft().thePlayer.inventory;
            for (int i = 0; i < inventory.mainInventory.length; i++) {
                ItemStack item = inventory.mainInventory[i];
                if (item != null && item.getItem() == Items.golden_pickaxe && i != slotID) {
                    System.out.println("Found golden pickaxe");
                    inventory.setInventorySlotContents(slotID, item.copy());
                    break; // Exit loop after transferring the pickaxe
                }
            }
        }
    }
	
	
	
}
