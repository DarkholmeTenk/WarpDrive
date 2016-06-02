package cr0s.warpdrive.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBeamFrequency;
import cr0s.warpdrive.api.IVideoChannel;

public class ItemTuningFork extends Item {
	private IIcon icons[];
	
	public ItemTuningFork() {
		super();
		setMaxDamage(0);
		setCreativeTab(WarpDrive.creativeTabWarpDrive);
		setMaxStackSize(1);
		setUnlocalizedName("warpdrive.tool.TuningFork");
		setFull3D();
		setHasSubtypes(true);
	}
	
	@Override
	public void registerIcons(IIconRegister iconRegister) {
		icons = new IIcon[16];
		
		for (int i = 0; i < 16; ++i) {
			icons[i] = iconRegister.registerIcon("warpdrive:tool/tuningFork_" + getDyeColorName(i));
		}
	}
	
	public static String getDyeColorName(int metadata) {
		return ItemDye.field_150921_b[metadata];
	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		if (damage < icons.length) {
			return icons[damage];
		}
		return Blocks.fire.getFireIcon(0);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		for(int dyeColor = 0; dyeColor < 16; dyeColor++) {
			list.add(new ItemStack(item, 1, dyeColor));
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int damage = itemStack.getItemDamage();
		if (damage >= 0 && damage < 16) {
			return getUnlocalizedName() + "." + ItemDye.field_150923_a[damage];
		}
		return getUnlocalizedName();
	}
	
	public static int getVideoChannel(ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemTuningFork)) {
			return -1;
		}
		return (itemStack.getItemDamage() % 16) + 100;
	}
	
	public static int getBeamFrequency(ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemTuningFork)) {
			return -1;
		}
		return ((itemStack.getItemDamage() % 16) + 1) * 10;
	}
	
	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null) {
			return false;
		}
		
		boolean hasVideoChannel = tileEntity instanceof IVideoChannel;
		boolean hasBeamFrequency = tileEntity instanceof IBeamFrequency;
		if (!hasVideoChannel && !hasBeamFrequency) {
			return false;
		}
		if (hasVideoChannel && !(entityPlayer.isSneaking() && hasBeamFrequency)) {
			((IVideoChannel)tileEntity).setVideoChannel(getVideoChannel(itemStack));
			WarpDrive.addChatMessage(entityPlayer, StatCollector.translateToLocalFormatted("item.warpdrive.tool.TuningFork.use.setVideoChannel",
					tileEntity.getBlockType().getLocalizedName(),
					getVideoChannel(itemStack)));
			world.playSoundAtEntity(entityPlayer, "WarpDrive:ding", 0.1F, 1F);
		} else if (hasBeamFrequency) {
			((IBeamFrequency)tileEntity).setBeamFrequency(getBeamFrequency(itemStack));
			WarpDrive.addChatMessage(entityPlayer, StatCollector.translateToLocalFormatted("item.warpdrive.tool.TuningFork.use.setBeamFrequency",
					tileEntity.getBlockType().getLocalizedName(),
					getBeamFrequency(itemStack)));
			world.playSoundAtEntity(entityPlayer, "WarpDrive:ding", 0.1F, 1F);
		} else {
			WarpDrive.addChatMessage(entityPlayer, "Error: invalid state, please contact the mod authors"
					+ "\nof " + itemStack
					+ "\nand " + tileEntity);
		}
		return true;
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean advancedItemTooltips) {
		super.addInformation(itemStack, entityPlayer, list, advancedItemTooltips);
		
		String tooltip = "";
		tooltip += StatCollector.translateToLocalFormatted("item.warpdrive.tool.TuningFork.tooltip.videoChannel", getVideoChannel(itemStack));
		tooltip += "\n" + StatCollector.translateToLocalFormatted("item.warpdrive.tool.TuningFork.tooltip.beamFrequency", getBeamFrequency(itemStack));
		
		tooltip += StatCollector.translateToLocal("item.warpdrive.tool.TuningFork.tooltip.usage");
		
		WarpDrive.addTooltip(list, tooltip);
	}
}
