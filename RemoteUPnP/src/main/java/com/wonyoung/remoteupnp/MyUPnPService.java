package com.wonyoung.remoteupnp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.util.ArrayList;

/**
 * Created by wonyoungjang on 2013. 11. 18..
 */
public class MyUPnPService implements UPnPService {
    private final MainActivity activity;
    private Context context;
    private AndroidUpnpService upnpService;

    private BrowseRegistryListener registryListener;
    private DeviceList mediaServers = new MyDeviceList();
    private DeviceList renderers = new MyDeviceList();

    private Device mediaServer;
    private Device renderer;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            // Clear the list
            renderers.clear();
            mediaServers.clear();

            // Get ready for future device advertisements
            upnpService.getRegistry().addListener(registryListener);

            // Now add all devices to the list we already know about
            for (Device device : upnpService.getRegistry().getDevices()) {
                registryListener.deviceAdded(device);
            }

            // Search asynchronously for all devices, they will respond soon
            upnpService.getControlPoint().search();
        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    public MyUPnPService(Context context) {
        this.activity = (MainActivity) context; // temp. code for browse
        this.context = context.getApplicationContext();

        registryListener = new BrowseRegistryListener();

        context.bindService(
                new Intent(context, RendererUpnpService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    @Override
    public void setMediaServer(int position) {
        mediaServer = mediaServers.get(position);
        activity.mSectionsPagerAdapter.librarySelectFragment.browse(mediaServer.findService(new UDAServiceId("ContentDirectory")));
    }

    @Override
    public void setRenderer(int position) {
        renderer = renderers.get(position);
    }

    @Override
    public void execute(ActionCallback action) {
        upnpService.getControlPoint().execute(action);
    }

    @Override
    public void destroy() {
        if (upnpService != null) {
            upnpService.getRegistry().removeListener(registryListener);
        }
        context.unbindService(serviceConnection);
    }

    @Override
    public DeviceList getMediaServers() {
        return mediaServers;
    }

    @Override
    public DeviceList getRenderers() {
        return renderers;
    }

    private static class MyDeviceList implements DeviceList {
        private ArrayList<Device> devices = new ArrayList<Device>();
        private DeviceSelectFragment.DeviceAdapter listener;

        @Override
        public void addListener(DeviceSelectFragment.DeviceAdapter listener) {
            this.listener = listener;
        }

        @Override
        public Device get(int position) {
            return devices.get(position);
        }

        @Override
        public int size() {
            return devices.size();
        }

        @Override
        public void clear() {
            devices.clear();
        }

        @Override
        public void add(Device device) {
            int position = devices.indexOf(device);
            if (position >= 0) {
                devices.set(position, device);
            } else {
                devices.add(device);
            }
            listener.update();
        }

        @Override
        public void remove(Device device) {
            devices.remove(device);
            listener.update();
        }
    }


    private class BrowseRegistryListener extends DefaultRegistryListener {

        private final UDAServiceType SERVICE_TYPE_RENDERER = new UDAServiceType("AVTransport");
        private final ServiceType SERVICE_TYPE_MEDIA_SERVER = new UDAServiceType("ContentDirectory");

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
            for (Service service : device.getServices()) {
                ServiceType serviceType = service.getServiceType();
                if (SERVICE_TYPE_RENDERER.equals(serviceType)) {
                    renderers.add(device);
                } else if (SERVICE_TYPE_MEDIA_SERVER.equals(serviceType)) {
                    mediaServers.add(device);
                }
            }
        }

        public void deviceRemoved(final Device device) {
            for (Service service : device.getServices()) {
                ServiceType serviceType = service.getServiceType();
                if (SERVICE_TYPE_RENDERER.equals(serviceType)) {
                    renderers.remove(device);
                } else if (SERVICE_TYPE_MEDIA_SERVER.equals(serviceType)) {
                    mediaServers.remove(device);
                }
            }
        }
    }

}
