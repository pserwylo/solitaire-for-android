package com.kmagic.solitaire.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.kmagic.solitaire.R;

public class Preferences extends PreferenceActivity {

	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		addPreferencesFromResource( R.xml.preferences );
	}

}
