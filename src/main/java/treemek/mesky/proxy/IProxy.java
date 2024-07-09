package treemek.mesky.proxy;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public interface IProxy {
    void sendDiggingPacket(C07PacketPlayerDigging.Action action, BlockPos pos, EnumFacing facing);
}