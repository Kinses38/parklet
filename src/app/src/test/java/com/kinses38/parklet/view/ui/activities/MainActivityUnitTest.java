package com.kinses38.parklet.view.ui.activities;

import android.view.MenuItem;

import com.kinses38.parklet.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import org.robolectric.fakes.RoboMenuItem;

import static org.junit.Assert.assertNotNull;


@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = 28)
public class MainActivityUnitTest {

    private MainActivity mainActivity;

    @Before
    public void setUp() throws Exception {
        mainActivity = Robolectric.buildActivity(MainActivity.class)
                .create()
                .visible()
                .get();

    }

    @Test
    public void shouldExist(){
        assertNotNull(mainActivity);
    }

    @Test
    public void checkToolBarExists(){
        assertNotNull(mainActivity.findViewById(R.id.toolbar));
    }

    @Test
    public void testMenuOptionsSelected(){
        MenuItem menuItem = new RoboMenuItem((R.id.sign_out_button));
        mainActivity.onOptionsItemSelected(menuItem);

    }


}