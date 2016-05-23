package fr.leomoldo.android.bunkerwar.sdk;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;

/**
 * Created by leomoldo on 23/05/2016.
 */
public abstract class Drawer {

    // TODO : Make it a simple interface and add a Paint parameter to "draw" method + mPaint getter ???

    private Paint mPaint;

    public Drawer() {
        mPaint = getDefaultPaint();
    }

    public Drawer(Paint paint) {
        mPaint = paint;
    }

    public abstract void draw(Canvas canvas, @Nullable ViewCoordinates vc, @Nullable int viewWidth, int viewHeight);

    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    private Paint getDefaultPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        return paint;
    }

}
