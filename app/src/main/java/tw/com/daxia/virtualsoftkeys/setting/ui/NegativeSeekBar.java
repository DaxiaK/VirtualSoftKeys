package tw.com.daxia.virtualsoftkeys.setting.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by daxia on 2016/10/4.
 * Ref : http://stackoverflow.com/questions/15471508/seekbar-with-negative-values-on-android
 */
public class NegativeSeekBar extends SeekBar {

    protected int minValue = 0;
    protected int maxValue = 0;
    protected OnSeekBarChangeListener listener;

    public NegativeSeekBar(Context context){
        super(context);
        setUpInternalListener();
    }

    public NegativeSeekBar(Context context, AttributeSet attrs){
        super(context, attrs);
        setUpInternalListener();
    }

    public NegativeSeekBar(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        setUpInternalListener();
    }

    public void setMinValue(int min){
        this.minValue = min;
        super.setMax(maxValue - minValue);
    }


    public void setMaxValue(int max){
        this.maxValue = max;
        super.setMax(maxValue - minValue);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener){
        this.listener = listener;
    }

    private void setUpInternalListener(){
        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(listener != null){
                    listener.onProgressChanged(seekBar, minValue + i, b);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(listener != null)
                    listener.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(listener != null)
                    listener.onStopTrackingTouch(seekBar);
            }
        });
    }
}
