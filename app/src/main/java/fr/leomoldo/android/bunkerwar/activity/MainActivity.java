package fr.leomoldo.android.bunkerwar.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
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

        mMediaPlayerSoundtrack.release();
        mMediaPlayerSoundtrack = null;

        super.onDestroy();
    }

    private void startPlayingSoundtrack() {
        // TODO Clean.
        //if (mMediaPlayerSoundtrack == null) {
            mMediaPlayerSoundtrack = MediaPlayer.create(this, R.raw.soundtrack_menu);
            mMediaPlayerSoundtrack.setLooping(true);
        //}
        // if (!mMediaPlayerSoundtrack.isPlaying()) {
            mMediaPlayerSoundtrack.start();
        // }
    }

    private void stopPlayingSoundtrack() {
        if (mMediaPlayerSoundtrack != null && mMediaPlayerSoundtrack.isPlaying()) {
            mMediaPlayerSoundtrack.stop();
        }
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
        // To be implemented.
    }

    public void onButtonClickedCredits(View view) {
        // To be implemented.
    }
}
