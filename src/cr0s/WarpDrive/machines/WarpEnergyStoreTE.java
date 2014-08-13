package cr0s.WarpDrive.machines;

import java.util.HashMap;

import cofh.api.energy.IEnergyHandler;
import cr0s.WarpDriveCore.IBlockUpdateDetector;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public abstract class WarpEnergyStoreTE extends WarpTE implements IBlockUpdateDetector //Ironically doesn't actually store energy, leaves that to the implementer
{
	private int energyStoreTickCount = -1;
	private boolean firstRun = false;
	private HashMap<ForgeDirection,IEnergyHandler> energyHandlers = new HashMap<ForgeDirection,IEnergyHandler>(6);
	
	public abstract int amountToRelease(ForgeDirection d); // how much energy can I release this time?
	public abstract void releaseEnergy(int amount); // remove energy from stores and update whatever variables you want
	
	public void scanForEnergyHandlers()
	{
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			boolean iehFound = false;
			if(canInterface(dir))
			{
				int oX = dir.offsetX;
				int oY = dir.offsetY;
				int oZ = dir.offsetZ;
				TileEntity te = worldObj.getBlockTileEntity(xCoord+oX, yCoord+oY, zCoord+oZ);
				if(te != null && te instanceof IEnergyHandler)
				{
					IEnergyHandler ieh = (IEnergyHandler)te;
					if(ieh.canInterface(dir.getOpposite()))
					{
						iehFound = true;
						energyHandlers.put(dir, ieh);
					}
				}
			}
			if(!iehFound)
			{
				if(energyHandlers.containsKey(dir))
					energyHandlers.remove(dir);
			}
		}
	}
	
	private void releaseEnergy(ForgeDirection d,IEnergyHandler ieh)
	{
		if(ieh == null || worldObj.getBlockTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ) == null)
			return;
		int relMax = amountToRelease(d);
		int relAm = ieh.receiveEnergy(d.getOpposite(), relMax, false);
		releaseEnergy(relAm);
	}
	
	private void releaseEnergy()
	{
		for(ForgeDirection dir: ForgeDirection.VALID_DIRECTIONS)
		{
			if(energyHandlers.containsKey(dir))
				releaseEnergy(dir,energyHandlers.get(dir));
		}
	}
	
	@Override
	public void updatedNeighbours()
	{
		scanForEnergyHandlers();
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(++energyStoreTickCount % 20 == 0)
		{
			energyStoreTickCount = 0;
			scanForEnergyHandlers();
		}
		releaseEnergy();
	}
	
	@Override
	public int extractEnergy(ForgeDirection d,int amount,boolean sim)
	{
		if(!canInterface(d))
			return 0;
		
		int amountRelease = Math.min(amount, amountToRelease(d));
		if(!sim)
			releaseEnergy(amountRelease);
		return amountRelease;
	}
}
