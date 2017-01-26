package cr0s.warpdrive.data;

import java.util.HashMap;

public enum EnumPermissionNode {
	NONE               ("None"),
	ENABLE             ("Enable"),
	OPEN_GUI           ("OpenGUI"),
	MODIFY             ("Modify"),
	SNEAK_THROUGH      ("SneakThrough"),
	;
	
	public final String unlocalizedName;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, EnumPermissionNode> ID_MAP = new HashMap<>();
	
	static {
		length = EnumPermissionNode.values().length;
		for (EnumPermissionNode enumPermissionNode : values()) {
			ID_MAP.put(enumPermissionNode.ordinal(), enumPermissionNode);
		}
	}
	
	EnumPermissionNode(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
	}
	
	public static EnumPermissionNode get(final int id) {
		return ID_MAP.get(id);
	}
}
