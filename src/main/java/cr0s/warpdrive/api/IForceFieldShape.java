package cr0s.warpdrive.api;

import cr0s.warpdrive.data.ForceFieldSetup;
import cr0s.warpdrive.data.VectorI;

import java.util.Collection;
import java.util.Map;

public interface IForceFieldShape {
	
	/**
	 * Return a collection of coordinates to all blocks in a shape.
	 * Rotation will be applied afterward by callee.
	 * Warning: this is a threaded call, do NOT access world object during this call!
	 * Boolean is true when it's the perimeter, false when it's the interior
	 */
	Map<VectorI, Boolean> getVertexes(ForceFieldSetup forceFieldSetup);
}
