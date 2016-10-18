package fr.leomoldo.android.bunkerwar;


import android.graphics.Color;
import android.os.AsyncTask;

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

    public final static int MAX_GAME_SPEED = 100;
    public final static int DEFAULT_GAME_SPEED = MAX_GAME_SPEED;
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
        mGameView.registerDrawer(mBombshell, 0);
    }

    @Override
    protected Drawer doInBackground(BombshellPathComputer... bombshellPathComputers) {
        while (true) {
            if (isCancelled()) {
                return null;
            }
            bombshellPathComputers[0].incrementCoordinates();
            publishProgress(bombshellPathComputers[0].getCurrentCoordinates());
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
            // Check if bombshell did leave the screen from the right of left hand side (+ bottom but is it really useful?).
            if (bombshellPathComputers[0].getCurrentCoordinates().getY() > mViewHeight ||
                    bombshellPathComputers[0].getCurrentCoordinates().getX() > mViewWidth ||
                    bombshellPathComputers[0].getCurrentCoordinates().getX() < 0) {
                return null;
            }
        }
    }

    @Override
    protected void onProgressUpdate(ViewCoordinates... values) {
        super.onProgressUpdate(values);
        mBombshell.setViewCoordinates(values[0]);
        mGameView.invalidate();
    }

    @Override
    protected void onPostExecute(Drawer drawer) {
        super.onPostExecute(drawer);
        mGameView.unregisterDrawer(mBombshell);
        mGameView.invalidate();
        mCollisionListener.onDrawerHit(drawer);
    }

    @Override
    protected void onCancelled(Drawer drawer) {
        mGameView.unregisterDrawer(mBombshell);
        super.onCancelled(drawer);
    }

    private int getIterationTimeFromGameSpeed(int gameSpeed) {
        float percentage = 1 - ((float) gameSpeed) / 100f;
        int iterationTime = (int) ((MAX_ITERATION_TIME - 1) * percentage);
        iterationTime++; // Iteration time must be at least 1 ms.
        return iterationTime;
    }

}
