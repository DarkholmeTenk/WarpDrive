package cr0s.warpdrive.data;

import java.util.HashMap;

import cr0s.warpdrive.block.TileEntityLaser;
import cr0s.warpdrive.block.detection.TileEntityCamera;

public enum CameraType {
	SIMPLE_CAMERA    (TileEntityCamera.class),
	LASER_CAMERA    (TileEntityLaser.class);
	
	public final Class<?> clazz;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, CameraType> ID_MAP = new HashMap<>();
	
	static {
		length = CameraType.values().length;
		for (CameraType cameraType : values()) {
			ID_MAP.put(cameraType.ordinal(), cameraType);
		}
	}
	
	private CameraType(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public static CameraType get(final int id) {
		return ID_MAP.get(id);
	}
}
