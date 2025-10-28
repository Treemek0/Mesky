package treemek.mesky.features.illegal;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.utils.Utils;

public class GhostBlock {
	boolean hasBeenClicked = false;
	public static boolean canDestroyHardBlock = false;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
		if(SettingsConfig.GhostBlocks.isOn) {
	        if (SettingsConfig.GhostKeybind.keybind.isKeybindDown()) {
	        	if(!hasBeenClicked) {
	        		hasBeenClicked = true;
			        MovingObjectPosition object = Minecraft.getMinecraft().thePlayer.rayTrace(Minecraft.getMinecraft().playerController.getBlockReachDistance(), 1.0F);
			        if (object != null && object.getBlockPos() != null) {
			            Block lookingAtblock = Minecraft.getMinecraft().theWorld.getBlockState(object.getBlockPos()).getBlock();
			            if (!isInteractable(lookingAtblock) && lookingAtblock != Blocks.air) {
			            	if(canDestroyHardBlock) {
			            		Minecraft.getMinecraft().theWorld.setBlockToAir(object.getBlockPos());
			            	}else {
			            		if(!isHardBlock(lookingAtblock)) {
			            			Minecraft.getMinecraft().theWorld.setBlockToAir(object.getBlockPos());
			            		}
			            	}
			            }
			        }
	        	}
	        }else {
	        	hasBeenClicked = false;
	        }
		}

    }
	
	public static boolean isInteractable(Block block) {
        return (new ArrayList(Arrays.asList((Object[]) new Block[] { (Block) Blocks.chest, Blocks.lever, Blocks.trapped_chest, Blocks.wooden_button, Blocks.stone_button, (Block)Blocks.skull, Blocks.command_block }))).contains(block);
    }
	
	public static boolean isHardBlock(Block block) {
        return (new ArrayList(Arrays.asList((Object[]) new Block[] { (Block) Blocks.bedrock, Blocks.barrier, Blocks.sponge, Blocks.obsidian, Blocks.diamond_block, Blocks.gold_block, Blocks.iron_block}))).contains(block);
    }

	
}
