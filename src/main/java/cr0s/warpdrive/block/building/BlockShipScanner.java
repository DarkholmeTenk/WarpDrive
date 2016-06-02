package cr0s.warpdrive.block.building;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.BlockAbstractContainer;

public class BlockShipScanner extends BlockAbstractContainer {
	private IIcon[] iconBuffer;
	private final static int ICON_BOTTOM = 0;
	private final static int ICON_TOP = 1;
	private final static int ICON_SIDE = 2;
	
	public BlockShipScanner() {
		super(Material.iron);
		setBlockName("warpdrive.building.ShipScanner");
	}
	
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		iconBuffer = new IIcon[16];
		iconBuffer[ICON_BOTTOM] = par1IconRegister.registerIcon("warpdrive:building/shipScannerBottom");
		iconBuffer[ICON_TOP   ] = par1IconRegister.registerIcon("warpdrive:building/shipScannerTop");
		iconBuffer[ICON_SIDE  ] = par1IconRegister.registerIcon("warpdrive:building/shipScannerSide");
	}
	
	@Override
	public IIcon getIcon(int side, int metadata) {
		if (side == 0) {
			return iconBuffer[ICON_BOTTOM];
		}
		if (side == 1) {
			return iconBuffer[ICON_TOP];
		}
		
		return iconBuffer[ICON_SIDE];
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int i) {
		return new TileEntityShipScanner();
	}
	
	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}
	
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
			if (tileEntity instanceof TileEntityShipScanner) {
				WarpDrive.addChatMessage(entityPlayer, ((TileEntityShipScanner)tileEntity).getStatus());
				return true;
			}
		}
		
		return false;
	}
}