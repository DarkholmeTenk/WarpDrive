package cr0s.warpdrive.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumCameraType;

public class ClientCameraHandler {
	public static boolean isOverlayEnabled = false;
	
	public static EnumCameraType overlayType = null;
	public static int zoomIndex = 0;
	public static String overlayLoggingMessage = "";
	public static float originalFOV = 70.0F;
	public static float originalSensitivity = 100.0F;
	
	public static EntityPlayer entityPlayer;
	public static int dimensionId = -666;
	public static BlockPos blockPosCheck1, blockPosCheck2;
	public static IBlockState blockStateCheck1, blockStateCheck2;
	
	public ClientCameraHandler() {
		Minecraft mc = Minecraft.getMinecraft();
		
		if (WarpDriveConfig.LOGGING_CAMERA) {
			WarpDrive.logger.info("FOV is " + mc.gameSettings.fovSetting + " Sensitivity is " + mc.gameSettings.mouseSensitivity);
		}
	}
	
	public static void setupViewpoint(final EnumCameraType enumCameraType, EntityPlayer parEntityPlayer, final float initialYaw, final float initialPitch,
	                                  final BlockPos blockPosMonitor, final IBlockState blockStateMonitor,
	                                  final BlockPos blockPosCamera, final IBlockState blockStateCamera) {
		Minecraft mc = Minecraft.getMinecraft();
		
		if (parEntityPlayer == null) {
			WarpDrive.logger.error("setupViewpoint with null player => denied");
			return;
		}
		
		// Save initial state
		originalFOV = mc.gameSettings.fovSetting;
		originalSensitivity = mc.gameSettings.mouseSensitivity;
		overlayType = enumCameraType;
		entityPlayer = parEntityPlayer;
		dimensionId = entityPlayer.worldObj.provider.getDimension();
		blockPosCheck1 = blockPosMonitor;
		blockStateCheck1 = blockStateMonitor;
		blockPosCheck2 = blockPosCamera;
		blockStateCheck2 = blockStateCamera;
		
		// Spawn camera entity
		EntityCamera entityCamera = new EntityCamera(entityPlayer.worldObj, blockPosCamera.getX(), blockPosCamera.getY(), blockPosCamera.getZ(), entityPlayer);
		entityPlayer.worldObj.spawnEntityInWorld(entityCamera);
		// entityCamera.setPositionAndUpdate(camera_x + 0.5D, camera_y + 0.5D, camera_z + 0.5D);
		entityCamera.setLocationAndAngles(blockPosCamera.getX() + 0.5D, blockPosCamera.getY() + 0.5D, blockPosCamera.getZ() + 0.5D, initialYaw, initialPitch);
		
		// Update view
		if (WarpDriveConfig.LOGGING_CAMERA) {
			WarpDrive.logger.info("Setting viewpoint to " + entityCamera);
		}
		mc.setRenderViewEntity(entityCamera);
		mc.gameSettings.thirdPersonView = 0;
		refreshViewPoint();
		isOverlayEnabled = true;
		
		Keyboard.enableRepeatEvents(true);
	}
	
	private static void refreshViewPoint() {
		Minecraft mc = Minecraft.getMinecraft();
		
		switch (zoomIndex) {
		case 0:
			mc.gameSettings.fovSetting = originalFOV;
			mc.gameSettings.mouseSensitivity = originalSensitivity / 2.0F;
			break;
			
		case 1:
			mc.gameSettings.fovSetting = originalFOV / 1.5F;
			mc.gameSettings.mouseSensitivity = originalSensitivity / 3.0F;
			break;
			
		case 2:
			mc.gameSettings.fovSetting = originalFOV / 3.0F;
			mc.gameSettings.mouseSensitivity = originalSensitivity / 6.0F;
			break;
			
		case 3:
			mc.gameSettings.fovSetting = originalFOV / 4.5F;
			mc.gameSettings.mouseSensitivity = originalSensitivity / 9.0F;
			break;
			
		default:
			mc.gameSettings.fovSetting = originalFOV;
			mc.gameSettings.mouseSensitivity = originalSensitivity / 2.0F;
			break;
		}
	}
	
	public static void zoom() {
		Minecraft mc = Minecraft.getMinecraft();
		
		zoomIndex = (zoomIndex + 1) % 4;
		refreshViewPoint();
		if (WarpDriveConfig.LOGGING_CAMERA) {
			mc.thePlayer.sendChatMessage("changed to fovSetting " + mc.gameSettings.fovSetting + " mouseSensitivity " + mc.gameSettings.mouseSensitivity);
		}
	}
	
	public static void resetViewpoint() {
		Minecraft mc = Minecraft.getMinecraft();
		if (entityPlayer != null) {
			mc.setRenderViewEntity(entityPlayer);
			entityPlayer = null;
			if (WarpDriveConfig.LOGGING_CAMERA) {
				WarpDrive.logger.info("Resetting viewpoint");
			}
		} else {
			WarpDrive.logger.error("resetting viewpoint with invalid player entity?");
		}
		
		Keyboard.enableRepeatEvents(false);
		
		isOverlayEnabled = false;
		mc.gameSettings.thirdPersonView = 0;
		mc.gameSettings.fovSetting = originalFOV;
		mc.gameSettings.mouseSensitivity = originalSensitivity;
		
		entityPlayer = null;
		dimensionId = -666;
	}
	
	public static boolean isValidContext(World worldObj) {
		if (worldObj == null || worldObj.provider.getDimension() != dimensionId) {
			return false;
		}
		if (!worldObj.getBlockState(blockPosCheck1).getBlock().isAssociatedBlock(blockStateCheck1.getBlock())) {
			WarpDrive.logger.error("checking viewpoint, found invalid block1 at (" + blockPosCheck1.getX() + " " + blockPosCheck1.getY() + " " + blockPosCheck1.getZ() + ")");
			return false;
		}
		if (!worldObj.getBlockState(blockPosCheck2).getBlock().isAssociatedBlock(blockStateCheck2.getBlock())) {
			WarpDrive.logger.error("checking viewpoint, found invalid block2 at (" + blockPosCheck2.getX() + " " + blockPosCheck2.getY() + " " + blockPosCheck2.getZ() + ")");
			return false;
		}
		return true;
	}
	
	@SubscribeEvent
	public void onEvent(ClientDisconnectionFromServerEvent event) {
		if (isOverlayEnabled) {
			resetViewpoint();
		}
	}
}
