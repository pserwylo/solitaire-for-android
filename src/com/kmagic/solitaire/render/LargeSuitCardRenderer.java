package com.kmagic.solitaire.render;

import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import com.kmagic.solitaire.*;

public class LargeSuitCardRenderer extends CardRenderer {

	public LargeSuitCardRenderer( Resources r, Paint suitPaint ) {
		super( r, suitPaint );
	}

	@Override
	protected void renderCards() {

		Paint cardFrontPaint = new Paint();
		Paint cardBorderPaint = new Paint();
		Bitmap[] bigSuit;
		Bitmap[] suit;
		Bitmap[] blackFont;
		Bitmap[] redFont;
		Canvas canvas;
		int width = Card.WIDTH;
		int height = Card.HEIGHT;

		Drawable drawable = resources.getDrawable( R.drawable.cardback );

		SuitRenderer suitRenderer = new SuitRenderer();
		Drawable bigSuitDrawable = resources.getDrawable( R.drawable.bigsuits );
		Drawable suitDrawable    = resources.getDrawable( R.drawable.suits );
		bigSuit = suitRenderer.render( bigSuitDrawable, 25, 25 );
		suit    = suitRenderer.render( suitDrawable, 10, 10 );

		FontRenderer fontRenderer  = new FontRenderer( 18, 15 );
		Drawable blackFontDrawable = resources.getDrawable( R.drawable.bigblackfont );
		Drawable redFontDrawable   = resources.getDrawable( R.drawable.bigredfont );
		blackFont = fontRenderer.render( blackFontDrawable );
		redFont   = fontRenderer.render( redFontDrawable );

		cardBorderPaint.setARGB( 255, 0, 0, 0 );
		cardFrontPaint.setARGB( 255, 255, 255, 255 );
		RectF pos = new RectF();
		for ( int suitIdx = 0; suitIdx < 4; suitIdx++ ) {
			for ( int valueIdx = 0; valueIdx < 13; valueIdx++ ) {
				getCards()[suitIdx * 13 + valueIdx] = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_4444 );
				canvas = new Canvas( getCards()[suitIdx * 13 + valueIdx] );
				pos.set( 0, 0, width, height );
				canvas.drawRoundRect( pos, 4, 4, cardBorderPaint );
				pos.set( 1, 1, width - 1, height - 1 );
				canvas.drawRoundRect( pos, 4, 4, cardFrontPaint );

				if ( (suitIdx & 1) == 1 ) {
					canvas.drawBitmap( redFont[valueIdx], 3, 4, suitPaint );
				} else {
					canvas.drawBitmap( blackFont[valueIdx], 3, 4, suitPaint );
				}


				canvas.drawBitmap( suit[suitIdx], width - 14, 4, suitPaint );
				canvas.drawBitmap( bigSuit[suitIdx], width / 2 - 12, height / 2 - 13, suitPaint );
			}
		}
	}
}
