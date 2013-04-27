package com.kmagic.solitaire.io;

import android.content.*;

abstract class SolitaireState {

	private static final String PREFS_SOLITAIRE = "SolitairePreferences";

	protected final SharedPreferences prefs;

	protected SolitaireState( Context context ) {
		this.prefs = context.getSharedPreferences( PREFS_SOLITAIRE, Context.MODE_PRIVATE );
	}

}
