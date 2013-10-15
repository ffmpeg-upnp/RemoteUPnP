package com.wonyoung.remoteupnp.test;

import com.wonyoung.remoteupnp.Renderer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by wonyoungjang on 13. 10. 16..
 */
public class FakeRenderer implements Renderer {
    public void hasReceivedUri(String uri) {
        assertThat(getTransportUri(), equalTo(uri));
    }

    private String getTransportUri() {
        return null;
    }

    public void isPlaying() {
    }
}
