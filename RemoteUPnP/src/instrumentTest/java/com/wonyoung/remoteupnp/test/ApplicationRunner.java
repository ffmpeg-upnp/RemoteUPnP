package com.wonyoung.remoteupnp.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.wonyoung.remoteupnp.MainActivity;
import com.wonyoung.remoteupnp.UPnPService;
import com.wonyoung.remoteupnp.folder.UPnPFolder;

import org.fourthline.cling.model.meta.Device;

/**
 * Created by wonyoungjang on 13. 10. 16..
 */
public class ApplicationRunner {
    private ActivityInstrumentationTestCase2<MainActivity> inst;
    private RemoteUPnpDriver driver;

    public ApplicationRunner(ActivityInstrumentationTestCase2<MainActivity> inst) {

        this.inst = inst;
    }

    public void startWith(UPnPService upnpService) {
        driver = new RemoteUPnpDriver(new Solo(inst.getInstrumentation(),
                inst.getActivity()));
        MainActivity activity = inst.getActivity();
        activity.setUPnPService(upnpService);
    }

    public void showsDevices(Device... devices) {
    }

    public void readyWith(Device renderer, Device mediaServer) {

    }

    public void requestFolders(Device mediaServer, String folder) {

    }

    public void showsFolders(UPnPFolder folder) {

    }

    public void addToPlayList(UPnPFolder.File file) {

    }

    public void play() {

    }
}
