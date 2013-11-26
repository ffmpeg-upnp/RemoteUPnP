package com.wonyoung.remoteupnp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.DescMeta;

import java.util.ArrayList;

/**
 * Created by wonyoungjang on 2013. 11. 23..
 */
public class FolderViewAdapter extends BaseAdapter implements FolderSubscriber {

    private ArrayList<DIDLObject> list = new ArrayList<DIDLObject>();
    private Activity activity;

    public FolderViewAdapter(Activity activity) {
        this.activity = activity;
    }

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
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_file_content, parent, false);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.itemIcon);
            holder.title = (TextView) convertView.findViewById(R.id.itemTitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DIDLObject item = list.get(position);

        Log.d("item", "title: " + item.getTitle());
        for (DescMeta meta : item.getDescMetadata()) {
            Log.d("meta", "id: " + meta.getId());
            Log.d("meta", "namespace: " + meta.getNameSpace().toString());
            Log.d("meta", "type: " + meta.getType());
            Log.d("meta", "data: " + meta.getMetadata().toString());
        }

//        holder.icon.setImageBitmap(null);
        holder.title.setText(item.getTitle());
        return convertView;
    }

    @Override
    public void updatedFolderList(ArrayList<DIDLObject> updated) {
        list = updated;
        update();
    }

    private void update() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private class ViewHolder {

        public ImageView icon;
        public TextView title;
    }
}
