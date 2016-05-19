package fr.leomoldo.android.bunkerwar.game;

import fr.leomoldo.android.bunkerwar.ViewCoordinates;

// TODO : Cette classe ne fait que gérer la ballistique pour un tri de BombShell --> à renommer!
public class PhysicalModel {

	// private final static float BOMBSHELL_WEIGHT = 50f;
	// private final static float GRAVITATIONAL_CONSTANT = 9.81f;
	private final static float GRAVITATION_FACTOR = 490.5f;

	private final static float MAX_INITIAL_SPEED = 30.0f; // TODO : tweak!

	private final static float TIME_FACTOR = 0.001f; // TODO : tweak!
	// TODO Regrouper Time Factor et Gravitation Factor en un seul parametre?
	
	
	private Double mInitialSpeedX;
	private Double mInitialSpeedY;
	private ViewCoordinates mCurrentCoordinates;
	private Integer mTimeCounter = 0;


	public PhysicalModel(Integer initialSpeed, double fireAngleRadian, ViewCoordinates initialCoordinates, Boolean isPlayerOnePlaying) {
		mCurrentCoordinates = initialCoordinates;
		mInitialSpeedX = initialSpeed*MAX_INITIAL_SPEED*Math.cos(fireAngleRadian) / 100;
		if (!isPlayerOnePlaying) {
			mInitialSpeedX = - mInitialSpeedX;
		}
		mInitialSpeedY = - initialSpeed*MAX_INITIAL_SPEED*Math.sin(fireAngleRadian) / 100;
	}


	public ViewCoordinates getNextCoordinates() {

		mCurrentCoordinates.setX(mCurrentCoordinates.getX() + mInitialSpeedX.floatValue());
		mCurrentCoordinates.setY(mCurrentCoordinates.getY() + (float) (mInitialSpeedY + (double) (GRAVITATION_FACTOR * TIME_FACTOR * mTimeCounter)));
		mTimeCounter++;
		return mCurrentCoordinates;
	}

	// TODO Clean :

	/*
	public Float getNextXOffset() {
        // TODO Debug only.
		return 1f;
		// return mInitialSpeedX.floatValue();
    }
	
	public Float getNextYOffset(Integer timeCounter) {
        // TODO Debug only.
		return -1f;
		// return (float) (mInitialSpeedY + (double) (GRAVITATION_FACTOR*TIME_FACTOR*timeCounter));
    }
    */

}
