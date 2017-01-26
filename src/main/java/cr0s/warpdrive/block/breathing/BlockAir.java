package cr0s.warpdrive.block.breathing;

import java.util.Random;

import cr0s.warpdrive.block.BlockAbstractBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cr0s.warpdrive.config.WarpDriveConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockAir extends BlockAbstractBase {
	private static final boolean TRANSPARENT_AIR = true;
	private static final boolean AIR_DEBUG = false;
	private static final int AIR_BLOCK_TICKS = 40;
	
	public BlockAir(final String registryName) {
		super(registryName, Material.FIRE);
		setHardness(0.0F);
		setUnlocalizedName("warpdrive.passive.Air");
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
	
	@Override
	public boolean isAir(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull World world, @Nonnull BlockPos blockPos) {
		return null;
	}

	@Override
	public boolean isReplaceable(IBlockAccess blockAccess, @Nonnull BlockPos blockPos) {
		return true;
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, @Nonnull BlockPos blockPos) {
		return true;
	}

	@Override
	public boolean canCollideCheck(IBlockState blockState, boolean hitIfLiquid) {
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state) {
		return EnumPushReaction.DESTROY;
	}

	@Nullable
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}
	
	@Override
	public int quantityDropped(Random par1Random) {
		return 0;
	}
	
	@Override
	public int tickRate(World par1World) {
		return AIR_BLOCK_TICKS;
	}
	
	@Override
	public void updateTick(World world, BlockPos blockPos, IBlockState blockState, Random random) {
		if (world.isRemote) {
			return;
		}
		
		int concentration = blockState.getBlock().getMetaFromState(blockState);
		boolean isInSpaceWorld = world.provider.getDimension() == WarpDriveConfig.G_SPACE_DIMENSION_ID || world.provider.getDimension() == WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID;
		
		// Remove air block to vacuum block
		if (concentration <= 0 || !isInSpaceWorld) {
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3); // replace our air block to vacuum block
		} else {
			// Try to spread the air
			spreadAirBlock(world, blockPos, concentration);
		}
		world.scheduleBlockUpdate(blockPos, this, 30 + 2 * concentration, 0);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos blockPos, EnumFacing facing) {
		if (AIR_DEBUG) {
			return facing == EnumFacing.DOWN || facing == EnumFacing.UP;
		}

		BlockPos blockPosSide = blockPos.offset(facing);
		Block sideBlock = blockAccess.getBlockState(blockPosSide).getBlock();
		return sideBlock != this && blockAccess.isAirBlock(blockPosSide);
	}
	
	private void spreadAirBlock(World world, final BlockPos blockPos, final int concentration) {
		/* @TODO MC1.10 air overhaul
		int air_count = 1;
		int empty_count = 0;
		int sum_concentration = concentration + 1;
		int max_concentration = concentration + 1;
		int min_concentration = concentration + 1;
		
		// Check air in adjacent blocks
		Block xp_block = world.getBlock(x + 1, y, z);
		boolean xp_isAir = world.isAirBlock(x + 1, y, z);
		int xp_concentration = (xp_block != this) ? -1 : world.getBlockMetadata(x + 1, y, z);
		if (xp_isAir) {
			air_count++;
			if (xp_concentration >= 0) {
				sum_concentration += xp_concentration + 1;
				max_concentration = Math.max(max_concentration, xp_concentration + 1);
				min_concentration = Math.min(min_concentration, xp_concentration + 1);
			} else {
				empty_count++;
			}
		}
		Block xn_block = world.getBlock(x - 1, y, z);
		boolean xn_isAir = world.isAirBlock(x - 1, y, z);
		int xn_concentration = (xn_block != this) ? -1 : world.getBlockMetadata(x - 1, y, z);
		if (xn_isAir) {
			air_count++;
			if (xn_concentration >= 0) {
				sum_concentration += xn_concentration + 1;
				max_concentration = Math.max(max_concentration, xn_concentration + 1);
				min_concentration = Math.min(min_concentration, xn_concentration + 1);
			} else {
				empty_count++;
			}
		}
		Block yp_block = world.getBlock(x, y + 1, z);
		boolean yp_isAir = world.isAirBlock(x, y + 1, z);
		int yp_concentration = (yp_block != this) ? -1 : world.getBlockMetadata(x, y + 1, z);
		if (yp_isAir) {
			air_count++;
			if (yp_concentration >= 0) {
				sum_concentration += yp_concentration + 1;
				max_concentration = Math.max(max_concentration, yp_concentration + 1);
				min_concentration = Math.min(min_concentration, yp_concentration + 1);
			} else {
				empty_count++;
			}
		}
		Block yn_block = world.getBlock(x, y - 1, z);
		boolean yn_isAir = world.isAirBlock(x, y - 1, z);
		int yn_concentration = (yn_block != this) ? -1 : world.getBlockMetadata(x, y - 1, z);
		if (yn_isAir) {
			air_count++;
			if (yn_concentration >= 0) {
				sum_concentration += yn_concentration + 1;
				max_concentration = Math.max(max_concentration, yn_concentration + 1);
				min_concentration = Math.min(min_concentration, yn_concentration + 1);
			} else {
				empty_count++;
			}
		}
		Block zp_block = world.getBlock(x, y, z + 1);
		boolean zp_isAir = world.isAirBlock(x, y, z + 1);
		int zp_concentration = (zp_block != this) ? -1 : world.getBlockMetadata(x, y, z + 1);
		if (zp_isAir) {
			air_count++;
			if (zp_concentration >= 0) {
				sum_concentration += zp_concentration + 1;
				max_concentration = Math.max(max_concentration, zp_concentration + 1);
				min_concentration = Math.min(min_concentration, zp_concentration + 1);
			} else {
				empty_count++;
			}
		}
		Block zn_block = world.getBlock(x, y, z - 1);
		boolean zn_isAir = world.isAirBlock(x, y, z - 1);
		int zn_concentration = (zn_block != this) ? -1 : world.getBlockMetadata(x, y, z - 1);
		if (zn_isAir) {
			air_count++;
			if (zn_concentration >= 0) {
				sum_concentration += zn_concentration + 1;
				max_concentration = Math.max(max_concentration, zn_concentration + 1);
				min_concentration = Math.min(min_concentration, zn_concentration + 1);
			} else {
				empty_count++;
			}
		}
		
		// air leaks means penalty plus some randomization for visual effects
		if (empty_count > 0) {
			if (concentration < 8) {
				sum_concentration -= empty_count;
			} else if (concentration < 4) {
				sum_concentration -= empty_count + (world.rand.nextBoolean() ? 0 : empty_count);
			} else {
				sum_concentration -= air_count;
			}
		}
		if (sum_concentration < 0) sum_concentration = 0;
		
		// compute new concentration, buffing closed space
		int mid_concentration;
		int new_concentration;
		boolean isGrowth = (max_concentration > 8 && (max_concentration - min_concentration < 9)) || (max_concentration > 5 && (max_concentration - min_concentration < 4));
		if (isGrowth) {
			mid_concentration = Math.round(sum_concentration / (float)air_count) - 1;
			new_concentration = sum_concentration - mid_concentration * (air_count - 1);
			new_concentration = Math.max(Math.max(concentration + 1, max_concentration - 1), new_concentration - 20);
		} else {
			mid_concentration = (int) Math.floor(sum_concentration / (float)air_count);
			new_concentration = sum_concentration - mid_concentration * (air_count - 1);
			if (empty_count > 0) {
				new_concentration = Math.max(0, new_concentration - 5);
			}
		}
		
		// apply scale and clamp
		if (mid_concentration < 1) {
			mid_concentration = 0;
		} else if (mid_concentration > 14) {
			mid_concentration = 14;
		} else if (mid_concentration > 0) {
			mid_concentration--;
		}
		if (new_concentration < 1) {
			new_concentration = 0;
		} else if (new_concentration > max_concentration - 2) {
			new_concentration = Math.max(0, max_concentration - 2);
		} else {
			new_concentration--;
		}
		
		if (WarpDriveConfig.LOGGING_BREATHING && (new_concentration < 0 || mid_concentration < 0 || new_concentration > 14 || mid_concentration > 14)) {
			WarpDrive.logger.info("Invalid concentration at step B " + isGrowth + " " + concentration + " + "
					+ xp_concentration + " " + xn_concentration + " "
					+ yp_concentration + " " + yn_concentration + " "
					+ zp_concentration + " " + zn_concentration + " = " + sum_concentration + " total, " + empty_count + " empty / " + air_count
					+ " -> " + new_concentration + " + " + (air_count - 1) + " * " + mid_concentration);
		}
		
		// new_concentration = mid_concentration = 0;
		
		// protect air generator
		if (concentration != new_concentration) {
			if (concentration == 15) {
				if ( xp_block != WarpDrive.blockAirGenerator && xn_block != WarpDrive.blockAirGenerator
				  && yp_block != WarpDrive.blockAirGenerator && yn_block != WarpDrive.blockAirGenerator
				  && zp_block != WarpDrive.blockAirGenerator && zn_block != WarpDrive.blockAirGenerator) {
					if (WarpDriveConfig.LOGGING_BREATHING) {
						WarpDrive.logger.info("AirGenerator not found, removing air block at " + x + ", " + y + ", " + z);
					}
					world.setBlockMetadataWithNotify(x, y, z, 1, 0);
				} else {
					// keep the block as a source
				}
			} else {
				world.setBlockMetadataWithNotify(x, y, z, new_concentration, 0);
			}
		}
		
		// Check and setup air to adjacent blocks
		// (do not overwrite source block, do not decrease neighbors if we're growing)
		if (xp_isAir) {
			if (xp_block == this) {
				if (xp_concentration != mid_concentration && xp_concentration != 15 && (!isGrowth || xp_concentration < mid_concentration)) {
					world.setBlockMetadataWithNotify(x + 1, y, z, mid_concentration, 0);
				}
			} else {
				world.setBlock(x + 1, y, z, this, mid_concentration, 2);
			}
		}
		
		if (xn_isAir) {
			if (xn_block == this) {
				if (xn_concentration != mid_concentration && xn_concentration != 15 && (!isGrowth || xn_concentration < mid_concentration)) {
					world.setBlockMetadataWithNotify(x - 1, y, z, mid_concentration, 0);
				}
			} else {
				world.setBlock(x - 1, y, z, this, mid_concentration, 2);
			}
		}
		
		if (yp_isAir) {
			if (yp_block == this) {
				if (yp_concentration != mid_concentration && yp_concentration != 15 && (!isGrowth || yp_concentration < mid_concentration)) {
					world.setBlockMetadataWithNotify(x, y + 1, z, mid_concentration, 0);
				}
			} else {
				world.setBlock(x, y + 1, z, this, mid_concentration, 2);
			}
		}
		
		if (yn_isAir) {
			if (yn_block == this) {
				if (yn_concentration != mid_concentration && yn_concentration != 15 && (!isGrowth || yn_concentration < mid_concentration)) {
					world.setBlockMetadataWithNotify(x, y - 1, z, mid_concentration, 0);
				}
			} else {
				world.setBlock(x, y - 1, z, this, mid_concentration, 2);
			}
		}
		
		if (zp_isAir) {
			if (zp_block == this) {
				if (zp_concentration != mid_concentration && zp_concentration != 15 && (!isGrowth || zp_concentration < mid_concentration)) {
					world.setBlockMetadataWithNotify(x, y, z + 1, mid_concentration, 0);
				}
			} else {
				world.setBlock(x, y, z + 1, this, mid_concentration, 2);
			}
		}
		
		if (zn_isAir) {
			if (zn_block == this) {
				if (zn_concentration != mid_concentration && zn_concentration != 15 && (!isGrowth || zn_concentration < mid_concentration)) {
					world.setBlockMetadataWithNotify(x, y, z - 1, mid_concentration, 0);
				}
			} else {
				world.setBlock(x, y, z - 1, this, mid_concentration, 2);
			}
		}
		/**/
	}
	
	@Override
	public boolean isCollidable() {
		return false;
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos blockPos, IBlockState blockState) {
		if (world.provider.getDimension() == WarpDriveConfig.G_SPACE_DIMENSION_ID || world.provider.getDimension() == WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID) {
			world.scheduleBlockUpdate(blockPos, this, tickRate(world), 0);
		} else {
			world.setBlockToAir(blockPos);
		}
		super.onBlockAdded(world, blockPos, blockState);
	}
}