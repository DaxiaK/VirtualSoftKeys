package tw.com.daxia.virtualsoftkeys.service.module;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.net.Uri;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageButton;

import tw.com.daxia.virtualsoftkeys.common.SPFManager;
import tw.com.daxia.virtualsoftkeys.service.ServiceFloating;

import static tw.com.daxia.virtualsoftkeys.common.Link.GOOGLE_APP_PACKAGE_NAME;
import static tw.com.daxia.virtualsoftkeys.common.Link.GOOGLE_PLAY_LINK;

/**
 * Created by daxia on 2017/4/26.
 */

public abstract class SofyKeyView {

    /*
     * View
     */
    private View baseView;
    private ImageButton IB_button_start, IB_button_end, IB_button_home;
    private ServiceFloating accessibilityService;
    /*
     *  Listener
     */
    private View.OnTouchListener baseViewTouchListener;
    private View.OnClickListener softKeyEventClickListener;
    private View.OnLongClickListener softKeyEventLongClickListener;

    /*
     * Configure
     */
    private boolean reverseFunctionButton;


    public SofyKeyView(ServiceFloating accessibilityService) {
        init(accessibilityService);
        loadConfigure();
        initBaseView();
        initButtonView();
        setSoftKeyEvent();

    }

    /*
     * The concrete method
     */

    /**
     * Link the base view
     */
    abstract void initBaseView();

    /**
     * Link the button view
     */
    abstract void initButtonView();


    private void init(ServiceFloating accessibilityService) {
        this.accessibilityService = accessibilityService;
    }

    /**
     * Get all configure from SPF.
     * It is also for refresh SPF or input new SPF.
     */
    public void loadConfigure() {
        this.reverseFunctionButton = SPFManager.getReverseFunctionButton(accessibilityService);

    }

    private void setSoftKeyEvent() {
        softKeyEventClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add HapticFeedback
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                //Click event
                if (v.getId() == IB_button_start.getId()) {
                    if (reverseFunctionButton) {
                        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                    } else {
                        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                } else if (v.getId() == IB_button_home.getId()) {
                    accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);

                } else if (v.getId() == IB_button_end.getId()) {
                    if (reverseFunctionButton) {
                        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    } else {
                        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                    }
                }

                accessibilityService.hiddenSoftKeyBar(false);
            }
        };
        softKeyEventLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.getId() == IB_button_start.getId()) {
                    if (reverseFunctionButton) {
                        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
                    }
                } else if (v.getId() == IB_button_home.getId()) {
                    Intent intent = accessibilityService.getPackageManager().getLaunchIntentForPackage(GOOGLE_APP_PACKAGE_NAME);
                    if (intent != null) {
                        // We found the activity now start the activity
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        accessibilityService.startActivity(intent);
                    } else {
                        // Bring user to the market or let them choose an app?
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.setData(Uri.parse(GOOGLE_PLAY_LINK + GOOGLE_APP_PACKAGE_NAME));
                        accessibilityService.startActivity(intent);
                    }

                } else if (v.getId() == IB_button_end.getId()) {
                    if (!reverseFunctionButton) {
                        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
                    }
                }
                //Only trigger long click
                return true;
            }
        };

        //Set the click listener
        IB_button_start.setOnClickListener(softKeyEventClickListener);
        IB_button_home.setOnClickListener(softKeyEventClickListener);
        IB_button_end.setOnClickListener(softKeyEventClickListener);

        //Set the long click listener
        IB_button_start.setOnLongClickListener(softKeyEventLongClickListener);
        IB_button_home.setOnLongClickListener(softKeyEventLongClickListener);
        IB_button_end.setOnLongClickListener(softKeyEventLongClickListener);
    }


    /*
     * The  public  method
     */
    public View getBaseView() {
        return baseView;
    }


}
