package cr0s.warpdrive.block;

import java.util.Map;

import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.Optional;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import cr0s.warpdrive.api.IUpgradable;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.UpgradeType;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class TileEntityChunkLoader extends TileEntityAbstractChunkLoading implements IUpgradable {
	
	private boolean canLoad = false;
	private boolean shouldLoad = false;
	
	private boolean initialised = false;
	private ChunkPos myChunk;
	
	int negDX, posDX, negDZ, posDZ;
	int area = 1;
	
	public TileEntityChunkLoader() {
		super();
		IC2_sinkTier = 2;
		IC2_sourceTier = 2;
		negDX = 0;
		negDZ = 0;
		posDX = 0;
		posDZ = 0;
		peripheralName = "warpdriveChunkloader";
		addMethods(new String[] {
				"radius",
				"bounds",
				"active",
				"upgrades"
		});
	}
	
	@Override
	public int getMaxEnergyStored() {
		return WarpDriveConfig.CL_MAX_ENERGY;
	}
	
	@Override
	public boolean shouldChunkLoad()
	{
		return shouldLoad && canLoad;
	}
	
	@Override
	public void update()
	{
		super.update();
		
		if(!initialised)
		{
			initialised = true;
			myChunk = worldObj.getChunkFromBlockCoords(pos).getChunkCoordIntPair();
			changedDistance();
		}
		
		if(shouldLoad)
		{
			canLoad = consumeEnergy(area * WarpDriveConfig.CL_RF_PER_CHUNKTICK, false);
		}
		else
		{
			canLoad = consumeEnergy(area * WarpDriveConfig.CL_RF_PER_CHUNKTICK, true);
		}
	}
	
	private void changedDistance()
	{
		if(worldObj == null) {
			return;
		}
		if (myChunk == null) {
			Chunk aChunk = worldObj.getChunkFromBlockCoords(pos);
			myChunk = aChunk.getChunkCoordIntPair();
		}
		negDX = - clamp(0, WarpDriveConfig.CL_MAX_DISTANCE, negDX);
		posDX =   clamp(0, WarpDriveConfig.CL_MAX_DISTANCE, posDX);
		negDZ = - clamp(0, WarpDriveConfig.CL_MAX_DISTANCE, negDZ);
		posDZ =   clamp(0, WarpDriveConfig.CL_MAX_DISTANCE, posDZ);
		minChunk = new ChunkPos(myChunk.chunkXPos + negDX, myChunk.chunkZPos + negDZ);
		maxChunk = new ChunkPos(myChunk.chunkXPos + posDX, myChunk.chunkZPos + posDZ);
		area = (posDX - negDX + 1) * (posDZ - negDZ + 1);
		refreshLoading(true);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		negDX  = nbt.getInteger("negDX");
		negDZ  = nbt.getInteger("negDZ");
		posDX  = nbt.getInteger("posDX");
		posDZ  = nbt.getInteger("posDZ");

		changedDistance();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setInteger("negDX", negDX);
		tag.setInteger("negDZ", negDZ);
		tag.setInteger("posDX", posDX);
		tag.setInteger("posDZ", posDZ);
		return tag;
	}

	// OpenComputer callback methods
	// FIXME: implement OpenComputers...

	@Override
	@Optional.Method(modid = "ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) {
		String methodName = getMethodName(method);
		
		switch (methodName) {
			case "radius":
				if (arguments.length == 1) {
					int dist = toInt(arguments[0]);
					negDX = dist;
					negDZ = dist;
					posDX = dist;
					posDZ = dist;
					changedDistance();
					return new Object[]{true};
				}
				return new Object[]{false};
			case "bounds":
				if (arguments.length == 4) {
					negDX = toInt(arguments[0]);
					posDX = toInt(arguments[1]);
					negDZ = toInt(arguments[2]);
					posDZ = toInt(arguments[3]);
					changedDistance();
				}
				return new Object[]{negDX, posDX, negDZ, posDZ};
			case "active":
				if (arguments.length == 1)
					shouldLoad = toBool(arguments[0]);
				return new Object[]{shouldChunkLoad()};
			case "upgrades":
				return getUpgrades_deprecated();
		}
		
		return super.callMethod(computer, context, method, arguments);
	}

	@Override
	public boolean takeUpgrade(UpgradeType upgradeType, boolean simulate)
	{
		int max = 0;
		if(upgradeType == UpgradeType.Energy)
			max = 2;
		else if(upgradeType == UpgradeType.Power)
			max = 2;

		if(max == 0)
			return false;

		if(deprecated_upgrades.containsKey(upgradeType))
			if(deprecated_upgrades.get(upgradeType) >= max)
				return false;

		if(!simulate)
		{
			int c = 0;
			if(deprecated_upgrades.containsKey(upgradeType))
				c = deprecated_upgrades.get(upgradeType);
			deprecated_upgrades.put(upgradeType, c+1);
		}
		return true;
	}

	@Override
	public Map<UpgradeType, Integer> getInstalledUpgrades()
	{
		return deprecated_upgrades;
	}
}
