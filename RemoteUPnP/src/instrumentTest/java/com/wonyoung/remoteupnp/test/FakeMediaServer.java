package com.wonyoung.remoteupnp.test;

import com.wonyoung.remoteupnp.folder.UPnPFolder;

import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.meta.UDAVersion;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDN;

import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 * Created by wonyoungjang on 13. 10. 19..
 */
public class FakeMediaServer extends Device {
    public static final String ROOT_FOLDER_NAME = "0";
    public static final UPnPFolder SUB_FOLDER = new UPnPFolder();

    public FakeMediaServer() throws ValidationException {
        super(new DeviceIdentity(new UDN("")));
    }

    @Override
    public Service[] getServices() {
        return new Service[0];
    }

    @Override
    public Device[] getEmbeddedDevices() {
        return new Device[0];
    }

    @Override
    public Device getRoot() {
        return null;
    }

    @Override
    public Device findDevice(UDN udn) {
        return null;
    }

    @Override
    public Device newInstance(UDN udn, UDAVersion version, DeviceType type, DeviceDetails details, Icon[] icons, Service[] services, List embeddedDevices) throws ValidationException {
        return null;
    }

    @Override
    public Service newInstance(ServiceType serviceType, ServiceId serviceId, URI descriptorURI, URI controlURI, URI eventSubscriptionURI, Action[] actions, StateVariable[] stateVariables) throws ValidationException {
        return null;
    }

    @Override
    public Device[] toDeviceArray(Collection col) {
        return new Device[0];
    }

    @Override
    public Service[] newServiceArray(int size) {
        return new Service[0];
    }

    @Override
    public Service[] toServiceArray(Collection col) {
        return new Service[0];
    }

    @Override
    public Resource[] discoverResources(Namespace namespace) {
        return new Resource[0];
    }

    public void hasReceivedRequestFolder(String folder) {

    }

    public void sendFolders(UPnPFolder folder) {

    }


}
