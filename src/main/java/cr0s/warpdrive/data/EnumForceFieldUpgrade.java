package cr0s.warpdrive.data;

import java.util.HashMap;

public enum EnumForceFieldUpgrade {
	NONE               ("none"          , false, false),
	BREAK              ("break"         , false, true ),
	CAMOUFLAGE         ("camouflage"    , false, true ),
	COOL               ("cool"          , true , true ),
	FUSION             ("fusion"        , true , true ),
	INVERT             ("invert"        , true , false),
	MUTE               ("mute"          , true , false),
	RANGE              ("range"         , true , true ),
	ROTATION           ("rotation"      , true , false),
	SHOCK              ("shock"         , true , true ),
	SPEED              ("speed"         , true , true ),
	STABILIZE          ("stabilize"     , false, true ),
	THICKNESS          ("thickness"     , true , true ),
	TRANSLATION        ("translation"   , true , false),
	WARM               ("warm"          , true , true ),
	// reserved 14
	// reserved 15
	;
	
	public final String unlocalizedName;
	public final boolean allowAsItem;
	public final boolean allowAsBlock;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, EnumForceFieldUpgrade> ID_MAP = new HashMap<>();
	
	static {
		length = EnumForceFieldUpgrade.values().length;
		for (EnumForceFieldUpgrade forceFieldShapeType : values()) {
			ID_MAP.put(forceFieldShapeType.ordinal(), forceFieldShapeType);
		}
	}
	
	EnumForceFieldUpgrade(final String unlocalizedName, final boolean allowAsItem, final boolean allowAsBlock) {
		this.unlocalizedName = unlocalizedName;
		this.allowAsItem = allowAsItem;
		this.allowAsBlock = allowAsBlock;
	}
	
	public static EnumForceFieldUpgrade get(final int damage) {
		return ID_MAP.get(damage);
	}
}
