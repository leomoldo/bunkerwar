package fr.leomoldo.android.bunkerwar;


import android.os.AsyncTask;

import fr.leomoldo.android.bunkerwar.game.PhysicalModel;

/**
 * Created by leomoldo on 19/05/2016.
 */
public class BombShellAnimatorAsyncTask extends AsyncTask<PhysicalModel, ViewCoordinates, Boolean> {

    @Override
    protected Boolean doInBackground(PhysicalModel... physicalModels) {
        return false;
    }

    @Override
    protected void onProgressUpdate(ViewCoordinates... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean b) {
        super.onPostExecute(b);
    }


    public void run() {

        Boolean shouldHalt = false;
        Integer timeCounter = 0;

        while (!shouldHalt) {

            /*
            Log.d(LOG_TAG, "timeCounter : " + timeCounter);
            Log.d(LOG_TAG, "currentBombShellX : " + currentBombShellX);
            Log.d(LOG_TAG, "currentBombShellY : " + currentBombShellY);
            */

            /*
            currentBombShellX += physicalModel.getNextXOffset();
            currentBombShellY += physicalModel.getNextYOffset(timeCounter);

            mGameView.showBombShell(currentBombShellX, currentBombShellY);
            mGameView.invalidate();
            */

            timeCounter++;

            if (timeCounter > 100) {
                shouldHalt = true;
                // mGameView.hideBombShell();
            }
        }
    }
}
