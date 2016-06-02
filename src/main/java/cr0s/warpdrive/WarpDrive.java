package cr0s.warpdrive;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import cr0s.warpdrive.block.forcefield.*;
import cr0s.warpdrive.item.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import cr0s.warpdrive.block.BlockAirGenerator;
import cr0s.warpdrive.block.BlockChunkLoader;
import cr0s.warpdrive.block.BlockLaser;
import cr0s.warpdrive.block.BlockLaserMedium;
import cr0s.warpdrive.block.TileEntityAbstractChunkLoading;
import cr0s.warpdrive.block.TileEntityAirGenerator;
import cr0s.warpdrive.block.TileEntityChunkLoader;
import cr0s.warpdrive.block.TileEntityLaser;
import cr0s.warpdrive.block.TileEntityLaserMedium;
import cr0s.warpdrive.block.building.BlockShipScanner;
import cr0s.warpdrive.block.building.TileEntityShipScanner;
import cr0s.warpdrive.block.collection.BlockLaserTreeFarm;
import cr0s.warpdrive.block.collection.BlockMiningLaser;
import cr0s.warpdrive.block.collection.TileEntityLaserTreeFarm;
import cr0s.warpdrive.block.collection.TileEntityMiningLaser;
import cr0s.warpdrive.block.detection.BlockCamera;
import cr0s.warpdrive.block.detection.BlockCloakingCoil;
import cr0s.warpdrive.block.detection.BlockCloakingCore;
import cr0s.warpdrive.block.detection.BlockMonitor;
import cr0s.warpdrive.block.detection.BlockRadar;
import cr0s.warpdrive.block.detection.BlockWarpIsolation;
import cr0s.warpdrive.block.detection.TileEntityCamera;
import cr0s.warpdrive.block.detection.TileEntityCloakingCore;
import cr0s.warpdrive.block.detection.TileEntityMonitor;
import cr0s.warpdrive.block.detection.TileEntityRadar;
import cr0s.warpdrive.block.energy.BlockEnanReactorCore;
import cr0s.warpdrive.block.energy.BlockEnanReactorLaser;
import cr0s.warpdrive.block.energy.BlockEnergyBank;
import cr0s.warpdrive.block.energy.BlockIC2reactorLaserMonitor;
import cr0s.warpdrive.block.energy.TileEntityEnanReactorCore;
import cr0s.warpdrive.block.energy.TileEntityEnanReactorLaser;
import cr0s.warpdrive.block.energy.TileEntityEnergyBank;
import cr0s.warpdrive.block.energy.TileEntityIC2reactorLaserMonitor;
import cr0s.warpdrive.block.hull.BlockHullGlass;
import cr0s.warpdrive.block.hull.BlockHullPlain;
import cr0s.warpdrive.block.hull.ItemBlockHull;
import cr0s.warpdrive.block.movement.BlockLift;
import cr0s.warpdrive.block.movement.BlockShipController;
import cr0s.warpdrive.block.movement.BlockShipCore;
import cr0s.warpdrive.block.movement.BlockTransporter;
import cr0s.warpdrive.block.movement.TileEntityLift;
import cr0s.warpdrive.block.movement.TileEntityShipController;
import cr0s.warpdrive.block.movement.TileEntityShipCore;
import cr0s.warpdrive.block.movement.TileEntityTransporter;
import cr0s.warpdrive.block.passive.BlockAir;
import cr0s.warpdrive.block.passive.BlockDecorative;
import cr0s.warpdrive.block.passive.BlockGas;
import cr0s.warpdrive.block.passive.BlockHighlyAdvancedMachine;
import cr0s.warpdrive.block.passive.BlockIridium;
import cr0s.warpdrive.block.passive.BlockTransportBeacon;
import cr0s.warpdrive.block.passive.ItemBlockDecorative;
import cr0s.warpdrive.block.weapon.BlockLaserCamera;
import cr0s.warpdrive.block.weapon.BlockWeaponController;
import cr0s.warpdrive.block.weapon.TileEntityLaserCamera;
import cr0s.warpdrive.block.weapon.TileEntityWeaponController;
import cr0s.warpdrive.command.CommandDebug;
import cr0s.warpdrive.command.CommandEntity;
import cr0s.warpdrive.command.CommandGenerate;
import cr0s.warpdrive.command.CommandInvisible;
import cr0s.warpdrive.command.CommandJumpgates;
import cr0s.warpdrive.command.CommandSpace;
import cr0s.warpdrive.config.Recipes;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.CamerasRegistry;
import cr0s.warpdrive.data.CloakManager;
import cr0s.warpdrive.data.JumpgatesRegistry;
import cr0s.warpdrive.data.StarMapRegistry;
import cr0s.warpdrive.event.ClientHandler;
import cr0s.warpdrive.event.LivingHandler;
import cr0s.warpdrive.event.WorldHandler;
import cr0s.warpdrive.network.PacketHandler;
import cr0s.warpdrive.render.ClientCameraHandler;
import cr0s.warpdrive.render.RenderBlockForceField;
import cr0s.warpdrive.render.RenderBlockStandard;
import cr0s.warpdrive.render.RenderOverlayCamera;
import cr0s.warpdrive.world.BiomeSpace;
import cr0s.warpdrive.world.HyperSpaceWorldProvider;
import cr0s.warpdrive.world.HyperSpaceWorldGenerator;
import cr0s.warpdrive.world.SpaceWorldProvider;
import cr0s.warpdrive.world.SpaceWorldGenerator;

@Mod(modid = WarpDrive.MODID, name = "WarpDrive", version = WarpDrive.VERSION, dependencies = "after:IC2API;" + " after:CoFHCore;" + " after:ComputerCraft;"
		+ " after:OpenComputer;" + " after:CCTurtle;" + " after:gregtech;" + " after:AppliedEnergistics;" + " after:EnderIO;")
/**
 * @author Cr0s
 */
public class WarpDrive implements LoadingCallback {
	public static final String MODID = "WarpDrive";
	public static final String VERSION = "@version@";
	public static final boolean isDev = VERSION.equals("@" + "version" + "@") || VERSION.contains("-dev");
	public static GameProfile gameProfile = new GameProfile(UUID.nameUUIDFromBytes("[WarpDrive]".getBytes()), "[WarpDrive]");
	
	public static Block blockShipCore;
	public static Block blockShipController;
	public static Block blockRadar;
	public static Block blockWarpIsolation;
	public static Block blockAirGenerator;
	public static Block blockLaser;
	public static Block blockLaserCamera;
	public static Block blockWeaponController;
	public static Block blockCamera;
	public static Block blockMonitor;
	public static Block blockLaserMedium;
	public static Block blockMiningLaser;
	public static Block blockLaserTreeFarm;
	public static Block blockLift;
	public static Block blockShipScanner;
	public static Block blockCloakingCore;
	public static Block blockCloakingCoil;
	public static Block blockTransporter;
	public static Block blockIC2reactorLaserMonitor;
	public static Block blockEnanReactorCore;
	public static Block blockEnanReactorLaser;
	public static Block blockEnergyBank;
	public static Block blockAir;
	public static Block blockGas;
	public static Block blockIridium;
	public static Block blockHighlyAdvancedMachine;
	public static Block blockTransportBeacon;
	public static Block blockChunkLoader;
	public static Block blockForceField;
	public static Block blockProjector;
	public static BlockDecorative blockDecorative;
	public static Block[] blockHulls_plain;
	public static Block[] blockHulls_glass;
	public static Block[] blockHulls_stair;
	public static Block[] blockHulls_slab;
	
	public static Item itemIC2reactorLaserFocus;
	public static ItemComponent itemComponent;
	public static ItemUpgrade itemUpgrade;
	public static ItemTuningFork itemTuningRod;
	public static ItemForceFieldShape itemForceFieldShape;
	public static BlockForceFieldUpgrade blockForceFieldUpgrade;
	
	public static final ArmorMaterial armorMaterial = EnumHelper.addArmorMaterial("WARP", 18, new int[] { 2, 6, 5, 2 }, 9);
	public static ItemHelmet itemHelmet;
	public static ItemAirCanisterFull itemAirCanisterFull;
	
	public static DamageAsphyxia damageAsphyxia;
	public static DamageCold damageCold;
	public static DamageLaser damageLaser;
	public static DamageShock damageShock;
	public static DamageTeleportation damageTeleportation;
	public static DamageWarm damageWarm;
	
	public static BiomeGenBase spaceBiome;
	@SuppressWarnings("FieldCanBeLocal")
	private SpaceWorldGenerator spaceWorldGenerator;
	@SuppressWarnings("FieldCanBeLocal")
	private HyperSpaceWorldGenerator hyperSpaceWorldGenerator;
	
	public static Field fieldBlockHardness = null;
	
	// Client settings
	public static final CreativeTabs creativeTabWarpDrive = new CreativeTabWarpDrive("WarpDrive", "WarpDrive").setBackgroundImageName("warpdrive:creativeTab");
	
	@Instance(WarpDrive.MODID)
	public static WarpDrive instance;
	@SidedProxy(clientSide = "cr0s.warpdrive.client.ClientProxy", serverSide = "cr0s.warpdrive.CommonProxy")
	public static CommonProxy proxy;
	
	public static StarMapRegistry starMap;
	public static JumpgatesRegistry jumpgates;
	public static CloakManager cloaks;
	public static CamerasRegistry cameras;
	
	@SuppressWarnings("FieldCanBeLocal")
	private static WarpDrivePeripheralHandler peripheralHandler = null;
	
	public static Logger logger;
	
	@EventHandler
	public void onFMLPreInitialization(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
		WarpDriveConfig.onFMLpreInitialization(event.getModConfigurationDirectory().getAbsolutePath());
		
		if (FMLCommonHandler.instance().getSide().isClient()) {
			MinecraftForge.EVENT_BUS.register(new RenderOverlayCamera(Minecraft.getMinecraft()));
			
			FMLCommonHandler.instance().bus().register(new ClientCameraHandler());
			
			RenderBlockStandard.renderId = RenderingRegistry.getNextAvailableRenderId();
			RenderingRegistry.registerBlockHandler(RenderBlockStandard.instance);
			
			RenderBlockForceField.renderId = RenderingRegistry.getNextAvailableRenderId();
			RenderingRegistry.registerBlockHandler(RenderBlockForceField.instance);
		}
	}
	
	@EventHandler
	public void onFMLInitialization(FMLInitializationEvent event) {
		PacketHandler.init();
		
		WarpDriveConfig.onFMLInitialization();
		
		// open access to Block.blockHardness
		fieldBlockHardness = WarpDrive.getField(Block.class, "blockHardness", "field_149782_v");
		
		// CORE CONTROLLER
		blockShipController = new BlockShipController();
		
		GameRegistry.registerBlock(blockShipController, "blockShipController");
		GameRegistry.registerTileEntity(TileEntityShipController.class, MODID + ":blockShipController");
		
		// SHIP CORE
		blockShipCore = new BlockShipCore();
		
		GameRegistry.registerBlock(blockShipCore, "blockShipCore");
		GameRegistry.registerTileEntity(TileEntityShipCore.class, MODID + ":blockShipCore");
		
		// RADAR
		blockRadar = new BlockRadar();
		
		GameRegistry.registerBlock(blockRadar, "blockRadar");
		GameRegistry.registerTileEntity(TileEntityRadar.class, MODID + ":blockRadar");
		
		// WARP ISOLATION
		blockWarpIsolation = new BlockWarpIsolation();
		
		GameRegistry.registerBlock(blockWarpIsolation, "blockWarpIsolation");
		
		// AIR GENERATOR
		blockAirGenerator = new BlockAirGenerator();
		
		GameRegistry.registerBlock(blockAirGenerator, "blockAirGenerator");
		GameRegistry.registerTileEntity(TileEntityAirGenerator.class, MODID + ":blockAirGenerator");
		
		// AIR BLOCK
		blockAir = new BlockAir();
		
		GameRegistry.registerBlock(blockAir, "blockAir");
		
		// GAS BLOCK
		blockGas = new BlockGas();
		
		GameRegistry.registerBlock(blockGas, "blockGas");
		
		// LASER EMITTER
		blockLaser = new BlockLaser();
		
		GameRegistry.registerBlock(blockLaser, "blockLaser");
		GameRegistry.registerTileEntity(TileEntityLaser.class, MODID + ":blockLaser");
		
		// LASER EMITTER WITH CAMERA
		blockLaserCamera = new BlockLaserCamera();
		
		GameRegistry.registerBlock(blockLaserCamera, "blockLaserCamera");
		GameRegistry.registerTileEntity(TileEntityLaserCamera.class, MODID + ":blockLaserCamera");
		
		// LASER EMITTER WITH CAMERA
		blockWeaponController = new BlockWeaponController();
		
		GameRegistry.registerBlock(blockWeaponController, "blockWeaponController");
		GameRegistry.registerTileEntity(TileEntityWeaponController.class, MODID + ":blockWeaponController");
		
		// CAMERA
		blockCamera = new BlockCamera();
		
		GameRegistry.registerBlock(blockCamera, "blockCamera");
		GameRegistry.registerTileEntity(TileEntityCamera.class, MODID + ":blockCamera");
		
		// MONITOR
		blockMonitor = new BlockMonitor();
		
		GameRegistry.registerBlock(blockMonitor, "blockMonitor");
		GameRegistry.registerTileEntity(TileEntityMonitor.class, MODID + ":blockMonitor");
		
		// MINING LASER
		blockMiningLaser = new BlockMiningLaser();
		
		GameRegistry.registerBlock(blockMiningLaser, "blockMiningLaser");
		GameRegistry.registerTileEntity(TileEntityMiningLaser.class, MODID + ":blockMiningLaser");
		
		// LASER TREE FARM
		blockLaserTreeFarm = new BlockLaserTreeFarm();
		
		GameRegistry.registerBlock(blockLaserTreeFarm, "blockLaserTreeFarm");
		GameRegistry.registerTileEntity(TileEntityLaserTreeFarm.class, MODID + ":blockLaserTreeFarm");
		
		// LASER MEDIUM
		blockLaserMedium = new BlockLaserMedium();
		
		GameRegistry.registerBlock(blockLaserMedium, "blockLaserMedium");
		GameRegistry.registerTileEntity(TileEntityLaserMedium.class, MODID + ":blockLaserMedium");
		
		// LIFT
		blockLift = new BlockLift();
		
		GameRegistry.registerBlock(blockLift, "blockLift");
		GameRegistry.registerTileEntity(TileEntityLift.class, MODID + ":blockLift");
		
		// IRIDIUM BLOCK
		blockIridium = new BlockIridium();
		
		GameRegistry.registerBlock(blockIridium, "blockIridium");
		
		// HIGHLY ADVANCED MACHINE BLOCK
		blockHighlyAdvancedMachine = new BlockHighlyAdvancedMachine();
		
		GameRegistry.registerBlock(blockHighlyAdvancedMachine, "blockHighlyAdvancedMachine");
		
		// SHIP SCANNER
		blockShipScanner = new BlockShipScanner();
		
		GameRegistry.registerBlock(blockShipScanner, "blockShipScanner");
		GameRegistry.registerTileEntity(TileEntityShipScanner.class, MODID + ":blockShipScanner");
		
		// CLOAKING DEVICE CORE
		blockCloakingCore = new BlockCloakingCore();
		
		GameRegistry.registerBlock(blockCloakingCore, "blockCloakingCore");
		GameRegistry.registerTileEntity(TileEntityCloakingCore.class, MODID + ":blockCloakingCore");
		
		// CLOAKING DEVICE COIL
		blockCloakingCoil = new BlockCloakingCoil();
		
		GameRegistry.registerBlock(blockCloakingCoil, "blockCloakingCoil");
		
		// TRANSPORTER
		blockTransporter = new BlockTransporter();
		
		GameRegistry.registerBlock(blockTransporter, "blockTransporter");
		GameRegistry.registerTileEntity(TileEntityTransporter.class, MODID + ":blockTransporter");
		
		// IC2 REACTOR LASER MONITOR
		if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			blockIC2reactorLaserMonitor = new BlockIC2reactorLaserMonitor();
			
			GameRegistry.registerBlock(blockIC2reactorLaserMonitor, "blockIC2reactorLaserMonitor");
			GameRegistry.registerTileEntity(TileEntityIC2reactorLaserMonitor.class, MODID + ":blockIC2reactorLaserMonitor");
		}
		
		// TRANSPORT BEACON
		blockTransportBeacon = new BlockTransportBeacon();
		
		GameRegistry.registerBlock(blockTransportBeacon, "blockTransportBeacon");
		
		// POWER REACTOR, LASER, STORE
		blockEnanReactorCore = new BlockEnanReactorCore();
		GameRegistry.registerBlock(blockEnanReactorCore, "blockEnanReactorCore");
		GameRegistry.registerTileEntity(TileEntityEnanReactorCore.class, MODID + ":blockEnanReactorCore");
		
		blockEnanReactorLaser = new BlockEnanReactorLaser();
		GameRegistry.registerBlock(blockEnanReactorLaser, "blockEnanReactorLaser");
		GameRegistry.registerTileEntity(TileEntityEnanReactorLaser.class, MODID + ":blockEnanReactorLaser");
		
		blockEnergyBank = new BlockEnergyBank();
		GameRegistry.registerBlock(blockEnergyBank, "blockEnergyBank");
		GameRegistry.registerTileEntity(TileEntityEnergyBank.class, MODID + ":blockEnergyBank");
		
		// CHUNK LOADER
		blockChunkLoader = new BlockChunkLoader();
		GameRegistry.registerBlock(blockChunkLoader, "blockChunkLoader");
		GameRegistry.registerTileEntity(TileEntityChunkLoader.class, MODID + ":blockChunkLoader");
		
		for(byte tier = 1; tier <= 3; tier++) {
			// FORCE FIELD
			blockForceField = new BlockForceField(tier);
			GameRegistry.registerBlock(blockForceField, "blockForceField" + tier);
			GameRegistry.registerTileEntity(TileEntityForceField.class, MODID + ":blockForceField" + tier);
			
			// FORCE FIELD PROJECTOR
			blockProjector = new BlockProjector(tier);
			GameRegistry.registerBlock(blockProjector, "blockProjector" + tier);
			GameRegistry.registerTileEntity(TileEntityProjector.class, MODID + ":blockProjector" + tier);
/*	TODO		
			// FORCE FIELD CORE
			blockForceFieldCore = new BlockForceFieldCore(tier);
			GameRegistry.registerBlock(blockProjector, "blockForceFieldCore" + tier);
			GameRegistry.registerTileEntity(TileEntityForceFieldCore.class, MODID + ":blockForceFieldCore" + tier);
			*/
		}
		/* TODO
		// SECURITY STATION
		blockSecurityStation = new BlockSecurityStation();
		GameRegistry.registerBlock(blockSecurityStation, "blockSecurityStation");
		GameRegistry.registerTileEntity(TileEntitySecurityStation.class, MODID + ":blockSecurityStation");
		*/
		// DECORATIVE
		blockDecorative = new BlockDecorative();
		GameRegistry.registerBlock(blockDecorative, ItemBlockDecorative.class, "blockDecorative");
		
		// HULL BLOCKS
		blockHulls_plain = new Block[3];
		blockHulls_glass = new Block[3];
		blockHulls_stair = new Block[3 * 16];
		blockHulls_slab = new Block[3 * 16];
		
		for(int tier = 1; tier <= 3; tier++) {
			int index = tier - 1;
			blockHulls_plain[index] = new BlockHullPlain(tier);
			blockHulls_glass[index] = new BlockHullGlass(tier);
			GameRegistry.registerBlock(blockHulls_plain[index], ItemBlockHull.class, "blockHull" + tier + "_plain");
			GameRegistry.registerBlock(blockHulls_glass[index], ItemBlockHull.class, "blockHull" + tier + "_glass");
		}
		
		// REACTOR LASER FOCUS
		if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			itemIC2reactorLaserFocus = new ItemIC2reactorLaserFocus();
			GameRegistry.registerItem(itemIC2reactorLaserFocus, "itemIC2reactorLaserFocus");
		}
		
		// COMPONENT ITEMS
		itemComponent = new ItemComponent();
		GameRegistry.registerItem(itemComponent, "itemComponent");
		
		itemHelmet = new ItemHelmet(armorMaterial, 0);
		GameRegistry.registerItem(itemHelmet, "itemHelmet");
		
		itemAirCanisterFull = new ItemAirCanisterFull();
		GameRegistry.registerItem(itemAirCanisterFull, "itemAirCanisterFull");
		
		if (WarpDriveConfig.RECIPES_ENABLE_VANILLA) {
			itemUpgrade = new ItemUpgrade();
			GameRegistry.registerItem(itemUpgrade, "itemUpgrade");
		}
		
		// TOOL ITEMS
		itemTuningRod = new ItemTuningFork();
		GameRegistry.registerItem(itemTuningRod, "itemTuningRod");
		
		// FORCEFIELD UPGRADES
		itemForceFieldShape = new ItemForceFieldShape();
		GameRegistry.registerItem(itemForceFieldShape, "itemForceFieldShape");
		
		blockForceFieldUpgrade = new BlockForceFieldUpgrade();
		GameRegistry.registerBlock(blockForceFieldUpgrade, ItemBlockForceFieldUpgrade.class, "blockForceFieldUpgrade");
		GameRegistry.registerTileEntity(TileEntityForceFieldUpgrade.class, MODID + ":blockForceFieldUpgrade");
		
		// DAMAGE SOURCES
		damageAsphyxia = new DamageAsphyxia();
		damageCold = new DamageCold();
		damageLaser = new DamageLaser();
		damageShock = new DamageShock();
		damageTeleportation = new DamageTeleportation();
		damageWarm = new DamageWarm();
		
		proxy.registerEntities();
		
		ForgeChunkManager.setForcedChunkLoadingCallback(instance, instance);
		
		spaceWorldGenerator = new SpaceWorldGenerator();
		GameRegistry.registerWorldGenerator(spaceWorldGenerator, 0);
		hyperSpaceWorldGenerator = new HyperSpaceWorldGenerator();
		GameRegistry.registerWorldGenerator(hyperSpaceWorldGenerator, 0);
		
		spaceBiome = (new BiomeSpace(WarpDriveConfig.G_SPACE_BIOME_ID)).setColor(0).setDisableRain().setBiomeName("Space");
		BiomeDictionary.registerBiomeType(spaceBiome, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.WASTELAND);
		DimensionManager.registerProviderType(WarpDriveConfig.G_SPACE_PROVIDER_ID, SpaceWorldProvider.class, true);
		DimensionManager.registerDimension(WarpDriveConfig.G_SPACE_DIMENSION_ID, WarpDriveConfig.G_SPACE_PROVIDER_ID);
		
		DimensionManager.registerProviderType(WarpDriveConfig.G_HYPERSPACE_PROVIDER_ID, HyperSpaceWorldProvider.class, true);
		DimensionManager.registerDimension(WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID, WarpDriveConfig.G_HYPERSPACE_PROVIDER_ID);
		
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			creativeTabWarpDrive.setBackgroundImageName("items.png");
		}
	}
	
	@EventHandler
	public void onFMLPostInitialization(FMLPostInitializationEvent event) {
		DimensionManager.getWorld(WarpDriveConfig.G_SPACE_DIMENSION_ID);
		DimensionManager.getWorld(WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID);
		
		WarpDriveConfig.onFMLPostInitialization();
		
		if (WarpDriveConfig.RECIPES_ENABLE_DYNAMIC) {
			Recipes.initDynamic();
		} else {
			if (WarpDriveConfig.isIndustrialCraft2Loaded && WarpDriveConfig.RECIPES_ENABLE_IC2) {
				Recipes.initIC2();
			}
			if (WarpDriveConfig.isIndustrialCraft2Loaded && WarpDriveConfig.RECIPES_ENABLE_HARD_IC2) {
				Recipes.initHardIC2();
			}
			if (WarpDriveConfig.RECIPES_ENABLE_VANILLA) {
				Recipes.initVanilla();
			}
		}
		
		// Registers
		starMap = new StarMapRegistry();
		jumpgates = new JumpgatesRegistry();
		cloaks = new CloakManager();
		cameras = new CamerasRegistry();
		
		// Event handlers
		MinecraftForge.EVENT_BUS.register(new ClientHandler());
		
		MinecraftForge.EVENT_BUS.register(new LivingHandler());
		
		if (WarpDriveConfig.isComputerCraftLoaded) {
			peripheralHandler = new WarpDrivePeripheralHandler();
			peripheralHandler.register();
		}
		
		WorldHandler worldHandler = new WorldHandler();
		MinecraftForge.EVENT_BUS.register(worldHandler);
		FMLCommonHandler.instance().bus().register(worldHandler);
	}
	
	@EventHandler
	public void onFMLServerStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandGenerate());
		event.registerServerCommand(new CommandSpace());
		event.registerServerCommand(new CommandInvisible());
		event.registerServerCommand(new CommandJumpgates());
		event.registerServerCommand(new CommandDebug());
		event.registerServerCommand(new CommandEntity());
	}
	
	public Ticket registerChunkLoadTE(TileEntityAbstractChunkLoading tileEntity, boolean refreshLoading) {
		World worldObj = tileEntity.getWorldObj();
		if (ForgeChunkManager.ticketCountAvailableFor(this, worldObj) > 0) {
			Ticket ticket = ForgeChunkManager.requestTicket(this, worldObj, Type.NORMAL);
			if (ticket != null) {
				tileEntity.giveTicket(ticket); // FIXME calling the caller is a bad idea
				if (refreshLoading)
					tileEntity.refreshLoading();
				return ticket;
			} else {
				WarpDrive.logger.error("Ticket not granted");
			}
		} else {
			WarpDrive.logger.error("No tickets left!");
		}
		return null;
	}
	
	public Ticket registerChunkLoadTE(TileEntityAbstractChunkLoading te) {
		return registerChunkLoadTE(te, true);
	}
	
	public Ticket getTicket(TileEntityAbstractChunkLoading te) {
		return registerChunkLoadTE(te, false);
	}
	
	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) {
		for (Ticket ticket : tickets) {
			NBTTagCompound data = ticket.getModData();
			if (data != null) {
				int w = data.getInteger("ticketWorldObj");
				int x = data.getInteger("ticketX");
				int y = data.getInteger("ticketY");
				int z = data.getInteger("ticketZ");
				if (w != 0 || x != 0 || y != 0 || z != 0) {
					WorldServer worldServer = DimensionManager.getWorld(w);
					if (worldServer != null) {// skip non-loaded worlds
						TileEntity tileEntity = worldServer.getTileEntity(x, y, z);
						if (tileEntity != null && tileEntity instanceof TileEntityAbstractChunkLoading) {
							if (((TileEntityAbstractChunkLoading) tileEntity).shouldChunkLoad()) {
								WarpDrive.logger.info("ChunkLoadingTicket is loading " + tileEntity);
								((TileEntityAbstractChunkLoading) tileEntity).giveTicket(ticket);
								((TileEntityAbstractChunkLoading) tileEntity).refreshLoading(true);
								return;
							}
						}
					}
				}
			}
			
			ForgeChunkManager.releaseTicket(ticket);
		}
	}
	
	public static void addChatMessage(final ICommandSender sender, final String message) {
		String[] lines = message.replace("§", "" + (char)167).replace("\\n", "\n").split("\n");
		for (String line : lines) {
			sender.addChatMessage(new ChatComponentText(line));
		}
		
		// logger.info(message);
	}
	
	@Mod.EventHandler
	public void onFMLMissingMappings(FMLMissingMappingsEvent event) {
		for (FMLMissingMappingsEvent.MissingMapping mapping: event.get()) {
			if (mapping.type == GameRegistry.Type.ITEM) {
				switch (mapping.name) {
					case "WarpDrive:airBlock":
						mapping.remap(Item.getItemFromBlock(blockAir));
						break;
					case "WarpDrive:airCanisterFull":
						mapping.remap(itemAirCanisterFull);
						break;
					case "WarpDrive:airgenBlock":
						mapping.remap(Item.getItemFromBlock(blockAirGenerator));
						break;
					case "WarpDrive:blockHAMachine":
						mapping.remap(Item.getItemFromBlock(blockHighlyAdvancedMachine));
						break;
					case "WarpDrive:boosterBlock":
						mapping.remap(Item.getItemFromBlock(blockLaserMedium));
						break;
					case "WarpDrive:cameraBlock":
						mapping.remap(Item.getItemFromBlock(blockCamera));
						break;
					case "WarpDrive:chunkLoader":
						mapping.remap(Item.getItemFromBlock(blockChunkLoader));
						break;
					case "WarpDrive:cloakBlock":
						mapping.remap(Item.getItemFromBlock(blockCloakingCore));
						break;
					case "WarpDrive:cloakCoilBlock":
						mapping.remap(Item.getItemFromBlock(blockCloakingCoil));
						break;
					case "WarpDrive:component":
						mapping.remap(itemComponent);
						break;
					case "WarpDrive:decorative":
						mapping.remap(Item.getItemFromBlock(blockDecorative));
						break;
					case "WarpDrive:gasBlock":
						mapping.remap(Item.getItemFromBlock(blockGas));
						break;
					case "WarpDrive:helmet":
						mapping.remap(itemHelmet);
						break;
					case "WarpDrive:iridiumBlock":
						mapping.remap(Item.getItemFromBlock(blockIridium));
						break;
					case "WarpDrive:isolationBlock":
						mapping.remap(Item.getItemFromBlock(blockWarpIsolation));
						break;
					case "WarpDrive:laserBlock":
						mapping.remap(Item.getItemFromBlock(blockLaser));
						break;
					case "WarpDrive:laserCamBlock":
						mapping.remap(Item.getItemFromBlock(blockLaserCamera));
						break;
					case "WarpDrive:laserTreeFarmBlock":
						mapping.remap(Item.getItemFromBlock(blockLaserTreeFarm));
						break;
					case "WarpDrive:liftBlock":
						mapping.remap(Item.getItemFromBlock(blockLift));
						break;
					case "WarpDrive:miningLaserBlock":
						mapping.remap(Item.getItemFromBlock(blockMiningLaser));
						break;
					case "WarpDrive:monitorBlock":
						mapping.remap(Item.getItemFromBlock(blockMonitor));
						break;
					case "WarpDrive:powerLaser":
						mapping.remap(Item.getItemFromBlock(blockEnanReactorLaser));
						break;
					case "WarpDrive:powerReactor":
						mapping.remap(Item.getItemFromBlock(blockEnanReactorCore));
						break;
					case "WarpDrive:powerStore":
						mapping.remap(Item.getItemFromBlock(blockEnergyBank));
						break;
					case "WarpDrive:protocolBlock":
						mapping.remap(Item.getItemFromBlock(blockShipController));
						break;
					case "WarpDrive:radarBlock":
						mapping.remap(Item.getItemFromBlock(blockRadar));
						break;
					case "WarpDrive:reactorLaserFocus":
						mapping.remap(itemIC2reactorLaserFocus);
						break;
					case "WarpDrive:reactorMonitor":
						mapping.remap(Item.getItemFromBlock(blockIC2reactorLaserMonitor));
						break;
					case "WarpDrive:scannerBlock":
						mapping.remap(Item.getItemFromBlock(blockShipScanner));
						break;
					case "WarpDrive:transportBeacon":
						mapping.remap(Item.getItemFromBlock(blockTransportBeacon));
						break;
					case "WarpDrive:transporter":
						mapping.remap(Item.getItemFromBlock(blockTransporter));
						break;
					case "WarpDrive:upgrade":
						mapping.remap(itemUpgrade);
						break;
					case "WarpDrive:warpCore":
						mapping.remap(Item.getItemFromBlock(blockShipCore));
						break;
				}
			} else if (mapping.type == GameRegistry.Type.BLOCK) {
				switch (mapping.name) {
					case "WarpDrive:airBlock":
						mapping.remap(blockAir);
						break;
					case "WarpDrive:airgenBlock":
						mapping.remap(blockAirGenerator);
						break;
					case "WarpDrive:blockHAMachine":
						mapping.remap(blockHighlyAdvancedMachine);
						break;
					case "WarpDrive:boosterBlock":
						mapping.remap(blockLaserMedium);
						break;
					case "WarpDrive:cameraBlock":
						mapping.remap(blockCamera);
						break;
					case "WarpDrive:chunkLoader":
						mapping.remap(blockChunkLoader);
						break;
					case "WarpDrive:cloakBlock":
						mapping.remap(blockCloakingCore);
						break;
					case "WarpDrive:cloakCoilBlock":
						mapping.remap(blockCloakingCoil);
						break;
					case "WarpDrive:decorative":
						mapping.remap(blockDecorative);
						break;
					case "WarpDrive:gasBlock":
						mapping.remap(blockGas);
						break;
					case "WarpDrive:iridiumBlock":
						mapping.remap(blockIridium);
						break;
					case "WarpDrive:isolationBlock":
						mapping.remap(blockWarpIsolation);
						break;
					case "WarpDrive:laserBlock":
						mapping.remap(blockLaser);
						break;
					case "WarpDrive:laserCamBlock":
						mapping.remap(blockLaserCamera);
						break;
					case "WarpDrive:laserTreeFarmBlock":
						mapping.remap(blockLaserTreeFarm);
						break;
					case "WarpDrive:liftBlock":
						mapping.remap(blockLift);
						break;
					case "WarpDrive:miningLaserBlock":
						mapping.remap(blockMiningLaser);
						break;
					case "WarpDrive:monitorBlock":
						mapping.remap(blockMonitor);
						break;
					case "WarpDrive:powerLaser":
						mapping.remap(blockEnanReactorLaser);
						break;
					case "WarpDrive:powerReactor":
						mapping.remap(blockEnanReactorCore);
						break;
					case "WarpDrive:powerStore":
						mapping.remap(blockEnergyBank);
						break;
					case "WarpDrive:protocolBlock":
						mapping.remap(blockShipController);
						break;
					case "WarpDrive:radarBlock":
						mapping.remap(blockRadar);
						break;
					case "WarpDrive:reactorMonitor":
						mapping.remap(blockIC2reactorLaserMonitor);
						break;
					case "WarpDrive:scannerBlock":
						mapping.remap(blockShipScanner);
						break;
					case "WarpDrive:transportBeacon":
						mapping.remap(blockTransportBeacon);
						break;
					case "WarpDrive:transporter":
						mapping.remap(blockTransporter);
						break;
					case "WarpDrive:warpCore":
						mapping.remap(blockShipCore);
						break;
				}
			}
		}
	}
	
	// add tooltip information with text formatting and line splitting
	// will ensure it fits on minimum screen width
	public static void addTooltip(List<String> list, String tooltip) {
		tooltip = tooltip.replace("§", "" + (char)167).replace("\\n", "\n").replace("|", "\n");
		
		String[] split = tooltip.split("\n");
		for (String line : split) {
			String lineRemaining = line;
			while (lineRemaining.length() > 38) {
				int index = lineRemaining.substring(0, 38).lastIndexOf(' ');
				if (index == -1) {
					list.add(lineRemaining);
					lineRemaining = "";
				} else {
					list.add(lineRemaining.substring(0, index));
					lineRemaining = lineRemaining.substring(index + 1);
				}
			}
			
			list.add(lineRemaining);
		}
	}
	
	public static Field getField(Class<?> clazz, String deobfuscatedName, String obfuscatedName) {
		Field fieldToReturn = null;
		
		try {
			fieldToReturn = clazz.getDeclaredField(deobfuscatedName);
		} catch (Exception exception1) {
			try {
				fieldToReturn = clazz.getDeclaredField(obfuscatedName);
			} catch (Exception exception2) {
				exception2.printStackTrace();
				String map = "";
				for(Field fieldDeclared : clazz.getDeclaredFields()) {
					if (!map.isEmpty()) {
						map += ", ";
					}
					map += fieldDeclared.getName();
				}
				WarpDrive.logger.error(String.format("Unable to find %1$s field in %2$s class. Available fields are: %3$s",
						deobfuscatedName, clazz.toString(), map));
			}
		}
		if (fieldToReturn != null) {
			fieldToReturn.setAccessible(true);
		}
		return fieldToReturn;
	}
}
