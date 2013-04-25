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
		Bitmap face = Bitmap.createBitmap( width, height * 2, Bitmap.Config.ARGB_4444 );

		Canvas canvas = new Canvas( face );
		drawable.setBounds( 0, 0, width, height );
		drawable.draw( canvas );

		canvas.rotate( 180, width / 2, height / 2 );
		drawable.setBounds( 0, -height, width, 0 );
		drawable.draw( canvas );
		canvas.rotate( 180, width / 2, height / 2 );

		Paint paint = new Paint();
		paint.setARGB( 255, 0, 0, 0 );
		paint.setStrokeWidth( 1 );
		paint.setStyle( Paint.Style.STROKE );

		Rect border = canvas.getClipBounds();
		canvas.drawRect( 0, 0, border.width() - 1, border.height() - 1, paint );

		return face;
	}

}
