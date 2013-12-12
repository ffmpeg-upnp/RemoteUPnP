package com.wonyoung.remoteupnp.renderer;

import android.util.Log;

import com.wonyoung.remoteupnp.playlist.Playlist;
import com.wonyoung.remoteupnp.playlist.PlaylistAdapter;
import com.wonyoung.remoteupnp.service.UPnPService;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.model.item.Item;

import java.util.Map;
import com.wonyoung.remoteupnp.ui.*;

/**
 * Created by wonyoungjang on 2013. 11. 23..
 */
public class Renderer {
    private UPnPService uPnPService;
    private Device device;
    private Playlist playlist;

	private MainActivity activityDebug;

    public Renderer(Playlist playlist) {

        this.playlist = playlist;
    }

	public void debugToastTo(MainActivity p0)
	{
		this.activityDebug = p0;
		// TODO: Implement this method
	}

    public void play(String url) {
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

    public void playFrom(int index) {

        if (index >= playlist.size()) {
            return;
        }
        Item item = (Item) playlist.get(index);
        String url = item.getFirstResource().getValue();
        playNext(url, index + 1);
    }

    private void playNext(String url, final int p0) {
        Service service = device.findService(new UDAServiceId("AVTransport"));

        if (service != null) {
            final SubscriptionCallback callback = new SubscriptionCallback(service) {
                @Override
                protected void failed(GENASubscription sub, UpnpResponse response,
                                      Exception ex, String arg3) {
                    Log.d("remoteUpnp", arg3 + " " + createDefaultFailureMessage(response, ex));
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
                    try {
                        translateLastchange(value);
                    } catch (Exception e) {
                    }
                    for (Map.Entry<String, StateVariableValue> entry : values.entrySet()) {
						String s = entry.getKey() + " is: " + entry.getValue().toString();
						activityDebug.toast(s);
                        Log.d("remoteUpnp", s);

                    }
                }

                private void translateLastchange(StateVariableValue value) throws Exception {
                    LastChange lastChange = new LastChange(new AVTransportLastChangeParser(),
                            value.toString());
                    TransportState state = lastChange.getEventedValue(0,
                            AVTransportVariable.TransportState.class).getValue();
                    if (TransportState.STOPPED.equals(state)) {
                        playFrom(p0);
                        end();
                    }
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

    public void updateDevice(UPnPService uPnPService, Device device) {
        this.uPnPService = uPnPService;        
        this.device = device;
    }

}

