package com.lorenzobraghetto.cacciaaltesoro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button ok = (Button) findViewById(R.id.button_start);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent maps = new Intent(MainActivity.this, MapsActivity.class);
				startActivity(maps);
			}
		});

	}

}
