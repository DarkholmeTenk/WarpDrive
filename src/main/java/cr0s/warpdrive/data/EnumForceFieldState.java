package cr0s.warpdrive.data;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.HashMap;

public enum EnumForceFieldState implements IStringSerializable {
	NOT_CONNECTED           ("not_connected"        ,  0.1F ),
	CONNECTED_NOT_POWERED   ("connected_not_powered",  0.5F ),
	CONNECTED_OFFLINE       ("connected_offline"    ,  2.5F ),
	CONNECTED_POWERED       ("connected_powered"    , 15.0F );
	
	private final String name;
	private final float rotationSpeed_degPerTick;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, EnumForceFieldState> ID_MAP = new HashMap<>();
	
	static {
		length = EnumForceFieldState.values().length;
		for (EnumForceFieldState enumForceFieldShape : values()) {
			ID_MAP.put(enumForceFieldShape.ordinal(), enumForceFieldShape);
		}
	}
	
	EnumForceFieldState(final String name, final float rotationSpeed_degPerTick) {
		this.name = name;
		this.rotationSpeed_degPerTick = rotationSpeed_degPerTick;
	}
	
	@Nonnull
	@Override
	public String getName() {
		return name;
	}
	
	public float getRotationSpeed_degPerTick() {
		return rotationSpeed_degPerTick;
	}
	
	public static EnumForceFieldState get(final int damage) {
		return ID_MAP.get(damage);
	}
}
