package cr0s.WarpDrive.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cr0s.WarpDrive.WarpDrive;
import cr0s.WarpDrive.api.IAirCanister;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemWarpAircan extends Item implements IAirCanister
{
	Icon icon;
	public ItemWarpAircan(int id)
	{
		super(id);
		setMaxDamage(20);
		setCreativeTab(WarpDrive.warpdriveTab);
		setMaxStackSize(1);
		setUnlocalizedName("warpdrive.useful.AirCanFull");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister ir)
	{
		icon = ir.registerIcon("warpdrive:aircanFull");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int damage)
	{
		return icon;
	}

	@Override
	public ItemStack emptyDrop()
	{
		return WarpDrive.componentItem.getISNoCache(1, 8);
	}

}
