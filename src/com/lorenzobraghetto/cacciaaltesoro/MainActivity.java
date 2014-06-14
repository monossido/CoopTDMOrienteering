package com.lorenzobraghetto.cacciaaltesoro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends SherlockActivity {

	private Button ok;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final EditText user = (EditText) findViewById(R.id.edittext_name);
		user.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 0)
					ok.setEnabled(true);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		ok = (Button) findViewById(R.id.button_start);
		ok.setEnabled(false);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle(R.string.difficolta)
						.setItems(R.array.difficult_levels, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (which == 0) {
									Intent maps = new Intent(MainActivity.this, MapsActivity.class);
									maps.putExtra("user", user.getText().toString());
									startActivity(maps);
								} else {
									Toast.makeText(MainActivity.this, "La difficoltà difficile arriverà presto", Toast.LENGTH_LONG).show();
								}
							}
						});
				builder.create().show();

			}
		});

	}

}
