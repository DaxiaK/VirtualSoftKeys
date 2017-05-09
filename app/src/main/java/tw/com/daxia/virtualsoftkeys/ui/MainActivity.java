package tw.com.daxia.virtualsoftkeys.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import tw.com.daxia.virtualsoftkeys.BuildConfig;
import tw.com.daxia.virtualsoftkeys.R;
import tw.com.daxia.virtualsoftkeys.common.SPFManager;
import tw.com.daxia.virtualsoftkeys.common.ScreenHepler;
import tw.com.daxia.virtualsoftkeys.service.ServiceFloating;

import static tw.com.daxia.virtualsoftkeys.common.Link.MY_GIT_HUB_URL;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ColorPickerFragment.colorPickerCallback {


    private final static String TAG = "MainActivity";
    private final static int MAX_HEIGHT_PERCENTAGE = 20;
    private final static String descriptionDialogTAG = "descriptionDialog";
    private final static String permissionDialogTAG = "permissionDialog";
    private final static String accessibilityServiceDialogTAG = "accessibilityServiceDialog";

    /**
     * UI
     */
    private SeekBar Seek_touch_area_height, Seek_touch_area_width;
    private SeekBar Seek_touch_area_position;
    private TextView TV_config_name;
    private CheckedTextView CTV_stylus_only_mode,
            CTV_reverse_button,
            CTV_smart_hidden, CTV_hidden_when_rotate;
    private Spinner SP_bar_disappear_time;

    private View View_touchviewer;
    private ImageView IV_bg_color, IV_my_github;

    /**
     * Dialog
     */
    private DescriptionDialog descriptionDialog;
    private PermissionDialog permissionDialog;
    private AccessibilityServiceErrorDialog accessibilityServiceDialog;

    /**
     * Config
     */
    private int screenWidth;
    private boolean isPortrait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View_touchviewer = findViewById(R.id.View_touchviewer);

        TV_config_name = (TextView) findViewById(R.id.TV_config_name);

        Seek_touch_area_height = (SeekBar) findViewById(R.id.Seek_touch_area_height);
        Seek_touch_area_height.setOnSeekBarChangeListener(touchviewHeightSeekBarListener);
        Seek_touch_area_height.setSaveEnabled(false);

        Seek_touch_area_width = (SeekBar) findViewById(R.id.Seek_touch_area_width);
        Seek_touch_area_width.setOnSeekBarChangeListener(touchviewWidthSeekBarListener);
        Seek_touch_area_width.setSaveEnabled(false);

        Seek_touch_area_position = (SeekBar) findViewById(R.id.Seek_touch_area_position);
        Seek_touch_area_position.setOnSeekBarChangeListener(touchviewPositionSeekBarListener);
        Seek_touch_area_position.setSaveEnabled(false);

        CTV_stylus_only_mode = (CheckedTextView) findViewById(R.id.CTV_stylus_only_mode);
        SP_bar_disappear_time = (Spinner) findViewById(R.id.SP_bar_disappear_time);
        IV_bg_color = (ImageView) findViewById(R.id.IV_bg_color);
        CTV_reverse_button = (CheckedTextView) findViewById(R.id.CTV_reverse_button);
        CTV_smart_hidden = (CheckedTextView) findViewById(R.id.CTV_smart_hidden);
        CTV_hidden_when_rotate = (CheckedTextView) findViewById(R.id.CTV_hidden_when_rotate);

        IV_my_github = (ImageView) findViewById(R.id.IV_my_github);
        IV_my_github.setOnClickListener(this);
        /*
         * Init the conf
         */
        //Init orientation
        if (ScreenHepler.isPortrait(getResources())) {
            isPortrait = true;
        } else {
            isPortrait = false;
        }
        //Init shared setting
        initSharedConfig();
        //Init All Seekbar
        initSeekBar();
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
        //Init All Seekbar
        initSeekBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean drawOverlays = checkSystemAlertWindowPermission();
        boolean accessibility = isAccessibilitySettingsOn();
        if (!drawOverlays || !accessibility) {
            permissionDialog = PermissionDialog.newInstance(drawOverlays, accessibility);
            permissionDialog.show(this.getSupportFragmentManager(), permissionDialogTAG);
        } else {
            if (ServiceFloating.getSharedInstance() == null) {
                Log.e("test","null");
                accessibilityServiceDialog = new AccessibilityServiceErrorDialog();
                accessibilityServiceDialog.show(this.getSupportFragmentManager(), accessibilityServiceDialogTAG);
            }else{
                Log.e("test","not null");
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


    private void showDescription() {
        if (!SPFManager.getDescriptionClose(this)) {
            descriptionDialog = new DescriptionDialog();
            descriptionDialog.show(this.getSupportFragmentManager(), descriptionDialogTAG);
        }
    }

    private void initSharedConfig() {
        //StylusMode
        CTV_stylus_only_mode.setChecked(SPFManager.getStylusOnlyMode(this));
        CTV_stylus_only_mode.setOnClickListener(this);
        //Disappear time
        initDisappearSpinner();
        //Reverse button position
        CTV_reverse_button.setChecked(SPFManager.getReverseFunctionButton(this));
        CTV_reverse_button.setOnClickListener(this);
        //make bar bg be transparent
        IV_bg_color.setImageDrawable(new ColorDrawable(SPFManager.getSoftKeyBarBgGolor(this)));
        IV_bg_color.setOnClickListener(this);
        //smart hieedn
        CTV_smart_hidden.setChecked(SPFManager.getSmartHidden(this));
        CTV_smart_hidden.setOnClickListener(this);
        //hidden when rotate
        CTV_hidden_when_rotate.setChecked(SPFManager.getRotateHidden(this));
        CTV_hidden_when_rotate.setOnClickListener(this);
    }

    private void initDisappearSpinner() {
        ArrayAdapter disappearAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.bar_disappear_time));
        SP_bar_disappear_time.setAdapter(disappearAdapter);
        SP_bar_disappear_time.setSelection(SPFManager.getDisappearPosition(this));
        SP_bar_disappear_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ServiceFloating mAccessibilityService = ServiceFloating.getSharedInstance();
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateDisappearTime(position);
                    mAccessibilityService = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
    }

    private void initSeekBar() {
        initSeekBarStyle();
        initSeekBarContent();
    }

    private void initSeekBarStyle() {
        int configColor;
        if (isPortrait) {
            TV_config_name.setText(getString(R.string.config_name_portrait));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                configColor = getResources().getColor(R.color.config_portrait_color, this.getTheme());
            } else {
                configColor = getResources().getColor(R.color.config_portrait_color);
            }
        } else {
            TV_config_name.setText(getString(R.string.config_name_landscape));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                configColor = getResources().getColor(R.color.config_landscape_color, this.getTheme());
            } else {
                configColor = getResources().getColor(R.color.config_landscape_color);
            }
        }
        TV_config_name.setTextColor(configColor);
        View_touchviewer.setBackgroundColor(configColor);
        Seek_touch_area_height.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN));
        Seek_touch_area_height.getThumb().setColorFilter(new PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN));
        Seek_touch_area_width.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN));
        Seek_touch_area_width.getThumb().setColorFilter(new PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN));
        Seek_touch_area_position.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN));
        Seek_touch_area_position.getThumb().setColorFilter(new PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN));
    }

    private void initSeekBarContent() {
        //Default
        int screenHeight = ScreenHepler.getScreenHeight(this);
        screenWidth = ScreenHepler.getScreenWidth(this);
        int touchviewWidth;
        //Default Height init
        Seek_touch_area_height.setMax(screenHeight / MAX_HEIGHT_PERCENTAGE);

        //Default width init
        Seek_touch_area_width.setMax(screenWidth);

        if (isPortrait) {
            touchviewWidth = SPFManager.getTouchviewPortraitWidth(this);
            //Height
            Seek_touch_area_height.setProgress(SPFManager.getTouchviewPortraitHeight(this));
            //position  + Width
            //For match content
            if (touchviewWidth == ScreenHepler.getDefautlTouchviewWidth()) {
                Seek_touch_area_width.setProgress(screenWidth);
                //set position
                updateTouchViewPosition(screenWidth, SPFManager.getTouchviewPortraitPosition(this));
            } else {
                Seek_touch_area_width.setProgress(touchviewWidth);
                //set position
                updateTouchViewPosition(touchviewWidth, SPFManager.getTouchviewPortraitPosition(this));
            }
        } else {
            touchviewWidth = SPFManager.getTouchviewLandscapeWidth(this);
            //Height
            Seek_touch_area_height.setProgress(SPFManager.getTouchviewLandscapeHeight(this));
            //position  + Width
            //For match content
            if (touchviewWidth == ScreenHepler.getDefautlTouchviewWidth()) {
                Seek_touch_area_width.setProgress(screenWidth);
                //set position
                updateTouchViewPosition(screenWidth, SPFManager.getTouchviewLandscapePosition(this));
            } else {
                Seek_touch_area_width.setProgress(touchviewWidth);
                //set position
                updateTouchViewPosition(touchviewWidth, SPFManager.getTouchviewLandscapePosition(this));
            }
        }


    }

    private void updateTouchViewPosition(int toughviewWidth, int positionFromSPF) {
        Seek_touch_area_position.setMax(screenWidth - toughviewWidth);
        if (positionFromSPF >= 0) {
            Seek_touch_area_position.setProgress(positionFromSPF);
        } else {
            //Default is in center horizontal
            Seek_touch_area_position.setProgress((screenWidth / 2) - (toughviewWidth / 2));
        }
    }

    private SeekBar.OnSeekBarChangeListener touchviewHeightSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (isPortrait) {
                SPFManager.setTouchviewPortraitHeight(MainActivity.this, seekBar.getProgress());
            } else {
                SPFManager.setTouchviewLandscapeHeight(MainActivity.this, seekBar.getProgress());
            }
            ServiceFloating mAccessibilityService = ServiceFloating.getSharedInstance();
            if (mAccessibilityService != null) {
                mAccessibilityService.updateTouchView(seekBar.getProgress(), null, null);
                mAccessibilityService = null;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) View_touchviewer.getLayoutParams();
            params.height = progress;
            View_touchviewer.setLayoutParams(params);
        }
    };

    private SeekBar.OnSeekBarChangeListener touchviewWidthSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            updateTouchViewPosition(seekBar.getProgress(), -1);
            if (isPortrait) {
                SPFManager.setTouchviewPortraitPosition(MainActivity.this, Seek_touch_area_position.getProgress());
            } else {
                SPFManager.setTouchviewLandscapePosition(MainActivity.this, Seek_touch_area_position.getProgress());
            }
            ServiceFloating mAccessibilityService = ServiceFloating.getSharedInstance();
            if (seekBar.getProgress() == seekBar.getMax()) {
                if (isPortrait) {
                    SPFManager.setTouchviewPortraitWidth(MainActivity.this, ViewGroup.LayoutParams.MATCH_PARENT);
                } else {
                    SPFManager.setTouchviewLandscapeWidth(MainActivity.this, ViewGroup.LayoutParams.MATCH_PARENT);

                }
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateTouchView(null, ViewGroup.LayoutParams.MATCH_PARENT, Seek_touch_area_position.getProgress());
                    mAccessibilityService = null;
                }
            } else {
                if (isPortrait) {
                    SPFManager.setTouchviewPortraitWidth(MainActivity.this, seekBar.getProgress());
                } else {
                    SPFManager.setTouchviewLandscapeWidth(MainActivity.this, seekBar.getProgress());
                }
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateTouchView(null, seekBar.getProgress(), Seek_touch_area_position.getProgress());
                    mAccessibilityService = null;
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) View_touchviewer.getLayoutParams();
            params.width = progress;
            View_touchviewer.setLayoutParams(params);
        }
    };

    private SeekBar.OnSeekBarChangeListener touchviewPositionSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (isPortrait) {
                SPFManager.setTouchviewPortraitPosition(MainActivity.this, seekBar.getProgress());
            } else {
                SPFManager.setTouchviewLandscapePosition(MainActivity.this, seekBar.getProgress());
            }
            ServiceFloating mAccessibilityService = ServiceFloating.getSharedInstance();
            if (mAccessibilityService != null) {
                mAccessibilityService.updateTouchView(null, null, seekBar.getProgress());
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) View_touchviewer.getLayoutParams();
            params.setMarginStart(progress);
            View_touchviewer.setLayoutParams(params);
        }
    };


    private boolean checkSystemAlertWindowPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(this)) {
            return false;
        }
        return true;
    }

    public boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CTV_stylus_only_mode: {
                CTV_stylus_only_mode.toggle();
                SPFManager.setStylusOnlyMode(this, CTV_stylus_only_mode.isChecked());
                ServiceFloating mAccessibilityService = ServiceFloating.getSharedInstance();
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateTouchViewConfigure();
                }
                break;
            }
            case R.id.CTV_reverse_button: {
                CTV_reverse_button.toggle();
                SPFManager.setReverseFunctionButton(this, CTV_reverse_button.isChecked());
                ServiceFloating mAccessibilityService = ServiceFloating.getSharedInstance();
                if (mAccessibilityService != null) {
                    mAccessibilityService.refreshSoftKey();
                }
                break;
            }
            case R.id.IV_bg_color: {
                ColorPickerFragment secColorPickerFragment =
                        ColorPickerFragment.newInstance(SPFManager.getSoftKeyBarBgGolor(this));
                secColorPickerFragment.show(getSupportFragmentManager(), "ColorPickerFragment");
                break;
            }
            case R.id.CTV_smart_hidden: {
                CTV_smart_hidden.toggle();
                SPFManager.setSmartHidden(this, CTV_smart_hidden.isChecked());
                ServiceFloating mAccessibilityService = ServiceFloating.getSharedInstance();
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateSmartHidden(CTV_smart_hidden.isChecked());
                }
                break;
            }
            case R.id.CTV_hidden_when_rotate: {
                CTV_hidden_when_rotate.toggle();
                SPFManager.setRotateHidden(this, CTV_hidden_when_rotate.isChecked());
                ServiceFloating mAccessibilityService = ServiceFloating.getSharedInstance();
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateRotateHidden(CTV_hidden_when_rotate.isChecked());
                }
                break;
            }
            case R.id.IV_my_github: {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MY_GIT_HUB_URL));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void onColorChange(int colorCode) {
        IV_bg_color.setImageDrawable(new ColorDrawable(colorCode));
        SPFManager.setSoftKeyBgGolor(this, colorCode);
        ServiceFloating mAccessibilityService = ServiceFloating.getSharedInstance();
        if (mAccessibilityService != null) {
            mAccessibilityService.refreshSoftKey();
        }
    }
}
