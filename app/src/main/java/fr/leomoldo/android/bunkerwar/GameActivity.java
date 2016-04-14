package fr.leomoldo.android.bunkerwar;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import fr.leomoldo.android.bunkerwar.game.Bunker;
import fr.leomoldo.android.bunkerwar.game.GameSequencer;
import fr.leomoldo.android.bunkerwar.game.Landscape;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private final static float HEIGHT_MARGIN_RATIO_FOR_LANDSCAPE = 0.5f;

    // Model :
    private GameSequencer mGameSequencer;
    private Bunker mPlayerOneBunker;
    private Bunker mPlayerTwoBunker;
    private Landscape mLandscape;

    // Landscape generation :
    Integer mMainRelativeLayoutHeight;
    private ArrayList<FrameLayout> mLandscapeFrameLayouts; // TODO inutile?
    private ArrayList<Float> mLandscapeFrameLayoutHeights;

    // Views :
    private LinearLayout mLandscapeLinearLayout;
    private BunkerView mPlayerOneBunkerView;
    private BunkerView mPlayerTwoBunkerView;
    private TextView mTextViewIndicatorAnglePlayerOne;
    private TextView mTextViewIndicatorPowerPlayerOne;
    private TextView mTextViewIndicatorAnglePlayerTwo;
    private TextView mTextViewIndicatorPowerPlayerTwo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        // Initialize game model.
        mGameSequencer = new GameSequencer();
        mLandscape = new Landscape();
        mPlayerOneBunker = new Bunker(true);
        mPlayerTwoBunker = new Bunker(false);

        // Retrieve useful views.
        mTextViewIndicatorAnglePlayerOne = (TextView) findViewById(R.id.textViewIndicatorAnglePlayerOne);
        mTextViewIndicatorPowerPlayerOne = (TextView) findViewById(R.id.textViewIndicatorPowerPlayerOne);
        mTextViewIndicatorAnglePlayerTwo = (TextView) findViewById(R.id.textViewIndicatorAnglePlayerTwo);
        mTextViewIndicatorPowerPlayerTwo = (TextView) findViewById(R.id.textViewIndicatorPowerPlayerTwo);
        mLandscapeLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutLandscape);
        ((SeekBar) findViewById(R.id.seekBarAnglePlayerOne)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.seekBarPowerPlayerOne)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.seekBarAnglePlayerTwo)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.seekBarPowerPlayerTwo)).setOnSeekBarChangeListener(this);


        // Landscape drawing must be done on pre-draw to retrieve the view size information.
        mLandscapeLinearLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {

                // Remove the listener, so that the following code runs only once.
                mLandscapeLinearLayout.getViewTreeObserver().removeOnPreDrawListener(this);

                addLandscapeViews();

                addBunkerViews();

                return false;
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Integer value = progress;

        switch(seekBar.getId()) {

            case R.id.seekBarAnglePlayerOne :
                mPlayerOneBunker.setCanonAngle(progress);
                mPlayerOneBunkerView.invalidate();
                mTextViewIndicatorAnglePlayerOne.setText(value.toString());
                break;
            case R.id.seekBarPowerPlayerOne :
                mPlayerOneBunker.setCanonPower(progress);
                mTextViewIndicatorPowerPlayerOne.setText(value.toString());
                break;
            case R.id.seekBarAnglePlayerTwo :
                mPlayerTwoBunker.setCanonAngle(progress);
                mPlayerTwoBunkerView.invalidate();
                mTextViewIndicatorAnglePlayerTwo.setText(value.toString());
                break;
            case R.id.seekBarPowerPlayerTwo :
                mPlayerTwoBunker.setCanonPower(progress);
                mTextViewIndicatorPowerPlayerTwo.setText(value.toString());
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void onFireButtonClicked(View view) {
        // Check which player has clicked the button.
        Boolean didPlayerOneFire = false;
        if (view.getId() == R.id.buttonFirePlayerOne) {
            didPlayerOneFire = true;
        }

        // Update GameSequencer.
        mGameSequencer.fireButtonPressed(didPlayerOneFire);

        // Update UI.
        if (didPlayerOneFire) {
            findViewById(R.id.buttonFirePlayerOne).setVisibility(View.GONE);
        } else {
            findViewById(R.id.buttonFirePlayerTwo).setVisibility(View.GONE);
        }

        // TODO Lancer le tir (calcul + affichage).
        BombShellView bombShellView;
        if (didPlayerOneFire) {
            bombShellView = new BombShellView(this, mPlayerOneBunker);
        } else {
            bombShellView = new BombShellView(this, mPlayerTwoBunker);
        }
        int viewDimension = BombShellView.BOMBSHELL_RADIUS.intValue() * 2;
        RelativeLayout mainRelativeLayout = ( (RelativeLayout) findViewById(R.id.mainRelativeLayout));
        mainRelativeLayout.addView(bombShellView, viewDimension, viewDimension);

        if (didPlayerOneFire) {
            bombShellView.setX(mPlayerOneBunkerView.getX());
            bombShellView.setY(mPlayerOneBunkerView.getY());
        } else {
            bombShellView.setX(mPlayerTwoBunkerView.getX());
            bombShellView.setY(mPlayerTwoBunkerView.getY());
        }

    }

    private void addLandscapeViews() {

        mMainRelativeLayoutHeight = mLandscapeLinearLayout.getHeight();

        Float landSliceHeight = 0f;
        Float percentage = 0f;
        Integer viewHeight = 0;

        mLandscapeFrameLayouts = new ArrayList<FrameLayout>(mLandscape.getLandscapeWidth());
        mLandscapeFrameLayoutHeights = new ArrayList<Float>(mLandscape.getLandscapeWidth());

        for (int i = 0; i < mLandscape.getLandscapeWidth(); i++) {

            landSliceHeight = (float) mLandscape.getLandscapeHeight(i);
            percentage = landSliceHeight / 100f;
            viewHeight = (int) ( mMainRelativeLayoutHeight * HEIGHT_MARGIN_RATIO_FOR_LANDSCAPE * percentage );

            FrameLayout frame = new FrameLayout(GameActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, viewHeight);
            params.weight = 1;
            params.gravity = Gravity.BOTTOM;
            frame.setLayoutParams(params);
            frame.setBackgroundColor(getResources().getColor(R.color.green_land_slice));

            mLandscapeLinearLayout.addView(frame);

            mLandscapeFrameLayouts.add(i, frame);
            mLandscapeFrameLayoutHeights.add(i, (float) viewHeight);
        }
    }

    /**
     * Must be called imperatively after addLandscapeViews.
     */
    private void addBunkerViews() {

        if( mLandscapeFrameLayoutHeights == null ) {
            return;
        }

        float landSliceWidth = ((float) mLandscapeLinearLayout.getWidth()) / ((float) mLandscape.getLandscapeWidth());

        float bunkerOneX = Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER * landSliceWidth ;
        float bunkerOneY = mMainRelativeLayoutHeight - mLandscapeFrameLayoutHeights.get(Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER) - BunkerView.BUNKER_RADIUS;

        float bunkerTwoX = (mLandscape.getLandscapeWidth() - 1  - Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER) * landSliceWidth;
        float bunkerTwoY = mMainRelativeLayoutHeight - mLandscapeFrameLayoutHeights.get(mLandscape.getLandscapeWidth() - 1  - Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER) - BunkerView.BUNKER_RADIUS;

        mPlayerOneBunkerView = new BunkerView(this, mPlayerOneBunker);
        mPlayerOneBunkerView.setX(bunkerOneX);
        mPlayerOneBunkerView.setY(bunkerOneY);

        mPlayerTwoBunkerView = new BunkerView(this, mPlayerTwoBunker);
        mPlayerTwoBunkerView.setX(bunkerTwoX);
        mPlayerTwoBunkerView.setY(bunkerTwoY);

        RelativeLayout mainRelativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
        mainRelativeLayout.addView(mPlayerOneBunkerView, 0, new RelativeLayout.LayoutParams((int) landSliceWidth, (int) landSliceWidth));
        mainRelativeLayout.addView(mPlayerTwoBunkerView, 0, new RelativeLayout.LayoutParams((int) landSliceWidth, (int) landSliceWidth));
    }


}
