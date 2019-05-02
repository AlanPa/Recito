package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
        ProgressBar progressBar = findViewById(R.id.ProgressBar_ResultatSimple);
        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
        TextView scoreUtilisateur = findViewById(R.id.ValScore_ResultatSimple);
        int leScore = Integer.valueOf(scoreUtilisateur.getText().toString());
        progressBar.setProgress(leScore);
        if(leScore < 25)
        {

            progressDrawable.setColorFilter(Color.rgb(255, 87, 51), PorterDuff.Mode.SRC_IN);
        }
        else if(leScore < 50)
        {
            progressDrawable.setColorFilter(Color.rgb(255, 189, 51), PorterDuff.Mode.SRC_IN);
        }
        else if(leScore < 75)
        {
            progressDrawable.setColorFilter(Color.	rgb(219, 255, 51), PorterDuff.Mode.SRC_IN);
        }
        else
        {
            progressDrawable.setColorFilter(Color.rgb(51, 255, 87), PorterDuff.Mode.SRC_IN);
        }
        progressBar.setProgressDrawable(progressDrawable);

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
