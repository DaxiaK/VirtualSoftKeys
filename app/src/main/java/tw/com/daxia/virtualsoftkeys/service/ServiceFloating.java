package tw.com.daxia.virtualsoftkeys.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageButton;

import tw.com.daxia.virtualsoftkeys.R;
import tw.com.daxia.virtualsoftkeys.common.ScreenHepler;


public class ServiceFloating extends AccessibilityService implements View.OnClickListener, View.OnLongClickListener {

    private final static String GOOGLE_APP_PACKAGE_NAME = "com.google.android.googlequicksearchbox";

    private static ServiceFloating sSharedInstance;


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


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        sSharedInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(Service.WINDOW_SERVICE);
        touchView = new View(this);
        touchView.setBackgroundColor(Color.parseColor("#74525c"));
        windowManager.addView(touchView, createTouchViewParms(ScreenHepler.dpToPixel(getResources(), 10)));
        touchView.setOnTouchListener(new View.OnTouchListener() {
            private float initialTouchX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
//
//                if (event.getPointerCount() > 0 && event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialTouchX = event.getRawX();
                        Log.e("test", "initialTouchX =" + initialTouchX);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("test", "event.getRawX() =" + event.getRawX());
                        if (event.getRawX() - initialTouchX > 10) {
                            showSoftKeyBar();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                }
//                }
                return false;
            }
        });
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
        Log.e("test", "event = " + event.getEventType());
    }

    @Override
    public void onInterrupt() {

    }

    public void updateTouchView(int px) {
        if (touchView != null) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) touchView.getLayoutParams();
            params.height = px;
            windowManager.updateViewLayout(touchView, params);
        }
    }

    private WindowManager.LayoutParams createTouchViewParms(int px) {

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                px,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM;
        params.x = 0;
        params.y = 0;
        return params;
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