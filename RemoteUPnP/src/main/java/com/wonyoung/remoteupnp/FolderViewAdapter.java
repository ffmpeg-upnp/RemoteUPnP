package com.wonyoung.remoteupnp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.DescMeta;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.fourthline.cling.support.model.*;
import org.fourthline.cling.support.model.item.Item;

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

//        holder.icon.setImageBitmap(null);
        holder.title.setText(item.getTitle());
		Res res = item.getFirstResource();
		if (res != null) {
            Item i = (Item) item;
			holder.duration.setText(res.getDuration());
            holder.info.setText(res.getProtocolInfo().getNetwork());

            if (position == 0)
            new AlbumArtLoadTask(holder.icon).execute(res.getValue());
		}

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
            String url = params[0];
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(url, new HashMap<String, String>());

            return null;
//            try {
//                byte[] art = retriever.getEmbeddedPicture();
//                return BitmapFactory.decodeByteArray(art, 0, art.length);
//            } catch (Exception e) {
//                return null;// BitmapFactory.decodeResource(activity.getResources(), android.R.drawable.ic_dialog_info);
//            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        }
    }
}
