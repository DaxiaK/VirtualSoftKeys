package tw.com.daxia.virtualsoftkeys.setting;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import tw.com.daxia.virtualsoftkeys.R;


/**
 * Created by daxia on 2016/8/27.
 */
public class PermissionDialog extends DialogFragment implements View.OnClickListener {


    private boolean systemAlertPermission, accessibilityPermission;
    private Button But_intent_system_alert, But_intent_accessibility;

    public static PermissionDialog newInstance(boolean systemAlertPermission, boolean accessibilityPermission) {
        Bundle args = new Bundle();
        PermissionDialog fragment = new PermissionDialog();
        args.putBoolean("systemAlertPermission", systemAlertPermission);
        args.putBoolean("accessibilityPermission", accessibilityPermission);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        systemAlertPermission = getArguments().getBoolean("systemAlertPermission", false);
        accessibilityPermission = getArguments().getBoolean("accessibilityPermission", false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.dialog_permission, container);
        But_intent_system_alert = (Button) rootView.findViewById(R.id.But_intent_system_alert);
        But_intent_system_alert.setOnClickListener(this);
        But_intent_accessibility = (Button) rootView.findViewById(R.id.But_intent_accessibility);
        But_intent_accessibility.setOnClickListener(this);
        initButton();
        return rootView;
    }


    private void initButton() {
        if (!systemAlertPermission && !accessibilityPermission) {
            //Use layout default value
        } else if (systemAlertPermission && !accessibilityPermission) {
            But_intent_system_alert.setText("已獲得授權");
            But_intent_system_alert.setEnabled(false);
            But_intent_accessibility.setText("前往頁面");
            But_intent_accessibility.setEnabled(true);
        } else if (!systemAlertPermission && accessibilityPermission) {
            //User change the Permission without this dialog
            But_intent_system_alert.setText("請允許此授權，並且重新執行在無障礙設定中的VirtualSoftKeys");
            But_intent_system_alert.setEnabled(true);
        }
    }

    private void gotoSettingPage() {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }

    private void gotoDrawOverlaysPage() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getActivity().getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_intent_system_alert:
                gotoDrawOverlaysPage();
                break;
            case R.id.But_intent_accessibility:
                gotoSettingPage();
                break;
        }
        this.dismiss();
    }


}
