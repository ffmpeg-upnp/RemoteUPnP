package com.wonyoung.remoteupnp;

import android.content.ServiceConnection;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.meta.Device;

/**
 * Created by wonyoungjang on 13. 10. 19..
 */
public interface UPnPService
{
    ServiceConnection getServiceConnection();

    void addListener(DeviceSubscriber listener);
    void removeListener(DeviceSubscriber listener);

    void execute(SubscriptionCallback callback);
    void execute(ActionCallback action);

    void setMediaDevice(Device device);
    void setRenderer(Device device);
    Device getMediaDevice();
    Device getRendererDevice();
    void setOnMediaServerChangeListener(OnMediaServerChangeListener listener);
    void setOnRendererChangeListener(OnRendererChangeListener listner);

	PlaylistAdapter getPlaylistAdapter();
}
