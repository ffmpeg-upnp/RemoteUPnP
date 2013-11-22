package com.wonyoung.remoteupnp;

import org.fourthline.cling.model.meta.Device;

/**
* Created by wonyoungjang on 2013. 11. 23..
*/
public interface DeviceSubscriber {
    void add(Device item);

    void remove(Device item);
}
