package com.kmagic.solitaire.render;

import android.graphics.*;
import android.graphics.drawable.*;

public class FaceRenderer {

	private final int width;
	private final int height;

	public FaceRenderer( int width, int height ) {
		this.width  = width;
		this.height = height;
	}

	public Bitmap render( Drawable drawable ) {
		return render( drawable, false );
	}

	public Bitmap renderReversed( Drawable drawable ) {
		return render( drawable, true );
	}

	protected Bitmap render( Drawable drawable, boolean reversed ) {
		Bitmap face = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_4444 );
		Canvas canvas = new Canvas( face );
		if ( reversed ) {
			canvas.rotate( 180 );
			drawable.setBounds( -width, -height, 0, 0 );
		} else {
			drawable.setBounds( 0, 0, width, height );
		}
		drawable.draw( canvas );
		return face;
	}

}
