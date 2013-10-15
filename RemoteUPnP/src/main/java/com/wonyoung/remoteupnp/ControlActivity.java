package com.wonyoung.remoteupnp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.app.Activity;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;

public class ControlActivity extends Activity {
    private ArrayAdapter<DeviceDisplay> rendererListAdapter;
    private BrowseRegistryListener rendererRegistryListener;
    private AndroidUpnpService rendererUpnpService;
    private ServiceConnection rendererServiceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            rendererUpnpService = (AndroidUpnpService) service;

            // Clear the list
            rendererListAdapter.clear();

            // Get ready for future device advertisements
            rendererUpnpService.getRegistry().addListener(rendererRegistryListener);

            // Now add all devices to the list we already know about
            for (Device device : rendererUpnpService.getRegistry().getDevices()) {
                rendererRegistryListener.deviceAdded(device);
            }

            // Search asynchronously for all devices, they will respond soon
            rendererUpnpService.getControlPoint().search();
        }

        public void onServiceDisconnected(ComponentName className) {
            rendererUpnpService = null;
        }
    };

    private ArrayAdapter<DeviceDisplay> mediaServerListAdapter;
    private BrowseRegistryListener mediaServerRegistryListener;
    private AndroidUpnpService mediaServerUpnpService;
    private ServiceConnection mediaServerServiceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            mediaServerUpnpService = (AndroidUpnpService) service;

            // Clear the list
            mediaServerListAdapter.clear();

            // Get ready for future device advertisements
            mediaServerUpnpService.getRegistry().addListener(mediaServerRegistryListener);

            // Now add all devices to the list we already know about
            for (Device device : mediaServerUpnpService.getRegistry().getDevices()) {
                mediaServerRegistryListener.deviceAdded(device);
            }

            // Search asynchronously for all devices, they will respond soon
            mediaServerUpnpService.getControlPoint().search();
        }

        public void onServiceDisconnected(ComponentName className) {
            mediaServerUpnpService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        AdapterView.OnItemClickListener detailViewListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog dialog = new AlertDialog.Builder(ControlActivity.this).create();
                DeviceDisplay deviceDisplay = (DeviceDisplay) parent.getItemAtPosition(position);
                dialog.setTitle(deviceDisplay.toString());
                dialog.setMessage(deviceDisplay.getDetailsMessage());
                dialog.setButton(
                        "Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }
                );
                dialog.show();
                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                textView.setTextSize(12);

                Service service = deviceDisplay.getDevice().findService(new UDAServiceId("AVTransport"));

                if (service != null) {
                    ActionCallback setAVTransportURIAction = new SetAVTransportURI(service, "http://192.168.1.123/piano.mp3", "NO METADATA") {
                        @Override
                        public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                        }
                    };

                    ActionCallback playAction = new Play(service) {

                        @Override
                        public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                        }
                    };

                    rendererUpnpService.getControlPoint().execute(setAVTransportURIAction);
                    rendererUpnpService.getControlPoint().execute(playAction);
                }
            }
        };

        rendererListAdapter = new ArrayAdapter<DeviceDisplay>(this, android.R.layout.simple_list_item_1);
        rendererRegistryListener = new BrowseRegistryListener(rendererListAdapter);
        ListView rendererList = (ListView) findViewById(R.id.renderer_list);
        rendererList.setAdapter(rendererListAdapter);
        rendererList.setOnItemClickListener(detailViewListener);

        getApplicationContext().bindService(
                new Intent(this, BrowserRendererUpnpService.class),
                rendererServiceConnection,
                Context.BIND_AUTO_CREATE
        );

        mediaServerListAdapter = new ArrayAdapter<DeviceDisplay>(this, android.R.layout.simple_list_item_1);
        mediaServerRegistryListener = new BrowseRegistryListener(mediaServerListAdapter);
        ListView mediaServerList = (ListView) findViewById(R.id.media_server_list);
        mediaServerList.setAdapter(mediaServerListAdapter);
        mediaServerList.setOnItemClickListener(detailViewListener);

        getApplicationContext().bindService(
                new Intent(this, BrowserMediaServerUpnpService.class),
                mediaServerServiceConnection,
                Context.BIND_AUTO_CREATE
        );


        Button testButton = (Button) findViewById(R.id.test_button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rendererUpnpService != null) {
            rendererUpnpService.getRegistry().removeListener(rendererRegistryListener);
        }
        // This will stop the UPnP service if nobody else is bound to it
        getApplicationContext().unbindService(rendererServiceConnection);

        if (mediaServerUpnpService != null) {
            mediaServerUpnpService.getRegistry().removeListener(mediaServerRegistryListener);
        }
        // This will stop the UPnP service if nobody else is bound to it
        getApplicationContext().unbindService(mediaServerServiceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.control, menu);
        return true;
    }

    private class DeviceDisplay {

        private final Device device;

        public DeviceDisplay(Device device) {
            this.device = device;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DeviceDisplay that = (DeviceDisplay) o;

            if (!device.equals(that.device)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return device.hashCode();
        }

        @Override
        public String toString() {
            return device.getDisplayString();
        }

        public String getDetailsMessage() {
            StringBuilder sb = new StringBuilder();
            DeviceDetails detail = device.getDetails();
            sb.append("BaseURL : " + detail.getBaseURL() + "\n");
            sb.append("FriendlyName : " + detail.getFriendlyName() + "\n");
            sb.append("SerialNumber : " + detail.getSerialNumber() + "\n");
            sb.append("Upc : " + detail.getUpc() + "\n");
            sb.append("PresentationURI : " + detail.getPresentationURI() + "\n");

            ModelDetails modelDetails = detail.getModelDetails();
            sb.append("\n");
            sb.append("ModelDescription : " + modelDetails.getModelDescription() + "\n");
            sb.append("ModelName : " + modelDetails.getModelName() + "\n");
            sb.append("ModelNumber : " + modelDetails.getModelNumber() + "\n");
            sb.append("ModelURI : " + modelDetails.getModelURI() + "\n");

            sb.append("\n\n");
            if (device.isFullyHydrated()) {
                for (Service service : device.getServices()) {
                    sb.append(service.getServiceType()).append("\n");
                    for (Action action : service.getActions()) {
                        sb.append("(" + action.getName() + ")\n");
                    }
                    sb.append("\n");
                }
            } else {
                sb.append("Device Details not yet Available");
            }

            return sb.toString();
        }

        public Device getDevice() {
            return device;
        }
    }


    protected class BrowseRegistryListener extends DefaultRegistryListener {

        private ArrayAdapter<DeviceDisplay> listAdapter;

        public BrowseRegistryListener(ArrayAdapter<DeviceDisplay> listAdapter) {

            this.listAdapter = listAdapter;
        }

        /* Discovery performance optimization for very slow Android devices! */
        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(
                            ControlActivity.this,
                            "Discovery failed of '" + device.getDisplayString() + "': "
                                    + (ex != null ? ex.toString() : "Couldn't retrieve device/service descriptors"),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
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
            runOnUiThread(new Runnable() {
                public void run() {
                    DeviceDisplay d = new DeviceDisplay(device);
                    int position = listAdapter.getPosition(d);
                    if (position >= 0) {
                        // Device already in the list, re-set new value at same position
                        listAdapter.remove(d);
                        listAdapter.insert(d, position);
                    } else {
                        listAdapter.add(d);
                    }
                }
            });
        }

        public void deviceRemoved(final Device device) {
            runOnUiThread(new Runnable() {
                public void run() {
                    listAdapter.remove(new DeviceDisplay(device));
                }
            });
        }
    }

}
