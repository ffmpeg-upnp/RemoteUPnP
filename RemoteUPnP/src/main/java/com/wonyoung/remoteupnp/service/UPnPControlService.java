package com.wonyoung.remoteupnp.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.wonyoung.remoteupnp.R;
import com.wonyoung.remoteupnp.ui.MainActivity;
import com.wonyoung.remoteupnp.localrenderer.*;
import org.fourthline.cling.android.*;

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

        addNotification();

	    return START_STICKY;
	}

    private void addNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("My notification")
                .setContentText("Hello World!");
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());
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
