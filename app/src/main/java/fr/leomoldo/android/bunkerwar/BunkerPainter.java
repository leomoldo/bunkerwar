package fr.leomoldo.android.bunkerwar;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Rect;

import fr.leomoldo.android.bunkerwar.game.Bunker;


public class BunkerPainter {

	
	private final static Double CANON_LENGTH = 30.0;
	public final static Float BUNKER_RADIUS = 17f;
	private final static Float STROKE_WIDTH = 10f;

    //private final static Integer CANVAS_EXPANSION = 30;

    private Bunker mBunker;
	private Paint mPaint;
	private Rect mClipRect;

    public BunkerPainter(Bunker bunker) {

        mBunker = bunker;
		mClipRect = new Rect();
		initializePaint();
	}

    /*
    @Override
	protected void onDraw (Canvas canvas) {
		
		super.onDraw(canvas); 
        if(canvas == null) return; 
        
        // Expand view clipping zone.*
        canvas.getClipBounds(mClipRect);
        mClipRect.inset(-CANVAS_EXPANSION, -CANVAS_EXPANSION);
        canvas.clipRect (mClipRect, Region.Op.REPLACE);
       
        // Draw a circle and a rectangle for the bunker.
        canvas.drawCircle(getWidth()/2, getHeight()/2, BUNKER_RADIUS, mPaint);
        canvas.drawRect(getWidth()/2 - BUNKER_RADIUS, getHeight()/2, getWidth()/2 + BUNKER_RADIUS, getHeight()/2 + 2*BUNKER_RADIUS, mPaint);
        
        // Draw the canon of the bunker.
        double angle = mBunker.getCanonAngleRadian();
                
        float lengthX = (float) (CANON_LENGTH*Math.cos(angle));
        float lengthY = (float) ( - CANON_LENGTH*Math.sin(angle));
        
        if(!mBunker.isPlayerOne()) {
        	lengthX = - lengthX;
		}
        
        float startX = (float) (getWidth()/2);
        float startY = (float) (getHeight()/2);
        float endX = (float) (getWidth()/2) + lengthX;
        float endY = (float) (getHeight()/2) + lengthY;
        
        canvas.drawLine( startX, startY, endX, endY, mPaint );
	}
	*/


    private void initializePaint() {
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		if(mBunker.isPlayerOne()) {
			mPaint.setColor(Color.RED); 
		}
		else {
			mPaint.setColor(Color.YELLOW);
		}
        mPaint.setStyle(Paint.Style.FILL); 
        mPaint.setStrokeCap(Cap.BUTT);

        mPaint.setStrokeWidth(STROKE_WIDTH);
	}
}
