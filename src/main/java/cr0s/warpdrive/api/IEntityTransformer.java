package cr0s.warpdrive.api;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;

@Deprecated
public interface IEntityTransformer {
	// Return true if this transformer is applicable to that Entity.
	boolean isApplicable(Entity entity);
	
	// Called when preparing to save a ship structure.
	// Use this to prevent jump during critical events/animations.
	boolean isJumpReady(Entity entity);
	
	// Called when saving a ship structure.
	// Use this to save external data in the ship schematic.
	// You don't need to save entity data here, it's already covered.
	// Warning: do NOT assume that the ship will be removed!
	NBTBase saveExternals(Entity entity);
	
	// Called when removing the original ship structure.
	// Use this to prevents drops, clear energy networks, etc.
	// Entity will be removed right after this call. 
	void remove(Entity entity);
	
	// Called when restoring a ship in the world.
	// Use this to apply entity rotation, right before entity placement.
	// Warning: do NOT spawn the entity!
	short rotate(NBTBase nbtEntity, final byte rotationSteps, final float rotationYaw);
	
	// Called when placing back a ship in the world.
	// Use this to restore external data from the ship schematic, right after entity placement.
	// Use priority placement to ensure dependent blocks are placed first.
	void restoreExternals(Entity entity, ITransformation transformation, NBTBase nbtBase);
}