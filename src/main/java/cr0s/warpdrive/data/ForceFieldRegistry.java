package cr0s.warpdrive.data;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBeamFrequency;
import cr0s.warpdrive.config.WarpDriveConfig;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

import java.util.*;

/**
 * Registry of all known forcefield in the loaded worlds, grouped by frequency
 * 
 * @author LemADEC
 */
public class ForceFieldRegistry {
	private static final int FORCE_FIELD_REGISTRY_DEFAULT_ENTRIES_PER_FREQUENCY = 10;
	private static final HashMap<Integer, HashSet<GlobalPosition>> registry = new HashMap<>();
	
	public static Set<TileEntity> getTileEntities(final int beamFrequency) {
		Set<TileEntity> setEntries = new HashSet<>();
		Set<GlobalPosition> setGlobalPositions = registry.get(beamFrequency);
		if (setGlobalPositions == null) {
			return setEntries;
		}
		for (Iterator<GlobalPosition> iterator = setGlobalPositions.iterator(); iterator.hasNext();) {
			GlobalPosition globalPosition = iterator.next();
			WorldServer world = globalPosition.getWorldServerIfLoaded();
			if (world != null) {
				TileEntity tileEntity = world.getTileEntity(globalPosition.x, globalPosition.y, globalPosition.z);
				if ((tileEntity instanceof IBeamFrequency) && ((IBeamFrequency)tileEntity).getBeamFrequency() == beamFrequency) {
					setEntries.add(tileEntity);
					continue;
				}
			}
			// world isn't loaded or block no longer exist => remove from registry
			iterator.remove();
			if (WarpDriveConfig.LOGGING_FORCEFIELD_REGISTRY) {
				printRegistry("removed");
			}
		}
		
		return setEntries;
	}
	
	public static boolean isInRegistry(final int beamFrequency) {
		return !getTileEntities(beamFrequency).isEmpty();
	}
	
	public static void updateInRegistry(IBeamFrequency tileEntity) {
		assert(tileEntity instanceof TileEntity);
		
		HashSet<GlobalPosition> setGlobalPositions = registry.get(tileEntity.getBeamFrequency());
		if (setGlobalPositions == null) {
			setGlobalPositions = new HashSet<>(FORCE_FIELD_REGISTRY_DEFAULT_ENTRIES_PER_FREQUENCY);
		}
		for (Iterator<GlobalPosition> iterator = setGlobalPositions.iterator(); iterator.hasNext();) {
			GlobalPosition globalPosition = iterator.next();
			if (globalPosition.equals(tileEntity)) {
				// already registered
				return;
			}
		}
		// not found => add
		setGlobalPositions.add(new GlobalPosition((TileEntity)tileEntity));
		registry.put(tileEntity.getBeamFrequency(), setGlobalPositions);
		if (WarpDriveConfig.LOGGING_FORCEFIELD_REGISTRY) {
			printRegistry("added");
		}
	}
	
	public static void removeFromRegistry(IBeamFrequency tileEntity) {
		assert(tileEntity instanceof TileEntity);
		
		Set<GlobalPosition> setGlobalPositions = registry.get(tileEntity.getBeamFrequency());
		if (setGlobalPositions == null) {
			// noting to remove
			return;
		}
		for (Iterator<GlobalPosition> iterator = setGlobalPositions.iterator(); iterator.hasNext();) {
			GlobalPosition globalPosition = iterator.next();
			if (globalPosition.equals(tileEntity)) {
				// found it, remove and exit
				iterator.remove();
				return;
			}
		}
		// not found => ignore it
	}
	
	public static void printRegistry(final String trigger) {
		WarpDrive.logger.info("Forcefield registry (" + registry.size() + " entries after " + trigger + "):");
		
		for (Map.Entry<Integer, HashSet<GlobalPosition>> entry : registry.entrySet()) {
			String message = "";
			for (GlobalPosition globalPosition : entry.getValue()) {
				if (!message.isEmpty()) {
					message += ", ";
				}
				message += globalPosition.dimensionId + ": " + globalPosition.x + " " + globalPosition.y + " " + globalPosition.z;
			}
			WarpDrive.logger.info("- " + entry.getValue().size() + " entries at frequency " + entry.getKey() + ": " + message);
		}
	}
}
