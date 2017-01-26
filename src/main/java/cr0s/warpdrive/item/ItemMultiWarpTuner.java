package cr0s.warpdrive.item;

import java.util.List;

import cr0s.warpdrive.data.SoundEvents;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBeamFrequency;
import cr0s.warpdrive.api.IVideoChannel;

import javax.annotation.Nonnull;

public class ItemMultiWarpTuner extends ItemAbstractBase {
	static final private int MODE_VIDEO_CHANNEL = 0;
	static final private int MODE_BEAM_FREQUENCY = 1;
	static final private String TAG_VIDEO_CHANNEL = "videoChannel";
	static final private String TAG_BEAM_FREQUENCY = "beamFrequency";
	
	public ItemMultiWarpTuner(final String registryName) {
		super(registryName);
		setMaxDamage(0);
		setMaxStackSize(1);
		setUnlocalizedName("warpdrive.tool.MultiWarpTuner");
		setFull3D();
	}
	
	@Override
	public void getSubItems(@Nonnull Item item, @Nonnull CreativeTabs creativeTabs, @Nonnull List<ItemStack> subItems) {
		for(int dyeColor = 0; dyeColor < 16; dyeColor++) {
			subItems.add(new ItemStack(item, 1, dyeColor));
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
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack itemStack, World world, EntityPlayer entityPlayer, EnumHand hand) {
		if (world.isRemote || !(itemStack.getItem() instanceof ItemMultiWarpTuner)) {
			return new ActionResult<>(EnumActionResult.PASS, itemStack);
		}
		if (entityPlayer.isSneaking()) {
			switch (itemStack.getItemDamage()) {
			case MODE_VIDEO_CHANNEL:
				setVideoChannel(itemStack, world.rand.nextInt(32768));
				WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("item.warpdrive.tool.MultiWarpTuner.use.getVideoChannel",
						getVideoChannel(itemStack)));
				return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
				
			case MODE_BEAM_FREQUENCY:
				setBeamFrequency(itemStack, world.rand.nextInt(65000));
				WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("item.warpdrive.tool.MultiWarpTuner.use.getBeamFrequency",
						getBeamFrequency(itemStack)));
				return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
				
			default:
				return new ActionResult<>(EnumActionResult.PASS, itemStack);
			}
			
		} else {
			switch (itemStack.getItemDamage()) {
			case MODE_VIDEO_CHANNEL:
				itemStack.setItemDamage(MODE_BEAM_FREQUENCY);
				entityPlayer.setHeldItem(hand, itemStack);
				break;
				
			case MODE_BEAM_FREQUENCY:
				itemStack.setItemDamage(MODE_VIDEO_CHANNEL);
				entityPlayer.setHeldItem(hand, itemStack);
				break;
				
			default:
				itemStack.setItemDamage(MODE_VIDEO_CHANNEL);
				break;
			}
			world.playSound(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, SoundEvents.DING, SoundCategory.PLAYERS, 0.1F, 1F, false);
			return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
		}
	}
	
	@Nonnull
	@Override
	public EnumActionResult onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, BlockPos blockPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return EnumActionResult.PASS;
		}
		TileEntity tileEntity = world.getTileEntity(blockPos);
		if (tileEntity == null) {
			return EnumActionResult.PASS;
		}
		
		switch (itemStack.getItemDamage()) {
		case MODE_VIDEO_CHANNEL:
			if (tileEntity instanceof IVideoChannel) {
				if (entityPlayer.isSneaking()) {
					setVideoChannel(itemStack, ((IVideoChannel)tileEntity).getVideoChannel());
					WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("item.warpdrive.tool.MultiWarpTuner.use.getVideoChannel",
							getVideoChannel(itemStack)));
				} else {
					((IVideoChannel)tileEntity).setVideoChannel(getVideoChannel(itemStack));
					WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("item.warpdrive.tool.MultiWarpTuner.use.setVideoChannel",
							tileEntity.getBlockType().getLocalizedName(),
							getVideoChannel(itemStack)));
				}
				return EnumActionResult.SUCCESS;
			}
			return EnumActionResult.PASS;
			
		case MODE_BEAM_FREQUENCY:
			if (tileEntity instanceof IBeamFrequency) {
				if (entityPlayer.isSneaking()) {
					setBeamFrequency(itemStack, ((IBeamFrequency)tileEntity).getBeamFrequency());
					WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("item.warpdrive.tool.MultiWarpTuner.use.getBeamFrequency",
							getBeamFrequency(itemStack)));
				} else {
					((IBeamFrequency)tileEntity).setBeamFrequency(getBeamFrequency(itemStack));
					WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("item.warpdrive.tool.MultiWarpTuner.use.setBeamFrequency",
							tileEntity.getBlockType().getLocalizedName(),
							getBeamFrequency(itemStack)));
				}
				return EnumActionResult.SUCCESS;
			}
			return EnumActionResult.PASS;
			
		default:
			return EnumActionResult.PASS;
		}
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean advancedItemTooltips) {
		super.addInformation(itemStack, entityPlayer, list, advancedItemTooltips);
		
		String tooltip = "";
		switch (itemStack.getItemDamage()) {
		case MODE_VIDEO_CHANNEL:
			tooltip += new TextComponentTranslation("item.warpdrive.tool.MultiWarpTuner.tooltip.videoChannel", getVideoChannel(itemStack)).getFormattedText();
			// String.format("Video channel set to %1$d", getVideoChannel(itemStack));
			break;
		case MODE_BEAM_FREQUENCY:
			tooltip += new TextComponentTranslation("item.warpdrive.tool.MultiWarpTuner.tooltip.beamFrequency", getBeamFrequency(itemStack)).getFormattedText();
			// tooltip = String.format("Laser frequency set to %{0}i", getBeamFrequency(itemStack));
			break;
		default:
			tooltip += "I'm broken :(";
			break;
		}
		
		tooltip += new TextComponentTranslation("item.warpdrive.tool.MultiWarpTuner.tooltip.usage").getFormattedText();
		
		WarpDrive.addTooltip(list, tooltip);
	}
}
