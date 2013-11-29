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

import com.wonyoung.remoteupnp.PlaylistAdapter;
import com.wonyoung.remoteupnp.R;
import com.wonyoung.remoteupnp.Renderer;
import com.wonyoung.remoteupnp.UPnPService;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;

/**
 * Created by wonyoungjang on 13. 10. 18..
 */
public class PlaylistFragment extends Fragment {
    private PlaylistAdapter adapter;
    private Renderer renderer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist_layout, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final MainActivity activity = (MainActivity) getActivity();
        UPnPService uPnPService = activity.getUPnPService();
        this.renderer = activity.getRenderer();

        adapter = uPnPService.getPlaylistAdapter();
        ListView listView = (ListView) activity.findViewById(R.id.playlistView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DIDLObject item = (DIDLObject) adapter.getItem(position);

                Res resource = item.getFirstResource();
                if (resource != null) {
                    renderer.play(resource.getValue());
                }
                Toast.makeText(activity.getApplicationContext(), "" + resource.getDuration(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        Button playAll = (Button) activity.findViewById(R.id.playAll);
        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                renderer.playFrom(0);
            }
        });
    }
}

