package fr.leomoldo.android.bunkerwar.game;

public class PhysicalModel {

	// private final static float BOMBSHELL_WEIGHT = 50f;
	// private final static float GRAVITATIONAL_CONSTANT = 9.81f;
	private final static float GRAVITATION_FACTOR = 490.5f;

	private final static float MAX_INITIAL_SPEED = 30.0f; // TODO : tweak!

	private final static float TIME_FACTOR = 1; // TODO : tweak!
	// TODO Regrouper Time Factor et Gravition Factor en un seul parametre?
	
	
	private Double mInitialSpeedX;
	private Double mInitialSpeedY;
	
	
	public PhysicalModel(Integer initialSpeed, double fireAngleRadian, Boolean isPlayerOnePlaying) {
		mInitialSpeedX = initialSpeed*MAX_INITIAL_SPEED*Math.cos(fireAngleRadian) / 100;
		if (!isPlayerOnePlaying) {
			mInitialSpeedX = - mInitialSpeedX;
		}
		mInitialSpeedY = - initialSpeed*MAX_INITIAL_SPEED*Math.sin(fireAngleRadian) / 100;
	}
	
	public Float getNextXOffset() {
		return mInitialSpeedX.floatValue();
	}
	
	public Float getNextYOffset(Integer timeCounter) {
		return (float) (mInitialSpeedY + (double) (GRAVITATION_FACTOR*TIME_FACTOR*timeCounter));
	}

}
