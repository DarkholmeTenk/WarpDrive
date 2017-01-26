package cr0s.warpdrive.network;

import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.CloakedArea;
import cr0s.warpdrive.data.Vector3;

public class PacketHandler {
	private static final SimpleNetworkWrapper simpleNetworkManager = NetworkRegistry.INSTANCE.newSimpleChannel(WarpDrive.MODID);
	private static Method EntityTrackerEntry_getPacketForThisEntity;
	
	public static void init() {
		// Forge packets
		simpleNetworkManager.registerMessage(MessageBeamEffect.class   , MessageBeamEffect.class   , 0, Side.CLIENT);
		simpleNetworkManager.registerMessage(MessageVideoChannel.class , MessageVideoChannel.class , 1, Side.CLIENT);
		simpleNetworkManager.registerMessage(MessageCloak.class        , MessageCloak.class        , 2, Side.CLIENT);
		simpleNetworkManager.registerMessage(MessageSpawnParticle.class, MessageSpawnParticle.class, 3, Side.CLIENT);
		
		simpleNetworkManager.registerMessage(MessageTargeting.class    , MessageTargeting.class    , 100, Side.SERVER);
		
		// Entity packets for 'uncloaking' entities
		try {
			EntityTrackerEntry_getPacketForThisEntity = ReflectionHelper.findMethod(
				EntityTrackerEntry.class, null, new String[] { "createSpawnPacket", "func_151260_c" } );
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
	
	// Beam effect sent to client side
	public static void sendBeamPacket(World worldObj, Vector3 source, Vector3 target, float red, float green, float blue, int age, int energy, int radius) {
		assert(!worldObj.isRemote);
		
		MessageBeamEffect messageBeamEffect = new MessageBeamEffect(source, target, red, green, blue, age, energy);
		
		// small beam are sent relative to beam center
		if (source.distanceTo_square(target) < 3600 /* 60 * 60 */) {
			simpleNetworkManager.sendToAllAround(messageBeamEffect, new TargetPoint(
					worldObj.provider.getDimension(), (source.x + target.x) / 2, (source.y + target.y) / 2, (source.z + target.z) / 2, radius));
		} else {// large beam are sent from both ends
			if (true) {
				List<EntityPlayerMP> playerEntityList = worldObj.getMinecraftServer().getPlayerList().getPlayerList();
				int dimensionId = worldObj.provider.getDimension();
				int radius_square = radius * radius;
				for (int index = 0; index < playerEntityList.size(); index++) {
					EntityPlayerMP entityplayermp = playerEntityList.get(index);
					
					if (entityplayermp.dimension == dimensionId) {
						Vector3 player = new Vector3(entityplayermp);
						if (source.distanceTo_square(player) < radius_square || target.distanceTo_square(player) < radius_square) {
							simpleNetworkManager.sendTo(messageBeamEffect, entityplayermp);
						}
					}
				}
			} else {
				simpleNetworkManager.sendToAllAround(messageBeamEffect, new TargetPoint(
						worldObj.provider.getDimension(), source.x, source.y, source.z, radius));
				simpleNetworkManager.sendToAllAround(messageBeamEffect, new TargetPoint(
						worldObj.provider.getDimension(), target.x, target.y, target.z, radius));
			}
		}
	}
	
	public static void sendBeamPacketToPlayersInArea(World worldObj, Vector3 source, Vector3 target, float red, float green, float blue, int age, int energy, AxisAlignedBB aabb) {
		assert(!worldObj.isRemote);
		
		MessageBeamEffect messageBeamEffect = new MessageBeamEffect(source, target, red, green, blue, age, energy);
		// Send packet to all players within cloaked area
		List<Entity> list = worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, aabb);
		for (Entity entity : list) {
			if (entity != null && entity instanceof EntityPlayerMP) {
				PacketHandler.simpleNetworkManager.sendTo(messageBeamEffect, (EntityPlayerMP) entity);
			}
		}
	}
	
	// Forced particle effect sent to client side
	public static void sendSpawnParticlePacket(World worldObj, final String type, final Vector3 origin, final Vector3 direction,
	                                           final float baseRed, final float baseGreen, final float baseBlue,
	                                           final float fadeRed, final float fadeGreen, final float fadeBlue, final int radius) {
		assert(!worldObj.isRemote);
		
		MessageSpawnParticle messageSpawnParticle = new MessageSpawnParticle(type, origin, direction, baseRed, baseGreen, baseBlue, fadeRed, fadeGreen, fadeBlue);
		
		// small beam are sent relative to beam center
		simpleNetworkManager.sendToAllAround(messageSpawnParticle, new TargetPoint(
				worldObj.provider.getDimension(), origin.x, origin.y, origin.z, radius));
		
		if (WarpDriveConfig.LOGGING_EFFECTS) {
			WarpDrive.logger.info("Sent particle effect '" + type + "' from " + origin + " toward " + direction
				+ " as RGB " + baseRed + " " + baseGreen + " " + baseBlue + " fading to " + fadeRed + " " + fadeGreen + " " + fadeBlue);
		}
	}
	
	// Monitor/Laser/Camera updating its video channel to client side
	public static void sendVideoChannelPacket(int dimensionId, BlockPos blockPos, int videoChannel) {
		MessageVideoChannel messageVideoChannel = new MessageVideoChannel(blockPos, videoChannel);
		simpleNetworkManager.sendToAllAround(messageVideoChannel, new TargetPoint(dimensionId, blockPos.getX(),blockPos.getY(), blockPos.getZ(), 100));
		if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
			WarpDrive.logger.info("Sent video channel packet (" + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ() + ") video channel " + videoChannel);
		}
	}
	
	// LaserCamera shooting at target (client -> server)
	public static void sendLaserTargetingPacket(int x, int y, int z, float yaw, float pitch) {
		MessageTargeting messageTargeting = new MessageTargeting(x, y, z, yaw, pitch);
		simpleNetworkManager.sendToServer(messageTargeting);
		if (WarpDriveConfig.LOGGING_TARGETING) {
			WarpDrive.logger.info("Sent targeting packet (" + x + " " + y + " " + z + ") yaw " + yaw + " pitch " + pitch);
		}
	}
	
	// Sending cloaking area definition (server -> client)
	public static void sendCloakPacket(EntityPlayer player, CloakedArea area, final boolean decloak) {
		MessageCloak messageCloak = new MessageCloak(area, decloak);
		simpleNetworkManager.sendTo(messageCloak, (EntityPlayerMP) player);
		if (WarpDriveConfig.LOGGING_CLOAKING) {
			WarpDrive.logger.info("Sent cloak packet (area " + area + " decloak " + decloak + ")");
		}
	}
	
	public static Packet getPacketForThisEntity(Entity entity) {
		EntityTrackerEntry entry = new EntityTrackerEntry(entity, 0, 0, 0, false);
		try {
			return (Packet) EntityTrackerEntry_getPacketForThisEntity.invoke(entry);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}
}