package tw.com.daxia.virtualsoftkeys.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckedTextView;

import tw.com.daxia.virtualsoftkeys.R;
import tw.com.daxia.virtualsoftkeys.common.SPFManager;

/**
 * Created by daxia on 2016/10/7.
 */

public class DescriptionDialog extends DialogFragment implements View.OnClickListener {

    private CheckedTextView CTV_close_description;
    private Button But_dismiss_description;

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
        View rootView = inflater.inflate(R.layout.dialog_description, container);
        CTV_close_description = (CheckedTextView) rootView.findViewById(R.id.CTV_close_description);
        CTV_close_description.setOnClickListener(this);
        But_dismiss_description = (Button) rootView.findViewById(R.id.But_dismiss_description);
        But_dismiss_description.setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CTV_close_description:
                CTV_close_description.toggle();
                break;
            case R.id.But_dismiss_description:
                SPFManager.setDescriptionClose(getActivity(), CTV_close_description.isChecked());
                this.dismiss();
                break;
        }
    }
}
