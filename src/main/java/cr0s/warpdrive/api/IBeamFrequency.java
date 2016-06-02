package cr0s.warpdrive.api;

import cr0s.warpdrive.data.Vector3;

public interface IBeamFrequency {
	int BEAM_FREQUENCY_SCANNING = 1420;
	int BEAM_FREQUENCY_MAX = 65000;
	
	// get beam frequency, return -1 if invalid 
	int getBeamFrequency();
	
	// sets beam frequency
	void setBeamFrequency(final int beamFrequency);
	
	static Vector3 getBeamColor(final int beamFrequency) {
		float r, g, b;
		if (beamFrequency <= 0) { // invalid frequency
			r = 1.0F;
			g = 0.0F;
			b = 0.0F;
		} else if (beamFrequency <= 10000) { // red
			r = 1.0F;
			g = 0.0F;
			b = 0.0F + 0.5f * beamFrequency / 10000F;
		} else if (beamFrequency <= 20000) { // orange
			r = 1.0F;
			g = 0.0F + 1.0F * (beamFrequency - 10000F) / 10000F;
			b = 0.5F - 0.5F * (beamFrequency - 10000F) / 10000F;
		} else if (beamFrequency <= 30000) { // yellow
			r = 1.0F - 1.0F * (beamFrequency - 20000F) / 10000F;
			g = 1.0F;
			b = 0.0F;
		} else if (beamFrequency <= 40000) { // green
			r = 0.0F;
			g = 1.0F - 1.0F * (beamFrequency - 30000F) / 10000F;
			b = 0.0F + 1.0F * (beamFrequency - 30000F) / 10000F;
		} else if (beamFrequency <= 50000) { // blue
			r = 0.0F + 0.5F * (beamFrequency - 40000F) / 10000F;
			g = 0.0F;
			b = 1.0F - 0.5F * (beamFrequency - 40000F) / 10000F;
		} else if (beamFrequency <= 60000) { // violet
			r = 0.5F + 0.5F * (beamFrequency - 50000F) / 10000F;
			g = 0.0F;
			b = 0.5F - 0.5F * (beamFrequency - 50000F) / 10000F;
		} else if (beamFrequency <= BEAM_FREQUENCY_MAX) { // rainbow
			int component = Math.round(4096F * (beamFrequency - 60000F) / (BEAM_FREQUENCY_MAX - 60000F));
			r = 1.0F - 0.5F * (component & 0xF);
			g = 0.5F + 0.5F * (component >> 4 & 0xF);
			b = 0.5F + 0.5F * (component >> 8 & 0xF);
		} else { // invalid frequency
			r = 1.0F;
			g = 0.0F;
			b = 0.0F;
		}
		return new Vector3(r, g, b);
	}
}
