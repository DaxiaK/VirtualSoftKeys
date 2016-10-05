package tw.com.daxia.virtualsoftkeys.common;

import android.content.Context;
import android.content.SharedPreferences;

import tw.com.daxia.virtualsoftkeys.BuildConfig;


/**
 * Created by daxia on 2016/7/31.
 */
public class SPFManager {

    private static final String SPF_CONFIG_NEME = "CONFIG";
    private static final String CONFIG_TOUCHVIEW_HEIGHT = "OUCHVIEW_HEIGHT";
    private static final String CONFIG_TOUCHVIEW_WIDTH = "TOUCHVIEW_WIDTH";
    private static final String CONFIG_TOUCHVIEW_POSITION = "TOUCHVIEW_POSITION";

    private static final String CONFIG_STYLUS_ONLY_MODE = "STYLUS_ONLY_MODE";

    private static final String SPF_CRASHLOG = "CRASHLOG";
    private static final String CRASHLOG_MESSAGE = "CRASHLOG_MESSAGE";
    private static final String CRASHLOG_LAST_CRASH = "CRASHLOG_LAST_CRASH";


    private static final String SPF_SYSTEM = "SYSTEM";
    private static final String SYSTEM_VERSIONCODE = "VERSIONCODE";
    public static final int DEFAULT_VERSIONCODE = -1;


    /**
     * Setting Method
     */

    public static int getTouchviewHeight(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG_NEME, 0);
        return settings.getInt(CONFIG_TOUCHVIEW_HEIGHT, ScreenHepler.getDefautlTouchviewHeight(context));
    }

    public static void setTouchviewHeight(Context context, int heightPx) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG_NEME, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putInt(CONFIG_TOUCHVIEW_HEIGHT, heightPx);
        PE.commit();
    }

    public static int getTouchviewWidth(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG_NEME, 0);
        return settings.getInt(CONFIG_TOUCHVIEW_WIDTH, ScreenHepler.getDefautlTouchviewWidth());
    }

    public static void setTouchviewWidth(Context context, int widthPx) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG_NEME, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putInt(CONFIG_TOUCHVIEW_WIDTH, widthPx);
        PE.commit();
    }

    public static int getTouchviewPosition(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG_NEME, 0);
        //Default width is match_content , so  position = 0
        return settings.getInt(CONFIG_TOUCHVIEW_POSITION, 0);
    }

    public static void setTouchviewPosition(Context context, int position) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG_NEME, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putInt(CONFIG_TOUCHVIEW_POSITION, position);
        PE.commit();
    }


    public static boolean getStylusOnlyMode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG_NEME, 0);
        return settings.getBoolean(CONFIG_STYLUS_ONLY_MODE, false);
    }

    public static void setStylusOnlyMode(Context context, boolean stylusOnly) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG_NEME, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putBoolean(CONFIG_STYLUS_ONLY_MODE, stylusOnly);
        PE.commit();
    }

    /**
     * CrashLog
     */
    //TODO Add the  Crash trace
    public static void setCrashLog(Context context, String message) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CRASHLOG, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putString(CRASHLOG_MESSAGE, message);
        PE.putBoolean(CRASHLOG_LAST_CRASH, true);
        PE.commit();
    }

    public static String getLastCrashLog(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CRASHLOG, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putBoolean(CRASHLOG_LAST_CRASH, false).commit();
        return settings.getString(CRASHLOG_MESSAGE, "");
    }

    public static boolean isLastCrash(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CRASHLOG, 0);
        return settings.getBoolean(CRASHLOG_LAST_CRASH, false);
    }

    /**
     * For System
     */
    //TODO Add the version trace
    public static void setVersionCode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putInt(SYSTEM_VERSIONCODE, BuildConfig.VERSION_CODE);
        PE.commit();
    }


    public static int getVersionCode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        return settings.getInt(SYSTEM_VERSIONCODE, DEFAULT_VERSIONCODE);
    }


}
