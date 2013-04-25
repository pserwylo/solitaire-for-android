package com.kmagic.solitaire.render;

import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import com.kmagic.solitaire.*;

abstract public class CardRenderer {

	protected final SizeCalculator size = new SizeCalculator();

	protected final Resources resources;
	protected final Paint suitPaint;

	private Bitmap[] cards = new Bitmap[52];
	private Bitmap hiddenCard;

	protected Bitmap[] blackFont = new Bitmap[13];
	protected Bitmap[] revBlackFont = new Bitmap[13];
	protected Bitmap[] redFont = new Bitmap[13];
	protected Bitmap[] revRedFont = new Bitmap[13];

	public CardRenderer( Resources r, Paint suitPaint ) {
		this.resources = r;
		this.suitPaint = suitPaint;
	}

	public final Bitmap getHiddenCard() {
		return hiddenCard;
	}

	public Bitmap[] getCards() {
		return cards;
	}

	public final void render() {
		renderHiddenCard();
		renderCards();
	}

	abstract protected void renderCards();

	protected void renderHiddenCard() {
		hiddenCard = Bitmap.createBitmap( Card.getWidth(), Card.getHeight(), Bitmap.Config.ARGB_4444 );
		Drawable hiddenCardDrawable = resources.getDrawable( R.drawable.detailed_card_back );
		Canvas canvas = new Canvas( hiddenCard );
		hiddenCardDrawable.setBounds( 0, 0, hiddenCard.getWidth(), hiddenCard.getHeight() );
		hiddenCardDrawable.draw( canvas );
	}

	protected void prepareFonts( boolean includeReverse ) {
		FontRenderer renderer = new FontRenderer( size.FONT_WIDTH, size.FONT_HEIGHT );

		Drawable fontDrawable = resources.getDrawable( R.drawable.font_195x18 );
		blackFont    = renderer.render( fontDrawable );
		if ( includeReverse ) {
			revBlackFont = renderer.renderReversed( fontDrawable );
		}

		fontDrawable.setColorFilter( 0xffff0000, PorterDuff.Mode.SRC_ATOP );
		redFont    = renderer.render( fontDrawable );
		if ( includeReverse ) {
			revRedFont = renderer.renderReversed( fontDrawable );
		}
	}

}
