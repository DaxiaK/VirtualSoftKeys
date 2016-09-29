package tw.com.daxia.virtualsoftkeys;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import tw.com.daxia.virtualsoftkeys.common.SPFManager;
import tw.com.daxia.virtualsoftkeys.common.ScreenHepler;
import tw.com.daxia.virtualsoftkeys.service.ServiceFloating;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private final static int SYSTEM_ALERT_WINDOW_REQUEST_CODE = 0;
    private final static String TAG = "MainActivity";
    private SeekBar Seek_touch_area;
    private Button But_stop_service;
    private View View_touchviewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View_touchviewer = findViewById(R.id.View_touchviewer);
        Seek_touch_area = (SeekBar) findViewById(R.id.Seek_touch_area);
        initSeekBar();
        But_stop_service = (Button) findViewById(R.id.But_stop_service);
        But_stop_service.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSystemAlertWindowPermission();
        checkAccessibilitySettingsPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == SYSTEM_ALERT_WINDOW_REQUEST_CODE) {
            // ** if so check once again if we have permission */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // continue here - permission was granted
//                    goYourActivity();
                }
            }
        }
    }

    private void initSeekBar() {
        Seek_touch_area.setOnSeekBarChangeListener(seekBarOnSeekBarChange);
        Seek_touch_area.setMax(ScreenHepler.getScreenHeight(this) / 20);
        Seek_touch_area.setProgress(SPFManager.getTouchviewHeight(this));
    }

    private SeekBar.OnSeekBarChangeListener seekBarOnSeekBarChange = new SeekBar.OnSeekBarChangeListener() {


        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.e("test", "onStopTrackingTouch");
            ServiceFloating mAccessibilityService = ServiceFloating.getSharedInstance();
            if (mAccessibilityService != null) {
                mAccessibilityService.updateTouchView(seekBar.getProgress());
                SPFManager.setTouchviewHeight(MainActivity.this, seekBar.getProgress());
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        if (!Settings.canDrawOverlays(this)) {
            /** if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            /** request permission via start activity for result */
            startActivityForResult(intent, SYSTEM_ALERT_WINDOW_REQUEST_CODE);
            return false;
        }
        return true;
    }

    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.i(TAG, e.getMessage());
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }

        return false;
    }

    private boolean checkAccessibilitySettingsPermission() {
        if (!isAccessibilitySettingsOn(this)) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Seek_touch_area:
                break;
            case R.id.But_stop_service:
                break;
        }
    }
}
