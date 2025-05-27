package treemek.mesky.utils;

import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.pathfinder.WalkNodeProcessor;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import treemek.mesky.handlers.RenderHandler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;

public class PathfinderUtils {
	private static List<BlockPos> drawingPath = new ArrayList<>();
	private static boolean isSearching = false;
	private static boolean linesDepth = false;
	public static Thread pathThread;
	
	// TOFIX: path cant go thru open doors, trapdoors, open fences, spider webs
	// it goes under blocks when path go up and then blocks himself :/
	// before it blocked himself on fence (it stayed on it and idk couldnt jump or something) - idk if its still a bug or fixed
	
	public static void killThreadIfAlive() {
		if(pathThread != null && pathThread.isAlive()) {
			pathThread.interrupt();
		}
	}
	
	
	public static void clearDrawingPath() {
		drawingPath.clear();
	}
	
	public static boolean isSearching() {
		return isSearching;
	}
	
	public static void drawPath(List<BlockPos> path, boolean depth) {
		if(path != null) {
			linesDepth = depth;
			drawingPath = path;
		}
	}
	
	public static void drawPathTo(BlockPos pos, boolean depth, boolean ignoreAirBlocks) {
		if(ignoreAirBlocks) {
			Utils.addMinecraftMessageWithPrefix("Looking for path: " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
		}else {
			Utils.addMinecraftMessageWithPrefix("Looking for flying path (can take a while): " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
		}
		
		if(pathThread != null && pathThread.isAlive()) {
			pathThread.interrupt();
		}
		
		pathThread = new Thread(() -> {
			linesDepth = depth;
			List<BlockPos> list = PathfinderUtils.getPathTo(pos, ignoreAirBlocks, true);
			
			if(list != null) {
				drawingPath = list;
			}
		});
		
		pathThread.start();
	}
	
	public static List<BlockPos> getPathTo(BlockPos target, boolean ignoreAir, boolean logInfo) {
		List<BlockPos> path = new ArrayList<>();
	    Map<BlockPos, Double> gScore = new HashMap<>();
	    Map<BlockPos, Double> fScore = new HashMap<>();
	    Set<BlockPos> closedSet = new HashSet<>();
	    Map<BlockPos, BlockPos> cameFrom = new HashMap<>();
	    Map<BlockPos, Vec3i> directionMap = new HashMap<>();
	    PriorityQueue<BlockPos> openSet = new PriorityQueue<>(Comparator.comparingDouble(pos -> fScore.getOrDefault(pos, Double.POSITIVE_INFINITY)));

	    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	    if (player == null) return null;

	    isSearching = true;
	    BlockPos start = new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ)).down();
	    BlockPos finalTarget = target;

	    while (isSolid(finalTarget.up())) {
	        finalTarget = finalTarget.up();
	        if (finalTarget.getY() > Minecraft.getMinecraft().theWorld.getHeight()) {
	            if (logInfo)
	                Utils.addMinecraftMessageWithPrefix("Can't find standable block near: " + finalTarget);
	            isSearching = false;
	            return null;
	        }
	    }

	    while (!isSolid(finalTarget) && ignoreAir) {
	        finalTarget = finalTarget.down();
	        if (finalTarget.getY() < 0) {
	            if (logInfo)
	                Utils.addMinecraftMessageWithPrefix("Can't find solid block at: " + finalTarget);
	            isSearching = false;
	            return null;
	        }
	    }

	    gScore.put(start, 0.0);
	    fScore.put(start, heuristic(start, finalTarget));
	    openSet.add(start);
	    directionMap.put(start, new Vec3i(0, 0, 0));

	    while (!openSet.isEmpty()) {
	        if (Thread.currentThread().isInterrupted()) {
	            isSearching = false;
	            return null;
	        }

	        BlockPos current = openSet.poll();
	        if (closedSet.contains(current)) continue;
	        if (current.equals(finalTarget)) {
	            isSearching = false;
	            return reconstructPath(current, cameFrom, logInfo);
	        }

	        closedSet.add(current);

	        for (BlockPos neighbor : getNeighbors(current, ignoreAir)) {
	            double heightPenalty = 0.0;
	            if (neighbor.getY() > current.getY()) heightPenalty += 0.25;
	            else if (neighbor.getY() < current.getY()) heightPenalty += 0.1;

	            Vec3i currentDir = directionMap.getOrDefault(current, new Vec3i(0, 0, 0));
	            Vec3i newDir = neighbor.subtract(current);

	            double directionChangePenalty = !currentDir.equals(newDir) ? 0.5 : 0;

	            double tentativeG = gScore.getOrDefault(current, Double.POSITIVE_INFINITY) +
	                    current.distanceSq(neighbor) + heightPenalty + directionChangePenalty;

	            if (tentativeG < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
	                cameFrom.put(neighbor, current);
	                gScore.put(neighbor, tentativeG);
	                fScore.put(neighbor, tentativeG + heuristic(neighbor, finalTarget));
	                directionMap.put(neighbor, newDir);
	                openSet.add(neighbor);
	            }
	        }
	    }

	    if (logInfo) Utils.addMinecraftMessageWithPrefix("Path not found");
	    isSearching = false;
	    return null;
    }
	
	private static double heuristic(BlockPos a, BlockPos b) {
	    // Using Manhattan distance as the heuristic
	    return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
	}

    private static List<BlockPos> getNeighbors(BlockPos current, boolean ignoreAirBlocks) {
        List<BlockPos> neighbors = new ArrayList<>();
        double jumpHeight = Utils.getJumpHeight();
        int blockHeight = (Utils.getBlockHeight(current) < 0.8)?1:0;
        
        int blocksRange = (ignoreAirBlocks)?2:1;
        int minY = (int) ((ignoreAirBlocks)?-jumpHeight - 2:-1);
        int maxY = (int) ((ignoreAirBlocks)?jumpHeight - blockHeight:1);
        
        for (int x = -blocksRange; x <= blocksRange; x++) {
            for (int z = -blocksRange; z <= blocksRange; z++) {
                for (int y = minY; y <= maxY; y++) {
                    if (x == 0 && z == 0) continue;
                    BlockPos lookedPosition = current.add(x, y, z);
                    
                    if(!isSolid(lookedPosition) && ignoreAirBlocks) {
						continue;
					}
                    
                    if(y > 0 && y - 1 + Utils.getBlockHeight(lookedPosition) > Utils.getJumpHeight()) {
                    	continue;
                    }
                    
                    if(isSolid(lookedPosition.add(0,1,0))) {
						continue;
					}
					if(isSolid(lookedPosition.add(0,2,0))) {
						continue;
					}
                    
                    if(x == -2 || x == 2 || z == -2 || z == 2) {
                    	if(isSolid(current.add(0, 2, Math.signum(z)))) {
                    		continue;
                    	}
                    	
                    	if(isSolid(current.add(Math.signum(x), 2, 0))) {
                    		continue;
                    	}
                    	
                    	if(isSolid(current.add(Math.signum(x), 2, Math.signum(z)))) {
                    		continue;
                    	}
                    	
                    	if(isSolid(current.add(0, 1, Math.signum(z)))) {
                    		continue;
                    	}
                    	
                    	if(isSolid(current.add(Math.signum(x), 1, 0))) {
                    		continue;
                    	}
                    	
                    	if(isSolid(current.add(Math.signum(x), 1, Math.signum(z)))) {
                    		continue;
                    	}
                    }
                    
                    if(x != 0 && z != 0 && y <= 0) {
                    	boolean isBlocked = false;
                    	for (int i = 0; i <= -y + 1; i++) {
	                    	if(isSolid(lookedPosition.add(-Math.signum(x), y+1+i, 0)) && isSolid(lookedPosition.add(0, y+1+i, -Math.signum(z)))) {
								isBlocked = true; // if moving diagnally and blocks are in a way then dont count
							}
						}
						
						if(isBlocked) {
							continue;
						}
					}
					
					if(y < 0) {
						boolean isBlocked = false;
						for (int i = 3; i < 3 - y; i++) {
							if(isSolid(lookedPosition.add(0,i,0))) { // i need to test if moving diagnolly then if > 3 blocks above lookedPosition is air then it can be counted
								isBlocked = true;
							}
						}
						
						if(isBlocked) {
							continue;
						}
					}
					
					if(y > 0) {
						boolean isBlocked = false;
						
						for (int i = 1; i < 1+y; i++) {
							if(isSolid(lookedPosition.add(0,2+i,0))){
								isBlocked = true;
							}
							
							if(isSolid(current.add(0,2+i,0))){
								isBlocked = true;
							}
							
							if(isSolid(current.add(Math.signum(x),2+i, 0))){
								isBlocked = true;
							}
							
							if(isSolid(current.add(0 ,2+i, (Math.signum(z))))){
								isBlocked = true;
							}
							
							if(x == 2 || z == 2) {
								if(isSolid(current.add(Math.signum(x),2+i,Math.signum(z)))){
									isBlocked = true;
								}
							}
						}
						
						if(isBlocked) {
							continue;
						}
					}
					
                	neighbors.add(lookedPosition);
                }
            }
        }
        return neighbors;
    }

    private static List<BlockPos> reconstructPath(BlockPos current, Map<BlockPos, BlockPos> cameFrom, boolean logInfo) {
        List<BlockPos> totalPath = new ArrayList<>();
        totalPath.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(current);
        }
        Collections.reverse(totalPath);
        BlockPos endPoint = totalPath.get(totalPath.size()-1);
        BlockPos startPoint = totalPath.get(0);
        if(logInfo)	Utils.addMinecraftMessageWithPrefix("Found path for: " + endPoint.getX() + " " + (endPoint.getY()+1) + " " + endPoint.getZ() + ", from: " + startPoint.getX() + " " + (startPoint.getY()+1) + " " + startPoint.getZ());
        drawingPath = totalPath;
        isSearching = false;
        return totalPath;
    }
	
    private static boolean isSolid(BlockPos p) {
		IBlockState state = Utils.getBlockState(p);
		Block block = state.getBlock();
		if(block instanceof BlockSkull) return true;
		if(block == Blocks.standing_banner || block == Blocks.wall_banner || block == Blocks.standing_sign || block == Blocks.wall_sign || block == Blocks.heavy_weighted_pressure_plate || block == Blocks.light_weighted_pressure_plate || block == Blocks.stone_pressure_plate || block == Blocks.wooden_pressure_plate) return false;
		if(block == Blocks.snow_layer && state.getValue(BlockSnow.LAYERS) > 2) return true;
		if(block == Blocks.glass_pane || block == Blocks.stained_glass_pane) return true;
		return block.getMaterial().isSolid();
	}
	
	@SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
		float red = 255;
		float blue = 0;
		
		for (int i = 0; i < drawingPath.size(); i++) {
			red = Math.max(0, red - 255/drawingPath.size());
			blue = Math.min(255, blue + 255/drawingPath.size());
			int color = new Color(red/255f, 0f, blue/255f).getRGB();
			
    		BlockPos current = drawingPath.get(i);
    		BlockPos next = (i+1<drawingPath.size())?drawingPath.get(i+1):current;
    		RenderHandler.drawLine(current.getX()+0.5f, current.getY()+1.5, current.getZ()+0.5f, next.getX()+0.5f, next.getY()+1.5, next.getZ()+0.5f, color, linesDepth, event.partialTicks);
		}
		
		if(!drawingPath.isEmpty() && !isSearching) {
			BlockPos pos = drawingPath.get(drawingPath.size()-1);
			if(Minecraft.getMinecraft().thePlayer.getPosition().distanceSq(pos.getX()+0.5f, pos.getY()+1, pos.getZ()+0.5f) < 1){
				drawingPath.clear();
			}
		}
	}
	
	@SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
    	if (event.world.isRemote && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (player == Minecraft.getMinecraft().thePlayer) {
            	killThreadIfAlive();
            	
            	drawingPath.clear();
            }
        }
    }
    
    @SubscribeEvent
    public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
    	killThreadIfAlive();
    	
    	drawingPath.clear();
    }
	
}
