package com.wonyoung.remoteupnp;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

/**
 * Created by wonyoungjang on 13. 10. 18..
 */
public class LibrarySelectFragment extends ListFragment {
//    private List<Item> fileList;
//    private List<Container> folderList;


//    private void onRendererSelected(Device device) {
//        play(device);
//    }

//    private void play(Device device) {
//        Service service = device.findService(new UDAServiceId("AVTransport"));
//
//        if (service != null) {
//            ActionCallback setAVTransportURIAction = new SetAVTransportURI(service, "http://192.168.1.123:5001/get/0$1$2$1$2/02+VARIACION+01.mp3", "NO METADATA") {
//                @Override
//                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {
//
//                }
//            };
//
//            ActionCallback playAction = new Play(service) {
//
//                @Override
//                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {
//
//                }
//            };
//
//            upnpService.getControlPoint().execute(setAVTransportURIAction);
//            upnpService.getControlPoint().execute(playAction);
//        }
//    }

//    public void onMediaServerSelected(Device device) {
//        Toast.makeText(this, "Media Server : " + device.getDisplayString(), Toast.LENGTH_SHORT).show();
//        mediaServer = device;
//        browse(mediaServer.findService(new UDAServiceId("ContentDirectory")));
//    }

//    private void browse(Service service) {
//
//        ActionCallback rootBrowseAction = new Browse(service, "0", BrowseFlag.DIRECT_CHILDREN) {
//
//            @Override
//            public void received(ActionInvocation actionInvocation, DIDLContent didlContent) {
//                fileList = didlContent.getItems();
//                folderList = didlContent.getContainers();
//
//                Log.e("wonyoung", "folder count : " + folderList.size());
//                for (Container folder : folderList) {
//                    Log.e("wonyoung", folder.getTitle() + " id-" + folder.getId());
//                }
//
//                Log.e("wonyoung", "files count : " + fileList.size());
//                for (Item item : fileList) {
//                    if (item != null) {
//                        Log.e("wonyoung", String.format("[%s]",item.getTitle()));
//                        if (item.getFirstResource() != null)
//                            Log.e("wonyoung", String.format("      :[%s] ",item.getFirstResource().getValue()));
//
//                    }
//                }
//
//
//                ArrayAdapter<Item> itemAdapter = new ArrayAdapter<Item>(MainActivity.this, android.R.layout.simple_list_item_activated_1,
//                        fileList);
//                ArrayAdapter<Container> folderAdapter = new ArrayAdapter<Container>(MainActivity.this, android.R.layout.simple_list_item_activated_1,
//                        folderList);
//
//                mSectionsPagerAdapter.librarySelectFragment.setListAdapter(itemAdapter);
//            }
//
//            @Override
//            public void updateStatus(Status status) {
//
//            }
//
//            @Override
//            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
//
//            }
//        };
//        upnpService.getControlPoint().execute(rootBrowseAction);
//
//        ActionCallback browseAction = new Browse(service, "0$1$2$1", BrowseFlag.DIRECT_CHILDREN) {
//
//            @Override
//            public void received(ActionInvocation actionInvocation, DIDLContent didlContent) {
//                fileList = didlContent.getItems();
//                folderList = didlContent.getContainers();
//
//                Log.e("wonyoung", "folder count : " + folderList.size());
//                for (Container folder : folderList) {
//                    Log.e("wonyoung", folder.getTitle() + " id-" + folder.getId());
//                }
//
//                Log.e("wonyoung", "files count : " + fileList.size());
//                for (Item item : fileList) {
//                    Log.e("wonyoung", item.getTitle() + " : " + item.getFirstResource().getValue());
//                }
//
//
//                ArrayAdapter<Item> itemAdapter = new ArrayAdapter<Item>(MainActivity.this, android.R.layout.simple_list_item_activated_1,
//                        fileList);
//                ArrayAdapter<Container> folderAdapter = new ArrayAdapter<Container>(MainActivity.this, android.R.layout.simple_list_item_activated_1,
//                        folderList);
//
//                mSectionsPagerAdapter.librarySelectFragment.setListAdapter(itemAdapter);
//            }
//
//            @Override
//            public void updateStatus(Status status) {
//
//            }
//
//            @Override
//            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
//
//            }
//        };
//
//        upnpService.getControlPoint().execute(browseAction);
//
//    }

}
