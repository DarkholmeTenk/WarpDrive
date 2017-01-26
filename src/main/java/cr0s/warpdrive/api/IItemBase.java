package cr0s.warpdrive.api;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public interface IItemBase {
    @Nonnull
    @SideOnly(Side.CLIENT)
    ModelResourceLocation getModelResourceLocation(ItemStack itemStack);
}
