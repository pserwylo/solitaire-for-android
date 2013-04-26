package com.kmagic.solitaire;

/**
 * A game action is something the user can perform while playing a particular game.
 * It is indicated by a button on screen with a particular icon.
 */
public class GameAction {

	private Listener listener;
	private int resId;

	public GameAction( int resId, Listener listener ) {
		this.resId    = resId;
		this.listener = listener;
	}

	public int getResourceId() {
		return resId;
	}

	public void notifyPerformed() {
		listener.onActionPerformed( this );
	}

	public static interface Listener {
		abstract public void onActionPerformed( GameAction action );
	}

}
