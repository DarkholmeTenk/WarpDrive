package cr0s.warpdrive.block.hull;

import cr0s.warpdrive.block.ItemBlockAbstractBase;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ItemBlockHull extends ItemBlockAbstractBase {
	
	public ItemBlockHull(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
		setUnlocalizedName("warpdrive.hull");
	}
	
	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModelResourceLocation(ItemStack itemStack) {
		int damage = itemStack.getItemDamage();
		if (damage < 0 || damage > 15) {
			throw new IllegalArgumentException(String.format("Invalid damage %d for %s", damage, itemStack.getItem()));
		}
		ResourceLocation resourceLocation = getRegistryName();
		String variant = String.format("color=%s", EnumDyeColor.byDyeDamage( itemStack.getItemDamage() ).getName());
		return new ModelResourceLocation(resourceLocation, variant);
	}
	
	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		if (itemStack == null || block instanceof BlockHullStairs) {
			return getUnlocalizedName();
		}
		return getUnlocalizedName() + EnumDyeColor.byDyeDamage( itemStack.getItemDamage() ).getUnlocalizedName();
	}
}
