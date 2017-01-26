package cr0s.warpdrive.block.energy;

import cr0s.warpdrive.item.ItemTuningFork;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.BlockAbstractContainer;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockEnergyBank extends BlockAbstractContainer {
	
	public BlockEnergyBank(final String registryName) {
		super(registryName, Material.IRON);
		setUnlocalizedName("warpdrive.energy.EnergyBank");
		GameRegistry.registerTileEntity(TileEntityEnergyBank.class, WarpDrive.PREFIX + registryName);
	}

	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
		return new TileEntityEnergyBank();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState blockState, EntityPlayer entityPlayer, EnumHand hand, @Nullable ItemStack itemStackHeld, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		
		TileEntity tileEntity = world.getTileEntity(blockPos);
		if (!(tileEntity instanceof TileEntityEnergyBank)) {
			return false;
		}
		TileEntityEnergyBank tileEntityEnergyBank = (TileEntityEnergyBank) tileEntity;
		
		if (itemStackHeld == null) {
			WarpDrive.addChatMessage(entityPlayer, tileEntityEnergyBank.getStatus());
			return true;
		} else if (itemStackHeld.getItem() instanceof ItemTuningFork) {
			tileEntityEnergyBank.setMode(side, (byte)((tileEntityEnergyBank.getMode(side) + 1) % 3));
			switch (tileEntityEnergyBank.getMode(side)) {
				case TileEntityEnergyBank.MODE_INPUT:
					WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.guide.prefix", getLocalizedName())
					    .appendSibling(new TextComponentTranslation("warpdrive.energy.side.changedToInput", side.name())));
					return true;
				case TileEntityEnergyBank.MODE_OUTPUT:
					WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.guide.prefix", getLocalizedName())
					    .appendSibling(new TextComponentTranslation("warpdrive.energy.side.changedToOutput", side.name())));
					return true;
				case TileEntityEnergyBank.MODE_DISABLED:
				default:
					WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.guide.prefix", getLocalizedName())
					    .appendSibling(new TextComponentTranslation("warpdrive.energy.side.changedToDisabled", side.name())));
					return true;
			}
		}
		
		return false;
	}
}