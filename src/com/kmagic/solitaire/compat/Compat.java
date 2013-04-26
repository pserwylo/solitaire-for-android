package com.kmagic.solitaire.compat;

import android.os.*;

abstract class Compat {

	public static boolean hasApi( int version ) {
		return getApi() >= version;
	}

	public static int getApi() {
		return Integer.parseInt( Build.VERSION.SDK );
	}

}
