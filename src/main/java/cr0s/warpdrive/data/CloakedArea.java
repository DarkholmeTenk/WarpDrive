package cr0s.warpdrive.data;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.network.PacketHandler;
import cr0s.warpdrive.render.EntityFXBeam;

@SuppressWarnings("Convert2Diamond")
public class CloakedArea {
	public int dimensionId = -666;
	public BlockPos blockPosCore;
	public int minX, minY, minZ;
	public int maxX, maxY, maxZ;
	private LinkedList<UUID> playersInArea;
	public byte tier = 0;
	public IBlockState blockStateFog;
	
	public CloakedArea(World worldObj,
			final int dimensionId, final BlockPos blockPosCore, final byte tier,
			final int minX, final int minY, final int minZ,
			final int maxX, final int maxY, final int maxZ) {
		this.dimensionId = dimensionId;
		this.blockPosCore = blockPosCore;
		this.tier = tier;
		
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		
		this.playersInArea = new LinkedList<>();
		
		if (worldObj != null) {
			try {
				// Add all players currently inside the field
				List<EntityPlayer> list = worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
				for (EntityPlayer player : list) {
					addPlayer(player.getUniqueID());
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		
		if (tier == 1) {
			blockStateFog = WarpDrive.blockGas.getStateFromMeta(5);
		} else {
			blockStateFog = Blocks.AIR.getDefaultState();
		}
	}
	
	public boolean isPlayerListedInArea(final UUID uniqueId) {
		for (UUID playerInArea : playersInArea) {
			if (playerInArea.equals(uniqueId)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void removePlayer(final UUID uniqueId) {
		for (int i = 0; i < playersInArea.size(); i++) {
			if (playersInArea.get(i).equals(uniqueId)) {
				playersInArea.remove(i);
				return;
			}
		}
	}
	
	private void addPlayer(final UUID uniqueId) {
		if (!isPlayerListedInArea(uniqueId)) {
			playersInArea.add(uniqueId);
		}
	}
	
	public boolean isEntityWithinArea(EntityLivingBase entity) {
		return (minX <= entity.posX && (maxX + 1) > entity.posX
			 && minY <= (entity.posY + entity.height) && (maxY + 1) > entity.posY
			 && minZ <= entity.posZ && (maxZ + 1) > entity.posZ);
	}
	
	public boolean isBlockWithinArea(final int x, final int y, final int z) {
		return (minX <= x && (maxX + 1) > x
			 && minY <= y && (maxY + 1) > y
			 && minZ <= z && (maxZ + 1) > z);
	}
	
	// Sending only if field changes: sets up or collapsing
	public void sendCloakPacketToPlayersEx(final boolean decloak) {
		if (WarpDriveConfig.LOGGING_CLOAKING) {
			WarpDrive.logger.info("sendCloakPacketToPlayersEx " + decloak);
		}
		final int RADIUS = 250;
		
		double midX = minX + (Math.abs(maxX - minX) / 2.0D);
		double midY = minY + (Math.abs(maxY - minY) / 2.0D);
		double midZ = minZ + (Math.abs(maxZ - minZ) / 2.0D);
		
		for (EntityPlayerMP entityPlayerMP : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList()) {
			if (entityPlayerMP.dimension == dimensionId) {
				double dX = midX - entityPlayerMP.posX;
				double dY = midY - entityPlayerMP.posY;
				double dZ = midZ - entityPlayerMP.posZ;
				
				if (Math.abs(dX) < RADIUS && Math.abs(dY) < RADIUS && Math.abs(dZ) < RADIUS) {
					if (decloak) {
						revealChunksToPlayer(entityPlayerMP);
						revealEntitiesToPlayer(entityPlayerMP);
					}
					
					if (!isEntityWithinArea(entityPlayerMP) && !decloak) {
						PacketHandler.sendCloakPacket(entityPlayerMP, this, false);
					} else if (decloak) {
						PacketHandler.sendCloakPacket(entityPlayerMP, this, true);
					}
				}
			}
		}
	}
	
	public void updatePlayer(EntityPlayer player) {
		if (isEntityWithinArea(player)) {
			if (!isPlayerListedInArea(player.getUniqueID())) {
				if (WarpDriveConfig.LOGGING_CLOAKING) {
					WarpDrive.logger.info(this + " Player " + player.getUniqueID() + " has entered");
				}
				addPlayer(player.getUniqueID());
				revealChunksToPlayer(player);
				revealEntitiesToPlayer(player);
				PacketHandler.sendCloakPacket(player, this, false);
			}
		} else {
			if (isPlayerListedInArea(player.getUniqueID())) {
				if (WarpDriveConfig.LOGGING_CLOAKING) {
					WarpDrive.logger.info(this + " Player " + player.getUniqueID() + " has left");
				}
				removePlayer(player.getUniqueID());
				player.getEntityWorld().getMinecraftServer().getPlayerList()
						.sendToAllNearExcept(player, player.posX, player.posY, player.posZ, 100, player.worldObj.provider.getDimension(),
								PacketHandler.getPacketForThisEntity(player));
				PacketHandler.sendCloakPacket(player, this, false);
			}
		}
	}
	
	public void revealChunksToPlayer(EntityPlayer player) {
		if (WarpDriveConfig.LOGGING_CLOAKING) {
			 WarpDrive.logger.info(this + " Revealing cloaked blocks to player " + player.getDisplayNameString());
		}
		int minY_clamped = Math.max(0, minY);
		int maxY_clamped = Math.min(255, maxY);
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				for (int y = minY_clamped; y <= maxY_clamped; y++) {
					BlockPos blockPos = new BlockPos(x, y, z);
					IBlockState blockState = player.worldObj.getBlockState(blockPos);
					if (blockState.getBlock() != Blocks.AIR) {
						player.worldObj.notifyBlockUpdate(blockPos, blockState, blockState, 3);
						
						JumpBlock.refreshBlockStateOnClient(player.worldObj, new BlockPos(x, y, z));
					}
				}
			}
		}
		
		/*
		ArrayList<Chunk> chunksToSend = new ArrayList<Chunk>();
		
		for (int x = minX >> 4; x <= maxX >> 4; x++) {
			for (int z = minZ >> 4; z <= maxZ >> 4; z++) {
				chunksToSend.add(p.worldObj.getChunkFromChunkCoords(x, z));
			}
		}
		
		//System.out.println("[Cloak] Sending " + chunksToSend.size() + " chunks to player " + p.username);
		((EntityPlayerMP) p).connection.sendPacketToPlayer(new Packet56MapChunks(chunksToSend));
		
		//System.out.println("[Cloak] Sending decloak packet to player " + p.username);
		area.sendCloakPacketToPlayer(p, true);
		// decloak = true
		
		/**/
	}
	
	public void revealEntitiesToPlayer(EntityPlayer player) {
		List<Entity> list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
		
		for (Entity entity : list) {
			Packet packet = PacketHandler.getPacketForThisEntity(entity);
			if (packet != null) {
				if (WarpDriveConfig.LOGGING_CLOAKING) {
					WarpDrive.logger.warn("Revealing entity " + entity + " with packet " + packet);
				}
				((EntityPlayerMP) player).connection.sendPacket(packet);
			} else if (WarpDriveConfig.LOGGING_CLOAKING) {
				WarpDrive.logger.warn("Revealing entity " + entity + " fails: null packet");
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void clientCloak() {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		// Hide the blocks within area
		if (WarpDriveConfig.LOGGING_CLOAKING) { WarpDrive.logger.info("Refreshing cloaked blocks..."); }
		World worldObj = player.worldObj;
		int minY_clamped = Math.max(0, minY);
		int maxY_clamped = Math.min(255, maxY);
		for (int y = minY_clamped; y <= maxY_clamped; y++) {
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockPos blockPos = new BlockPos(x, y, z);
					IBlockState blockState = worldObj.getBlockState(blockPos);
					if (blockState.getBlock() != Blocks.AIR) {
						worldObj.setBlockState(blockPos, blockStateFog, 4);
					}
				}
			}
		}
		
		// Hide any entities inside area
		if (WarpDriveConfig.LOGGING_CLOAKING) { WarpDrive.logger.info("Refreshing cloaked entities..."); }
		AxisAlignedBB aabb = new AxisAlignedBB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(player, aabb);
		for (Entity entity : list) {
			worldObj.removeEntity(entity);
			((WorldClient) worldObj).removeEntityFromWorld(entity.getEntityId());
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void clientDecloak() {
		World worldObj = Minecraft.getMinecraft().theWorld;
		worldObj.markBlockRangeForRenderUpdate(minX - 1, Math.max(0, minY - 1), minZ - 1, maxX + 1, Math.min(255, maxY + 1), maxZ + 1);

		// Make some graphics
		int numLasers = 80 + worldObj.rand.nextInt(50);
		
		double centerX = (minX + maxX) / 2.0D;
		double centerY = (minY + maxY) / 2.0D;
		double centerZ = (minZ + maxZ) / 2.0D;
		double radiusX = (maxX - minX) / 2.0D + 5.0D;
		double radiusY = (maxY - minY) / 2.0D + 5.0D;
		double radiusZ = (maxZ - minZ) / 2.0D + 5.0D;
		
		for (int i = 0; i < numLasers; i++) {
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(new EntityFXBeam(worldObj,
				new Vector3(
					centerX + radiusX * worldObj.rand.nextGaussian(),
					centerY + radiusY * worldObj.rand.nextGaussian(),
					centerZ + radiusZ * worldObj.rand.nextGaussian()),
				new Vector3(
					centerX + radiusX * worldObj.rand.nextGaussian(),
					centerY + radiusY * worldObj.rand.nextGaussian(),
					centerZ + radiusZ * worldObj.rand.nextGaussian()),
				worldObj.rand.nextFloat(), worldObj.rand.nextFloat(), worldObj.rand.nextFloat(),
				60 + worldObj.rand.nextInt(60), 100));
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s @ DIM%d (%d %d %d) (%d %d %d) -> (%d %d %d)",
			getClass().getSimpleName(), dimensionId,
			blockPosCore.getX(), blockPosCore.getY(), blockPosCore.getZ(),
			minX, minY, minZ,
			maxX, maxY, maxZ);
	}
}
