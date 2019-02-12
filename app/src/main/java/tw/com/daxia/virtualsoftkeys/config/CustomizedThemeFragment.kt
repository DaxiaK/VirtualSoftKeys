package tw.com.daxia.virtualsoftkeys.config

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_customizedtheme.*
import tw.com.daxia.virtualsoftkeys.MainActivity
import tw.com.daxia.virtualsoftkeys.R
import tw.com.daxia.virtualsoftkeys.common.Link
import tw.com.daxia.virtualsoftkeys.common.SPFManager


class CustomizedThemeFragment : Fragment() {

    private val TAG = "CustomizedThemeFragment"

    companion object {
        fun newInstance(): CustomizedThemeFragment {
            return CustomizedThemeFragment()
        }
    }

    private lateinit var mainActivity: MainActivity


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_customizedtheme, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        initSharedConfig()
    }

    override fun onAttach(context: Context?) {
        Log.d(TAG, "onAttach")
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }


    private fun initSharedConfig() {
        //revert home long click
        val action = SPFManager.getHomeLongClickStartAction(activity)
        when (action) {
            Link.GOOGLE_APP_PACKAGE_NAME ->
                RB_longclick_action_google.isChecked = true
            Intent.ACTION_VOICE_COMMAND ->
                RB_longclick_action_google_assistant.isChecked = true
        }
        //set home long click  listener
        RG_longclick_action.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.RB_longclick_action_google -> {
                    SPFManager.setHomeLongClickStartAction(activity, Link.GOOGLE_APP_PACKAGE_NAME)
                }
                R.id.RB_longclick_action_google_assistant -> {
                    SPFManager.setHomeLongClickStartAction(activity, Intent.ACTION_VOICE_COMMAND)
                }
            }
        }
    }


}