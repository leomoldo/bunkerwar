package fr.leomoldo.android.bunkerwar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

    public void displayWindValue(int windValue) {
        mTextViewWindValue.setText(String.valueOf(windValue));
        if (windValue < 0) {
            mLinearLayoutPositiveWindArrows.setLayoutParams(new FrameLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));
            mLinearLayoutNegativeWindArrows.setLayoutParams(new FrameLayout.LayoutParams(getIndicatorViewLengthForValue(windValue), ViewGroup.LayoutParams.MATCH_PARENT));
            mImageViewFlag.setImageResource(R.drawable.arrow_negative_wind);
        } else {
            mLinearLayoutNegativeWindArrows.setLayoutParams(new FrameLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));
            mLinearLayoutPositiveWindArrows.setLayoutParams(new FrameLayout.LayoutParams(getIndicatorViewLengthForValue(windValue), ViewGroup.LayoutParams.MATCH_PARENT));
            mImageViewFlag.setImageResource(R.drawable.arrow_positive_wind);
        }
    }

    private void init() {
        inflate(getContext(), R.layout.layout_wind_indicator, this);
        mTextViewWindValue = (TextView) findViewById(R.id.textViewWindValue);
        mLinearLayoutPositiveWindArrows = (LinearLayout) findViewById(R.id.linearLayoutPositiveWindArrows);
        mLinearLayoutNegativeWindArrows = (LinearLayout) findViewById(R.id.linearLayoutNegativeWindArrows);
        mImageViewFlag = (ImageView) findViewById(R.id.imageViewFlag);
    }

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
}
