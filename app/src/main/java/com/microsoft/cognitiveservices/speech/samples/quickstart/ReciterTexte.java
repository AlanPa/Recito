package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    public static final String OTL_KEY = "originalTextList_key";
    public static final String STL_KEY = "saidTextList_key";
    private String currentText = null;
    private ArrayList<Integer> whoReads;
    private ArrayList<Pair<String, Integer>> fullOriginalTextList; // Le texte et le "qui doit parler"
    private ArrayList<String> originalTextList;
    private ArrayList<String> saidTextList;
    private int indToRead = 0; // Pour l'enregistrement phrase par phrase
    private int nbLinesToRead;
    private boolean somethingHasBeenSaid = false;

    private TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciter_texte);

        // Récupérer le texte à dire et qui doit le dire
        Intent currentIntent = getIntent();
        currentText = currentIntent.getStringExtra(TextManagerActivity.CURRENT_TEXT_KEY);
        whoReads = currentIntent.getIntegerArrayListExtra(TextManagerActivity.ORDER_TEXT_KEY);
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
            }
            String toWrite;
            // Si c'est à l'utilisateur de parler, on enregistre et on stocke ce qui est dit et l'original
            if (fullOriginalTextList.get(indToRead).second == 0) {
                //Toast.makeText(getApplicationContext(), "Enregistrement en cours",Toast.LENGTH_SHORT).show();
                String said = recordSpeechToText();
                toWrite = "<i>Vous : " + said+"</i>";

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
                tts.speak(textADire, TextToSpeech.QUEUE_FLUSH, null);

                // Si la prochaine phrase est à dire par l'utilisateur, on change le bouton
                if (indToRead+1 < fullOriginalTextList.size() && fullOriginalTextList.get(indToRead+1).second==0){
                    mettreBouton(1);
                }
            }
            writeHere.setText(Html.fromHtml(toWrite));
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
            texteBouton.setText("C'est terminé");
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
            boolean arreter = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("En cliquant ici, vous allez vers la correction de votre répétition.\n" +
                        "Avez-vous vraiment terminé ?");
                builder.setCancelable(false);
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                        Pair<Integer, String> fullResultList = calculateFullResult(originalTextList, saidTextList);

                        // Passer à l'activité suivante
                        Intent ResultatSimpleActivity = new Intent(ReciterTexte.this, ResultatSimple.class);
                        ResultatSimpleActivity.putExtra(SCORE_KEY, fullResultList.first);
                        ResultatSimpleActivity.putExtra(RESULT_TEXT_KEY, fullResultList.second);
                        ResultatSimpleActivity.putExtra(ReciterTexte.OTL_KEY, originalTextList);
                        ResultatSimpleActivity.putExtra(TextManagerActivity.CURRENT_TEXT_KEY, currentText);
                        ResultatSimpleActivity.putExtra(TextManagerActivity.ORDER_TEXT_KEY,whoReads);
                        setResult(RESULT_OK, ResultatSimpleActivity);
                        finish();
                        startActivity(ResultatSimpleActivity); }
                });
                builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      }
                });
                builder.show();
            }

    }

    private Pair<Integer, String> calculateFullResult(ArrayList<String> oTL, ArrayList<String> sTL){
        int nbWordsOriginalText = 0;
        int nbWordsSaidText = 0;
        int nbAjouts = 0;
        int nbCorrects;
        int nbOublis;
        String fullTextResult = "";
        String resultTextBeforeHTML;
        String saidText;
        String originalText;

        for (int i = 0 ; i<sTL.size() ; i++) {
            saidText = sTL.get(i);
            originalText = oTL.get(i);

            saidText = saidText.replace(".","");
            saidText = saidText.replace(",","");
            saidText = saidText.replace("?","");
            saidText = saidText.replace("!","");
            originalText = originalText.replace(".","");
            originalText = originalText.replace(",","");
            originalText = originalText.replace("?","");
            originalText = originalText.replace("!","");

            // Récupérer le résultat
            resultTextBeforeHTML = compareTexts(originalText, saidText);

            // Calculs nombres de mots
            nbWordsOriginalText += countWordsInText(originalText);
            nbWordsSaidText += countWordsInText(saidText);
            nbAjouts += calculateNumberWrongWords(resultTextBeforeHTML, '~');

            // Ajout au texte du résultat détaillé
            fullTextResult += editToHtmlResult(resultTextBeforeHTML)+"<br/><br/>";

        }
        nbCorrects = nbWordsSaidText-nbAjouts;
        nbOublis = nbWordsOriginalText-nbCorrects;

        String nbMots = "<br/><br/><br/> Vous deviez dire " + nbWordsOriginalText+" mots.<br/><br/> Parmis ceux-ci, "+nbCorrects+" étaient corrects. <br/> Vous avez ajouté "+nbAjouts+" mot(s) et vous en avez oublié "+nbOublis+".";
        fullTextResult += nbMots;

        int score = calculateScore(nbWordsOriginalText, nbCorrects, nbAjouts);
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

                    Arrays.asList(saidText.toLowerCase()),
                    Arrays.asList(originalText.toLowerCase()));
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