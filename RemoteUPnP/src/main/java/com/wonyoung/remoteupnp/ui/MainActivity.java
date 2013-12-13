package com.wonyoung.remoteupnp.ui;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.util.Log;
import android.view.*;

import com.wonyoung.remoteupnp.*;
import com.wonyoung.remoteupnp.device.DeviceSubscriber;
import com.wonyoung.remoteupnp.mediaserver.FolderSubscriber;
import com.wonyoung.remoteupnp.mediaserver.MediaServer;
import com.wonyoung.remoteupnp.playlist.PlaylistListener;
import com.wonyoung.remoteupnp.renderer.Renderer;
import com.wonyoung.remoteupnp.service.UPnPControlService;
import com.wonyoung.remoteupnp.service.UPnPService;

import java.util.*;

import org.fourthline.cling.model.meta.*;

import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.*;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    protected static final String TAG = MainActivity.class.getName();

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    private DeviceSubscriber deviceSubscriber;
    private FolderSubscriber folderSubscriber;
    private PlaylistListener playlistListener;
    protected UPnPService uPnpService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            uPnpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "upnpcontrolservice onServiceConnected");
            uPnpService = (UPnPService) service;

            if (deviceSubscriber != null)
                addListener(deviceSubscriber);
            if (folderSubscriber != null)
                setMediaServerListener(folderSubscriber);
            if (playlistListener != null)
                setPlaylistListener(playlistListener);
        }
    };

	private String parentFolder = null;

	public void shufflePlay()
	{
		
	//	ArrayList<Integer> orders = new ArrayList<Integer>();
	
		
		Renderer renderer = uPnpService.getRenderer();
		renderer.debugToastTo(this);
        renderer.playShuffle();
		// TODO: Implement this method
	}

	public void setParentFolder(String parentID)
	{
		parentFolder = parentID;
		// TODO: Implement this method
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpActionBarAndViewPager();
        Context context = getApplicationContext();

        Intent intent = new Intent(this, UPnPControlService.class);
        context.startService(intent);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        if (uPnpService != null) {
            getApplicationContext().unbindService(serviceConnection);
        }
        super.onDestroy();
    }

    private void setUpActionBarAndViewPager() {
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab,
                              FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

	@Override
	public void onBackPressed() {
		if (parentFolder != null) {
			browse(parentFolder);
		}
		else {
		super.onBackPressed();
		}
	}
	
    public void browse(String folder) {

        MediaServer mediaServer = uPnpService.getMediaServer();
        mediaServer.browse(folder);
    }

    public void addAll() {
        MediaServer mediaServer = uPnpService.getMediaServer();
        mediaServer.addAll();
    }

    public void play(String url) {
        Renderer renderer = uPnpService.getRenderer();
        renderer.play(url);
    }

    public void playFrom(int index) {
        Renderer renderer = uPnpService.getRenderer();
		renderer.debugToastTo(this);
        renderer.playFrom(index);
    }
	
	public void toast(final String s) {
		runOnUiThread(new Runnable() {

				public void run()
				{
					Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
					// TODO: Implement this method
				}
				
			
		});

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
                    deviceSelectFragment = new DeviceSelectFragment();
                    return deviceSelectFragment;
                case 1:
                    librarySelectFragment = new LibrarySelectFragment();
                    return librarySelectFragment;
                case 2:
                    playerFragment = new PlayerFragment();
                    return playerFragment;
                case 3:
                    return new PlaylistFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
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
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }

    }

    public UPnPService getUPnPService() {
        return uPnpService;
    }

    public void setUPnPService(UPnPService upnpService) {
        this.uPnpService = upnpService;
    }

    public void setRenderer(Device renderer) {
        if (uPnpService != null) {
            uPnpService.setRendererDevice(renderer);
        }
    }

    public void setMediaDevice(Device device) {
        if (uPnpService != null) {
            uPnpService.setMediaDevice(device);
        }
    }

    public void addListener(DeviceSubscriber subscriber) {
        this.deviceSubscriber = subscriber;
        if (uPnpService != null) {
            uPnpService.addListener(subscriber);
        }
    }

    public void removeListener(DeviceSubscriber subscriber) {
        this.deviceSubscriber = subscriber;
        if (uPnpService != null) {
            uPnpService.removeListener(subscriber);
        }
    }

    public void setMediaServerListener(FolderSubscriber subscriber) {
        this.folderSubscriber = subscriber;
        if (uPnpService != null) {
            uPnpService.setMediaServerListener(subscriber);
        }
    }

    public void setPlaylistListener(PlaylistListener listener) {
        this.playlistListener = listener;
        if (uPnpService != null) {
            uPnpService.setPlaylistListener(listener);
        }

    }
}
