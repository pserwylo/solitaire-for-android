package com.kmagic.solitaire.io;

import android.content.*;
import com.kmagic.solitaire.*;

public class StatsManager extends SolitaireState {

	private static final String SUFFIX_ATTEMPTS   = "Attempts";
	private static final String SUFFIX_WINS       = "Wins";
	private static final String SUFFIX_HIGH_SCORE = "Score";
	private static final String SUFFIX_BEST_TIME  = "Time";

	public StatsManager( Context context ) {
		super( context );
	}



	public StatsManager setGameAttempts( Rules rules, int attempts ) {
		prefs.edit().putInt( getAttemptKey( rules ), attempts ).commit();
		return this;
	}

	public int getGameAttempts( Rules rules ) {
		return prefs.getInt( getAttemptKey( rules ), 0 );
	}

	private String getAttemptKey( Rules rules ) {
		return rules.GetGameTypeString() + SUFFIX_ATTEMPTS;
	}



	public int getGameWins( Rules rules ) {
		return prefs.getInt( getWinsKey( rules ), 0 );
	}

	public StatsManager setGameWins( Rules rules, int wins ) {
		prefs.edit().putInt( getWinsKey( rules ), wins ).commit();
		return this;
	}

	private String getWinsKey( Rules rules ) {
		return rules.GetGameTypeString() + SUFFIX_WINS;
	}



	public int getGameHighScore( Rules rules ) {
		return prefs.getInt( getWinsKey( rules ), 0 );
	}

	public StatsManager setGameHighScore( Rules rules, int score ) {
		prefs.edit().putInt( getHighScoreKey( rules ), score ).commit();
		return this;
	}

	private String getHighScoreKey( Rules rules ) {
		return rules.GetGameTypeString() + SUFFIX_HIGH_SCORE;
	}



	public int getBestGameTime( Rules rules ) {
		return  prefs.getInt( getBestTimeKey( rules ), -1 );
	}

	public StatsManager setBestGameTime( Rules rules, int bestGameTime ) {
		prefs.edit().putInt( getBestTimeKey( rules ), bestGameTime ).commit();
		return this;
	}

	private String getBestTimeKey( Rules rules ) {
		return rules.GetGameTypeString() + SUFFIX_BEST_TIME;
	}



}
