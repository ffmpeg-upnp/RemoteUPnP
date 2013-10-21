package com.wonyoung.remoteupnp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by wonyoungjang on 13. 10. 17..
 */
public class DeviceSelectFragment extends Fragment {

    private ListAdapter rendererAdapter;
    private AdapterView.OnItemClickListener rendererOnItemClick;
    private ListAdapter mediaServerAdapter;
    private AdapterView.OnItemClickListener mediaServerOnItemClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device_select, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView rendererListView = (ListView) getActivity().findViewById(R.id.renderer_list);
        rendererListView.setAdapter(rendererAdapter);
        rendererListView.setOnItemClickListener(rendererOnItemClick);

        ListView mediaServerListView = (ListView) getActivity().findViewById(R.id.media_server_list);
        mediaServerListView.setAdapter(mediaServerAdapter);
        mediaServerListView.setOnItemClickListener(mediaServerOnItemClick);
    }

    public void setRendererAdapter(ListAdapter rendererAdapter, AdapterView.OnItemClickListener rendererOnItemClick) {
        this.rendererAdapter = rendererAdapter;
        this.rendererOnItemClick = rendererOnItemClick;
    }

    public void setMediaServerAdapter(ListAdapter mediaServerAdapter, AdapterView.OnItemClickListener mediaServerOnItemClick) {
        this.mediaServerAdapter = mediaServerAdapter;
        this.mediaServerOnItemClick = mediaServerOnItemClick;
    }
}
