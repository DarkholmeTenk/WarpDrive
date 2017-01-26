package cr0s.warpdrive.item;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.forcefield.BlockForceFieldProjector;
import cr0s.warpdrive.block.forcefield.BlockForceFieldRelay;
import cr0s.warpdrive.data.EnumComponentType;
import cr0s.warpdrive.data.EnumForceFieldShape;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.*;

public class ItemForceFieldShape extends ItemAbstractBase {	
	private static ItemStack[] itemStackCache;
	
	public ItemForceFieldShape(final String registryName) {
		super(registryName);
		setHasSubtypes(true);
		setUnlocalizedName("warpdrive.forcefield.shape");
		
		itemStackCache = new ItemStack[EnumForceFieldShape.length];
	}
	
	public static ItemStack getItemStack(EnumForceFieldShape enumForceFieldShape) {
		if (enumForceFieldShape != null) {
			int damage = enumForceFieldShape.ordinal();
			if (itemStackCache[damage] == null) {
				itemStackCache[damage] = new ItemStack(WarpDrive.itemForceFieldShape, 1, damage);
			}
			return itemStackCache[damage];
		}
		return null;
	}
	
	public static ItemStack getItemStackNoCache(EnumForceFieldShape enumForceFieldShape, int amount) {
		return new ItemStack(WarpDrive.itemForceFieldShape, amount, enumForceFieldShape.ordinal());
	}

	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int damage = itemStack.getItemDamage();
		if (damage >= 0 && damage < EnumForceFieldShape.length) {
			return getUnlocalizedName() + "." + EnumForceFieldShape.get(damage).getName();
		}
		return getUnlocalizedName();
	}
	
	@Override
	public void getSubItems(@Nonnull Item item, @Nonnull CreativeTabs creativeTabs, @Nonnull List<ItemStack> subItems) {
		for(EnumForceFieldShape enumForceFieldShape : EnumForceFieldShape.values()) {
			if (enumForceFieldShape != EnumForceFieldShape.NONE) {
				subItems.add(new ItemStack(item, 1, enumForceFieldShape.ordinal()));
			}
		}
	}
	
	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModelResourceLocation(ItemStack itemStack) {
		int damage = itemStack.getItemDamage();
		ResourceLocation resourceLocation = getRegistryName();
		if (damage >= 0 && damage < EnumComponentType.length) {
			resourceLocation = new ResourceLocation(resourceLocation.getResourceDomain(), resourceLocation.getResourcePath() + "-" + EnumForceFieldShape.get(damage).getName());
		}
		return new ModelResourceLocation(resourceLocation, "inventory");
	}
	
	@Override
	public boolean doesSneakBypassUse(ItemStack itemStack, IBlockAccess world, BlockPos blockPos, EntityPlayer player) {
		Block block = world.getBlockState(blockPos).getBlock();
		return block instanceof BlockForceFieldRelay || block instanceof BlockForceFieldProjector || super.doesSneakBypassUse(itemStack, world, blockPos, player);
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean advancedItemTooltips) {
		super.addInformation(itemStack, entityPlayer, list, advancedItemTooltips);
		
		String tooltipName1 = getUnlocalizedName(itemStack) + ".tooltip";
		if (I18n.canTranslate(tooltipName1)) {
			WarpDrive.addTooltip(list, new TextComponentTranslation(tooltipName1).getFormattedText());
		}
		
		String tooltipName2 = getUnlocalizedName() + ".tooltip";
		if ((!tooltipName1.equals(tooltipName2)) && I18n.canTranslate(tooltipName2)) {
			WarpDrive.addTooltip(list, new TextComponentTranslation(tooltipName2).getFormattedText());
		}
		
		WarpDrive.addTooltip(list, "\n");
		
		WarpDrive.addTooltip(list, new TextComponentTranslation("item.warpdrive.forcefield.shape.tooltip.usage").getFormattedText());
	}
}