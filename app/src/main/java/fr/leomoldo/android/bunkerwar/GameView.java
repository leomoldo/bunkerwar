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

    private ViewCoordinates mBunkerPlayerOneCoordinates;
    private ViewCoordinates mBunkerPlayerTwoCoordinates;
    private ViewCoordinates mBombShellCoordinates;

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
        mBunkerPlayerOneCoordinates = null;
        mBunkerPlayerTwoCoordinates = null;
        mLandscape = landscape;
        mPlayerOneBunker = playerOneBunker;
        mPlayerTwoBunker = playerTwoBunker;
        hideBombShell();
    }

    public void showBombShell(ViewCoordinates coordinates) {
        if (mBombShellCoordinates == null) {
            mBombShellCoordinates = new ViewCoordinates(coordinates.getX(), coordinates.getY());
        } else {
            mBombShellCoordinates.setX(coordinates.getX());
            mBombShellCoordinates.setY(coordinates.getY());
        }
    }

    public void hideBombShell() {
        mBombShellCoordinates = null;
    }

    public ViewCoordinates getBunkerPlayerOneCoordinates() {
        return mBunkerPlayerOneCoordinates;
    }

    public ViewCoordinates getBunkerPlayerTwoCoordinates() {
        return mBunkerPlayerTwoCoordinates;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) return;
        if (mBombShellCoordinates != null) {
            drawBombShell(canvas, mBombShellCoordinates);
        }
        if (mPlayerOneBunker != null) {
            drawPlayerOneBunker(canvas);
        }
        if (mPlayerTwoBunker != null) {
            drawPlayerTwoBunker(canvas);
        }
        if (mLandscape != null) {
            drawLandscape(canvas);
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
        if (mBunkerPlayerOneCoordinates == null) {
            setBunkerOneCoordinates();
        }
        drawBunker(canvas, mPlayerOneBunkerPaint, mBunkerPlayerOneCoordinates, mPlayerOneBunker.getCanonAngleRadian(), true);
    }

    private void drawPlayerTwoBunker(Canvas canvas) {
        if (mBunkerPlayerTwoCoordinates == null) {
            setBunkerTwoCoordinates();
        }
        drawBunker(canvas, mPlayerTwoBunkerPaint, mBunkerPlayerTwoCoordinates, mPlayerTwoBunker.getCanonAngleRadian(), false);
    }

    private void drawBunker(Canvas canvas, Paint paint, ViewCoordinates coordinates, double canonAngleRadian, boolean isCanonSetLeftToRight) {

        // Draw a circle and a rectangle for the bunker.
        canvas.drawCircle(coordinates.getX(), coordinates.getY(), BUNKER_RADIUS, paint);
        canvas.drawRect(coordinates.getX() - BUNKER_RADIUS, coordinates.getY(), coordinates.getX() + BUNKER_RADIUS, getHeight(), paint);

        // Draw the canon of the bunker.
        float lengthX = (float) (BUNKER_CANON_LENGTH * Math.cos(canonAngleRadian));
        float lengthY = (float) (-BUNKER_CANON_LENGTH * Math.sin(canonAngleRadian));

        if (!isCanonSetLeftToRight) {
            lengthX = -lengthX;
        }

        canvas.drawLine(coordinates.getX(), coordinates.getY(), coordinates.getX() + lengthX, coordinates.getY() + lengthY, paint);
    }

    private void drawBombShell(Canvas canvas, ViewCoordinates coordinates) {
        Log.d(LOG_TAG, "Drawing BombShell");
        canvas.drawCircle(coordinates.getX(), coordinates.getY(), BOMBSHELL_RADIUS, mBombShellPaint);
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
        mBombShellPaint.setColor(Color.BLACK);
        mBombShellPaint.setStyle(Paint.Style.FILL);
    }

    // TODO Make the Landscape class fully handle Bunker Position logic?

    private void setBunkerOneCoordinates() {
        float landSliceWidth = getWidth() / mLandscape.getNumberOfLandscapeSlices();
        mBunkerPlayerOneCoordinates = new ViewCoordinates(Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER * landSliceWidth,
                getHeight()
                        - getHeight() * MAX_HEIGHT_RATIO_FOR_LANDSCAPE * mLandscape.getLandscapeHeightPercentage(Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER)
                        - BUNKER_RADIUS);
    }

    private void setBunkerTwoCoordinates() {
        float landSliceWidth = getWidth() / mLandscape.getNumberOfLandscapeSlices();
        mBunkerPlayerTwoCoordinates = new ViewCoordinates(getWidth() - landSliceWidth * Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER,
                getHeight()
                        - getHeight() * MAX_HEIGHT_RATIO_FOR_LANDSCAPE * mLandscape.getLandscapeHeightPercentage(mLandscape.getNumberOfLandscapeSlices() - 1 - Landscape.BUNKER_POSITION_FROM_SCREEN_BORDER)
                        - BUNKER_RADIUS);
    }
}
