package cr0s.warpdrive.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.block.movement.TileEntityShipCore;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.config.WarpDriveConfig;
import net.minecraftforge.common.util.Constants;

public class JumpShip {
	public World worldObj;
	public BlockPos core;
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
	
	public void messageToAllPlayersOnShip(ITextComponent textComponent) {
		if (entitiesOnShip == null) {
			shipCore.messageToAllPlayersOnShip(textComponent);
		} else {
			WarpDrive.logger.info(this + " messageToAllPlayersOnShip: " + textComponent);
			for (MovingEntity movingEntity : entitiesOnShip) {
				if (movingEntity.entity instanceof EntityPlayer) {
					WarpDrive.addChatMessage(movingEntity.entity, new TextComponentString("["
						+ ((shipCore != null && !shipCore.shipName.isEmpty()) ? shipCore.shipName : "WarpCore") + "] ")
						.appendSibling(textComponent));
				}
			}
		}
	}
	
	public String saveEntities() {
		String result = null;
		entitiesOnShip = new ArrayList<>();
		
		AxisAlignedBB axisalignedbb = new AxisAlignedBB(minX, minY, minZ, maxX + 0.99D, maxY + 0.99D, maxZ + 0.99D);
		
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
			
			String playerName = movingEntity.entity.getName();
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
			worldObj == null ? "~NULL~" : worldObj.getWorldInfo().getWorldName(),
			core.getX(), core.getY(), core.getZ());
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
					BlockPos blockPos = new BlockPos(x, y, z);
					IBlockState blockState = worldObj.getBlockState(blockPos);
					
					// Skipping any air block & ignored blocks
					if (worldObj.isAirBlock(blockPos) || Dictionary.BLOCKS_LEFTBEHIND.contains(blockState.getBlock())) {
						continue;
					}
					
					// Skipping non-movable blocks
					if (Dictionary.BLOCKS_ANCHOR.contains(blockState.getBlock())) {
						continue;
					}
					
					// Skipping blocks without tile entities
					TileEntity tileEntity = worldObj.getTileEntity(blockPos);
					if (tileEntity == null) {
						continue;
					}
					
					reason.append("Ship snagged by " + blockState.getBlock().getLocalizedName() + " at " + x + " " + y + " " + z + ". Damage report pending...");
					worldObj.createExplosion(null, x, y, z, Math.min(4F * 30, 4F * (jumpBlocks.length / 50)), false);
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
		BlockPos blockPos = new BlockPos(0, -1, 0);
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
								blockPos = new BlockPos(x, y, z);
								IBlockState blockState = worldObj.getBlockState(blockPos);
								
								// Skipping vanilla air & ignored blocks
								if (blockState.getBlock() == Blocks.AIR || Dictionary.BLOCKS_LEFTBEHIND.contains(blockState.getBlock())) {
									continue;
								}
								actualVolume++;
								
								if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
									WarpDrive.logger.info("Block(" + x + " " + y + " " + z + ") is " + blockState);
								}
								
								if (!Dictionary.BLOCKS_NOMASS.contains(blockState.getBlock())) {
									newMass++;
								}
								
								// Stop on non-movable blocks
								if (Dictionary.BLOCKS_ANCHOR.contains(blockState.getBlock())) {
									reason.append(blockState.getBlock().getLocalizedName() + " detected on board at " + x + " " + y + " " + z + ". Aborting.");
									return false;
								}
								
								TileEntity tileEntity = worldObj.getTileEntity(blockPos);
								JumpBlock jumpBlock = new JumpBlock(blockState, tileEntity, blockPos);
								
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
								Integer placeTime = Dictionary.BLOCKS_PLACE.get(blockState.getBlock());
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
			reason.append("Exception while saving ship, probably a corrupted block at " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ());
			return false;
		}
		
		if (WarpDriveConfig.LOGGING_JUMP) {
			WarpDrive.logger.info(this + " Ship saved as " + jumpBlocks.length + " blocks");
		}
		return true;
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		core = new BlockPos(tag.getInteger("coreX"), tag.getInteger("coreY"), tag.getInteger("coreZ"));
		dx = tag.getInteger("dx");
		dz = tag.getInteger("dz");
		maxX = tag.getInteger("maxX");
		maxZ = tag.getInteger("maxZ");
		maxY = tag.getInteger("maxY");
		minX = tag.getInteger("minX");
		minZ = tag.getInteger("minZ");
		minY = tag.getInteger("minY");
		actualMass = tag.getInteger("actualMass");
		NBTTagList tagList = tag.getTagList("jumpBlocks", Constants.NBT.TAG_COMPOUND);
		jumpBlocks = new JumpBlock[tagList.tagCount()];
		for(int index = 0; index < tagList.tagCount(); index++) {
			jumpBlocks[index] = new JumpBlock();
			jumpBlocks[index].readFromNBT(tagList.getCompoundTagAt(index));
		}
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("coreX", core.getX());
		tag.setInteger("coreY", core.getY());
		tag.setInteger("coreZ", core.getZ());
		tag.setInteger("dx", dx);
		tag.setInteger("dz", dz);
		tag.setInteger("maxX", maxX);
		tag.setInteger("maxZ", maxZ);
		tag.setInteger("maxY", maxY);
		tag.setInteger("minX", minX);
		tag.setInteger("minZ", minZ);
		tag.setInteger("minY", minY);
		tag.setInteger("actualMass", actualMass);
		NBTTagList tagListJumpBlocks = new NBTTagList();
		for (JumpBlock jumpBlock : jumpBlocks) {
			NBTTagCompound tagCompoundBlock = new NBTTagCompound();
			jumpBlock.writeToNBT(tagCompoundBlock);
			tagListJumpBlocks.appendTag(tagCompoundBlock);
		}
		tag.setTag("jumpBlocks", tagListJumpBlocks);
	}
}