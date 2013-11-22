package com.wonyoung.remoteupnp;

import android.util.Log;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wonyoungjang on 2013. 11. 23..
 */
public class MediaServer {
    private Service service;
    private Device device;
    private UPnPService uPnPService;
    private FolderSubscriber subscriber;

    private List<Item> fileList;
    private List<Container> folderList;


    public MediaServer(UPnPService uPnPService, FolderSubscriber subscriber) {
        this.uPnPService = uPnPService;
        this.subscriber = subscriber;
    }

    public void browse(String folder) {
        this.device = uPnPService.getMediaDevice();
        service = device.findService(new UDAServiceId("ContentDirectory"));
        ActionCallback browseAction = new Browse(service, folder, BrowseFlag.DIRECT_CHILDREN) {

            @Override
            public void received(ActionInvocation actionInvocation, DIDLContent didlContent) {
                fileList = didlContent.getItems();
                folderList = didlContent.getContainers();

                final ArrayList<DIDLObject> list = new ArrayList<DIDLObject>();

                Log.e("wonyoung", "folder count : " + folderList.size());
                for (Container folder : folderList) {
                    list.add(folder);
                    Log.e("wonyoung", "title : " + folder.getTitle());
                    Log.e("wonyoung", "id : " + folder.getId());
                    Log.e("wonyoung", "parentId : " + folder.getParentID());
                }

                Log.e("wonyoung", "files count : " + fileList.size());
                for (Item item : fileList) {
                    if (item != null) {
                        list.add(item);
                        Log.e("wonyoung", String.format("[%s]", item.getTitle()));
                        if (item.getFirstResource() != null)
                            Log.e("wonyoung", String.format("      :[%s] ", item.getFirstResource().getValue()));

                    }
                }
                subscriber.updatedFolderList(list);
            }

            @Override
            public void updateStatus(Status status) {

            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {

            }
        };

        uPnPService.execute(browseAction);
    }

}
