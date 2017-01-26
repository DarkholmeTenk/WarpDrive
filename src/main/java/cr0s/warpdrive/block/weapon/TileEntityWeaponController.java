package cr0s.warpdrive.block.weapon;

import java.util.Arrays;

import net.minecraft.nbt.NBTTagCompound;
import cr0s.warpdrive.block.TileEntityAbstractInterfaced;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityWeaponController extends TileEntityAbstractInterfaced {
	
	public TileEntityWeaponController() {
		super();
		
		peripheralName = "warpdriveWeaponController";
		addMethods(new String[] {
		});
		CC_scripts = Arrays.asList("startup");
	}
	
	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return super.writeToNBT(tag);
	}
	
	@Override
	public String toString() {
		return String.format("%s @ \'%s\' (%d %d %d)", getClass().getSimpleName(),
				worldObj == null ? "~NULL~" : worldObj.getWorldInfo().getWorldName(), pos.getX(), pos.getY(), pos.getZ());
	}
}