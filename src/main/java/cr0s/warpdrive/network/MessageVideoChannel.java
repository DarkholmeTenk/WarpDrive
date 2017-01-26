package cr0s.warpdrive.network;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IVideoChannel;
import cr0s.warpdrive.config.WarpDriveConfig;


public class MessageVideoChannel implements IMessage, IMessageHandler<MessageVideoChannel, IMessage> {
	private BlockPos blockPos;
	private int videoChannel;

	@SuppressWarnings("unused")
	public MessageVideoChannel() {
		// required on receiving side
	}
	
	public MessageVideoChannel(final BlockPos blockPos, final int videoChannel) {
		this.blockPos = blockPos;
		this.videoChannel = videoChannel;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		blockPos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
		videoChannel = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(blockPos.getX());
		buffer.writeInt(blockPos.getY());
		buffer.writeInt(blockPos.getZ());
		buffer.writeInt(videoChannel);
	}
	
	@SideOnly(Side.CLIENT)
	private void handle(World worldObj) {
		TileEntity tileEntity = worldObj.getTileEntity(blockPos);
		if (tileEntity != null) {
			if (tileEntity instanceof IVideoChannel) {
				((IVideoChannel) tileEntity).setVideoChannel(videoChannel);
			} else {
				WarpDrive.logger.error("Received video channel packet: (" + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ() + ") is not a valid tile entity");
			}
		} else {
			WarpDrive.logger.error("Received video channel packet: (" + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ() + ") has no tile entity");
		}
 	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageVideoChannel videoChannelMessage, MessageContext context) {
		// skip in case player just logged in
		if (Minecraft.getMinecraft().theWorld == null) {
			WarpDrive.logger.error("WorldObj is null, ignoring video channel packet");
			return null;
		}
		
		if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
			WarpDrive.logger.info("Received video channel packet: (" + videoChannelMessage.blockPos.getX() + " " + videoChannelMessage.blockPos.getY() + " " + videoChannelMessage.blockPos.getZ() + ") videoChannel '" + videoChannelMessage.videoChannel + "'");
		}
		
		videoChannelMessage.handle(Minecraft.getMinecraft().theWorld);
		
		return null;	// no response
	}
}
