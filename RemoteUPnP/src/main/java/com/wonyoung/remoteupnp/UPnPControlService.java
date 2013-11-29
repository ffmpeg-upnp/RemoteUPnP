package com.wonyoung.remoteupnp;

import android.app.*;
import android.content.*;
import android.os.*;

import org.fourthline.cling.controlpoint.*;

import android.support.v4.app.*;
import android.util.Log;

import org.fourthline.cling.model.meta.Device;

import com.wonyoung.remoteupnp.ui.MainActivity;

public class UPnPControlService extends Service
{
    protected static final String TAG = UPnPControlService.class.getName();

	private IBinder binder = new BinderImpl();
    private UPnPService service = new MyUPnPService();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        Context context = getApplicationContext();
        context.bindService(
            new Intent(context, RendererUpnpService.class),
            service.getServiceConnection(),
            Context.BIND_AUTO_CREATE
        );

	    return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent p1)
	{
	    Log.d(TAG, "onBind");
		// TODO: Implement this method
		return binder;
	}
	
	@Override
	public void onDestroy() {
        Log.d(TAG, "onDestroy");
        Context context = getApplicationContext();
        context.unbindService(service.getServiceConnection());
	    super.onDestroy();
	}
	
	private class BinderImpl extends Binder implements UPnPService
	{

		public void setOnRendererChangeListener(OnRendererChangeListener listner)
		{
			// TODO: Implement this method
			service.setOnRendererChangeListener(listner);
		}


		public PlaylistAdapter getPlaylistAdapter()
		{
			// TODO: Implement this method
			return service.getPlaylistAdapter();
		}

		public void setRenderer(Device device)
		{
		    service.setRenderer(device);
			// TODO: Implement this method
		}

		public void execute(ActionCallback action)
		{
		    service.execute(action);
			// TODO: Implement this method
		}

		public void addListener(DeviceSubscriber listener)
		{
		    service.addListener(listener);
			// TODO: Implement this method
		}

		public void unbind()
		{
		    service.unbind();
			// TODO: Implement this method
		}

		public Device getMediaDevice()
		{
			// TODO: Implement this method
			return service.getMediaDevice();
		}

		public Device getRendererDevice()
		{
			// TODO: Implement this method
			return service.getRendererDevice();
		}

		public void setOnMediaServerChangeListener(OnMediaServerChangeListener listener)
		{
		    service.setOnMediaServerChangeListener(listener);
			// TODO: Implement this method
		}

		public void bind(FragmentActivity activity)
		{
		    service.bind(activity);
			// TODO: Implement this method
		}

		public ServiceConnection getServiceConnection()
		{
			// TODO: Implement this method
			return service.getServiceConnection();
		}

		public void execute(SubscriptionCallback callback)
		{
		    service.execute(callback);
			// TODO: Implement this method
		}

        @Override
        public void setMediaDevice(Device device) {
            // TODO Auto-generated method stub
            service.setMediaDevice(device);
        }

        @Override
        public void removeListener(DeviceSubscriber listener) {
            // TODO Auto-generated method stub
            service.removeListener(listener);
        }

		
	}
}
