package cr0s.warpdrive.data;

import net.minecraft.util.AxisAlignedBB;
import cr0s.warpdrive.world.JumpgateGenerator;

public class Jumpgate {
	public String name;
	public int xCoord, yCoord, zCoord;
	
	public Jumpgate(final String name, final int x, final int y, final int z) {
		this.name = name;
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
	}
	
	public Jumpgate(final String line) {
		String[] params = line.split(":");
		
		if (params.length < 4) {
			return;
		}
		
		name = params[0];
		xCoord = Integer.parseInt(params[1]);
		yCoord = Integer.parseInt(params[2]);
		zCoord = Integer.parseInt(params[3]);
	}
	
	public AxisAlignedBB getGateAABB() {
		int xMin, yMin, zMin;
		int xMax, yMax, zMax;
		xMin = xCoord - (JumpgateGenerator.GATE_LENGTH_HALF * 2);
		xMax = xCoord + (JumpgateGenerator.GATE_LENGTH_HALF * 2);
		yMin = yCoord - (JumpgateGenerator.GATE_SIZE_HALF);
		yMax = yCoord + (JumpgateGenerator.GATE_SIZE_HALF);
		zMin = zCoord - (JumpgateGenerator.GATE_SIZE_HALF);
		zMax = zCoord + (JumpgateGenerator.GATE_SIZE_HALF);
		return AxisAlignedBB.getBoundingBox(xMin, yMin, zMin, xMax, yMax, zMax);
	}
	
	@Override
	public String toString() {
		return name + ":" + xCoord + ":" + yCoord + ":" + zCoord;
	}
	
	public String toNiceString() {
		return name + " (" + xCoord + ", " + yCoord + ", " + zCoord + ")";
	}
}