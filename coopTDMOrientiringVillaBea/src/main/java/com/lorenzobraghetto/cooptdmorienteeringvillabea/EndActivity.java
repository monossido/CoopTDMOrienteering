package com.lorenzobraghetto.cooptdmorienteeringvillabea;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Required;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class EndActivity extends ActionBarActivity implements ValidationListener {

    private Validator validator;
    private Dialog dialog;
    @Required(order = 1, message = "Il nome è richiesto!")
    private EditText name;
    @Required(order = 2, message = "L'indirizzo email è richiesto!")
    @Email(order = 3, message = "Inserire un indirizzo email valido!")
    protected AutoCompleteTextView email;
    private String user;
    private String tempo;
    private String difficolta;
    protected View layout;
    protected View progressBar;
    private ProgressBar progressBarLayout;
    private Button inviaPunteggio;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        validator = new Validator(this);
        validator.setValidationListener(this);

        user = getIntent().getExtras().getString("user");
        tempo = getIntent().getExtras().getString("tempo");
        difficolta = ((OrientiringApplication) getApplication()).getLevelString();

        TextView complimenti = (TextView) findViewById(R.id.complimenti);
        complimenti.setText(getString(R.string.complimenti) + " " + user + "!");

        TextView time = (TextView) findViewById(R.id.time);
        time.setText(tempo);

        inviaPunteggio = (Button) findViewById(R.id.inviaPunteggio);
        inviaPunteggio.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // custom dialog
                dialog = new Dialog(EndActivity.this);
                dialog.setContentView(R.layout.custom_dialog_punteggio);
                dialog.setTitle("Invia il tuo punteggio...");

                layout = dialog.findViewById(R.id.layout);
                progressBar = dialog.findViewById(R.id.progressBar);

                name = (EditText) dialog.findViewById(R.id.nome);
                email = (AutoCompleteTextView) dialog.findViewById(R.id.email);

                Account[] accounts = AccountManager.get(EndActivity.this).getAccounts();
                Set<String> emailSet = new HashSet<String>();
                for (Account account : accounts) {
                    if (EMAIL_PATTERN.matcher(account.name).matches()) {
                        emailSet.add(account.name);
                    }
                }
                ArrayList<String> emails = new ArrayList<>(emailSet);
                email.setAdapter(new ArrayAdapter<>(EndActivity.this, android.R.layout.simple_dropdown_item_1line, emails));
                if (emails != null && emails.size() > 0)
                    email.setText(emails.get(0));

                name.setText(user);

                TextView tempoText = (TextView) dialog.findViewById(R.id.tempo);
                TextView difficoltaText = (TextView) dialog.findViewById(R.id.difficolta);
                tempoText.setText("Tempo totale: " + tempo);
                difficoltaText.setText("Difficoltà: " + difficolta);

                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButtonOk.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        validator.validate();
                    }
                });

                dialog.show();
            }
        });
        progressBarLayout = (ProgressBar) findViewById(R.id.progressBarLayout);
        (findViewById(R.id.vediClassifica)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                progressBarLayout.setVisibility(View.VISIBLE);
                Ion.with(EndActivity.this)
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
                                    Toast.makeText(EndActivity.this, R.string.errore_download, Toast.LENGTH_LONG).show();
                                }
                            }

                        });
            }

        });
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();

        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(message);
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onValidationSucceeded() {
        progressBar.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);

        Ion.with(EndActivity.this)
                .load(Params.POST_STATS)
                .setBodyParameter("name", name.getText().toString())
                .setBodyParameter("email", email.getText().toString())
                .setBodyParameter("app", "orienteering")
                .setBodyParameter("tempo", tempo)
                .setBodyParameter("soloClassifica", "false")
                .setBodyParameter("difficolta", difficolta)
                .setBodyParameter("api_key", Params.API_KEY_MIO)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.v("TDM", "result=" + result);
                        if (result != null && result.get("result").getAsString().equals("success")) {
                            showStats(result);
                            Toast.makeText(EndActivity.this, "Punteggio inviato", Toast.LENGTH_LONG).show();
                            inviaPunteggio.setEnabled(false);
                        } else {
                            Toast.makeText(EndActivity.this, R.string.errore_download, Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }

                });
    }

    private void showStats(JsonObject result) {
        List<Punteggio> punteggi = parseResult(result);
        // custom dialog
        final Dialog dialogClassifica = new Dialog(EndActivity.this);
        dialogClassifica.setContentView(R.layout.custom_dialog_stats);
        dialogClassifica.setTitle("Classifica");

        ListView classifica = (ListView) dialogClassifica.findViewById(R.id.classifica);
        PunteggioListAdapter adapter = new PunteggioListAdapter(punteggi, EndActivity.this);
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
            Toast.makeText(EndActivity.this, R.string.errore_download, Toast.LENGTH_LONG).show();
        }
        if (punteggi.size() == 0)
            punteggi.add(new Punteggio("", getString(R.string.no_partite), "", ""));
        return punteggi;
    }
}
