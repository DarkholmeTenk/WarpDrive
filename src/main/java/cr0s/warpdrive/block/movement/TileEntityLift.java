package cr0s.warpdrive.block.movement;

import java.util.List;

import cr0s.warpdrive.data.EnumLiftMode;
import cr0s.warpdrive.data.SoundEvents;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Optional;
import cr0s.warpdrive.block.TileEntityAbstractEnergy;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.network.PacketHandler;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class TileEntityLift extends TileEntityAbstractEnergy {
	
	private int firstUncoveredY;
	private EnumLiftMode mode = EnumLiftMode.INACTIVE;
	private boolean isEnabled = false;
	private boolean computerEnabled = true;
	private EnumLiftMode computerMode = EnumLiftMode.REDSTONE;
	
	private int tickCount = 0;
	
	public TileEntityLift() {
		super();
		IC2_sinkTier = 2;
		IC2_sourceTier = 2;
		peripheralName = "warpdriveLift";
		addMethods(new String[] {
				"mode",
				"active"
		});
	}
	
	@Override
	public void update() {
		super.update();
		
		if (worldObj.isRemote) {
			return;
		}
		
		tickCount++;
		if (tickCount >= WarpDriveConfig.LIFT_UPDATE_INTERVAL_TICKS) {
			tickCount = 0;
			
			// Switching mode
			if (  computerMode == EnumLiftMode.DOWN
			  || (computerMode == EnumLiftMode.REDSTONE && worldObj.isBlockIndirectlyGettingPowered(pos) > 0)) {
				mode = EnumLiftMode.DOWN;
			} else {
				mode = EnumLiftMode.UP;
			}
			
			isEnabled = computerEnabled
				     && isPassableBlock(pos.getY() + 1)
				     && isPassableBlock(pos.getY() + 2)
				     && isPassableBlock(pos.getY() - 1)
				     && isPassableBlock(pos.getY() - 2);

			IBlockState blockState = worldObj.getBlockState(pos);
			if (getEnergyStored() < WarpDriveConfig.LIFT_ENERGY_PER_ENTITY || !isEnabled) {
				mode = EnumLiftMode.INACTIVE;
				if (blockState.getValue(BlockLift.MODE) != EnumLiftMode.INACTIVE) {
					worldObj.setBlockState(pos, blockState.withProperty(BlockLift.MODE, EnumLiftMode.INACTIVE));
				}
				return;
			}

			if (blockState.getValue(BlockLift.MODE) != mode) {
				worldObj.setBlockState(pos, blockState.withProperty(BlockLift.MODE, mode));
			}
			
			// Launch a beam: search non-air blocks under lift
			for (int ny = pos.getY() - 2; ny > 0; ny--) {
				if (!isPassableBlock(ny)) {
					firstUncoveredY = ny + 1;
					break;
				}
			}
			
			if (pos.getY() - firstUncoveredY >= 2) {
				if (mode == EnumLiftMode.UP) {
					PacketHandler.sendBeamPacket(worldObj,
							new Vector3(pos.getX() + 0.5D, firstUncoveredY, pos.getZ() + 0.5D),
							new Vector3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D),
							0f, 1f, 0f, 40, 0, 100);
				} else if (mode == EnumLiftMode.DOWN) {
					PacketHandler.sendBeamPacket(worldObj,
							new Vector3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D),
							new Vector3(pos.getX() + 0.5D, firstUncoveredY, pos.getZ() + 0.5D), 0f,
							0f, 1f, 40, 0, 100);
				}
				
				liftEntity();
			}
		}
	}
	
	private boolean isPassableBlock(int yPosition) {
		BlockPos blockPos = new BlockPos(pos.getX(), yPosition, pos.getZ());
		IBlockState blockState = worldObj.getBlockState(blockPos);
		return blockState.getBlock() == Blocks.AIR
			|| worldObj.isAirBlock(blockPos)
			|| blockState.getCollisionBoundingBox(worldObj, blockPos) == null;
	}
	
	private void liftEntity() {
		final double CUBE_RADIUS = 0.4;
		double xMax, zMax;
		double xMin, zMin;
		
		xMin = pos.getX() + 0.5 - CUBE_RADIUS;
		xMax = pos.getX() + 0.5 + CUBE_RADIUS;
		zMin = pos.getZ() + 0.5 - CUBE_RADIUS;
		zMax = pos.getZ() + 0.5 + CUBE_RADIUS;
		
		// Lift up
		if (mode == EnumLiftMode.UP) {
			AxisAlignedBB aabb = new AxisAlignedBB(xMin, firstUncoveredY, zMin, xMax, pos.getY(), zMax);
			List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(null, aabb);
			for (Entity entity : list) {
				if ( entity != null
				  && entity instanceof EntityLivingBase
				  && consumeEnergy(WarpDriveConfig.LIFT_ENERGY_PER_ENTITY, true)) {
					entity.setPositionAndUpdate(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
					PacketHandler.sendBeamPacket(worldObj,
							new Vector3(pos.getX() + 0.5D, firstUncoveredY, pos.getZ() + 0.5D),
							new Vector3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D),
							1F, 1F, 0F, 40, 0, 100);
					worldObj.playSound(null, pos, SoundEvents.LASER_HIGH, SoundCategory.AMBIENT, 4.0F, 1.0F);
					consumeEnergy(WarpDriveConfig.LIFT_ENERGY_PER_ENTITY, false);
				}
			}
		} else if (mode == EnumLiftMode.DOWN) {
			AxisAlignedBB aabb = new AxisAlignedBB(
					xMin, Math.min(firstUncoveredY + 4.0D, pos.getY()), zMin,
					xMax, pos.getY() + 2.0D, zMax);
			List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(null, aabb);
			if (list != null) {
				for (Entity entity : list) {
					if ( entity != null
					  && entity instanceof EntityLivingBase
					  && consumeEnergy(WarpDriveConfig.LIFT_ENERGY_PER_ENTITY, true)) {
						entity.setPositionAndUpdate(pos.getX() + 0.5D, firstUncoveredY, pos.getZ() + 0.5D);
						PacketHandler.sendBeamPacket(worldObj,
								new Vector3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D),
								new Vector3(pos.getX() + 0.5D, firstUncoveredY, pos.getZ() + 0.5D), 1F, 1F, 0F, 40, 0, 100);
						worldObj.playSound(null, pos, SoundEvents.LASER_HIGH, SoundCategory.AMBIENT, 4.0F, 1.0F);
						consumeEnergy(WarpDriveConfig.LIFT_ENERGY_PER_ENTITY, false);
					}
				}
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("mode")) {
			mode = EnumLiftMode.get(clamp(-1, 2, tag.getByte("mode")));
		}
		if (tag.hasKey("computerEnabled")) {
			computerEnabled = tag.getBoolean("computerEnabled");
		}
		if (tag.hasKey("computerMode")) {
			byte byteValue = tag.getByte("computerMode");
			computerMode = EnumLiftMode.get(clamp(0, 3, byteValue == -1 ? 3 : byteValue));
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setByte("mode", (byte)mode.ordinal());
		tag.setBoolean("computerEnabled", computerEnabled);
		tag.setByte("computerMode", (byte)computerMode.ordinal());
		return tag;
	}
	
	@Override
	public NBTTagCompound writeItemDropNBT(NBTTagCompound nbtTagCompound) {
		nbtTagCompound = super.writeItemDropNBT(nbtTagCompound);
		nbtTagCompound.removeTag("mode");
		return nbtTagCompound;
	}
	
	@Override
	public int getMaxEnergyStored() {
		return WarpDriveConfig.LIFT_MAX_ENERGY_STORED;
	}
	
	@Override
	public boolean canInputEnergy(EnumFacing from) {
		return true;
	}
	
	// OpenComputer callback methods
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] mode(Context context, Arguments arguments) {
		return mode(
			new Object[] {
				arguments.checkString(0)
			}
		);
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] active(Context context, Arguments arguments) {
		if (arguments.count() == 1) {
			computerEnabled = arguments.checkBoolean(0);
			markDirty();
		}
		return new Object[] { !computerEnabled && isEnabled };
	}
	
	private Object[] mode(Object[] arguments) {
		if (arguments.length == 1) {
			if (arguments[0].toString().equals("up")) {
				computerMode = EnumLiftMode.UP;
			} else if (arguments[0].toString().equals("down")) {
				computerMode = EnumLiftMode.DOWN;
			} else {
				computerMode = EnumLiftMode.REDSTONE;
			}
			markDirty();
		}
		
		switch (computerMode) {
		case REDSTONE:
			return new Object[] { "redstone" };
		case UP:
			return new Object[] { "up" };
		case DOWN:
			return new Object[] { "down" };
		default:
			return null;
		}
	}
	
	// ComputerCraft IPeripheral methods implementation
	@Override
	@Optional.Method(modid = "ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) {
		String methodName = getMethodName(method);
		
		if (methodName.equals("mode")) {
			return mode(arguments);
			
		} else if (methodName.equals("active")) {
			if (arguments.length == 1) {
				computerEnabled = toBool(arguments);
			}
			return new Object[] { !computerEnabled && isEnabled };
		}
		
		return super.callMethod(computer, context, method, arguments);
	}
}
