package fr.leomoldo.android.bunkerwar.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.leomoldo.android.bunkerwar.R;

/**
 * Created by leomoldo on 08/06/2016.
 */
public class MainActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mLinearLayoutButtons = (LinearLayout) findViewById(R.id.linearLayoutButtons);
        mRelativeLayoutSettings = (RelativeLayout) findViewById(R.id.relativeLayoutSettings);
        mRelativeLayoutCredits = (RelativeLayout) findViewById(R.id.relativeLayoutCredits);
        mCheckBoxSettingsWindChange = (CheckBox) findViewById(R.id.checkBox_settings_windChange);

        boolean shouldChangeWindAtEveryTurn = getSharedPreferences(getString(R.string.shared_preferences_name), Context.MODE_PRIVATE).getBoolean(getString(R.string.shared_preferences_key_wind_change), true);
        mCheckBoxSettingsWindChange.setChecked(shouldChangeWindAtEveryTurn);

        // Display app version in the credits screen.
        TextView textViewAppVersion = (TextView) findViewById(R.id.textViewCreditsAppVersion);
        String appVersionString;
        try {
            appVersionString = getPackageManager().getPackageInfo(getPackageResourcePath(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVersionString = "";
        }
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