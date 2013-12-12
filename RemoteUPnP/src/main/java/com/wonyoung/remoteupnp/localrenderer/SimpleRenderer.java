package com.wonyoung.remoteupnp.localrenderer;


import java.util.*;
import org.fourthline.cling.*;
import org.fourthline.cling.binding.annotations.*;
import org.fourthline.cling.model.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.*;
import org.fourthline.cling.support.avtransport.impl.*;
import org.fourthline.cling.support.avtransport.lastchange.*;
import org.fourthline.cling.support.connectionmanager.*;
import org.fourthline.cling.support.lastchange.*;
import org.seamless.statemachine.*;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.android.*;

@States({
        SimpleRendererNoMediaPresent.class,
        SimpleRendererStopped.class,
        SimpleRendererPlaying.class
	})
interface SimpleRendererStateMachine extends AVTransportStateMachine {
}


public class SimpleRenderer implements Runnable
{
	public void run() {
        try {

	//		final UpnpService upnpService = new AndroidUpnpServiceImpl();
/*
            Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						upnpService.shutdown();
					}
				});
*/
            // Add the bound local device to the registry
       //     upnpService.getRegistry().addDevice(
	//			createDevice()
     //       );

        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
        }
    }
	
	public LocalDevice createDevice() {

        LocalService<AVTransportService> service =
			new AnnotationLocalServiceBinder().read(AVTransportService.class);

        // Service's which have "logical" instances are very special, they use the
        // "LastChange" mechanism for eventing. This requires some extra wrappers.
        LastChangeParser lastChangeParser = new AVTransportLastChangeParser();

        service.setManager(
			new LastChangeAwareServiceManager<AVTransportService>(service, null) {
				@Override
				protected AVTransportService createServiceInstance() throws Exception {
					return new AVTransportService(
						SimpleRendererStateMachine.class,   // All states
						SimpleRendererNoMediaPresent.class  // Initial state
					);
				}
			}
        );

        LocalService connectionManagerService = new AnnotationLocalServiceBinder().read(ConnectionManagerService.class);
        connectionManagerService.setManager(
			new DefaultServiceManager(connectionManagerService) {
				@Override
                protected Object createServiceInstance() throws  Exception {
					return new ConnectionManagerService();
				}
			}
        );
		

        LocalService renderControlService = new AnnotationLocalServiceBinder().read(SimpleRenderingControlService.class);
        renderControlService.setManager(
			new DefaultServiceManager(connectionManagerService) {
				@Override
                protected Object createServiceInstance() throws  Exception {
					return new SimpleRenderingControlService();
				}
			}
        );

		

        LocalDevice device = null;
        try {
            device = new LocalDevice(
				new DeviceIdentity(new UDN(UUID.randomUUID())),
				new UDADeviceType("MediaRenderer", 1),
				new DeviceDetails(
					"MediaRenderer on ...",
					new ManufacturerDetails("RemoteUPnp", "http://none"),
					new ModelDetails("RemoteUpnp mediarenderer", "REMOTEUPNP", "1", "http")
				),
				new Icon[]{createDefaultDeviceIcon()},
				new LocalService[]{
					service,
					connectionManagerService,
					renderControlService
				}
            );
        } catch (ValidationException e) {
            e.printStackTrace();
        }
		return device;
    }

    private Icon createDefaultDeviceIcon() {
        byte[] v = new byte[] {0,0,0};

		return new Icon("image/png",
						48, 48, 8,
						"icon.png",
						v);
    }
	
}
