package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.Locale;
import java.util.concurrent.Future;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.widget.Toast;

public class ReciterTexte extends AppCompatActivity {
    private static String speechSubscriptionKey;
    private static String serviceRegion;
    public static final String SCORE_KEY="score_key";
    public static final String RESULT_TEXT_KEY="result_text_key";
    public static final String OTL_KEY = "originalTextList_key";
    public static final String STL_KEY = "saidTextList_key";
    public static String idText;
    public static String idClient;
    private Integer score;
    private String currentText = "";
    private ArrayList<Integer> whoReads;
    private ArrayList<Pair<String, Integer>> fullOriginalTextList; // Le texte et le "qui doit parler"
    private ArrayList<String> originalTextList;
    private ArrayList<String> saidTextList;
    private int indToRead = 0; // Pour l'enregistrement phrase par phrase
    private int nbLinesToRead;
    private boolean somethingHasBeenSaid = false;
    private Intent ResultatSimpleActivity;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciter_texte);

        KeyTask keyTask = new KeyTask();
        keyTask.execute();

        String response = "no response";
        try {
            response = keyTask.get();
            JSONObject jsonObject = new JSONObject(response);
            speechSubscriptionKey = jsonObject.get("speechSubscriptionKey").toString();
            serviceRegion = jsonObject.get("serviceRegion").toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Récupérer le texte à dire et qui doit le dire
        Intent currentIntent = getIntent();
        idText= currentIntent.getStringExtra(TextManagerActivity.CURRENT_TEXT_ID);
        currentText = currentIntent.getStringExtra(TextManagerActivity.CURRENT_TEXT_KEY);
        whoReads = currentIntent.getIntegerArrayListExtra(TextManagerActivity.ORDER_TEXT_KEY);
        idClient = currentIntent.getStringExtra(TextManagerActivity.CLIENT_ID);
        fullOriginalTextList = toCorrectStructure(currentIntent.getStringExtra(TextManagerActivity.CURRENT_TEXT_KEY),whoReads);

        // Nombre de lignes à lire au total dans le texte
        nbLinesToRead = fullOriginalTextList.size()-1;

        // Initialisation des textes qu'il va falloir comparer
        originalTextList = new ArrayList<>();
        saidTextList = new ArrayList<>();

        TextView textClue = (TextView) this.findViewById(R.id.TexteDit_Reciter);
        // Initialisation du bouton
        // Si la première phrase est à dire par l'utilisateur
        if (fullOriginalTextList.get(0).second == 0){
            // Mettre le bouton micro
            mettreBouton(1);
            textClue.setText("Appuyez sur l'icone microphone et dites votre première réplique, pour commencer votre répétition.");

        }
        // Si elle est à dire par Recito
        else {
            // Mettre le bouton play
            mettreBouton(2);
            textClue.setText("Appuyez sur l'icone play, pour que la première phrase soit dite et pour commencer votre répétition.");
        }

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

    // Fait en sorte de mettre la chaîne de caractères dans l'Arraylist en indiquant qui parle
    // 0 : utilisateur | 1 : Recito
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

    public void onSpeechToTextButtonClicked(View v) {
        if (indToRead<fullOriginalTextList.size()) {
            TextView writeHere = this.findViewById(R.id.TexteDit_Reciter);

            if (indToRead == 1) {
                writeHere = this.findViewById(R.id.TexteDit_Reciter2);
            } else if (indToRead == 2) {
                writeHere = this.findViewById(R.id.TexteDit_Reciter3);
            } else if (indToRead == 3) {
                writeHere = this.findViewById(R.id.TexteDit_Reciter4);
            } else if (indToRead == 4) {
                writeHere = this.findViewById(R.id.TexteDit_Reciter5);
            } else if (indToRead == 5) {
                writeHere = this.findViewById(R.id.TexteDit_Reciter6);
            } else if (indToRead == 6) {
                writeHere = this.findViewById(R.id.TexteDit_Reciter7);
            } else if (indToRead == 7) {
                writeHere = this.findViewById(R.id.TexteDit_Reciter8);
            } else if (indToRead == 8) {
                writeHere = this.findViewById(R.id.TexteDit_Reciter9);
            } else {

                TextView toClear = this.findViewById(R.id.TexteDit_Reciter2);
                toClear.setText("");
                toClear = this.findViewById(R.id.TexteDit_Reciter3);
                toClear.setText("");
                toClear = this.findViewById(R.id.TexteDit_Reciter4);
                toClear.setText("");
                toClear = this.findViewById(R.id.TexteDit_Reciter5);
                toClear.setText("");
                toClear = this.findViewById(R.id.TexteDit_Reciter6);
                toClear.setText("");
                toClear = this.findViewById(R.id.TexteDit_Reciter7);
                toClear.setText("");
                toClear = this.findViewById(R.id.TexteDit_Reciter8);
                toClear.setText("");
                toClear = this.findViewById(R.id.TexteDit_Reciter9);
                toClear.setText("");

                if (indToRead == 9) {
                    writeHere = this.findViewById(R.id.TexteDit_Reciter);
                } else if (indToRead == 10) {
                    writeHere = this.findViewById(R.id.TexteDit_Reciter2);

                } else if (indToRead == 11) {
                    writeHere = this.findViewById(R.id.TexteDit_Reciter3);
                }
            }
            String toWrite;
            // Si c'est à l'utilisateur de parler, on enregistre et on stocke ce qui est dit et l'original
            if (fullOriginalTextList.get(indToRead).second == 0) {
                String said = recordSpeechToText();
                toWrite = "Vous : " + said;

                saidTextList.add(said);
                originalTextList.add(fullOriginalTextList.get(indToRead).first);
                somethingHasBeenSaid = true;

                // Si la prochaine phrase est à dire par Recito, on change le bouton
                if(indToRead+1 < fullOriginalTextList.size() && fullOriginalTextList.get(indToRead+1).second!=0){
                   mettreBouton(2);
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
                    mettreBouton(1);
                }
            }
            writeHere.setText(toWrite);
            indToRead++; // On passe à la réplique suivante

            if (indToRead == fullOriginalTextList.size()){
                mettreBouton(0);
            }
        }
    }

    // 0 c'est terminé / 1 mettre micro / 2 mettre play
    private void mettreBouton(int etat){
        ImageButton bouton = this.findViewById(R.id.Mic_Button_Reciter);
        TextView texteBouton = this.findViewById(R.id.Tour_Reciter);

        if (etat == 0)
            passerAResultatSimple();
            //texteBouton.setText("C'est terminé");
        else if (etat==1){
            texteBouton.setText("C'est à vous");
            bouton.setImageResource(R.drawable.ic_mic_black_50dp);
        }
        else if (etat==2){
            bouton.setImageResource(R.drawable.ic_play_arrow_black_50dp);
            texteBouton.setText("Appuyez pour entendre\nla phrase suivante");
        }
    }

    private String recordSpeechToText(){
        String textSaid="";
        try {
            SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            config.setSpeechRecognitionLanguage("fr-FR");
            SpeechRecognizer reco = new SpeechRecognizer(config);
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

        if (!somethingHasBeenSaid) {
            Toast.makeText(getApplicationContext(), "Dites au moins une réplique avant de terminer la session",Toast.LENGTH_SHORT).show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("En cliquant ici, vous allez vers la correction de votre répétition.\n" +
                    "Avez-vous vraiment terminé ?");
            builder.setCancelable(false);
            builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    passerAResultatSimple(); }
            });
            builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();

        }
    }
    private void passerAResultatSimple(){
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

            CompareTask txtComparison = new CompareTask();

            Map<String,Object> m=new HashMap<>();
            m.put("originalText",originalTextList);
            m.put("textRead",saidTextList);
            txtComparison.execute(m);

            String response = "no response";
            score = -1;
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
        ResultatSimpleActivity = new Intent(ReciterTexte.this, ResultatSimple.class);
        ResultatSimpleActivity.putExtra(SCORE_KEY, score);
        ResultatSimpleActivity.putExtra(RESULT_TEXT_KEY, text);
        ResultatSimpleActivity.putExtra(ReciterTexte.OTL_KEY, originalTextList);
        ResultatSimpleActivity.putExtra(TextManagerActivity.CURRENT_TEXT_KEY, currentText);
        ResultatSimpleActivity.putExtra(TextManagerActivity.ORDER_TEXT_KEY,whoReads);
        ResultatSimpleActivity.putExtra(TextManagerActivity.CURRENT_TEXT_ID,idText);
        ResultatSimpleActivity.putExtra(TextManagerActivity.CLIENT_ID, idClient);
        startActivityForResult(ResultatSimpleActivity,1111);
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
            ResultatSimpleActivity.putExtra("textScore", textScore);
            ResultatSimpleActivity.putExtra("idText", idText);
            setResult(RESULT_OK, ResultatSimpleActivity);
            finish();

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

            String json = "";
            JSONObject jo=new JSONObject();
            //JSONObject textId = new JSONObject();

            try{
                JSONArray jaIdText=new JSONArray();
                jaIdText.put(idText);
                /*JSONArray jaIdClient=new JSONArray();
                jaIdClient.put(idClient);
                jo.put("idClient",jaIdClient);*/
                jo.put("idText",jaIdText);


                for(Map<String, Object> m: mapList){
                    for(String s : m.keySet()){
                        List<String> ls=(List<String>)m.get(s);
                        JSONArray ja=new JSONArray();
                        for(String stringList : ls){
                            ja.put(stringList);
                        }
                        jo.put(s,ja);
                    }
                }
                json=jo.toString();
            }catch(JSONException je){
                return je.getMessage();
            }

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(stringUrl)
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                //return "error in http post text comparison results request";
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private static class KeyTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            String stringUrl = "https://recitoback.azurewebsites.net/RetrieveSpeechKey";

            Request request = new Request.Builder()
                    .url(stringUrl)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonTest = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonTest);
                return jsonObject.toString();
            } catch (IOException | JSONException e) {
                //return "error in http get speech subscription key request";
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}