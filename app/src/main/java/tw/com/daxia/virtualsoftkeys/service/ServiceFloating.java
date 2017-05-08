package tw.com.daxia.virtualsoftkeys.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import tw.com.daxia.virtualsoftkeys.R;
import tw.com.daxia.virtualsoftkeys.common.SPFManager;
import tw.com.daxia.virtualsoftkeys.common.ScreenHepler;
import tw.com.daxia.virtualsoftkeys.service.view.SoftKeyTabletLandscapeView;
import tw.com.daxia.virtualsoftkeys.service.view.SoftKeyView;
import tw.com.daxia.virtualsoftkeys.service.view.TouchEventView;
import tw.com.daxia.virtualsoftkeys.setting.DisappearObj;


public class ServiceFloating extends AccessibilityService {


    private static ServiceFloating sSharedInstance;
    /**
     * Handler
     */
    private SoftKeyBarHandler softKeyBarHandler;
    private boolean isDelay = false;

    /**
     * Config
     */

    private DisappearObj disappearObj;

    private boolean isPortrait;
    private boolean rotateHidden;

    /**
     * View
     */
    private WindowManager windowManager;
    private SoftKeyView softKeyBar;
    private TouchEventView touchEventView;

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
        if (rotateHidden) {
            hiddenSoftKeyBar(true);
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
        rotateHidden = SPFManager.getRotateHidden(this);
        updateServiceInfo(SPFManager.getSmartHidden(this));
        //Check permission & orientation
       boolean canDrawOverlays = checkSystemAlertWindowPermission();
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
        if (touchEventView != null) {
            windowManager.removeView(touchEventView.getTouchView());
        }
        if (softKeyBar != null) {
            windowManager.removeView(softKeyBar.getBaseView());
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //Experimental features for smart hidden
        try {
            //Check the focused view is from Edittext object
            Class<?> clazz = Class.forName(event.getClassName().toString());
            if ((Editable.class.isAssignableFrom(clazz) || EditText.class.isAssignableFrom(clazz))
                    && disappearObj.getConfigTime() == DisappearObj.TIME_NEVER
                    && softKeyBar != null) {
                softKeyBar.getBaseView().setVisibility(View.GONE);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            //do nothing
        }
    }

    @Override
    public void onInterrupt() {
        //Do nothing
    }

    private void updateServiceInfo(boolean allowTraceEvent) {
        AccessibilityServiceInfo info = getServiceInfo();
        if (allowTraceEvent) {
            info.eventTypes = AccessibilityEvent.TYPE_VIEW_FOCUSED;
        } else {
            info.eventTypes = 0;
        }
        setServiceInfo(info);
    }


    private void initTouchView() {
        touchEventView = new TouchEventView(this);
        touchEventView.updateParamsForLocation(windowManager,isPortrait);
    }


    /**
     * Update config by mainActivity
     */
    public void updateTouchView(@Nullable Integer heightPx,
                                @Nullable Integer widthPx,
                                @Nullable Integer position) {
        //set config
        if (touchEventView != null) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) touchEventView.getTouchView().getLayoutParams();
            if (heightPx != null) {
                touchEventView.updateMiniTouchGestureHeight(heightPx);
                params.height = heightPx;
            }
            if (widthPx != null) {
                params.width = widthPx;
            }
            if (position != null) {
                params.x = position;
            }
            touchEventView.updateParamsForLocation(windowManager,params);
        }
    }

    public void refreshSoftKey() {
        if(softKeyBar!=null){
            softKeyBar.refresh();
        }
    }



    public void updateTouchViewConfigure() {
        if (touchEventView != null) {
            touchEventView.loadConfigure();
        }
    }

    public void updateDisappearTime(int spinnerPosition) {
        this.disappearObj.updateConfigTime(spinnerPosition);
    }

    public void updateSmartHidden(boolean smartHidden) {
        updateServiceInfo(smartHidden);
    }

    public void updateRotateHidden(boolean rotateHidden) {
        this.rotateHidden = rotateHidden;
    }


    public void showSoftKeyBar() {
        if (softKeyBar == null) {
            softKeyBar = new SoftKeyTabletLandscapeView(this);
            windowManager.addView(softKeyBar.getBaseView(), softKeyBar.getLayoutParamsForLocation());
        } else {
            softKeyBar.getBaseView().setVisibility(View.VISIBLE);
        }
    }


    /**
     * Handler + Runnable
     */

    public void hiddenSoftKeyBar(boolean now) {
        if (now) {
            softKeyBarHandler.removeCallbacksAndMessages(null);
            isDelay = false;
            softKeyBarHandler.sendEmptyMessage(0);
        } else {
            if (disappearObj.getConfigTime() >= DisappearObj.TIME_NOW && !isDelay) {
                softKeyBarHandler.sendEmptyMessageDelayed(0, disappearObj.getConfigTime());
                isDelay = true;
            }
        }
    }

    private static class SoftKeyBarHandler extends Handler {

        private WeakReference<ServiceFloating> mService;

        SoftKeyBarHandler(ServiceFloating aService) {
            mService = new WeakReference<>(aService);
        }

        @Override
        public void handleMessage(Message msg) {
            ServiceFloating theService = mService.get();
            if (theService.softKeyBar != null) {
                theService.softKeyBar.getBaseView().setVisibility(View.GONE);
            }
            theService.isDelay = false;
        }
    }


}