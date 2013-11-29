package com.wonyoung.remoteupnp;

import android.app.*;
import android.content.*;
import android.os.*;
import org.fourthline.cling.controlpoint.*;
import android.support.v4.app.*;
import org.fourthline.cling.model.meta.Device;

public class UPnPColtrolService extends Service
{
	private IBinder binder = new BinderImpl();

	@Override
	public IBinder onBind(Intent p1)
	{
		// TODO: Implement this method
		return binder;
	}
	
	private class BinderImpl extends Binder implements UPnPService
	{

		public PlaylistAdapter getPlaylistAdapter()
		{
			// TODO: Implement this method
			return null;
		}

		public MediaServer getMediaServer()
		{
			// TODO: Implement this method
			return null;
		}

		public void setMediaServer(Device device)
		{
			// TODO: Implement this method
		}

		public void setRenderer(Device device)
		{
			// TODO: Implement this method
		}

		public void execute(ActionCallback action)
		{
			// TODO: Implement this method
		}

		public void addListener(DeviceSubscriber listener)
		{
			// TODO: Implement this method
		}

		public void unbind()
		{
			// TODO: Implement this method
		}

		public Device getMediaDevice()
		{
			// TODO: Implement this method
			return null;
		}

		public Device getRendererDevice()
		{
			// TODO: Implement this method
			return null;
		}

		public void setOnMediaServerChangeListener(OnMediaServerChangeListener listener)
		{
			// TODO: Implement this method
		}

		public void bind(FragmentActivity activity)
		{
			// TODO: Implement this method
		}

		public ServiceConnection getServiceConnection()
		{
			// TODO: Implement this method
			return null;
		}

		public void execute(SubscriptionCallback callback)
		{
			// TODO: Implement this method
		}

		
	}
}
