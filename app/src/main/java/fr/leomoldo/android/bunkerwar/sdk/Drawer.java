package fr.leomoldo.android.bunkerwar.sdk;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;

/**
 * Created by leomoldo on 23/05/2016.
 */
public abstract class Drawer {

    private Paint mPaint;
    private ViewCoordinates mViewCoordinates;

    public Drawer() {
        mPaint = getDefaultPaint();
    }

    public Drawer(Paint paint) {
        mPaint = paint;
    }

    public abstract void draw(Canvas canvas, @Nullable float viewWidth, float viewHeight);

    public abstract boolean isHitByBombshell(ViewCoordinates bombshellVC, float viewWidth, float viewHeight);

    public void setViewCoordinates(ViewCoordinates vc) {
        mViewCoordinates = vc;
    }

    public ViewCoordinates getViewCoordinates() {
        return mViewCoordinates;
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    public Paint getPaint() {
        return mPaint;
    }

    private Paint getDefaultPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        return paint;
    }

}
