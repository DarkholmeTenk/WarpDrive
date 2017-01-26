package cr0s.warpdrive.item;

import cr0s.warpdrive.config.WarpDriveConfig;
import net.minecraftforge.fml.common.Optional;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cr0s.warpdrive.WarpDrive;

@Optional.InterfaceList({
	@Optional.Interface(iface = "ic2.api.reactor.IReactorComponent", modid = "IC2")
	})
public class ItemIC2reactorLaserFocus extends ItemAbstractBase implements IReactorComponent {
	private final static int maxHeat = 3000;
	
	public ItemIC2reactorLaserFocus(final String registryName) {
		super(registryName);
		setMaxDamage(maxHeat);
		setUnlocalizedName("warpdrive.energy.IC2reactorLaserFocus");
	}
	
	private static void damageComponent(ItemStack self, int damage) {
		int currDamage = self.getItemDamage();
		int nextDamage = Math.min(maxHeat, Math.max(0, currDamage + damage));
		self.setItemDamage(nextDamage);
	}
	
	private static void balanceComponent(ItemStack self, ItemStack other) {
		final int selfBalance = 4;
		int otherDamage = other.getItemDamage();
		int myDamage = self.getItemDamage();
		int newOne = (otherDamage + (selfBalance - 1) * myDamage) / selfBalance;
		int newTwo = otherDamage - (newOne - myDamage);
		self.setItemDamage(newTwo);
		other.setItemDamage(newOne);
	}
	
	@Optional.Method(modid = "IC2")
	private static void coolComponent(ItemStack self, IReactorComponent comp, IReactor reactor, ItemStack stack, int x, int y) {
		int maxTransfer = maxHeat - self.getItemDamage();
		int compHeat = comp.getCurrentHeat(stack, reactor, x, y);
		int transferHeat = -Math.min(compHeat, maxTransfer);
		int retained = comp.alterHeat(stack, reactor, x, y, transferHeat);
		damageComponent(self, retained - transferHeat);
	}
	
	@Optional.Method(modid = "IC2")
	private static void coolReactor(IReactor reactor, ItemStack stack) {
		int reactorHeat = reactor.getHeat();
		int myHeat = stack.getItemDamage();
		int transfer = Math.min(maxHeat - myHeat, reactorHeat);
		reactor.addHeat(-transfer);
		damageComponent(stack, transfer);
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public void processChamber(ItemStack yourStack, IReactor reactor, int x, int y, boolean heatrun) {
		if (heatrun) {
			int[] xDif = { -1, 0, 0, 1 };
			int[] yDif = { 0, -1, 1, 0 };
			for (int i = 0; i < xDif.length; i++) {
				int iX = x + xDif[i];
				int iY = y + yDif[i];
				ItemStack stack = reactor.getItemAt(iX, iY);
				if (stack != null) {
					Item item = stack.getItem();
					if (item instanceof ItemIC2reactorLaserFocus) {
						balanceComponent(yourStack, stack);
					} else if (item instanceof IReactorComponent) {
						coolComponent(yourStack, (IReactorComponent) item, reactor, stack, iX, iY);
					}
				}
			}
			
			coolReactor(reactor, yourStack);
		}
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public boolean acceptUraniumPulse(ItemStack yourStack, IReactor reactor, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatrun) {
		return false;
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public boolean canStoreHeat(ItemStack yourStack, IReactor reactor, int x, int y) {
		return true;
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public int getMaxHeat(ItemStack yourStack, IReactor reactor, int x, int y) {
		return maxHeat;
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public int getCurrentHeat(ItemStack yourStack, IReactor reactor, int x, int y) {
		return yourStack.getItemDamage();
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public int alterHeat(ItemStack yourStack, IReactor reactor, int x, int y, int heat) {
		if (WarpDriveConfig.LOGGING_ENERGY) {
			WarpDrive.logger.info(this + " alterHeat " + heat);
		}
		int transferred = Math.min(heat, maxHeat - yourStack.getItemDamage());
		damageComponent(yourStack, transferred);
		return heat - transferred;
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public float influenceExplosion(ItemStack yourStack, IReactor reactor) {
		return 0;
	}
	
	@Override
	public boolean canBePlacedIn(ItemStack stack, IReactor reactor) {
		return true;
	}
}
