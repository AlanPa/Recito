package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class TextManagerActivity extends AppCompatActivity {
    private static final int RECITE_TEXT_ACTIVITY = 4;
    public static final String CURRENT_TEXT_ID="current_text_id";
    public static final String CURRENT_TEXT_KEY="current_text_key";
    private String currentText="Ceci est un texte à lire par l'application";
    private TextView currentTextView=null;
    private Button startReciteButton=null;
    private Button readReciteButton=null;
    private long currentTextID=-1;
    private TextToSpeech tts;

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

        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.FRENCH);
                }
            }
        });

        startReciteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ReciteTexteActivity = new Intent(TextManagerActivity.this, ReciterTexte.class);
                ReciteTexteActivity.putExtra(CURRENT_TEXT_ID, currentTextID);
                ReciteTexteActivity.putExtra(CURRENT_TEXT_KEY, currentText);
                setResult(RESULT_OK,ReciteTexteActivity);
                finish();
                startActivity(ReciteTexteActivity);
            }
        });

        readReciteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toSpeak = currentTextView.getText().toString();
                Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    public void onPause(){
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }
}
