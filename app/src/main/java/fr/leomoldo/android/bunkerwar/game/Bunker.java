package fr.leomoldo.android.bunkerwar.game;

public class Bunker {

	private Boolean mIsPlayerOne;	
	
	private Integer mCanonAngle; // Integer between 0 and 90.
	private Integer mCanonPower; // Integer between 0 and 100.
	
	
	public Bunker(Boolean isPlayerOne) {
		mIsPlayerOne = isPlayerOne;
		mCanonPower = 50;
		mCanonAngle = 45;
	}

	public Integer getCanonAngleDegrees() {
		return mCanonAngle;
	}
	
	public double getCanonAngleRadian() {
		double angle = (double) mCanonAngle;
		return angle * Math.PI / 180.0;
	}

	public void setCanonAngle(Integer mCanonAngle) {
		this.mCanonAngle = mCanonAngle;
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
