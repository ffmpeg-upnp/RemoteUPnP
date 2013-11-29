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
import org.fourthline.cling.support.avtransport.callback.*;

/**
 * Created by wonyoungjang on 2013. 11. 23..
 */
public class Renderer {
    private UPnPService uPnPService;
    private Device device;


    public void play(String url) {
        Device device = uPnPService.getRendererDevice();
        Service service = device.findService(new UDAServiceId("AVTransport"));

        if (service != null) {
			final ActionCallback playAction = new Play(service) {

                @Override
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                }
            };
			
			
            ActionCallback setAVTransportURIAction = new SetAVTransportURI(service, url, "NO METADATA") {
				@Override
				public void success(ActionInvocation p1) {
					uPnPService.execute(playAction);
				}
				@Override
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                }
            };

         
            uPnPService.execute(setAVTransportURIAction);
        }
    }
	
    public void updateDevice(UPnPService uPnPService, Device device) {
        this.uPnPService = uPnPService;        
        this.device = device;
    }

}

