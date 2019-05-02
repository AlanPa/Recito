package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ResultatDetaille extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat_detaille);
    }

    public void RetournerResultatSimple(View view) {
        ResultatDetaille.this.finish();
    }
}
