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

    private final static int SIZE_OF_ARROW_IMAGE_VIEW = 24;

    private TextView mTextViewWindValue;
    private LinearLayout mLinearLayoutPositiveWindArrows;
    private LinearLayout mLinearLayoutNegativeWindArrows;
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
        mLinearLayoutPositiveWindArrows = (LinearLayout) findViewById(R.id.linearLayoutPositiveWindArrows);
        mLinearLayoutNegativeWindArrows = (LinearLayout) findViewById(R.id.linearLayoutNegativeWindArrows);
        mViewPaddingPositiveWindArrows = findViewById(R.id.viewPaddingPositiveWindArrows);
        mViewPaddingNegativeWindArrows = findViewById(R.id.viewPaddingNegativeWindArrows);
        mImageViewFlag = (ImageView) findViewById(R.id.imageViewFlag);
    }

    public void displayWindValue(int windValue) {
        mTextViewWindValue.setText(String.valueOf(windValue));
        if (windValue < 0) {
            mViewPaddingNegativeWindArrows.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, getPaddingWeightForWindValue(windValue)));
            mViewPaddingPositiveWindArrows.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, Integer.MAX_VALUE));
            // TODO Clean.
            /*
            mLinearLayoutPositiveWindArrows.setLayoutParams(new FrameLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT));
            mLinearLayoutNegativeWindArrows.setLayoutParams(new FrameLayout.LayoutParams(getIndicatorViewLengthForValue(windValue), FrameLayout.LayoutParams.WRAP_CONTENT));
            */
            mImageViewFlag.setImageResource(R.drawable.flag_negative_wind);
        } else {
            mViewPaddingPositiveWindArrows.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, getPaddingWeightForWindValue(windValue)));
            mViewPaddingNegativeWindArrows.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, Integer.MAX_VALUE));
            // TODO Clean.
            /*
            mLinearLayoutNegativeWindArrows.setLayoutParams(new FrameLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT));
            mLinearLayoutPositiveWindArrows.setLayoutParams(new FrameLayout.LayoutParams(getIndicatorViewLengthForValue(windValue), FrameLayout.LayoutParams.WRAP_CONTENT));
            */
            mImageViewFlag.setImageResource(R.drawable.flag_positive_wind);
        }
    }

    // TODO Clean.
    private int getIndicatorViewLengthForValue(int value) {
        if (value == 0) {
            return 0;
        }
        int numberOfArrowsToBeShown = Math.abs(value) / 10;
        if (numberOfArrowsToBeShown < 5) {
            numberOfArrowsToBeShown++;
        }
        return numberOfArrowsToBeShown * SIZE_OF_ARROW_IMAGE_VIEW;
    }

    // TODO Comment.
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
