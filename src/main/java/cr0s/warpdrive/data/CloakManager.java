package cr0s.warpdrive.data;

import java.util.LinkedList;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.network.PacketHandler;


public class CloakManager {
	
	private static LinkedList<CloakedArea> cloaks;
	
	public CloakManager() {
		cloaks = new LinkedList<>();
	}
	
	public boolean isCloaked(final int dimensionID, final BlockPos blockPos) {
		for (CloakedArea area : cloaks) {
			if (area.dimensionId != dimensionID) {
				continue;
			}
			
			if ( area.minX <= blockPos.getX() && area.maxX >= blockPos.getX() 
			  && area.minY <= blockPos.getY() && area.maxY >= blockPos.getY()
			  && area.minZ <= blockPos.getZ() && area.maxZ >= blockPos.getZ() ) {
				return true;
			}
		}
		
		return false;
	}
	
	public void onChunkLoaded(EntityPlayerMP player, int chunkPosX, int chunkPosZ) {
		for (CloakedArea area : cloaks) {
			// skip other dimensions
			if (area.dimensionId != player.worldObj.provider.getDimension()) {
				continue;
			}
			
			// force refresh if the chunk overlap the cloak
			if ( area.minX <= (chunkPosX << 4 + 15) && area.maxX >= (chunkPosX << 4)
			  && area.minZ <= (chunkPosZ << 4 + 15) && area.maxZ >= (chunkPosZ << 4) ) {
				PacketHandler.sendCloakPacket(player, area, false);
			}
		}
	}
	
	public void onPlayerEnteringDimension(EntityPlayer player) {
		if (WarpDriveConfig.LOGGING_CLOAKING) { WarpDrive.logger.info("onEntityJoinWorld " + player); }
		for (CloakedArea area : cloaks) {
			// skip other dimensions
			if (area.dimensionId != player.worldObj.provider.getDimension()) {
				continue;
			}
			
			// force refresh if player is outside the cloak
			if ( area.minX > player.posX || area.maxX < player.posX
			  || area.minY > player.posY || area.maxY < player.posY
			  || area.minZ > player.posZ || area.maxZ < player.posZ ) {
				PacketHandler.sendCloakPacket(player, area, false);
			}
		}
	}
	
	public boolean isAreaExists(World world, final BlockPos blockPos) {
		return (getCloakedArea(world, blockPos) != null);
	}
	
	public void updateCloakedArea(
			World world,
			final int dimensionId, final BlockPos blockPosCore, final byte tier,
			final int minX, final int minY, final int minZ,
			final int maxX, final int maxY, final int maxZ) {
		CloakedArea newArea = new CloakedArea(world, dimensionId, blockPosCore, tier, minX, minY, minZ, maxX, maxY, maxZ);
		
		// find existing one
		int index = -1;
		for (int i = 0; i < cloaks.size(); i++) {
			CloakedArea area = cloaks.get(i);
			if ( area.dimensionId == world.provider.getDimension()
			  && area.blockPosCore.equals(blockPosCore) ) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			cloaks.set(index, newArea);
		} else {
			cloaks.add(newArea);
		}
		if (world.isRemote) {
			newArea.clientCloak();
		}
		if (WarpDriveConfig.LOGGING_CLOAKING) { WarpDrive.logger.info("Cloak count is " + cloaks.size()); }
	}
	
	public void removeCloakedArea(final int dimensionId, final BlockPos blockPos) {
		int index = -1;
		for (int i = 0; i < cloaks.size(); i++) {
			CloakedArea area = cloaks.get(i);
			if ( area.dimensionId == dimensionId
			  && area.blockPosCore.equals(blockPos) ) {
				if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
					area.clientDecloak();
				} else {
					area.sendCloakPacketToPlayersEx(true); // send info about collapsing cloaking field
				}
				index = i;
				break;
			}
		}
		
		if (index != -1) {
			cloaks.remove(index);
		}
	}
	
	public CloakedArea getCloakedArea(World world, final BlockPos blockPos) {
		for (CloakedArea area : cloaks) {
			if (area.dimensionId == world.provider.getDimension() && area.blockPosCore.equals(blockPos))
				return area;
		}
		
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public CloakedArea getCloakedArea(BlockPos blockPos) {
		// client only 
		for (CloakedArea area : cloaks) {
			if (area.blockPosCore.equals(blockPos))
				return area;
		}
		
		return null;
	}
	
	public void updatePlayer(EntityPlayer player) {
		for (CloakedArea area : cloaks) {
			area.updatePlayer(player);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static boolean onBlockChange(int x, int y, int z, Block block, int metadata, int flag) {
		if (block != Blocks.AIR && cloaks != null) {
			for (CloakedArea area : cloaks) {
				if (area.isBlockWithinArea(x, y, z)) {
					// WarpDrive.logger.info("CM block is inside");
					if (!area.isEntityWithinArea(Minecraft.getMinecraft().thePlayer)) {
						// WarpDrive.logger.info("CM player is outside");
						return Minecraft.getMinecraft().theWorld.setBlockState(new BlockPos(x, y, z), area.blockStateFog, flag);
					}
				}
			}
		}
		return Minecraft.getMinecraft().theWorld.setBlockState(new BlockPos(x, y, z), block.getStateFromMeta(metadata), flag);
	}
	
	@SideOnly(Side.CLIENT)
	public static void onFillChunk(Chunk chunk) {
		if (cloaks == null) {
			// WarpDrive.logger.info("CM onFillChunk (" + chunk.xPosition + " " + chunk.zPosition + ") no cloaks");
			return;
		}
		
		int chunkX_min = chunk.xPosition * 16;
		int chunkX_max = chunk.xPosition * 16 + 15;
		int chunkZ_min = chunk.zPosition * 16;
		int chunkZ_max = chunk.zPosition * 16 + 15;
		// WarpDrive.logger.info("CM onFillChunk (" + chunk.xPosition + " " + chunk.zPosition + ") " + cloaks.size() + " cloak(s) from (" + chunkX_min + " " + chunkZ_min + ") to (" + chunkX_max + " " + chunkZ_max + ")");
		
		for (CloakedArea area : cloaks) {
			if ( area.minX <= chunkX_max && area.maxX >= chunkX_min
			  && area.minZ <= chunkZ_max && area.maxZ >= chunkZ_min ) {
				// WarpDrive.logger.info("CM chunk is inside");
				if (!area.isEntityWithinArea(Minecraft.getMinecraft().thePlayer)) {
					// WarpDrive.logger.info("CM player is outside");
					
					int areaX_min = Math.max(chunkX_min, area.minX) & 15;
					int areaX_max = Math.min(chunkX_max, area.maxX) & 15;
					int areaZ_min = Math.max(chunkZ_min, area.minZ) & 15;
					int areaZ_max = Math.min(chunkZ_max, area.maxZ) & 15;
					
					for (int x = areaX_min; x <= areaX_max; x++) {
						for (int z = areaZ_min; z <= areaZ_max; z++) {
							for (int y = area.maxY; y >= area.minY; y--) {
								if (chunk.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.AIR) {
									chunk.setBlockState(new BlockPos(x, y, z), area.blockStateFog);
								}
								
							}
						}
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void onClientChangingDimension() {
		cloaks.clear();
	}
}
