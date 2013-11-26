package com.wonyoung.remoteupnp.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.wonyoung.remoteupnp.ui.MainActivity;
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
        MainActivity activity = inst.getActivity();
        driver = new RemoteUPnpDriver(new Solo(inst.getInstrumentation(),
                activity), activity);

        driver.setUPnPService(upnpService);
    }

    public void showsDevices(Device... devices) {
        for (Device device : devices) {
            driver.showsDevice(device.getDisplayString());
        }
    }

    public void readyWith(Device renderer, Device mediaServer) {
        driver.setRenderer(renderer);
        driver.setMediaServer(mediaServer);
    }

    public void requestFolders(Device mediaServer, String folder) {
        driver.browse(mediaServer, folder);
    }

    public void showsFolders(UPnPFolder folder) {

    }

    public void addToPlayList(UPnPFolder.File file) {

    }

    public void play() {

    }
}
