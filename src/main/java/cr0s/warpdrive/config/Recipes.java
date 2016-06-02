package cr0s.warpdrive.config;

import java.util.ArrayList;

import cpw.mods.fml.common.registry.GameRegistry;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.hull.BlockHullPlain;
import cr0s.warpdrive.block.passive.BlockDecorative;
import cr0s.warpdrive.data.ComponentType;
import cr0s.warpdrive.data.DecorativeType;
import cr0s.warpdrive.data.UpgradeType;
import cr0s.warpdrive.item.ItemComponent;
import cr0s.warpdrive.item.ItemUpgrade;
import net.minecraft.block.BlockColored;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Hold the different recipe sets
 *
 * @author LemADEC
 *
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
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL), false, "nrn", "r r", "nrn",
				'r', Items.redstone,
				'n', Items.gold_nugget));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.ENDER_CRYSTAL), false, "g", "e", "c",
				'g', Blocks.glass,
				'e', Items.ender_pearl,
				'c', ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL)));
			
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.DIAMOND_CRYSTAL), false, " g ", "ede", " c ",
				'g', Blocks.glass,
				'e', Items.ender_pearl,
				'd', Items.diamond,
				'c', ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL)));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.DIFFRACTION_GRATING), false, " g ", "rtr", " c ",
				'g', Blocks.glass,
				'r', "dyeBlue",
				't', Blocks.torch,
				'c', ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL)));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.REACTOR_CORE), false, " l ", "rcr", " l ",
				'l', "dyeWhite",
				'r', Items.coal,
				'c', ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL)));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE), false, "g  ", "gwr", "rwr",
				'g', Items.gold_nugget,
				'r', Items.redstone,
				'w', "plankWood"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.POWER_INTERFACE), false, "gig", "iri", "gig",
				'g', Items.gold_nugget,
				'r', Items.redstone,
				'i', Items.iron_ingot));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.CAPACITIVE_CRYSTAL), false, "glg", "ldl", "glg",
				'g', Items.gold_nugget,
				'l', "dyeBlue",
				'd', Items.diamond));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.AIR_CANISTER), false, "gcg", "g g", "gcg",
				'g', Blocks.glass,
				'c', ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL)));
		
		// Decorative blocks
		GameRegistry.addRecipe(new ShapedOreRecipe(BlockDecorative.getItemStackNoCache(DecorativeType.PLAIN, 8), false, "sss", "scs", "sss",
				's', Blocks.stone,
				'c', ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL)));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(BlockDecorative.getItemStackNoCache(DecorativeType.NETWORK, 8), false, "sss", "scs", "sss",
				's', BlockDecorative.getItemStack(DecorativeType.PLAIN),
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE)));
		
		// Upgrade items
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUpgrade.getItemStack(UpgradeType.Energy), false, "c", "e", "r",
				'c', ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL),
				'e', ItemComponent.getItemStack(ComponentType.CAPACITIVE_CRYSTAL),
				'r', Items.redstone));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUpgrade.getItemStack(UpgradeType.Power), false, "c", "e", "r",
				'c', ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL),
				'e', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE),
				'r', Items.redstone));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUpgrade.getItemStack(UpgradeType.Speed), false, "c", "e", "r",
				'c', ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL),
				'e', Items.sugar,
				'r', Items.redstone));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemUpgrade.getItemStack(UpgradeType.Range), false, "c", "e", "r",
				'c', ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL),
				'e', WarpDrive.blockTransportBeacon,
				'r', Items.redstone));
		
		// WarpCore
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipCore), false, "ipi", "ici", "idi",
				'i', Items.iron_ingot,
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE),
				'c', ItemComponent.getItemStack(ComponentType.DIAMOND_CRYSTAL),
				'd', Items.diamond));
		
		// Controller
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipController), false, "ici", "idi", "iii",
				'i', Items.iron_ingot,
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'd', Items.diamond));
		
		// Radar
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockRadar), false, "ggg", "pdc", "iii",
				'i', Items.iron_ingot,
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE),
				'g', Blocks.glass,
				'd', Items.diamond));
		
		// Isolation Block
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockWarpIsolation), false, "igi", "geg", "igi",
				'i', Items.iron_ingot,
				'g', Blocks.glass,
				'e', Items.ender_pearl));
		
		// Air generator
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockAirGenerator), false, "ibi", "i i", "ipi",
				'i', Items.iron_ingot,
				'b', Blocks.iron_bars,
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE)));
		
		// Laser
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaser), false, "ili", "iri", "ici",
				'i', Items.iron_ingot,
				'r', Items.redstone,
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'l', ItemComponent.getItemStack(ComponentType.DIFFRACTION_GRATING),
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE)));
		
		// Mining laser
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockMiningLaser), false, "ici", "iti", "ili",
				'i', Items.iron_ingot,
				'r', Items.redstone,
				't', ItemComponent.getItemStack(ComponentType.ENDER_CRYSTAL),
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'l', ItemComponent.getItemStack(ComponentType.DIFFRACTION_GRATING)));
		
		// Tree farm laser
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserTreeFarm), false, "ili", "sts", "ici",
				'i', Items.iron_ingot,
				's', "treeSapling",
				't', ItemComponent.getItemStack(ComponentType.ENDER_CRYSTAL), 
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'l', ItemComponent.getItemStack(ComponentType.DIFFRACTION_GRATING)));
		
		// Laser Lift
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLift), false, "ipi", "rtr", "ili",
				'i', Items.iron_ingot,
				'r', Items.redstone,
				't', ItemComponent.getItemStack(ComponentType.ENDER_CRYSTAL),
				'l', ItemComponent.getItemStack(ComponentType.DIFFRACTION_GRATING),
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE)));
		
		// Transporter
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockTransporter), false, "iii", "ptc", "iii",
				'i', Items.iron_ingot,
				't', ItemComponent.getItemStack(ComponentType.ENDER_CRYSTAL),
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE)));
		
		// Laser medium
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserMedium), false, "ipi", "rgr", "iii", 
				'i', Items.iron_ingot,
				'r', Items.redstone,
				'g', Blocks.glass,
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE)));
		
		// Camera
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCamera), false, "ngn", "i i", "ici",
				'i', Items.iron_ingot,
				'n', Items.gold_nugget,
				'g', Blocks.glass,
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE)));
		
		// LaserCamera
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(WarpDrive.blockLaserCamera), WarpDrive.blockCamera, WarpDrive.blockLaser));
		
		// Monitor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockMonitor), false, "ggg", "iti", "ici",
				'i', Items.iron_ingot,
				't', Blocks.torch,
				'g', Blocks.glass,
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE)));
		
		// Cloaking device
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCloakingCore), false, "ipi", "lrl", "ici",
				'i', Items.iron_ingot,
				'r', Items.redstone,
				'l', ItemComponent.getItemStack(ComponentType.DIFFRACTION_GRATING),
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE)));
		
		// Cloaking coil
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCloakingCoil), false, "ini", "rdr", "ini",
				'i', Items.iron_ingot,
				'd', Items.diamond,
				'r', Items.redstone,
				'n', Items.gold_nugget));
		
		// Power Laser
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockEnanReactorLaser), false, "iii", "ilg", "ici",
				'i', Items.iron_ingot,
				'g', Blocks.glass,
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'l', ItemComponent.getItemStack(ComponentType.DIFFRACTION_GRATING)));
		
		// Power Reactor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockEnanReactorCore), false, "ipi", "gog", "ici",
				'i', Items.iron_ingot,
				'g', Blocks.glass,
				'o', ItemComponent.getItemStack(ComponentType.REACTOR_CORE),
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE)));
		
		// Power Store
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockEnergyBank), false, "ipi", "isi", "ici",
				'i', Items.iron_ingot,
				's', ItemComponent.getItemStack(ComponentType.CAPACITIVE_CRYSTAL),
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE)));
		
		// Transport Beacon
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockTransportBeacon), false, " e ", "ldl", " s ",
				'e', Items.ender_pearl,
				'l', "dyeBlue",
				'd', Items.diamond,
				's', Items.stick));
		
		// Chunk Loader
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockChunkLoader), false, "ipi", "ici", "ifi",
				'i', Items.iron_ingot,
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE),
				'c', ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL),
				'f', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE)));
		
		// Helmet
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.itemHelmet), false, "iii", "iwi", "gcg",
				'i', Items.iron_ingot,
				'w', Blocks.wool,
				'g', Blocks.glass,
				'c', ItemComponent.getItemStack(ComponentType.AIR_CANISTER)));
	}
	
	public static void initIC2() {
		ItemStack advancedAlloy = WarpDriveConfig.getModItemStack("IC2", "itemPartAlloy", -1);
		ItemStack iridiumAlloy = WarpDriveConfig.getModItemStack("IC2", "itemPartIridium", -1);
		ItemStack advancedMachine = WarpDriveConfig.getModItemStack("IC2", "blockMachine", 12);
		ItemStack miner = WarpDriveConfig.getModItemStack("IC2", "blockMachine", 7);
		ItemStack magnetizer = WarpDriveConfig.getModItemStack("IC2", "blockMachine", 9);
		ItemStack fiberGlassCable = WarpDriveConfig.getModItemStack("IC2", "itemCable", 9);
		ItemStack circuit = WarpDriveConfig.getModItemStack("IC2", "itemPartCircuit", -1);
		ItemStack advancedCircuit = WarpDriveConfig.getModItemStack("IC2", "itemPartCircuitAdv", -1);
		ItemStack ironPlate = WarpDriveConfig.getModItemStack("IC2", "itemPlates", 4);
		ItemStack mfe = WarpDriveConfig.getModItemStack("IC2", "blockElectric", 1);
		
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
				'f', WarpDriveConfig.getModItemStack("IC2", "itemFreq", -1));
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockWarpIsolation), "iii", "idi", "iii",
				'i', WarpDriveConfig.getModItemStack("IC2", "itemPartIridium", -1),
				'm', advancedMachine,
				'd', Blocks.diamond_block);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockAirGenerator), "lcl", "lml", "lll",
				'l', Blocks.leaves,
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
				'a', WarpDriveConfig.getModItemStack("IC2", "itemPartAlloy", -1),
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
				'g', Blocks.glass);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockMonitor), "gcg", "gmg", "ggg",
				'm', advancedMachine,
				'c', advancedCircuit,
				'g', Blocks.glass);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockShipScanner), "sgs", "mma", "amm",
				'm', advancedMachine,
				'a', advancedAlloy,
				's', advancedCircuit,
				'g', Blocks.glass);
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserTreeFarm), false, new Object[] { "cwc", "wmw", "cwc",
				'c', circuit,
				'w', "logWood",
				'm', WarpDrive.blockMiningLaser }));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockTransporter), false, new Object[] { "ece", "imi", "iei",
				'e', Items.ender_pearl,
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
		ItemStack advancedAlloy = WarpDriveConfig.getModItemStack("IC2", "itemPartAlloy", -1);
		ItemStack iridiumAlloy = WarpDriveConfig.getModItemStack("IC2", "itemPartIridium", -1);
		ItemStack advancedMachine = WarpDriveConfig.getModItemStack("IC2", "blockMachine", 12);
		ItemStack magnetizer = WarpDriveConfig.getModItemStack("IC2", "blockMachine", 9);
		ItemStack fiberGlassCable = WarpDriveConfig.getModItemStack("IC2", "itemCable", 9);
		ItemStack mfe = WarpDriveConfig.getModItemStack("IC2", "blockElectric", 1);
		ItemStack mfsu = WarpDriveConfig.getModItemStack("IC2", "blockElectric", 2);
		ItemStack energiumDust = WarpDriveConfig.getModItemStack("IC2", "itemDust2", 2);
		ItemStack crystalmemory = WarpDriveConfig.getModItemStack("IC2", "itemcrystalmemory", -1);
		ItemStack itemHAMachine = new ItemStack(WarpDrive.blockHighlyAdvancedMachine);
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockShipCore),"uau", "tmt", "uau",
				'a', advancedAlloy,
				't', WarpDriveConfig.getModItemStack("IC2", "blockMachine2", 0), // Teleporter
				'm', itemHAMachine,
				'u', mfsu);
		
		if (WarpDriveConfig.isOpenComputersLoaded) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipController), false, new Object[] { "aha", "cmc", "apa", // With OC Adapter
					'a', advancedAlloy,
					'm', itemHAMachine,
					'c', "circuitAdvanced",
					'h', crystalmemory,
					'p', WarpDriveConfig.getModItemStack("OpenComputers", "adapter", -1)}));
		} else if (WarpDriveConfig.isComputerCraftLoaded) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipController), false, new Object[] { "aha", "cmc", "apa", // With CC Modem
					'a', advancedAlloy,
					'm', itemHAMachine,
					'c', "circuitAdvanced",
					'h', crystalmemory,
					'p', WarpDriveConfig.getModItemStack("ComputerCraft", "CC-Cable", 1)}));
		} else {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipController), false, new Object[] { "aha", "cmc", "aca",
				'a', advancedAlloy,
				'm', itemHAMachine,
				'c', "circuitAdvanced",
				'h', crystalmemory}));
		}
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockRadar), false, new Object[] { "afa", "cmc", "aca",
				'a', advancedAlloy,
				'm', itemHAMachine,
				'c', "circuitAdvanced",
				'f', WarpDriveConfig.getModItemStack("IC2", "itemFreq", -1)}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockWarpIsolation), false, new Object[] { "sls", "lml", "sls",
				's', "plateDenseSteel",
				'l', "plateDenseLead",
				'm', itemHAMachine}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockAirGenerator), false, new Object[] { "lel", "vmv", "lcl",
				'l', Blocks.leaves, 
				'm', WarpDriveConfig.getModItemStack("IC2", "blockMachine", 0),
				'c', "circuitBasic",
				'e', WarpDriveConfig.getModItemStack("IC2", "blockMachine", 5), // Compressor
				'v', WarpDriveConfig.getModItemStack("IC2", "reactorVent", -1)}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaser), false, new Object[] { "aca", "cmc", "ala",
				'm', advancedMachine,
				'a', advancedAlloy,
				'c', "circuitAdvanced",
				'l', WarpDriveConfig.getModItemStack("IC2", "itemToolMiningLaser", -1)}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockMiningLaser), false, new Object[] { "pcp", "pap", "plp",
				'c', "circuitAdvanced",
				'p', advancedAlloy,
				'a', WarpDriveConfig.getModItemStack("IC2", "blockMachine2", 11), // Advanced Miner
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
				'e', WarpDriveConfig.getModItemStack("IC2", "itemRecipePart", 1), // Electric Motor
				'd', "gemDiamond",
				'c', crystalmemory,
				'm', advancedMachine,
				'g', WarpDriveConfig.getModItemStack("IC2", "itemCable", 2)}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockMonitor), false, new Object[] { "ala", "aca", "aga",
				'a', advancedAlloy,
				'l', Blocks.redstone_lamp,
				'c', "circuitAdvanced",
				'g', "paneGlassColorless" }));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipScanner), false, new Object[] { "ici", "isi", "mcm",
				'm', mfsu,
				'i', iridiumAlloy,
				'c', "circuitAdvanced",
				's', WarpDriveConfig.getModItemStack("IC2", "blockMachine2", 7) })); // Scanner
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserTreeFarm), false, new Object[] { "awa", "cmc", "asa",
				'a', advancedAlloy,
				'c', "circuitAdvanced",
				'w', "logWood",
				'm', WarpDrive.blockMiningLaser,
				's', WarpDriveConfig.getModItemStack("IC2", "itemToolChainsaw", -1) }));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockTransporter), false, new Object[] { "aea", "ctc", "ama",
				'a', advancedAlloy,
				'e', Items.ender_pearl,
				'c', "circuitAdvanced",
				'm', advancedMachine,
				't', WarpDriveConfig.getModItemStack("IC2", "blockMachine2", 0) })); // Teleporter
		
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
				'c', WarpDriveConfig.getModItemStack("IC2", "itemRecipePart", 0), // Coil
				'a', advancedAlloy })); 
		
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockHighlyAdvancedMachine), "iii", "imi", "iii",
				'i', iridiumAlloy,
				'm', advancedMachine);
	}
	
	@SuppressWarnings("UnusedAssignment")
	public static void initDynamic() {
		// Support iron bars from all mods
		Object ironBars = Blocks.iron_bars;
		if (OreDictionary.doesOreNameExist("barsIron") && !OreDictionary.getOres("barsIron").isEmpty()) {
			ironBars = "barsIron";
		}
		
		// Add Reinforced iridium plate to ore registry as applicable (it's missing in IC2 without GregTech)
		if (WarpDriveConfig.isIndustrialCraft2Loaded && (!OreDictionary.doesOreNameExist("plateAlloyIridium") || OreDictionary.getOres("plateAlloyIridium").isEmpty())) {
			ItemStack iridiumAlloy = WarpDriveConfig.getModItemStack("IC2", "itemPartIridium", -1);
			OreDictionary.registerOre("plateAlloyIridium", iridiumAlloy);
		}
		
		// Get the machine casing to use
		ItemStack itemMachineCasingLV;
		ItemStack itemMachineCasingMV;
		ItemStack itemMachineCasingHV;
		ItemStack itemMachineCasingEV;
		ItemStack itemMotorLV = ItemComponent.getItemStack(ComponentType.MOTOR);
		ItemStack itemMotorMV = ItemComponent.getItemStack(ComponentType.MOTOR);
		ItemStack itemMotorHV = ItemComponent.getItemStack(ComponentType.MOTOR);
		@SuppressWarnings("unused")
		ItemStack itemMotorEV = ItemComponent.getItemStack(ComponentType.MOTOR);
		
		if (WarpDriveConfig.isGregTech5Loaded) {
			itemMachineCasingLV = WarpDriveConfig.getModItemStack("gregtech", "gt.blockcasings", 1);
			itemMachineCasingMV = WarpDriveConfig.getModItemStack("gregtech", "gt.blockcasings", 2);
			itemMachineCasingHV = WarpDriveConfig.getModItemStack("gregtech", "gt.blockcasings", 3);
			itemMachineCasingEV = WarpDriveConfig.getModItemStack("gregtech", "gt.blockcasings", 4);
			
			itemMotorLV = WarpDriveConfig.getModItemStack("gregtech", "gt.metaitem.01", 32600);	// LV Motor
			itemMotorMV = WarpDriveConfig.getModItemStack("gregtech", "gt.metaitem.01", 32601);	// MV Motor
			itemMotorHV = WarpDriveConfig.getModItemStack("gregtech", "gt.metaitem.01", 32602);	// HV Motor
			itemMotorEV = WarpDriveConfig.getModItemStack("gregtech", "gt.metaitem.01", 32603);	// EV Motor
			
		} else if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			itemMachineCasingLV = WarpDriveConfig.getModItemStack("IC2", "blockMachine", 0);	// Basic machine casing
			itemMachineCasingMV = WarpDriveConfig.getModItemStack("IC2", "blockMachine", 12);	// Advanced machine casing
			itemMachineCasingHV = new ItemStack(WarpDrive.blockHighlyAdvancedMachine);
			itemMachineCasingEV = new ItemStack(WarpDrive.blockHighlyAdvancedMachine);
			
			itemMotorHV = WarpDriveConfig.getModItemStack("IC2", "itemRecipePart", 1);			// Electric motor
			itemMotorEV = WarpDriveConfig.getModItemStack("IC2", "itemRecipePart", 1);
			
			ItemStack iridiumAlloy = WarpDriveConfig.getModItemStack("IC2", "itemPartIridium", -1);
			GameRegistry.addRecipe(new ItemStack(WarpDrive.blockHighlyAdvancedMachine), "iii", "imi", "iii",
					'i', iridiumAlloy,
					'm', itemMachineCasingMV);
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			itemMachineCasingLV = WarpDriveConfig.getModItemStack("ThermalExpansion", "Frame", 0);
			itemMachineCasingMV = WarpDriveConfig.getModItemStack("ThermalExpansion", "Frame", 1);
			itemMachineCasingHV = WarpDriveConfig.getModItemStack("ThermalExpansion", "Frame", 2);
			itemMachineCasingEV = WarpDriveConfig.getModItemStack("ThermalExpansion", "Frame", 3);
		} else if (WarpDriveConfig.isEnderIOloaded) {
			itemMachineCasingLV = WarpDriveConfig.getModItemStack("EnderIO", "itemMachinePart", 0);		// Machine chassis
			itemMachineCasingMV = WarpDriveConfig.getModItemStack("EnderIO", "blockVacuumChest", 0);	// Pulsating crystal => Vacuum chest
			itemMachineCasingHV = WarpDriveConfig.getModItemStack("EnderIO", "blockIngotStorage", 2);	// Vibrant alloy block
			itemMachineCasingEV = WarpDriveConfig.getModItemStack("EnderIO", "itemMaterial", 8);		// Ender crystal
		} else {// vanilla
			itemMachineCasingLV = new ItemStack(Blocks.iron_block);
			itemMachineCasingMV = new ItemStack(Blocks.diamond_block);
			itemMachineCasingHV = new ItemStack(WarpDrive.blockHighlyAdvancedMachine);
			itemMachineCasingEV = new ItemStack(Blocks.beacon);
			
			GameRegistry.addRecipe(new ItemStack(WarpDrive.blockHighlyAdvancedMachine, 4), "pep", "ede", "pep",
					'e', Items.emerald,
					'p', Items.ender_eye,
					'd', Blocks.diamond_block);
		}
		
		// Base tuning crystals
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL), false, " e ", "BBB", "qrq",
				'e', Items.emerald,
				'B', ironBars,
				'r', Items.redstone,
				'q', Items.quartz));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.ENDER_CRYSTAL), false, " e ", "BBB", "grg",
				'e', Items.ender_pearl,
				'B', ironBars,
				'r', Items.redstone,
				'g', "nuggetGold"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.DIAMOND_CRYSTAL), false, " d ", "BBB", "prp",
				'd', Items.diamond,
				'B', ironBars,
				'r', Items.redstone,
				'p', Items.paper));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.DIFFRACTION_GRATING), false, " t ", "iii", "ggg",
				't', Items.ghast_tear,
				'i', ironBars,
				'g', Blocks.glowstone));
		
		// Intermediary component for reactor core
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.REACTOR_CORE), false, "shs", "hmh", "shs",
				's', Items.nether_star,
				'h', "blockHull3_plain",
				'm', itemMachineCasingHV));
		
		
		// Computer interface is 2 gold ingot, 2 wired modems (or redstone), 1 lead/tin ingot
		{
			Object redstoneOrModem = Items.redstone;
			if (WarpDriveConfig.isComputerCraftLoaded) {
				redstoneOrModem = WarpDriveConfig.getModItemStack("ComputerCraft", "CC-Cable", 1); // Wired modem
			}
			// if (OreDictionary.doesOreNameExist("circuitPrimitive") && !OreDictionary.getOres("circuitPrimitive").isEmpty()) { // Gregtech
			Object oreCircuitOrHeavyPressurePlate = Blocks.heavy_weighted_pressure_plate;
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
				GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(ComponentType.COMPUTER_INTERFACE, 2 * outputFactor), false, "   ", "rar", "gGg",
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
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(ComponentType.COMPUTER_INTERFACE, outputFactor), false, "   ", "rar", "gGg",
					'G', oreCircuitOrHeavyPressurePlate,
					'g', "ingotGold",
					'r', redstoneOrModem,
					'a', oreSlimeOrTinOrLead));
		}
		
		// Power interface is 4 redstone, 2 iron ingot, 3 gold ingot
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(ComponentType.POWER_INTERFACE, 2), false, "rgr", "igi", "rgr",
				'g', Items.gold_ingot,
				'r', Items.redstone,
				'i', Items.iron_ingot));
		
		// Capacitive crystal is 3 redstone block, 3 paper, 3 lapis block or 1 HV capacitor from IE or 1 MFE from IC2
		if (OreDictionary.doesOreNameExist("dustLithium") && !OreDictionary.getOres("dustLithium").isEmpty()) {// comes with GregTech and Industrial Craft 2
			// (Lithium is processed from nether quartz)
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(ComponentType.CAPACITIVE_CRYSTAL, 2), false, "plp", "lRl", "plp",
					'R', new ItemStack(Items.potionitem, 1, 8225),	// Regeneration II
					'l', "dustLithium",
					'p', Items.paper));
		} else if (WarpDriveConfig.isImmersiveEngineeringLoaded) {
			ItemStack itemStackCapacitorHV = WarpDriveConfig.getModItemStack("ImmersiveEngineering", "metalDevice", 7);
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(ComponentType.CAPACITIVE_CRYSTAL, 2), false, " m ", "ppp", " m ",
					'm', itemStackCapacitorHV,
					'p', Items.paper));
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			ItemStack itemStackHardenedEnergyCell = WarpDriveConfig.getModItemStack("ThermalExpansion", "Cell", 2);
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(ComponentType.CAPACITIVE_CRYSTAL, 2), false, " m ", "ppp", " m ",
					'm', itemStackHardenedEnergyCell,
					'p', Items.paper));
		} else if (WarpDriveConfig.isEnderIOloaded) {
			ItemStack itemStackBasicCapacitorBank = WarpDriveConfig.getModItemStack("EnderIO", "blockCapBank", 1);
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(ComponentType.CAPACITIVE_CRYSTAL, 2), false, " m ", "ppp", " m ",
					'm', itemStackBasicCapacitorBank,
					'p', Items.paper));
		} else {// Vanilla
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(ComponentType.CAPACITIVE_CRYSTAL, 2), false, "qrq", "pSp", "qrq",
					'S', new ItemStack(Items.potionitem, 1, 8265),	// Strength I long (blaze powder + redstone)
					'r', Blocks.redstone_block,
					'p', Items.paper,
					'q', Items.quartz));
		}
		
		// Air canister is 4 iron bars, 2 leather/rubber, 2 yellow wool, 1 tank
		Object rubberOrLeather = Items.leather;
		if (OreDictionary.doesOreNameExist("plateRubber") && !OreDictionary.getOres("plateRubber").isEmpty()) {// comes with GregTech
			rubberOrLeather = "plateRubber";
		} else if (OreDictionary.doesOreNameExist("itemRubber") && !OreDictionary.getOres("itemRubber").isEmpty()) {// comes with IndustrialCraft2
			rubberOrLeather = "itemRubber";
		}
		Object woolYellow = new ItemStack(Blocks.wool, 1, 4);
		if (OreDictionary.doesOreNameExist("blockWoolYellow") && !OreDictionary.getOres("blockWoolYellow").isEmpty()) {
			woolYellow = "blockWoolYellow";
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(ComponentType.AIR_CANISTER, 4), false, "iyi", "rgr", "iyi",
				'r', rubberOrLeather,
				'g', ItemComponent.getItemStack(ComponentType.GLASS_TANK),
				'y', woolYellow,
				'i', ironBars));
		
		// Lens is 1 diamond, 2 gold ingots, 2 glass panels
		if (OreDictionary.doesOreNameExist("lensDiamond") && !OreDictionary.getOres("lensDiamond").isEmpty()) {
			if (OreDictionary.doesOreNameExist("craftingLensWhite") && !OreDictionary.getOres("craftingLensWhite").isEmpty()) {
				GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(ComponentType.LENS, 3), false, "ggg", "pdp", "ggg",
						'g', "nuggetGold",
						'p', "craftingLensWhite",
						'd', "lensDiamond"));
			} else {
				GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.LENS), false, " g ", "pdp", " g ",
						'g', Items.gold_ingot,
						'p', "paneGlassColorless",
						'd', "lensDiamond"));
			}
		} else if (WarpDriveConfig.isAdvancedRepulsionSystemLoaded) {
			ItemStack diamondLens = WarpDriveConfig.getModItemStack("AdvancedRepulsionSystems", "{A8F3AF2F-0384-4EAA-9486-8F7E7A1B96E7}", 1);
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.LENS), false, " g ", "pdp", " g ",
					'g', Items.gold_ingot,
					'p', "paneGlassColorless",
					'd', diamondLens));
		} else {
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(ComponentType.LENS, 2), false, " g ", "pdp", " g ",
					'g', Items.gold_ingot,
					'p', "paneGlassColorless",
					'd', "gemDiamond"));
		}
		
		// Zoom is 3 lenses, 2 iron ingot, 2 dyes, 2 redstone
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.ZOOM), false, "dir", "lll", "dit",
				'r', Items.redstone,
				'i', Items.iron_ingot,
				'l', ItemComponent.getItemStack(ComponentType.LENS),
				't', itemMotorLV,
				'd', "dye"));
		
		// Glass tank is 4 slime balls, 4 glass
		// slimeball && blockGlass are defined by forge itself
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(ComponentType.GLASS_TANK, 4), false, "sgs", "g g", "sgs",
				's', "slimeball",
				'g', "blockGlass"));
		
		// Flat screen is 3 dyes, 1 glowstone dust, 2 paper, 3 glass panes
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.FLAT_SCREEN), false, "gRp", "gGd", "gBp",
				'R', "dyeRed",
				'G', "dyeLime",
				'B', "dyeBlue",
				'd', "dustGlowstone",
				'g', "paneGlassColorless",
				'p', Items.paper));
		
		// Memory bank is 2 papers, 2 iron bars, 4 comparators, 1 redstone
		if (OreDictionary.doesOreNameExist("circuitPrimitive") && !OreDictionary.getOres("circuitPrimitive").isEmpty()) { // Gregtech
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.MEMORY_CRYSTAL), false, "cic", "cic", "prp",
					'i', ironBars,
					'c', "circuitPrimitive",
					'r', Items.redstone,
					'p', Items.paper));
		} else if (OreDictionary.doesOreNameExist("oc:ram3") && !OreDictionary.getOres("oc:ram3").isEmpty()) {
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStackNoCache(ComponentType.MEMORY_CRYSTAL, 4), false, "cic", "cic", "prp",
					'i', ironBars,
					'c', "oc:ram3",
					'r', Items.redstone,
					'p', Items.paper));
		} else {
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.MEMORY_CRYSTAL), false, "cic", "cic", "prp",
					'i', ironBars,
					'c', Items.comparator,
					'r', Items.redstone,
					'p', Items.paper));
		}
		
		// Motor is 2 gold nuggets (wires), 3 iron ingots (steel rods), 4 iron bars (coils)
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.MOTOR), false, "bbn", "iii", "bbn",
				'b', ironBars,
				'i', Items.iron_ingot,
				'n', "nuggetGold"));
		
		// Bone charcoal is smelting 1 bone
		GameRegistry.addSmelting(Items.bone, ItemComponent.getItemStackNoCache(ComponentType.BONE_CHARCOAL, 1), 1);
		
		// Activated carbon is 1 bone charcoal, 4 sticks, 4 leaves
		Object leaves = Blocks.leaves;
		if (OreDictionary.doesOreNameExist("treeLeaves") && !OreDictionary.getOres("treeLeaves").isEmpty()) {
			leaves = "treeLeaves";
		}
		if (OreDictionary.doesOreNameExist("dustSulfur") && !OreDictionary.getOres("dustSulfur").isEmpty()) {
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.ACTIVATED_CARBON), false, "lll", "aaa", "fwf",
					'l', leaves,
					'a', ItemComponent.getItemStack(ComponentType.BONE_CHARCOAL),
					'w', new ItemStack(Items.potionitem, 1, 0),
					'f', "dustSulfur"));
		} else {
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.ACTIVATED_CARBON), false, "lll", "aaa", "wgw",
					'l', leaves,
					'a', ItemComponent.getItemStack(ComponentType.BONE_CHARCOAL),
					'w', new ItemStack(Items.potionitem, 1, 0),
					'g', Items.gunpowder));
		}
		
		// Laser medium (empty) is 3 glass tanks, 1 power interface, 1 computer interface, 1 MV Machine casing
		GameRegistry.addRecipe(new ShapedOreRecipe(ItemComponent.getItemStack(ComponentType.LASER_MEDIUM_EMPTY), false, "   ", "ggg", "pmc",
				'g', ItemComponent.getItemStack(ComponentType.GLASS_TANK),
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE),
				'm', itemMachineCasingMV,
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE)));
		
		// *** Blocks
		// Ship core is 1x Ghast tear, 4x Capacitive crystal, 2x Tuning ender, 1x Power interface, 1x MV Machine casing
		GameRegistry.addRecipe(new ItemStack(WarpDrive.blockShipCore),"csc", "eme", "cpc",
				's', Items.ghast_tear,
				'c', ItemComponent.getItemStack(ComponentType.CAPACITIVE_CRYSTAL),
				'e', ItemComponent.getItemStack(ComponentType.ENDER_CRYSTAL),
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE),
				'm', itemMachineCasingMV);
		
		// Ship controller is 1x Computer interface, 1x Tuning emerald, 1x LV Machine casing, 2x Memory bank
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipController), false, " e ", "bmb", " c ",
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'e', ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL),
				'm', itemMachineCasingLV,
				'b', ItemComponent.getItemStack(ComponentType.MEMORY_CRYSTAL)));
		
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
		Object oreAntenna = Items.ghast_tear;
		if (OreDictionary.doesOreNameExist("stickQuartzite")) {// GregTech
			oreAntenna = "stickQuartzite";
		} else if (OreDictionary.doesOreNameExist("ingotSignalum") && !OreDictionary.getOres("ingotSignalum").isEmpty()) {// ThermalExpansion
			oreAntenna = "ingotSignalum";
		} else if (OreDictionary.doesOreNameExist("nuggetPulsatingIron") && !OreDictionary.getOres("nuggetPulsatingIron").isEmpty()) {// EnderIO
			oreAntenna = "nuggetPulsatingIron";
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockRadar), false, "PAP", "PtP", "pmc",
			't', itemMotorHV,
			'P', oreCloakingPlate,
			'A', oreAntenna,
			'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
			'm', itemMachineCasingHV,
			'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE)));
		
		// Warp isolation is 1 EV Machine casing (Ti), 4 Titanium plate/Enderium ingot/Vibrant alloy/Iridium plate/quartz
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockWarpIsolation), false, "i i", " m ", "i i",
				'i', oreCloakingPlate,
				'm', itemMachineCasingEV));
		
		// Air generator is 1 power interface, 4 activated carbon, 1 motor, 1 MV Machine casing, 2 tanks
		ItemStack itemStackCompressorOrTank = ItemComponent.getItemStack(ComponentType.GLASS_TANK);
		if (WarpDriveConfig.isGregTech5Loaded) {
			itemStackCompressorOrTank = WarpDriveConfig.getModItemStack("gregtech", "gt.metaitem.02", 21300); // Bronze rotor
		} else if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			itemStackCompressorOrTank = WarpDriveConfig.getModItemStack("IC2", "blockMachine", 5); // Compressor
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			itemStackCompressorOrTank = WarpDriveConfig.getModItemStack("ThermalExpansion", "Machine", 5); // Fluid transposer
		} else if (WarpDriveConfig.isEnderIOloaded) {
			itemStackCompressorOrTank = WarpDriveConfig.getModItemStack("EnderIO", "blockReservoir", 0);
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockAirGenerator), false, "aca", "ata", "gmp",
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE),
				'a', ItemComponent.getItemStack(ComponentType.ACTIVATED_CARBON),
				't', itemMotorMV,
				'g', ItemComponent.getItemStack(ComponentType.GLASS_TANK),
				'm', itemMachineCasingMV,
				'c', itemStackCompressorOrTank));
		
		// Laser cannon is 2 motors, 1 diffraction grating, 1 lens, 1 computer interface, 1 HV Machine casing, 1 redstone dust, 2 glass pane
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaser), false, "gtr", "ldm", "gtc",
				't', itemMotorHV,
				'd', ItemComponent.getItemStack(ComponentType.DIFFRACTION_GRATING),
				'l', ItemComponent.getItemStack(ComponentType.LENS),
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'm', itemMachineCasingHV,
				'r', Items.redstone,
				'g', "paneGlassColorless"));
		
		// Mining laser is 2 motors, 1 diffraction grating, 1 lens, 1 computer interface, 1 MV Machine casing, 1 diamond pick, 2 glass pane
		ItemStack itemStackDiamondPick = new ItemStack(Items.diamond_pickaxe);
		if (WarpDriveConfig.isGregTech5Loaded) {
			itemStackDiamondPick = WarpDriveConfig.getModItemStack("IC2", "itemToolMiningLaser", 1); // Mining laser
		} else if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			itemStackDiamondPick = WarpDriveConfig.getModItemStack("IC2", "blockMachine2", 11); // Advanced Miner
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockMiningLaser), false, "cmr", "tdt", "glg",
				't', itemMotorMV,
				'd', ItemComponent.getItemStack(ComponentType.DIFFRACTION_GRATING),
				'l', ItemComponent.getItemStack(ComponentType.LENS),
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'm', itemMachineCasingMV,
				'r', itemStackDiamondPick,
				'g', "paneGlassColorless"));
		
		// Laser medium (full) is 1 laser medium (empty), 4 redstone blocks, 4 lapis blocks
		// TODO: add fluid transposer/canning support
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserMedium), false, "lrl", "rmr", "lrl",
				'm', ItemComponent.getItemStack(ComponentType.LASER_MEDIUM_EMPTY),
				'r', Blocks.redstone_block,
				'l', Blocks.lapis_block));
		
		// Laser lift is ...
		Object oreMagnetizer = itemMachineCasingLV;
		if (WarpDriveConfig.isGregTech5Loaded) {
			oreMagnetizer = WarpDriveConfig.getModItemStack("gregtech", "gt.blockmachines", 551);	// Basic polarizer
		} else if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			oreMagnetizer = WarpDriveConfig.getModItemStack("IC2", "blockMachine", 9);
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			oreMagnetizer = WarpDriveConfig.getModItemStack("ThermalExpansion", "Plate", 3);
		} else if (OreDictionary.doesOreNameExist("ingotRedstoneAlloy") && !OreDictionary.getOres("ingotRedstoneAlloy").isEmpty()) {// EnderIO
			oreMagnetizer = "ingotRedstoneAlloy";
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLift), false, "rmw", "plc", "glg",
				'r', Items.redstone,
				'w', Blocks.wool,
				'l', ItemComponent.getItemStack(ComponentType.LENS),
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'm', oreMagnetizer,
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE),
				'g', "paneGlassColorless"));
		
		// Iridium block is just that
		if (WarpDriveConfig.isGregTech5Loaded) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockIridium), "iii", "iii", "iii",
					'i', "plateAlloyIridium"));
			ItemStack itemStackIridiumAlloy = WarpDriveConfig.getModItemStack("IC2", "itemPartIridium", -1);
			GameRegistry.addShapelessRecipe(new ItemStack(itemStackIridiumAlloy.getItem(), 9), new ItemStack(WarpDrive.blockIridium));
			
		} else if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			ItemStack itemStackIridiumAlloy = WarpDriveConfig.getModItemStack("IC2", "itemPartIridium", -1);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockIridium), "iii", "iii", "iii",
					'i', itemStackIridiumAlloy));
			GameRegistry.addShapelessRecipe(new ItemStack(itemStackIridiumAlloy.getItem(), 9), new ItemStack(WarpDrive.blockIridium));
			
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockIridium), "ses", "ele", "ses",
					'l', "ingotLumium",
					's', "ingotSignalum",
					'e', "ingotEnderium"));
			// GameRegistry.addShapelessRecipe(new ItemStack(itemStackIridiumAlloy.getItem(), 9), new ItemStack(WarpDrive.blockIridium));
			
		} else if (WarpDriveConfig.isEnderIOloaded) {
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
					't', Items.ghast_tear,
					'd', Items.diamond,
					'e', Items.emerald,
					'y', Items.ender_eye);
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
				's', ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL),
				'w', Items.diamond_sword,
				'c', WarpDrive.blockShipController));
		
		// Camera is 1 daylight sensor, 2 motors, 1 computer interface, 2 glass panel, 1 Tuning diamond, 1 LV Machine casing
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCamera), false, "gtd", "zlm", "gtc",
				't', itemMotorLV,
				'z', ItemComponent.getItemStack(ComponentType.ZOOM),
				'd', ItemComponent.getItemStack(ComponentType.DIAMOND_CRYSTAL),
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'm', itemMachineCasingLV,
				'l', Blocks.daylight_detector,
				'g', "paneGlassColorless"));
		
		// Monitor is 3 flat screen, 1 computer interface, 1 Tuning diamond, 1 LV Machine casing
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockMonitor), false, "fd ", "fm ", "fc ",
				'f', ItemComponent.getItemStack(ComponentType.FLAT_SCREEN),
				'd', ItemComponent.getItemStack(ComponentType.DIAMOND_CRYSTAL),
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'm', itemMachineCasingLV));
		
		// Ship scanner is non-functional => no recipe
		/*
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockShipScanner), false, "ici", "isi", "mcm",
				'm', mfsu,
				'i', iridiumAlloy,
				'c', "circuitAdvanced",
				's', WarpDriveConfig.getModItemStack("IC2", "blockMachine2", 7) )); // Scanner
		/**/
		
		// Laser tree farm is 2 motors, 2 lenses, 1 computer interface, 1 LV Machine casing, 1 diamond axe, 2 glass pane
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockLaserTreeFarm), false, "glg", "tlt", "amc",
				't', itemMotorLV,
				'l', ItemComponent.getItemStack(ComponentType.LENS),
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'm', itemMachineCasingLV,
				'a', Items.diamond_axe,
				'g', "paneGlassColorless"));
		
		// Transporter is non-functional => no recipe
		/*
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockTransporter), false, "aea", "ctc", "ama",
				'a', advancedAlloy,
				'e', Items.ender_pearl,
				'c', "circuitAdvanced",
				'm', advancedMachine,
				't', WarpDriveConfig.getModItemStack("IC2", "blockMachine2", 0) )); // Teleporter
		/**/
		
		// IC2 needs to loaded for the following 2 recipes
		if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			ItemStack itemStackOverclockedHeatVent = WarpDriveConfig.getModItemStack("IC2", "reactorVentGold", 1);
			ItemStack itemStackReactorCoolant1 = WarpDriveConfig.getModItemStack("IC2", "reactorCoolantSix", -1);
			ItemStack itemStackReactorCoolant2 = itemStackReactorCoolant1;
			if (WarpDriveConfig.isGregTech5Loaded) {
				itemStackReactorCoolant1 = WarpDriveConfig.getModItemStack("gregtech", "gt.360k_Helium_Coolantcell", -1);
				itemStackReactorCoolant2 = WarpDriveConfig.getModItemStack("gregtech", "gt.360k_NaK_Coolantcell", -1);
			}
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.itemIC2reactorLaserFocus), false, "cld", "lhl", "dlc",
					'l', ItemComponent.getItemStack(ComponentType.LENS),
					'h', itemStackOverclockedHeatVent,
					'c', itemStackReactorCoolant1,
					'd', itemStackReactorCoolant2));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockIC2reactorLaserMonitor), false, "gCp", "lme", "gCc",
					'l', ItemComponent.getItemStack(ComponentType.LENS),
					'e', ItemComponent.getItemStack(ComponentType.EMERALD_CRYSTAL),
					'C', ItemComponent.getItemStack(ComponentType.CAPACITIVE_CRYSTAL),
					'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
					'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE),
					'g', "paneGlassColorless",
					'm', itemMachineCasingMV));
		}
		
		// Cloaking core is 3 Cloaking coils, 4 iridium blocks, 1 ship controller, 1 power interface
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCloakingCore), false, "ici", "csc", "ipi",
				'i', WarpDrive.blockIridium,
				'c', WarpDrive.blockCloakingCoil,
				's', WarpDrive.blockShipController,
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE)));
		
		// Cloaking coil is 1 Titanium plate, 4 reinforced iridium plate, 1x EV Machine casing (Ti) or 1 Beacon, 4 emerald, 4 diamond
		ItemStack itemStackGoldIngotOrCoil = new ItemStack(Items.gold_ingot);
		if (WarpDriveConfig.isGregTech5Loaded) {
			itemStackGoldIngotOrCoil = WarpDriveConfig.getModItemStack("gregtech", "gt.blockcasings", 14);	// Nichrome Coil block
		} else if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			itemStackGoldIngotOrCoil = WarpDriveConfig.getModItemStack("IC2", "itemRecipePart", 0);	// Coil
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			itemStackGoldIngotOrCoil = WarpDriveConfig.getModItemStack("ThermalExpansion", "material", 3);	// Redstone conductance coil
		} else if (WarpDriveConfig.isImmersiveEngineeringLoaded) {
			itemStackGoldIngotOrCoil = WarpDriveConfig.getModItemStack("ImmersiveEngineering", "coil", 2);	// HV wire coil
		} else if (WarpDriveConfig.isEnderIOloaded) {
			itemStackGoldIngotOrCoil = WarpDriveConfig.getModItemStack("EnderIO", "itemPowerConduit", 2);	// Ender energy conduit
		}
		ItemStack oreEmeraldOrTitaniumPlate = new ItemStack(Items.gold_ingot);
		if (OreDictionary.doesOreNameExist("plateTitanium") && !OreDictionary.getOres("plateTitanium").isEmpty()) {
			ArrayList<ItemStack> itemStackTitaniumPlates = OreDictionary.getOres("plateTitanium");
			oreEmeraldOrTitaniumPlate = itemStackTitaniumPlates.get(0);
		} else if (WarpDriveConfig.isAdvancedSolarPanelLoaded) {
			oreEmeraldOrTitaniumPlate = WarpDriveConfig.getModItemStack("AdvancedSolarPanel", "asp_crafting_items", 0);	// Sunnarium
		} else if (OreDictionary.doesOreNameExist("plateDenseSteel") && !OreDictionary.getOres("plateDenseSteel").isEmpty()) {// IndustrialCraft2
			ArrayList<ItemStack> itemStackPlateDenseSteel = OreDictionary.getOres("plateDenseSteel");
			oreEmeraldOrTitaniumPlate = itemStackPlateDenseSteel.get(0);
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			oreEmeraldOrTitaniumPlate = WarpDriveConfig.getModItemStack("ThermalExpansion", "Frame", 11);
		} else if (WarpDriveConfig.isImmersiveEngineeringLoaded) {
			oreEmeraldOrTitaniumPlate = WarpDriveConfig.getModItemStack("ImmersiveEngineering", "metalDecoration", 5);	// Heavy engineering block
		} else if (WarpDriveConfig.isEnderIOloaded) {
			oreEmeraldOrTitaniumPlate = WarpDriveConfig.getModItemStack("EnderIO", "itemAlloy", 2);	// Vibrant alloy
		}
		Object oreEmeraldOrReinforcedIridiumPlate = "gemEmerald";
		if (OreDictionary.doesOreNameExist("plateAlloyIridium") && !OreDictionary.getOres("plateAlloyIridium").isEmpty()) {// IndustrialCraft2 and Gregtech
			oreEmeraldOrReinforcedIridiumPlate = "plateAlloyIridium";
		} else if (WarpDriveConfig.isEnderIOloaded) {// EnderIO
			oreEmeraldOrReinforcedIridiumPlate = WarpDriveConfig.getModItemStack("EnderIO", "itemFrankenSkull", 2);
		} else if (WarpDriveConfig.isThermalExpansionLoaded) {
			oreEmeraldOrReinforcedIridiumPlate = "ingotLumium";
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockCloakingCoil), false, "iti", "cmc", "iti",
				't', oreEmeraldOrTitaniumPlate,
				'i', oreEmeraldOrReinforcedIridiumPlate,
				'c', itemStackGoldIngotOrCoil,
				'm', itemMachineCasingEV )); 
		
		// Enantiomorphic reactor core is 1 EV Machine casing (Ti), 4 Capacitive crystal, 1 Computer interface, 1 Power interface, 2 Lenses
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockEnanReactorCore), false, "CpC", "lml", "CcC",
				'm', ItemComponent.getItemStack(ComponentType.REACTOR_CORE),
				'l', ItemComponent.getItemStack(ComponentType.LENS),
				'p', ItemComponent.getItemStack(ComponentType.POWER_INTERFACE),
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'C', ItemComponent.getItemStack(ComponentType.CAPACITIVE_CRYSTAL))); 
		
		// Enantiomorphic reactor stabilization laser is 1 EV Machine casing (Ti), 4 Capacitive crystal, 1 Computer interface, 1 Power interface, 2 Lenses
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockEnanReactorLaser), false, "ghr", "ldm", "ghc",
				'd', ItemComponent.getItemStack(ComponentType.DIFFRACTION_GRATING),
				'l', ItemComponent.getItemStack(ComponentType.LENS),
				'c', ItemComponent.getItemStack(ComponentType.COMPUTER_INTERFACE),
				'm', itemMachineCasingHV,
				'r', Items.redstone,
				'g', "paneGlassColorless",
				'h', "blockHull3_plain"));
		
		// Decorative blocks
		OreDictionary.registerOre("warpDecorative", BlockDecorative.getItemStack(DecorativeType.PLAIN));
		OreDictionary.registerOre("warpDecorative", BlockDecorative.getItemStack(DecorativeType.ENERGIZED));
		OreDictionary.registerOre("warpDecorative", BlockDecorative.getItemStack(DecorativeType.NETWORK));
		GameRegistry.addRecipe(new ShapedOreRecipe(BlockDecorative.getItemStackNoCache(DecorativeType.PLAIN, 8), false, "sss", "scs", "sss",
				's', Blocks.stone,
				'c', Items.paper));
		GameRegistry.addRecipe(new ShapedOreRecipe(BlockDecorative.getItemStackNoCache(DecorativeType.PLAIN, 8), false, "sss", "scs", "sss",
				's', "warpDecorative",
				'c', "dyeWhite"));
		GameRegistry.addRecipe(new ShapedOreRecipe(BlockDecorative.getItemStackNoCache(DecorativeType.ENERGIZED, 8), false, "sss", "scs", "sss",
				's', "warpDecorative",
				'c', "dyeRed"));
		GameRegistry.addRecipe(new ShapedOreRecipe(BlockDecorative.getItemStackNoCache(DecorativeType.NETWORK, 8), false, "sss", "scs", "sss",
				's', "warpDecorative",
				'c', "dyeBlue"));
		
		// Armor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.itemHelmet), false, "iii", "iwi", "gcg",
				'i', Items.iron_ingot,
				'w', Blocks.wool,
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
					'q', Items.quartz, // "gemQuartz", // Items.quartz,
					'i', Items.iron_ingot, // "ingotIron",
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
			ItemStack reinforcedStone = WarpDriveConfig.getModItemStack("IC2", "blockAlloy", -1);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[0], 10, 0), false, "cbc", "bcb", "cbc",
					'b', Blocks.obsidian,
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
		for (int woolColor = 0; woolColor < 16; woolColor++) {
			if (WarpDriveConfig.isGregTech5Loaded) {
				ItemStack tungstensteelReinforcedBlock = WarpDriveConfig.getModItemStack("gregtech", "gt.blockreinforced", 3);
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, BlockColored.func_150031_c(woolColor)), false, " b ", "bcb", " b ",
						'b', new ItemStack(WarpDrive.blockHulls_plain[0], 4, BlockColored.func_150031_c(woolColor)),
						'c', tungstensteelReinforcedBlock ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, BlockColored.func_150031_c(woolColor)), false, "Xb ", "bcb", " b ",
						'b', "blockHull1_plain",
						'c', tungstensteelReinforcedBlock,
						'X', oreDyes[woolColor] ));
			} else if (WarpDriveConfig.isIndustrialCraft2Loaded) {
				ItemStack carbonPlate = WarpDriveConfig.getModItemStack("IC2", "itemPartCarbonPlate", -1);
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, BlockColored.func_150031_c(woolColor)), false, "cbc", "b b", "cbc",
						'b', new ItemStack(WarpDrive.blockHulls_plain[0], 4, BlockColored.func_150031_c(woolColor)),
						'c', carbonPlate ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, BlockColored.func_150031_c(woolColor)), false, "cbc", "bXb", "cbc",
						'b', "blockHull1_plain",
						'c', carbonPlate,
						'X', oreDyes[woolColor] ));
			} else if (OreDictionary.doesOreNameExist("ingotDarkSteel") && !OreDictionary.getOres("ingotDarkSteel").isEmpty()) {// EnderIO
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, BlockColored.func_150031_c(woolColor)), false, "cbc", "b b", "cbc",
						'b', new ItemStack(WarpDrive.blockHulls_plain[0], 4, BlockColored.func_150031_c(woolColor)),
						'c', "ingotDarkSteel" ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, BlockColored.func_150031_c(woolColor)), false, "cbc", "bXb", "cbc",
						'b', "blockHull1_plain",
						'c', "ingotDarkSteel",
						'X', oreDyes[woolColor] ));
			} else {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, BlockColored.func_150031_c(woolColor)), false, "cbc", "b b", "cbc",
						'b', new ItemStack(WarpDrive.blockHulls_plain[0], 4, BlockColored.func_150031_c(woolColor)),
						'c', Blocks.obsidian ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[1], 4, BlockColored.func_150031_c(woolColor)), false, "cbc", "bXb", "cbc",
						'b', "blockHull1_plain",
						'c', Blocks.obsidian,
						'X', oreDyes[woolColor] ));
			}
		}
		// Tier 3 = 4 Tier 2 + 1 naquadah plate gives 4
		// Tier 3 = 4 Tier 2 + 4 iridium plate gives 4
		// Tier 3 = 4 Tier 2 + 4 pulsating crystal gives 4
		// Tier 3 = 4 Tier 2 + 4 diamond gives 4
		for (int woolColor = 0; woolColor < 16; woolColor++) {
			if (OreDictionary.doesOreNameExist("plateNaquadah") && !OreDictionary.getOres("plateNaquadah").isEmpty()) {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, BlockColored.func_150031_c(woolColor)), false, " b ", "bcb", " b ",
						'b', new ItemStack(WarpDrive.blockHulls_plain[1], 4, BlockColored.func_150031_c(woolColor)),
						'c', "plateNaquadah" ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, BlockColored.func_150031_c(woolColor)), false, "Xb ", "bcb", " b ",
						'b', "blockHull2_plain",
						'c', "plateNaquadah",
						'X', oreDyes[woolColor] ));
			} else if (OreDictionary.doesOreNameExist("plateAlloyIridium") && !OreDictionary.getOres("plateAlloyIridium").isEmpty()) {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, BlockColored.func_150031_c(woolColor)), false, "cbc", "b b", "cbc",
						'b', new ItemStack(WarpDrive.blockHulls_plain[1], 4, BlockColored.func_150031_c(woolColor)),
						'c', "plateAlloyIridium" ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, BlockColored.func_150031_c(woolColor)), false, "cbc", "bXb", "cbc",
						'b', "blockHull2_plain",
						'c', "plateAlloyIridium",
						'X', oreDyes[woolColor] ));
			} else if (OreDictionary.doesOreNameExist("itemPulsatingCrystal") && !OreDictionary.getOres("itemPulsatingCrystal").isEmpty()) {// EnderIO
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, BlockColored.func_150031_c(woolColor)), false, "cbc", "b b", "cbc",
						'b', new ItemStack(WarpDrive.blockHulls_plain[1], 4, BlockColored.func_150031_c(woolColor)),
						'c', "itemPulsatingCrystal" ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, BlockColored.func_150031_c(woolColor)), false, "cbc", "bXb", "cbc",
						'b', "blockHull2_plain",
						'c', "itemPulsatingCrystal",
						'X', oreDyes[woolColor] ));
			} else {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, BlockColored.func_150031_c(woolColor)), false, "cbc", "b b", "cbc",
						'b', new ItemStack(WarpDrive.blockHulls_plain[1], 4, BlockColored.func_150031_c(woolColor)),
						'c', "gemDiamond" ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[2], 4, BlockColored.func_150031_c(woolColor)), false, "cbc", "bXb", "cbc",
						'b', "blockHull2_plain",
						'c', "gemDiamond",
						'X', oreDyes[woolColor] ));
			}
		}
		
		// Hull blocks variation
		for(int tier = 1; tier <= 3; tier++) {
			int index = tier - 1;
			for (int woolColor = 0; woolColor < 16; woolColor++) {
				
				// crafting glass
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_glass[index], 4, BlockColored.func_150031_c(woolColor)), false, "gpg", "pFp", "gpg",
						'g', "blockGlass",
						'p', new ItemStack(WarpDrive.blockHulls_plain[index], 8, BlockColored.func_150031_c(woolColor)),
						'F', "dustGlowstone" ));
				
				// changing colors
				GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[index], 1, BlockColored.func_150031_c(woolColor)),
						"dye" + BlockHullPlain.getDyeColorName(woolColor),
						"blockHull" + tier + "_plain"));
				GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(WarpDrive.blockHulls_glass[index], 1, BlockColored.func_150031_c(woolColor)),
						"dye" + BlockHullPlain.getDyeColorName(woolColor),
						"blockHull" + tier + "_glass"));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_plain[index], 8, BlockColored.func_150031_c(woolColor)), false, "###", "#X#", "###",
						'#', "blockHull" + tier + "_plain",
						'X', oreDyes[woolColor] ));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(WarpDrive.blockHulls_glass[index], 8, BlockColored.func_150031_c(woolColor)), false, "###", "#X#", "###",
						'#', "blockHull" + tier + "_glass",
						'X', oreDyes[woolColor] ));
			}
		}
	}
}
