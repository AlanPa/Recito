package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class ResultatDetaille extends AppCompatActivity {

    private String resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat_detaille);

        Intent currentIntent = getIntent();
        resultText = currentIntent.getStringExtra(ReciterTexte.RESULT_TEXT_KEY);

        TextView correction = (TextView) this.findViewById(R.id.Correction_ResultatDetaille);
        correction.setText(Html.fromHtml(resultText));

    }

    public void RetournerResultatSimple(View view) {
        ResultatDetaille.this.finish();
    }
}
