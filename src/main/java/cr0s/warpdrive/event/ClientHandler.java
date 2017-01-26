package cr0s.warpdrive.event;

import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.config.WarpDriveConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import cr0s.warpdrive.WarpDrive;

public class ClientHandler {

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTooltipEvent(ItemTooltipEvent event) {
		if (event.getEntityPlayer() == null || !event.getEntityPlayer().capabilities.isCreativeMode) {
			return;
		}
		if (Dictionary.ITEMS_BREATHINGIC2.contains(event.getItemStack().getItem()) && WarpDriveConfig.isIndustrialCraft2Loaded) {
			WarpDrive.addTooltip(event.getToolTip(), new TextComponentTranslation("warpdrive.tooltip.itemTag.breathingIC2").getFormattedText());
		}
		if (Dictionary.ITEMS_FLYINSPACE.contains(event.getItemStack().getItem())) {
			WarpDrive.addTooltip(event.getToolTip(), new TextComponentTranslation("warpdrive.tooltip.itemTag.flyInSpace").getFormattedText());
		}
		if (Dictionary.ITEMS_NOFALLDAMAGE.contains(event.getItemStack().getItem())) {
			WarpDrive.addTooltip(event.getToolTip(), new TextComponentTranslation("warpdrive.tooltip.itemTag.noFallDamage").getFormattedText());
		}
		if (WarpDrive.isDev && event.getEntityPlayer().capabilities.isCreativeMode) {// disabled in production
			Block block = Block.getBlockFromItem(event.getItemStack().getItem());
			if (block != Blocks.AIR && block != null) {
				try {
					ResourceLocation resourceLocation = Block.REGISTRY.getNameForObject(block);
					WarpDrive.addTooltip(event.getToolTip(), "" + resourceLocation + "");
				} catch(Exception exception) {
					// no operation
				}
				
				try {
					IBlockState blockState = block.getStateFromMeta(event.getItemStack().getItemDamage());
					String harvestTool = block.getHarvestTool(blockState);
					WarpDrive.addTooltip(event.getToolTip(), "Harvest with " + harvestTool + " (" + block.getHarvestLevel(blockState) + ")");
				} catch(Exception exception) {
					// no operation
				}
				
				try {
					IBlockState blockState = block.getStateFromMeta(event.getItemStack().getItemDamage());
					WarpDrive.addTooltip(event.getToolTip(), "Light opacity is " + block.getLightOpacity(blockState));
				} catch(Exception exception) {
					// no operation
				}
				
				try {
					WarpDrive.addTooltip(event.getToolTip(), "Hardness is " + (float)WarpDrive.fieldBlockHardness.get(block));
				} catch(Exception exception) {
					// no operation
				}
				try {
					WarpDrive.addTooltip(event.getToolTip(), "Explosion resistance is " + block.getExplosionResistance(null));
				} catch(Exception exception) {
					// no operation
				}
			}
		}
	}
}
