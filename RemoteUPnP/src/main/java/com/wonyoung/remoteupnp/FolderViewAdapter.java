package com.wonyoung.remoteupnp;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.fourthline.cling.support.model.DIDLObject;

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
        TextView textView = (TextView) convertView;
        if (textView == null) {
            textView = new TextView(parent.getContext());
        }
        textView.setText(list.get(position).getTitle());
        return textView;
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
}
