package fr.leomoldo.android.bunkerwar.view;

import android.content.Context;
import android.util.AttributeSet;

import fr.leomoldo.android.bunkerwar.R;

/**
 * Created by leomoldo on 30/06/2016.
 */
public class AnglePrecisionSliderLayout extends AbstractPrecisionSliderLayout {

    public AnglePrecisionSliderLayout(Context context) {
        super(context);
        init();
    }

    public AnglePrecisionSliderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnglePrecisionSliderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTextViewTitle.setText(getContext().getResources().getString(R.string.UI_text_angle));
        mTextViewUnit.setText(getContext().getResources().getString(R.string.UI_text_angle_unit));
        mSeekBar.setMax(90);
    }
}
