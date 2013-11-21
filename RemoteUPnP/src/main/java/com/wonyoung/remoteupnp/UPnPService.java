package com.wonyoung.remoteupnp;

import org.fourthline.cling.registry.RegistryListener;

/**
 * Created by wonyoungjang on 13. 10. 19..
 */
public interface UPnPService {
    void addListener(RegistryListener registryListener);
    void updateDeviceList();

    void destroy();
}
