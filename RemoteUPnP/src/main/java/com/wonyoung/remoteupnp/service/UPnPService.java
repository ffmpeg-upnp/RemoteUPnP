package com.wonyoung.remoteupnp.service;

import android.content.ServiceConnection;

import com.wonyoung.remoteupnp.playlist.PlaylistAdapter;
import com.wonyoung.remoteupnp.device.DeviceSubscriber;
import com.wonyoung.remoteupnp.mediaserver.OnMediaServerChangeListener;
import com.wonyoung.remoteupnp.renderer.OnRendererChangeListener;

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
