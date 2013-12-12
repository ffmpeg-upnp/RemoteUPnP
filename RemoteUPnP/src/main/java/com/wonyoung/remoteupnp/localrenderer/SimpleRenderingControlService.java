package com.wonyoung.remoteupnp.localrenderer;
import org.fourthline.cling.support.renderingcontrol.*;
import org.fourthline.cling.model.types.*;
import org.fourthline.cling.support.model.*;

public class SimpleRenderingControlService extends 
AbstractAudioRenderingControl
{

	public UnsignedIntegerFourBytes[] getCurrentInstanceIds()
	{
		// TODO: Implement this method
		return null;
	}

	public boolean getMute(UnsignedIntegerFourBytes p1, String p2) throws RenderingControlException
	{
		// TODO: Implement this method
		return false;
	}

	public void setMute(UnsignedIntegerFourBytes p1, String p2, boolean p3) throws RenderingControlException
	{
		// TODO: Implement this method
	}

	public UnsignedIntegerTwoBytes getVolume(UnsignedIntegerFourBytes p1, String p2) throws RenderingControlException
	{
		// TODO: Implement this method
		return null;
	}

	public void setVolume(UnsignedIntegerFourBytes p1, String p2, UnsignedIntegerTwoBytes p3) throws RenderingControlException
	{
		// TODO: Implement this method
	}

	protected Channel[] getCurrentChannels()
	{
		// TODO: Implement this method
		return null;
	}
	
}
