package tw.com.daxia.virtualsoftkeys;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.daxia.virtualsoftkeys.common.PermissionUtils;
import tw.com.daxia.virtualsoftkeys.common.SPFManager;
import tw.com.daxia.virtualsoftkeys.common.ScreenHepler;
import tw.com.daxia.virtualsoftkeys.common.ThemeHelper;
import tw.com.daxia.virtualsoftkeys.config.FunctionConfigFragment;
import tw.com.daxia.virtualsoftkeys.config.TouchConfigFragment;
import tw.com.daxia.virtualsoftkeys.service.ServiceFloating;
import tw.com.daxia.virtualsoftkeys.ui.AboutDialog;
import tw.com.daxia.virtualsoftkeys.ui.AccessibilityServiceErrorDialog;
import tw.com.daxia.virtualsoftkeys.ui.ColorPickerDialogFragment;
import tw.com.daxia.virtualsoftkeys.ui.DescriptionDialog;
import tw.com.daxia.virtualsoftkeys.ui.PermissionDialog;


public class MainActivity extends AppCompatActivity implements ColorPickerDialogFragment.colorPickerCallback {


    private final static String TAG = "MainActivity";
    public final static int MAX_HEIGHT_PERCENTAGE = 20;
    private final static String descriptionDialogTAG = "descriptionDialog";
    private final static String permissionDialogTAG = "permissionDialog";
    private final static String accessibilityServiceDialogTAG = "accessibilityServiceDialog";

    @BindView(R.id.View_touchviewer)
    public View ViewTouchviewer;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.RL_content)
    RelativeLayout RLContent;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    /*
     * Permission
     */
    private boolean drawOverlays = false;
    private AppOpsManager.OnOpChangedListener onOpChangedListener = null;

    /*
     * viewpager + Fragment
     */
    private MainFragmentPagerAdapter pagerAdapter;
    private List<Fragment> tabloyoutFragmentList;
    private List<String> tabloyoutTitleList;

    /*
     * Dialog
     */
    private DescriptionDialog descriptionDialog;
    private PermissionDialog permissionDialog;
    private AccessibilityServiceErrorDialog accessibilityServiceDialog;


    /*
     * Config
     */
    private boolean isPortrait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        /*
         * Init the conf
         */
        //Init orientation
        if (ScreenHepler.isPortrait(getResources())) {
            isPortrait = true;
        } else {
            isPortrait = false;
        }
        //Init UI
        initUIStyle();
        initTabloyout();
        //Init shared setting
        //initSharedConfig();
        //Show Description Dialog
        showDescription();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Reset the seekbar
        if (ScreenHepler.isPortrait(getResources())) {
            isPortrait = true;
        } else {
            isPortrait = false;
        }
        //Init UI
        initUIStyle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needAndroidOCanDrawWorkaround()) {
            if (onOpChangedListener == null) {
                drawOverlays = PermissionUtils.checkSystemAlertWindowPermission(this);
            }
            AndroidOCanDrawWorkaround();
        } else {
            drawOverlays = PermissionUtils.checkSystemAlertWindowPermission(this);
        }

        boolean accessibility = isAccessibilitySettingsOn();
        //Fix Android O bug
        if (!drawOverlays || !accessibility) {
            permissionDialog = PermissionDialog.newInstance(drawOverlays, accessibility);
            permissionDialog.show(this.getSupportFragmentManager(), permissionDialogTAG);
        } else {
            if (ServiceFloating.getSharedInstance() == null) {
                accessibilityServiceDialog = new AccessibilityServiceErrorDialog();
                accessibilityServiceDialog.show(this.getSupportFragmentManager(), accessibilityServiceDialogTAG);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (permissionDialog != null) {
            permissionDialog.dismiss();
        }
        if (accessibilityServiceDialog != null) {
            accessibilityServiceDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*
     * Set about icon
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                AboutDialog aboutDialog = new AboutDialog();
                aboutDialog.setCancelable(true);
                aboutDialog.show(this.getSupportFragmentManager(), "AboutDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Android O has a bug  which is Settings.canDrawOverlays(context) not work before we restart app
    //So we use workaround from:
    //https://stackoverflow.com/questions/46173460/why-in-android-o-method-settings-candrawoverlays-returns-false-when-user-has/48127195.
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void AndroidOCanDrawWorkaround() {
        if (!drawOverlays) {
            AppOpsManager opsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            onOpChangedListener = new AppOpsManager.OnOpChangedListener() {
                @Override
                public void onOpChanged(String op, String packageName) {
                    String myPackageName = getPackageName();
                    if (myPackageName.equals(packageName) &&
                            AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW.equals(op)) {
                        drawOverlays = !drawOverlays;
                    }
                }
            };
            if (opsManager != null) {
                opsManager.startWatchingMode(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                        null, onOpChangedListener);
            }
        } else {
            AppOpsManager opsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            if (opsManager != null && onOpChangedListener != null) {
                opsManager.stopWatchingMode(onOpChangedListener);
                onOpChangedListener = null;
            }
        }
    }

    private boolean needAndroidOCanDrawWorkaround() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }


    private void showDescription() {
        if (!SPFManager.getDescriptionClose(this)) {
            descriptionDialog = new DescriptionDialog();
            descriptionDialog.show(this.getSupportFragmentManager(), descriptionDialogTAG);
        }
    }

    private void initUIStyle() {
        int configColor;
        if (isPortrait) {
            configColor = ThemeHelper.getColorResource(this, R.color.config_portrait_color);
        } else {
            configColor = ThemeHelper.getColorResource(this, R.color.config_landscape_color);
        }
        ViewTouchviewer.setBackgroundColor(configColor);
    }

    private void initTabloyout() {
        //init viewpager
        tabloyoutFragmentList = new ArrayList<>();
        tabloyoutFragmentList.add(TouchConfigFragment.Companion.newInstance());
        tabloyoutFragmentList.add(FunctionConfigFragment.Companion.newInstance());
        tabloyoutTitleList = new ArrayList<>();
        tabloyoutTitleList.add(getString(R.string.config_touch_title));
        tabloyoutTitleList.add(getString(R.string.config_fuuchion_title));

        pagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), tabloyoutFragmentList, tabloyoutTitleList);
        tablayout.setTabMode(TabLayout.MODE_FIXED);
        viewpager.setAdapter(pagerAdapter);
        tablayout.setupWithViewPager(viewpager);
    }

    public boolean isPortrait() {
        return isPortrait;
    }


    public boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            //accessibility is Enable
            if (BuildConfig.DEBUG) {
                Log.i(TAG, e.getMessage());
            }
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(this.getPackageName().toLowerCase());
            }
        }
        return false;
    }

    @Override
    public void onColorChange(int colorCode) {
        //Communicating with Other Fragments
        Fragment functionFragment = tabloyoutFragmentList.get(2);
        if (functionFragment instanceof FunctionConfigFragment) {
            ((FunctionConfigFragment) functionFragment).onColorChange(colorCode);
        }
    }

}
