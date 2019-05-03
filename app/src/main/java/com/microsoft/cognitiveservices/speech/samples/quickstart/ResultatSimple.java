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
    private String resultText;
    private int score;
    private String currentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resultat_simple);

        Intent currentIntent = getIntent();
        score =currentIntent.getIntExtra(ReciterTexte.SCORE_KEY,-1);
        resultText = currentIntent.getStringExtra(ReciterTexte.RESULT_TEXT_KEY);
        currentText = currentIntent.getStringExtra(TextManagerActivity.CURRENT_TEXT_KEY);

        TextView scoreSur100 = (TextView) this.findViewById(R.id.Score_ResultatSimple);
        scoreSur100.setText(score+" erreurs");



        ProgressBar progressBar = findViewById(R.id.ProgressBar_ResultatSimple);
        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();

        //TextView scoreUtilisateur = findViewById(R.id.ValScore_ResultatSimple);
        //int leScore = Integer.valueOf(scoreUtilisateur.getText().toString());
        int leScore = score;

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
        intent.putExtra(TextManagerActivity.CURRENT_TEXT_KEY,currentText);
        setResult(RESULT_OK,intent);
        finish();
        startActivity(intent);
    }

    public void RetournerBibliotheque(View view) {
        Intent intent = new Intent(ResultatSimple.this, LibraryActivity.class);
        startActivity(intent);
    }

    public void VoirResultatDetaille(View view) {
        Intent intent = new Intent(ResultatSimple.this, ResultatDetaille.class);
        intent.putExtra(ReciterTexte.RESULT_TEXT_KEY,resultText);
        intent.putExtra(ReciterTexte.SCORE_KEY,score);
        intent.putExtra(TextManagerActivity.CURRENT_TEXT_KEY,currentText);

        setResult(RESULT_OK,intent);
        finish();
        startActivity(intent);
    }
}
