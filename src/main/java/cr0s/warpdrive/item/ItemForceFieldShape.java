package cr0s.warpdrive.item;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IShapeProvider;
import cr0s.warpdrive.data.ForceFieldSetup;
import cr0s.warpdrive.data.EnumForceFieldShape;
import cr0s.warpdrive.data.VectorI;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import java.util.*;

public class ItemForceFieldShape extends Item implements IShapeProvider {	
	private final IIcon[] icons;
	private static ItemStack[] itemStackCache;
	
	public ItemForceFieldShape() {
		super();
		setHasSubtypes(true);
		setUnlocalizedName("warpdrive.forcefield.shape");
		setCreativeTab(WarpDrive.creativeTabWarpDrive);
		
		icons = new IIcon[EnumForceFieldShape.length];
		itemStackCache = new ItemStack[EnumForceFieldShape.length];
	}
	
	public static ItemStack getItemStack(EnumForceFieldShape enumForceFieldShape) {
		if (enumForceFieldShape != null) {
			int damage = enumForceFieldShape.ordinal();
			if (itemStackCache[damage] == null) {
				itemStackCache[damage] = new ItemStack(WarpDrive.itemComponent, 1, damage);
			}
			return itemStackCache[damage];
		}
		return null;
	}
	
	public static ItemStack getItemStackNoCache(EnumForceFieldShape enumForceFieldShape, int amount) {
		return new ItemStack(WarpDrive.itemForceFieldShape, amount, enumForceFieldShape.ordinal());
	}
	
	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		for(EnumForceFieldShape enumForceFieldShape : EnumForceFieldShape.values()) {
			icons[enumForceFieldShape.ordinal()] = par1IconRegister.registerIcon("warpdrive:forcefield/shape_" + enumForceFieldShape.unlocalizedName);
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int damage = itemStack.getItemDamage();
		if (damage >= 0 && damage < EnumForceFieldShape.length) {
			return getUnlocalizedName() + "." + EnumForceFieldShape.get(damage).unlocalizedName;
		}
		return getUnlocalizedName();
	}
	
	@Override
	public IIcon getIconFromDamage(int damage) {
		if (damage >= 0 && damage < EnumForceFieldShape.length) {
			return icons[damage];
		}
		return icons[0];
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		for(EnumForceFieldShape enumForceFieldShape : EnumForceFieldShape.values()) {
			list.add(new ItemStack(item, 1, enumForceFieldShape.ordinal()));
		}
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean advancedItemTooltips) {
		super.addInformation(itemStack, entityPlayer, list, advancedItemTooltips);
		
		String tooltipName1 = getUnlocalizedName(itemStack) + ".tooltip";
		if (StatCollector.canTranslate(tooltipName1)) {
			WarpDrive.addTooltip(list, StatCollector.translateToLocalFormatted(tooltipName1));
		}
		
		String tooltipName2 = getUnlocalizedName() + ".tooltip";
		if ((!tooltipName1.equals(tooltipName2)) && StatCollector.canTranslate(tooltipName2)) {
			WarpDrive.addTooltip(list, StatCollector.translateToLocalFormatted(tooltipName2));
		}
	}
	
	@Override
	public Map<VectorI, Boolean> getVertexes(ForceFieldSetup forceFieldSetup) {
		VectorI vScale = forceFieldSetup.vMax.clone().translateBack(forceFieldSetup.vMin);
		Map<VectorI, Boolean> mapVertexes = new HashMap<>(vScale.x * vScale.y * vScale.z);
		int radius;
		int thickness;
		int radiusInterior2;
		int radiusPerimeter2;
		VectorI vCenter;
		switch(forceFieldSetup.enumForceFieldShape) {
			case SPHERE:
				radius = forceFieldSetup.vMax.y;
				radiusInterior2 = Math.round((radius - forceFieldSetup.thickness) * (radius - forceFieldSetup.thickness));
				radiusPerimeter2 = Math.round((radius + forceFieldSetup.thickness) * (radius + forceFieldSetup.thickness));
				vCenter = new VectorI(0, 0, 0);
				for (int y = forceFieldSetup.vMin.y; y <= forceFieldSetup.vMax.y; y++) {
					int y2 = (y - vCenter.y) * (y - vCenter.y);
					for (int x = forceFieldSetup.vMin.x; x <= forceFieldSetup.vMax.x; x++) {
						int x2 = (x - vCenter.x) * (x - vCenter.x);
						for (int z = forceFieldSetup.vMin.z; z <= forceFieldSetup.vMax.z; z++) {
							int z2 = (z - vCenter.z) * (z - vCenter.z);
							if (x2 + y2 + z2 <= radiusPerimeter2) {
								mapVertexes.put(new VectorI(x, y, z), x2 + y2 + z2 >= radiusInterior2);
							}
						}
					}
				}
				break;
			
			case CYLINDER_H:
				radius = Math.round((forceFieldSetup.vMax.y + forceFieldSetup.vMax.z) / 2);
				radiusInterior2 = Math.round((radius - forceFieldSetup.thickness) * (radius - forceFieldSetup.thickness));
				radiusPerimeter2 = Math.round((radius + forceFieldSetup.thickness) * (radius + forceFieldSetup.thickness));
				vCenter = new VectorI(0, 0, 0);
				for (int y = forceFieldSetup.vMin.y; y <= forceFieldSetup.vMax.y; y++) {
					int y2 = (y - vCenter.y) * (y - vCenter.y);
					for (int z = forceFieldSetup.vMin.z; z <= forceFieldSetup.vMax.z; z++) {
						int z2 = (z - vCenter.z) * (z - vCenter.z);
						if (y2 + z2 <= radiusPerimeter2) {
							boolean isPerimeter = y2 + z2 >= radiusInterior2;
							for (int x = forceFieldSetup.vMin.x; x <= forceFieldSetup.vMax.x; x++) {
								mapVertexes.put(new VectorI(x, y, z), isPerimeter);
							}
						}
					}
				}
				break;
			
			case CYLINDER_V:
				radius = Math.round((forceFieldSetup.vMax.x + forceFieldSetup.vMax.y) / 2);
				radiusInterior2 = Math.round((radius - forceFieldSetup.thickness) * (radius - forceFieldSetup.thickness));
				radiusPerimeter2 = Math.round((radius + forceFieldSetup.thickness) * (radius + forceFieldSetup.thickness));
				vCenter = new VectorI(0, 0, 0);
				for (int x = forceFieldSetup.vMin.x; x <= forceFieldSetup.vMax.x; x++) {
					int x2 = (x - vCenter.x) * (x - vCenter.x);
					for (int y = forceFieldSetup.vMin.y; y <= forceFieldSetup.vMax.y; y++) {
						int y2 = (y - vCenter.y) * (y - vCenter.y);
						if (x2 + y2 <= radiusPerimeter2) {
							boolean isPerimeter = x2 + y2 >= radiusInterior2;
							for (int z = forceFieldSetup.vMin.z; z <= forceFieldSetup.vMax.z; z++) {
								mapVertexes.put(new VectorI(x, y, z), isPerimeter);
							}
						}
					}
				}
				break;
			
			case TUBE:
				radius = Math.round((forceFieldSetup.vMax.x + forceFieldSetup.vMax.z) / 2);
				radiusInterior2 = Math.round((radius - forceFieldSetup.thickness) * (radius - forceFieldSetup.thickness));
				radiusPerimeter2 = Math.round((radius + forceFieldSetup.thickness) * (radius + forceFieldSetup.thickness));
				vCenter = new VectorI(0, 0, 0);
				for (int x = forceFieldSetup.vMin.x; x <= forceFieldSetup.vMax.x; x++) {
					int x2 = (x - vCenter.x) * (x - vCenter.x);
					for (int z = forceFieldSetup.vMin.z; z <= forceFieldSetup.vMax.z; z++) {
						int z2 = (z - vCenter.z) * (z - vCenter.z);
						if (x2 + z2 <= radiusPerimeter2) {
							boolean isPerimeter = x2 + z2 >= radiusInterior2;
							for (int y = forceFieldSetup.vMin.y; y <= forceFieldSetup.vMax.y; y++) {
								mapVertexes.put(new VectorI(x, y, z), isPerimeter);
							}
						}
					}
				}
				break;
			
			case CUBE:
				thickness = Math.round(forceFieldSetup.thickness);
				for (int y = forceFieldSetup.vMin.y; y <= forceFieldSetup.vMax.y; y++) {
					boolean yFace = Math.abs(y - forceFieldSetup.vMin.y) <= thickness
								 || Math.abs(y - forceFieldSetup.vMax.y) <= thickness;
					for (int x = forceFieldSetup.vMin.x; x <= forceFieldSetup.vMax.x; x++) {
						boolean xFace = Math.abs(x - forceFieldSetup.vMin.x) <= thickness
									 || Math.abs(x - forceFieldSetup.vMax.x) <= thickness;
						for (int z = forceFieldSetup.vMin.z; z <= forceFieldSetup.vMax.z; z++) {
							boolean zFace = Math.abs(z - forceFieldSetup.vMin.z) <= thickness
										 || Math.abs(z - forceFieldSetup.vMax.z) <= thickness;
							mapVertexes.put(new VectorI(x, y, z), xFace || yFace || zFace);
						}
					}
				}
				break;
			
			case PLANE:
				thickness = Math.round(forceFieldSetup.thickness);
				for (int y = forceFieldSetup.vMin.y; y <= forceFieldSetup.vMax.y; y++) {
					boolean yFace = Math.abs(y - forceFieldSetup.vMin.y) <= thickness
								 || Math.abs(y - forceFieldSetup.vMax.y) <= thickness;
					for (int x = forceFieldSetup.vMin.x; x <= forceFieldSetup.vMax.x; x++) {
						for (int z = forceFieldSetup.vMin.z; z <= forceFieldSetup.vMax.z; z++) {
							mapVertexes.put(new VectorI(x, y, z), yFace);
						}
					}
				}
				break;
			
			case TUNNEL:
				thickness = Math.round(forceFieldSetup.thickness);
				for (int y = forceFieldSetup.vMin.y; y <= forceFieldSetup.vMax.y; y++) {
					for (int x = forceFieldSetup.vMin.x; x <= forceFieldSetup.vMax.x; x++) {
						boolean xFace = Math.abs(x - forceFieldSetup.vMin.x) <= thickness
									 || Math.abs(x - forceFieldSetup.vMax.x) <= thickness;
						for (int z = forceFieldSetup.vMin.z; z <= forceFieldSetup.vMax.z; z++) {
							boolean isPerimeter = xFace
								|| Math.abs(z - forceFieldSetup.vMin.z) <= thickness
								|| Math.abs(z - forceFieldSetup.vMax.z) <= thickness;
							mapVertexes.put(new VectorI(x, y, z), isPerimeter);
						}
					}
				}
				break;
			
			default:
				break;
			
		}
		
		return mapVertexes;
	}
}