package tw.com.daxia.virtualsoftkeys.setting;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import tw.com.daxia.virtualsoftkeys.R;
import tw.com.daxia.virtualsoftkeys.common.SPFManager;
import tw.com.daxia.virtualsoftkeys.common.ScreenHepler;
import tw.com.daxia.virtualsoftkeys.service.ServiceFloating;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private final static String TAG = "MainActivity";
    private SeekBar Seek_touch_area;
    private CheckedTextView CTV_stylus_only_mode;
    private View View_touchviewer;
    private  PermissionDialog permissionDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View_touchviewer = findViewById(R.id.View_touchviewer);
        Seek_touch_area = (SeekBar) findViewById(R.id.Seek_touch_area);
        CTV_stylus_only_mode = (CheckedTextView) findViewById(R.id.CTV_stylus_only_mode);
        //Init all view setting
        initSeekBar();
        initStylusMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean drawOverlays = checkSystemAlertWindowPermission();
        boolean accessibility = isAccessibilitySettingsOn();
        if(!drawOverlays || !accessibility){
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

    private void initSeekBar() {
        Seek_touch_area.setOnSeekBarChangeListener(touchviewHeightSeekBarListener);
        Seek_touch_area.setMax(ScreenHepler.getScreenHeight(this) / 20);
        Seek_touch_area.setProgress(SPFManager.getTouchviewHeight(this));
    }

    private void initStylusMode() {
        CTV_stylus_only_mode.setChecked(SPFManager.getStylusOnlyMode(this));
        CTV_stylus_only_mode.setOnClickListener(this);
    }

    private SeekBar.OnSeekBarChangeListener touchviewHeightSeekBarListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            SPFManager.setTouchviewHeight(MainActivity.this, seekBar.getProgress());
            ServiceFloating mAccessibilityService = ServiceFloating.getSharedInstance();
            if (mAccessibilityService != null) {
                mAccessibilityService.updateTouchView(seekBar.getProgress());
                mAccessibilityService = null;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.e("test", "onProgressChanged");
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) View_touchviewer.getLayoutParams();
            params.height = progress;
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
            Log.i(TAG, e.getMessage());
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
                Log.e("test", "checked = " + CTV_stylus_only_mode.isChecked());
                SPFManager.setStylusOnlyMode(this, CTV_stylus_only_mode.isChecked());
                ServiceFloating mAccessibilityService = ServiceFloating.getSharedInstance();
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateStylusOnlyMode(CTV_stylus_only_mode.isChecked());
                    mAccessibilityService = null;
                }
                break;
        }
    }
}
