package cr0s.warpdrive.block.movement;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.BlockAbstractContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockTransporter extends BlockAbstractContainer {

	private IIcon[] iconBuffer;

	public BlockTransporter() {
		super(Material.iron);
		setBlockName("warpdrive.movement.Transporter");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityTransporter();
	}

	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		iconBuffer = new IIcon[3];
		// Solid textures
		iconBuffer[0] = par1IconRegister.registerIcon("warpdrive:movement/transporterBottom");
		iconBuffer[1] = par1IconRegister.registerIcon("warpdrive:movement/transporterTop");
		iconBuffer[2] = par1IconRegister.registerIcon("warpdrive:movement/transporterSide");
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		if (side == 0 || side == 1) {
			return iconBuffer[side];
		}

		return iconBuffer[2];
	}
	
	
	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		
		if (entityPlayer.getHeldItem() == null) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityTransporter) {
				WarpDrive.addChatMessage(entityPlayer, ((TileEntityTransporter)tileEntity).getStatus());
				return true;
			}
		}
		
		return false;
	}
}