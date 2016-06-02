package cr0s.warpdrive.block.building;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import cr0s.warpdrive.data.JumpShip;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.Optional;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.TileEntityAbstractEnergy;
import cr0s.warpdrive.block.movement.TileEntityShipCore;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.JumpBlock;
import cr0s.warpdrive.data.Transformation;
import cr0s.warpdrive.data.Vector3;
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
	
	private String schematicFileName;
	
	private JumpShip jumpShip;
	private int currentDeployIndex;
	private int blocksToDeployCount;
	private boolean isDeploying = false;
	
	private int targetX, targetY, targetZ;
	
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
	public void updateEntity() {
		super.updateEntity();
		
		if (worldObj.isRemote) {
			return;
		}
		
		searchTicks++;
		if (searchTicks > WarpDriveConfig.SS_SEARCH_INTERVAL_TICKS) {
			searchTicks = 0;
			shipCore = searchShipCore();
		}
		
		// Ship core is not found
		if (!isDeploying && shipCore == null) {
			setActive(false); // disable scanner
			laserTicks++;
			if (laserTicks > 20) {
				PacketHandler.sendBeamPacket(worldObj,
					new Vector3(this).translate(0.5D),
					new Vector3(xCoord, 255, zCoord).translate(0.5D), 
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
					new Vector3(shipCore.xCoord, shipCore.yCoord, shipCore.zCoord).translate(0.5D),
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
					
					worldObj.playSoundEffect(xCoord + 0.5f, yCoord, zCoord + 0.5f, "warpdrive:lowlaser", 4F, 1F);
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
			if (deployDelayTicks > 20) {
				deployDelayTicks = 0;
				
				int blocks = Math.min(WarpDriveConfig.SS_DEPLOY_BLOCKS_PER_SECOND, blocksToDeployCount - currentDeployIndex);
				
				if (blocks == 0) {
					isDeploying = false;
					setActive(false); // disable scanner
					if (WarpDriveConfig.LOGGING_BUILDING) {
						WarpDrive.logger.info(this + " Deployment done");
					}
					return;
				}
				
				if (WarpDriveConfig.LOGGING_BUILDING) {
					WarpDrive.logger.info(this + " Deploying " + blocks + " more blocks");
				}
				for (int index = 0; index < blocks; index++) {
					if (currentDeployIndex >= blocksToDeployCount) {
						isDeploying = false;
						setActive(false); // disable scanner
						break;
					}
					
					// Deploy single block
					JumpBlock jumpBlock = jumpShip.jumpBlocks[currentDeployIndex];
					
					if (jumpBlock == null) {
						if (WarpDriveConfig.LOGGING_BUILDING) {
							WarpDrive.logger.info("At index " + currentDeployIndex + ", skipping undefined block");
						}
					} else if (Dictionary.BLOCKS_ANCHOR.contains(jumpBlock.block)) {
						if (WarpDriveConfig.LOGGING_BUILDING) {
							WarpDrive.logger.info("At index " + currentDeployIndex + ", skipping anchor block " + jumpBlock.block);
						}
					} else {
						if (WarpDriveConfig.LOGGING_BUILDING) {
							WarpDrive.logger.info("At index " + currentDeployIndex + ", deploying block " + jumpBlock.block + ":" + jumpBlock.blockMeta
								+ " tileEntity " + jumpBlock.blockTileEntity + " NBT " + jumpBlock.blockNBT);
						}
						Block blockAtTarget = worldObj.getBlock(targetX + jumpBlock.x, targetY + jumpBlock.y, targetZ + jumpBlock.z);
						if (blockAtTarget == Blocks.air || Dictionary.BLOCKS_EXPANDABLE.contains(blockAtTarget)) {
							Transformation transformation = new Transformation(jumpShip, worldObj, targetX, targetY, targetZ, (byte) 0);
							jumpBlock.deploy(worldObj, transformation);
							
							if (worldObj.rand.nextInt(100) <= 1000) {
								worldObj.playSoundEffect(xCoord + 0.5f, yCoord, zCoord + 0.5f, "warpdrive:lowlaser", 4F, 1F);
								
								PacketHandler.sendBeamPacket(worldObj,
										new Vector3(this).translate(0.5D),
										new Vector3(targetX + jumpBlock.x, targetY + jumpBlock.y, targetZ + jumpBlock.z).translate(0.5D),
										0f, 1f, 0f, 15, 0, 100);
							}
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
			}
		}
	}
	
	private void setActive(boolean newState) {
		isActive = newState;
		if ((getBlockMetadata() == 1) == newState) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, isActive ? 1 : 0, 2);
		}
	}
	
	private TileEntityShipCore searchShipCore() {
		StringBuilder reason = new StringBuilder();
		TileEntityShipCore result = null;
		
		// Search for ship cores above
		for (int newY = yCoord + 1; newY <= 255; newY++) {
			if (worldObj.getBlock(xCoord, newY, zCoord).isAssociatedBlock(WarpDrive.blockShipCore)) { // found ship core above
				result = (TileEntityShipCore) worldObj.getTileEntity(xCoord, newY, zCoord);
				
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
		if (WarpDriveConfig.SS_ENERGY_PER_BLOCK_SCAN > 0) {
			return size * WarpDriveConfig.SS_ENERGY_PER_BLOCK_SCAN;
		} else {
			return WarpDriveConfig.SS_MAX_ENERGY_STORED;
		}
	}
	
	private int getDeploymentEnergyCost(int size) {
		if (WarpDriveConfig.SS_ENERGY_PER_BLOCK_DEPLOY > 0) {
			return size * WarpDriveConfig.SS_ENERGY_PER_BLOCK_DEPLOY;
		} else {
			return WarpDriveConfig.SS_MAX_ENERGY_STORED;
		}
	}
	
	private boolean saveShipToSchematic(String fileName, StringBuilder reason) {
		NBTTagCompound schematic = new NBTTagCompound();
		
		short width = (short) (shipCore.maxX - shipCore.minX + 1);
		short length = (short) (shipCore.maxZ - shipCore.minZ + 1);
		short height = (short) (shipCore.maxY - shipCore.minY + 1);
		
		if (width <= 0 || length <= 0 || height <= 0) {
			reason.append("Invalid ship dimensions, nothing to scan");
			return false;
		}
		
		schematic.setShort("Width", width);
		schematic.setShort("Length", length);
		schematic.setShort("Height", height);
		
		int size = width * length * height;
		
		// Consume energy
		if (!consumeEnergy(getScanningEnergyCost(size), false)) {
			reason.append("Insufficient energy (" + getScanningEnergyCost(size) + " required)");
			return false;
		}
		
		String stringBlockRegistryNames[] = new String[size];
		byte byteMetadatas[] = new byte[size];
		
		NBTTagList tileEntitiesList = new NBTTagList();
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					Block block = worldObj.getBlock(shipCore.minX + x, shipCore.minY + y, shipCore.minZ + z);
					
					// Skip leftBehind and anchor blocks
					if (Dictionary.BLOCKS_LEFTBEHIND.contains(block) || Dictionary.BLOCKS_ANCHOR.contains(block)) {
						block = Blocks.air;
					}
					
					int index = x + (y * length + z) * width;
					stringBlockRegistryNames[index] = Block.blockRegistry.getNameForObject(block);
					byteMetadatas[index] = (byte) worldObj.getBlockMetadata(shipCore.minX + x, shipCore.minY + y, shipCore.minZ + z);
					
					if (!block.isAssociatedBlock(Blocks.air)) {
						TileEntity tileEntity = worldObj.getTileEntity(shipCore.minX + x, shipCore.minY + y, shipCore.minZ + z);
						if (tileEntity != null) {
							try {
								NBTTagCompound tagTileEntity = new NBTTagCompound();
								tileEntity.writeToNBT(tagTileEntity);
								
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
									tagTileEntity.setInteger("energy", 0);
								}
								// Gregtech
								if (tagTileEntity.hasKey("mStoredEnergy")) {
									tagTileEntity.setInteger("mStoredEnergy", 0);
								}
								
								// Transform TE's coordinates from local axis to .schematic offset-axis
								// FIXME: transform all data, not just coordinates
								tagTileEntity.setInteger("x", tileEntity.xCoord - shipCore.minX);
								tagTileEntity.setInteger("y", tileEntity.yCoord - shipCore.minY);
								tagTileEntity.setInteger("z", tileEntity.zCoord - shipCore.minZ);
								
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
		File f = new File(WarpDriveConfig.G_SCHEMALOCATION);
		if (!f.exists() || !f.isDirectory()) {
			f.mkdirs();
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
	private int deployShip(String fileName, int offsetX, int offsetY, int offsetZ, StringBuilder reason) {
		// Load schematic
		NBTTagCompound schematic = readNBTFromFile(WarpDriveConfig.G_SCHEMALOCATION + "/" + fileName + ".schematic");
		if (schematic == null) {
			reason.append("Schematic not found or unknown error reading it.");
			return -1;
		}
		jumpShip = new JumpShip();
		
		// Compute geometry
		short width = schematic.getShort("Width");
		short height = schematic.getShort("Height");
		short length = schematic.getShort("Length");
		jumpShip.minX = 0;
		jumpShip.maxX = width - 1;
		jumpShip.minY = 0;
		jumpShip.maxY = height - 1;
		jumpShip.minZ = 0;
		jumpShip.maxZ = length - 1;
		
		targetX = xCoord + offsetX;
		targetY = yCoord + offsetY;
		targetZ = zCoord + offsetZ;
		blocksToDeployCount = width * height * length;
		
		// Validate context
		{
			// Check distance
			double dX = xCoord - targetX;
			double dY = yCoord - targetY;
			double dZ = zCoord - targetZ;
			double distance = MathHelper.sqrt_double(dX * dX + dY * dY + dZ * dZ);
			
			if (distance > WarpDriveConfig.SS_MAX_DEPLOY_RADIUS_BLOCKS) {
				reason.append("Cannot deploy ship more than " + WarpDriveConfig.SS_MAX_DEPLOY_RADIUS_BLOCKS + " blocks away from scanner.");
				return 5;
			}
			
			// Consume energy
			if (!consumeEnergy(getDeploymentEnergyCost(blocksToDeployCount), false)) {
				reason.append("Insufficient energy (" + getDeploymentEnergyCost(blocksToDeployCount) + " required)");
				return 1;
			}
			
			// Check specified area for occupation by blocks
			// If specified area occupied, break deploying with error message
			int occupiedBlockCount = 0;
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < length; z++) {
						if (!worldObj.isAirBlock(targetX + x, targetY + y, targetZ + z)) {
							occupiedBlockCount++;
							worldObj.newExplosion(null, targetX + x, targetY + y, targetZ + z, 3, false, false);
							WarpDrive.logger.info("Deployment collision detected at " + (targetX + x) + " " + (targetY + y) + " " + (targetZ + z));
						}
					}
				}
			}
			if (occupiedBlockCount > 0) {
				reason.append("Deploying area occupied with " + occupiedBlockCount + " blocks. Can't deploy ship.");
				return 2;
			}
		}
		
		// Set deployment variables
		jumpShip.jumpBlocks = new JumpBlock[blocksToDeployCount];
		isDeploying = true;
		currentDeployIndex = 0;
		
		// Read blocks and TileEntities from NBT to internal storage array
		NBTTagList localBlocks = (NBTTagList) schematic.getTag("Blocks");
		byte localMetadata[] = schematic.getByteArray("Data");
		
		// Load Tile Entities
		NBTTagCompound[] tileEntities = new NBTTagCompound[blocksToDeployCount];
		NBTTagList tagListTileEntities = schematic.getTagList("TileEntities", new NBTTagByteArray(new byte[0]).getId()); //TODO: 0 is not correct
		
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
		
		setActive(true);
		reason.append("Ship deploying...");
		return 3;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
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
		} else if (!consumeEnergy(getScanningEnergyCost(shipCore.shipMass), true)) {
			return new Object[] { false, 2, "Not enough energy!" };
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
		if (arguments.length == 4) {
			String fileName = (String) arguments[0];
			int x = toInt(arguments[1]);
			int y = toInt(arguments[2]);
			int z = toInt(arguments[3]);
			
			if (!new File(WarpDriveConfig.G_SCHEMALOCATION + "/" + fileName + ".schematic").exists()) {
				return new Object[] { 0, "Specified schematic file was not found!" };
			} else {
				StringBuilder reason = new StringBuilder();
				int result = deployShip(fileName, x, y, z, reason);
				return new Object[] { result, reason.toString() };
			}
		} else {
			return new Object[] { 4, "Invalid arguments count, you need .schematic file name, offsetX, offsetY and offsetZ!" };
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
	
	// IEnergySink methods implementation
	@Override
	public int getMaxEnergyStored() {
		return WarpDriveConfig.SS_MAX_ENERGY_STORED;
	}
	
	@Override
	public boolean canInputEnergy(ForgeDirection from) {
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("%s @ \'%s\' (%d %d %d)", getClass().getSimpleName(),
			worldObj == null ? "~NULL~" : worldObj.getWorldInfo().getWorldName(), xCoord, yCoord, zCoord);
	}
}
