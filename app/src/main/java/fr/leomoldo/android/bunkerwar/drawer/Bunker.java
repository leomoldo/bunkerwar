package fr.leomoldo.android.bunkerwar.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import fr.leomoldo.android.bunkerwar.sdk.Drawer;
import fr.leomoldo.android.bunkerwar.sdk.ViewCoordinates;

public class Bunker extends Drawer implements Parcelable {

    public final static float BUNKER_RADIUS = 17f;
    public final static float BUNKER_HEIGHT = 30f;
    public final static float BUNKER_CANON_LENGTH = 30f;
    private final static float BUNKER_STROKE_WIDTH = 10f;
    private final static float BUNKER_HITBOX_EXPANSION_RATIO = 1.5f;
    private final static float POWER_INDICATOR_DOTS_DISTANCE = 20f;

    private Boolean mIsPlayerOne;
    private Boolean mIsPlaying;

    private Integer mAsboluteCanonAngle; // Integer between 0 and 90.
    private Integer mCanonPower; // Integer between 0 and 100.
    private int mColor;

    public Bunker(Boolean isPlayerOne, int color, ViewCoordinates vc) {
        mIsPlayerOne = isPlayerOne;
        mIsPlaying = false;
        mCanonPower = 50;
        mAsboluteCanonAngle = 45;
        setViewCoordinates(vc);
        mColor = color;
        initializePaint();
    }

    protected Bunker(Parcel in) {
        mIsPlayerOne = in.readInt() >= 1;
        mIsPlaying = in.readInt() >= 1;
        mAsboluteCanonAngle = in.readInt();
        mCanonPower = in.readInt();
        mColor = in.readInt();
        ViewCoordinates vc = in.readParcelable(getClass().getClassLoader());
        setViewCoordinates(vc);
        initializePaint();
    }

    public static final Creator<Bunker> CREATOR = new Creator<Bunker>() {
        @Override
        public Bunker createFromParcel(Parcel in) {
            return new Bunker(in);
        }

        @Override
        public Bunker[] newArray(int size) {
            return new Bunker[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mIsPlayerOne ? 1 : 0);
        dest.writeInt(mIsPlaying ? 1 : 0);
        dest.writeInt(mAsboluteCanonAngle);
        dest.writeInt(mCanonPower);
        dest.writeInt(mColor);
        dest.writeParcelable(getViewCoordinates(), 0);
    }

    protected void initializePaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeWidth(BUNKER_STROKE_WIDTH);
        setPaint(paint);
    }

    public void setIsPlaying(boolean isPlaying) {
        mIsPlaying = isPlaying;
    }

    public int getAbsoluteCanonAngleDegrees() {
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

    public int getCanonPower() {
        return mCanonPower;
    }

    public void setCanonPower(Integer mCanonPower) {
        this.mCanonPower = mCanonPower;
    }

    public boolean isPlayerOne() {
        return mIsPlayerOne;
    }

    public float getCanonLengthX() {
        return (float) (BUNKER_CANON_LENGTH * Math.cos(getGeometricalCanonAngleRadian()));
    }

    public float getCanonLengthY() {
        return (float) (-BUNKER_CANON_LENGTH * Math.sin(getGeometricalCanonAngleRadian()));
    }

    @Override
    public void draw(Canvas canvas, @Nullable float viewWidth, float viewHeight) {
        // Draw a circle and a rectangle for the bunker.
        canvas.drawCircle(getViewCoordinates().getX(), getViewCoordinates().getY(), BUNKER_RADIUS, getPaint());
        canvas.drawRect(getViewCoordinates().getX() - BUNKER_RADIUS, getViewCoordinates().getY(), getViewCoordinates().getX() + BUNKER_RADIUS, viewHeight, getPaint());
        // Draw the canon of the bunker.
        canvas.drawLine(getViewCoordinates().getX(), getViewCoordinates().getY(), getViewCoordinates().getX() + getCanonLengthX(), getViewCoordinates().getY() + getCanonLengthY(), getPaint());
        // Draw power indicator.
        if (mIsPlaying) {
            float powerIndicatorDotX;
            float powerIndicatorDotY;
            for (int i = 1; i <= mCanonPower / 10; i++) {
                powerIndicatorDotX = (float) ((BUNKER_CANON_LENGTH + i * POWER_INDICATOR_DOTS_DISTANCE) * Math.cos(getGeometricalCanonAngleRadian()));
                powerIndicatorDotY = (float) ((-BUNKER_CANON_LENGTH - +i * POWER_INDICATOR_DOTS_DISTANCE) * Math.sin(getGeometricalCanonAngleRadian()));
                canvas.drawPoint(getViewCoordinates().getX() + powerIndicatorDotX, getViewCoordinates().getY() + powerIndicatorDotY, getPaint());
            }
        }
    }

    @Override
    public boolean isHitByBombshell(ViewCoordinates bombshellVC, float viewWidth, float viewHeight) {
        double distance = Math.sqrt(
                Math.pow(bombshellVC.getX() - getViewCoordinates().getX(), 2) +
                        Math.pow(bombshellVC.getY() - getViewCoordinates().getY(), 2)
        );
        return distance < BUNKER_RADIUS * BUNKER_HITBOX_EXPANSION_RATIO;
    }
}
