package fr.leomoldo.android.bunkerwar.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Random;

import fr.leomoldo.android.bunkerwar.sdk.Drawer;
import fr.leomoldo.android.bunkerwar.sdk.ViewCoordinates;

public class Landscape extends Drawer implements Parcelable {

	private final static Integer INITIAL_LANDSCAPE_WIDTH = 6;
	private final static Integer NUMBER_OF_INTERPOLATION_ITERATIONS = 4;
	
	private final static Integer MAX_HEIGHT_VALUE = 100;

    public final static Integer BUNKER_POSITION_FROM_SCREEN_BORDER = 4;

    public final static float MAX_HEIGHT_RATIO_FOR_LANDSCAPE = 0.5f;


	// Contains Integers between 0 and MAX_HEIGHT_VALUE.
	private ArrayList<Integer> mLandscapeHeights;
    private int mColor;


    public Landscape(int color) {

		mLandscapeHeights = new ArrayList<Integer>(calculateFinalLandscapeWidth(INITIAL_LANDSCAPE_WIDTH, NUMBER_OF_INTERPOLATION_ITERATIONS));
		Random random = new Random();
		for (int i = 0 ; i < INITIAL_LANDSCAPE_WIDTH ; i++) {
			mLandscapeHeights.add(i, random.nextInt(MAX_HEIGHT_VALUE));
		}
		
		for (int i = 0 ; i < NUMBER_OF_INTERPOLATION_ITERATIONS ; i++) {
			interpolateHeightsArrayList(mLandscapeHeights);
		}

        mColor = color;
        initializePaint();
    }

    protected Landscape(Parcel in) {
        mColor = in.readInt();

        // TODO Refactor to implement this :
        /*
        int[] parcelArray = new int[calculateFinalLandscapeWidth(INITIAL_LANDSCAPE_WIDTH, NUMBER_OF_INTERPOLATION_ITERATIONS)];
        in.readIntArray(parcelArray);
        mLandscapeHeights = new ArrayList<Integer>(parcelArray);
        */

        initializePaint();
    }

    public static final Creator<Landscape> CREATOR = new Creator<Landscape>() {
        @Override
        public Landscape createFromParcel(Parcel in) {
            return new Landscape(in);
        }

        @Override
        public Landscape[] newArray(int size) {
            return new Landscape[size];
        }
    };

    protected void initializePaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mColor);
        setPaint(paint);
    }

	public Integer getLandscapeHeight(int index) {
		if ((index < 0) || (index >= mLandscapeHeights.size())) {
			return -1;
		}
			return mLandscapeHeights.get(index);
	}

	public Float getLandscapeHeightPercentage(int index) {
		return getLandscapeHeight(index) / 100f;
	}

	public Integer getNumberOfLandscapeSlices() {
		return mLandscapeHeights.size();
	}


    @Override
    public void draw(Canvas canvas, int viewWidth, int viewHeight) {
        for (int i = 0; i < getNumberOfLandscapeSlices(); i++) {
            canvas.drawRect(i * viewWidth / getNumberOfLandscapeSlices(),
                    viewHeight - viewHeight * MAX_HEIGHT_RATIO_FOR_LANDSCAPE * getLandscapeHeightPercentage(i),
                    (i + 1) * viewWidth / getNumberOfLandscapeSlices(),
                    viewHeight,
                    getPaint());
        }
    }

    @Override
    public boolean isHitByBombshell(ViewCoordinates bombshellVC, int viewWidth, int viewHeight) {
        return bombshellVC.getY() > getLandscapeHeightForX(bombshellVC.getX(), viewWidth, viewHeight);
    }

    private void interpolateHeightsArrayList(ArrayList<Integer> heightsArrayList) {
		Integer sizeOfNewArrayList = 2 * heightsArrayList.size() - 1;
		int currentInterpolationValue = 0;
		for (int i = 1 ; i < sizeOfNewArrayList; i += 2) {
			currentInterpolationValue = ( heightsArrayList.get(i-1) + heightsArrayList.get(i) ) / 2;
			heightsArrayList.add(i, currentInterpolationValue);
		}
		
	}
	
	private int calculateFinalLandscapeWidth(int initialWidth, int numberOfIterations) {
		for (int i = 0 ; i < numberOfIterations ; i++) {
			initialWidth = 2* initialWidth - 1;
		}
		return initialWidth;
	}

    private float getLandscapeHeightForX(float x, int viewWidth, int mViewHeight) {
        int sliceIndex = (int) (x / (viewWidth / getNumberOfLandscapeSlices()));
        return mViewHeight - mViewHeight * Landscape.MAX_HEIGHT_RATIO_FOR_LANDSCAPE * getLandscapeHeightPercentage(sliceIndex);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mColor);
        // TODO Refactor to implement this correctly :
        dest.writeArray(mLandscapeHeights.toArray());
    }
}
