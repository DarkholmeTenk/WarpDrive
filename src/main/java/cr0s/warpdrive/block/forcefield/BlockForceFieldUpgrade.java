package cr0s.warpdrive.block.forcefield;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.BlockAbstractContainer;
import cr0s.warpdrive.data.EnumForceFieldUpgrade;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.List;

public class BlockForceFieldUpgrade extends BlockAbstractContainer {
	@SideOnly(Side.CLIENT)
	private final IIcon[] icons;
	
	public BlockForceFieldUpgrade() {
		super(Material.iron);
		isRotating = false;
		setBlockName("warpdrive.forcefield.upgrade");
		setBlockTextureName("warpdrive:forcefield/upgrade");
		
		icons = new IIcon[EnumForceFieldUpgrade.length];
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list) {
		for (EnumForceFieldUpgrade enumForceFieldUpgrade : EnumForceFieldUpgrade.values()) {
			list.add(new ItemStack(item, 1, enumForceFieldUpgrade.ordinal()));
		}
	}
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		for (EnumForceFieldUpgrade enumForceFieldUpgrade : EnumForceFieldUpgrade.values()) {
			icons[enumForceFieldUpgrade.ordinal()] = iconRegister.registerIcon("warpdrive:forcefield/upgrade" + "_" + enumForceFieldUpgrade.unlocalizedName);
		}
	}
	
	@Override
	public IIcon getIcon(int side, int damage) {
		if (damage >= 0 && damage < EnumForceFieldUpgrade.length) {
			return icons[damage];
		}
		return icons[0];
	}
	
	@Override
	public int damageDropped(int damage) {
		return damage;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof TileEntityForceFieldUpgrade)) {
			return false;
		}
		TileEntityForceFieldUpgrade tileEntityForceFieldUpgrade = (TileEntityForceFieldUpgrade) tileEntity;
		ItemStack itemStackHeld = entityPlayer.getHeldItem();
		
		if (itemStackHeld == null) {
			WarpDrive.addChatMessage(entityPlayer, tileEntityForceFieldUpgrade.getStatus());
			return true;
		}
		
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityForceFieldUpgrade();
	}
	
	@Override
	protected boolean canSilkHarvest() {
		return false;
	}
	
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer entityPlayer) {
		return null; // FIXME
	}
}
