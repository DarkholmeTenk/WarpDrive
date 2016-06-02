package cr0s.warpdrive.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class SpaceTeleporter extends Teleporter {
	final int x;
	final int y;
	final int z;
	final int orientation;
	final World world;
	
	public SpaceTeleporter(WorldServer worldServer, int orientation, int x, int y, int z) {
		super(worldServer);
		this.orientation = orientation;
		this.x = x;
		this.y = y;
		this.z = z;
		world = worldServer;
	}
	
	@Override
	public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8) {
		//EntityPlayer player = (EntityPlayer) par1Entity;
		//player.setWorld(world);
		//player.setPositionAndUpdate(x, y, z);
	}
	
	@Override
	public boolean placeInExistingPortal(Entity par1Entity, double par2, double par4, double par6, float par8) {
		return true;
	}
	
	@Override
	public boolean makePortal(Entity par1Entity) {
		return true;
	}
	
	@Override
	public void removeStalePortalLocations(long p_85189_1_) {
		// do nothing
	}
}
