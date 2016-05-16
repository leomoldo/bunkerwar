package fr.leomoldo.android.bunkerwar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import fr.leomoldo.android.bunkerwar.game.Landscape;

/**
 * Created by leomoldo on 16/05/2016.
 */
public class GameView extends View {

    private final static float MAX_HEIGHT_RATIO_FOR_LANDSCAPE = 0.5f;

    private Context mContext;

    private Landscape mLandscape;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        this.setWillNotDraw(false);
    }

    public void setLandscape(Landscape landscape) {
        mLandscape = landscape;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (canvas == null) return;

        if (mLandscape != null) {
            drawLandscape(canvas);
        }

    }

    private void drawLandscape(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mContext.getResources().getColor(R.color.green_land_slice));

        // TODO Debug only :
        // canvas.drawRect(10f, 10f, (float) getWidth() - 10f, (float) getHeight() - 10f, paint);

        /*
        Float landSliceHeight = 0f;
        Float percentage = 0f;
        Integer viewHeight = 0;
        */

        for (int i = 0; i < mLandscape.getNumberOfLandscapeSlices(); i++) {

            canvas.drawRect(i * getWidth() / mLandscape.getNumberOfLandscapeSlices(),
                    getHeight() - getHeight() * MAX_HEIGHT_RATIO_FOR_LANDSCAPE * mLandscape.getLandscapeHeightPercentage(i),
                    (i + 1) * getWidth() / mLandscape.getNumberOfLandscapeSlices(),
                    getHeight(),
                    paint);

            /*
            landSliceHeight = (float) mLandscape.getLandscapeHeight(i);
            percentage = landSliceHeight / 100f;
            viewHeight = (int) ( mMainRelativeLayoutHeight * MAX_HEIGHT_RATIO_FOR_LANDSCAPE * percentage );

            FrameLayout frame = new FrameLayout(GameActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, viewHeight);
            params.weight = 1;
            params.gravity = Gravity.BOTTOM;
            frame.setLayoutParams(params);
            frame.setBackgroundColor(getResources().getColor(R.color.green_land_slice));

            mLandscapeLinearLayout.addView(frame);

            mLandscapeFrameLayouts.add(i, frame);
            mLandscapeFrameLayoutHeights.add(i, (float) viewHeight);
            */
        }
    }
}
