package cr0s.warpdrive;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import cr0s.warpdrive.block.*;
import cr0s.warpdrive.block.forcefield.*;
import cr0s.warpdrive.block.hull.BlockHullStairs;
import cr0s.warpdrive.item.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import cr0s.warpdrive.block.BlockAirGenerator;
import cr0s.warpdrive.block.BlockChunkLoader;
import cr0s.warpdrive.block.BlockLaser;
import cr0s.warpdrive.block.BlockLaserMedium;
import cr0s.warpdrive.block.TileEntityAbstractChunkLoading;
import cr0s.warpdrive.block.building.BlockShipScanner;
import cr0s.warpdrive.block.collection.BlockLaserTreeFarm;
import cr0s.warpdrive.block.collection.BlockMiningLaser;
import cr0s.warpdrive.block.detection.BlockCamera;
import cr0s.warpdrive.block.detection.BlockCloakingCoil;
import cr0s.warpdrive.block.detection.BlockCloakingCore;
import cr0s.warpdrive.block.detection.BlockMonitor;
import cr0s.warpdrive.block.detection.BlockRadar;
import cr0s.warpdrive.block.detection.BlockWarpIsolation;
import cr0s.warpdrive.block.energy.BlockEnanReactorCore;
import cr0s.warpdrive.block.energy.BlockEnanReactorLaser;
import cr0s.warpdrive.block.energy.BlockEnergyBank;
import cr0s.warpdrive.block.energy.BlockIC2reactorLaserMonitor;
import cr0s.warpdrive.block.hull.BlockHullGlass;
import cr0s.warpdrive.block.hull.BlockHullPlain;
import cr0s.warpdrive.block.movement.BlockLift;
import cr0s.warpdrive.block.movement.BlockShipController;
import cr0s.warpdrive.block.movement.BlockShipCore;
import cr0s.warpdrive.block.movement.BlockTransporter;
import cr0s.warpdrive.block.passive.BlockAir;
import cr0s.warpdrive.block.passive.BlockDecorative;
import cr0s.warpdrive.block.passive.BlockGas;
import cr0s.warpdrive.block.passive.BlockHighlyAdvancedMachine;
import cr0s.warpdrive.block.passive.BlockIridium;
import cr0s.warpdrive.block.passive.BlockTransportBeacon;
import cr0s.warpdrive.block.weapon.BlockLaserCamera;
import cr0s.warpdrive.block.weapon.BlockWeaponController;
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
import cr0s.warpdrive.render.RenderOverlayCamera;
import cr0s.warpdrive.world.BiomeSpace;
import cr0s.warpdrive.world.HyperSpaceWorldProvider;
import cr0s.warpdrive.world.HyperSpaceWorldGenerator;
import cr0s.warpdrive.world.SpaceWorldGenerator;

@Mod(modid = WarpDrive.MODID, name = "WarpDrive", version = WarpDrive.VERSION, dependencies = "after:IC2API;" + " after:CoFHCore;" + " after:ComputerCraft;"
		+ " after:OpenComputer;" + " after:CCTurtle;" + " after:gregtech;" + " after:AppliedEnergistics;" + " after:EnderIO;")
public class WarpDrive implements LoadingCallback {
	public static final String MODID = "WarpDrive";
	public static final String VERSION = "@version@";
	public static final String PREFIX = MODID + ":";
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
	public static Block[] blockForceFields;
	public static Block[] blockForceFieldProjectors;
	public static Block[] blockForceFieldRelays;
	public static BlockDecorative blockDecorative;
	public static Block[] blockHulls_plain;
	public static Block[] blockHulls_glass;
	public static Block[][] blockHulls_stairs;
	public static Block[][] blockHulls_slab;
	
	public static Item itemIC2reactorLaserFocus;
	public static ItemComponent itemComponent;
	public static ItemCrystalToken itemCrystalToken;
	public static ItemUpgrade itemUpgrade;
	public static ItemTuningFork itemTuningRod;
	public static ItemForceFieldShape itemForceFieldShape;
	public static ItemForceFieldUpgrade itemForceFieldUpgrade;
	
	public static final ArmorMaterial armorMaterial = EnumHelper.addArmorMaterial("WARP", "warp", 18, new int[] { 2, 6, 5, 2 }, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
	public static ItemHelmet itemHelmet;
	public static ItemAirCanisterFull itemAirCanisterFull;
	
	public static DamageAsphyxia damageAsphyxia;
	public static DamageCold damageCold;
	public static DamageLaser damageLaser;
	public static DamageShock damageShock;
	public static DamageTeleportation damageTeleportation;
	public static DamageWarm damageWarm;
	
	public static Biome spaceBiome;
	public static DimensionType dimensionTypeSpace;
	public static DimensionType dimensionTypeHyperSpace;
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

			MinecraftForge.EVENT_BUS.register(new ClientCameraHandler());
			
			// @TODO MC1.10 force field rendering
			// RenderBlockForceField.renderId = RenderingRegistry.getNextAvailableRenderId();
			// RenderingRegistry.registerBlockHandler(RenderBlockForceField.instance);
		}
	}
	
	@EventHandler
	public void onFMLInitialization(FMLInitializationEvent event) {
		PacketHandler.init();
		
		WarpDriveConfig.onFMLInitialization();
		
		// open access to Block.blockHardness
		fieldBlockHardness = WarpDrive.getField(Block.class, "blockHardness", "field_149782_v");

		// building blocks
		blockShipScanner = new BlockShipScanner("blockShipScanner");
		
		// collection blocks
		blockMiningLaser = new BlockMiningLaser("blockMiningLaser");
		blockLaserTreeFarm = new BlockLaserTreeFarm("blockLaserTreeFarm");
		
		// detection blocks
		blockCamera = new BlockCamera("blockCamera");
		blockCloakingCore = new BlockCloakingCore("blockCloakingCore");
		blockCloakingCoil = new BlockCloakingCoil("blockCloakingCoil");
		blockMonitor = new BlockMonitor("blockMonitor");
		blockRadar = new BlockRadar("blockRadar");
		blockWarpIsolation = new BlockWarpIsolation("blockWarpIsolation");
		
		// energy blocks and items
		blockEnanReactorCore = new BlockEnanReactorCore("blockEnanReactorCore");
		blockEnanReactorLaser = new BlockEnanReactorLaser("blockEnanReactorLaser");
		blockEnergyBank = new BlockEnergyBank("blockEnergyBank");
		
		if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			blockIC2reactorLaserMonitor = new BlockIC2reactorLaserMonitor("blockIC2reactorLaserMonitor");
			itemIC2reactorLaserFocus = new ItemIC2reactorLaserFocus("itemIC2reactorLaserFocus");
		}
		
		// movement blocks
		blockLift = new BlockLift("blockLift");
		blockShipController = new BlockShipController("blockShipController");
		blockShipCore = new BlockShipCore("blockShipCore");
		blockTransporter = new BlockTransporter("blockTransporter");
		
		// passive blocks
		blockAir = new BlockAir("blockAir");
		blockDecorative = new BlockDecorative("blockDecorative");
		blockGas = new BlockGas("blockGas");
		blockHighlyAdvancedMachine = new BlockHighlyAdvancedMachine("blockHighlyAdvancedMachine");
		blockIridium = new BlockIridium("blockIridium");
		blockTransportBeacon = new BlockTransportBeacon("blockTransportBeacon");
		
		// weapon blocks
		blockLaserCamera = new BlockLaserCamera("blockLaserCamera");
		blockWeaponController = new BlockWeaponController("blockWeaponController");
		
		// common blocks
		blockAirGenerator = new BlockAirGenerator("blockAirGenerator");
		blockChunkLoader = new BlockChunkLoader("blockChunkLoader");
		blockLaser = new BlockLaser("blockLaser");
		blockLaserMedium = new BlockLaserMedium("blockLaserMedium");
		
		// force field blocks and items
		blockForceFields = new Block[3];
		blockForceFieldProjectors = new Block[3];
		blockForceFieldRelays = new Block[3];
		for(byte tier = 1; tier <= 3; tier++) {
			int index = tier - 1;
			blockForceFields[index] = new BlockForceField("blockForceField" + tier, tier);
			blockForceFieldProjectors[index] = new BlockForceFieldProjector("blockProjector" + tier, tier);
			blockForceFieldRelays[index] = new BlockForceFieldRelay("blockForceFieldRelay" + tier, tier);
		}
		/* @TODO security station
		blockSecurityStation = new BlockSecurityStation("blockSecurityStation");
		*/
		itemForceFieldShape = new ItemForceFieldShape("itemForceFieldShape");
		itemForceFieldUpgrade = new ItemForceFieldUpgrade("itemForceFieldUpgrade");
		
		// hull blocks
		blockHulls_plain = new Block[3];
		blockHulls_glass = new Block[3];
		blockHulls_stairs = new Block[3][16];
		blockHulls_slab = new Block[3][16];
		
		for(int tier = 1; tier <= 3; tier++) {
			int index = tier - 1;
			blockHulls_plain[index] = new BlockHullPlain("blockHull" + tier + "_plain", tier);
			blockHulls_glass[index] = new BlockHullGlass("blockHull" + tier + "_glass", tier);
			for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
				blockHulls_stairs[index][enumDyeColor.getMetadata()] = new BlockHullStairs("blockHull" + tier + "_stairs_" + enumDyeColor.getName(), blockHulls_plain[index].getStateFromMeta(enumDyeColor.getMetadata()), tier);
			}
		}
		
		// generic items
		itemComponent = new ItemComponent("itemComponent");
		itemCrystalToken = new ItemCrystalToken("itemCrystalToken");
		itemHelmet = new ItemHelmet("itemHelmet", armorMaterial, EntityEquipmentSlot.HEAD);
		itemAirCanisterFull = new ItemAirCanisterFull("itemAirCanisterFull");
		
		if (WarpDriveConfig.RECIPES_ENABLE_VANILLA) {
			itemUpgrade = new ItemUpgrade("itemUpgrade");
		}
		
		// tool items
		itemTuningRod = new ItemTuningFork("itemTuningRod");
				
		// damage sources
		damageAsphyxia = new DamageAsphyxia();
		damageCold = new DamageCold();
		damageLaser = new DamageLaser();
		damageShock = new DamageShock();
		damageTeleportation = new DamageTeleportation();
		damageWarm = new DamageWarm();
		
		// entities
		proxy.registerEntities();
		
		// chunk loading
		ForgeChunkManager.setForcedChunkLoadingCallback(instance, instance);
		
		// world generation
		spaceWorldGenerator = new SpaceWorldGenerator();
		GameRegistry.registerWorldGenerator(spaceWorldGenerator, 0);
		hyperSpaceWorldGenerator = new HyperSpaceWorldGenerator();
		GameRegistry.registerWorldGenerator(hyperSpaceWorldGenerator, 0);
		
		Biome.BiomeProperties biomeProperties = new Biome.BiomeProperties("Space").setRainDisabled().setWaterColor(0);
		spaceBiome = (new BiomeSpace(biomeProperties));
		BiomeDictionary.registerBiomeType(spaceBiome, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.WASTELAND);
		
		dimensionTypeSpace = DimensionType.register("Space", "_space", WarpDriveConfig.G_SPACE_PROVIDER_ID, HyperSpaceWorldProvider.class, true);
		DimensionManager.registerDimension(WarpDriveConfig.G_SPACE_DIMENSION_ID, dimensionTypeSpace);

		dimensionTypeHyperSpace = DimensionType.register("Hyperspace", "_hyperspace", WarpDriveConfig.G_HYPERSPACE_PROVIDER_ID, HyperSpaceWorldProvider.class, true);
		DimensionManager.registerDimension(WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID, dimensionTypeHyperSpace);
		
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
	
	private Ticket registerChunkLoadTE(TileEntityAbstractChunkLoading tileEntity, boolean refreshLoading) {
		World worldObj = tileEntity.getWorld();
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
						TileEntity tileEntity = worldServer.getTileEntity(new BlockPos(x, y, z));
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
	
	public static void addChatMessage(final ICommandSender sender, final ITextComponent textComponent) {
		if (sender == null) {
			logger.error("Unable to send message to NULL sender: " + textComponent.getFormattedText());
			return;
		}
		String[] lines = textComponent.getFormattedText().replace("§", "" + (char)167).replace("\\n", "\n").split("\n");
		for (String line : lines) {
			sender.addChatMessage(new TextComponentString(line));
		}
		
		// logger.info(message);
	}
	
	@SuppressWarnings("ConstantConditions")
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
		final String charFormatting = "" + (char)167;
		tooltip = tooltip.replace("§", charFormatting).replace("\\n", "\n").replace("|", "\n");
		tooltip = tooltip.replace(charFormatting + "r", charFormatting + "7");
		
		String[] split = tooltip.split("\n");
		for (String line : split) {
			String lineRemaining = line;
			String formatNextLine = "";
			while (!lineRemaining.isEmpty()) {
				int indexToCut = formatNextLine.length();
				int displayLength = 0;
				int length = lineRemaining.length();
				while (indexToCut < length && displayLength <= 38) {
					if (lineRemaining.charAt(indexToCut) == (char)167 && indexToCut + 1 < length) {
						indexToCut++;
					} else {
						displayLength++;
					}
					indexToCut++;
				}
				if (indexToCut < length) {
					indexToCut = lineRemaining.substring(0, indexToCut).lastIndexOf(' ');
					if (indexToCut == -1) {// no space available, show the whole line 'as is'
						list.add(lineRemaining);
						lineRemaining = "";
					} else {// cut at last space
						list.add(lineRemaining.substring(0, indexToCut));
						
						// compute remaining format
						int index = formatNextLine.length();
						while (index <= indexToCut) {
							if (lineRemaining.charAt(index) == (char)167 && index + 1 < indexToCut) {
								index++;
								formatNextLine += ("" + (char)167) + lineRemaining.charAt(index);
							}
							index++;
						}
						
						// cut for next line, recovering current format
						lineRemaining = formatNextLine + " " + lineRemaining.substring(indexToCut + 1);
					}
				} else {
					list.add(lineRemaining);
					lineRemaining = "";
				}
			}
		}
	}
	
	private static Field getField(Class<?> clazz, String deobfuscatedName, String obfuscatedName) {
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
