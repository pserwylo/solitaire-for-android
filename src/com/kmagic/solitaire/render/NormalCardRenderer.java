package com.kmagic.solitaire.render;

import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import com.kmagic.solitaire.*;

public class NormalCardRenderer extends CardRenderer {

	final int FACE_WIDTH  = Card.WIDTH - 20;
	final int FACE_HEIGHT = Card.HEIGHT / 2 - 9;

	final int FONT_WIDTH = 7;
	final int FONT_HEIGHT = 9;

	private Bitmap[] suit = new Bitmap[4];
	private Bitmap[] revSuit = new Bitmap[4];
	private Bitmap[] smallSuit = new Bitmap[4];
	private Bitmap[] revSmallSuit = new Bitmap[4];
	private Bitmap[] blackFont = new Bitmap[13];
	private Bitmap[] revBlackFont = new Bitmap[13];
	private Bitmap[] redFont = new Bitmap[13];
	private Bitmap[] revRedFont = new Bitmap[13];
	private Bitmap redJack;
	private Bitmap redRevJack;
	private Bitmap redQueen;
	private Bitmap redRevQueen;
	private Bitmap redKing;
	private Bitmap redRevKing;
	private Bitmap blackJack;
	private Bitmap blackRevJack;
	private Bitmap blackQueen;
	private Bitmap blackRevQueen;
	private Bitmap blackKing;
	private Bitmap blackRevKing;

	public NormalCardRenderer( Resources r, Paint suitPaint ) {
		super( r, suitPaint );
	}

	private void prepareSuits() {
		SuitRenderer suitRenderer = new SuitRenderer();

		Drawable suitDrawable = resources.getDrawable( R.drawable.suits );
		suit    = suitRenderer.render( suitDrawable, 10, 10 );
		revSuit = suitRenderer.renderReversed( suitDrawable, 10, 10 );

		Drawable tinySuitDrawable = resources.getDrawable( R.drawable.smallsuits );
		smallSuit    = suitRenderer.render( tinySuitDrawable, 5, 5 );
		revSmallSuit = suitRenderer.renderReversed( tinySuitDrawable, 5, 5 );
	}

	private void prepareFaces() {

		FaceRenderer faceRenderer = new FaceRenderer( FACE_WIDTH, FACE_HEIGHT );

		Drawable redJackDrawable = resources.getDrawable( R.drawable.redjack );
		redJack    = faceRenderer.render( redJackDrawable );
		redRevJack = faceRenderer.renderReversed( redJackDrawable );

		Drawable redQueenDrawable = resources.getDrawable( R.drawable.redqueen );
		redQueen    = faceRenderer.render( redQueenDrawable );
		redRevQueen = faceRenderer.renderReversed( redQueenDrawable );

		Drawable redKingDrawable = resources.getDrawable( R.drawable.redking );
		redKing    = faceRenderer.render( redKingDrawable );
		redRevKing = faceRenderer.renderReversed( redKingDrawable );

		Drawable blackJackDrawable = resources.getDrawable( R.drawable.blackjack );
		blackJack    = faceRenderer.render( blackJackDrawable );
		blackRevJack = faceRenderer.renderReversed( blackJackDrawable );

		Drawable blackQueenDrawable = resources.getDrawable( R.drawable.blackqueen );
		blackQueen    = faceRenderer.render( blackQueenDrawable );
		blackRevQueen = faceRenderer.renderReversed( blackQueenDrawable );

		Drawable blackKingDrawable = resources.getDrawable( R.drawable.blackking );
		blackKing    = faceRenderer.render( blackKingDrawable );
		blackRevKing = faceRenderer.renderReversed( blackKingDrawable );

	}

	private void prepareFonts() {
		FontRenderer renderer = new FontRenderer( FONT_WIDTH, FONT_HEIGHT );

		Drawable blackFontDrawable = resources.getDrawable( R.drawable.medblackfont );
		blackFont    = renderer.render( blackFontDrawable );
		revBlackFont = renderer.renderReversed( blackFontDrawable );

		Drawable redFontDrawable = resources.getDrawable( R.drawable.medredfont );
		redFont    = renderer.render( redFontDrawable );
		revRedFont = renderer.render( redFontDrawable );
	}

	public void renderCards() {

		prepareSuits();
		prepareFaces();
		prepareFonts();

		int width = Card.WIDTH;
		int height = Card.HEIGHT;
		float[] faceBox = { 9, 8, width - 10, 8, width - 10, 8, width - 10, height - 9, width - 10, height - 9, 9, height - 9, 9, height - 8, 9, 8 };

		Paint cardFrontPaint = new Paint();
		Paint cardBorderPaint = new Paint();
		cardBorderPaint.setARGB( 255, 0, 0, 0 );
		cardFrontPaint.setARGB( 255, 255, 255, 255 );

		RectF pos = new RectF();
		for ( int suitIdx = 0; suitIdx < 4; suitIdx++ ) {
			for ( int valueIdx = 0; valueIdx < 13; valueIdx++ ) {
				getCards()[suitIdx * 13 + valueIdx] = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_4444 );
				Canvas canvas = new Canvas( getCards()[suitIdx * 13 + valueIdx] );
				pos.set( 0, 0, width, height );
				canvas.drawRoundRect( pos, 4, 4, cardBorderPaint );
				pos.set( 1, 1, width - 1, height - 1 );
				canvas.drawRoundRect( pos, 4, 4, cardFrontPaint );

				if ( (suitIdx & 1) == 1 ) {
					canvas.drawBitmap( redFont[valueIdx], 2, 4, suitPaint );
					canvas.drawBitmap( revRedFont[valueIdx], width - FONT_WIDTH - 2, height - FONT_HEIGHT - 4, suitPaint );
				} else {
					canvas.drawBitmap( blackFont[valueIdx], 2, 4, suitPaint );
					canvas.drawBitmap( revBlackFont[valueIdx], width - FONT_WIDTH - 2, height - FONT_HEIGHT - 4, suitPaint );
				}
				if ( FONT_WIDTH > 6 ) {
					canvas.drawBitmap( smallSuit[suitIdx], 3, 5 + FONT_HEIGHT, suitPaint );
					canvas.drawBitmap( revSmallSuit[suitIdx], width - 7, height - 11 - FONT_HEIGHT, suitPaint );
				} else {
					canvas.drawBitmap( smallSuit[suitIdx], 2, 5 + FONT_HEIGHT, suitPaint );
					canvas.drawBitmap( revSmallSuit[suitIdx], width - 6, height - 11 - FONT_HEIGHT, suitPaint );
				}

				if ( valueIdx >= 10 ) {
					canvas.drawBitmap( suit[suitIdx], 10, 9, suitPaint );
					canvas.drawBitmap( revSuit[suitIdx], width - 21, height - 20, suitPaint );
				}

				int[] suitX = { 9, width / 2 - 5, width - 20 };
				int[] suitY = { 7, 2 * height / 5 - 5, 3 * height / 5 - 5, height - 18 };
				int suitMidY = height / 2 - 6;
				switch ( valueIdx + 1 ) {
					case 1:
						canvas.drawBitmap( suit[suitIdx], suitX[1], suitMidY, suitPaint );
						break;
					case 2:
						canvas.drawBitmap( suit[suitIdx], suitX[1], suitY[0], suitPaint );
						canvas.drawBitmap( revSuit[suitIdx], suitX[1], suitY[3], suitPaint );
						break;
					case 3:
						canvas.drawBitmap( suit[suitIdx], suitX[1], suitY[0], suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[1], suitMidY, suitPaint );
						canvas.drawBitmap( revSuit[suitIdx], suitX[1], suitY[3], suitPaint );
						break;
					case 4:
						canvas.drawBitmap( suit[suitIdx], suitX[0], suitY[0], suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[2], suitY[0], suitPaint );
						canvas.drawBitmap( revSuit[suitIdx], suitX[0], suitY[3], suitPaint );
						canvas.drawBitmap( revSuit[suitIdx], suitX[2], suitY[3], suitPaint );
						break;
					case 5:
						canvas.drawBitmap( suit[suitIdx], suitX[0], suitY[0], suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[2], suitY[0], suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[1], suitMidY, suitPaint );
						canvas.drawBitmap( revSuit[suitIdx], suitX[0], suitY[3], suitPaint );
						canvas.drawBitmap( revSuit[suitIdx], suitX[2], suitY[3], suitPaint );
						break;
					case 6:
						canvas.drawBitmap( suit[suitIdx], suitX[0], suitY[0], suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[2], suitY[0], suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[0], suitMidY, suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[2], suitMidY, suitPaint );
						canvas.drawBitmap( revSuit[suitIdx], suitX[0], suitY[3], suitPaint );
						canvas.drawBitmap( revSuit[suitIdx], suitX[2], suitY[3], suitPaint );
						break;
					case 7:
						canvas.drawBitmap( suit[suitIdx], suitX[0], suitY[0], suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[2], suitY[0], suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[0], suitMidY, suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[2], suitMidY, suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[1], (suitMidY + suitY[0]) / 2, suitPaint );
						canvas.drawBitmap( revSuit[suitIdx], suitX[0], suitY[3], suitPaint );
						canvas.drawBitmap( revSuit[suitIdx], suitX[2], suitY[3], suitPaint );
						break;
					case 8:
						canvas.drawBitmap( suit[suitIdx], suitX[0], suitY[0], suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[2], suitY[0], suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[0], suitMidY, suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[2], suitMidY, suitPaint );
						canvas.drawBitmap( suit[suitIdx], suitX[1], (suitMidY + suitY[0]) / 2, suitPaint );
						canvas.drawBitmap( revSuit[suitIdx], suitX[0], suitY[3], suitPaint );
						canvas.drawBitmap( revSuit[suitIdx], suitX[2], suitY[3], suitPaint );
						canvas.drawBitmap( revSuit[suitIdx], suitX[1], (suitY[3] + suitMidY) / 2, suitPaint );
						break;
					case 9:
						for ( int i = 0; i < 4; i++ ) {
							canvas.drawBitmap( suit[suitIdx], suitX[(i % 2) * 2], suitY[i / 2], suitPaint );
							canvas.drawBitmap( revSuit[suitIdx], suitX[(i % 2) * 2], suitY[i / 2 + 2], suitPaint );
						}
						canvas.drawBitmap( suit[suitIdx], suitX[1], suitMidY, suitPaint );
						break;
					case 10:
						for ( int i = 0; i < 4; i++ ) {
							canvas.drawBitmap( suit[suitIdx], suitX[(i % 2) * 2], suitY[i / 2], suitPaint );
							canvas.drawBitmap( revSuit[suitIdx], suitX[(i % 2) * 2], suitY[i / 2 + 2], suitPaint );
						}
						canvas.drawBitmap( suit[suitIdx], suitX[1], (suitY[1] + suitY[0]) / 2, suitPaint );
						canvas.drawBitmap( revSuit[suitIdx], suitX[1], (suitY[3] + suitY[2]) / 2, suitPaint );
						break;

					case Card.JACK:
						canvas.drawLines( faceBox, cardBorderPaint );
						if ( (suitIdx & 1) == 1 ) {
							canvas.drawBitmap( redJack, 10, 9, suitPaint );
							canvas.drawBitmap( redRevJack, 10, height - FACE_HEIGHT - 9, suitPaint );
						} else {
							canvas.drawBitmap( blackJack, 10, 9, suitPaint );
							canvas.drawBitmap( blackRevJack, 10, height - FACE_HEIGHT - 9, suitPaint );
						}
						break;
					case Card.QUEEN:
						canvas.drawLines( faceBox, cardBorderPaint );
						if ( (suitIdx & 1) == 1 ) {
							canvas.drawBitmap( redQueen, 10, 9, suitPaint );
							canvas.drawBitmap( redRevQueen, 10, height - FACE_HEIGHT - 9, suitPaint );
						} else {
							canvas.drawBitmap( blackQueen, 10, 9, suitPaint );
							canvas.drawBitmap( blackRevQueen, 10, height - FACE_HEIGHT - 9, suitPaint );
						}
						break;
					case Card.KING:
						canvas.drawLines( faceBox, cardBorderPaint );
						if ( (suitIdx & 1) == 1 ) {
							canvas.drawBitmap( redKing, 10, 9, suitPaint );
							canvas.drawBitmap( redRevKing, 10, height - FACE_HEIGHT - 9, suitPaint );
						} else {
							canvas.drawBitmap( blackKing, 10, 9, suitPaint );
							canvas.drawBitmap( blackRevKing, 10, height - FACE_HEIGHT - 9, suitPaint );
						}
						break;
				}
			}
		}
	}
}