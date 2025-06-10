package treemek.mesky.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.lwjgl.input.Keyboard;

import ibxm.Player;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import treemek.mesky.Mesky;

public class MovementUtils {
	@FunctionalInterface
	public interface MovementFunction {
	    void execute(BlockPos pos);
	}
	
	@FunctionalInterface
	public interface Task {
	    void execute();
	}
	
	public static class Movement {
		private BlockPos finalPos;
		private double[] finalRotation;
	    private MovementFunction function;
	    private Task task;
	    private long miliDelay;
	    private Long startTime = null; // Time when the function was scheduled
	    private boolean functionExecuted; // Track if the function has executed

	    public Movement(BlockPos finalBlock, MovementFunction function, long miliDelay) {
	        this.finalPos = finalBlock;
	        this.function = function;
	        this.miliDelay = miliDelay;
	        this.functionExecuted = false; // Reset state for each new instance
	    }
	    
	    public Movement(Task task, long miliDelay) {
	        this.task = task;
	        this.miliDelay = miliDelay;
	        this.functionExecuted = false; // Reset state for each new instance
	    }
	    
	    public Movement(Task task, long miliDelay, double finalYaw, double finalPitch) {
	        this.task = task;
	        this.miliDelay = miliDelay;
	        this.finalRotation = new double[] { finalYaw, finalPitch };
	        this.functionExecuted = false; // Reset state for each new instance
	    }

	    public boolean isNearEnd() {
	        if (Minecraft.getMinecraft().thePlayer == null) return false;

	        if(finalPos != null){
	        	return Minecraft.getMinecraft().thePlayer.getDistance(finalPos.getX() + 0.5, finalPos.getY() + Utils.getBlockHeight(finalPos), finalPos.getZ() + 0.5) < 0.6f;
	        }
	        
	        if(finalRotation != null){
	        	return Math.abs(Utils.normalizeAngle(Minecraft.getMinecraft().thePlayer.rotationYaw) - Utils.normalizeAngle((float) finalRotation[0])) < 0.1f && Math.abs(Utils.clampPitch(Minecraft.getMinecraft().thePlayer.rotationPitch) - Utils.clampPitch((float) finalRotation[1])) < 0.1f;
	        }
	        
	        return true;
	    }


	    public void executeFunction() {
	    	if(startTime == null) startTime = System.currentTimeMillis();
	    	
	        if (!functionExecuted && System.currentTimeMillis() - startTime >= miliDelay) {
	            if (function != null) {
	                function.execute(finalPos);
	                functionExecuted = true; // Mark as executed
	            }
	            
	            if (task != null) {
	                task.execute();
	                functionExecuted = true; // Mark as executed
	            }
	        }
	    }

	    public boolean wasFunctionExecuted() {
	        return functionExecuted;
	    }
	}
	
	private static List<BlockPos> movingPath = new ArrayList<>();
	private static Queue<Movement> movementQueue = new LinkedList<>();
	private static Queue<Movement> miniMovementQueue = new LinkedList<>();
	private static boolean useRotation = false;
	private static int currentPosIndex = -1;
	static int rotationIndex = 0;
	float rotationYaw = 0f;
	
	public static int rightClick = Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode();
	public static KeyBinding leftClick = Minecraft.getMinecraft().gameSettings.keyBindAttack;
	static int shift = Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode();
	int right = Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode();
	int left = Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode();
	int back = Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode();
	int forward = Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode();
	int jump = Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode();
	
	public static void addMovement(Movement movement) {
		movementQueue.add(movement);
	}
	
	public static void addMiniMovement(Movement movement) {
		miniMovementQueue.add(movement); // its for multimovement like when you use multiple aotv functions on movementQueue and have to use another queue to make aotv movement happen (make it list in future)
	}
	
	public static void resetMovementsList() {
		movementQueue.clear();
	}
	
	// its getting stuck when block that it wants to jump to also has no blocks under it (y-1), it goes under it and dupa
	BlockPos sprintingStartedBlock = null;
	
	static int ticksSneaking = 0;
	static int ticksWithoutMoving = 0;
	float[] lastPos;
	@SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
        	if(currentPosIndex != -1) {
        		if(Minecraft.getMinecraft().thePlayer == null) return;
        		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        		
        		if(player.isSneaking()) {
        			ticksSneaking++;
        			if(ticksSneaking >= 10) {
        				KeyBinding.setKeyBindState(shift, false);
        			}
        		}else {
        			ticksSneaking = 0;
        		}

        		if(sprintingStartedBlock != null && player.onGround && Utils.playerPosition().distanceSq(sprintingStartedBlock) > 0.0f) {
        			Utils.debug("stopped sprinting ");
        			KeyBinding.unPressAllKeys();
        			KeyBinding.setKeyBindState(shift, true);
        			Minecraft.getMinecraft().thePlayer.setSprinting(false);
        			sprintingStartedBlock = null;
        		}
        		
		        if (currentPosIndex >= movingPath.size()) {
		        	movingPath.clear();
		        	ticksWithoutMoving = 0;
		        	currentPosIndex = -1;
		        	KeyBinding.unPressAllKeys();
		            return; // Path completed
		        }
		        
		        int closest = currentPosIndex;
		        Double distance = Double.POSITIVE_INFINITY;
		        for (int i = currentPosIndex; i < movingPath.size(); i++) {
		        	BlockPos pos = movingPath.get(i);
		        	
		        	Double dist = Minecraft.getMinecraft().thePlayer.getDistance(pos.getX()+0.5, pos.getY()+Utils.getBlockHeight(pos), pos.getZ()+0.5);
		        	
			        if(dist < distance) {
			        	if(Utils.getRaycastToBlock(pos, false) != null) {
			        		closest = i;
			        		distance = dist;
			        	}
			        }
		        }
		        
		        BlockPos targetPos = movingPath.get(closest).add(0,1,0);
		        BlockPos oldPos = (closest>0)?movingPath.get(closest-1).add(0,1,0):targetPos;
		        movePlayerInDirection(oldPos, targetPos);
		        
		        if(Mesky.debug) {
			        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
						public void run() {
							Waypoints.addTemporaryWaypoint("target", "000000", targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1, 50);
						}
			        });
		        }
		        
		        float[] currentPos = Utils.precisePlayerPosition();
		        
		        if(lastPos != null) {
		        	float d = Utils.distance(currentPos[0], 0, currentPos[2], lastPos[0], 0, lastPos[2]);
		        	float h = Math.abs(currentPos[1] - lastPos[1]);

		        	float g = (player.isInLava() || player.isInWater())?0.005f:0.03f;
		        	
		        	if(d < g && h <= 0.5f && player.onGround) {
		        		if((Utils.getYawDifference(rotationYaw, player.rotationYaw) < 2.5 || !useRotation) && !player.isSneaking()){
		        			ticksWithoutMoving++;
		        		}
		        	}else {
		        		ticksWithoutMoving = 0;
		        	}
		        }
		        
		        lastPos = currentPos;
		        
		        if(ticksWithoutMoving >= 100) {
		        	Utils.debug("Creating new path: 100tick wait");
		        	BlockPos finalBlock = movingPath.get(movingPath.size()-1);
		        	movingPath.clear();
		        	ticksWithoutMoving = 0;
		        	currentPosIndex = -1;
		        	KeyBinding.unPressAllKeys();
		        	quietlyMovePlayerTo(finalBlock);
		            return; // path reset
		        }
		        
		        if(ticksWithoutMoving == 60 || ticksWithoutMoving == 90) {
		        	Utils.debug("Adding +1 to current, ticks: " + ticksWithoutMoving);
		        	if(currentPosIndex + 1 < movingPath.size()-1) {
		        		currentPosIndex++;
		        	}else {
		        		BlockPos finalBlock = movingPath.get(movingPath.size()-1);
			        	movingPath.clear();
			        	ticksWithoutMoving = 0;
			        	currentPosIndex = -1;
			        	KeyBinding.unPressAllKeys();
			        	quietlyMovePlayerTo(finalBlock);
		        	}
		        	return;
		        }
		        
		        if(useRotation) {
		        	BlockPos lookAt;
		        	BlockPos playerPos = Utils.playerPosition();
		        	if(closest+1 < movingPath.size() && Utils.distance(movingPath.get(closest+1).getX(), 0, movingPath.get(closest+1).getZ(), playerPos.getX(), 0, playerPos.getZ()) < 2f) {
		        		lookAt = movingPath.get(closest+1);
		        	}else {
		        		lookAt = targetPos;
		        	}
	            	double lookAtY = (player.onGround)?player.getEyeHeight():player.getEyeHeight()+(player.posY - targetPos.getY());
		            float[] rotation = RotationUtils.getPlayerRotationToLookAtVector(lookAt.getX()+0.5, lookAt.getY() + lookAtY, lookAt.getZ()+0.5f);
		            
		            float targetYaw = rotation[0];
		            rotationYaw = targetYaw;
		            float targetPitch = rotation[1];

		            // Normalize yaw difference to -180 to 180
		            float yawDiff = MathHelper.wrapAngleTo180_float(targetYaw - player.rotationYaw);
		            float pitchDiff = targetPitch - player.rotationPitch;

		            // Apply easing factor (smaller = slower, smoother)
		            float yawStep = yawDiff * 0.1f;
		            float pitchStep = pitchDiff * 0.1f;

		            // Apply rotation gradually
		            RotationUtils.addToRotation(yawStep, pitchStep);
		        }
		        
		        if(targetPos.getY() - player.posY > Utils.getJumpHeight() || player.posY - targetPos.getY() < -(Utils.getJumpHeight() + 2) && player.onGround) {
		        	Utils.debug("Creating new path: too much difference with Y");
		        	BlockPos finalBlock = movingPath.get(movingPath.size()-1);
		        	KeyBinding.unPressAllKeys();
		        	quietlyMovePlayerTo(finalBlock);
		            return; // path reset
		        }
		        
		        if(Math.abs(targetPos.getX() - player.posX) > 3 || Math.abs(targetPos.getZ() - player.posZ) > 3) {
		        	Utils.debug("Creating new path: too much difference with XZ: x" + Math.abs(targetPos.getX() - player.posX) + ", z" + Math.abs(targetPos.getZ() - player.posZ));
		        	BlockPos finalBlock = movingPath.get(movingPath.size()-1);
		        	quietlyMovePlayerTo(finalBlock);
		            return; // path reset
		        }
		        
		        if (distance < 0.7f && closest < movingPath.size()-1) {
		            currentPosIndex = closest+1;
		        }
		        
		        if (distance < 0.4f && closest >= movingPath.size()-1) {
		        	currentPosIndex = -1;
		        	movingPath.clear();
		        	ticksWithoutMoving = 0;
	            	KeyBinding.unPressAllKeys();
    	            return; // Path completed
		        }
	        }
        }
        
        if (!movementQueue.isEmpty()) {
            Movement movement = movementQueue.peek();
            if (!movement.wasFunctionExecuted()) {
                movement.executeFunction(); 
            } else if (movement.isNearEnd()) {
                movementQueue.poll();
            }
        }
        
        if (!miniMovementQueue.isEmpty()) {
            Movement movement = miniMovementQueue.peek();
            if (!movement.wasFunctionExecuted()) {
                movement.executeFunction(); 
            } else if (movement.isNearEnd()) {
                miniMovementQueue.poll();
            }
        }
    }

	private void movePlayerInDirection(BlockPos oldPos, BlockPos targetPos) {
	    if (Minecraft.getMinecraft().thePlayer == null) return;
	    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	    
	    // Calculate difference in global space
	    double targetCenterX = targetPos.getX() + 0.5f;
	    double targetCenterZ = targetPos.getZ() + 0.5f;

	    double diffX = targetCenterX - player.posX;
	    double diffY = targetPos.getY() - player.posY;
	    double diffZ = targetCenterZ - player.posZ;

	    // Convert to local space (relative to player's yaw)
	    double yaw = Math.toRadians(player.rotationYaw);
	    double sinYaw = Math.sin(yaw);
	    double cosYaw = Math.cos(yaw);

	    // Rotate the difference to get the local direction
	    double localX = diffX * cosYaw + diffZ * sinYaw;
	    double localZ = diffZ * cosYaw - diffX * sinYaw;

	 // Estimate horizontal speed (including Speed effect, sprinting, etc.)
	    double velocity = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);

	    // Add a small constant to cover deceleration time
	    double stoppingDistance = velocity * 4.0 + 0.1; // tweak multiplier as needed

	    // Dead zones adapt to current speed (higher speed = earlier key release)
	    double forwardDeadZone = Math.max(0.03, stoppingDistance);  // normal: ~0.1
	    double lateralDeadZone = Math.max(0.02, stoppingDistance * 0.6); // normal: ~0.05

	    // Initialize movement flags
	    boolean moveForward = false;
	    boolean moveBackward = false;
	    boolean moveLeft = false;
	    boolean moveRight = false;

	    // Prioritize forward/backward movement towards the center of the block
	    if (localZ > forwardDeadZone) {
	        moveForward = true;
	    } else if (localZ < -forwardDeadZone) {
	        moveBackward = true;
	    }

	    // Only move laterally if the difference is significant
	    if (Math.abs(localZ) < Math.abs(localX)) {
	        if (localX > lateralDeadZone) {
	            moveLeft = true;
	        } else if (localX < -lateralDeadZone) {
	            moveRight = true;
	        }
	    }
	    
	    if(!moveBackward && !moveForward && !moveLeft && !moveRight) {
	    	moveForward = true;
	    }

	    boolean isGap = isAirGapBetween(oldPos, targetPos);
	    
	    // Apply combined movement to prevent getting stuck at diagonal walls
	    if(!isGap || (isGap && targetPos.getY() - oldPos.getY() <= 3)) {
		    if (moveForward && moveLeft) {
		    	if(player.moveForward != 1) KeyBinding.setKeyBindState(forward, true);
		        KeyBinding.setKeyBindState(left, true);
		    } else if (moveForward && moveRight) {
		    	if(player.moveForward != 1) KeyBinding.setKeyBindState(forward, true);
		        KeyBinding.setKeyBindState(right, true);
		    } else if (moveBackward && moveLeft) {
		        KeyBinding.setKeyBindState(back, true);
		        KeyBinding.setKeyBindState(left, true);
		    } else if (moveBackward && moveRight) {
		        KeyBinding.setKeyBindState(back, true);
		        KeyBinding.setKeyBindState(right, true);
		    } else {
		        // If no diagonal movement is required, move in the prioritized direction
		        KeyBinding.setKeyBindState(forward, moveForward);
		        KeyBinding.setKeyBindState(back, moveBackward);
		        KeyBinding.setKeyBindState(left, moveLeft);
		        KeyBinding.setKeyBindState(right, moveRight);
		    }
	    }

	    // Ensure lateral movement is disabled if not needed
	    if (!moveLeft && !moveRight) {
	        KeyBinding.setKeyBindState(left, false);
	        KeyBinding.setKeyBindState(right, false);
	    }
	    
	    // jumping
	    if (isGap && player.onGround) {
    		if((Utils.getYawDifference(rotationYaw, player.rotationYaw) < 2.5 || !useRotation) && !player.isSneaking()) {
    			Utils.debug(isOnEdge(targetPos) + " " + !player.isSneaking() + " " + player.onGround);
		    	if(isOnEdge(targetPos) && !player.isSneaking() && player.onGround) {
		    		if(targetPos.getY() - oldPos.getY() > 0 || player.getDistanceSq(targetPos) > 1.4f) {
		    			Minecraft.getMinecraft().thePlayer.setSprinting(true);
		    			sprintingStartedBlock = Utils.playerPosition();
		    		}
		        	player.jump();
		        	Utils.debug("jump1 " + rotationYaw + " " + player.rotationYaw);
		    	}
    		}else {
	    		Utils.debug("stopped moving for rotation before jump: " + rotationYaw + " " + player.rotationYaw);
	    		if(!RotationUtils.isPlayerRotating()) RotationUtils.rotateStraight(RotationUtils.getNeededYawFromMinecraftRotation(rotationYaw), 0, 0.25f, false);
	    		KeyBinding.setKeyBindState(forward, false);
		        KeyBinding.setKeyBindState(back, false);
		        KeyBinding.setKeyBindState(left, false);
		        KeyBinding.setKeyBindState(right, false);
	    	}
	    }else {
	    	KeyBinding.setKeyBindState(jump, false);
	    	if(!player.isInWater() && !player.isInLava()) {
		    	if (diffY > 0 && player.onGround && shouldJump(targetPos) && isOnEdge(targetPos) && isTargetLowEnough(targetPos.add(0,-1,0))) {
		    		player.jump();
		    		Utils.debug("jump2");
			    } else {
			        KeyBinding.setKeyBindState(jump, false);
			    }
	    	}else {
	    		if (diffY > 0 && shouldJump(targetPos) && isOnEdge(targetPos)) {
	    			KeyBinding.setKeyBindState(jump, true);
		    		Utils.debug("jump3");
			    } else {
			        KeyBinding.setKeyBindState(jump, false);
			    }
	    	}
	    }
	}
	
	private boolean isTargetLowEnough(BlockPos pos) {
		double a = pos.getY() + Utils.getBlockHeight(pos) - Minecraft.getMinecraft().thePlayer.posY;
		Utils.debug(a + " " + Utils.getJumpHeight());
		if(a > Utils.getJumpHeight()) return false;
		return true;
	}

	private boolean isOnEdge(BlockPos targetPos) {
	    double edgeThreshold = 0.40; // How close to the edge the player needs to be to trigger the jump

	    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

	    // Get the player's position on the X and Z axes
	    double playerPosX = player.posX;
	    double playerPosZ = player.posZ;

	    // Get the block center position
	    double blockPosX = Math.floor(playerPosX) + 0.5;
	    double blockPosZ = Math.floor(playerPosZ) + 0.5;

	    // Calculate the distance from the player to the center of the block
	    double distanceX = Math.abs(playerPosX - blockPosX);
	    double distanceZ = Math.abs(playerPosZ - blockPosZ);

	    // Calculate the target direction relative to the player
	    double targetDirectionX = targetPos.getX() + 0.5 - playerPosX;
	    double targetDirectionZ = targetPos.getZ() + 0.5 - playerPosZ;

	    // Determine if the player is moving more along the X or Z axis
	    boolean checkX = Math.abs(targetDirectionX) > Math.abs(targetDirectionZ);
	    
	    // Check if the player is near the edge closest to the target block
	    if (checkX) {
	        return distanceX > (0.5 - edgeThreshold);
	    } else {
	        return distanceZ > (0.5 - edgeThreshold);
	    }
	}
	
	private boolean shouldJump(BlockPos pos) {
		BlockPos p = new BlockPos(Math.floor(Minecraft.getMinecraft().thePlayer.posX), Math.floor(Minecraft.getMinecraft().thePlayer.posY), Math.floor(Minecraft.getMinecraft().thePlayer.posZ));
		pos = p.add(Math.signum(pos.getX() - p.getX()), pos.getY()-1 - p.getY(), Math.signum(pos.getZ() - p.getZ()));
		Block block = Utils.getBlockState(pos).getBlock();
		if((pos.getY() + Utils.getBlockHeight(pos)) - Minecraft.getMinecraft().thePlayer.posY <= 0.5) return false;
		if(block == Blocks.stone_slab) return false;
		if(block == Blocks.stone_slab2) return false;
		if(block == Blocks.wooden_slab) return false;
		if(block == Blocks.stone_stairs) return false;
		if(block == Blocks.red_sandstone_stairs) return false;
		if(block == Blocks.acacia_stairs) return false;
		if(block == Blocks.birch_stairs) return false;
		if(block == Blocks.brick_stairs) return false;
		if(block == Blocks.dark_oak_stairs) return false;
		if(block == Blocks.jungle_stairs) return false;
		if(block == Blocks.nether_brick_stairs) return false;
		if(block == Blocks.oak_stairs) return false;
		if(block == Blocks.quartz_stairs) return false;
		if(block == Blocks.sandstone_stairs) return false;
		if(block == Blocks.spruce_stairs) return false;
		if(block == Blocks.stone_brick_stairs) return false;
		
		return true;
	}

	private boolean isAirGapBetween(BlockPos oldPos, BlockPos targetPos) {
	    double distance = Math.sqrt(oldPos.distanceSq(targetPos));

	    if (distance >= 1.5) {
	        int x = targetPos.getX() - oldPos.getX();
	        int z = targetPos.getZ() - oldPos.getZ();
	        int y = targetPos.getY() - oldPos.getY();

	        // No need to jump if the target is lower
	        if(y == -1 && Math.abs(x) >= 2 && Math.abs(z) >= 2) return true; 
	        if (y < 0) return false;

	        // Check if there's an air gap along the path
	        for (int i = 1; i <= Math.max(Math.abs(x), Math.abs(z)); i++) {
	            int stepX = x == 0 ? 0 : i * Integer.signum(x);
	            int stepZ = z == 0 ? 0 : i * Integer.signum(z);
	            BlockPos checkPos = oldPos.add(stepX, 0, stepZ);

	            // Check the block at the current level and the level below
	            boolean isAir = Minecraft.getMinecraft().theWorld.isAirBlock(checkPos);
	            boolean isAirBelow = Minecraft.getMinecraft().theWorld.isAirBlock(checkPos.down());

	            if (isAir && isAirBelow) {
	                return true; // There's an air gap that would require a jump
	            }
	        }
	    }

	    return false;
	}
	
	public static void movePlayerToWithoutRotation(BlockPos pos) {
		PathfinderUtils.killThreadIfAlive();
		
		PathfinderUtils.pathThread = new Thread(() -> {
			List<BlockPos> blockPath = PathfinderUtils.getPathTo(pos, true, true);
			if(blockPath == null)  blockPath = new ArrayList<>();
			useRotation = false;
			Utils.addMinecraftMessageWithPrefix("[WIP] If there's problem with moving, use " + EnumChatFormatting.GOLD + "/mesky stopmoving");
			PathfinderUtils.drawPath(blockPath, true);
			currentPosIndex = 0;
			ticksWithoutMoving = 0;
			movingPath = new ArrayList<BlockPos>(blockPath);
			rotationIndex = 4;
		});
		
		PathfinderUtils.pathThread.start();
	}
	
	public static void movePlayerTo(BlockPos pos) {
		PathfinderUtils.killThreadIfAlive();
		
		PathfinderUtils.pathThread = new Thread(() -> {
			List<BlockPos> blockPath = PathfinderUtils.getPathTo(pos, true, true);
			if(blockPath == null)  blockPath = new ArrayList<>();
			useRotation = true;
			Utils.addMinecraftMessageWithPrefix("[WIP] If there's problem with moving, use " + EnumChatFormatting.GOLD + "/mesky stopmoving");
			PathfinderUtils.drawPath(blockPath, true);
			currentPosIndex = 0;
			ticksWithoutMoving = 0;
			movingPath = new ArrayList<BlockPos>(blockPath);
			rotationIndex = 4;
		});
		
		PathfinderUtils.pathThread.start();
	}
	
	// without logging to chat
	public static void quietlyMovePlayerToWithoutRotation(BlockPos pos) {
		PathfinderUtils.killThreadIfAlive();
		
		PathfinderUtils.pathThread = new Thread(() -> {
			List<BlockPos> blockPath = PathfinderUtils.getPathTo(pos, true, false);
			if(blockPath == null)  blockPath = new ArrayList<>();
			useRotation = false;
			PathfinderUtils.drawPath(blockPath, true);
			currentPosIndex = 0;
			ticksWithoutMoving = 0;
			movingPath = new ArrayList<BlockPos>(blockPath);
			rotationIndex = 4;
		});
		
		PathfinderUtils.pathThread.start();
	}
	
	// without logging to chat
	public static void quietlyMovePlayerTo(BlockPos pos) {
		PathfinderUtils.killThreadIfAlive();
		
		PathfinderUtils.pathThread = new Thread(() -> {
			List<BlockPos> blockPath = PathfinderUtils.getPathTo(pos, true, false);
			if(blockPath == null)  blockPath = new ArrayList<>();
			useRotation = true;
			PathfinderUtils.drawPath(blockPath, true);
			currentPosIndex = 0;
			ticksWithoutMoving = 0;
			movingPath = new ArrayList<BlockPos>(blockPath);
			rotationIndex = 4;
		});
		
		PathfinderUtils.pathThread.start();
	}
	
	public static void stopMoving() {
		if(!movingPath.isEmpty()) {
			PathfinderUtils.clearDrawingPath();
			movingPath.clear();
			KeyBinding.unPressAllKeys();
		}
		
		MacroWaypoints.MacroActive = null;
		
		KeyBinding.unPressAllKeys();
		Minecraft.getMinecraft().thePlayer.moveForward = 0;
		Minecraft.getMinecraft().thePlayer.moveStrafing = 0;
		miniMovementQueue.clear();
		movementQueue.clear();
		rotationIndex = 0;
		currentPosIndex = -1;
	}

	public static boolean isCurrentlyMoving() {
		return !movingPath.isEmpty();
	}
	
	public static boolean isPlayerAuto() {
		return !movementQueue.isEmpty() || !miniMovementQueue.isEmpty();
	}
	
	public static boolean needShifting = false;
	public static void useAOTVto(BlockPos pos) {
		if(Minecraft.getMinecraft().thePlayer == null) return;
		InventoryPlayer inventory = Minecraft.getMinecraft().thePlayer.inventory;
		
		for (int i = 0; i < 9; i++) {
            ItemStack item = inventory.mainInventory[i];
            if (item != null && item.getItem() == Items.diamond_shovel) {
            	List<String> itemLore = Utils.getItemLore(item);
            	
            	if(itemLore == null || itemLore.size() == 0) return; 
            	if(!Utils.containsWord(itemLore, "Ether Transmission")) return;
            	
            	Minecraft.getMinecraft().thePlayer.inventory.currentItem = i; // eq aotv 
            	
            	double[] raycast = Utils.getRaycastToBlock(pos, true);
        		
        		if(raycast == null) {
        			Utils.addMinecraftMessageWithPrefix("Block not visible, can't AOTV: " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
        			return;
        		}
            	
            	RotationUtils.clearAllRotations();
            	float[] rotation = RotationUtils.getPlayerRotationToLookAtVectorWithCrouching(raycast[0], raycast[1], raycast[2]);
            	addMiniMovement(new Movement(() -> {
            		RotationUtils.rotateStraight(RotationUtils.getNeededYawFromMinecraftRotation(rotation[0]), RotationUtils.getNeededPitchFromMinecraftRotation(rotation[1]), 0.1f, false);
            	}, 0, rotation[0], rotation[1]));
            	
            	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            	
            	addMiniMovement(new Movement(() -> {
            		KeyBinding.setKeyBindState(shift, true);
            	}, 0));
            			
            	addMiniMovement(new Movement(() -> {
            		KeyBinding.onTick(rightClick);
            	}, 500));	
            	
            	addMiniMovement(new Movement(() -> {
            		if(Utils.distance(player.posX, player.posY, player.posZ, pos.getX(), pos.getY(), pos.getZ()) > 2) {
						KeyBinding.setKeyBindState(shift, true);

						KeyBinding.onTick(rightClick);

						KeyBinding.setKeyBindState(shift, false);
					}else {
						KeyBinding.setKeyBindState(shift, false);
					}
            	}, 500));	
            }
		}
	}
	
	@SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
		if(!isPlayerAuto() && !isCurrentlyMoving() && MacroWaypoints.MacroActive == null) return;
		if(MacroWaypoints.MacroActive != null && MacroWaypoints.MacroActive.boundingBox.intersectsWith(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox())) return;
        if (Keyboard.getEventKeyState()) { // Check if key is pressed (true when pressed, false when released)
            int key = Keyboard.getEventKey();

            if (key == forward || key == back || key == left || key == right) {
                stopMoving();
            }
        }
    }
	
	@SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
    	if (event.world.isRemote && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (player == Minecraft.getMinecraft().thePlayer) {
            	KeyBinding.unPressAllKeys();
            	movingPath.clear();
            	rotationIndex = 0;
            	currentPosIndex = -1;
            }
        }
    }
    
    @SubscribeEvent
    public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
    	KeyBinding.unPressAllKeys();
    	movingPath.clear();
    	rotationIndex = 0;
    	currentPosIndex = -1;
    }
	
    
    
}
