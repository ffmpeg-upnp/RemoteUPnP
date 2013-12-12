package com.wonyoung.remoteupnp.playlist;

import org.fourthline.cling.support.model.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

/**
 * Created by wonyoungjang on 2013. 11. 30..
 */
public class Playlist {
    ArrayList<Item> list = new ArrayList<Item>();
    private PlaylistListener listener;

	public void shuffle()
	{
		Collections.shuffle(list);
		listener.set(list);
		// TODO: Implement this method
	}

    public void add(List<Item> items) {
        list.addAll(items);
        if (listener != null) {
            listener.addAll(items);
        }
    }

    public void setListener(PlaylistListener listener) {
        this.listener = listener;
        listener.addAll(list);
    }

    public int size() {
        return list.size();
    }

    public Item get(int index) {
        return list.get(index);
    }
}
