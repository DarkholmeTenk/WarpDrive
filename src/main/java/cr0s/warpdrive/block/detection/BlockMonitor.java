package cr0s.warpdrive.block.detection;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.data.CameraRegistryItem;
import cr0s.warpdrive.render.ClientCameraHandler;

public class BlockMonitor extends BlockAbstractContainer {
	private IIcon iconFront;
	private IIcon iconSide;
	
	public BlockMonitor() {
		super(Material.iron);
		isRotating = true;
		setBlockName("warpdrive.detection.Monitor");
	}
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		iconFront = iconRegister.registerIcon("warpdrive:detection/monitorFront");
		iconSide = iconRegister.registerIcon("warpdrive:detection/monitorSide");
	}
	
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int metadata  = world.getBlockMetadata(x, y, z);
		return side == metadata ? iconFront : iconSide;
	}
	
	@Override
	public IIcon getIcon(int side, int metadata) {
		return side == 3 ? iconFront : iconSide;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
		// Monitor is only reacting client side
		if (!world.isRemote) {
			return false;
		}
		
		if (entityPlayer.getHeldItem() == null) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			
			if (tileEntity instanceof TileEntityMonitor) {
				int videoChannel = ((TileEntityMonitor)tileEntity).getVideoChannel();
				CameraRegistryItem camera = WarpDrive.cameras.getCameraByVideoChannel(world, videoChannel);
				if (camera == null || entityPlayer.isSneaking()) {
					WarpDrive.addChatMessage(entityPlayer, ((TileEntityMonitor)tileEntity).getStatus());
					return true;
				} else {
					WarpDrive.addChatMessage(entityPlayer, StatCollector.translateToLocalFormatted("warpdrive.monitor.viewingCamera",
							videoChannel,
							camera.position.chunkPosX,
							camera.position.chunkPosY,
							camera.position.chunkPosZ ));
					ClientCameraHandler.setupViewpoint(
							camera.type, entityPlayer, entityPlayer.rotationYaw, entityPlayer.rotationPitch,
							x, y, z, this,
							camera.position.chunkPosX, camera.position.chunkPosY, camera.position.chunkPosZ, world.getBlock(camera.position.chunkPosX, camera.position.chunkPosY, camera.position.chunkPosZ));
				}
			}
		}
		
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityMonitor();
	}
}