package cr0s.warpdrive.data;

import java.util.HashMap;

public enum DecorativeType {
	PLAIN              ("Plain"),
	ENERGIZED          ("Energized"),
	NETWORK            ("Network");
	
	public final String unlocalizedName;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, DecorativeType> ID_MAP = new HashMap<>();
	
	static {
		length = DecorativeType.values().length;
		for (DecorativeType componentType : values()) {
			ID_MAP.put(componentType.ordinal(), componentType);
		}
	}
	
	private DecorativeType(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
	}
	
	public static DecorativeType get(final int damage) {
		return ID_MAP.get(damage);
	}
}
