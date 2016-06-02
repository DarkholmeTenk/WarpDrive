package cr0s.warpdrive.block.energy;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.BlockAbstractContainer;

public class BlockIC2reactorLaserMonitor extends BlockAbstractContainer {
	static IIcon[] icons;
	
	public BlockIC2reactorLaserMonitor() {
		super(Material.iron);
		setBlockName("warpdrive.energy.IC2ReactorLaserMonitor");
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int i) {
		return new TileEntityIC2reactorLaserMonitor();
	}
	
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		icons = new IIcon[3];
		icons[0] = par1IconRegister.registerIcon("warpdrive:energy/IC2reactorLaserMonitorNotConnected");
		icons[1] = par1IconRegister.registerIcon("warpdrive:energy/IC2reactorLaserMonitorConnectedNotPowered");
		icons[2] = par1IconRegister.registerIcon("warpdrive:energy/IC2reactorLaserMonitorConnectedPowered");
	}
	
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null || !(tileEntity instanceof TileEntityIC2reactorLaserMonitor)) {
			return icons[0];
		}
		
		if (((TileEntityIC2reactorLaserMonitor)tileEntity).isSideActive(side)) {
			if ((meta & 8) == 0) {
				return icons[1];
			} else {
				return icons[2];
			}
		}
		
		return icons[0];
	}
	
	@Override
	public IIcon getIcon(int side, int metadata) {
		if (side == 4) {
			return icons[1];
		} else {
			return icons[0];
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		
		if (entityPlayer.getHeldItem() == null) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityIC2reactorLaserMonitor) {
				WarpDrive.addChatMessage(entityPlayer, ((TileEntityIC2reactorLaserMonitor) tileEntity).getStatus());
				return true;
			}
		}
		
		return false;
	}
}
