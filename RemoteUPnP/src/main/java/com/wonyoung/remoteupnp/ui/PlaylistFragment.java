package com.wonyoung.remoteupnp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.wonyoung.remoteupnp.playlist.PlaylistAdapter;
import com.wonyoung.remoteupnp.R;
import com.wonyoung.remoteupnp.playlist.PlaylistListener;
import com.wonyoung.remoteupnp.renderer.Renderer;
import com.wonyoung.remoteupnp.service.UPnPService;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;

import java.util.List;

/**
 * Created by wonyoungjang on 13. 10. 18..
 */
public class PlaylistFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist_layout, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final MainActivity activity = (MainActivity) getActivity();
//        UPnPService uPnPService = activity.getUPnPService();

        final PlaylistAdapter adapter = new PlaylistAdapter(activity);//uPnPService.getPlaylistAdapter();
        ListView listView = (ListView) activity.findViewById(R.id.playlistView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DIDLObject item = (DIDLObject) adapter.getItem(position);

                Res resource = item.getFirstResource();
                if (resource != null) {
                    activity.play(resource.getValue());
                }
                Toast.makeText(activity.getApplicationContext(), "" + resource.getDuration(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        activity.setPlaylistListener(adapter);
        Button playAll = (Button) activity.findViewById(R.id.playAll);
        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                activity.playFrom(0);
            }
        });
		
		Button surffle = (Button) activity.findViewById(R.id.surffle);
		surffle.setOnClickListener(new View.OnClickListener() {

				public void onClick(View p1)
				{
					activity.shufflePlay();
					// TODO: Implement this method
				}
				
			
		});
    }
}

