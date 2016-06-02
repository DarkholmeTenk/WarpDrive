package cr0s.warpdrive.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.block.movement.TileEntityShipCore;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.config.WarpDriveConfig;

public class JumpShip {
	public World worldObj;
	public int coreX;
	public int coreY;
	public int coreZ;
	public int dx;
	public int dz;
	public int maxX;
	public int maxZ;
	public int maxY;
	public int minX;
	public int minZ;
	public int minY;
	public JumpBlock[] jumpBlocks;
	public int actualMass;
	public TileEntityShipCore shipCore;
	public List<MovingEntity> entitiesOnShip;
	
	public JumpShip() {
	}
	
	public void messageToAllPlayersOnShip(String message) {
		if (entitiesOnShip == null) {
			shipCore.messageToAllPlayersOnShip(message);
		} else {
			WarpDrive.logger.info(this + " messageToAllPlayersOnShip: " + message);
			for (MovingEntity me : entitiesOnShip) {
				if (me.entity instanceof EntityPlayer) {
					WarpDrive.addChatMessage((EntityPlayer) me.entity, "["
							+ ((shipCore != null && !shipCore.shipName.isEmpty()) ? shipCore.shipName : "WarpCore") + "] " + message);
				}
			}
		}
	}
	
	public String saveEntities() {
		String result = null;
		entitiesOnShip = new ArrayList<>();
		
		AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX + 0.99D, maxY + 0.99D, maxZ + 0.99D);
		
		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb);
		
		for (Entity entity : list) {
			if (entity == null) {
				continue;
			}
			
			String id = EntityList.getEntityString(entity);
			if (Dictionary.ENTITIES_ANCHOR.contains(id)) {
				result = "Anchor entity " + id + " detected at " + Math.floor(entity.posX) + ", " + Math.floor(entity.posY) + ", " + Math.floor(entity.posZ) + ", aborting jump...";
				// we need to continue so players are added so they can see the message...
				continue;
			}
			if (Dictionary.ENTITIES_LEFTBEHIND.contains(id)) {
				if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
					WarpDrive.logger.info("Leaving entity " + id + " behind: " + entity);
				}
				continue;
			}
			if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
				if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
					WarpDrive.logger.info("Adding entity " + id + ": " + entity);
				}
			} 
			MovingEntity movingEntity = new MovingEntity(entity);
			entitiesOnShip.add(movingEntity);
		}
		
		return result;
	}
	
	public boolean isUnlimited() {
		if (entitiesOnShip == null) {
			return false;
		}
		for (MovingEntity movingEntity : entitiesOnShip) {
			if (!(movingEntity.entity instanceof EntityPlayer)) {
				continue;
			}
			
			String playerName = ((EntityPlayer) movingEntity.entity).getDisplayName();
			for (String unlimitedName : WarpDriveConfig.SHIP_VOLUME_UNLIMITED_PLAYERNAMES) {
				if (unlimitedName.equals(playerName)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void setMinMaxes(int minXV, int maxXV, int minYV, int maxYV, int minZV, int maxZV) {
		minX = minXV;
		maxX = maxXV;
		minY = minYV;
		maxY = maxYV;
		minZ = minZV;
		maxZ = maxZV;
	}
	
	@Override
	public String toString() {
		return String.format("%s/%d \'%s\' @ \'%s\' (%d %d %d)",
			getClass().getSimpleName(), hashCode(),
			shipCore == null ? "~NULL~" : (shipCore.uuid + ":" + shipCore.shipName),
			worldObj == null || worldObj.getWorldInfo() == null ? "~NULL~" : worldObj.getWorldInfo().getWorldName(),
			coreX, coreY, coreZ);
	}
	
	public boolean checkBorders(StringBuilder reason) {
		// Abort jump if blocks with TE are connecting to the ship (avoid crash when splitting multi-blocks)
		for (int x = minX - 1; x <= maxX + 1; x++) {
			boolean xBorder = (x == minX - 1) || (x == maxX + 1);
			for (int z = minZ - 1; z <= maxZ + 1; z++) {
				boolean zBorder = (z == minZ - 1) || (z == maxZ + 1);
				for (int y = minY - 1; y <= maxY + 1; y++) {
					boolean yBorder = (y == minY - 1) || (y == maxY + 1);
					if ((y < 0) || (y > 255)) {
						continue;
					}
					if (!(xBorder || yBorder || zBorder)) {
						continue;
					}
					
					Block block = worldObj.getBlock(x, y, z);
					
					// Skipping any air block & ignored blocks
					if (worldObj.isAirBlock(x, y, z) || Dictionary.BLOCKS_LEFTBEHIND.contains(block)) {
						continue;
					}
					
					// Skipping non-movable blocks
					if (Dictionary.BLOCKS_ANCHOR.contains(block)) {
						continue;
					}
					
					// Skipping blocks without tile entities
					TileEntity tileEntity = worldObj.getTileEntity(x, y, z);
					if (tileEntity == null) {
						continue;
					}
					
					reason.append("Ship snagged by " + block.getLocalizedName() + " at " + x + ", " + y + ", " + z + ". Damage report pending...");
					worldObj.createExplosion((Entity) null, x, y, z, Math.min(4F * 30, 4F * (jumpBlocks.length / 50)), false);
					return false;
				}
			}
		}
		
		return true;
	}

	/**
	 * Saving ship to memory
	 */
	public boolean save(StringBuilder reason) {
		try {
			int estimatedVolume = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
			JumpBlock[][] placeTimeJumpBlocks = { new JumpBlock[estimatedVolume], new JumpBlock[estimatedVolume], new JumpBlock[estimatedVolume], new JumpBlock[estimatedVolume], new JumpBlock[estimatedVolume] };
			int[] placeTimeIndexes = { 0, 0, 0, 0, 0 }; 
			
			int actualVolume = 0;
			int newMass = 0;
			int xc1 = minX >> 4;
			int xc2 = maxX >> 4;
			int zc1 = minZ >> 4;
			int zc2 = maxZ >> 4;
			
			for (int xc = xc1; xc <= xc2; xc++) {
				int x1 = Math.max(minX, xc << 4);
				int x2 = Math.min(maxX, (xc << 4) + 15);
				
				for (int zc = zc1; zc <= zc2; zc++) {
					int z1 = Math.max(minZ, zc << 4);
					int z2 = Math.min(maxZ, (zc << 4) + 15);
					
					for (int y = minY; y <= maxY; y++) {
						for (int x = x1; x <= x2; x++) {
							for (int z = z1; z <= z2; z++) {
								Block block = worldObj.getBlock(x, y, z);
								
								// Skipping vanilla air & ignored blocks
								if (block == Blocks.air || Dictionary.BLOCKS_LEFTBEHIND.contains(block)) {
									continue;
								}
								actualVolume++;
								
								if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
									WarpDrive.logger.info("Block(" + x + " " + y + " " + z + ") is " + block.getUnlocalizedName() + "@" + worldObj.getBlockMetadata(x, y, z));
								}
								
								if (!Dictionary.BLOCKS_NOMASS.contains(block)) {
									newMass++;
								}
								
								// Stop on non-movable blocks
								if (Dictionary.BLOCKS_ANCHOR.contains(block)) {
									reason.append(block.getUnlocalizedName() + " detected onboard at " + x + " " + y + " " + z + ". Aborting.");
									return false;
								}
								
								int blockMeta = worldObj.getBlockMetadata(x, y, z);
								TileEntity tileEntity = worldObj.getTileEntity(x, y, z);
								JumpBlock jumpBlock = new JumpBlock(block, blockMeta, tileEntity, x, y, z);
								
								if (jumpBlock.blockTileEntity != null && jumpBlock.externals != null) {
									for (Entry<String, NBTBase> external : jumpBlock.externals.entrySet()) {
										IBlockTransformer blockTransformer = WarpDriveConfig.blockTransformers.get(external.getKey());
										if (blockTransformer != null) {
											if (!blockTransformer.isJumpReady(jumpBlock.block, jumpBlock.blockMeta, jumpBlock.blockTileEntity, reason)) {
												reason.append(" Jump aborted by " + jumpBlock.block.getLocalizedName() + " at " + jumpBlock.x + " " + jumpBlock.y + " " + jumpBlock.z);
												return false;
											}
										}
									}
								}
								
								// default priority is 2 for block, 3 for tile entities
								Integer placeTime = Dictionary.BLOCKS_PLACE.get(block);
								if (placeTime == null) {
									if (tileEntity == null) {
										placeTime = 2;
									} else {
										placeTime = 3;
									}
								}
								
								placeTimeJumpBlocks[placeTime][placeTimeIndexes[placeTime]] = jumpBlock;
								placeTimeIndexes[placeTime]++;
							}
						}
					}
				}
			}
			
			jumpBlocks = new JumpBlock[actualVolume];
			int indexShip = 0;
			for (int placeTime = 0; placeTime < 5; placeTime++) {
				for (int placeTimeIndex = 0; placeTimeIndex < placeTimeIndexes[placeTime]; placeTimeIndex++) {
					jumpBlocks[indexShip] = placeTimeJumpBlocks[placeTime][placeTimeIndex];
					indexShip++;
				}
			}
			actualMass = newMass;
		} catch (Exception exception) {
			exception.printStackTrace();
			reason.append("Exception while saving ship");
			return false;
		}
		
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Ship saved as " + jumpBlocks.length + " blocks");
		}
		return true;
	}
}