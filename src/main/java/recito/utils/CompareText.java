package recito.utils;

import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class CompareText {

    private static final Logger log = LoggerFactory.getLogger(PdfExtractor.class);

    private CompareText(){}

    public static Pair<Integer, String> calculateFullResult(String originalText, String saidText){
        // Récupérer le résultat
        String resultTextBeforeHTML=compareTexts(originalText,saidText);

        // Calculs nombres de mots
        int nbWordsOriginalText = countWordsInText(originalText);
        int nbWordsSaidText = countWordsInText(saidText);
        int nbAjouts = calculateNumberWrongWords(resultTextBeforeHTML,'~');
        int nbCorrects = nbWordsSaidText-nbAjouts;
        int nbOublis = nbWordsOriginalText-nbCorrects;

        String nbMots = "<br/><br/><br/> Vous deviez dire " + nbWordsOriginalText+" mots, vous en avez dit " + nbWordsSaidText+".<br/><br/> Parmi ceux-ci, "+nbCorrects+" étaient corrects. <br/> Vous avez ajouté "+nbAjouts+" mot(s) et vous en avez oublié "+nbOublis+".";

        int score = calculateScore(nbWordsOriginalText, nbCorrects, nbAjouts);
        String fullTextResult = editToHtmlResult(resultTextBeforeHTML)+nbMots;

        return new Pair<>(score,fullTextResult);
    }

    // Compare deux chaînes de caractères et renvoie les mots oubliés entre '*' et les mots ajoutés entre '~'
    private static String compareTexts(String originalText, String saidText){
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
            return (rows.get(0).getOldLine());
        } catch (Exception e) {
            log.error("La comparaison a engendré une erreur : ",e);
            return "";
        }

    }

    // Passe en html une chaîne de caractère où le gras est indiqué entre '*' et le barré entre '~'
    private static String editToHtmlResult(String resultLine){

        char gras = '*';
        char barre = '~';
        boolean baliseOuvrante = true;
        StringBuilder sb=new StringBuilder();

        for (int i=0; i < resultLine.length(); i++)
        {
            if (resultLine.charAt(i) == gras ) {
                if (baliseOuvrante) {
                    sb.append("<b> ");
                } else {
                    sb.append("</b> ");
                }
                baliseOuvrante=!baliseOuvrante;
            }
            else if (resultLine.charAt(i) == barre) {
                if (baliseOuvrante) {
                    sb.append("<strike> ");
                } else {
                    sb.append("</strike>");
                }
                baliseOuvrante=!baliseOuvrante;

            }
            else {
                sb.append(resultLine.charAt(i));
            }
        }

        return sb.toString();
    }

    // Compte le nombre de mots dans une chaîne de caractères
    private static int countWordsInText(String text){
        int nbWords = 0;
        int previousChar='.';
        boolean onlyOneWord = true;
        StringBuilder charlus=new StringBuilder();


        for (int i=0; i < text.length(); i++)
        {
            charlus.append(text.charAt(i));

            if (text.charAt(i) == ' ' && previousChar!=' ' && i!=0) {
                log.debug("/////// Char lus quand ++ = {}",charlus);
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
    private static int calculateNumberWrongWords(String resultText, char limit){
        boolean correctPart = false;
        StringBuilder words=new StringBuilder();

        for (int i=0; i < resultText.length(); i++) {
            if (resultText.charAt(i) == limit){
                correctPart = !correctPart;
                words.append(" ");
            }
            else if (correctPart && resultText.charAt(i)!='.' && resultText.charAt(i)!=','){
                words.append(resultText.charAt(i));
            }
        }
        return countWordsInText(words.toString());
    }

    // Calcule un score en fonction du nombre de mots du texte original, du nombre de mots corrects, du nombre de mots ajoutés
    private static int calculateScore(int nbWordsOriginalText, int nbCorrects, int nbAjouts){
        int score = (int)((nbCorrects-nbAjouts/1.5)*100/nbWordsOriginalText);
        if (score<0){
            score = 0;
        }
        else if (score > 100){
            score = 100;
        }

        return score;
    }
}
