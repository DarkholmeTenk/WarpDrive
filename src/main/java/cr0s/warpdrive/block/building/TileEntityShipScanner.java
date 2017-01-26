package cr0s.warpdrive.block.building;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cr0s.warpdrive.data.*;
import cr0s.warpdrive.item.ItemCrystalToken;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.TileEntityAbstractEnergy;
import cr0s.warpdrive.block.movement.TileEntityShipCore;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.network.PacketHandler;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class TileEntityShipScanner extends TileEntityAbstractEnergy {
	private boolean isActive = false;
	private TileEntityShipCore shipCore = null;
	
	private int laserTicks = 0;
	private int scanTicks = 0;
	private int deployDelayTicks = 0;
	
	private int searchTicks = 0;
	
	private String schematicFileName = "";
	private String playerName = "";
	
	private JumpShip jumpShip;
	private int currentDeployIndex;
	private int blocksToDeployCount;
	private boolean isDeploying = false;
	
	private int targetX, targetY, targetZ;
	private byte rotationSteps;
	
	public TileEntityShipScanner() {
		super();
		
		peripheralName = "warpdriveShipScanner";
		addMethods(new String[] {
				"scan",
				"fileName",
				"deploy",
				"state"
		});
	}
	
	@Override
	public void update() {
		super.update();
		
		if (worldObj.isRemote) {
			return;
		}
		
		searchTicks++;
		if (searchTicks > WarpDriveConfig.SS_SEARCH_INTERVAL_TICKS) {
			searchTicks = 0;
			shipCore = searchShipCore();
		}
		
		// Trigger deployment by player
		if (!isActive) {
			checkPlayerToken();
		}
		
		// Ship core is not found
		if (!isDeploying && shipCore == null) {
			setActive(false); // disable scanner
			laserTicks++;
			if (laserTicks > 20) {
				PacketHandler.sendBeamPacket(worldObj,
					new Vector3(this).translate(0.5D),
					new Vector3(pos.getX(), pos.getY() + 5, pos.getZ()).translate(0.5D), 
					1.0F, 0.2F, 0.0F, 40, 0, 100);
				laserTicks = 0;
			}
			return;
		}
		
		if (!isActive) {// inactive
			laserTicks++;
			if (laserTicks > 20) {
				PacketHandler.sendBeamPacket(worldObj,
					new Vector3(this).translate(0.5D),
					new Vector3(shipCore).translate(0.5D),
					0.0F, 1.0F, 0.2F, 40, 0, 100);
				laserTicks = 0;
			}
		} else if (!isDeploying) {// active and scanning
			laserTicks++;
			if (laserTicks > 5) {
				laserTicks = 0;
				
				for (int index = 0; index < 10; index++) {
					int randomX = shipCore.minX + worldObj.rand.nextInt(shipCore.maxX - shipCore.minX + 1);
					int randomY = shipCore.minY + worldObj.rand.nextInt(shipCore.maxY - shipCore.minY + 1);
					int randomZ = shipCore.minZ + worldObj.rand.nextInt(shipCore.maxZ - shipCore.minZ + 1);

					worldObj.playSound(null, pos, SoundEvents.LASER_LOW, SoundCategory.HOSTILE, 4F, 1F);
					float r = worldObj.rand.nextFloat() - worldObj.rand.nextFloat();
					float g = worldObj.rand.nextFloat() - worldObj.rand.nextFloat();
					float b = worldObj.rand.nextFloat() - worldObj.rand.nextFloat();
					
					PacketHandler.sendBeamPacket(worldObj,
							new Vector3(this).translate(0.5D),
							new Vector3(randomX, randomY, randomZ).translate(0.5D),
							r, g, b, 15, 0, 100);
				}
			}
			
			scanTicks++;
			if (scanTicks > 20 * (1 + shipCore.shipMass / WarpDriveConfig.SS_SCAN_BLOCKS_PER_SECOND)) {
				setActive(false); // disable scanner
				scanTicks = 0;
			}
			
		} else {// active and deploying
			deployDelayTicks++;
			if (deployDelayTicks > WarpDriveConfig.SS_DEPLOY_INTERVAL_TICKS) {
				deployDelayTicks = 0;
				
				// deploy at most (jump speed / 4), at least (deploy speed), optimally in 10 seconds 
				final int optimumSpeed = Math.round(blocksToDeployCount * WarpDriveConfig.SS_DEPLOY_INTERVAL_TICKS / (20 * 10.0F));
				int blockToDeployPerTick = Math.max(WarpDriveConfig.SS_DEPLOY_BLOCKS_PER_INTERVAL,
					Math.min(WarpDriveConfig.G_BLOCKS_PER_TICK / 4, optimumSpeed));
				int blocksToDeployCurrentTick = Math.min(blockToDeployPerTick, blocksToDeployCount - currentDeployIndex);
				int periodLaserEffect = Math.max(1, (blocksToDeployCurrentTick / 10));
				// WarpDrive.logger.info("optimumSpeed " + optimumSpeed + " blockToDeployPerTick " + blockToDeployPerTick + " blocksToDeployCurrentTick " + blocksToDeployCurrentTick + " currentDeployIndex " + currentDeployIndex);
				
				if (blocksToDeployCurrentTick == 0) {
					isDeploying = false;
					setActive(false); // disable scanner
					if (WarpDriveConfig.LOGGING_BUILDING) {
						WarpDrive.logger.info(this + " Deployment done");
					}
					return;
				}
				
				if (WarpDriveConfig.LOGGING_BUILDING) {
					WarpDrive.logger.info(this + " Deploying " + blocksToDeployCurrentTick + " more blocks");
				}
				Transformation transformation = new Transformation(jumpShip, worldObj, targetX - jumpShip.core.getX(), targetY - jumpShip.core.getY(), targetZ - jumpShip.core.getZ(), rotationSteps);
				int index = 0;
				while (index < blocksToDeployCurrentTick && currentDeployIndex < blocksToDeployCount) {
					// Deploy single block
					JumpBlock jumpBlock = jumpShip.jumpBlocks[currentDeployIndex];
					
					if (jumpBlock == null) {
						if (WarpDriveConfig.LOGGING_BUILDING) {
							WarpDrive.logger.info("At index " + currentDeployIndex + ", skipping undefined block");
						}
					} else if (jumpBlock.block == Blocks.AIR) {
						if (WarpDriveConfig.LOGGING_BUILDING) {
							WarpDrive.logger.info("At index " + currentDeployIndex + ", skipping air block");
						}
					} else if (Dictionary.BLOCKS_ANCHOR.contains(jumpBlock.block)) {
						if (WarpDriveConfig.LOGGING_BUILDING) {
							WarpDrive.logger.info("At index " + currentDeployIndex + ", skipping anchor block " + jumpBlock.block);
						}
					} else {
						index++;
						if (WarpDriveConfig.LOGGING_BUILDING) {
							WarpDrive.logger.info("At index " + currentDeployIndex + ", deploying block " + Block.REGISTRY.getNameForObject(jumpBlock.block) + ":" + jumpBlock.blockMeta
								+ " tileEntity " + jumpBlock.blockTileEntity + " NBT " + jumpBlock.blockNBT);
						}
						BlockPos targetLocation = transformation.apply(jumpBlock.x, jumpBlock.y, jumpBlock.z);
						Block blockAtTarget = worldObj.getBlockState(targetLocation).getBlock();
						if (blockAtTarget == Blocks.AIR || Dictionary.BLOCKS_EXPANDABLE.contains(blockAtTarget)) {
							jumpBlock.deploy(worldObj, transformation);
							
							if (index % periodLaserEffect == 0) {
								worldObj.playSound(null, pos, SoundEvents.LASER_LOW, SoundCategory.HOSTILE, 0.5F, 1.0F);
								
								PacketHandler.sendBeamPacket(worldObj,
										new Vector3(this).translate(0.5D),
										new Vector3(targetLocation).translate(0.5D),
										0f, 1f, 0f, 15, 0, 100);
							}
							worldObj.playSound(null, targetLocation, jumpBlock.block.getSoundType().getPlaceSound(), SoundCategory.BLOCKS,
								(jumpBlock.block.getSoundType().getVolume() + 1.0F) / 2.0F, jumpBlock.block.getSoundType().getPitch() * 0.8F);
							
						} else {
							if (WarpDriveConfig.LOGGING_BUILDING) {
								WarpDrive.logger.info("Target position is occupied, skipping");
							}
							worldObj.newExplosion(null, targetX + jumpBlock.x, targetY + jumpBlock.y, targetZ + jumpBlock.z, 3, false, false);
							WarpDrive.logger.info("Deployment collision detected at " + (targetX + jumpBlock.x) + " " + (targetY + jumpBlock.y) + " " + (targetZ + jumpBlock.z));
						}
					}
					
					currentDeployIndex++;
				}
				
				// deployment done, teleport owner inside
				if (currentDeployIndex >= blocksToDeployCount) {
					TileEntity tileEntity = worldObj.getTileEntity(new BlockPos(targetX, targetY, targetZ));
					if (tileEntity instanceof TileEntityShipCore) {
						((TileEntityShipCore)tileEntity).summonOwnerOnDeploy(playerName);
					}
					isDeploying = false;
					setActive(false); // disable scanner
				}
			}
		}
	}
	
	private void setActive(boolean newState) {
		isActive = newState;
		if ((getBlockMetadata() == 1) == newState) {
			updateMetadata(isActive ? 1 : 0);
		}
	}
	
	private TileEntityShipCore searchShipCore() {
		StringBuilder reason = new StringBuilder();
		TileEntityShipCore result = null;
		
		// Search for ship cores above
		for (int newY = pos.getY() + 1; newY <= 255; newY++) {
			BlockPos blockPos = new BlockPos(pos.getX(), newY, pos.getZ());
			if (worldObj.getBlockState(blockPos).getBlock().isAssociatedBlock(WarpDrive.blockShipCore)) { // found ship core above
				result = (TileEntityShipCore) worldObj.getTileEntity(blockPos);
				
				if (result != null) {
					if (!result.validateShipSpatialParameters(reason)) { // If we can't refresh ship's spatial parameters
						result = null;
					}
				}
				
				break;
			}
		}
		
		return result;
	}
	
	private int getScanningEnergyCost(int size) {
		if (WarpDriveConfig.SS_ENERGY_PER_BLOCK_SCAN >= 0) {
			return size * WarpDriveConfig.SS_ENERGY_PER_BLOCK_SCAN;
		} else {
			return WarpDriveConfig.SS_MAX_ENERGY_STORED;
		}
	}
	
	private int getDeploymentEnergyCost(int size) {
		if (WarpDriveConfig.SS_ENERGY_PER_BLOCK_DEPLOY >= 0) {
			return size * WarpDriveConfig.SS_ENERGY_PER_BLOCK_DEPLOY;
		} else {
			return WarpDriveConfig.SS_MAX_ENERGY_STORED;
		}
	}
	
	private boolean saveShipToSchematic(String fileName, StringBuilder reason) {
		if (!shipCore.validateShipSpatialParameters(reason)) {
			return false;
		}
		short width = (short) (shipCore.maxX - shipCore.minX + 1);
		short length = (short) (shipCore.maxZ - shipCore.minZ + 1);
		short height = (short) (shipCore.maxY - shipCore.minY + 1);
		int size = width * length * height;
		
		if (width <= 0 || length <= 0 || height <= 0) {
			reason.append("Invalid ship dimensions, nothing to scan");
			return false;
		}
		
		// Consume energy
		int energyCost = getScanningEnergyCost(shipCore.shipMass);
		if (!consumeEnergy(energyCost, false)) {
			reason.append(String.format("Insufficient energy (%d required)", energyCost));
			return false;
		}
		
		// Save header
		NBTTagCompound schematic = new NBTTagCompound();
		
		schematic.setShort("Width", width);
		schematic.setShort("Length", length);
		schematic.setShort("Height", height);
		schematic.setInteger("shipMass", shipCore.shipMass);
		schematic.setString("shipName", shipCore.shipName);
		schematic.setInteger("shipVolume", shipCore.shipVolume);
		
		// Save new format
		JumpShip ship = new JumpShip();
		ship.worldObj = shipCore.getWorld();
		ship.core = shipCore.getPos();
		ship.dx = shipCore.dx;
		ship.dz = shipCore.dz;
		ship.minX = shipCore.minX;
		ship.maxX = shipCore.maxX;
		ship.minY = shipCore.minY;
		ship.maxY = shipCore.maxY;
		ship.minZ = shipCore.minZ;
		ship.maxZ = shipCore.maxZ;
		ship.shipCore = shipCore;
		if (!ship.save(reason)) {
			return false;
		}
		NBTTagCompound tagCompoundShip = new NBTTagCompound();
		ship.writeToNBT(tagCompoundShip);
		schematic.setTag("ship", tagCompoundShip);
		
		// Storage collections
		String stringBlockRegistryNames[] = new String[size];
		byte byteMetadatas[] = new byte[size];
		NBTTagList tileEntitiesList = new NBTTagList();
		
		// Scan the whole area
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					BlockPos blockPos = new BlockPos(shipCore.minX + x, shipCore.minY + y, shipCore.minZ + z);
					IBlockState blockState = worldObj.getBlockState(blockPos);
					
					// Skip leftBehind and anchor blocks
					if (Dictionary.BLOCKS_LEFTBEHIND.contains(blockState.getBlock()) || Dictionary.BLOCKS_ANCHOR.contains(blockState.getBlock())) {
						blockState = Blocks.AIR.getDefaultState();
					}
					
					int index = x + (y * length + z) * width;
					stringBlockRegistryNames[index] = Block.REGISTRY.getNameForObject(blockState.getBlock()).toString();
					byteMetadatas[index] = (byte) blockState.getBlock().getMetaFromState(blockState);
					
					if (!blockState.getBlock().isAssociatedBlock(Blocks.AIR)) {
						TileEntity tileEntity = worldObj.getTileEntity(blockPos);
						if (tileEntity != null) {
							try {
								NBTTagCompound tagTileEntity = new NBTTagCompound();
								tileEntity.writeToNBT(tagTileEntity);
								
								// Clear computer IDs
								if (tagTileEntity.hasKey("computerID")) {
									tagTileEntity.removeTag("computerID");
								}
								if (tagTileEntity.hasKey("oc:computer")) {
									NBTTagCompound tagComputer = tagTileEntity.getCompoundTag("oc:computer");
									tagComputer.removeTag("components");
									tagComputer.removeTag("node");
									tagTileEntity.setTag("oc:computer", tagComputer);
								}
								/*
								// Clear inventory.
								if (tileEntity instanceof IInventory) {
									TileEntity tileEntityClone = TileEntity.createAndLoadEntity(tagTileEntity);
									if (tileEntityClone instanceof IInventory) {
										for (int i = 0; i < ((IInventory) tileEntityClone).getSizeInventory(); i++) {
											((IInventory) tileEntityClone).setInventorySlotContents(i, null);
										}
									}
									tileEntityClone.writeToNBT(tagTileEntity);
								}
								
								// Empty energy storage
								// IC2
								if (tagTileEntity.hasKey("energy")) {
									consumeEnergy((int)Math.round(tagTileEntity.getDouble("energy")), true);
									tagTileEntity.setDouble("energy", 0);
								}
								// Gregtech
								if (tagTileEntity.hasKey("mStoredEnergy")) {
									tagTileEntity.setInteger("mStoredEnergy", 0);
								}
								// Immersive Engineering & Thermal Expansion
								if (tagTileEntity.hasKey("Energy")) {
									consumeEnergy(tagTileEntity.getInteger("Energy"), true);
									tagTileEntity.setInteger("Energy", 0);
								}
								if (tagTileEntity.hasKey("Owner")) {
									tagTileEntity.setString("Owner", "None");
								}
								// Mekanism
								if (tagTileEntity.hasKey("electricityStored")) {
									tagTileEntity.setDouble("electricityStored", 0);
								}
								if (tagTileEntity.hasKey("owner")) {
									tagTileEntity.setString("owner", "None");
								}
								/**/
								
								// Transform TE's coordinates from local axis to .schematic offset-axis
								// Warning: this is a cheap workaround for World Edit. Use the native format for proper transformation
								tagTileEntity.setInteger("x", tileEntity.getPos().getX() - shipCore.minX);
								tagTileEntity.setInteger("y", tileEntity.getPos().getY() - shipCore.minY);
								tagTileEntity.setInteger("z", tileEntity.getPos().getZ() - shipCore.minZ);
								
								tileEntitiesList.appendTag(tagTileEntity);
							} catch (Exception exception) {
								exception.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		schematic.setString("Materials", "Alpha");
		NBTTagList tagListBlocks = new NBTTagList();
		for (String stringRegistryName : stringBlockRegistryNames) {
			tagListBlocks.appendTag(new NBTTagString(stringRegistryName));
		}
		schematic.setTag("Blocks", tagListBlocks);
		schematic.setByteArray("Data", byteMetadatas);
		
		schematic.setTag("Entities", new NBTTagList()); // don't save entities
		schematic.setTag("TileEntities", tileEntitiesList);
		
		writeNBTToFile(fileName, schematic);
		
		return true;
	}
	
	private void writeNBTToFile(String fileName, NBTTagCompound nbttagcompound) {
		WarpDrive.logger.info(this + " writeNBTToFile " + fileName);
		
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				//noinspection ResultOfMethodCallIgnored
				file.createNewFile();
			}
			
			FileOutputStream fileoutputstream = new FileOutputStream(file);
			
			CompressedStreamTools.writeCompressed(nbttagcompound, fileoutputstream);
			
			fileoutputstream.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	// Begins ship scan
	private boolean scanShip(StringBuilder reason) {
		// Enable scanner
		setActive(true);
		File file = new File(WarpDriveConfig.G_SCHEMALOCATION);
		if (!file.exists() || !file.isDirectory()) {
			if (!file.mkdirs()) {
				return false;
			}
		}
		
		// Generate unique file name
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH'h'mm'm'ss's'SSS");
		String shipName = shipCore.shipName.replaceAll("[^ -~]", "").replaceAll("[:/\\\\]", "");
		do {
			Date now = new Date();
			schematicFileName = shipName + "_" + sdfDate.format(now);
		} while (new File(WarpDriveConfig.G_SCHEMALOCATION + "/" + schematicFileName + ".schematic").exists());
		
		if (!saveShipToSchematic(WarpDriveConfig.G_SCHEMALOCATION + "/" + schematicFileName + ".schematic", reason)) {
			return false;
		}
		reason.append(schematicFileName);
		return true;
	}
	
	private static NBTTagCompound readNBTFromFile(String fileName) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				return null;
			}
			
			FileInputStream fileinputstream = new FileInputStream(file);
			NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
			
			fileinputstream.close();
			
			return nbttagcompound;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return null;
	}
	
	// Returns error code and reason string
	private int deployShip(final String fileName, final int offsetX, final int offsetY, final int offsetZ, final byte rotationSteps, final boolean isForced, final StringBuilder reason) {
		// Load schematic
		NBTTagCompound schematic = readNBTFromFile(WarpDriveConfig.G_SCHEMALOCATION + "/" + fileName + ".schematic");
		if (schematic == null) {
			reason.append(String.format("Schematic not found or unknown error reading it: '%s'.", fileName));
			return -1;
		}
		
		jumpShip = new JumpShip();
		targetX = pos.getX() + offsetX;
		targetY = pos.getY() + offsetY;
		targetZ = pos.getZ() + offsetZ;
		this.rotationSteps = rotationSteps;
		
		// Compute geometry
		// int shipMass = schematic.getInteger("shipMass");
		// String shipName = schematic.getString("shipName");
		// int shipVolume = schematic.getInteger("shipVolume");
		if (schematic.hasKey("ship")) {
			jumpShip.readFromNBT(schematic.getCompoundTag("ship"));
			blocksToDeployCount = jumpShip.jumpBlocks.length;
			if (WarpDriveConfig.LOGGING_BUILDING) {
				WarpDrive.logger.info(String.format("[ShipScanner] Loaded %d blocks to deploy", blocksToDeployCount));
			}
			
		} else {
			// Set deployment variables
			short width = schematic.getShort("Width");
			short height = schematic.getShort("Height");
			short length = schematic.getShort("Length");
			jumpShip.minX = 0;
			jumpShip.maxX = width - 1;
			jumpShip.minY = 0;
			jumpShip.maxY = height - 1;
			jumpShip.minZ = 0;
			jumpShip.maxZ = length - 1;
			jumpShip.core = new BlockPos(0, -1, 0);
			blocksToDeployCount = width * height * length;
			jumpShip.jumpBlocks = new JumpBlock[blocksToDeployCount];
			
			// Read blocks and TileEntities from NBT to internal storage array
			NBTTagList localBlocks = (NBTTagList) schematic.getTag("Blocks");
			byte localMetadata[] = schematic.getByteArray("Data");
			
			// Load Tile Entities
			NBTTagCompound[] tileEntities = new NBTTagCompound[blocksToDeployCount];
			NBTTagList tagListTileEntities = schematic.getTagList("TileEntities", Constants.NBT.TAG_COMPOUND);
			
			for (int i = 0; i < tagListTileEntities.tagCount(); i++) {
				NBTTagCompound tagTileEntity = tagListTileEntities.getCompoundTagAt(i);
				int teX = tagTileEntity.getInteger("x");
				int teY = tagTileEntity.getInteger("y");
				int teZ = tagTileEntity.getInteger("z");
				
				tileEntities[teX + (teY * length + teZ) * width] = tagTileEntity;
			}
			
			// Create list of blocks to deploy
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < length; z++) {
						int index = x + (y * length + z) * width;
						JumpBlock jumpBlock = new JumpBlock();
						
						jumpBlock.x = x;
						jumpBlock.y = y;
						jumpBlock.z = z;
						jumpBlock.block = Block.getBlockFromName(localBlocks.getStringTagAt(index));
						jumpBlock.blockMeta = (localMetadata[index]) & 0xFF;
						jumpBlock.blockNBT = tileEntities[index];
						
						if (jumpBlock.block != null) {
							if (WarpDriveConfig.LOGGING_BUILDING) {
								if (tileEntities[index] == null) {
									WarpDrive.logger.info("[ShipScanner] Adding block to deploy: "
										                      + jumpBlock.block.getUnlocalizedName() + ":" + jumpBlock.blockMeta
										                      + " (no tile entity)");
								} else {
									WarpDrive.logger.info("[ShipScanner] Adding block to deploy: "
										                      + jumpBlock.block.getUnlocalizedName() + ":" + jumpBlock.blockMeta
										                      + " with tile entity " + tileEntities[index].getString("id"));
								}
							}
						} else {
							jumpBlock = null;
						}
						jumpShip.jumpBlocks[index] = jumpBlock;
					}
				}
			}
		}
		
		// Validate context
		{
			// Check distance
			double dX = pos.getX() - targetX;
			double dY = pos.getY() - targetY;
			double dZ = pos.getZ() - targetZ;
			double distance = MathHelper.sqrt_double(dX * dX + dY * dY + dZ * dZ);
			
			if (distance > WarpDriveConfig.SS_MAX_DEPLOY_RADIUS_BLOCKS) {
				reason.append(String.format("Cannot deploy ship more than %d blocks away from scanner.", WarpDriveConfig.SS_MAX_DEPLOY_RADIUS_BLOCKS));
				return 5;
			}
			
			// Consume energy
			int energyCost = getDeploymentEnergyCost(blocksToDeployCount);
			if (!consumeEnergy(energyCost, false)) {
				reason.append(String.format("Insufficient energy (%d required)", energyCost));
				return 1;
			}
			
			// Compute target area
			Transformation transformation = new Transformation(jumpShip, worldObj, targetX - jumpShip.core.getX(), targetY - jumpShip.core.getY(), targetZ - jumpShip.core.getZ(), rotationSteps);
			BlockPos targetLocation1 = transformation.apply(jumpShip.minX, jumpShip.minY, jumpShip.minZ);
			BlockPos targetLocation2 = transformation.apply(jumpShip.maxX, jumpShip.maxY, jumpShip.maxZ);
			BlockPos targetLocationMin = new BlockPos(
			                Math.min(targetLocation1.getX(), targetLocation2.getX()) - 1,
			    Math.max(0, Math.min(targetLocation1.getY(), targetLocation2.getY()) - 1),
			                Math.min(targetLocation1.getZ(), targetLocation2.getZ()) - 1);
			BlockPos targetLocationMax = new BlockPos(
			                  Math.max(targetLocation1.getX(), targetLocation2.getX()) + 1,
			    Math.min(255, Math.max(targetLocation1.getY(), targetLocation2.getY()) + 1),
			                  Math.max(targetLocation1.getZ(), targetLocation2.getZ()) + 1);
			
			if (isForced) {
				if (!worldObj.isAirBlock(new BlockPos(targetX, targetY, targetZ))) {
					worldObj.newExplosion(null, targetX, targetY, targetZ, 1, false, false);
					if (WarpDriveConfig.LOGGING_BUILDING) {
						WarpDrive.logger.info("Deployment collision detected at " + targetX + " " + targetY + " " + targetZ);
					}
					reason.append(String.format("Deployment area occupied with existing ship. Can't deploy new ship at " + targetX + " " + targetY + " " + targetZ));
					return 2;
				}
				
				// Clear specified area for any blocks to avoid corruption and ensure clean full ship
				for (int x = targetLocationMin.getX(); x <= targetLocationMax.getX(); x++) {
					for (int y = targetLocationMin.getY(); y <= targetLocationMax.getY(); y++) {
						for (int z = targetLocationMin.getZ(); z <= targetLocationMax.getZ(); z++) {
							worldObj.setBlockToAir(new BlockPos(x, y, z));
						}
					}
				}
				
			} else {
				
				// Check specified area for occupation by blocks
				// If specified area is occupied, break deployment with error message
				int occupiedBlockCount = 0;
				for (int x = targetLocationMin.getX(); x <= targetLocationMax.getX(); x++) {
					for (int y = targetLocationMin.getY(); y <= targetLocationMax.getY(); y++) {
						for (int z = targetLocationMin.getZ(); z <= targetLocationMax.getZ(); z++) {
							if (!worldObj.isAirBlock(new BlockPos(x, y, z))) {
								occupiedBlockCount++;
								if (occupiedBlockCount == 1 || (occupiedBlockCount <= 100 && worldObj.rand.nextInt(10) == 0)) {
									worldObj.newExplosion(null, x, y, z, 1, false, false);
								}
								if (WarpDriveConfig.LOGGING_BUILDING) {
									WarpDrive.logger.info("Deployment collision detected at " + x + " " + y + " " + z);
								}
							}
						}
					}
				}
				if (occupiedBlockCount > 0) {
					reason.append(String.format("Deployment area occupied with %d blocks. Can't deploy ship.", occupiedBlockCount));
					return 2;
				}
			}
		}
		
		// initiate deployment sequencer
		isDeploying = true;
		currentDeployIndex = 0;
		
		setActive(true);
		reason.append(String.format("Deploying ship '%s'...", fileName));
		return 3;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		schematicFileName = tag.getString("schematic");
		targetX = tag.getInteger("targetX");
		targetY = tag.getInteger("targetY");
		targetZ = tag.getInteger("targetZ");
		rotationSteps = tag.getByte("rotationSteps");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setString("schematic", schematicFileName);
		tag.setInteger("targetX", targetX);
		tag.setInteger("targetY", targetY);
		tag.setInteger("targetZ", targetZ);
		tag.setByte("rotationSteps", rotationSteps);
		return tag;
	}
	
	// OpenComputer callback methods
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] scan(Context context, Arguments arguments) {
		return scan();
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] filename(Context context, Arguments arguments) {
		return filename();
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] deploy(Context context, Arguments arguments) {
		return deploy(argumentsOCtoCC(arguments));
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] state(Context context, Arguments arguments) {
		return state();
	}
	
	private Object[] scan() {
		// Already scanning?
		if (isActive) {
			return new Object[] { false, 0, "Already active" };
		}
		
		if (shipCore == null) {
			return new Object[] { false, 1, "Ship-Core not found" };
		}
		int energyCost = getScanningEnergyCost(shipCore.shipMass);
		if (!consumeEnergy(energyCost, true)) {
			return new Object[] { false, 2, "Not enough energy! " + energyCost + " required." };
		} else {
			StringBuilder reason = new StringBuilder();
			boolean success = scanShip(reason);
			return new Object[] { success, 3, reason.toString() };
		}
	}
	
	private Object[] filename() {
		if (isActive && !schematicFileName.isEmpty()) {
			if (isDeploying) {
				return new Object[] { false, "Deployment in progress. Please wait..." };
			} else {
				return new Object[] { false, "Scan in progress. Please wait..." };
			}
		}
		
		return new Object[] { true, schematicFileName };
	}
	
	private Object[] deploy(Object[] arguments) {
		if (arguments.length == 5) {
			String fileName = (String) arguments[0];
			int x = toInt(arguments[1]);
			int y = toInt(arguments[2]);
			int z = toInt(arguments[3]);
			byte rotationSteps = (byte) toInt(arguments[4]);
			
			if (!new File(WarpDriveConfig.G_SCHEMALOCATION + "/" + fileName + ".schematic").exists()) {
				return new Object[] { 0, "Specified schematic file was not found!" };
			} else {
				StringBuilder reason = new StringBuilder();
				int result = deployShip(fileName, x, y, z, rotationSteps, false, reason);
				return new Object[] { result, reason.toString() };
			}
		} else {
			return new Object[] { 4, "Invalid arguments count, you need .schematic file name, offsetX, offsetY, offsetZ, rotationSteps!" };
		}
	}
	
	private Object[] state() {
		if (!isActive) {
			return new Object[] { false, "IDLE", 0, 0 };
		} else if (!isDeploying) {
			return new Object[] { true, "Scanning", 0, 0 };
		} else {
			return new Object[] { true, "Deploying", currentDeployIndex, blocksToDeployCount };
		}
	}
	
	// ComputerCraft IPeripheral methods implementation
	@Override
	@Optional.Method(modid = "ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) {
		String methodName = getMethodName(method);
		
		switch (methodName) {
			case "scan":
				return scan();

			case "fileName":
				return filename();

			case "deploy": // deploy(schematicFileName, offsetX, offsetY, offsetZ)
				return deploy(arguments);

			case "state":
				return state();
		}
		
		return super.callMethod(computer, context, method, arguments);
	}
	
	private static final int SS_SEARCH_INTERVAL_TICKS = 20;
	private int cooldownPlayerDetection = 5;
	private void checkPlayerToken() {
		// cooldown to prevent player chat spam and server lag
		cooldownPlayerDetection--;
		if (cooldownPlayerDetection > 0) {
			return;
		}
		cooldownPlayerDetection = SS_SEARCH_INTERVAL_TICKS;
		
		// skip unless setup is done
		if (targetX == 0 && targetY == 0 && targetZ == 0) {
			return;
		}
		
		// find a unique player in range
		AxisAlignedBB axisalignedbb = new AxisAlignedBB(pos.getX() - 1.0D, pos.getY() + 1.0D, pos.getZ() - 1.0D, pos.getX() + 1.99D, pos.getY() + 5.0D, pos.getZ() + 1.99D);
		List list = worldObj.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb);
		List<EntityPlayer> entityPlayers = new ArrayList<>(10);
		for (Object object : list) {
			if (object instanceof EntityPlayer) {
				entityPlayers.add((EntityPlayer) object);
			}
		}
		if (entityPlayers.isEmpty()) {
			return;
		}
		if (entityPlayers.size() > 1) {
			for (EntityPlayer entityPlayer : entityPlayers) {
				WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("Too many players detected: please stand in the beam one at a time."));
				cooldownPlayerDetection = 3 * SS_SEARCH_INTERVAL_TICKS;
			}
			return;
		}
		EntityPlayer entityPlayer = entityPlayers.get(0);
		
		// check inventory
		int slotIndex = 0;
		ItemStack itemStack = null;
		for (; slotIndex < entityPlayer.inventory.getSizeInventory(); slotIndex++) {
			itemStack = entityPlayer.inventory.getStackInSlot(slotIndex);
			if ( itemStack != null
			  && itemStack.getItem() == WarpDrive.itemCrystalToken
			  && itemStack.stackSize >= 1) {
				break;
			}
		}
		if (itemStack == null || slotIndex >= entityPlayer.inventory.getSizeInventory()) {
			WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("Please come back once you've a Crystal token."));
			cooldownPlayerDetection = 3 * SS_SEARCH_INTERVAL_TICKS;
			return;
		}
		playerName = entityPlayer.getName();
		
		// try deploying
		StringBuilder reason = new StringBuilder();
		deployShip(ItemCrystalToken.getSchematicName(itemStack), targetX - pos.getX(), targetY - pos.getY(), targetZ - pos.getZ(), rotationSteps, true, reason);
		WarpDrive.addChatMessage(entityPlayer, new TextComponentString(reason.toString()));
		if (!isActive) {
			// failed
			cooldownPlayerDetection = 5 * SS_SEARCH_INTERVAL_TICKS;
			return;
		}
		
		// success => remove token
		if (!entityPlayer.capabilities.isCreativeMode) {
			itemStack.stackSize--;
			if (itemStack.stackSize > 0) {
				entityPlayer.inventory.setInventorySlotContents(slotIndex, itemStack);
			} else {
				entityPlayer.inventory.setInventorySlotContents(slotIndex, null);
			}
			entityPlayer.inventory.markDirty();
		}
	}
	
	// IEnergySink methods implementation
	@Override
	public int getMaxEnergyStored() {
		return WarpDriveConfig.SS_MAX_ENERGY_STORED;
	}
	
	@Override
	public boolean canInputEnergy(EnumFacing from) {
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("%s @ \'%s\' (%d %d %d)", getClass().getSimpleName(),
			worldObj == null ? "~NULL~" : worldObj.getWorldInfo().getWorldName(), pos.getX(), pos.getY(), pos.getZ());
	}
}
