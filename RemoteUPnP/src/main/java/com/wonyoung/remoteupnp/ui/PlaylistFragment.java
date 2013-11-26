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
import com.wonyoung.remoteupnp.*;
import android.widget.*;
import org.fourthline.cling.support.model.item.*;

/**
 * Created by wonyoungjang on 13. 10. 18..
 */
public class PlaylistFragment extends Fragment {
    private UPnPService uPnPService;
    private PlaylistAdapter adapter;

	private void playFrom(int p0)
	{
		if (p0 >= adapter.getCount() - 1) {
			return;
		}
	    Item item = (Item) adapter.getItem(p0);
		String url = item.getFirstResource().getValue();
		playNext(url, p0+ 1);
	}

	private void playNext(String url, final int p0)
	{
		Device device = uPnPService.getRendererDevice();
        Service service = device.findService(new UDAServiceId("AVTransport"));

        if (service != null) {
			final ActionCallback playAction = new Play(service) {
				@Override
				public void success(ActionInvocation p1) {
					playFrom(p0);
				}
				
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
          

    private void play(String url) {
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
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist_layout, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final MainActivity activity = (MainActivity) getActivity();
        this.uPnPService = activity.getUPnPService();

        adapter = uPnPService.getPlaylistAdapter();
        ListView listView = (ListView) activity.findViewById(R.id.playlistView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					DIDLObject item = (DIDLObject) adapter.getItem(position);

					Res resource = item.getFirstResource();
					if (resource != null) {
						play(resource.getValue());
					}
				}
			});
			
		Button playAll = (Button) activity.findViewById(R.id.playAll);
		playAll.setOnClickListener(new View.OnClickListener() {

				public void onClick(View p1)
				{
					playFrom(0);
				}

		});
		
    }

}

