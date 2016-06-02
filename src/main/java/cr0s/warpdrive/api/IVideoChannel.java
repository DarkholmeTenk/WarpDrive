package cr0s.warpdrive.api;

public interface IVideoChannel {
	// get video channel, return -1 if invalid 
	public abstract int getVideoChannel();
	
	// sets video channel
	public abstract void setVideoChannel(int videoChannel);
}
