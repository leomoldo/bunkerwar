package fr.leomoldo.android.bunkerwar.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;

import fr.leomoldo.android.bunkerwar.sdk.Drawer;
import fr.leomoldo.android.bunkerwar.sdk.ViewCoordinates;

/**
 * Created by leomoldo on 23/05/2016.
 */
public class Bombshell extends Drawer {

    public final static Float BOMBSHELL_RADIUS = 5f;

    public Bombshell(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        setPaint(paint);
    }

    @Override
    public void draw(Canvas canvas, @Nullable int viewWidth, int viewHeight) {
        canvas.drawCircle(getViewCoordinates().getX(), getViewCoordinates().getY(), BOMBSHELL_RADIUS, getPaint());
    }

    @Override
    public boolean isHitByBombshell(ViewCoordinates bombshellVC, int viewWidth, int viewHeight) {
        double distance = Math.sqrt(
                Math.pow(bombshellVC.getX() - getViewCoordinates().getX(), 2) +
                        Math.pow(bombshellVC.getY() - getViewCoordinates().getY(), 2)
        );
        return distance < 2 * BOMBSHELL_RADIUS;
    }
}
