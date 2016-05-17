package fr.leomoldo.android.bunkerwar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import fr.leomoldo.android.bunkerwar.game.Bunker;
import fr.leomoldo.android.bunkerwar.game.GameSequencer;
import fr.leomoldo.android.bunkerwar.game.Landscape;
import fr.leomoldo.android.bunkerwar.game.PhysicalModel;

public class GameActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private final static String LOG_TAG = GameActivity.class.getSimpleName();

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

                mGameView.initializeNewGame(mLandscape, mPlayerOneBunker, mPlayerTwoBunker);
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
                mPlayerOneBunker.setCanonAngle(progress);
                mGameView.invalidate();
                mTextViewIndicatorAnglePlayerOne.setText(value.toString());
                break;
            case R.id.seekBarPowerPlayerOne:
                mPlayerOneBunker.setCanonPower(progress);
                mTextViewIndicatorPowerPlayerOne.setText(value.toString());
                break;
            case R.id.seekBarAnglePlayerTwo:
                mPlayerTwoBunker.setCanonAngle(progress);
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

    private void initializeGame() {
        // Initialize game model.
        mGameSequencer = new GameSequencer();
        mLandscape = new Landscape();
        mPlayerOneBunker = new Bunker(true);
        mPlayerTwoBunker = new Bunker(false);

        // Initialize GameView.
        mGameView.initializeNewGame(mLandscape, mPlayerOneBunker, mPlayerTwoBunker);
        mGameView.invalidate();

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

        float currentBombShellX;
        float currentBombShellY;
        Boolean shouldHalt = false;
        PhysicalModel physicalModel;
        Integer timeCounter = 0;

        // Update UI.
        if (didPlayerOneFire) {
            findViewById(R.id.buttonFirePlayerOne).setVisibility(View.GONE);
            currentBombShellX = mGameView.getBunkerPlayerOneX();
            currentBombShellY = mGameView.getBunkerPlayerOneY();
            physicalModel = new PhysicalModel(mPlayerOneBunker.getCanonPower(), mPlayerOneBunker.getCanonAngleRadian(), true);
        } else {
            findViewById(R.id.buttonFirePlayerTwo).setVisibility(View.GONE);
            currentBombShellX = mGameView.getBunkerPlayerTwoX();
            currentBombShellY = mGameView.getBunkerPlayerTwoY();
            physicalModel = new PhysicalModel(mPlayerTwoBunker.getCanonPower(), mPlayerTwoBunker.getCanonAngleRadian(), true);
        }

        mGameView.showBombShell(currentBombShellX, currentBombShellY);

        while (!shouldHalt) {

            Log.d(LOG_TAG, "timeCounter : " + timeCounter);
            Log.d(LOG_TAG, "currentBombShellX : " + currentBombShellX);
            Log.d(LOG_TAG, "currentBombShellY : " + currentBombShellY);

            currentBombShellX += physicalModel.getNextXOffset();
            currentBombShellY += physicalModel.getNextYOffset(timeCounter);

            mGameView.showBombShell(currentBombShellX, currentBombShellY);
            mGameView.invalidate();

            timeCounter++;

            if (timeCounter > 1000) {
                shouldHalt = true;
                mGameView.hideBombShell();
            }
        }


        // TODO Clean following :

        /*
        BombShellView bombShellView;
        if (didPlayerOneFire) {
            bombShellView = new BombShellView(this, mPlayerOneBunker);
        } else {
            bombShellView = new BombShellView(this, mPlayerTwoBunker);
        }
        int viewDimension = BombShellView.BOMBSHELL_RADIUS.intValue() * 2;
        RelativeLayout mainRelativeLayout = ( (RelativeLayout) findViewById(R.id.mainRelativeLayout));
        mainRelativeLayout.addView(bombShellView, viewDimension, viewDimension);
        */

        /*
        PhysicalModel physicalModel;
        if (didPlayerOneFire) {
            bombShellView.setX(mPlayerOneBunkerView.getX());
            bombShellView.setY(mPlayerOneBunkerView.getY());
            physicalModel = new PhysicalModel(mPlayerOneBunker.getCanonPower(), mPlayerOneBunker.getCanonAngleRadian(), true);
        } else {
            bombShellView.setX(mPlayerTwoBunkerView.getX());
            bombShellView.setY(mPlayerTwoBunkerView.getY());
            physicalModel = new PhysicalModel(mPlayerTwoBunker.getCanonPower(), mPlayerTwoBunker.getCanonAngleRadian(), false);
        }
        */

        /*
        Boolean shouldHalt = false;
        Integer timeCounter = 0;
        while(!shouldHalt) {

            Float currentX = bombShellView.getX();
            Float currentY = bombShellView.getY();

            Float currentXOffset = physicalModel.getNextXOffset();
            Float currentYOffset = physicalModel.getNextYOffset(timeCounter);

            bombShellView.setX(bombShellView.getX() + physicalModel.getNextXOffset());
            bombShellView.setY(bombShellView.getY() + physicalModel.getNextYOffset(timeCounter));

            timeCounter++;

            if(timeCounter > 1000000) {
                shouldHalt = true;
            }
        }
        */
    }

    // TODO Clean following :

    /*
    private void addLandscapeViews() {


        mMainRelativeLayoutHeight = mLandscapeLinearLayout.getHeight();

        Float landSliceHeight = 0f;
        Float percentage = 0f;
        Integer viewHeight = 0;

        mLandscapeFrameLayouts = new ArrayList<FrameLayout>(mLandscape.getNumberOfLandscapeSlices());
        mLandscapeFrameLayoutHeights = new ArrayList<Float>(mLandscape.getNumberOfLandscapeSlices());

        for (int i = 0; i < mLandscape.getNumberOfLandscapeSlices(); i++) {

            landSliceHeight = (float) mLandscape.getLandscapeHeight(i);
            percentage = landSliceHeight / 100f;
            viewHeight = (int) ( mMainRelativeLayoutHeight * HEIGHT_MARGIN_RATIO_FOR_LANDSCAPE * percentage );

            FrameLayout frame = new FrameLayout(GameActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, viewHeight);
            params.weight = 1;
            params.gravity = Gravity.BOTTOM;
            frame.setLayoutParams(params);
            frame.setBackgroundColor(getResources().getColor(R.color.green_land_slice));

            mLandscapeLinearLayout.addView(frame);

            mLandscapeFrameLayouts.add(i, frame);
            mLandscapeFrameLayoutHeights.add(i, (float) viewHeight);
        }

    }
    */

    /**
     * Must be called imperatively after addLandscapeViews.
     */

    /*
    private void addBunkerViews() {

        if (mLandscapeFrameLayoutHeights == null) {
            return;
        }

        float landSliceWidth = ((float) mLandscapeLinearLayout.getWidth()) / ((float) mLandscape.getNumberOfLandscapeSlices());

        float bunkerOneX = Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER * landSliceWidth;
        float bunkerOneY = mMainRelativeLayoutHeight - mLandscapeFrameLayoutHeights.get(Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER) - BunkerView.BUNKER_RADIUS;

        float bunkerTwoX = (mLandscape.getNumberOfLandscapeSlices() - 1 - Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER) * landSliceWidth;
        float bunkerTwoY = mMainRelativeLayoutHeight - mLandscapeFrameLayoutHeights.get(mLandscape.getNumberOfLandscapeSlices() - 1 - Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER) - BunkerView.BUNKER_RADIUS;

        mPlayerOneBunkerView = new BunkerView(this, mPlayerOneBunker);
        mPlayerOneBunkerView.setX(bunkerOneX);
        mPlayerOneBunkerView.setY(bunkerOneY);

        mPlayerTwoBunkerView = new BunkerView(this, mPlayerTwoBunker);
        mPlayerTwoBunkerView.setX(bunkerTwoX);
        mPlayerTwoBunkerView.setY(bunkerTwoY);

        RelativeLayout mainRelativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
        mainRelativeLayout.addView(mPlayerOneBunkerView, 0, new RelativeLayout.LayoutParams((int) landSliceWidth, (int) landSliceWidth));
        mainRelativeLayout.addView(mPlayerTwoBunkerView, 0, new RelativeLayout.LayoutParams((int) landSliceWidth, (int) landSliceWidth));
    }
    */

}