package cr0s.warpdrive.api;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by LemADEC on 16/05/2016.
 */
public interface IEffector {
	void onEntityCollided(World world, final int x, final int y, final int z, Entity entity);
}
