package cr0s.warpdrive.block.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import cr0s.warpdrive.data.EnumLaserTreeFarmMode;
import cr0s.warpdrive.data.SoundEvents;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Optional;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IPlantable;
import net.minecraft.util.EnumFacing;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.network.PacketHandler;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class TileEntityLaserTreeFarm extends TileEntityAbstractMiner {
	private boolean breakLeaves = false;
	private boolean tapTrees = false;
	
	private boolean isFarming() {
		return currentState != STATE_IDLE;
	}
	private static final int STATE_IDLE = 0;
	private static final int STATE_WARMUP = 1;
	private static final int STATE_SCAN = 2;
	private static final int STATE_HARVEST = 3;
	private static final int STATE_TAP = 4;
	private static final int STATE_PLANT = 5;
	private int currentState = STATE_IDLE;
	
	private boolean enoughPower = false;
	
	private static final int TREE_FARM_WARMUP_DELAY_TICKS = 40;
	private static final int TREE_FARM_SCAN_DELAY_TICKS = 40;
	private static final int TREE_FARM_HARVEST_LOG_DELAY_TICKS = 4;
	private static final int TREE_FARM_BREAK_LEAF_DELAY_TICKS = 4;
	private static final int TREE_FARM_SILKTOUCH_LEAF_DELAY_TICKS = 4;
	private static final int TREE_FARM_TAP_TREE_WET_DELAY_TICKS = 4;
	private static final int TREE_FARM_TAP_TREE_DRY_DELAY_TICKS = 1;
	private static final int TREE_FARM_PLANT_DELAY_TICKS = 1;
	private static final int TREE_FARM_LOW_POWER_DELAY_TICKS = 40;
	
	private static final int TREE_FARM_ENERGY_PER_SURFACE = 1;
	private static final int TREE_FARM_ENERGY_PER_WET_SPOT = 1;
	private static final double TREE_FARM_ENERGY_PER_LOG = 1;
	private static final double TREE_FARM_ENERGY_PER_LEAF = 1;
	private static final double TREE_FARM_SILKTOUCH_ENERGY_FACTOR = 2.0D;
	private static final int TREE_FARM_ENERGY_PER_SAPLING = 1;
	
	private int delayTargetTicks = 0;
	
	private int totalHarvested = 0;
	
	private boolean bScanOnReload = false;
	private int delayTicks = 0;
	
	private int radiusX = WarpDriveConfig.TREE_FARM_MAX_SCAN_RADIUS_NO_LASER_MEDIUM;
	private int radiusZ = WarpDriveConfig.TREE_FARM_MAX_SCAN_RADIUS_NO_LASER_MEDIUM;
	
	private LinkedList<BlockPos> soils;
	private int soilIndex = 0;
	private ArrayList<BlockPos> valuables;
	private int valuableIndex = 0;
	
	public TileEntityLaserTreeFarm() {
		super();
		laserOutputSide = EnumFacing.UP;
		peripheralName = "warpdriveLaserTreeFarm";
		addMethods(new String[] {
				"start",
				"stop",
				"radius",
				"state",
				"breakLeaves",
				"silktouch",
				"tapTrees"
		});
		laserMediumMaxCount = WarpDriveConfig.TREE_FARM_MAX_MEDIUMS_COUNT;
		CC_scripts = Arrays.asList("farm", "stop");
	}
	
	@SuppressWarnings("UnnecessaryReturnStatement")
	@Override
	public void update() {
		super.update();
		
		if (worldObj.isRemote) {
			return;
		}
		
		if (bScanOnReload) {
			soils = scanSoils();
			valuables = new ArrayList<>(scanTrees());
			bScanOnReload = false;
			return;
		}
		
		IBlockState blockState = worldObj.getBlockState(pos);
		if (currentState == STATE_IDLE) {
			delayTicks = 0;
			delayTargetTicks = TREE_FARM_WARMUP_DELAY_TICKS;
			updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.INACTIVE);
			
			// force start if no computer control is available
			if (!WarpDriveConfig.isComputerCraftLoaded && !WarpDriveConfig.isOpenComputersLoaded) {
				breakLeaves = true;
				enableSilktouch = false;
				tapTrees = true;
				start();
			}
			return;
		}
		
		delayTicks++;
		
		// Scanning
		if (currentState == STATE_WARMUP) {
			updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.SCANNING_LOW_POWER);
			if (delayTicks >= delayTargetTicks) {
				delayTicks = 0;
				delayTargetTicks = TREE_FARM_SCAN_DELAY_TICKS;
				currentState = STATE_SCAN;
				updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.SCANNING_LOW_POWER);
				return;
			}
			
		} else if (currentState == STATE_SCAN) {
			int energyCost = TREE_FARM_ENERGY_PER_SURFACE * (1 + 2 * radiusX) * (1 + 2 * radiusZ);
			if (delayTicks == 1) {
				if (WarpDriveConfig.LOGGING_COLLECTION) {
					WarpDrive.logger.debug("Scan pre-tick");
				}
				// check power level
				enoughPower = consumeEnergyFromLaserMediums(energyCost, true);
				if (!enoughPower) {
					currentState = STATE_WARMUP;	// going back to warmup state to show the animation when it'll be back online
					delayTicks = 0;
					delayTargetTicks = TREE_FARM_LOW_POWER_DELAY_TICKS;
					updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.SCANNING_LOW_POWER);
					return;
				} else {
					updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.SCANNING_POWERED);
				}
				
				// show current layer
				int age = Math.max(40, 2 * TREE_FARM_SCAN_DELAY_TICKS);
				double xMax = pos.getX() + radiusX + 1.0D;
				double xMin = pos.getX() - radiusX + 0.0D;
				double zMax = pos.getZ() + radiusZ + 1.0D;
				double zMin = pos.getZ() - radiusZ + 0.0D;
				double y = pos.getY() + worldObj.rand.nextInt(9);
				PacketHandler.sendBeamPacket(worldObj, new Vector3(xMin, y, zMin), new Vector3(xMax, y, zMin), 0.3F, 0.0F, 1.0F, age, 0, 50);
				PacketHandler.sendBeamPacket(worldObj, new Vector3(xMax, y, zMin), new Vector3(xMax, y, zMax), 0.3F, 0.0F, 1.0F, age, 0, 50);
				PacketHandler.sendBeamPacket(worldObj, new Vector3(xMax, y, zMax), new Vector3(xMin, y, zMax), 0.3F, 0.0F, 1.0F, age, 0, 50);
				PacketHandler.sendBeamPacket(worldObj, new Vector3(xMin, y, zMax), new Vector3(xMin, y, zMin), 0.3F, 0.0F, 1.0F, age, 0, 50);
				
			} else if (delayTicks >= delayTargetTicks) {
				if (WarpDriveConfig.LOGGING_COLLECTION) {
					WarpDrive.logger.debug("Scan tick");
				}
				delayTicks = 0;
				
				// consume power
				enoughPower = consumeEnergyFromLaserMediums(energyCost, false);
				if (!enoughPower) {
					delayTargetTicks = TREE_FARM_LOW_POWER_DELAY_TICKS;
					updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.SCANNING_LOW_POWER);
					return;
				} else {
					delayTargetTicks = TREE_FARM_SCAN_DELAY_TICKS;
					updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.SCANNING_POWERED);
				}
				
				// scan
				soils = scanSoils();
				soilIndex = 0;
				
				valuables = new ArrayList<>(scanTrees());
				valuableIndex = 0;
				if (!valuables.isEmpty()) {
					worldObj.playSound(null, pos, SoundEvents.LASER_HIGH, SoundCategory.BLOCKS, 4F, 1F);
					currentState = tapTrees ? STATE_TAP : STATE_HARVEST;
					delayTargetTicks = TREE_FARM_HARVEST_LOG_DELAY_TICKS;
					updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.FARMING_POWERED);
					return;
					
				} else if (soils != null && !soils.isEmpty()) {
					worldObj.playSound(null, pos, SoundEvents.LASER_HIGH, SoundCategory.BLOCKS, 4F, 1F);
					currentState = STATE_PLANT;
					delayTargetTicks = TREE_FARM_PLANT_DELAY_TICKS;
					updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.PLANTING_POWERED);
					return;
					
				} else {
					worldObj.playSound(null, pos, SoundEvents.LASER_LOW, SoundCategory.BLOCKS, 4F, 1F);
					currentState = STATE_WARMUP;
					delayTargetTicks = TREE_FARM_WARMUP_DELAY_TICKS;
					updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.SCANNING_LOW_POWER);
				}
			}
		} else if (currentState == STATE_HARVEST || currentState == STATE_TAP) {
			if (delayTicks >= delayTargetTicks) {
				if (WarpDriveConfig.LOGGING_COLLECTION) {
					WarpDrive.logger.debug("Harvest/tap tick");
				}
				delayTicks = 0;
				
				// harvesting done => plant
				if (valuables == null || valuableIndex >= valuables.size()) {
					valuableIndex = 0;
					currentState = STATE_PLANT;
					delayTargetTicks = TREE_FARM_PLANT_DELAY_TICKS;
					updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.PLANTING_POWERED);
					return;
				}
				
				// get current block
				BlockPos valuable = valuables.get(valuableIndex);
				IBlockState blockStateValuable = worldObj.getBlockState(valuable);
				valuableIndex++;
				boolean isLog = isLog(blockStateValuable.getBlock());
				boolean isLeaf = isLeaf(blockStateValuable.getBlock());
				
				// check area protection
				if (isBlockBreakCanceled(null, worldObj, valuable)) {
					if (WarpDriveConfig.LOGGING_COLLECTION) {
						WarpDrive.logger.info(this + " Harvesting cancelled at (" + valuable.getX() + " " + valuable.getY() + " " + valuable.getZ() + ")");
					}
					// done with this block
					return;
				}
				
				// save the rubber producing blocks in tapping mode
				if (currentState == STATE_TAP) {
					if (blockStateValuable.getBlock().isAssociatedBlock(WarpDriveConfig.IC2_rubberWood)) {
						int metadata = blockStateValuable.getBlock().getMetaFromState(blockStateValuable);
						if (metadata >= 2 && metadata <= 5) {
							if (WarpDriveConfig.LOGGING_COLLECTION) {
								WarpDrive.logger.info("Tap found rubber wood wet-spot at " + valuable + " with metadata " + metadata);
							}
							
							// consume power
							int energyCost = TREE_FARM_ENERGY_PER_WET_SPOT;
							enoughPower = consumeEnergyFromLaserMediums(energyCost, false);
							if (!enoughPower) {
								delayTargetTicks = TREE_FARM_LOW_POWER_DELAY_TICKS;
								updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.FARMING_LOW_POWER);
								return;
							} else {
								delayTargetTicks = TREE_FARM_TAP_TREE_WET_DELAY_TICKS;
								updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.FARMING_POWERED);
							}
							
							ItemStack resin = WarpDriveConfig.IC2_Resin.copy();
							resin.stackSize = (int) Math.round(Math.random() * 4);
							if (addToConnectedInventories(resin)) {
								stop();
							}
							totalHarvested += resin.stackSize;
							int age = Math.max(10, Math.round((4 + worldObj.rand.nextFloat()) * TREE_FARM_HARVEST_LOG_DELAY_TICKS));
							PacketHandler.sendBeamPacket(worldObj, laserOutput, new Vector3(valuable).translate(0.5D),
									0.8F, 0.8F, 0.2F, age, 0, 50);
							
							worldObj.setBlockState(valuable, blockStateValuable.getBlock().getStateFromMeta(metadata + 6), 3);
							// done with this block
							return;
							
						} else if (metadata != 0 && metadata != 1) {
							delayTargetTicks = TREE_FARM_TAP_TREE_DRY_DELAY_TICKS;
							// done with this block
							return;
						}
					}
				}
				
				if (isLog || (breakLeaves && isLeaf)) {// actually break the block?
					// consume power
					double energyCost = isLog ? TREE_FARM_ENERGY_PER_LOG : TREE_FARM_ENERGY_PER_LEAF;
					if (enableSilktouch) {
						energyCost *= TREE_FARM_SILKTOUCH_ENERGY_FACTOR;
					}
					enoughPower = consumeEnergyFromLaserMediums((int) Math.round(energyCost), false);
					if (!enoughPower) {
						delayTargetTicks = TREE_FARM_LOW_POWER_DELAY_TICKS;
						updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.FARMING_LOW_POWER);
						return;
					} else {
						delayTargetTicks = isLog ? TREE_FARM_HARVEST_LOG_DELAY_TICKS : enableSilktouch ? TREE_FARM_SILKTOUCH_LEAF_DELAY_TICKS : TREE_FARM_BREAK_LEAF_DELAY_TICKS;
						updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.FARMING_POWERED);
					}
					
					totalHarvested++;
					int age = Math.max(10, Math.round((4 + worldObj.rand.nextFloat()) * WarpDriveConfig.MINING_LASER_MINE_DELAY_TICKS));
					PacketHandler.sendBeamPacket(worldObj, laserOutput, new Vector3(valuable).translate(0.5D),
							0.2F, 0.7F, 0.4F, age, 0, 50);
					worldObj.playSound(null, pos, SoundEvents.LASER_LOW, SoundCategory.BLOCKS, 4F, 1F);
					
					harvestBlock(valuable);
				}
			}
		} else if (currentState == STATE_PLANT) {
			if (delayTicks >= delayTargetTicks) {
				if (WarpDriveConfig.LOGGING_COLLECTION) {
					WarpDrive.logger.debug("Plant final tick");
				}
				delayTicks = 0;
				
				// planting done => scan
				if (soils == null || soilIndex >= soils.size()) {
					soilIndex = 0;
					currentState = STATE_SCAN;
					delayTargetTicks = TREE_FARM_SCAN_DELAY_TICKS;
					updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.SCANNING_POWERED);
					return;
				}
				
				// get current block
				BlockPos soil = soils.get(soilIndex);
				BlockPos blockPosPlant = soil.add(0, 1, 0);
				IBlockState blockStateSoil = worldObj.getBlockState(soil);
				soilIndex++;
				Collection<IInventory> inventories = getConnectedInventories(this);
				if (inventories == null || inventories.isEmpty()) {
					currentState = STATE_WARMUP;
					delayTargetTicks = TREE_FARM_WARMUP_DELAY_TICKS;
					updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.SCANNING_LOW_POWER);
					return;
				}
				
				int slotIndex = 0;
				int plantableCount = 0;
				ItemStack itemStack = null;
				IBlockState plant = null;
				IInventory inventory = null;
				for (IInventory inventoryLoop : inventories) {
					if (plant == null) {
						slotIndex = 0;
					}
					while (slotIndex < inventoryLoop.getSizeInventory()) {
						itemStack = inventoryLoop.getStackInSlot(slotIndex);
						if (itemStack == null || itemStack.stackSize <= 0) {
							slotIndex++;
							continue;
						}
						Block blockFromItem = Block.getBlockFromItem(itemStack.getItem());
						if (!(itemStack.getItem() instanceof IPlantable) && !(blockFromItem instanceof IPlantable)) {
							slotIndex++;
							continue;
						}
						plantableCount++;
						IPlantable plantable = (IPlantable) ((itemStack.getItem() instanceof IPlantable) ? itemStack.getItem() : blockFromItem);
						plant = plantable.getPlant(worldObj, blockPosPlant);
						if (WarpDriveConfig.LOGGING_COLLECTION) {
							WarpDrive.logger.info("Slot " + slotIndex + " as " + itemStack + " which plantable " + plantable + " as block " + plant);
						}
						
						if (!blockStateSoil.getBlock().canSustainPlant(blockStateSoil, worldObj, soil, EnumFacing.UP, plantable)) {
							plant = null;
							slotIndex++;
							continue;
						}
						
						if (!plant.getBlock().canPlaceBlockAt(worldObj, blockPosPlant)) {
							plant = null;
							slotIndex++;
							continue;
						}
						
						inventory = inventoryLoop;
					}
				}
				
				// no plantable found at all, back to scanning
				if (plantableCount <= 0) {
					currentState = STATE_SCAN;
					delayTargetTicks = TREE_FARM_SCAN_DELAY_TICKS;
					updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.SCANNING_POWERED);
					return;
				}
				
				// no sapling found for this soil, moving on...
				if (inventory == null || plant == null || itemStack == null) {
					if (WarpDriveConfig.LOGGING_COLLECTION) {
						WarpDrive.logger.debug("No sapling found");
					}
					return;
				}
				
				// check area protection
				if (isBlockPlaceCanceled(null, worldObj, blockPosPlant, plant)) {
					if (WarpDriveConfig.LOGGING_COLLECTION) {
						WarpDrive.logger.info(this + " Planting cancelled at (" + blockPosPlant.getX() + " " + blockPosPlant.getY() + " " + blockPosPlant.getZ() + ")");
					}
					// done with this block
					return;
				}
				
				// consume power
				double energyCost = TREE_FARM_ENERGY_PER_SAPLING;
				enoughPower = consumeEnergyFromLaserMediums((int) Math.round(energyCost), false);
				if (!enoughPower) {
					delayTargetTicks = TREE_FARM_LOW_POWER_DELAY_TICKS;
					updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.PLANTING_LOW_POWER);
					return;
				} else {
					delayTargetTicks = TREE_FARM_PLANT_DELAY_TICKS;
					updateBlockState(blockState, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.PLANTING_POWERED);
				}
				
				itemStack.stackSize--;
				if (itemStack.stackSize <= 0) {
					itemStack = null;
				}
				inventory.setInventorySlotContents(slotIndex, itemStack);
				
				// totalPlanted++;
				int age = Math.max(10, Math.round((4 + worldObj.rand.nextFloat()) * WarpDriveConfig.MINING_LASER_MINE_DELAY_TICKS));
				PacketHandler.sendBeamPacket(worldObj, laserOutput, new Vector3(blockPosPlant).translate(0.5D),
						0.2F, 0.7F, 0.4F, age, 0, 50);
				worldObj.playSound(null, pos, SoundEvents.LASER_LOW, SoundCategory.BLOCKS, 4F, 1F);
				worldObj.setBlockState(blockPosPlant, plant, 3);
			}
		}
	}
	
	@Override
	protected void stop() {
		super.stop();
		currentState = STATE_IDLE;
		updateBlockState(null, BlockLaserTreeFarm.MODE, EnumLaserTreeFarmMode.INACTIVE);
	}
	
	private static boolean isSoil(Block block) {
		return Dictionary.BLOCKS_SOILS.contains(block);
	}
	
	private static boolean isLog(Block block) {
		return Dictionary.BLOCKS_LOGS.contains(block);
	}
	
	private static boolean isLeaf(Block block) {
		return Dictionary.BLOCKS_LEAVES.contains(block);
	}
	
	private LinkedList<BlockPos> scanSoils() {
		int maxRadius = WarpDriveConfig.TREE_FARM_MAX_SCAN_RADIUS_NO_LASER_MEDIUM + laserMediumCount * WarpDriveConfig.TREE_FARM_MAX_SCAN_RADIUS_PER_LASER_MEDIUM;
		int xMin = pos.getX() - Math.min(radiusX, maxRadius);
		int xMax = pos.getX() + Math.min(radiusX, maxRadius);
		int yMin = pos.getY();
		int yMax = pos.getY() + 8;
		int zMin = pos.getZ() - Math.min(radiusZ, maxRadius);
		int zMax = pos.getZ() + Math.min(radiusZ, maxRadius);
		
		LinkedList<BlockPos> soilPositions = new LinkedList<>();
		
		for(int y = yMin; y <= yMax; y++) {
			for(int x = xMin; x <= xMax; x++) {
				for(int z = zMin; z <= zMax; z++) {
					BlockPos blockPos = new BlockPos(x, y, z);
					if (worldObj.isAirBlock(blockPos.add(0, 1, 0))) {
						Block block = worldObj.getBlockState(blockPos).getBlock();
						if (isSoil(block)) {
							if (WarpDriveConfig.LOGGING_COLLECTION) {
								WarpDrive.logger.info("Found soil at " + x + " " + y + " " + z);
							}
							soilPositions.add(blockPos);
						}
					}
				}
			}
		}
		if (WarpDriveConfig.LOGGING_COLLECTION) {
			WarpDrive.logger.info("Found " + soilPositions.size() + " soils");
		}
		return soilPositions;
	}
	
	private Collection<BlockPos> scanTrees() {
		int maxRadius = WarpDriveConfig.TREE_FARM_MAX_SCAN_RADIUS_NO_LASER_MEDIUM + laserMediumCount * WarpDriveConfig.TREE_FARM_MAX_SCAN_RADIUS_PER_LASER_MEDIUM;
		int xMin = pos.getX() - Math.min(radiusX, maxRadius);
		int xMax = pos.getX() + Math.min(radiusX, maxRadius);
		int yMin = pos.getY() + 1;
		int yMax = pos.getY() + 1 + (tapTrees ? 8 : 0);
		int zMin = pos.getZ() - Math.min(radiusZ, maxRadius);
		int zMax = pos.getZ() + Math.min(radiusZ, maxRadius);
		
		Collection<BlockPos> logPositions = new HashSet<>();
		
		for(int y = yMin; y <= yMax; y++) {
			for(int x = xMin; x <= xMax; x++) {
				for(int z = zMin; z <= zMax; z++) {
					BlockPos blockPos = new BlockPos(x, y, z);
					Block block = worldObj.getBlockState(blockPos).getBlock();
					if (isLog(block)) {
						if (!logPositions.contains(blockPos)) {
							if (WarpDriveConfig.LOGGING_COLLECTION) {
								WarpDrive.logger.info("Found tree base at " + x + "," + y + "," + z);
							}
							logPositions.add(blockPos);
						}
					}
				}
			}
		}
		if (!logPositions.isEmpty()) {
			@SuppressWarnings("unchecked")
			HashSet<Block> whitelist = (HashSet<Block>) Dictionary.BLOCKS_LOGS.clone();
			if (breakLeaves) {
				whitelist.addAll(Dictionary.BLOCKS_LEAVES);
			}
			logPositions = getConnectedBlocks(worldObj, logPositions, UP_DIRECTIONS, whitelist, WarpDriveConfig.TREE_FARM_MAX_LOG_DISTANCE + laserMediumCount * WarpDriveConfig.TREE_FARM_MAX_LOG_DISTANCE_PER_MEDIUM);
		}
		if (WarpDriveConfig.LOGGING_COLLECTION) {
			WarpDrive.logger.info("Found " + logPositions.size() + " valuables");
		}
		return logPositions;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setInteger("radiusX", radiusX);
		tag.setInteger("radiusZ", radiusZ);
		tag.setBoolean("breakLeaves", breakLeaves);
		tag.setBoolean("tapTrees", tapTrees);
		tag.setInteger("currentState", currentState);
		return tag;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		radiusX = tag.getInteger("radiusX");
		if (radiusX == 0) {
			radiusX = 1;
		}
		radiusX = clamp(1, WarpDriveConfig.TREE_FARM_totalMaxRadius, radiusX);
		radiusZ = tag.getInteger("radiusZ");
		if (radiusZ == 0) {
			radiusZ = 1;
		}
		radiusZ = clamp(1, WarpDriveConfig.TREE_FARM_totalMaxRadius, radiusZ);
		
		breakLeaves     = tag.getBoolean("breakLeaves");
		tapTrees        = tag.getBoolean("tapTrees");
		currentState    = tag.getInteger("currentState");
		if (currentState == STATE_HARVEST || currentState == STATE_TAP || currentState == STATE_PLANT) {
			bScanOnReload = true;
		}
	}
	
	// OpenComputer callback methods
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] start(Context context, Arguments arguments) {
		return start();
	}
	
	@SuppressWarnings("SameReturnValue")
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] stop(Context context, Arguments arguments) {
		stop();
		return null;
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] state(Context context, Arguments arguments) {
		return state();
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] radius(Context context, Arguments arguments) {
		return radius(argumentsOCtoCC(arguments));
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] breakLeaves(Context context, Arguments arguments) {
		return breakLeaves(argumentsOCtoCC(arguments));
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] silktouch(Context context, Arguments arguments) {
		return silktouch(argumentsOCtoCC(arguments));
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] tapTrees(Context context, Arguments arguments) {
		return tapTrees(argumentsOCtoCC(arguments));
	}
	
	// Common OC/CC methods
	private Object[] start() {
		if (isFarming()) {
			return new Object[] { false, "Already started" };
		}
		
		totalHarvested = 0;
		delayTicks = 0;
		currentState = STATE_WARMUP;
		return new Boolean[] { true };
	}
	
	private Object[] state() {
		int energy = getEnergyStored();
		String status = getStatus().getFormattedText();
		Integer retValuables, retValuablesIndex;
		if (isFarming() && valuables != null) {
			retValuables = valuables.size();
			retValuablesIndex = valuableIndex;
			
			return new Object[] { status, isFarming(), energy, totalHarvested, retValuablesIndex, retValuables };
		}
		return new Object[] { status, isFarming(), energy, totalHarvested, 0, 0 };
	}
	
	private Object[] radius(Object[] arguments) {
		try {
			if (arguments.length == 1) {
				radiusX = clamp(1, WarpDriveConfig.TREE_FARM_totalMaxRadius, toInt(arguments[0]));
				radiusZ = radiusX;
				markDirty();
			} else if (arguments.length == 2) {
				radiusX = clamp(1, WarpDriveConfig.TREE_FARM_totalMaxRadius, toInt(arguments[0]));
				radiusZ = clamp(1, WarpDriveConfig.TREE_FARM_totalMaxRadius, toInt(arguments[1]));
				markDirty();
			}
		} catch(NumberFormatException exception) {
			radiusX = WarpDriveConfig.TREE_FARM_MAX_SCAN_RADIUS_NO_LASER_MEDIUM;
			radiusZ = WarpDriveConfig.TREE_FARM_MAX_SCAN_RADIUS_NO_LASER_MEDIUM;
		}
		return new Integer[] { radiusX , radiusZ };
	}
	
	private Object[] breakLeaves(Object[] arguments) {
		if (arguments.length == 1) {
			try {
				breakLeaves = toBool(arguments[0]);
				markDirty();
			} catch (Exception exception) {
				return new Object[] { breakLeaves };
			}
		}
		return new Object[] { breakLeaves };
	}
	
	private Object[] silktouch(Object[] arguments) {
		if (arguments.length == 1) {
			try {
				enableSilktouch = toBool(arguments[0]);
				markDirty();
			} catch (Exception exception) {
				return new Object[] { enableSilktouch };
			}
		}
		return new Object[] { enableSilktouch };
	}
	
	private Object[] tapTrees(Object[] arguments) {
		if (arguments.length == 1) {
			try {
				tapTrees = toBool(arguments[0]);
				markDirty();
			} catch (Exception exception) {
				return new Object[] { tapTrees };
			}
		}
		return new Object[] { tapTrees };
	}
	
	// ComputerCraft IPeripheral methods implementation
	@Override
	@Optional.Method(modid = "ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) {
		String methodName = getMethodName(method);
		
		switch (methodName) {
			case "start":
				return start();

			case "stop":
				stop();
				return null;

			case "state":
				return state();

			case "radius":
				return radius(arguments);

			case "breakLeaves":
				return breakLeaves(arguments);

			case "silktouch":
				return silktouch(arguments);

			case "tapTrees":
				return tapTrees(arguments);
		}
		
		return super.callMethod(computer, context, method, arguments);
	}
	
	@Override
	public ITextComponent getStatus() {
		// @TODO merge with base
		int energy = getEnergyStored();
		String state = "IDLE (not farming)";
		if (currentState == STATE_IDLE) {
			state = "IDLE (not farming)";
		} else if (currentState == STATE_WARMUP) {
			state = "Warming up...";
		} else if (currentState == STATE_SCAN) {
			if (breakLeaves) {
				state = "Scanning all";
			} else {
				state = "Scanning logs";
			}
		} else if (currentState == STATE_HARVEST) {
			if (breakLeaves) {
				state = "Harvesting all";
			} else {
				state = "Harvesting logs";
			}
			if (enableSilktouch) {
				state = state + " with silktouch";
			}
		} else if (currentState == STATE_TAP) {
			if (breakLeaves) {
				state = "Tapping trees, harvesting all";
			} else {
				state = "Tapping trees, harvesting logs";
			}
			if (enableSilktouch) {
				state = state + " with silktouch";
			}
		} else if (currentState == STATE_PLANT) {
			state = "Planting trees";
		}
		if (energy <= 0) {
			state = state + " - Out of energy";
		} else if (((currentState == STATE_SCAN) || (currentState == STATE_HARVEST) || (currentState == STATE_TAP)) && !enoughPower) {
			state = state + " - Not enough power";
		}
		return new TextComponentString(state);
	}
}
