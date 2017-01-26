package cr0s.warpdrive.data;

import cr0s.warpdrive.WarpDrive;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SoundEvents {
	public static final SoundEvent LASER_LOW;
	public static final SoundEvent LASER_MEDIUM;
	public static final SoundEvent LASER_HIGH;
	public static final SoundEvent CLOAK;
	public static final SoundEvent DECLOAK;
	public static final SoundEvent PROJECTING;
	public static final SoundEvent WARP_4_SECONDS;
	public static final SoundEvent WARP_10_SECONDS;
	public static final SoundEvent WARP_30_SECONDS;
    public static final SoundEvent DING;

    static {
		LASER_LOW = registerSound("lowlaser");
		LASER_MEDIUM = registerSound("midlaser");
		LASER_HIGH = registerSound("hilaser");
		CLOAK = registerSound("cloak");
		DECLOAK = registerSound("decloak");
		PROJECTING = registerSound("projecting");
		WARP_4_SECONDS = registerSound("warp_4s");
		WARP_10_SECONDS = registerSound("warp_10s");
		WARP_30_SECONDS = registerSound("warp_30s");
		DING = registerSound("ding");
	}
	
	@SuppressWarnings({"unused", "EmptyMethod"})
	public static void registerSounds() {
		// Dummy method to make sure the static initializer runs
	}
	
	private static SoundEvent registerSound(String soundName) {
		final ResourceLocation soundID = new ResourceLocation(WarpDrive.MODID, soundName);
		return GameRegistry.register(new SoundEvent(soundID).setRegistryName(soundID));
	}
}
