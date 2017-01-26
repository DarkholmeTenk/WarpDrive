package cr0s.warpdrive.block.forcefield;

import cr0s.warpdrive.block.ItemBlockAbstractBase;
import cr0s.warpdrive.data.EnumForceFieldShape;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ItemBlockForceFieldProjector extends ItemBlockAbstractBase {
	
	public ItemBlockForceFieldProjector(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
		setUnlocalizedName("warpdrive.forcefield.projector");
	}
	
	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModelResourceLocation(ItemStack itemStack) {
		int damage = itemStack.getItemDamage();
		ResourceLocation resourceLocation = getRegistryName();
		String stringVariant = "inventory";
/*		if (itemStack.hasTagCompound() && itemStack.getTagCompound() != null) {
			NBTTagCompound tagCompound = itemStack.getTagCompound();
			stringVariant = "facing=" + (tagCompound.getBoolean("isDoubleSided") ? "east" : "north")
					+ ",is_double_sided=false"
					+ "state=connected_powered,shape=" + EnumForceFieldShape.get(tagCompound.getByte("shape")).getName();
		} else {
			if (damage >= 0 && damage < 2) {
				stringVariant = "facing=" + (damage == 1 ? "east" : "north")
						+ ",is_double_sided=false"
						+ ",state=connected_powered,shape=" + EnumForceFieldShape.NONE.getName();
			}
		}/**/
		return new ModelResourceLocation(resourceLocation, stringVariant);
	}
	
	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		if (itemStack == null) {
			return getUnlocalizedName();
		}
		return getUnlocalizedName() + (itemStack.getItemDamage() == 1 ? ".double" : ".single");
	}
}
