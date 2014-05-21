package com.lorenzobraghetto.cacciaaltesoro;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;

import android.app.Application;

public class CacciaAlTesoroApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		AndroidGraphicFactory.createInstance(this);
	}
}
