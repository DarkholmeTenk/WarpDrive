package cr0s.warpdrive.event;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.nbt.NBTTagCompound;

public abstract class AbstractSequencer {
	
	private static AtomicBoolean isUpdating = new AtomicBoolean(false);
	private static ConcurrentHashMap<AbstractSequencer, Boolean> sequencers = new ConcurrentHashMap<>(10);
	
	public static void updateTick() {
		if (sequencers.isEmpty()) {
			return;
		}
		while (!isUpdating.compareAndSet(false, true)) {
			Thread.yield();
		}
		for(Iterator<Entry<AbstractSequencer, Boolean>> iterator = sequencers.entrySet().iterator(); iterator.hasNext(); ) {
			Entry<AbstractSequencer, Boolean> entry = iterator.next();
			boolean doContinue = entry.getKey().onUpdate();
			if (!doContinue) {
				iterator.remove();
			}
		}
		isUpdating.set(false);
	}
	
	protected void register() {
		while (!isUpdating.compareAndSet(false, true)) {
			Thread.yield();
		}
		sequencers.put(this, true);
		isUpdating.set(false);
	}
	
	protected void unregister() {
		sequencers.put(this, false);
	}
	
	abstract public boolean onUpdate();

	abstract protected void readFromNBT(NBTTagCompound nbttagcompound);

	abstract protected void writeToNBT(NBTTagCompound nbttagcompound);
	
}
