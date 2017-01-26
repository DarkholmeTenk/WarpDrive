package cr0s.warpdrive.data;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.HashMap;

public enum EnumLaserTreeFarmMode implements IStringSerializable {
	INACTIVE			("inactive"),
	FARMING_LOW_POWER	("farming_low_power"),
	FARMING_POWERED		("farming_powered"),
	SCANNING_LOW_POWER	("scanning_low_power"),
	SCANNING_POWERED	("scanning_powered"),
	PLANTING_LOW_POWER	("planting_low_power"),
	PLANTING_POWERED	("planting_powered");
	
	private final String unlocalizedName;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, EnumLaserTreeFarmMode> ID_MAP = new HashMap<>();
	
	static {
		length = EnumLaserTreeFarmMode.values().length;
		for (EnumLaserTreeFarmMode componentType : values()) {
			ID_MAP.put(componentType.ordinal(), componentType);
		}
	}
	
	EnumLaserTreeFarmMode(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
	}
	
	public static EnumLaserTreeFarmMode get(final int damage) {
		return ID_MAP.get(damage);
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}
	
	@Nonnull
	@Override
	public String getName() { return unlocalizedName; }
}
