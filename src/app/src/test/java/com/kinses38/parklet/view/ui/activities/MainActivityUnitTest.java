package com.kinses38.parklet.view.ui.activities;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.kinses38.parklet.ParkLet;
import com.kinses38.parklet.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = 28, application = ParkLet.class)
public class MainActivityUnitTest {

    private MainActivity mainActivity;

    @Before
    public void setUp() {
        mainActivity = Robolectric.buildActivity(MainActivity.class)
                .create()
                .visible()
                .get();
    }

    @Test
    public void shouldExist() {
        assertNotNull(mainActivity);
    }

    @Test
    public void checkToolBarExists() {
        assertNotNull(mainActivity.findViewById(R.id.toolbar));
    }

    @Test
    public void checkTitle() {
        String title = mainActivity.getTitle().toString();
        assertNotNull("Title not null", title);
        assertThat(title, equalTo("ParkLet"));
    }

    @Test
    public void checkNavBar() {
        assertNotNull(mainActivity.findViewById(R.id.nav_view));
        NavigationView view = mainActivity.findViewById(R.id.nav_view);
        assertNotNull(view.getMenu());
        assertThat("Home nav button", view.getMenu().getItem(0).getTitle(), equalTo("Home"));
        assertThat("Vehicles nav button", view.getMenu().getItem(1).getTitle(), equalTo("My Vehicles"));
        assertThat("Properties nav button", view.getMenu().getItem(2).getTitle(), equalTo("My Properties"));
    }

    @Test
    public void DrawerClosedByDefault(){
        DrawerLayout drawer = mainActivity.findViewById(R.id.drawer_layout);
        assertFalse("Drawer not visible", drawer.isDrawerVisible(mainActivity.findViewById(R.id.nav_view)));
        assertFalse("Drawer not open", drawer.isDrawerOpen(GravityCompat.START));
    }
}
