package fr.leomoldo.android.bunkerwar;

import android.os.Parcel;
import android.os.Parcelable;

public class GameSequencer implements Parcelable {

	public enum GameState {
        PLAYER_ONE_PLAYING,
        PLAYER_TWO_PLAYING,
		PLAYER_ONE_FIRING,
		PLAYER_TWO_FIRING,
		PLAYER_ONE_WON,
        PLAYER_TWO_WON;

        static GameState fromInt(int value) {
            switch (value) {
                case 0:
                    return PLAYER_ONE_PLAYING;
                case 1:
                    return PLAYER_TWO_PLAYING;
                case 2:
                    return PLAYER_ONE_FIRING;
                case 3:
                    return PLAYER_TWO_FIRING;
                case 4:
                    return PLAYER_ONE_WON;
                case 5:
                    return PLAYER_TWO_WON;
                default:
                    return PLAYER_ONE_PLAYING;
            }
        }
    }

    private Integer mRoundsCountPlayerOne;
    private Integer mRoundsCountPlayerTwo;
    private GameState mGameState;

    public GameSequencer() {
        mRoundsCountPlayerOne = 1;
        mRoundsCountPlayerTwo = 0;
        mGameState = GameState.PLAYER_ONE_PLAYING;
    }

    protected GameSequencer(Parcel in) {
        mRoundsCountPlayerOne = in.readInt();
        mRoundsCountPlayerTwo = in.readInt();
        mGameState = GameState.fromInt(in.readInt());
    }

    public static final Creator<GameSequencer> CREATOR = new Creator<GameSequencer>() {
        @Override
        public GameSequencer createFromParcel(Parcel in) {
            return new GameSequencer(in);
        }

        @Override
        public GameSequencer[] newArray(int size) {
            return new GameSequencer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mRoundsCountPlayerOne);
        dest.writeInt(mRoundsCountPlayerTwo);
        dest.writeInt(mGameState.ordinal());
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

    public void fireButtonPressed() {
        switch(mGameState) {
			case PLAYER_ONE_PLAYING :
				mGameState = GameState.PLAYER_ONE_FIRING;
				break;
			case PLAYER_TWO_PLAYING :
				mGameState = GameState.PLAYER_TWO_FIRING;
				break;
			default:
				break;
		}
	}

    public void bombshellDitHitBunker(Boolean bunkerOneHit) {
        if (bunkerOneHit) {
            mGameState = GameState.PLAYER_TWO_WON;
        } else {
            mGameState = GameState.PLAYER_ONE_WON;
        }
    }

    public void bombshellMissedTarget() {
        if (mGameState == GameState.PLAYER_ONE_FIRING) {
            mGameState = GameState.PLAYER_TWO_PLAYING;
            mRoundsCountPlayerTwo++;
        } else if (mGameState == GameState.PLAYER_TWO_FIRING) {
            mGameState = GameState.PLAYER_ONE_PLAYING;
            mRoundsCountPlayerOne++;
        } else {
            // Issue...
        }
    }

    public void cancelFiring() {
        if (mGameState == GameState.PLAYER_ONE_FIRING) {
            mGameState = GameState.PLAYER_ONE_FIRING;
        } else if (mGameState == GameState.PLAYER_TWO_FIRING) {
            mGameState = GameState.PLAYER_TWO_PLAYING;
        }
    }
}
