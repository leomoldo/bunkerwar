package fr.leomoldo.android.bunkerwar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import fr.leomoldo.android.bunkerwar.drawer.Bunker;
import fr.leomoldo.android.bunkerwar.drawer.Landscape;
import fr.leomoldo.android.bunkerwar.sdk.ViewCoordinates;

public class TwoPlayerGameActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private final static String LOG_TAG = TwoPlayerGameActivity.class.getSimpleName();

    // Model :
    private GameSequencer mGameSequencer;
    private Bunker mPlayerOneBunker;
    private Bunker mPlayerTwoBunker;
    private Landscape mLandscape;

    // Views :
    private TwoPlayerGameView mTwoPlayerGameView;

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
        mTwoPlayerGameView = (TwoPlayerGameView) findViewById(R.id.gameView);

        initializeGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.menu.game_menu:

                mTwoPlayerGameView.initializeNewGame(mLandscape, mPlayerOneBunker, mPlayerTwoBunker);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Integer value = progress;

        switch (seekBar.getId()) {

            case R.id.seekBarAnglePlayerOne:
                mPlayerOneBunker.setAbsoluteCanonAngle(progress);
                mTwoPlayerGameView.invalidate();
                mTextViewIndicatorAnglePlayerOne.setText(value.toString());
                break;
            case R.id.seekBarPowerPlayerOne:
                mPlayerOneBunker.setCanonPower(progress);
                mTextViewIndicatorPowerPlayerOne.setText(value.toString());
                break;
            case R.id.seekBarAnglePlayerTwo:
                mPlayerTwoBunker.setAbsoluteCanonAngle(progress);
                mTwoPlayerGameView.invalidate();
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

    private void initializeGame() {
        // Initialize game model.
        mGameSequencer = new GameSequencer();
        mLandscape = new Landscape();
        mPlayerOneBunker = new Bunker(true);
        mPlayerTwoBunker = new Bunker(false);

        // Initialize GameView.
        mTwoPlayerGameView.initializeNewGame(mLandscape, mPlayerOneBunker, mPlayerTwoBunker);
        mTwoPlayerGameView.invalidate();

        findViewById(R.id.buttonFirePlayerOne).setVisibility(View.VISIBLE);

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

        ViewCoordinates currentBombShellCoordinates;
        ViewCoordinates targetBunkerCoordinates;
        BombShellPathComputer bombShellPathComputer;

        // Update UI.
        if (didPlayerOneFire) {
            // TODO Uncomment.
            // findViewById(R.id.buttonFirePlayerOne).setVisibility(View.GONE);
            currentBombShellCoordinates = mTwoPlayerGameView.getBunkerPlayerOneCoordinates();
            bombShellPathComputer = new BombShellPathComputer(mPlayerOneBunker.getCanonPower(), mPlayerOneBunker.getGeometricalCanonAngleRadian(), currentBombShellCoordinates);
            targetBunkerCoordinates = mTwoPlayerGameView.getBunkerPlayerTwoCoordinates();
        } else {
            findViewById(R.id.buttonFirePlayerTwo).setVisibility(View.GONE);
            currentBombShellCoordinates = mTwoPlayerGameView.getBunkerPlayerTwoCoordinates();
            bombShellPathComputer = new BombShellPathComputer(mPlayerTwoBunker.getCanonPower(), mPlayerTwoBunker.getGeometricalCanonAngleRadian(), currentBombShellCoordinates);
            targetBunkerCoordinates = mTwoPlayerGameView.getBunkerPlayerOneCoordinates();
        }

        mTwoPlayerGameView.showBombShell(currentBombShellCoordinates);
        mTwoPlayerGameView.invalidate();

        BombShellAnimatorAsyncTask task = new BombShellAnimatorAsyncTask(mTwoPlayerGameView, mLandscape, targetBunkerCoordinates);
        task.execute(bombShellPathComputer);
    }
}