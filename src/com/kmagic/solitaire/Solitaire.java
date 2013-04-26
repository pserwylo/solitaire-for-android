/*
  Copyright 2008 Google Inc.
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.kmagic.solitaire;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.kmagic.solitaire.activities.*;
import com.kmagic.solitaire.compat.*;

// Base activity class.
public class Solitaire extends Activity {

	// View extracted from main.xml.
	private View mMainView;
	private SolitaireView mSolitaireView;
	private SharedPreferences mSettings;

	private MainMenuCompat mainMenu = MainMenuCompat.create( this );

	private boolean mDoSave;

	// Shared preferences are where the various user settings are stored.
	public SharedPreferences GetSettings() {
		return mSettings;
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		mDoSave = true;

		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );

		// If the user has never accepted the EULA show it again.
		mSettings = getSharedPreferences( "SolitairePreferences", 0 );
		setContentView( R.layout.main );
		mMainView = findViewById( R.id.main_view );
		mSolitaireView = (SolitaireView) findViewById( R.id.solitaire );
		mSolitaireView.SetHelpTextView( (TextView) findViewById( R.id.text_help ) );
		mSolitaireView.SetStatusTextView( (TextView) findViewById( R.id.text_status ) );

		mainMenu.onCreate( savedInstanceState );

		//StartSolitaire(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		return mainMenu.onOptionsItemSelected( item );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		return mainMenu.onCreateOptionsMenu( menu );
	}

	// Entry point for starting the game.
	//public void StartSolitaire(Bundle savedInstanceState) {
	@Override
	public void onStart() {
		super.onStart();
		if ( mSettings.getBoolean( "SolitaireSaveValid", false ) ) {
			SharedPreferences.Editor editor = GetSettings().edit();
			editor.putBoolean( "SolitaireSaveValid", false );
			editor.commit();
			// If save is corrupt, just start a new game.
			if ( mSolitaireView.LoadSave() ) {
				HelpSplashScreen();
				return;
			}
		}

		mSolitaireView.InitGame( mSettings.getInt( "LastType", Rules.SOLITAIRE ) );
		HelpSplashScreen();
	}

	// Force show the help if this is the first time played. Sadly no one reads
	// it anyways.
	private void HelpSplashScreen() {
		if ( !mSettings.getBoolean( "PlayedBefore", false ) ) {
			mSolitaireView.DisplayHelp();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSolitaireView.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if ( mDoSave ) {
			mSolitaireView.SaveGame();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSolitaireView.onResume();
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
	}

	public void DisplayOptions() {
		mSolitaireView.SetTimePassing( false );
		Intent intent = new Intent( this, Preferences.class );
		startActivity( intent );
	}

	public void DisplayStats() {
		mSolitaireView.SetTimePassing( false );
		new Stats( this, mSolitaireView );
	}

	public void CancelOptions() {
		setContentView( mMainView );
		mSolitaireView.requestFocus();
		mSolitaireView.SetTimePassing( true );
	}

	public void NewOptions() {
		setContentView( mMainView );
		mSolitaireView.InitGame( mSettings.getInt( "LastType", Rules.SOLITAIRE ) );
	}

	// This is called for option changes that require a refresh, but not a new game
	public void RefreshOptions() {
		setContentView( mMainView );
		mSolitaireView.RefreshOptions();
	}

	public void initGame( int gameType ) {
		mSolitaireView.InitGame( gameType );
	}

	public void restartGame() {
		mSolitaireView.RestartGame();
	}

	public void displayHelp() {
		mSolitaireView.DisplayHelp();
	}

	public void quit( boolean save ) {
		if ( save ) {
			mSolitaireView.SaveGame();
		}
		mDoSave = false;
		finish();
	}
}
