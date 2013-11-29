package com.wonyoung.remoteupnp.service;

import android.content.Intent;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;

/**
 * Created by wonyoungjang on 13. 10. 15..
 */
public class RendererUpnpService extends AndroidUpnpServiceImpl {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    protected UpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration() {
            @Override
            public int getRegistryMaintenanceIntervalMillis() {
                return 7000;
            }

            @Override
            public ServiceType[] getExclusiveServiceTypes() {
                return new ServiceType[] {
                  new UDAServiceType("ConnectionManager"),
                  new UDAServiceType("ContentDirectory"),
                  new UDAServiceType("AVTransport")
                };
            }
        };
    }
}
