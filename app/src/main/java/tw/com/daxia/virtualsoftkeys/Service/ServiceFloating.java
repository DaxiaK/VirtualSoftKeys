package tw.com.daxia.virtualsoftkeys.Service;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import tw.com.daxia.virtualsoftkeys.R;


public class ServiceFloating extends AccessibilityService implements View.OnClickListener {


    private WindowManager windowManager;
    private View chatHead;
    private PopupWindow pwindo;


    ArrayList<String> myArray;
    //    ArrayList<PInfo> apps;
    List listCity;


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
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

//        RetrievePackages getInstalledPackages = new RetrievePackages(getApplicationContext());
//        apps = getInstalledPackages.getInstalledApps(false);
        myArray = new ArrayList<String>();
//        for (int i = 0; i < apps.size(); ++i) {
//            myArray.add(apps.get(i).appname);
//        }
//
//        listCity = new ArrayList();
//        for (int i = 0; i < apps.size(); ++i) {
//            listCity.add(apps.get(i));
//        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater li = LayoutInflater.from(this);
        chatHead = li.inflate(R.layout.softkey_bar, null, false);

        ImageButton IB_button_home, IB_button_back, IB_button_menu;
        IB_button_back = (ImageButton) chatHead.findViewById(R.id.IB_button_back);
        IB_button_back.setOnClickListener(this);
        IB_button_home = (ImageButton) chatHead.findViewById(R.id.IB_button_home);
        IB_button_home.setOnClickListener(this);
        IB_button_menu = (ImageButton) chatHead.findViewById(R.id.IB_button_menu);
        IB_button_menu.setOnClickListener(this);


        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, r.getDisplayMetrics());

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                (int) px,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM;
        params.x = 0;
        params.y = 0;

        windowManager.addView(chatHead, params);

        try {
            chatHead.setOnTouchListener(new View.OnTouchListener() {
                private WindowManager.LayoutParams paramsF = params;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
//                    Log.e("test", "Count = " + event.getPointerCount());
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
//                            windowManager.updateViewLayout(chatHead, paramsF);
                            break;
                    }
//                    }
                    return false;
                }
            });
        } catch (
                Exception e
                )

        {
            // TODO: handle exception
        }

//		chatHead.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				initiatePopupWindow(chatHead);
//				_enable = false;
//				//				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//				//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				//				getApplicationContext().startActivity(intent);
//			}
//		});

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) {
            windowManager.removeView(chatHead);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e("test", event.getPackageName().toString());
    }

    @Override
    public void onInterrupt() {

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
            case R.id.IB_button_menu:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                break;
        }
//        windowManager.removeView(chatHead);
    }
}