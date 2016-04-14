package fr.leomoldo.android.bunkerwar.game;

import java.util.ArrayList;
import java.util.Random;

public class Landscape {

	private final static Integer INITIAL_LANDSCAPE_WIDTH = 6;
	private final static Integer NUMBER_OF_INTERPOLATION_ITERATIONS = 4;
	
	private final static Integer MAX_HEIGHT_VALUE = 100; 
	
	public final static Integer BUNKER_POSITION_FROM_SCREEN_BORDER = 2;
	
	
	// Contient des entiers entre 0 et 100.
	private ArrayList<Integer> mLandscapeHeights;
	
	
	public Landscape() {

		mLandscapeHeights = new ArrayList<Integer>(calculateFinalLandscapeWidth(INITIAL_LANDSCAPE_WIDTH, NUMBER_OF_INTERPOLATION_ITERATIONS));
		Random random = new Random();
		for (int i = 0 ; i < INITIAL_LANDSCAPE_WIDTH ; i++) {
			mLandscapeHeights.add(i, random.nextInt(MAX_HEIGHT_VALUE));
		}
		
		for (int i = 0 ; i < NUMBER_OF_INTERPOLATION_ITERATIONS ; i++) {
			interpolateHeightsArrayList(mLandscapeHeights);
		}
	}
	
	
	public Integer getLandscapeHeight(int index) {
		if ((index < 0) || (index >= mLandscapeHeights.size())) {
			return -1;
		}
			return mLandscapeHeights.get(index);
	}
	
	public Integer getLandscapeWidth() {
		return mLandscapeHeights.size();
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
}
