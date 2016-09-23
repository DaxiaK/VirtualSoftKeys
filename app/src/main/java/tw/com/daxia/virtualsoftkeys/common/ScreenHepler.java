package tw.com.daxia.virtualsoftkeys.common;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by daxia on 2016/9/23.
 */

public class ScreenHepler {

    public static int dpToPixel(final Resources r, final int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }


}
