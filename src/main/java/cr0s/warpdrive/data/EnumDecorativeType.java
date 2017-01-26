package cr0s.warpdrive.data;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.HashMap;

public enum EnumDecorativeType implements IStringSerializable {
	PLAIN              ("plain"),
	ENERGIZED          ("energized"),
	NETWORK            ("network");
	
	private final String unlocalizedName;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, EnumDecorativeType> ID_MAP = new HashMap<>();
	
	static {
		length = EnumDecorativeType.values().length;
		for (EnumDecorativeType componentType : values()) {
			ID_MAP.put(componentType.ordinal(), componentType);
		}
	}
	
	EnumDecorativeType(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
	}
	
	public static EnumDecorativeType get(final int damage) {
		return ID_MAP.get(damage);
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}
	
	@Nonnull
	@Override
	public String getName() { return unlocalizedName; }
}
