package fr.leomoldo.android.bunkerwar.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
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
import fr.leomoldo.android.bunkerwar.view.AbstractPrecisionSliderLayout;
import fr.leomoldo.android.bunkerwar.view.AnglePrecisionSliderLayout;
import fr.leomoldo.android.bunkerwar.view.PowerPrecisionSliderLayout;

public class TwoPlayerGameActivity extends AppCompatActivity implements BombshellAnimatorAsyncTask.CollisionListener, AbstractPrecisionSliderLayout.PrecisionSliderLayoutListener {

    private final static String LOG_TAG = TwoPlayerGameActivity.class.getSimpleName();

    private final static String BUNDLE_KEY_GAME_SEQUENCER = TwoPlayerGameActivity.class.getName() + ".gameSequencer";
    private final static String BUNDLE_KEY_BUNKER_ONE = TwoPlayerGameActivity.class.getName() + ".bunkerOne";
    private final static String BUNDLE_KEY_BUNKER_TWO = TwoPlayerGameActivity.class.getName() + ".bunkerTwo";
    private final static String BUNDLE_KEY_LANDSCAPE = TwoPlayerGameActivity.class.getName() + ".landscape";

    // Model :
    private GameSequencer mGameSequencer;
    private Bunker mPlayerOneBunker;
    private Bunker mPlayerTwoBunker;
    private Landscape mLandscape;

    private BombshellAnimatorAsyncTask mBombshellAnimatorAsyncTask;

    // Views :
    private GameView mGameView;
    private LinearLayout mLinearLayoutControls;
    private TextView mTextViewPlayersName;
    private Button mButtonFire;
    private AnglePrecisionSliderLayout mAnglePrecisionSliderLayout;
    private PowerPrecisionSliderLayout mPowerPrecisionSliderLayout;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_two_player_game);

        // Retrieve useful views.
        mLinearLayoutControls = (LinearLayout) findViewById(R.id.linearLayoutControls);
        mTextViewPlayersName = (TextView) findViewById(R.id.textView_playersName);
        mButtonFire = (Button) findViewById(R.id.button_fire);
        mAnglePrecisionSliderLayout = (AnglePrecisionSliderLayout) findViewById(R.id.anglePrecisionSliderLayout);
        mPowerPrecisionSliderLayout = (PowerPrecisionSliderLayout) findViewById(R.id.powerPrecisionSliderLayout);
        mGameView = (GameView) findViewById(R.id.gameView);

        mAnglePrecisionSliderLayout.setListener(this);
        mPowerPrecisionSliderLayout.setListener(this);

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

                        // Initialize or restore game model.
                        if (savedInstanceState != null) {
                            mGameSequencer = savedInstanceState.getParcelable(BUNDLE_KEY_GAME_SEQUENCER);
                            mLandscape = savedInstanceState.getParcelable(BUNDLE_KEY_LANDSCAPE);
                            mPlayerOneBunker = savedInstanceState.getParcelable(BUNDLE_KEY_BUNKER_ONE);
                            mPlayerTwoBunker = savedInstanceState.getParcelable(BUNDLE_KEY_BUNKER_TWO);

                            // TODO : Update UI Controls (Seekbars and text etc...).

                        } else {
                            mGameSequencer = new GameSequencer();
                            mLandscape = new Landscape(getResources().getColor(R.color.green_land_slice));
                            mPlayerOneBunker = new Bunker(true, Color.RED, getBunkerOneCoordinates());
                            mPlayerTwoBunker = new Bunker(false, Color.YELLOW, getBunkerTwoCoordinates());
                        }

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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_KEY_GAME_SEQUENCER, mGameSequencer);
        outState.putParcelable(BUNDLE_KEY_LANDSCAPE, mLandscape);
        outState.putParcelable(BUNDLE_KEY_BUNKER_ONE, mPlayerOneBunker);
        outState.putParcelable(BUNDLE_KEY_BUNKER_TWO, mPlayerTwoBunker);
        super.onSaveInstanceState(outState);
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
    public void onValueChanged(int newValue) {
        // TODO Implement.

        /*
        Integer value = progress;

        switch (seekBar.getId()) {

            case R.id.seekBarAnglePlayerOne:
                if (mPlayerOneBunker != null) {
                    mPlayerOneBunker.setAbsoluteCanonAngle(progress);
                }
                mGameView.invalidate();
                mTextViewIndicatorAnglePlayerOne.setText(value.toString());
                break;
            case R.id.seekBarPowerPlayerOne:
                if (mPlayerOneBunker != null) {
                    mPlayerOneBunker.setCanonPower(progress);
                }
                mTextViewIndicatorPowerPlayerOne.setText(value.toString());
                break;
            case R.id.seekBarAnglePlayerTwo:
                if (mPlayerTwoBunker != null) {
                    mPlayerTwoBunker.setAbsoluteCanonAngle(progress);
                }
                mGameView.invalidate();
                mTextViewIndicatorAnglePlayerTwo.setText(value.toString());
                break;
            case R.id.seekBarPowerPlayerTwo:
                if (mPlayerTwoBunker != null) {
                    mPlayerTwoBunker.setCanonPower(progress);
                }
                mTextViewIndicatorPowerPlayerTwo.setText(value.toString());
                break;
        }
         */
    }

    public void onButtonClickedFire(View view) {

        if (mBombshellAnimatorAsyncTask != null) {
            Log.d(LOG_TAG, "There is a Bombshell flying already");
            return;
        }

        mLinearLayoutControls.setVisibility(View.GONE);

        // Update GameSequencer.
        mGameSequencer.fireButtonPressed();

        BombshellPathComputer bombshellPathComputer;
        ArrayList<Drawer> collidableDrawers = new ArrayList<Drawer>();
        collidableDrawers.add(mLandscape);

        // Update UI.
        if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_ONE_FIRING) {
            bombshellPathComputer = new BombshellPathComputer(mPlayerOneBunker.getCanonPower(), mPlayerOneBunker.getGeometricalCanonAngleRadian(), mPlayerOneBunker.getViewCoordinates());
            collidableDrawers.add(mPlayerTwoBunker);
        } else if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_TWO_FIRING) {
            bombshellPathComputer = new BombshellPathComputer(mPlayerTwoBunker.getCanonPower(), mPlayerTwoBunker.getGeometricalCanonAngleRadian(), mPlayerTwoBunker.getViewCoordinates());
            collidableDrawers.add(mPlayerOneBunker);
        } else {
            // Issue...
            return;
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
            mLinearLayoutControls.setVisibility(View.VISIBLE);
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