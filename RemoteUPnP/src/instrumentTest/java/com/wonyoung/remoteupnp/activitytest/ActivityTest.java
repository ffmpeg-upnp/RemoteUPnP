package com.wonyoung.remoteupnp.activitytest;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.wonyoung.remoteupnp.ui.MainActivity;

/**
 * Created by wonyoung.jang on 13. 11. 26.
 */
public class ActivityTest extends ActivityInstrumentationTestCase2 <MainActivity> {
    public ActivityTest() {
        super(MainActivity.class);
    }

    public void testBindMakesNoLeakWhenRotates() throws Exception {
        Solo solo = new Solo(getInstrumentation(), getActivity());

        solo.setActivityOrientation(Solo.PORTRAIT);

        solo.setActivityOrientation(Solo.LANDSCAPE);

        solo.setActivityOrientation(Solo.PORTRAIT);
    }
}
