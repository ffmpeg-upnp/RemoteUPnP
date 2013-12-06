package com.wonyoung.remoteupnp.device;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wonyoung.remoteupnp.R;

/**
 * Created by wonyoung.jang on 13. 11. 22.
 */
public class DeviceAdapter extends BaseAdapter {
    public final static ServiceType SERVICE_TYPE_RENDERER = new UDAServiceType("AVTransport");
    public final static ServiceType SERVICE_TYPE_MEDIA_SERVER = new UDAServiceType("ContentDirectory");
// move to filter
    private Activity activity;
    private ServiceType serviceTypeFilter;
    protected ArrayList<Device> devices = new ArrayList<Device>();

	private Device userSelected = null;
	private Device selectedDevice = null;
	
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
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_device_content, parent, false);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.deviceIcon);
            holder.title = (TextView) convertView.findViewById(R.id.deviceTitle);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        Device device = devices.get(position);
//        URL url = ((RemoteDevice) device).getIdentity().getDescriptorURL();
//        Log.d("meta", "device: " + device.getDisplayString());
//        Log.d("meta", "tostring: " + device.toString());
//        Log.d("meta", "uri: " + ((RemoteDevice)device).getIdentity().getDescriptorURL().getAuthority());
//        for (Icon icon : device.findIcons()) {
//            Log.d("meta", "icon uri: " + icon.getUri().toString());
//        }
//        if (device.hasIcons()) {
//            Icon icon = peakIcon(device.getIcons());
//            String iconUrl = url.getProtocol() + "://" + url.getAuthority() + icon.getUri();
//            new DownloadImageTask(holder.icon).execute(iconUrl);
//        }
        holder.title.setText(device.getDisplayString());
        return convertView;
    }

    private Icon peakIcon(Icon[] icons) {
        Icon selected = null;
        int minDiff = Integer.MAX_VALUE;
        for (Icon i : icons) {
            int diff = Math.abs(i.getHeight() - i.getWidth());
            if (diff <= minDiff) {
                minDiff = diff;
                selected = i;
            }
        }
        return selected;
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
					if (item == userSelected) {
						selectedDevice = item;
					}
					if (selectedDevice == null) {
						selectedDevice = item;
					}
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

    private class ViewHolder {
        public ImageView icon;
        public TextView title;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{
        private final ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bm = null;
            try {
                InputStream in = new URL(url).openStream();
                bm = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
