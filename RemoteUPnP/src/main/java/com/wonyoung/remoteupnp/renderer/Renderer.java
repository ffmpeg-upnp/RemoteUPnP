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

	public void playShuffle()
	{
		ArrayList<Integer> orders = createOrders();
		Collections.shuffle(orders);
		
		playFrom(0, orders);
	}

	private ArrayList<Integer> createOrders()
	{
		ArrayList<Integer> orders = new ArrayList<Integer>();

		for (Integer i = 0; i < playlist.size(); i++)
		{
			orders.add(i);
		}
		return orders;
	}

	public void debugToastTo(MainActivity p0)
	{
		this.activityDebug = p0;
		// TODO: Implement this method
	}

    public void play(String url) {
		playNow(url, null);
	}
	
	private void playNow(final String url, final SubscriptionCallback callback) {
        Service service = device.findService(new UDAServiceId("AVTransport"));

        if (service != null) {
			final ActionCallback playAction = new Play(service) {
				@Override
                public void success(ActionInvocation p1) {
					if (callback != null) {
						uPnPService.execute(callback);
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
	
	public void playFrom(int index)
	{
		ArrayList<Integer> orders = createOrders();

		playFrom(index, orders);
		// TODO: Implement this method
	}
	
    private void playFrom(final int index, final List<Integer> orders) {

        if (index >= orders.size()) {
            return;
        }
        Item item = (Item) playlist.get(orders.get(index));
        String url = item.getFirstResource().getValue();
		final int next = index + 1;
		Service service = device.findService(new UDAServiceId("AVTransport"));
		final SubscriptionCallback callback = new SubscriptionCallback(service) {

			private String currentPlayingUrl;
			private int playIndex = next;

			private Object nextPlayingUrl;
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
				AVTransportVariable.AVTransportURI transportURIEventedValue = lastChange.getEventedValue(0,
																										 AVTransportVariable.AVTransportURI.class);
				if (transportURIEventedValue != null)
 {
					currentPlayingUrl = transportURIEventedValue.toString();
					
					if (currentPlayingUrl.equals(nextPlayingUrl)) {
						playIndex++;
						nextPlayingUrl = "transitioning";
						setNext(playIndex, orders);
					}
				
					activityDebug.toast("Url---"+currentPlayingUrl);
					//      playFrom(next, orders);
					//      end();
				}
				
				AVTransportVariable.NextAVTransportURI nextTransportURIEventedValue = lastChange.getEventedValue(0,
																												 AVTransportVariable.NextAVTransportURI.class);

				if (nextTransportURIEventedValue != null) {
					String url = nextTransportURIEventedValue.toString();
					nextPlayingUrl = url;

					activityDebug.toast("nextUrl---"+url);
				}
				
				TransportState state = lastChange.getEventedValue(0,
																  AVTransportVariable.TransportState.class).getValue();
				if (TransportState.STOPPED.equals(state)) {
					end();
				}												  
			}

			@Override
			protected void established(GENASubscription sub) {

				String s = "Established: " + sub.getSubscriptionId();
				activityDebug.toast(s);
				Log.d("remoteUpnp", s);
			}

			@Override
			protected void ended(GENASubscription sub, CancelReason arg1,
								 UpnpResponse arg2) {
				String s = "ended: " + sub.getSubscriptionId();
				activityDebug.toast(s);
		//		playFrom(next, orders);
	//			end();
				Log.d("remoteUpnp", s);
			}
		};
		
		playNow(url, callback);
		setNext(next, orders);
    }

	private void setNext(final int next, final List<Integer> orders) {
		if (next >= orders.size()) {
            return;
        }
		Item item = (Item) playlist.get(orders.get(next));
        String url = item.getFirstResource().getValue();
		Service service = device.findService(new UDAServiceId("AVTransport"));

        if (service != null) {

            ActionCallback setAVTransportURIAction = new SetAVTransportURI(service, url, "NO METADATA") {
				@Override
				public void success(ActionInvocation p1) {
				
				}
				@Override
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                }
            };

            uPnPService.execute(setAVTransportURIAction);
        }
		
	}
    private void playNext(String url, final int next, final List<Integer> orders) {
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
                  //      playFrom(next, orders);
                  //      end();
                    }
                }

                @Override
                protected void established(GENASubscription sub) {
					
                    String s = "Established: " + sub.getSubscriptionId();
					activityDebug.toast(s);
					Log.d("remoteUpnp", s);
                }

                @Override
                protected void ended(GENASubscription sub, CancelReason arg1,
                                     UpnpResponse arg2) {
                    String s = "ended: " + sub.getSubscriptionId();
					activityDebug.toast(s);
					playFrom(next, orders);
					end();
					Log.d("remoteUpnp", s);
                }
            };
			
			playNow(url, callback);
        }
    }

    public void updateDevice(UPnPService uPnPService, Device device) {
        this.uPnPService = uPnPService;        
        this.device = device;
    }

}

