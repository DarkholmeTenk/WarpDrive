package cr0s.warpdrive.world;

import java.util.Random;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;

public class WorldGenSmallShip extends WorldGenerator {
	private final boolean corrupted;
	
	public WorldGenSmallShip(boolean corrupted) {
		this.corrupted = corrupted;
	}
	
	@Override
	public boolean generate(World world, Random rand, BlockPos blockPos) {
		WorldGenStructure genStructure = new WorldGenStructure(corrupted, rand);
		int x = blockPos.getX() - 5;
		int y = blockPos.getY() - 4;
		int z = blockPos.getZ() - 6;
		genStructure.setHullPlain(world, x + 0, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 0, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 1, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 1, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 1, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 1, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 1, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 1, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 2, y + 3, z + 6);
		genStructure.setHullPlain(world, x + 2, y + 3, z + 7);
		genStructure.setHullPlain(world, x + 2, y + 3, z + 8);
		genStructure.setHullGlass(world, x + 2, y + 4, z + 6);
		genStructure.setHullGlass(world, x + 2, y + 4, z + 7);
		genStructure.setHullGlass(world, x + 2, y + 4, z + 8);
		genStructure.setHullGlass(world, x + 2, y + 5, z + 6);
		genStructure.setHullGlass(world, x + 2, y + 5, z + 7);
		genStructure.setHullGlass(world, x + 2, y + 5, z + 8);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 3, y + 3, z + 5);
		genStructure.setHullPlain(world, x + 3, y + 3, z + 9);
		genStructure.setHullPlain(world, x + 3, y + 4, z + 5);
		genStructure.setHullPlain(world, x + 3, y + 4, z + 9);
		genStructure.setHullPlain(world, x + 3, y + 5, z + 5);
		genStructure.setHullPlain(world, x + 3, y + 5, z + 9);
		genStructure.setHullPlain(world, x + 3, y + 6, z + 6);
		genStructure.setHullPlain(world, x + 3, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 3, y + 6, z + 8);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 4);
		world.setBlockState(new BlockPos(x + 4, y + 2, z + 5), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 4, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 8);
		world.setBlockState(new BlockPos(x + 4, y + 2, z + 9), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 4, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 4, y + 3, z + 4);
		genStructure.setHullPlain(world, x + 4, y + 3, z + 10);
		genStructure.setHullGlass(world, x + 4, y + 4, z + 4);
		genStructure.setHullGlass(world, x + 4, y + 4, z + 10);
		genStructure.setHullPlain(world, x + 4, y + 5, z + 4);
		genStructure.setHullPlain(world, x + 4, y + 5, z + 5);
		genStructure.setHullPlain(world, x + 4, y + 5, z + 9);
		genStructure.setHullPlain(world, x + 4, y + 5, z + 10);
		genStructure.setHullPlain(world, x + 4, y + 6, z + 6);
		genStructure.setHullPlain(world, x + 4, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 4, y + 6, z + 8);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 3);
		world.setBlockState(new BlockPos(x + 5, y + 2, z + 4), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 5, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 6);
		world.setBlockState(new BlockPos(x + 5, y + 2, z + 7), Blocks.WOOL.getStateFromMeta(14), 0);
		world.setBlockState(new BlockPos(x + 5, y + 2, z + 8), Blocks.WOOL.getStateFromMeta(8), 0);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 9);
		world.setBlockState(new BlockPos(x + 5, y + 2, z + 10), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 5, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 5, y + 3, z + 3);
		genStructure.setHullPlain(world, x + 5, y + 3, z + 11);
		genStructure.setHullGlass(world, x + 5, y + 4, z + 3);
		genStructure.setHullGlass(world, x + 5, y + 4, z + 11);
		genStructure.setHullPlain(world, x + 5, y + 5, z + 3);
		genStructure.setHullPlain(world, x + 5, y + 5, z + 4);
		genStructure.setHullPlain(world, x + 5, y + 5, z + 10);
		genStructure.setHullPlain(world, x + 5, y + 5, z + 11);
		genStructure.setHullPlain(world, x + 5, y + 6, z + 5);
		genStructure.setHullPlain(world, x + 5, y + 6, z + 6);
		genStructure.setHullPlain(world, x + 5, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 5, y + 6, z + 8);
		genStructure.setHullPlain(world, x + 5, y + 6, z + 9);
		genStructure.setHullPlain(world, x + 5, y + 7, z + 6);
		genStructure.setHullPlain(world, x + 5, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 5, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 5);
		world.setBlockState(new BlockPos(x + 6, y + 2, z + 6), Blocks.WOOL.getStateFromMeta(14), 0);
		world.setBlockState(new BlockPos(x + 6, y + 2, z + 7), Blocks.WOOL.getStateFromMeta(8), 0);
		world.setBlockState(new BlockPos(x + 6, y + 2, z + 8), Blocks.WOOL.getStateFromMeta(14), 0);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 6, y + 3, z + 2);
		world.setBlockState(new BlockPos(x + 6, y + 3, z + 3), Blocks.CHEST.getStateFromMeta(3), 0);
		fillChestWithBonuses(world, rand, x + 6, y + 3, z + 3);
		world.setBlockState(new BlockPos(x + 6, y + 3, z + 11), Blocks.CHEST.getStateFromMeta(2), 0);
		fillChestWithBonuses(world, rand, x + 6, y + 3, z + 11);
		genStructure.setHullPlain(world, x + 6, y + 3, z + 12);
		genStructure.setHullPlain(world, x + 6, y + 4, z + 2);
		world.setBlockState(new BlockPos(x + 6, y + 4, z + 3), Blocks.CHEST.getStateFromMeta(3), 0);
		fillChestWithBonuses(world, rand, x + 6, y + 4, z + 3);
		world.setBlockState(new BlockPos(x + 6, y + 4, z + 11), Blocks.CHEST.getStateFromMeta(2), 0);
		fillChestWithBonuses(world, rand, x + 6, y + 4, z + 11);
		genStructure.setHullPlain(world, x + 6, y + 4, z + 12);
		genStructure.setHullPlain(world, x + 6, y + 5, z + 2);
		genStructure.setHullPlain(world, x + 6, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 6, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 6, y + 6, z + 4);
		if (!corrupted || rand.nextBoolean()) {
			world.setBlockState(new BlockPos(x + 6, y + 6, z + 7), WarpDrive.blockAirGenerator.getDefaultState(), 0);
		}
		genStructure.setHullPlain(world, x + 6, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 6, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 6, y + 7, z + 5);
		genStructure.setHullPlain(world, x + 6, y + 7, z + 6);
		genStructure.setHullPlain(world, x + 6, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 6, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 6, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 6);
		world.setBlockState(new BlockPos(x + 7, y + 2, z + 7), Blocks.WOOL.getStateFromMeta(8), 0);
		world.setBlockState(new BlockPos(x + 7, y + 2, z + 8), Blocks.WOOL.getStateFromMeta(8), 0);
		world.setBlockState(new BlockPos(x + 7, y + 2, z + 9), Blocks.WOOL.getStateFromMeta(14), 0);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 7, y + 3, z + 2);
		genStructure.setHullPlain(world, x + 7, y + 3, z + 12);
		genStructure.setHullGlass(world, x + 7, y + 4, z + 2);
		genStructure.setHullGlass(world, x + 7, y + 4, z + 12);
		genStructure.setHullGlass(world, x + 7, y + 5, z + 2);
		genStructure.setHullGlass(world, x + 7, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 7, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 7, y + 6, z + 4);
		genStructure.setCable(world, x + 7, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 7, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 7, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 7, y + 7, z + 5);
		genStructure.setHullPlain(world, x + 7, y + 7, z + 6);
		genStructure.setSolarPanel(world, x + 7, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 7, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 7, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 5);
		world.setBlockState(new BlockPos(x + 8, y + 2, z + 6), Blocks.WOOL.getStateFromMeta(14), 0);
		world.setBlockState(new BlockPos(x + 8, y + 2, z + 7), Blocks.WOOL.getStateFromMeta(14), 0);
		world.setBlockState(new BlockPos(x + 8, y + 2, z + 8), Blocks.WOOL.getStateFromMeta(14), 0);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 8, y + 3, z + 2);
		genStructure.setHullPlain(world, x + 8, y + 3, z + 12);
		genStructure.setHullGlass(world, x + 8, y + 4, z + 2);
		genStructure.setHullGlass(world, x + 8, y + 4, z + 12);
		genStructure.setHullGlass(world, x + 8, y + 5, z + 2);
		genStructure.setHullGlass(world, x + 8, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 8, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 8, y + 6, z + 4);
		genStructure.setCable(world, x + 8, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 8, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 8, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 8, y + 7, z + 4);
		genStructure.setHullPlain(world, x + 8, y + 7, z + 5);
		genStructure.setHullPlain(world, x + 8, y + 7, z + 6);
		genStructure.setSolarPanel(world, x + 8, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 8, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 8, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 8, y + 7, z + 10);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 4);
		world.setBlockState(new BlockPos(x + 9, y + 2, z + 5), Blocks.WOOL.getStateFromMeta(14), 0);
		world.setBlockState(new BlockPos(x + 9, y + 2, z + 6), Blocks.WOOL.getStateFromMeta(8), 0);
		world.setBlockState(new BlockPos(x + 9, y + 2, z + 7), Blocks.WOOL.getStateFromMeta(14), 0);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 9);
		world.setBlockState(new BlockPos(x + 9, y + 2, z + 10), Blocks.WOOL.getStateFromMeta(14), 0);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 9, y + 3, z + 2);
		genStructure.setHullPlain(world, x + 9, y + 3, z + 12);
		genStructure.setHullGlass(world, x + 9, y + 4, z + 2);
		genStructure.setHullGlass(world, x + 9, y + 4, z + 12);
		genStructure.setHullGlass(world, x + 9, y + 5, z + 2);
		genStructure.setHullGlass(world, x + 9, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 9, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 9, y + 6, z + 4);
		genStructure.setCable(world, x + 9, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 9, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 9, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 4);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 5);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 6);
		genStructure.setSolarPanel(world, x + 9, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 10);
		genStructure.setHullPlain(world, x + 9, y + 8, z + 4);
		genStructure.setHullPlain(world, x + 9, y + 8, z + 10);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 4);
		world.setBlockState(new BlockPos(x + 10, y + 2, z + 5), Blocks.WOOL.getStateFromMeta(8), 0);
		world.setBlockState(new BlockPos(x + 10, y + 2, z + 6), Blocks.WOOL.getStateFromMeta(8), 0);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 8);
		world.setBlockState(new BlockPos(x + 10, y + 2, z + 9), Blocks.WOOL.getStateFromMeta(8), 0);
		world.setBlockState(new BlockPos(x + 10, y + 2, z + 10), Blocks.WOOL.getStateFromMeta(14), 0);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 10, y + 3, z + 2);
		genStructure.setHullGlass(world, x + 10, y + 3, z + 7);
		genStructure.setHullPlain(world, x + 10, y + 3, z + 12);
		genStructure.setHullGlass(world, x + 10, y + 4, z + 2);
		genStructure.setHullGlass(world, x + 10, y + 4, z + 12);
		genStructure.setHullGlass(world, x + 10, y + 5, z + 2);
		genStructure.setHullGlass(world, x + 10, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 10, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 10, y + 6, z + 4);
		genStructure.setCable(world, x + 10, y + 6, z + 7);	
		genStructure.setHullPlain(world, x + 10, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 10, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 4);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 5);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 6);
		genStructure.setSolarPanel(world, x + 10, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 10);
		genStructure.setHullPlain(world, x + 10, y + 8, z + 4);
		genStructure.setHullPlain(world, x + 10, y + 8, z + 10);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 11, y + 3, z + 2);
		genStructure.setHullGlass(world, x + 11, y + 3, z + 6);
		if (!corrupted || rand.nextBoolean()) {
			world.setBlockState(new BlockPos(x + 11, y + 3, z + 7), WarpDrive.blockShipController.getDefaultState());
		}
		genStructure.setHullGlass(world, x + 11, y + 3, z + 8);
		genStructure.setHullPlain(world, x + 11, y + 3, z + 12);
		genStructure.setHullGlass(world, x + 11, y + 4, z + 2);
		genStructure.setHullGlass(world, x + 11, y + 4, z + 6);
		if ((!corrupted || rand.nextBoolean()) && WarpDriveConfig.isComputerCraftLoaded) {
			world.setBlockState(new BlockPos(x + 11, y + 4, z + 7), WarpDriveConfig.CC_Computer.getStateFromMeta(12), 3);
		}
		genStructure.setHullGlass(world, x + 11, y + 4, z + 8);
		genStructure.setHullGlass(world, x + 11, y + 4, z + 12);
		genStructure.setHullGlass(world, x + 11, y + 5, z + 2);
		if (!corrupted || rand.nextBoolean()) {
			world.setBlockState(new BlockPos(x + 11, y + 5, z + 7), WarpDrive.blockAirGenerator.getDefaultState(), 0);
		}
		genStructure.setHullGlass(world, x + 11, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 11, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 11, y + 6, z + 4);
		genStructure.setCable(world, x + 11, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 11, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 11, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 4);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 5);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 6);
		genStructure.setSolarPanel(world, x + 11, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 10);
		genStructure.setHullPlain(world, x + 11, y + 8, z + 4);
		genStructure.setHullPlain(world, x + 11, y + 8, z + 10);
		genStructure.setHullPlain(world, x + 11, y + 9, z + 4);
		genStructure.setHullPlain(world, x + 11, y + 9, z + 10);
		genStructure.setHullPlain(world, x + 12, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 12, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 12, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 12, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 12, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 12, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 12, y + 3, z + 2);
		genStructure.setHullGlass(world, x + 12, y + 3, z + 6);
		if (!corrupted || rand.nextBoolean()) {
			world.setBlockState(new BlockPos(x + 12, y + 3, z + 7), WarpDrive.blockShipCore.getDefaultState());
		}
		genStructure.setHullGlass(world, x + 12, y + 3, z + 8);
		genStructure.setHullPlain(world, x + 12, y + 3, z + 12);
		genStructure.setHullGlass(world, x + 12, y + 4, z + 2);
		genStructure.setHullGlass(world, x + 12, y + 4, z + 6);
		genStructure.setCable(world, x + 12, y + 4, z + 7);
		genStructure.setHullGlass(world, x + 12, y + 4, z + 8);
		genStructure.setHullGlass(world, x + 12, y + 4, z + 12);
		genStructure.setHullGlass(world, x + 12, y + 5, z + 2);
		genStructure.setCable(world, x + 12, y + 5, z + 7);
		genStructure.setHullGlass(world, x + 12, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 4);
		genStructure.setCable(world, x + 12, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 12, y + 7, z + 5);
		genStructure.setHullPlain(world, x + 12, y + 7, z + 6);
		genStructure.setHullPlain(world, x + 12, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 12, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 12, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 13, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 13, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 13, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 13, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 13, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 13, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 13, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 13, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 2);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 3);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 4);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 5);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 6);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 7);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 8);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 9);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 10);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 11);
		genStructure.setHullPlain(world, x + 13, y + 3, z + 12);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 2);
		genStructure.setHullGlass(world, x + 13, y + 4, z + 3);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 4);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 5);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 6);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 7);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 8);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 9);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 10);
		genStructure.setHullGlass(world, x + 13, y + 4, z + 11);
		genStructure.setHullPlain(world, x + 13, y + 4, z + 12);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 2);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 3);
		world.setBlockState(new BlockPos(x + 13, y + 5, z + 4), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 13, y + 5, z + 5);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 6);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 7);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 8);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 9);
		world.setBlockState(new BlockPos(x + 13, y + 5, z + 10), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 13, y + 5, z + 11);
		genStructure.setHullPlain(world, x + 13, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 3);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 4);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 5);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 6);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 8);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 9);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 13, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 13, y + 7, z + 6);
		genStructure.setHullPlain(world, x + 13, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 13, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 14, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 14, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 14, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 14, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 14, y + 3, z + 4);
		genStructure.setHullPlain(world, x + 14, y + 3, z + 5);
		genStructure.setHullPlain(world, x + 14, y + 3, z + 6);
		genStructure.setHullPlain(world, x + 14, y + 3, z + 7);
		genStructure.setHullPlain(world, x + 14, y + 3, z + 8);
		genStructure.setHullPlain(world, x + 14, y + 3, z + 9);
		genStructure.setHullPlain(world, x + 14, y + 3, z + 10);
		genStructure.setHullPlain(world, x + 14, y + 4, z + 4);
		world.setBlockState(new BlockPos(x + 14, y + 4, z + 5), Blocks.REDSTONE_BLOCK.getDefaultState());
		world.setBlockState(new BlockPos(x + 14, y + 4, z + 6), Blocks.REDSTONE_BLOCK.getDefaultState());
		genStructure.setHullPlain(world, x + 14, y + 4, z + 7);
		world.setBlockState(new BlockPos(x + 14, y + 4, z + 8), Blocks.REDSTONE_BLOCK.getDefaultState());
		world.setBlockState(new BlockPos(x + 14, y + 4, z + 9), Blocks.REDSTONE_BLOCK.getDefaultState());
		genStructure.setHullPlain(world, x + 14, y + 4, z + 10);
		genStructure.setHullPlain(world, x + 14, y + 5, z + 4);
		world.setBlockState(new BlockPos(x + 14, y + 5, z + 5), Blocks.REDSTONE_BLOCK.getDefaultState());
		world.setBlockState(new BlockPos(x + 14, y + 5, z + 6), Blocks.REDSTONE_BLOCK.getDefaultState());
		genStructure.setHullPlain(world, x + 14, y + 5, z + 7);
		world.setBlockState(new BlockPos(x + 14, y + 5, z + 8), Blocks.REDSTONE_BLOCK.getDefaultState());
		world.setBlockState(new BlockPos(x + 14, y + 5, z + 9), Blocks.REDSTONE_BLOCK.getDefaultState());
		genStructure.setHullPlain(world, x + 14, y + 5, z + 10);
		genStructure.setHullPlain(world, x + 14, y + 6, z + 4);
		genStructure.setHullPlain(world, x + 14, y + 6, z + 5);
		genStructure.setHullPlain(world, x + 14, y + 6, z + 6);
		genStructure.setHullPlain(world, x + 14, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 14, y + 6, z + 8);
		genStructure.setHullPlain(world, x + 14, y + 6, z + 9);
		genStructure.setHullPlain(world, x + 14, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 15, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 15, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 15, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 15, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 15, y + 3, z + 4);
		genStructure.setHullPlain(world, x + 15, y + 3, z + 10);
		genStructure.setHullPlain(world, x + 15, y + 4, z + 7);
		world.setBlockState(new BlockPos(x + 15, y + 5, z + 7), Blocks.REDSTONE_BLOCK.getDefaultState());
		genStructure.setHullPlain(world, x + 15, y + 6, z + 4);
		genStructure.setHullPlain(world, x + 15, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 15, y + 6, z + 10);
		genStructure.setHullPlain(world, x + 16, y + 4, z + 7);
		world.setBlockState(new BlockPos(x + 16, y + 5, z + 7), Blocks.REDSTONE_BLOCK.getDefaultState());
		genStructure.setHullPlain(world, x + 16, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 17, y + 5, z + 7);
		world.setBlockState(new BlockPos(x + 12, y + 2, z + 9), Blocks.TRAPDOOR.getStateFromMeta(10), 0);
		spawnNPC(world, x + 9, y + 3, z + 5);
		return true;
	}
	
	public static void spawnNPC(World world, int i, int j, int k) {
		int numMobs = 2 + world.rand.nextInt(10);
		
		if (world.rand.nextBoolean()) {// Villagers
			for (int idx = 0; idx < numMobs; idx++) {
				EntityVillager entityvillager = new EntityVillager(world, 0);
				entityvillager.setLocationAndAngles(i + 0.5D, j, k + 0.5D, 0.0F, 0.0F);
				world.spawnEntityInWorld(entityvillager);
			}
		} else {// Zombies
			for (int idx = 0; idx < numMobs; idx++) {
				EntityZombie entityzombie = new EntityZombie(world);
				entityzombie.setLocationAndAngles(i + 0.5D, j, k + 0.5D, 0.0F, 0.0F);
				world.spawnEntityInWorld(entityzombie);
			}
		}
	}
	
	public void fillChestWithBonuses(World worldObj, Random rand, int x, int y, int z) {
		TileEntity tileEntity = worldObj.getTileEntity(new BlockPos(x, y, z));
		
		if (tileEntity != null) {
			TileEntityChest chest = (TileEntityChest) tileEntity;
			int size = chest.getSizeInventory();
			int numBonuses = rand.nextInt(size) / 2;
			
			for (int i = 0; i < size; i++) {
				if (rand.nextInt(size) <= numBonuses) {
					numBonuses--;
					chest.setInventorySlotContents(i, getRandomBonus(rand));
				}
			}
		}
	}
	
	private ItemStack getRandomBonus(Random rand) {
		ItemStack res = null;
		boolean isDone = false;
		
		while (!isDone) {
			switch (rand.nextInt(14)) {
			case 0: // Mass fabricator
				if (WarpDriveConfig.isIndustrialCraft2Loaded) {
					res = WarpDriveConfig.getModItemStack("IC2", "blockMachine", -1);
					res.setItemDamage(14);
					res.stackSize = 1; // + rand.nextInt(2);
					isDone = true;
				}
				break;
				
			case 1:
				if (WarpDriveConfig.isIndustrialCraft2Loaded) {
					res = WarpDriveConfig.getModItemStack("IC2", "blockNuke", -1);
					res.stackSize = 1 + rand.nextInt(2);
					isDone = true;
				}
				break;
				
			case 2: // Quantum armor bonuses
			case 3:
			case 4:
			case 5:
				isDone = true;
				break;// skipped
				
			case 6:// Glass fiber cable item
				if (WarpDriveConfig.isIndustrialCraft2Loaded) {
					res = WarpDriveConfig.getModItemStack("IC2", "itemCable", -1);
					res.setItemDamage(9);
					res.stackSize = 2 + rand.nextInt(12);
					isDone = true;
				}
				break;
			
			case 7:// UU matter cell
				if (WarpDriveConfig.isIndustrialCraft2Loaded) {
					res = WarpDriveConfig.getModItemStack("IC2", "itemCellEmpty", -1);
					res.setItemDamage(3);
					res.stackSize = 2 + rand.nextInt(14);
					isDone = true;
				}
				break;
			
			case 8:
				isDone = true;
				break;// skipped
			
			case 9:
			case 10:
			case 11: // Rocket launcher platform Tier3
				if (WarpDriveConfig.isICBMLoaded) {
					// TODO: No 1.7 ICBM yet
					// res = new ItemStack(WarpDriveConfig.ICBM_Machine, 1 +
					// rand.nextInt(1), 2).copy();
					isDone = true;
				}
				break;
			
			
			case 12: // Missiles from conventional to hypersonic
				if (WarpDriveConfig.isICBMLoaded) {
					// TODO: No 1.7 ICBM yet
					// res = new ItemStack(WarpDriveConfig.ICBM_Missile, 2 +
					// rand.nextInt(1), rand.nextInt(10)).copy();
					isDone = true;
				}
				break;
			
			case 13: // Advanced solar panels
				if (WarpDriveConfig.isAdvancedSolarPanelLoaded) {
					// TODO: res = new ItemStack(solarPanel_block, rand.nextInt(3), solarPanel_metadata);
					isDone = true;
				}
				break;
			
			default:
				break;
			}
		}
		
		return res;
	}
}
