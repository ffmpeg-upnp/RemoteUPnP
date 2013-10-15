package com.wonyoung.remoteupnp.test;

import junit.framework.TestCase;

public class RemoteUPnpEndToEndTest extends TestCase {

    private static final String uri = "http://192.168.1.123/piano.mp3";
    FakeRenderer renderer = new FakeRenderer();
    ApplicationRunner application = new ApplicationRunner();

    public void testPlayUri() {
        application.readyWith(renderer);
        application.play(uri);
        renderer.hasReceivedUri(uri);
        renderer.isPlaying();
        application.showsPlaying();
    }
}
