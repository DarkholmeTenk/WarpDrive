package cr0s.warpdrive.event;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cr0s.warpdrive.WarpDrive;

/**
 *
 * @author LemADEC
 */
public class ClientHandler {

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTooltipEvent(ItemTooltipEvent event) {
		if (event.entityPlayer == null || !event.entityPlayer.capabilities.isCreativeMode) {
			return;
		}
		if (WarpDrive.isDev) {// disabled in production
			Block block = Block.getBlockFromItem(event.itemStack.getItem());
			if (block != Blocks.air) {
				try {
					String uniqueName = Block.blockRegistry.getNameForObject(block);
					if (uniqueName != null) {
						event.toolTip.add("" + uniqueName + "");
					}
				} catch(Exception exception) {
					// no operation
				}
				
				try {
					String harvestTool = block.getHarvestTool(event.itemStack.getItemDamage());
					if (harvestTool != null) {
						event.toolTip.add("Harvest with " + harvestTool + " (" + block.getHarvestLevel(event.itemStack.getItemDamage()) + ")");
					}
				} catch(Exception exception) {
					// no operation
				}
				
				event.toolTip.add("Light opacity is " + block.getLightOpacity());
				
				try {
					event.toolTip.add("Hardness is " + (float)WarpDrive.fieldBlockHardness.get(block));
				} catch(Exception exception) {
					// no operation
				}
				event.toolTip.add("Explosion resistance is " + block.getExplosionResistance(null));
			}
		}
	}
}
