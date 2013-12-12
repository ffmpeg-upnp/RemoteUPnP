package com.wonyoung.remoteupnp.service;

import android.content.ServiceConnection;

import com.wonyoung.remoteupnp.mediaserver.FolderSubscriber;
import com.wonyoung.remoteupnp.mediaserver.MediaServer;
import com.wonyoung.remoteupnp.device.DeviceSubscriber;
import com.wonyoung.remoteupnp.playlist.PlaylistListener;
import com.wonyoung.remoteupnp.renderer.Renderer;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.meta.Device;

/**
 * Created by wonyoungjang on 13. 10. 19..
 */
public interface UPnPService
{

	public void shuffle();

    ServiceConnection getServiceConnection();

    void addListener(DeviceSubscriber listener);
    void removeListener(DeviceSubscriber listener);

    void execute(SubscriptionCallback callback);
    void execute(ActionCallback action);

    void setMediaServerListener(FolderSubscriber subscriber);
    void setMediaDevice(Device device);
    MediaServer getMediaServer();

    void setRendererDevice(Device device);
    Renderer getRenderer();


    void setPlaylistListener(PlaylistListener listener);

}
