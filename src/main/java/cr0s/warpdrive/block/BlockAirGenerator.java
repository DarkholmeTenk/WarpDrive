package cr0s.warpdrive.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IAirCanister;
import cr0s.warpdrive.config.WarpDriveConfig;

public class BlockAirGenerator extends BlockAbstractContainer {
	private IIcon[] iconBuffer;
	
	private static final int ICON_INACTIVE_SIDE = 0;
	private static final int ICON_BOTTOM = 1;
	private static final int ICON_SIDE_ACTIVATED = 2;
	
	public BlockAirGenerator() {
		super(Material.iron);
		setBlockName("warpdrive.machines.AirGenerator");
	}
	
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		iconBuffer = new IIcon[3];
		iconBuffer[ICON_INACTIVE_SIDE] = par1IconRegister.registerIcon("warpdrive:airGeneratorSideInactive");
		iconBuffer[ICON_BOTTOM] = par1IconRegister.registerIcon("warpdrive:airGeneratorBottom");
		iconBuffer[ICON_SIDE_ACTIVATED] = par1IconRegister.registerIcon("warpdrive:airGeneratorSideActive");
	}
	
	@Override
	public IIcon getIcon(int side, int metadata) {
		if (side == 0) {
			return iconBuffer[ICON_BOTTOM];
		} else if (side == 1) {
			if (metadata == 0) {
				return iconBuffer[ICON_INACTIVE_SIDE];
			} else {
				return iconBuffer[ICON_SIDE_ACTIVATED];
			}
		}
		
		if (metadata == 0) { // Inactive state
			return iconBuffer[ICON_INACTIVE_SIDE];
		} else if (metadata == 1) {
			return iconBuffer[ICON_SIDE_ACTIVATED];
		}
		
		return null;
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int i) {
		return new TileEntityAirGenerator();
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
		
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityAirGenerator) {
			TileEntityAirGenerator airGenerator = (TileEntityAirGenerator)tileEntity;
			ItemStack heldItemStack = entityPlayer.getHeldItem();
			if (heldItemStack == null) {
				WarpDrive.addChatMessage(entityPlayer, airGenerator.getStatus());
				return true;
			} else {
				Item heldItem = heldItemStack.getItem();
				if (heldItem != null && (heldItem instanceof IAirCanister)) {
					IAirCanister airCanister = (IAirCanister) heldItem;
					if (airCanister.canContainAir(heldItemStack) && airGenerator.consumeEnergy(WarpDriveConfig.AIRGEN_ENERGY_PER_CANISTER, true)) {
						entityPlayer.inventory.decrStackSize(entityPlayer.inventory.currentItem, 1);
						ItemStack toAdd = airCanister.fullDrop(heldItemStack);
						if (toAdd != null) {
							if (!entityPlayer.inventory.addItemStackToInventory(toAdd)) {
								EntityItem ie = new EntityItem(entityPlayer.worldObj, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, toAdd);
								entityPlayer.worldObj.spawnEntityInWorld(ie);
							}
							((EntityPlayerMP)entityPlayer).sendContainerToPlayer(entityPlayer.inventoryContainer);
							airGenerator.consumeEnergy(WarpDriveConfig.AIRGEN_ENERGY_PER_CANISTER, false);
						}
					}
				}
			}
		}
		
		return false;
	}
}
