package treemek.mesky.utils.manager;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import treemek.mesky.Mesky;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Waypoints;

public class AntyGhostBlock {
	
	private final long COOLDOWN_TIME = 2500;
	private long COOLDOWN = 0;
	
	boolean BLOCK = false;
	private long BLOCK_COOLDOWN = 0;
	private double prevX;
	private double prevY;
	private double prevZ;
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
		if(SettingsConfig.AntyGhostBlocks.isOn) {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			if(player == null) return;
			
			if(BLOCK) {
				if (System.currentTimeMillis() - BLOCK_COOLDOWN >= 1000) {
					BLOCK = false;
					prevX = player.posX;
				    prevY = player.posY;
				    prevZ = player.posZ;
				}
				
				return;
			}else {
				double dx = player.posX - prevX;
			    double dy = player.posY - prevY;
			    double dz = player.posZ - prevZ;
			    double distSq = dx * dx + dy * dy + dz * dz;
				
				prevX = player.posX;
			    prevY = player.posY;
			    prevZ = player.posZ;
				
			    PotionEffect speed = player.getActivePotionEffect(Potion.moveSpeed);
			    int amplifier = (speed != null)?speed.getAmplifier():1;
			    
				if(distSq > 4 * Math.max(1, amplifier/20)) { 
					Utils.debug("Detected teleporting");
					blockAntyGhostBlocks();
					return;
				}
			}
			
		    
			
			boolean isMovingKey = player.moveForward != 0 || player.moveStrafing != 0;
			boolean hasMotion = player.motionX != 0 || player.motionZ != 0;
			
		    if (!hasMotion && isMovingKey) {
		    	float yawRad = (float) Math.toRadians(player.rotationYaw);
		    	float forward = player.moveForward;
		    	float strafe = player.moveStrafing;

		    	// Normalize movement input
		    	float length = MathHelper.sqrt_float(forward * forward + strafe * strafe);
		    	if (length >= 1) {
		    	    forward /= length;
		    	    strafe /= length;
		    	}

		    	// Convert to world direction
		    	double dirX = Math.signum((forward * -Math.sin(yawRad)) + (strafe * Math.cos(yawRad)));
		    	double dirZ = Math.signum((forward * Math.cos(yawRad)) + (strafe * Math.sin(yawRad)));
		    	
		    	List<BlockPos> positions = new ArrayList<>();
		    	positions.add(new BlockPos(player.posX + dirX, player.posY, player.posZ));
		    	positions.add(new BlockPos(player.posX, player.posY, player.posZ + dirZ));

		    	for (BlockPos pos : positions) {
		    		if (Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock().isAir(Minecraft.getMinecraft().theWorld, pos)) {
		    			if(!Minecraft.getMinecraft().theWorld.getBlockState(pos.add(0,1,0)).getBlock().isAir(Minecraft.getMinecraft().theWorld, pos.add(0,1,0))) continue;
		    			AxisAlignedBB blockBB = new AxisAlignedBB(
		    				    pos.getX() - 0.2f, pos.getY(), pos.getZ() - 0.2f,
		    				    pos.getX() + 1.2f, pos.getY() + 1, pos.getZ() + 1.2f
		    				);

		    			AxisAlignedBB lastTickPlayerBB = new AxisAlignedBB(
		    				    player.lastTickPosX - player.width/2, player.lastTickPosY+player.height, player.lastTickPosZ - player.width/2,
		    				    player.lastTickPosX + player.width/2, player.lastTickPosY, player.lastTickPosZ + player.width/2
		    				);
		    			
		    			if (player.getEntityBoundingBox().intersectsWith(blockBB) || lastTickPlayerBB.intersectsWith(blockBB)) { // is ghost block
		    				if (System.currentTimeMillis() - COOLDOWN >= COOLDOWN_TIME) {
		    		            COOLDOWN = System.currentTimeMillis();
		    		            
		    		            removeGhostBlocks(positions);
		    		            Utils.debug("Detected ghost block at " + pos);
		    		        }
		    			}
	    	        }
				}
		    }
		}
	}
	
	
	private void removeGhostBlocks(List<BlockPos> positions) {
		Minecraft mc = Minecraft.getMinecraft();
        NetHandlerPlayClient net = mc.getNetHandler();
        if (net == null) return;
        
        BlockPos pos = mc.thePlayer.getPosition();
        
        for (BlockPos blockPos : positions) {
            for (int dy = -0; dy <= 1; dy++) {
                C07PacketPlayerDigging packet = new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockPos.add(0,dy,0), EnumFacing.UP);
                net.addToSendQueue(packet);
            }
        }
	}
	
	@SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
    	if (event.world.isRemote && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (player == Minecraft.getMinecraft().thePlayer) {
            	// block antyghost when entering world because velocity is 0 while holding keys
            	blockAntyGhostBlocks();
            }
        }
    }
	
	@SubscribeEvent
    public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
		blockAntyGhostBlocks();
	}
	
	public void blockAntyGhostBlocks() {
		BLOCK = true;
    	BLOCK_COOLDOWN = System.currentTimeMillis();
	}
}
