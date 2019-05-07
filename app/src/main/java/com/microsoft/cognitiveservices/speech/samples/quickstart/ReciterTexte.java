package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.Locale;
import java.util.concurrent.Future;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.widget.Toast;

public class ReciterTexte extends AppCompatActivity {
    // Replace below with your own subscription key
    private static String speechSubscriptionKey = "5eae85560bb241b884f09a170d1a3214";
    // Replace below with your own service region (e.g., "westus").
    private static String serviceRegion = "francecentral";

    public static final String SCORE_KEY="score_key";
    public static final String RESULT_TEXT_KEY="result_text_key";
    public static final String OTL_KEY = "originalTextList_key";
    public static final String STL_KEY = "saidTextList_key";
    private String currentText = "";
    private ArrayList<Integer> whoReads;
    private ArrayList<Pair<String, Integer>> fullOriginalTextList; // Le texte et le "qui doit parler"
    private ArrayList<String> originalTextList;
    private ArrayList<String> saidTextList;

    private int indToRead = 0; // Pour l'enregistrement phrase par phrase
    private int nbLinesToRead;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciter_texte);


        Intent currentIntent = getIntent();
        /*TODO ADD : whoReads = currentIntent.getIntegerArrayListExtra(TextManagerActivity.ORDER_TEXT_KEY);
        if (whoReads == null){
            whoReads = new ArrayList<>();
            whoReads.add(0);
            whoReads.add(1);
            whoReads.add(0);
            whoReads.add(1);
            whoReads.add(0);
            whoReads.add(1);
            whoReads.add(0);
        }
        */
        fullOriginalTextList = toCorrectStructure(currentIntent.getStringExtra(TextManagerActivity.CURRENT_TEXT_KEY));
        //TODO ADD : fullOriginalTextList = toCorrectStructure(currentIntent.getStringExtra(TextManagerActivity.CURRENT_TEXT_KEY),whoReads);

        fullOriginalTextList = dialogLines(fullOriginalTextList);
        nbLinesToRead = fullOriginalTextList.size()-1;
        //printStruct(fullOriginalTextList);

        originalTextList = new ArrayList<>();
        saidTextList = new ArrayList<>();

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


        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.FRENCH);
                }
            }
        });
    }

    // Fait en sorte de mettre la chaîne de caractères dans l'Arraylist, avec des 0 = c'est l'utilisateur qui doit parler par défaut
    private ArrayList<Pair<String, Integer>> toCorrectStructure(String text){
        String[] tabTexte = text.split("\n");

        ArrayList<Pair<String, Integer>> fullText = new ArrayList<>();
        Pair<String, Integer> toAdd ;
        for (int i = 0 ; i<tabTexte.length ; i++){
            // dans la structure de base, tout doit être lu par l'utilisateur
            toAdd = new Pair (tabTexte[i],0);
            fullText.add(toAdd);
        }
        return fullText;
    }

    private ArrayList<Pair<String, Integer>> toCorrectStructure(String text,ArrayList<Integer> quiDoitLire){
        String[] tabTexte = text.split("\n");
        ArrayList<Pair<String, Integer>> fullText = new ArrayList<>();
        Pair<String, Integer> toAdd ;
        for (int i = 0 ; i<tabTexte.length ; i++){
            // dans la structure de base, tout doit être lu par l'utilisateur
            toAdd = new Pair (tabTexte[i],quiDoitLire.get(i));
            fullText.add(toAdd);
        }
        return fullText;
    }

    // Fait en sorte d'indiquer que l'utilisateur devra parler une ligne sur deux
    private ArrayList<Pair<String, Integer>> dialogLines(ArrayList<Pair<String, Integer>> text){
        for (int i = 1 ; i<text.size() ; i=i+2){
            Pair<String, Integer> toEdit = new Pair<>(text.get(i).first,1);
            text.set(i,toEdit);
        }
        return text;
    }

    // Affiche la structure dans Logcat
    private void printStruct(ArrayList<Pair<String, Integer>> text){
        for (int i = 0 ; i<text.size() ; i++){
            System.out.println("/////////////////STRUCTURE ELEM N°"+i+"///FIRST="+text.get(i).first+"|SECOND="+text.get(i).second);
        }
    }

    public void onSpeechToTextButtonClicked(View v) {
        ImageButton bouton = (ImageButton) this.findViewById(R.id.Mic_Button_Reciter);
        TextView texteBouton = (TextView) this.findViewById(R.id.Tour_Reciter);

        if (indToRead<fullOriginalTextList.size()) {
            TextView writeHere = (TextView) this.findViewById(R.id.TexteDit_Reciter);

            if (indToRead == 1) {
                writeHere = (TextView) this.findViewById(R.id.TexteDit_Reciter2);
            } else if (indToRead == 2) {
                writeHere = (TextView) this.findViewById(R.id.TexteDit_Reciter3);
            } else if (indToRead == 3) {
                writeHere = (TextView) this.findViewById(R.id.TexteDit_Reciter4);
            } else if (indToRead == 4) {
                writeHere = (TextView) this.findViewById(R.id.TexteDit_Reciter5);
            } else if (indToRead == 5) {
                writeHere = (TextView) this.findViewById(R.id.TexteDit_Reciter6);
            } else if (indToRead == 6) {
                writeHere = (TextView) this.findViewById(R.id.TexteDit_Reciter7);
            }
            String toWrite = "";
            // Si c'est à l'utilisateur de parler, on enregistre et on stocke ce qui est dit et l'original
            if (fullOriginalTextList.get(indToRead).second == 0) {
                String said = recordSpeechToText();
                toWrite = "Vous : " + said;

                saidTextList.add(said);
                originalTextList.add(fullOriginalTextList.get(indToRead).first);

                // Si la prochaine phrase est à dire par Recito, on change le bouton
                if(indToRead+1 < fullOriginalTextList.size() && fullOriginalTextList.get(indToRead+1).second!=0){
                   bouton.setImageResource(R.drawable.ic_play_arrow_black_50dp);
                   texteBouton.setText("Appuyez pour entendre\nla phrase suivante");
                }
            }
            // Si c'est à Recito de parler, on parle et on écrit le texte
            else {
                String textADire = fullOriginalTextList.get(indToRead).first;
                toWrite = "Recito : " + textADire;
                // Recito doit parler
                Toast.makeText(getApplicationContext(), textADire,Toast.LENGTH_SHORT).show();
                tts.speak(textADire, TextToSpeech.QUEUE_FLUSH, null);

                // Si la prochaine phrase est à dire par l'utilisateur, on change le bouton
                if (indToRead+1 < fullOriginalTextList.size() && fullOriginalTextList.get(indToRead+1).second==0){
                    texteBouton.setText("C'est à vous");
                    bouton.setImageResource(R.drawable.ic_mic_black_50dp);
                }
            }
            writeHere.setText(toWrite);
            indToRead++; // On passe à la réplique suivante
            if (indToRead == fullOriginalTextList.size()){
                texteBouton.setText("C'est terminé");
            }
        }
    }

    public String recordSpeechToText(){
        String textSaid="";
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
                textSaid="result == null";
            }
            assert(result != null);

            if (result.getReason() == ResultReason.RecognizedSpeech) {
                textSaid=result.getText();
            }
            else {
                textSaid = "Error recognizing. Did you update the subscription info?" + System.lineSeparator() + result.toString();
            }
            reco.close();
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            assert(false);
        }
        return textSaid;
    }

    public void AfficherResultatSimple(View view) {

        if (indToRead == 0) {
            Toast.makeText(getApplicationContext(), "Dites au moins une réplique avant de terminer la session",Toast.LENGTH_SHORT).show();
        }
        else {
            // Récupérer le texte original
            Intent currentIntent = getIntent();
            if (originalTextList == null) {
                originalTextList = currentIntent.getStringArrayListExtra(ReciterTexte.OTL_KEY);
            }

            // Récupérer le texte dit par l'utilisateur
            if (saidTextList == null) {
                saidTextList = currentIntent.getStringArrayListExtra(ReciterTexte.STL_KEY);
            }

            // Calculer un résultat complet
            //Pair<Integer, String> fullResultList = calculateFullResult(originalTextList, saidTextList);
            CompareTask txtComparison = new CompareTask();

            Map<String,Object> m=new HashMap<>();
            m.put("originalText",originalTextList);
            m.put("textRead",saidTextList);
            txtComparison.execute(m);

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
            ResultatSimpleActivity.putExtra(RESULT_TEXT_KEY, text);
            ResultatSimpleActivity.putExtra(ReciterTexte.OTL_KEY, originalTextList);
            setResult(RESULT_OK, ResultatSimpleActivity);
            finish();
            startActivity(ResultatSimpleActivity);
        }
    }

    private static class CompareTask extends AsyncTask<Map<String,Object>, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Map<String, Object>... mapList) {
            OkHttpClient client = new OkHttpClient();
            String stringUrl = "https://recitoback.azurewebsites.net/RetrieveTextComparison";

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(mapList);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(stringUrl)
                    .addHeader("Content-type","application/json")
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
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