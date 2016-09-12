package fr.leomoldo.android.bunkerwar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.leomoldo.android.bunkerwar.R;

/**
 * Created by leomoldo on 22/07/2016.
 */
public class WindIndicatorLayout extends LinearLayout {

    private TextView mTextViewWindValue;
    private View mViewPaddingPositiveWindArrows;
    private View mViewPaddingNegativeWindArrows;
    private ImageView mImageViewFlag;

    public WindIndicatorLayout(Context context) {
        super(context);
        init();
    }

    public WindIndicatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WindIndicatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_wind_indicator, this);
        mTextViewWindValue = (TextView) findViewById(R.id.textViewWindValue);
        mViewPaddingPositiveWindArrows = findViewById(R.id.viewPaddingPositiveWindArrows);
        mViewPaddingNegativeWindArrows = findViewById(R.id.viewPaddingNegativeWindArrows);
        mImageViewFlag = (ImageView) findViewById(R.id.imageViewFlag);
    }

    public void displayWindValue(int windValue) {
        mTextViewWindValue.setText(String.valueOf(windValue));
        if (windValue < 0) {
            mViewPaddingNegativeWindArrows.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, getPaddingWeightForWindValue(windValue)));
            mViewPaddingPositiveWindArrows.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, Integer.MAX_VALUE));
            mImageViewFlag.setImageResource(R.drawable.flag_negative_wind);
        } else {
            mViewPaddingPositiveWindArrows.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, getPaddingWeightForWindValue(windValue)));
            mViewPaddingNegativeWindArrows.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, Integer.MAX_VALUE));
            mImageViewFlag.setImageResource(R.drawable.flag_positive_wind);
        }
    }

    // To display graphically wind values, two horizontal linear_layouts are used (respectively for
    // positive and negative values).
    // Each one is 120dp wide, contains a padding view + a horizontal linear_layout of 5 images of
    // an arrow (showing the wind's direction).
    // The arrows linear_layout have a fixed layout_weight of 60 (which is a convenient value for
    // dividing an integer).
    // The following method gives us the layout_weight value we have to set to the padding view to
    // hide or show a number of arrows according to the absolute wind's value (0 to 50 negative
    // or positive).
    private int getPaddingWeightForWindValue(int value) {
        value = Math.abs(value);
        if (value == 0) {
            return Integer.MAX_VALUE;
        } else if (value <= 10) {
            return 240;
        } else if (value <= 20) {
            return 90;
        } else if (value <= 30) {
            return 40;
        } else if (value <= 40) {
            return 15;
        } else {
            return 0;
        }
    }
}
