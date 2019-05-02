package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ResultatSimple extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat_simple);

        Intent currentIntent = getIntent();
        int score =currentIntent.getIntExtra(ReciterTexte.SCORE_KEY,-1);
        TextView scoreSur100 = (TextView) this.findViewById(R.id.Score_ResultatSimple);


        // Ne pas mettre 100 ni "erreurs", mais faire un pourcentage d'erreurs en fonction des mots
        // --> à réfléchir mieux que ças
        ProgressBar progressBar = findViewById(R.id.ProgressBar_ResultatSimple);
        progressBar.setSecondaryProgress(100);
        progressBar.setProgress(score);
        progressBar.setMax(100);
        scoreSur100.setText(score+" erreurs");
    }

    public void ReessayerRecitation(View view) {
        Intent intent = new Intent(ResultatSimple.this, ReciterTexte.class);
        startActivity(intent);
    }

    public void RetournerBibliotheque(View view) {
        Intent intent = new Intent(ResultatSimple.this, LibraryActivity.class);
        startActivity(intent);
    }
}
