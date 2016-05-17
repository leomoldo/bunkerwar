package fr.leomoldo.android.bunkerwar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import fr.leomoldo.android.bunkerwar.game.Bunker;
import fr.leomoldo.android.bunkerwar.game.Landscape;

/**
 * Created by leomoldo on 16/05/2016.
 */
public class GameView extends View {

    private static final String LOG_TAG = GameView.class.getSimpleName();

    private final static float MAX_HEIGHT_RATIO_FOR_LANDSCAPE = 0.5f;

    private final static Double BUNKER_CANON_LENGTH = 30.0;
    public final static Float BUNKER_RADIUS = 17f;
    private final static Float BUNKER_STROKE_WIDTH = 10f;

    public final static Float BOMBSHELL_RADIUS = 5f;


    // Context.
    private Context mContext;

    // Model.
    private Landscape mLandscape;
    private Bunker mPlayerOneBunker;
    private Bunker mPlayerTwoBunker;

    private Float mBunkerPlayerOneX;
    private Float mBunkerPlayerOneY;
    private Float mBunkerPlayerTwoX;
    private Float mBunkerPlayerTwoY;

    private Float mBombShellX;
    private Float mBombShellY;

    // Paints.
    private Paint mLandscapePaint;
    private Paint mPlayerOneBunkerPaint;
    private Paint mPlayerTwoBunkerPaint;
    private Paint mBombShellPaint;


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

        initializePaints();
    }

    public void initializeNewGame(Landscape landscape, Bunker playerOneBunker, Bunker playerTwoBunker) {
        mLandscape = landscape;
        mPlayerOneBunker = playerOneBunker;
        mPlayerTwoBunker = playerTwoBunker;
    }

    public void showBombShell(float x, float y) {
        mBombShellX = x;
        mBombShellY = y;
    }

    public void hideBombShell() {
        mBombShellY = null;
        mBombShellY = null;
    }

    public float getBunkerPlayerOneX() {
        return mBunkerPlayerOneX;
    }

    public float getBunkerPlayerOneY() {
        return mBunkerPlayerOneY;
    }

    public float getBunkerPlayerTwoX() {
        return mBunkerPlayerTwoX;
    }

    public float getBunkerPlayerTwoY() {
        return mBunkerPlayerTwoY;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (canvas == null) return;

        if (mPlayerOneBunker != null) {
            drawPlayerOneBunker(canvas);
        }

        if (mPlayerTwoBunker != null) {
            drawPlayerTwoBunker(canvas);
        }

        if (mLandscape != null) {
            drawLandscape(canvas);
        }

        // TODO Draw BombShell if necessary (after Landscape).

        if (mBombShellX != null && mBombShellY != null) {
            drawBombShell(canvas, mBombShellX, mBombShellY);
        }
    }

    private void drawLandscape(Canvas canvas) {

        for (int i = 0; i < mLandscape.getNumberOfLandscapeSlices(); i++) {
            canvas.drawRect(i * getWidth() / mLandscape.getNumberOfLandscapeSlices(),
                    getHeight() - getHeight() * MAX_HEIGHT_RATIO_FOR_LANDSCAPE * mLandscape.getLandscapeHeightPercentage(i),
                    (i + 1) * getWidth() / mLandscape.getNumberOfLandscapeSlices(),
                    getHeight(),
                    mLandscapePaint);
        }
    }

    private void drawPlayerOneBunker(Canvas canvas) {
        if (mBunkerPlayerOneX == null) {
            setBunkerOneX();
        }
        if (mBunkerPlayerOneY == null) {
            setBunkerOneY();
        }
        drawBunker(canvas, mPlayerOneBunkerPaint, mBunkerPlayerOneX, mBunkerPlayerOneY, mPlayerOneBunker.getCanonAngleRadian(), true);
    }

    private void drawPlayerTwoBunker(Canvas canvas) {
        if (mBunkerPlayerTwoX == null) {
            setBunkerTwoX();
        }
        if (mBunkerPlayerTwoY == null) {
            setBunkerTwoY();
        }
        drawBunker(canvas, mPlayerTwoBunkerPaint, mBunkerPlayerTwoX, mBunkerPlayerTwoY, mPlayerTwoBunker.getCanonAngleRadian(), false);
    }

    private void drawBunker(Canvas canvas, Paint paint, float x, float y, double canonAngleRadian, boolean isCanonSetLeftToRight) {

        // Draw a circle and a rectangle for the bunker.
        canvas.drawCircle(x, y, BUNKER_RADIUS, paint);
        canvas.drawRect(x - BUNKER_RADIUS, y, x + BUNKER_RADIUS, getHeight(), paint);

        // Draw the canon of the bunker.
        float lengthX = (float) (BUNKER_CANON_LENGTH * Math.cos(canonAngleRadian));
        float lengthY = (float) (-BUNKER_CANON_LENGTH * Math.sin(canonAngleRadian));

        if (!isCanonSetLeftToRight) {
            lengthX = -lengthX;
        }

        canvas.drawLine(x, y, x + lengthX, y + lengthY, paint);
    }

    private void drawBombShell(Canvas canvas, float x, float y) {
        Log.d(LOG_TAG, "Drawing BombShell");
        canvas.drawCircle(x, y, BOMBSHELL_RADIUS, mBombShellPaint);
    }

    private void initializePaints() {

        // Landscape.
        mLandscapePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLandscapePaint.setColor(mContext.getResources().getColor(R.color.green_land_slice));

        // Bunker One.
        mPlayerOneBunkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPlayerOneBunkerPaint.setColor(Color.RED);
        mPlayerOneBunkerPaint.setStyle(Paint.Style.FILL);
        mPlayerOneBunkerPaint.setStrokeCap(Paint.Cap.BUTT);
        mPlayerOneBunkerPaint.setStrokeWidth(BUNKER_STROKE_WIDTH);

        // Bunker Two.
        mPlayerTwoBunkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPlayerTwoBunkerPaint.setColor(Color.YELLOW);
        mPlayerTwoBunkerPaint.setStyle(Paint.Style.FILL);
        mPlayerTwoBunkerPaint.setStrokeCap(Paint.Cap.BUTT);
        mPlayerTwoBunkerPaint.setStrokeWidth(BUNKER_STROKE_WIDTH);

        // Bombshell.
        mBombShellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        /*if (mBunker.isPlayerOne()) {
            mBombShellPaint.setColor(Color.RED);
		} else {
			mBombShellPaint.setColor(Color.YELLOW);
		}*/
        mBombShellPaint.setColor(Color.BLACK); // TODO Debug only.
        mBombShellPaint.setStyle(Paint.Style.FILL);
    }


    // TODO Make the Landscape class fully handle Bunker Position logic.
    private void setBunkerOneX() {
        float landSliceWidth = getWidth() / mLandscape.getNumberOfLandscapeSlices();
        mBunkerPlayerOneX = Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER * landSliceWidth;
    }

    private void setBunkerOneY() {
        mBunkerPlayerOneY = getHeight()
                - getHeight() * MAX_HEIGHT_RATIO_FOR_LANDSCAPE * mLandscape.getLandscapeHeightPercentage(Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER)
                - BUNKER_RADIUS;
    }

    private void setBunkerTwoX() {
        float landSliceWidth = getWidth() / mLandscape.getNumberOfLandscapeSlices();
        mBunkerPlayerTwoX = getWidth() - landSliceWidth * Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER;
    }

    private void setBunkerTwoY() {
        mBunkerPlayerTwoY = getHeight()
                - getHeight() * MAX_HEIGHT_RATIO_FOR_LANDSCAPE * mLandscape.getLandscapeHeightPercentage(mLandscape.getNumberOfLandscapeSlices() - 1 - Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER)
                - BUNKER_RADIUS;
    }
}
