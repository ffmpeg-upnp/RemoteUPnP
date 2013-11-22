package com.wonyoung.remoteupnp;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;

import java.util.ArrayList;

/**
 * Created by wonyoung.jang on 13. 11. 22.
 */
public class DeviceAdapter extends BaseAdapter implements DeviceSubscriber {
    public final static ServiceType SERVICE_TYPE_RENDERER = new UDAServiceType("AVTransport");
    public final static ServiceType SERVICE_TYPE_MEDIA_SERVER = new UDAServiceType("ContentDirectory");

    private Activity activity;
    private ServiceType serviceTypeFilter;
    protected ArrayList<Device> devices = new ArrayList<Device>();

    public DeviceAdapter(Activity activity, ServiceType serviceTypeFilter) {
        this.activity = activity;
        this.serviceTypeFilter = serviceTypeFilter;
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

    public void add(Device item) {
        for (Service service : item.getServices()) {
            ServiceType serviceType = service.getServiceType();
            if (serviceTypeFilter.equals(serviceType)) {
                int position = devices.indexOf(item);
                if (position >= 0) {
                    devices.set(position, item);
                } else {
                    devices.add(item);
                }
                update();
                break;
            }
        }
    }

    public void remove(Device item) {
        devices.remove(item);
        update();
    }
}
