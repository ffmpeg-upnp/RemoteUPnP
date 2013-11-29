package com.wonyoung.remoteupnp;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.meta.Device;

import android.content.ServiceConnection;
import android.support.v4.app.FragmentActivity;

/**
 * Created by wonyoungjang on 13. 10. 19..
 */
public interface UPnPService
{

	public PlaylistAdapter getPlaylistAdapter();

    void setRenderer(Device device);

    void execute(ActionCallback action);

    void addListener(DeviceSubscriber listener);
    public void removeListener(DeviceSubscriber listener);

    void unbind();

    Device getMediaDevice();

    Device getRendererDevice();

    void setOnMediaServerChangeListener(OnMediaServerChangeListener listener);
    void setOnRendererChangeListener(OnRendererChangeListener listner);
    void bind(FragmentActivity activity);

    ServiceConnection getServiceConnection();


    void execute(SubscriptionCallback callback);

    public void setMediaDevice(Device device);

}
