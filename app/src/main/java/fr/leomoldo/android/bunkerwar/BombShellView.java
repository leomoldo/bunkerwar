package fr.leomoldo.android.bunkerwar;

import fr.leomoldo.android.bunkerwar.game.Bunker;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class BombShellView extends View {

	public final static Float BOMBSHELL_RADIUS = 5f;

	private Bunker mBunker; //TODO Useless attribute?
	private Paint mPaint;
	
	
	public BombShellView(Context context, Bunker bunker) {

		super(context, null, 0);
		mBunker = bunker;
		this.setWillNotDraw(false);
		initializePaint();
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		
		super.onDraw(canvas); 
        if(canvas == null) return; 
       
        // Draw a circle and a rectangle for the bunker.
        canvas.drawCircle(getWidth()/2, getHeight()/2, BOMBSHELL_RADIUS, mPaint);
	}
	
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
