package treemek.mesky.proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class ClientProxy implements IProxy {

    @Override
    public void sendDiggingPacket(C07PacketPlayerDigging.Action action, BlockPos pos, EnumFacing facing) {
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(action, pos, facing));
    }
}
