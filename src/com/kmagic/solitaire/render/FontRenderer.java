package com.kmagic.solitaire.render;

import android.graphics.*;
import android.graphics.drawable.*;

public class FontRenderer {

	private final int FONT_WIDTH;
	private final int FONT_HEIGHT;

	public FontRenderer( int width, int height ) {
		this.FONT_WIDTH = width;
		this.FONT_HEIGHT = height;
	}

	public Bitmap[] render( Drawable drawable ) {
		return render( drawable, false );
	}

	public Bitmap[] renderReversed( Drawable drawable ) {
		return render( drawable, true );
	}

	protected Bitmap[] render( Drawable drawable, boolean reverse ) {
		Bitmap[] characters = new Bitmap[13];
		for ( int i = 0; i < 13; i++ ) {
			characters[ i ] = Bitmap.createBitmap( FONT_WIDTH, FONT_HEIGHT, Bitmap.Config.ARGB_4444 );
			Canvas canvas = new Canvas( characters[ i ] );
			if ( reverse ) {
				canvas.rotate( 180 );
				drawable.setBounds( -i * FONT_WIDTH - FONT_WIDTH, -FONT_HEIGHT, -i * FONT_WIDTH + (12 * FONT_WIDTH), 0 );
			} else {
				drawable.setBounds( -i * FONT_WIDTH, 0, -i * FONT_WIDTH + 13 * FONT_WIDTH, FONT_HEIGHT );
			}
			drawable.draw( canvas );
		}
		return characters;
	}

}
