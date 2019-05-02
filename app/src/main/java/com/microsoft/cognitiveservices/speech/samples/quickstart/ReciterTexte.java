package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ReciterTexte extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciter_texte);
    }
    public void AfficherRésultatSimple(View view) {
        Intent intent = new Intent(ReciterTexte.this, ResultatSimple.class);
        startActivity(intent);
    }
}
