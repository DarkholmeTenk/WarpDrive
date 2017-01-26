package cr0s.warpdrive.block.passive;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.client.ClientProxy;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockTransportBeacon extends BlockTorch {
	public BlockTransportBeacon(final String registryName) {
		super();
		setHardness(0.5F);
		setUnlocalizedName("warpdrive.passive.TransportBeacon");
		setRegistryName(registryName);
		WarpDrive.register(this);
	}
	
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		EnumFacing enumfacing = stateIn.getValue(FACING);
		double d0 = (double)pos.getX() + 0.5D;
		double d1 = (double)pos.getY() + 0.7D;
		double d2 = (double)pos.getZ() + 0.5D;
		
		if (enumfacing.getAxis().isHorizontal()) {
			EnumFacing opposite = enumfacing.getOpposite();
			worldIn.spawnParticle(EnumParticleTypes.PORTAL, d0 + 0.27D * opposite.getFrontOffsetX(), d1 + 0.22D, d2 + 0.27D * opposite.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D);
			worldIn.spawnParticle(EnumParticleTypes.SUSPENDED, d0 + 0.27D * opposite.getFrontOffsetX(), d1 + 0.22D, d2 + 0.27D * opposite.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D);
		} else {
			worldIn.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			worldIn.spawnParticle(EnumParticleTypes.SUSPENDED, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}
}
