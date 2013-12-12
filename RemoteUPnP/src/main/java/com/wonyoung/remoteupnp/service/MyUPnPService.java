package com.wonyoung.remoteupnp.service;

import java.util.ArrayList;
import java.util.UUID;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.avtransport.impl.AVTransportService;
import org.fourthline.cling.support.avtransport.impl.AVTransportStateMachine;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;
import org.fourthline.cling.support.lastchange.LastChangeParser;
import org.seamless.statemachine.States;

import com.wonyoung.remoteupnp.mediaserver.FolderSubscriber;
import com.wonyoung.remoteupnp.mediaserver.MediaServer;
import com.wonyoung.remoteupnp.playlist.Playlist;
import com.wonyoung.remoteupnp.device.DeviceSubscriber;
import com.wonyoung.remoteupnp.localrenderer.SimpleRendererNoMediaPresent;
import com.wonyoung.remoteupnp.localrenderer.SimpleRendererPlaying;
import com.wonyoung.remoteupnp.localrenderer.SimpleRendererStopped;
import com.wonyoung.remoteupnp.playlist.PlaylistListener;
import com.wonyoung.remoteupnp.renderer.Renderer;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.graphics.*;
import com.wonyoung.remoteupnp.localrenderer.*;

/**
 * Created by wonyoungjang on 2013. 11. 18..
 */
public class MyUPnPService extends Binder implements UPnPService
{

	public void shuffle()
	{
		playlist.shuffle();
		// TODO: Implement this method
	}


    private Playlist playlist = new Playlist();

    private MediaServer mediaServer = new MediaServer(playlist);

    private final Renderer renderer = new Renderer(playlist);

    private ArrayList<Device> devices = new ArrayList<Device>();
    private ArrayList<DeviceSubscriber> listeners = new ArrayList<DeviceSubscriber>();

    private AndroidUpnpService upnpService;
    private BrowseRegistryListener registryListener = new BrowseRegistryListener();
    private boolean bounded = false;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("bind", "Bounded connection - " + this);
            upnpService = (AndroidUpnpService) service;

			upnpService.getRegistry().addDevice(new SimpleRenderer().createDevice());
			
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

  

    @Override
    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    @Override
    public void setMediaDevice(Device device) {
        mediaServer.updateDevice(this, device);
    }

    @Override
    public void setRendererDevice(Device device) {
        renderer.updateDevice(this, device);
    }

    @Override
    public Renderer getRenderer() {
        return renderer;
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

    public void unbind() {
        if (upnpService != null) {
            upnpService.getRegistry().removeListener(registryListener);
        }
        if (bounded) {
            Log.d("bind", "try to unbound - " + serviceConnection);
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

    @Override
    public void setMediaServerListener(FolderSubscriber subscriber) {
        mediaServer.setListener(subscriber);
    }

    @Override
    public MediaServer getMediaServer() {
        return mediaServer;
    }

    @Override
    public void setPlaylistListener(PlaylistListener listener) {
        playlist.setListener(listener);
    }

}
