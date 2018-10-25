package tw.com.daxia.virtualsoftkeys;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import tw.com.daxia.virtualsoftkeys.common.Link;
import tw.com.daxia.virtualsoftkeys.common.SPFManager;

@RunWith(AndroidJUnit4.class)
public class FunctionConfigTest {

    private final static String FILENAME = "filename";
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;


    private Context context;

    @Before
    public void setUp() {
        context = (Application) ApplicationProvider.getApplicationContext();
        sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        // Ensure no shared preferences have leaked from previous tests.
        Assert.assertEquals(0, sharedPreferences.getAll().size());

        editor = sharedPreferences.edit();
    }


    public void spf_HomeLongClickStartAction_Test() {
        SPFManager.setHomeLongClickStartAction(context, Link.GOOGLE_APP_PACKAGE_NAME);
        Assert.assertEquals(Link.GOOGLE_APP_PACKAGE_NAME, SPFManager.getHomeLongClickStartAction(context));

    }

}
