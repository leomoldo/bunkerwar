package fr.leomoldo.android.bunkerwar;

public class GameSequencer {

	public enum GameState {
		PLAYER_ONE_PLAYING,	
		PLAYER_TWO_PLAYING,
		PLAYER_ONE_FIRING,
		PLAYER_TWO_FIRING,
		PLAYER_ONE_WON,
		PLAYER_TWO_WON
	}
	
	
	private Integer mRoundsCount;
	private GameState mGameState;
	
	
	public GameSequencer() {
		mRoundsCount = 1;
		mGameState = GameState.PLAYER_ONE_PLAYING;
	}

	public Integer getRoundsCount() {
		return mRoundsCount;
	}
	
	public GameState getGameState() {
		return mGameState;
	}
	
	public void fireButtonPressed(Boolean didPlayerOneFire) {
		
		switch(mGameState) {
		
			case PLAYER_ONE_PLAYING :
				if (!didPlayerOneFire) {
					// Throw exception?
				}
				mGameState = GameState.PLAYER_ONE_FIRING;
				break;
				
			case PLAYER_TWO_PLAYING :
				if (didPlayerOneFire) {
					// Throw exception?
				}
				mGameState = GameState.PLAYER_TWO_FIRING;
				break;
			default:
				break;
		}
		
		mRoundsCount++;
	}

	public void bombshellTouchedBunker(Boolean bunkerOneTouched) {
		// To be implemented.
	}

	public void bombshellTouchedLandscape() {
		// To be implemented.
	}
	
	public void exportGameResult() {
		// To be implemented.
	}
}
