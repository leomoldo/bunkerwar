package fr.leomoldo.android.bunkerwar.sdk;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;

/**
 * Created by leomoldo on 23/05/2016.
 */
public abstract class GameView extends View {

    // Not needed?
    // protected Context mContext;

    private HashMap<Drawer, ViewCoordinates> mRegisteredDrawers;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Not needed?
        // mContext = context;
        this.setWillNotDraw(false);
    }

    public int registerDrawer(Drawer drawer, @Nullable ViewCoordinates vc) {

        if (mRegisteredDrawers == null) {
            mRegisteredDrawers = new HashMap<Drawer, ViewCoordinates>();
        }

        mRegisteredDrawers.put(drawer, vc);

        return 0;
    }

    public int unregisterDrawer(Drawer drawer) {

        if (mRegisteredDrawers == null) {
            return 0;
        }

        if (mRegisteredDrawers.remove(drawer) == null) {
            return 0;
        } else {
            return 0;
        }
    }

    public int moveDrawer(Drawer drawer, ViewCoordinates vc) {

        if (!mRegisteredDrawers.containsKey(drawer)) {
            return 0;
        }

        mRegisteredDrawers.put(drawer, vc);

        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) return;

        for (Drawer drawer : mRegisteredDrawers.keySet()) {
            drawer.draw(canvas, mRegisteredDrawers.get(drawer), getWidth(), getHeight());
        }
    }

}