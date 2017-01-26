package cr0s.warpdrive.core;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class MyDummyModContainer extends DummyModContainer {
	public MyDummyModContainer() {
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "WarpDriveCore";
		meta.name = "WarpDriveCore";
		meta.parent = "WarpDrive";
		meta.version = "@version@";
		meta.credits = "Cr0s";
		meta.authorList = Arrays.asList("LemADEC", "cr0s");
		meta.description = "";
		meta.url = "";
		meta.updateJSON = "";
		meta.screenshots = new String[0];
		meta.logoFile = "";
	}
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}
	
	@Subscribe
	public void modConstruction(FMLConstructionEvent evt) {
	}
	
	@Subscribe
	public void init(FMLInitializationEvent evt) {
	}
	
	@Subscribe
	public void preInit(FMLPreInitializationEvent evt) {
	}
	
	@Subscribe
	public void postInit(FMLPostInitializationEvent evt) {
	}
}
