package fr.leomoldo.android.bunkerwar.activity;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
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
import fr.leomoldo.android.bunkerwar.view.WindIndicatorLayout;

public class TwoPlayerGameActivity extends AppCompatActivity implements BombshellAnimatorAsyncTask.CollisionListener, AbstractPrecisionSliderLayout.PrecisionSliderLayoutListener, AudioManager.OnAudioFocusChangeListener {

    private final static String LOG_TAG = TwoPlayerGameActivity.class.getSimpleName();

    private final static String BUNDLE_KEY_GAME_SEQUENCER = TwoPlayerGameActivity.class.getName() + ".gameSequencer";
    private final static String BUNDLE_KEY_BUNKER_ONE = TwoPlayerGameActivity.class.getName() + ".bunkerOne";
    private final static String BUNDLE_KEY_BUNKER_TWO = TwoPlayerGameActivity.class.getName() + ".bunkerTwo";
    private final static String BUNDLE_KEY_LANDSCAPE = TwoPlayerGameActivity.class.getName() + ".landscape";
    private final static String BUNDLE_KEY_WIND_VALUE = TwoPlayerGameActivity.class.getName() + ".windValue";

    private final static float SOUNDTRACK_VOLUME = 1.0f;
    private final static float SOUNDTRACK_DUCKING_VOLUME = 0.5f;
    private final static float SOUND_EFFECTS_VOLUME = 0.2f;
    private final static float SOUND_EFFECTS_BUTTONS_VOLUME = 0.15f;

    // Empirical value corresponding to the approximate height of mLinearLayoutControls + some margin.
    private final static float LAYOUT_TRANSITION_Y_TRANSLATION_OFFSET = 500f;

    // View width for a Nexus 4, which was used for testing.
    private final static float SCREEN_WIDTH_REFERENCE_FOR_SHOOTING_POWER = 1196;

    // Model :
    private GameSequencer mGameSequencer;
    private Bunker mPlayerOneBunker;
    private Bunker mPlayerTwoBunker;
    private Landscape mLandscape;
    private Integer mWindValue; // Integer between -50 and 50.

    private float mScreenWidthShotPowerFactor;
    private int mGameSpeed;

    private BombshellAnimatorAsyncTask mBombshellAnimatorAsyncTask;

    // Views :
    private GameView mGameView;
    private LinearLayout mLinearLayoutChooseLandscape;
    private LinearLayout mLinearLayoutControls;
    private WindIndicatorLayout mWindIndicatorLayout;
    private AnglePrecisionSliderLayout mAnglePrecisionSliderLayout;
    private PowerPrecisionSliderLayout mPowerPrecisionSliderLayout;
    private LinearLayout mLinearLayoutVictory;
    private TextView mTextViewVictory;

    // Audio :
    private MediaPlayer mMediaPlayerSoundtrack;
    private boolean mShouldPlaySoundtrack;
    private SoundPool mSoundPool;
    private int mSoundIdFire;
    private int mSoundIdMissed;
    private int mSoundIdBunkerHit;
    private int mSoundIdButtonHigh;
    private int mSoundIdButtonLow;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_game);
        // Retrieve useful views.
        mLinearLayoutChooseLandscape = (LinearLayout) findViewById(R.id.linearLayoutChooseLandscape);
        mLinearLayoutControls = (LinearLayout) findViewById(R.id.linearLayoutControls);
        mWindIndicatorLayout = (WindIndicatorLayout) findViewById(R.id.layoutWindIndicator);
        mAnglePrecisionSliderLayout = (AnglePrecisionSliderLayout) findViewById(R.id.anglePrecisionSliderLayout);
        mPowerPrecisionSliderLayout = (PowerPrecisionSliderLayout) findViewById(R.id.powerPrecisionSliderLayout);
        mLinearLayoutVictory = (LinearLayout) findViewById(R.id.linearLayoutVictory);
        mTextViewVictory = (TextView) findViewById(R.id.textViewVictory);
        mGameView = (GameView) findViewById(R.id.gameView);
        // Define layout animation.
        ObjectAnimator animatorAppearing = ObjectAnimator.ofFloat(mLinearLayoutControls, "translationY", -LAYOUT_TRANSITION_Y_TRANSLATION_OFFSET, 0f);
        ObjectAnimator animatorDisappearing = ObjectAnimator.ofFloat(mLinearLayoutControls, "translationY", 0f, -LAYOUT_TRANSITION_Y_TRANSLATION_OFFSET);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setAnimator(LayoutTransition.APPEARING, animatorAppearing);
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, animatorDisappearing);
        ((RelativeLayout) findViewById(R.id.mainRelativeLayout)).setLayoutTransition(layoutTransition);
        // Set Listeners.
        mAnglePrecisionSliderLayout.setListener(this);
        mPowerPrecisionSliderLayout.setListener(this);
        // Read GameSpeed Value from SharedPreferences.
        mGameSpeed = getSharedPreferences(getString(R.string.shared_preferences_name), Context.MODE_PRIVATE).getInt(getString(R.string.shared_preferences_key_game_speed), BombshellAnimatorAsyncTask.MAX_GAME_SPEED / 2);
        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Log.d(LOG_TAG, "View width onGlobalLayout: " + findViewById(android.R.id.content).getWidth());
                        Log.d(LOG_TAG, "View height onGlobalLayout: " + findViewById(android.R.id.content).getHeight());
                        // Determine screen width factor to adapt shooting power.
                        mScreenWidthShotPowerFactor = findViewById(android.R.id.content).getWidth() / SCREEN_WIDTH_REFERENCE_FOR_SHOOTING_POWER;
                        // Initialize or restore game model.
                        if (savedInstanceState != null) {
                            mGameSequencer = savedInstanceState.getParcelable(BUNDLE_KEY_GAME_SEQUENCER);
                            mLandscape = savedInstanceState.getParcelable(BUNDLE_KEY_LANDSCAPE);
                            mWindValue = savedInstanceState.getInt(BUNDLE_KEY_WIND_VALUE, 0);
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

                        // TODO Clean if not necessary (re-test).
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
    protected void onStart() {
        super.onStart();
        mShouldPlaySoundtrack = true;
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            startPlayingSoundtrack();
        }
        if (android.os.Build.VERSION.SDK_INT < 21) {
            mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        } else {
            SoundPool.Builder builder = new SoundPool.Builder();
            mSoundPool = builder.build();
        }
        mSoundIdFire = mSoundPool.load(this, R.raw.fire, 1);
        mSoundIdMissed = mSoundPool.load(this, R.raw.missed, 1);
        mSoundIdBunkerHit = mSoundPool.load(this, R.raw.bunker_hit, 1);
        mSoundIdButtonHigh = mSoundPool.load(this, R.raw.button_high, 1);
        mSoundIdButtonLow = mSoundPool.load(this, R.raw.button_low, 1);
    }

    @Override
    protected void onStop() {
        stopPlayingSoundtrack();
        mShouldPlaySoundtrack = false;
        mSoundPool.release();
        mSoundPool = null;
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mGameSequencer.cancelFiring();
        outState.putParcelable(BUNDLE_KEY_GAME_SEQUENCER, mGameSequencer);
        outState.putParcelable(BUNDLE_KEY_LANDSCAPE, mLandscape);
        if (mWindValue != null) {
            outState.putInt(BUNDLE_KEY_WIND_VALUE, mWindValue);
        }
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
        if (mMediaPlayerSoundtrack != null) {
            mMediaPlayerSoundtrack.release();
            mMediaPlayerSoundtrack = null;
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
                mSoundPool.play(mSoundIdButtonHigh, SOUND_EFFECTS_BUTTONS_VOLUME, SOUND_EFFECTS_BUTTONS_VOLUME, 0, 0, 1f);
                TwoPlayerGameActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSoundPool.play(mSoundIdButtonLow, SOUND_EFFECTS_BUTTONS_VOLUME, SOUND_EFFECTS_BUTTONS_VOLUME, 0, 0, 1f);
            }
        });
        builder.show();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mShouldPlaySoundtrack) {
                    startPlayingSoundtrack();
                    mMediaPlayerSoundtrack.setVolume(SOUNDTRACK_VOLUME, SOUNDTRACK_VOLUME);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                stopPlayingSoundtrack();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mMediaPlayerSoundtrack != null && mMediaPlayerSoundtrack.isPlaying()) {
                    mMediaPlayerSoundtrack.pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mMediaPlayerSoundtrack != null && mMediaPlayerSoundtrack.isPlaying()) {
                    mMediaPlayerSoundtrack.setVolume(SOUNDTRACK_DUCKING_VOLUME, SOUNDTRACK_DUCKING_VOLUME);
                }
                break;
        }
    }

    @Override
    public void onValueChanged(AbstractPrecisionSliderLayout sliderLayout, int newValue, boolean fromButton) {
        if (fromButton) {
            mSoundPool.play(mSoundIdButtonLow, SOUND_EFFECTS_BUTTONS_VOLUME, SOUND_EFFECTS_BUTTONS_VOLUME, 0, 0, 1f);
        }
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
        mSoundPool.play(mSoundIdButtonLow, SOUND_EFFECTS_BUTTONS_VOLUME, SOUND_EFFECTS_BUTTONS_VOLUME, 0, 0, 1f);
        mGameView.unregisterDrawer(mLandscape);
        mLandscape = new Landscape(getResources().getColor(R.color.green_land_slice));
        mGameView.registerDrawer(mLandscape);
        mPlayerOneBunker.setViewCoordinates(getBunkerOneCoordinates());
        mPlayerTwoBunker.setViewCoordinates(getBunkerTwoCoordinates());
        mGameView.invalidate();
    }

    public void onButtonClickedStartPlaying(View view) {
        mSoundPool.play(mSoundIdButtonHigh, SOUND_EFFECTS_BUTTONS_VOLUME, SOUND_EFFECTS_BUTTONS_VOLUME, 0, 0, 1f);
        mWindIndicatorLayout.setVisibility(View.VISIBLE);
        changeWindValue();
        mGameSequencer.startPlaying();
        mAnglePrecisionSliderLayout.setValue(mPlayerOneBunker.getAbsoluteCanonAngleDegrees());
        mPowerPrecisionSliderLayout.setValue(mPlayerOneBunker.getCanonPower());
        mPlayerOneBunker.setIsPlaying(true);
        mLinearLayoutChooseLandscape.setVisibility(View.GONE);
        mWindIndicatorLayout.setVisibility(View.VISIBLE);
        mLinearLayoutControls.setVisibility(View.VISIBLE);
        mGameView.invalidate();
    }

    public void onButtonClickedFire(View view) {
        if (mBombshellAnimatorAsyncTask != null) {
            return;
        }
        // Play sound effect.
        mSoundPool.play(mSoundIdFire, SOUND_EFFECTS_VOLUME, SOUND_EFFECTS_VOLUME, 0, 0, 1f);
        // Update UI and GameSequencer.
        mLinearLayoutControls.setVisibility(View.GONE);
        mGameSequencer.fireButtonPressed();
        // Configure and launch Firing AsyncTask.
        BombshellPathComputer bombshellPathComputer;
        ArrayList<Drawer> collidableDrawers = new ArrayList<Drawer>();
        collidableDrawers.add(mLandscape);
        ViewCoordinates initialVC;
        Bunker firingBunker;
        Bunker targetBunker;
        if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_ONE_FIRING) {
            firingBunker = mPlayerOneBunker;
            targetBunker = mPlayerTwoBunker;
        } else if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_TWO_FIRING) {
            firingBunker = mPlayerTwoBunker;
            targetBunker = mPlayerOneBunker;
        } else {
            Log.e(LOG_TAG, "Game Sequencer state error.");
            return;
        }
        firingBunker.setIsPlaying(false);
        initialVC = new ViewCoordinates(firingBunker.getViewCoordinates().getX() + firingBunker.getCanonLengthX(), firingBunker.getViewCoordinates().getY() + firingBunker.getCanonLengthY());
        bombshellPathComputer = new BombshellPathComputer(firingBunker.getCanonPower(), firingBunker.getGeometricalCanonAngleRadian(), initialVC, mWindValue, mScreenWidthShotPowerFactor);
        collidableDrawers.add(targetBunker);
        mBombshellAnimatorAsyncTask = new BombshellAnimatorAsyncTask(mGameView, collidableDrawers, this, mGameSpeed);
        mBombshellAnimatorAsyncTask.execute(bombshellPathComputer);
    }

    public void onButtonClickedBackToMenu(View view) {
        mSoundPool.play(mSoundIdButtonHigh, SOUND_EFFECTS_BUTTONS_VOLUME, SOUND_EFFECTS_BUTTONS_VOLUME, 0, 0, 1f);
        super.onBackPressed();
    }

    @Override
    public void onDrawerHit(Drawer drawer) {
        mBombshellAnimatorAsyncTask = null;
        if (drawer == null || drawer.equals(mLandscape)) {
            if (mShouldPlaySoundtrack) {
                mSoundPool.play(mSoundIdMissed, SOUND_EFFECTS_VOLUME, SOUND_EFFECTS_VOLUME, 0, 0, 1f);
            }
            Toast.makeText(this, R.string.target_missed, Toast.LENGTH_SHORT).show();
            mGameSequencer.bombshellMissedTarget();
            if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_ONE_PLAYING) {
                mAnglePrecisionSliderLayout.setValue(mPlayerOneBunker.getAbsoluteCanonAngleDegrees());
                mPowerPrecisionSliderLayout.setValue(mPlayerOneBunker.getCanonPower());
                mPlayerOneBunker.setIsPlaying(true);
            } else if (mGameSequencer.getGameState() == GameSequencer.GameState.PLAYER_TWO_PLAYING) {
                mAnglePrecisionSliderLayout.setValue(mPlayerTwoBunker.getAbsoluteCanonAngleDegrees());
                mPowerPrecisionSliderLayout.setValue(mPlayerTwoBunker.getCanonPower());
                mPlayerTwoBunker.setIsPlaying(true);
            } else {
                Log.e(LOG_TAG, "GameSequencer game state issue : no bunker currently playing.");
            }
            mLinearLayoutControls.setVisibility(View.VISIBLE);

            if (getSharedPreferences(getString(R.string.shared_preferences_name), Context.MODE_PRIVATE).getBoolean(getString(R.string.shared_preferences_key_wind_change), true)) {
                changeWindValue();
            }
        } else if (drawer.equals(mPlayerTwoBunker)) {
            mSoundPool.play(mSoundIdBunkerHit, SOUND_EFFECTS_VOLUME, SOUND_EFFECTS_VOLUME, 0, 0, 1f);
            String victoryForPlayerOne;
            if (mGameSequencer.getRoundsCountPlayerOne() == 1) {
                victoryForPlayerOne = getString(R.string.player_one) + " " + getString(R.string.player_won) + " " + mGameSequencer.getRoundsCountPlayerOne() + " " + getString(R.string.player_rounds_count_singular);
            } else {
                victoryForPlayerOne = getString(R.string.player_one) + " " + getString(R.string.player_won) + " " + mGameSequencer.getRoundsCountPlayerOne() + " " + getString(R.string.player_rounds_count_plural);
            }
            mTextViewVictory.setText(victoryForPlayerOne);
            mWindIndicatorLayout.setVisibility(View.GONE);
            mLinearLayoutVictory.setVisibility(View.VISIBLE);
            mGameView.unregisterDrawer(mPlayerTwoBunker);
            mPlayerTwoBunker = null;
            mGameSequencer.bombshellDitHitBunker(false);
        } else if (drawer.equals(mPlayerOneBunker)) {
            mSoundPool.play(mSoundIdBunkerHit, SOUND_EFFECTS_VOLUME, SOUND_EFFECTS_VOLUME, 0, 0, 1f);
            String victoryForPlayerTwo;
            if (mGameSequencer.getRoundsCountPlayerTwo() == 1) {
                victoryForPlayerTwo = getString(R.string.player_two) + " " + getString(R.string.player_won) + " " + mGameSequencer.getRoundsCountPlayerTwo() + " " + getString(R.string.player_rounds_count_singular);
            } else {
                victoryForPlayerTwo = getString(R.string.player_two) + " " + getString(R.string.player_won) + " " + mGameSequencer.getRoundsCountPlayerTwo() + " " + getString(R.string.player_rounds_count_plural);
            }
            mTextViewVictory.setText(victoryForPlayerTwo);
            mWindIndicatorLayout.setVisibility(View.GONE);
            mLinearLayoutVictory.setVisibility(View.VISIBLE);
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

    private void changeWindValue() {
        mWindValue = ((int) (Math.random() * 100)) - 50;
        mWindIndicatorLayout.displayWindValue(mWindValue);
    }

    private void startPlayingSoundtrack() {
        if (mMediaPlayerSoundtrack == null || !mMediaPlayerSoundtrack.isPlaying()) {
            mMediaPlayerSoundtrack = MediaPlayer.create(this, R.raw.soundtrack_game);
            mMediaPlayerSoundtrack.setLooping(true);
            mMediaPlayerSoundtrack.start();
        }
    }

    private void stopPlayingSoundtrack() {
        if (mMediaPlayerSoundtrack != null && mMediaPlayerSoundtrack.isPlaying()) {
            mMediaPlayerSoundtrack.stop();
        }
    }
}