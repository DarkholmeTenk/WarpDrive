package cr0s.warpdrive.block.passive;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.data.DecorativeType;

public class BlockDecorative extends Block {
	private static IIcon[] icons;
	private static ItemStack[] itemStackCache;
	
	public BlockDecorative() {
		super(Material.iron);
		setHardness(1.5f);
		setStepSound(Block.soundTypeMetal);
		setBlockName("warpdrive.passive.Plain");
		setCreativeTab(WarpDrive.creativeTabWarpDrive);
		
		icons = new IIcon[DecorativeType.length];
		itemStackCache = new ItemStack[DecorativeType.length];
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list) {
		for (DecorativeType decorativeType : DecorativeType.values()) {
			list.add(new ItemStack(item, 1, decorativeType.ordinal()));
		}
	}
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		for (DecorativeType decorativeType : DecorativeType.values()) {
			icons[decorativeType.ordinal()] = iconRegister.registerIcon("warpdrive:passive/decorative" + decorativeType.unlocalizedName);
		}
	}
	
	@Override
	public IIcon getIcon(int side, int damage) {
		if (damage >= 0 && damage < DecorativeType.length) {
			return icons[damage];
		}
		return icons[0];
	}
	
	@Override
	public int damageDropped(int damage) {
		return damage;
	}
	
	public static ItemStack getItemStack(DecorativeType decorativeType) {
		if (decorativeType != null) {
			int damage = decorativeType.ordinal();
			if (itemStackCache[damage] == null) {
				itemStackCache[damage] = new ItemStack(WarpDrive.blockDecorative, 1, damage);
			}
			return itemStackCache[damage];
		}
		return null;
	}
	
	public static ItemStack getItemStackNoCache(DecorativeType decorativeType, int amount) {
		return new ItemStack(WarpDrive.blockDecorative, amount, decorativeType.ordinal());
	}
}
