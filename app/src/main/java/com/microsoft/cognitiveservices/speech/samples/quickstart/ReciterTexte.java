package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReciterTexte extends AppCompatActivity {
    // Replace below with your own subscription key
    private static String speechSubscriptionKey = "5eae85560bb241b884f09a170d1a3214";
    // Replace below with your own service region (e.g., "westus").
    private static String serviceRegion = "francecentral";

    public static final String SCORE_KEY="score_key";
    public static final String RESULT_TEXT_KEY="result_text_key";
    private String currentText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciter_texte);

        // Initialize SpeechSDK and request required permissions.
        try {
            // a unique number within the application to allow
            // correlating permission request responses with the request.
            int permissionRequestId = 5;

            // Request permissions needed for speech recognition
            ActivityCompat.requestPermissions(ReciterTexte.this, new String[]{RECORD_AUDIO, INTERNET}, permissionRequestId);
        }
        catch (Exception ex) {
            Log.e("SpeechSDK", "could not init sdk, " + ex.toString());
            TextView recognizedTextView = (TextView) this.findViewById(R.id.hello);
            recognizedTextView.setText("Could not initialize SpeechSDK: " + ex.toString());
        }
    }

    public void onSpeechToTextButtonClicked(View v) {
        TextView txt = (TextView) this.findViewById(R.id.TexteDit_Reciter); // 'hello' is the ID of your text view

        try {
            SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            config.setSpeechRecognitionLanguage("fr-FR");

            assert(config != null);

            SpeechRecognizer reco = new SpeechRecognizer(config);
            assert(reco != null);

            Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
            assert(task != null);

            // Note: this will block the UI thread, so eventually, you want to
            //        register for the event (see full samples)
            SpeechRecognitionResult result = task.get();
            if (result == null ) {
                txt.setText("result == null");
            }
            assert(result != null);

            if (result.getReason() == ResultReason.RecognizedSpeech) {
                txt.setText(result.getText());
            }
            else {
                txt.setText("Error recognizing. Did you update the subscription info?" + System.lineSeparator() + result.toString());
            }
            reco.close();
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            assert(false);
        }
    }


    public void AfficherResultatSimple(View view) {
        // Récupérer le texte original
        Intent currentIntent = getIntent();
        currentText=currentIntent.getStringExtra(TextManagerActivity.CURRENT_TEXT_KEY);

        // Récupérer le texte dit par l'utilisateur
        TextView txt = (TextView) this.findViewById(R.id.TexteDit_Reciter);
        String saidText = txt.getText().toString();

        // Calculer un résultat complet
        CompareTask txtComparison = new CompareTask();
        txtComparison.execute(currentText,saidText);
        String response = "no response";
        Integer score = -1;
        String text = "";
        try {
            response = txtComparison.get();
            JSONObject jsonObject = new JSONObject(response);
            score = jsonObject.getInt("score");
            text = jsonObject.getString("text");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Passer à l'activité suivante
        Intent ResultatSimpleActivity = new Intent(ReciterTexte.this, ResultatSimple.class);
        ResultatSimpleActivity.putExtra(SCORE_KEY, score);
        ResultatSimpleActivity.putExtra(RESULT_TEXT_KEY,text);
        ResultatSimpleActivity.putExtra(TextManagerActivity.CURRENT_TEXT_KEY,currentText);
        setResult(RESULT_OK,ResultatSimpleActivity);
        finish();
        startActivity(ResultatSimpleActivity);
    }
    private static class CompareTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            String stringUrl = "https://recitoback.azurewebsites.net/RetrieveTextComparison";
            String originalText = strings[0];
            String textRead = strings[1];
            RequestBody body = new FormBody.Builder()
                    .add("originalText", originalText)
                    .add("textRead", textRead)
                    .build();
            Request request = new Request.Builder()
                    .url(stringUrl)
                    .addHeader("Content-type","application/json")
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                //Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonTest = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonTest);
                return jsonObject.toString();
            } catch (IOException | JSONException e) {
                return "error in http post request";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}