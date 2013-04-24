/*
  Copyright 2008 Google Inc.
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.kmagic.solitaire;

import android.app.*;
import android.graphics.*;
import android.view.*;

public class Card {

	public static final int CLUBS = 0;
	public static final int DIAMONDS = 1;
	public static final int SPADES = 2;
	public static final int HEARTS = 3;

	public static final int ACE = 1;
	public static final int JACK = 11;
	public static final int QUEEN = 12;
	public static final int KING = 13;
	public static final String TEXT[] = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };

	private static int width = 45;
	private static int height = 64;

	private int mValue;
	private int mSuit;
	private float mX;
	private float mY;

	public static void SetSize( int type, Point screenSize ) {
		int cardsAcross;
		switch ( type ) {
			case Rules.SOLITAIRE:
				cardsAcross = 7;
				break;

			case Rules.FREECELL:
				cardsAcross = 8;
				break;

			case Rules.SPIDER:
			case Rules.FORTYTHIEVES:
				cardsAcross = 10;
				break;

			default:
				cardsAcross = 8;
				break;
		}

		float estimatedWidth = screenSize.x / ( cardsAcross + 2 ); // Add two to make extra padding between.
		float ratio = 1.37f;
		width = (int)estimatedWidth;
		height = (int)( estimatedWidth * ratio );
	}

	public Card( int value, int suit ) {
		mValue = value;
		mSuit = suit;
		mX = 1;
		mY = 1;
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}

	public float GetX() {
		return mX;
	}

	public float GetY() {
		return mY;
	}

	public int GetValue() {
		return mValue;
	}

	public int GetSuit() {
		return mSuit;
	}

	public void SetPosition( float x, float y ) {
		mX = x;
		mY = y;
	}

	public void MovePosition( float dx, float dy ) {
		mX -= dx;
		mY -= dy;
	}
}


