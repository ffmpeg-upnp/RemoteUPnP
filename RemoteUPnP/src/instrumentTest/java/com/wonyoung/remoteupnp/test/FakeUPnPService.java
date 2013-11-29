package com.wonyoung.remoteupnp.test;

import android.content.ServiceConnection;
import android.support.v4.app.FragmentActivity;

import com.wonyoung.remoteupnp.device.DeviceSubscriber;
import com.wonyoung.remoteupnp.mediaserver.OnMediaServerChangeListener;
import com.wonyoung.remoteupnp.service.UPnPService;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.registry.RegistryListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by wonyoungjang on 13. 10. 19..
 */
public class FakeUPnPService implements UPnPService {

    private boolean deviceListUpdated = false;
    private RegistryListener registryListener;

    public void hasReceivedDeviceListRequest() {
        assertThat(registryListener, notNullValue());
        assertThat(deviceListUpdated, equalTo(true));
        deviceListUpdated = false;
    }

    public void sendDeviceList(LocalDevice... devices) {
        for (LocalDevice device : devices) {
            registryListener.localDeviceAdded(null, device);
        }
    }

    @Override
    public void setMediaServer(Device device) {

    }

    @Override
    public void setRendererDevice(Device device) {

    }

    @Override
    public void execute(ActionCallback action) {

    }

    @Override
    public void addListener(DeviceSubscriber listener) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public Device getMediaDevice() {
        return null;
    }

    @Override
    public Device getRendererDevice() {
        return null;
    }

    @Override
    public void setOnMediaServerChangeListener(OnMediaServerChangeListener listener) {

    }

    @Override
    public void bind(FragmentActivity activity) {

    }

    @Override
    public ServiceConnection getServiceConnection() {
        return null;
    }
}
