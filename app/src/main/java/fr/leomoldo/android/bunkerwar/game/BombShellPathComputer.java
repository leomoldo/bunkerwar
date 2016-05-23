package fr.leomoldo.android.bunkerwar.game;

import fr.leomoldo.android.bunkerwar.sdk.ViewCoordinates;

public class BombShellPathComputer {

	// private final static float BOMBSHELL_WEIGHT = 50f;
	// private final static float GRAVITATIONAL_CONSTANT = 9.81f;
	private final static float GRAVITATION_FACTOR = 490.5f;
    private final static float MAX_INITIAL_SPEED = 30.0f;
    private final static float TIME_FACTOR = 0.001f;

	private Double mInitialSpeedX;
	private Double mInitialSpeedY;
	private ViewCoordinates mCurrentCoordinates;
	private Integer mTimeCounter = 0;

    public BombShellPathComputer(Integer initialSpeed, double fireAngleRadian, ViewCoordinates initialCoordinates) {
        mCurrentCoordinates = initialCoordinates.clone();
        mInitialSpeedX = initialSpeed*MAX_INITIAL_SPEED*Math.cos(fireAngleRadian) / 100;
		mInitialSpeedY = - initialSpeed*MAX_INITIAL_SPEED*Math.sin(fireAngleRadian) / 100;
	}

    public void incrementCoordinates() {
        mCurrentCoordinates.setX(mCurrentCoordinates.getX() + mInitialSpeedX.floatValue());
		mCurrentCoordinates.setY(mCurrentCoordinates.getY() + (float) (mInitialSpeedY + (double) (GRAVITATION_FACTOR * TIME_FACTOR * mTimeCounter)));
		mTimeCounter++;
	}

    public int getTimeCounter() {
        return mTimeCounter;
    }

    public ViewCoordinates getCurrentCoordinates() {
        return mCurrentCoordinates;
    }
}
