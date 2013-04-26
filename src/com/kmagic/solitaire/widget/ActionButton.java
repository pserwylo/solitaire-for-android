package com.kmagic.solitaire.widget;

import android.*;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.kmagic.solitaire.*;
import com.kmagic.solitaire.R;

public class ActionButton extends ImageButton {

	public ActionButton( Context context ) {
		super( context );
		this.hide();
	}

	public ActionButton( Context context, AttributeSet attrs ) {
		super( context, attrs );
		this.hide();
	}

	public ActionButton( Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs, defStyle );
		this.hide();
	}

	private void hide() {
		this.setVisibility( View.INVISIBLE );
	}

	private void show() {
		this.setVisibility( View.VISIBLE );
	}

	public void setAction( final GameAction action ) {
		if ( action == null ) {
			this.hide();
			this.setOnClickListener( null );
		} else {
			this.show();
			this.setImageResource( action.getResourceId() );
			this.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick( View v ) {
					action.notifyPerformed();
				}
			});
		}
	}
}
