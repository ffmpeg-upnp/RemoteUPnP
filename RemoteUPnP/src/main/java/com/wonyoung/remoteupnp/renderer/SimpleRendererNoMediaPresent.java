package com.wonyoung.remoteupnp.renderer;

import java.net.*;
import org.fourthline.cling.support.avtransport.impl.state.*;
import org.fourthline.cling.support.avtransport.lastchange.*;
import org.fourthline.cling.support.model.*;

public class SimpleRendererNoMediaPresent extends NoMediaPresent
 {

    public SimpleRendererNoMediaPresent(AVTransport transport) {
        super(transport);
    }

    @Override
    public Class<? extends AbstractState> setTransportURI(URI uri, String metaData) {

        getTransport().setMediaInfo(
			new MediaInfo(uri.toString(), metaData)
        );

        // If you can, you should find and set the duration of the track here!
        getTransport().setPositionInfo(
			new PositionInfo(1, metaData, uri.toString())
        );

        // It's up to you what "last changes" you want to announce to event listeners
        getTransport().getLastChange().setEventedValue(
			getTransport().getInstanceId(),
			new AVTransportVariable.AVTransportURI(uri),
			new AVTransportVariable.CurrentTrackURI(uri)
        );

        return SimpleRendererStopped.class;
    }
}
