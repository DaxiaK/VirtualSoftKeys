package tw.com.daxia.virtualsoftkeys.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import tw.com.daxia.virtualsoftkeys.R;

/**
 * Created by daxia on 2016/10/7.
 */

public class AboutDialog extends DialogFragment {

    private StringBuilder license;
    private TextView TV_about_text;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.dialog_about, container);
        TV_about_text = (TextView) rootView.findViewById(R.id.TV_about_text);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        license = new StringBuilder();
        license.append("This project release by APACHE License:\n");
        license.append(
                new LicenseObj("VirtualSoftKeys", "Daxia", "2016 - 2017", LicenseObj.APACHE)
                        .getLicense());
        license.append("\nI use some lib from:\n");
        license.append(
                new LicenseObj("HoloColorPicker", "Lars Werkman", "2012", LicenseObj.APACHE)
                        .getLicense());
        TV_about_text.setText(license.toString());
    }

    public class LicenseObj {

        public final static int MIT = 0;
        public final static int APACHE = 1;
        public final static int GPLv2 = 2;
        public final static int BSD = 3;
        public final static int MPLv2 = 4;

        private String softwareName;
        private String author;
        private String year;
        private int license;

        public LicenseObj(String softwareName, String author, String year, int license) {
            this.softwareName = softwareName;
            this.author = author;
            this.year = year;
            this.license = license;
        }

        public String getLicense() {
            switch (license) {
                case APACHE:
                    return "\n==" + softwareName + "==\n\n" +
                            "copyright " + year + ", " + author + "\n\n" +
                            "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                            "you may not use this file except in compliance with the License.\n" +
                            "You may obtain a copy of the License at\n" +
                            "\n" +
                            "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                            "\n" +
                            "Unless required by applicable law or agreed to in writing, software\n" +
                            "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                            "See the License for the specific language governing permissions and\n" +
                            "limitations under the License." +
                            "\n\n=====\n";
                default:
                    return "";
            }
        }
    }

}
