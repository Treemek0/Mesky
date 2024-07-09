package treemek.mesky.utils;

import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.pathfinder.WalkNodeProcessor;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;

public class PathfinderUtils {
	
	//public static final KeyBinding PathFindKey = new KeyBinding("Pathfinding", Keyboard.KEY_F9, "Mesky");
	
	PathEntity path;
	
	// idfk how this shit works
	
	public void MovePlayerTo(BlockPos pos) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if(player != null) {
			WalkNodeProcessor nodeProcessor = new WalkNodeProcessor();
	        nodeProcessor.setEnterDoors(true);
	        
	        PathFinder pathfinder = new PathFinder(nodeProcessor);
	        WorldClient worldObj = Minecraft.getMinecraft().theWorld;
			//	        EntityLiving fakeEntity = new EntitySlime(Minecraft.getMinecraft().theWorld);
//	        fakeEntity.setPosition(player.posX, player.posY, player.posZ);
	        int f = 2;
	        
            worldObj .theProfiler.startSection("pathfind");
            BlockPos blockpos = new BlockPos(player);
            int i = (int)(f + 8.0F);
            ChunkCache chunkcache = new ChunkCache(worldObj, blockpos.add(-i, -i, -i), blockpos.add(i, i, i), 0);
            path = pathfinder.createEntityPathTo(chunkcache, player, pos, f);
            worldObj.theProfiler.endSection();
            
//	        path = pathfinder.createEntityPathTo(Minecraft.getMinecraft().theWorld, player, pos, 0);
	        System.out.println(path);
	        
		}
		
	}
//	
//	@SubscribeEvent
//    public void onRenderTick(TickEvent.RenderTickEvent event) {
//        if (event.phase == TickEvent.Phase.END) {
//        	if(path != null) {
//        		updateMovement(path);
//        	}
//        }
//        
//        if(PathFindKey.isPressed()) {
//        	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
//        	BlockPos pos = new BlockPos(player.posZ + 2, player.posY, player.posX + 2);
//        	MovePlayerTo(pos);
//        }
//    }
	
	
	public void updateMovement(PathEntity path) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
        if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength()) {
            PathPoint nextPoint = path.getPathPointFromIndex(path.getCurrentPathIndex());

            // Calculate direction to the next waypoint
            double dx = nextPoint.xCoord - player.posX;
            double dy = nextPoint.yCoord - player.posY;
            double dz = nextPoint.zCoord - player.posZ;

            // Normalize direction
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            dx /= distance;
            dy /= distance;
            dz /= distance;

            // Move towards the next waypoint
            double speed = 0.1; // Adjust speed as needed
            player.motionX = dx * speed;
            player.motionY = dy * speed;
            player.motionZ = dz * speed;

            // Check if close enough to the waypoint to proceed to the next one
            double distToNext = player.getDistance(nextPoint.xCoord, nextPoint.yCoord, nextPoint.zCoord);
            if (distToNext < 0.5) {
                path.incrementPathIndex();;
            }
        } else {
            // Clear path and stop movement when path is completed or invalid
        	path = null;
            player.motionX = 0;
            player.motionY = 0;
            player.motionZ = 0;
        }
    }
	
	
}
