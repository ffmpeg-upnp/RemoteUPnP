package com.wonyoung.remoteupnp.test;

import com.wonyoung.remoteupnp.folder.UPnPFolder;

import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.meta.UDAVersion;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.model.AVTransport;

import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 * Created by wonyoungjang on 13. 10. 19..
 */
public class FakeMediaServer extends LocalDevice {
    public static final String ROOT_FOLDER_NAME = "0";
    public static final UPnPFolder SUB_FOLDER = new UPnPFolder();

    public static FakeMediaServer createDevice() throws ValidationException {
        DeviceType type = new UDADeviceType("ContentDirectory", 1);
        DeviceDetails detail = new DeviceDetails("Fake MediaServer",
                new ManufacturerDetails("RemoteUPnP"),
                new ModelDetails("FakeMediaServer", "A fake media server device.", "v1"));
        LocalService service = new AnnotationLocalServiceBinder().read(AbstractContentDirectoryService.class);
        service.setManager(new DefaultServiceManager<AbstractContentDirectoryService>(service,
                AbstractContentDirectoryService.class));

        return new FakeMediaServer(new DeviceIdentity(new UDN("def")),
                type, detail, createDefaultDeviceIcon(), service);
    }

    private static Icon createDefaultDeviceIcon() {
        return new Icon("image/png", 48, 48, 8,
                "icon.png",
                "89504E470D0A1A0A0000000D494844520000002D000000300803000000B28C03ED0000001974455874536F6674" +
                        "776172650041646F626520496D616765526561647971C9653C00000300504C5445EEF2DBAAAAABF5F5" +
                        "F5EEEEF2D1DDBABCC99DCBD2CBC5D0AB88888AFCFCFCD6E6B1CEDAB0BEC9A1F1F1F1F1F5DCDCEAB6D6" +
                        "E2B6D9E4BAB5C78DEAEECCDAE5BCB3BA95D6E1B8DDEEB9C4CCBDB2B3B3CDD9BAE6EEC2F6F6F8D9EAAD" +
                        "DBDDE2EEF3E1C4CEABA4A3A5CDD9AED1D1D1CDDDA9DDECB6D4E3B1DDEDB1656466FAFAFABCBCBCC9D9" +
                        "A6D2DDB5D8E6B4CDDAC2D8E2B9D5DADB899A6BD9DADD9BA684DAEAB0D5E7AACBD6C4F8F8F8C6D2A7EA"
        );
    }

    public FakeMediaServer(DeviceIdentity identity, DeviceType type, DeviceDetails detail, Icon icon, LocalService service) throws ValidationException {
        super(identity, type, detail, icon, service);
    }

    public void hasReceivedRequestFolder(String folder) {

    }

    public void sendFolders(UPnPFolder folder) {

    }


}
