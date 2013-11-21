package com.wonyoung.remoteupnp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.registry.RegistryListener;

/**
 * Created by wonyoungjang on 2013. 11. 18..
 */
public class MyUPnPService implements UPnPService {
    private Context context;
    private AndroidUpnpService upnpService;
    private final ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            // Clear the list
            searchedRenderers.clear();
            searchedMediaServers.clear();

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
        this.context = context.getApplicationContext();

        context.bindService(
                new Intent(context, RendererUpnpService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    @Override
    public void addListener(RegistryListener registryListener) {

    }

    @Override
    public void updateDeviceList() {

    }

    @Override
    public void destroy() {
        if (upnpService != null) {
            upnpService.getRegistry().removeListener(registryListener);
        }
        context.unbindService(serviceConnection);
    }
}
