package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultatDetaille extends AppCompatActivity {

    private String resultText;
    private int score;
    private ArrayList<String> originalTextList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat_detaille);

        Intent currentIntent = getIntent();
        resultText = currentIntent.getStringExtra(ReciterTexte.RESULT_TEXT_KEY);
        score =currentIntent.getIntExtra(ReciterTexte.SCORE_KEY,-1);
        originalTextList = currentIntent.getStringArrayListExtra(ReciterTexte.OTL_KEY);

        TextView correction = (TextView) this.findViewById(R.id.Correction_ResultatDetaille);
        correction.setText(Html.fromHtml(resultText));

    }

    public void RetournerResultatSimple(View view) {
        Intent currentIntent = new Intent(ResultatDetaille.this,ResultatSimple.class);
        currentIntent.putExtra(ReciterTexte.RESULT_TEXT_KEY,resultText);
        currentIntent.putExtra(ReciterTexte.SCORE_KEY,score);
        currentIntent.putExtra(ReciterTexte.OTL_KEY,originalTextList);

        setResult(RESULT_OK,currentIntent);
        finish();
        startActivity(currentIntent);
    }
}
