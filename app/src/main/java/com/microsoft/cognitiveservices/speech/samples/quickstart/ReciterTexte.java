package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ReciterTexte extends AppCompatActivity {
    private String currentText=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciter_texte);
    }
}
