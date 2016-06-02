package cr0s.warpdrive.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBeamFrequency;
import cr0s.warpdrive.api.IVideoChannel;

public class ItemMultiWarpTuner extends Item {
	static final int MODE_VIDEO_CHANNEL = 0;
	static final int MODE_BEAM_FREQUENCY = 1;
	static final String TAG_VIDEO_CHANNEL = "videoChannel";
	static final String TAG_BEAM_FREQUENCY = "beamFrequency";
	
	private IIcon icons[];
	
	public ItemMultiWarpTuner() {
		super();
		setMaxDamage(0);
		setCreativeTab(WarpDrive.creativeTabWarpDrive);
		setMaxStackSize(1);
		setUnlocalizedName("warpdrive.tool.MultiWarpTuner");
		setFull3D();
	}
	
	@Override
	public void registerIcons(IIconRegister iconRegister) {
		icons = new IIcon[16];
		
		for (int i = 0; i < 16; ++i) {
			icons[i] = iconRegister.registerIcon("warpdrive:toolMultiWarpTuner_" + getDyeColorName(i));
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
	
	public static int getVideoChannel(ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemMultiWarpTuner)) {
			return -1;
		}
		if (!itemStack.hasTagCompound()) {
			return -1;
		}
		NBTTagCompound nbt = itemStack.getTagCompound();
		if (nbt.hasKey(TAG_VIDEO_CHANNEL)) {
			return nbt.getInteger(TAG_VIDEO_CHANNEL);
		}
		return -1;
	}
	
	public static ItemStack setVideoChannel(ItemStack itemStack, int videoChannel) {
		if (!(itemStack.getItem() instanceof ItemMultiWarpTuner) || videoChannel == -1) {
			return itemStack;
		}
		NBTTagCompound nbt = itemStack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		nbt.setInteger(TAG_VIDEO_CHANNEL, videoChannel);
		itemStack.setTagCompound(nbt);
		return itemStack;
	}
	
	public static int getBeamFrequency(ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemMultiWarpTuner)) {
			return -1;
		}
		if (!itemStack.hasTagCompound()) {
			return -1;
		}
		NBTTagCompound nbt = itemStack.getTagCompound();
		if (nbt.hasKey(TAG_BEAM_FREQUENCY)) {
			return nbt.getInteger(TAG_BEAM_FREQUENCY);
		}
		return -1;
	}
	
	public static ItemStack setBeamFrequency(ItemStack itemStack, int beamFrequency) {
		if (!(itemStack.getItem() instanceof ItemMultiWarpTuner) || beamFrequency == -1) {
			return itemStack;
		}
		NBTTagCompound nbt = itemStack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		nbt.setInteger(TAG_BEAM_FREQUENCY, beamFrequency);
		itemStack.setTagCompound(nbt);
		return itemStack;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		if (world.isRemote || !(itemStack.getItem() instanceof ItemMultiWarpTuner)) {
			return itemStack;
		}
		if (entityPlayer.isSneaking()) {
			switch (itemStack.getItemDamage()) {
			case MODE_VIDEO_CHANNEL:
				setVideoChannel(itemStack, world.rand.nextInt(32768));
				WarpDrive.addChatMessage(entityPlayer, StatCollector.translateToLocalFormatted("item.warpdrive.tool.MultiWarpTuner.use.getVideoChannel",
						getVideoChannel(itemStack)));
				return itemStack;
				
			case MODE_BEAM_FREQUENCY:
				setBeamFrequency(itemStack, world.rand.nextInt(65000));
				WarpDrive.addChatMessage(entityPlayer, StatCollector.translateToLocalFormatted("item.warpdrive.tool.MultiWarpTuner.use.getBeamFrequency",
						getBeamFrequency(itemStack)));
				return itemStack;
				
			default:
				return itemStack;
			}
			
		} else {
			switch (itemStack.getItemDamage()) {
			case MODE_VIDEO_CHANNEL:
				itemStack.setItemDamage(MODE_BEAM_FREQUENCY);
				entityPlayer.setCurrentItemOrArmor(0, itemStack);
				break;
				
			case MODE_BEAM_FREQUENCY:
				itemStack.setItemDamage(MODE_VIDEO_CHANNEL);
				entityPlayer.setCurrentItemOrArmor(0, itemStack);
				break;
				
			default:
				itemStack.setItemDamage(MODE_VIDEO_CHANNEL);
				break;
			}
			world.playSoundAtEntity(entityPlayer, "WarpDrive:ding", 0.1F, 1F);
			return itemStack;
		}
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
		
		switch (itemStack.getItemDamage()) {
		case MODE_VIDEO_CHANNEL:
			if (tileEntity instanceof IVideoChannel) {
				if (entityPlayer.isSneaking()) {
					setVideoChannel(itemStack, ((IVideoChannel)tileEntity).getVideoChannel());
					WarpDrive.addChatMessage(entityPlayer, StatCollector.translateToLocalFormatted("item.warpdrive.tool.MultiWarpTuner.use.getVideoChannel",
							getVideoChannel(itemStack)));
				} else {
					((IVideoChannel)tileEntity).setVideoChannel(getVideoChannel(itemStack));
					WarpDrive.addChatMessage(entityPlayer, StatCollector.translateToLocalFormatted("item.warpdrive.tool.MultiWarpTuner.use.setVideoChannel",
							tileEntity.getBlockType().getLocalizedName(),
							getVideoChannel(itemStack)));
				}
				return true;
			}
			return false;
			
		case MODE_BEAM_FREQUENCY:
			if (tileEntity instanceof IBeamFrequency) {
				if (entityPlayer.isSneaking()) {
					setBeamFrequency(itemStack, ((IBeamFrequency)tileEntity).getBeamFrequency());
					WarpDrive.addChatMessage(entityPlayer, StatCollector.translateToLocalFormatted("item.warpdrive.tool.MultiWarpTuner.use.getBeamFrequency",
							getBeamFrequency(itemStack)));
				} else {
					((IBeamFrequency)tileEntity).setBeamFrequency(getBeamFrequency(itemStack));
					WarpDrive.addChatMessage(entityPlayer, StatCollector.translateToLocalFormatted("item.warpdrive.tool.MultiWarpTuner.use.setBeamFrequency",
							tileEntity.getBlockType().getLocalizedName(),
							getBeamFrequency(itemStack)));
				}
				return false;
			}
			return false;
			
		default:
			return false;
		}
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean advancedItemTooltips) {
		super.addInformation(itemStack, entityPlayer, list, advancedItemTooltips);
		
		String tooltip = "";
		switch (itemStack.getItemDamage()) {
		case MODE_VIDEO_CHANNEL:
			tooltip += StatCollector.translateToLocalFormatted("item.warpdrive.tool.MultiWarpTuner.tooltip.videoChannel", getVideoChannel(itemStack));
			// String.format("Video channel set to %1$d", getVideoChannel(itemStack));
			break;
		case MODE_BEAM_FREQUENCY:
			tooltip += StatCollector.translateToLocalFormatted("item.warpdrive.tool.MultiWarpTuner.tooltip.beamFrequency", getBeamFrequency(itemStack));
			// tooltip = String.format("Laser frequency set to %{0}i", getBeamFrequency(itemStack));
			break;
		default:
			tooltip += "I'm broken :(";
			break;
		}
		
		tooltip += StatCollector.translateToLocal("item.warpdrive.tool.MultiWarpTuner.tooltip.usage");
		
		WarpDrive.addTooltip(list, tooltip);
	}
}
