package cr0s.warpdrive.data;

import java.util.HashMap;

public enum PermissionNode {
	NONE               ("None"),
	ENABLE             ("Enable"),
	OPEN_GUI           ("OpenGUI"),
	MODIFY             ("Modify"),
	SNEAK_THROUGH      ("SneakThrough"),
	;
	
	public final String unlocalizedName;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, PermissionNode> ID_MAP = new HashMap<>();
	
	static {
		length = PermissionNode.values().length;
		for (PermissionNode permissionNode : values()) {
			ID_MAP.put(permissionNode.ordinal(), permissionNode);
		}
	}
	
	PermissionNode(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
	}
	
	public static PermissionNode get(final int id) {
		return ID_MAP.get(id);
	}
}
