package fr.leomoldo.android.bunkerwar;


import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import fr.leomoldo.android.bunkerwar.drawer.Bombshell;
import fr.leomoldo.android.bunkerwar.sdk.Drawer;
import fr.leomoldo.android.bunkerwar.sdk.GameView;
import fr.leomoldo.android.bunkerwar.sdk.ViewCoordinates;

/**
 * Created by leomoldo on 19/05/2016.
 */
public class BombshellAnimatorAsyncTask extends AsyncTask<BombshellPathComputer, ViewCoordinates, Drawer> {

    public interface CollisionListener {

        void onDrawerHit(Drawer drawer);

    }

    private static final String LOG_TAG = BombshellAnimatorAsyncTask.class.getSimpleName();

    // TODO Clean.
    // private final static int ITERATION_WAITING_TIME = 10;
    public final static int MAX_GAME_SPEED = 100;
    private final static int MAX_ITERATION_TIME = 51;

    private GameView mGameView;
    private int mViewHeight;
    private int mViewWidth;
    private Bombshell mBombshell;
    private ArrayList<Drawer> mCollidableDrawers;
    private CollisionListener mCollisionListener;
    private int mIterationWaitingTime;

    public BombshellAnimatorAsyncTask(GameView gameView, ArrayList<Drawer> collidableDrawers, CollisionListener collisionListener, int gameSpeed) {
        mGameView = gameView;
        mViewHeight = mGameView.getHeight();
        mViewWidth = mGameView.getWidth();
        mCollidableDrawers = collidableDrawers;
        mCollisionListener = collisionListener;
        mIterationWaitingTime = getIterationTimeFromGameSpeed(gameSpeed);

        mBombshell = new Bombshell(Color.BLACK);
        // mBombshell.setViewCoordinates(new ViewCoordinates(-2*Bombshell.BOMBSHELL_RADIUS, -2*Bombshell.BOMBSHELL_RADIUS));
        mGameView.registerDrawer(mBombshell, 0);
        // mGameView.invalidate();

        /*
        Log.d(LOG_TAG, "View Width : " + mViewWidth);
        Log.d(LOG_TAG, "View Height : " + mViewHeight);
        */
    }

    @Override
    protected Drawer doInBackground(BombshellPathComputer... bombshellPathComputers) {
        while (true) {

            if (isCancelled()) {
                return null;
            }

            bombshellPathComputers[0].incrementCoordinates();
            publishProgress(bombshellPathComputers[0].getCurrentCoordinates());

            /*
            Log.d(LOG_TAG, "timeCounter : " + bombshellPathComputers[0].getTimeCounter());
            Log.d(LOG_TAG, "currentBombshellX : " + bombshellPathComputers[0].getCurrentCoordinates().getX());
            Log.d(LOG_TAG, "currentBombshellY : " + bombshellPathComputers[0].getCurrentCoordinates().getY());
            */

            try {
                Thread.sleep(mIterationWaitingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Check if a collidable drawer was hit.
            for (Drawer drawer : mCollidableDrawers) {
                if (drawer.isHitByBombshell(bombshellPathComputers[0].getCurrentCoordinates(), mViewWidth, mViewHeight)) {
                    return drawer;
                }
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

                return null;
            }
        }
    }

    @Override
    protected void onProgressUpdate(ViewCoordinates... values) {
        super.onProgressUpdate(values);
        // Log.d(LOG_TAG, "onProgressUpdate.");
        mBombshell.setViewCoordinates(values[0]);
        mGameView.invalidate();
    }

    @Override
    protected void onPostExecute(Drawer drawer) {
        super.onPostExecute(drawer);
        // Log.d(LOG_TAG, "onPostExecute");
        mGameView.unregisterDrawer(mBombshell);
        mGameView.invalidate();
        mCollisionListener.onDrawerHit(drawer);
    }

    /*
    @Override
    protected void onCancelled(Drawer drawer) {
        super.onCancelled(drawer);
        mGameView.unregisterDrawer(mBombshell);
        mGameView.invalidate();
        mCollisionListener.onDrawerHit(drawer);
    }
    */

    // TODO Good candidate for a unit test.
    private int getIterationTimeFromGameSpeed(int gameSpeed) {
        float percentage = 1 - ((float) gameSpeed) / 100f;
        int iterationTime = (int) ((MAX_ITERATION_TIME - 1) * percentage);
        iterationTime++; // Iteration time must be at least 1 ms.
        return iterationTime;
    }

}
