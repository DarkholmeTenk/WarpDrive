package cr0s.warpdrive.world;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenStation extends WorldGenerator {
	private final boolean corrupted;

	public WorldGenStation(boolean corrupted) {
		this.corrupted = corrupted;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos blockPos) {
		int x = blockPos.getX();
		int y = blockPos.getY();
		int z = blockPos.getZ();
		WorldGenStructure genStructure = new WorldGenStructure(corrupted, rand);
		genStructure.setHullPlain(world, x + 1, y + 0, z + 3);
		genStructure.setHullPlain(world, x + 1, y + 0, z + 12);
		genStructure.setHullPlain(world, x + 1, y + 1, z + 3);
		genStructure.setHullPlain(world, x + 1, y + 1, z + 12);
		genStructure.setHullPlain(world, x + 1, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 1, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 1, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 1, y + 2, z + 6);
		world.setBlockState(new BlockPos(x + 1, y + 2, z + 7), Blocks.DIRT.getDefaultState());
		world.setBlockState(new BlockPos(x + 1, y + 2, z + 8), Blocks.DIRT.getDefaultState());
		genStructure.setHullPlain(world, x + 1, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 1, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 1, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 1, y + 2, z + 12);
		genStructure.setHullPlain(world, x + 1, y + 3, z + 3);
		genStructure.setHullPlain(world, x + 1, y + 3, z + 4);
		genStructure.setHullPlain(world, x + 1, y + 3, z + 5);
		genStructure.setHullPlain(world, x + 1, y + 3, z + 10);
		genStructure.setHullPlain(world, x + 1, y + 3, z + 11);
		genStructure.setHullPlain(world, x + 1, y + 3, z + 12);
		genStructure.setHullPlain(world, x + 1, y + 4, z + 3);
		genStructure.setHullPlain(world, x + 1, y + 4, z + 4);
		genStructure.setHullPlain(world, x + 1, y + 4, z + 5);
		genStructure.setHullPlain(world, x + 1, y + 4, z + 10);
		genStructure.setHullPlain(world, x + 1, y + 4, z + 11);
		genStructure.setHullPlain(world, x + 1, y + 4, z + 12);
		genStructure.setHullPlain(world, x + 1, y + 5, z + 3);
		genStructure.setHullGlass(world, x + 1, y + 5, z + 4);
		genStructure.setHullPlain(world, x + 1, y + 5, z + 5);
		genStructure.setHullPlain(world, x + 1, y + 5, z + 6);
		genStructure.setHullPlain(world, x + 1, y + 5, z + 9);
		genStructure.setHullPlain(world, x + 1, y + 5, z + 10);
		genStructure.setHullGlass(world, x + 1, y + 5, z + 11);
		genStructure.setHullPlain(world, x + 1, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 1, y + 6, z + 3);
		genStructure.setHullGlass(world, x + 1, y + 6, z + 4);
		genStructure.setHullGlass(world, x + 1, y + 6, z + 5);
		genStructure.setHullPlain(world, x + 1, y + 6, z + 6);
		genStructure.setHullPlain(world, x + 1, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 1, y + 6, z + 8);
		genStructure.setHullPlain(world, x + 1, y + 6, z + 9);
		genStructure.setHullGlass(world, x + 1, y + 6, z + 10);
		genStructure.setHullGlass(world, x + 1, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 1, y + 6, z + 12);
		genStructure.setHullPlain(world, x + 1, y + 7, z + 3);
		genStructure.setHullPlain(world, x + 1, y + 7, z + 12);
		genStructure.setHullPlain(world, x + 2, y + 0, z + 2);
		genStructure.setHullPlain(world, x + 2, y + 0, z + 3);
		genStructure.setHullPlain(world, x + 2, y + 0, z + 4);
		genStructure.setHullPlain(world, x + 2, y + 0, z + 12);
		genStructure.setHullPlain(world, x + 2, y + 0, z + 13);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 2);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 3);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 11);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 12);
		genStructure.setHullPlain(world, x + 2, y + 1, z + 13);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 2);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 12);
		genStructure.setHullPlain(world, x + 2, y + 2, z + 13);
		genStructure.setHullPlain(world, x + 2, y + 3, z + 2);
		genStructure.setHullPlain(world, x + 2, y + 3, z + 13);
		genStructure.setHullPlain(world, x + 2, y + 4, z + 2);
		genStructure.setHullPlain(world, x + 2, y + 4, z + 13);
		genStructure.setHullPlain(world, x + 2, y + 5, z + 2);
		genStructure.setHullPlain(world, x + 2, y + 5, z + 13);
		genStructure.setHullPlain(world, x + 2, y + 6, z + 2);
		genStructure.setHullPlain(world, x + 2, y + 6, z + 13);
		genStructure.setHullPlain(world, x + 2, y + 7, z + 2);
		genStructure.setHullPlain(world, x + 2, y + 7, z + 3);
		genStructure.setHullPlain(world, x + 2, y + 7, z + 4);
		genStructure.setHullPlain(world, x + 2, y + 7, z + 5);
		genStructure.setHullGlass(world, x + 2, y + 7, z + 6);
		genStructure.setHullGlass(world, x + 2, y + 7, z + 7);
		genStructure.setHullGlass(world, x + 2, y + 7, z + 8);
		genStructure.setHullGlass(world, x + 2, y + 7, z + 9);
		genStructure.setHullGlass(world, x + 2, y + 7, z + 10);
		genStructure.setHullPlain(world, x + 2, y + 7, z + 11);
		genStructure.setHullPlain(world, x + 2, y + 7, z + 12);
		genStructure.setHullPlain(world, x + 2, y + 7, z + 13);
		genStructure.setHullPlain(world, x + 3, y + 0, z + 4);
		genStructure.setHullPlain(world, x + 3, y + 0, z + 5);
		genStructure.setHullPlain(world, x + 3, y + 0, z + 11);
		genStructure.setHullPlain(world, x + 3, y + 0, z + 12);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 3);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 11);
		genStructure.setHullPlain(world, x + 3, y + 1, z + 12);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 1);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 12);
		genStructure.setHullPlain(world, x + 3, y + 2, z + 13);
		genStructure.setHullPlain(world, x + 3, y + 3, z + 1);
		world.setBlockState(new BlockPos(x + 3, y + 3, z + 13), Blocks.CHEST.getStateFromMeta(2), 0);
		genStructure.setHullPlain(world, x + 3, y + 3, z + 14);
		genStructure.setHullPlain(world, x + 3, y + 4, z + 1);
		genStructure.setHullPlain(world, x + 3, y + 4, z + 14);
		genStructure.setHullPlain(world, x + 3, y + 5, z + 1);
		world.setBlockState(new BlockPos(x + 3, y + 5, z + 2), Blocks.DISPENSER.getStateFromMeta(3), 0);
		genStructure.setHullPlain(world, x + 3, y + 5, z + 14);
		genStructure.setHullPlain(world, x + 3, y + 6, z + 1);
		world.setBlockState(new BlockPos(x + 3, y + 6, z + 2), Blocks.PLANKS.getDefaultState());
		genStructure.setHullPlain(world, x + 3, y + 6, z + 14);
		genStructure.setHullPlain(world, x + 3, y + 7, z + 1);
		world.setBlockState(new BlockPos(x + 3, y + 7, z + 2), Blocks.LAPIS_BLOCK.getDefaultState());
		genStructure.setHullPlain(world, x + 3, y + 7, z + 3);
		world.setBlockState(new BlockPos(x + 3, y + 7, z + 4), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 3, y + 7, z + 5);
		genStructure.setHullPlain(world, x + 3, y + 7, z + 6);
		genStructure.setHullGlass(world, x + 3, y + 7, z + 7);
		genStructure.setHullGlass(world, x + 3, y + 7, z + 8);
		genStructure.setHullGlass(world, x + 3, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 3, y + 7, z + 10);
		genStructure.setHullPlain(world, x + 3, y + 7, z + 11);
		world.setBlockState(new BlockPos(x + 3, y + 7, z + 12), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 3, y + 7, z + 13);
		genStructure.setHullPlain(world, x + 3, y + 7, z + 14);
		genStructure.setHullGlass(world, x + 3, y + 8, z + 2);
		genStructure.setHullPlain(world, x + 3, y + 8, z + 4);
		genStructure.setHullPlain(world, x + 3, y + 8, z + 12);
		genStructure.setHullPlain(world, x + 4, y + 0, z + 5);
		genStructure.setHullPlain(world, x + 4, y + 0, z + 6);
		genStructure.setHullPlain(world, x + 4, y + 0, z + 10);
		genStructure.setHullPlain(world, x + 4, y + 0, z + 11);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 3);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 11);
		genStructure.setHullPlain(world, x + 4, y + 1, z + 12);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 2);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 12);
		genStructure.setHullPlain(world, x + 4, y + 2, z + 13);
		genStructure.setHullPlain(world, x + 4, y + 3, z + 1);
		world.setBlockState(new BlockPos(x + 4, y + 3, z + 13), Blocks.CHEST.getStateFromMeta(2), 0);
		genStructure.setHullPlain(world, x + 4, y + 3, z + 14);
		genStructure.setHullGlass(world, x + 4, y + 4, z + 1);
		genStructure.setHullGlass(world, x + 4, y + 4, z + 14);
		genStructure.setHullGlass(world, x + 4, y + 5, z + 1);
		genStructure.setHullGlass(world, x + 4, y + 5, z + 14);
		genStructure.setHullPlain(world, x + 4, y + 6, z + 1);
		world.setBlockState(new BlockPos(x + 4, y + 6, z + 2), Blocks.PLANKS.getDefaultState());
		genStructure.setHullPlain(world, x + 4, y + 6, z + 14);
		genStructure.setHullGlass(world, x + 4, y + 7, z + 1);
		world.setBlockState(new BlockPos(x + 4, y + 7, z + 2), Blocks.LAPIS_BLOCK.getDefaultState());
		genStructure.setHullPlain(world, x + 4, y + 7, z + 3);
		genStructure.setHullPlain(world, x + 4, y + 7, z + 4);
		world.setBlockState(new BlockPos(x + 4, y + 7, z + 5), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 4, y + 7, z + 6);
		genStructure.setHullPlain(world, x + 4, y + 7, z + 7);
		genStructure.setHullGlass(world, x + 4, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 4, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 4, y + 7, z + 10);
		world.setBlockState(new BlockPos(x + 4, y + 7, z + 11), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 4, y + 7, z + 12);
		genStructure.setHullPlain(world, x + 4, y + 7, z + 13);
		genStructure.setHullGlass(world, x + 4, y + 8, z + 2);
		genStructure.setHullPlain(world, x + 4, y + 8, z + 5);
		genStructure.setHullPlain(world, x + 4, y + 8, z + 11);
		genStructure.setHullPlain(world, x + 5, y + 0, z + 6);
		genStructure.setHullPlain(world, x + 5, y + 0, z + 7);
		genStructure.setHullPlain(world, x + 5, y + 0, z + 9);
		genStructure.setHullPlain(world, x + 5, y + 0, z + 10);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 3);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 11);
		genStructure.setHullPlain(world, x + 5, y + 1, z + 12);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 2);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 12);
		genStructure.setHullPlain(world, x + 5, y + 2, z + 13);
		genStructure.setHullPlain(world, x + 5, y + 3, z + 1);
		genStructure.setHullPlain(world, x + 5, y + 3, z + 14);
		genStructure.setHullGlass(world, x + 5, y + 4, z + 1);
		genStructure.setHullGlass(world, x + 5, y + 4, z + 14);
		genStructure.setHullGlass(world, x + 5, y + 5, z + 1);
		genStructure.setHullGlass(world, x + 5, y + 5, z + 14);
		genStructure.setHullPlain(world, x + 5, y + 6, z + 1);
		world.setBlockState(new BlockPos(x + 5, y + 6, z + 2), Blocks.PLANKS.getDefaultState());
		genStructure.setHullPlain(world, x + 5, y + 6, z + 14);
		genStructure.setHullGlass(world, x + 5, y + 7, z + 1);
		world.setBlockState(new BlockPos(x + 5, y + 7, z + 2), Blocks.LAPIS_BLOCK.getDefaultState());
		genStructure.setHullGlass(world, x + 5, y + 7, z + 3);
		genStructure.setHullPlain(world, x + 5, y + 7, z + 4);
		genStructure.setHullPlain(world, x + 5, y + 7, z + 5);
		genStructure.setHullPlain(world, x + 5, y + 7, z + 6);
		genStructure.setHullPlain(world, x + 5, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 5, y + 7, z + 8);
		world.setBlockState(new BlockPos(x + 5, y + 7, z + 9), Blocks.GLOWSTONE.getDefaultState());
		world.setBlockState(new BlockPos(x + 5, y + 7, z + 10), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 5, y + 7, z + 11);
		genStructure.setHullPlain(world, x + 5, y + 7, z + 12);
		genStructure.setHullGlass(world, x + 5, y + 7, z + 13);
		genStructure.setHullGlass(world, x + 5, y + 8, z + 2);
		genStructure.setHullPlain(world, x + 5, y + 8, z + 9);
		genStructure.setHullPlain(world, x + 5, y + 8, z + 10);
		genStructure.setHullPlain(world, x + 6, y + 0, z + 7);
		genStructure.setHullPlain(world, x + 6, y + 0, z + 8);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 3);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 11);
		genStructure.setHullPlain(world, x + 6, y + 1, z + 12);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 2);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 12);
		genStructure.setHullPlain(world, x + 6, y + 2, z + 13);
		genStructure.setHullPlain(world, x + 6, y + 3, z + 1);
		world.setBlockState(new BlockPos(x + 6, y + 3, z + 13), Blocks.CHEST.getStateFromMeta(2), 0);
		genStructure.setHullPlain(world, x + 6, y + 3, z + 14);
		genStructure.setHullGlass(world, x + 6, y + 4, z + 1);
		genStructure.setHullGlass(world, x + 6, y + 4, z + 14);
		genStructure.setHullGlass(world, x + 6, y + 5, z + 1);
		genStructure.setHullGlass(world, x + 6, y + 5, z + 14);
		genStructure.setHullPlain(world, x + 6, y + 6, z + 1);
		world.setBlockState(new BlockPos(x + 6, y + 6, z + 2), Blocks.PLANKS.getDefaultState());
		genStructure.setHullPlain(world, x + 6, y + 6, z + 14);
		genStructure.setHullGlass(world, x + 6, y + 7, z + 1);
		world.setBlockState(new BlockPos(x + 6, y + 7, z + 2), Blocks.LAPIS_BLOCK.getDefaultState());
		genStructure.setHullGlass(world, x + 6, y + 7, z + 3);
		genStructure.setHullGlass(world, x + 6, y + 7, z + 4);
		genStructure.setHullPlain(world, x + 6, y + 7, z + 5);
		genStructure.setHullPlain(world, x + 6, y + 7, z + 6);
		world.setBlockState(new BlockPos(x + 6, y + 7, z + 7), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 6, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 6, y + 7, z + 9);
		world.setBlockState(new BlockPos(x + 6, y + 7, z + 10), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 6, y + 7, z + 11);
		genStructure.setHullGlass(world, x + 6, y + 7, z + 12);
		genStructure.setHullGlass(world, x + 6, y + 7, z + 13);
		genStructure.setHullGlass(world, x + 6, y + 8, z + 2);
		genStructure.setHullPlain(world, x + 6, y + 8, z + 7);
		genStructure.setHullPlain(world, x + 6, y + 8, z + 10);
		genStructure.setHullPlain(world, x + 7, y + 0, z + 7);
		genStructure.setHullPlain(world, x + 7, y + 0, z + 8);
		genStructure.setHullPlain(world, x + 7, y + 0, z + 9);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 3);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 11);
		genStructure.setHullPlain(world, x + 7, y + 1, z + 12);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 2);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 12);
		genStructure.setHullPlain(world, x + 7, y + 2, z + 13);
		genStructure.setHullPlain(world, x + 7, y + 3, z + 1);
		world.setBlockState(new BlockPos(x + 7, y + 3, z + 13), Blocks.CHEST.getStateFromMeta(2), 0);
		genStructure.setHullPlain(world, x + 7, y + 3, z + 14);
		genStructure.setHullGlass(world, x + 7, y + 4, z + 1);
		genStructure.setHullGlass(world, x + 7, y + 4, z + 14);
		genStructure.setHullGlass(world, x + 7, y + 5, z + 1);
		genStructure.setHullGlass(world, x + 7, y + 5, z + 14);
		genStructure.setHullPlain(world, x + 7, y + 6, z + 1);
		world.setBlockState(new BlockPos(x + 7, y + 6, z + 2), Blocks.PLANKS.getDefaultState());
		genStructure.setHullPlain(world, x + 7, y + 6, z + 14);
		genStructure.setHullGlass(world, x + 7, y + 7, z + 1);
		world.setBlockState(new BlockPos(x + 7, y + 7, z + 2), Blocks.LAPIS_BLOCK.getDefaultState());
		genStructure.setHullGlass(world, x + 7, y + 7, z + 3);
		genStructure.setHullPlain(world, x + 7, y + 7, z + 4);
		world.setBlockState(new BlockPos(x + 7, y + 7, z + 5), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 7, y + 7, z + 6);
		genStructure.setHullPlain(world, x + 7, y + 7, z + 7);
		world.setBlockState(new BlockPos(x + 7, y + 7, z + 8), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 7, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 7, y + 7, z + 10);
		genStructure.setHullGlass(world, x + 7, y + 7, z + 11);
		genStructure.setHullGlass(world, x + 7, y + 7, z + 12);
		genStructure.setHullGlass(world, x + 7, y + 7, z + 13);
		genStructure.setHullGlass(world, x + 7, y + 8, z + 2);
		genStructure.setHullPlain(world, x + 7, y + 8, z + 5);
		genStructure.setHullPlain(world, x + 7, y + 8, z + 8);
		genStructure.setHullPlain(world, x + 8, y + 0, z + 6);
		genStructure.setHullPlain(world, x + 8, y + 0, z + 7);
		genStructure.setHullPlain(world, x + 8, y + 0, z + 9);
		genStructure.setHullPlain(world, x + 8, y + 0, z + 10);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 3);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 11);
		genStructure.setHullPlain(world, x + 8, y + 1, z + 12);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 2);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 12);
		genStructure.setHullPlain(world, x + 8, y + 2, z + 13);
		genStructure.setHullPlain(world, x + 8, y + 3, z + 1);
		genStructure.setHullPlain(world, x + 8, y + 3, z + 14);
		genStructure.setHullGlass(world, x + 8, y + 4, z + 1);
		genStructure.setHullGlass(world, x + 8, y + 4, z + 14);
		genStructure.setHullGlass(world, x + 8, y + 5, z + 1);
		genStructure.setHullGlass(world, x + 8, y + 5, z + 14);
		genStructure.setHullPlain(world, x + 8, y + 6, z + 1);
		world.setBlockState(new BlockPos(x + 8, y + 6, z + 2), Blocks.PLANKS.getDefaultState());
		genStructure.setHullPlain(world, x + 8, y + 6, z + 14);
		genStructure.setHullGlass(world, x + 8, y + 7, z + 1);
		world.setBlockState(new BlockPos(x + 8, y + 7, z + 2), Blocks.LAPIS_BLOCK.getDefaultState());
		genStructure.setHullPlain(world, x + 8, y + 7, z + 3);
		genStructure.setHullPlain(world, x + 8, y + 7, z + 4);
		world.setBlockState(new BlockPos(x + 8, y + 7, z + 5), Blocks.GLOWSTONE.getDefaultState());
		world.setBlockState(new BlockPos(x + 8, y + 7, z + 6), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 8, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 8, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 8, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 8, y + 7, z + 10);
		genStructure.setHullPlain(world, x + 8, y + 7, z + 11);
		genStructure.setHullGlass(world, x + 8, y + 7, z + 12);
		genStructure.setHullGlass(world, x + 8, y + 7, z + 13);
		genStructure.setHullGlass(world, x + 8, y + 8, z + 2);
		genStructure.setHullPlain(world, x + 8, y + 8, z + 5);
		genStructure.setHullPlain(world, x + 8, y + 8, z + 6);
		genStructure.setHullPlain(world, x + 9, y + 0, z + 5);
		genStructure.setHullPlain(world, x + 9, y + 0, z + 6);
		genStructure.setHullPlain(world, x + 9, y + 0, z + 10);
		genStructure.setHullPlain(world, x + 9, y + 0, z + 11);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 3);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 11);
		genStructure.setHullPlain(world, x + 9, y + 1, z + 12);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 2);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 12);
		genStructure.setHullPlain(world, x + 9, y + 2, z + 13);
		genStructure.setHullPlain(world, x + 9, y + 3, z + 1);
		world.setBlockState(new BlockPos(x + 9, y + 3, z + 13), Blocks.CHEST.getStateFromMeta(2), 0);
		genStructure.setHullPlain(world, x + 9, y + 3, z + 14);
		genStructure.setHullGlass(world, x + 9, y + 4, z + 1);
		genStructure.setHullGlass(world, x + 9, y + 4, z + 14);
		genStructure.setHullGlass(world, x + 9, y + 5, z + 1);
		genStructure.setHullGlass(world, x + 9, y + 5, z + 14);
		genStructure.setHullPlain(world, x + 9, y + 6, z + 1);
		world.setBlockState(new BlockPos(x + 9, y + 6, z + 2), Blocks.PLANKS.getDefaultState());
		genStructure.setHullPlain(world, x + 9, y + 6, z + 14);
		genStructure.setHullGlass(world, x + 9, y + 7, z + 1);
		world.setBlockState(new BlockPos(x + 9, y + 7, z + 2), Blocks.LAPIS_BLOCK.getDefaultState());
		genStructure.setHullPlain(world, x + 9, y + 7, z + 3);
		world.setBlockState(new BlockPos(x + 9, y + 7, z + 4), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 9, y + 7, z + 5);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 6);
		genStructure.setHullGlass(world, x + 9, y + 7, z + 7);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 9);
		world.setBlockState(new BlockPos(x + 9, y + 7, z + 10), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 9, y + 7, z + 11);
		genStructure.setHullPlain(world, x + 9, y + 7, z + 12);
		genStructure.setHullGlass(world, x + 9, y + 7, z + 13);
		genStructure.setHullGlass(world, x + 9, y + 8, z + 2);
		genStructure.setHullPlain(world, x + 9, y + 8, z + 4);
		genStructure.setHullPlain(world, x + 9, y + 8, z + 10);
		genStructure.setHullPlain(world, x + 10, y + 0, z + 4);
		genStructure.setHullPlain(world, x + 10, y + 0, z + 5);
		genStructure.setHullPlain(world, x + 10, y + 0, z + 11);
		genStructure.setHullPlain(world, x + 10, y + 0, z + 12);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 3);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 11);
		genStructure.setHullPlain(world, x + 10, y + 1, z + 12);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 1);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 12);
		genStructure.setHullPlain(world, x + 10, y + 2, z + 13);
		genStructure.setHullPlain(world, x + 10, y + 3, z + 1);
		world.setBlockState(new BlockPos(x + 10, y + 3, z + 13), Blocks.CHEST.getStateFromMeta(2), 0);
		genStructure.setHullPlain(world, x + 10, y + 3, z + 14);
		genStructure.setHullPlain(world, x + 10, y + 4, z + 1);
		genStructure.setHullPlain(world, x + 10, y + 4, z + 14);
		genStructure.setHullPlain(world, x + 10, y + 5, z + 1);
		world.setBlockState(new BlockPos(x + 10, y + 5, z + 2), Blocks.DISPENSER.getStateFromMeta(3), 0);
		genStructure.setHullPlain(world, x + 10, y + 5, z + 14);
		genStructure.setHullPlain(world, x + 10, y + 6, z + 1);
		world.setBlockState(new BlockPos(x + 10, y + 6, z + 2), Blocks.PLANKS.getDefaultState());
		genStructure.setHullPlain(world, x + 10, y + 6, z + 14);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 1);
		world.setBlockState(new BlockPos(x + 10, y + 7, z + 2), Blocks.LAPIS_BLOCK.getDefaultState());
		world.setBlockState(new BlockPos(x + 10, y + 7, z + 3), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 10, y + 7, z + 4);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 5);
		genStructure.setHullGlass(world, x + 10, y + 7, z + 6);
		genStructure.setHullGlass(world, x + 10, y + 7, z + 7);
		genStructure.setHullGlass(world, x + 10, y + 7, z + 8);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 10);
		world.setBlockState(new BlockPos(x + 10, y + 7, z + 11), Blocks.GLOWSTONE.getDefaultState());
		genStructure.setHullPlain(world, x + 10, y + 7, z + 12);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 13);
		genStructure.setHullPlain(world, x + 10, y + 7, z + 14);
		genStructure.setHullGlass(world, x + 10, y + 8, z + 2);
		genStructure.setHullPlain(world, x + 10, y + 8, z + 3);
		genStructure.setHullPlain(world, x + 10, y + 8, z + 11);
		genStructure.setHullPlain(world, x + 11, y + 0, z + 2);
		genStructure.setHullPlain(world, x + 11, y + 0, z + 3);
		genStructure.setHullPlain(world, x + 11, y + 0, z + 4);
		genStructure.setHullPlain(world, x + 11, y + 0, z + 12);
		genStructure.setHullPlain(world, x + 11, y + 0, z + 13);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 2);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 3);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 4);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 5);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 6);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 7);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 8);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 9);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 10);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 11);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 12);
		genStructure.setHullPlain(world, x + 11, y + 1, z + 13);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 2);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 6);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 7);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 8);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 12);
		genStructure.setHullPlain(world, x + 11, y + 2, z + 13);
		genStructure.setHullPlain(world, x + 11, y + 3, z + 2);
		genStructure.setHullPlain(world, x + 11, y + 3, z + 13);
		genStructure.setHullPlain(world, x + 11, y + 4, z + 2);
		genStructure.setHullPlain(world, x + 11, y + 4, z + 13);
		genStructure.setHullPlain(world, x + 11, y + 5, z + 2);
		genStructure.setHullPlain(world, x + 11, y + 5, z + 13);
		genStructure.setHullPlain(world, x + 11, y + 6, z + 2);
		genStructure.setHullPlain(world, x + 11, y + 6, z + 13);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 2);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 3);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 4);
		genStructure.setHullGlass(world, x + 11, y + 7, z + 5);
		genStructure.setHullGlass(world, x + 11, y + 7, z + 6);
		genStructure.setHullGlass(world, x + 11, y + 7, z + 7);
		genStructure.setHullGlass(world, x + 11, y + 7, z + 8);
		genStructure.setHullGlass(world, x + 11, y + 7, z + 9);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 10);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 11);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 12);
		genStructure.setHullPlain(world, x + 11, y + 7, z + 13);
		genStructure.setHullPlain(world, x + 12, y + 0, z + 3);
		genStructure.setHullPlain(world, x + 12, y + 0, z + 12);
		genStructure.setHullPlain(world, x + 12, y + 1, z + 3);
		genStructure.setHullPlain(world, x + 12, y + 1, z + 12);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 3);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 4);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 5);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 6);
		world.setBlockState(new BlockPos(x + 12, y + 2, z + 7), Blocks.DIRT.getDefaultState());
		world.setBlockState(new BlockPos(x + 12, y + 2, z + 8), Blocks.DIRT.getDefaultState());
		genStructure.setHullPlain(world, x + 12, y + 2, z + 9);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 10);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 11);
		genStructure.setHullPlain(world, x + 12, y + 2, z + 12);
		genStructure.setHullPlain(world, x + 12, y + 3, z + 3);
		genStructure.setHullPlain(world, x + 12, y + 3, z + 4);
		genStructure.setHullPlain(world, x + 12, y + 3, z + 5);
		genStructure.setHullPlain(world, x + 12, y + 3, z + 10);
		genStructure.setHullPlain(world, x + 12, y + 3, z + 11);
		genStructure.setHullPlain(world, x + 12, y + 3, z + 12);
		genStructure.setHullPlain(world, x + 12, y + 4, z + 3);
		genStructure.setHullPlain(world, x + 12, y + 4, z + 4);
		genStructure.setHullPlain(world, x + 12, y + 4, z + 5);
		genStructure.setHullPlain(world, x + 12, y + 4, z + 10);
		genStructure.setHullPlain(world, x + 12, y + 4, z + 11);
		genStructure.setHullPlain(world, x + 12, y + 4, z + 12);
		genStructure.setHullPlain(world, x + 12, y + 5, z + 3);
		genStructure.setHullGlass(world, x + 12, y + 5, z + 4);
		genStructure.setHullPlain(world, x + 12, y + 5, z + 5);
		genStructure.setHullPlain(world, x + 12, y + 5, z + 6);
		genStructure.setHullPlain(world, x + 12, y + 5, z + 9);
		genStructure.setHullPlain(world, x + 12, y + 5, z + 10);
		genStructure.setHullGlass(world, x + 12, y + 5, z + 11);
		genStructure.setHullPlain(world, x + 12, y + 5, z + 12);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 3);
		genStructure.setHullGlass(world, x + 12, y + 6, z + 4);
		genStructure.setHullGlass(world, x + 12, y + 6, z + 5);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 6);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 7);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 8);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 9);
		genStructure.setHullGlass(world, x + 12, y + 6, z + 10);
		genStructure.setHullGlass(world, x + 12, y + 6, z + 11);
		genStructure.setHullPlain(world, x + 12, y + 6, z + 12);
		genStructure.setHullPlain(world, x + 12, y + 7, z + 3);
		genStructure.setHullPlain(world, x + 12, y + 7, z + 12);
		return true;
	}
}
