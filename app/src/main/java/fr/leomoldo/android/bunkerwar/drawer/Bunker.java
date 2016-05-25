package fr.leomoldo.android.bunkerwar.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;

import fr.leomoldo.android.bunkerwar.sdk.Drawer;
import fr.leomoldo.android.bunkerwar.sdk.ViewCoordinates;

public class Bunker extends Drawer {

	private final static Double BUNKER_CANON_LENGTH = 30.0;
	public final static Float BUNKER_RADIUS = 17f;
	private final static Float BUNKER_STROKE_WIDTH = 10f;

    private final static float BUNKER_HITBOX_EXPANSION_RATIO = 1.5f;

	private Boolean mIsPlayerOne;

	private Integer mAsboluteCanonAngle; // Integer between 0 and 90.
	private Integer mCanonPower; // Integer between 0 and 100.


	public Bunker(Boolean isPlayerOne, int color, ViewCoordinates vc) {
		mIsPlayerOne = isPlayerOne;
		mCanonPower = 50;
		mAsboluteCanonAngle = 45;

		setViewCoordinates(vc);

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeCap(Paint.Cap.BUTT);
		paint.setStrokeWidth(BUNKER_STROKE_WIDTH);
		setPaint(paint);
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

	@Override
	public void draw(Canvas canvas, @Nullable int viewWidth, int viewHeight) {

		// Draw a circle and a rectangle for the bunker.
		canvas.drawCircle(getViewCoordinates().getX(), getViewCoordinates().getY(), BUNKER_RADIUS, getPaint());
		canvas.drawRect(getViewCoordinates().getX() - BUNKER_RADIUS, getViewCoordinates().getY(), getViewCoordinates().getX() + BUNKER_RADIUS, viewHeight, getPaint());

		// Draw the canon of the bunker.
		float lengthX = (float) (BUNKER_CANON_LENGTH * Math.cos(getGeometricalCanonAngleRadian()));
		float lengthY = (float) (-BUNKER_CANON_LENGTH * Math.sin(getGeometricalCanonAngleRadian()));
		canvas.drawLine(getViewCoordinates().getX(), getViewCoordinates().getY(), getViewCoordinates().getX() + lengthX, getViewCoordinates().getY() + lengthY, getPaint());
	}

    @Override
    public boolean isHitByBombshell(ViewCoordinates bombshellVC, int viewWidth, int viewHeight) {

        double distance = Math.sqrt(
                Math.pow(bombshellVC.getX() - getViewCoordinates().getX(), 2) +
                        Math.pow(bombshellVC.getY() - getViewCoordinates().getY(), 2)
        );
        return distance < BUNKER_RADIUS * BUNKER_HITBOX_EXPANSION_RATIO;
    }
}
