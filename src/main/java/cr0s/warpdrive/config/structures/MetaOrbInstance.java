package cr0s.warpdrive.config.structures;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import cr0s.warpdrive.LocalProfiler;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.config.filler.Filler;
import cr0s.warpdrive.config.structures.Orb.OrbShell;
import cr0s.warpdrive.data.VectorI;

public class MetaOrbInstance extends OrbInstance {
	private static final int CORE_MAX_TRIES = 10;
	protected final MetaShell metaShell;
	
	public MetaOrbInstance(MetaOrb asteroid, Random random) {
		super(asteroid, random);
		metaShell = new MetaShell(asteroid, random);
		// FIXME setRadius(Math.round(totalThickness + metaShell.radius));
	}
	
	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		if (WarpDriveConfig.LOGGING_WORLDGEN) {
			WarpDrive.logger.info("Generating MetaOrb " + structure.name + " of " + metaShell.count  + " cores with radius of " + totalThickness);
		}
		LocalProfiler.start("[AsteroidInstance] Generating MetaOrb " + structure.name + " of " + metaShell.count + " cores with radius of " + totalThickness);
		
		int y2 = Math.min(WarpDriveConfig.SPACE_GENERATOR_Y_MAX_BORDER - totalThickness - (int) metaShell.radius,
			  Math.max(y, WarpDriveConfig.SPACE_GENERATOR_Y_MIN_BORDER + totalThickness + (int) metaShell.radius));
		if (((MetaOrb)structure).metaShell == null) {
			return super.generate(world, random, x, y2, z);
		}
		
		// generate an abstract form for the core
		for (VectorI location: metaShell.locations) {
			// place core block
			if (metaShell.block != null) {
				world.setBlock(x + location.x, y2 + location.y, z + location.z, metaShell.block, metaShell.metadata, 2);
			}
			
			// calculate distance to borders of generation area
			int maxRadX = totalThickness - Math.abs(location.x);
			int maxRadY = totalThickness - Math.abs(location.y);
			int maxRadZ = totalThickness - Math.abs(location.z);
			// keep the biggest one to have a bumpy effect
			int maxLocalRadius = Math.max(maxRadX, Math.max(maxRadY, maxRadZ));
			// enforce a minimum thickness to prevent lone core blocks
			// (see case where core radius is close to totalThickness)
			maxLocalRadius = Math.max(minThickness, maxLocalRadius);
			
			// Generate shell
			addShell(world, new VectorI(x, y2, z).add(location), maxLocalRadius);
		}
		
		int minY_clamped = Math.max(0, y2 - totalThickness);
		int maxY_clamped = Math.min(255, y2 + totalThickness);
		for (int xIndex = x - totalThickness; xIndex <= x + totalThickness; xIndex++) {
			for (int zIndex = z - totalThickness; zIndex <= z + totalThickness; zIndex++) {
				for (int yIndex = minY_clamped; yIndex <= maxY_clamped; yIndex++) {
					if (world.getBlock(xIndex, yIndex, zIndex) != Blocks.air) {
						world.markBlockForUpdate(xIndex, yIndex, zIndex);
					}
				}
			}
		}
		
		LocalProfiler.stop();
		return false;
	}
	
	private void addShell(World world, VectorI location, int radius) {
		double sqRadius = radius * radius;
		// iterate all blocks within cube with side 2 * radius
		for(int x = location.x - radius; x <= location.x + radius; x++) {
			int dX2 = (x - location.x) * (x - location.x);
			for(int y = location.y - radius; y <= location.y + radius; y++) {
				int dX2Y2 = dX2 + (y - location.y) * (y - location.y);
				for(int z = location.z - radius; z <= location.z + radius; z++) {
					// current radius
					int sqRange = dX2Y2 + (location.z - z) * (location.z - z);
					
					// if inside radius
					if (sqRange <= sqRadius && isReplaceableOreGen(world, x, y, z)) {
						OrbShell shell = getShellForSqRadius(sqRange);
						Filler filler = shell.getRandomBlock(world.rand);
						filler.setBlock(world, x, y, z);
					}
				}
			}
		}
	}
	
	private static boolean isReplaceableOreGen(World world, int x, int y, int z) {
		return world.getBlock(x, y, z).isReplaceableOreGen(world, x, y, z, Blocks.air);
	}
	
	public class MetaShell {
		protected final int count;
		protected final double radius;
		protected ArrayList<VectorI> locations;
		protected final Block block;
		protected final int metadata;
		
		public MetaShell(MetaOrb asteroid, Random random) {
			if (asteroid.metaShell == null) {
				count = 1;
				radius = 0;
				block = null;
				metadata = 0;
				return;
			}
			count = randomRange(random, asteroid.metaShell.minCount, asteroid.metaShell.maxCount);
			radius = Math.max(asteroid.metaShell.minRadius, asteroid.metaShell.relativeRadius * totalThickness);
			block = asteroid.metaShell.block;
			metadata = asteroid.metaShell.metadata;
			
			// evaluate core positions
			locations = new ArrayList<>();
			double diameter = Math.max(1D, 2 * radius);
			double xMin = -radius;
			double yMin = -radius;
			double zMin = -radius;
			
			for (int index = 0; index < count; index++) {
				boolean found = false;
				
				for(int step = 0; step < CORE_MAX_TRIES && !found; step++) {
					VectorI location = new VectorI(
							(int)Math.round(xMin + diameter * random.nextDouble()),
							(int)Math.round(yMin + diameter * random.nextDouble()),
							(int)Math.round(zMin + diameter * random.nextDouble()));
					if (!locations.contains(location)) {
						locations.add(location);
						found = true;
					}
				}
			}
		}
	}
}
