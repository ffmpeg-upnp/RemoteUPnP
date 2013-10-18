package com.wonyoung.remoteupnp;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity
        implements ActionBar.TabListener {

    public static final String ARG_RENDER_LIST = "render_list";
    public static final String ARG_MEDIA_SERVER_LIST = "media_server_list";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private ArrayList<Device> rendererList = new ArrayList<Device>();
    private ArrayList<Device> mediaServerList = new ArrayList<Device>();
    private DeviceAdapter rendererListAdapter = new DeviceAdapter(rendererList);
    private DeviceAdapter mediaServerListAdapter = new DeviceAdapter(mediaServerList);

    private AndroidUpnpService upnpService;
    private BrowseRegistryListener registryListener;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            // Clear the list
            rendererList.clear();
            mediaServerList.clear();

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
    private Device mediaServer;
    private List<Item> fileList;
    private List<Container> folderList;

    private AdapterView.OnItemClickListener mediaServerOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AlertDialog dialog = new AlertDialog.Builder(view.getContext()).create();
            final Device device = (Device) parent.getItemAtPosition(position);
            dialog.setTitle(device.toString());

            dialog.setMessage(getDetailsMessage(device));
            dialog.setButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            onMediaServerSelected(device);
                        }
                    }
            );
            dialog.show();
            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setTextSize(12);
        }

        private String getDetailsMessage(Device device) {
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
    };
    private AdapterView.OnItemClickListener rendererOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AlertDialog dialog = new AlertDialog.Builder(view.getContext()).create();
            final Device device = (Device) parent.getItemAtPosition(position);
            dialog.setTitle(device.toString());

            dialog.setMessage(getDetailsMessage(device));
            dialog.setButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            onRendererSelected(device);
                        }
                    }
            );
            dialog.show();
            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setTextSize(12);
        }

        private String getDetailsMessage(Device device) {
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
    };

    private void onRendererSelected(Device device) {
        play(device);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionsPagerAdapter.instantiateItem(mViewPager, 0);
        mSectionsPagerAdapter.instantiateItem(mViewPager, 1);
        mSectionsPagerAdapter.instantiateItem(mViewPager, 2);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        registryListener = new BrowseRegistryListener(rendererList, mediaServerList);
        getApplicationContext().bindService(
                new Intent(this, RendererUpnpService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );

    }

    @Override
    protected void onDestroy() {
        if (upnpService != null) {
            upnpService.getRegistry().removeListener(registryListener);
        }
        getApplicationContext().unbindService(serviceConnection);

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    private void play(Device device) {
        Service service = device.findService(new UDAServiceId("AVTransport"));

        if (service != null) {
            ActionCallback setAVTransportURIAction = new SetAVTransportURI(service, "http://192.168.1.123:5001/get/0$1$2$1$2/02+VARIACION+01.mp3", "NO METADATA") {
                @Override
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                }
            };

            ActionCallback playAction = new Play(service) {

                @Override
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                }
            };

            upnpService.getControlPoint().execute(setAVTransportURIAction);
            upnpService.getControlPoint().execute(playAction);
        }
    }

    public void onMediaServerSelected(Device device) {
        Toast.makeText(this, "Media Server : " + device.getDisplayString(), Toast.LENGTH_SHORT).show();
        mediaServer = device;
        browse(mediaServer.findService(new UDAServiceId("ContentDirectory")));
    }

    private void browse(Service service) {

        ActionCallback rootBrowseAction = new Browse(service, "0", BrowseFlag.DIRECT_CHILDREN) {

            @Override
            public void received(ActionInvocation actionInvocation, DIDLContent didlContent) {
                fileList = didlContent.getItems();
                folderList = didlContent.getContainers();

                Log.e("wonyoung", "folder count : " + folderList.size());
                for (Container folder : folderList) {
                    Log.e("wonyoung", folder.getTitle() + " id-" + folder.getId());
                }

                Log.e("wonyoung", "files count : " + fileList.size());
                for (Item item : fileList) {
                    if (item != null) {
                        Log.e("wonyoung", String.format("[%s]",item.getTitle()));
                        if (item.getFirstResource() != null)
                            Log.e("wonyoung", String.format("      :[%s] ",item.getFirstResource().getValue()));

                    }
                }


                ArrayAdapter<Item> itemAdapter = new ArrayAdapter<Item>(MainActivity.this, android.R.layout.simple_list_item_activated_1,
                        fileList);
                ArrayAdapter<Container> folderAdapter = new ArrayAdapter<Container>(MainActivity.this, android.R.layout.simple_list_item_activated_1,
                        folderList);

                mSectionsPagerAdapter.librarySelectFragment.setListAdapter(itemAdapter);
            }

            @Override
            public void updateStatus(Status status) {

            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {

            }
        };
        upnpService.getControlPoint().execute(rootBrowseAction);

        ActionCallback browseAction = new Browse(service, "0$1$2$1", BrowseFlag.DIRECT_CHILDREN) {

            @Override
            public void received(ActionInvocation actionInvocation, DIDLContent didlContent) {
                fileList = didlContent.getItems();
                folderList = didlContent.getContainers();

                Log.e("wonyoung", "folder count : " + folderList.size());
                for (Container folder : folderList) {
                    Log.e("wonyoung", folder.getTitle() + " id-" + folder.getId());
                }

                Log.e("wonyoung", "files count : " + fileList.size());
                for (Item item : fileList) {
                    Log.e("wonyoung", item.getTitle() + " : " + item.getFirstResource().getValue());
                }


                ArrayAdapter<Item> itemAdapter = new ArrayAdapter<Item>(MainActivity.this, android.R.layout.simple_list_item_activated_1,
                        fileList);
                ArrayAdapter<Container> folderAdapter = new ArrayAdapter<Container>(MainActivity.this, android.R.layout.simple_list_item_activated_1,
                        folderList);

                mSectionsPagerAdapter.librarySelectFragment.setListAdapter(itemAdapter);
            }

            @Override
            public void updateStatus(Status status) {

            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {

            }
        };

        upnpService.getControlPoint().execute(browseAction);

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public PlayerFragment playerFragment;

        public LibrarySelectFragment librarySelectFragment;
        public DeviceSelectFragment deviceSelectFragment;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    playerFragment = new PlayerFragment();
                    return playerFragment;
                case 1:
                    librarySelectFragment = new LibrarySelectFragment();
                    return librarySelectFragment;
                case 2:
                    deviceSelectFragment = createDeviceSelectFragment();
                    deviceSelectFragment.setRendererAdapter(rendererListAdapter, rendererOnClickListener);
                    deviceSelectFragment.setMediaServerAdapter(mediaServerListAdapter, mediaServerOnClickListener);
                    return deviceSelectFragment;
            }
            return null;
        }

        private DeviceSelectFragment createDeviceSelectFragment() {
            DeviceSelectFragment deviceSelectFragment = new DeviceSelectFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_RENDER_LIST, rendererList);
            args.putSerializable(ARG_MEDIA_SERVER_LIST, mediaServerList);
            deviceSelectFragment.setArguments(args);
            return deviceSelectFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }

    }
    private class BrowseRegistryListener extends DefaultRegistryListener {

        private final UDAServiceType SERVICE_TYPE_RENDERER = new UDAServiceType("AVTransport");
        private final ServiceType SERVICE_TYPE_MEDIA_SERVER = new UDAServiceType("ContentDirectory");
        private ArrayList<Device> rendererList;
        private ArrayList<Device> mediaServerList;
        public BrowseRegistryListener(ArrayList<Device> rendererList,
                                      ArrayList<Device> mediaServerList) {

            this.rendererList = rendererList;
            this.mediaServerList = mediaServerList;
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
                            MainActivity.this,
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
            for (Service service : device.getServices()) {
                ServiceType serviceType = service.getServiceType();
                final ServiceId serviceId = service.getServiceId();
                if (SERVICE_TYPE_RENDERER.equals(serviceType)) {
                    addDeviceTo(rendererListAdapter, device);
                } else if (SERVICE_TYPE_MEDIA_SERVER.equals(serviceType)) {
                    addDeviceTo(mediaServerListAdapter, device);
                }
            }
        }

        private void addDeviceTo(final DeviceAdapter adapter, final Device device) {
            runOnUiThread(new Runnable() {
                public void run() {
                    int position = adapter.indexOf(device);
                    if (position >= 0) {
                        adapter.set(position, device);
                    } else {
                        adapter.add(device);
                    }
                }
            });
        }

        public void deviceRemoved(final Device device) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mediaServerList.remove(device);
                    rendererList.remove(device);
                    mediaServerListAdapter.notifyDataSetChanged();
                    rendererListAdapter.notifyDataSetChanged();
                }
            });
        }

    }
    private class DeviceAdapter extends BaseAdapter {


        private ArrayList<Device> devices;

        public DeviceAdapter(ArrayList<Device> devices) {
            this.devices = devices;
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) convertView;
            if (view == null) {
                view = new TextView(parent.getContext());

            } else {

            }

            Device device = devices.get(position);
            view.setText(device.getDisplayString());
            return view;
        }

        public int indexOf(Device device) {
            return devices.indexOf(device);
        }

        public void set(int position, Device device) {
            devices.set(position, device);
            notifyDataSetChanged();
        }

        public void add(Device device) {
            devices.add(device);
            notifyDataSetChanged();
        }

    }

    public void setUPnPService(UPnPService upnpService) {

    }
}
