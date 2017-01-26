package cr0s.warpdrive.item;

import java.util.List;

import cr0s.warpdrive.data.SoundEvents;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBeamFrequency;
import cr0s.warpdrive.api.IVideoChannel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ItemTuningFork extends ItemAbstractBase {
	
	public ItemTuningFork(final String registryName) {
		super(registryName);
		setMaxDamage(0);
		setMaxStackSize(1);
		setUnlocalizedName("warpdrive.tool.TuningFork");
		setFull3D();
		setHasSubtypes(true);
	}
	
	@Override
	public void getSubItems(@Nonnull Item item, @Nonnull CreativeTabs creativeTabs, @Nonnull List<ItemStack> subItems) {
		for(int dyeColor = 0; dyeColor < 16; dyeColor++) {
			subItems.add(new ItemStack(item, 1, dyeColor));
		}
	}
	
	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModelResourceLocation(ItemStack itemStack) {
		int damage = itemStack.getItemDamage();
		ResourceLocation resourceLocation = getRegistryName();
		if (damage >= 0 && damage < 16) {
			resourceLocation = new ResourceLocation(resourceLocation.getResourceDomain(), resourceLocation.getResourcePath() + "-" + EnumDyeColor.byDyeDamage(damage).getUnlocalizedName());
		}
		return new ModelResourceLocation(resourceLocation, "inventory");
	}
	
	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int damage = itemStack.getItemDamage();
		if (damage >= 0 && damage < 16) {
			return getUnlocalizedName() + "." + EnumDyeColor.byDyeDamage(damage).getUnlocalizedName();
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
	
	@Nonnull
	@Override
	public EnumActionResult onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, BlockPos blockPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return EnumActionResult.FAIL;
		}
		TileEntity tileEntity = world.getTileEntity(blockPos);
		if (tileEntity == null) {
			return EnumActionResult.FAIL;
		}
		
		boolean hasVideoChannel = tileEntity instanceof IVideoChannel;
		boolean hasBeamFrequency = tileEntity instanceof IBeamFrequency;
		if (!hasVideoChannel && !hasBeamFrequency) {
			return EnumActionResult.FAIL;
		}
		if (hasVideoChannel && !(entityPlayer.isSneaking() && hasBeamFrequency)) {
			((IVideoChannel)tileEntity).setVideoChannel(getVideoChannel(itemStack));
			WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("item.warpdrive.tool.TuningFork.use.setVideoChannel",
					tileEntity.getBlockType().getLocalizedName(),
					getVideoChannel(itemStack)));
			world.playSound(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, SoundEvents.DING, SoundCategory.PLAYERS, 0.1F, 1F, false);
		} else if (hasBeamFrequency) {
			((IBeamFrequency)tileEntity).setBeamFrequency(getBeamFrequency(itemStack));
			WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("item.warpdrive.tool.TuningFork.use.setBeamFrequency",
					tileEntity.getBlockType().getLocalizedName(),
					getBeamFrequency(itemStack)));
			world.playSound(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, SoundEvents.DING, SoundCategory.PLAYERS, 0.1F, 1F, false);
		} else {
			WarpDrive.addChatMessage(entityPlayer, new TextComponentString("Error: invalid state, please contact the mod authors"
					+ "\nof " + itemStack
					+ "\nand " + tileEntity));
		}
		return EnumActionResult.SUCCESS;
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean advancedItemTooltips) {
		super.addInformation(itemStack, entityPlayer, list, advancedItemTooltips);
		
		String tooltip = "";
		tooltip += new TextComponentTranslation("item.warpdrive.tool.TuningFork.tooltip.videoChannel", getVideoChannel(itemStack));
		tooltip += "\n" + new TextComponentTranslation("item.warpdrive.tool.TuningFork.tooltip.beamFrequency", getBeamFrequency(itemStack));
		
		tooltip += "\n\n" + new TextComponentTranslation("item.warpdrive.tool.TuningFork.tooltip.usage").getFormattedText();
		
		WarpDrive.addTooltip(list, tooltip);
	}
}
