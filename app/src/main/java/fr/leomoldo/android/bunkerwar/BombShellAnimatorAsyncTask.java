package fr.leomoldo.android.bunkerwar;


import android.os.AsyncTask;
import android.util.Log;

import fr.leomoldo.android.bunkerwar.game.PhysicalModel;

/**
 * Created by leomoldo on 19/05/2016.
 */
public class BombShellAnimatorAsyncTask extends AsyncTask<PhysicalModel, ViewCoordinates, Boolean> {

    private static final String LOG_TAG = BombShellAnimatorAsyncTask.class.getSimpleName();

    private final static int ITERATION_WAITING_TIME = 10;

    private GameView mGameView;


    public BombShellAnimatorAsyncTask(GameView gameView) {
        mGameView = gameView;
    }

    @Override
    protected Boolean doInBackground(PhysicalModel... physicalModels) {

        Boolean shouldHalt = false;

        while (!shouldHalt) {

            physicalModels[0].incrementCoordinates();
            publishProgress(physicalModels[0].getCurrentCoordinates());

            /*
            Log.d(LOG_TAG, "timeCounter : " + physicalModels[0].getTimeCounter());
            Log.d(LOG_TAG, "currentBombShellX : " + physicalModels[0].getCurrentCoordinates().getX());
            Log.d(LOG_TAG, "currentBombShellY : " + physicalModels[0].getCurrentCoordinates().getY());
            */

            try {
                Thread.sleep(ITERATION_WAITING_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (physicalModels[0].getTimeCounter() > 100) {
                shouldHalt = true;
                // mGameView.hideBombShell();
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
        Log.d(LOG_TAG, "onPostExecute");
    }
}
