package tw.com.daxia.virtualsoftkeys.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;

/**
 * Created by daxia on 2016/9/23.
 */

public class ScreenHepler {

    private final static int DEFAULT_TOUCHVIEW_HEIGHT_DP = 10;
    private final static int DEFAULT_TOUCHVIEW_WIDTH = ViewGroup.LayoutParams.MATCH_PARENT;


    public static int dpToPixel(final Resources r, final int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getDefautlTouchviewHeight(Context context) {
        return dpToPixel(context.getResources(), DEFAULT_TOUCHVIEW_HEIGHT_DP);
    }

    public static int getDefautlTouchviewWidth() {
        return DEFAULT_TOUCHVIEW_WIDTH;
    }

    public static boolean isPortrait(Resources resources) {
        return resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }


}
