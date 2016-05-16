package fr.leomoldo.android.bunkerwar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import fr.leomoldo.android.bunkerwar.game.Bunker;

public class BombShellPainter {

	public final static Float BOMBSHELL_RADIUS = 5f;

	private Bunker mBunker; //TODO Useless attribute?
	private Paint mPaint;


    public BombShellPainter(Context context, Bunker bunker) {

		mBunker = bunker;
		initializePaint();
	}

	/*
    @Override
	protected void onDraw(Canvas canvas) {
		
		super.onDraw(canvas); 
        if(canvas == null) return; 
       
        // Draw a circle and a rectangle for the bunker.
        canvas.drawCircle(getWidth()/2, getHeight()/2, BOMBSHELL_RADIUS, mPaint);
	}
	*/

    private void initializePaint() {

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		/*if (mBunker.isPlayerOne()) {
			mPaint.setColor(Color.RED);
		} else {
			mPaint.setColor(Color.YELLOW);
		}*/
		mPaint.setColor(Color.BLACK); // TODO Debug only.
		
		mPaint.setStyle(Paint.Style.FILL);
	}
}
