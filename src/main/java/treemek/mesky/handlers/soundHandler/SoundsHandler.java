package treemek.mesky.handlers.soundHandler;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundListSerializer;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Source;
import treemek.mesky.Mesky;
import treemek.mesky.Reference;
import treemek.mesky.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

public class SoundsHandler {
	@SideOnly(Side.CLIENT)
    static class SoundSystemStarterThread extends SoundSystem
    {
        private SoundSystemStarterThread()
        {
        }

        public boolean playing(String p_playing_1_)
        {
            synchronized (SoundSystemConfig.THREAD_SYNC)
            {
                if (this.soundLibrary == null)
                {
                    return false;
                }
                else
                {
                    Source source = (Source)this.soundLibrary.getSources().get(p_playing_1_);
                    return source == null ? false : source.playing() || source.paused() || source.preLoad;
                }
            }
        }
    }
	
	@SideOnly(Side.CLIENT)
	public static class SoundEventAccessor implements ISoundEventAccessor<SoundPoolEntry>
	{
	    private final SoundPoolEntry entry;
	    private final int weight;

	    public SoundEventAccessor(SoundPoolEntry entry, int weight)
	    {
	        this.entry = entry;
	        this.weight = weight;
	    }

	    public int getWeight()
	    {
	        return this.weight;
	    }

	    public SoundPoolEntry cloneEntry()
	    {
	        return new SoundPoolEntry(this.entry);
	    }
	}
	
	public static Map<String, List<String>> sounds = new HashMap<>();

    private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
    private static final Type TYPE = new TypeToken<Map<String, SoundList>>() {}.getType();
    
    static SoundRegistry sndRegistry = new SoundRegistry();
    
    public static SoundSystem getSoundSystem() {
        try {
            SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
            
            // Using Forge's ReflectionHelper
            SoundManager soundManager = ReflectionHelper.getPrivateValue(SoundHandler.class, soundHandler, "sndManager", "field_147694_f");
            
            return ReflectionHelper.getPrivateValue(SoundManager.class, soundManager, "sndSystem", "field_148620_e");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
    private static void clearSounds() {
    	SoundSystem sndSystem = getSoundSystem();
    	
    	for (Entry<String, List<String>> entry : sounds.entrySet()) {
			String path = entry.getKey();
			List<String> s = entry.getValue();
			
			for (String id : s) {
				sndSystem.stop(id);
	            sndSystem.removeSource(id);
			}
		}
    	sounds.clear();
    }
    
    public static void reloadSounds(){
    	sndRegistry.clearMap();
    	clearSounds();
    	for (File file : getAllImportedSounds()) {
			loadSound(file);
		}
    }
    
    public static void loadSound(File file) {
    	ResourceLocation location = new ResourceLocation(Reference.MODID, file.getName().replace(".ogg", ""));
    	
    	Utils.debug(location + " loaded.");
    	loadSoundResource(location, SoundCategory.MASTER);
    }

    public static Map<String, SoundList> getSoundMap(InputStream stream){
        Map map;

        try{
            map = (Map)GSON.fromJson((Reader)(new InputStreamReader(stream)), TYPE);
        }finally{
            IOUtils.closeQuietly(stream);
        }

        return map;
    }
    
    private static void loadSoundResource(ResourceLocation location, SoundCategory category) {
		boolean flag = !sndRegistry.containsKey(location);
        SoundEventAccessorComposite soundeventaccessorcomposite;

        soundeventaccessorcomposite = new SoundEventAccessorComposite(location, 1.0D, 1.0D, category);
        sndRegistry.registerSound(soundeventaccessorcomposite);

        // sound file
        String s = location.getResourceDomain();
        ResourceLocation resourcelocation = new ResourceLocation(s);
        final String s1 = s.contains(":") ? resourcelocation.getResourceDomain() : location.getResourceDomain();
        ISoundEventAccessor<SoundPoolEntry> isoundeventaccessor;
        
        ResourceLocation resourcelocation1 = new ResourceLocation(s1, "sounds/" + resourcelocation.getResourcePath() + ".ogg");
        InputStream inputstream = null;

        isoundeventaccessor = new SoundEventAccessor(new SoundPoolEntry(resourcelocation1, 1, 1, true), 1);
    

        soundeventaccessorcomposite.addSoundToEventPool(isoundeventaccessor);
    }
    
    public static boolean deleteSound(File sound) {
    	Minecraft.getMinecraft().getSoundHandler().stopSounds();
    	unloadSound(sound);
    	
    	try {
    		if(sound.delete()) {
		    	reloadSounds();
		    	return true;
    		}else {
    			return false;
    		}
    	}catch (Exception e) {
    		Utils.writeError(e);
	    	return false;
 		}
    }
    
    public static void stopSounds() {
    	Minecraft.getMinecraft().getSoundHandler().stopSounds();
    }
    
    public static void stopSound(Sound sound) {
    	Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
    }
    
    private static void unloadSound(File sound) {
    	SoundSystem sndSystem = getSoundSystem();

        // Get the list of sound IDs associated with the file path
        List<String> soundIds = sounds.get(sound.getName());

        if (soundIds != null) {
            for (String soundId : soundIds) {
                sndSystem.stop(soundId);
                sndSystem.removeSource(soundId);
            }

            sounds.remove(sound.getName());
        }
    }
    
    public static boolean doesSoundExists(String sound) {
    	if(!sound.contains(":")) return false;
    	ResourceLocation soundLocation = new ResourceLocation(getSoundResourceLocation(sound), getSoundName(sound));
    	Sound Isound = new Sound(soundLocation);
    	
    	SoundEventAccessorComposite soundeventaccessorcomposite = Minecraft.getMinecraft().getSoundHandler().getSound(Isound.getSoundLocation());

        if (soundeventaccessorcomposite == null){
        	return doesSoundExistsInMeskyRegistry(Isound);
        }else{
            SoundPoolEntry soundpoolentry = soundeventaccessorcomposite.cloneEntry();

            if (soundpoolentry == SoundHandler.missing_sound)
            {
            	return doesSoundExistsInMeskyRegistry(Isound);
            }
        }
        
        return true;
    }
    
    public static boolean doesSoundExistsInMeskyRegistry(ISound sound) {
    	SoundEventAccessorComposite soundeventaccessorcomposite = (SoundEventAccessorComposite)sndRegistry.getObject(sound.getSoundLocation());

        if (soundeventaccessorcomposite == null)
        {
        	return false;
        }
        else
        {
            SoundPoolEntry soundpoolentry = soundeventaccessorcomposite.cloneEntry();

            if (soundpoolentry == SoundHandler.missing_sound)
            {
            	return false;
            }
        }
        
        return true;
    }
    
    public static boolean doesSoundExists(ISound Isound) {
    	SoundEventAccessorComposite soundeventaccessorcomposite = Minecraft.getMinecraft().getSoundHandler().getSound(Isound.getSoundLocation());

        if (soundeventaccessorcomposite == null)
        {
        	return doesSoundExistsInMeskyRegistry(Isound);
        }
        else
        {
            SoundPoolEntry soundpoolentry = soundeventaccessorcomposite.cloneEntry();

            if (soundpoolentry == SoundHandler.missing_sound)
            {
            	return doesSoundExistsInMeskyRegistry(Isound);
            }
        }
        
        return true;
    }
	
	public static void importSoundToResources(File file) {
		Utils.debug("Importing sound: " + file.getName());
		File SOUND_DIR = new File(Mesky.configDirectory, "/mesky/sounds");
		if(!SOUND_DIR.exists()) SOUND_DIR.mkdir();
		
		if(!file.getName().endsWith(".ogg")) return;
		File destFile = new File(SOUND_DIR, file.getName());
		
		try {
            Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		reloadSounds();
	}
	
	
	public static List<File> getAllImportedSounds(){
		File SOUND_DIR = new File(Mesky.configDirectory, "/mesky/sounds");
		
		List<File> sounds = new ArrayList<>();

        if (SOUND_DIR.isDirectory()) {
            File[] files = SOUND_DIR.listFiles((dir, name) -> name.endsWith(".ogg"));

            if (files != null) {
                for (File file : files) {
                    sounds.add(file);
                }
            }
        }
		 
		 return sounds;
	}
	
	public static List<String> getAllMeskySounds(){
		List<String> sounds = new ArrayList<>();
		
		for (File sound : getAllImportedSounds()) {
			sounds.add("mesky:" + sound.getName().replace(".ogg", ""));
		}
		
		try {
			for (IResource iresource : Minecraft.getMinecraft().getResourceManager().getAllResources(new ResourceLocation(Reference.MODID, "sounds.json")))
			{
			    try
			    {
			        Map<String, SoundList> map = getSoundMap(iresource.getInputStream());

			        for (Entry<String, SoundList> entry : map.entrySet())
			        {
			        	sounds.add("mesky:" + entry.getKey());
			        }
			    }
			    catch (RuntimeException runtimeexception)
			    {
			        Utils.writeError("Invalid sounds.json file");
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 return sounds;
	}
	
    public static String getSoundResourceLocation(String sound) {
    	return sound.split(":")[0];
    }
    
    public static String getSoundName(String sound) {
    	return sound.split(":")[1];
    }
	
    public static void playSound(Sound sound) {
    	if(!doesSoundExists(sound)) {
			Utils.addMinecraftMessageWithPrefix("Sound \"" + EnumChatFormatting.GOLD + sound.getSoundLocation().getResourceDomain() + ":" + sound.getSoundLocation().getResourcePath() + EnumChatFormatting.WHITE +  "\" doesn't exist!");
			return;
		}
    	
		if(!doesSoundExistsInMeskyRegistry(sound)) {
			Minecraft.getMinecraft().getSoundHandler().playSound(sound); // pitch is capped (0.5 - 2) and volume (0 - 1) by minecraft
		}else {
			playSoundFromFile(sound);
		}
    }
    
	public static void playSound(String sound) {
		if(!doesSoundExists(sound)) {
			if(getSoundResourceLocation(sound).equals("mesky")) {
				Utils.addMinecraftMessageWithPrefix("Sound \"" + EnumChatFormatting.GOLD + sound + EnumChatFormatting.WHITE + "\" doesn't exist, try /mesky sounds");
			}else {
				Utils.addMinecraftMessageWithPrefix("Sound \"" + EnumChatFormatting.GOLD + sound + EnumChatFormatting.WHITE + "\" doesn't exist!");
			}
			return;
		}
		
		ResourceLocation soundLocation = new ResourceLocation(getSoundResourceLocation(sound), getSoundName(sound));
		Sound Isound = new Sound(soundLocation);
		if(!doesSoundExistsInMeskyRegistry(Isound)) {
			Utils.debug(sound + " doesnt exists in mesky register and is played");
			Minecraft.getMinecraft().getSoundHandler().playSound(Isound); // pitch is capped (0.5 - 2) and volume (0 - 1) by minecraft
		}else {
			Utils.debug(sound + " exists in mesky register and is played");
			playSoundFromFile(Isound);
		}
	}
	
	public static void playSound(String sound, float volume, float pitch) {
		if(!doesSoundExists(sound)) {
			if(!sound.contains(":")){
				Utils.addMinecraftMessageWithPrefix("Sound \"" + EnumChatFormatting.GOLD + sound + EnumChatFormatting.WHITE + "\" doesn't exist! There's no sound domain did you meant to play \"" + EnumChatFormatting.GOLD + "mesky:" + sound + EnumChatFormatting.WHITE + "\"?");
			}else if(getSoundResourceLocation(sound).equals("mesky")) {
				Utils.addMinecraftMessageWithPrefix("Sound \"" + EnumChatFormatting.GOLD + sound + EnumChatFormatting.WHITE + "\" doesn't exist, try /mesky sounds");
			}else {
				Utils.addMinecraftMessageWithPrefix("Sound \"" + EnumChatFormatting.GOLD + sound + EnumChatFormatting.WHITE + "\" doesn't exist!");
			}
			return;
		}
		
		ResourceLocation soundLocation = new ResourceLocation(getSoundResourceLocation(sound), getSoundName(sound));
		Sound Isound = new Sound(soundLocation, volume, pitch);
		
		if(!doesSoundExistsInMeskyRegistry(Isound)) {
			Minecraft.getMinecraft().getSoundHandler().playSound(Isound); // pitch is capped (0.5 - 2) and volume (0 - 1) by minecraft
		}else {
			playSoundFromFile(Isound);
		}
	}
	
	public static void playSound(ResourceLocation soundLocation, float volume, float pitch) {
		Sound Isound = new Sound(soundLocation, volume, pitch);
		
		if(!doesSoundExistsInMeskyRegistry(Isound)) {
			Minecraft.getMinecraft().getSoundHandler().playSound(Isound); // pitch is capped (0.5 - 2) and volume (0 - 1) by minecraft
		}else {
			playSoundFromFile(Isound);
		}
	}
	
	
	private static void playSoundFromFile(Sound sound) {
		SoundSystem sndSystem = getSoundSystem();
		if(sndSystem == null) {
			Utils.debug("Sound system == null");
			return;
		}
		
		SoundEventAccessorComposite soundeventaccessorcomposite = sndRegistry.getObject(sound.getSoundLocation());
		
		File file = new File(Mesky.configDirectory, "/mesky/sounds/" + sound.getSoundLocation().getResourcePath() + ".ogg");
		
		SoundPoolEntry soundpoolentry = soundeventaccessorcomposite.cloneEntry();
		
		 if (soundpoolentry == SoundHandler.missing_sound){
	          Utils.writeError("Unable to play empty soundEvent: " + soundpoolentry.getSoundPoolEntryLocation());
	     }else{
	         float f = sound.getVolume();
	         float d = sound.getPitch();
	         float f1 = 16.0F;
	
	         if (f > 1.0F)
	         {
	             f1 *= f;
	         }
	
	         ResourceLocation resourcelocation = soundpoolentry.getSoundPoolEntryLocation();
	
	         if (f > 0.0F){
	             boolean flag = sound.canRepeat() && sound.getRepeatDelay() == 0;
	             String s = MathHelper.getRandomUuid(ThreadLocalRandom.current()).toString();
	             
				if (soundpoolentry.isStreamingSound())
	             {
	                 try {
						sndSystem.newStreamingSource(false, s, file.toURI().toURL(), resourcelocation.toString(), flag, sound.getXPosF(), sound.getYPosF(), sound.getZPosF(), sound.getAttenuationType().getTypeInt(), f1);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
	             }
	             else
	             {
	                 try {
						sndSystem.newSource(false, s, file.toURI().toURL(), resourcelocation.toString(), flag, sound.getXPosF(), sound.getYPosF(), sound.getZPosF(), sound.getAttenuationType().getTypeInt(), f1);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
	             }
				
	             sndSystem.setPitch(s, d);
	             sndSystem.setVolume(s, f);
	             sndSystem.play(s);
	             
	             sounds.computeIfAbsent(file.getName(), k -> new ArrayList<>()).add(s);
	         }
	     }
	}
	
	private static URL getURLForSoundResource(final ResourceLocation p_148612_0_)
    {
        String s = String.format("%s:%s:%s", new Object[] {"mcsounddomain", p_148612_0_.getResourceDomain(), p_148612_0_.getResourcePath()});
        URLStreamHandler urlstreamhandler = new URLStreamHandler()
        {
            protected URLConnection openConnection(final URL p_openConnection_1_)
            {
                return new URLConnection(p_openConnection_1_)
                {
                    public void connect() throws IOException
                    {
                    }
                    public InputStream getInputStream() throws IOException
                    {
                        return Minecraft.getMinecraft().getResourceManager().getResource(p_148612_0_).getInputStream();
                    }
                };
            }
        };

        try
        {
            return new URL((URL)null, s, urlstreamhandler);
        }
        catch (MalformedURLException var4)
        {
            throw new Error("TODO: Sanely handle url exception! :D");
        }
    }
}
