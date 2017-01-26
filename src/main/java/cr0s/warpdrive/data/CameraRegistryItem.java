package cr0s.warpdrive.data;

import cr0s.warpdrive.api.IVideoChannel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CameraRegistryItem {
	public int dimensionId = -666;
	public BlockPos position = null;
	public int videoChannel = -1;
	public EnumCameraType type = null;
	
	public CameraRegistryItem(World parWorldObj, BlockPos parPosition, int parFrequency, EnumCameraType parType) {
		videoChannel = parFrequency;
		position = parPosition;
		dimensionId = parWorldObj.provider.getDimension();
		type = parType;
	}
	
	public boolean isTileEntity(TileEntity tileEntity) {
		return tileEntity != null
			&& tileEntity instanceof IVideoChannel
			&& dimensionId == tileEntity.getWorld().provider.getDimension()
			&& position.equals(tileEntity.getPos())
			&& videoChannel == ((IVideoChannel)tileEntity).getVideoChannel();
	}
}