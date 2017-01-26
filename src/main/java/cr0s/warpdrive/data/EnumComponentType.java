package cr0s.warpdrive.data;

import java.util.HashMap;

public enum EnumComponentType {
	EMERALD_CRYSTAL            ("emerald_crystal"),
	ENDER_CRYSTAL              ("ender_crystal"),
	DIAMOND_CRYSTAL            ("diamond_crystal"),
	DIFFRACTION_GRATING        ("diffraction_grating"),
	REACTOR_CORE               ("reactor_core"),
	COMPUTER_INTERFACE         ("computer_interface"),
	POWER_INTERFACE            ("power_interface"),
	CAPACITIVE_CRYSTAL         ("capacitive_crystal"),
	AIR_CANISTER               ("air_canister_empty"),
	LENS                       ("lens"),
	ZOOM                       ("zoom"),
	GLASS_TANK                 ("glass_tank"),
	FLAT_SCREEN                ("flat_screen"),
	MEMORY_CRYSTAL             ("memory_crystal"),
	MOTOR                      ("motor"),
	BONE_CHARCOAL              ("bone_charcoal"),
	ACTIVATED_CARBON           ("activated_carbon"),
	LASER_MEDIUM_EMPTY         ("laser_medium_empty"),
	COIL_CRYSTAL               ("coil_crystal"),
	ELECTROMAGNETIC_PROJECTOR  ("electromagnetic_projector");
	
	private final String unlocalizedName;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, EnumComponentType> ID_MAP = new HashMap<>();
	
	static {
		length = EnumComponentType.values().length;
		for (EnumComponentType enumComponentType : values()) {
			ID_MAP.put(enumComponentType.ordinal(), enumComponentType);
		}
	}
	
	EnumComponentType(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
	}
	
	public static EnumComponentType get(final int damage) {
		return ID_MAP.get(damage);
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}
}
