package fr.leomoldo.android.bunkerwar.sdk;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by leomoldo on 23/05/2016.
 */
public class GameView extends View {

    private ArrayList<Drawer> mRegisteredDrawers;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mRegisteredDrawers = new ArrayList<>();
        this.setWillNotDraw(false);
    }

    // TODO Add a way to change drawing order?
    // TODO Remove int returned value (no error codes needed?) --> See ArrayList doc.
    public int registerDrawer(Drawer drawer) {
        mRegisteredDrawers.add(drawer);
        return 0;
    }

    public int registerDrawer(Drawer drawer, int index) {
        mRegisteredDrawers.add(index, drawer);
        return 0;
    }

    public int unregisterDrawer(Drawer drawer) {

        if (mRegisteredDrawers == null) {
            return 0;
        }

        if (mRegisteredDrawers.remove(drawer)) {
            return 0;
        } else {
            return 0;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) return;

        for (Drawer drawer : mRegisteredDrawers) {
            drawer.draw(canvas, getWidth(), getHeight());
        }
    }

}