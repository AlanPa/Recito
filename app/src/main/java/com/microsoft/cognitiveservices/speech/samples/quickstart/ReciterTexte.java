package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.difflib.algorithm.DiffException;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;
import android.view.View;
import android.widget.Toast;

public class ReciterTexte extends AppCompatActivity {
    // Replace below with your own subscription key
    private static String speechSubscriptionKey = "5eae85560bb241b884f09a170d1a3214";
    // Replace below with your own service region (e.g., "westus").
    private static String serviceRegion = "francecentral";

    public static final String SCORE_KEY="score_key";
    public static final String RESULT_TEXT_KEY="result_text_key";
    private String currentText = null;
    private ArrayList<Pair<String, Integer>> fullOriginalTextList; // Le texte et le "qui doit parler"
    private ArrayList<String> originalTextList;
    private ArrayList<String> saidTextList;
    private int indToRead = 0; // Pour l'enregistrement phrase par phrase

    private TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciter_texte);

        Intent currentIntent = getIntent();
        fullOriginalTextList = toCorrectStructure(currentIntent.getStringExtra(TextManagerActivity.CURRENT_TEXT_KEY));
        fullOriginalTextList = dialogLines(fullOriginalTextList);
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
            recognizedTextView.setText("Could not initialize SpeeckSDK: " + ex.toString());
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
                   bouton.setImageResource(R.drawable.ic_mic_red_50dp);
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
        //TODO : Modifier cette méthode. Il faut comparer originalTextList et saidTextList, élément par élément
        //TODO : Calculer un résultat simple entre deux phrases
        //TODO : Calculer un résultat global entre toutes les phrases
        //TODO : Créer String de résultat détaillé
        //TODO : (Ici la méthode ne fait qu'avec une seule phrase)


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
        int score = (int)(nbCorrects-nbAjouts/1.5)*100/nbWordsOriginalText;
        if (score<0){
            score = 0;
        }
        else if (score > 100){
            score = 100;
        }

        return score;
    }

}