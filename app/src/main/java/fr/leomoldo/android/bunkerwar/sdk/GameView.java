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

    public void registerDrawer(Drawer drawer) {
        mRegisteredDrawers.add(drawer);
    }

    public void registerDrawer(Drawer drawer, int index) {
        mRegisteredDrawers.add(index, drawer);
    }

    public void unregisterDrawer(Drawer drawer) {
        mRegisteredDrawers.remove(drawer);
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