package com.wonyoung.remoteupnp;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;

/**
 * Created by wonyoungjang on 13. 10. 19..
 */
public interface UPnPService {
    void setMediaServer(int position);
    void setRenderer(int position);

    void execute(ActionCallback action);

    void addListener(Callback listener);

    public interface Callback {
        void update();
    }

    void destroy();

    ArrayList<Device> getMediaServers();
    ArrayList<Device> getRenderers();
}
