package com.kmagic.solitaire.io;

import android.content.*;
import android.os.*;
import android.util.*;
import com.kmagic.solitaire.*;

import java.io.*;

public class GameLoader extends GameIO {

	private final SolitaireView view;
	private final AnimateCard   animateCard;

	public GameLoader( SolitaireView view, AnimateCard animateCard ) {
		super( view.getContext() );
		this.view        = view;
		this.animateCard = animateCard;
	}

	public boolean load() {
		boolean success = false;
		FileInputStream fileInput = null;
		ObjectInputStream objectInput = null;
		try {
			fileInput   = context.openFileInput( SAVE_FILENAME );
			objectInput = new ObjectInputStream( fileInput );

			String version = (String) objectInput.readObject();
			if ( !version.equals( SAVE_VERSION ) ) {
				Log.e( "Solitaire", "Invalid save version" );
				success = false;
			} else {
				Bundle map = new Bundle();
				map.putInt( "cardAnchorCount", objectInput.readInt() );
				map.putInt( "cardCount", objectInput.readInt() );
				int type = objectInput.readInt();
				map.putIntArray( "anchorCardCount", (int[]) objectInput.readObject() );
				map.putIntArray( "anchorHiddenCount", (int[]) objectInput.readObject() );
				map.putIntArray( "value", (int[]) objectInput.readObject() );
				map.putIntArray( "suit", (int[]) objectInput.readObject() );
				map.putInt( "rulesExtra", objectInput.readInt() );
				map.putInt( "score", objectInput.readInt() );
				elapsed = objectInput.readInt();
				int[] historyFrom = (int[]) objectInput.readObject();
				int[] historyToBegin = (int[]) objectInput.readObject();
				int[] historyToEnd = (int[]) objectInput.readObject();
				int[] historyCount = (int[]) objectInput.readObject();
				int[] historyFlags = (int[]) objectInput.readObject();
				for ( int i = historyFrom.length - 1; i >= 0; i-- ) {
					history.push( new Move( historyFrom[i], historyToBegin[i], historyToEnd[i], historyCount[i], historyFlags[i] ) );
				}
				rules = Rules.CreateRules( type, map, view, history, animateCard );
				success = true;
			}
		} catch ( IOException e ) {
			Log.e( "Solitaire", "Error loading game - " + e.getMessage() + ": " + Log.getStackTraceString( e ) );
		} catch ( ClassNotFoundException e ) {
			Log.e( "SolitaireView.java", "LoadSave(): Class not found exception" );
		} finally {
			if ( fileInput != null ) {
				try {
					fileInput.close();
				} catch ( IOException e ) {
					Log.e( "Solitaire", "Error closing file output stream during game load" );
				}
			}
			if ( objectInput != null ) {
				try {
					objectInput.close();
				} catch ( IOException e ) {
					Log.e( "Solitaire", "Error closing object output stream during game load" );
				}
			}
		}
		return success;
	}

}
