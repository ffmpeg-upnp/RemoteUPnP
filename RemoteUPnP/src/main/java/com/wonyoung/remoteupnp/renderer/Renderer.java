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

import java.util.*;
import java.net.*;

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

    public void playShuffle() {
        ArrayList<Integer> orders = createOrders();
        Collections.shuffle(orders);

        playFrom(0, orders);
    }

    private ArrayList<Integer> createOrders() {
        ArrayList<Integer> orders = new ArrayList<Integer>();

        for (Integer i = 0; i < playlist.size(); i++) {
            orders.add(i);
        }
        return orders;
    }

    public void debugToastTo(MainActivity p0) {
        this.activityDebug = p0;
        // TODO: Implement this method
    }

    public void play(String url) {
        playNow(url, null);
    }

    private void play(final int index) {
        if (index >= playlist.size()) {
            return;
        }

        String url = getPlayUrl(index);
        playNow(url, null);
    }

    private void playNow(final String url, final Runnable onSuccess) {
        Service service = device.findService(new UDAServiceId("AVTransport"));

        if (service != null) {
            final ActionCallback playAction = new Play(service) {
                @Override
                public void success(ActionInvocation p1) {
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
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

    public void playFrom(int index) {
        ArrayList<Integer> orders = createOrders();

        playFrom(index, orders);
    }

    private void playFrom(final int index, final List<Integer> orders) {
        if (index >= orders.size()) {
            return;
        }

        if (index == orders.size() - 1) {
            play(orders.get(index));
            return;
        }

        startPlaylist(index, orders);
    }

    private void startPlaylist(int index, final List<Integer> orders) {
        final int next = index + 1;
        String firstUrl = getPlayUrl(orders.get(index));

        Service service = device.findService(new UDAServiceId("AVTransport"));
        if (service != null) {
            final SubscriptionCallback subscriptionCallback = new SubscriptionCallback(service) {
                private String currentPlayingUrl;

                @Override
                protected void failed(GENASubscription sub, UpnpResponse response,
                                      Exception ex, String arg3) {
                    activityDebug.toast(arg3 + " " + createDefaultFailureMessage(response, ex));
                }

                @Override
                protected void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
                    activityDebug.toast("Missed events: " + numberOfMissedEvents);
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
                        activityDebug.toast(entry.getKey() + " is: " + entry.getValue().toString());
                    }
                }

                private void translateLastchange(StateVariableValue value) throws Exception {
                    LastChange lastChange = new LastChange(new AVTransportLastChangeParser(),
                            value.toString());
                    AVTransportVariable.AVTransportURI transportURIEventedValue = lastChange.getEventedValue(0,
                            AVTransportVariable.AVTransportURI.class);
                    if (transportURIEventedValue != null) {
                        currentPlayingUrl = transportURIEventedValue.toString();

                        activityDebug.toast("Url---" + currentPlayingUrl);
                    }

                    TransportState state = lastChange.getEventedValue(0,
                            AVTransportVariable.TransportState.class).getValue();
                    if (TransportState.STOPPED.equals(state)) {
                        playFrom(next, orders);
                        end();
                    }
                }

                @Override
                protected void established(GENASubscription sub) {
                    activityDebug.toast("Established: " + sub.getSubscriptionId());
                }

                @Override
                protected void ended(GENASubscription sub, CancelReason arg1,
                                     UpnpResponse arg2) {
                    activityDebug.toast("ended: " + sub.getSubscriptionId());
                }
            };

            Runnable onSuccess = new Runnable() {
                @Override
                public void run() {
                    uPnPService.execute(subscriptionCallback);
                }
            };

            playNow(firstUrl, onSuccess);
        }
    }

    private String getPlayUrl(int playIndex) {
        Item item = (Item) playlist.get(playIndex);
        return item.getFirstResource().getValue();
    }

    public void updateDevice(UPnPService uPnPService, Device device) {
        this.uPnPService = uPnPService;
        this.device = device;
    }

}

