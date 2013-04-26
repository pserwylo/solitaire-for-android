package com.kmagic.solitaire.compat;

import android.os.*;
import android.view.*;
import android.widget.*;
import com.kmagic.solitaire.*;

abstract public class MainMenuCompat extends Compat {

	protected final Solitaire solitaire;

	public static MainMenuCompat create( Solitaire solitaire ) {
		if ( hasApi( 11 ) ) {
			return new PopupMainMenuImpl( solitaire );
		} else {
			return new ContextMainMenuImpl( solitaire );
		}
	}

	public MainMenuCompat( Solitaire solitaire ) {
		this.solitaire = solitaire;
	}

	protected void optionSelected( int id ) {
		switch ( id ) {
			case R.id.menu_solitaire:
				solitaire.initGame( Rules.SOLITAIRE );
				break;
			case R.id.menu_spider:
				solitaire.initGame( Rules.SPIDER );
				break;
			case R.id.menu_free_cell:
				solitaire.initGame( Rules.FREECELL );
				break;
			case R.id.menu_forty_thieves:
				solitaire.initGame( Rules.FORTYTHIEVES );
				break;
			case R.id.menu_restart:
				solitaire.restartGame();
				break;
			case R.id.menu_stats:
				solitaire.DisplayStats();
				break;
			case R.id.menu_options:
				solitaire.DisplayOptions();
				break;
			case R.id.menu_help:
				solitaire.displayHelp();
				break;
			case R.id.menu_save_quit:
				solitaire.quit( true );
				break;
			case R.id.menu_quit:
				solitaire.quit( false );
				break;
		}
	}

	abstract public boolean onCreateOptionsMenu( Menu menu );

	abstract public boolean onCreate( Bundle savedInstanceState );

	abstract public boolean onOptionsItemSelected( MenuItem item );

}

class PopupMainMenuImpl extends ContextMainMenuImpl {

	public PopupMainMenuImpl( Solitaire solitaire ) {
		super( solitaire );
	}

	@Override
	public boolean onCreate( Bundle savedInstanceState ) {
		ImageButton btnMenu = (ImageButton)solitaire.findViewById( R.id.btn_menu );
		btnMenu.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View v ) {
				showOptions();
			}
		} );
		return true;
	}

	private void showOptions() {
		PopupMenu popup = new PopupMenu( solitaire, solitaire.findViewById( R.id.btn_menu ) );
		popup.getMenuInflater().inflate( R.menu.main, popup.getMenu() );
		popup.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick( MenuItem item ) {
				optionSelected( item.getItemId() );
				return true;
			}
		});
		popup.show();
	}

}

class ContextMainMenuImpl extends MainMenuCompat {

	public ContextMainMenuImpl( Solitaire solitaire ) {
		super( solitaire );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		solitaire.getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}

	@Override
	public boolean onCreate( Bundle savedInstanceState ) {
		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT );
		layout.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );
		layout.addRule( RelativeLayout.ALIGN_PARENT_BOTTOM );
		solitaire.findViewById( R.id.text_status ).setLayoutParams( layout );
		solitaire.findViewById( R.id.btn_menu ).setVisibility( View.GONE );
		return false;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		optionSelected( item.getItemId() );
		return true;
	}

}
