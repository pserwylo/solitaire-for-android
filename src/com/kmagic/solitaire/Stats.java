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

import android.content.*;
import android.view.*;
import android.widget.*;
import com.kmagic.solitaire.io.*;


public class Stats {

	public Stats( final Solitaire solitaire, final SolitaireView view ) {

		solitaire.setContentView( R.layout.stats );
		View statsView = (View) solitaire.findViewById( R.id.stats_view );
		statsView.setFocusable( true );
		statsView.setFocusableInTouchMode( true );

		final Rules rules = view.GetRules();
		final StatsManager stats = new StatsManager( solitaire );
		int attempts  = stats.getGameAttempts( rules );
		int wins      = stats.getGameWins( rules );
		int bestTime  = stats.getBestGameTime( rules );
		int highScore = stats.getGameHighScore( rules );
		float ratio   = 0;
		if ( attempts > 0 ) {
			ratio = (float) wins / (float) attempts * 100.0f;
		}

		TextView tv = (TextView) solitaire.findViewById( R.id.text_title );
		tv.setText( rules.GetPrettyGameTypeString() + " Statistics\n\n" );
		tv = (TextView) solitaire.findViewById( R.id.text_wins );
		tv.setText( "Wins: " + wins + " Attempts: " + attempts );
		tv = (TextView) solitaire.findViewById( R.id.text_percentage );
		tv.setText( "Winning Percentage: " + ratio );
		if ( bestTime != -1 ) {
			int seconds = (bestTime / 1000) % 60;
			int minutes = bestTime / 60000;
			tv = (TextView) solitaire.findViewById( R.id.text_best_time );
			tv.setText( "Fastest Time: " + String.format( "%d:%02d", minutes, seconds ) );
		}
		if ( rules.HasScore() ) {
			tv = (TextView) solitaire.findViewById( R.id.text_high_score );
			tv.setText( "High Score: " + highScore );
		}


		final Button accept = (Button) solitaire.findViewById( R.id.button_accept );
		accept.setOnClickListener( new View.OnClickListener() {
			public void onClick( View v ) {
				solitaire.CancelOptions();
			}
		} );
		final Button clear = (Button) solitaire.findViewById( R.id.button_clear );
		clear.setOnClickListener( new View.OnClickListener() {
			public void onClick( View v ) {
				stats.setGameAttempts( rules, 0 );
				stats.setGameWins( rules, 0 );
				stats.setBestGameTime( rules, -1 );
				view.ClearGameStarted();
				solitaire.CancelOptions();
			}
		} );
		statsView.setOnKeyListener( new View.OnKeyListener() {
			@Override
			public boolean onKey( View v, int keyCode, KeyEvent event ) {
				switch ( keyCode ) {
					case KeyEvent.KEYCODE_BACK:
					case KeyEvent.KEYCODE_HOME:
						solitaire.CancelOptions();
						return true;
				}
				return false;
			}
		} );
		statsView.requestFocus();
	}
}

