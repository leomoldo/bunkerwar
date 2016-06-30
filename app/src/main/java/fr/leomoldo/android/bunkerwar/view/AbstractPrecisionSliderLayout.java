package fr.leomoldo.android.bunkerwar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import fr.leomoldo.android.bunkerwar.R;

/**
 * Created by leomoldo on 29/06/2016.
 */
public abstract class AbstractPrecisionSliderLayout extends LinearLayout implements SeekBar.OnSeekBarChangeListener, Button.OnClickListener {

    public interface PrecisionSliderLayoutListener {
        void onValueChanged(int newValue);
    }

    protected SeekBar mSeekBar;
    protected TextView mTextViewValue;
    protected TextView mTextViewTitle;
    protected Button mButtonMinus;
    protected Button mButtonPlus;

    private PrecisionSliderLayoutListener mListener;

    public AbstractPrecisionSliderLayout(Context context) {
        super(context);
        init();
    }

    public AbstractPrecisionSliderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AbstractPrecisionSliderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_precision_slider, this);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mTextViewValue = (TextView) findViewById(R.id.textViewValue);
        mTextViewTitle = (TextView) findViewById(R.id.textViewTitle);
        mButtonMinus = (Button) findViewById(R.id.button_minus);
        mButtonPlus = (Button) findViewById(R.id.button_plus);
        mSeekBar.setOnSeekBarChangeListener(this);
        mButtonMinus.setOnClickListener(this);
        mButtonPlus.setOnClickListener(this);
    }

    public void setListener(PrecisionSliderLayoutListener listener) {
        mListener = listener;
    }

    public void setValue(int value) {
        mSeekBar.setProgress(value);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Integer newValue = progress;
        mTextViewValue.setText(newValue.toString());
        if (fromUser) {
            mListener.onValueChanged(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        int currentValue = mSeekBar.getProgress();
        switch (v.getId()) {
            case R.id.button_minus:
                currentValue--;
                break;
            case R.id.button_plus:
                currentValue++;
                break;
            default:
        }
        mSeekBar.setProgress(currentValue);
        mListener.onValueChanged(currentValue);
    }
}