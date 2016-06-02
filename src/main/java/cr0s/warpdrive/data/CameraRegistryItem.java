package cr0s.warpdrive.data;

import cr0s.warpdrive.api.IVideoChannel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class CameraRegistryItem {
	public int dimensionId = -666;
	public ChunkPosition position = null;
	public int videoChannel = -1;
	public CameraType type = null;
	
	public CameraRegistryItem(World parWorldObj, ChunkPosition parPosition, int parFrequency, CameraType parType) {
		videoChannel = parFrequency;
		position = parPosition;
		dimensionId = parWorldObj.provider.dimensionId;
		type = parType;
	}
	
	public boolean isTileEntity(TileEntity tileEntity) {
		return tileEntity != null
			&& tileEntity instanceof IVideoChannel
			&& dimensionId == tileEntity.getWorldObj().provider.dimensionId
			&& position.chunkPosX == tileEntity.xCoord
			&& position.chunkPosY == tileEntity.yCoord
			&& position.chunkPosZ == tileEntity.zCoord
			&& videoChannel == ((IVideoChannel)tileEntity).getVideoChannel();
	}
}