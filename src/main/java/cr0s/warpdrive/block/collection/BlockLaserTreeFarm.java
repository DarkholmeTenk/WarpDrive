package cr0s.warpdrive.block.collection;

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

public class BlockLaserTreeFarm extends BlockAbstractContainer {
	private IIcon[] iconBuffer;
	public final static int ICON_IDLE = 0;
	public final static int ICON_FARMING_LOW_POWER = 1;
	public final static int ICON_FARMING_POWERED = 2;
	public final static int ICON_SCANNING_LOW_POWER = 3;
	public final static int ICON_SCANNING_POWERED = 4;
	public final static int ICON_PLANTING_LOW_POWER = 5;
	public final static int ICON_PLANTING_POWERED = 6;
	private final static int ICON_BOTTOM = 7;
	private final static int ICON_TOP = 8;
	
	public BlockLaserTreeFarm() {
		super(Material.iron);
		setBlockName("warpdrive.collection.LaserTreeFarm");
	}
	
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		iconBuffer = new IIcon[16];
		iconBuffer[ICON_IDLE            ] = par1IconRegister.registerIcon("warpdrive:collection/laserTreeFarmSide_idle");
		iconBuffer[ICON_FARMING_LOW_POWER] = par1IconRegister.registerIcon("warpdrive:collection/laserTreeFarmSide_farmingLowPower");
		iconBuffer[ICON_FARMING_POWERED] = par1IconRegister.registerIcon("warpdrive:collection/laserTreeFarmSide_farmingPowered");
		iconBuffer[ICON_SCANNING_LOW_POWER] = par1IconRegister.registerIcon("warpdrive:collection/laserTreeFarmSide_scanningLowPower");
		iconBuffer[ICON_SCANNING_POWERED] = par1IconRegister.registerIcon("warpdrive:collection/laserTreeFarmSide_scanningPowered");
		iconBuffer[ICON_PLANTING_LOW_POWER] = par1IconRegister.registerIcon("warpdrive:collection/laserTreeFarmSide_plantingLowPower");
		iconBuffer[ICON_PLANTING_POWERED] = par1IconRegister.registerIcon("warpdrive:collection/laserTreeFarmSide_plantingPowered");
		iconBuffer[ICON_BOTTOM          ] = par1IconRegister.registerIcon("warpdrive:collection/laserTreeFarmBottom");
		iconBuffer[ICON_TOP             ] = par1IconRegister.registerIcon("warpdrive:collection/laserTreeFarmTop");
	}
	
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int metadata  = world.getBlockMetadata(x, y, z);
		if (side == 0) {
			return iconBuffer[ICON_BOTTOM];
		}
		if (side == 1) {
			return iconBuffer[ICON_TOP];
		}
		if (metadata < iconBuffer.length) {
			return iconBuffer[metadata];
		}
		return null;
	}
	
	@Override
	public IIcon getIcon(int side, int metadata) {
		if (side == 0) {
			return iconBuffer[ICON_BOTTOM];
		}
		if (side == 1) {
			return iconBuffer[ICON_TOP];
		}
		return iconBuffer[ICON_PLANTING_LOW_POWER];
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityLaserTreeFarm();
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 1;
	}
	
	/**
	 * Returns the item to drop on destruction.
	 */
	@Override
	public Item getItemDropped(int par1, Random random, int par3) {
		return Item.getItemFromBlock(this);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		
		if (entityPlayer.getHeldItem() == null) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityLaserTreeFarm) {
				WarpDrive.addChatMessage(entityPlayer, ((TileEntityLaserTreeFarm)tileEntity).getStatus());
				return true;
			}
		}
		
		return false;
	}
}