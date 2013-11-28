package com.wonyoung.remoteupnp.ui;

import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.wonyoung.remoteupnp.*;
import java.util.*;
import java.util.Map.*;
import org.fourthline.cling.controlpoint.*;
import org.fourthline.cling.model.action.*;
import org.fourthline.cling.model.gena.*;
import org.fourthline.cling.model.message.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.state.*;
import org.fourthline.cling.model.types.*;
import org.fourthline.cling.support.avtransport.callback.*;
import org.fourthline.cling.support.avtransport.lastchange.*;
import org.fourthline.cling.support.lastchange.*;
import org.fourthline.cling.support.model.*;
import org.fourthline.cling.support.model.item.*;

/**
 * Created by wonyoungjang on 13. 10. 18..
 */
public class PlaylistFragment extends Fragment {
    private UPnPService uPnPService;
    private PlaylistAdapter adapter;

	private void playFrom(int p0)
	{
		if (p0 >= adapter.getCount()) {
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


            final SubscriptionCallback callback = new SubscriptionCallback(service) {

                @Override
                protected void failed(GENASubscription sub, UpnpResponse response,
									  Exception ex, String arg3) {
                    Log.d("remoteUpnp", arg3 + " "+ createDefaultFailureMessage(response, ex));
                }

                @Override
                protected void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
                    Log.d("remoteUpnp", "Missed events: " + numberOfMissedEvents);
                }

                @Override
                protected void eventReceived(GENASubscription sub) {
                    Log.d("remoteUpnp", "Event: " + sub.getCurrentSequence().getValue());
                    Map<String, StateVariableValue> values = sub.getCurrentValues();
                    StateVariableValue value = values.get("LastChange");
                    try
					{
						translateLastchange(value);
					}
					catch (Exception e)
					{}
                    for (Entry<String, StateVariableValue> entry : values.entrySet())
					{
                        Log.d("remoteUpnp", entry.getKey() + " is: " + entry.getValue().toString());

                    }
                }

				private void translateLastchange(StateVariableValue value) throws Exception
				{
					LastChange lastChange = new LastChange(new AVTransportLastChangeParser(),
														   value.toString());
					TransportState state = lastChange.getEventedValue(0,
																	  AVTransportVariable.TransportState.class).getValue();
					if (TransportState.STOPPED.equals(state))
					{
playFrom(p0);
						end();
					}
					// TODO: Implement this method
				}

                @Override
                protected void established(GENASubscription sub) {
                    Log.d("remoteUpnp", "Established: " + sub.getSubscriptionId());
                }

                @Override
                protected void ended(GENASubscription sub, CancelReason arg1,
									 UpnpResponse arg2) {
                    Log.d("remoteUpnp", "ended: " + sub.getSubscriptionId());
                }
            };
            
			
			final ActionCallback playAction = new Play(service) {
	            @Override
                public void success(ActionInvocation p1) {
                    uPnPService.execute(callback);
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
					Toast.makeText(activity.getApplicationContext(), "" + resource.getDuration(),
					Toast.LENGTH_SHORT).show();
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

