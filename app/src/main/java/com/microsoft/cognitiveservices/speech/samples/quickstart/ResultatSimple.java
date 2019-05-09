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
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ResultatSimple extends AppCompatActivity {
    private String resultText;
    private int score;
    private ArrayList<String> originalTextList;
    private String currentText;
    private ArrayList<Integer> whoReads;
    private String currentTextId="none";
    private String idClient;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resultat_simple);

        Intent currentIntent = getIntent();
        score =currentIntent.getIntExtra(ReciterTexte.SCORE_KEY,-1);
        resultText = currentIntent.getStringExtra(ReciterTexte.RESULT_TEXT_KEY);
        originalTextList = currentIntent.getStringArrayListExtra(ReciterTexte.OTL_KEY);
        currentText = currentIntent.getStringExtra(TextManagerActivity.CURRENT_TEXT_KEY);
        whoReads = currentIntent.getIntegerArrayListExtra(TextManagerActivity.ORDER_TEXT_KEY);
        currentTextId = currentIntent.getStringExtra(TextManagerActivity.CURRENT_TEXT_ID);
        idClient = currentIntent.getStringExtra(TextManagerActivity.CLIENT_ID);


        TextView scoreSur100 = (TextView) this.findViewById(R.id.Score_ResultatSimple);
        scoreSur100.setText(score+"%");



        ProgressBar progressBar = findViewById(R.id.ProgressBar_ResultatSimple);
        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();

        progressBar.setProgress(score);
        if(score < 25)
        {

            progressDrawable.setColorFilter(Color.rgb(255, 87, 51), PorterDuff.Mode.SRC_IN);
        }
        else if(score < 50)
        {
            progressDrawable.setColorFilter(Color.rgb(255, 189, 51), PorterDuff.Mode.SRC_IN);
        }
        else if(score < 75)
        {
            progressDrawable.setColorFilter(Color.	rgb(219, 255, 51), PorterDuff.Mode.SRC_IN);
        }
        else
        {
            progressDrawable.setColorFilter(Color.rgb(51, 255, 87), PorterDuff.Mode.SRC_IN);
        }
        progressBar.setProgressDrawable(progressDrawable);

    }

    public void ReessayerRecitation(View view) { intent = new Intent(ResultatSimple.this, ReciterTexte.class);
        intent.putExtra(ReciterTexte.OTL_KEY,originalTextList);
        intent.putExtra(TextManagerActivity.CURRENT_TEXT_KEY,currentText);
        intent.putExtra(TextManagerActivity.ORDER_TEXT_KEY,whoReads);
        intent.putExtra(TextManagerActivity.CURRENT_TEXT_ID, currentTextId);
        intent.putExtra(TextManagerActivity.CLIENT_ID, idClient);
        setResult(RESULT_OK,intent);
        startActivityForResult(intent,1234);
    }

    public void RetournerBibliotheque(View view) {
        Intent backToLibraryIntent = new Intent();
        //backToLibraryIntent.putExtras(getIntent().getSelector());
        backToLibraryIntent.putExtra("idText", currentTextId);
        backToLibraryIntent.putExtra("textScore", score);
        setResult(RESULT_OK, backToLibraryIntent);
        finish();
        //Intent intent = new Intent(ResultatSimple.this, LibraryActivity.class);
        //startActivity(intent);
    }

    public void VoirResultatDetaille(View view) {
        intent = new Intent(ResultatSimple.this, ResultatDetaille.class);
        intent.putExtra(ReciterTexte.RESULT_TEXT_KEY,resultText);
        intent.putExtra(ReciterTexte.SCORE_KEY,score);
        intent.putExtra(ReciterTexte.OTL_KEY,originalTextList);
        intent.putExtra(TextManagerActivity.CURRENT_TEXT_KEY,currentText);
        intent.putExtra(TextManagerActivity.ORDER_TEXT_KEY,whoReads);

        setResult(RESULT_OK,intent);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Vous ne pouvez pas revenir en arrière avant d'avoir terminé l'action en cours",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            int textScore = data.getIntExtra("textScore", -1);
            String idText = data.getStringExtra("idText");
            intent.putExtra("textScore", textScore);
            intent.putExtra("idText", idText);
            setResult(RESULT_OK, intent);
            finish();

        }

    }
}