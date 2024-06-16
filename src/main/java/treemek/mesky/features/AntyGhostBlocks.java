package treemek.mesky.features;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.config.SettingsConfig;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class AntyGhostBlocks {

	private double[] lastPosition;
	private boolean isMoving;

	
	// WORK IN PROGRESS (idk how hypixel could interpret it because i dont really know anything about packets so i dont wanna risk something thats server sided)

//	@SubscribeEvent
//    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        // Check if this is running on the client side and if the entity is the local player
//        if (event.player.worldObj.isRemote && SettingsConfig.AntyGhostBlocks && event.phase == TickEvent.Phase.START) {
//            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
//            
//            if(player != null) {
//		        double[] currentPosition = new double[] {player.posX, player.posY, player.posZ};
//		        if (lastPosition == null) {
//		            lastPosition = currentPosition;
//		            return;
//		        }
//		
//		       
//		        if (currentPosition[0] == lastPosition[0] && currentPosition[1] == lastPosition[1] && currentPosition[2] == lastPosition[2]) {
//		            if(isMoving == true) { // player stopped
//		            	isMoving = false;
//		            	checkSurroundingBlocks(player.posX, player.posY, player.posZ);
//		            }
//		        } else { // Player moving
//		            isMoving = true;
//		        }
//		
//		        lastPosition = currentPosition;
//		    }
//        }
//    }
//	
//	private void checkSurroundingBlocks(double x, double y, double z) {
//        World world = Minecraft.getMinecraft().theWorld;
//        BlockPos playerPos = new BlockPos(x, y, z);
//        
//        // Check blocks around the player
//        for (int dx = -1; dx <= 1; dx++) {
//            for (int dy = 0; dy <= 2; dy++) { // Checking from feet to head height
//                for (int dz = -1; dz <= 1; dz++) {
//                    BlockPos checkPos = playerPos.add(dx, dy, dz);
//                    syncBlockWithServer(world, checkPos);
//                }
//            }
//        }
//    }
//	
//	
//	private void syncBlockWithServer(World world, BlockPos pos) {
//        if (world.isRemote) {
//            // Request block update from server
//        	Block block = world.getBlockState(pos).getBlock();
//        	if (block != null && block == Blocks.air) {
//	        	Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
//        	}
//        }
//    }
}
