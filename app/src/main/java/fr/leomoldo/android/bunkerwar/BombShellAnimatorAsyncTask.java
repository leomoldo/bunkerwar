package fr.leomoldo.android.bunkerwar;


import android.os.AsyncTask;
import android.util.Log;

import fr.leomoldo.android.bunkerwar.game.BombShellPathComputer;
import fr.leomoldo.android.bunkerwar.game.Landscape;

/**
 * Created by leomoldo on 19/05/2016.
 */
public class BombShellAnimatorAsyncTask extends AsyncTask<BombShellPathComputer, ViewCoordinates, Boolean> {

    private static final String LOG_TAG = BombShellAnimatorAsyncTask.class.getSimpleName();

    private final static int ITERATION_WAITING_TIME = 10;

    private GameView mGameView;
    private int mViewHeight;
    private int mViewWidth;
    private Landscape mLandscape;

    public BombShellAnimatorAsyncTask(GameView gameView, Landscape landscape) {
        mGameView = gameView;
        mLandscape = landscape;
        mViewHeight = mGameView.getHeight();
        mViewWidth = mGameView.getWidth();

        Log.d(LOG_TAG, "View Width : " + mViewWidth);
        Log.d(LOG_TAG, "View Height : " + mViewHeight);
    }

    @Override
    protected Boolean doInBackground(BombShellPathComputer... bombShellPathComputers) {
        Boolean shouldHalt = false;
        while (!shouldHalt) {
            bombShellPathComputers[0].incrementCoordinates();
            publishProgress(bombShellPathComputers[0].getCurrentCoordinates());
            /*
            Log.d(LOG_TAG, "timeCounter : " + bombShellPathComputers[0].getTimeCounter());
            Log.d(LOG_TAG, "currentBombShellX : " + bombShellPathComputers[0].getCurrentCoordinates().getX());
            Log.d(LOG_TAG, "currentBombShellY : " + bombShellPathComputers[0].getCurrentCoordinates().getY());
            */
            try {
                Thread.sleep(ITERATION_WAITING_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Check that bombshell did not hurt landscape.
            if (bombShellPathComputers[0].getCurrentCoordinates().getY() > getLandscapeHeightForX(bombShellPathComputers[0].getCurrentCoordinates().getX())) {

                Log.d(LOG_TAG, "BombShell hurt landscape");
                Log.d(LOG_TAG, "BombShell X : " + bombShellPathComputers[0].getCurrentCoordinates().getX());
                Log.d(LOG_TAG, "BombShell Y : " + bombShellPathComputers[0].getCurrentCoordinates().getY());

                shouldHalt = true;
            }

            // Check that bombshell did not left the screen from right of left side (+ bottom but is it really useful?).
            if (bombShellPathComputers[0].getCurrentCoordinates().getY() > mViewHeight ||
                    bombShellPathComputers[0].getCurrentCoordinates().getX() > mViewWidth ||
                    bombShellPathComputers[0].getCurrentCoordinates().getX() < 0) {

                Log.d(LOG_TAG, "BombShell out of Screen");
                Log.d(LOG_TAG, "BombShell X : " + bombShellPathComputers[0].getCurrentCoordinates().getX());
                Log.d(LOG_TAG, "BombShell Y : " + bombShellPathComputers[0].getCurrentCoordinates().getY());

                shouldHalt = true;
            }
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(ViewCoordinates... values) {
        super.onProgressUpdate(values);
        mGameView.showBombShell(values[0]);
        mGameView.invalidate();
    }

    @Override
    protected void onPostExecute(Boolean b) {
        super.onPostExecute(b);
        mGameView.hideBombShell();
        Log.d(LOG_TAG, "onPostExecute");
    }

    // TODO : Refactor into specific Landscape Collision detection method.
    public float getLandscapeHeightForX(float x) {
        int sliceIndex = (int) (x / (mViewWidth / mLandscape.getNumberOfLandscapeSlices()));
        return mViewHeight - mViewHeight * GameView.MAX_HEIGHT_RATIO_FOR_LANDSCAPE * mLandscape.getLandscapeHeightPercentage(sliceIndex);
    }
}
