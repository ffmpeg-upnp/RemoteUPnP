package com.wonyoung.remoteupnp;

import java.util.ArrayList;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.avtransport.impl.AVTransportService;
import org.fourthline.cling.support.avtransport.impl.AVTransportStateMachine;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;
import org.fourthline.cling.support.lastchange.LastChangeParser;
import org.seamless.statemachine.States;

import com.wonyoung.remoteupnp.renderer.SimpleRendererNoMediaPresent;
import com.wonyoung.remoteupnp.renderer.SimpleRendererPlaying;
import com.wonyoung.remoteupnp.renderer.SimpleRendererStopped;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

@States({
    SimpleRendererNoMediaPresent.class,
    SimpleRendererStopped.class,
    SimpleRendererPlaying.class
})
interface SimpleRendererStateMachine extends AVTransportStateMachine {}
/**
 * Created by wonyoungjang on 2013. 11. 18..
 */
public class MyUPnPService implements UPnPService
{

	private OnRendererChangeListener rendererChangeListener;

	public void setOnRendererChangeListener(OnRendererChangeListener listener)
	{
		rendererChangeListener = listener;
		// TODO: Implement this method
	}


	private PlaylistAdapter playlistAdapter
	 = new PlaylistAdapter();

	public PlaylistAdapter getPlaylistAdapter()
	{
		return playlistAdapter;
	}
	
    private AndroidUpnpService upnpService;

    private BrowseRegistryListener registryListener = new BrowseRegistryListener();
    private ArrayList<Device> devices = new ArrayList<Device>();

    private Device mediaServerDevice;
    private Device renderer;

    
    
    private boolean bounded = false;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("bind", "Bounded connection - " + this );
            upnpService = (AndroidUpnpService) service;

            addLocalRenderer();
            
            // Clear the list
            devices.clear();

            // Get ready for future device advertisements
            upnpService.getRegistry().addListener(registryListener);

            // Now add all devices to the list we already know about
            for (Device device : upnpService.getRegistry().getDevices()) {
                registryListener.deviceAdded(device);
            }

            // Search asynchronously for all devices, they will respond soon
            upnpService.getControlPoint().search();
            bounded = true;
        }


        @Override
        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
            bounded = false;
        }
    };
    private ArrayList<DeviceSubscriber> listeners = new ArrayList<DeviceSubscriber>();
    private OnMediaServerChangeListener mediaServerChangeListener;

    private void addLocalRenderer() {
        LocalService<AVTransportService> service =
                new AnnotationLocalServiceBinder().read(AVTransportService.class);

        // Service's which have "logical" instances are very special, they use the
        // "LastChange" mechanism for eventing. This requires some extra wrappers.
        LastChangeParser lastChangeParser = new AVTransportLastChangeParser();

        service.setManager(
                new LastChangeAwareServiceManager<AVTransportService>(service, lastChangeParser) {
                    @Override
                    protected AVTransportService createServiceInstance() throws Exception {
                        return new AVTransportService(
                                SimpleRendererStateMachine.class,   // All states
                                SimpleRendererNoMediaPresent.class  // Initial state
                        );
                    }
                }
        );
    }
    
    @Override
    public void bind(FragmentActivity context) {
    }

    @Override
    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    @Override
    public Device getMediaDevice() {
        return mediaServerDevice;
    }

    @Override
    public Device getRendererDevice() {
        return renderer;
    }

    @Override
    public void setOnMediaServerChangeListener(OnMediaServerChangeListener listener) {
        mediaServerChangeListener = listener;
    }

    @Override
    public void setMediaDevice(Device device) {
		if (mediaServerDevice != device) {
			applyMediaServer(device);
		}
    }

	private void applyMediaServer(Device device)
	{
		mediaServerDevice = device;
		if (mediaServerChangeListener != null) {
			mediaServerChangeListener.OnMediaServerChanged(device);
		}
	}

    @Override
    public void setRenderer(Device device) {
		rendererChangeListener.OnRendererChanged(device);
        renderer = device;
    }

    @Override
    public void execute(ActionCallback action) {
        upnpService.getControlPoint().execute(action);
    }

    @Override
    public void execute(SubscriptionCallback callback) {
        upnpService.getControlPoint().execute(callback);
    }
    
    @Override
    public void addListener(DeviceSubscriber listener) {
        listeners.add(listener);
        for (Device device : devices) {
            listener.add(device);
        }
    }

    @Override
    public void unbind() {
        if (upnpService != null) {
            upnpService.getRegistry().removeListener(registryListener);
        }
        if (bounded) {
            Log.d("bind", "try to unbound - " + serviceConnection );
            bounded = false;
        }
    }

    private class BrowseRegistryListener extends DefaultRegistryListener {

        /* Discovery performance optimization for very slow Android devices! */
        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
            deviceRemoved(device);
        }

        /* End of optimization, you can remove the whole block if your Android handset is fast (>= 600 Mhz) */
        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            deviceRemoved(device);
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            deviceAdded(device);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            deviceRemoved(device);
        }

        public void deviceAdded(final Device device) {
            Log.e("remoteUPNP", "device added - " + device.getDisplayString());
            devices.add(device);
            for (DeviceSubscriber listener : listeners) {
                listener.add(device);
            }
        }

        public void deviceRemoved(final Device device) {
            devices.remove(device);
            for (DeviceSubscriber listener : listeners) {
                listener.remove(device);
            }
        }
    }

    @Override
    public void removeListener(DeviceSubscriber listener) {
        listeners.remove(listener);
    }

}
