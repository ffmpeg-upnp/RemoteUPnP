package com.wonyoung.remoteupnp;

import org.fourthline.cling.support.model.DIDLObject;

import java.util.ArrayList;

/**
 * Created by wonyoungjang on 2013. 11. 23..
 */
public interface FolderSubscriber {
    void updatedFolderList(ArrayList<DIDLObject> updated);
}
