package tw.com.daxia.virtualsoftkeys.service.view;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import tw.com.daxia.virtualsoftkeys.common.SPFManager;
import tw.com.daxia.virtualsoftkeys.service.ServiceFloating;

/**
 * Created by daxia on 2017/4/26.
 */

public class TouchEventView implements View.OnTouchListener {

    private final static int MINI_TOUCH_GESTURE_HIEGHT_SENSITIVITY = 4;
    private int miniTouchGestureHeight;


    /*
     * View
     */
    private View touchView;
    protected ServiceFloating accessibilityService;
    private float initialTouchY;

    /*
     *  Listener
     */

    /*
     * Configure
     */
    private boolean stylusOnlyMode;

    public TouchEventView(ServiceFloating accessibilityService) {
        //Set variable
        this.accessibilityService = accessibilityService;
        loadConfigure();
        init();
    }


    private void init() {
        touchView = new View(accessibilityService);
        touchView.setBackgroundColor(Color.parseColor("#00000000"));
        touchView.setOnTouchListener(this);
    }

    /*
     * public method
     */

    public void loadConfigure() {
        stylusOnlyMode = SPFManager.getStylusOnlyMode(accessibilityService);
    }

    public void updateMiniTouchGestureHeight(@Nullable Integer heightPx) {
        miniTouchGestureHeight = heightPx / MINI_TOUCH_GESTURE_HIEGHT_SENSITIVITY;
    }

    public void updateParamsForLocation(WindowManager windowManager, Boolean isPortrait) {
        if (isPortrait) {
            miniTouchGestureHeight = SPFManager.getTouchviewPortraitHeight(accessibilityService) / MINI_TOUCH_GESTURE_HIEGHT_SENSITIVITY;
            //transparent color
            windowManager.addView(touchView, createTouchViewParams(SPFManager.getTouchviewPortraitHeight(accessibilityService),
                    SPFManager.getTouchviewPortraitWidth(accessibilityService), SPFManager.getTouchviewPortraitPosition(accessibilityService)));
        } else {
            miniTouchGestureHeight = SPFManager.getTouchviewLandscapeHeight(accessibilityService) / MINI_TOUCH_GESTURE_HIEGHT_SENSITIVITY;
            //transparent color
            windowManager.addView(touchView, createTouchViewParams(SPFManager.getTouchviewLandscapeHeight(accessibilityService),
                    SPFManager.getTouchviewLandscapeWidth(accessibilityService), SPFManager.getTouchviewLandscapePosition(accessibilityService)));
        }

    }

    public void updateParamsForLocation(WindowManager windowManager, WindowManager.LayoutParams params) {
        windowManager.updateViewLayout(touchView, params);
    }

    private WindowManager.LayoutParams createTouchViewParams(int heightPx, int weightPx, int position) {
        WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    weightPx,
                    heightPx,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    weightPx,
                    heightPx,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
        params.gravity = Gravity.BOTTOM | Gravity.LEFT;
        params.x = position;
        params.y = 0;
        return params;
    }

    public View getTouchView() {
        return touchView;
    }


    private void touchViewTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialTouchY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if ((initialTouchY - event.getRawY()) > miniTouchGestureHeight) {
                    accessibilityService.showSoftKeyBar();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e("test","onTouch = " + event);
        if (stylusOnlyMode) {
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) {
                touchViewTouchEvent(event);
            }
        } else {
            touchViewTouchEvent(event);
        }

        return false;
    }
}
