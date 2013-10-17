package com.wonyoung.remoteupnp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.meta.Service;

import java.util.ArrayList;

/**
 * Created by wonyoungjang on 13. 10. 17..
 */
public class DeviceSelectFragment extends Fragment {

    private ArrayList<Device> rendererList;
    private ArrayList<Device> mediaServiceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device_select, container, false);

        Bundle args = savedInstanceState;
        if (args == null) {
            args = getArguments();
        }

        restoreDeviceListFromBundle(args);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();

        if (savedInstanceState != null) {
            restoreDeviceListFromBundle(savedInstanceState);
        }

        ListView rendererListView = (ListView) activity.findViewById(R.id.renderer_list);
        rendererListView.setAdapter(new DeviceAdapter(rendererList));

        rendererListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog dialog = new AlertDialog.Builder(activity).create();
                Device device = (Device) parent.getItemAtPosition(position);
                dialog.setTitle(device.toString());

                dialog.setMessage(getDetailsMessage(device));
                dialog.setButton(
                        "Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }
                );
                dialog.show();
                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                textView.setTextSize(12);
            }

            private String getDetailsMessage(Device device) {
                StringBuilder sb = new StringBuilder();
                DeviceDetails detail = device.getDetails();
                sb.append("BaseURL : " + detail.getBaseURL() + "\n");
                sb.append("FriendlyName : " + detail.getFriendlyName() + "\n");
                sb.append("SerialNumber : " + detail.getSerialNumber() + "\n");
                sb.append("Upc : " + detail.getUpc() + "\n");
                sb.append("PresentationURI : " + detail.getPresentationURI() + "\n");

                ModelDetails modelDetails = detail.getModelDetails();
                sb.append("\n");
                sb.append("ModelDescription : " + modelDetails.getModelDescription() + "\n");
                sb.append("ModelName : " + modelDetails.getModelName() + "\n");
                sb.append("ModelNumber : " + modelDetails.getModelNumber() + "\n");
                sb.append("ModelURI : " + modelDetails.getModelURI() + "\n");

                sb.append("\n\n");
                if (device.isFullyHydrated()) {
                    for (Service service : device.getServices()) {
                        sb.append(service.getServiceType()).append("\n");
                        for (Action action : service.getActions()) {
                            sb.append("(" + action.getName() + ")\n");
                        }
                        sb.append("\n");
                    }
                } else {
                    sb.append("Device Details not yet Available");
                }

                return sb.toString();
            }
        });


        DeviceAdapter mediaServerListAdapter = new DeviceAdapter(mediaServiceList);
        ListView mediaServerListView = (ListView) activity.findViewById(R.id.media_server_list);
        mediaServerListView.setAdapter(mediaServerListAdapter);

    }

    private void restoreDeviceListFromBundle(Bundle args) {
        rendererList = (ArrayList<Device>) args.getSerializable(MainActivity.ARG_RENDER_LIST);
        mediaServiceList = (ArrayList<Device>) args.getSerializable(MainActivity.ARG_MEDIA_SERVER_LIST);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(MainActivity.ARG_RENDER_LIST, rendererList);
        outState.putSerializable(MainActivity.ARG_MEDIA_SERVER_LIST, mediaServiceList);
    }

    private class DeviceAdapter extends BaseAdapter {

        private ArrayList<Device> devices;

        public DeviceAdapter(ArrayList<Device> devices) {
            this.devices = devices;
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) convertView;
            if (view == null) {
                view = new TextView(parent.getContext());

            } else {

            }

            Device device = devices.get(position);
            view.setText(device.getDisplayString());
            return view;
        }
    }
}
