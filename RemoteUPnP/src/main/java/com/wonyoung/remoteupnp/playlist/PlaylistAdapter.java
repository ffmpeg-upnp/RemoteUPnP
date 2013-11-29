package com.wonyoung.remoteupnp.playlist;
import android.widget.*;
import android.view.*;
import org.fourthline.cling.support.model.item.*;
import java.util.*;

public class PlaylistAdapter extends BaseAdapter 
{
	private ArrayList<Item> list = new ArrayList<Item>();
	public void add(Item item)
	{
		list.add(item);
	}

	public int getCount()
	{
		return list.size();
	}

	public Object getItem(int p1)
	{
		return list.get(p1);
	}

	public long getItemId(int p1)
	{
		return 0;
	}

	public View getView(int p1, View p2, ViewGroup p3)
	{
		TextView tv = (TextView) p2;
		if (tv == null) {
			tv = new TextView(p3.getContext());
		}
		
		tv.setText(list.get(p1).getTitle());
		return tv;
	}
	
}
