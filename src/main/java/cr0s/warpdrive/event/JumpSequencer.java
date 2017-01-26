package cr0s.warpdrive.event;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import cr0s.warpdrive.LocalProfiler;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.block.movement.TileEntityShipCore;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.JumpBlock;
import cr0s.warpdrive.data.JumpShip;
import cr0s.warpdrive.data.MovingEntity;
import cr0s.warpdrive.data.Planet;
import cr0s.warpdrive.data.Transformation;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.data.VectorI;
import cr0s.warpdrive.world.SpaceTeleporter;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class JumpSequencer extends AbstractSequencer {
	// Jump vector
	private Transformation transformation;
	
	private int moveX, moveY, moveZ;
	private final byte rotationSteps;
	private final boolean isHyperspaceJump;
	
	private final World sourceWorld;
	private World targetWorld;
	private Ticket sourceWorldTicket;
	private Ticket targetWorldTicket;
	
	private boolean collisionDetected = false;
	private ArrayList<Vector3> collisionAtSource;
	private ArrayList<Vector3> collisionAtTarget;
	private float collisionStrength = 0;
	
	private boolean isEnabled = false;
	private final static int STATE_IDLE = 0;
	private final static int STATE_CHUNKLOADING = 1;
	private final static int STATE_SAVING = 2;
	private final static int STATE_BORDERS = 3;
	private final static int STATE_TRANSFORMER = 4;
	private final static int STATE_BLOCKS = 5;
	private final static int STATE_EXTERNALS = 6;
	private final static int STATE_ENTITIES = 7;
	private final static int STATE_REMOVING = 8;
	private final static int STATE_CHUNKUNLOADING = 9;
	private final static int STATE_FINISHING = 10;
	private int state = STATE_IDLE;
	private int actualIndexInShip = 0;
	
	private final JumpShip ship;
	private boolean betweenWorlds;
	
	private final int destX;
	private final int destY;
	private final int destZ;
	private final boolean isCoordJump;
	
	private long msCounter = 0;
	private int ticks = 0;
	
	public JumpSequencer(TileEntityShipCore shipCore, boolean isHyperspaceJump,
			final int moveX, final int moveY, final int moveZ, final byte rotationSteps,
			boolean isCoordJump, int destX, int destY, int destZ) {
		this.sourceWorld = shipCore.getWorld();
		this.ship = new JumpShip();
		this.ship.worldObj = sourceWorld;
		this.ship.core = shipCore.getPos();
		this.ship.dx = shipCore.dx;
		this.ship.dz = shipCore.dz;
		this.ship.minX = shipCore.minX;
		this.ship.maxX = shipCore.maxX;
		this.ship.minY = shipCore.minY;
		this.ship.maxY = shipCore.maxY;
		this.ship.minZ = shipCore.minZ;
		this.ship.maxZ = shipCore.maxZ;
		this.ship.shipCore = shipCore;
		this.isHyperspaceJump = isHyperspaceJump;
		this.moveX = moveX;
		this.moveY = moveY;
		this.moveZ = moveZ;
		this.rotationSteps = rotationSteps;
		this.isCoordJump = isCoordJump;
		this.destX = destX;
		this.destY = destY;
		this.destZ = destZ;
		
		// set when preparing jump
		targetWorld = null;
		
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Sequencer created");
		}
	}
	
	public void enable() {
		isEnabled = true;
		register();
	}

	@Deprecated
	private void disableAndMessage(String message) {
		disableAndMessage(new TextComponentString(message));
	}
	private void disableAndMessage(ITextComponent textComponent) {
		disable(textComponent);
		ship.messageToAllPlayersOnShip(textComponent);
	}
	private void disable(ITextComponent textComponent) {
		if (!isEnabled) {
			return;
		}
		
		isEnabled = false;
		
		if (WarpDriveConfig.LOGGING_JUMP) {
			if (textComponent == null || textComponent.getFormattedText().isEmpty()) {
				WarpDrive.logger.info(this + " Killing jump sequencer...");
			} else {
				WarpDrive.logger.info(this + " Killing jump sequencer... (" + textComponent.getFormattedText() + ")");
			}
		}
		
		unforceChunks();
		unregister();
	}
	
	private static final boolean enforceEntitiesPosition = false;
	
	@Override
	public boolean onUpdate() {
		if (sourceWorld.isRemote) {
			return false;
		}
		
		if (!isEnabled) {
			if (WarpDriveConfig.LOGGING_JUMP) {
				WarpDrive.logger.info(this + " Removing from onUpdate...");
			}
			return false;
		}
		
		if (ship.minY < 0 || ship.maxY > 255) {
			disableAndMessage(new TextComponentString("Invalid Y coordinate(s), check ship dimensions..."));
			return true;
		}
		
		ticks++;
		switch (state) {
		case STATE_IDLE:
			// blank state in case we got desync
			msCounter = System.currentTimeMillis();
			if (isEnabled) {
				state = STATE_CHUNKLOADING;
			}
			break;
			
		case STATE_CHUNKLOADING:
			state_chunkLoading();
			if (isEnabled) {
				actualIndexInShip = 0;
				state = STATE_SAVING;
			}
			break;
			
		case STATE_SAVING:
			state_saving();
			if (isEnabled) {
				actualIndexInShip = 0;
				state = STATE_BORDERS;
			}
			break;
			
		case STATE_BORDERS:
			state_borders();
			if (isEnabled) {
				actualIndexInShip = 0;
				state = STATE_TRANSFORMER;
			}
			break;
			
		case STATE_TRANSFORMER:
			state_transformer();
			if (isEnabled) {
				actualIndexInShip = 0;
				state = STATE_BLOCKS;
			}
			break;
			
		case STATE_BLOCKS:
			state_moveBlocks();
			if (actualIndexInShip >= ship.jumpBlocks.length - 1) {
				actualIndexInShip = 0;
				state = STATE_EXTERNALS;
			}
			break;
			
		case STATE_EXTERNALS:
			state_moveExternals();
			if (actualIndexInShip >= ship.jumpBlocks.length - 1) {
				state = STATE_ENTITIES;
			}
			break;
			
		case STATE_ENTITIES:
			state_moveEntities();
			actualIndexInShip = 0;
			state = STATE_REMOVING;
			break;
			
		case STATE_REMOVING:
			if (enforceEntitiesPosition) {
				restoreEntitiesPosition();
			}
			state_removeBlocks();
			
			if (actualIndexInShip >= ship.jumpBlocks.length - 1) {
				state = STATE_CHUNKUNLOADING;
			}
			break;
			
		case STATE_CHUNKUNLOADING:
			state_chunkUnloading();
			state = STATE_FINISHING;
			break;
			
		case STATE_FINISHING:
			state_finishing();
			state = STATE_IDLE;
			break;
			
		default:
			disableAndMessage(new TextComponentString("Invalid state, aborting jump..."));
			return true;
		}
		return true;
	}
	
	private boolean forceSourceChunks(StringBuilder reason) {
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Forcing source chunks in " + sourceWorld.provider.getDimensionType().getName());
		}
		sourceWorldTicket = ForgeChunkManager.requestTicket(WarpDrive.instance, sourceWorld, Type.NORMAL);
		if (sourceWorldTicket == null) {
			reason.append("Chunkloading rejected in source world " + sourceWorld.getWorldInfo().getWorldName() + ". Aborting.");
			return false;
		}
		int x1 = ship.minX >> 4;
		int x2 = ship.maxX >> 4;
		int z1 = ship.minZ >> 4;
		int z2 = ship.maxZ >> 4;
		int chunkCount = 0;
		for (int x = x1; x <= x2; x++) {
			for (int z = z1; z <= z2; z++) {
				chunkCount++;
				if (chunkCount > sourceWorldTicket.getMaxChunkListDepth()) {
					reason.append("Ship is extending over too many chunks in source world. Max is currently set to " + sourceWorldTicket.getMaxChunkListDepth() + " in forgeChunkLoading.cfg. Aborting.");
					return false;
				}
				ForgeChunkManager.forceChunk(sourceWorldTicket, new ChunkPos(x, z));
			}
		}
		return true;
	}
	
	private boolean forceTargetChunks(StringBuilder reason) {
		LocalProfiler.start("Jump.forceTargetChunks");
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Forcing target chunks in " + targetWorld.provider.getDimensionType().getName());
		}
		targetWorldTicket = ForgeChunkManager.requestTicket(WarpDrive.instance, targetWorld, Type.NORMAL);
		if (targetWorldTicket == null) {
			reason.append("Chunkloading rejected in target world " + sourceWorld.getWorldInfo().getWorldName() + ". Aborting.");
			return false;
		}
		
		BlockPos targetMin = transformation.apply(ship.minX, ship.minY, ship.minZ);
		BlockPos targetMax = transformation.apply(ship.maxX, ship.maxY, ship.maxZ);
		int x1 = Math.min(targetMin.getX(), targetMax.getX()) >> 4;
		int x2 = Math.max(targetMin.getX(), targetMax.getX()) >> 4;
		int z1 = Math.min(targetMin.getZ(), targetMax.getZ()) >> 4;
		int z2 = Math.max(targetMin.getZ(), targetMax.getZ()) >> 4;
		int chunkCount = 0;
		for (int x = x1; x <= x2; x++) {
			for (int z = z1; z <= z2; z++) {
				chunkCount++;
				if (chunkCount > targetWorldTicket.getMaxChunkListDepth()) {
					reason.append("Ship is extending over too many chunks in target world. Max is currently set to " + targetWorldTicket.getMaxChunkListDepth() + " in forgeChunkLoading.cfg. Aborting.");
					return false;
				}
				ForgeChunkManager.forceChunk(targetWorldTicket, new ChunkPos(x, z));
			}
		}
		LocalProfiler.stop();
		return true;
	}
	
	private void unforceChunks() {
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Unforcing chunks");
		}
		
		int x1, x2, z1, z2;
		if (sourceWorldTicket != null) {
			x1 = ship.minX >> 4;
			x2 = ship.maxX >> 4;
			z1 = ship.minZ >> 4;
			z2 = ship.maxZ >> 4;
			for (int x = x1; x <= x2; x++) {
				for (int z = z1; z <= z2; z++) {
					ForgeChunkManager.unforceChunk(sourceWorldTicket, new ChunkPos(x, z));
				}
			}
			ForgeChunkManager.releaseTicket(sourceWorldTicket);
			sourceWorldTicket = null;
		}
		
		if (targetWorldTicket != null) {
			BlockPos targetMin = transformation.apply(ship.minX, ship.minY, ship.minZ);
			BlockPos targetMax = transformation.apply(ship.maxX, ship.maxY, ship.maxZ);
			x1 = Math.min(targetMin.getX(), targetMax.getX()) >> 4;
			x2 = Math.max(targetMin.getX(), targetMax.getX()) >> 4;
			z1 = Math.min(targetMin.getZ(), targetMax.getZ()) >> 4;
			z2 = Math.max(targetMin.getZ(), targetMax.getZ()) >> 4;
			for (int x = x1; x <= x2; x++) {
				for (int z = z1; z <= z2; z++) {
					ForgeChunkManager.unforceChunk(targetWorldTicket, new ChunkPos(x, z));
				}
			}
			ForgeChunkManager.releaseTicket(targetWorldTicket);
			targetWorldTicket = null;
		}
	}
	
	private void state_chunkLoading() {
		LocalProfiler.start("Jump.chunkLoading");
		
		StringBuilder reason = new StringBuilder();
		
		if (!forceSourceChunks(reason)) {
			disableAndMessage(reason.toString());
			LocalProfiler.stop();
			return;
		}
		
		LocalProfiler.stop();
	}
	
	private void state_saving() {
		LocalProfiler.start("Jump.saving");
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Saving ship...");
		}
		
		StringBuilder reason = new StringBuilder();
		
		if (!ship.save(reason)) {
			disableAndMessage(reason.toString());
			LocalProfiler.stop();
			return;
		}
		
		LocalProfiler.stop();
	}
	
	private void state_borders() {
		LocalProfiler.start("Jump.borders");
		
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Checking ship borders...");
		}
		
		StringBuilder reason = new StringBuilder();
		
		if (!ship.checkBorders(reason)) {
			disableAndMessage(reason.toString());
			LocalProfiler.stop();
			return;
		}
		
		try {
			// Generate unique file name
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH'h'mm'm'ss's'SSS");
			String shipName = ship.shipCore.shipName.replaceAll("[^ -~]", "").replaceAll("[:/\\\\]", "");
			String schematicFileName;
			do {
				Date now = new Date();
				schematicFileName = WarpDriveConfig.G_SCHEMALOCATION + "/auto/" + shipName + "_" + sdfDate.format(now) + ".schematic";
			} while (new File(schematicFileName).exists());
			
			// Save header
			NBTTagCompound schematic = new NBTTagCompound();
			
			short width = (short) (ship.shipCore.maxX - ship.shipCore.minX + 1);
			short length = (short) (ship.shipCore.maxZ - ship.shipCore.minZ + 1);
			short height = (short) (ship.shipCore.maxY - ship.shipCore.minY + 1);
			schematic.setShort("Width", width);
			schematic.setShort("Length", length);
			schematic.setShort("Height", height);
			schematic.setInteger("shipMass", ship.shipCore.shipMass);
			schematic.setString("shipName", ship.shipCore.shipName);
			schematic.setInteger("shipVolume", ship.shipCore.shipVolume);
			NBTTagCompound tagCompoundShip = new NBTTagCompound();
			ship.writeToNBT(tagCompoundShip);
			schematic.setTag("ship", tagCompoundShip);
			writeNBTToFile(schematicFileName, schematic);
			if (WarpDriveConfig.LOGGING_JUMP) {
				//	WarpDrive.logger.info(this + " Ship saved as " + schematicFileName);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		msCounter = System.currentTimeMillis();
		LocalProfiler.stop();
	}
	
	private void writeNBTToFile(String fileName, NBTTagCompound nbttagcompound) {
		WarpDrive.logger.info(this + " Saving ship state prior to jump in " + fileName);
		
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
	
	private void state_transformer() {
		LocalProfiler.start("Jump.transformer");
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Transformer evaluation...");
		}
		
		StringBuilder reason = new StringBuilder();
		
		boolean isInSpace = (sourceWorld.provider.getDimension() == WarpDriveConfig.G_SPACE_DIMENSION_ID);
		boolean isInHyperSpace = (sourceWorld.provider.getDimension() == WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID);
		
		boolean toSpace = (moveY > 0) && (ship.maxY + moveY > 255) && (!isInSpace) && (!isInHyperSpace);
		boolean fromSpace = (moveY < 0) && (ship.minY + moveY < 0) && isInSpace;
		betweenWorlds = fromSpace || toSpace || isHyperspaceJump;
		
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (isHyperspaceJump) {
			if (isInHyperSpace) {
				targetWorld = server.worldServerForDimension(WarpDriveConfig.G_SPACE_DIMENSION_ID);
				if (targetWorld == null) {
					LocalProfiler.stop();
					disableAndMessage("Unable to load Space dimension " + WarpDriveConfig.G_SPACE_DIMENSION_ID + ", aborting jump.");
					return;
				}
			} else if (isInSpace) {
				targetWorld = server.worldServerForDimension(WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID);
				if (targetWorld == null) {
					LocalProfiler.stop();
					disableAndMessage("Unable to load Hyperspace dimension " + WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID + ", aborting jump.");
					return;
				}
			} else {
				disableAndMessage("Unable to reach hyperspace from a planet");
				LocalProfiler.stop();
				return;
			}
			
		} else if (toSpace) {
			Boolean planetFound = false;
			Boolean planetValid = false;
			int closestPlanetDistance = Integer.MAX_VALUE;
			Planet closestPlanet = null;
			for (int indexPlanet = 0; (!planetValid) && indexPlanet < WarpDriveConfig.PLANETS.length; indexPlanet++) {
				Planet planet = WarpDriveConfig.PLANETS[indexPlanet];
				if (sourceWorld.provider.getDimension() == planet.dimensionId) {
					planetFound = true;
					int planetDistance = planet.isValidToSpace(new VectorI(ship.core));
					if (planetDistance == 0) {
						planetValid = true;
						moveX = planet.spaceCenterX - planet.dimensionCenterX;
						moveZ = planet.spaceCenterZ - planet.dimensionCenterZ;
						targetWorld = sourceWorld.getMinecraftServer().worldServerForDimension(WarpDriveConfig.G_SPACE_DIMENSION_ID);
						if (targetWorld == null) {
							LocalProfiler.stop();
							disableAndMessage("Unable to load Space dimension " + WarpDriveConfig.G_SPACE_DIMENSION_ID + ", aborting jump.");
							return;
						}
					} else if (closestPlanetDistance > planetDistance) {
						closestPlanetDistance = planetDistance;
						closestPlanet = planet;
					}
				}
			}
			if (!planetFound) {
				LocalProfiler.stop();
				disableAndMessage("Unable to reach space!\nThere's no planet defined for current dimension " + sourceWorld.provider.getDimensionType().getName() + " ("
						+ sourceWorld.provider.getDimension() + ")");
				return;
			}
			if (!planetValid) {
				LocalProfiler.stop();
				assert(closestPlanet != null);
				disableAndMessage(new TextComponentString("Ship is outside planet border, unable to reach space!\nClosest transition plane is ~" + closestPlanetDistance + " m away ("
						+ (closestPlanet.dimensionCenterX - closestPlanet.borderSizeX) + " 250 "
						+ (closestPlanet.dimensionCenterZ - closestPlanet.borderSizeZ) + ") to ("
						+ (closestPlanet.dimensionCenterX + closestPlanet.borderSizeX) + " 255 "
						+ (closestPlanet.dimensionCenterZ + closestPlanet.borderSizeZ) + ")"));
				return;
			}
			
		} else if (fromSpace) {
			Boolean planetFound = false;
			int closestPlaneDistance = Integer.MAX_VALUE;
			Planet closestTransitionPlane = null;
			for (int indexPlanet = 0; (!planetFound) && indexPlanet < WarpDriveConfig.PLANETS.length; indexPlanet++) {
				Planet planet = WarpDriveConfig.PLANETS[indexPlanet];
				int planeDistance = planet.isValidFromSpace(new VectorI(ship.core));
				if (planeDistance == 0) {
					planetFound = true;
					moveX = planet.dimensionCenterX - planet.spaceCenterX;
					moveZ = planet.dimensionCenterZ - planet.spaceCenterZ;
					targetWorld = sourceWorld.getMinecraftServer().worldServerForDimension(planet.dimensionId);
					if (targetWorld == null) {
						LocalProfiler.stop();
						disableAndMessage(new TextComponentString("Undefined dimension " + planet.dimensionId + ", aborting jump. Check your server configuration!"));
						return;
					}
				} else if (closestPlaneDistance > planeDistance) {
					closestPlaneDistance = planeDistance;
					closestTransitionPlane = planet;
				}
			}
			if (!planetFound) {
				LocalProfiler.stop();
				ITextComponent msg;
				if (closestTransitionPlane == null) {
					msg = new TextComponentString("No planet defined, unable to enter atmosphere!");
				} else {
					msg = new TextComponentString("No planet in range, unable to enter atmosphere!\nClosest planet is " + closestPlaneDistance + " m away ("
							+ (closestTransitionPlane.spaceCenterX - closestTransitionPlane.borderSizeX) + " 250 "
							+ (closestTransitionPlane.spaceCenterZ - closestTransitionPlane.borderSizeZ) + ") to ("
							+ (closestTransitionPlane.spaceCenterX + closestTransitionPlane.borderSizeX) + " 255 "
							+ (closestTransitionPlane.spaceCenterZ + closestTransitionPlane.borderSizeZ) + ")");
				}
				disableAndMessage(msg);
				return;
			}
			
		} else {
			targetWorld = sourceWorld;
		}
		
		// Check mass constrains
		if ( ((!isInSpace) && (!isInHyperSpace))
		  || ( (targetWorld.provider.getDimension() != WarpDriveConfig.G_SPACE_DIMENSION_ID)
			&& (targetWorld.provider.getDimension() != WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID)) ) {
			if (!ship.isUnlimited() && ship.actualMass > WarpDriveConfig.SHIP_VOLUME_MAX_ON_PLANET_SURFACE) {
				disableAndMessage(new TextComponentString("Ship is too big for a planet (max is " + WarpDriveConfig.SHIP_VOLUME_MAX_ON_PLANET_SURFACE + " blocks)"));
				return;
			}
		}
		
		if (betweenWorlds && WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " From world " + sourceWorld.provider.getDimensionType().getName() + " to " + targetWorld.provider.getDimensionType().getName());
		}
		
		// Calculate jump vector
		Boolean isPluginCheckDone = false;
		String firstAdjustmentReason = "";
		if (isCoordJump) {
			moveX = destX - ship.core.getX();
			moveY = destY - ship.core.getY();
			moveZ = destZ - ship.core.getZ();
		} else if (!isHyperspaceJump) {
			if (toSpace) {
				// enter space at current altitude
				moveY = 0;
			} else if (fromSpace) {
				// re-enter atmosphere at max altitude
				moveY = 245 - ship.maxY;
			} else {
				if ((ship.maxY + moveY) > 255) {
					moveY = 255 - ship.maxY;
				}
				
				if ((ship.minY + moveY) < 5) {
					moveY = 5 - ship.minY;
				}
				
				// Do not check in long jumps
				int rangeX = Math.abs(moveX) - (ship.maxX - ship.minX);
				int rangeZ = Math.abs(moveZ) - (ship.maxZ - ship.minZ);
				if (Math.max(rangeX, rangeZ) < 256) {
					firstAdjustmentReason = getPossibleJumpDistance();
					isPluginCheckDone = true;
				}
			}
		}
		transformation = new Transformation(ship, targetWorld, moveX, moveY, moveZ, rotationSteps);
		
		if (betweenWorlds && WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " From world " + sourceWorld.provider.getDimensionType().getName() + " to " + targetWorld.provider.getDimensionType().getName());
		}
		
		{
			BlockPos target1 = transformation.apply(ship.minX, ship.minY, ship.minZ);
			BlockPos target2 = transformation.apply(ship.maxX, ship.maxY, ship.maxZ);
			AxisAlignedBB aabbSource = new AxisAlignedBB(ship.minX, ship.minY, ship.minZ, ship.maxX, ship.maxY, ship.maxZ);
			aabbSource.expand(1.0D, 1.0D, 1.0D);
			AxisAlignedBB aabbTarget = new AxisAlignedBB(
					Math.min(target1.getX(), target2.getX()), Math.min(target1.getY(), target2.getY()), Math.min(target1.getZ(), target2.getZ()),
					Math.max(target1.getX(), target2.getX()), Math.max(target1.getY(), target2.getY()), Math.max(target1.getZ(), target2.getZ()));
			// Validate positions aren't overlapping
			if (!betweenWorlds && aabbSource.intersectsWith(aabbTarget)) {
				// render fake explosions
				doCollisionDamage(false);
				
				// cancel jump
				String msg;
				if (firstAdjustmentReason == null || firstAdjustmentReason.isEmpty()) {
					msg = "Source and target areas are overlapping, jump aborted! Try increasing jump distance...";
				} else {
					msg = firstAdjustmentReason + "\nNot enough space after adjustment, jump aborted!";
				}
				disableAndMessage(msg);
				LocalProfiler.stop();
				return;
			}
			
			// Check world border
			if ( (targetWorld.provider.getDimension() == WarpDriveConfig.G_SPACE_DIMENSION_ID)
			  || (targetWorld.provider.getDimension() == WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID)) {
				if (WarpDriveConfig.G_SPACE_WORLDBORDER_BLOCKS > 0) {// Space world border is enabled
					if ( Math.abs(aabbTarget.minX) > WarpDriveConfig.G_SPACE_WORLDBORDER_BLOCKS
					  || Math.abs(aabbTarget.minZ) > WarpDriveConfig.G_SPACE_WORLDBORDER_BLOCKS
					  || Math.abs(aabbTarget.maxX) > WarpDriveConfig.G_SPACE_WORLDBORDER_BLOCKS
					  || Math.abs(aabbTarget.maxZ) > WarpDriveConfig.G_SPACE_WORLDBORDER_BLOCKS ) {
						// cancel jump
						disableAndMessage(new TextComponentString("Space border reach, max is " + WarpDriveConfig.G_SPACE_WORLDBORDER_BLOCKS));
						LocalProfiler.stop();
						return;
					}
				}
				
			} else {
				Boolean planetFound = false;
				Boolean planetValid = false;
				int closestPlanetDistance = Integer.MAX_VALUE;
				Planet closestPlanet = null;
				for (int indexPlanet = 0; (!planetValid) && indexPlanet < WarpDriveConfig.PLANETS.length; indexPlanet++) {
					Planet planet = WarpDriveConfig.PLANETS[indexPlanet];
					if (targetWorld.provider.getDimension() == planet.dimensionId) {
						planetFound = true;
						int planetDistance = planet.isInsideBorder(aabbTarget);
						if (planetDistance == 0) {
							planetValid = true;
						} else if (closestPlanetDistance > planetDistance) {
							closestPlanetDistance = planetDistance;
							closestPlanet = planet;
						}
					}
				}
				if (!planetFound) {
					if (WarpDriveConfig.LOGGING_JUMP) {
						WarpDrive.logger.error("There's no world border defined for dimension " + targetWorld.provider.getDimensionType().getName());
					}
				} else if (!planetValid) {
					LocalProfiler.stop();
					assert (closestPlanet != null);
					String msg = "Target ship position is outside planet border, unable to jump!\nPlanet borders are ("
					             + (closestPlanet.dimensionCenterX - closestPlanet.borderSizeX) + " 0 "
					             + (closestPlanet.dimensionCenterZ - closestPlanet.borderSizeZ) + ") to ("
					             + (closestPlanet.dimensionCenterX + closestPlanet.borderSizeX) + " 255 "
					             + (closestPlanet.dimensionCenterZ + closestPlanet.borderSizeZ) + ")";
					disableAndMessage(msg);
					return;
				}
			}
		}
		if (!isPluginCheckDone) {
			CheckMovementResult checkMovementResult = checkCollisionAndProtection(transformation, true, "target");
			if (checkMovementResult != null) {
				String msg = checkMovementResult.reason + "\nJump aborted!";
				disableAndMessage(msg);
				LocalProfiler.stop();
				return;
			}
		}
		
		if (!forceTargetChunks(reason)) {
			disableAndMessage(reason.toString());
			LocalProfiler.stop();
			return;
		}
		
		{
			String msg = ship.saveEntities();
			if (msg != null) {
				disableAndMessage(msg);
				LocalProfiler.stop();
				return;
			}
			if (WarpDriveConfig.LOGGING_JUMP) {
				WarpDrive.logger.info(this + " Saved " + ship.entitiesOnShip.size() + " entities from ship");
			}
		}
		
		if (isHyperspaceJump && isInSpace) {
			ship.messageToAllPlayersOnShip(new TextComponentString("Entering HYPERSPACE..."));
		} else if (isHyperspaceJump && isInHyperSpace) {
			ship.messageToAllPlayersOnShip(new TextComponentString("Leaving HYPERSPACE.."));
		} else if (isCoordJump) {
			ship.messageToAllPlayersOnShip(new TextComponentString("Jumping to coordinates (" + destX + " " + destY + " " + destZ + ")!"));
		} else {
			ship.messageToAllPlayersOnShip(new TextComponentString("Jumping of " + Math.round(Math.sqrt(moveX * moveX + moveY * moveY + moveZ * moveZ)) + " blocks (" + moveX + " " + moveY + " " + moveZ + ")"));
		}
		switch (rotationSteps) {
			case 1:
				ship.messageToAllPlayersOnShip(new TextComponentString("Turning to the right"));
				break;
			case 2:
				ship.messageToAllPlayersOnShip(new TextComponentString("Turning back"));
				break;
			case 3:
				ship.messageToAllPlayersOnShip(new TextComponentString("Turning to the left"));
				break;
			default:
				break;
		}
		
		LocalProfiler.stop();
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info("Removing TE duplicates: tileEntities in target world before jump: " + targetWorld.loadedTileEntityList.size());
		}
	}
	
	private void state_moveBlocks() {
		LocalProfiler.start("Jump.moveBlocks");
		int blocksToMove = Math.min(WarpDriveConfig.G_BLOCKS_PER_TICK, ship.jumpBlocks.length - actualIndexInShip);
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Moving ship blocks " + actualIndexInShip + " to " + (actualIndexInShip + blocksToMove - 1) + " / " + (ship.jumpBlocks.length - 1));
		}
		
		for (int index = 0; index < blocksToMove; index++) {
			if (actualIndexInShip >= ship.jumpBlocks.length) {
				break;
			}
			
			JumpBlock jumpBlock = ship.jumpBlocks[actualIndexInShip];
			if (jumpBlock != null) {
				if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
					WarpDrive.logger.info("Deploying from " + jumpBlock.x + " " + jumpBlock.y + " " + jumpBlock.z + " of " + jumpBlock.block + "@" + jumpBlock.blockMeta);
				}
				jumpBlock.deploy(targetWorld, transformation);
				
				sourceWorld.removeTileEntity(new BlockPos(jumpBlock.x, jumpBlock.y, jumpBlock.z));
			}
			actualIndexInShip++;
		}
		
		LocalProfiler.stop();
	}
	
	private void state_moveExternals() {
		LocalProfiler.start("Jump.moveExternals");
		int blocksToMove = Math.min(WarpDriveConfig.G_BLOCKS_PER_TICK, ship.jumpBlocks.length - actualIndexInShip);
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Moving ship externals from " + actualIndexInShip + " / " + (ship.jumpBlocks.length - 1));
		}
		int index = 0;
		while (index < blocksToMove && actualIndexInShip < ship.jumpBlocks.length) {
			JumpBlock jumpBlock = ship.jumpBlocks[ship.jumpBlocks.length - actualIndexInShip - 1];
			if (jumpBlock == null) {
				if (WarpDriveConfig.LOGGING_JUMP) {
					WarpDrive.logger.info(this + " Moving ship externals: unexpected null found at ship[" + actualIndexInShip + "]");
				}
				actualIndexInShip++;
				continue;
			}
			
			if (jumpBlock.blockTileEntity != null && jumpBlock.externals != null) {
				if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
					WarpDrive.logger.info("Moving externals for block " + jumpBlock.block + "@" + jumpBlock.blockMeta + " at " + jumpBlock.x + " " + jumpBlock.y + " " + jumpBlock.z);
				}
				for (Entry<String, NBTBase> external : jumpBlock.externals.entrySet()) {
					IBlockTransformer blockTransformer = WarpDriveConfig.blockTransformers.get(external.getKey());
					if (blockTransformer != null) {
						blockTransformer.remove(jumpBlock.blockTileEntity);
						
						BlockPos target = transformation.apply(jumpBlock.x, jumpBlock.y, jumpBlock.z);
						TileEntity newTileEntity = targetWorld.getTileEntity(target);
						blockTransformer.restoreExternals(newTileEntity, transformation, external.getValue());
					}
				}
				index++;
			}
			actualIndexInShip++;
		}
		LocalProfiler.stop();
	}
	
	private void state_moveEntities() {
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Moving entities");
		}
		LocalProfiler.start("Jump.moveEntities");
		
		if (ship.entitiesOnShip != null) {
			for (MovingEntity me : ship.entitiesOnShip) {
				Entity entity = me.entity;
				
				if (entity == null) {
					continue;
				}
				
				double oldEntityX = me.oldX;
				double oldEntityY = me.oldY;
				double oldEntityZ = me.oldZ;
				Vec3d target = transformation.apply(oldEntityX, oldEntityY, oldEntityZ);
				double newEntityX = target.xCoord;
				double newEntityY = target.yCoord;
				double newEntityZ = target.zCoord;
				
				if (WarpDriveConfig.LOGGING_JUMP) {
					WarpDrive.logger.info(String.format("Entity moving: (%.2f %.2f %.2f) -> (%.2f %.2f %.2f) entity %s",
							oldEntityX, oldEntityY, oldEntityZ,
							newEntityX, newEntityY, newEntityZ, entity.toString()));
				}
				
				// Travel to another dimension if needed
				if (betweenWorlds) {
					MinecraftServer server = sourceWorld.getMinecraftServer();
					WorldServer from = server.worldServerForDimension(sourceWorld.provider.getDimension());
					WorldServer to = server.worldServerForDimension(targetWorld.provider.getDimension());
					SpaceTeleporter teleporter = new SpaceTeleporter(to, 0,
							MathHelper.floor_double(newEntityX),
							MathHelper.floor_double(newEntityY),
							MathHelper.floor_double(newEntityZ));
					
					if (entity instanceof EntityPlayerMP) {
						EntityPlayerMP player = (EntityPlayerMP) entity;
						server.getPlayerList().transferPlayerToDimension(player, targetWorld.provider.getDimension(), teleporter);
						player.sendPlayerAbilities();
					} else {
						server.getPlayerList().transferEntityToWorld(entity, sourceWorld.provider.getDimension(), from, to, teleporter);
					}
				}
				
				// Update position
				transformation.rotate(entity);
				if (entity instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP) entity;
					
					BlockPos bedLocation = player.getBedLocation(player.worldObj.provider.getDimension());
					
					if ( ship.minX <= bedLocation.getX() && ship.maxX >= bedLocation.getX()
					  && ship.minY <= bedLocation.getY() && ship.maxY >= bedLocation.getY()
					  && ship.minZ <= bedLocation.getZ() && ship.maxZ >= bedLocation.getZ()) {
						bedLocation = transformation.apply(bedLocation);
						player.setSpawnChunk(bedLocation, false, targetWorld.provider.getDimension());
					}
					player.setPositionAndUpdate(newEntityX, newEntityY, newEntityZ);
				} else {
					entity.setPosition(newEntityX, newEntityY, newEntityZ);
				}
			}
		}
		
		LocalProfiler.stop();
	}
	
	private void state_removeBlocks() {
		LocalProfiler.start("Jump.removeBlocks");
		int blocksToMove = Math.min(WarpDriveConfig.G_BLOCKS_PER_TICK, ship.jumpBlocks.length - actualIndexInShip);
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Removing ship blocks " + actualIndexInShip + " to " + (actualIndexInShip + blocksToMove - 1) + " / " + (ship.jumpBlocks.length - 1));
		}
		for (int index = 0; index < blocksToMove; index++) {
			if (actualIndexInShip >= ship.jumpBlocks.length) {
				break;
			}
			JumpBlock jumpBlock = ship.jumpBlocks[ship.jumpBlocks.length - actualIndexInShip - 1];
			if (jumpBlock == null) {
				if (WarpDriveConfig.LOGGING_JUMP) {
					WarpDrive.logger.info(this + " Removing ship part: unexpected null found at ship[" + actualIndexInShip + "]");
				}
				actualIndexInShip++;
				continue;
			}
			if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
				WarpDrive.logger.info("Removing block " + jumpBlock.block + "@" + jumpBlock.blockMeta + " at " + jumpBlock.x + " " + jumpBlock.y + " " + jumpBlock.z);
			}
			
			if (jumpBlock.blockTileEntity != null) {
				if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
					WarpDrive.logger.info("Removing tile entity at " + jumpBlock.x + " " + jumpBlock.y + " " + jumpBlock.z);
				}
				sourceWorld.removeTileEntity(new BlockPos(jumpBlock.x, jumpBlock.y, jumpBlock.z));
			}
			try {
				JumpBlock.setBlockNoLight(sourceWorld, new BlockPos(jumpBlock.x, jumpBlock.y, jumpBlock.z), Blocks.AIR.getDefaultState(), 2);
			} catch (Exception exception) {
				WarpDrive.logger.info("Exception while removing " + jumpBlock.block + "@" + jumpBlock.blockMeta + " at " + jumpBlock.x + " " + jumpBlock.y + " " + jumpBlock.z);
				if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
					exception.printStackTrace();
				}
			}
			
			BlockPos target = transformation.apply(jumpBlock.x, jumpBlock.y, jumpBlock.z); 
			JumpBlock.refreshBlockStateOnClient(targetWorld, target);
			
			actualIndexInShip++;
		}
		LocalProfiler.stop();
	}
	
	private void state_chunkUnloading() {
		LocalProfiler.start("Jump.chunkUnloading");
		
		unforceChunks();
		
		LocalProfiler.stop();
	}
	
	/**
	 * Finishing jump: cleanup, collision effects and delete self
	 **/
	private void state_finishing() {
		LocalProfiler.start("Jump.finishing()");
		// FIXME TileEntity duplication workaround
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Jump done in " + ((System.currentTimeMillis() - msCounter) / 1000F) + " seconds and " + ticks + " ticks");
		}
		if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
			WarpDrive.logger.info("Removing TE duplicates: tileEntities in target world after jump, before cleanup: " + targetWorld.loadedTileEntityList.size());
		}
		
		try {
			// @TODO MC1.10 still leaking tile entities?
			// targetWorld.loadedTileEntityList = removeDuplicates(targetWorld.loadedTileEntityList);
		} catch (Exception exception) {
			if (WarpDriveConfig.LOGGING_JUMP) {
				WarpDrive.logger.info("TE Duplicates removing exception: " + exception.getMessage());
				exception.printStackTrace();
			}
		}
		
		doCollisionDamage(true);
		
		disable(new TextComponentString("Jump done"));
		if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
			WarpDrive.logger.info("Removing TE duplicates: tileEntities in target world after jump, after cleanup: " + targetWorld.loadedTileEntityList.size());
		}
		LocalProfiler.stop();
	}
	
	private String getPossibleJumpDistance() {
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Calculating possible jump distance...");
		}
		int originalRange = Math.max(Math.abs(moveX), Math.max(Math.abs(moveY), Math.abs(moveZ)));
		int testRange = originalRange;
		int blowPoints = 0;
		collisionDetected = false;
		
		CheckMovementResult result;
		String firstAdjustmentReason = "";
		while (testRange >= 0) {
			// Is there enough space in destination point?
			result = checkMovement(testRange / (double)originalRange, false);
			if (result == null) {
				break;
			}
			if (firstAdjustmentReason.isEmpty()) {
				firstAdjustmentReason = result.reason;
			}
			
			if (result.isCollision) {
				blowPoints++;
			}
			testRange--;
		}
		VectorI finalMovement = getMovementVector(testRange / (double)originalRange);
		
		if (originalRange != testRange && WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Jump range adjusted from " + originalRange + " to " + testRange + " after " + blowPoints + " collisions");
		}
		
		// Register explosion(s) at collision point
		if (blowPoints > WarpDriveConfig.SHIP_COLLISION_TOLERANCE_BLOCKS) {
			result = checkMovement(Math.min(1.0D, Math.max(0.0D, (testRange + 1) / (double)originalRange)), true);
			if (result != null) {
				/*
				 * Strength scaling:
				 * Wither skull = 1
				 * Creeper = 3 or 6
				 * TNT = 4
				 * TNT cart = 4 to 11.5
				 * Wither boom = 5
				 * Endercrystal = 6
				 */
				float massCorrection = 0.5F
						+ (float) Math.sqrt(Math.min(1.0D, Math.max(0.0D, ship.shipCore.shipMass - WarpDriveConfig.SHIP_VOLUME_MAX_ON_PLANET_SURFACE)
								/ WarpDriveConfig.SHIP_VOLUME_MIN_FOR_HYPERSPACE));
				collisionDetected = true;
				collisionStrength = (4.0F + blowPoints - WarpDriveConfig.SHIP_COLLISION_TOLERANCE_BLOCKS) * massCorrection;
				collisionAtSource = result.atSource;
				collisionAtTarget = result.atTarget;
				WarpDrive.logger.info(this + " Reporting " + collisionAtTarget.size() + " collisions points after " + blowPoints
							+ " blowPoints with " + String.format("%.2f", massCorrection) + " ship mass correction => "
							+ String.format("%.2f", collisionStrength) + " explosion strength");
			} else {
				WarpDrive.logger.error("WarpDrive error: unable to compute collision points, ignoring...");
			}
		}
		
		// Update movement after computing collision points 
		moveX = finalMovement.x;
		moveY = finalMovement.y;
		moveZ = finalMovement.z;
		return firstAdjustmentReason;
	}
	
	private void doCollisionDamage(boolean atTarget) {
		if (!collisionDetected) {
			if (WarpDriveConfig.LOGGING_JUMP) {
				WarpDrive.logger.info(this + " doCollisionDamage No collision detected...");
			}
			return;
		}
		ArrayList<Vector3> collisionPoints = atTarget ? collisionAtTarget : collisionAtSource;
		Vector3 min = collisionPoints.get(0).clone();
		Vector3 max = collisionPoints.get(0).clone();
		for (Vector3 v : collisionPoints) {
			if (min.x > v.x) {
				min.x = v.x;
			} else if (max.x < v.x) {
				max.x = v.x;
			}
			if (min.y > v.y) {
				min.y = v.y;
			} else if (max.y < v.y) {
				max.y = v.y;
			}
			if (min.z > v.z) {
				min.z = v.z;
			} else if (max.z < v.z) {
				max.z = v.z;
			}
		}
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Ship collision from " + min + " to " + max);
		}
		
		// inform players on board
		double rx = Math.round(min.x + sourceWorld.rand.nextInt(Math.max(1, (int) (max.x - min.x))));
		double ry = Math.round(min.y + sourceWorld.rand.nextInt(Math.max(1, (int) (max.y - min.y))));
		double rz = Math.round(min.z + sourceWorld.rand.nextInt(Math.max(1, (int) (max.z - min.z))));
		ship.messageToAllPlayersOnShip(new TextComponentString("Ship collision detected around " + (int) rx + ", " + (int) ry + ", " + (int) rz + ". Damage report pending..."));
		
		// randomize if too many collision points
		int nbExplosions = Math.min(5, collisionPoints.size());
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info("doCollisionDamage nbExplosions " + nbExplosions + "/" + collisionPoints.size());
		}
		for (int i = 0; i < nbExplosions; i++) {
			// get location
			Vector3 current;
			if (nbExplosions < collisionPoints.size()) {
				if (WarpDriveConfig.LOGGING_JUMP) {
					WarpDrive.logger.info("doCollisionDamage random #" + i);
				}
				current = collisionPoints.get(sourceWorld.rand.nextInt(collisionPoints.size()));
			} else {
				if (WarpDriveConfig.LOGGING_JUMP) {
					WarpDrive.logger.info("doCollisionDamage get " + i);
				}
				current = collisionPoints.get(i);
			}
			
			// compute explosion strength with a jitter, at least 1 TNT
			float strength = Math.max(4.0F, collisionStrength / nbExplosions - 2.0F + 2.0F * sourceWorld.rand.nextFloat());
			
			(atTarget ? targetWorld : sourceWorld).newExplosion(null, current.x, current.y, current.z, strength, atTarget, atTarget);
			WarpDrive.logger.info("Ship collision caused explosion at " + current.x + " " + current.y + " " + current.z + " with strength " + strength);
		}
	}
	
	private void restoreEntitiesPosition() {
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Restoring entities position");
		}
		LocalProfiler.start("Jump.restoreEntitiesPosition");
		
		if (ship.entitiesOnShip != null) {
			for (MovingEntity movingEntity : ship.entitiesOnShip) {
				Entity entity = movingEntity.entity;
				
				if (entity == null) {
					continue;
				}
				
				if (WarpDriveConfig.LOGGING_JUMP) {
					WarpDrive.logger.info("Entity restoring position at (" + movingEntity.oldX + " " + movingEntity.oldY + " " + movingEntity.oldZ + ")");
				}
				
				// Update position
				if (entity instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP) entity;
					
					player.setPositionAndUpdate(movingEntity.oldX, movingEntity.oldY, movingEntity.oldZ);
				} else {
					entity.setPosition(movingEntity.oldX, movingEntity.oldY, movingEntity.oldZ);
				}
			}
		}
		
		LocalProfiler.stop();
	}
	
	private class CheckMovementResult {
		final ArrayList<Vector3> atSource;
		final ArrayList<Vector3> atTarget;
		boolean isCollision = false;
		public String reason = "";
		
		CheckMovementResult() {
			atSource = new ArrayList<>(1);
			atTarget = new ArrayList<>(1);
			isCollision = false;
			reason = "Unknown reason";
		}
		
		public void add(double sx, double sy, double sz, double tx, double ty, double tz, boolean pisCollision, String preason) {
			atSource.add(new Vector3(sx, sy, sz));
			atTarget.add(new Vector3(tx, ty, tz));
			isCollision = isCollision || pisCollision;
			reason = preason;
			if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
				WarpDrive.logger.info("CheckMovementResult " + sx + ", " + sy + ", " + sz + " -> " + tx + ", " + ty + ", " + tz + " " + isCollision + " '" + reason + "'");
			}
		}
	}
	
	private CheckMovementResult checkCollisionAndProtection(ITransformation transformation, final boolean fullCollisionDetails, final String context) {
		CheckMovementResult result = new CheckMovementResult();
		VectorI offset = new VectorI((int)Math.signum(moveX), (int)Math.signum(moveY), (int)Math.signum(moveZ));
		
		int x, y, z;
		BlockPos blockPosTarget;
		IBlockState blockStateSource;
		IBlockState blockStateTarget;
		for (y = ship.minY; y <= ship.maxY; y++) {
			for (x = ship.minX; x <= ship.maxX; x++) {
				for (z = ship.minZ; z <= ship.maxZ; z++) {
					blockPosTarget = transformation.apply(x, y, z);
					blockStateSource = sourceWorld.getBlockState(new BlockPos(x, y, z));
					blockStateTarget = targetWorld.getBlockState(blockPosTarget);
					if (Dictionary.BLOCKS_ANCHOR.contains(blockStateTarget.getBlock())) {
						result.add(x, y, z,
							blockPosTarget.getX() + 0.5D - offset.x,
							blockPosTarget.getY() + 0.5D - offset.y,
							blockPosTarget.getZ() + 0.5D - offset.z,
							true, "Impassable block " + blockStateTarget + " detected at destination (" + blockPosTarget.getX() + " " + blockPosTarget.getY() + " " + blockPosTarget.getZ() + ")");
						if (!fullCollisionDetails) {
							return result;
						} else if (WarpDriveConfig.LOGGING_JUMP) {
							WarpDrive.logger.info("Anchor collision at " + context);
						}
					}
					
					if ( blockStateSource != Blocks.AIR
					  && !Dictionary.BLOCKS_EXPANDABLE.contains(blockStateSource.getBlock())
					  && blockStateTarget != Blocks.AIR
					  && !Dictionary.BLOCKS_EXPANDABLE.contains(blockStateTarget.getBlock())) {
						result.add(x, y, z,
							blockPosTarget.getX() + 0.5D + offset.x * 0.1D,
							blockPosTarget.getY() + 0.5D + offset.y * 0.1D,
							blockPosTarget.getZ() + 0.5D + offset.z * 0.1D,
							true, "Obstacle " + blockStateTarget + " detected at (" + blockPosTarget.getX() + " " + blockPosTarget.getY() + " " + blockPosTarget.getZ() + ")");
						if (!fullCollisionDetails) {
							return result;
						} else if (WarpDriveConfig.LOGGING_JUMP) {
							WarpDrive.logger.info("Hard collision at " + context);
						}
					}
					
					if (blockStateSource != Blocks.AIR && WarpDrive.proxy.isBlockPlaceCanceled(null, ship.core,
						targetWorld, blockPosTarget, blockStateSource)) {
						result.add(x, y, z, blockPosTarget.getX(), blockPosTarget.getZ(), blockPosTarget.getZ(),
							false, "Ship is entering a protected area");
						return result;
					}
				}
			}
		}
		
		if (fullCollisionDetails && result.isCollision) {
			return result;
		} else {
			return null;
		}
	}
	
	private CheckMovementResult checkMovement(final double ratio, final boolean fullCollisionDetails) {
		CheckMovementResult result = new CheckMovementResult();
		VectorI testMovement = getMovementVector(ratio);
		if ((moveY > 0 && ship.maxY + testMovement.y > 255) && !betweenWorlds) {
			result.add(ship.core.getX(), ship.maxY + testMovement.y,
				ship.core.getZ(), ship.core.getX() + 0.5D,
				ship.maxY + testMovement.y + 1.0D,
				ship.core.getZ() + 0.5D,
				false, "Ship core is moving too high");
			return result;
		}
		
		if ((moveY < 0 && ship.minY + testMovement.y <= 8) && !betweenWorlds) {
			result.add(ship.core.getX(), ship.minY + testMovement.y, ship.core.getZ(),
				ship.core.getX() + 0.5D,
				ship.maxY + testMovement.y,
				ship.core.getZ() + 0.5D,
				false, "Ship core is moving too low");
			return result;
		}
		
		ITransformation testTransformation = new Transformation(ship, targetWorld, testMovement.x, testMovement.y, testMovement.z, rotationSteps);
		return checkCollisionAndProtection(testTransformation, fullCollisionDetails, "ratio " + ratio + " testMovement " + testMovement);
	}
	
	private VectorI getMovementVector(final double ratio) {
		return new VectorI((int)Math.round(moveX * ratio), (int)Math.round(moveY * ratio), (int)Math.round(moveZ * ratio));
	}
	
	private static List<TileEntity> removeDuplicates(List<TileEntity> l) {
		Set<TileEntity> s = new TreeSet<>(new Comparator<TileEntity>() {
			@Override
			public int compare(TileEntity o1, TileEntity o2) {
				if (o1.getPos().getX() == o2.getPos().getX() && o1.getPos().getY() == o2.getPos().getY() && o1.getPos().getZ() == o2.getPos().getZ()) {
					if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
						WarpDrive.logger.info("Removed duplicated TE: " + o1 + " vs " + o2);
					}
					return 0;
				} else {
					return 1;
				}
			}
		});
		s.addAll(l);
		List<TileEntity> listTileEntities = new ArrayList<>();
		listTileEntities.addAll(s);
		return listTileEntities;
	}
	
	@Override
	protected void readFromNBT(NBTTagCompound nbttagcompound) {
		WarpDrive.logger.error(this + " readFromNBT()");
	}
	
	@Override
	protected NBTTagCompound writeToNBT(NBTTagCompound tag) {
		WarpDrive.logger.error(this + " writeToNBT()");
		return tag;
	}
	
	@Override
	public String toString() {
		return String.format("%s/%d \'%s\' @ \'%s\' (%d %d %d) #%d",
			getClass().getSimpleName(), hashCode(),
			(ship == null || ship.shipCore == null) ? "~NULL~" : (ship.shipCore.uuid + ":" + ship.shipCore.shipName),
			sourceWorld == null ? "~NULL~" : sourceWorld.getWorldInfo().getWorldName(),
			ship == null ? -1 : ship.core.getX(), ship == null ? -1 : ship.core.getY(), ship == null ? -1 : ship.core.getZ(),
			ticks);
	}
}
