package cr0s.warpdrive.block;

import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.client.ClientProxy;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.Optional;
import cr0s.warpdrive.config.WarpDriveConfig;
import defense.api.IEMPBlock;
import defense.api.IExplosion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockUpdateDetector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Optional.InterfaceList({
    @Optional.Interface(iface = "defense.api.IEMPBlock", modid = "DefenseTech")
})
public abstract class BlockAbstractContainer extends BlockContainer implements IEMPBlock, IBlockBase {
	
	protected BlockAbstractContainer(final String registryName, final Material material) {
		super(material);
		setHardness(5.0F);
		setResistance(6.0F * 5 / 3);
		setSoundType(SoundType.METAL);
		setCreativeTab(WarpDrive.creativeTabWarpDrive);
		setRegistryName(registryName);
		WarpDrive.register(this);
		
		setDefaultState(blockState.getBaseState());
	}
	
	@Nullable
	@Override
	public ItemBlock createItemBlock() {
		return new ItemBlockAbstractBase(this);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void modelInitialisation() {
		Item item = Item.getItemFromBlock(this);
		ClientProxy.modelInitialisation(item);
	}
	
	@Nonnull
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof IBlockUpdateDetector) {
			((IBlockUpdateDetector) tileEntity).updatedNeighbours();
		}
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLiving, ItemStack itemStack) {
		super.onBlockPlacedBy(world, pos, state, entityLiving, itemStack);
		
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity != null && itemStack.getTagCompound() != null) {
			NBTTagCompound nbtTagCompound = itemStack.getTagCompound().copy();
			nbtTagCompound.setInteger("x", pos.getX());
			nbtTagCompound.setInteger("y", pos.getY());
			nbtTagCompound.setInteger("z", pos.getZ());
			tileEntity.readFromNBT(nbtTagCompound);
			IBlockState blockState = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, blockState, blockState, 3);
		}
	}
	
	@Override
	public boolean removedByPlayer(@Nonnull IBlockState blockState, World world, @Nonnull BlockPos blockPos, @Nonnull EntityPlayer player, boolean willHarvest) {
		return willHarvest || super.removedByPlayer(blockState, world, blockPos, player, false);
	}
	
	@Override
	public void dropBlockAsItemWithChance(World world, @Nonnull BlockPos blockPos, @Nonnull IBlockState blockState, float chance, int fortune) {
		ItemStack itemStack = new ItemStack(this);
		itemStack.setItemDamage(damageDropped(blockState));
		TileEntity tileEntity = world.getTileEntity(blockPos);
		if (tileEntity == null) {
			WarpDrive.logger.error("Missing tile entity for " + this + " at " + world + " " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ());
		} else if (tileEntity instanceof TileEntityAbstractBase) {
			NBTTagCompound nbtTagCompound = new NBTTagCompound();
			((TileEntityAbstractBase) tileEntity).writeItemDropNBT(nbtTagCompound);
			itemStack.setTagCompound(nbtTagCompound);
		}
		world.setBlockToAir(blockPos);
		super.dropBlockAsItemWithChance(world, blockPos, blockState, chance, fortune);
	}
	
	@Nonnull
	@Override
	public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos blockPos, EntityPlayer entityPlayer) {
		ItemStack itemStack = super.getPickBlock(state, target, world, blockPos, entityPlayer);
		TileEntity tileEntity = world.getTileEntity(blockPos);
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		if (tileEntity instanceof TileEntityAbstractBase) {
			((TileEntityAbstractBase) tileEntity).writeItemDropNBT(nbtTagCompound);
			itemStack.setTagCompound(nbtTagCompound);
		}
		return itemStack;
	}
	
	// FIXME untested
	/*
	@Override
	public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState blockState, EntityPlayer entityPlayer, EnumHand hand, @Nullable ItemStack itemStackHeld, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		
		boolean hasResponse = false;
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof IUpgradable) {
			IUpgradable upgradable = (IUpgradable) tileEntity;
			ItemStack itemStack = entityPlayer.inventory.getCurrentItem();
			if (itemStack != null) {
				Item i = itemStack.getItem();
				if (i instanceof ItemWarpUpgrade) {
					if (upgradable.takeUpgrade(EnumUpgradeTypes.values()[itemStack.getItemDamage()], false)) {
						if (!entityPlayer.capabilities.isCreativeMode)
							entityPlayer.inventory.decrStackSize(entityPlayer.inventory.currentItem, 1);
						entityPlayer.addChatMessage("Upgrade accepted");
					} else {
						entityPlayer.addChatMessage("Upgrade declined");
					}
					hasResponse = true;
				}
			}
		}
		
		return hasResponse;
	}
	/**/
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(IBlockState blockState, World world, BlockPos blockPos, Block block) {
		super.neighborChanged(blockState, world, blockPos, block);
		TileEntity tileEntity = world.getTileEntity(blockPos);
		if (tileEntity instanceof IBlockUpdateDetector) {
			((IBlockUpdateDetector) tileEntity).updatedNeighbours();
		}
	}
	
	@Optional.Method(modid = "DefenseTech")
	public void onEMP(World world, int x, int y, int z, IExplosion explosiveEMP) {
		if (WarpDriveConfig.LOGGING_WEAPON) {
			WarpDrive.logger.info("EMP received at " + x + " " + y + " " + z + " from " + explosiveEMP + " with energy " + explosiveEMP.getEnergy() + " and radius " + explosiveEMP.getRadius());
		}
		// EMP tower = 3k Energy, 60 radius
		// EMP explosive = 3k Energy, 50 radius
		onEMP(world, new BlockPos(x, y, z), explosiveEMP.getRadius() / 100.0F);
	}
	
	public void onEMP(World world, final BlockPos blockPos, final float efficiency) {
		TileEntity tileEntity = world.getTileEntity(blockPos);
		if (tileEntity instanceof TileEntityAbstractEnergy) {
			TileEntityAbstractEnergy tileEntityAbstractEnergy = (TileEntityAbstractEnergy) tileEntity;
			if (tileEntityAbstractEnergy.getMaxEnergyStored() > 0) {
				tileEntityAbstractEnergy.consumeEnergy(Math.round(tileEntityAbstractEnergy.getEnergyStored() * efficiency), false);
			}
		}
	}
}
