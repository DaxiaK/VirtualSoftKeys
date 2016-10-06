package tw.com.daxia.virtualsoftkeys.setting;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import tw.com.daxia.virtualsoftkeys.BuildConfig;
import tw.com.daxia.virtualsoftkeys.R;
import tw.com.daxia.virtualsoftkeys.common.SPFManager;
import tw.com.daxia.virtualsoftkeys.common.ScreenHepler;
import tw.com.daxia.virtualsoftkeys.service.ServiceFloating;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private final static String TAG = "MainActivity";
    private final static String MY_GIT_HUB_URL = "https://github.com/erttyy8821/VirtualSoftKeys";
    private SeekBar Seek_touch_area_height, Seek_touch_area_width;
    private SeekBar Seek_touch_area_position;
    private TextView TV_touchview_current_width, TV_touchview_total_width;
    private CheckedTextView CTV_stylus_only_mode;
    private View View_touchviewer;
    private ImageView IV_my_github;
    private int screenWidth;
    private boolean isPortrait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("test", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View_touchviewer = findViewById(R.id.View_touchviewer);
        Seek_touch_area_height = (SeekBar) findViewById(R.id.Seek_touch_area_height);
        Seek_touch_area_height.setOnSeekBarChangeListener(touchviewHeightSeekBarListener);
        Seek_touch_area_height.setSaveEnabled(false);

        Seek_touch_area_width = (SeekBar) findViewById(R.id.Seek_touch_area_width);
        Seek_touch_area_width.setOnSeekBarChangeListener(touchviewWidthSeekBarListener);
        Seek_touch_area_width.setSaveEnabled(false);

        Seek_touch_area_position = (SeekBar) findViewById(R.id.Seek_touch_area_position);
        Seek_touch_area_position.setOnSeekBarChangeListener(touchviewPositionSeekBarListener);
        Seek_touch_area_position.setSaveEnabled(false);

        TV_touchview_current_width = (TextView) findViewById(R.id.TV_touchview_current_width);
        TV_touchview_total_width = (TextView) findViewById(R.id.TV_touchview_total_width);

        CTV_stylus_only_mode = (CheckedTextView) findViewById(R.id.CTV_stylus_only_mode);
        IV_my_github = (ImageView) findViewById(R.id.IV_my_github);
        IV_my_github.setOnClickListener(this);
        //Init common setting
        initStylusMode();
        //Init orientation setting
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.e("test","ORIENTATION_PORTRAIT");
            isPortrait = true;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("test","ORIENTATION_LANDSCAPE");
            isPortrait = false;
        }
        //Init all view setting
        initSeekBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean drawOverlays = checkSystemAlertWindowPermission();
        boolean accessibility = isAccessibilitySettingsOn();
        if (!drawOverlays || !accessibility) {
            PermissionDialog permissionDialog = PermissionDialog.newInstance(drawOverlays, accessibility);
            permissionDialog.show(this.getSupportFragmentManager(), "permissionDialog");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initStylusMode() {
        CTV_stylus_only_mode.setChecked(SPFManager.getStylusOnlyMode(this));
        CTV_stylus_only_mode.setOnClickListener(this);
    }

    private void initSeekBar() {
        //Default
        int screenHeight = ScreenHepler.getScreenHeight(this);
        screenWidth = ScreenHepler.getScreenWidth(this);
        int touchviewWidth;
        //Default Height init
        Seek_touch_area_height.setMax(screenHeight / 20);

        //Default width init
        Seek_touch_area_width.setMax(screenWidth);

        //Default position init
        TV_touchview_total_width.setText("/" + String.valueOf(screenWidth));

        if (isPortrait) {
            Log.e("test", "isPortrait");
            touchviewWidth = SPFManager.getTouchviewPortraitWidth(this);
            Log.e("test", "touchviewWidth=" + touchviewWidth);

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
                updateTouchViewPosition(View_touchviewer.getWidth(), SPFManager.getTouchviewPortraitPosition(this));
            }
        } else {
            Log.e("test", "Landscape");
            touchviewWidth = SPFManager.getTouchviewLandscapeWidth(this);
            Log.e("test", "touchviewWidth=" + touchviewWidth);
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
                updateTouchViewPosition(View_touchviewer.getWidth(), SPFManager.getTouchviewLandscapePosition(this));
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
            } else{
                SPFManager.setTouchviewLandscapePosition(MainActivity.this, Seek_touch_area_position.getProgress());
            }
            ServiceFloating mAccessibilityService = ServiceFloating.getSharedInstance();
            if (seekBar.getProgress() == seekBar.getMax()) {
                if (isPortrait) {
                    SPFManager.setTouchviewPortraitWidth(MainActivity.this, ViewGroup.LayoutParams.MATCH_PARENT);
                } else{
                    SPFManager.setTouchviewLandscapeWidth(MainActivity.this, ViewGroup.LayoutParams.MATCH_PARENT);

                }
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateTouchView(null, ViewGroup.LayoutParams.MATCH_PARENT, Seek_touch_area_position.getProgress());
                    mAccessibilityService = null;
                }
            } else {
                if (isPortrait) {
                    SPFManager.setTouchviewPortraitWidth(MainActivity.this, seekBar.getProgress());
                }else {
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
            TV_touchview_current_width.setText(String.valueOf(progress));
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
                mAccessibilityService = null;
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
            case R.id.CTV_stylus_only_mode:
                CTV_stylus_only_mode.toggle();
                SPFManager.setStylusOnlyMode(this, CTV_stylus_only_mode.isChecked());
                ServiceFloating mAccessibilityService = ServiceFloating.getSharedInstance();
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateStylusOnlyMode(CTV_stylus_only_mode.isChecked());
                    mAccessibilityService = null;
                }
                break;
            case R.id.IV_my_github:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MY_GIT_HUB_URL));
                startActivity(browserIntent);
                break;
        }
    }
}
