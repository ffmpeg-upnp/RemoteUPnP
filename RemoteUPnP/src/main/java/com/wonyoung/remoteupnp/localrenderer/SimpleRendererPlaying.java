package com.wonyoung.remoteupnp.localrenderer;

import java.net.*;
import org.fourthline.cling.support.avtransport.impl.state.*;
import org.fourthline.cling.support.model.*;

public class SimpleRendererPlaying extends Playing
{

	public Class play(String p1)
	{
		// TODO: Implement this method
		return null;
	}

	public Class pause()
	{
		// TODO: Implement this method
		return null;
	}

	public Class next()
	{
		// TODO: Implement this method
		return null;
	}

	public Class previous()
	{
		// TODO: Implement this method
		return null;
	}

	public Class seek(SeekMode p1, String p2)
	{
		// TODO: Implement this method
		return null;
	}

    public SimpleRendererPlaying(AVTransport transport) {
        super(transport);
    }

    @Override
    public void onEntry() {
        super.onEntry();
        // Start playing now!
    }

    @Override
    public Class<? extends AbstractState> setTransportURI(URI uri, String metaData) {
        // Your choice of action here, and what the next state is going to be!
        return SimpleRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> stop() {
        // Stop playing!
        return SimpleRendererStopped.class;
    }
}
