package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.support.v4.app.ActivityCompat;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
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

        // Récupérer le texte dit par l'utilisateur
        TextView txt = (TextView) this.findViewById(R.id.TexteDit_Reciter);
        String saidText = txt.getText().toString();

        // Calculer un résultat complet
        Pair<Integer, String> fullResult = calculateFullResult(currentText,saidText);

        // Passer à l'activité suivante
        Intent ResultatSimpleActivity = new Intent(ReciterTexte.this, ResultatSimple.class);
        ResultatSimpleActivity.putExtra(SCORE_KEY, fullResult.first);
        ResultatSimpleActivity.putExtra(RESULT_TEXT_KEY,fullResult.second);
        ResultatSimpleActivity.putExtra(TextManagerActivity.CURRENT_TEXT_KEY,currentText);
        setResult(RESULT_OK,ResultatSimpleActivity);
        finish();
        startActivity(ResultatSimpleActivity);
    }


    private Pair<Integer, String> calculateFullResult(String originalText, String saidText){
        // Récupérer le résultat
        String resultTextBeforeHTML=compareTexts(currentText,saidText);

        // Calculs nombres de mots
        int nbWordsOriginalText = countWordsInText(currentText);
        int nbWordsSaidText = countWordsInText(saidText);
        int nbAjouts = calculateNumberWrongWords(resultTextBeforeHTML,'~');
        int nbCorrects = nbWordsSaidText-nbAjouts;
        int nbOublis = nbWordsOriginalText-nbCorrects;

        String nbMots = "<br/><br/><br/> Vous deviez dire " + nbWordsOriginalText+" mots, vous en avez dit " + nbWordsSaidText+".<br/><br/> Parmis ceux-ci, "+nbCorrects+" étaient corrects. <br/> Vous avez ajouté "+nbAjouts+" mot(s) et vous en avez oublié "+nbOublis+".";

        int score = calculateScore(nbWordsOriginalText, nbCorrects, nbAjouts);
        String fullTextResult = editToHtmlResult(resultTextBeforeHTML)+nbMots;

        Pair<Integer, String> fullResult = new Pair<>(score,fullTextResult);
        return fullResult;
    }

    // Compare deux chaînes de caractères et renvoie les mots oubliés entre '*' et les mots ajoutés entre '~'
    private String compareTexts(String originalText, String saidText){
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

                    Arrays.asList(saidText),
                    Arrays.asList(originalText));
        } catch (DiffException e) {
            e.printStackTrace();
        }

        return (rows.get(0).getOldLine());
    }

    // Passe en html une chaîne de caractère où le gras est indiqué entre '*' et le barré entre '~'
    private String editToHtmlResult(String resultLine){
        String newResult="";

        char gras = '*';
        char barre = '~';
        boolean baliseOuvrante = true;
        //int nbOublis = 0;
        //int nbAjouts = 0;

        for (int i=0; i < resultLine.length(); i++)
        {
            if (resultLine.charAt(i) == gras ) {
                if (baliseOuvrante) {
                    newResult += "<b> ";
                    //nbOublis++;
                } else {
                    newResult += "</b> ";
                }
                baliseOuvrante=!baliseOuvrante;
            }
            else if (resultLine.charAt(i) == barre) {
                if (baliseOuvrante) {
                    newResult += "<strike> ";
                    //nbAjouts++;
                } else {
                    newResult += "</strike>" ;
                }
                baliseOuvrante=!baliseOuvrante;

            }
            else {
                newResult += resultLine.charAt(i);
            }
        }

        //String resFautes = "<br/><br/> Il y a eu "+nbAjouts+" ajouts, et "+nbOublis+" oublis.";
        //score = nbAjouts+nbOublis;
        return newResult;
    }

    // Compte le nombre de mots dans une chaîne de caractères
    private int countWordsInText(String text){
        int nbWords = 0;
        int previousChar='.';
        boolean onlyOneWord = true;
        String charlus="";


        for (int i=0; i < text.length(); i++)
        {
            charlus += text.charAt(i);

           if (text.charAt(i) == ' ' && previousChar!=' ' && i!=0) {
               System.out.println("/////// Char lus quand ++ ="+charlus);
                nbWords++;
                if (onlyOneWord){
                    onlyOneWord=false;
                }
           }
           previousChar=text.charAt(i);
        }
        if (!onlyOneWord && text.charAt(text.length()-1)!=' '){
            nbWords++;
        }
        return (nbWords);
    }

    // Donne le nombre de mots compris entre deux caractères "limit" dans un String
    private int calculateNumberWrongWords(String resultText, char limit){
        boolean correctPart = false;
        String words="";

        for (int i=0; i < resultText.length(); i++) {
            if (resultText.charAt(i) == limit){
                correctPart = !correctPart;
                words += " ";
            }
            else if (correctPart && resultText.charAt(i)!='.' && resultText.charAt(i)!=','){
                words += resultText.charAt(i);
            }
        }
        return countWordsInText(words);
    }

    // Calcule un score en fonction du nombre de mots du texte original, du nombre de mots corrects, du nombre de mots ajoutés
    private int calculateScore(int nbWordsOriginalText, int nbCorrects, int nbAjouts){
        int score = (int)(nbCorrects-nbAjouts)*100/nbWordsOriginalText;
        if (score<0){
            score = 0;
        }
        else if (score > 100){
            score = 100;
        }

        return score;
    }

}