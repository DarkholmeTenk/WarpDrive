package cr0s.warpdrive.block.energy;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraftforge.fml.common.Optional;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.TileEntityAbstractLaser;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.network.PacketHandler;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class TileEntityEnanReactorLaser extends TileEntityAbstractLaser {
	Vector3 myVec;
	Vector3 reactorVec;
	EnumFacing side = null;
	TileEntityEnanReactorCore reactor;
	
	private boolean isFirstUpdate = true;
	
	public TileEntityEnanReactorLaser() {
		super();
		
		addMethods(new String[] {
				"hasReactor",
				"side",
				"stabilize"
		});
		peripheralName = "warpdriveEnanReactorLaser";
		laserMediumMaxCount = 1;
		directionsValidLaserMedium = new EnumFacing[] { EnumFacing.UP, EnumFacing.DOWN };
	}
	
	public TileEntityEnanReactorCore scanForReactor() {
		reactor = null;
		TileEntity tileEntity;
		side = null;
		// I AM ON THE NORTH SIDE
		tileEntity = worldObj.getTileEntity(pos.add(0, 0, 2));
		if (tileEntity instanceof TileEntityEnanReactorCore && worldObj.isAirBlock(pos.add(0, 0, 1))) {
			side = EnumFacing.NORTH;
			reactor = (TileEntityEnanReactorCore) tileEntity;
		}
		
		// I AM ON THE SOUTH SIDE
		tileEntity = worldObj.getTileEntity(pos.add(0, 0, -2));
		if (tileEntity instanceof TileEntityEnanReactorCore && worldObj.isAirBlock(pos.add(0, 0, -1))) {
			side = EnumFacing.SOUTH;
			reactor = (TileEntityEnanReactorCore) tileEntity;
		}
		
		// I AM ON THE WEST SIDE
		tileEntity = worldObj.getTileEntity(pos.add(2, 0, 0));
		if (tileEntity instanceof TileEntityEnanReactorCore && worldObj.isAirBlock(pos.add(1, 0, 0))) {
			side = EnumFacing.WEST;
			reactor = (TileEntityEnanReactorCore) tileEntity;
		}
		
		// I AM ON THE EAST SIDE
		tileEntity = worldObj.getTileEntity(pos.add(-2, 0, 0));
		if (tileEntity instanceof TileEntityEnanReactorCore && worldObj.isAirBlock(pos.add(-1, 0, 0))) {
			side = EnumFacing.EAST;
			reactor = (TileEntityEnanReactorCore) tileEntity;
		}
		
		setMetadata();
		
		if (reactor != null) {
			reactorVec = new Vector3(reactor).translate(0.5);
		}
		return reactor;
	}
	
	private void setMetadata() {
		int metadata = 0;
		if (side != null) {
			metadata = side.ordinal() - 1;
		}
		if (getBlockMetadata() != metadata) {
			updateMetadata(metadata);
		}
	}
	
	@Override
	public void update() {
		super.update();
		
		if (isFirstUpdate) {
			isFirstUpdate = false;
			scanForReactor();
			myVec = new Vector3(this).translate(0.5);
		}
	}
	
	public void unlink() {
		side = null;
		setMetadata();
	}
	
	@Override
	public void updatedNeighbours() {
		super.updatedNeighbours();
		
		scanForReactor();
	}
	
	private void stabilize(final int energy) {
		if (energy <= 0) {
			return;
		}
		
		scanForReactor();
		if (facingLaserMedium == null) {
			return;
		}
		if (reactor == null) {
			return;
		}
		if (consumeEnergyFromLaserMediums(energy, false)) {
			if (WarpDriveConfig.LOGGING_ENERGY && WarpDriveConfig.LOGGING_LUA) {
				WarpDrive.logger.info("ReactorLaser on " + side + " side sending " + energy);
			}
			reactor.decreaseInstability(side, energy);
			PacketHandler.sendBeamPacket(worldObj, myVec, reactorVec, 0.1F, 0.2F, 1.0F, 25, 50, 100);
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		return super.writeToNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
	}
	
	// OpenComputers callback methods
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] hasReactor(Context context, Arguments arguments) {
		return new Object[] { scanForReactor() != null };
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] stabilize(Context context, Arguments arguments) {
		if (arguments.count() >= 1) {
			stabilize(arguments.checkInteger(0));
		}
		
		return null;
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] side(Context context, Arguments arguments) {
		return new Object[] { side.ordinal() - 2 };
	}
	
	// ComputerCraft methods
	@Override
	@Optional.Method(modid = "ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) {
		String methodName = getMethodName(method);
		
		switch (methodName) {
			case "hasReactor":
				return new Object[]{scanForReactor() != null};

			case "stabilize":
				if (arguments.length >= 1) {
					stabilize(toInt(arguments[0]));
				}
				break;

			case "side":
				return new Object[]{side.ordinal() - 2};
		}
		
		return super.callMethod(computer, context, method, arguments);
	}
}