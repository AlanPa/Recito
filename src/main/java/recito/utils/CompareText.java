package recito.utils;

import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

public class CompareText {
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

        Pair<Integer, String> fullResult = new Pair<>(score,fullTextResult);
        return fullResult;
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (rows.get(0).getOldLine());
    }

    // Passe en html une chaîne de caractère où le gras est indiqué entre '*' et le barré entre '~'
    private static String editToHtmlResult(String resultLine){
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
    private static int countWordsInText(String text){
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
    private static int calculateNumberWrongWords(String resultText, char limit){
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
