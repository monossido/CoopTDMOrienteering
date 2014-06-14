package com.lorenzobraghetto.cacciaaltesoro;

import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class EndActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end);

		String user = getIntent().getExtras().getString("user");
		String tempo = getIntent().getExtras().getString("tempo");

		TextView complimenti = (TextView) findViewById(R.id.complimenti);
		complimenti.setText(getString(R.string.complimenti) + " " + user + "!");

		TextView time = (TextView) findViewById(R.id.time);
		time.setText(tempo);
		//TODO
	}

}
