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
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.kmagic.solitaire.io.*;
import com.kmagic.solitaire.widget.*;

import java.util.*;

// The brains of the operation
public class SolitaireView extends View {

	private static final int MODE_NORMAL = 1;
	private static final int MODE_MOVE_CARD = 2;
	private static final int MODE_CARD_SELECT = 3;
	private static final int MODE_TEXT = 4;
	private static final int MODE_ANIMATE = 5;
	private static final int MODE_WIN = 6;
	private static final int MODE_WIN_STOP = 7;

	private CharSequence mHelpText;
	private CharSequence mWinText;

	private CardAnchor[] mCardAnchor;
	private DrawMaster mDrawMaster;
	private Rules mRules;
	private TextView mHelpTextView;
	private TextView mStatusTextView;
	private AnimateCard mAnimateCard;

	private MoveCard mMoveCard;
	private SelectCard mSelectCard;
	private int mViewMode;
	private boolean mTextViewDown;

	private PointF mLastPoint;
	private PointF mDownPoint;
	private RefreshHandler mRefreshHandler;
	private Thread mRefreshThread;
	private Stack<Move> mMoveHistory;
	private Replay mReplay;
	private Context mContext;
	private boolean mHasMoved;
	private Speed mSpeed;

	private Card[] mUndoStorage;

	private int mElapsed = 0;
	private long mStartTime;
	private boolean mTimePaused;

	private boolean mGameStarted;
	private boolean mPaused;
	private boolean mDisplayTime;

	private int mWinningScore;

	public SolitaireView( Context context, AttributeSet attrs ) {
		super( context, attrs );
		setFocusable( true );
		setFocusableInTouchMode( true );

		mDrawMaster = new DrawMaster( this );
		mMoveCard = new MoveCard();
		mSelectCard = new SelectCard();
		mViewMode = MODE_NORMAL;
		mLastPoint = new PointF();
		mDownPoint = new PointF();
		mRefreshHandler = new RefreshHandler( this );
		mRefreshThread = new Thread( mRefreshHandler );
		mMoveHistory = new Stack<Move>();
		mUndoStorage = new Card[CardAnchor.MAX_CARDS];
		mAnimateCard = new AnimateCard( this );
		mSpeed = new Speed();
		mReplay = new Replay( this, mAnimateCard );

		mHelpText = context.getResources().getText( R.string.help_text );
		mWinText = context.getResources().getText( R.string.win_text );
		mContext = context;
		mTextViewDown = false;
		mRefreshThread.start();
		mWinningScore = 0;
	}

	/**
	 * dpi to px
	 * http://developer.android.com/guide/practices/screens_support.html#screen-independence
	 */
	public int calcGutterSize() {
		final float GUTTER_SIZE_DP = 24.0f;
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (GUTTER_SIZE_DP * scale + 0.5f);
	}

	/**
	 * Leaves a gutter at the bottom for status and action buttons.
	 */
	public Point calcScreenSize() {
		WindowManager wm = (WindowManager)getContext().getSystemService( Context.WINDOW_SERVICE );
		Point screenSize = new Point();
		screenSize.set(
			wm.getDefaultDisplay().getWidth(),
			wm.getDefaultDisplay().getHeight()
		);
		return screenSize;
	}

	public void InitGame() {
		AppState state = new AppState( mContext );
		InitGame( state.getMostRecentGame() );
	}

	public void InitGame( int gameType ) {
		int oldScore = 0;
		String oldGameType = "None";

		// We really really want focus :)
		setFocusable( true );
		setFocusableInTouchMode( true );
		requestFocus();

		SolitairePrefs prefs = new SolitairePrefs( mContext );
		AppState state = new AppState( mContext );

		if ( mRules != null ) {
			if ( mRules.HasScore() ) {
				if ( mViewMode == MODE_WIN || mViewMode == MODE_WIN_STOP ) {
					oldScore = mWinningScore;
				} else {
					oldScore = mRules.GetScore();
				}
				oldGameType = mRules.GetGameTypeString();
				if ( oldScore > state.getGameScore( mRules ) ) {
					state.setGameScore( mRules, oldScore );
				}
			}
		}
		ChangeViewMode( MODE_NORMAL );
		mHelpTextView.setVisibility( View.INVISIBLE );
		mMoveHistory.clear();
		mRules = Rules.CreateRules( gameType, null, this, mMoveHistory, mAnimateCard );
		if ( oldGameType.equals( mRules.GetGameTypeString() ) ) {
			mRules.SetCarryOverScore( oldScore );
		}
		Card.SetSize( gameType, calcScreenSize() );
		mDrawMaster.DrawCards( prefs.displayBigCards() );
		mCardAnchor = mRules.GetAnchorArray();
		if ( mDrawMaster.GetWidth() > 1 ) {
			mRules.Resize( mDrawMaster.GetWidth(), mDrawMaster.GetHeight() );
			Refresh();
		}

		setupActions();

		SetDisplayTime( prefs.displayTime() );
		state.setMostRecentGame( gameType );
		mStartTime = SystemClock.uptimeMillis();
		mElapsed = 0;
		mTimePaused = false;
		mPaused = false;
		mGameStarted = false;
	}

	/**
	 * Setup on-screen action buttons according to the current game (mRules).
	 */
	private void setupActions() {
		ActionButton btn1 = (ActionButton)((Solitaire) mContext).findViewById( R.id.btn_action_1 );
		GameAction[] actions = mRules.getActions();
		if ( actions.length == 0 ) {
			btn1.setAction( null );
		} else {
			btn1.setAction( actions[ 0 ] );
		}
	}

	public DrawMaster GetDrawMaster() {
		return mDrawMaster;
	}

	public Rules GetRules() {
		return mRules;
	}

	public void ClearGameStarted() {
		mGameStarted = false;
	}

	public void SetDisplayTime( boolean displayTime ) {
		mDisplayTime = displayTime;
	}

	public void SetTimePassing( boolean timePassing ) {
		if ( timePassing == true && (mViewMode == MODE_WIN || mViewMode == MODE_WIN_STOP) ) {
			return;
		}
		if ( timePassing == true && mTimePaused == true ) {
			mStartTime = SystemClock.uptimeMillis() - mElapsed;
			mTimePaused = false;
		} else if ( timePassing == false ) {
			mTimePaused = true;
		}
	}

	public void UpdateTime() {
		if ( !mTimePaused ) {
			int elapsed = (int) (SystemClock.uptimeMillis() - mStartTime);
			if ( elapsed / 1000 > mElapsed / 1000 ) {
				Refresh();
			}
			mElapsed = elapsed;
		}
	}

	private void ChangeViewMode( int newMode ) {
		switch ( mViewMode ) {
			case MODE_NORMAL:
				if ( newMode != MODE_NORMAL ) {
					DrawBoard();
				}
				break;
			case MODE_MOVE_CARD:
				mMoveCard.Release();
				DrawBoard();
				break;
			case MODE_CARD_SELECT:
				mSelectCard.Release();
				DrawBoard();
				break;
			case MODE_TEXT:
				mHelpTextView.setVisibility( View.INVISIBLE );
				break;
			case MODE_ANIMATE:
				mRefreshHandler.SetRefresh( RefreshHandler.SINGLE_REFRESH );
				break;
			case MODE_WIN:
			case MODE_WIN_STOP:
				if ( newMode != MODE_WIN_STOP ) {
					mHelpTextView.setVisibility( View.INVISIBLE );
				}
				DrawBoard();
				mReplay.StopPlaying();
				break;
		}
		mViewMode = newMode;
		switch ( newMode ) {
			case MODE_WIN:
				SetTimePassing( false );
			case MODE_MOVE_CARD:
			case MODE_CARD_SELECT:
			case MODE_ANIMATE:
				mRefreshHandler.SetRefresh( RefreshHandler.LOCK_REFRESH );
				break;

			case MODE_NORMAL:
			case MODE_TEXT:
			case MODE_WIN_STOP:
				mRefreshHandler.SetRefresh( RefreshHandler.SINGLE_REFRESH );
				break;
		}
	}

	public void onPause() {
		mPaused = true;

		if ( mRefreshThread != null ) {
			mRefreshHandler.SetRunning( false );
			mRules.ClearEvent();
			mRules.SetIgnoreEvents( true );
			mReplay.StopPlaying();
			try {
				mRefreshThread.join( 1000 );
			} catch ( InterruptedException e ) {
			}
			mRefreshThread = null;
			if ( mAnimateCard.GetAnimate() ) {
				mAnimateCard.Cancel();
			}
			if ( mViewMode != MODE_WIN && mViewMode != MODE_WIN_STOP ) {
				ChangeViewMode( MODE_NORMAL );
			}

			AppState state = new AppState( mContext );
			if ( mRules != null && mRules.GetScore() > state.getGameScore( mRules ) ) {
				state.setGameScore( mRules );
			}
		}
	}

	public void SaveGame() {
		// This is supposed to have been called but I've seen instances where it wasn't.
		if ( mRefreshThread != null ) {
			onPause();
		}

		if ( mRules != null && mViewMode == MODE_NORMAL ) {
			new GameSaver( mContext, mRules, mMoveHistory, mElapsed ).save();
		}
	}

	public boolean LoadSave() {
		SolitairePrefs prefs = new SolitairePrefs( mContext );
		mDrawMaster.DrawCards( prefs.displayBigCards() );
		mTimePaused = true;

		GameLoader loader = new GameLoader( this, mAnimateCard );
		if ( loader.load() ) {

			mElapsed     = loader.getElapsed();
			mStartTime   = SystemClock.uptimeMillis() - mElapsed;
			mRules       = loader.getRules();
			mCardAnchor  = mRules.GetAnchorArray();
			mMoveHistory = loader.getHistory();
			mGameStarted = !mMoveHistory.isEmpty();

			Card.SetSize( mRules.GetType(), calcScreenSize() );
			SetDisplayTime( prefs.displayTime() );
			if ( mDrawMaster.GetWidth() > 1 ) {
				mRules.Resize( mDrawMaster.GetWidth(), mDrawMaster.GetHeight() );
				Refresh();
			}
		}

		mTimePaused = false;
		mPaused = false;
		return false;
	}

	public void onResume() {
		mStartTime = SystemClock.uptimeMillis() - mElapsed;
		mRefreshHandler.SetRunning( true );
		mRefreshThread = new Thread( mRefreshHandler );
		mRefreshThread.start();
		mRules.SetIgnoreEvents( false );
		mPaused = false;
	}

	public void Refresh() {
		mRefreshHandler.SingleRefresh();
	}

	public void SetHelpTextView( TextView textView ) {
		mHelpTextView = textView;
	}

	public void SetStatusTextView( TextView textView ) {
		mStatusTextView = textView;
	}

	protected void onSizeChanged( int w, int h, int oldw, int oldh ) {
		mDrawMaster.SetScreenSize( w, h );
		mRules.Resize( w, h );
		mSelectCard.SetHeight( h );
	}

	public void DisplayHelp() {
		mHelpTextView.setTextSize( 15 );
		mHelpTextView.setGravity( Gravity.LEFT );
		DisplayHelpText( mHelpText );
	}

	public void DisplayWin() {
		MarkWin();
		mHelpTextView.setTextSize( 24 );
		mHelpTextView.setGravity( Gravity.CENTER_HORIZONTAL );
		DisplayHelpText( mWinText );
		ChangeViewMode( MODE_WIN );
		mHelpTextView.setVisibility( View.VISIBLE );
		mRules.SetIgnoreEvents( true );
		mReplay.StartReplay( mMoveHistory, mCardAnchor );
	}

	public void RestartGame() {
		mRules.SetIgnoreEvents( true );
		while ( !mMoveHistory.empty() ) {
			Undo();
		}
		mRules.SetIgnoreEvents( false );
		Refresh();
	}

	public void DisplayHelpText( CharSequence text ) {
		ChangeViewMode( MODE_TEXT );
		mHelpTextView.setVisibility( View.VISIBLE );
		mHelpTextView.setText( text );
		Refresh();
	}

	public void DrawBoard() {
		Canvas boardCanvas = mDrawMaster.GetBoardCanvas();
		mDrawMaster.DrawBackground( boardCanvas );
		for ( int i = 0; i < mCardAnchor.length; i++ ) {
			mCardAnchor[i].Draw( mDrawMaster, boardCanvas );
		}
	}

	@Override
	public void onDraw( Canvas canvas ) {

		// Only draw the stagnant stuff if it may have changed
		if ( mViewMode == MODE_NORMAL ) {
			// SanityCheck is for debug use only.
			SanityCheck();
			DrawBoard();
		}
		mDrawMaster.DrawLastBoard( canvas );
		String status = "";
		if ( mDisplayTime ) {
			int seconds = (mElapsed / 1000) % 60;
			int minutes = mElapsed / 60000;
			String time;
			// String.format is insanely slow (~15ms)
			if ( seconds < 10 ) {
				time = minutes + ":0" + seconds;
			} else {
				time = minutes + ":" + seconds;
			}
			status += time;
			if ( mRules.HasString() ) {
				status += "\n";
			}
			// mDrawMaster.DrawTime( canvas, mElapsed );
		}
		if ( mRules.HasString() ) {
			status += mRules.GetString();
			// mDrawMaster.DrawRulesString( canvas, mRules.GetString() );
		}

		mStatusTextView.setText( status );

		switch ( mViewMode ) {
			case MODE_MOVE_CARD:
				mMoveCard.Draw( mDrawMaster, canvas );
				break;
			case MODE_CARD_SELECT:
				mSelectCard.Draw( mDrawMaster, canvas );
				break;
			case MODE_WIN:
				if ( mReplay.IsPlaying() ) {
					mAnimateCard.Draw( mDrawMaster, canvas );
				}
			case MODE_WIN_STOP:
			case MODE_TEXT:
				mDrawMaster.DrawShade( canvas );
				break;
			case MODE_ANIMATE:
				mAnimateCard.Draw( mDrawMaster, canvas );
		}

		mRules.HandleEvents();
	}

	@Override
	public boolean onKeyDown( int keyCode, KeyEvent msg ) {
		switch ( keyCode ) {
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_SEARCH:
				if ( mViewMode == MODE_TEXT ) {
					ChangeViewMode( MODE_NORMAL );
				} else if ( mViewMode == MODE_NORMAL ) {
					mRules.EventAlert( Rules.EVENT_DEAL, mCardAnchor[0] );
					Refresh();
				}
				return true;
			case KeyEvent.KEYCODE_BACK:
				Undo();
				return true;
		}
		mRules.HandleEvents();
		return super.onKeyDown( keyCode, msg );
	}

	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		boolean ret = false;

		// Yes you can get touch events while in the "paused" state.
		if ( mPaused ) {
			return false;
		}

		// Text mode only handles clickys
		if ( mViewMode == MODE_TEXT ) {
			if ( event.getAction() == MotionEvent.ACTION_UP && mTextViewDown ) {
				new AppState( mContext ).hasPlayed( true );
				mTextViewDown = false;
				ChangeViewMode( MODE_NORMAL );
			}
			if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
				mTextViewDown = true;
			}
			return true;
		}

		switch ( event.getAction() ) {
			case MotionEvent.ACTION_DOWN:
				mHasMoved = false;
				mSpeed.Reset();
				ret = onDown( event.getX(), event.getY() );
				mDownPoint.set( event.getX(), event.getY() );
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				ret = onRelease( event.getX(), event.getY() );
				break;
			case MotionEvent.ACTION_MOVE:
				if ( !mHasMoved ) {
					CheckMoved( event.getX(), event.getY() );
				}
				ret = onMove( mLastPoint.x - event.getX(), mLastPoint.y - event.getY(), event.getX(), event.getY() );
				break;
		}
		mLastPoint.set( event.getX(), event.getY() );

		if ( !mGameStarted && !mMoveHistory.empty() ) {
			mGameStarted = true;
			MarkAttempt();
		}

		mRules.HandleEvents();
		return ret;
	}

	private boolean onRelease( float x, float y ) {
		switch ( mViewMode ) {
			case MODE_NORMAL:
				if ( !mHasMoved ) {
					for ( int i = 0; i < mCardAnchor.length; i++ ) {
						if ( mCardAnchor[i].ExpandStack( x, y ) ) {
							mSelectCard.InitFromAnchor( mCardAnchor[i] );
							ChangeViewMode( MODE_CARD_SELECT );
							return true;
						} else if ( mCardAnchor[i].TapCard( x, y ) ) {
							Refresh();
							return true;
						}
					}
				}
				break;
			case MODE_MOVE_CARD:
				for ( int close = 0; close < 2; close++ ) {
					CardAnchor prevAnchor = mMoveCard.GetAnchor();
					boolean unhide = (prevAnchor.GetVisibleCount() == 0 && prevAnchor.GetCount() > 0);
					int count = mMoveCard.GetCount();

					for ( int i = 0; i < mCardAnchor.length; i++ ) {
						if ( mCardAnchor[i] != prevAnchor ) {
							if ( mCardAnchor[i].CanDropCard( mMoveCard, close ) ) {
								mMoveHistory.push( new Move( prevAnchor.GetNumber(), i, count, false, unhide ) );
								mCardAnchor[i].AddMoveCard( mMoveCard );
								if ( mViewMode == MODE_MOVE_CARD ) {
									ChangeViewMode( MODE_NORMAL );
								}
								return true;
							}
						}
					}
				}
				if ( !mMoveCard.HasMoved() ) {
					CardAnchor anchor = mMoveCard.GetAnchor();
					mMoveCard.Release();
					if ( anchor.ExpandStack( x, y ) ) {
						mSelectCard.InitFromAnchor( anchor );
						ChangeViewMode( MODE_CARD_SELECT );
					} else {
						ChangeViewMode( MODE_NORMAL );
					}
				} else if ( mSpeed.IsFast() && mMoveCard.GetCount() == 1 ) {
					if ( !mRules.Fling( mMoveCard ) ) {
						ChangeViewMode( MODE_NORMAL );
					}
				} else {
					mMoveCard.Release();
					ChangeViewMode( MODE_NORMAL );
				}
				return true;
			case MODE_CARD_SELECT:
				if ( !mSelectCard.IsOnCard() && !mHasMoved ) {
					mSelectCard.Release();
					ChangeViewMode( MODE_NORMAL );
					return true;
				}
				break;
		}

		return false;
	}

	public boolean onDown( float x, float y ) {
		switch ( mViewMode ) {
			case MODE_NORMAL:
				Card card = null;
				for ( int i = 0; i < mCardAnchor.length; i++ ) {
					card = mCardAnchor[i].GrabCard( x, y );
					if ( card != null ) {
						if ( y < card.GetY() + Card.getHeight() / 4 ) {
							boolean lastIgnore = mRules.GetIgnoreEvents();
							mRules.SetIgnoreEvents( true );
							mCardAnchor[i].AddCard( card );
							mRules.SetIgnoreEvents( lastIgnore );
							if ( mCardAnchor[i].ExpandStack( x, y ) ) {
								mMoveCard.InitFromAnchor( mCardAnchor[i], x - Card.getWidth() / 2, y - Card.getHeight() / 2 );
								ChangeViewMode( MODE_MOVE_CARD );
								break;
							}
							card = mCardAnchor[i].PopCard();
						}
						mMoveCard.SetAnchor( mCardAnchor[i] );
						mMoveCard.AddCard( card );
						ChangeViewMode( MODE_MOVE_CARD );
						break;
					}
				}
				break;
			case MODE_CARD_SELECT:
				mSelectCard.Tap( x, y );
				break;
		}
		return true;
	}

	public boolean onMove( float dx, float dy, float x, float y ) {
		mSpeed.AddSpeed( dx, dy );
		switch ( mViewMode ) {
			case MODE_NORMAL:
				if ( Math.abs( mDownPoint.x - x ) > 15 || Math.abs( mDownPoint.y - y ) > 15 ) {
					for ( int i = 0; i < mCardAnchor.length; i++ ) {
						if ( mCardAnchor[i].CanMoveStack( mDownPoint.x, mDownPoint.y ) ) {
							mMoveCard.InitFromAnchor( mCardAnchor[i], x - Card.getWidth() / 2, y - Card.getHeight() / 2 );
							ChangeViewMode( MODE_MOVE_CARD );
							return true;
						}
					}
				}
				break;
			case MODE_MOVE_CARD:
				mMoveCard.MovePosition( dx, dy );
				return true;
			case MODE_CARD_SELECT:
				if ( mSelectCard.IsOnCard() && Math.abs( mDownPoint.x - x ) > 30 ) {
					mMoveCard.InitFromSelectCard( mSelectCard, x, y );
					ChangeViewMode( MODE_MOVE_CARD );
				} else {
					mSelectCard.Scroll( dy );
					if ( !mSelectCard.IsOnCard() ) {
						mSelectCard.Tap( x, y );
					}
				}
				return true;
		}

		return false;
	}

	private void CheckMoved( float x, float y ) {
		if ( x >= mDownPoint.x - 30 && x <= mDownPoint.x + 30 &&
			y >= mDownPoint.y - 30 && y <= mDownPoint.y + 30 ) {
			mHasMoved = false;
		} else {
			mHasMoved = true;
		}
	}

	public void StartAnimating() {
		DrawBoard();
		if ( mViewMode != MODE_WIN && mViewMode != MODE_ANIMATE ) {
			ChangeViewMode( MODE_ANIMATE );
		}
	}

	public void StopAnimating() {
		if ( mViewMode == MODE_ANIMATE ) {
			ChangeViewMode( MODE_NORMAL );
		} else if ( mViewMode == MODE_WIN ) {
			ChangeViewMode( MODE_WIN_STOP );
		}
	}

	public void Undo() {
		if ( mViewMode != MODE_NORMAL && mViewMode != MODE_WIN ) {
			return;
		}
		boolean oldIgnore = mRules.GetIgnoreEvents();
		mRules.SetIgnoreEvents( true );

		mMoveCard.Release();
		mSelectCard.Release();

		if ( !mMoveHistory.empty() ) {
			Move move = mMoveHistory.pop();
			int count = 0;
			int from = move.GetFrom();
			if ( move.GetToBegin() != move.GetToEnd() ) {
				for ( int i = move.GetToBegin(); i <= move.GetToEnd(); i++ ) {
					for ( int j = 0; j < move.GetCount(); j++ ) {
						mUndoStorage[count++] = mCardAnchor[i].PopCard();
					}
				}
			} else {
				for ( int i = 0; i < move.GetCount(); i++ ) {
					mUndoStorage[count++] = mCardAnchor[move.GetToBegin()].PopCard();
				}
			}
			if ( move.GetUnhide() ) {
				mCardAnchor[from].SetHiddenCount( mCardAnchor[from].GetHiddenCount() + 1 );
			}
			if ( move.GetInvert() ) {
				for ( int i = 0; i < count; i++ ) {
					mCardAnchor[from].AddCard( mUndoStorage[i] );
				}
			} else {
				for ( int i = count - 1; i >= 0; i-- ) {
					mCardAnchor[from].AddCard( mUndoStorage[i] );
				}
			}
			if ( move.GetAddDealCount() ) {
				mRules.AddDealCount();
			}
			if ( mUndoStorage[0].GetValue() == 1 ) {
				for ( int i = 0; i < mCardAnchor[from].GetCount(); i++ ) {
					Card card = mCardAnchor[from].GetCards()[i];
				}
			}
			Refresh();
		}
		mRules.SetIgnoreEvents( oldIgnore );
	}

	private void MarkAttempt() {
		StatsManager stats = new StatsManager( mContext );
		int currentAttempts = stats.getGameAttempts( mRules );
		stats.setGameAttempts( mRules, currentAttempts + 1 );
	}

	private void MarkWin() {
		StatsManager stats = new StatsManager( mContext );
		int bestTime = stats.getBestGameTime( mRules );
		if ( bestTime == -1 || mElapsed < bestTime ) {
			stats.setBestGameTime( mRules, mElapsed );
		}

		int wins = stats.getGameWins( mRules );
		stats.setGameWins( mRules, wins + 1 );

		if ( mRules.HasScore() ) {
			mWinningScore = mRules.GetScore();
			if ( mWinningScore > stats.getGameHighScore( mRules ) ) {
				stats.setBestGameScore( mRules, mWinningScore );
			}
		}
	}

	// Simple function to check for a consistent state in Solitaire.
	private void SanityCheck() {
		int cardCount;
		int diffCardCount;
		int matchCount;
		String type = mRules.GetGameTypeString();
		if ( type.equals( "Spider1Suit" ) ) {
			cardCount = 13;
			matchCount = 8;
		} else if ( type.equals( "Spider2Suit" ) ) {
			cardCount = 26;
			matchCount = 4;
		} else if ( type.equals( "Spider4Suit" ) ) {
			cardCount = 52;
			matchCount = 2;
		} else if ( type.equals( "Forty Thieves" ) ) {
			cardCount = 52;
			matchCount = 2;
		} else {
			cardCount = 52;
			matchCount = 1;
		}

		int[] cards = new int[cardCount];
		for ( int i = 0; i < cardCount; i++ ) {
			cards[i] = 0;
		}
		for ( int i = 0; i < mCardAnchor.length; i++ ) {
			for ( int j = 0; j < mCardAnchor[i].GetCount(); j++ ) {
				Card card = mCardAnchor[i].GetCards()[j];
				int idx = card.GetSuit() * 13 + card.GetValue() - 1;
				if ( cards[idx] >= matchCount ) {
					mHelpTextView.setTextSize( 20 );
					mHelpTextView.setGravity( Gravity.CENTER );
					DisplayHelpText( "Sanity Check Failed\nExtra: " + card.GetValue() + " " + card.GetSuit() );
					return;
				}
				cards[idx]++;
			}
		}
		for ( int i = 0; i < cardCount; i++ ) {
			if ( cards[i] != matchCount ) {
				mHelpTextView.setTextSize( 20 );
				mHelpTextView.setGravity( Gravity.CENTER );
				DisplayHelpText( "Sanity Check Failed\nMissing: " + (i % 13 + 1) + " " + i / 13 );
				return;
			}
		}
	}

	public void RefreshOptions() {
		mRules.RefreshOptions();
		SolitairePrefs prefs = new SolitairePrefs( mContext );
		SetDisplayTime( prefs.displayTime() );
	}

}

class RefreshHandler implements Runnable {
	public static final int NO_REFRESH = 1;
	public static final int SINGLE_REFRESH = 2;
	public static final int LOCK_REFRESH = 3;

	private static final int FPS = 30;

	private boolean mRun;
	private int mRefresh;
	private SolitaireView mView;

	public RefreshHandler( SolitaireView solitaireView ) {
		mView = solitaireView;
		mRun = true;
		mRefresh = NO_REFRESH;
	}

	public void SetRefresh( int refresh ) {
		synchronized ( this ) {
			mRefresh = refresh;
		}
	}

	public void SingleRefresh() {
		synchronized ( this ) {
			if ( mRefresh == NO_REFRESH ) {
				mRefresh = SINGLE_REFRESH;
			}
		}
	}

	public void SetRunning( boolean run ) {
		mRun = run;
	}

	public void run() {
		while ( mRun ) {
			try {
				Thread.sleep( 1000 / FPS );
			} catch ( InterruptedException e ) {
			}
			mView.UpdateTime();
			if ( mRefresh != NO_REFRESH ) {
				mView.postInvalidate();
				if ( mRefresh == SINGLE_REFRESH ) {
					SetRefresh( NO_REFRESH );
				}
			}
		}
	}
}

class Speed {
	private static final int SPEED_COUNT = 4;
	private static final float SPEED_THRESHOLD = 10 * 10;
	private float[] mSpeed;
	private int mIdx;

	public Speed() {
		mSpeed = new float[SPEED_COUNT];
		Reset();
	}

	public void Reset() {
		mIdx = 0;
		for ( int i = 0; i < SPEED_COUNT; i++ ) {
			mSpeed[i] = 0;
		}
	}

	public void AddSpeed( float dx, float dy ) {
		mSpeed[mIdx] = dx * dx + dy * dy;
		mIdx = (mIdx + 1) % SPEED_COUNT;
	}

	public boolean IsFast() {
		for ( int i = 0; i < SPEED_COUNT; i++ ) {
			if ( mSpeed[i] > SPEED_THRESHOLD ) {
				return true;
			}
		}
		return false;
	}
}

