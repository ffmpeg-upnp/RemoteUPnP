package com.wonyoung.remoteupnp.test;

import android.test.ActivityInstrumentationTestCase2;

import com.wonyoung.remoteupnp.ui.MainActivity;

public class RemoteUPnpEndToEndTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private ApplicationRunner application = new ApplicationRunner(this);
    private FakeUPnPService upnpService = new FakeUPnPService();
    private FakeRenderer renderer;
    private FakeMediaServer mediaServer;

    public RemoteUPnpEndToEndTest() {
        super(MainActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        mediaServer = FakeMediaServer.createDevice();
        renderer = FakeRenderer.createDevice();
    }

    public void testSetRendererAndMediaServer() {
        application.startWith(upnpService);
        upnpService.hasReceivedDeviceListRequest();

        upnpService.sendDeviceList(renderer, mediaServer);
        application.showsDevices(renderer, mediaServer);

        application.readyWith(renderer, mediaServer);
    }

    public void testAddFirstFileToPlayListAndPlay() {
        application.startWith(upnpService);
        upnpService.hasReceivedDeviceListRequest();

        upnpService.sendDeviceList(renderer, mediaServer);
        application.showsDevices(renderer, mediaServer);

        application.readyWith(renderer, mediaServer);

        application.requestFolders(mediaServer, FakeMediaServer.ROOT_FOLDER_NAME);
        mediaServer.hasReceivedRequestFolder(FakeMediaServer.ROOT_FOLDER_NAME);
        mediaServer.sendFolders(FakeMediaServer.SUB_FOLDER);
        application.showsFolders(FakeMediaServer.SUB_FOLDER);

        application.addToPlayList(FakeMediaServer.SUB_FOLDER.getFileAt(0));
        application.play();
        renderer.isPlaying(FakeMediaServer.SUB_FOLDER.getFileAt(0));
    }

    public void ignoreTestNotes() {
//        deviceSelector.requestDeviceList();
//        devices.hasReceivedRequest();
//        devices.sendDeviceList();
//        deviceSelector.showsDeviceList();
//
//        deviceSelector.setMediaServer(one);
//        mediaSelector.mediaServerWith(one);
//        deviceSelector.setRenderer(second);
//        player.showsRendererWith(second);
//
//
//        mediaSelector.getRoot();
//        mediaServer.sendFolders();
//        mediaSelector.addFile();
//        playList.hasAdded();
//
//        playList.play();
//        player.shows();
    }
}
