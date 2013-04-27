package com.kmagic.solitaire.io;

import android.content.*;
import com.kmagic.solitaire.*;

import java.util.*;

public class GameIO {

	protected static final String SAVE_FILENAME = "solitaire_save.bin";
	protected static final String SAVE_VERSION  = "solitaire_save_2";

	protected final Context context;

	protected Rules rules;
	protected Stack<Move> history;
	protected int elapsed;

	public GameIO( Context context ) {
		this.context = context;
	}

	public int getElapsed() {
		return elapsed;
	}

	public Rules getRules() {
		return rules;
	}

	public Stack<Move> getHistory() {
		return history;
	}
}
