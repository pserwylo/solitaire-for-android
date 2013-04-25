package com.kmagic.solitaire.render;

import com.kmagic.solitaire.*;

public class SizeCalculator {

	public final int CARD_WIDTH;
	public final int CARD_HEIGHT;
	public final int FACE_CARD_PADDING;
	public final int FONT_PADDING;

	public final int FACE_WIDTH;
	public final int FACE_HEIGHT;

	public final int FONT_WIDTH;
	public final int FONT_HEIGHT;

	public final int SUIT_WIDTH;
	public final int SUIT_HEIGHT;

	public final int SMALL_SUIT_WIDTH;
	public final int SMALL_SUIT_HEIGHT;

	public final int CARD_ROUNDED_CORNER;

	private final int MIN_FONT_WIDTH   = 5;
	private final int MIN_FONT_PADDING = 1;
	private final int MIN_FACE_PADDING = MIN_FONT_WIDTH + MIN_FONT_PADDING * 2;
	private final int MIN_ROUNDED_CORNER = 3;

	public SizeCalculator() {

		CARD_WIDTH          = Card.getWidth();
		CARD_HEIGHT         = Card.getHeight();

		FACE_CARD_PADDING   = calcFacePadding();
		FONT_PADDING        = calcFontPadding();

		FACE_WIDTH          = Card.getWidth() - FACE_CARD_PADDING * 2;
		FACE_HEIGHT         = Card.getHeight() / 2 - FACE_CARD_PADDING;

		FONT_WIDTH          = calcFontWidth();
		FONT_HEIGHT         = (int)( FONT_WIDTH * 1.2 );

		SUIT_WIDTH          = Card.getWidth() / 5;
		SUIT_HEIGHT         = Card.getWidth() / 5;

		SMALL_SUIT_WIDTH    = Card.getWidth() / 12;
		SMALL_SUIT_HEIGHT   = Card.getWidth() / 12;

		CARD_ROUNDED_CORNER = calcRoundedCorner();
	}


	private int calcRoundedCorner() {
		int ideal = idealRoundedCorner();
		return ideal > MIN_ROUNDED_CORNER ? ideal : MIN_ROUNDED_CORNER;
	}

	private int idealRoundedCorner() {
		return Card.getWidth() / 16;
	}


	private int calcFontWidth() {
		int ideal = idealFontWidth();
		return ideal > MIN_FONT_WIDTH ? ideal : MIN_FONT_WIDTH;
	}

	private int idealFontWidth() {
		return FACE_CARD_PADDING - FONT_PADDING * 2;
	}



	private int calcFontPadding() {
		int ideal = idealFontPadding();
		return ideal > MIN_FONT_PADDING ? ideal : MIN_FONT_PADDING;
	}

	private int idealFontPadding() {
		return (int)Math.ceil( FACE_CARD_PADDING * 0.15 );
	}



	private int calcFacePadding() {
		int ideal = idealFacePadding();
		return ideal > MIN_FACE_PADDING ? ideal : MIN_FACE_PADDING;
	}

	private int idealFacePadding() {
		return Card.getWidth() / 8;
	}

}
