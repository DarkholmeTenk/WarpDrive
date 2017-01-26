package cr0s.warpdrive.data;

import java.util.HashMap;

import cr0s.warpdrive.block.TileEntityLaser;
import cr0s.warpdrive.block.detection.TileEntityCamera;

public enum EnumCameraType {
	SIMPLE_CAMERA    (TileEntityCamera.class),
	LASER_CAMERA    (TileEntityLaser.class);
	
	public final Class<?> clazz;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, EnumCameraType> ID_MAP = new HashMap<>();
	
	static {
		length = EnumCameraType.values().length;
		for (EnumCameraType enumCameraType : values()) {
			ID_MAP.put(enumCameraType.ordinal(), enumCameraType);
		}
	}
	
	private EnumCameraType(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public static EnumCameraType get(final int id) {
		return ID_MAP.get(id);
	}
}
