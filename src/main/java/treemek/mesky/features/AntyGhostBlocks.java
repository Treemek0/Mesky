package treemek.mesky.features;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
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
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.Mesky;
import treemek.mesky.config.SettingsConfig;
import net.minecraftforge.fml.common.network.NetworkRegistry;


public class AntyGhostBlocks {
	private double[] lastPosition;
	private double[] prevLastPosition;
	private boolean isMoving;
	
	// yeah so if anyone would use it it would probably instaban you XDDD
	
//	@SubscribeEvent
//    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        // Check if this is running on the client side and if the entity is the local player
//        if (event.player.worldObj.isRemote && SettingsConfig.AntyGhostBlocks.isOn && event.phase == TickEvent.Phase.START) {
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
//		        prevLastPosition = lastPosition;
//		        lastPosition = currentPosition;
//		    }
//        }
//    }
//	
//	 private void checkSurroundingBlocks(double x, double y, double z) {
//	        BlockPos playerPos = new BlockPos(x, y, z);
//	        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
//
//	        if (lastPosition == null) return;
//
//	        int dx = (int) Math.signum(prevLastPosition[0] - player.posX);
//	        int dz = (int) Math.signum(prevLastPosition[2] - player.posZ);
//	        
//	        syncBlockWithServer(playerPos.add(dx, 0, 0));
//	        syncBlockWithServer(playerPos.add(dx, 1, 0));
//	        syncBlockWithServer(playerPos.add(dx, 2, 0));
//	        
//	        syncBlockWithServer(playerPos.add(0, 0, dz));
//        	syncBlockWithServer(playerPos.add(0, 1, dz));
//        	syncBlockWithServer(playerPos.add(0, 2, dz));
//	        
//        	syncBlockWithServer(playerPos.add(dx, 0, dz));
//        	syncBlockWithServer(playerPos.add(dx, 1, dz));
//        	syncBlockWithServer(playerPos.add(dx, 2, dz));
//	    }
//	
//	
//	
//	private void syncBlockWithServer(BlockPos pos) {
//		World world = Minecraft.getMinecraft().theWorld;
//        if (world.isRemote) {
//            // Request block update from server
//        	Block block = world.getBlockState(pos).getBlock();
//        	if (block != null && block == Blocks.air) {
//        		EnumFacing face = Minecraft.getMinecraft().objectMouseOver.sideHit;
//        		if(face == null) face = Minecraft.getMinecraft().thePlayer.getHorizontalFacing().getOpposite();
//        		Mesky.proxy.sendDiggingPacket(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, face);
//        	}
//        }
//    }
}
