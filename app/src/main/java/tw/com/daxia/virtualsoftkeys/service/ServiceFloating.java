package tw.com.daxia.virtualsoftkeys.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageButton;

import tw.com.daxia.virtualsoftkeys.R;
import tw.com.daxia.virtualsoftkeys.common.ScreenHepler;


public class ServiceFloating extends AccessibilityService implements View.OnClickListener, View.OnLongClickListener {


    private WindowManager windowManager;
    private View softKeyBar;
    private View touchView;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = getResources().getConfiguration().orientation;
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int actual_orientation = -1;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE
                && (rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_90)) {
            orientation = Configuration.ORIENTATION_LANDSCAPE;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT
                && (rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_90)) {
            orientation = Configuration.ORIENTATION_PORTRAIT;
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE
                && (rotation == Surface.ROTATION_180
                || rotation == Surface.ROTATION_270)) {
            orientation = 3;//any constant for reverse landscape orientation;
        } else {
            if (orientation == Configuration.ORIENTATION_PORTRAIT
                    && (rotation == Surface.ROTATION_180
                    || rotation == Surface.ROTATION_270)) {
                orientation = 4;//any constant for reverse portrait orientation;
            }
        }
        Log.e("tset", "orientation=" + orientation);
    }

    @Override
    protected void onServiceConnected() {
        Log.e("test", "onServiceConnected");
        super.onServiceConnected();
//        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
//        info.flags = AccessibilityServiceInfo.DEFAULT;
//        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
//        setServiceInfo(info);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(Service.WINDOW_SERVICE);
        touchView = new View(this);
        touchView.setBackgroundColor(Color.parseColor("#74525c"));
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                ScreenHepler.dpToPixel(getResources(), 10),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM;
        params.x = 0;
        params.y = 0;
        windowManager.addView(touchView, params);
        touchView.setOnTouchListener(new View.OnTouchListener() {
            private WindowManager.LayoutParams paramsF = params;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("test", "Count = " + event.getPointerCount());
//                    Log.e("test", "getToolType = " + event.getToolType(0));
//                    if (event.getPointerCount() > 0 && event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("test", "ACTION_DOWN");
                        initialX = paramsF.x;
                        initialY = paramsF.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        Log.e("test", "initialX =" + initialX + " , initialY =" + initialY);
                        Log.e("test", "initialTouchX =" + initialTouchX + " , initialTouchY =" + initialTouchY);

                        break;
                    case MotionEvent.ACTION_UP:
//                                Log.e("test", "ACTION_UP");
                        break;
                    case MotionEvent.ACTION_MOVE:
//                                Log.e("test", "ACTION_MOVE");
                        paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                        paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                        showSoftKeyBar();
                        break;
                }
//                    }
                return false;
            }
        });
        Log.e("tset", "onCreate");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (softKeyBar != null) {
            windowManager.removeView(softKeyBar);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e("test", "event = " + event.getEventType());
    }

    @Override
    public void onInterrupt() {

    }

    private void showSoftKeyBar() {
        if (softKeyBar == null) {
            LayoutInflater li = LayoutInflater.from(this);
            softKeyBar = li.inflate(R.layout.softkey_bar, null, true);
            softKeyBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (softKeyBar != null) {
                        softKeyBar.setVisibility(View.GONE);
                    }
                    return true;
                }
            });
            ImageButton IB_button_home, IB_button_back, IB_button_recents;
            IB_button_back = (ImageButton) softKeyBar.findViewById(R.id.IB_button_back);
            IB_button_back.setOnClickListener(this);
            IB_button_home = (ImageButton) softKeyBar.findViewById(R.id.IB_button_home);
            IB_button_home.setOnClickListener(this);
            IB_button_home.setOnLongClickListener(this);
            IB_button_recents = (ImageButton) softKeyBar.findViewById(R.id.IB_button_recents);
            IB_button_recents.setOnClickListener(this);
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    ScreenHepler.dpToPixel(getResources(), 48),
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.BOTTOM;
            params.x = 0;
            params.y = 0;
            windowManager.addView(softKeyBar, params);
        } else {
            softKeyBar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
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
        if (softKeyBar != null) {
            softKeyBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.IB_button_back:
                break;
            case R.id.IB_button_home:
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.googlequicksearchbox");
                if (intent != null) {
                    // We found the activity now start the activity
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    // Bring user to the market or let them choose an app?
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.setData(Uri.parse("market://details?id=" + "com.package.name"));
                    startActivity(intent);
                }
                break;
            case R.id.IB_button_recents:
                break;
        }

        return true;
    }
}