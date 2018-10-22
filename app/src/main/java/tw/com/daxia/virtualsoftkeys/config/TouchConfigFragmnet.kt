package tw.com.daxia.virtualsoftkeys.config

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_touchconfig.*
import tw.com.daxia.virtualsoftkeys.MainActivity
import tw.com.daxia.virtualsoftkeys.R
import tw.com.daxia.virtualsoftkeys.common.SPFManager
import tw.com.daxia.virtualsoftkeys.common.ScreenHepler
import tw.com.daxia.virtualsoftkeys.common.ThemeHelper
import tw.com.daxia.virtualsoftkeys.service.ServiceFloating

class TouchConfigFragmnet : Fragment() {

    companion object {
        fun newInstance(): TouchConfigFragmnet {
            return TouchConfigFragmnet()
        }
    }

    /*
     * Config
     */
    private var screenWidth: Int = 0
    private lateinit var mainActivity: MainActivity

    /**
     * height listener
     */
    private val touchviewHeightSeekBarListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            if (mainActivity.isPortrait) {
                SPFManager.setTouchviewPortraitHeight(mainActivity, seekBar.progress)
            } else {
                SPFManager.setTouchviewLandscapeHeight(mainActivity, seekBar.progress)
            }
            var mAccessibilityService: ServiceFloating? = ServiceFloating.getSharedInstance()
            if (mAccessibilityService != null) {
                mAccessibilityService.updateTouchView(seekBar.progress, null, null)
                mAccessibilityService = null
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val params = mainActivity.ViewTouchviewer.layoutParams as RelativeLayout.LayoutParams
            params.height = progress
            mainActivity.ViewTouchviewer.layoutParams = params
        }
    }

    /**
     * width listener
     */
    private val touchviewWidthSeekBarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onStopTrackingTouch(seekBar: SeekBar) {
            //make touch view position rollback to default
            updateTouchViewPosition(seekBar.progress, -1)
            if (mainActivity.isPortrait) {
                SPFManager.setTouchviewPortraitPosition(mainActivity, Seek_touch_area_position.progress)
            } else {
                SPFManager.setTouchviewLandscapePosition(mainActivity, Seek_touch_area_position.progress)
            }
            var mAccessibilityService: ServiceFloating? = ServiceFloating.getSharedInstance()
            if (seekBar.progress == seekBar.max) {
                if (mainActivity.isPortrait) {
                    SPFManager.setTouchviewPortraitWidth(mainActivity, ViewGroup.LayoutParams.MATCH_PARENT)
                } else {
                    SPFManager.setTouchviewLandscapeWidth(mainActivity, ViewGroup.LayoutParams.MATCH_PARENT)

                }
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateTouchView(null, ViewGroup.LayoutParams.MATCH_PARENT, Seek_touch_area_position.progress)
                    mAccessibilityService = null
                }
            } else {
                if (mainActivity.isPortrait) {
                    SPFManager.setTouchviewPortraitWidth(mainActivity, seekBar.progress)
                } else {
                    SPFManager.setTouchviewLandscapeWidth(mainActivity, seekBar.progress)
                }
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateTouchView(null, seekBar.progress, Seek_touch_area_position.progress)
                    mAccessibilityService = null
                }
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val params = mainActivity.ViewTouchviewer.layoutParams as RelativeLayout.LayoutParams
            params.width = progress
            mainActivity.ViewTouchviewer.layoutParams = params
        }
    }

    /**
     * Position listener
     */
    private val touchviewPositionSeekBarListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            if (mainActivity.isPortrait) {
                SPFManager.setTouchviewPortraitPosition(mainActivity, seekBar.progress)
            } else {
                SPFManager.setTouchviewLandscapePosition(mainActivity, seekBar.progress)
            }
            val mAccessibilityService = ServiceFloating.getSharedInstance()
            mAccessibilityService?.updateTouchView(null, null, seekBar.progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val params = mainActivity.ViewTouchviewer.layoutParams as RelativeLayout.LayoutParams
            params.marginStart = progress
            mainActivity.ViewTouchviewer.layoutParams = params
        }
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_touchconfig, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initSeekBar()

        Seek_touch_area_height.setOnSeekBarChangeListener(touchviewHeightSeekBarListener);
        Seek_touch_area_height.isSaveEnabled = false;

        Seek_touch_area_width.setOnSeekBarChangeListener(touchviewWidthSeekBarListener);
        Seek_touch_area_width.isSaveEnabled = false;

        Seek_touch_area_position.setOnSeekBarChangeListener(touchviewPositionSeekBarListener);
        Seek_touch_area_position.isSaveEnabled = false;
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    /*
     * To update touch view position or revert value form spf
     */
    private fun updateTouchViewPosition(toughviewWidth: Int, positionFromSPF: Int) {
        Seek_touch_area_position.max = screenWidth - toughviewWidth
        Seek_touch_area_position.max = screenWidth - toughviewWidth
        if (positionFromSPF >= 0) {
            Seek_touch_area_position.progress = positionFromSPF
        } else {
            //Default is in center horizontal
            Seek_touch_area_position.progress = screenWidth / 2 - toughviewWidth / 2
        }
    }

    private fun initSeekBar() {
        initSeekBarStyle()
        initSeekBarContent()
    }

    private fun initSeekBarStyle() {
        val configColor: Int
        if (mainActivity.isPortrait) {
            TV_config_name.text = getString(R.string.config_name_portrait)
            configColor = ThemeHelper.getColorResource(mainActivity, R.color.config_portrait_color)
        } else {
            TV_config_name.text = getString(R.string.config_name_landscape)

            configColor = ThemeHelper.getColorResource(mainActivity, R.color.config_landscape_color)
        }
        TV_config_name.setTextColor(configColor)
        Seek_touch_area_height.progressDrawable.colorFilter = PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN)
        Seek_touch_area_height.thumb.colorFilter = PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN)
        Seek_touch_area_width.progressDrawable.colorFilter = PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN)
        Seek_touch_area_width.thumb.colorFilter = PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN)
        Seek_touch_area_position.progressDrawable.colorFilter = PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN)
        Seek_touch_area_position.thumb.colorFilter = PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN)
    }

    private fun initSeekBarContent() {
        //Default
        val screenHeight = ScreenHepler.getScreenHeight(mainActivity)
        this.screenWidth = ScreenHepler.getScreenWidth(mainActivity)
        val touchviewWidth: Int
        //Default Height init
        Seek_touch_area_height.max = screenHeight / MainActivity.MAX_HEIGHT_PERCENTAGE

        //Default width init
        Seek_touch_area_width.max = screenWidth

        if (mainActivity.isPortrait) {
            touchviewWidth = SPFManager.getTouchviewPortraitWidth(mainActivity)
            //Height
            Seek_touch_area_height.progress = SPFManager.getTouchviewPortraitHeight(mainActivity)
            //position  + Width
            //For match content
            if (touchviewWidth == ScreenHepler.getDefautlTouchviewWidth()) {
                Seek_touch_area_width.progress = screenWidth
                //set position
                updateTouchViewPosition(screenWidth, SPFManager.getTouchviewPortraitPosition(mainActivity))
            } else {
                Seek_touch_area_width.progress = touchviewWidth
                //set position
                updateTouchViewPosition(touchviewWidth, SPFManager.getTouchviewPortraitPosition(mainActivity))
            }
        } else {
            touchviewWidth = SPFManager.getTouchviewLandscapeWidth(mainActivity)
            //Height
            Seek_touch_area_height.progress = SPFManager.getTouchviewLandscapeHeight(mainActivity)
            //position  + Width
            //For match content
            if (touchviewWidth == ScreenHepler.getDefautlTouchviewWidth()) {
                Seek_touch_area_width.progress = screenWidth
                //set position
                updateTouchViewPosition(screenWidth, SPFManager.getTouchviewLandscapePosition(mainActivity))
            } else {
                Seek_touch_area_width.progress = touchviewWidth
                //set position
                updateTouchViewPosition(touchviewWidth, SPFManager.getTouchviewLandscapePosition(mainActivity))
            }
        }


    }
}