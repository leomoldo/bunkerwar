package fr.leomoldo.android.bunkerwar.view;

import android.content.Context;
import android.util.AttributeSet;

import fr.leomoldo.android.bunkerwar.R;

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
        mTextViewTitle.setText(getContext().getResources().getString(R.string.UI_text_power));
        mTextViewUnit.setText(getContext().getResources().getString(R.string.UI_text_power_unit));
        mSeekBar.setMax(100);
    }
}