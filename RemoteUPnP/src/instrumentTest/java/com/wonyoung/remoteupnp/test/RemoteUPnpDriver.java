package com.wonyoung.remoteupnp.test;

import com.jayway.android.robotium.solo.Solo;
import com.wonyoung.remoteupnp.ui.MainActivity;
import com.wonyoung.remoteupnp.service.UPnPService;

import org.fourthline.cling.model.meta.Device;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by wonyoungjang on 13. 10. 19..
 */
public class RemoteUPnpDriver {
    private Solo solo;
    private MainActivity activity;

    public RemoteUPnpDriver(Solo solo, MainActivity activity) {
        this.solo = solo;
        this.activity = activity;
    }

    public void showsDevice(String s) {
        assertThat(solo.waitForText(s), equalTo(true));
    }

    public void setUPnPService(UPnPService upnpService) {
        activity.setUPnPService(upnpService);
    }

    public void setRenderer(Device renderer) {
        activity.setRenderer(renderer);
    }

    public void setMediaServer(Device mediaServer) {
        activity.setMediaServer(mediaServer);
    }

    public void browse(Device mediaServer, String folder) {
    }
}
