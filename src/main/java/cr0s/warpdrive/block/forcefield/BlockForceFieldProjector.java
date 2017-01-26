package cr0s.warpdrive.block.forcefield;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.client.ClientProxy;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumForceFieldState;
import cr0s.warpdrive.render.TileEntityForceFieldProjectorRenderer;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.data.EnumForceFieldShape;
import cr0s.warpdrive.data.EnumForceFieldUpgrade;
import cr0s.warpdrive.item.ItemForceFieldShape;
import cr0s.warpdrive.item.ItemForceFieldUpgrade;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockForceFieldProjector extends BlockAbstractForceField {

	public static final PropertyBool IS_DOUBLE_SIDED = PropertyBool.create("is_double_sided");
	public static final IUnlistedProperty<EnumForceFieldShape> SHAPE = Properties.toUnlisted(PropertyEnum.create("shape", EnumForceFieldShape.class));
	public static final IUnlistedProperty<EnumForceFieldState> STATE = Properties.toUnlisted(PropertyEnum.create("state", EnumForceFieldState.class));
	
	private static final AxisAlignedBB AABB_DOWN  = new AxisAlignedBB(0.00D, 0.27D, 0.00D, 1.00D, 0.73D, 1.00D);
	private static final AxisAlignedBB AABB_UP    = new AxisAlignedBB(0.00D, 0.27D, 0.00D, 1.00D, 0.73D, 1.00D);
	private static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.00D, 0.00D, 0.27D, 1.00D, 1.00D, 0.73D);
	private static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.00D, 0.00D, 0.27D, 1.00D, 1.00D, 0.73D);
	private static final AxisAlignedBB AABB_WEST  = new AxisAlignedBB(0.27D, 0.00D, 0.00D, 0.73D, 1.00D, 1.00D);
	private static final AxisAlignedBB AABB_EAST  = new AxisAlignedBB(0.27D, 0.00D, 0.00D, 0.73D, 1.00D, 1.00D);
	
	public BlockForceFieldProjector(final String registryName, final byte tier) {
		super(registryName, tier, Material.IRON);
		setUnlocalizedName("warpdrive.forcefield.projector" + tier);
		
		setDefaultState(getDefaultState()
				.withProperty(BlockProperties.FACING, EnumFacing.NORTH)
				.withProperty(IS_DOUBLE_SIDED, false)
				);
		GameRegistry.registerTileEntity(TileEntityForceFieldProjector.class, WarpDrive.PREFIX + registryName);
		// ModelLoader.setCustomMeshDefinition();
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this,
				new IProperty[] { BlockProperties.FACING, IS_DOUBLE_SIDED },
				new IUnlistedProperty[] { SHAPE, STATE });
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int metadata) {
		return getDefaultState()
				.withProperty(BlockProperties.FACING, EnumFacing.getFront(metadata & 7))
				.withProperty(IS_DOUBLE_SIDED, metadata > 7);
	}
	
	@Nonnull
	@Override
	public IBlockState getExtendedState(@Nonnull IBlockState blockState, IBlockAccess world, BlockPos blockPos) {
		TileEntity tileEntity = world.getTileEntity(blockPos);
		EnumForceFieldShape forceFieldShape = EnumForceFieldShape.NONE;
		EnumForceFieldState forceFieldState = EnumForceFieldState.NOT_CONNECTED;
		if (tileEntity instanceof TileEntityForceFieldProjector) {
			TileEntityForceFieldProjector tileEntityForceFieldProjector = (TileEntityForceFieldProjector) tileEntity;
			forceFieldShape = tileEntityForceFieldProjector.getShape();
			forceFieldState = tileEntityForceFieldProjector.getState();
		}
		
		return ((IExtendedBlockState) blockState)
				.withProperty(SHAPE, forceFieldShape)
				.withProperty(STATE, forceFieldState);
	}
	
	@Override
	public int getMetaFromState(IBlockState blockState) {
		return blockState.getValue(BlockProperties.FACING).getIndex()
			+ (blockState.getValue(IS_DOUBLE_SIDED) ? 8 : 0);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void modelInitialisation() {
		Item item = Item.getItemFromBlock(this);
		ClientProxy.modelInitialisation(item);
		/*
		// Force a single model through through a custom state mapper
		StateMapperBase stateMapperBase = new StateMapperBase() {
			@Nonnull
			@Override
			protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState iBlockState) {
				return ForceFieldProjectorBakedModel.MODEL_RESOURCE_LOCATION;
			}
		};
		ModelLoader.setCustomStateMapper(this, stateMapperBase);
		/**/
		// Bind our TESR to our tile entity
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityForceFieldProjector.class, new TileEntityForceFieldProjectorRenderer());
	}
	
	@SuppressWarnings("deprecation")
	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos blockPos, EnumFacing side) {
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isBlockNormalCube(IBlockState blockState) {
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Nonnull
	@SuppressWarnings("deprecation")
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(BlockProperties.FACING)) {
			case DOWN : return AABB_DOWN ;
			case UP   : return AABB_UP   ;
			case NORTH: return AABB_NORTH;
			case SOUTH: return AABB_SOUTH;
			case WEST : return AABB_WEST ;
			case EAST : return AABB_EAST ;
			default   : return AABB_UP;
		}
	}
	
	@Nonnull
	@SuppressWarnings("deprecation")
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase entityLiving) {
		EnumFacing enumFacing = BlockAbstractBase.getFacingFromEntity(pos, entityLiving);
		return this.getDefaultState().withProperty(BlockProperties.FACING, enumFacing);
	}
	
	@Nullable
	@Override
	public ItemBlock createItemBlock() {
		return new ItemBlockForceFieldProjector(this);
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(@Nonnull Item item, CreativeTabs creativeTab, List<ItemStack> list) {
		for (int i = 0; i < 2; ++i) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(IS_DOUBLE_SIDED) ? 1 : 0;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos blockPos, IBlockState blockState, EntityLivingBase entityLiving, ItemStack itemStack) {
		super.onBlockPlacedBy(world, blockPos, blockState, entityLiving, itemStack);
		TileEntityForceFieldProjector tileEntityForceFieldProjector = (TileEntityForceFieldProjector)world.getTileEntity(blockPos);
		if (!itemStack.hasTagCompound() && tileEntityForceFieldProjector != null) {
			tileEntityForceFieldProjector.isDoubleSided = (itemStack.getItemDamage() == 1);
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState blockState, EntityPlayer entityPlayer, EnumHand hand, @Nullable ItemStack itemStackHeld, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		
		TileEntity tileEntity = world.getTileEntity(blockPos);
		if (!(tileEntity instanceof TileEntityForceFieldProjector)) {
			return false;
		}
		TileEntityForceFieldProjector tileEntityForceFieldProjector = (TileEntityForceFieldProjector) tileEntity;
		
		EnumForceFieldUpgrade enumForceFieldUpgrade = EnumForceFieldUpgrade.NONE;
		if (itemStackHeld != null && itemStackHeld.getItem() instanceof ItemForceFieldUpgrade) {
			enumForceFieldUpgrade = EnumForceFieldUpgrade.get(itemStackHeld.getItemDamage());
		}
		
		// sneaking with an empty hand or an upgrade/shape item in hand to dismount current upgrade/shape
		if (entityPlayer.isSneaking()) {
			// using an upgrade item or no shape defined means dismount upgrade, otherwise dismount shape
			if ( (itemStackHeld != null && itemStackHeld.getItem() instanceof ItemForceFieldUpgrade)
			  || (tileEntityForceFieldProjector.getShape() == EnumForceFieldShape.NONE)
			  || (side != blockState.getValue(BlockProperties.FACING) && (!tileEntityForceFieldProjector.isDoubleSided || side.getOpposite() != blockState.getValue(BlockProperties.FACING))) ) {
				// find a valid upgrade to dismount
				if (!tileEntityForceFieldProjector.hasUpgrade(enumForceFieldUpgrade)) {
					enumForceFieldUpgrade = (EnumForceFieldUpgrade)tileEntityForceFieldProjector.getFirstUpgradeOfType(EnumForceFieldUpgrade.class, EnumForceFieldUpgrade.NONE);
				}
				
				if (enumForceFieldUpgrade == EnumForceFieldUpgrade.NONE) {
					// no more upgrades to dismount
					WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.forcefield.upgrade.result.noUpgradeToDismount"));
					return true;
				}
				
				if (!entityPlayer.capabilities.isCreativeMode) {
					// dismount the current upgrade item
					ItemStack itemStackDrop = ItemForceFieldUpgrade.getItemStackNoCache(enumForceFieldUpgrade, 1);
					EntityItem entityItem = new EntityItem(world, entityPlayer.posX, entityPlayer.posY + 0.5D, entityPlayer.posZ, itemStackDrop);
					entityItem.setNoPickupDelay();
					world.spawnEntityInWorld(entityItem);
				}
				
				tileEntityForceFieldProjector.dismountUpgrade(enumForceFieldUpgrade);
				// upgrade dismounted
				WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.forcefield.upgrade.result.dismounted"));
				return false;
				
			} else {// default to dismount shape
				if (tileEntityForceFieldProjector.getShape() != EnumForceFieldShape.NONE) {
					if (side == blockState.getValue(BlockProperties.FACING) || (tileEntityForceFieldProjector.isDoubleSided && side.getOpposite() == blockState.getValue(BlockProperties.FACING))) {
						if (!entityPlayer.capabilities.isCreativeMode) {
							// dismount the shape item(s)
							ItemStack itemStackDrop = ItemForceFieldShape.getItemStackNoCache(tileEntityForceFieldProjector.getShape(), tileEntityForceFieldProjector.isDoubleSided ? 2 : 1);
							EntityItem entityItem = new EntityItem(world, entityPlayer.posX, entityPlayer.posY + 0.5D, entityPlayer.posZ, itemStackDrop);
							entityItem.setNoPickupDelay();
							world.spawnEntityInWorld(entityItem);
						}
						
						tileEntityForceFieldProjector.setShape(EnumForceFieldShape.NONE);
						// shape dismounted
						WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.forcefield.shape.result.dismounted"));
					} else {
						// wrong side
						WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.forcefield.shape.result.wrongSide"));
						return true;
					}
					
				} else {
					// no shape to dismount
					WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.forcefield.shape.result.noShapeToDismount"));
					return true;
				}
			}
			
		} else if (itemStackHeld == null) {// no sneaking and no item in hand => show status
			WarpDrive.addChatMessage(entityPlayer, tileEntityForceFieldProjector.getStatus());
			return true;
			
		} else if (itemStackHeld.getItem() instanceof ItemForceFieldShape) {// no sneaking and shape in hand => mounting a shape
			if (side == blockState.getValue(BlockProperties.FACING) || (tileEntityForceFieldProjector.isDoubleSided && side.getOpposite() == blockState.getValue(BlockProperties.FACING))) {
				if (!entityPlayer.capabilities.isCreativeMode) {
					// validate quantity
					if (itemStackHeld.stackSize < (tileEntityForceFieldProjector.isDoubleSided ? 2 : 1)) {
						// not enough shape items
						WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation(
							tileEntityForceFieldProjector.isDoubleSided ?
								"warpdrive.forcefield.shape.result.notEnoughShapes.double" : "warpdrive.forcefield.shape.result.notEnoughShapes.single"));
						return true;
					}
					
					// update player inventory
					itemStackHeld.stackSize -= tileEntityForceFieldProjector.isDoubleSided ? 2 : 1;
					
					// dismount the current shape item(s)
					if (tileEntityForceFieldProjector.getShape() != EnumForceFieldShape.NONE) {
						ItemStack itemStackDrop = ItemForceFieldShape.getItemStackNoCache(tileEntityForceFieldProjector.getShape(), tileEntityForceFieldProjector.isDoubleSided ? 2 : 1);
						EntityItem entityItem = new EntityItem(world, entityPlayer.posX, entityPlayer.posY + 0.5D, entityPlayer.posZ, itemStackDrop);
						entityItem.setNoPickupDelay();
						world.spawnEntityInWorld(entityItem);
					}
				}
				
				// mount the new shape item(s)
				tileEntityForceFieldProjector.setShape(EnumForceFieldShape.get(itemStackHeld.getItemDamage()));
				// shape mounted
				WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.forcefield.shape.result.mounted"));
				
			} else {
				// wrong side
				WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.forcefield.shape.result.wrongSide"));
				return true;
			}
			
		} else if (itemStackHeld.getItem() instanceof ItemForceFieldUpgrade) {// no sneaking and an upgrade in hand => mounting an upgrade
			// validate type
			if (tileEntityForceFieldProjector.getUpgradeMaxCount(enumForceFieldUpgrade) <= 0) {
				// invalid upgrade type
				WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.forcefield.upgrade.result.invalidProjectorUpgrade"));
				return true;
			}
			if (!tileEntityForceFieldProjector.canUpgrade(enumForceFieldUpgrade)) {
				// too many upgrades
				WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.forcefield.upgrade.result.tooManyUpgrades",
					tileEntityForceFieldProjector.getUpgradeMaxCount(enumForceFieldUpgrade)));
				return true;
			}
			
			if (!entityPlayer.capabilities.isCreativeMode) {
				// validate quantity
				if (itemStackHeld.stackSize < 1) {
					// not enough upgrade items
					WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.forcefield.upgrade.result.notEnoughUpgrades"));
					return true;
				}
				
				// update player inventory
				itemStackHeld.stackSize -= 1;
			}
			
			// mount the new upgrade item
			tileEntityForceFieldProjector.mountUpgrade(enumForceFieldUpgrade);
			// upgrade mounted
			WarpDrive.addChatMessage(entityPlayer, new TextComponentTranslation("warpdrive.forcefield.upgrade.result.mounted"));
		}
		
		return false;
	}
	
	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
		return new TileEntityForceFieldProjector();
	}
}
