package cr0s.warpdrive.api;

import net.minecraft.item.ItemBlock;

import javax.annotation.Nullable;

public interface IBlockBase {
    @Nullable
    ItemBlock createItemBlock();
    
    void modelInitialisation();
}
