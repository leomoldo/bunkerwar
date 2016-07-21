package fr.leomoldo.android.bunkerwar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import fr.leomoldo.android.bunkerwar.R;

/**
 * Created by leomoldo on 08/06/2016.
 */
public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
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

    public void onButtonClickedTutorial(View view) {
        // To be implemented.
    }
}
