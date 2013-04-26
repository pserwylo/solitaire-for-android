package com.kmagic.solitaire;

import android.content.*;
import android.preference.*;

public class SolitairePrefs {

	private final Context context;
	private SharedPreferences prefs;

	public SolitairePrefs( Context context ) {
		this.context = context;
		this.prefs   = PreferenceManager.getDefaultSharedPreferences( context );
	}

	private static final String PREFS_NAME        = "org.kmagic.solitaire";

	private static final String DISPLAY_BIG_CARDS = "displayBigCards";
	private static final String DISPLAY_TIME      = "displayTime";
	private static final String AUTO_MOVE         = "autoMove";

	private static final String KLONDIKE_DEAL_MODE = "klondikeDealMode";
	private static final String KLONDIKE_SCORING   = "klondikeScoring";

	private static final String SPIDER_NUM_SUITS = "spiderNumSuits";

	public enum AutoMove {
		ALWAYS,
		FLING,
		NEVER
	}

	public enum KlondikeScoring {
		NORMAL,
		VEGAS
	}

	public boolean displayBigCards() {
		return prefs.getBoolean( DISPLAY_BIG_CARDS, false );
	}

	public boolean displayTime() {
		return prefs.getBoolean( DISPLAY_TIME, true );
	}

	public AutoMove autoMove() {
		return stringToAutoMove( prefs.getString( AUTO_MOVE, "" ) );
	}

	private AutoMove stringToAutoMove( String value ) {
		final String ALWAYS = getArrayValue( R.array.auto_move_values, 0 );
		final String FLING  = getArrayValue( R.array.auto_move_values, 1 );
		final String NEVER  = getArrayValue( R.array.auto_move_values, 2 );

		if ( value.equals( ALWAYS ) ) {
			return AutoMove.ALWAYS;
		} else if ( value.equals( FLING ) ) {
			return AutoMove.FLING;
		} else if ( value.equals( NEVER ) ) {
			return AutoMove.NEVER;
		} else {
			return AutoMove.ALWAYS;
		}
	}

	public KlondikeScoring klondikeScoring() {
		return stringToKlondikeScoring( prefs.getString( KLONDIKE_SCORING, "" ) );
	}

	private KlondikeScoring stringToKlondikeScoring( String value ) {
		final String NORMAL = getArrayValue( R.array.klondike_scoring_values, 0 );
		final String VEGAS  = getArrayValue( R.array.klondike_scoring_values, 1 );

		if ( value.equals( NORMAL ) ) {
			return KlondikeScoring.NORMAL;
		} else if ( value.equals( VEGAS ) ) {
			return KlondikeScoring.VEGAS;
		} else {
			return KlondikeScoring.NORMAL;
		}
	}

	public int klondikeCardsToDeal() {
		String defaultVal = getArrayValue( R.array.klondike_deal_values, 0 );
		return Integer.parseInt( prefs.getString( KLONDIKE_DEAL_MODE, defaultVal ) );
	}

	public int spiderNumSuits() {
		String defaultVal = getArrayValue( R.array.spider_num_suits_values, 0 );
		return Integer.parseInt( prefs.getString( SPIDER_NUM_SUITS, defaultVal ) );
	}

	protected String getArrayValue( int id, int index ) {
		return context.getResources().getStringArray( id )[ index ];
	}

}