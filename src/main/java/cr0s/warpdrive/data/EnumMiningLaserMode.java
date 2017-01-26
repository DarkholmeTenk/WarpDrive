package cr0s.warpdrive.data;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.HashMap;

public enum EnumMiningLaserMode implements IStringSerializable {
	INACTIVE			("inactive"),
	SCANNING_LOW_POWER	("scanning_low_power"),
	SCANNING_POWERED	("scanning_powered"),
	MINING_LOW_POWER	("mining_low_power"),
	MINING_POWERED		("mining_powered");
	
	private final String unlocalizedName;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, EnumMiningLaserMode> ID_MAP = new HashMap<>();
	
	static {
		length = EnumMiningLaserMode.values().length;
		for (EnumMiningLaserMode componentType : values()) {
			ID_MAP.put(componentType.ordinal(), componentType);
		}
	}
	
	EnumMiningLaserMode(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
	}
	
	public static EnumMiningLaserMode get(final int damage) {
		return ID_MAP.get(damage);
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}
	
	@Nonnull
	@Override
	public String getName() { return unlocalizedName; }
}
