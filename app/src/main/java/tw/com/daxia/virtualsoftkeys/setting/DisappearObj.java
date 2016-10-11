package tw.com.daxia.virtualsoftkeys.setting;

import android.content.Context;

import tw.com.daxia.virtualsoftkeys.common.SPFManager;

/**
 * Created by daxia on 2016/10/11.
 */

public class DisappearObj {

    public final static int TIME_NEVER = -1;
    public final static int TIME_NOW = 0;
    public final static int TIME_1S = 1 * 1000;
    public final static int TIME_3S = 3 * 1000;
    public final static int TIME_5S = 5 * 1000;

    private int configTime;
    private Context context;

    public DisappearObj(Context context) {
        this.context = context;
        this.configTime = getTimeByPosition(SPFManager.getDisappearPosition(context));
    }

    public void updateConfigTime(int spinnerPosition) {
        SPFManager.setDisappearPosition(context, spinnerPosition);
        configTime = getTimeByPosition(spinnerPosition);
    }

    public int getConfigTime() {
        return configTime;
    }

    /**
     * Position is form bar_disappear_time.
     * @param spinnerPosition
     * @return
     */
    public int getTimeByPosition(int spinnerPosition) {
        int disappearTime;
        switch (spinnerPosition) {
            case 1:
                disappearTime = TIME_NOW;
                break;
            case 2:
                disappearTime = TIME_1S;
                break;
            case 3:
                disappearTime = TIME_3S;
                break;
            case 4:
                disappearTime = TIME_5S;
                break;
            // = case 0
            default:
                disappearTime = TIME_NEVER;
                break;
        }
        return disappearTime;
    }


}
