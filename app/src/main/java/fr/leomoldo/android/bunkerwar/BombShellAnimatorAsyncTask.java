package fr.leomoldo.android.bunkerwar;


import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import fr.leomoldo.android.bunkerwar.drawer.Bombshell;
import fr.leomoldo.android.bunkerwar.drawer.Bunker;
import fr.leomoldo.android.bunkerwar.drawer.Landscape;
import fr.leomoldo.android.bunkerwar.sdk.GameView;
import fr.leomoldo.android.bunkerwar.sdk.ViewCoordinates;

/**
 * Created by leomoldo on 19/05/2016.
 */
public class BombshellAnimatorAsyncTask extends AsyncTask<BombshellPathComputer, ViewCoordinates, Boolean> {

    private static final String LOG_TAG = BombshellAnimatorAsyncTask.class.getSimpleName();

    private final static int ITERATION_WAITING_TIME = 10;
    private final static float BUNKER_HITBOX_EXPANSION_RATIO = 2f;

    private GameView mGameView;
    private int mViewHeight;
    private int mViewWidth;
    private ViewCoordinates mTargetBunkerVC;
    private Landscape mLandscape;
    private Bombshell mBombshell;

    public BombshellAnimatorAsyncTask(GameView gameView, Landscape landscape, ViewCoordinates initialVC, ViewCoordinates targetBunkerVC) {
        mGameView = gameView;
        mLandscape = landscape;
        mViewHeight = mGameView.getHeight();
        mViewWidth = mGameView.getWidth();
        mTargetBunkerVC = targetBunkerVC;

        mBombshell = new Bombshell(Color.BLACK);
        mBombshell.setViewCoordinates(initialVC);
        mGameView.registerDrawer(mBombshell);

        /*
        Log.d(LOG_TAG, "View Width : " + mViewWidth);
        Log.d(LOG_TAG, "View Height : " + mViewHeight);
        */
    }

    @Override
    protected Boolean doInBackground(BombshellPathComputer... bombshellPathComputers) {
        Boolean shouldHalt = false;
        while (!shouldHalt) {
            bombshellPathComputers[0].incrementCoordinates();
            publishProgress(bombshellPathComputers[0].getCurrentCoordinates());
            /*
            Log.d(LOG_TAG, "timeCounter : " + bombshellPathComputers[0].getTimeCounter());
            Log.d(LOG_TAG, "currentBombshellX : " + bombshellPathComputers[0].getCurrentCoordinates().getX());
            Log.d(LOG_TAG, "currentBombshellY : " + bombshellPathComputers[0].getCurrentCoordinates().getY());
            */
            try {
                Thread.sleep(ITERATION_WAITING_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // We must prevent the playing bunker from hitting itself when firing (don't check when timecounter is to low).
            // Check if bombshell dit hit player one bunker.
            /*
            if ( Math.abs(bombshellPathComputers[0].getCurrentCoordinates().getX() - mBunkerPlayerOneCoordinates.getX()) < GameView.BUNKER_RADIUS &&
                    Math.abs(bombshellPathComputers[0].getCurrentCoordinates().getY() - mBunkerPlayerOneCoordinates.getY()) < GameView.BUNKER_RADIUS) {
                Log.d(LOG_TAG, "Bombshell collided with player ONE.");
                shouldHalt = true;
            }
            */

            // Check if bombshell dit hit target bunker.
            if (Math.abs(bombshellPathComputers[0].getCurrentCoordinates().getX() - mTargetBunkerVC.getX()) < Bunker.BUNKER_RADIUS * BUNKER_HITBOX_EXPANSION_RATIO &&
                    Math.abs(bombshellPathComputers[0].getCurrentCoordinates().getY() - mTargetBunkerVC.getY()) < Bunker.BUNKER_RADIUS * BUNKER_HITBOX_EXPANSION_RATIO) {
                Log.d(LOG_TAG, "Bombshell collided with target Bunker.");
                shouldHalt = true;
            }

            // Check that bombshell did not collide landscape.
            if (bombshellPathComputers[0].getCurrentCoordinates().getY() > getLandscapeHeightForX(bombshellPathComputers[0].getCurrentCoordinates().getX())) {

                Log.d(LOG_TAG, "Bombshell collided with landscape.");
                /*
                Log.d(LOG_TAG, "Bombshell X : " + bombshellPathComputers[0].getCurrentCoordinates().getX());
                Log.d(LOG_TAG, "Bombshell Y : " + bombshellPathComputers[0].getCurrentCoordinates().getY());
                */
                shouldHalt = true;
            }

            // Check that bombshell did not left the screen from right of left side (+ bottom but is it really useful?).
            if (bombshellPathComputers[0].getCurrentCoordinates().getY() > mViewHeight ||
                    bombshellPathComputers[0].getCurrentCoordinates().getX() > mViewWidth ||
                    bombshellPathComputers[0].getCurrentCoordinates().getX() < 0) {

                Log.d(LOG_TAG, "Bombshell out of Screen.");
                /*
                Log.d(LOG_TAG, "Bombshell X : " + bombshellPathComputers[0].getCurrentCoordinates().getX());
                Log.d(LOG_TAG, "Bombshell Y : " + bombshellPathComputers[0].getCurrentCoordinates().getY());
                */

                shouldHalt = true;
            }
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(ViewCoordinates... values) {
        super.onProgressUpdate(values);
        // TODO Clean.
        // mGameView.showBombshell(values[0]);
        mBombshell.setViewCoordinates(values[0]);
        mGameView.invalidate();
    }

    @Override
    protected void onPostExecute(Boolean b) {
        super.onPostExecute(b);
        // TODO Clean.
        // mGameView.hideBombshell();
        mGameView.unregisterDrawer(mBombshell);
        Log.d(LOG_TAG, "onPostExecute");
    }

    // TODO : Refactor into specific Landscape collision detection method.
    public float getLandscapeHeightForX(float x) {
        int sliceIndex = (int) (x / (mViewWidth / mLandscape.getNumberOfLandscapeSlices()));
        return mViewHeight - mViewHeight * Landscape.MAX_HEIGHT_RATIO_FOR_LANDSCAPE * mLandscape.getLandscapeHeightPercentage(sliceIndex);
    }
}
