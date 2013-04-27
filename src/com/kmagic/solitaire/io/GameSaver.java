package com.kmagic.solitaire.io;

import android.content.*;
import android.util.*;
import com.kmagic.solitaire.*;

import java.io.*;
import java.util.*;

public class GameSaver extends GameIO {

	public GameSaver( Context context, Rules rules, Stack<Move> history, int elapsed ) {
		super( context );

		this.rules   = rules;
		this.history = history;
		this.elapsed = elapsed;
	}

	public void save() {
		FileOutputStream fout = null;
		ObjectOutputStream oout = null;
		try {
			fout = context.openFileOutput( SAVE_FILENAME, 0 );
			oout = new ObjectOutputStream( fout );

			CardAnchor[] anchors = rules.GetAnchorArray();

			int cardCount = rules.GetCardCount();
			int[] value = new int[cardCount];
			int[] suit = new int[cardCount];
			int[] anchorCardCount = new int[ anchors.length ];
			int[] anchorHiddenCount = new int[ anchors.length ];
			int historySize = history.size();
			int[] historyFrom = new int[historySize];
			int[] historyToBegin = new int[historySize];
			int[] historyToEnd = new int[historySize];
			int[] historyCount = new int[historySize];
			int[] historyFlags = new int[historySize];
			Card[] card;

			cardCount = 0;
			for ( int i = 0; i < anchors.length; i++ ) {
				anchorCardCount[i] = anchors[i].GetCount();
				anchorHiddenCount[i] = anchors[i].GetHiddenCount();
				card = anchors[i].GetCards();
				for ( int j = 0; j < anchorCardCount[i]; j++, cardCount++ ) {
					value[cardCount] = card[j].GetValue();
					suit[cardCount] = card[j].GetSuit();
				}
			}

			for ( int i = 0; i < historySize; i++ ) {
				Move move = history.pop();
				historyFrom[i] = move.GetFrom();
				historyToBegin[i] = move.GetToBegin();
				historyToEnd[i] = move.GetToEnd();
				historyCount[i] = move.GetCount();
				historyFlags[i] = move.GetFlags();
			}

			oout.writeObject( SAVE_VERSION );
			oout.writeInt( anchors.length );
			oout.writeInt( cardCount );
			oout.writeInt( rules.GetType() );
			oout.writeObject( anchorCardCount );
			oout.writeObject( anchorHiddenCount );
			oout.writeObject( value );
			oout.writeObject( suit );
			oout.writeInt( rules.GetRulesExtra() );
			oout.writeInt( rules.GetScore() );
			oout.writeInt( elapsed );
			oout.writeObject( historyFrom );
			oout.writeObject( historyToBegin );
			oout.writeObject( historyToEnd );
			oout.writeObject( historyCount );
			oout.writeObject( historyFlags );
			oout.close();

			new AppState( context ).hasSave( true );

		} catch ( IOException e ) {
			Log.e( "Solitaire", "Couldn't save game - " + e.getMessage() + ": " + Log.getStackTraceString( e ) );
		} finally {
			if ( fout != null ) {
				try {
					fout.close();
				} catch ( IOException e ) {
					Log.e( "Solitaire", "Error closing file output stream" );
				}
			}
			if ( oout != null ) {
				try {
					oout.close();
				} catch ( IOException e ) {
					Log.e( "Solitaire", "Error closing object output stream" );
				}
			}
		}
	}

}
