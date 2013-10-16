package com.wonyoung.remoteupnp;

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
    protected UpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration() {
            @Override
            public int getRegistryMaintenanceIntervalMillis() {
                return 7000;
            }

            @Override
            public ServiceType[] getExclusiveServiceTypes() {
                return new ServiceType[] {
                  new UDAServiceType("ContentDirectory"),
                  new UDAServiceType("AVTransport")
                };
            }
        };
    }
}
