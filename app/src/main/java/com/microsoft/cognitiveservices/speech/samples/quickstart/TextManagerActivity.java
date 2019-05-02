package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TextManagerActivity extends AppCompatActivity {
    private String currentText=null;
    private TextView currentTextView=null;
    private Button startReciteButton=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_manager);

        currentTextView = findViewById(R.id.Text_Item_Text);
        startReciteButton = findViewById(R.id.Start_Button_Item_Text);

        //Je remplis le currentText

        currentTextView.setText(currentText);
        currentTextView.setMovementMethod(new ScrollingMovementMethod());

        startReciteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent gameActivity = new Intent(TextManagerActivity.this, GameActivity.class);
                //startActivityForResult(gameActivity, GAME_ACTIVITY_REQUEST_CODE);
            }
        });
    }
}
