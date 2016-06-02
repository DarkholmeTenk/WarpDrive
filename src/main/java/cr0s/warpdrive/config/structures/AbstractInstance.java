/**
 *
 */
package cr0s.warpdrive.config.structures;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * @author LemADEC
 *
 */
public abstract class AbstractInstance extends WorldGenerator {
	protected AbstractStructure structure;
	protected HashMap<String,Double> variables = new HashMap<>();
	
	public AbstractInstance(AbstractStructure structure, Random random) {
		this.structure = structure;
		
		// evaluate variables
		for (Entry<String, String> entry : structure.variables.entrySet()) {
			double value;
			String stringValue = entry.getValue();
			try {
				if (stringValue.contains(",")) {
					String[] values = stringValue.split(",");
					stringValue = values[random.nextInt(values.length)];
				}
				value = Double.parseDouble(entry.getValue());
			} catch (NumberFormatException exception) {
				throw new RuntimeException("Invalid expression '" + entry.getValue() + "'"
						+ (stringValue.equalsIgnoreCase(entry.getValue()) ? "" : " in '" + entry.getValue() + "'")
						+ " for variable " + entry.getKey()
						+ " in deployable structure " + structure.name
						+ ": a numeric value is expected. Check the related XML configuration file...");
			}
			
			variables.put(entry.getKey(), value);
		}
	}
	
	protected static int randomRange(Random random, final int min, final int max) {
		return min + ((max - min > 0) ? random.nextInt(max - min + 1) : 0); 
	}
	
	protected static double randomRange(Random random, final double min, final double max) {
		return min + ((max - min > 0) ? random.nextDouble() * (max - min) : 0); 
	}
	
	protected String evaluate(final String valueOrExpression) {
		if (!valueOrExpression.contains("%")) {
			return valueOrExpression;
		}
		String result = valueOrExpression;
		for (Entry<String, Double> variable : variables.entrySet()) {
			result = result.replaceAll(variable.getKey(), "" + variable.getValue());
		}
		return result;
	}
	
	public AbstractInstance(NBTTagCompound tag) {
		// FIXME to be implemented
		
		// get deployable
		// String deployableName = tag.getString("wd_structureName");
		
		// get variables values
		/*
		NBTTagCompound tagVariables = tag.getCompoundTag("wd_variables");
		NBTTagList names = tagVariables.getTagList("x", 0);
		for (Entry<String, Double> entry : tagVariables.getTagList("x", 0)) {
			tagVariables.setDouble(entry.getKey(), entry.getValue());
		}
		/**/
	}
	
	public void WriteToNBT(NBTTagCompound tag) {
		tag.setString("wd_structureGroup", structure.group);
		tag.setString("wd_structureName", structure.name);
		NBTTagCompound tagVariables = new NBTTagCompound();
		for (Entry<String, Double> entry : variables.entrySet()) {
			tagVariables.setDouble(entry.getKey(), entry.getValue());
		}
		tag.setTag("wd_variables", tagVariables);
	}
}
