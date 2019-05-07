package recito.utils;

import com.github.difflib.algorithm.DiffException;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompareText {
    private static final Logger log = LoggerFactory.getLogger(PdfExtractor.class);

    private CompareText(){}

    public static Pair<Integer, String> calculateFullResult(List<String> oTL, List<String> sTL) {
        int nbWordsOriginalText = 0;
        int nbWordsSaidText = 0;
        int nbAjouts = 0;
        int nbCorrects;
        int nbOublis;
        StringBuilder fullTextResult = new StringBuilder();
        String resultTextBeforeHTML;
        String saidText;
        String originalText;

        for (int i = 0; i < sTL.size(); i++) {
            saidText = sTL.get(i);
            originalText = oTL.get(i);saidText = saidText.replace(".","");
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
            fullTextResult.append(editToHtmlResult(resultTextBeforeHTML)+"<br/><br/>");

        }
        nbCorrects = nbWordsSaidText-nbAjouts;
        nbOublis = nbWordsOriginalText-nbCorrects;

        String nbMots = "<br/><br/><br/> Vous deviez dire " + nbWordsOriginalText+" mots.<br/><br/> Parmis ceux-ci, "+nbCorrects+" étaient corrects. <br/> Vous avez ajouté "+nbAjouts+" mot(s) et vous en avez oublié "+nbOublis+".";
        fullTextResult.append(nbMots);

        int score = calculateScore(nbWordsOriginalText, nbCorrects, nbAjouts);
        return new Pair<>(score,fullTextResult.toString());
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
                    Arrays.asList(saidText.toLowerCase()),
                    Arrays.asList(originalText.toLowerCase()));
            return (rows.get(0).getOldLine());
        } catch (DiffException e) {
            log.error("La comparaison a engendré une erreur : ",e);
            return "";
        }
    }

    // Passe en html une chaîne de caractère où le gras est indiqué entre '*' et le barré entre '~'
    private static String editToHtmlResult(String resultLine){
        StringBuilder newResult = new StringBuilder();

        char gras = '*';
        char barre = '~';
        boolean baliseOuvrante = true;

        for (int i=0; i < resultLine.length(); i++)
        {
            if (resultLine.charAt(i) == gras ) {
                if (baliseOuvrante) {
                    newResult.append("<b> ");
                } else {
                    newResult.append("</b> ");
                }
                baliseOuvrante=!baliseOuvrante;
            }
            else if (resultLine.charAt(i) == barre) {
                if (baliseOuvrante) {
                    newResult.append("<strike> ");
                    //nbAjouts++;
                } else {
                    newResult.append("</strike>");
                }
                baliseOuvrante=!baliseOuvrante;

            }
            else {
                newResult.append(resultLine.charAt(i));
            }
        }
        return newResult.toString();
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
