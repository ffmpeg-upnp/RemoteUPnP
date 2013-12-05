package com.wonyoung.remoteupnp.mediaserver;

import java.util.ArrayList;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wonyoung.remoteupnp.R;
import com.wonyoung.remoteupnp.mediaserver.FolderSubscriber;
import android.widget.*;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Created by wonyoungjang on 2013. 11. 23..
 */
public class FolderViewAdapter extends BaseAdapter {

    private ArrayList<DIDLObject> list = new ArrayList<DIDLObject>();
    private Activity activity;
    private LruCache<String, Bitmap> mMemoryCache;

    public FolderViewAdapter(Activity activity) {
        this.activity = activity;
        final int maxMemroy = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemroy / 8;
        this.mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
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
			holder.duration = (TextView) convertView.findViewById(R.id.itemDuration);
            holder.info = (TextView) convertView.findViewById(R.id.itemInfo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DIDLObject item = list.get(position);

        Log.d("item", "title: " + item.getTitle());
        for (Res res : item.getResources()) {
            ProtocolInfo protocolInfo = res.getProtocolInfo();
            Log.d("res", "name: " + protocolInfo);
            if (protocolInfo != null) {
                Log.d("res", "info: " + protocolInfo.getAdditionalInfo());
                Log.d("res", "format: " + protocolInfo.getContentFormat());
                Log.d("res", "mime: " + protocolInfo.getContentFormatMimeType());
                Log.d("res", "network: " + protocolInfo.getNetwork());
            }
            Log.d("res", "value: " + res.getValue());
            Log.d("res", "resolution: " + res.getResolution());
            Log.d("res", "uri: " + res.getImportUri());
	
			
        }
		
        holder.title.setText(item.getTitle());
		Res res = item.getFirstResource();
		String albumArtUrl = null;
		if (res != null) {
			holder.duration.setText(res.getDuration());
            holder.info.setText(res.getProtocolInfo().getNetwork());
		}
		URI uri = item.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM_ART_URI.class);
		if (uri != null)
            albumArtUrl = uri.toString();

        if (albumArtUrl != null) {
            Bitmap bitmap = getBitmapFromMemCache(albumArtUrl);

            if (bitmap != null) {
                holder.icon.setImageBitmap(bitmap);
            } else {
//            holder.icon.setImageResource(R.drawable.i);
                new AlbumArtLoadTask(holder.icon).execute(albumArtUrl);
            }

        }
        return convertView;
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

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

		public TextView duration;
        public TextView info;
    }

    private class AlbumArtLoadTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public AlbumArtLoadTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
			Bitmap bm;
			
            String url = params[0];
            if (url == null) {
                bm = BitmapFactory.decodeResource(activity.getResources(), android.R.drawable.ic_menu_more);
            }
			else {
            try {
                InputStream in = new URL(url).openStream();
                bm = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
				bm = BitmapFactory.decodeResource(activity.getResources(), android.R.drawable.ic_media_play);
            }
			}
            addBitmapToMemoryCache(url, bm);
			return bm;
//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            retriever.setDataSource(url, new HashMap<String, String>());

//            try {
//                byte[] art = retriever.getEmbeddedPicture();
//                return BitmapFactory.decodeByteArray(art, 0, art.length);
//            } catch (Exception e) {
//            }
   //         return BitmapFactory.decodeResource(activity.getResources(), android.R.drawable.ic_media_play);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        }
    }

    private void addBitmapToMemoryCache(String url, Bitmap bm) {
        if (mMemoryCache.get(url) == null) {
            mMemoryCache.put(url, bm);
        }
    }
}
