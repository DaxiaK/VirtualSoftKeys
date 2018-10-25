package tw.com.daxia.virtualsoftkeys.config

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_functionconfig.*
import tw.com.daxia.virtualsoftkeys.MainActivity
import tw.com.daxia.virtualsoftkeys.R
import tw.com.daxia.virtualsoftkeys.common.Link.MY_GIT_HUB_URL
import tw.com.daxia.virtualsoftkeys.common.SPFManager
import tw.com.daxia.virtualsoftkeys.service.ServiceFloating
import tw.com.daxia.virtualsoftkeys.ui.ColorPickerDialogFragment

class FunctionConfigFragment : Fragment(){

    private val TAG = "FunctionConfigFragment";


    companion object {
        fun newInstance(): FunctionConfigFragment {
            return FunctionConfigFragment()
        }
    }

    private lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_functionconfig, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,"onViewCreated")
        initSharedConfig()
    }

    override fun onAttach(context: Context?) {
        Log.d(TAG,"onAttach")
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }


    private fun initSharedConfig() {
        //StylusMode
        CTV_stylus_only_mode.isChecked = SPFManager.getStylusOnlyMode(mainActivity)
        CTV_stylus_only_mode.setOnClickListener {
            CTV_stylus_only_mode.toggle()
            SPFManager.setStylusOnlyMode(mainActivity, CTV_stylus_only_mode.isChecked)
            val mAccessibilityService = ServiceFloating.getSharedInstance()
            mAccessibilityService?.updateTouchViewConfigure()
        }
        //Disappear time
        initDisappearSpinner()
        //Reverse button position
        CTV_reverse_button.isChecked = SPFManager.getReverseFunctionButton(mainActivity)
        CTV_reverse_button.setOnClickListener {
            CTV_reverse_button.toggle()
            SPFManager.setReverseFunctionButton(mainActivity, CTV_reverse_button.isChecked)
            val mAccessibilityService = ServiceFloating.getSharedInstance()
            mAccessibilityService?.refreshSoftKey()
        }
        //make bar bg be transparent
        IV_bg_color.setImageDrawable(ColorDrawable(SPFManager.getSoftKeyBarBgGolor(mainActivity)))
        IV_bg_color.setOnClickListener {
            val secColorPickerFragment = ColorPickerDialogFragment.newInstance(SPFManager.getSoftKeyBarBgGolor(mainActivity))
            secColorPickerFragment.show(mainActivity.supportFragmentManager, "ColorPickerFragment")
        }
        //smart hieedn
        CTV_smart_hidden.isChecked = SPFManager.getSmartHidden(mainActivity)
        CTV_smart_hidden.setOnClickListener {
            CTV_smart_hidden.toggle()
            SPFManager.setSmartHidden(mainActivity, CTV_smart_hidden.isChecked)
            val mAccessibilityService = ServiceFloating.getSharedInstance()
            mAccessibilityService?.updateSmartHidden(CTV_smart_hidden.isChecked)
        }
        //hidden when rotate
        CTV_hidden_when_rotate.isChecked = SPFManager.getRotateHidden(mainActivity)
        CTV_hidden_when_rotate.setOnClickListener {
            CTV_hidden_when_rotate.toggle()
            SPFManager.setRotateHidden(mainActivity, CTV_hidden_when_rotate.isChecked)
            val mAccessibilityService = ServiceFloating.getSharedInstance()
            mAccessibilityService?.updateRotateHidden(CTV_hidden_when_rotate.isChecked)
        }
        IV_my_github.setOnClickListener {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(MY_GIT_HUB_URL))
                startActivity(browserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initDisappearSpinner() {

        val disappearAdapter = ArrayAdapter(mainActivity, android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.bar_disappear_time))

        SP_bar_disappear_time.adapter = disappearAdapter
        SP_bar_disappear_time.setSelection(SPFManager.getDisappearPosition(mainActivity))
        SP_bar_disappear_time.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                var mAccessibilityService: ServiceFloating? = ServiceFloating.getSharedInstance()
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateDisappearTime(position)
                    mAccessibilityService = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                //Do nothing
            }
        }
    }


    fun onColorChange(colorCode: Int) {
        IV_bg_color.setImageDrawable(ColorDrawable(colorCode))
        SPFManager.setSoftKeyBgGolor(mainActivity, colorCode)
        val mAccessibilityService = ServiceFloating.getSharedInstance();
        mAccessibilityService?.refreshSoftKey()
    }

}