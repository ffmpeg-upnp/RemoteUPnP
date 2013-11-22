package com.wonyoung.remoteupnp;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.meta.Device;

/**
 * Created by wonyoungjang on 13. 10. 19..
 */
public interface UPnPService {

    void setMediaServer(Device device);

    void setRenderer(Device device);

    void execute(ActionCallback action);

    void addListener(DeviceSubscriber listener);

    void destroy();

    Device getMediaDevice();

    Device getRendererDevice();

    void setOnMediaServerChangeListener(OnMediaServerChangeListener listener);
}
