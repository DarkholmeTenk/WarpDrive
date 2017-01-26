package cr0s.warpdrive.data;

import java.util.ArrayList;
import java.util.LinkedList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import cr0s.warpdrive.LocalProfiler;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.movement.TileEntityShipCore;
import cr0s.warpdrive.block.movement.TileEntityShipCore.EnumShipCoreMode;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.StarMapRegistryItem.EnumStarMapEntryType;

/**
 * Registry of all known ships, jumpgates, etc. in the world
 * 
 * @author LemADEC
 */
public class StarMapRegistry {
	private final LinkedList<StarMapRegistryItem> registry;
	
	public StarMapRegistry() {
		registry = new LinkedList<>();
	}
	
	public int searchInRegistry(StarMapRegistryItem entryKey) {
		int res = -1;
		
		for (int i = 0; i < registry.size(); i++) {
			StarMapRegistryItem entry = registry.get(i);
			
			if (entry.dimensionId == entryKey.dimensionId && entry.x == entryKey.x && entry.y == entryKey.y && entry.z == entryKey.z) {
				return i;
			}
		}
		
		return res;
	}
	
	public boolean isInRegistry(StarMapRegistryItem entryKey) {
		return (searchInRegistry(entryKey) != -1);
	}
	
	public void updateInRegistry(StarMapRegistryItem entryKey) {
		int idx = searchInRegistry(entryKey);
		
		// update
		if (idx != -1) {
			registry.set(idx, entryKey);
		} else {
			registry.add(entryKey);
			if (WarpDriveConfig.LOGGING_STARMAP) {
				printRegistry("added");
			}
		}
	}
	
	public void removeFromRegistry(StarMapRegistryItem entryKey) {
		int idx = searchInRegistry(entryKey);
		
		if (idx != -1) {
			registry.remove(idx);
			if (WarpDriveConfig.LOGGING_STARMAP) {
				printRegistry("removed");
			}
		}
	}
	
	public ArrayList<StarMapRegistryItem> radarScan(TileEntity tileEntity, final int radius) {
		ArrayList<StarMapRegistryItem> res = new ArrayList<>(registry.size());
		cleanup();
		
		// printRegistry();
		int radius2 = radius * radius;
		for (StarMapRegistryItem entry : registry) {
			double dX = entry.x - tileEntity.getPos().getX();
			double dY = entry.y - tileEntity.getPos().getY();
			double dZ = entry.z - tileEntity.getPos().getZ();
			double distance2 = dX * dX + dY * dY + dZ * dZ;
			
			if ( distance2 <= radius2
			  && (entry.isolationRate == 0.0D || tileEntity.getWorld().rand.nextDouble() >= entry.isolationRate)
			  && (entry.getSpaceCoordinates() != null)) {
				res.add(entry);
			}
		}
		
		return res;
	}
	
	public void printRegistry(final String trigger) {
		WarpDrive.logger.info("Starmap registry (" + registry.size() + " entries after " + trigger + "):");
		
		for (StarMapRegistryItem entry : registry) {
			WarpDrive.logger.info("- " + entry.type + " '" + entry.name + "' @ "
					+ entry.dimensionId + ": " + entry.x + ", " + entry.y + ", " + entry.z
					+ " with " + entry.isolationRate + " isolation rate");
		}
	}
	
	public boolean isWarpCoreIntersectsWithOthers(TileEntityShipCore core) {
		StringBuilder reason = new StringBuilder();
		AxisAlignedBB aabb1, aabb2;
		cleanup();
		
		core.validateShipSpatialParameters(reason);
		aabb1 = new AxisAlignedBB(core.minX, core.minY, core.minZ, core.maxX, core.maxY, core.maxZ);
		
		for (StarMapRegistryItem entry : registry) {
			// Skip cores in other worlds
			if (entry.dimensionId != core.getWorld().provider.getDimension()) {
				continue;
			}
			// only check cores
			if (entry.type != EnumStarMapEntryType.SHIP) {
				continue;
			}
			
			// Skip self
			if (entry.x == core.getPos().getX() && entry.y == core.getPos().getY() && entry.z == core.getPos().getZ()) {
				continue;
			}
			
			// Skip missing ship cores
			TileEntity tileEntity = core.getWorld().getTileEntity(new BlockPos(entry.x, entry.y, entry.z));
			if (!(tileEntity instanceof TileEntityShipCore)) {
				continue;
			}
			TileEntityShipCore shipCore = (TileEntityShipCore) tileEntity;
			
			// Skip offline warp cores
			if (shipCore.controller == null || shipCore.controller.getMode() == EnumShipCoreMode.IDLE || !shipCore.validateShipSpatialParameters(reason)) {
				continue;
			}
			
			// Search for nearest warp cores
			double d3 = entry.x - core.getPos().getX();
			double d4 = entry.y - core.getPos().getY();
			double d5 = entry.z - core.getPos().getZ();
			double distance2 = d3 * d3 + d4 * d4 + d5 * d5;
			
			if (distance2 <= ((2 * WarpDriveConfig.SHIP_MAX_SIDE_SIZE) - 1) * ((2 * WarpDriveConfig.SHIP_MAX_SIDE_SIZE) - 1)) {
				// Compare warp-fields for intersection
				aabb2 = new AxisAlignedBB(entry.minX, entry.minY, entry.minZ, entry.maxX, entry.maxY, entry.maxZ);
				if (aabb1.intersectsWith(aabb2)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	// do not call during tileEntity construction (readFromNBT and validate)
	private void cleanup() {
		LocalProfiler.start("StarMapRegistry cleanup");
		
		StarMapRegistryItem entry;
		boolean isValid; 
		for (int i = registry.size() - 1; i >= 0; i--) {
			entry = registry.get(i);
			isValid = false;
			if (entry != null) {
				// skip unloaded worlds / chunks
				WorldServer world = entry.getWorldServerIfLoaded();
				if (world == null) {
					continue;
				}
				
				// get block and tile entity
				IBlockState blockState = world.getBlockState(new BlockPos(entry.x, entry.y, entry.z));
				
				TileEntity tileEntity = world.getTileEntity(new BlockPos(entry.x, entry.y, entry.z));
				isValid = true;
				switch (entry.type) {
				case UNDEFINED: break;
				case SHIP:
					isValid = blockState.getBlock() == WarpDrive.blockShipCore && tileEntity != null && !tileEntity.isInvalid();
					break;
				case JUMPGATE: break;
				case PLANET: break;
				case STAR: break;
				case STRUCTURE: break;
				case WARP_ECHO: break;
				default: break;
				}
			}
			
			if (!isValid) {
				if (WarpDriveConfig.LOGGING_STARMAP) {
					if (entry == null) {
						WarpDrive.logger.info("Cleaning up starmap object ~null~");
					} else {
						WarpDrive.logger.info("Cleaning up starmap object " + entry.type + " at "
								+ entry.dimensionId + " " + entry.x + " " + entry.y + " " + entry.z);
					}
				}
				registry.remove(i);
			}
		}
		
		LocalProfiler.stop();
	}
}
