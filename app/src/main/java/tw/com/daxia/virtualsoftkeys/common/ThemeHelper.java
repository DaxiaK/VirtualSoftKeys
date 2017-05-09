package tw.com.daxia.virtualsoftkeys.common;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;

/**
 * Created by daxia on 2016/9/23.
 */

public class ThemeHelper {

    /**
     * It should be return Drawable , or you will get the VerifyError on pre-lollipop devices.
     * See http://stackoverflow.com/questions/31474297/could-not-find-rippledrawable
     * @param pressedColor
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getPressedColorRippleDrawable(int pressedColor) {
        return new RippleDrawable(ColorStateList.valueOf(pressedColor), null, null);
    }

    public static boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness < 0.5) {
            return false; // It's a light color
        } else {
            return true; // It's a dark color
        }
    }
}
