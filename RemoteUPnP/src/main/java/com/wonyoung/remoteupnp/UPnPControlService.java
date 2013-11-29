package com.wonyoung.remoteupnp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UPnPControlService extends Service
{
    protected static final String TAG = UPnPControlService.class.getName();

    private MyUPnPService service = new MyUPnPService();

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
	public IBinder onBind(Intent intent)
	{
	    Log.d(TAG, "onBind");
		return service;
	}
	
	@Override
	public void onDestroy() {
        Log.d(TAG, "onDestroy");
        Context context = getApplicationContext();
        context.unbindService(service.getServiceConnection());
	    super.onDestroy();
	}
}
