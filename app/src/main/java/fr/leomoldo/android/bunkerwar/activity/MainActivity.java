package fr.leomoldo.android.bunkerwar.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import fr.leomoldo.android.bunkerwar.BombshellAnimatorAsyncTask;
import fr.leomoldo.android.bunkerwar.BuildConfig;
import fr.leomoldo.android.bunkerwar.R;

/**
 * Created by leomoldo on 08/06/2016.
 */
public class MainActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener, SeekBar.OnSeekBarChangeListener {

    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    private final static float SOUNDTRACK_VOLUME = 1.0f;
    private final static float SOUNDTRACK_DUCKING_VOLUME = 0.5f;

    // Audio :
    private MediaPlayer mMediaPlayerSoundtrack;
    private boolean mShouldPlaySoundtrack;

    // Views :
    private LinearLayout mLinearLayoutButtons;
    private RelativeLayout mRelativeLayoutSettings;
    private RelativeLayout mRelativeLayoutCredits;
    private CheckBox mCheckBoxSettingsWindChange;
    private SeekBar mSeekBarSettingsGameSpeed;
    private TextView mTextViewSettingsGameSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mLinearLayoutButtons = (LinearLayout) findViewById(R.id.linearLayoutButtons);
        mRelativeLayoutSettings = (RelativeLayout) findViewById(R.id.relativeLayoutSettings);
        mRelativeLayoutCredits = (RelativeLayout) findViewById(R.id.relativeLayoutCredits);
        mCheckBoxSettingsWindChange = (CheckBox) findViewById(R.id.checkBoxSettingsWindChange);
        mSeekBarSettingsGameSpeed = (SeekBar) findViewById(R.id.seekBarSettingsGameSpeed);
        mSeekBarSettingsGameSpeed.setMax(100);
        mSeekBarSettingsGameSpeed.setOnSeekBarChangeListener(this);
        mTextViewSettingsGameSpeed = (TextView) findViewById(R.id.textViewSettingsGameSpeed);

        // Retrieve SharedPreferences.
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
        int gameSpeedValue = sharedPreferences.getInt(getString(R.string.shared_preferences_key_game_speed), BombshellAnimatorAsyncTask.MAX_GAME_SPEED);
        boolean shouldChangeWindAtEveryTurn = sharedPreferences.getBoolean(getString(R.string.shared_preferences_key_wind_change), true);
        mCheckBoxSettingsWindChange.setChecked(shouldChangeWindAtEveryTurn);
        mSeekBarSettingsGameSpeed.setProgress(gameSpeedValue);
        // TODO Clean.
        // mTextViewSettingsGameSpeed.setText(String.valueOf(gameSpeedValue));

        // Display app version on the credits screen.
        TextView textViewAppVersion = (TextView) findViewById(R.id.textViewCreditsAppVersion);
        String appVersionString = " " + getString(R.string.credits_app_version) + BuildConfig.VERSION_NAME;
        textViewAppVersion.setText(appVersionString);
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
    }

    @Override
    protected void onStop() {
        stopPlayingSoundtrack();
        mShouldPlaySoundtrack = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        if (mMediaPlayerSoundtrack != null) {
            mMediaPlayerSoundtrack.release();
            mMediaPlayerSoundtrack = null;
        }
        super.onDestroy();
    }

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


    public void onButtonClickedStartNewGame(View view) {
        Intent intent = new Intent(this, TwoPlayerGameActivity.class);
        startActivity(intent);
    }

    public void onButtonClickedSettings(View view) {
        mRelativeLayoutSettings.setVisibility(View.VISIBLE);
        mLinearLayoutButtons.setVisibility(View.GONE);
    }

    public void onButtonClickedCredits(View view) {
        mRelativeLayoutCredits.setVisibility(View.VISIBLE);
        mLinearLayoutButtons.setVisibility(View.GONE);
    }

    public void onButtonClickedCloseSettings(View view) {
        mLinearLayoutButtons.setVisibility(View.VISIBLE);
        mRelativeLayoutSettings.setVisibility(View.GONE);
    }

    public void onButtonClickedCloseCredits(View view) {
        mLinearLayoutButtons.setVisibility(View.VISIBLE);
        mRelativeLayoutCredits.setVisibility(View.GONE);
    }

    public void onButtonClickedCreditsWebLink(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch (view.getId()) {
            case R.id.buttonLinkSourceCode:
                intent.setData(Uri.parse(getString(R.string.credits_link_source_code)));
                break;
            case R.id.buttonLinkLeo:
                intent.setData(Uri.parse(getString(R.string.credits_link_pulsarjericho)));
                break;
            case R.id.buttonLinkElodie:
                intent.setData(Uri.parse(getString(R.string.credits_link_elodie)));
                break;
            case R.id.buttonLinkSamples01:
                intent.setData(Uri.parse(getString(R.string.credits_link_samples_01)));
                break;
            case R.id.buttonLinkSamples02:
                intent.setData(Uri.parse(getString(R.string.credits_link_samples_02)));
                break;
            default:
                return;
        }
        startActivity(intent);
    }

    public void onCheckboxClickedSettingsWindChange(View view) {
        boolean shouldChangeWindAtEveryTurn = false;
        if (mCheckBoxSettingsWindChange.isChecked()) {
            shouldChangeWindAtEveryTurn = true;
        }
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.shared_preferences_key_wind_change), shouldChangeWindAtEveryTurn);
        editor.commit();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        mTextViewSettingsGameSpeed.setText(String.valueOf(progress));
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.shared_preferences_key_game_speed), progress);
        editor.commit();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void startPlayingSoundtrack() {
        if (mMediaPlayerSoundtrack == null || !mMediaPlayerSoundtrack.isPlaying()) {
            mMediaPlayerSoundtrack = MediaPlayer.create(this, R.raw.soundtrack_menu);
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