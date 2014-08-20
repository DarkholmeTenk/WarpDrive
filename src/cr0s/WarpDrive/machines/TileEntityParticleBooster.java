package cr0s.WarpDrive.machines;

import cpw.mods.fml.common.FMLCommonHandler;
import cr0s.WarpDrive.WarpDriveConfig;

public class TileEntityParticleBooster extends WarpTE
{

    int ticks = 0;

    @Override
    public void updateEntity()
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            return;
        }

        if (++ticks > 20)
        {
            ticks = 0;
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, (10* getEnergyStored()) / getMaxEnergyStored(), 3);
        }
    }

    @Override
    public int getMaxEnergyStored()
    {
    	return WarpDriveConfig.PB_MAX_ENERGY_VALUE;
    }
}
