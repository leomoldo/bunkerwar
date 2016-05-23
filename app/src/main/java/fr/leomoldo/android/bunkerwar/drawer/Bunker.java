package fr.leomoldo.android.bunkerwar.drawer;

public class Bunker {

	private Boolean mIsPlayerOne;

	private Integer mAsboluteCanonAngle; // Integer between 0 and 90.
	private Integer mCanonPower; // Integer between 0 and 100.
	
	
	public Bunker(Boolean isPlayerOne) {
		mIsPlayerOne = isPlayerOne;
		mCanonPower = 50;
		mAsboluteCanonAngle = 45;
	}

	public Integer getAbsoluteCanonAngleDegrees() {
		return mAsboluteCanonAngle;
	}

	public double getGeometricalCanonAngleRadian() {
		double angle = ((double) mAsboluteCanonAngle) * Math.PI / 180.0;
		if (!mIsPlayerOne) {
			angle = Math.PI - angle;
		}
		return angle;

		// TODO Clean old code :
		/*
		double angle = (double) mAsboluteCanonAngle;
		return angle * Math.PI / 180.0;
		*/
	}

	public void setAbsoluteCanonAngle(Integer mCanonAngle) {
		this.mAsboluteCanonAngle = mCanonAngle;
	}
	
	public Integer getCanonPower() {
		return mCanonPower;
	}

	public void setCanonPower(Integer mCanonPower) {
		this.mCanonPower = mCanonPower;
	}

	public Boolean isPlayerOne() {
		return mIsPlayerOne;
	}
	
	
	
}
