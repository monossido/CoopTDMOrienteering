package com.lorenzobraghetto.cooptdmorienteeringvillabea;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private Button ok;
    private Button about;
    private Button info;
    private ProgressBar progressBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText user = (EditText) findViewById(R.id.edittext_name);
        user.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    ok.setEnabled(true);
                else
                    ok.setEnabled(false);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        progressBarLayout = (ProgressBar) findViewById(R.id.progressBarLayout);
        ok = (Button) findViewById(R.id.button_start);
        ok.setEnabled(false);
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.difficolta)
                        .setItems(R.array.difficult_levels, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ((OrientiringApplication) getApplication()).setLevel(which);
                                Intent maps = new Intent(MainActivity.this, MapsActivity.class);
                                maps.putExtra("user", user.getText().toString());
                                startActivity(maps);
                            }
                        });
                builder.create().show();

            }
        });

        info = (Button) findViewById(R.id.info);
        info.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.info)
                        .setMessage(Html.fromHtml(getString(R.string.testo_info)))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });

        about = (Button) findViewById(R.id.about);
        about.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PackageInfo pInfo = null;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
                String version = pInfo.versionName;
                String dialogText = String.format(getString(R.string.testo_About), version);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setTitle(R.string.about);
                dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = dialogBuilder.create();
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.custom_dialog, null);
                dialog.setView(view);

                // set the custom dialog components - text, image and button
                TextView text = (TextView) view.findViewById(R.id.dialogText);
                text.setText(Html.fromHtml(dialogText));

                dialog.show();
            }
        });
        (findViewById(R.id.vediClassifica)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                progressBarLayout.setVisibility(View.VISIBLE);
                Ion.with(MainActivity.this)
                        .load(Params.POST_STATS)
                        .setBodyParameter("soloClassifica", "true")
                        .setBodyParameter("app", "orienteering")
                        .setBodyParameter("api_key", Params.API_KEY_MIO)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                progressBarLayout.setVisibility(View.GONE);
                                if (result != null && result.get("result").getAsString().equals("success")) {
                                    showStats(result);
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.errore_download, Toast.LENGTH_LONG).show();
                                }
                            }

                        });
            }

        });

        checkIfShowInfo();
    }

    private void checkIfShowInfo() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pref.getBoolean("firstTime", true)) {
            info.performClick();
            pref.edit().putBoolean("firstTime", false).apply();
        }
    }

    private void showStats(JsonObject result) {
        List<Punteggio> punteggi = parseResult(result);
        // custom dialog
        final Dialog dialogClassifica = new Dialog(MainActivity.this);
        dialogClassifica.setContentView(R.layout.custom_dialog_stats);
        dialogClassifica.setTitle("Classifica");

        ListView classifica = (ListView) dialogClassifica.findViewById(R.id.classifica);
        PunteggioListAdapter adapter = new PunteggioListAdapter(punteggi, MainActivity.this);
        classifica.setAdapter(adapter);

        Button dialogButtonOk = (Button) dialogClassifica.findViewById(R.id.dialogButtonOK);
        dialogButtonOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogClassifica.dismiss();
            }
        });

        dialogClassifica.show();
    }

    private List<Punteggio> parseResult(JsonObject result) {
        List<Punteggio> punteggi = new ArrayList<>();
        try {
            JsonArray punteggiJson = result.get("punteggi").getAsJsonArray();
            for (int i = 0; i < punteggiJson.size(); i++) {
                JsonObject itemJson = punteggiJson.get(i).getAsJsonObject();
                Punteggio item = new Punteggio(itemJson.get("nome").getAsString()
                        , itemJson.get("tempo").getAsString()
                        , itemJson.get("difficolta").getAsString()
                        , itemJson.get("risultato").getAsString());
                punteggi.add(item);
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, R.string.errore_download, Toast.LENGTH_LONG).show();
        }
        if (punteggi.size() == 0)
            punteggi.add(new Punteggio("", getString(R.string.no_partite), "", ""));
        return punteggi;
    }
}
