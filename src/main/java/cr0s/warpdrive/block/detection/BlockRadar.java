package cr0s.warpdrive.block.detection;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.BlockAbstractContainer;

public class BlockRadar extends BlockAbstractContainer {
	private IIcon[] iconBuffer;
	
	private static final int ICON_SIDE_INACTIVE = 0;
	private static final int ICON_BOTTOM = 1;
	private static final int ICON_TOP = 2;
	private static final int ICON_SIDE_ACTIVATED = 3;
	private static final int ICON_SIDE_ACTIVATED_SCAN = 4;
	
	public BlockRadar() {
		super(Material.iron);
		setBlockName("warpdrive.detection.Radar");
	}
	
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		iconBuffer = new IIcon[16];
		iconBuffer[ICON_SIDE_INACTIVE] = par1IconRegister.registerIcon("warpdrive:detection/radarSideInactive");
		iconBuffer[ICON_BOTTOM] = par1IconRegister.registerIcon("warpdrive:detection/radarBottom");
		iconBuffer[ICON_TOP] = par1IconRegister.registerIcon("warpdrive:detection/radarTop");
		iconBuffer[ICON_SIDE_ACTIVATED] = par1IconRegister.registerIcon("warpdrive:detection/radarSideActive");
		iconBuffer[ICON_SIDE_ACTIVATED_SCAN] = par1IconRegister.registerIcon("warpdrive:detection/radarSideActiveScan");
	}
	
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int metadata  = world.getBlockMetadata(x, y, z);
		if (side == 0) {
			return iconBuffer[ICON_BOTTOM];
		} else if (side == 1) {
			return iconBuffer[ICON_TOP];
		}
		
		if (metadata == 0) {// Inactive state
			return iconBuffer[ICON_SIDE_INACTIVE];
		} else if (metadata == 1) { // Attached state
			return iconBuffer[ICON_SIDE_ACTIVATED];
		} else if (metadata == 2) { // Scanning state
			return iconBuffer[ICON_SIDE_ACTIVATED_SCAN];
		}
		
		return iconBuffer[ICON_SIDE_INACTIVE];
	}
	
	@Override
	public IIcon getIcon(int side, int metadata) {
		if (side == 0) {
			return iconBuffer[ICON_BOTTOM];
		} else if (side == 1) {
			return iconBuffer[ICON_TOP];
		}
		
		// return iconBuffer[ICON_SIDE_ACTIVATED];
		return iconBuffer[ICON_SIDE_ACTIVATED_SCAN];
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int i) {
		return new TileEntityRadar();
	}
	
	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}
	
	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3) {
		return Item.getItemFromBlock(this);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		
		if (entityPlayer.getHeldItem() == null) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityRadar) {
				WarpDrive.addChatMessage(entityPlayer, ((TileEntityRadar)tileEntity).getStatus());
				return true;
			}
		}
		
		return false;
	}
}
