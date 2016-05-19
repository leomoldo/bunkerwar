package fr.leomoldo.android.bunkerwar;

/**
 * Created by leomoldo on 19/05/2016.
 */
public class ViewCoordinates {

    private Float mX;
    private Float mY;

    public ViewCoordinates(float x, float y) {
        mX = x;
        mY = y;
    }

    public Float getY() {
        return mY;
    }

    public void setY(float mY) {
        this.mY = mY;
    }

    public Float getX() {
        return mX;
    }

    public void setX(float mX) {
        this.mX = mX;
    }

    @Override
    public ViewCoordinates clone() {
        return new ViewCoordinates(getX(), getY());
    }
}
