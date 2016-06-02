package cr0s.warpdrive.data;

import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;

public class CamerasRegistry {
	private LinkedList<CameraRegistryItem> registry;
	
	public CamerasRegistry() {
		registry = new LinkedList<>();
	}
	
	public CameraRegistryItem getCameraByVideoChannel(World world, int videoChannel) {
		CameraRegistryItem cam;
		for (Iterator<CameraRegistryItem> it = registry.iterator(); it.hasNext();) {
			cam = it.next();
			if (cam.videoChannel == videoChannel && cam.dimensionId == world.provider.dimensionId) {
				if (isCamAlive(world, cam)) {
					return cam;
				} else {
					if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
						WarpDrive.logger.info("Removing 'dead' camera at "
							+ cam.position.chunkPosX + ", " + cam.position.chunkPosY + ", " + cam.position.chunkPosZ + " (while searching)");
					}
					it.remove();
				}
			}
		}
		
		// not found => dump registry
		printRegistry(world);
		return null;
	}
	
	private CameraRegistryItem getCamByPosition(World world, ChunkPosition position) {
		CameraRegistryItem cam;
		for (Iterator<CameraRegistryItem> it = registry.iterator(); it.hasNext();) {
			cam = it.next();
			if (cam.position.chunkPosX == position.chunkPosX && cam.position.chunkPosY == position.chunkPosY && cam.position.chunkPosZ == position.chunkPosZ
					&& cam.dimensionId == world.provider.dimensionId) {
				return cam;
			}
		}
		
		return null;
	}
	
	private static boolean isCamAlive(World world, CameraRegistryItem cam) {
		if (world.provider.dimensionId != cam.dimensionId) {
			WarpDrive.logger.error("Inconsistent worldObj with camera " + world.provider.dimensionId + " vs " + cam.dimensionId);
			return false;
		}
		
		if (!world.getChunkFromBlockCoords(cam.position.chunkPosX, cam.position.chunkPosZ).isChunkLoaded) {
			if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
				WarpDrive.logger.info("Reporting an 'unloaded' camera in dimension " + cam.dimensionId + " at "
						+ cam.position.chunkPosX + ", " + cam.position.chunkPosY + ", " + cam.position.chunkPosZ);
			}
			return false;
		}
		Block block = world.getBlock(cam.position.chunkPosX, cam.position.chunkPosY, cam.position.chunkPosZ);
		if ((block != WarpDrive.blockCamera) && (block != WarpDrive.blockLaserCamera)) {
			if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
				WarpDrive.logger.info("Reporting a 'dead' camera in dimension " + cam.dimensionId + " at "
						+ cam.position.chunkPosX + ", " + cam.position.chunkPosY + ", " + cam.position.chunkPosZ);
			}
			return false;
		}
		
		return true;
	}
	
	private void removeDeadCams(World world) {
		// LocalProfiler.start("CamRegistry Removing dead cameras");
		
		CameraRegistryItem cam;
		for (Iterator<CameraRegistryItem> it = registry.iterator(); it.hasNext();) {
			cam = it.next();
			if (!isCamAlive(world, cam)) {
				if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
					WarpDrive.logger.info("Removing 'dead' camera in dimension " + cam.dimensionId + " at "
							+ cam.position.chunkPosX + ", " + cam.position.chunkPosY + ", " + cam.position.chunkPosZ);
				}
				it.remove();
			}
		}
		
		// LocalProfiler.stop();
	}
	
	public void removeFromRegistry(World world, ChunkPosition position) {
		CameraRegistryItem cam = getCamByPosition(world, position);
		if (cam != null) {
			if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
				WarpDrive.logger.info("Removing camera by request in dimension " + cam.dimensionId + " at "
						+ cam.position.chunkPosX + ", " + cam.position.chunkPosY + ", " + cam.position.chunkPosZ);
			}
			registry.remove(cam);
		}
	}
	
	public void updateInRegistry(World world, ChunkPosition position, int videoChannel, CameraType cameraType) {
		CameraRegistryItem cam = new CameraRegistryItem(world, position, videoChannel, cameraType);
		// WarpDrive.debugPrint("updateInRegistry " + cam.position.x + ", " + cam.position.y + ", " + cam.position.z);
		removeDeadCams(world);
		
		if (isCamAlive(world, cam)) {
			CameraRegistryItem existingCam = getCamByPosition(world, cam.position);
			if (existingCam == null) {
				if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
					WarpDrive.logger.info("Adding 'live' camera at "
							+ cam.position.chunkPosX + ", " + cam.position.chunkPosY + ", " + cam.position.chunkPosZ
							+ " with video channel '" + cam.videoChannel + "'");
				}
				registry.add(cam);
			} else if (existingCam.videoChannel != cam.videoChannel) {
				if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
					WarpDrive.logger.info("Updating 'live' camera at "
							+ cam.position.chunkPosX + ", " + cam.position.chunkPosY + ", " + cam.position.chunkPosZ
							+ " from video channel '" + existingCam.videoChannel + "' to video channel '" + cam.videoChannel + "'");
				}
				existingCam.videoChannel = cam.videoChannel;
			}
		} else {
			if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
				WarpDrive.logger.info("Unable to update 'dead' camera at " + cam.position.chunkPosX + ", " + cam.position.chunkPosY + ", " + cam.position.chunkPosZ);
			}
		}
	}
	
	public void printRegistry(World world) {
		WarpDrive.logger.info("Cameras registry for dimension " + world.provider.dimensionId + ":");
		
		for (CameraRegistryItem cam : registry) {
			WarpDrive.logger.info("- " + cam.videoChannel + " (" + cam.position.chunkPosX + ", " + cam.position.chunkPosY + ", " + cam.position.chunkPosZ + ")");
		}
	}
}
