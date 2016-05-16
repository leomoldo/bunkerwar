package fr.leomoldo.android.bunkerwar;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by leomoldo on 16/05/2016.
 */
public class GameView extends View {

    public GameView(Context context) {
        super(context, null, 0);

        this.setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (canvas == null) return;
    }
}
