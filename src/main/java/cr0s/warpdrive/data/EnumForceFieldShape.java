package cr0s.warpdrive.data;

import cr0s.warpdrive.api.IForceFieldShape;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public enum EnumForceFieldShape implements IStringSerializable, IForceFieldShape {
	NONE               ("none"),
	SPHERE             ("sphere"),
	CYLINDER_H         ("cylinder_h"),
	CYLINDER_V         ("cylinder_v"),
	CUBE               ("cube"),
	PLANE              ("plane"),
	TUBE               ("tube"),
	TUNNEL             ("tunnel");
	
	private final String name;
	
	// cached values
	public static final int length;
	private static final HashMap<Integer, EnumForceFieldShape> ID_MAP = new HashMap<>();
	
	static {
		length = EnumForceFieldShape.values().length;
		for (EnumForceFieldShape enumForceFieldShape : values()) {
			ID_MAP.put(enumForceFieldShape.ordinal(), enumForceFieldShape);
		}
	}
	
	EnumForceFieldShape(String name) {
		this.name = name;
	}
	
	@Nonnull
	@Override
	public String getName() {
		return name;
	}
	
	public static EnumForceFieldShape get(final int damage) {
		return ID_MAP.get(damage);
	}
	
	@Override
	public Map<VectorI, Boolean> getVertexes(ForceFieldSetup forceFieldSetup) {
		VectorI vScale = forceFieldSetup.vMax.clone().translateBack(forceFieldSetup.vMin);
		Map<VectorI, Boolean> mapVertexes = new HashMap<>(vScale.x * vScale.y * vScale.z);
		float radius;
		float halfThickness = forceFieldSetup.thickness / 2.0F;
		float radiusInterior2;
		float radiusPerimeter2;
		VectorI vCenter;
		switch(this) {
		case SPHERE:
			radius = forceFieldSetup.vMax.y;
			radiusInterior2 = (radius - halfThickness) * (radius - halfThickness);
			radiusPerimeter2 = (radius + halfThickness) * (radius + halfThickness);
			vCenter = new VectorI(0, 0, 0);
			for (int y = forceFieldSetup.vMin.y; y <= forceFieldSetup.vMax.y; y++) {
				int y2 = (y - vCenter.y) * (y - vCenter.y);
				for (int x = forceFieldSetup.vMin.x; x <= forceFieldSetup.vMax.x; x++) {
					int x2 = (x - vCenter.x) * (x - vCenter.x);
					for (int z = forceFieldSetup.vMin.z; z <= forceFieldSetup.vMax.z; z++) {
						int z2 = (z - vCenter.z) * (z - vCenter.z);
						if (x2 + y2 + z2 <= radiusPerimeter2) {
							mapVertexes.put(new VectorI(x, y, z), x2 + y2 + z2 >= radiusInterior2);
						}
					}
				}
			}
			break;
		
		case CYLINDER_H:
			radius = (forceFieldSetup.vMax.y + forceFieldSetup.vMax.z) / 2.0F;
			radiusInterior2 = (radius - halfThickness) * (radius - halfThickness);
			radiusPerimeter2 = (radius + halfThickness) * (radius + halfThickness);
			vCenter = new VectorI(0, 0, 0);
			for (int y = forceFieldSetup.vMin.y; y <= forceFieldSetup.vMax.y; y++) {
				int y2 = (y - vCenter.y) * (y - vCenter.y);
				for (int z = forceFieldSetup.vMin.z; z <= forceFieldSetup.vMax.z; z++) {
					int z2 = (z - vCenter.z) * (z - vCenter.z);
					if (y2 + z2 <= radiusPerimeter2) {
						boolean isPerimeter = y2 + z2 >= radiusInterior2;
						for (int x = forceFieldSetup.vMin.x; x <= forceFieldSetup.vMax.x; x++) {
							mapVertexes.put(new VectorI(x, y, z), isPerimeter);
						}
					}
				}
			}
			break;
		
		case CYLINDER_V:
			radius = (forceFieldSetup.vMax.x + forceFieldSetup.vMax.y) / 2.0F;
			radiusInterior2 = (radius - halfThickness) * (radius - halfThickness);
			radiusPerimeter2 = (radius + halfThickness) * (radius + halfThickness);
			vCenter = new VectorI(0, 0, 0);
			for (int x = forceFieldSetup.vMin.x; x <= forceFieldSetup.vMax.x; x++) {
				int x2 = (x - vCenter.x) * (x - vCenter.x);
				for (int y = forceFieldSetup.vMin.y; y <= forceFieldSetup.vMax.y; y++) {
					int y2 = (y - vCenter.y) * (y - vCenter.y);
					if (x2 + y2 <= radiusPerimeter2) {
						boolean isPerimeter = x2 + y2 >= radiusInterior2;
						for (int z = forceFieldSetup.vMin.z; z <= forceFieldSetup.vMax.z; z++) {
							mapVertexes.put(new VectorI(x, y, z), isPerimeter);
						}
					}
				}
			}
			break;
		
		case TUBE:
			radius =(forceFieldSetup.vMax.x + forceFieldSetup.vMax.z) / 2.0F;
			radiusInterior2 = (radius - halfThickness) * (radius - halfThickness);
			radiusPerimeter2 = (radius + halfThickness) * (radius + halfThickness);
			vCenter = new VectorI(0, 0, 0);
			for (int x = forceFieldSetup.vMin.x; x <= forceFieldSetup.vMax.x; x++) {
				int x2 = (x - vCenter.x) * (x - vCenter.x);
				for (int z = forceFieldSetup.vMin.z; z <= forceFieldSetup.vMax.z; z++) {
					int z2 = (z - vCenter.z) * (z - vCenter.z);
					if (x2 + z2 <= radiusPerimeter2) {
						boolean isPerimeter = x2 + z2 >= radiusInterior2;
						for (int y = forceFieldSetup.vMin.y; y <= forceFieldSetup.vMax.y; y++) {
							mapVertexes.put(new VectorI(x, y, z), isPerimeter);
						}
					}
				}
			}
			break;
		
		case CUBE:
			for (int y = forceFieldSetup.vMin.y; y <= forceFieldSetup.vMax.y; y++) {
				boolean yFace = Math.abs(y - forceFieldSetup.vMin.y) <= halfThickness
				             || Math.abs(y - forceFieldSetup.vMax.y) <= halfThickness;
				for (int x = forceFieldSetup.vMin.x; x <= forceFieldSetup.vMax.x; x++) {
					boolean xFace = Math.abs(x - forceFieldSetup.vMin.x) <= halfThickness
					             || Math.abs(x - forceFieldSetup.vMax.x) <= halfThickness;
					for (int z = forceFieldSetup.vMin.z; z <= forceFieldSetup.vMax.z; z++) {
						boolean zFace = Math.abs(z - forceFieldSetup.vMin.z) <= halfThickness
						             || Math.abs(z - forceFieldSetup.vMax.z) <= halfThickness;
						mapVertexes.put(new VectorI(x, y, z), xFace || yFace || zFace);
					}
				}
			}
			break;
		
		case PLANE:
			for (int y = forceFieldSetup.vMin.y; y <= forceFieldSetup.vMax.y; y++) {
				boolean yFace = Math.abs(y - forceFieldSetup.vMin.y) <= halfThickness
				             || Math.abs(y - forceFieldSetup.vMax.y) <= halfThickness;
				for (int x = forceFieldSetup.vMin.x; x <= forceFieldSetup.vMax.x; x++) {
					for (int z = forceFieldSetup.vMin.z; z <= forceFieldSetup.vMax.z; z++) {
						mapVertexes.put(new VectorI(x, y, z), yFace);
					}
				}
			}
			break;
		
		case TUNNEL:
			for (int y = forceFieldSetup.vMin.y; y <= forceFieldSetup.vMax.y; y++) {
				for (int x = forceFieldSetup.vMin.x; x <= forceFieldSetup.vMax.x; x++) {
					boolean xFace = Math.abs(x - forceFieldSetup.vMin.x) <= halfThickness
					             || Math.abs(x - forceFieldSetup.vMax.x) <= halfThickness;
					for (int z = forceFieldSetup.vMin.z; z <= forceFieldSetup.vMax.z; z++) {
						boolean isPerimeter = xFace
						                   || Math.abs(z - forceFieldSetup.vMin.z) <= halfThickness
						                   || Math.abs(z - forceFieldSetup.vMax.z) <= halfThickness;
						mapVertexes.put(new VectorI(x, y, z), isPerimeter);
					}
				}
			}
			break;
		
		default:
			break;
			
		}
		
		return mapVertexes;
	}
}
