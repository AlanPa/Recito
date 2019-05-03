package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.support.v4.app.ActivityCompat;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.difflib.algorithm.DiffException;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;
import android.view.View;

public class ReciterTexte extends AppCompatActivity {
    // Replace below with your own subscription key
    private static String speechSubscriptionKey = "5eae85560bb241b884f09a170d1a3214";
    // Replace below with your own service region (e.g., "westus").
    private static String serviceRegion = "francecentral";

    public static final String SCORE_KEY="score_key";
    public static final String RESULT_TEXT_KEY="result_text_key";
    private int score;
    private String resultText;
    private String currentText = null;

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
            recognizedTextView.setText("Could not initialize SpeeckSDK: " + ex.toString());
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

        // Récupérer le résultat en html (et mise à jour du score en interne)
        resultText=compareTexts(view);

        Intent ResultatSimpleActivity = new Intent(ReciterTexte.this, ResultatSimple.class);
        ResultatSimpleActivity.putExtra(SCORE_KEY, score);
        ResultatSimpleActivity.putExtra(RESULT_TEXT_KEY,resultText);
        ResultatSimpleActivity.putExtra(TextManagerActivity.CURRENT_TEXT_KEY,currentText);
        setResult(RESULT_OK,ResultatSimpleActivity);
        finish();
        startActivity(ResultatSimpleActivity);
    }



    private String compareTexts(View v){
        TextView txt = (TextView) this.findViewById(R.id.TexteDit_Reciter);

        //create a configured DiffRowGenerator
        DiffRowGenerator generator = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .mergeOriginalRevised(true)
                .inlineDiffByWord(true)
                .oldTag(f -> "~")      //introduce markdown style for strikethrough
                .newTag(f -> "*")     //introduce markdown style for bold
                .build();

        //compute the differences for two test texts.
        List<DiffRow> rows = null;
        try {
            rows = generator.generateDiffRows(

                    Arrays.asList(txt.getText().toString()),
                    Arrays.asList(currentText));
        } catch (DiffException e) {
            e.printStackTrace();
        }

        return (editToHtmlResult(rows.get(0).getOldLine()));
    }


    //////////////
    // TODO Modifier de manière à ne pas prendre en compte les suppr/ajouts comme deux erreurs
    // TODO Faire un pourcentage correct
    // TODO Enlever ponctuation
    //////////////

    private String editToHtmlResult(String resultLine){
        String newResult="";

        char gras = '*';
        char barre = '~';
        boolean baliseOuvrante = true;
        int nbOublis = 0;
        int nbAjouts = 0;

        for (int i=0; i < resultLine.length(); i++)
        {
            if (resultLine.charAt(i) == gras ) {
                if (baliseOuvrante) {
                    newResult += "<b> ";
                    nbOublis++;
                } else {
                    newResult += "</b> ";
                }
                baliseOuvrante=!baliseOuvrante;
            }
            else if (resultLine.charAt(i) == barre) {
                if (baliseOuvrante) {
                    newResult += "<strike> ";
                    nbAjouts++;
                } else {
                    newResult += "</strike>" ;
                }
                baliseOuvrante=!baliseOuvrante;

            }
            else {
                newResult += resultLine.charAt(i);
            }
        }

        String resFautes = "<br/><br/> Il y a eu "+nbAjouts+" ajouts de mots, et "+nbOublis+" oublis.";
        score = nbAjouts+nbOublis;
        return newResult+resFautes;
    }

}