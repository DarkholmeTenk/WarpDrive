package cr0s.warpdrive.block.detection;

import java.util.Arrays;

import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.SoundEvents;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.TileEntityAbstractEnergy;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.CloakedArea;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.network.PacketHandler;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class TileEntityCloakingCore extends TileEntityAbstractEnergy {
	public boolean isEnabled = false;
	public byte tier = 1; // cloaking field tier, 1 or 2
	
	// inner coils color map
	final float[] innerCoilColor_r = { 1.00f, 1.00f, 1.00f, 1.00f, 0.75f, 0.25f, 0.00f, 0.00f, 0.00f, 0.00f, 0.50f, 1.00f }; 
	final float[] innerCoilColor_g = { 0.00f, 0.25f, 0.75f, 1.00f, 1.00f, 1.00f, 1.00f, 1.00f, 0.50f, 0.25f, 0.00f, 0.00f }; 
	final float[] innerCoilColor_b = { 0.25f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.50f, 1.00f, 1.00f, 1.00f, 1.00f, 0.75f }; 
	
	// Spatial cloaking field parameters
	private static final int innerCoilsDistance = 2; // Step length from core block to main coils
	private final int[] outerCoilsDistance = {0, 0, 0, 0, 0, 0};
	public int minX = 0;
	public int minY = 0;
	public int minZ = 0;
	public int maxX = 0;
	public int maxY = 0;
	public int maxZ = 0;
	
	public boolean isValid = false;
	public boolean isCloaking = false;
	public int volume = 0;
	private int updateTicks = 0;
	private int laserDrawingTicks = 0;
	
	private boolean soundPlayed = false;
	private int soundTicks = 0;
	
	public TileEntityCloakingCore() {
		super();
		peripheralName = "warpdriveCloakingCore";
		addMethods(new String[] {
			"tier",				// set field tier to 1 or 2, return field tier
			"isAssemblyValid",	// returns true or false
			"enable"			// set field enable state (true or false), return true if enabled
		});
		CC_scripts = Arrays.asList("cloak1", "cloak2", "uncloak");
	}
	
	@Override
	public void update() {
		super.update();
		
		if (worldObj.isRemote) {
			return;
		}
		
		// Reset sound timer
		soundTicks++;
		if (soundTicks >= 40) {
			soundTicks = 0;
			soundPlayed = false;
		}
		
		updateTicks--;
		if (updateTicks <= 0) {
			if (WarpDriveConfig.LOGGING_CLOAKING) {
				WarpDrive.logger.info(this + " Updating cloaking state...");
			}
			updateTicks = ((tier == 1) ? 20 : (tier == 2) ? 10 : 20) * WarpDriveConfig.CLOAKING_FIELD_REFRESH_INTERVAL_SECONDS; // resetting timer
			
			isValid = validateAssembly();
			isCloaking = WarpDrive.cloaks.isAreaExists(worldObj, pos); 
			if (!isEnabled) {// disabled
				if (isCloaking) {// disabled, cloaking => stop cloaking
					if (WarpDriveConfig.LOGGING_CLOAKING) {
						WarpDrive.logger.info(this + " Disabled, cloak field going down...");
					}
					disableCloakingField();
				} else {// disabled, no cloaking
					// IDLE
				}
			} else {// isEnabled
				boolean hasEnoughPower = countBlocksAndConsumeEnergy();
				if (!isCloaking) {// enabled, not cloaking
					if (hasEnoughPower && isValid) {// enabled, can cloak and able to
						setCoilsState(true);
						
						// Register cloak
						WarpDrive.cloaks.updateCloakedArea(worldObj,
								worldObj.provider.getDimension(), pos, tier,
								minX, minY, minZ, maxX, maxY, maxZ);
						if (!soundPlayed) {
							soundPlayed = true;
							worldObj.playSound(null, pos, SoundEvents.CLOAK, SoundCategory.BLOCKS, 4F, 1F);
						}
						
						// Refresh the field
						CloakedArea area = WarpDrive.cloaks.getCloakedArea(worldObj, pos);
						if (area != null) {
							area.sendCloakPacketToPlayersEx(false); // re-cloak field
						} else {
							if (WarpDriveConfig.LOGGING_CLOAKING) {
								WarpDrive.logger.info("getCloakedArea1 returned null for " + worldObj + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
							}
						}
					} else {// enabled, not cloaking but not able to
						// IDLE
					}
				} else {// enabled & cloaked
					if (!isValid) {// enabled, cloaking but invalid
						if (WarpDriveConfig.LOGGING_CLOAKING) {
							WarpDrive.logger.info(this + " Coil(s) lost, cloak field is collapsing...");
						}
						consumeAllEnergy();
						disableCloakingField();				
					} else {// enabled, cloaking and valid
						if (hasEnoughPower) {// enabled, cloaking and able to
							// IDLE
							// Refresh the field (workaround to re-synchronize players since client may 'eat up' the packets)
							CloakedArea area = WarpDrive.cloaks.getCloakedArea(worldObj, pos);
							if (area != null) {
								area.sendCloakPacketToPlayersEx(false); // re-cloak field
							} else {
								if (WarpDriveConfig.LOGGING_CLOAKING) {
									WarpDrive.logger.info("getCloakedArea2 returned null for " + worldObj + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
								}
							}
							setCoilsState(true);
						} else {// loosing power
							if (WarpDriveConfig.LOGGING_CLOAKING) {
								WarpDrive.logger.info(this + " Low power, cloak field is collapsing...");
							}
							disableCloakingField();
						}
					}
				}
			}
		}
		
		if (laserDrawingTicks++ > 100) {
			laserDrawingTicks = 0;
			
			if (isEnabled && isValid) {
				drawLasers();
			}
		}
	}
	
	private void setCoilsState(final boolean enabled) {
		updateBlockState(null, BlockProperties.ACTIVE, enabled);
		
		for (EnumFacing direction : EnumFacing.VALUES) {
			setCoilState(innerCoilsDistance, direction, enabled);
			setCoilState(outerCoilsDistance[direction.ordinal()], direction, enabled);
		}
	}
	
	private void setCoilState(final int distance, final EnumFacing facing, final boolean enabled) {
		BlockPos blockPos = pos.offset(facing);
		if (worldObj.getBlockState(blockPos).getBlock().isAssociatedBlock(WarpDrive.blockCloakingCoil)) {
			if (distance == innerCoilsDistance) {
				BlockCloakingCoil.setBlockState(worldObj, blockPos, enabled, false, EnumFacing.UP);
			} else {
				BlockCloakingCoil.setBlockState(worldObj, blockPos, enabled, true, null);
			}
		}
	}
	
	private void drawLasers() {
		float r = 0.0f;
		float g = 1.0f;
		float b = 0.0f;
		if (!isCloaking) {// out of energy
			r = 0.75f;
			g = 0.50f;
			b = 0.50f;
		} else if (tier == 1) {
			r = 0.25f;
			g = 1.00f;
			b = 0.00f;
		} else if (tier == 2) {
			r = 0.00f;
			g = 0.25f;
			b = 1.00f;
		}
		
		// Directions to check (all six directions: left, right, up, down, front, back)
		for (EnumFacing facing : EnumFacing.values()) {
			PacketHandler.sendBeamPacketToPlayersInArea(worldObj,
					new Vector3(
						pos.getX() + innerCoilsDistance * facing.getFrontOffsetX(),
						pos.getY() + innerCoilsDistance * facing.getFrontOffsetY(),
						pos.getZ() + innerCoilsDistance * facing.getFrontOffsetZ()).translate(0.5),
					new Vector3(
						pos.getX() + outerCoilsDistance[facing.ordinal()] * facing.getFrontOffsetX(),
						pos.getY() + outerCoilsDistance[facing.ordinal()] * facing.getFrontOffsetY(),
						pos.getZ() + outerCoilsDistance[facing.ordinal()] * facing.getFrontOffsetZ()).translate(0.5),
					r, g, b, 110, 0,
					new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
		}
		
		// draw connecting coils
		for (int i = 0; i < 5; i++) {
			EnumFacing start = EnumFacing.VALUES[i];
			for (int j = i + 1; j < 6; j++) {
				EnumFacing stop = EnumFacing.VALUES[j];
				// skip mirrored coils (removing the inner lines)
				if (start.getOpposite() == stop) {
					continue;
				}
				
				// draw a random colored beam
				int mapIndex = worldObj.rand.nextInt(innerCoilColor_b.length);
				r = innerCoilColor_r[mapIndex];
				g = innerCoilColor_g[mapIndex];
				b = innerCoilColor_b[mapIndex];
				
				PacketHandler.sendBeamPacketToPlayersInArea(worldObj,
					new Vector3(
						pos.getX() + innerCoilsDistance * start.getFrontOffsetX(),
						pos.getY() + innerCoilsDistance * start.getFrontOffsetY(),
						pos.getZ() + innerCoilsDistance * start.getFrontOffsetZ()).translate(0.5),
					new Vector3(
						pos.getX() + innerCoilsDistance * stop .getFrontOffsetX(),
						pos.getY() + innerCoilsDistance * stop .getFrontOffsetY(),
						pos.getZ() + innerCoilsDistance * stop .getFrontOffsetZ()).translate(0.5),
					r, g, b, 110, 0,
					new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
			}
		}
	}
	
	public void disableCloakingField() {
		setCoilsState(false);
		if (WarpDrive.cloaks.isAreaExists(worldObj, pos)) {
			WarpDrive.cloaks.removeCloakedArea(worldObj.provider.getDimension(), pos);
			
			if (!soundPlayed) {
				soundPlayed = true;
				worldObj.playSound(null, pos, SoundEvents.DECLOAK, SoundCategory.BLOCKS, 4F, 1F);
			}
		}
	}
	
	public boolean countBlocksAndConsumeEnergy() {
		int x, y, z, energyToConsume;
		volume = 0;
		if (tier == 1) {// tier1 = gaz and air blocks don't count
			for (y = minY; y <= maxY; y++) {
				for (x = minX; x <= maxX; x++) {
					for(z = minZ; z <= maxZ; z++) {
						if (!worldObj.isAirBlock(new BlockPos(x, y, z))) {
							volume++;
						} 
					}
				}
			}
			energyToConsume = volume * WarpDriveConfig.CLOAKING_TIER1_ENERGY_PER_BLOCK;
		} else {// tier2 = everything counts
			for (y = minY; y <= maxY; y++) {
				for (x = minX; x <= maxX; x++) {
					for(z = minZ; z <= maxZ; z++) {
						if (!worldObj.getBlockState(new BlockPos(x, y, z)).getBlock().isAssociatedBlock(Blocks.AIR)) {
							volume++;
						} 
					}
				}
			}
			energyToConsume = volume * WarpDriveConfig.CLOAKING_TIER2_ENERGY_PER_BLOCK;
		}
		
		// WarpDrive.logger.info(this + " Consuming " + energyToConsume + " EU for " + blocksCount + " blocks");
		return consumeEnergy(energyToConsume, false);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tier = tag.getByte("tier");
		isEnabled = tag.getBoolean("enabled");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setByte("tier", tier);
		tag.setBoolean("enabled", isEnabled);
		return tag;
	}
	
	public boolean validateAssembly() {
		final int maxOuterCoilDistance = WarpDriveConfig.CLOAKING_MAX_FIELD_RADIUS - WarpDriveConfig.CLOAKING_COIL_CAPTURE_BLOCKS; 
		
		// Directions to check (all six directions: left, right, up, down, front, back)
		for (EnumFacing direction : EnumFacing.values()) {
			
			// check validity of inner coil
			BlockPos blockPos = new BlockPos(pos.offset(direction, innerCoilsDistance));
			if (worldObj.getBlockState(blockPos).getBlock().isAssociatedBlock(WarpDrive.blockCloakingCoil)) {
				BlockCloakingCoil.setBlockState(worldObj, blockPos, true, false, EnumFacing.UP);
			} else {
				return false;
			}
			
			// find closest outer coil
			int newCoilDistance = 0;
			for (int distance = 3; distance < maxOuterCoilDistance; distance++) {
				blockPos = blockPos.offset(direction);
				
				if (worldObj.getBlockState(blockPos).getBlock().isAssociatedBlock(WarpDrive.blockCloakingCoil)) {
					BlockCloakingCoil.setBlockState(worldObj, blockPos, true, true, direction);
					newCoilDistance = distance;
					break;
				}
			}
			
			// disable previous outer coil, in case a different one was found
			int oldCoilDistance = outerCoilsDistance[direction.ordinal()];
			if ( newCoilDistance != oldCoilDistance && oldCoilDistance > 0) {
				BlockPos blockPosOld = pos.offset(direction, oldCoilDistance);
				if (worldObj.getBlockState(blockPosOld).getBlock().isAssociatedBlock(WarpDrive.blockCloakingCoil)) {
					BlockCloakingCoil.setBlockState(worldObj, blockPos, false, false, EnumFacing.UP);
				}
			}
			
			// check validity and save new coil position
			if (newCoilDistance <= 0) {
				outerCoilsDistance[direction.ordinal()] = 0;
				if (WarpDriveConfig.LOGGING_CLOAKING) {
					WarpDrive.logger.info("Invalid outer coil assembly at " + direction);
				}
				return false;
			}
			outerCoilsDistance[direction.ordinal()] = newCoilDistance;
		}
		
		// Update cloaking field parameters defined by coils		
		minX =               pos.getX() - outerCoilsDistance[4] - WarpDriveConfig.CLOAKING_COIL_CAPTURE_BLOCKS;
		maxX =               pos.getX() + outerCoilsDistance[5] + WarpDriveConfig.CLOAKING_COIL_CAPTURE_BLOCKS;
		minY = Math.max(  0, pos.getY() - outerCoilsDistance[0] - WarpDriveConfig.CLOAKING_COIL_CAPTURE_BLOCKS);
		maxY = Math.min(255, pos.getY() + outerCoilsDistance[1] + WarpDriveConfig.CLOAKING_COIL_CAPTURE_BLOCKS);
		minZ =               pos.getZ() - outerCoilsDistance[2] - WarpDriveConfig.CLOAKING_COIL_CAPTURE_BLOCKS;
		maxZ =               pos.getZ() + outerCoilsDistance[3] + WarpDriveConfig.CLOAKING_COIL_CAPTURE_BLOCKS;
		return true;
	}
	
	@Override
	public ITextComponent getStatus() {
		if (worldObj == null) {
			return super.getStatus();
		}
		
		String unlocalizedStatus;
		if (!isValid) {
			unlocalizedStatus = "warpdrive.cloakingCore.invalidAssembly";
		} else if (!isEnabled) {
			unlocalizedStatus = "warpdrive.cloakingCore.disabled";
		} else if (!isCloaking) {
			unlocalizedStatus = "warpdrive.cloakingCore.lowPower";
		} else {
			unlocalizedStatus = "warpdrive.cloakingCore.cloaking";
		}
		return super.getStatus()
				.appendSibling(new TextComponentString("\n")).appendSibling(new TextComponentTranslation(unlocalizedStatus,
						tier,
						volume));
	}
	
	// OpenComputer callback methods
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] tier(Context context, Arguments arguments) {
		if (arguments.count() == 1) {
			if (arguments.checkInteger(0) == 2) {
				tier = 2;
			} else {
				tier = 1;
			}
			markDirty();
		}
		return new Integer[] { (int)tier };
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] isAssemblyValid(Context context, Arguments arguments) {
		return new Object[] { validateAssembly() };
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] enable(Context context, Arguments arguments) {
		if (arguments.count() == 1) {
			isEnabled = arguments.checkBoolean(0);
			markDirty();
		}
		return new Object[] { isEnabled };
	}
	
	// ComputerCraft IPeripheral methods implementation
	@Override
	@Optional.Method(modid = "ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) {
		String methodName = getMethodName(method);
		
		switch (methodName) {
			case "tier":
				if (arguments.length == 1) {
					if (toInt(arguments[0]) == 2) {
						tier = 2;
					} else {
						tier = 1;
					}
					markDirty();
				}
				return new Integer[] { (int) tier };

			case "isAssemblyValid":
				return new Object[] { validateAssembly() };

			case "enable":
				if (arguments.length == 1) {
					isEnabled = toBool(arguments[0]);
					markDirty();
				}
				return new Object[] { isEnabled };
		}
		
		return super.callMethod(computer, context, method, arguments);
	}
	
	@Override
	public int getMaxEnergyStored() {
		return WarpDriveConfig.CLOAKING_MAX_ENERGY_STORED;
	}
	
	@Override
	public boolean canInputEnergy(EnumFacing from) {
		return true;
	}
}
