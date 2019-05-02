package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TextManagerActivity extends AppCompatActivity {
    private static final int RECITE_TEXT_ACTIVITY = 4;
    private static final String CURRENT_TEXT_ID="current_text_id";
    private String currentText=null;
    private TextView currentTextView=null;
    private Button startReciteButton=null;
    private Button readReciteButton=null;
    private long currentTextID=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_manager);

        currentTextView = findViewById(R.id.Text_Item_Text);
        startReciteButton = findViewById(R.id.Start_Button_Item_Text);
        readReciteButton = findViewById(R.id.Read_Button_Item_Text);

        //Je remplis le currentText

        currentTextView.setText(currentText);
        currentTextView.setMovementMethod(new ScrollingMovementMethod());

        startReciteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ReciteTexteActivity = new Intent(TextManagerActivity.this, ReciterTexte.class);
                ReciteTexteActivity.putExtra(CURRENT_TEXT_ID, currentTextID);
                startActivityForResult(ReciteTexteActivity,RECITE_TEXT_ACTIVITY);
            }
        });

        readReciteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Partie Clementine avec la lecture du currentTexte;
            }
        });
    }
}
