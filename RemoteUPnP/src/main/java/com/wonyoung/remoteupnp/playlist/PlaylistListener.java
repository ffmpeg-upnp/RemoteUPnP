package com.wonyoung.remoteupnp.playlist;

import org.fourthline.cling.support.model.item.Item;

import java.util.List;

/**
 * Created by wonyoungjang on 2013. 11. 30..
 */
public interface PlaylistListener
{
    void addAll(List<Item> items);
	void set(List<Item> items);
}
