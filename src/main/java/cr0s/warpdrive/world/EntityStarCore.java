package cr0s.warpdrive.world;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;

public final class EntityStarCore extends Entity {
	public int xCoord;
	public int yCoord;
	public int zCoord;
	
	private int radius;
	
	private final int KILL_RADIUS = 60;
	private final int BURN_RADIUS = 200;
	//private final int ROCKET_INTERCEPT_RADIUS = 100; //disabled
	private boolean isLogged = false;
	
	private final int ENTITY_ACTION_INTERVAL = 10; // ticks
	
	private int ticks = 0;
	
	public EntityStarCore(World world) {
		super(world);
	}
	
	public EntityStarCore(World world, int x, int y, int z, int radius) {
		super(world);
		this.xCoord = x;
		this.posX = x;
		this.yCoord = y;
		this.posY = y;
		this.zCoord = z;
		this.posZ = z;
		this.radius = radius;
	}
	
	private void actionToEntitiesNearStar() {
		int xMax, yMax, zMax;
		int xMin, yMin, zMin;
		final int MAX_RANGE = radius + KILL_RADIUS + BURN_RADIUS;// + ROCKET_INTERCEPT_RADIUS;
		final int KILL_RANGESQ = (radius + KILL_RADIUS) * (radius + KILL_RADIUS);
		final int BURN_RANGESQ = (radius + KILL_RADIUS + BURN_RADIUS) * (radius + KILL_RADIUS + BURN_RADIUS);
		xMin = xCoord - MAX_RANGE;
		xMax = xCoord + MAX_RANGE;
		
		zMin = zCoord - MAX_RANGE;
		zMax = zCoord + MAX_RANGE;
		
		yMin = yCoord - MAX_RANGE;
		yMax = yCoord + MAX_RANGE;
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(xMin, yMin, zMin, xMax, yMax, zMax);
		List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, aabb);
		
		if (!isLogged) {
			isLogged = true;
			WarpDrive.logger.info(this + " Capture range " + MAX_RANGE + " X " + xMin + " to " + xMax + " Y " + yMin + " to " + yMax + " Z " + zMin + " to " + zMax);
		}
		for (Object object : list) {
			if (!(object instanceof Entity)) {
				continue;
			}
			
			if (object instanceof EntityLivingBase) {
				EntityLivingBase entity = (EntityLivingBase) object;
				
				//System.out.println("Found: " + entity.getEntityName() + " distance: " + entity.getDistanceToEntity(this));
				
				// creative bypass
				if (entity.invulnerable) {
					continue;
				}
				if (entity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) entity;
					if (player.capabilities.isCreativeMode) {
						continue;
					}
				}
				
				double distanceSq = entity.getDistanceSqToEntity(this);
				if (distanceSq <= KILL_RANGESQ) {
					// 100% kill, ignores any protection
					entity.attackEntityFrom(DamageSource.onFire, 9000);
					entity.attackEntityFrom(DamageSource.generic, 9000);
					if (!entity.isDead) {
						WarpDrive.logger.warn("Forcing entity death due to star proximity: " + entity);
						entity.setDead();
					}
				} else if (distanceSq <= BURN_RANGESQ) {
					// burn entity
					if (!entity.isImmuneToFire()) {
						entity.setFire(3);
					}
					entity.attackEntityFrom(DamageSource.onFire, 1);
				}
			}/* else { // Intercept ICBM rocket and kill

				   Entity entity = (Entity) o;
				   if (entity.getDistanceToEntity(this) <= (this.radius + ROCKET_INTERCEPT_RADIUS)) {
				       System.out.println("[SC] Intercepted entity: " + entity.getEntityName());
				       worldObj.removeEntity(entity);
				   }
				}*/
		}
	}
	
	public void killEntity() {
		worldObj.removeEntity(this);
	}
	
	@Override
	public void onUpdate() {
		if (worldObj.isRemote) {
			return;
		}
		
		if (++ticks > ENTITY_ACTION_INTERVAL) {
			ticks = 0;
			actionToEntitiesNearStar();
		}
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		xCoord = nbttagcompound.getInteger("x");
		yCoord = nbttagcompound.getInteger("y");
		zCoord = nbttagcompound.getInteger("z");
		radius = nbttagcompound.getInteger("radius");
	}
	
	@Override
	protected void entityInit() {
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("x", xCoord);
		nbttagcompound.setInteger("y", yCoord);
		nbttagcompound.setInteger("z", zCoord);
		nbttagcompound.setInteger("radius", radius);
	}
	
	@Override
	public boolean shouldRenderInPass(int pass) {
		return false;
	}
}