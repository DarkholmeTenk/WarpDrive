package cr0s.warpdrive.block.forcefield;

import cr0s.warpdrive.block.ItemBlockAbstractBase;
import net.minecraft.block.Block;

public class ItemBlockForceFieldRelay extends ItemBlockAbstractBase {
	
	public ItemBlockForceFieldRelay(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(false);
		setUnlocalizedName("warpdrive.forcefield.relay");
	}
}
