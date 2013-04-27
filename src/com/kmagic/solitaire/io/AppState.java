package com.kmagic.solitaire.io;

import android.content.*;
import android.preference.*;
import com.kmagic.solitaire.*;

public class AppState extends SolitaireState {

	private static final String KEY_HAS_SAVE = "SolitaireSaveValid";
	private static final String KEY_MOST_RECENT_GAME = "LastType";
	private static final String KEY_HAS_PLAYED = "PlayedBefore";

	public AppState( Context context ) {
		super( context );
	}

	public AppState hasPlayed( boolean hasPlayed ) {
		prefs.edit().putBoolean( KEY_HAS_PLAYED, hasPlayed ).commit();
		return this;
	}

	public boolean hasPlayed() {
		return prefs.getBoolean( KEY_HAS_PLAYED, false );
	}

	public AppState hasSave( boolean hasSave ) {
		prefs.edit().putBoolean( KEY_HAS_SAVE, hasSave ).commit();
		return this;
	}

	public boolean hasSave() {
		return prefs.getBoolean( KEY_HAS_SAVE, false );
	}

	public AppState setGameScore( Rules rules ) {
		return setGameScore( rules, rules.GetScore() );
	}

	public AppState setGameScore( Rules rules, int score ) {
		prefs.edit().putInt( getScoreKey( rules ), score ).commit();
		return this;
	}

	public int getGameScore( Rules rules ) {
		return prefs.getInt( getScoreKey( rules ), -52 );
	}

	private String getScoreKey( Rules rules ) {
		return rules.GetGameTypeString() + "Score";
	}

	public AppState setMostRecentGame( int gameType ) {
		prefs.edit().putInt( KEY_MOST_RECENT_GAME, gameType ).commit();
		return this;
	}

	public int getMostRecentGame() {
		return prefs.getInt( KEY_MOST_RECENT_GAME, Rules.SOLITAIRE );
	}

}
