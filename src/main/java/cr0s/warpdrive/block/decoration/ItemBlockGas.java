package cr0s.warpdrive.block.decoration;

import cr0s.warpdrive.block.ItemBlockAbstractBase;
import cr0s.warpdrive.data.EnumGasColor;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ItemBlockGas extends ItemBlockAbstractBase {
	
	public ItemBlockGas(Block block) {
		super(block);
		setHasSubtypes(true);
		setUnlocalizedName("warpdrive.passive.Gas");
	}
	
	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModelResourceLocation(ItemStack itemStack) {
		int damage = itemStack.getItemDamage();
		if (damage < 0 || damage > EnumGasColor.length) {
			throw new IllegalArgumentException(String.format("Invalid damage %d for %s", damage, itemStack.getItem()));
		}
		ResourceLocation resourceLocation = getRegistryName();
		String variant = String.format("color=%s", EnumGasColor.get( itemStack.getItemDamage() ).getName());
		return new ModelResourceLocation(resourceLocation, variant);
	}
}
