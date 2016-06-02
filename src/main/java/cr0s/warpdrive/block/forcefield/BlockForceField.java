package cr0s.warpdrive.block.forcefield;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IEffector;
import cr0s.warpdrive.api.IDamageReceiver;
import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.block.hull.BlockHullGlass;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.ForceFieldSetup;
import cr0s.warpdrive.data.PermissionNode;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.render.RenderBlockForceField;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Random;

public class BlockForceField extends BlockAbstractContainer implements IDamageReceiver {
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	private int tier;
	private static final float BOUNDING_TOLERANCE = 0.05F;
	
	public BlockForceField(final int tier) {
		super(Material.glass);
		this.tier = tier;
		setHardness(WarpDriveConfig.HULL_HARDNESS[tier - 1]);
		setResistance(WarpDriveConfig.HULL_BLAST_RESISTANCE[tier - 1] * 5 / 3);
		setStepSound(Block.soundTypeCloth);
		setCreativeTab(WarpDrive.creativeTabWarpDrive);
		setBlockName("warpdrive.forcefield.block" + tier);
		setBlockTextureName("warpdrive:forcefield/forcefield");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityForceField();
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer entityPlayer) {
		return null;
	}
	
	@Override
	public int getMobilityFlag() {
		return 2;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		return icons[metadata % 16];
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 0;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	protected boolean canSilkHarvest() {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list) {
		/* Hide in NEI
		for (int i = 0; i < 16; ++i) {
			list.add(new ItemStack(item, 1, i));
		}
		/**/
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		icons = new IIcon[16];
		
		for (int i = 0; i < 16; ++i) {
			icons[i] = iconRegister.registerIcon(getTextureName() + "_" + i);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return 1;
	}
	
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return RenderBlockForceField.renderId;
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		if (world.isAirBlock(x, y, z)) {
			return true;
		}
		ForgeDirection direction = ForgeDirection.getOrientation(side).getOpposite();
		Block sideBlock = world.getBlock(x, y, z);
		if (sideBlock instanceof BlockGlass || sideBlock instanceof BlockHullGlass || sideBlock instanceof BlockForceField) {
			return world.getBlockMetadata(x, y, z)
				!= world.getBlockMetadata(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
		}
		return !world.isSideSolid(x, y, z, direction, false);
	}
	
	protected TileEntityProjector getProjector(World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityForceField) {
			return ((TileEntityForceField) tileEntity).getProjector();
		}
		return null;
	}
	
	private ForceFieldSetup getForceFieldSetup(World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityForceField) {
			return ((TileEntityForceField) tileEntity).getForceFieldSetup();
		}
		return null;
	}
	
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer entityPlayer) {
		ForceFieldSetup forceFieldSetup = getForceFieldSetup(world, x, y, z);
		if (forceFieldSetup != null) {
			for (IEffector effector : forceFieldSetup.getEffectors()) {
				effector.onEntityCollided(world, x, y, z, entityPlayer);
			}
		}
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		ForceFieldSetup forceFieldSetup = getForceFieldSetup(world, x, y, z);
		if (forceFieldSetup != null) {
			List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 0.9D, z + 1));
			
			for (EntityPlayer entityPlayer : entities) {
				if (entityPlayer != null && entityPlayer.isSneaking()) {
					if ( entityPlayer.capabilities.isCreativeMode 
					  || forceFieldSetup.isAccessGranted(entityPlayer, PermissionNode.SNEAK_THROUGH)) {
							return null;
					}
				}
			}
		}
		
		return AxisAlignedBB.getBoundingBox(
			x + BOUNDING_TOLERANCE, y + BOUNDING_TOLERANCE, z + BOUNDING_TOLERANCE,
			x + 1 - BOUNDING_TOLERANCE, y + 1 - BOUNDING_TOLERANCE, z + 1 - BOUNDING_TOLERANCE);
	}
	
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (world.isRemote) {
			return;
		}
		
		ForceFieldSetup forceFieldSetup = getForceFieldSetup(world, x, y, z);
		if (forceFieldSetup != null) {
			for (IEffector effector : forceFieldSetup.getEffectors()) {
				effector.onEntityCollided(world, x, y, z, entity);
			}
			
			if (entity instanceof EntityLiving && new Vector3(x, y, z).translate(0.5F).distanceTo_square(entity) < 0.4D) {
				boolean hasPermission = false;
				
				List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 0.9D, z + 1));
				for (EntityPlayer entityPlayer : entities) {
					if (entityPlayer != null && entityPlayer.isSneaking()) {
						if ( entityPlayer.capabilities.isCreativeMode
							|| forceFieldSetup.isAccessGranted(entityPlayer, PermissionNode.SNEAK_THROUGH)) {
							hasPermission = true;
							break;
						}
					}
				}
				
				// always slowdown
				((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 1));
				
				if (!hasPermission) {
					((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.confusion.id, 80, 3));
					entity.attackEntityFrom(WarpDrive.damageShock, 5);
				}
			}
		}
	}
	
	@Override
	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		TileEntity tileEntity = blockAccess.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityForceField) {
			ForceFieldSetup forceFieldSetup = ((TileEntityForceField)tileEntity).getForceFieldSetup();
			if (forceFieldSetup != null) {
				Block blockCamouflage = forceFieldSetup.getCamouflageBlock();
				if (blockCamouflage != null) {
					try {
						return blockCamouflage.colorMultiplier(blockAccess, x, y, z);
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}
		
		return super.colorMultiplier(blockAccess, x, y, z);
	}
	
	@Override
	public int getLightValue(IBlockAccess blockAccess, int x, int y, int z) {
		TileEntity tileEntity = blockAccess.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityForceField) {
			ForceFieldSetup forceFieldSetup = ((TileEntityForceField)tileEntity).getForceFieldSetup();
			if (forceFieldSetup != null) {
				Block blockCamouflage = forceFieldSetup.getCamouflageBlock();
				if (blockCamouflage != null) {
					try {
						return blockCamouflage.getLightValue(blockAccess, x, y, z);
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}
		
		return 0;
	}
	
	@Override
	public float getBlockHardness(World world, int x, int y, int z, DamageSource damageSource, int damageParameter, Vector3 damageDirection, int damageLevel) {
		return WarpDriveConfig.HULL_HARDNESS[tier - 1];
	}
	
	@Override
	public int applyDamage(World world, int x, int y, int z, DamageSource damageSource, int damageParameter, Vector3 damageDirection, int damageLevel) {
		ForceFieldSetup forceFieldSetup = getForceFieldSetup(world, x, y, z);
		if (forceFieldSetup != null) {
			return forceFieldSetup.applyDamages(world, x, y, z, damageSource, damageParameter, damageDirection, damageLevel);
		}
		
		return 0;
	}
}
