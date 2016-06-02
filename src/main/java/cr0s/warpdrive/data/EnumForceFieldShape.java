package cr0s.warpdrive.data;

import java.util.HashMap;

public enum EnumForceFieldShape {
	NONE               ("none"),
	SPHERE             ("sphere"),
	CYLINDER_H         ("cylinder_h"),
	CYLINDER_V         ("cylinder_v"),
	CUBE               ("cube"),
	PLANE              ("plane"),
	TUBE               ("tube"),
	TUNNEL             ("tunnel");
	
	public final String unlocalizedName;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, EnumForceFieldShape> ID_MAP = new HashMap<>();
	
	static {
		length = EnumForceFieldShape.values().length;
		for (EnumForceFieldShape enumForceFieldShape : values()) {
			ID_MAP.put(enumForceFieldShape.ordinal(), enumForceFieldShape);
		}
	}
	
	EnumForceFieldShape(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
	}
	
	public static EnumForceFieldShape get(final int damage) {
		return ID_MAP.get(damage);
	}
}
