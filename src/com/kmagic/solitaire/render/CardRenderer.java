package com.kmagic.solitaire.render;

import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import com.kmagic.solitaire.*;

abstract public class CardRenderer {

	protected final Resources resources;
	protected final Paint suitPaint;

	private Bitmap[] cards = new Bitmap[52];
	private Bitmap hiddenCard;

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
		hiddenCard = Bitmap.createBitmap( Card.WIDTH, Card.HEIGHT, Bitmap.Config.ARGB_4444 );
		Drawable hiddenCardDrawable = resources.getDrawable( R.drawable.cardback );
		Canvas canvas = new Canvas( hiddenCard );
		hiddenCardDrawable.setBounds( 0, 0, hiddenCard.getWidth(), hiddenCard.getHeight() );
		hiddenCardDrawable.draw( canvas );
	}
}
