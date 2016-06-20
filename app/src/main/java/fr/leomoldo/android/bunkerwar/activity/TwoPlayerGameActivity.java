package fr.leomoldo.android.bunkerwar.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.leomoldo.android.bunkerwar.BombshellAnimatorAsyncTask;
import fr.leomoldo.android.bunkerwar.BombshellPathComputer;
import fr.leomoldo.android.bunkerwar.GameSequencer;
import fr.leomoldo.android.bunkerwar.R;
import fr.leomoldo.android.bunkerwar.drawer.Bunker;
import fr.leomoldo.android.bunkerwar.drawer.Landscape;
import fr.leomoldo.android.bunkerwar.sdk.Drawer;
import fr.leomoldo.android.bunkerwar.sdk.GameView;
import fr.leomoldo.android.bunkerwar.sdk.ViewCoordinates;

public class TwoPlayerGameActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, BombshellAnimatorAsyncTask.CollisionListener {

    private final static String LOG_TAG = TwoPlayerGameActivity.class.getSimpleName();

    // Model :
    private GameSequencer mGameSequencer;
    private Bunker mPlayerOneBunker;
    private Bunker mPlayerTwoBunker;
    private Landscape mLandscape;

    private BombshellAnimatorAsyncTask mBombshellAnimatorAsyncTask;

    // Views :
    private GameView mGameView;

    private TextView mTextViewIndicatorAnglePlayerOne;
    private TextView mTextViewIndicatorPowerPlayerOne;
    private TextView mTextViewIndicatorAnglePlayerTwo;
    private TextView mTextViewIndicatorPowerPlayerTwo;

    private Button mFireButtonPlayerOne;
    private Button mFireButtonPlayerTwo;

    private SeekBar mSeekBarAnglePlayerOne;
    private SeekBar mSeekBarPowerPlayerOne;
    private SeekBar mSeekBarAnglePlayerTwo;
    private SeekBar mSeekBarPowerPlayerTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_two_player_game);

        // Retrieve useful views.
        mTextViewIndicatorAnglePlayerOne = (TextView) findViewById(R.id.textViewIndicatorAnglePlayerOne);
        mTextViewIndicatorPowerPlayerOne = (TextView) findViewById(R.id.textViewIndicatorPowerPlayerOne);
        mTextViewIndicatorAnglePlayerTwo = (TextView) findViewById(R.id.textViewIndicatorAnglePlayerTwo);
        mTextViewIndicatorPowerPlayerTwo = (TextView) findViewById(R.id.textViewIndicatorPowerPlayerTwo);
        mFireButtonPlayerOne = (Button) findViewById(R.id.buttonFirePlayerOne);
        mFireButtonPlayerTwo = (Button) findViewById(R.id.buttonFirePlayerTwo);
        mSeekBarAnglePlayerOne = (SeekBar) findViewById(R.id.seekBarAnglePlayerOne);
        mSeekBarPowerPlayerOne = (SeekBar) findViewById(R.id.seekBarPowerPlayerOne);
        mSeekBarAnglePlayerTwo = (SeekBar) findViewById(R.id.seekBarAnglePlayerTwo);
        mSeekBarPowerPlayerTwo = (SeekBar) findViewById(R.id.seekBarPowerPlayerTwo);
        mGameView = (GameView) findViewById(R.id.gameView);

        mSeekBarAnglePlayerOne.setOnSeekBarChangeListener(this);
        mSeekBarPowerPlayerOne.setOnSeekBarChangeListener(this);
        mSeekBarAnglePlayerTwo.setOnSeekBarChangeListener(this);
        mSeekBarPowerPlayerTwo.setOnSeekBarChangeListener(this);

        // TODO Change UI visibility handling.
        mFireButtonPlayerOne.setVisibility(View.VISIBLE);
        mFireButtonPlayerTwo.setVisibility(View.VISIBLE);

        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        // TODO Clean :
                        /*
                        mGameView.unregisterDrawer(mLandscape);
                        mGameView.unregisterDrawer(mPlayerOneBunker);
                        mGameView.unregisterDrawer(mPlayerTwoBunker);
                        */

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

                        if (Build.VERSION.SDK_INT < 16) {
                            rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mBombshellAnimatorAsyncTask != null) {
            mBombshellAnimatorAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // TODO Implement AlertDialog to confirm call to super.onBackPressed.
        super.onBackPressed();
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

    public void onButtonClickedFire(View view) {

        if (mBombshellAnimatorAsyncTask != null) {
            Log.d(LOG_TAG, "There is a Bombshell flying already");
            return;
        }

        // Check which player has clicked the button.
        Boolean didPlayerOneFire = false;
        if (view.getId() == R.id.buttonFirePlayerOne) {
            didPlayerOneFire = true;
        }

        // Update GameSequencer.
        mGameSequencer.fireButtonPressed(didPlayerOneFire);

        BombshellPathComputer bombshellPathComputer;
        ArrayList<Drawer> collidableDrawers = new ArrayList<Drawer>();
        collidableDrawers.add(mLandscape);

        // Update UI.
        if (didPlayerOneFire) {
            mFireButtonPlayerOne.setVisibility(View.GONE);
            mFireButtonPlayerTwo.setVisibility(View.GONE);
            bombshellPathComputer = new BombshellPathComputer(mPlayerOneBunker.getCanonPower(), mPlayerOneBunker.getGeometricalCanonAngleRadian(), mPlayerOneBunker.getViewCoordinates());
            collidableDrawers.add(mPlayerTwoBunker);
        } else {
            mFireButtonPlayerTwo.setVisibility(View.GONE);
            mFireButtonPlayerOne.setVisibility(View.GONE);
            bombshellPathComputer = new BombshellPathComputer(mPlayerTwoBunker.getCanonPower(), mPlayerTwoBunker.getGeometricalCanonAngleRadian(), mPlayerTwoBunker.getViewCoordinates());
            collidableDrawers.add(mPlayerOneBunker);
        }

        mBombshellAnimatorAsyncTask = new BombshellAnimatorAsyncTask(mGameView, collidableDrawers, this);
        mBombshellAnimatorAsyncTask.execute(bombshellPathComputer);
    }

    @Override
    public void onDrawerHit(Drawer drawer) {

        mBombshellAnimatorAsyncTask = null;

        // TODO Update GameSequencer.
        // TODO Replace by AlertDialog displaying rounds count.

        if (drawer == null || drawer.equals(mLandscape)) {
            Toast.makeText(this, R.string.target_missed, Toast.LENGTH_SHORT).show();
            mFireButtonPlayerOne.setVisibility(View.VISIBLE);
            mFireButtonPlayerTwo.setVisibility(View.VISIBLE);
        } else if (drawer.equals(mPlayerTwoBunker)) {
            Toast.makeText(this, R.string.player_one_won, Toast.LENGTH_LONG).show();
            mGameView.unregisterDrawer(mPlayerTwoBunker);
        } else if (drawer.equals(mPlayerOneBunker)) {
            Toast.makeText(this, R.string.player_two_won, Toast.LENGTH_LONG).show();
            mGameView.unregisterDrawer(mPlayerOneBunker);
        }
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