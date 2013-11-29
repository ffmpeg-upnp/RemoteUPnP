package com.wonyoung.remoteupnp.playlist;

import android.support.v4.app.FragmentActivity;
import android.widget.*;
import android.view.*;

import com.wonyoung.remoteupnp.ui.MainActivity;

import org.fourthline.cling.support.model.item.*;

import java.util.*;

public class PlaylistAdapter extends BaseAdapter {
    private ArrayList<Item> list = new ArrayList<Item>();
    private FragmentActivity activity;

    public PlaylistAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

    public void addAll(List<Item> items) {
        list.addAll(items);
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
        TextView tv = (TextView) convertView;
        if (tv == null) {
            tv = new TextView(parent.getContext());
        }

        tv.setText(list.get(position).getTitle());
        return tv;
    }

}
