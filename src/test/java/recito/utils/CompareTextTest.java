package recito.utils;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class CompareTextTest {

    @Test
    public void compareIdenticalTexts() {
        // given
        ArrayList<String> originalText = new ArrayList<>();
        originalText.add("The text read is identical to the original one.");
        ArrayList<String> textRead = new ArrayList<>();
        textRead.add("the text read is identical to the original one");

        // when
        Pair<Integer, String> res = CompareText.calculateFullResult(originalText, textRead);
        // TODO change Parmis to Parmi
        Pair<Integer, String> expected = new Pair<>(100, "the text read is identical to the original one<br/><br/><br/><br/><br/> Vous deviez dire 9 mots.<br/><br/> Parmis ceux-ci, 9 étaient corrects. <br/> Vous avez ajouté 0 mot(s) et vous en avez oublié 0.");

        // then
        assertThat(res, equalTo(expected));
    }

    @Test
    public void compareTextsWithMissingWord() {
        // given
        ArrayList<String> originalText = new ArrayList<>();
        originalText.add("The text read is identical to the original one.");
        ArrayList<String> textRead = new ArrayList<>();
        textRead.add("the text read is to the original one");

        // when
        Pair<Integer, String> res = CompareText.calculateFullResult(originalText, textRead);
        // TODO change Parmis to Parmi
        Pair<Integer, String> expected = new Pair<>(88, "the text read is <b> identical </b> to the original one<br/><br/><br/><br/><br/> Vous deviez dire 9 mots.<br/><br/> Parmis ceux-ci, 8 étaient corrects. <br/> Vous avez ajouté 0 mot(s) et vous en avez oublié 1.");

        // then
        assertThat(res, equalTo(expected));
    }

    @Test
    public void compareTextsWithAddedWord() {
        // given
        ArrayList<String> originalText = new ArrayList<>();
        originalText.add("The text read is identical to the original one.");
        ArrayList<String> textRead = new ArrayList<>();
        textRead.add("the text read is not identical to the original one");

        // when
        Pair<Integer, String> res = CompareText.calculateFullResult(originalText, textRead);
        // TODO change Parmis to Parmi
        Pair<Integer, String> expected = new Pair<>(88, "the text read is <strike> not </strike>identical to the original one<br/><br/><br/><br/><br/> Vous deviez dire 9 mots.<br/><br/> Parmis ceux-ci, 9 étaient corrects. <br/> Vous avez ajouté 1 mot(s) et vous en avez oublié 0.");

        // then
        assertThat(res, equalTo(expected));
    }
}
