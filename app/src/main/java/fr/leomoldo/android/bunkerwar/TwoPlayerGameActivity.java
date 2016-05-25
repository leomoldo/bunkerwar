package fr.leomoldo.android.bunkerwar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;
import android.widget.TextView;

import fr.leomoldo.android.bunkerwar.drawer.Bunker;
import fr.leomoldo.android.bunkerwar.drawer.Landscape;
import fr.leomoldo.android.bunkerwar.sdk.GameView;
import fr.leomoldo.android.bunkerwar.sdk.ViewCoordinates;

public class TwoPlayerGameActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private final static String LOG_TAG = TwoPlayerGameActivity.class.getSimpleName();

    // Model :
    private GameSequencer mGameSequencer;
    private Bunker mPlayerOneBunker;
    private Bunker mPlayerTwoBunker;
    private Landscape mLandscape;

    // Views :
    private GameView mGameView;

    private TextView mTextViewIndicatorAnglePlayerOne;
    private TextView mTextViewIndicatorPowerPlayerOne;
    private TextView mTextViewIndicatorAnglePlayerTwo;
    private TextView mTextViewIndicatorPowerPlayerTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        // Retrieve useful views.
        mTextViewIndicatorAnglePlayerOne = (TextView) findViewById(R.id.textViewIndicatorAnglePlayerOne);
        mTextViewIndicatorPowerPlayerOne = (TextView) findViewById(R.id.textViewIndicatorPowerPlayerOne);
        mTextViewIndicatorAnglePlayerTwo = (TextView) findViewById(R.id.textViewIndicatorAnglePlayerTwo);
        mTextViewIndicatorPowerPlayerTwo = (TextView) findViewById(R.id.textViewIndicatorPowerPlayerTwo);
        ((SeekBar) findViewById(R.id.seekBarAnglePlayerOne)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.seekBarPowerPlayerOne)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.seekBarAnglePlayerTwo)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.seekBarPowerPlayerTwo)).setOnSeekBarChangeListener(this);
        mGameView = (GameView) findViewById(R.id.gameView);

        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        initializeGame();
                    }
                });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Integer value = progress;

        switch (seekBar.getId()) {

            case R.id.seekBarAnglePlayerOne:
                mPlayerOneBunker.setAbsoluteCanonAngle(progress);
                mGameView.invalidate();
                mTextViewIndicatorAnglePlayerOne.setText(value.toString());
                break;
            case R.id.seekBarPowerPlayerOne:
                mPlayerOneBunker.setCanonPower(progress);
                mTextViewIndicatorPowerPlayerOne.setText(value.toString());
                break;
            case R.id.seekBarAnglePlayerTwo:
                mPlayerTwoBunker.setAbsoluteCanonAngle(progress);
                mGameView.invalidate();
                mTextViewIndicatorAnglePlayerTwo.setText(value.toString());
                break;
            case R.id.seekBarPowerPlayerTwo:
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

    public void onReloadButtonClicked(View view) {
        initializeGame();
    }

    public void onFireButtonClicked(View view) {
        // Check which player has clicked the button.
        Boolean didPlayerOneFire = false;
        if (view.getId() == R.id.buttonFirePlayerOne) {
            didPlayerOneFire = true;
        }

        // Update GameSequencer.
        mGameSequencer.fireButtonPressed(didPlayerOneFire);

        ViewCoordinates initialBombshellCoordinates;
        Bunker targetBunker;
        BombshellPathComputer bombshellPathComputer;

        // Update UI.
        if (didPlayerOneFire) {
            // This line is commented for debug purposes until GameSequencer is implemented :
            // findViewById(R.id.buttonFirePlayerOne).setVisibility(View.GONE);
            initialBombshellCoordinates = mPlayerOneBunker.getViewCoordinates();
            bombshellPathComputer = new BombshellPathComputer(mPlayerOneBunker.getCanonPower(), mPlayerOneBunker.getGeometricalCanonAngleRadian(), initialBombshellCoordinates);
            targetBunker = mPlayerTwoBunker;
        } else {
            findViewById(R.id.buttonFirePlayerTwo).setVisibility(View.GONE);
            initialBombshellCoordinates = mPlayerTwoBunker.getViewCoordinates();
            bombshellPathComputer = new BombshellPathComputer(mPlayerTwoBunker.getCanonPower(), mPlayerTwoBunker.getGeometricalCanonAngleRadian(), initialBombshellCoordinates);
            targetBunker = mPlayerOneBunker;
        }

        BombshellAnimatorAsyncTask task = new BombshellAnimatorAsyncTask(mGameView, mLandscape, initialBombshellCoordinates, targetBunker);
        task.execute(bombshellPathComputer);
    }

    private void initializeGame() {

        // TODO Only useful for now? (with "reload game" debug button)
        mGameView.unregisterDrawer(mLandscape);
        mGameView.unregisterDrawer(mPlayerOneBunker);
        mGameView.unregisterDrawer(mPlayerTwoBunker);

        // Initialize game model.
        mGameSequencer = new GameSequencer();
        mLandscape = new Landscape(getResources().getColor(R.color.green_land_slice));
        mPlayerOneBunker = new Bunker(true, Color.RED, getBunkerOneCoordinates());
        mPlayerTwoBunker = new Bunker(false, Color.YELLOW, getBunkerTwoCoordinates());

        // Initialize GameView.
        mGameView.registerDrawer(mPlayerOneBunker);
        mGameView.registerDrawer(mPlayerTwoBunker);
        mGameView.registerDrawer(mLandscape);
        mGameView.invalidate();

        findViewById(R.id.buttonFirePlayerOne).setVisibility(View.VISIBLE);

    }

    private ViewCoordinates getBunkerOneCoordinates() {
        float landSliceWidth = mGameView.getWidth() / mLandscape.getNumberOfLandscapeSlices();
        return new ViewCoordinates(Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER * landSliceWidth,
                mGameView.getHeight()
                        - mGameView.getHeight() * Landscape.MAX_HEIGHT_RATIO_FOR_LANDSCAPE * mLandscape.getLandscapeHeightPercentage(Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER)
                        - Bunker.BUNKER_RADIUS);
    }

    private ViewCoordinates getBunkerTwoCoordinates() {
        float landSliceWidth = mGameView.getWidth() / mLandscape.getNumberOfLandscapeSlices();
        return new ViewCoordinates(mGameView.getWidth() - landSliceWidth * Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER,
                mGameView.getHeight()
                        - mGameView.getHeight() * Landscape.MAX_HEIGHT_RATIO_FOR_LANDSCAPE * mLandscape.getLandscapeHeightPercentage(mLandscape.getNumberOfLandscapeSlices() - 1 - Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER)
                        - Bunker.BUNKER_RADIUS);
    }
}