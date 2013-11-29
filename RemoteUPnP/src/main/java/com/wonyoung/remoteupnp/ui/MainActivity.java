package com.wonyoung.remoteupnp.ui;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.view.*;
import com.wonyoung.remoteupnp.*;
import java.util.*;
import org.fourthline.cling.model.meta.*;

import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class MainActivity extends FragmentActivity
implements ActionBar.TabListener, OnMediaServerChangeListener
{

	public MediaServer getMediaServer()
	{
		return mediaServer;
	}

	private MediaServer mediaServer;

	public void OnMediaServerChanged(Device device)
	{
		mediaServer = service.getMediaServer();
		mSectionsPagerAdapter.librarySelectFragment.updateMediaServer();
	}

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    private UPnPService service = new MyUPnPService();

    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpActionBarAndViewPager();
        Context context = getApplicationContext();
        context.bindService(
			new Intent(context, RendererUpnpService.class),
			service.getServiceConnection(),
			Context.BIND_AUTO_CREATE
        );

		service.setOnMediaServerChangeListener(this);
    }

    private void setUpActionBarAndViewPager()
	{
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position)
				{
					actionBar.setSelectedNavigationItem(position);
				}
			});

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
		{
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
				actionBar.newTab()
				.setText(mSectionsPagerAdapter.getPageTitle(i))
				.setTabListener(this));
        }
    }

    @Override
    protected void onDestroy()
	{
        super.onDestroy();
        service.unbind();
        getApplicationContext().unbindService(service.getServiceConnection());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
    }

    public UPnPService getUPnPService()
	{
        return service;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
	{

        public PlayerFragment playerFragment;
        public LibrarySelectFragment librarySelectFragment;
        public DeviceSelectFragment deviceSelectFragment;

        public SectionsPagerAdapter(FragmentManager fm)
		{
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
		{
            switch (position)
			{
                case 0:
                    deviceSelectFragment = new DeviceSelectFragment();
                    return deviceSelectFragment;
                case 1:
                    librarySelectFragment = new LibrarySelectFragment();
					if (service.getMediaDevice() != null)
					{
						mediaServer = service.getMediaServer();
						librarySelectFragment.updateMediaServer();
					}
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
        public int getCount()
		{
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position)
		{
            Locale l = Locale.getDefault();
            switch (position)
			{
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

    public void setUPnPService(UPnPService upnpService)
	{
        this.service = upnpService;
    }

    public void setRenderer(Device renderer)
	{
    }

    public void setMediaServer(Device mediaServer)
	{
    }
}
