package cr0s.warpdrive.block.detection;

import cr0s.warpdrive.block.BlockAbstractRotatingContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.data.CameraRegistryItem;
import cr0s.warpdrive.render.ClientCameraHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockMonitor extends BlockAbstractRotatingContainer {
	
	public BlockMonitor(final String registryName) {
		super(registryName, Material.IRON);
		setUnlocalizedName("warpdrive.detection.Monitor");
		GameRegistry.registerTileEntity(TileEntityMonitor.class, WarpDrive.PREFIX + registryName);
	}

	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
		return new TileEntityMonitor();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState blockState, EntityPlayer entityPlayer, EnumHand hand, @Nullable ItemStack itemStackHeld, EnumFacing side, float hitX, float hitY, float hitZ) {
		// Monitor is only reacting client side
		if (!world.isRemote) {
			return false;
		}
		
		if (itemStackHeld == null) {
			TileEntity tileEntity = world.getTileEntity(blockPos);
			
			if (tileEntity instanceof TileEntityMonitor) {
				int videoChannel = ((TileEntityMonitor)tileEntity).getVideoChannel();
				CameraRegistryItem camera = WarpDrive.cameras.getCameraByVideoChannel(world, videoChannel);
				if (camera == null || entityPlayer.isSneaking()) {
					WarpDrive.addChatMessage(entityPlayer, ((TileEntityMonitor)tileEntity).getStatus());
					return true;
				} else {
					WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.monitor.viewingCamera",
							videoChannel,
							camera.position.getX(),
							camera.position.getY(),
							camera.position.getZ() ));
					ClientCameraHandler.setupViewpoint(
							camera.type, entityPlayer, entityPlayer.rotationYaw, entityPlayer.rotationPitch,
							blockPos, blockState,
							camera.position, world.getBlockState(camera.position));
				}
			}
		}
		
		return false;
	}
}