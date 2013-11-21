package com.wonyoung.remoteupnp;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wonyoungjang on 13. 10. 18..
 */
public class LibrarySelectFragment extends Fragment {
    private final UPnPService uPnPService;
    private List<Item> fileList;
    private List<Container> folderList;


//    private void onRendererSelected(Device device) {
//        play(device);
//    }

//    private void play(Device device) {
//        Service service = device.findService(new UDAServiceId("AVTransport"));
//
//        if (service != null) {
//            ActionCallback setAVTransportURIAction = new SetAVTransportURI(service, "http://192.168.1.123:5001/get/0$1$2$1$2/02+VARIACION+01.mp3", "NO METADATA") {
//                @Override
//                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {
//
//                }
//            };
//
//            ActionCallback playAction = new Play(service) {
//
//                @Override
//                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {
//
//                }
//            };
//
//            upnpService.getControlPoint().execute(setAVTransportURIAction);
//            upnpService.getControlPoint().execute(playAction);
//        }
//    }

//    public void onMediaServerSelected(Device device) {
//        Toast.makeText(this, "Media Server : " + device.getDisplayString(), Toast.LENGTH_SHORT).show();
//        mediaServer = device;
//        browse(mediaServer.findService(new UDAServiceId("ContentDirectory")));
//    }

    public void browse(Service service) {

        ActionCallback rootBrowseAction = new Browse(service, "0", BrowseFlag.DIRECT_CHILDREN) {

            @Override
            public void received(ActionInvocation actionInvocation, DIDLContent didlContent) {
                fileList = didlContent.getItems();
                folderList = didlContent.getContainers();

                final ArrayList<String> list = new ArrayList<String>();

                Log.e("wonyoung", "folder count : " + folderList.size());
                for (Container folder : folderList) {
                    list.add(folder.getTitle());
                    Log.e("wonyoung", folder.getTitle() + " id-" + folder.getId());
                }

                Log.e("wonyoung", "files count : " + fileList.size());
                for (Item item : fileList) {
                    if (item != null) {
                        list.add(item.getTitle());
                        Log.e("wonyoung", String.format("[%s]", item.getTitle()));
                        if (item.getFirstResource() != null)
                            Log.e("wonyoung", String.format("      :[%s] ", item.getFirstResource().getValue()));

                    }
                }

                final Context context = getActivity();

                ListView listView = (ListView) getActivity().findViewById(R.id.listView);

                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_activated_1,
                        list);
                ArrayAdapter<Container> folderAdapter = new ArrayAdapter<Container>(context, android.R.layout.simple_list_item_activated_1,
                        folderList);

                BaseAdapter adapter = new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return list.size();
                    }

                    @Override
                    public Object getItem(int position) {
                        return list.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) convertView;
                        if (textView == null) {
                            textView = new TextView(parent.getContext());
                        }
                        textView.setText(list.get(position));
                        return textView;
                    }
                };

                listView.setAdapter(adapter);
            }

            @Override
            public void updateStatus(Status status) {

            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {

            }
        };

        uPnPService.execute(rootBrowseAction);
    }

    public LibrarySelectFragment(UPnPService service) {
        this.uPnPService = service;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_library_select, container, false);

        return rootView;
    }
}
