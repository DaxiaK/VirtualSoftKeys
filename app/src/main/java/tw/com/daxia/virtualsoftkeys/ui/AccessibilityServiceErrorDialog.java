package tw.com.daxia.virtualsoftkeys.ui;

import android.app.Dialog;
import android.content.Intent;
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
public class AccessibilityServiceErrorDialog extends DialogFragment implements View.OnClickListener {


private Button But_go_to_accessibility;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View rootView = inflater.inflate(R.layout.dialog_accessibility_service_error, container);
        But_go_to_accessibility = (Button) rootView.findViewById(R.id.But_go_to_accessibility);
        But_go_to_accessibility.setOnClickListener(this);
        return rootView;
    }



    private void gotoSettingPage() {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_go_to_accessibility:
                gotoSettingPage();
                break;
        }
        this.dismiss();
    }


}
