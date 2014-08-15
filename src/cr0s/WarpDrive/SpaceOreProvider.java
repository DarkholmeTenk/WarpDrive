package cr0s.WarpDrive;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;

public class SpaceOreProvider
{
	private static Set<SpaceOreProvider> oreList = new HashSet<SpaceOreProvider>();
	private static int totalChance = 0;
	
	private final int ore;
	private final int metaData;
	private final int chance;
	
	SpaceOreProvider(Block o,int c)
	{
		chance		= c;
		ore			= o.blockID;
		metaData	= 0;
	}
	
	SpaceOreProvider(Block o,int m, int c)
	{
		chance   = c;
		ore      = o.blockID;
		metaData = m;
	}
	
	SpaceOreProvider(int o,int m, int c)
	{
		chance = c;
		ore	= o;
		metaData = m;
	}
	
	@Override
	public String toString()
	{
		return "SOP:{" + ore + "," + metaData + "," + chance + "}";
	}
	
	@Override
	public int hashCode()
	{
		return (ore << 4) + metaData;
	}
	
	private boolean equalSOP(SpaceOreProvider sop)
	{
		if(sop == null)
			return false;
		
		return (sop.ore == ore && sop.metaData == metaData);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof SpaceOreProvider)
			return ((SpaceOreProvider)o).equalSOP(this);
		return false;
	}
	
	public static void addOre(Block b, int m, int c)
	{
		addOre(b.blockID,m,c);
	}
	
	public static void addOre(int b, int m, int c)
	{
		SpaceOreProvider sop = new SpaceOreProvider(b,m,c);
		/*if(oreList.contains(sop))
			oreList.remove(sop);
		
		oreList.add(sop);*/
		if(!oreList.contains(sop))
			oreList.add(sop);
		totalChance();
	}
	
	private static int totalChance()
	{
		int tC = 0;
		for(SpaceOreProvider sop:oreList)
		{
			if(sop == null)
				continue;
			tC += sop.chance;
		}
		totalChance = tC;
		return tC;
	}
	
	public static SpaceOreProvider getOre(Random r)
	{
		int value = r.nextInt(totalChance);
		int cV = 0;
		for(SpaceOreProvider sop : oreList)
		{
			if(sop == null)
				continue;
			
			cV += sop.chance;
			if(cV > value)
				return sop;
		}
		return null;
	}
	
	public static int[] getOreOF(Random r)
	{
		SpaceOreProvider tempOre = getOre(r);
		return new int[] { tempOre.ore, tempOre.metaData };
	}
	
	public static Set<Integer[]> oreList()
	{
		Set<Integer[]> tempSet = new HashSet<Integer[]>(oreList.size());
		for(SpaceOreProvider sop:oreList)
		{
			if(sop == null)
				continue;
			tempSet.add(new Integer[] {sop.ore , sop.metaData });
		}
		return tempSet;
	}
	
}
