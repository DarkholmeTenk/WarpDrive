package cr0s.WarpDrive.machines;

import cpw.mods.fml.common.FMLCommonHandler;
import cr0s.WarpDrive.Vector3;
import cr0s.WarpDrive.WarpDrive;
import cr0s.WarpDrive.WarpDriveConfig;

import java.util.List;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityLift extends TileEntityAbstractLaser implements IPeripheral
{

    private int mode = 0; // 0 - inactive, 1 - up, 2 - down
    private int firstUncoveredY;
    private Vector3 firstUncoveredYVec;
    private Vector3 myVector;

    private boolean isEnabled = false;
    private boolean computerDisabled = false;
    private int computerModeForce = -1;
    
    private String[] methodsArray = {
    		"energy",
    		"mode",
    		"active",
    		"help"
    };

    int ticks = 0;

    private void sendLaser(float r,float g,float b, int age, int energy, int rad)
    {
    	sendLaserPacket(myVector,firstUncoveredYVec,r,g,b,age,energy,rad);
    }
    
    @Override
    public void updateEntity()
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            return;
        }
        
        if (++ticks % WarpDriveConfig.LL_TICK_RATE == 0)
        {
        	liftEntity();
            ticks = 0;

            // Switching mode
            if (computerModeForce == 2 || (computerModeForce == -1 && worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)))
            {
                mode = 2; // down
            }
            else
            {
                mode = 1; // up
            }

            isEnabled = ((!computerDisabled) && worldObj.isAirBlock(xCoord, yCoord + 1, zCoord) && worldObj.isAirBlock(xCoord, yCoord + 2, zCoord) && worldObj.isAirBlock(xCoord, yCoord-1, zCoord));

            if (getEnergyStored() < WarpDriveConfig.LL_LIFT_ENERGY || !isEnabled)
            {
                mode = 0;
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 2);    // disabled
                return;
            }

            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, mode, 2); // current mode

            // Launch a beam
            if (isEnabled)
            {
                // Search non-air blocks under lift
                for (int ny = yCoord - 1; ny > 0; ny--)
                {
                    if (!worldObj.isAirBlock(xCoord, ny, zCoord))
                    {
                        firstUncoveredY = ny;
                        myVector = new Vector3(this).translate(0.5);
                        firstUncoveredYVec = new Vector3(xCoord,ny,zCoord).translate(0.5);
                        break;
                    }
                }

                if (yCoord - firstUncoveredY > 0)
                    if (mode == 1)
                    {
                    	sendLaser(0f,1f,0f,40,0,100);
                        //sendLaserPacket(new Vector3(this).translate(0.5), new Vector3(xCoord, firstUncoveredY, zCoord).translate(0.5), 0f, 1f, 0f, 40, 0, 100);
                    }
                    else if (mode == 2)
                    {
                    	sendLaser(0f,0f,1f,40,0,100);
                        //sendLaserPacket(new Vector3(this).translate(0.5), new Vector3(xCoord, firstUncoveredY, zCoord).translate(0.5), 0f, 0f, 1f, 40, 0, 100);
                    }

                liftEntity();
            }
        }
    }

    public void liftEntity()
    {
        double xmax, zmax;
        double xmin, zmin;
        int x1, x2, z1, z2;
        final int CUBE_SIDE = 2;
        double errorMargin = -0.1;
        x1 = xCoord + CUBE_SIDE / 2;
        x2 = xCoord - CUBE_SIDE / 2;

        xmin = Math.min(x1,x2) - errorMargin + 1;
        xmax = Math.max(x1,x2) + errorMargin;

        z1 = zCoord + CUBE_SIDE / 2;
        z2 = zCoord - CUBE_SIDE / 2;

        zmin = Math.min(z1,z2) - errorMargin + 1;
        zmax = Math.max(z1,z2) + errorMargin;
        //WarpDrive.debugPrint("LL x:" + xmin + "-" + xmax + " | z: " +zmin + "-" + zmax);
        // Lift up
        if (mode == 1)
        {
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(xmin, firstUncoveredY, zmin, xmax, yCoord, zmax);
            List list = worldObj.getEntitiesWithinAABBExcludingEntity(null, aabb);

            if (list != null)   // up
            {
                for (Object o : list)
                {
                    if (o != null && o instanceof EntityLivingBase && removeEnergy(WarpDriveConfig.LL_LIFT_ENERGY,false))
                    {
                    	//((EntityLivingBase)o).setVelocity(0, 0, 0);
                        ((EntityLivingBase)o).setPositionAndUpdate(xCoord + 0.5f, yCoord + 1, zCoord + 0.5f);
                        sendLaser(1f,1f,0f,40,0,100);
                        //sendLaserPacket(new Vector3(this).add(0.5), new Vector3(xCoord, firstUncoveredY, zCoord).add(0.5), 1, 1, 0, 40, 0, 100);
                        worldObj.playSoundEffect(xCoord + 0.5f, yCoord, zCoord + 0.5f, "warpdrive:hilaser", 4F, 1F);
                    }
                }
            }
        }
        else if (mode == 2)     // down
        {
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(xmin, yCoord, zmin, xmax, yCoord + 2, zmax);
            List list = worldObj.getEntitiesWithinAABBExcludingEntity(null, aabb);

            if (list != null)
            {
                for (Object o : list)
                {
                    if (o != null && o instanceof EntityLivingBase && removeEnergy(WarpDriveConfig.LL_LIFT_ENERGY,false))
                    {
                    	//((EntityLivingBase)o).setVelocity(0, 0, 0);
                        ((EntityLivingBase)o).setPositionAndUpdate(xCoord + 0.5f, firstUncoveredY + 1, zCoord + 0.5f);
                        //sendLaserPacket(new Vector3(this).translate(0.5), new Vector3(xCoord, firstUncoveredY + 1, zCoord).translate(0.5), 1, 1, 0, 40, 0, 100);
                        sendLaser(1f,1f,0f,40,0,100);
                        worldObj.playSoundEffect(xCoord + 0.5f, yCoord, zCoord + 0.5f, "warpdrive:hilaser", 4F, 1F);
                    }
                }
            }
        }
    }
    
    @Override
    public boolean shouldChunkLoad()
    {
    	return false;
    }

    @Override
    public int getMaxEnergyStored()
    {
    	return WarpDriveConfig.LL_MAX_ENERGY;
    }

	@Override
	public String getType() {
		return "warpdriveLaserLift";
	}

	@Override
	public String[] getMethodNames()
	{
		return methodsArray;
	}
	
	public String helpStr(Object[] args)
	{
		if(args.length == 1)
		{
			String fun = args[0].toString().toLowerCase();
			if(fun.equals("energy"))
				return WarpDrive.defEnergyStr;
			else if(fun.equals("mode"))
				return "mode(\"up\" or \"down\" or \"redstone\"): sets the mode\nmode(): returns the current mode";
			else if(fun.equals("active"))
				return "active(boolean): sets whether the laser is active\nactive(): returns whether the laser is active";
		}
		return WarpDrive.defHelpStr;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] args) throws Exception
	{
		String meth = methodsArray[method];
		if(meth.equals("energy"))
			return getEnergyObject();
		else if(meth.equals("mode"))
		{
			if(args.length == 1)
				computerModeForce = args[0].toString().equals("up") ? 1 : (args[0].toString().equals("down") ? 2 : -1);
			switch(computerModeForce)
			{
				case -1: return new Object[] { "redstone" };
				case 1: return new Object[] { "up" };
				case 2: return new Object[] { "down" };
			}
		}
		else if(meth.equals("active"))
		{
			if(args.length == 1)
				computerDisabled = !toBool(args[0]);
			return new Object[] { computerDisabled ? false : isEnabled };
		}
		else if(meth.equals("help"))
			return new Object[] { helpStr(args) };
		return null;
	}

	@Override
	public void attach(IComputerAccess computer)
	{
		
	}

	@Override
	public void detach(IComputerAccess computer)
	{
		
	}
	
	@Override
	public int hashCode()
	{
		return (((((super.hashCode() + worldObj.provider.dimensionId << 4) + xCoord) << 4) + yCoord) << 4) + zCoord;
	}

	@Override
	public boolean equals(IPeripheral other)
	{
		return other.hashCode() == hashCode();
	}
}
