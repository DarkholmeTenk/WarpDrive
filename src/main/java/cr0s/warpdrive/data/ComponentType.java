package cr0s.warpdrive.data;

import java.util.HashMap;

public enum ComponentType {
	EMERALD_CRYSTAL    ("EmeraldCrystal"),		// EmptyCore
	ENDER_CRYSTAL      ("EnderCrystal"),		// TeleCore
	DIAMOND_CRYSTAL    ("DiamondCrystal"),		// WarpCore
	DIFFRACTION_GRATING("DiffractionGrating"),	// LaserCore
	REACTOR_CORE       ("ReactorCore"),
	COMPUTER_INTERFACE ("ComputerInterface"),	// InterfaceComputer
	POWER_INTERFACE    ("PowerInterface"),		// InterfacePower
	CAPACITIVE_CRYSTAL ("CapacitiveCrystal"),	// PowerCore
	AIR_CANISTER       ("AirCanisterEmpty"),
	LENS               ("Lens"),
	ZOOM               ("Zoom"),
	GLASS_TANK         ("GlassTank"),
	FLAT_SCREEN        ("FlatScreen"),
	MEMORY_CRYSTAL     ("MemoryCrystal"),
	MOTOR              ("Motor"),
	BONE_CHARCOAL      ("BoneCharcoal"),
	ACTIVATED_CARBON   ("ActivatedCarbon"),
	LASER_MEDIUM_EMPTY ("LaserMediumEmpty");
	
	public final String unlocalizedName;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, ComponentType> ID_MAP = new HashMap<>();
	
	static {
		length = ComponentType.values().length;
		for (ComponentType componentType : values()) {
			ID_MAP.put(componentType.ordinal(), componentType);
		}
	}
	
	private ComponentType(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
	}
	
	public static ComponentType get(final int damage) {
		return ID_MAP.get(damage);
	}
}
