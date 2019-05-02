package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Accueil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
    }

    public void ConnexionButtonClicked(View view) {
        Intent intent = new Intent(Accueil.this, ConnectionActivity.class);
        startActivity(intent);
    }

    public void InscriptionButtonClicked(View view) {
        Intent intent = new Intent(Accueil.this, CreateAccountActivity.class);
        startActivity(intent);
    }
}
