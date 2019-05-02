package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class ResultatSimple extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resultat_simple);
        ProgressBar progressBar = findViewById(R.id.ProgressBar_ResultatSimple);
        progressBar.setSecondaryProgress(100);
        progressBar.setProgress(10);
        progressBar.setMax(100);
    }

    public void ReessayerRecitation(View view) {
        Intent intent = new Intent(ResultatSimple.this, ReciterTexte.class);
        startActivity(intent);
    }

    public void RetournerBibliotheque(View view) {
        Intent intent = new Intent(ResultatSimple.this, LibraryActivity.class);
        startActivity(intent);
    }

    public void VoirResultatDetaille(View view) {
        Intent intent = new Intent(ResultatSimple.this, ResultatDetaille.class);
        startActivity(intent);
    }
}
