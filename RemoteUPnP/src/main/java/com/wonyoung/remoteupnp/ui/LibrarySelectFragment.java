package com.wonyoung.remoteupnp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wonyoung.remoteupnp.FolderViewAdapter;
import com.wonyoung.remoteupnp.MediaServer;
import com.wonyoung.remoteupnp.OnMediaServerChangeListener;
import com.wonyoung.remoteupnp.R;
import com.wonyoung.remoteupnp.UPnPService;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;

/**
 * Created by wonyoungjang on 13. 10. 18..
 */
public class LibrarySelectFragment extends Fragment implements OnMediaServerChangeListener {
    private UPnPService uPnPService;
    public MediaServer mediaServer;
    private FolderViewAdapter adapter;


    private void play(String url) {
        Device device = uPnPService.getRendererDevice();
        Service service = device.findService(new UDAServiceId("AVTransport"));

        if (service != null) {
            ActionCallback setAVTransportURIAction = new SetAVTransportURI(service, url, "NO METADATA") {
                @Override
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                }
            };

            ActionCallback playAction = new Play(service) {

                @Override
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                }
            };

            uPnPService.execute(setAVTransportURIAction);
            uPnPService.execute(playAction);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_library_select, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final MainActivity activity = (MainActivity) getActivity();
        this.uPnPService = activity.getUPnPService();

        adapter = new FolderViewAdapter(activity);
        ListView listView = (ListView) activity.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DIDLObject item = (DIDLObject) adapter.getItem(position);
                mediaServer.browse(item.getId());

                Res resource = item.getFirstResource();
                if (resource != null) {
                    play(resource.getValue());
                }
            }
        });
        mediaServer = new MediaServer(uPnPService, adapter);

        uPnPService.setOnMediaServerChangeListener(this);
    }

    private void browseRoot() {
        mediaServer.browse("0");
    }

    @Override
    public void OnMediaServerChanged(Device device) {
        browseRoot();
    }
}
