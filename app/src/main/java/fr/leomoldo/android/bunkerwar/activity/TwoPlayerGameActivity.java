package fr.leomoldo.android.bunkerwar.activity;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

    // TODO : Make this a dimen in a xml and adapt value to screen size.
    private final static float LAYOUT_TRANSITION_Y_TRANSLATION_OFFSET = 500f;

    // Model :
    private GameSequencer mGameSequencer;
    private Bunker mPlayerOneBunker;
    private Bunker mPlayerTwoBunker;
    private Landscape mLandscape;

    private BombshellAnimatorAsyncTask mBombshellAnimatorAsyncTask;

    // Views :
    private GameView mGameView;
    private LinearLayout mLinearLayoutChooseLandscape;
    private LinearLayout mLinearLayoutControls;
    private TextView mTextViewPlayersName;
    private AnglePrecisionSliderLayout mAnglePrecisionSliderLayout;
    private PowerPrecisionSliderLayout mPowerPrecisionSliderLayout;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_two_player_game);

        // Retrieve useful views.
        mLinearLayoutChooseLandscape = (LinearLayout) findViewById(R.id.linearLayoutChooseLandscape);
        mLinearLayoutControls = (LinearLayout) findViewById(R.id.linearLayoutControls);
        mTextViewPlayersName = (TextView) findViewById(R.id.textView_playersName);
        mAnglePrecisionSliderLayout = (AnglePrecisionSliderLayout) findViewById(R.id.anglePrecisionSliderLayout);
        mPowerPrecisionSliderLayout = (PowerPrecisionSliderLayout) findViewById(R.id.powerPrecisionSliderLayout);
        mGameView = (GameView) findViewById(R.id.gameView);

        // Define layout animation.
        ObjectAnimator animatorAppearing = ObjectAnimator.ofFloat(mLinearLayoutControls, "translationY", -LAYOUT_TRANSITION_Y_TRANSLATION_OFFSET, 0f);
        ObjectAnimator animatorDisappearing = ObjectAnimator.ofFloat(mLinearLayoutControls, "translationY", 0f, -LAYOUT_TRANSITION_Y_TRANSLATION_OFFSET);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setAnimator(LayoutTransition.APPEARING, animatorAppearing);
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, animatorDisappearing);
        ((RelativeLayout) findViewById(R.id.mainRelativeLayout)).setLayoutTransition(layoutTransition);

        mAnglePrecisionSliderLayout.setListener(this);
        mPowerPrecisionSliderLayout.setListener(this);

        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        // Initialize or restore game model.
                        if (savedInstanceState != null) {
                            mGameSequencer = savedInstanceState.getParcelable(BUNDLE_KEY_GAME_SEQUENCER);
                            mLandscape = savedInstanceState.getParcelable(BUNDLE_KEY_LANDSCAPE);
                            if (mGameSequencer.getGameState() != GameSequencer.GameState.PLAYER_TWO_WON) {
                                mPlayerOneBunker = savedInstanceState.getParcelable(BUNDLE_KEY_BUNKER_ONE);
                            }
                            if (mGameSequencer.getGameState() != GameSequencer.GameState.PLAYER_ONE_WON) {
                                mPlayerTwoBunker = savedInstanceState.getParcelable(BUNDLE_KEY_BUNKER_TWO);
                            }

                        } else {
                            mGameSequencer = new GameSequencer();
                            mLandscape = new Landscape(getResources().getColor(R.color.green_land_slice));
                            mPlayerOneBunker = new Bunker(true, getResources().getColor(R.color.red_bunker), getBunkerOneCoordinates());
                            mPlayerTwoBunker = new Bunker(false, getResources().getColor(R.color.yellow_bunker), getBunkerTwoCoordinates());
                        }

                        // TODO Clean.
                        /*
                        if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_ONE_PLAYING) {
                            mTextViewPlayersName.setText(getString(R.string.player_one));
                            mAnglePrecisionSliderLayout.setValue(mPlayerOneBunker.getAbsoluteCanonAngleDegrees());
                            mPowerPrecisionSliderLayout.setValue(mPlayerOneBunker.getCanonPower());
                        } else if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_TWO_PLAYING) {
                            mTextViewPlayersName.setText(getString(R.string.player_two));
                            mAnglePrecisionSliderLayout.setValue(mPlayerTwoBunker.getAbsoluteCanonAngleDegrees());
                            mPowerPrecisionSliderLayout.setValue(mPlayerTwoBunker.getCanonPower());
                        } else {
                            mLinearLayoutControls.setVisibility(View.GONE);
                        }
                        */

                        // Initialize GameView.
                        if (mPlayerOneBunker != null) {
                            mGameView.registerDrawer(mPlayerOneBunker);
                        }
                        if (mPlayerTwoBunker != null) {
                            mGameView.registerDrawer(mPlayerTwoBunker);
                        }
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
        mGameSequencer.cancelFiring();
        outState.putParcelable(BUNDLE_KEY_GAME_SEQUENCER, mGameSequencer);
        outState.putParcelable(BUNDLE_KEY_LANDSCAPE, mLandscape);
        if (mGameSequencer.getGameState() != GameSequencer.GameState.PLAYER_TWO_WON) {
            outState.putParcelable(BUNDLE_KEY_BUNKER_ONE, mPlayerOneBunker);
        }
        if (mGameSequencer.getGameState() != GameSequencer.GameState.PLAYER_ONE_WON) {
            outState.putParcelable(BUNDLE_KEY_BUNKER_TWO, mPlayerTwoBunker);
        }
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

        // If game is over, just call super.
        if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_ONE_WON ||
                mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_TWO_WON) {
            super.onBackPressed();
            return;
        }

        // Else show a confirmation dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_abondon_game));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TwoPlayerGameActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public void onValueChanged(AbstractPrecisionSliderLayout sliderLayout, int newValue) {
        if (sliderLayout.equals(mAnglePrecisionSliderLayout)) {
            if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_ONE_PLAYING) {
                mPlayerOneBunker.setAbsoluteCanonAngle(newValue);
                mGameView.invalidate();
            } else if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_TWO_PLAYING) {
                mPlayerTwoBunker.setAbsoluteCanonAngle(newValue);
                mGameView.invalidate();
            }
        } else if (sliderLayout.equals(mPowerPrecisionSliderLayout)) {
            if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_ONE_PLAYING) {
                mPlayerOneBunker.setCanonPower(newValue);
                mGameView.invalidate();
            } else if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_TWO_PLAYING) {
                mPlayerTwoBunker.setCanonPower(newValue);
                mGameView.invalidate();
            }
        }
    }

    public void onButtonClickedChangeLandscape(View view) {
        mGameView.unregisterDrawer(mLandscape);
        mLandscape = new Landscape(getResources().getColor(R.color.green_land_slice));
        mGameView.registerDrawer(mLandscape);
        mPlayerOneBunker.setViewCoordinates(getBunkerOneCoordinates());
        mPlayerTwoBunker.setViewCoordinates(getBunkerTwoCoordinates());
        mGameView.invalidate();
    }

    public void onButtonClickedStartPlaying(View view) {
        mGameSequencer.startPlaying();
        mTextViewPlayersName.setText(getString(R.string.player_one));
        mAnglePrecisionSliderLayout.setValue(mPlayerOneBunker.getAbsoluteCanonAngleDegrees());
        mPowerPrecisionSliderLayout.setValue(mPlayerOneBunker.getCanonPower());
        mPlayerOneBunker.setIsPlaying(true);
        mLinearLayoutChooseLandscape.setVisibility(View.GONE);
        mLinearLayoutControls.setVisibility(View.VISIBLE);
        mGameView.invalidate();
    }

    public void onButtonClickedFire(View view) {

        // Code armouring.
        if (mBombshellAnimatorAsyncTask != null) {
            Log.d(LOG_TAG, "There is a Bombshell flying already");
            return;
        }

        // Update UI and GameSequencer.
        mLinearLayoutControls.setVisibility(View.GONE);
        mGameSequencer.fireButtonPressed();

        // Configure and launch Firing AsyncTask.
        BombshellPathComputer bombshellPathComputer;
        ArrayList<Drawer> collidableDrawers = new ArrayList<Drawer>();
        collidableDrawers.add(mLandscape);
        if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_ONE_FIRING) {
            mPlayerOneBunker.setIsPlaying(false);
            bombshellPathComputer = new BombshellPathComputer(mPlayerOneBunker.getCanonPower(), mPlayerOneBunker.getGeometricalCanonAngleRadian(), mPlayerOneBunker.getViewCoordinates());
            collidableDrawers.add(mPlayerTwoBunker);
        } else if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_TWO_FIRING) {
            mPlayerTwoBunker.setIsPlaying(false);
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

        if (drawer == null || drawer.equals(mLandscape)) {

            Toast.makeText(this, R.string.target_missed, Toast.LENGTH_SHORT).show();
            mGameSequencer.bombshellMissedTarget();
            if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_ONE_PLAYING) {
                mTextViewPlayersName.setText(getString(R.string.player_one));
                mAnglePrecisionSliderLayout.setValue(mPlayerOneBunker.getAbsoluteCanonAngleDegrees());
                mPowerPrecisionSliderLayout.setValue(mPlayerOneBunker.getCanonPower());
                mPlayerOneBunker.setIsPlaying(true);
            } else if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_TWO_PLAYING) {
                mTextViewPlayersName.setText(getString(R.string.player_two));
                mAnglePrecisionSliderLayout.setValue(mPlayerTwoBunker.getAbsoluteCanonAngleDegrees());
                mPowerPrecisionSliderLayout.setValue(mPlayerTwoBunker.getCanonPower());
                mPlayerTwoBunker.setIsPlaying(true);
            } else {
                // Issue...
            }
            mLinearLayoutControls.setVisibility(View.VISIBLE);

        } else if (drawer.equals(mPlayerTwoBunker)) {

            Toast.makeText(this, getString(R.string.player_won) + " " + mGameSequencer.getRoundsCountPlayerOne() + " " + getString(R.string.player_rounds_count), Toast.LENGTH_LONG).show();
            mGameView.unregisterDrawer(mPlayerTwoBunker);
            mPlayerTwoBunker = null;
            mGameSequencer.bombshellDitHitBunker(false);

        } else if (drawer.equals(mPlayerOneBunker)) {

            Toast.makeText(this, getString(R.string.player_won) + " " + mGameSequencer.getRoundsCountPlayerTwo() + " " + getString(R.string.player_rounds_count), Toast.LENGTH_LONG).show();
            mGameView.unregisterDrawer(mPlayerOneBunker);
            mPlayerOneBunker = null;
            mGameSequencer.bombshellDitHitBunker(true);
        }

        mGameView.invalidate();
    }

    private ViewCoordinates getBunkerOneCoordinates() {
        float landSliceWidth = mGameView.getWidth() / mLandscape.getNumberOfLandscapeSlices();
        return new ViewCoordinates(Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER * landSliceWidth,
                mGameView.getHeight()
                        - mGameView.getHeight() * Landscape.MAX_HEIGHT_RATIO_FOR_LANDSCAPE * mLandscape.getLandscapeHeightPercentage(Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER)
                        - Bunker.BUNKER_HEIGHT);
    }

    private ViewCoordinates getBunkerTwoCoordinates() {
        float landSliceWidth = mGameView.getWidth() / mLandscape.getNumberOfLandscapeSlices();
        return new ViewCoordinates(mGameView.getWidth() - landSliceWidth * Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER,
                mGameView.getHeight()
                        - mGameView.getHeight() * Landscape.MAX_HEIGHT_RATIO_FOR_LANDSCAPE * mLandscape.getLandscapeHeightPercentage(mLandscape.getNumberOfLandscapeSlices() - 1 - Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER)
                        - Bunker.BUNKER_HEIGHT);
    }
}