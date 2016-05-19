package fr.leomoldo.android.bunkerwar;


import android.os.AsyncTask;
import android.util.Log;

import fr.leomoldo.android.bunkerwar.game.BombShellPathComputer;

/**
 * Created by leomoldo on 19/05/2016.
 */
public class BombShellAnimatorAsyncTask extends AsyncTask<BombShellPathComputer, ViewCoordinates, Boolean> {

    private static final String LOG_TAG = BombShellAnimatorAsyncTask.class.getSimpleName();

    private final static int ITERATION_WAITING_TIME = 10;

    private GameView mGameView;

    public BombShellAnimatorAsyncTask(GameView gameView) {
        mGameView = gameView;
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
            if (bombShellPathComputers[0].getTimeCounter() > 100) {
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
}
