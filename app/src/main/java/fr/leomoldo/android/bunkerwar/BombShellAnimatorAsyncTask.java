package fr.leomoldo.android.bunkerwar;


import android.os.AsyncTask;
import android.util.Log;

import fr.leomoldo.android.bunkerwar.game.BombShellPathComputer;
import fr.leomoldo.android.bunkerwar.game.Landscape;
import fr.leomoldo.android.bunkerwar.sdk.ViewCoordinates;

/**
 * Created by leomoldo on 19/05/2016.
 */
public class BombShellAnimatorAsyncTask extends AsyncTask<BombShellPathComputer, ViewCoordinates, Boolean> {

    private static final String LOG_TAG = BombShellAnimatorAsyncTask.class.getSimpleName();

    private final static int ITERATION_WAITING_TIME = 10;

    private TwoPlayerGameView mGameView;
    private int mViewHeight;
    private int mViewWidth;
    private ViewCoordinates mTargetBunkerVC;
    private Landscape mLandscape;

    public BombShellAnimatorAsyncTask(TwoPlayerGameView gameView, Landscape landscape, ViewCoordinates targetBunkerVC) {
        mGameView = gameView;
        mLandscape = landscape;
        mViewHeight = mGameView.getHeight();
        mViewWidth = mGameView.getWidth();
        mTargetBunkerVC = targetBunkerVC;

        /*
        Log.d(LOG_TAG, "View Width : " + mViewWidth);
        Log.d(LOG_TAG, "View Height : " + mViewHeight);
        */
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

            // TODO We must prevent the playing bunker from hitting itself when firing (don't check when timecounter is to low).
            // Check if bombshell dit hit player one bunker.
            /*
            if ( Math.abs(bombShellPathComputers[0].getCurrentCoordinates().getX() - mBunkerPlayerOneCoordinates.getX()) < GameView.BUNKER_RADIUS &&
                    Math.abs(bombShellPathComputers[0].getCurrentCoordinates().getY() - mBunkerPlayerOneCoordinates.getY()) < GameView.BUNKER_RADIUS) {
                Log.d(LOG_TAG, "BombShell collided with player ONE.");
                shouldHalt = true;
            }
            */

            // Check if bombshell dit hit target bunker.
            // TODO Replace "2" by constant factor for collision detection.
            if (Math.abs(bombShellPathComputers[0].getCurrentCoordinates().getX() - mTargetBunkerVC.getX()) < TwoPlayerGameView.BUNKER_RADIUS * 2 &&
                    Math.abs(bombShellPathComputers[0].getCurrentCoordinates().getY() - mTargetBunkerVC.getY()) < TwoPlayerGameView.BUNKER_RADIUS * 2) {
                Log.d(LOG_TAG, "BombShell collided with target Bunker.");
                shouldHalt = true;
            }

            // Check that bombshell did not collide landscape.
            if (bombShellPathComputers[0].getCurrentCoordinates().getY() > getLandscapeHeightForX(bombShellPathComputers[0].getCurrentCoordinates().getX())) {

                Log.d(LOG_TAG, "BombShell collided with landscape.");
                /*
                Log.d(LOG_TAG, "BombShell X : " + bombShellPathComputers[0].getCurrentCoordinates().getX());
                Log.d(LOG_TAG, "BombShell Y : " + bombShellPathComputers[0].getCurrentCoordinates().getY());
                */
                shouldHalt = true;
            }

            // Check that bombshell did not left the screen from right of left side (+ bottom but is it really useful?).
            if (bombShellPathComputers[0].getCurrentCoordinates().getY() > mViewHeight ||
                    bombShellPathComputers[0].getCurrentCoordinates().getX() > mViewWidth ||
                    bombShellPathComputers[0].getCurrentCoordinates().getX() < 0) {

                Log.d(LOG_TAG, "BombShell out of Screen.");
                /*
                Log.d(LOG_TAG, "BombShell X : " + bombShellPathComputers[0].getCurrentCoordinates().getX());
                Log.d(LOG_TAG, "BombShell Y : " + bombShellPathComputers[0].getCurrentCoordinates().getY());
                */

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

    // TODO : Refactor into specific Landscape collision detection method.
    public float getLandscapeHeightForX(float x) {
        int sliceIndex = (int) (x / (mViewWidth / mLandscape.getNumberOfLandscapeSlices()));
        return mViewHeight - mViewHeight * TwoPlayerGameView.MAX_HEIGHT_RATIO_FOR_LANDSCAPE * mLandscape.getLandscapeHeightPercentage(sliceIndex);
    }
}
