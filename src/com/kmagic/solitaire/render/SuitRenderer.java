package com.kmagic.solitaire.render;

import android.graphics.*;
import android.graphics.drawable.*;
import com.kmagic.solitaire.*;

public class SuitRenderer {

	public Bitmap[] render( Drawable drawable, int width, int height ) {
		return render( drawable, width, height, false );
	}

	public Bitmap[] renderReversed( Drawable drawable, int width, int height ) {
		return render( drawable, width, height, true );
	}

	/*drawable.setBounds( -i * 10, 0, -i * 10 + 40, 10 );
	drawable.draw( canvas );
	canvas = new Canvas( revSuit[i] );
	canvas.rotate( 180 );
	drawable.setBounds( -i * 10 - 10, -10, -i * 10 + 30, 0 );*/

	protected Bitmap[] render( Drawable drawable, int width, int height, boolean reversed ) {
		Bitmap[] suits = new Bitmap[4];
		for ( int i = 0; i < 4; i++ ) {
			suits[i] = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_4444 );
			Canvas canvas = new Canvas( suits[i] );
			if ( reversed ) {
				canvas.rotate( 180 );
				drawable.setBounds( -i * width - width, -height, -i * width + width * 3, 0 );
			} else {
				drawable.setBounds( -i * width, 0, -i * width + width * 4, height );
			}
			drawable.draw( canvas );
		}
		return suits;
	}

}
