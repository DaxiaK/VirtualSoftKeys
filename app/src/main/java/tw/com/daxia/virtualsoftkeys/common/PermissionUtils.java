package tw.com.daxia.virtualsoftkeys.common;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by daxia on 2018/2/6.
 */

public class PermissionUtils {

    public static boolean checkSystemAlertWindowPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(context)) {
            return false;
        }
        return true;
    }
}
