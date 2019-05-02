package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
}
