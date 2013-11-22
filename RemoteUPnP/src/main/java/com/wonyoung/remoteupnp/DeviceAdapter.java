package com.wonyoung.remoteupnp;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;

/**
* Created by wonyoung.jang on 13. 11. 22.
*/
public class DeviceAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Device> devices;

    public DeviceAdapter(Activity activity, ArrayList<Device> deviceList) {
        this.activity = activity;
        this.devices = deviceList;
    }

    public DeviceAdapter(Activity activity) {
        this.activity = activity;
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
        }

        Device device = devices.get(position);
        view.setText(device.getDisplayString());
        return view;
    }

    public void update() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void clear() {
        devices.clear();
    }

    public Device get(int position) {
        return devices.get(position);
    }

    public void add(Device device) {
        devices.add(device);
    }

    public void remove(Device device) {
        devices.remove(device);
    }
}
