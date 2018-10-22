package tw.com.daxia.virtualsoftkeys;

import android.support.test.filters.SmallTest;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(RobolectricTestRunner.class)
@SmallTest
public class MainActivityTest {

    ActivityController<MainActivity> controller;
    MainActivity mainActivity;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(MainActivity.class).create().start().resume();
        mainActivity = controller.get();
    }

}