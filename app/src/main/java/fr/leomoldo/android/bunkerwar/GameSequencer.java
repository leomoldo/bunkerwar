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


    private Integer mRoundsCountPlayerOne;
    private Integer mRoundsCountPlayerTwo;
    private GameState mGameState;
	
	
	public GameSequencer() {
        mRoundsCountPlayerOne = 1;
        mRoundsCountPlayerTwo = 0;
        mGameState = GameState.PLAYER_ONE_PLAYING;
	}

    public Integer getRoundsCountPlayerOne() {
        return mRoundsCountPlayerOne;
    }

    public Integer getRoundsCountPlayerTwo() {
        return mRoundsCountPlayerTwo;
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
	}

    public void bombshellDitHitBunker(Boolean bunkerOneHit) {
        // To be implemented.
	}

    public void bombshellMissedTarget() {
        // To be implemented.
	}
}
