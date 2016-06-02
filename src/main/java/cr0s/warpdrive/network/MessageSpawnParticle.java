package cr0s.warpdrive.network;

import java.nio.charset.StandardCharsets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.Vector3;


public class MessageSpawnParticle implements IMessage, IMessageHandler<MessageSpawnParticle, IMessage> {
	
	private String type;
	private Vector3 origin;
	private Vector3 direction;
	private float red;
	private float green;
	private float blue;
	
	public MessageSpawnParticle() {
		// required on receiving side
	}
	
	public MessageSpawnParticle(final String type, final Vector3 origin, final Vector3 direction, final float red, final float green, final float blue) {
		this.type = type;
		this.origin = origin;
		this.direction = direction;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		int typeSize = buffer.readByte();
		type = buffer.toString(buffer.readerIndex(), typeSize, StandardCharsets.US_ASCII);
		buffer.skipBytes(typeSize);
		
		double x = buffer.readDouble();
		double y = buffer.readDouble();
		double z = buffer.readDouble();
		origin = new Vector3(x, y, z);
		
		x = buffer.readDouble();
		y = buffer.readDouble();
		z = buffer.readDouble();
		direction = new Vector3(x, y, z);
		
		red = buffer.readFloat();
		green = buffer.readFloat();
		blue = buffer.readFloat();
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeByte(type.length());
		buffer.writeBytes(type.getBytes(StandardCharsets.US_ASCII), 0, type.length());
		buffer.writeDouble(origin.x);
		buffer.writeDouble(origin.y);
		buffer.writeDouble(origin.z);
		buffer.writeDouble(direction.x);
		buffer.writeDouble(direction.y);
		buffer.writeDouble(direction.z);
		buffer.writeFloat(red);
		buffer.writeFloat(green);
		buffer.writeFloat(blue);
	}
	
	@SideOnly(Side.CLIENT)
	private void handle(World worldObj) {
		// Directly spawn particle as per RenderGlobal.doSpawnParticle, bypassing range check
		// adjust color as needed
		for (int i = 0; i < 5; i++) {
			direction = new Vector3(
					direction.x + 0.05 * (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()),
					direction.y + 0.05 * (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()),
					direction.z + 0.05 * (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()));
			EntityFX effect = new EntityExplodeFX(worldObj, origin.x, origin.y, origin.z,
					direction.x, direction.y, direction.z); // TODO: implements other effects
			if (red >= 0.0F && green >= 0.0F && blue >= 0.0F) {
				effect.setRBGColorF(red, green, blue);
			}
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(effect);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageSpawnParticle messageSpawnParticle, MessageContext context) {
		// skip in case player just logged in
		if (Minecraft.getMinecraft().theWorld == null) {
			WarpDrive.logger.error("WorldObj is null, ignoring beam packet");
			return null;
		}
		
		if (WarpDriveConfig.LOGGING_EFFECTS) {
			WarpDrive.logger.info("Received particle effect '" + messageSpawnParticle.type + "' from " + messageSpawnParticle.origin + " toward " + messageSpawnParticle.direction
				+ " as RGB " + messageSpawnParticle.red + " " + messageSpawnParticle.green + " " + messageSpawnParticle.blue);
		}
		
		messageSpawnParticle.handle(Minecraft.getMinecraft().theWorld);
		
		return null;	// no response
	}
}
