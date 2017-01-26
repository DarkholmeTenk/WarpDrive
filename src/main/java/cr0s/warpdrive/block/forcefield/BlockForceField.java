package cr0s.warpdrive.block.forcefield;

import cr0s.warpdrive.data.EnumPermissionNode;
import cr0s.warpdrive.data.ForceFieldSetup;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.data.VectorI;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IDamageReceiver;
import cr0s.warpdrive.block.hull.BlockHullGlass;
import cr0s.warpdrive.config.WarpDriveConfig;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockForceField extends BlockAbstractForceField implements IDamageReceiver {
	private static final float BOUNDING_TOLERANCE = 0.05F;
	public static final PropertyInteger FREQUENCY = PropertyInteger.create("frequency", 0, 15);
	
	public BlockForceField(final String registryName, final byte tier) {
		super(registryName, tier, Material.GLASS);
		setSoundType(SoundType.CLOTH);
		setUnlocalizedName("warpdrive.forcefield.block" + tier);
		
		setDefaultState(getDefaultState().withProperty(FREQUENCY, 0));
		GameRegistry.registerTileEntity(TileEntityForceField.class, WarpDrive.PREFIX + registryName);
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FREQUENCY);
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public MapColor getMapColor(IBlockState state) {
		// @TODO: color from force field frequency
		return super.getMapColor(state);
	}

	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FREQUENCY, meta);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FREQUENCY);
	}
	
	@Nullable
	@Override
	public ItemBlock createItemBlock() {
		return new ItemBlockForceField(this);
	}
	
	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
		return new TileEntityForceField();
	}
	
	@Override
	public boolean isVisuallyOpaque() {
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isFullyOpaque(IBlockState state) {
		return false;
	}

	@Nonnull
	@Override
	public ItemStack getPickBlock(@Nonnull IBlockState blockState, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos blockPos, EntityPlayer entityPlayer) {
		return new ItemStack(Blocks.AIR);
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(@Nonnull Item item, CreativeTabs creativeTab, List<ItemStack> list) {
		// @TODO: Hide in NEI
		for (int i = 0; i < 16; ++i) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	@Nonnull
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos blockPos, EnumFacing facing) {
		BlockPos blockPosSide = blockPos.offset(facing);
		if (blockAccess.isAirBlock(blockPosSide)) {
			return true;
		}
		EnumFacing opposite = facing.getOpposite();
		IBlockState blockStateSide = blockAccess.getBlockState(blockPosSide);
		if ( blockStateSide.getBlock() instanceof BlockGlass 
		  || blockStateSide.getBlock() instanceof BlockHullGlass
		  || blockStateSide.getBlock() instanceof BlockForceField ) {
			return blockState.getBlock().getMetaFromState(blockState)
				!= blockStateSide.getBlock().getMetaFromState(blockStateSide);
		}
		return !blockAccess.isSideSolid(blockPosSide, opposite, false);
	}
	
	protected TileEntityForceFieldProjector getProjector(World world, @Nonnull BlockPos blockPos) {
		TileEntity tileEntity = world.getTileEntity(blockPos);
		if (tileEntity instanceof TileEntityForceField) {
			return ((TileEntityForceField) tileEntity).getProjector();
		}
		return null;
	}
	
	private ForceFieldSetup getForceFieldSetup(World world, @Nonnull BlockPos blockPos) {
		TileEntity tileEntity = world.getTileEntity(blockPos);
		if (tileEntity instanceof TileEntityForceField) {
			return ((TileEntityForceField) tileEntity).getForceFieldSetup();
		}
		return null;
	}
	
	@Override
	public void onBlockClicked(World world, BlockPos blockPos, EntityPlayer entityPlayer) {
		ForceFieldSetup forceFieldSetup = getForceFieldSetup(world, blockPos);
		if (forceFieldSetup != null) {
			forceFieldSetup.onEntityEffect(world, blockPos, entityPlayer);
		}
	}

	@SuppressWarnings("deprecation")
	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull World world, @Nonnull BlockPos blockPos) {
		ForceFieldSetup forceFieldSetup = getForceFieldSetup(world, blockPos);
		if (forceFieldSetup != null) {
			List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(
				blockPos.getX(), blockPos.getY(), blockPos.getZ(),
				blockPos.getX() + 1.0D, blockPos.getY() + 0.9D, blockPos.getZ() + 1.0D));
			
			for (EntityPlayer entityPlayer : entities) {
				if (entityPlayer != null && entityPlayer.isSneaking()) {
					if ( entityPlayer.capabilities.isCreativeMode 
					  || forceFieldSetup.isAccessGranted(entityPlayer, EnumPermissionNode.SNEAK_THROUGH)) {
							return null;
					}
				}
			}
		}
		
		return new AxisAlignedBB(blockPos).expand(-BOUNDING_TOLERANCE, -BOUNDING_TOLERANCE, -BOUNDING_TOLERANCE);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos blockPos, IBlockState blockState, Entity entity) {
		if (world.isRemote) {
			return;
		}
		
		ForceFieldSetup forceFieldSetup = getForceFieldSetup(world, blockPos);
		if (forceFieldSetup != null) {
			forceFieldSetup.onEntityEffect(world, blockPos, entity);
			double distance2 = new Vector3(blockPos).translate(0.5F).distanceTo_square(entity);
			if (entity instanceof EntityLiving && distance2 < 0.26D) {
				boolean hasPermission = false;
				
				List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(
					blockPos.getX(), blockPos.getY(), blockPos.getZ(),
					blockPos.getX() + 1.0D, blockPos.getY() + 0.9D, blockPos.getZ() + 1.0D));
				for (EntityPlayer entityPlayer : entities) {
					if (entityPlayer != null && entityPlayer.isSneaking()) {
						if ( entityPlayer.capabilities.isCreativeMode
						  || forceFieldSetup.isAccessGranted(entityPlayer, EnumPermissionNode.SNEAK_THROUGH) ) {
							hasPermission = true;
							break;
						}
					}
				}
				
				// always slowdown
				((EntityLiving) entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 1));
				
				if (!hasPermission) {
					((EntityLiving) entity).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 80, 3));
					if (distance2 < 0.24D) {
						entity.attackEntityFrom(WarpDrive.damageShock, 5);
					}
				}
			}
		}
	}
	
	/* @TODO MC1.10 camouflage color multiplier
	@Override
	public int colorMultiplier(IBlockAccess blockAccess, BlockPos blockPos) {
		TileEntity tileEntity = blockAccess.getTileEntity(blockPos);
		if (tileEntity instanceof TileEntityForceField && ((TileEntityForceField)tileEntity).cache_blockStateCamouflage != null) {
			return ((TileEntityForceField)tileEntity).cache_colorMultiplierCamouflage;
		}
		
		return super.colorMultiplier(blockAccess, blockPos);
	}
	/**/

	@Override
	public int getLightValue(@Nonnull IBlockState blockState, IBlockAccess blockAccess, @Nonnull BlockPos blockPos) {
		TileEntity tileEntity = blockAccess.getTileEntity(blockPos);
		if (tileEntity instanceof TileEntityForceField) {
			return ((TileEntityForceField)tileEntity).cache_lightCamouflage;
		}
		
		return 0;
	}
	
	private void downgrade(World world, final BlockPos blockPos) {
		if (tier > 1) {
			TileEntityForceFieldProjector tileEntityForceFieldProjector = getProjector(world, blockPos);
			IBlockState blockState = world.getBlockState(blockPos);
			int frequency = blockState.getValue(FREQUENCY);
			world.setBlockState(blockPos, WarpDrive.blockForceFields[tier - 2].getDefaultState().withProperty(FREQUENCY, (frequency + 1) % 16), 2);
			if (tileEntityForceFieldProjector != null) {
				TileEntity tileEntity = world.getTileEntity(blockPos);
				if (tileEntity instanceof TileEntityForceField) {
					((TileEntityForceField) tileEntity).setProjector(new VectorI(tileEntityForceFieldProjector));
				}
			}
			
		} else {
			world.setBlockToAir(blockPos);
		}
	}
	
	/* @TODO MC1.10 explosion effect redesign
	private double log_explosionX;
	private double log_explosionY = -1;
	private double log_explosionZ;
	
	@Override
	public float getExplosionResistance(Entity exploder) {
		return super.getExplosionResistance(exploder);
	}
	@Override
	public float getExplosionResistance(Entity entity, World world, BlockPos blockPos, double explosionX, double explosionY, double explosionZ) {
		boolean enableFirstHit = (log_explosionX != explosionX || log_explosionY != explosionY || log_explosionZ != explosionZ); 
		if (enableFirstHit) {
			log_explosionX = explosionX;
			log_explosionY = explosionY;
			log_explosionZ = explosionZ;
		}
		
		// find explosion strength, defaults to no effect
		double strength = 0.0D;
		if (entity == null && (explosionX == Math.rint(explosionX)) && (explosionY == Math.rint(explosionY)) && (explosionZ == Math.rint(explosionZ)) ) {
			// IC2 Reactor blowing up => bloc is already air
			IBlockState blockState = world.getBlockState(new BlockPos((int)explosionX, (int)explosionY, (int)explosionZ));
			TileEntity tileEntity = world.getTileEntity(new BlockPos((int)explosionX, (int)explosionY, (int)explosionZ));
			if (enableFirstHit && WarpDriveConfig.LOGGING_FORCEFIELD) {
				WarpDrive.logger.info("Block at location is " + blockState.getBlock() + " " + blockState.getBlock().getUnlocalizedName() + " with tileEntity " + tileEntity);
			}
			// explosion with no entity and block removed, hence we can compute the energy impact => boosting explosion resistance
			return 2.0F * super.getExplosionResistance(entity, world, blockPos, explosionX, explosionY, explosionZ);
		}
		
		if (entity != null) {
			switch (entity.getClass().toString()) {
			case "class net.minecraft.entity.item.EntityEnderCrystal": strength = 6.0D; break;
			case "class net.minecraft.entity.item.EntityMinecartTNT": strength = 4.0D; break;
			case "class net.minecraft.entity.item.EntityTNTPrimed": strength = 5.0D; break;
			case "class net.minecraft.entity.monster.EntityCreeper": strength = 3.0D; break;  // *2 for powered ones
			case "class appeng.entity.EntityTinyTNTPrimed": strength = 0.2D; break;
			case "class com.arc.bloodarsenal.common.entity.EntityBloodTNT": strength = 1.0D; break;
			case "class ic2.core.block.EntityItnt": strength = 5.5D; break; 
			case "class ic2.core.block.EntityNuke": strength = 0.02D; break;
			case "class ic2.core.block.EntityDynamite": strength = 1.0D; break;
			case "class ic2.core.block.EntityStickyDynamite": strength = 1.0D; break;
			
			// S-mine (initial)
			case "class defense.common.entity.EntityExplosion": strength = 1.0D; break;
			
			// Condensed, Incendiary, Repulsive, Attractive, Fragmentation, Sonic, Breaching, Thermobaric, Nuclear,
			// Exothermic, Endothermic, Anti-gravitational, Hypersonic, (Antimatter?)
			case "class defense.common.entity.EntityExplosive": strength = 15.0D; break;
			
			// Fragmentation, S-mine
			case "class defense.common.entity.EntityFragments": strength = 0.02D; break;
			default:
				if (enableFirstHit) {
					WarpDrive.logger.error("Unknown explosion source " + entity.getClass().toString() + " " + entity);
				}
				break;
			}
		}
		
		// apply damages to force field by consuming energy
		Explosion explosion = new Explosion(world, entity, explosionX, explosionY, explosionZ, 4.0F);
		Vector3 vDirection = new Vector3(x + 0.5D - explosionX, y + 0.5D - explosionY, z + 0.5D - explosionZ);
		double magnitude = Math.max(1.0D, vDirection.getMagnitude());
		if (magnitude != 0) {// normalize
			vDirection.scale(1 / magnitude);
		}
		double damageLevel = strength / (magnitude * magnitude) * 1.0D;
		double damageLeft = 0;
		ForceFieldSetup forceFieldSetup = getForceFieldSetup(world, blockPos);
		if (forceFieldSetup != null) {
			damageLeft = forceFieldSetup.applyDamage(world, DamageSource.setExplosionSource(explosion), damageLevel);
		}
		
		assert(damageLeft >= 0);
		if (enableFirstHit && WarpDriveConfig.LOGGING_FORCEFIELD) {
			WarpDrive.logger.info( "BlockForceField(" + tier + " at " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ() + ")"
				                 + " involved in explosion " + ((entity != null) ? " from " + entity : " at " + explosionX + " " + explosionY + " " + explosionZ)
								 + (WarpDrive.isDev ? (" damageLevel " + damageLevel + " damageLeft " + damageLeft) : ""));
		}
		return super.getExplosionResistance(entity, world, blockPos, explosionX, explosionY, explosionZ);
	}
	/**/
	
	@Override
	public boolean canDropFromExplosion(Explosion p_149659_1_) {
		return false;
	}
	
	@Override
	public void onBlockExploded(World world, @Nonnull BlockPos blockPos, @Nonnull Explosion explosion) {
		downgrade(world, blockPos);
		super.onBlockExploded(world, blockPos, explosion);
	}
	
	@Override
	public void onEMP(World world, BlockPos blockPos, float efficiency) {
		if (efficiency > 0.0F) {
			downgrade(world, blockPos);
		}
		// already handled => no ancestor call
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos blockPos, Explosion explosion) {
		// (block is already set to air by caller, see IC2 iTNT for example)
		downgrade(world, blockPos);
		super.onBlockDestroyedByExplosion(world, blockPos, explosion);
	}
	
	@Override
	public float getBlockHardness(IBlockState blockState, World world, final BlockPos blockPos,
	                              final DamageSource damageSource, final int damageParameter, final Vector3 damageDirection, final int damageLevel) {
		return WarpDriveConfig.HULL_HARDNESS[tier - 1];
	}
	
	@Override
	public int applyDamage(IBlockState blockState, World world, final BlockPos blockPos, final DamageSource damageSource,
	                       final int damageParameter, final Vector3 damageDirection, final int damageLevel) {
		ForceFieldSetup forceFieldSetup = getForceFieldSetup(world, blockPos);
		if (forceFieldSetup != null) {
			return (int) Math.round(forceFieldSetup.applyDamage(world, damageSource, damageLevel));
		}
		
		return damageLevel;
	}
}
