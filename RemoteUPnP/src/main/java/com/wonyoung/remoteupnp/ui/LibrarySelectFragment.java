package com.wonyoung.remoteupnp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.wonyoung.remoteupnp.R;
import com.wonyoung.remoteupnp.mediaserver.FolderSubscriber;
import com.wonyoung.remoteupnp.mediaserver.FolderViewAdapter;
import com.wonyoung.remoteupnp.renderer.Renderer;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;

import java.util.ArrayList;

/**
 * Created by wonyoungjang on 13. 10. 18..
 */
public class LibrarySelectFragment extends Fragment implements FolderSubscriber {
    private FolderViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_library_select, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final MainActivity activity = (MainActivity) getActivity();

        adapter = new FolderViewAdapter(activity);
        ListView listView = (ListView) activity.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DIDLObject item = (DIDLObject) adapter.getItem(position);
                activity.browse(item.getId());

                Res resource = item.getFirstResource();
                if (resource != null) {
                    activity.play(resource.getValue());
                }
            }
        });

        activity.setMediaServerListener(this);

        Button addAll = (Button) activity.findViewById(R.id.addAll);
        addAll.setOnClickListener(new View.OnClickListener() {

            public void onClick(View p1) {
                activity.addAll();
            }
        });
    }

    @Override
    public void updatedFolderList(ArrayList<DIDLObject> updated) {
        adapter.updatedFolderList(updated);
    }
}
