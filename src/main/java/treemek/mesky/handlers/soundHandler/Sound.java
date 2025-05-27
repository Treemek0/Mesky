package treemek.mesky.handlers.soundHandler;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Sound  extends PositionedSound {
	public Sound(ResourceLocation soundResource) {
        super(soundResource);
        this.volume = 1;
        this.pitch = 1;
        this.xPosF = 0;
        this.yPosF = 0;
        this.zPosF = 0;
        this.repeat = false;
        this.repeatDelay = 0;
        this.attenuationType = ISound.AttenuationType.NONE;
    }
	
	public Sound(ResourceLocation soundResource, float volume, float pitch) {
        super(soundResource);
        this.volume = volume;
        this.pitch = pitch;
        this.xPosF = 0;
        this.yPosF = 0;
        this.zPosF = 0;
        this.repeat = false;
        this.repeatDelay = 0;
        this.attenuationType = ISound.AttenuationType.NONE;
    }
	
    public Sound(ResourceLocation soundResource, float volume, float pitch, boolean repeat, int repeatDelay, ISound.AttenuationType attenuationType, float xPosition, float yPosition, float zPosition) {
        super(soundResource);
        this.volume = volume;
        this.pitch = pitch;
        this.xPosF = xPosition;
        this.yPosF = yPosition;
        this.zPosF = zPosition;
        this.repeat = repeat;
        this.repeatDelay = repeatDelay;
        this.attenuationType = attenuationType;
    }
}
