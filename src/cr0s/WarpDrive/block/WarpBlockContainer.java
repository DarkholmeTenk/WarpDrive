package cr0s.WarpDrive.block;

import cr0s.WarpDrive.WarpDrive;
import cr0s.WarpDriveCore.IBlockUpdateDetector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class WarpBlockContainer extends BlockContainer
{
	protected WarpBlockContainer(int par1)
	{
		super(par1,Material.iron);
	}
	
	protected WarpBlockContainer(int par1,Material m)
	{
		super(par1,m);
	}
	
	//lazy constructor method. Shhhhh, everything's fine.
	{
		setHardness(0.5F);
		setStepSound(Block.soundMetalFootstep);
		setCreativeTab(WarpDrive.warpdriveTab);
	}
	
	@Override
	public void onBlockAdded(World w,int x, int y,int z)
	{
		super.onBlockAdded(w, x, y, z);
		TileEntity te = w.getBlockTileEntity(x, y, z);
		if(te instanceof IBlockUpdateDetector)
			((IBlockUpdateDetector)te).updatedNeighbours();
	}
	
	@Override
	public void onNeighborBlockChange(World w,int x,int y,int z,int b)
	{
		super.onNeighborBlockChange(w, x, y, z, b);
		TileEntity te = w.getBlockTileEntity(x, y, z);
		if(te instanceof IBlockUpdateDetector)
			((IBlockUpdateDetector)te).updatedNeighbours();
	}

	
}
