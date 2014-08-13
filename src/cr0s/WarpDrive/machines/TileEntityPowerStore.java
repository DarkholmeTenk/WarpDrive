package cr0s.WarpDrive.machines;

import cr0s.WarpDrive.WarpDriveConfig;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityPowerStore extends WarpEnergyStoreTE implements IPeripheral
{
	
	private String[] methodArray = {
			"energy"
	};

	@Override
	public int amountToRelease(ForgeDirection d)
	{
		return getEnergyStored();
	}

	@Override
	public void releaseEnergy(int amount)
	{
		removeEnergy(amount,false);
	}
	
	@Override
	public int getMaxEnergyStored()
	{
		return WarpDriveConfig.PS_MAX_ENERGY;
	}

	
	//COMPUTER STUFF.
	@Override
	public String getType()
	{
		return "warpdrivePowerStore";
	}

	@Override
	public String[] getMethodNames()
	{
		return methodArray;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception
	{
		String fun = methodArray[method];
		if(fun == "energy")
			return getEnergyObject();
		return null;
	}

	@Override
	public void attach(IComputerAccess computer){}

	@Override
	public void detach(IComputerAccess computer){}

	@Override
	public boolean equals(IPeripheral other)
	{
		return this == other;
	}

}
