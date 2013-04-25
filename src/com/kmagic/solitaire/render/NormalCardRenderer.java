package com.kmagic.solitaire.render;

import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import com.kmagic.solitaire.*;

public class NormalCardRenderer extends CardRenderer {

	private final SizeCalculator size = new SizeCalculator();

	private Paint cardFrontPaint = new Paint();
	private Paint cardBorderPaint = new Paint();

	private Bitmap[] suit = new Bitmap[4];
	private Bitmap[] revSuit = new Bitmap[4];
	private Bitmap[] smallSuit = new Bitmap[4];
	private Bitmap[] revSmallSuit = new Bitmap[4];
	private Bitmap[] blackFont = new Bitmap[13];
	private Bitmap[] revBlackFont = new Bitmap[13];
	private Bitmap[] redFont = new Bitmap[13];
	private Bitmap[] revRedFont = new Bitmap[13];
	private Bitmap redJack;
	private Bitmap redQueen;
	private Bitmap redKing;
	private Bitmap blackJack;
	private Bitmap blackQueen;
	private Bitmap blackKing;

	public NormalCardRenderer( Resources r, Paint suitPaint ) {
		super( r, suitPaint );

		cardBorderPaint.setARGB( 255, 0, 0, 0 );
		cardFrontPaint.setARGB( 255, 255, 255, 255 );
	}

	private void prepareSuits() {
		SuitRenderer suitRenderer = new SuitRenderer();

		Drawable suitDrawable = resources.getDrawable( R.drawable.suits_25x25 );
		suit    = suitRenderer.render( suitDrawable, size.SUIT_WIDTH, size.SUIT_HEIGHT );
		revSuit = suitRenderer.renderReversed( suitDrawable, size.SUIT_WIDTH, size.SUIT_HEIGHT );

		smallSuit    = suitRenderer.render( suitDrawable, size.SMALL_SUIT_WIDTH, size.SMALL_SUIT_HEIGHT );
		revSmallSuit = suitRenderer.renderReversed( suitDrawable, size.SMALL_SUIT_WIDTH, size.SMALL_SUIT_HEIGHT );
	}

	private void prepareFaces() {

		FaceRenderer faceRenderer = new FaceRenderer( size.FACE_WIDTH, size.FACE_HEIGHT );

		Drawable redJackDrawable = resources.getDrawable( R.drawable.redjack );
		redJack = faceRenderer.render( redJackDrawable );

		Drawable redQueenDrawable = resources.getDrawable( R.drawable.redqueen );
		redQueen = faceRenderer.render( redQueenDrawable );

		Drawable redKingDrawable = resources.getDrawable( R.drawable.redking );
		redKing = faceRenderer.render( redKingDrawable );

		Drawable blackJackDrawable = resources.getDrawable( R.drawable.blackjack );
		blackJack = faceRenderer.render( blackJackDrawable );

		Drawable blackQueenDrawable = resources.getDrawable( R.drawable.blackqueen );
		blackQueen = faceRenderer.render( blackQueenDrawable );

		Drawable blackKingDrawable = resources.getDrawable( R.drawable.blackking );
		blackKing = faceRenderer.render( blackKingDrawable );

	}

	private void prepareFonts() {
		FontRenderer renderer = new FontRenderer( size.FONT_WIDTH, size.FONT_HEIGHT );

		Drawable blackFontDrawable = resources.getDrawable( R.drawable.font_black_195x18 );
		blackFont    = renderer.render( blackFontDrawable );
		revBlackFont = renderer.renderReversed( blackFontDrawable );

		Drawable redFontDrawable = resources.getDrawable( R.drawable.font_black_195x18 );
		redFontDrawable.setColorFilter( 0xffff0000, PorterDuff.Mode.SRC_ATOP );
		redFont    = renderer.render( redFontDrawable );
		revRedFont = renderer.renderReversed( redFontDrawable );
	}

	public void renderCards() {

		prepareSuits();
		prepareFaces();
		prepareFonts();

		for ( int suitIdx = 0; suitIdx < 4; suitIdx++ ) {
			for ( int valueIdx = 0; valueIdx < 13; valueIdx++ ) {
				prepareCard( suitIdx, valueIdx );
			}
		}
	}

	private void prepareCard( int suit, int value ) {

		int cardIndex = suit * 13 + value;
		getCards()[ cardIndex ] = Bitmap.createBitmap( Card.getWidth(), Card.getHeight(), Bitmap.Config.ARGB_4444 );
		Canvas canvas = new Canvas( getCards()[ cardIndex ] );

		int width = Card.getWidth();
		int height = Card.getHeight();

		RectF pos = new RectF( 0, 0, width, height );
		canvas.drawRoundRect( pos, size.CARD_ROUNDED_CORNER, size.CARD_ROUNDED_CORNER, cardBorderPaint );
		pos.set( 1, 1, width - 1, height - 1 );
		canvas.drawRoundRect( pos, size.CARD_ROUNDED_CORNER, size.CARD_ROUNDED_CORNER, cardFrontPaint );

		boolean isBlack = ( suit == Card.SPADES || suit == Card.CLUBS );

		Bitmap[] font    = isBlack ? blackFont    : redFont;
		Bitmap[] revFont = isBlack ? revBlackFont : revRedFont;
		canvas.drawBitmap( font[ value ], size.FONT_PADDING, size.FONT_PADDING * 2, suitPaint );
		canvas.drawBitmap( revFont[ value ], width - size.FONT_WIDTH - size.FONT_PADDING, height - size.FONT_HEIGHT - size.FONT_PADDING * 2, suitPaint );

		canvas.drawBitmap(
			smallSuit[suit],
			size.FONT_PADDING + ( size.FONT_WIDTH - size.SMALL_SUIT_WIDTH ) / 2,
			size.FONT_PADDING * 3 + size.FONT_HEIGHT,
			suitPaint );

		canvas.drawBitmap(
			revSmallSuit[suit],
			width - size.FONT_PADDING - size.SMALL_SUIT_WIDTH - ( size.FONT_WIDTH - size.SMALL_SUIT_WIDTH ) / 2,
			height - size.SMALL_SUIT_HEIGHT - size.FONT_HEIGHT - size.FONT_PADDING * 3,
			suitPaint );

		if ( value >= 10 ) {
			canvas.drawBitmap(
				this.suit[suit],
				size.FACE_CARD_PADDING + size.FONT_PADDING / 2,
				size.FACE_CARD_PADDING + size.FONT_PADDING - 1,
				suitPaint );
			canvas.drawBitmap(
				revSuit[suit],
				width - size.SUIT_WIDTH - size.FACE_CARD_PADDING - size.FONT_PADDING / 2,
				height - size.SUIT_HEIGHT - size.FACE_CARD_PADDING - size.FONT_PADDING - 1,
				suitPaint );
		}

		int[] suitX = new int[ 5 ];
		float xIncrement = (float)width / suitX.length;
		for ( int i = 0; i < suitX.length; i ++ ) {
			suitX[ i ] = (int)( xIncrement * i );
		}

		int[] suitY = new int[ 9 ];
		float yIncrement = (float)height / suitY.length;
		for ( int i = 0; i < suitY.length; i ++ ) {
			suitY[ i ] = (int)( yIncrement * i );
		}

		switch ( value + 1 ) {
			case 1:
				canvas.drawBitmap( this.suit[suit], suitX[ 2 ], suitY[ 4 ], suitPaint );
				break;
			case 2:
				canvas.drawBitmap( this.suit[suit], suitX[ 2 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 2 ], suitY[ 7 ], suitPaint );
				break;
			case 3:
				canvas.drawBitmap( this.suit[suit], suitX[ 2 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 2 ], suitY[ 4 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 2 ], suitY[ 7 ], suitPaint );
				break;
			case 4:
				canvas.drawBitmap( this.suit[suit], suitX[ 1 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 3 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 1 ], suitY[ 7 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 3 ], suitY[ 7 ], suitPaint );
				break;
			case 5:
				canvas.drawBitmap( this.suit[suit], suitX[ 1 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 3 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 2 ], suitY[ 4 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 1 ], suitY[ 7 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 3 ], suitY[ 7 ], suitPaint );
				break;
			case 6:
				canvas.drawBitmap( this.suit[suit], suitX[ 1 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 3 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 1 ], suitY[ 4 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 3 ], suitY[ 4 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 1 ], suitY[ 7 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 3 ], suitY[ 7 ], suitPaint );
				break;
			case 7:
				canvas.drawBitmap( this.suit[suit], suitX[ 1 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 3 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 1 ], suitY[ 4 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 3 ], suitY[ 4 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 2 ], suitY[ 3 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 1 ], suitY[ 7 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 3 ], suitY[ 7 ], suitPaint );
				break;
			case 8:
				canvas.drawBitmap( this.suit[suit], suitX[ 1 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 3 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 1 ], suitY[ 4 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 3 ], suitY[ 4 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 2 ], suitY[ 2 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 1 ], suitY[ 7 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 3 ], suitY[ 7 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 2 ], suitY[ 6 ], suitPaint );
				break;
			case 9:
				canvas.drawBitmap( this.suit[suit], suitX[ 1 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 1 ], suitY[ 3 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 1 ], suitY[ 5 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 1 ], suitY[ 7 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 2 ], suitY[ 4 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 3 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 3 ], suitY[ 3 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 3 ], suitY[ 5 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 3 ], suitY[ 7 ], suitPaint );
				break;
			case 10:
				canvas.drawBitmap( this.suit[suit], suitX[ 1 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 1 ], suitY[ 3 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 1 ], suitY[ 5 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 1 ], suitY[ 7 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 2 ], suitY[ 2 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 2 ], suitY[ 6 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 3 ], suitY[ 1 ], suitPaint );
				canvas.drawBitmap( this.suit[suit], suitX[ 3 ], suitY[ 3 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 3 ], suitY[ 5 ], suitPaint );
				canvas.drawBitmap( revSuit[suit], suitX[ 3 ], suitY[ 7 ], suitPaint );
				break;

			case Card.JACK:
				canvas.drawBitmap( isBlack ? blackJack : redJack, size.FACE_CARD_PADDING, size.FACE_CARD_PADDING, suitPaint );
				break;
			case Card.QUEEN:
				canvas.drawBitmap( isBlack ? blackQueen : redQueen, size.FACE_CARD_PADDING, size.FACE_CARD_PADDING, suitPaint );
				break;
			case Card.KING:
				canvas.drawBitmap( isBlack ? blackKing : redKing, size.FACE_CARD_PADDING, size.FACE_CARD_PADDING, suitPaint );
				break;
		}
	}
}