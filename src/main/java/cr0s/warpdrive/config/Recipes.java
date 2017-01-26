package cr0s.warpdrive.config;

import java.util.List;

import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fml.common.registry.GameRegistry;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.decoration.BlockDecorative;
import cr0s.warpdrive.data.*;
import cr0s.warpdrive.item.ItemComponent;
import cr0s.warpdrive.item.ItemForceFieldShape;
import cr0s.warpdrive.item.ItemForceFieldUpgrade;
import cr0s.warpdrive.item.ItemUpgrade;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Hold the different recipe sets
 */
public class Recipes {
	private static final String[] oreDyes = {
		"dyeBlack",
		"dyeRed",
		"dyeGreen",
		"dyeBrown",
		"dyeBlue",
		"dyePurple",
		"dyeCyan",
		"dyeLightGray",
		"dyeGray",
		"dyePink",
		"dyeLime",
		"dyeYellow",
		"dyeLightBlue",
		"dyeMagenta",
		"dyeOrange",
		"dyeWhite"
	};
	
	public static void initVanilla() {
		// Components recipes
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL), false, "nrn", "r r", "nrn",
				'r', Items.REDSTONE,
				'n', Items.GOLD_NUGGET));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.ENDER_CRYSTAL), false, "g", "e", "c",
				'g', Blocks.GLASS,
				'e', Items.ENDER_PEARL,
				'c', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL)));
			
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.DIAMOND_CRYSTAL), false, " g ", "ede", " c ",
				'g', Blocks.GLASS,
				'e', Items.ENDER_PEARL,
				'd', Items.DIAMOND,
				'c', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL)));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.DIFFRACTION_GRATING), false, " g ", "rtr", " c ",
				'g', Blocks.GLASS,
				'r', "dyeBlue",
				't', Blocks.TORCH,
				'c', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL)));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.REACTOR_CORE), false, " l ", "rcr", " l ",
				'l', "dyeWhite",
				'r', Items.COAL,
				'c', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL)));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE), false, "g  ", "gwr", "rwr",
				'g', Items.GOLD_NUGGET,
				'r', Items.REDSTONE,
				'w', "plankWood"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE), false, "gig", "iri", "gig",
				'g', Items.GOLD_NUGGET,
				'r', Items.REDSTONE,
				'i', Items.IRON_INGOT));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.CAPACITIVE_CRYSTAL), false, "glg", "ldl", "glg",
				'g', Items.GOLD_NUGGET,
				'l', "dyeBlue",
				'd', Items.DIAMOND));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.AIR_CANISTER), false, "gcg", "g g", "gcg",
				'g', Blocks.GLASS,
				'c', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL)));
		
		// Decorative blocks
		GameRegistry.addRecipe(new ShapedOreRecipe(BlockDecorative.getItemStackNoCache(EnumDecorativeType.PLAIN, 8), false, "sss", "scs", "sss",
				's', Blocks.STONE,
				'c', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL)));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(BlockDecorative.getItemStackNoCache(EnumDecorativeType.NETWORK, 8), false, "sss", "scs", "sss",
				's', BlockDecorative.getItemStack(EnumDecorativeType.PLAIN),
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE)));
		
		// Upgrade items
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUpgrade.getItemStack(UpgradeType.Energy), false, "c", "e", "r",
				'c', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL),
				'e', ItemComponent.getItemStack(EnumComponentType.CAPACITIVE_CRYSTAL),
				'r', Items.REDSTONE));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUpgrade.getItemStack(UpgradeType.Power), false, "c", "e", "r",
				'c', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL),
				'e', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE),
				'r', Items.REDSTONE));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUpgrade.getItemStack(UpgradeType.Speed), false, "c", "e", "r",
				'c', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL),
				'e', Items.SUGAR,
				'r', Items.REDSTONE));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUpgrade.getItemStack(UpgradeType.Range), false, "c", "e", "r",
				'c', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL),
				'e', WarpDrive.blockTransportBeacon,
				'r', Items.REDSTONE));
		
		// WarpCore
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipCore), false, "ipi", "ici", "idi",
				'i', Items.IRON_INGOT,
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE),
				'c', ItemComponent.getItemStack(EnumComponentType.DIAMOND_CRYSTAL),
				'd', Items.DIAMOND));
		
		// Controller
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipController), false, "ici", "idi", "iii",
				'i', Items.IRON_INGOT,
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'd', Items.DIAMOND));
		
		// Radar
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockRadar), false, "ggg", "pdc", "iii",
				'i', Items.IRON_INGOT,
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE),
				'g', Blocks.GLASS,
				'd', Items.DIAMOND));
		
		// Isolation Block
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockWarpIsolation), false, "igi", "geg", "igi",
				'i', Items.IRON_INGOT,
				'g', Blocks.GLASS,
				'e', Items.ENDER_PEARL));
		
		// Air generator
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockAirGenerator), false, "ibi", "i i", "ipi",
				'i', Items.IRON_INGOT,
				'b', Blocks.IRON_BARS,
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE)));
		
		// Laser
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaser), false, "ili", "iri", "ici",
				'i', Items.IRON_INGOT,
				'r', Items.REDSTONE,
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'l', ItemComponent.getItemStack(EnumComponentType.DIFFRACTION_GRATING),
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE)));
		
		// Mining laser
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockMiningLaser), false, "ici", "iti", "ili",
				'i', Items.IRON_INGOT,
				'r', Items.REDSTONE,
				't', ItemComponent.getItemStack(EnumComponentType.ENDER_CRYSTAL),
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'l', ItemComponent.getItemStack(EnumComponentType.DIFFRACTION_GRATING)));
		
		// Tree farm laser
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserTreeFarm), false, "ili", "sts", "ici",
				'i', Items.IRON_INGOT,
				's', "treeSapling",
				't', ItemComponent.getItemStack(EnumComponentType.ENDER_CRYSTAL), 
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'l', ItemComponent.getItemStack(EnumComponentType.DIFFRACTION_GRATING)));
		
		// Laser Lift
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLift), false, "ipi", "rtr", "ili",
				'i', Items.IRON_INGOT,
				'r', Items.REDSTONE,
				't', ItemComponent.getItemStack(EnumComponentType.ENDER_CRYSTAL),
				'l', ItemComponent.getItemStack(EnumComponentType.DIFFRACTION_GRATING),
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE)));
		
		// Transporter
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockTransporter), false, "iii", "ptc", "iii",
				'i', Items.IRON_INGOT,
				't', ItemComponent.getItemStack(EnumComponentType.ENDER_CRYSTAL),
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE)));
		
		// Laser medium
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserMedium), false, "ipi", "rgr", "iii", 
				'i', Items.IRON_INGOT,
				'r', Items.REDSTONE,
				'g', Blocks.GLASS,
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE)));
		
		// Camera
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCamera), false, "ngn", "i i", "ici",
				'i', Items.IRON_INGOT,
				'n', Items.GOLD_NUGGET,
				'g', Blocks.GLASS,
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE)));
		
		// LaserCamera
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(WarpDrive.blockLaserCamera), WarpDrive.blockCamera, WarpDrive.blockLaser));
		
		// Monitor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockMonitor), false, "ggg", "iti", "ici",
				'i', Items.IRON_INGOT,
				't', Blocks.TORCH,
				'g', Blocks.GLASS,
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE)));
		
		// Cloaking device
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCloakingCore), false, "ipi", "lrl", "ici",
				'i', Items.IRON_INGOT,
				'r', Items.REDSTONE,
				'l', ItemComponent.getItemStack(EnumComponentType.DIFFRACTION_GRATING),
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE)));
		
		// Cloaking coil
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCloakingCoil), false, "ini", "rdr", "ini",
				'i', Items.IRON_INGOT,
				'd', Items.DIAMOND,
				'r', Items.REDSTONE,
				'n', Items.GOLD_NUGGET));
		
		// Power Laser
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockEnanReactorLaser), false, "iii", "ilg", "ici",
				'i', Items.IRON_INGOT,
				'g', Blocks.GLASS,
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'l', ItemComponent.getItemStack(EnumComponentType.DIFFRACTION_GRATING)));
		
		// Power Reactor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockEnanReactorCore), false, "ipi", "gog", "ici",
				'i', Items.IRON_INGOT,
				'g', Blocks.GLASS,
				'o', ItemComponent.getItemStack(EnumComponentType.REACTOR_CORE),
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE)));
		
		// Power Store
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockEnergyBank), false, "ipi", "isi", "ici",
				'i', Items.IRON_INGOT,
				's', ItemComponent.getItemStack(EnumComponentType.CAPACITIVE_CRYSTAL),
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE)));
		
		// Transport Beacon
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockTransportBeacon), false, " e ", "ldl", " s ",
				'e', Items.ENDER_PEARL,
				'l', "dyeBlue",
				'd', Items.DIAMOND,
				's', Items.STICK));
		
		// Chunk Loader
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockChunkLoader), false, "ipi", "ici", "ifi",
				'i', Items.IRON_INGOT,
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE),
				'c', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL),
				'f', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE)));
		
		// Helmet
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.itemHelmet), false, "iii", "iwi", "gcg",
				'i', Items.IRON_INGOT,
				'w', Blocks.WOOL,
				'g', Blocks.GLASS,
				'c', ItemComponent.getItemStack(EnumComponentType.AIR_CANISTER)));
	}
	
	public static void initIC2() {
		ItemStack advancedAlloy = WarpDriveConfig.getModItemStack("ic2", "crafting", 3);
		ItemStack iridiumAlloy = WarpDriveConfig.getModItemStack("ic2", "crafting", 4); // Iridium reinforced place
		ItemStack advancedMachine = WarpDriveConfig.getModItemStack("ic2", "resource", 13); // Advanced machine casing
		ItemStack miner = WarpDriveConfig.getModItemStack("ic2", "te", 60);
		ItemStack magnetizer = WarpDriveConfig.getModItemStack("ic2", "te", 37);
		ItemStack fiberGlassCable = WarpDriveConfig.getModItemStack("ic2", "cable", 1);
		ItemStack circuit = WarpDriveConfig.getModItemStack("ic2", "crafting", 1);
		ItemStack advancedCircuit = WarpDriveConfig.getModItemStack("ic2", "crafting", 2);
		ItemStack ironPlate = WarpDriveConfig.getModItemStack("ic2", "plate", 3);
		ItemStack mfe = WarpDriveConfig.getModItemStack("ic2", "te", 74);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockShipCore), "ici", "cmc", "ici",
				'i', iridiumAlloy,
				'm', advancedMachine,
				'c', advancedCircuit);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockShipController), "iic", "imi", "cii",
				'i', iridiumAlloy,
				'm', advancedMachine,
				'c', advancedCircuit);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockRadar), "ifi", "imi", "imi",
				'i', iridiumAlloy,
				'm', advancedMachine,
				'f', WarpDriveConfig.getModItemStack("ic2", "frequency_transmitter", -1));
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockWarpIsolation), "iii", "idi", "iii",
				'i', iridiumAlloy,
				'm', advancedMachine,
				'd', Blocks.DIAMOND_BLOCK);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockAirGenerator), "lcl", "lml", "lll",
				'l', Blocks.LEAVES,
				'm', advancedMachine,
				'c', advancedCircuit);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockLaser), "sss", "ama", "aaa",
				'm', advancedMachine,
				'a', advancedAlloy,
				's', advancedCircuit);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockMiningLaser), "aaa", "ama", "ccc",
				'c', advancedCircuit,
				'a', advancedAlloy,
				'm', miner);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockLaserMedium), "afc", "ama", "cfa",
				'c', advancedCircuit,
				'a', advancedAlloy,
				'f', fiberGlassCable,
				'm', mfe);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockLift), "aca", "ama", "a#a",
				'c', advancedCircuit,
				'a', advancedAlloy,
				'm', magnetizer);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockIridium), "iii", "iii", "iii",
				'i', iridiumAlloy);
		
		GameRegistry.addShapelessRecipe(new ItemStack(iridiumAlloy.getItem(), 9), new ItemStack(WarpDrive.blockIridium));
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockLaserCamera), "imi", "cec", "#k#",
				'i', iridiumAlloy,
				'm', advancedMachine,
				'c', advancedCircuit,
				'e', WarpDrive.blockLaser,
				'k', WarpDrive.blockCamera);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockCamera), "cgc", "gmg", "cgc",
				'm', advancedMachine,
				'c', advancedCircuit,
				'g', Blocks.GLASS);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockMonitor), "gcg", "gmg", "ggg",
				'm', advancedMachine,
				'c', advancedCircuit,
				'g', Blocks.GLASS);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockShipScanner), "sgs", "mma", "amm",
				'm', advancedMachine,
				'a', advancedAlloy,
				's', advancedCircuit,
				'g', Blocks.GLASS);
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserTreeFarm), false, new Object[] { "cwc", "wmw", "cwc",
				'c', circuit,
				'w', "logWood",
				'm', WarpDrive.blockMiningLaser }));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockTransporter), false, new Object[] { "ece", "imi", "iei",
				'e', Items.ENDER_PEARL,
				'c', circuit,
				'i', ironPlate,
				'm', advancedMachine }));
		
		if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.itemIC2reactorLaserFocus), false, new Object[] { " p ", "pdp", " p ",
					'p', ironPlate,
					'd', "gemDiamond" }));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockIC2reactorLaserMonitor), false, new Object[] { "pdp", "dmd", "pdp",
					'p', ironPlate,
					'd', "gemDiamond",
					'm', mfe }));
		}
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockCloakingCore), "imi", "mcm", "imi",
				'i', WarpDrive.blockIridium,
				'c', WarpDrive.blockCloakingCoil,
				'm', advancedMachine);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockCloakingCoil), "iai", "aca", "iai",
				'i', iridiumAlloy,
				'c', advancedCircuit,
				'a', advancedAlloy);
	}
	
	public static void initHardIC2() {
		ItemStack advancedAlloy = WarpDriveConfig.getModItemStack("ic2", "crafting", 3);
		ItemStack iridiumAlloy = WarpDriveConfig.getModItemStack("ic2", "crafting", 4); // Iridium reinforced place
		ItemStack advancedMachine = WarpDriveConfig.getModItemStack("ic2", "resource", 13);
		ItemStack magnetizer = WarpDriveConfig.getModItemStack("ic2", "te", 37);
		ItemStack fiberGlassCable = WarpDriveConfig.getModItemStack("ic2", "cable", 1);
		ItemStack mfe = WarpDriveConfig.getModItemStack("ic2", "te", 74);
		ItemStack mfsu = WarpDriveConfig.getModItemStack("ic2", "te", 75);
		ItemStack energiumDust = WarpDriveConfig.getModItemStack("ic2", "dust", 6);
		ItemStack crystalMemory = WarpDriveConfig.getModItemStack("ic2", "crystal_memory", -1);
		ItemStack itemHAMachine = new ItemStack(WarpDrive.blockHighlyAdvancedMachine);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockShipCore),"uau", "tmt", "uau",
				'a', advancedAlloy,
				't', WarpDriveConfig.getModItemStack("ic2", "te", 39), // Teleporter
				'm', itemHAMachine,
				'u', mfsu);
		
		if (WarpDriveConfig.isOpenComputersLoaded) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipController), false, new Object[] { "aha", "cmc", "apa", // With OC Adapter
					'a', advancedAlloy,
					'm', itemHAMachine,
					'c', "circuitAdvanced",
					'h', crystalMemory,
					'p', WarpDriveConfig.getModItemStack("OpenComputers", "adapter", -1)}));
		} else if (WarpDriveConfig.isComputerCraftLoaded) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipController), false, new Object[] { "aha", "cmc", "apa", // With CC Modem
					'a', advancedAlloy,
					'm', itemHAMachine,
					'c', "circuitAdvanced",
					'h', crystalMemory,
					'p', WarpDriveConfig.getModItemStack("ComputerCraft", "CC-Cable", 1)}));
		} else {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipController), false, new Object[] { "aha", "cmc", "aca",
				'a', advancedAlloy,
				'm', itemHAMachine,
				'c', "circuitAdvanced",
				'h', crystalMemory}));
		}
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockRadar), false, new Object[] { "afa", "cmc", "aca",
				'a', advancedAlloy,
				'm', itemHAMachine,
				'c', "circuitAdvanced",
				'f', WarpDriveConfig.getModItemStack("ic2", "frequency_transmitter", -1)}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockWarpIsolation), false, new Object[] { "sls", "lml", "sls",
				's', "plateDenseSteel",
				'l', "plateDenseLead",
				'm', itemHAMachine}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockAirGenerator), false, new Object[] { "lel", "vmv", "lcl",
				'l', Blocks.LEAVES, 
				'm', WarpDriveConfig.getModItemStack("ic2", "resource", 12), // Basic machine casing
				'c', "circuitBasic",
				'e', WarpDriveConfig.getModItemStack("ic2", "te", 43), // Compressor
				'v', WarpDriveConfig.getModItemStack("ic2", "reactorVent", -1)}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaser), false, new Object[] { "aca", "cmc", "ala",
				'm', advancedMachine,
				'a', advancedAlloy,
				'c', "circuitAdvanced",
				'l', WarpDriveConfig.getModItemStack("ic2", "mining_laser", -1)}));	// Mining laser
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockMiningLaser), false, new Object[] { "pcp", "pap", "plp",
				'c', "circuitAdvanced",
				'p', advancedAlloy,
				'a', WarpDriveConfig.getModItemStack("ic2", "te", 57), // Advanced Miner
				'l', WarpDrive.blockLaser}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserMedium), false, new Object[] { "efe", "aca", "ama",
				'c', "circuitAdvanced",
				'a', advancedAlloy,
				'f', fiberGlassCable,
				'e', energiumDust,
				'm', mfe}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLift), false, new Object[] { "aca", "ama", "aea",
				'c', "circuitAdvanced",
				'a', advancedAlloy,
				'm', magnetizer,
				'e', energiumDust}));
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockIridium), "iii", "iii", "iii",
				'i', iridiumAlloy);
		
		GameRegistry.addShapelessRecipe(new ItemStack(iridiumAlloy.getItem(), 9), new ItemStack(WarpDrive.blockIridium));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserCamera), false, new Object[] { "ala", "sss", "aca",
				'a', advancedAlloy,
				's', "circuitAdvanced",
				'l', WarpDrive.blockLaser,
				'c', WarpDrive.blockCamera}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCamera), false, new Object[] { "aed", "cma", "aga",
				'a', advancedAlloy,
				'e', WarpDriveConfig.getModItemStack("ic2", "crafting", 6), // Electric Motor
				'd', "gemDiamond",
				'c', crystalMemory,
				'm', advancedMachine,
				'g', WarpDriveConfig.getModItemStack("ic2", "cable", 2)})); // Gold cable
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockMonitor), false, new Object[] { "ala", "aca", "aga",
				'a', advancedAlloy,
				'l', Blocks.REDSTONE_LAMP,
				'c', "circuitAdvanced",
				'g', "paneGlassColorless" }));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipScanner), false, new Object[] { "ici", "isi", "mcm",
				'm', mfsu,
				'i', iridiumAlloy,
				'c', "circuitAdvanced",
				's', WarpDriveConfig.getModItemStack("ic2", "te", 64) })); // Scanner
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserTreeFarm), false, new Object[] { "awa", "cmc", "asa",
				'a', advancedAlloy,
				'c', "circuitAdvanced",
				'w', "logWood",
				'm', WarpDrive.blockMiningLaser,
				's', WarpDriveConfig.getModItemStack("ic2", "chainsaw", -1) })); // Chainsaw
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockTransporter), false, new Object[] { "aea", "ctc", "ama",
				'a', advancedAlloy,
				'e', Items.ENDER_PEARL,
				'c', "circuitAdvanced",
				'm', advancedMachine,
				't', WarpDriveConfig.getModItemStack("ic2", "te", 39) })); // Teleporter
		
		// IC2 is loaded for this recipe set
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.itemIC2reactorLaserFocus), false, new Object[] { "a a", " d ", "a a",
				'a', advancedAlloy,
				'd', "gemDiamond" }));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockIC2reactorLaserMonitor), false, new Object[] { "pdp", "dmd", "pdp",
				'p', advancedAlloy,
				'd', "gemDiamond",
				'm', mfe }));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCloakingCore), false, new Object[] { "ici", "cmc", "igi",
				'i', WarpDrive.blockIridium,
				'c', WarpDrive.blockCloakingCoil,
				'm', WarpDrive.blockHighlyAdvancedMachine,
				'g', "circuitAdvanced" }));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCloakingCoil), false, new Object[] { "iai", "ccc", "iai",
				'i', iridiumAlloy,
				'c', WarpDriveConfig.getModItemStack("ic2", "crafting", 5), // Coil
				'a', advancedAlloy })); 
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockHighlyAdvancedMachine), "iii", "imi", "iii",
				'i', iridiumAlloy,
				'm', advancedMachine);
	}
	
	@SuppressWarnings("UnusedAssignment")
	public static void initDynamic() {
		// Support iron bars from all mods
		Object ironBars = Blocks.IRON_BARS;
		if (OreDictionary.doesOreNameExist("barsIron") && !OreDictionary.getOres("barsIron").isEmpty()) {
			ironBars = "barsIron";
		}
		
		// Add Reinforced iridium plate to ore registry as applicable (it's missing in IC2 without GregTech)
		if (WarpDriveConfig.isIndustrialCraft2Loaded && (!OreDictionary.doesOreNameExist("plateAlloyIridium") || OreDictionary.getOres("plateAlloyIridium").isEmpty())) {
			ItemStack iridiumAlloy = WarpDriveConfig.getModItemStack("ic2", "crafting", 4); // Iridium reinforced place
			OreDictionary.registerOre("plateAlloyIridium", iridiumAlloy);
		}
		
		// Get the machine casing to use
		ItemStack itemStackMachineCasingLV;
		ItemStack itemStackMachineCasingMV;
		ItemStack itemStackMachineCasingHV;
		ItemStack itemStackMachineCasingEV;
		ItemStack itemStackMotorLV = ItemComponent.getItemStack(EnumComponentType.MOTOR);
		ItemStack itemStackMotorMV = ItemComponent.getItemStack(EnumComponentType.MOTOR);
		ItemStack itemStackMotorHV = ItemComponent.getItemStack(EnumComponentType.MOTOR);
		ItemStack itemStackMotorEV = ItemComponent.getItemStack(EnumComponentType.MOTOR);
		
		if (WarpDriveConfig.isGregTech5Loaded) {
			itemStackMachineCasingLV = WarpDriveConfig.getModItemStack("gregtech", "gt.blockcasings", 1);
			itemStackMachineCasingMV = WarpDriveConfig.getModItemStack("gregtech", "gt.blockcasings", 2);
			itemStackMachineCasingHV = WarpDriveConfig.getModItemStack("gregtech", "gt.blockcasings", 3);
			itemStackMachineCasingEV = WarpDriveConfig.getModItemStack("gregtech", "gt.blockcasings", 4);
			
			itemStackMotorLV = WarpDriveConfig.getModItemStack("gregtech", "gt.metaitem.01", 32600);    // LV Motor
			itemStackMotorMV = WarpDriveConfig.getModItemStack("gregtech", "gt.metaitem.01", 32601);    // MV Motor
			itemStackMotorHV = WarpDriveConfig.getModItemStack("gregtech", "gt.metaitem.01", 32602);    // HV Motor
			itemStackMotorEV = WarpDriveConfig.getModItemStack("gregtech", "gt.metaitem.01", 32603);    // EV Motor
			
		} else if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			itemStackMachineCasingLV = WarpDriveConfig.getModItemStack("ic2", "resource", 12);          // Basic machine casing
			itemStackMachineCasingMV = WarpDriveConfig.getModItemStack("ic2", "resource", 13);          // Advanced machine casing
			itemStackMachineCasingHV = new ItemStack(WarpDrive.blockHighlyAdvancedMachine);
			itemStackMachineCasingEV = new ItemStack(WarpDrive.blockHighlyAdvancedMachine);
			
			itemStackMotorHV = WarpDriveConfig.getModItemStack("ic2", "crafting", 6);                   // Electric motor
			itemStackMotorEV = WarpDriveConfig.getModItemStack("ic2", "crafting", 6);                   // Electric motor
			
			ItemStack iridiumAlloy = WarpDriveConfig.getModItemStack("ic2", "crafting", 4);				// Iridium reinforced place
			GameRegistry.addRecipe(new ItemStack(WarpDrive.blockHighlyAdvancedMachine), "iii", "imi", "iii",
				'i', iridiumAlloy,
				'm', itemStackMachineCasingMV);
			
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			itemStackMachineCasingLV = WarpDriveConfig.getModItemStack("ThermalExpansion", "Frame", 0);
			itemStackMachineCasingMV = WarpDriveConfig.getModItemStack("ThermalExpansion", "Frame", 1);
			itemStackMachineCasingHV = WarpDriveConfig.getModItemStack("ThermalExpansion", "Frame", 2);
			itemStackMachineCasingEV = WarpDriveConfig.getModItemStack("ThermalExpansion", "Frame", 3);
			
		} else if (WarpDriveConfig.isEnderIOLoaded) {
			itemStackMachineCasingLV = WarpDriveConfig.getModItemStack("EnderIO", "itemMachinePart", 0);     // Machine chassis
			itemStackMachineCasingMV = WarpDriveConfig.getModItemStack("EnderIO", "blockVacuumChest", 0);    // Pulsating crystal => Vacuum chest
			itemStackMachineCasingHV = WarpDriveConfig.getModItemStack("EnderIO", "blockIngotStorage", 2);   // Vibrant alloy block
			itemStackMachineCasingEV = WarpDriveConfig.getModItemStack("EnderIO", "itemMaterial", 8);        // Ender crystal
			
		} else {// vanilla
			itemStackMachineCasingLV = new ItemStack(Blocks.IRON_BLOCK);
			itemStackMachineCasingMV = new ItemStack(Blocks.DIAMOND_BLOCK);
			itemStackMachineCasingHV = new ItemStack(WarpDrive.blockHighlyAdvancedMachine);
			itemStackMachineCasingEV = new ItemStack(Blocks.BEACON);
			
			GameRegistry.addRecipe(new ItemStack(WarpDrive.blockHighlyAdvancedMachine, 4), "pep", "ede", "pep",
			'e', Items.EMERALD,
			'p', Items.ENDER_EYE,
			'd', Blocks.DIAMOND_BLOCK);
		}
		
		ItemStack[] itemStackMachineCasings = { itemStackMachineCasingLV, itemStackMachineCasingMV, itemStackMachineCasingHV, itemStackMachineCasingEV };
		ItemStack[] itemStackMotors  = { itemStackMotorLV, itemStackMotorMV, itemStackMotorHV, itemStackMotorEV };
		
		// Base tuning crystals
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL), false, " e ", "BBB", "qrq",
				'e', Items.EMERALD,
				'B', ironBars,
				'r', Items.REDSTONE,
				'q', Items.QUARTZ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.ENDER_CRYSTAL), false, " e ", "BBB", "grg",
				'e', Items.ENDER_PEARL,
				'B', ironBars,
				'r', Items.REDSTONE,
				'g', "nuggetGold"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.DIAMOND_CRYSTAL), false, " d ", "BBB", "prp",
				'd', Items.DIAMOND,
				'B', ironBars,
				'r', Items.REDSTONE,
				'p', Items.PAPER));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.DIFFRACTION_GRATING), false, " t ", "iii", "ggg",
				't', Items.GHAST_TEAR,
				'i', ironBars,
				'g', Blocks.GLOWSTONE));
		
		// Intermediary component for reactor core
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.REACTOR_CORE), false, "shs", "hmh", "shs",
				's', Items.NETHER_STAR,
				'h', "blockHull3_plain",
				'm', itemStackMachineCasings[2]));
		
		
		// Computer interface is 2 gold ingot, 2 wired modems (or redstone), 1 lead/tin ingot
		{
			Object redstoneOrModem = Items.REDSTONE;
			if (WarpDriveConfig.isComputerCraftLoaded) {
				redstoneOrModem = WarpDriveConfig.getModItemStack("ComputerCraft", "CC-Cable", 1); // Wired modem
			}
			// if (OreDictionary.doesOreNameExist("circuitPrimitive") && !OreDictionary.getOres("circuitPrimitive").isEmpty()) { // Gregtech
			Object oreCircuitOrHeavyPressurePlate = Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE;
			int outputFactor = 1;
			if (OreDictionary.doesOreNameExist("oc:materialCU") && !OreDictionary.getOres("oc:materialCU").isEmpty()) {
				oreCircuitOrHeavyPressurePlate = "oc:materialCU";	// Control circuit is 5 redstone, 5 gold ingot, 3 paper
				outputFactor = 2;
			} else if (OreDictionary.doesOreNameExist("circuitBasic") && !OreDictionary.getOres("circuitBasic").isEmpty()) {// comes with IndustrialCraft2
				oreCircuitOrHeavyPressurePlate = "circuitBasic";
				outputFactor = 2;
			}
			String oreSlimeOrTinOrLead = "slimeball";
			// Computer interface: double output with Soldering alloy
			if (OreDictionary.doesOreNameExist("ingotSolderingAlloy") && !OreDictionary.getOres("ingotSolderingAlloy").isEmpty()) {
				GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(EnumComponentType.COMPUTER_INTERFACE, 2 * outputFactor), false, "   ", "rar", "gGg",
						'G', oreCircuitOrHeavyPressurePlate,
						'g', "ingotGold",
						'r', redstoneOrModem,
						'a', "ingotSolderingAlloy"));
			}
			// Computer interface: simple output
			if (OreDictionary.doesOreNameExist("ingotTin") && !OreDictionary.getOres("ingotTin").isEmpty()) {
				oreSlimeOrTinOrLead = "ingotTin";
			} else if (OreDictionary.doesOreNameExist("ingotLead") && !OreDictionary.getOres("ingotLead").isEmpty()) {
				oreSlimeOrTinOrLead = "ingotLead";
			}
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(EnumComponentType.COMPUTER_INTERFACE, outputFactor), false, "   ", "rar", "gGg",
					'G', oreCircuitOrHeavyPressurePlate,
					'g', "ingotGold",
					'r', redstoneOrModem,
					'a', oreSlimeOrTinOrLead));
		}
		
		// Power interface is 4 redstone, 2 iron ingot, 3 gold ingot
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(EnumComponentType.POWER_INTERFACE, 2), false, "rgr", "igi", "rgr",
				'g', Items.GOLD_INGOT,
				'r', Items.REDSTONE,
				'i', Items.IRON_INGOT));
		
		// Capacitive crystal is 3 redstone block, 3 paper, 3 lapis block or 1 HV capacitor from IE or 1 MFE from IC2
		if (OreDictionary.doesOreNameExist("dustLithium") && !OreDictionary.getOres("dustLithium").isEmpty()) {// comes with GregTech and Industrial Craft 2
			// (Lithium is processed from nether quartz)
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(EnumComponentType.CAPACITIVE_CRYSTAL, 2), false, "plp", "lRl", "plp",
					'R', new ItemStack(Items.POTIONITEM, 1, 8225),	// Regeneration II
					'l', "dustLithium",
					'p', Items.PAPER));
		} else if (WarpDriveConfig.isImmersiveEngineeringLoaded) {
			ItemStack itemStackCapacitorHV = WarpDriveConfig.getModItemStack("ImmersiveEngineering", "metalDevice", 7);
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(EnumComponentType.CAPACITIVE_CRYSTAL, 2), false, " m ", "ppp", " m ",
					'm', itemStackCapacitorHV,
					'p', Items.PAPER));
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			ItemStack itemStackHardenedEnergyCell = WarpDriveConfig.getModItemStack("ThermalExpansion", "Cell", 2);
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(EnumComponentType.CAPACITIVE_CRYSTAL, 2), false, " m ", "ppp", " m ",
					'm', itemStackHardenedEnergyCell,
					'p', Items.PAPER));
		} else if (WarpDriveConfig.isEnderIOLoaded) {
			ItemStack itemStackBasicCapacitorBank = WarpDriveConfig.getModItemStack("EnderIO", "blockCapBank", 1);
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(EnumComponentType.CAPACITIVE_CRYSTAL, 2), false, " m ", "ppp", " m ",
					'm', itemStackBasicCapacitorBank,
					'p', Items.PAPER));
		} else {// Vanilla
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(EnumComponentType.CAPACITIVE_CRYSTAL, 2), false, "qrq", "pSp", "qrq",
					'S', new ItemStack(Items.POTIONITEM, 1, 8265),	// Strength I long (blaze powder + redstone)
					'r', Blocks.REDSTONE_BLOCK,
					'p', Items.PAPER,
					'q', Items.QUARTZ));
		}
		
		// Air canister is 4 iron bars, 2 leather/rubber, 2 yellow wool, 1 tank
		Object rubberOrLeather = Items.LEATHER;
		if (OreDictionary.doesOreNameExist("plateRubber") && !OreDictionary.getOres("plateRubber").isEmpty()) {// comes with GregTech
			rubberOrLeather = "plateRubber";
		} else if (OreDictionary.doesOreNameExist("itemRubber") && !OreDictionary.getOres("itemRubber").isEmpty()) {// comes with IndustrialCraft2
			rubberOrLeather = "itemRubber";
		}
		Object woolYellow = new ItemStack(Blocks.WOOL, 1, 4);
		if (OreDictionary.doesOreNameExist("blockWoolYellow") && !OreDictionary.getOres("blockWoolYellow").isEmpty()) {
			woolYellow = "blockWoolYellow";
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(EnumComponentType.AIR_CANISTER, 4), false, "iyi", "rgr", "iyi",
				'r', rubberOrLeather,
				'g', ItemComponent.getItemStack(EnumComponentType.GLASS_TANK),
				'y', woolYellow,
				'i', ironBars));
		
		// Lens is 1 diamond, 2 gold ingots, 2 glass panels
		if (OreDictionary.doesOreNameExist("lensDiamond") && !OreDictionary.getOres("lensDiamond").isEmpty()) {
			if (OreDictionary.doesOreNameExist("craftingLensWhite") && !OreDictionary.getOres("craftingLensWhite").isEmpty()) {
				GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(EnumComponentType.LENS, 3), false, "ggg", "pdp", "ggg",
						'g', "nuggetGold",
						'p', "craftingLensWhite",
						'd', "lensDiamond"));
			} else {
				GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.LENS), false, " g ", "pdp", " g ",
						'g', Items.GOLD_INGOT,
						'p', "paneGlassColorless",
						'd', "lensDiamond"));
			}
		} else if (WarpDriveConfig.isAdvancedRepulsionSystemLoaded) {
			ItemStack diamondLens = WarpDriveConfig.getModItemStack("AdvancedRepulsionSystems", "{A8F3AF2F-0384-4EAA-9486-8F7E7A1B96E7}", 1);
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.LENS), false, " g ", "pdp", " g ",
					'g', Items.GOLD_INGOT,
					'p', "paneGlassColorless",
					'd', diamondLens));
		} else {
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(EnumComponentType.LENS, 2), false, " g ", "pdp", " g ",
					'g', Items.GOLD_INGOT,
					'p', "paneGlassColorless",
					'd', "gemDiamond"));
		}
		
		// Zoom is 3 lenses, 2 iron ingot, 2 dyes, 2 redstone
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.ZOOM), false, "dir", "lll", "dit",
				'r', Items.REDSTONE,
				'i', Items.IRON_INGOT,
				'l', ItemComponent.getItemStack(EnumComponentType.LENS),
				't', itemStackMotors[0],
				'd', "dye"));
		
		// Glass tank is 4 slime balls, 4 glass
		// slimeball && blockGlass are defined by forge itself
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(EnumComponentType.GLASS_TANK, 4), false, "sgs", "g g", "sgs",
				's', "slimeball",
				'g', "blockGlass"));
		
		// Flat screen is 3 dyes, 1 glowstone dust, 2 paper, 3 glass panes
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.FLAT_SCREEN), false, "gRp", "gGd", "gBp",
				'R', "dyeRed",
				'G', "dyeLime",
				'B', "dyeBlue",
				'd', "dustGlowstone",
				'g', "paneGlassColorless",
				'p', Items.PAPER));
		
		// Memory bank is 2 papers, 2 iron bars, 4 comparators, 1 redstone
		if (OreDictionary.doesOreNameExist("circuitPrimitive") && !OreDictionary.getOres("circuitPrimitive").isEmpty()) { // Gregtech
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.MEMORY_CRYSTAL), false, "cic", "cic", "prp",
					'i', ironBars,
					'c', "circuitPrimitive",
					'r', Items.REDSTONE,
					'p', Items.PAPER));
		} else if (OreDictionary.doesOreNameExist("oc:ram3") && !OreDictionary.getOres("oc:ram3").isEmpty()) {
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(EnumComponentType.MEMORY_CRYSTAL, 4), false, "cic", "cic", "prp",
					'i', ironBars,
					'c', "oc:ram3",
					'r', Items.REDSTONE,
					'p', Items.PAPER));
		} else {
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.MEMORY_CRYSTAL), false, "cic", "cic", "prp",
					'i', ironBars,
					'c', Items.COMPARATOR,
					'r', Items.REDSTONE,
					'p', Items.PAPER));
		}
		
		// Motor is 2 gold nuggets (wires), 3 iron ingots (steel rods), 4 iron bars (coils)
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.MOTOR), false, "bbn", "iii", "bbn",
				'b', ironBars,
				'i', Items.IRON_INGOT,
				'n', "nuggetGold"));
		
		// Bone charcoal is smelting 1 bone
		GameRegistry.addSmelting(Items.BONE, ItemComponent.getItemStackNoCache(EnumComponentType.BONE_CHARCOAL, 1), 1);
		
		// Activated carbon is 1 bone charcoal, 4 sticks, 4 leaves
		Object leaves = Blocks.LEAVES;
		if (OreDictionary.doesOreNameExist("treeLeaves") && !OreDictionary.getOres("treeLeaves").isEmpty()) {
			leaves = "treeLeaves";
		}
		if (OreDictionary.doesOreNameExist("dustSulfur") && !OreDictionary.getOres("dustSulfur").isEmpty()) {
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.ACTIVATED_CARBON), false, "lll", "aaa", "fwf",
					'l', leaves,
					'a', ItemComponent.getItemStack(EnumComponentType.BONE_CHARCOAL),
					'w', new ItemStack(Items.POTIONITEM, 1, 0),
					'f', "dustSulfur"));
		} else {
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.ACTIVATED_CARBON), false, "lll", "aaa", "wgw",
					'l', leaves,
					'a', ItemComponent.getItemStack(EnumComponentType.BONE_CHARCOAL),
					'w', new ItemStack(Items.POTIONITEM, 1, 0),
					'g', Items.GUNPOWDER));
		}
		
		// Laser medium (empty) is 3 glass tanks, 1 power interface, 1 computer interface, 1 MV Machine casing
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.LASER_MEDIUM_EMPTY), false, "   ", "ggg", "pmc",
				'g', ItemComponent.getItemStack(EnumComponentType.GLASS_TANK),
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE),
				'm', itemStackMachineCasings[1],
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE)));
		
		// Coil crystal is 6 iron bars, 2 gold ingots, 1 diamond crystal, return 12x
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(EnumComponentType.COIL_CRYSTAL, 12), false, "bbg", "bdb", "gbb",
		                                          'b', ironBars,
		                                          'g', "ingotGold",
		                                          'd', ItemComponent.getItemStack(EnumComponentType.DIAMOND_CRYSTAL)));
		
		// Electromagnetic Projector is 5 coil crystals, 1 power interface, 1 computer interface, 2 motors
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(EnumComponentType.ELECTROMAGNETIC_PROJECTOR), false, "CCm", "Cpc", "CCm",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE),
		                                          'm', ItemComponent.getItemStack(EnumComponentType.MOTOR),
		                                          'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE)));
		
		// *** Force field shapes
		// Force field shapes are 1 Memory crystal, 3 to 5 Coil crystal
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldShape.getItemStack(EnumForceFieldShape.SPHERE), false, "   ", "CmC", "CCC",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'm', ItemComponent.getItemStack(EnumComponentType.MEMORY_CRYSTAL)));
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldShape.getItemStack(EnumForceFieldShape.CYLINDER_H), false, "C C", " m ", "C C",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'm', ItemComponent.getItemStack(EnumComponentType.MEMORY_CRYSTAL)));
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldShape.getItemStack(EnumForceFieldShape.CYLINDER_V), false, " C ", "CmC", " C ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'm', ItemComponent.getItemStack(EnumComponentType.MEMORY_CRYSTAL)));
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldShape.getItemStack(EnumForceFieldShape.CUBE), false, "CCC", "CmC", "   ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'm', ItemComponent.getItemStack(EnumComponentType.MEMORY_CRYSTAL)));
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldShape.getItemStack(EnumForceFieldShape.PLANE), false, "CCC", " m ", "   ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'm', ItemComponent.getItemStack(EnumComponentType.MEMORY_CRYSTAL)));
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldShape.getItemStack(EnumForceFieldShape.TUBE), false, "   ", "CmC", "C C",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'm', ItemComponent.getItemStack(EnumComponentType.MEMORY_CRYSTAL)));
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldShape.getItemStack(EnumForceFieldShape.TUNNEL), false, "C C", "CmC", "   ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'm', ItemComponent.getItemStack(EnumComponentType.MEMORY_CRYSTAL)));
		
		// *** Force field upgrades
		// Force field attraction upgrade is 3 Coil crystal, 1 Iron block, 2 Redstone block, 1 MV motor
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.ATTRACTION), false, "CCC", "rir", " m ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'r', Blocks.REDSTONE_BLOCK,
		                                          'i', Blocks.IRON_BLOCK,
		                                          'm', itemStackMotorMV));
		// Force field breaking upgrade is 3 Coil crystal, 1 Diamond axe, 1 diamond shovel, 1 diamond pick
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.BREAKING), false, "CCC", "sap", "   ",
			                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
			                                          's', Items.DIAMOND_AXE,
			                                          'a', Items.DIAMOND_SHOVEL,
			                                          'p', Items.DIAMOND_PICKAXE));
		// Force field camouflage upgrade is 3 Coil crystal, 2 Diffraction grating, 1 Zoom, 1 Emerald crystal
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.CAMOUFLAGE), false, "CCC", "zre", "   ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'z', ItemComponent.getItemStack(EnumComponentType.ZOOM),
		                                          'r', Blocks.DAYLIGHT_DETECTOR,
		                                          'e', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL)));
		// Force field cooling upgrade is 3 Coil crystal, 2 Ice, 1 MV Motor
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.COOLING), false, "CCC", "imi", "   ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'i', Blocks.ICE,
		                                          'm', itemStackMotors[1]));
		// Force field fusion upgrade is 3 Coil crystal, 2 Computer interface, 1 Emerald crystal
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.FUSION), false, "CCC", "cec", "   ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
		                                          'e', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL)));
		// Force field heating upgrade is 3 Coil crystal, 2 Blaze rod, 1 MV Motor
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.HEATING), false, "CCC", "bmb", "   ",
			                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
			                                          'b', Items.BLAZE_ROD,
			                                          'm', itemStackMotors[1]));
		// Force field inversion upgrade is 3 Coil crystal, 1 Gold nugget, 2 Redstone
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.INVERSION), false, "rgr", "CCC", "CCC",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'r', Items.REDSTONE,
		                                          'g', Items.GOLD_NUGGET));
		// Force field item port upgrade is 3 Coil crystal, 3 Chests, 1 MV motor
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.ITEM_PORT), false, "CCC", "cmc", " c ",
			                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
			                                          'c', Blocks.CHEST,
			                                          'm', itemStackMotorMV));
		// Force field silencer upgrade is 3 Coil crystal, 3 Wool
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.SILENCER), false, "CCC", "www", "   ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'w', Blocks.WOOL));
		// Force field pumping upgrade is 3 Coil crystal, 1 MV Motor, 2 glass tanks
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.PUMPING), false, "CCC", "tmt", "   ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'm', itemStackMotors[1],
		                                          't', ItemComponent.getItemStack(EnumComponentType.GLASS_TANK)));
		// Force field range upgrade is 3 Coil crystal, 2 Memory crystal, 1 Redstone block
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.RANGE), false, "CCC", "RMR", "   ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'M', ItemComponent.getItemStack(EnumComponentType.MEMORY_CRYSTAL),
		                                          'R', Blocks.REDSTONE_BLOCK));
		// Force field repulsion upgrade is 3 Coil crystal, 1 Iron block, 2 Redstone block, 1 MV motor
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.REPULSION), false, " m ", "rir", "CCC",
			                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
			                                          'r', Blocks.REDSTONE_BLOCK,
			                                          'i', Blocks.IRON_BLOCK,
			                                          'm', itemStackMotorMV));
		// Force field rotation upgrade is 3 Coil crystal, 2 MV Motors, 1 Computer interface
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStackNoCache(EnumForceFieldUpgrade.ROTATION, 2), false, "CCC", " m ", " mc",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'm', itemStackMotors[1],
		                                          'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE)));
		// Force field shock upgrade is 3 Coil crystal, 1 Power interface
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.SHOCK), false, "CCC", " p ", "   ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE)));
		// Force field speed upgrade is 3 Coil crystal, 2 Ghast tear, 1 Emerald crystal
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.SPEED), false, "CCC", "geg", "   ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'g', Items.GHAST_TEAR,
		                                          'e', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL)));
		// Force field stabilization upgrade is 3 Coil crystal, 1 Memory crystal, 2 Lapis block
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.STABILIZATION), "CCC", "lMl", "   ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'M', ItemComponent.getItemStack(EnumComponentType.MEMORY_CRYSTAL),
		                                          'l', Blocks.LAPIS_BLOCK));
		// Force field thickness upgrade is 8 Coil crystal, 1 Diamond crystal
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStack(EnumForceFieldUpgrade.THICKNESS), false, "CCC", "CpC", "   ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'p', ItemComponent.getItemStack(EnumComponentType.ELECTROMAGNETIC_PROJECTOR)));
		// Force field translation upgrade is 3 Coil crystal, 2 MV Motor, 1 Computer interface
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemForceFieldUpgrade.getItemStackNoCache(EnumForceFieldUpgrade.TRANSLATION, 2), false, "CCC", "m m", " c ",
		                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
		                                          'm', itemStackMotors[1],
		                                          'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE)));
		
		// *** Blocks
		// Ship core is 1 Ghast tear, 4 Capacitive crystal, 2 Tuning ender, 1 Power interface, 1 MV Machine casing
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockShipCore),"csc", "eme", "cpc",
				's', Items.GHAST_TEAR,
				'c', ItemComponent.getItemStack(EnumComponentType.CAPACITIVE_CRYSTAL),
				'e', ItemComponent.getItemStack(EnumComponentType.ENDER_CRYSTAL),
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE),
				'm', itemStackMachineCasings[1]);
		
		// Ship controller is 1 Computer interface, 1 Tuning emerald, 1 LV Machine casing, 2 Memory bank
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipController), false, " e ", "bmb", " c ",
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'e', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL),
				'm', itemStackMachineCasings[0],
				'b', ItemComponent.getItemStack(EnumComponentType.MEMORY_CRYSTAL)));
		
		// Radar is 1 motor, 4 Titanium plate (diamond), 1 quarztite rod (nether quartz), 1 computer interface, 1 HV Machine casing, 1 power interface
		String oreCloakingPlate = "gemQuartz";
		if (OreDictionary.doesOreNameExist("plateTitanium") && !OreDictionary.getOres("plateTitanium").isEmpty()) {// Gregtech
			oreCloakingPlate = "plateTitanium";
		} else if (OreDictionary.doesOreNameExist("ingotEnderium") && !OreDictionary.getOres("ingotEnderium").isEmpty()) {// ThermalExpansion
			oreCloakingPlate = "ingotEnderium";
		} else if (OreDictionary.doesOreNameExist("ingotPhasedGold") && !OreDictionary.getOres("ingotPhasedGold").isEmpty()) {// EnderIO
			oreCloakingPlate = "ingotPhasedGold";
		} else if (OreDictionary.doesOreNameExist("plateAlloyIridium") && !OreDictionary.getOres("plateAlloyIridium").isEmpty()) {// IndustrialCraft2
			oreCloakingPlate = "plateAlloyIridium";
		}
		Object oreAntenna = Items.GHAST_TEAR;
		if (OreDictionary.doesOreNameExist("stickQuartzite")) {// GregTech
			oreAntenna = "stickQuartzite";
		} else if (OreDictionary.doesOreNameExist("ingotSignalum") && !OreDictionary.getOres("ingotSignalum").isEmpty()) {// ThermalExpansion
			oreAntenna = "ingotSignalum";
		} else if (OreDictionary.doesOreNameExist("nuggetPulsatingIron") && !OreDictionary.getOres("nuggetPulsatingIron").isEmpty()) {// EnderIO
			oreAntenna = "nuggetPulsatingIron";
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockRadar), false, "PAP", "PtP", "pmc",
			't', itemStackMotors[2],
			'P', oreCloakingPlate,
			'A', oreAntenna,
			'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
			'm', itemStackMachineCasings[2],
			'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE)));
		
		// Warp isolation is 1 EV Machine casing (Ti), 4 Titanium plate/Enderium ingot/Vibrant alloy/Iridium plate/quartz
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockWarpIsolation), false, "i i", " m ", "i i",
				'i', oreCloakingPlate,
				'm', itemStackMachineCasings[3]));
		
		// Air generator is 1 power interface, 4 activated carbon, 1 motor, 1 MV Machine casing, 2 tanks
		ItemStack itemStackCompressorOrTank = ItemComponent.getItemStack(EnumComponentType.GLASS_TANK);
		if (WarpDriveConfig.isGregTech5Loaded) {
			itemStackCompressorOrTank = WarpDriveConfig.getModItemStack("gregtech", "gt.metaitem.02", 21300); // Bronze rotor
		} else if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			itemStackCompressorOrTank = WarpDriveConfig.getModItemStack("ic2", "te", 43); // Compressor
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			itemStackCompressorOrTank = WarpDriveConfig.getModItemStack("ThermalExpansion", "Machine", 5); // Fluid transposer
		} else if (WarpDriveConfig.isEnderIOLoaded) {
			itemStackCompressorOrTank = WarpDriveConfig.getModItemStack("EnderIO", "blockReservoir", 0);
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockAirGenerator), false, "aca", "ata", "gmp",
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE),
				'a', ItemComponent.getItemStack(EnumComponentType.ACTIVATED_CARBON),
				't', itemStackMotors[1],
				'g', ItemComponent.getItemStack(EnumComponentType.GLASS_TANK),
				'm', itemStackMachineCasings[1],
				'c', itemStackCompressorOrTank));
		
		// Laser cannon is 2 motors, 1 diffraction grating, 1 lens, 1 computer interface, 1 HV Machine casing, 1 redstone dust, 2 glass pane
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaser), false, "gtr", "ldm", "gtc",
				't', itemStackMotors[2],
				'd', ItemComponent.getItemStack(EnumComponentType.DIFFRACTION_GRATING),
				'l', ItemComponent.getItemStack(EnumComponentType.LENS),
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'm', itemStackMachineCasings[1],
				'r', Items.REDSTONE,
				'g', "paneGlassColorless"));
		
		// Mining laser is 2 motors, 1 diffraction grating, 1 lens, 1 computer interface, 1 MV Machine casing, 1 diamond pick, 2 glass pane
		ItemStack itemStackDiamondPick = new ItemStack(Items.DIAMOND_PICKAXE);
		if (WarpDriveConfig.isGregTech5Loaded) {
			itemStackDiamondPick = WarpDriveConfig.getModItemStack("ic2", "mining_laser", 1); // Mining laser
		} else if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			// itemStackDiamondPick = WarpDriveConfig.getModItemStack("ic2", "te", 57); // Advanced Miner
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockMiningLaser), false, "cmr", "tdt", "glg",
				't', itemStackMotors[1],
				'd', ItemComponent.getItemStack(EnumComponentType.DIFFRACTION_GRATING),
				'l', ItemComponent.getItemStack(EnumComponentType.LENS),
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'm', itemStackMachineCasings[1],
				'r', itemStackDiamondPick,
				'g', "paneGlassColorless"));
		
		// Laser medium (full) is 1 laser medium (empty), 4 redstone blocks, 4 lapis blocks
		// TODO: add fluid transposer/canning support
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserMedium), false, "lrl", "rmr", "lrl",
				'm', ItemComponent.getItemStack(EnumComponentType.LASER_MEDIUM_EMPTY),
				'r', Blocks.REDSTONE_BLOCK,
				'l', Blocks.LAPIS_BLOCK));
		
		// Laser lift is ...
		Object oreMagnetizer = itemStackMachineCasings[0];
		if (WarpDriveConfig.isGregTech5Loaded) {
			oreMagnetizer = WarpDriveConfig.getModItemStack("gregtech", "gt.blockmachines", 551);	// Basic polarizer
		} else if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			oreMagnetizer = WarpDriveConfig.getModItemStack("ic2", "te", 37); // Magnetizer
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			oreMagnetizer = WarpDriveConfig.getModItemStack("ThermalExpansion", "Plate", 3);
		} else if (OreDictionary.doesOreNameExist("ingotRedstoneAlloy") && !OreDictionary.getOres("ingotRedstoneAlloy").isEmpty()) {// EnderIO
			oreMagnetizer = "ingotRedstoneAlloy";
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLift), false, "rmw", "plc", "glg",
				'r', Items.REDSTONE,
				'w', Blocks.WOOL,
				'l', ItemComponent.getItemStack(EnumComponentType.LENS),
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'm', oreMagnetizer,
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE),
				'g', "paneGlassColorless"));
		
		// Iridium block is just that
		if (WarpDriveConfig.isGregTech5Loaded) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockIridium), "iii", "iii", "iii",
					'i', "plateAlloyIridium"));
			ItemStack itemStackIridiumAlloy = WarpDriveConfig.getModItemStack("ic2", "crafting", 4); // Iridium reinforced place
			GameRegistry.addShapelessRecipe(new ItemStack(itemStackIridiumAlloy.getItem(), 9), new ItemStack(WarpDrive.blockIridium));
			
		} else if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			ItemStack itemStackIridiumAlloy = WarpDriveConfig.getModItemStack("ic2", "crafting", 4); // Iridium reinforced place
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockIridium), "iii", "iii", "iii",
					'i', itemStackIridiumAlloy));
			GameRegistry.addShapelessRecipe(new ItemStack(itemStackIridiumAlloy.getItem(), 9), new ItemStack(WarpDrive.blockIridium));
			
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockIridium), "ses", "ele", "ses",
					'l', "ingotLumium",
					's', "ingotSignalum",
					'e', "ingotEnderium"));
			// GameRegistry.addShapelessRecipe(new ItemStack(itemStackIridiumAlloy.getItem(), 9), new ItemStack(WarpDrive.blockIridium));
			
		} else if (WarpDriveConfig.isEnderIOLoaded) {
			ItemStack itemStackVibrantAlloy = WarpDriveConfig.getModItemStack("EnderIO", "itemAlloy", 2);
			ItemStack itemStackRedstoneAlloy = WarpDriveConfig.getModItemStack("EnderIO", "itemAlloy", 3);
			ItemStack itemStackFranckNZombie = WarpDriveConfig.getModItemStack("EnderIO", "itemFrankenSkull", 2);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockIridium, 4), "ses", "ele", "ses",
					'l', itemStackFranckNZombie,
					's', itemStackRedstoneAlloy,
					'e', itemStackVibrantAlloy));
			// GameRegistry.addShapelessRecipe(new ItemStack(itemStackIridiumAlloy.getItem(), 9), new ItemStack(WarpDrive.blockIridium));
			
		} else {
			GameRegistry.addRecipe(new ItemStack(WarpDrive.blockIridium), "ded", "yty", "ded",
					't', Items.GHAST_TEAR,
					'd', Items.DIAMOND,
					'e', Items.EMERALD,
					'y', Items.ENDER_EYE);
		}
		
		// Laser camera is just Laser + Camera
		if (OreDictionary.doesOreNameExist("circuitBasic") && !OreDictionary.getOres("circuitBasic").isEmpty()) {// comes with IndustrialCraft2
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserCamera), false, "rlr", "rsr", "rcr",
					'r', rubberOrLeather,
					's', "circuitBasic",
					'l', WarpDrive.blockLaser,
					'c', WarpDrive.blockCamera));
		} else {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserCamera), false, "rlr", "rsr", "rcr",
					'r', rubberOrLeather,
					's', "nuggetGold",
					'l', WarpDrive.blockLaser,
					'c', WarpDrive.blockCamera));
		}
		
		// Weapon controller is diamond sword with Ship controller
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockWeaponController), false, "rwr", "rsr", "rcr",
				'r', rubberOrLeather,
				's', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL),
				'w', Items.DIAMOND_SWORD,
				'c', WarpDrive.blockShipController));
		
		// Camera is 1 daylight sensor, 2 motors, 1 computer interface, 2 glass panel, 1 Tuning diamond, 1 LV Machine casing
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCamera), false, "gtd", "zlm", "gtc",
				't', itemStackMotors[0],
				'z', ItemComponent.getItemStack(EnumComponentType.ZOOM),
				'd', ItemComponent.getItemStack(EnumComponentType.DIAMOND_CRYSTAL),
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'm', itemStackMachineCasings[0],
				'l', Blocks.DAYLIGHT_DETECTOR,
				'g', "paneGlassColorless"));
		
		// Monitor is 3 flat screen, 1 computer interface, 1 Tuning diamond, 1 LV Machine casing
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockMonitor), false, "fd ", "fm ", "fc ",
				'f', ItemComponent.getItemStack(EnumComponentType.FLAT_SCREEN),
				'd', ItemComponent.getItemStack(EnumComponentType.DIAMOND_CRYSTAL),
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'm', itemStackMachineCasings[0]));
		
		// Ship scanner is non-functional => no recipe
		/*
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipScanner), false, "ici", "isi", "mcm",
				'm', mfsu,
				'i', iridiumAlloy,
				'c', "circuitAdvanced",
				's', WarpDriveConfig.getModItemStack("ic2", "te", 64) )); // Scanner
		/**/
		
		// Laser tree farm is 2 motors, 2 lenses, 1 computer interface, 1 LV Machine casing, 1 diamond axe, 2 glass pane
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserTreeFarm), false, "glg", "tlt", "amc",
				't', itemStackMotors[0],
				'l', ItemComponent.getItemStack(EnumComponentType.LENS),
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'm', itemStackMachineCasings[0],
				'a', Items.DIAMOND_AXE,
				'g', "paneGlassColorless"));
		
		// Transporter is non-functional => no recipe
		/*
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockTransporter), false, "aea", "ctc", "ama",
				'a', advancedAlloy,
				'e', Items.ender_pearl,
				'c', "circuitAdvanced",
				'm', advancedMachine,
				't', WarpDriveConfig.getModItemStack("ic2", "te", 39) )); // Teleporter
		/**/
		
		// IC2 needs to loaded for the following 2 recipes
		if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			ItemStack itemStackOverclockedHeatVent = WarpDriveConfig.getModItemStack("ic2", "overclocked_heat_vent", 1);
			ItemStack itemStackReactorCoolant1 = WarpDriveConfig.getModItemStack("ic2", "hex_heat_storage", -1);
			ItemStack itemStackReactorCoolant2 = itemStackReactorCoolant1;
			if (WarpDriveConfig.isGregTech5Loaded) {
				itemStackReactorCoolant1 = WarpDriveConfig.getModItemStack("gregtech", "gt.360k_Helium_Coolantcell", -1);
				itemStackReactorCoolant2 = WarpDriveConfig.getModItemStack("gregtech", "gt.360k_NaK_Coolantcell", -1);
			}
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.itemIC2reactorLaserFocus), false, "cld", "lhl", "dlc",
					'l', ItemComponent.getItemStack(EnumComponentType.LENS),
					'h', itemStackOverclockedHeatVent,
					'c', itemStackReactorCoolant1,
					'd', itemStackReactorCoolant2));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockIC2reactorLaserMonitor), false, "gCp", "lme", "gCc",
					'l', ItemComponent.getItemStack(EnumComponentType.LENS),
					'e', ItemComponent.getItemStack(EnumComponentType.EMERALD_CRYSTAL),
					'C', ItemComponent.getItemStack(EnumComponentType.CAPACITIVE_CRYSTAL),
					'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
					'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE),
					'g', "paneGlassColorless",
					'm', itemStackMachineCasings[1]));
		}
		
		// Cloaking core is 3 Cloaking coils, 4 iridium blocks, 1 ship controller, 1 power interface
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCloakingCore), false, "ici", "csc", "ipi",
				'i', WarpDrive.blockIridium,
				'c', WarpDrive.blockCloakingCoil,
				's', WarpDrive.blockShipController,
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE)));
		
		// Cloaking coil is 1 Titanium plate, 4 reinforced iridium plate, 1 EV Machine casing (Ti) or 1 Beacon, 4 emerald, 4 diamond
		ItemStack itemStackGoldIngotOrCoil = new ItemStack(Items.GOLD_INGOT);
		if (WarpDriveConfig.isGregTech5Loaded) {
			itemStackGoldIngotOrCoil = WarpDriveConfig.getModItemStack("gregtech", "gt.blockcasings", 14);	// Nichrome Coil block
		} else if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			itemStackGoldIngotOrCoil = WarpDriveConfig.getModItemStack("ic2", "crafting", 5);				// Coil
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			itemStackGoldIngotOrCoil = WarpDriveConfig.getModItemStack("ThermalExpansion", "material", 3);	// Redstone conductance coil
		} else if (WarpDriveConfig.isImmersiveEngineeringLoaded) {
			itemStackGoldIngotOrCoil = WarpDriveConfig.getModItemStack("ImmersiveEngineering", "coil", 2);	// HV wire coil
		} else if (WarpDriveConfig.isEnderIOLoaded) {
			itemStackGoldIngotOrCoil = WarpDriveConfig.getModItemStack("EnderIO", "itemPowerConduit", 2);	// Ender energy conduit
		}
		ItemStack oreEmeraldOrTitaniumPlate = new ItemStack(Items.GOLD_INGOT);
		if (OreDictionary.doesOreNameExist("plateTitanium") && !OreDictionary.getOres("plateTitanium").isEmpty()) {
			List<ItemStack> itemStackTitaniumPlates = OreDictionary.getOres("plateTitanium");
			oreEmeraldOrTitaniumPlate = itemStackTitaniumPlates.get(0);
		} else if (WarpDriveConfig.isAdvancedSolarPanelLoaded) {
			oreEmeraldOrTitaniumPlate = WarpDriveConfig.getModItemStack("AdvancedSolarPanel", "asp_crafting_items", 0);	// Sunnarium
		} else if (OreDictionary.doesOreNameExist("plateDenseSteel") && !OreDictionary.getOres("plateDenseSteel").isEmpty()) {// IndustrialCraft2
			List<ItemStack> itemStackPlateDenseSteel = OreDictionary.getOres("plateDenseSteel");
			oreEmeraldOrTitaniumPlate = itemStackPlateDenseSteel.get(0);
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			oreEmeraldOrTitaniumPlate = WarpDriveConfig.getModItemStack("ThermalExpansion", "Frame", 11);
		} else if (WarpDriveConfig.isImmersiveEngineeringLoaded) {
			oreEmeraldOrTitaniumPlate = WarpDriveConfig.getModItemStack("ImmersiveEngineering", "metalDecoration", 5);	// Heavy engineering block
		} else if (WarpDriveConfig.isEnderIOLoaded) {
			oreEmeraldOrTitaniumPlate = WarpDriveConfig.getModItemStack("EnderIO", "itemAlloy", 2);	// Vibrant alloy
		}
		Object oreEmeraldOrReinforcedIridiumPlate = "gemEmerald";
		if (OreDictionary.doesOreNameExist("plateAlloyIridium") && !OreDictionary.getOres("plateAlloyIridium").isEmpty()) {// IndustrialCraft2 and Gregtech
			oreEmeraldOrReinforcedIridiumPlate = "plateAlloyIridium";
		} else if (WarpDriveConfig.isEnderIOLoaded) {// EnderIO
			oreEmeraldOrReinforcedIridiumPlate = WarpDriveConfig.getModItemStack("EnderIO", "itemFrankenSkull", 2);
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			oreEmeraldOrReinforcedIridiumPlate = "ingotLumium";
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCloakingCoil), false, "iti", "cmc", "iti",
				't', oreEmeraldOrTitaniumPlate,
				'i', oreEmeraldOrReinforcedIridiumPlate,
				'c', itemStackGoldIngotOrCoil,
				'm', itemStackMachineCasings[3] )); 
		
		// Enantiomorphic reactor core is 1 EV Machine casing, 4 Capacitive crystal, 1 Computer interface, 1 Power interface, 2 Lenses
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockEnanReactorCore), false, "CpC", "lml", "CcC",
				'm', ItemComponent.getItemStack(EnumComponentType.REACTOR_CORE),
				'l', ItemComponent.getItemStack(EnumComponentType.LENS),
				'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE),
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'C', ItemComponent.getItemStack(EnumComponentType.CAPACITIVE_CRYSTAL))); 
		
		// Enantiomorphic reactor stabilization laser is 1 HV Machine casing, 2 Advanced hull, 1 Computer interface, 1 Power interface, 1 Lense, 1 Redstone, 2 glass pane
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockEnanReactorLaser), false, "ghr", "ldm", "ghc",
				'd', ItemComponent.getItemStack(EnumComponentType.DIFFRACTION_GRATING),
				'l', ItemComponent.getItemStack(EnumComponentType.LENS),
				'c', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
				'm', itemStackMachineCasings[1],
				'r', Items.REDSTONE,
				'g', "paneGlassColorless",
				'h', "blockHull2_plain"));
		
		// Anuic Energy bank is 1 capacitive crystal + 1 MV Machine casing + 3 Power interfaces 
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockEnergyBank), false, "pip", "pcp", "pmp",
		                                          'c', ItemComponent.getItemStack(EnumComponentType.CAPACITIVE_CRYSTAL),
		                                          'm', itemStackMachineCasings[1],
		                                          'i', ItemComponent.getItemStack(EnumComponentType.COMPUTER_INTERFACE),
		                                          'p', ItemComponent.getItemStack(EnumComponentType.POWER_INTERFACE)));
		
		// Force field projector is 1 or 2 Electromagnetic Projector + 1 LV/MV/HV Machine casing + 1 Ender crystal + 1 Redstone
		for (int tier = 1; tier <= 3; tier++) {
			int index = tier - 1;
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockForceFieldProjectors[index], 1, 0), false, " e ", "pm ", " r ",
			                                          'p', ItemComponent.getItemStack(EnumComponentType.ELECTROMAGNETIC_PROJECTOR),
			                                          'm', itemStackMachineCasings[index],
			                                          'e', ItemComponent.getItemStack(EnumComponentType.ENDER_CRYSTAL),
			                                          'r', Items.REDSTONE));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockForceFieldProjectors[index], 1, 0), false, " e ", " mp", " r ",
			                                          'p', ItemComponent.getItemStack(EnumComponentType.ELECTROMAGNETIC_PROJECTOR),
			                                          'm', itemStackMachineCasings[index],
			                                          'e', ItemComponent.getItemStack(EnumComponentType.ENDER_CRYSTAL),
			                                          'r', Items.REDSTONE));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockForceFieldProjectors[index], 1, 1), false, " e ", "pmp", " r ",
			                                          'p', ItemComponent.getItemStack(EnumComponentType.ELECTROMAGNETIC_PROJECTOR),
			                                          'm', itemStackMachineCasings[index],
			                                          'e', ItemComponent.getItemStack(EnumComponentType.ENDER_CRYSTAL),
			                                          'r', Items.REDSTONE));
		}
		
		// Force field relay is 2 Coil crystals + 1 LV/MV/HV Machine casing + 1 Ender crystal + 1 Redstone
		for (int tier = 1; tier <= 3; tier++) {
			int index = tier - 1;
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockForceFieldRelays[index]), false, " e ", "CmC", " r ",
			                                          'C', ItemComponent.getItemStack(EnumComponentType.COIL_CRYSTAL),
			                                          'm', itemStackMachineCasings[index],
			                                          'e', ItemComponent.getItemStack(EnumComponentType.ENDER_CRYSTAL),
			                                          'r', Items.REDSTONE));
		}
		
		// Decorative blocks
		OreDictionary.registerOre("warpDecorative", BlockDecorative.getItemStack(EnumDecorativeType.PLAIN));
		OreDictionary.registerOre("warpDecorative", BlockDecorative.getItemStack(EnumDecorativeType.ENERGIZED));
		OreDictionary.registerOre("warpDecorative", BlockDecorative.getItemStack(EnumDecorativeType.NETWORK));
		GameRegistry.addRecipe(new ShapedOreRecipe(BlockDecorative.getItemStackNoCache(EnumDecorativeType.PLAIN, 8), false, "sss", "scs", "sss",
				's', Blocks.STONE,
				'c', Items.PAPER));
		GameRegistry.addRecipe(new ShapedOreRecipe(BlockDecorative.getItemStackNoCache(EnumDecorativeType.PLAIN, 8), false, "sss", "scs", "sss",
				's', "warpDecorative",
				'c', "dyeWhite"));
		GameRegistry.addRecipe(new ShapedOreRecipe(BlockDecorative.getItemStackNoCache(EnumDecorativeType.ENERGIZED, 8), false, "sss", "scs", "sss",
				's', "warpDecorative",
				'c', "dyeRed"));
		GameRegistry.addRecipe(new ShapedOreRecipe(BlockDecorative.getItemStackNoCache(EnumDecorativeType.NETWORK, 8), false, "sss", "scs", "sss",
				's', "warpDecorative",
				'c', "dyeBlue"));
		
		// Lamp
		// @TODO: add 16 color variations
		GameRegistry.addRecipe(new ShapedOreRecipe(WarpDrive.blockLamp_bubble, false, " g ", "glg", "h  ",
				'g', "blockGlass",
                'l', Blocks.REDSTONE_LAMP,
				'h', "blockHull1_plain"));
		GameRegistry.addRecipe(new ShapedOreRecipe(WarpDrive.blockLamp_flat, false, " g ", "glg", " h ",
				'g', "blockGlass",
				'l', Blocks.REDSTONE_LAMP,
				'h', "blockHull1_plain"));
		GameRegistry.addRecipe(new ShapedOreRecipe(WarpDrive.blockLamp_long, false, " g ", "glg", "  h",
				'g', "blockGlass",
				'l', Blocks.REDSTONE_LAMP,
				'h', "blockHull1_plain"));
		
        // Plasma cutter
        GameRegistry.addRecipe(new ShapedOreRecipe(WarpDrive.blockLamp_bubble, false, "tcr", "mgb", "i  ",
                't', ItemComponent.getItemStack(EnumComponentType.GLASS_TANK),
                'c', ItemComponent.getItemStack(EnumComponentType.ACTIVATED_CARBON),
                'r', Items.BLAZE_ROD,
                'm', ItemComponent.getItemStack(EnumComponentType.MOTOR),
                'g', Items.GOLD_INGOT,
                'b', Blocks.STONE_BUTTON,
                'i', Items.IRON_INGOT));
        
        // Armor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.itemHelmet), false, "iii", "iwi", "gcg",
				'i', Items.IRON_INGOT,
				'w', Blocks.WOOL,
				'g', "blockGlass",
				'c', WarpDrive.itemAirCanisterFull));
		
		// Tuning rod ore dictionary
		for (int dyeColor = 0; dyeColor < 16; dyeColor++) {
			OreDictionary.registerOre("itemTuningRod", new ItemStack(WarpDrive.itemTuningRod, 1, dyeColor));
		}
		
		// Tuning rod variations
		for (int dyeColor = 0; dyeColor < 16; dyeColor++) {
			
			// crafting tuning rod
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.itemTuningRod, 1, dyeColor), false, "  q", "iX ", " i ",
					'q', Items.QUARTZ, // "gemQuartz", // Items.quartz,
					'i', Items.IRON_INGOT, // "ingotIron",
					'X', oreDyes[dyeColor] ));
			
			// changing colors
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(WarpDrive.itemTuningRod, 1, dyeColor),
					oreDyes[dyeColor],
					"itemTuningRod"));
		}
		
		// HULL blocks and variations
		initDynamicHull();
	}
	
	private static void initDynamicHull() {
		// Hull ore dictionary
		for(int tier = 1; tier <= 3; tier++) {
			int index = tier - 1;
			for (int woolColor = 0; woolColor < 16; woolColor++) {
				OreDictionary.registerOre("blockHull" + tier + "_plain", new ItemStack(WarpDrive.blockHulls_plain[index], 1, woolColor));
				OreDictionary.registerOre("blockHull" + tier + "_glass", new ItemStack(WarpDrive.blockHulls_glass[index], 1, woolColor));
				OreDictionary.registerOre("blockHull" + tier + "_stairs", new ItemStack(WarpDrive.blockHulls_stairs[index][woolColor], 1));
			}
		}
		
		// Hull blocks plain
		// (BlockColored.func_150031_c is converting wool metadata into dye metadata)
		// Tier 1 = 5 reinforced stone + 4 obsidian gives 10
		// Tier 1 = 5 stone + 4 steel ingots gives 10
		// Tier 1 = 5 stone + 4 iron ingots gives 10
		// Tier 1 = 5 stone + 4 bronze ingots gives 5
		// Tier 1 = 5 stone + 4 aluminium ingots gives 3
		if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			ItemStack reinforcedStone = WarpDriveConfig.getModItemStack("ic2", "resource", 11); // Reinforced stone
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[0], 10, 0), false, "cbc", "bcb", "cbc",
					'b', Blocks.OBSIDIAN,
					'c', reinforcedStone ));
		} else if (OreDictionary.doesOreNameExist("ingotSteel") && !OreDictionary.getOres("ingotSteel").isEmpty()) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[0], 10, 0), false, "cbc", "bcb", "cbc",
					'b', "ingotSteel",
					'c', "stone" ));
		} else {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[0], 10, 0), false, "cbc", "bcb", "cbc",
					'b', "ingotIron",
					'c', "stone" ));
		}
		if (OreDictionary.doesOreNameExist("ingotBronze") && !OreDictionary.getOres("ingotBronze").isEmpty()) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[0], 5, 0), false, "cbc", "bcb", "cbc",
					'b', "ingotBronze",
					'c', "stone" ));
		}
		if (OreDictionary.doesOreNameExist("ingotAluminium") && !OreDictionary.getOres("ingotAluminium").isEmpty()) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[0], 3, 0), false, "cbc", "bcb", "cbc",
					'b', "ingotAluminium",
					'c', "stone" ));
		} else if (OreDictionary.doesOreNameExist("ingotAluminum") && !OreDictionary.getOres("ingotAluminum").isEmpty()) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[0], 3, 0), false, "cbc", "bcb", "cbc",
					'b', "ingotAluminum",
					'c', "stone" ));
		}
		// Tier 2 = 4 Tier 1 + 1 Gregtech 5 Tungstensteel reinforced block gives 4
		// Tier 2 = 4 Tier 1 + 4 carbon plates gives 4
		// Tier 2 = 4 Tier 1 + 4 dark steel ingot gives 4
		// Tier 2 = 4 Tier 1 + 4 obsidian gives 4
		for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
			if (WarpDriveConfig.isGregTech5Loaded) {
				ItemStack tungstensteelReinforcedBlock = WarpDriveConfig.getModItemStack("gregtech", "gt.blockreinforced", 3);
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, enumDyeColor.getDyeDamage()), false, " b ", "bcb", " b ",
						'b', new ItemStack(WarpDrive.blockHulls_plain[0], 4, enumDyeColor.getDyeDamage()),
						'c', tungstensteelReinforcedBlock ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4,enumDyeColor.getDyeDamage()), false, "Xb ", "bcb", " b ",
						'b', "blockHull1_plain",
						'c', tungstensteelReinforcedBlock,
						'X', oreDyes[enumDyeColor.getMetadata()] ));    // TODO MC1.10 not tested
			} else if (WarpDriveConfig.isIndustrialCraft2Loaded) {
				ItemStack carbonPlate = WarpDriveConfig.getModItemStack("ic2", "crafting", 15); // Carbon plate
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, enumDyeColor.getDyeDamage()), false, "cbc", "b b", "cbc",
						'b', new ItemStack(WarpDrive.blockHulls_plain[0], 4, enumDyeColor.getDyeDamage()),
						'c', carbonPlate ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, enumDyeColor.getDyeDamage()), false, "cbc", "bXb", "cbc",
						'b', "blockHull1_plain",
						'c', carbonPlate,
						'X', oreDyes[enumDyeColor.getMetadata()] ));
			} else if (OreDictionary.doesOreNameExist("ingotDarkSteel") && !OreDictionary.getOres("ingotDarkSteel").isEmpty()) {// EnderIO
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, enumDyeColor.getDyeDamage()), false, "cbc", "b b", "cbc",
						'b', new ItemStack(WarpDrive.blockHulls_plain[0], 4, enumDyeColor.getDyeDamage()),
						'c', "ingotDarkSteel" ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, enumDyeColor.getDyeDamage()), false, "cbc", "bXb", "cbc",
						'b', "blockHull1_plain",
						'c', "ingotDarkSteel",
						'X', oreDyes[enumDyeColor.getMetadata()] ));
			} else {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, enumDyeColor.getDyeDamage()), false, "cbc", "b b", "cbc",
						'b', new ItemStack(WarpDrive.blockHulls_plain[0], 4, enumDyeColor.getDyeDamage()),
						'c', Blocks.OBSIDIAN ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, enumDyeColor.getDyeDamage()), false, "cbc", "bXb", "cbc",
						'b', "blockHull1_plain",
						'c', Blocks.OBSIDIAN,
						'X', oreDyes[enumDyeColor.getMetadata()] ));
			}
		}
		// Tier 3 = 4 Tier 2 + 1 naquadah plate gives 4
		// Tier 3 = 4 Tier 2 + 4 iridium plate gives 4
		// Tier 3 = 4 Tier 2 + 4 pulsating crystal gives 4
		// Tier 3 = 4 Tier 2 + 4 diamond gives 4
		for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
			if (OreDictionary.doesOreNameExist("plateNaquadah") && !OreDictionary.getOres("plateNaquadah").isEmpty()) {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, enumDyeColor.getDyeDamage()), false, " b ", "bcb", " b ",
						'b', new ItemStack(WarpDrive.blockHulls_plain[1], 4, enumDyeColor.getDyeDamage()),
						'c', "plateNaquadah" ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, enumDyeColor.getDyeDamage()), false, "Xb ", "bcb", " b ",
						'b', "blockHull2_plain",
						'c', "plateNaquadah",
						'X', oreDyes[enumDyeColor.getMetadata()] ));
			} else if (OreDictionary.doesOreNameExist("plateAlloyIridium") && !OreDictionary.getOres("plateAlloyIridium").isEmpty()) {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, enumDyeColor.getDyeDamage()), false, "cbc", "b b", "cbc",
						'b', new ItemStack(WarpDrive.blockHulls_plain[1], 4, enumDyeColor.getDyeDamage()),
						'c', "plateAlloyIridium" ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, enumDyeColor.getDyeDamage()), false, "cbc", "bXb", "cbc",
						'b', "blockHull2_plain",
						'c', "plateAlloyIridium",
						'X', oreDyes[enumDyeColor.getMetadata()] ));
			} else if (OreDictionary.doesOreNameExist("itemPulsatingCrystal") && !OreDictionary.getOres("itemPulsatingCrystal").isEmpty()) {// EnderIO
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, enumDyeColor.getDyeDamage()), false, "cbc", "b b", "cbc",
						'b', new ItemStack(WarpDrive.blockHulls_plain[1], 4, enumDyeColor.getDyeDamage()),
						'c', "itemPulsatingCrystal" ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, enumDyeColor.getDyeDamage()), false, "cbc", "bXb", "cbc",
						'b', "blockHull2_plain",
						'c', "itemPulsatingCrystal",
						'X', oreDyes[enumDyeColor.getMetadata()] ));
			} else {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, enumDyeColor.getDyeDamage()), false, "cbc", "b b", "cbc",
						'b', new ItemStack(WarpDrive.blockHulls_plain[1], 4, enumDyeColor.getDyeDamage()),
						'c', "gemDiamond" ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, enumDyeColor.getDyeDamage()), false, "cbc", "bXb", "cbc",
						'b', "blockHull2_plain",
						'c', "gemDiamond",
						'X', oreDyes[enumDyeColor.getMetadata()] ));
			}
		}
		
		// Hull blocks variation
		for(int tier = 1; tier <= 3; tier++) {
			int index = tier - 1;
			for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
				
				// crafting glass
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_glass[index], 4, enumDyeColor.getDyeDamage()), false, "gpg", "pFp", "gpg",
						'g', "blockGlass",
						'p', new ItemStack(WarpDrive.blockHulls_plain[index], 8, enumDyeColor.getDyeDamage()),
						'F', "dustGlowstone" ));
				
				// crafting stairs
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_stairs[index][enumDyeColor.getDyeDamage()], 4), false, "p  ", "pp ", "ppp",
				        'p', new ItemStack(WarpDrive.blockHulls_plain[index], 8, enumDyeColor.getDyeDamage()) ));
				
				// uncrafting
				GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[index], 6, enumDyeColor.getDyeDamage()),
				        WarpDrive.blockHulls_stairs[index][enumDyeColor.getDyeDamage()],
				        WarpDrive.blockHulls_stairs[index][enumDyeColor.getDyeDamage()],
				        WarpDrive.blockHulls_stairs[index][enumDyeColor.getDyeDamage()],
				        WarpDrive.blockHulls_stairs[index][enumDyeColor.getDyeDamage()] ));
				
				// changing colors
				GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[index], 1, enumDyeColor.getDyeDamage()),
						"dye" + enumDyeColor.getUnlocalizedName(),
						"blockHull" + tier + "_plain"));
				GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(WarpDrive.blockHulls_glass[index], 1, enumDyeColor.getDyeDamage()),
						"dye" + enumDyeColor.getUnlocalizedName(),
						"blockHull" + tier + "_glass"));
				GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(WarpDrive.blockHulls_stairs[index][enumDyeColor.getDyeDamage()], 1),
						"dye" + enumDyeColor.getUnlocalizedName(),
						"blockHull" + tier + "_stairs"));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[index], 8, enumDyeColor.getDyeDamage()), false, "###", "#X#", "###",
						'#', "blockHull" + tier + "_plain",
						'X', oreDyes[enumDyeColor.getMetadata()] ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_glass[index], 8, enumDyeColor.getDyeDamage()), false, "###", "#X#", "###",
						'#', "blockHull" + tier + "_glass",
						'X', oreDyes[enumDyeColor.getMetadata()] ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_stairs[index][enumDyeColor.getDyeDamage()], 8), false, "###", "#X#", "###",
						'#', "blockHull" + tier + "_stairs",
						'X', oreDyes[enumDyeColor.getMetadata()] ));
			}
		}
	}
}
