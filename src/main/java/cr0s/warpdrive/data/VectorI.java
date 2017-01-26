package cr0s.warpdrive.data;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;


/**
 * Generic 3D vector for efficient block manipulation.
 * Loosely based on Mojang Vec3d and Calclavia Vector3. 
 *
 * @author LemADEC
 */
public class VectorI implements Cloneable {
	public int x;
	public int y;
	public int z;
	
	public VectorI() {
		this(0, 0, 0);
	}
	
	// constructor from float/double is voluntarily skipped
	// if you need it, you're probably doing something wrong :)
	
	public VectorI(final int x, final int y, final int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public VectorI(final Entity entity) {
		x = ((int) Math.floor(entity.posX));
		y = ((int) Math.floor(entity.posY));
		z = ((int) Math.floor(entity.posZ));
	}
	
	public VectorI(final TileEntity tileEntity) {
		x = tileEntity.getPos().getX();
		y = tileEntity.getPos().getY();
		z = tileEntity.getPos().getZ();
	}
	
	public VectorI(final RayTraceResult movingObject) {
		x = movingObject.getBlockPos().getX();
		y = movingObject.getBlockPos().getY();
		z = movingObject.getBlockPos().getZ();
	}
	
	public VectorI(final BlockPos blockPos) {
		x = blockPos.getX();
		y = blockPos.getY();
		z = blockPos.getZ();
	}
	
	public VectorI(final EnumFacing direction) {
		x = direction.getFrontOffsetX();
		y = direction.getFrontOffsetY();
		z = direction.getFrontOffsetZ();
	}
	
	
	public Vector3 getBlockCenter() {
		return new Vector3(x + 0.5D, y + 0.5D, z + 0.5D);
	}
	
	
	@Override
	public VectorI clone() {
		return new VectorI(x, y, z);
	}
	
	public VectorI invertedClone() {
		return new VectorI(-x, -y, -z);
	}
	
	// clone in a given direction
	public VectorI clone(final EnumFacing side) {
		return new VectorI(x + side.getFrontOffsetX(), y + side.getFrontOffsetY(), z + side.getFrontOffsetZ());
	}
	
	public IBlockState getBlockState(IBlockAccess world) {
		return world.getBlockState(new BlockPos(x, y, z));
	}
	
	public IBlockState getBlockState_noChunkLoading(IBlockAccess world, EnumFacing side) {
		return getBlockState_noChunkLoading(world, x + side.getFrontOffsetX(), y + side.getFrontOffsetY(), z + side.getFrontOffsetZ());
	}
	
	public IBlockState getBlockState_noChunkLoading(IBlockAccess world) {
		return getBlockState_noChunkLoading(world, x, y, z);
	}
	
	static public IBlockState getBlockState_noChunkLoading(IBlockAccess world, final int x, final int y, final int z) {
		// skip unloaded worlds
		if (world == null) {
			return null;
		}
		if (world instanceof WorldServer) {
			boolean isLoaded;
			if (((WorldServer)world).getChunkProvider() instanceof ChunkProviderServer) {
				ChunkProviderServer chunkProviderServer = ((WorldServer)world).getChunkProvider();
				try {
					Chunk chunk = chunkProviderServer.id2ChunkMap.get(ChunkPos.chunkXZ2Int(x >> 4, z >> 4));
					isLoaded = chunk != null && chunk.isLoaded();
				} catch (NoSuchFieldError exception) {
					isLoaded = chunkProviderServer.chunkExists(x >> 4, z >> 4);
				}
			} else {
				isLoaded = ((WorldServer)world).getChunkProvider().chunkExists(x >> 4, z >> 4);
			}
			// skip unloaded chunks
			if (!isLoaded) {
				return null;
			}
		}
		return world.getBlockState(new BlockPos(x, y, z));
	}
	
	public TileEntity getTileEntity(IBlockAccess world) {
		return world.getTileEntity(new BlockPos(x, y, z));
	}
	
	public void setBlockState(World worldObj, final IBlockState blockState) {
		worldObj.setBlockState(getBlockPos(), blockState, 3);
	}
	
	
	// modify current vector by adding another one
	public VectorI translate(final VectorI vector) {
		x += vector.x;
		y += vector.y;
		z += vector.z;
		return this;
	}

	// modify current vector by subtracting another one
	public VectorI translateBack(final VectorI vector) {
		x -= vector.x;
		y -= vector.y;
		z -= vector.z;
		return this;
	}
	
	// modify current vector by translation of amount block in side direction
	public VectorI translate(final EnumFacing side, final int amount) {
		switch (side) {
		case DOWN:
			y -= amount;
			break;
		case UP:
			y += amount;
			break;
		case NORTH:
			z -= amount;
			break;
		case SOUTH:
			z += amount;
			break;
		case WEST:
			x -= amount;
			break;
		case EAST:
			x += amount;
			break;
		default:
			break;
		}
		
		return this;
	}
	
	// modify current vector by translation of 1 block in side direction
	public VectorI translate(final EnumFacing side) {
		x += side.getFrontOffsetX();
		y += side.getFrontOffsetY();
		z += side.getFrontOffsetZ();
		return this;
	}
	
	// return a new vector adding both parts
	public static VectorI add(final VectorI vector1, final VectorI vector2) {
		return new VectorI(vector1.x + vector2.x, vector1.y + vector2.y, vector1.z + vector2.z);
	}
	
	// return a new vector adding both parts
	public VectorI add(final VectorI vector) {
		return new VectorI(x + vector.x, y + vector.y, z + vector.z);
	}
	
	// return a new vector adding both parts
	@Deprecated
	public VectorI add(final Vector3 vector) {
		x = ((int) (x + Math.round(vector.x)));
		y = ((int) (y + Math.round(vector.y)));
		z = ((int) (z + Math.round(vector.z)));
		return this;
	}
	
	
	// return a new vector subtracting both parts
	public static VectorI subtract(final VectorI vector1, final VectorI vector2) {
		return new VectorI(vector1.x - vector2.x, vector1.y - vector2.y, vector1.z - vector2.z);
	}
	
	// return a new vector subtracting the argument from current vector
	public VectorI subtract(final VectorI vector) {
		return new VectorI(x - vector.x, y - vector.y, z - vector.z);
	}
	
	
	@Deprecated
	public static VectorI set(final Vector3 vector) {
		return new VectorI((int) Math.round(vector.x), (int) Math.round(vector.y), (int) Math.round(vector.z));
	}
	
	@Override
	public int hashCode() {
		return (x + "X" + y + "Y" + z + "lem").hashCode();
	}
	
	public boolean equals(final TileEntity tileEntity) {
		return (x == tileEntity.getPos().getX()) && (y == tileEntity.getPos().getY()) && (z == tileEntity.getPos().getZ());
	}
	
	@Override
	public boolean equals(final Object object) {
		if (object instanceof VectorI) {
			VectorI vector = (VectorI) object;
			return (x == vector.x) && (y == vector.y) && (z == vector.z);
		} else if (object instanceof TileEntity) {
			TileEntity tileEntity = (TileEntity) object;
			return (x == tileEntity.getPos().getX()) && (y == tileEntity.getPos().getY()) && (z == tileEntity.getPos().getZ());
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "VectorI [" + x + "," + y + "," + z + "]";
	}
	
	
	public static VectorI readFromNBT(NBTTagCompound nbtCompound) {
		VectorI vector = new VectorI();
		vector.x = nbtCompound.getInteger("x");
		vector.y = nbtCompound.getInteger("y");
		vector.z = nbtCompound.getInteger("z");
		return vector;
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound nbtCompound) {
		nbtCompound.setInteger("x", x);
		nbtCompound.setInteger("y", y);
		nbtCompound.setInteger("z", z);
		return nbtCompound;
	}
	
	// Square roots are evil, avoid them at all cost
	@Deprecated
	public double distanceTo(final VectorI vector) {
		int newX = vector.x - x;
		int newY = vector.y - y;
		int newZ = vector.z - z;
		return Math.sqrt(newX * newX + newY * newY + newZ * newZ);
	}
	
	public int distance2To(final VectorI vector) {
		int newX = vector.x - x;
		int newY = vector.y - y;
		int newZ = vector.z - z;
		return (newX * newX + newY * newY + newZ * newZ);
	}
	
	public int distance2To(final Entity entity) {
		int newX = (int) (Math.round(entity.posX)) - x;
		int newY = (int) (Math.round(entity.posY)) - y;
		int newZ = (int) (Math.round(entity.posZ)) - z;
		return (newX * newX + newY * newY + newZ * newZ);
	}
	
	public int distance2To(final TileEntity tileEntity) {
		int newX = tileEntity.getPos().getX() - x;
		int newY = tileEntity.getPos().getY() - y;
		int newZ = tileEntity.getPos().getZ() - z;
		return (newX * newX + newY * newY + newZ * newZ);
	}
	
	static public int distance2To(final VectorI vector1, final VectorI vector2) {
		int newX = vector1.x - vector2.x;
		int newY = vector1.y - vector2.y;
		int newZ = vector1.z - vector2.z;
		return (newX * newX + newY * newY + newZ * newZ);
	}
	
	// Square roots are evil, avoid them at all cost
	@Deprecated
	public double getMagnitude() {
		return Math.sqrt(getMagnitudeSquared());
	}
	
	public int getMagnitudeSquared() {
		return x * x + y * y + z * z;
	}
	
	public VectorI scale(final float amount) {
		x = Math.round(x * amount);
		y = Math.round(y * amount);
		z = Math.round(z * amount);
		return this;
	}
	
	public void rotateByAngle(final double yaw, final double pitch) {
		rotateByAngle(yaw, pitch, 0.0D);
	}
	
	public void rotateByAngle(final double yaw, final double pitch, final double roll) {
		double yawRadians = Math.toRadians(yaw);
		double yawCosinus = Math.cos(yawRadians);
		double yawSinus = Math.sin(yawRadians);
		double pitchRadians = Math.toRadians(pitch);
		double pitchCosinus = Math.cos(pitchRadians);
		double pitchSinus = Math.sin(pitchRadians);
		double rollRadians = Math.toRadians(roll);
		double rollCosinus = Math.cos(rollRadians);
		double rollSinus = Math.sin(rollRadians);
		
		double oldX = x;
		double oldY = y;
		double oldZ = z;
		
		x = (int)Math.round(( oldX * yawCosinus * pitchCosinus
			+ oldZ * (yawCosinus * pitchSinus * rollSinus - yawSinus * rollCosinus)
			+ oldY * (yawCosinus * pitchSinus * rollCosinus + yawSinus * rollSinus)));
		
		z = (int)Math.round(( oldX * yawSinus * pitchCosinus
			+ oldZ * (yawSinus * pitchSinus * rollSinus + yawCosinus * rollCosinus)
			+ oldY * (yawSinus * pitchSinus * rollCosinus - yawCosinus * rollSinus)));
		
		y = (int)Math.round((-oldX * pitchSinus + oldZ * pitchCosinus * rollSinus
			+ oldY * pitchCosinus * rollCosinus));
	}

	public BlockPos getBlockPos() {
		return new BlockPos(x, y, z);
	}
}