package cr0s.warpdrive.block.forcefield;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumForceFieldShape;
import cr0s.warpdrive.item.ItemForceFieldShape;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockProjector extends BlockAbstractContainer {
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	protected byte tier;
	
	public BlockProjector(final byte tier) {
		super(Material.iron);
		isRotating = true;
		this.tier = tier;
		setHardness(WarpDriveConfig.HULL_HARDNESS[tier - 1]);
		setResistance(WarpDriveConfig.HULL_BLAST_RESISTANCE[tier - 1] * 5 / 3);
		setBlockName("warpdrive.forcefield.projector" + tier);
		setBlockTextureName("warpdrive:forcefield/projector");
	}
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		icons = new IIcon[11];
		icons[ 0] = iconRegister.registerIcon("warpdrive:forcefield/projectorSide_notConnected");
		icons[ 1] = iconRegister.registerIcon("warpdrive:forcefield/projectorSide_connectedNotPowered");
		icons[ 2] = iconRegister.registerIcon("warpdrive:forcefield/projectorSide_connectedPowered");
		icons[ 3] = iconRegister.registerIcon("warpdrive:forcefield/projectorShape_none");
		icons[ 4] = iconRegister.registerIcon("warpdrive:forcefield/projectorShape_sphere");
		icons[ 5] = iconRegister.registerIcon("warpdrive:forcefield/projectorShape_cylinder_h");
		icons[ 6] = iconRegister.registerIcon("warpdrive:forcefield/projectorShape_cylinder_v");
		icons[ 7] = iconRegister.registerIcon("warpdrive:forcefield/projectorShape_cube");
		icons[ 8] = iconRegister.registerIcon("warpdrive:forcefield/projectorShape_plane");
		icons[ 9] = iconRegister.registerIcon("warpdrive:forcefield/projectorShape_tube");
		icons[10] = iconRegister.registerIcon("warpdrive:forcefield/projectorShape_tunnel");
	}
	
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int metadata = world.getBlockMetadata(x, y, z);
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null || !(tileEntity instanceof TileEntityProjector)) {
			return icons[0];
		}
		
		if (side == (metadata & 7) || (((TileEntityProjector)tileEntity).isDoubleSided && ForgeDirection.OPPOSITES[side] == (metadata & 7))) {
			return icons[3 + ((TileEntityProjector)tileEntity).getShape().ordinal()];
		} else if (((TileEntityProjector)tileEntity).isConnected) {
			if (((TileEntityProjector)tileEntity).isPowered) {
				return icons[2];
			} else {
				return icons[1];
			}
		}
		
		return icons[0];
	}
	
	@Override
	public IIcon getIcon(int side, int metadata) {
		return side == 3 ? icons[4] : icons[2];
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof TileEntityProjector)) {
			return false;
		}
		TileEntityProjector tileEntityProjector = (TileEntityProjector) tileEntity;
		ItemStack itemStackHeld = entityPlayer.getHeldItem();
		int metadata = world.getBlockMetadata(x, y, z);
		
		if (itemStackHeld == null) {
			if (entityPlayer.isSneaking()) {
				if (tileEntityProjector.getShape() != EnumForceFieldShape.NONE) {
					if (side == (metadata & 7) || (tileEntityProjector.isDoubleSided && ForgeDirection.OPPOSITES[side] == (metadata & 7))) {
						// dismount the shape item(s)
						ItemStack itemStackDrop = ItemForceFieldShape.getItemStackNoCache(tileEntityProjector.getShape(), tileEntityProjector.isDoubleSided ? 2 : 1);
						EntityItem entityItem = new EntityItem(world, entityPlayer.posX, entityPlayer.posY + 0.5D, entityPlayer.posZ, itemStackDrop);
						entityItem.delayBeforeCanPickup = 0;
						world.spawnEntityInWorld(entityItem);
						
						tileEntityProjector.setShape(EnumForceFieldShape.NONE);
					} else {
						// wrong side TODO
						WarpDrive.addChatMessage(entityPlayer, tileEntityProjector.getStatus());
						return true;
					}
				} else {
					// no shape to dismount TODO
					WarpDrive.addChatMessage(entityPlayer, tileEntityProjector.getStatus());
					return true;
				}
			} else {
				WarpDrive.addChatMessage(entityPlayer, tileEntityProjector.getStatus());
				return true;
			}
		} else if (itemStackHeld.getItem() instanceof ItemForceFieldShape) {
			if (side == (metadata & 7) || (((TileEntityProjector) tileEntity).isDoubleSided && ForgeDirection.OPPOSITES[side] == (metadata & 7))) {
				// validate quantity
				if (itemStackHeld.stackSize < (tileEntityProjector.isDoubleSided ? 2 : 1)) {
					// not enough shape items TODO
					WarpDrive.addChatMessage(entityPlayer, ((TileEntityProjector) tileEntity).getStatus());
					return true;
				}
				
				// update player inventory
				itemStackHeld.stackSize -= tileEntityProjector.isDoubleSided ? 2 : 1;
				
				// dismount the shape item(s)
				if (tileEntityProjector.getShape() != EnumForceFieldShape.NONE) {
					ItemStack itemStackDrop = ItemForceFieldShape.getItemStackNoCache(tileEntityProjector.getShape(), tileEntityProjector.isDoubleSided ? 2 : 1);
					EntityItem entityItem = new EntityItem(world, entityPlayer.posX, entityPlayer.posY + 0.5D, entityPlayer.posZ, itemStackDrop);
					entityItem.delayBeforeCanPickup = 0;
					world.spawnEntityInWorld(entityItem);
				}
				
				// mount the new shape item(s)
				tileEntityProjector.setShape(EnumForceFieldShape.get(itemStackHeld.getItemDamage()));
			} else {
				// wrong side TODO
				WarpDrive.addChatMessage(entityPlayer, ((TileEntityProjector) tileEntity).getStatus());
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityProjector();
	}
	
	@Override
	protected boolean canSilkHarvest() {
		return false;
	}
	
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer entityPlayer) {
		return null; // FIXME
	}
}
