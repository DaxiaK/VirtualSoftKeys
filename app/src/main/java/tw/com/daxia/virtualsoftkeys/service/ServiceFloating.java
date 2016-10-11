package tw.com.daxia.virtualsoftkeys.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageButton;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import tw.com.daxia.virtualsoftkeys.R;
import tw.com.daxia.virtualsoftkeys.common.SPFManager;
import tw.com.daxia.virtualsoftkeys.common.ScreenHepler;
import tw.com.daxia.virtualsoftkeys.setting.DisappearObj;


public class ServiceFloating extends AccessibilityService implements View.OnClickListener, View.OnLongClickListener {

    private final static String GOOGLE_APP_PACKAGE_NAME = "com.google.android.googlequicksearchbox";

    private static ServiceFloating sSharedInstance;
    /**
     * Handler
     */
    private SoftKeyBarHandler softKeyBarHandler;
    private boolean isDelay = false;

    /**
     * Config
     */
    private int miniTouchGestureHeight;
    private final static int miniTouchGestureHeightSensitivity = 4;
    private final static int SOFTKEY_BAR_HEIGHT = 48;

    private DisappearObj disappearObj;

    private boolean stylusOnlyMode;
    private boolean canDrawOverlays;
    private boolean isPortrait;

    /**
     * View
     */
    private WindowManager windowManager;
    private View softKeyBar;
    private View touchView;

    public static ServiceFloating getSharedInstance() {
        return sSharedInstance;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        sSharedInstance = null;
        return false;
    }


    private boolean checkSystemAlertWindowPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(this)) {
            return false;
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ScreenHepler.isPortrait(getResources())) {
            isPortrait = true;
            updateTouchView(SPFManager.getTouchviewPortraitHeight(this), SPFManager.getTouchviewPortraitWidth(this),
                    SPFManager.getTouchviewPortraitPosition(this));
        } else {
            isPortrait = false;
            updateTouchView(SPFManager.getTouchviewLandscapeHeight(this), SPFManager.getTouchviewLandscapeWidth(this),
                    SPFManager.getTouchviewLandscapePosition(this));
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //init
        sSharedInstance = this;
        disappearObj = new DisappearObj(this);
        softKeyBarHandler = new SoftKeyBarHandler(this);
        //Check permission & orientation
        canDrawOverlays = checkSystemAlertWindowPermission();
        if (canDrawOverlays) {
            windowManager = (WindowManager) getSystemService(Service.WINDOW_SERVICE);
            if (ScreenHepler.isPortrait(getResources())) {
                isPortrait = true;
            } else {
                isPortrait = false;
            }
            initTouchView();
        } else {
            Toast.makeText(this, getString(R.string.Toast_allow_system_alert_first), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (touchView != null) {
            windowManager.removeView(touchView);
        }
        if (softKeyBar != null) {
            windowManager.removeView(softKeyBar);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //Do nothing
    }

    @Override
    public void onInterrupt() {
        //Do nothing
    }


    private void initTouchView() {
        touchView = new View(this);
        touchView.setBackgroundColor(Color.parseColor("#00000000"));
        if (isPortrait) {
            miniTouchGestureHeight = SPFManager.getTouchviewPortraitHeight(this) / miniTouchGestureHeightSensitivity;
            //transparent color
            windowManager.addView(touchView, createTouchViewParms(SPFManager.getTouchviewPortraitHeight(this),
                    SPFManager.getTouchviewPortraitWidth(this), SPFManager.getTouchviewPortraitPosition(this)));
        } else {
            miniTouchGestureHeight = SPFManager.getTouchviewLandscapeHeight(this) / miniTouchGestureHeightSensitivity;
            //transparent color
            windowManager.addView(touchView, createTouchViewParms(SPFManager.getTouchviewLandscapeHeight(this),
                    SPFManager.getTouchviewLandscapeWidth(this), SPFManager.getTouchviewLandscapePosition(this)));
        }
        touchView.setOnTouchListener(touchViewOnTouchListener);
    }

    private View.OnTouchListener touchViewOnTouchListener = new View.OnTouchListener() {
        private float initialTouchY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (stylusOnlyMode) {
                if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) {
                    touchViewTouchEvent(event);
                }
            } else {
                touchViewTouchEvent(event);
            }

            return false;
        }

        private void touchViewTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialTouchY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    if ((initialTouchY - event.getRawY()) > miniTouchGestureHeight) {
                        showSoftKeyBar();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
            }
        }
    };

    /**
     * Update config by mainActivity
     */
    public void updateTouchView(@Nullable Integer heightPx, @Nullable Integer widthPx, @Nullable Integer position) {
        //set config
        if (touchView != null) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) touchView.getLayoutParams();
            if (heightPx != null) {
                this.miniTouchGestureHeight = heightPx / miniTouchGestureHeightSensitivity;
                params.height = heightPx;
            }
            if (widthPx != null) {
                params.width = widthPx;
            }
            if (position != null) {
                params.x = position;
            }
            windowManager.updateViewLayout(touchView, params);
        }
    }

    public void updateStylusOnlyMode(boolean stylusOnly) {
        this.stylusOnlyMode = stylusOnly;
    }

    public void updateDisappearTime(int spinnerPosition) {
        this.disappearObj.updateConfigTime(spinnerPosition);
    }


    private WindowManager.LayoutParams createTouchViewParms(int heightPx, int weightPx, int position) {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                weightPx,
                heightPx,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM | Gravity.LEFT;
        params.x = position;
        params.y = 0;
        return params;
    }


    private void showSoftKeyBar() {
        if (softKeyBar == null) {
            LayoutInflater li = LayoutInflater.from(this);
            softKeyBar = li.inflate(R.layout.navigation_bar, null, true);
            softKeyBar.setOnTouchListener(new View.OnTouchListener() {
                private float firstSoftKeyTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hiddenSoftKeyBar();
                    if (stylusOnlyMode) {
                        if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) {
                            touchViewTouchEvent(event);
                        }
                    } else {
                        touchViewTouchEvent(event);
                    }
                    return false;
                }

                private void touchViewTouchEvent(MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            firstSoftKeyTouchY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            //Close the softKeyBar after swiping down more the 1/4 height
                            if ((event.getRawY() - firstSoftKeyTouchY) > (ScreenHepler.dpToPixel(getResources(), SOFTKEY_BAR_HEIGHT) / 4)) {
                                softKeyBarHandler.removeCallbacksAndMessages(null);
                                isDelay = false;
                                softKeyBarHandler.sendEmptyMessage(0);
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                    }
                }
            });
            //Init all button
            ImageButton IB_button_home, IB_button_back, IB_button_recents;
            IB_button_back = (ImageButton) softKeyBar.findViewById(R.id.IB_button_back);
            IB_button_back.setOnClickListener(this);
            IB_button_back.setOnLongClickListener(this);
            IB_button_home = (ImageButton) softKeyBar.findViewById(R.id.IB_button_home);
            IB_button_home.setOnClickListener(this);
            IB_button_home.setOnLongClickListener(this);
            IB_button_recents = (ImageButton) softKeyBar.findViewById(R.id.IB_button_recents);
            IB_button_recents.setOnClickListener(this);
            IB_button_recents.setOnLongClickListener(this);
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    ScreenHepler.dpToPixel(getResources(), SOFTKEY_BAR_HEIGHT),
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.windowAnimations = android.R.style.Animation_InputMethod;
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            params.x = 0;
            params.y = 0;
            windowManager.addView(softKeyBar, params);
        } else {
            softKeyBar.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Handler + Runnable
     */

    private void hiddenSoftKeyBar() {
        if (disappearObj.getConfigTime() >= DisappearObj.TIME_NOW && !isDelay) {
            softKeyBarHandler.sendEmptyMessageDelayed(0, disappearObj.getConfigTime());
            isDelay = true;
        }
    }

    private static class SoftKeyBarHandler extends Handler {

        private WeakReference<ServiceFloating> mService;

        public SoftKeyBarHandler(ServiceFloating aService) {
            mService = new WeakReference<>(aService);
        }
        @Override
        public void handleMessage(Message msg) {
            ServiceFloating theService = mService.get();
            if (theService.softKeyBar != null) {
                theService.softKeyBar.setVisibility(View.GONE);
            }
            theService.isDelay = false;
        }
    }


    /**
     * Implements
     */

    @Override
    public void onClick(View v) {
        //Add HapticFeedback
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        switch (v.getId()) {
            case R.id.IB_button_back:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case R.id.IB_button_home:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                break;
            case R.id.IB_button_recents:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                break;
        }
        hiddenSoftKeyBar();
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.IB_button_back:
                break;
            case R.id.IB_button_home:
                Intent intent = getPackageManager().getLaunchIntentForPackage(GOOGLE_APP_PACKAGE_NAME);
                if (intent != null) {
                    // We found the activity now start the activity
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    // Bring user to the market or let them choose an app?
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.setData(Uri.parse("market://details?id=" + GOOGLE_APP_PACKAGE_NAME));
                    startActivity(intent);
                }
                break;
            case R.id.IB_button_recents:
                break;
        }

        return true;
    }
}