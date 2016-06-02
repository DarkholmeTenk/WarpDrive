package cr0s.warpdrive.config.structures;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.config.structures.Orb.OrbShell;
import cr0s.warpdrive.world.EntitySphereGen;
import cr0s.warpdrive.world.EntityStarCore;
import cr0s.warpdrive.world.WorldGenSmallShip;

public class OrbInstance extends AbstractInstance {
	protected OrbShell[] orbShells;
	protected int[] orbShellThicknesses;
	protected int totalThickness;
	protected int minThickness;
	protected String schematicName;
	
	// internal look-up table to accelerate computations
	private OrbShell[] sqRadiusToOrbShell;
	
	public OrbInstance(Orb orb, Random random) {
		super(orb, random);
		orbShells = new OrbShell[orb.orbShells.length];
		orbShellThicknesses = new int[orb.orbShells.length];
		totalThickness = 0;
		minThickness = 0;
		int orbShellIndexOut = 0;
		for(int orbShellIndexIn = 0; orbShellIndexIn < orb.orbShells.length; orbShellIndexIn++) {
			OrbShell orbShell = orb.orbShells[orbShellIndexIn].instantiate(random);
			if (orbShell != null) {
				orbShells[orbShellIndexOut] = orbShell;
				int thickness = randomRange(random, orbShell.minThickness, orbShell.maxThickness);
				orbShellThicknesses[orbShellIndexOut] = thickness;
				totalThickness += thickness;
				minThickness += orbShell.minThickness;
				orbShellIndexOut++;
			}
		}
		
		sqRadiusToOrbShell = new OrbShell[totalThickness * totalThickness];
		for(int sqRadius = 0; sqRadius < sqRadiusToOrbShell.length; sqRadius++) {
			int cumulatedRange = 0;
			for (int shellIndex = 0; shellIndex < orbShells.length; shellIndex++) {
				cumulatedRange += orbShellThicknesses[shellIndex];
				if (sqRadius <= cumulatedRange * cumulatedRange) {
					sqRadiusToOrbShell[sqRadius] = orbShells[shellIndex];
					break;
				}
			}
		}
		
		schematicName = orb.schematicName;
	}
	
	public OrbInstance(NBTTagCompound tag) {
		super(tag);
		// TODO not implemented
	}
	
	@Override
	public void WriteToNBT(NBTTagCompound tag) {
		super.WriteToNBT(tag);
		// TODO not implemented
	}
	
	public int getTotalThickness() {
		return totalThickness;
	}
	
	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		boolean hasShip = schematicName != null && !schematicName.isEmpty();
		int y2 = Math.min(WarpDriveConfig.SPACE_GENERATOR_Y_MAX_BORDER - totalThickness,
			  Math.max(y, WarpDriveConfig.SPACE_GENERATOR_Y_MIN_BORDER + totalThickness));
		if (hasShip) {
			new WorldGenSmallShip(random.nextFloat() < 0.2F).generate(world, random, x, y2, z);
		}
		EntitySphereGen entitySphereGen = new EntitySphereGen(world, x, y2, z, this, !hasShip);
		world.spawnEntityInWorld(entitySphereGen);
		if (((Orb)structure).hasStarCore) {
			return world.spawnEntityInWorld(new EntityStarCore(world, x, y2, z, totalThickness));
		}
		return true;
	}
	
	public OrbShell getShellForSqRadius(final double sqRadius) {
		int intSqRadius = (int)Math.round(sqRadius);
		if (intSqRadius < sqRadiusToOrbShell.length) {
			return sqRadiusToOrbShell[intSqRadius];
		} else {
			return sqRadiusToOrbShell[sqRadiusToOrbShell.length - 1];
		}
	}
}
