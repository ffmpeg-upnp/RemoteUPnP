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
import org.fourthline.cling.support.model.DescMeta;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wonyoungjang on 2013. 11. 23..
 */
public class MediaServer {
    private UPnPService uPnPService;
    private FolderSubscriber subscriber;

    private List<Item> fileList;
    private List<Container> folderList;

	private ArrayList<DIDLObject> list = new ArrayList<DIDLObject>();
    private Device device;

	public void addAll()
	{
		PlaylistAdapter listAdapter = uPnPService.getPlaylistAdapter();
		for (Item item : fileList) {
			listAdapter.add(item);
		}
	}

	public void setListener(FolderSubscriber subscriber)
	{
		this.subscriber = subscriber;
		subscriber.updatedFolderList(list);
	}

    public void browse(String folder) {
        if (device == null) return;

        Service service = device.findService(new UDAServiceId("ContentDirectory"));
        ActionCallback browseAction = new Browse(service, folder, BrowseFlag.DIRECT_CHILDREN) {

            @Override
            public void received(ActionInvocation actionInvocation, DIDLContent didlContent) {
                fileList = didlContent.getItems();
                folderList = didlContent.getContainers();

                list.clear();

                for (Container folder : folderList) {
                    list.add(folder);
                }

                for (Item item : fileList) {
                    list.add(item);
                    if (item.getFirstResource() != null)
                        Log.e("remoteUpnp", String.format("url : [%s] ", item.getFirstResource().getValue()));

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

    public void updateDevice(UPnPService uPnPService, Device device) {
        this.uPnPService = uPnPService;        
        this.device = device;
    }

}
