package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;


public class TextManagerActivity extends AppCompatActivity {
    private static final int RECITE_TEXT_ACTIVITY = 4;
    public static final String CURRENT_TEXT_ID="current_text_id";
    public static final String CURRENT_TEXT_KEY="current_text_key";
    private final String urlTORequest = "https://recitoback.azurewebsites.net/getText";
    private String currentText="Chargement...";
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
        Intent curIntent = getIntent();

        new FetchTask().execute(urlTORequest, curIntent.getStringExtra("idClient"), curIntent.getStringExtra("idText"));

        //currentTextView.setText(currentText);
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

    private class FetchTask extends AsyncTask<String, Void, String> {
        private String errorMessage="Error : Texte non trouvé";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON =MediaType.parse("application/json; charset=utf-8");
            JsonObject json = new JsonObject();
            json.addProperty("idClient", strings[1]);
            json.addProperty("idText", strings[2]);

            Request request = new Request.Builder()
                    .url(strings[0])
                    .post(RequestBody.create(JSON, json.toString()))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                //Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonTest = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonTest);
                System.out.println("---------------------------------"+jsonObject.toString());
                if(jsonObject.has("text"))
                {
                    currentText=jsonObject.getJSONObject("text").getString("contenu");
                    return "true";
                }
                else
                {
                    if(jsonObject.has("Message"))
                    {
                        errorMessage=jsonObject.getString("Message");
                    }
                    currentText="Error : Texte non trouvé";
                    return "false";
                }

            } catch (IOException | JSONException e) {
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.compareTo("false")==0) {
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
            currentTextView.setText(currentText);

        }
    }
}
