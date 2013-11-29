package com.wonyoung.remoteupnp.mediaserver;

import org.fourthline.cling.model.meta.Device;

/**
 * Created by wonyoungjang on 2013. 11. 23..
 */
public interface OnMediaServerChangeListener {

    void OnMediaServerChanged(Device device);
}
