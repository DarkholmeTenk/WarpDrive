package cr0s.warpdrive.config;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.compat.CompatAdvancedRepulsionSystems;
import cr0s.warpdrive.compat.CompatAppliedEnergistics2;
import cr0s.warpdrive.compat.CompatArsMagica2;
import cr0s.warpdrive.compat.CompatBiblioCraft;
import cr0s.warpdrive.compat.CompatBotania;
import cr0s.warpdrive.compat.CompatComputerCraft;
import cr0s.warpdrive.compat.CompatEnderIO;
import cr0s.warpdrive.compat.CompatEvilCraft;
import cr0s.warpdrive.compat.CompatForgeMultipart;
import cr0s.warpdrive.compat.CompatImmersiveEngineering;
import cr0s.warpdrive.compat.CompatIndustrialCraft2;
import cr0s.warpdrive.compat.CompatJABBA;
import cr0s.warpdrive.compat.CompatMetallurgy;
import cr0s.warpdrive.compat.CompatNatura;
import cr0s.warpdrive.compat.CompatOpenComputers;
import cr0s.warpdrive.compat.CompatRedstonePaste;
import cr0s.warpdrive.compat.CompatSGCraft;
import cr0s.warpdrive.compat.CompatStargateTech2;
import cr0s.warpdrive.compat.CompatTConstruct;
import cr0s.warpdrive.compat.CompatThaumcraft;
import cr0s.warpdrive.config.filler.FillerManager;
import cr0s.warpdrive.config.structures.StructureManager;
import cr0s.warpdrive.config.structures.StructureReference;
import cr0s.warpdrive.data.Planet;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class WarpDriveConfig {
	private static final boolean unused = false; // TODO
	
	private static File configDirectory;
	private static DocumentBuilder xmlDocumentBuilder;
	private static final String[] defaultXMLfilenames = {
			// fillers
			"filler-default.xml", "filler-netherores.xml", "filler-undergroundbiomes.xml",
			// structures
			"structures-default.xml", "structures-netherores.xml",
	};
	
	/*
	 * The variables which store whether or not individual mods are loaded
	 */
	public static boolean isForgeMultipartLoaded = false;
	public static boolean isAdvancedSolarPanelLoaded = false;
	public static boolean isAppliedEnergistics2Loaded = false;
	public static boolean isICBMloaded = false;
	public static boolean isIndustrialCraft2Loaded = false;
	public static boolean isComputerCraftLoaded = false;
	public static boolean isOpenComputersLoaded = false;
	public static boolean isCoFHCoreLoaded = false;
	public static boolean isThermalExpansionLoaded = false;
	public static boolean isArsMagica2Loaded = false;
	public static boolean isImmersiveEngineeringLoaded = false;
	public static boolean isGregTech5Loaded = false;
	public static boolean isEnderIOloaded = false;
	public static boolean isAdvancedRepulsionSystemLoaded = false;
	
	public static ItemStack IC2_compressedAir;
	public static ItemStack IC2_emptyCell;
	public static Block IC2_rubberWood;
	public static ItemStack IC2_Resin;
	public static Block CC_Computer, CC_peripheral, CCT_Turtle, CCT_Expanded, CCT_Advanced;
	
	// Mod configuration (see loadWarpDriveConfig() for comments/definitions)
	// General
	public static int G_SPACE_BIOME_ID = 95;
	public static int G_SPACE_PROVIDER_ID = 14;
	public static int G_SPACE_DIMENSION_ID = -2;
	public static int G_HYPERSPACE_PROVIDER_ID = 15;
	public static int G_HYPERSPACE_DIMENSION_ID = -3;
	public static int G_SPACE_WORLDBORDER_BLOCKS = 100000;
	public static int G_ENTITY_JUMP_ID = 240;
	public static int G_ENTITY_SPHERE_GENERATOR_ID = 241;
	public static int G_ENTITY_STAR_CORE_ID = 242;
	public static int G_ENTITY_CAMERA_ID = 243;
	
	public static final int LUA_SCRIPTS_NONE = 0;
	public static final int LUA_SCRIPTS_TEMPLATES = 1;
	public static final int LUA_SCRIPTS_ALL = 2;
	public static int G_LUA_SCRIPTS = LUA_SCRIPTS_ALL;
	public static String G_SCHEMALOCATION = "warpDrive_schematics";
	public static int G_BLOCKS_PER_TICK = 3500;
	
	public static boolean RECIPES_ENABLE_DYNAMIC = true;
	public static boolean RECIPES_ENABLE_IC2 = false;
	public static boolean RECIPES_ENABLE_HARD_IC2 = false;
	public static boolean RECIPES_ENABLE_VANILLA = false;
	
	// Logging
	public static boolean LOGGING_JUMP = false;
	public static boolean LOGGING_JUMPBLOCKS = false;
	public static boolean LOGGING_ENERGY = false;
	public static boolean LOGGING_EFFECTS = false;
	public static boolean LOGGING_CLOAKING = false;
	public static boolean LOGGING_VIDEO_CHANNEL = false;
	public static boolean LOGGING_TARGETING = false;
	public static boolean LOGGING_WEAPON = false;
	public static boolean LOGGING_CAMERA = false;
	public static boolean LOGGING_BUILDING = false;
	public static boolean LOGGING_COLLECTION = false;
	public static boolean LOGGING_TRANSPORTER = false;
	public static boolean LOGGING_LUA = false;
	public static boolean LOGGING_RADAR = false;
	public static boolean LOGGING_BREATHING = false;
	public static boolean LOGGING_WORLDGEN = false;
	public static boolean LOGGING_PROFILING = true;
	public static boolean LOGGING_DICTIONARY = false;
	public static boolean LOGGING_STARMAP = false;
	public static boolean LOGGING_BREAK_PLACE = false;
	public static boolean LOGGING_FORCEFIELD = false;
	public static boolean LOGGING_FORCEFIELD_REGISTRY = false;
	
	// Planets
	public static Planet[] PLANETS = null;
	
	// Space generator
	public static int SPACE_GENERATOR_Y_MIN_CENTER = 55;
	public static int SPACE_GENERATOR_Y_MAX_CENTER = 128;
	public static int SPACE_GENERATOR_Y_MIN_BORDER = 5;
	public static int SPACE_GENERATOR_Y_MAX_BORDER = 200;
	public static RandomCollection<StructureReference> SPACE_GENERATOR_STRUCTURES_CHANCES = null;
	
	// Ship
	public static int SHIP_MAX_ENERGY_STORED = 100000000;
	public static int SHIP_NORMALJUMP_ENERGY_PER_BLOCK = 10;
	public static int SHIP_NORMALJUMP_ENERGY_PER_DISTANCE = 100;
	public static int SHIP_HYPERJUMP_ENERGY_PER_BLOCK = 100;
	public static int SHIP_HYPERJUMP_ENERGY_PER_DISTANCE = 1000;
	public static int SHIP_TELEPORT_ENERGY_PER_ENTITY = 1000000;
	public static int SHIP_MAX_JUMP_DISTANCE = 128;
	public static int SHIP_VOLUME_MAX_ON_PLANET_SURFACE = 3000;
	public static int SHIP_VOLUME_MIN_FOR_HYPERSPACE = 1200;
	public static int SHIP_MAX_SIDE_SIZE = 127;
	public static int SHIP_COOLDOWN_INTERVAL_SECONDS = 30;
	public static int SHIP_COLLISION_TOLERANCE_BLOCKS = 3;
	public static int SHIP_SHORTJUMP_THRESHOLD_BLOCKS = 50;
	public static int SHIP_SHORTJUMP_WARMUP_SECONDS = 10;
	public static int SHIP_LONGJUMP_WARMUP_SECONDS = 30;
	public static int SHIP_WARMUP_RANDOM_TICKS = 60;
	public static int SHIP_CORE_REGISTRY_UPDATE_INTERVAL_SECONDS = 10;
	public static int SHIP_CONTROLLER_UPDATE_INTERVAL_SECONDS = 2;
	public static int SHIP_CORE_ISOLATION_UPDATE_INTERVAL_SECONDS = 10;
	public static String[] SHIP_VOLUME_UNLIMITED_PLAYERNAMES = { "notch", "someone" };
	public static boolean SHIP_WARMUP_SICKNESS = true;
	
	// Radar
	public static int RADAR_MAX_ENERGY_STORED = 100000000; // 100kk eU
	public static int RADAR_SCAN_MIN_ENERGY_COST = 10000;
	public static double[] RADAR_SCAN_ENERGY_COST_FACTORS = { 0.0, 0.0, 0.0, 0.0001 };
	public static int RADAR_SCAN_MIN_DELAY_SECONDS = 1;
	public static double[] RADAR_SCAN_DELAY_FACTORS_SECONDS = { 1.0, 0.001, 0.0, 0.0 };
	public static int RADAR_MAX_ISOLATION_RANGE = 2;
	public static int RADAR_MIN_ISOLATION_BLOCKS = 5;
	public static int RADAR_MAX_ISOLATION_BLOCKS = 60;
	public static double RADAR_MIN_ISOLATION_EFFECT = 0.12;
	public static double RADAR_MAX_ISOLATION_EFFECT = 1.00;
	
	// Ship Scanner
	public static int SS_MAX_ENERGY_STORED = 500000000;
	public static int SS_ENERGY_PER_BLOCK_SCAN = 100; // eU per block of ship volume (including air)
	public static int SS_ENERGY_PER_BLOCK_DEPLOY = 5000;
	public static int SS_MAX_DEPLOY_RADIUS_BLOCKS = 50;
	public static int SS_SEARCH_INTERVAL_TICKS = 20;
	public static int SS_SCAN_BLOCKS_PER_SECOND = 10;
	public static int SS_DEPLOY_BLOCKS_PER_SECOND = 10;
	
	// Laser medium
	public static int LASER_MEDIUM_MAX_ENERGY_STORED = 100000;
	
	// Laser Emitter
	// 1 main laser + 4 boosting lasers = 10 * 100k + 0.6 * 40 * 100k = 3.4M
	public static int    LASER_CANNON_MAX_MEDIUMS_COUNT = 10;
	public static int    LASER_CANNON_MAX_LASER_ENERGY = 3400000;
	public static int    LASER_CANNON_EMIT_FIRE_DELAY_TICKS = 5;
	public static int    LASER_CANNON_EMIT_SCAN_DELAY_TICKS = 1;
	
	public static double LASER_CANNON_BOOSTER_BEAM_ENERGY_EFFICIENCY = 0.60D;
	public static double LASER_CANNON_ENERGY_ATTENUATION_PER_AIR_BLOCK  = 0.000200D;
	public static double LASER_CANNON_ENERGY_ATTENUATION_PER_VOID_BLOCK = 0.000005D;
	public static double LASER_CANNON_ENERGY_ATTENUATION_PER_BROKEN_BLOCK = 0.23D;
	public static int    LASER_CANNON_RANGE_MAX = 500;
	
	public static int    LASER_CANNON_ENTITY_HIT_SET_ON_FIRE_SECONDS = 20;
	public static int    LASER_CANNON_ENTITY_HIT_ENERGY = 15000;
	public static int    LASER_CANNON_ENTITY_HIT_BASE_DAMAGE = 3;
	public static int    LASER_CANNON_ENTITY_HIT_ENERGY_PER_DAMAGE = 30000;
	public static int    LASER_CANNON_ENTITY_HIT_MAX_DAMAGE = 100;
	
	public static int    LASER_CANNON_ENTITY_HIT_EXPLOSION_ENERGY_THRESHOLD = 900000;
	public static float  LASER_CANNON_ENTITY_HIT_EXPLOSION_BASE_STRENGTH = 4.0F;
	public static int    LASER_CANNON_ENTITY_HIT_EXPLOSION_ENERGY_PER_STRENGTH = 125000;
	public static float  LASER_CANNON_ENTITY_HIT_EXPLOSION_MAX_STRENGTH = 4.0F;
	
	public static int    LASER_CANNON_BLOCK_HIT_ENERGY_MIN = 75000;
	public static int    LASER_CANNON_BLOCK_HIT_ENERGY_PER_BLOCK_HARDNESS = 150000;
	public static int    LASER_CANNON_BLOCK_HIT_ENERGY_MAX = 750000;
	public static double LASER_CANNON_BLOCK_HIT_ABSORPTION_PER_BLOCK_HARDNESS = 0.01;
	public static double LASER_CANNON_BLOCK_HIT_ABSORPTION_MAX = 0.80;
	
	public static float  LASER_CANNON_BLOCK_HIT_EXPLOSION_HARDNESS_THRESHOLD = 5.0F;
	public static float  LASER_CANNON_BLOCK_HIT_EXPLOSION_BASE_STRENGTH = 8.0F;
	public static int    LASER_CANNON_BLOCK_HIT_EXPLOSION_ENERGY_PER_STRENGTH = 125000;
	public static float  LASER_CANNON_BLOCK_HIT_EXPLOSION_MAX_STRENGTH = 50F;
	
	// Mining laser
	// BuildCraft quarry values for reference
	// - harvesting one block is 60 MJ/block = 600 RF/block = ~145 EU/block
	// - maximum speed is 3.846 ticks per blocks
	// - overall consumption varies from 81.801 to 184.608 MJ/block (depending on speed) = up to 1846.08 RF/block = up to ~448 EU/block
	// - at radius 5, one layer takes ~465 ticks ((ML_MAX_RADIUS * 2 + 1) ^ 2 * 3.846)
	// - overall consumption is ((ML_MAX_RADIUS * 2 + 1) ^ 2) * 448 => ~ 54208 EU/layer
	// WarpDrive mining laser in comparison
	// - each mined layer is scanned twice
	// - default ore generation: 1 ore out of 25 blocks
	// - overall consumption in 'all, space' is ML_EU_PER_LAYER_SPACE / ((ML_MAX_RADIUS * 2 + 1) ^ 2) + ML_EU_PER_BLOCK_SPACE => ~ 356 EU/block
	// - overall consumption in 'all, space' is ML_EU_PER_LAYER_SPACE + ((ML_MAX_RADIUS * 2 + 1) ^ 2) * ML_EU_PER_BLOCK_SPACE => ~ 43150 EU/layer
	// - overall consumption in 'ores, space' is ML_EU_PER_LAYER_SPACE + ((ML_MAX_RADIUS * 2 + 1) ^ 2) * ML_EU_PER_BLOCK_SPACE * ML_EU_MUL_ORESONLY / 25 => ~ 28630 EU/layer
	// - at radius 5, one layer takes 403 ticks (2 * ML_SCAN_DELAY_TICKS + ML_MINE_DELAY_TICKS * (ML_MAX_RADIUS * 2 + 1) ^ 2)
	public static int MINING_LASER_MAX_MEDIUMS_COUNT = 1;
	public static int MINING_LASER_RADIUS_BLOCKS = 5;
	public static int MINING_LASER_WARMUP_DELAY_TICKS = 20;
	public static int MINING_LASER_SCAN_DELAY_TICKS = 20;
	public static int MINING_LASER_MINE_DELAY_TICKS = 3;
	public static int MINING_LASER_SPACE_ENERGY_PER_LAYER = 20000;
	public static int MINING_LASER_PLANET_ENERGY_PER_LAYER = 33000;
	public static int MINING_LASER_SPACE_ENERGY_PER_BLOCK = 1500;
	public static int MINING_LASER_PLANET_ENERGY_PER_BLOCK = 2500;
	public static double MINING_LASER_ORESONLY_ENERGY_FACTOR = 15.0; // lower value encourages to keep the land 'clean'
	public static double MINING_LASER_SILKTOUCH_ENERGY_FACTOR = 1.5;
	public static double MINING_LASER_SILKTOUCH_DEUTERIUM_L = 0.0;
	public static double MINING_LASER_FORTUNE_ENERGY_FACTOR = 1.5;
	
	// Tree farm
	public static int TREE_FARM_MAX_SCAN_RADIUS_NO_LASER_MEDIUM = 3;
	public static int TREE_FARM_MAX_SCAN_RADIUS_PER_LASER_MEDIUM = 2;
	public static int TREE_FARM_totalMaxRadius = 0;
	public static int TREE_FARM_MAX_MEDIUMS_COUNT = 5;
	public static int TREE_FARM_MAX_LOG_DISTANCE = 8;
	public static int TREE_FARM_MAX_LOG_DISTANCE_PER_MEDIUM = 4;
	
	// Cloaking
	public static int CLOAKING_MAX_ENERGY_STORED = 500000000;
	public static int CLOAKING_COIL_CAPTURE_BLOCKS = 5;
	public static int CLOAKING_MAX_FIELD_RADIUS = 63;
	public static int CLOAKING_TIER1_ENERGY_PER_BLOCK = 32;
	public static int CLOAKING_TIER2_ENERGY_PER_BLOCK = 128;
	public static int CLOAKING_FIELD_REFRESH_INTERVAL_SECONDS = 3;
	
	// Air generator
	public static int AIRGEN_ENERGY_PER_CANISTER = 20;
	public static int AIRGEN_ENERGY_PER_NEWAIRBLOCK = 12;
	public static int AIRGEN_ENERGY_PER_EXISTINGAIRBLOCK = 4;
	public static int AIRGEN_MAX_ENERGY_STORED = 4000;
	public static int AIRGEN_AIR_GENERATION_TICKS = 40;
	
	// IC2 Reactor monitor
	public static int IC2_REACTOR_MAX_ENERGY_STORED = 1000000;
	public static double IC2_REACTOR_ENERGY_PER_HEAT = 2;
	public static int IC2_REACTOR_COOLING_INTERVAL_TICKS = 10;
	
	// Transporter
	public static int TRANSPORTER_MAX_ENERGY_STORED = 1000000;
	public static boolean TRANSPORTER_USE_RELATIVE_COORDS = true;
	public static double TRANSPORTER_ENERGY_PER_BLOCK = 100.0;
	public static double TRANSPORTER_MAX_BOOST_MUL = 4.0;
	
	// Enantiomorphic power reactor
	public static int ENAN_REACTOR_MAX_ENERGY_STORED = 100000000;
	public static int ENAN_REACTOR_UPDATE_INTERVAL_TICKS = 5;
	public static int ENAN_REACTOR_MAX_LASERS_PER_SECOND = 6;
	
	// Power store
	public static int ENERGY_BANK_MAX_ENERGY_STORED = 1000000;
	
	// Laser lift
	public static int LIFT_MAX_ENERGY_STORED = 2400;
	public static int LIFT_ENERGY_PER_ENTITY = 800;
	public static int LIFT_UPDATE_INTERVAL_TICKS = 10;
	
	// Chunk loader
	public static int CL_MAX_ENERGY = 1000000;
	public static int CL_MAX_DISTANCE = 2;
	public static int CL_RF_PER_CHUNKTICK = 320;
	
	// Hull
	public static float[] HULL_HARDNESS = { 25.0F, 50.0F, 80.0F };
	public static float[] HULL_BLAST_RESISTANCE = { 60.0F, 90.0F, 120.0F };
	
	// Block transformers library
	public static HashMap<String, IBlockTransformer> blockTransformers = null;
	
	
	public static Block getModBlock(final String mod, final String id) {
		try {
			return GameRegistry.findBlock(mod, id);
		} catch (Exception exception) {
			WarpDrive.logger.info("Failed to get mod block for " + mod + ":" + id);
			exception.printStackTrace();
		}
		return null;
	}
	
	public static ItemStack getModItemStack(final String mod, final String id, final int meta) {
		try {
			ItemStack item = new ItemStack((Item) Item.itemRegistry.getObject(mod + ":" + id));
			if (meta != -1) {
				item.setItemDamage(meta);
			}
			return item;
		} catch (Exception exception) {
			WarpDrive.logger.info("Failed to get mod item for " + mod + ":" + id + "@" + meta);
		}
		return null;
	}
	
	public static void onFMLpreInitialization(final String stringConfigDirectory) {
		// create mod folder
		configDirectory = new File(stringConfigDirectory, WarpDrive.MODID);
		//noinspection ResultOfMethodCallIgnored
		configDirectory.mkdir();
		if (!configDirectory.isDirectory()) {
			throw new RuntimeException("Unable to create config directory " + configDirectory);
		}
		
		// read configuration file
		loadWarpDriveConfig(new File(configDirectory, WarpDrive.MODID + ".cfg"));
	}
	
	public static void loadWarpDriveConfig(File file) {
		Configuration config = new Configuration(file);
		config.load();
		
		// General
		G_SPACE_BIOME_ID = clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "space_biome_id", G_SPACE_BIOME_ID, "Space biome ID").getInt());
		G_SPACE_PROVIDER_ID = clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "space_provider_id", G_SPACE_PROVIDER_ID, "Space dimension provider ID").getInt());
		G_SPACE_DIMENSION_ID = clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "space_dimension_id", G_SPACE_DIMENSION_ID, "Space dimension world ID").getInt());
		G_HYPERSPACE_PROVIDER_ID = clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "hyperspace_provider_id", G_HYPERSPACE_PROVIDER_ID, "Hyperspace dimension provider ID").getInt());
		G_HYPERSPACE_DIMENSION_ID = clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "hyperspace_dimension_id", G_HYPERSPACE_DIMENSION_ID, "Hyperspace dimension world ID").getInt());
		G_SPACE_WORLDBORDER_BLOCKS = clamp(0, 3000000,
				config.get("general", "space_worldborder_blocks", G_SPACE_WORLDBORDER_BLOCKS, "World border applied to hyperspace & space, set to 0 to disable it").getInt());
		
		G_ENTITY_JUMP_ID = clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "entity_jump_id", G_ENTITY_JUMP_ID, "Entity jump ID").getInt());
		G_ENTITY_SPHERE_GENERATOR_ID = clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "entity_sphere_generator_id", G_ENTITY_SPHERE_GENERATOR_ID, "Entity sphere generator ID").getInt());
		G_ENTITY_STAR_CORE_ID = clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "entity_star_core_id", G_ENTITY_STAR_CORE_ID, "Entity star core ID").getInt());
		G_ENTITY_CAMERA_ID = clamp(Integer.MIN_VALUE, Integer.MAX_VALUE,
				config.get("general", "entity_camera_id", G_ENTITY_CAMERA_ID, "Entity camera ID").getInt());
		
		G_LUA_SCRIPTS = clamp(0, 2,
				config.get("general", "lua_scripts", G_LUA_SCRIPTS,
						"LUA scripts to load when connecting machines: 0 = none, 1 = templates in a subfolder, 2 = ready to roll (templates are still provided)").getInt());
		G_SCHEMALOCATION = config.get("general", "schematic_location", G_SCHEMALOCATION, "Folder where to save ship schematics").getString();
		G_BLOCKS_PER_TICK = clamp(100, 100000,
				config.get("general", "blocks_per_tick", G_BLOCKS_PER_TICK,
						"Number of blocks to move per ticks, too high will cause lag spikes on ship jumping or deployment, too low may break the ship wirings").getInt());
		
		// Recipes
		RECIPES_ENABLE_DYNAMIC = config.get("recipes", "enable_dynamic", RECIPES_ENABLE_DYNAMIC,
				"Mixed recipes dynamically integrating with other mods (Advanced Repulsion Systems, Advanced Solar Panels, IC2, GregTech 5, EnderIO, ThermalExpansion, Immersive Engineering)").getBoolean(true);
		RECIPES_ENABLE_VANILLA = config.get("recipes", "enable_vanilla", RECIPES_ENABLE_VANILLA, "Vanilla recipes by DarkholmeTenk (you need to disable Dynamic recipes to use those, no longer updated)").getBoolean(false);
		RECIPES_ENABLE_IC2 = config.get("recipes", "enable_ic2", RECIPES_ENABLE_IC2, "Original recipes based on IndustrialCraft2 by Cr0s (you need to disable Dynamic recipes to use those, no longer updated)").getBoolean(false);
		RECIPES_ENABLE_HARD_IC2 = config.get("recipes", "enable_hard_ic2", RECIPES_ENABLE_HARD_IC2, "Harder recipes based on IC2 by YuRaNnNzZZ (you need to disable Dynamic recipes to use those)").getBoolean(false);
		
		// Logging
		LOGGING_JUMP = config.get("logging", "enable_jump_logs", LOGGING_JUMP, "Basic jump logs, should always be enabled").getBoolean(true);
		LOGGING_JUMPBLOCKS = config.get("logging", "enable_jumpblocks_logs", LOGGING_JUMPBLOCKS, "Detailed jump logs to help debug the mod, will spam your logs...").getBoolean(false);
		LOGGING_ENERGY = config.get("logging", "enable_energy_logs", LOGGING_ENERGY, "Detailed energy logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		if (WarpDrive.isDev) {// disabled in production, for obvious reasons :)
			LOGGING_EFFECTS = config.get("logging", "enable_effects_logs", LOGGING_EFFECTS, "Detailed effects logs to help debug the mod, will spam your console!").getBoolean(false);
			LOGGING_CLOAKING = config.get("logging", "enable_cloaking_logs", LOGGING_CLOAKING, "Detailed cloaking logs to help debug the mod, will spam your console!").getBoolean(false);
			LOGGING_VIDEO_CHANNEL = config.get("logging", "enable_videoChannel_logs", LOGGING_VIDEO_CHANNEL, "Detailed video channel logs to help debug the mod, will spam your console!").getBoolean(false);
			LOGGING_TARGETING = config.get("logging", "enable_targeting_logs", LOGGING_TARGETING, "Detailed targeting logs to help debug the mod, will spam your console!").getBoolean(false);
		} else {
			LOGGING_EFFECTS = false;
			LOGGING_CLOAKING = false;
			LOGGING_VIDEO_CHANNEL = false;
			LOGGING_TARGETING = false;
		}
		LOGGING_WEAPON = config.get("logging", "enable_weapon_logs", LOGGING_WEAPON, "Detailed weapon logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_CAMERA = config.get("logging", "enable_camera_logs", LOGGING_CAMERA, "Detailed camera logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_BUILDING = config.get("logging", "enable_building_logs", LOGGING_BUILDING, "Detailed building logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_COLLECTION = config.get("logging", "enable_collection_logs", LOGGING_COLLECTION, "Detailed collection logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_TRANSPORTER = config.get("logging", "enable_transporter_logs", LOGGING_TRANSPORTER, "Detailed transporter logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_LUA = config.get("logging", "enable_LUA_logs", LOGGING_LUA, "Detailed LUA logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_RADAR = config.get("logging", "enable_radar_logs", LOGGING_RADAR, "Detailed radar logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_BREATHING = config.get("logging", "enable_breathing_logs", LOGGING_BREATHING, "Detailed breathing logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_WORLDGEN = config.get("logging", "enable_worldgen_logs", LOGGING_WORLDGEN, "Detailed world generation logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_PROFILING = config.get("logging", "enable_profiling_logs", LOGGING_PROFILING, "Profiling logs, enable it to check for lag").getBoolean(true);
		LOGGING_DICTIONARY = config.get("logging", "enable_dictionary_logs", LOGGING_DICTIONARY, "Dictionary logs, enable it to dump blocks hardness and blast resistance at boot").getBoolean(true);
		LOGGING_STARMAP = config.get("logging", "enable_starmap_logs", LOGGING_STARMAP, "Starmap logs, enable it to dump starmap registry updates").getBoolean(false);
		LOGGING_BREAK_PLACE = config.get("logging", "enable_break_place_logs", LOGGING_BREAK_PLACE, "Detailed break/place event logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_FORCEFIELD = config.get("logging", "enable_forcefield_logs", LOGGING_FORCEFIELD, "Detailed forcefield logs to help debug the mod, enable it before reporting a bug").getBoolean(false);
		LOGGING_FORCEFIELD_REGISTRY = config.get("logging", "enable_forcefield_registry_logs", LOGGING_FORCEFIELD_REGISTRY, "ForceField registry logs, enable it to dump forcefield registry updates").getBoolean(false);
		
		// Planets
		{
			config.addCustomCategoryComment("planets",
					  "Planets are other dimensions connected through the Space dimension. Default is overworld with 100k radius.\n"
					+ "Each planet orbit is square shaped and defined as a list of 7 integers (all measured in blocks).");
			
			ConfigCategory categoryPlanets = config.getCategory("planets");
			String[] planetsName = categoryPlanets.getValues().keySet().toArray(new String[0]);
			if (planetsName.length == 0) {
				planetsName = new String[] { "overworld" };
			}
			
			int[] defaultPlanet = { 0, 0, 0, 100000, 100000, 0, 0 }; // 30000000 is Minecraft limit for SetBlock
			PLANETS = new Planet[planetsName.length];
			int index = 0;
			for (String name : planetsName) {
				int[] planetInts = config.get("planets", name, defaultPlanet, "dimensionId, dimensionCenterX, dimensionCenterZ, radiusX, radiusZ, spaceCenterX, spaceCenterZ").getIntList();
				if (planetInts.length != 7) {
					WarpDrive.logger.warn("Invalid planet definition '" + name + "' (exactly 7 integers are expected), using default instead");
					planetInts = defaultPlanet.clone();
				}
				Planet planet = new Planet(planetInts[0], planetInts[1], planetInts[2], planetInts[3], planetInts[4], planetInts[5], planetInts[6]);
				WarpDrive.logger.info("Adding '" + name + "' as " + planet);
				PLANETS[index] = planet;
				index++;
			}
			// FIXME: check planets aren't overlapping
			// We're not checking invalid dimension id, so they can be pre-allocated (see MystCraft)
		}
		
		// Ship
		SHIP_MAX_ENERGY_STORED = clamp(0, Integer.MAX_VALUE,
				config.get("ship", "max_energy_stored", SHIP_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		SHIP_NORMALJUMP_ENERGY_PER_BLOCK = clamp(0, Integer.MAX_VALUE,
				config.get("ship", "normaljump_energy_per_block", SHIP_NORMALJUMP_ENERGY_PER_BLOCK, "Energy cost per non-air block without warping").getInt());
		SHIP_NORMALJUMP_ENERGY_PER_DISTANCE = clamp(0, Integer.MAX_VALUE,
				config.get("ship", "normaljump_energy_per_distance", SHIP_NORMALJUMP_ENERGY_PER_DISTANCE, "Energy cost per non-air block without warping").getInt());
		SHIP_HYPERJUMP_ENERGY_PER_DISTANCE = clamp(0, Integer.MAX_VALUE,
				config.get("ship", "hyperjump_energy_per_distance", SHIP_HYPERJUMP_ENERGY_PER_DISTANCE, "Energy cost per non-air block while warping").getInt());
		SHIP_HYPERJUMP_ENERGY_PER_BLOCK = clamp(0, Integer.MAX_VALUE,
				config.get("ship", "hyperjump_energy_per_block", SHIP_HYPERJUMP_ENERGY_PER_BLOCK, "Energy cost per non-air block while warping").getInt());
		SHIP_TELEPORT_ENERGY_PER_ENTITY = clamp(0, Integer.MAX_VALUE,
				config.get("ship", "teleport_energy_per_entity", SHIP_TELEPORT_ENERGY_PER_ENTITY, "Energy cost per entity").getInt());
		
		SHIP_MAX_JUMP_DISTANCE = clamp(0, 30000000,
				config.get("ship", "max_jump_distance", SHIP_MAX_JUMP_DISTANCE, "Maximum jump length value in blocks").getInt());
		
		SHIP_VOLUME_MAX_ON_PLANET_SURFACE = clamp(0, 10000000,
				config.get("ship", "volume_max_on_planet_surface", SHIP_VOLUME_MAX_ON_PLANET_SURFACE, "Maximum ship mass (in blocks) to jump on a planet").getInt());
		SHIP_VOLUME_MIN_FOR_HYPERSPACE = clamp(0, 10000000,
				config.get("ship", "volume_min_for_hyperspace", SHIP_VOLUME_MIN_FOR_HYPERSPACE, "Minimum ship mass (in blocks) to enter or exit hyperspace without a jumpgate").getInt());
		SHIP_VOLUME_UNLIMITED_PLAYERNAMES = config.get("ship", "volume_unlimited_playernames", SHIP_VOLUME_UNLIMITED_PLAYERNAMES,
				"List of player names which have unlimited block counts to their ship").getStringList();
		
		SHIP_MAX_SIDE_SIZE = clamp(0, 30000000,
				config.get("ship", "max_side_size", SHIP_MAX_SIDE_SIZE, "Maximum ship size on each axis in blocks").getInt());
		SHIP_COLLISION_TOLERANCE_BLOCKS = clamp(0, 30000000,
				config.get("ship", "collision_tolerance_blocks", SHIP_COLLISION_TOLERANCE_BLOCKS, "Tolerance in block in case of collision before causing damages...").getInt());
		SHIP_COOLDOWN_INTERVAL_SECONDS = clamp(0, 3600,
				config.get("ship", "cooldown_interval_seconds", SHIP_COOLDOWN_INTERVAL_SECONDS, "Cooldown seconds to wait after jumping").getInt());
		
		SHIP_SHORTJUMP_THRESHOLD_BLOCKS = clamp(0, 30000000,
				config.get("ship", "shortjump_threshold_blocs", SHIP_SHORTJUMP_THRESHOLD_BLOCKS, "Short jump definition").getInt());
		SHIP_SHORTJUMP_WARMUP_SECONDS = clamp(0, 3600,
				config.get("ship", "shortjump_warmup_seconds", SHIP_SHORTJUMP_WARMUP_SECONDS, "(measured in seconds)").getInt());
		SHIP_LONGJUMP_WARMUP_SECONDS = clamp(0, 3600,
				config.get("ship", "longjump_warmup_seconds", SHIP_LONGJUMP_WARMUP_SECONDS, "(measured in seconds)").getInt());
		SHIP_WARMUP_RANDOM_TICKS = clamp(10, 200,
				config.get("ship", "warmup_random_ticks", SHIP_WARMUP_RANDOM_TICKS, "Random variation added to warmup (measured in ticks)").getInt());
		SHIP_WARMUP_SICKNESS = config.get("ship", "warmup_sickness", true, "Enable warp sickness during warmup").getBoolean(true);
		
		SHIP_CORE_REGISTRY_UPDATE_INTERVAL_SECONDS = clamp(0, 300,
				config.get("ship", "core_registry_update_interval", SHIP_CORE_REGISTRY_UPDATE_INTERVAL_SECONDS, "(measured in seconds)").getInt());
		SHIP_CORE_ISOLATION_UPDATE_INTERVAL_SECONDS = clamp(0, 300,
				config.get("ship", "core_isolation_update_interval", SHIP_CORE_ISOLATION_UPDATE_INTERVAL_SECONDS, "(measured in seconds)").getInt());
		SHIP_CONTROLLER_UPDATE_INTERVAL_SECONDS = clamp(0, 300,
				config.get("ship", "controller_update_interval", SHIP_CONTROLLER_UPDATE_INTERVAL_SECONDS, "(measured in seconds)").getInt());
		
		// Radar
		RADAR_MAX_ENERGY_STORED = clamp(0, Integer.MAX_VALUE,
				config.get("radar", "max_energy_stored", RADAR_MAX_ENERGY_STORED, "maximum energy stored").getInt());
		
		RADAR_SCAN_MIN_ENERGY_COST = clamp(0, Integer.MAX_VALUE,
				config.get("radar", "min_energy_cost", RADAR_SCAN_MIN_ENERGY_COST, "minimum energy cost per scan (0+), independently of radius").getInt());
		RADAR_SCAN_ENERGY_COST_FACTORS = 
				config.get("radar", "factors_energy_cost", RADAR_SCAN_ENERGY_COST_FACTORS, "energy cost factors {a, b, c, d}. You need to provide exactly 4 values.\n"
						+ "The equation used is a + b * radius + c * radius^2 + d * radius^3").getDoubleList();
		if (RADAR_SCAN_ENERGY_COST_FACTORS.length != 4) {
			RADAR_SCAN_ENERGY_COST_FACTORS = new double[4];
			Arrays.fill(RADAR_SCAN_ENERGY_COST_FACTORS, 1.0);
		}
		RADAR_SCAN_MIN_DELAY_SECONDS = clamp(1, Integer.MAX_VALUE,
				config.get("radar", "scan_min_delay_seconds", RADAR_SCAN_MIN_DELAY_SECONDS, "minimum scan delay per scan (1+), (measured in seconds)").getInt());
		RADAR_SCAN_DELAY_FACTORS_SECONDS = 
				config.get("radar", "scan_delay_factors_seconds", RADAR_SCAN_DELAY_FACTORS_SECONDS, "scan delay factors {a, b, c, d}. You need to provide exactly 4 values.\n"
						+ "The equation used is a + b * radius + c * radius^2 + d * radius^3, (measured in seconds)").getDoubleList();
		if (RADAR_SCAN_DELAY_FACTORS_SECONDS.length != 4) {
			RADAR_SCAN_DELAY_FACTORS_SECONDS = new double[4];
			Arrays.fill(RADAR_SCAN_DELAY_FACTORS_SECONDS, 1.0);
		}
		
		RADAR_MAX_ISOLATION_RANGE = clamp(2, 8,
				config.get("radar", "max_isolation_range", RADAR_MAX_ISOLATION_RANGE, "radius around core where isolation blocks count (2 to 8), higher is lagger").getInt());
		
		RADAR_MIN_ISOLATION_BLOCKS = clamp(0, 20,
				config.get("radar", "min_isolation_blocks", RADAR_MIN_ISOLATION_BLOCKS, "number of isolation blocks required to get some isolation (0 to 20)").getInt());
		RADAR_MAX_ISOLATION_BLOCKS = clamp(5, 94,
				config.get("radar", "max_isolation_blocks", RADAR_MAX_ISOLATION_BLOCKS, "number of isolation blocks required to reach maximum effect (5 to 94)").getInt());
		
		RADAR_MIN_ISOLATION_EFFECT = clamp(0.01D, 0.95D,
				config.get("radar", "min_isolation_effect", RADAR_MIN_ISOLATION_EFFECT, "isolation effect achieved with min number of isolation blocks (0.01 to 0.95)").getDouble(0.12D));
		RADAR_MAX_ISOLATION_EFFECT = clamp(0.01D, 1.0D,
				config.get("radar", "max_isolation_effect", RADAR_MAX_ISOLATION_EFFECT, "isolation effect achieved with max number of isolation blocks (0.01 to 1.00)").getDouble(1.00D));
		
		// Ship Scanner
		SS_MAX_ENERGY_STORED = clamp(1, Integer.MAX_VALUE,
				config.get("ship_scanner", "max_energy_stored", SS_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		
		SS_ENERGY_PER_BLOCK_SCAN = config.get("ship_scanner", "energy_per_block_when_scanning", SS_ENERGY_PER_BLOCK_SCAN,
				"Energy consumed per block when scanning a ship (use -1 to consume everything)").getInt();
		if (SS_ENERGY_PER_BLOCK_SCAN != -1) {
			SS_ENERGY_PER_BLOCK_SCAN = clamp(1, SS_MAX_ENERGY_STORED, SS_ENERGY_PER_BLOCK_SCAN);
		}
		
		SS_ENERGY_PER_BLOCK_DEPLOY = config.get("ship_scanner", "energy_per_block_when_deploying", SS_ENERGY_PER_BLOCK_DEPLOY,
				"Energy consumed per block when deploying a ship (use -1 to consume everything)").getInt();
		if (SS_ENERGY_PER_BLOCK_DEPLOY != -1) {
			SS_ENERGY_PER_BLOCK_DEPLOY = clamp(1, SS_MAX_ENERGY_STORED, SS_ENERGY_PER_BLOCK_DEPLOY);
		}
		
		SS_MAX_DEPLOY_RADIUS_BLOCKS = clamp(5, 150,
				config.get("ship_scanner", "max_deploy_radius_blocks", SS_MAX_DEPLOY_RADIUS_BLOCKS, "Max distance from ship scanner to ship core, measured in blocks (5-150)").getInt());
		
		// Laser medium
		LASER_MEDIUM_MAX_ENERGY_STORED = clamp(1, Integer.MAX_VALUE,
				config.get("laser_medium", "max_energy_stored", LASER_MEDIUM_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		
		// Laser cannon
		LASER_CANNON_MAX_MEDIUMS_COUNT = clamp(1, 64,
				config.get("laser_cannon", "max_mediums_count", LASER_CANNON_MAX_MEDIUMS_COUNT, "Maximum number of laser mediums per laser").getInt());
		LASER_CANNON_MAX_LASER_ENERGY = clamp(1, Integer.MAX_VALUE,
				config.get("laser_cannon", "max_laser_energy", LASER_CANNON_MAX_LASER_ENERGY, "Maximum energy in beam after accounting for boosters beams").getInt());
		LASER_CANNON_EMIT_FIRE_DELAY_TICKS = clamp(1, 100,
				config.get("laser_cannon", "emit_fire_delay_ticks", LASER_CANNON_EMIT_FIRE_DELAY_TICKS, "Delay while booster beams are accepted, before actually shooting").getInt());
		LASER_CANNON_EMIT_SCAN_DELAY_TICKS = clamp(1, 100,
				config.get("laser_cannon", "emit_scan_delay_ticks", LASER_CANNON_EMIT_SCAN_DELAY_TICKS, "Delay while booster beams are accepted, before actually scanning").getInt());
		
		LASER_CANNON_BOOSTER_BEAM_ENERGY_EFFICIENCY = clamp(0.01D, 10.0D,
				config.get("laser_cannon", "booster_beam_energy_efficiency", LASER_CANNON_BOOSTER_BEAM_ENERGY_EFFICIENCY, "Energy factor applied from boosting to main laser").getDouble(0.6D));
		LASER_CANNON_ENERGY_ATTENUATION_PER_AIR_BLOCK = clamp(0.0D, 0.1D,
				config.get("laser_cannon", "energy_attenuation_per_air_block", LASER_CANNON_ENERGY_ATTENUATION_PER_AIR_BLOCK, "Energy attenuation when going through air blocks (on a planet or any gaz in space)").getDouble());
		LASER_CANNON_ENERGY_ATTENUATION_PER_VOID_BLOCK = clamp(0.0D, 0.1D,
				config.get("laser_cannon", "energy_attenuation_per_air_block", LASER_CANNON_ENERGY_ATTENUATION_PER_VOID_BLOCK, "Energy attenuation when going through void blocks (in space or hyperspace)").getDouble());
		LASER_CANNON_ENERGY_ATTENUATION_PER_BROKEN_BLOCK = clamp(0.0D, 1.0D,
				config.get("laser_cannon", "energy_attenuation_per_air_block", LASER_CANNON_ENERGY_ATTENUATION_PER_BROKEN_BLOCK, "Energy attenuation when going through a broken block").getDouble());
		LASER_CANNON_RANGE_MAX = clamp(64, 512,
				config.get("laser_cannon", "range_max", LASER_CANNON_RANGE_MAX, "Maximum distance travelled").getInt());
		
		LASER_CANNON_ENTITY_HIT_SET_ON_FIRE_SECONDS = clamp(0, 300,
				config.get("laser_cannon", "entity_hit_set_on_fire_seconds", LASER_CANNON_ENTITY_HIT_SET_ON_FIRE_SECONDS, "Duration of fire effect on entity hit (in seconds)").getInt());
		
		LASER_CANNON_ENTITY_HIT_ENERGY = clamp(0, LASER_CANNON_MAX_LASER_ENERGY,
				config.get("laser_cannon", "entity_hit_energy", LASER_CANNON_ENTITY_HIT_ENERGY, "Base energy consumed from hitting an entity").getInt());
		LASER_CANNON_ENTITY_HIT_BASE_DAMAGE = clamp(0, LASER_CANNON_MAX_LASER_ENERGY,
				config.get("laser_cannon", "entity_hit_base_damage", LASER_CANNON_ENTITY_HIT_BASE_DAMAGE, "Minimum damage to entity hit (measured in half hearts)").getInt());
		LASER_CANNON_ENTITY_HIT_ENERGY_PER_DAMAGE = clamp(0, LASER_CANNON_MAX_LASER_ENERGY,
				config.get("laser_cannon", "entity_hit_energy_per_damage", LASER_CANNON_ENTITY_HIT_ENERGY_PER_DAMAGE, "Energy required by additional hit point (won't be consumed)").getInt());
		LASER_CANNON_ENTITY_HIT_MAX_DAMAGE = clamp(0, Integer.MAX_VALUE,
				config.get("laser_cannon", "entity_hit_max_damage", LASER_CANNON_ENTITY_HIT_MAX_DAMAGE, "Maximum damage to entity hit, set to 0 to disable damage completely").getInt());
		
		LASER_CANNON_ENTITY_HIT_EXPLOSION_ENERGY_THRESHOLD = clamp(0, Integer.MAX_VALUE,
				config.get("laser_cannon", "entity_hit_energy_threshold_for_explosion", LASER_CANNON_ENTITY_HIT_EXPLOSION_ENERGY_THRESHOLD, "Minimum energy to cause explosion effect").getInt());
		LASER_CANNON_ENTITY_HIT_EXPLOSION_BASE_STRENGTH = (float) clamp(0.0D, 100.0D,
				config.get("laser_cannon", "entity_hit_explosion_base_strength", LASER_CANNON_ENTITY_HIT_EXPLOSION_BASE_STRENGTH, "Explosion base strength, 4 is Vanilla TNT").getDouble());
		LASER_CANNON_ENTITY_HIT_EXPLOSION_ENERGY_PER_STRENGTH = clamp(1, Integer.MAX_VALUE,
				config.get("laser_cannon", "entity_hit_explosion_energy_per_strength", LASER_CANNON_ENTITY_HIT_EXPLOSION_ENERGY_PER_STRENGTH, "Energy per added explosion strength").getInt());
		LASER_CANNON_ENTITY_HIT_EXPLOSION_MAX_STRENGTH = (float) clamp(0.0D, 1000.0D,
				config.get("laser_cannon", "entity_hit_explosion_max_strength", LASER_CANNON_ENTITY_HIT_EXPLOSION_MAX_STRENGTH, "Maximum explosion strength, set to 0 to disable explosion completely").getDouble());
		
		LASER_CANNON_BLOCK_HIT_ENERGY_MIN = clamp(0, Integer.MAX_VALUE,
				config.get("laser_cannon", "block_hit_energy_min", LASER_CANNON_BLOCK_HIT_ENERGY_MIN, "Minimum energy required for breaking a block").getInt());
		LASER_CANNON_BLOCK_HIT_ENERGY_PER_BLOCK_HARDNESS = clamp(0, Integer.MAX_VALUE,
				config.get("laser_cannon", "block_hit_energy_per_block_hardness", LASER_CANNON_BLOCK_HIT_ENERGY_PER_BLOCK_HARDNESS, "Energy cost per block hardness for breaking a block").getInt());
		LASER_CANNON_BLOCK_HIT_ENERGY_MAX = clamp(0, Integer.MAX_VALUE,
				config.get("laser_cannon", "block_hit_energy_max", LASER_CANNON_BLOCK_HIT_ENERGY_MAX, "Maximum energy required for breaking a block").getInt());
		LASER_CANNON_BLOCK_HIT_ABSORPTION_PER_BLOCK_HARDNESS = clamp(0.0D, 1.0D,
				config.get("laser_cannon", "block_hit_absorption_per_block_hardness", LASER_CANNON_BLOCK_HIT_ABSORPTION_PER_BLOCK_HARDNESS, "Probability of energy absorption (i.e. block not breaking) per block hardness. Set to 1.0 to always break the block.").getDouble());
		LASER_CANNON_BLOCK_HIT_ABSORPTION_MAX = clamp(0.0D, 1.0D,
				config.get("laser_cannon", "block_hit_absorption_max", LASER_CANNON_BLOCK_HIT_ABSORPTION_MAX, "Maximum probability of energy absorption (i.e. block not breaking)").getDouble());
		
		LASER_CANNON_BLOCK_HIT_EXPLOSION_HARDNESS_THRESHOLD = (float) clamp(0.0D, 10000.0D,
				config.get("laser_cannon", "block_hit_explosion_hardness_threshold", LASER_CANNON_BLOCK_HIT_EXPLOSION_HARDNESS_THRESHOLD,
						"Minimum block hardness required to cause an explosion").getDouble());
		LASER_CANNON_BLOCK_HIT_EXPLOSION_BASE_STRENGTH = (float) clamp(0.0D, 1000.0D,
				config.get("laser_cannon", "block_hit_explosion_base_strength", LASER_CANNON_BLOCK_HIT_EXPLOSION_BASE_STRENGTH, "Explosion base strength, 4 is Vanilla TNT").getDouble());
		LASER_CANNON_BLOCK_HIT_EXPLOSION_ENERGY_PER_STRENGTH = clamp(1, Integer.MAX_VALUE,
				config.get("laser_cannon", "block_hit_explosion_energy_per_strength", LASER_CANNON_BLOCK_HIT_EXPLOSION_ENERGY_PER_STRENGTH, "Energy per added explosion strength").getInt());
		LASER_CANNON_BLOCK_HIT_EXPLOSION_MAX_STRENGTH = (float) clamp(0.0D, 1000.0D,
				config.get("laser_cannon", "block_hit_explosion_max_strength", LASER_CANNON_BLOCK_HIT_EXPLOSION_MAX_STRENGTH, "Maximum explosion strength, set to 0 to disable explosion completely").getDouble());
		
		// Mining Laser
		MINING_LASER_MAX_MEDIUMS_COUNT = clamp(1, 64,
				config.get("mining_laser", "max_mediums_count", MINING_LASER_MAX_MEDIUMS_COUNT, "Maximum number of laser mediums").getInt());
		MINING_LASER_RADIUS_BLOCKS = clamp(1, 64,
				config.get("mining_laser", "radius_blocks", MINING_LASER_RADIUS_BLOCKS, "Mining radius").getInt());
		
		MINING_LASER_WARMUP_DELAY_TICKS = clamp(1, 300,
				config.get("mining_laser", "warmup_delay_ticks", MINING_LASER_WARMUP_DELAY_TICKS, "Warmup duration (buffer on startup when energy source is weak)").getInt());
		MINING_LASER_SCAN_DELAY_TICKS = clamp(1, 300,
				config.get("mining_laser", "scan_delay_ticks", MINING_LASER_SCAN_DELAY_TICKS, "Scan duration per layer").getInt());
		MINING_LASER_MINE_DELAY_TICKS = clamp(1, 300,
				config.get("mining_laser", "mine_delay_ticks", MINING_LASER_MINE_DELAY_TICKS, "Mining duration per scanned block").getInt());
		
		MINING_LASER_PLANET_ENERGY_PER_LAYER = clamp(1, Integer.MAX_VALUE,
				config.get("mining_laser", "planet_energy_per_layer", MINING_LASER_PLANET_ENERGY_PER_LAYER, "Energy cost per layer on a planet").getInt());
		MINING_LASER_PLANET_ENERGY_PER_BLOCK = clamp(1, Integer.MAX_VALUE,
				config.get("mining_laser", "planet_energy_per_block", MINING_LASER_PLANET_ENERGY_PER_BLOCK, "Energy cost per block in space").getInt());
		MINING_LASER_SPACE_ENERGY_PER_LAYER = clamp(1, Integer.MAX_VALUE,
				config.get("mining_laser", "space_energy_per_layer", MINING_LASER_SPACE_ENERGY_PER_LAYER, "Energy cost per layer on a planet").getInt());
		MINING_LASER_SPACE_ENERGY_PER_BLOCK = clamp(1, Integer.MAX_VALUE,
				config.get("mining_laser", "space_energy_per_block", MINING_LASER_SPACE_ENERGY_PER_BLOCK, "Energy cost per block in space").getInt());
		
		MINING_LASER_ORESONLY_ENERGY_FACTOR = clamp(1.5D, 1000.0D,
				config.get("mining_laser", "oresonly_energy_factor", MINING_LASER_ORESONLY_ENERGY_FACTOR, "Energy cost multiplier per block when mining only ores").getDouble(MINING_LASER_ORESONLY_ENERGY_FACTOR));
		MINING_LASER_SILKTOUCH_ENERGY_FACTOR = clamp(1.5D, 1000.0D,
				config.get("mining_laser", "silktouch_energy_factor", MINING_LASER_SILKTOUCH_ENERGY_FACTOR, "Energy cost multiplier per block when mining with silktouch").getDouble(MINING_LASER_SILKTOUCH_ENERGY_FACTOR));
		
		if (unused) {
			MINING_LASER_SILKTOUCH_DEUTERIUM_L = clamp(0.0D, 10.0D,
					config.get("mining_laser", "silktouch_deuterium_l", MINING_LASER_SILKTOUCH_DEUTERIUM_L, "Deuterium cost per block when mining with silktouch (0 to disable)").getDouble(1.0D));
			if (MINING_LASER_SILKTOUCH_DEUTERIUM_L < 0.001D) {
				MINING_LASER_SILKTOUCH_DEUTERIUM_L = 0.0D;
			}
			MINING_LASER_FORTUNE_ENERGY_FACTOR = clamp(0.01D, 1000.0D,
					config.get("mining_laser", "fortune_energy_factor", MINING_LASER_FORTUNE_ENERGY_FACTOR, "Energy cost multiplier per fortune level").getDouble(2.5D));
		}
		
		// Tree Farm
		TREE_FARM_MAX_MEDIUMS_COUNT = clamp(1, 10,
				config.get("tree_farm", "max_mediums_count", TREE_FARM_MAX_MEDIUMS_COUNT, "Maximum number of laser mediums").getInt());
		TREE_FARM_MAX_SCAN_RADIUS_NO_LASER_MEDIUM = clamp(1, 30,
				config.get("tree_farm", "max_scan_radius_no_laser_medium", TREE_FARM_MAX_SCAN_RADIUS_NO_LASER_MEDIUM, "Maximum scan radius without any laser medium, on X and Z axis, measured in blocks").getInt());
		TREE_FARM_MAX_SCAN_RADIUS_PER_LASER_MEDIUM = clamp(0, 5,
				config.get("tree_farm", "max_scan_radius_per_laser_medium", TREE_FARM_MAX_SCAN_RADIUS_PER_LASER_MEDIUM, "Bonus to maximum scan radius per laser medium, on X and Z axis, measured in blocks").getInt());
		TREE_FARM_totalMaxRadius = TREE_FARM_MAX_SCAN_RADIUS_NO_LASER_MEDIUM + TREE_FARM_MAX_MEDIUMS_COUNT * TREE_FARM_MAX_SCAN_RADIUS_PER_LASER_MEDIUM;
		
		TREE_FARM_MAX_LOG_DISTANCE = clamp(1, 64,
				config.get("tree_farm", "max_reach_distance", TREE_FARM_MAX_LOG_DISTANCE, "Maximum reach distance of the laser without any laser medium, measured in blocks").getInt());
		TREE_FARM_MAX_LOG_DISTANCE_PER_MEDIUM = clamp(0, 16,
				config.get("tree_farm", "max_reach_distance_per_laser_medium", TREE_FARM_MAX_LOG_DISTANCE_PER_MEDIUM, "Bonus to maximum reach distance per laser medium, measured in blocks").getInt());
		
		// Cloaking
		CLOAKING_MAX_ENERGY_STORED = clamp(1, Integer.MAX_VALUE,
				config.get("cloaking", "max_energy_stored", CLOAKING_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		CLOAKING_COIL_CAPTURE_BLOCKS = clamp(0, 30,
				config.get("cloaking", "coil_capture_blocks", CLOAKING_COIL_CAPTURE_BLOCKS, "Extra blocks covered after the outer coils").getInt());
		CLOAKING_MAX_FIELD_RADIUS = clamp(CLOAKING_COIL_CAPTURE_BLOCKS + 3, 128,
				config.get("cloaking", "max_field_radius", CLOAKING_MAX_FIELD_RADIUS, "Maximum distance between cloaking core and any cloaked side").getInt());
		CLOAKING_TIER1_ENERGY_PER_BLOCK = clamp(0, Integer.MAX_VALUE,
				config.get("cloaking", "tier1_energy_per_block", CLOAKING_TIER1_ENERGY_PER_BLOCK, "Energy cost per non-air block in a Tier1 cloak").getInt());
		CLOAKING_TIER2_ENERGY_PER_BLOCK = clamp(CLOAKING_TIER1_ENERGY_PER_BLOCK, Integer.MAX_VALUE,
				config.get("cloaking", "tier2_energy_per_block", CLOAKING_TIER2_ENERGY_PER_BLOCK, "Energy cost per non-air block in a Tier2 cloak").getInt());
		CLOAKING_FIELD_REFRESH_INTERVAL_SECONDS = clamp(1, 30,
				config.get("cloaking", "field_refresh_interval_seconds", CLOAKING_FIELD_REFRESH_INTERVAL_SECONDS, "Update speed of cloak simulation").getInt());
		
		// Air generator
		AIRGEN_MAX_ENERGY_STORED = clamp(1, Integer.MAX_VALUE,
				config.get("air_generator", "max_energy_stored", AIRGEN_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		AIRGEN_ENERGY_PER_CANISTER = clamp(1, AIRGEN_MAX_ENERGY_STORED,
				config.get("air_generator", "energy_per_canister", AIRGEN_ENERGY_PER_CANISTER, "Energy cost per air canister refilled").getInt());
		AIRGEN_ENERGY_PER_NEWAIRBLOCK = clamp(1, AIRGEN_MAX_ENERGY_STORED,
				config.get("air_generator", "energy_per_new_air_block", AIRGEN_ENERGY_PER_NEWAIRBLOCK, "Energy cost to start air distribution per open side").getInt());
		AIRGEN_ENERGY_PER_EXISTINGAIRBLOCK = clamp(1, AIRGEN_MAX_ENERGY_STORED,
				config.get("air_generator", "energy_per_existing_air_block", AIRGEN_ENERGY_PER_EXISTINGAIRBLOCK, "Energy cost to sustain air distribution per open side").getInt());
		AIRGEN_AIR_GENERATION_TICKS = clamp(1, 300,
				config.get("air_generator", "air_generation_ticks", AIRGEN_AIR_GENERATION_TICKS).getInt());
		
		// IC2 Reactor monitor
		IC2_REACTOR_MAX_ENERGY_STORED = clamp(1, Integer.MAX_VALUE,
				config.get("ic2_reactor_laser", "max_energy_stored", IC2_REACTOR_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		IC2_REACTOR_ENERGY_PER_HEAT = clamp(2.0D, 100000.0D,
				config.get("ic2_reactor_laser", "energy_per_heat", IC2_REACTOR_ENERGY_PER_HEAT, "Energy cost per heat absorbed").getDouble(2));
		IC2_REACTOR_COOLING_INTERVAL_TICKS = clamp(0, 1200,
				config.get("ic2_reactor_laser", "cooling_interval_ticks", IC2_REACTOR_COOLING_INTERVAL_TICKS, "Update speed of the check for reactors to cooldown").getInt());
		
		// Transporter
		TRANSPORTER_MAX_ENERGY_STORED = clamp(1, Integer.MAX_VALUE,
				config.get("transporter", "max_energy_stored", TRANSPORTER_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		TRANSPORTER_USE_RELATIVE_COORDS = config.get("transporter", "use_relative_coords", TRANSPORTER_USE_RELATIVE_COORDS, "Should transporter use relative coordinates?").getBoolean(true);
		TRANSPORTER_ENERGY_PER_BLOCK = clamp(1.0D, TRANSPORTER_MAX_ENERGY_STORED / 10.0D,
				config.get("transporter", "energy_per_block", TRANSPORTER_ENERGY_PER_BLOCK, "Energy cost per block distance").getDouble(100.0D));
		TRANSPORTER_MAX_BOOST_MUL = clamp(1.0D, 1000.0D,
				config.get("transporter", "max_boost", TRANSPORTER_MAX_BOOST_MUL, "Maximum energy boost allowed").getDouble(4.0));
		
		// Enantiomorphic reactor
		ENAN_REACTOR_MAX_ENERGY_STORED = clamp(1, Integer.MAX_VALUE,
				config.get("enantiomorphic_reactor", "max_energy_stored", ENAN_REACTOR_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		ENAN_REACTOR_UPDATE_INTERVAL_TICKS = clamp(1, 300,
				config.get("enantiomorphic_reactor", "update_interval_ticks", ENAN_REACTOR_UPDATE_INTERVAL_TICKS, "Update speed of the reactor simulation").getInt());
		ENAN_REACTOR_MAX_LASERS_PER_SECOND = clamp(4, 80,
				config.get("enantiomorphic_reactor", "max_lasers", ENAN_REACTOR_MAX_LASERS_PER_SECOND, "Maximum number of stabilisation laser shots per seconds before loosing efficiency").getInt());
		
		// Energy bank
		ENERGY_BANK_MAX_ENERGY_STORED = config.get("energy_bank", "max_energy_stored", ENERGY_BANK_MAX_ENERGY_STORED, "Maximum energy stored").getInt();
		
		// Lift
		LIFT_MAX_ENERGY_STORED = clamp(1, Integer.MAX_VALUE,
				config.get("lift", "max_energy_stored", LIFT_MAX_ENERGY_STORED, "Maximum energy stored").getInt());
		LIFT_ENERGY_PER_ENTITY = clamp(1, Integer.MAX_VALUE,
				config.get("lift", "energy_per_entity", LIFT_ENERGY_PER_ENTITY, "Energy consumed per entity moved").getInt());
		LIFT_UPDATE_INTERVAL_TICKS = clamp(1, 60,
				config.get("lift", "update_interval_ticks", LIFT_UPDATE_INTERVAL_TICKS, "Update speed of the check for entities").getInt());
		
		// Dictionary
		Dictionary.loadConfig(config);
		
		// Block transformers library
		blockTransformers = new HashMap<>();
		
		config.save();
	}
	
	public static void registerBlockTransformer(final String modId, IBlockTransformer blockTransformer) {
		blockTransformers.put(modId, blockTransformer);
		WarpDrive.logger.info(modId + " blockTransformer registered");
	}
	
	public static int clamp(final int min, final int max, final int value) {
		return Math.min(max, Math.max(value, min));
	}
	
	public static double clamp(final double min, final double max, final double value) {
		return Math.min(max, Math.max(value, min));
	}
	
	public static void onFMLInitialization() {
		isForgeMultipartLoaded = Loader.isModLoaded("ForgeMultipart");
		if (isForgeMultipartLoaded) {
			isForgeMultipartLoaded = CompatForgeMultipart.register();
		}
		
		isIndustrialCraft2Loaded = Loader.isModLoaded("IC2");
		if (isIndustrialCraft2Loaded) {
			loadIC2();
			CompatIndustrialCraft2.register();
		}
		
		isComputerCraftLoaded = Loader.isModLoaded("ComputerCraft");
		if (isComputerCraftLoaded) {
			loadCC();
			CompatComputerCraft.register();
		}
		
		isAdvancedSolarPanelLoaded = Loader.isModLoaded("AdvancedSolarPanel");
		isCoFHCoreLoaded = Loader.isModLoaded("CoFHCore");
		isThermalExpansionLoaded = Loader.isModLoaded("ThermalExpansion");
		isAppliedEnergistics2Loaded = Loader.isModLoaded("appliedenergistics2");
		if (isAppliedEnergistics2Loaded) {
			CompatAppliedEnergistics2.register();
		}
		isOpenComputersLoaded = Loader.isModLoaded("OpenComputers");
		if (isOpenComputersLoaded) {
			CompatOpenComputers.register();
		}
		isArsMagica2Loaded = Loader.isModLoaded("arsmagica2");
		if (isArsMagica2Loaded) {
			CompatArsMagica2.register();
		}
		isImmersiveEngineeringLoaded = Loader.isModLoaded("ImmersiveEngineering");
		if (isImmersiveEngineeringLoaded) {
			CompatImmersiveEngineering.register();
		}
		isGregTech5Loaded = false;
		if (Loader.isModLoaded("gregtech")) {
			String gregTechVersion = FMLCommonHandler.instance().findContainerFor("gregtech").getVersion();
			isGregTech5Loaded = gregTechVersion.equalsIgnoreCase("MC1710") || gregTechVersion.startsWith("5.");
		}
		isEnderIOloaded = Loader.isModLoaded("EnderIO");
		if (isEnderIOloaded) {
			CompatEnderIO.register();
		}
		isAdvancedRepulsionSystemLoaded = Loader.isModLoaded("AdvancedRepulsionSystems");
		if (isAdvancedRepulsionSystemLoaded) {
			CompatAdvancedRepulsionSystems.register();
		}
		
		boolean isBotaniaLoaded = Loader.isModLoaded("Botania");
		if (isBotaniaLoaded) {
			CompatBotania.register();
		}
		boolean isBibliocraftLoaded = Loader.isModLoaded("BiblioCraft");
		if (isBibliocraftLoaded) {
			CompatBiblioCraft.register();
		}
		boolean isEvilCraftLoaded = Loader.isModLoaded("evilcraft");
		if (isEvilCraftLoaded) {
			CompatEvilCraft.register();
		}
		boolean isJABBAloaded = Loader.isModLoaded("JABBA");
		if (isJABBAloaded) {
			CompatJABBA.register();
		}
		boolean isMetallurgyLoaded = Loader.isModLoaded("Metallurgy");
		if (isMetallurgyLoaded) {
			CompatMetallurgy.register();
		}
		boolean isNaturaLoaded = Loader.isModLoaded("Natura");
		if (isNaturaLoaded) {
			CompatNatura.register();
		}
		boolean isRedstonePasteLoaded = Loader.isModLoaded("RedstonePasteMod");
		if (isRedstonePasteLoaded) {
			CompatRedstonePaste.register();
		}
		boolean isSGCraftLoaded = Loader.isModLoaded("SGCraft");
		if (isSGCraftLoaded) {
			CompatSGCraft.register();
		}
		boolean isStargateTech2Loaded = Loader.isModLoaded("StargateTech2");
		if (isStargateTech2Loaded) {
			CompatStargateTech2.register();
		}
		boolean isTConstructLoaded = Loader.isModLoaded("TConstruct");
		if (isTConstructLoaded) {
			CompatTConstruct.register();
		}
		boolean isThaumcraftLoaded = Loader.isModLoaded("Thaumcraft");
		if (isThaumcraftLoaded) {
			CompatThaumcraft.register();
		}
	}
	
	public static void onFMLPostInitialization() {
		// unpack default XML files if none are defined
		File[] files = configDirectory.listFiles((file_notUsed, name) -> {
			return name.endsWith(".xml");
		});
		if (files.length == 0) {
			for (String defaultXMLfilename : defaultXMLfilenames) {
				unpackResourceToFolder(defaultXMLfilename, "config", configDirectory);
			}
		}
		
		// always unpack the XML Schema
		unpackResourceToFolder("WarpDrive.xsd", "config", configDirectory);
		
		// load XML files
		FillerManager.load(configDirectory);
		StructureManager.load(configDirectory);
		
		Dictionary.apply();
	}
	
	private static void loadIC2() {
		try {
			IC2_emptyCell = getModItemStack("IC2", "itemCellEmpty", -1);
			IC2_compressedAir = getModItemStack("IC2", "itemCellEmpty", 5);
			
			IC2_rubberWood = getModBlock("IC2", "blockRubWood");
			IC2_Resin = getModItemStack("IC2", "itemHarz", -1);
		} catch (Exception exception) {
			WarpDrive.logger.error("Error loading IndustrialCraft2 classes");
			exception.printStackTrace();
		}
	}
	
	private static void loadCC() {
		try {
			CC_Computer = getModBlock("ComputerCraft", "CC-Computer");
			CC_peripheral = getModBlock("ComputerCraft", "CC-Peripheral");
			CCT_Turtle = getModBlock("ComputerCraft", "CC-Turtle");
			CCT_Expanded = getModBlock("ComputerCraft", "CC-TurtleExpanded");
			CCT_Advanced = getModBlock("ComputerCraft", "CC-TurtleAdvanced");
		} catch (Exception exception) {
			WarpDrive.logger.error("Error loading ComputerCraft classes");
			exception.printStackTrace();
		}
	}
	
	public static DocumentBuilder getXmlDocumentBuilder() {
		if (xmlDocumentBuilder == null) {
			
			ErrorHandler xmlErrorHandler = new ErrorHandler() {
				@Override
				public void warning(SAXParseException exception) throws SAXException {
					WarpDrive.logger.warn("XML warning: " + exception.getLocalizedMessage());
					// exception.printStackTrace();
				}
				
				@Override
				public void fatalError(SAXParseException exception) throws SAXException {
					WarpDrive.logger.warn("XML fatal error: " + exception.getLocalizedMessage());
					// exception.printStackTrace();
				}
				
				@Override
				public void error(SAXParseException exception) throws SAXException {
					WarpDrive.logger.warn("XML error: " + exception.getLocalizedMessage());
					// exception.printStackTrace();
				}
			};
			
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setIgnoringComments(false);
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setValidating(true);
			documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
			
			try {
				xmlDocumentBuilder = documentBuilderFactory.newDocumentBuilder();
			} catch (ParserConfigurationException exception) {
				exception.printStackTrace();
			}
			xmlDocumentBuilder.setErrorHandler(xmlErrorHandler);
		}
		
		return xmlDocumentBuilder;
	}
	
	/**
	 * Copy a default configuration file from the mod's resources to the specified configuration folder
	 **/
	private static void unpackResourceToFolder(final String filename, final String sourceResourcePath, File targetFolder) {
		// targetFolder is already created by caller
		
		String resourceName = sourceResourcePath + "/" + filename;
		
		File destination = new File(targetFolder, filename);
		
		try {
			InputStream inputStream = WarpDrive.class.getClassLoader().getResourceAsStream(resourceName);
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destination));
			
			byte[] byteBuffer = new byte[Math.max(8192, inputStream.available())];
			int bytesRead;
			while ((bytesRead = inputStream.read(byteBuffer)) >= 0) {
				outputStream.write(byteBuffer, 0, bytesRead);
			}
			
			inputStream.close();
			outputStream.close();
		} catch (Exception exception) {
			WarpDrive.logger.error("Failed to unpack resource \'" + resourceName + "\' into " + destination);
			exception.printStackTrace();
		}
	}
}
