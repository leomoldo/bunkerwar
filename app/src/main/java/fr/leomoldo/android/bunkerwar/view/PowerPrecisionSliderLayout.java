package fr.leomoldo.android.bunkerwar.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by leomoldo on 30/06/2016.
 */
public class PowerPrecisionSliderLayout extends AbstractPrecisionSliderLayout {

    public PowerPrecisionSliderLayout(Context context) {
        super(context);
        init();
    }

    public PowerPrecisionSliderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PowerPrecisionSliderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSeekBar.setMax(100);
    }
}