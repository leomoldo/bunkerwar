package fr.leomoldo.android.bunkerwar.sdk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by leomoldo on 19/05/2016.
 */
public class ViewCoordinates implements Parcelable {

    private Float mX;
    private Float mY;

    public ViewCoordinates(float x, float y) {
        mX = x;
        mY = y;
    }

    protected ViewCoordinates(Parcel in) {
        mX = in.readFloat();
        mY = in.readFloat();
    }

    public static final Creator<ViewCoordinates> CREATOR = new Creator<ViewCoordinates>() {
        @Override
        public ViewCoordinates createFromParcel(Parcel in) {
            return new ViewCoordinates(in);
        }

        @Override
        public ViewCoordinates[] newArray(int size) {
            return new ViewCoordinates[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mX);
        dest.writeFloat(mY);
    }
}
