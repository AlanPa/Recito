package recito.utils;

import javafx.util.Pair;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class CompareTextTest {

    @Test
    public void compareIdenticalTexts() {
        // given
        String originalText = "The text read is identical to the original one.";
        String textRead = "The text read is identical to the original one.";

        // when
        Pair<Integer, String> res = CompareText.calculateFullResult(originalText, textRead);
        Pair<Integer, String> expected = new Pair<>(100, "The text read is identical to the original one.<br/><br/><br/> Vous deviez dire 9 mots, vous en avez dit 9.<br/><br/> Parmi ceux-ci, 9 étaient corrects. <br/> Vous avez ajouté 0 mot(s) et vous en avez oublié 0.");

        // then
        assertThat(res, equalTo(expected));
    }

    @Test
    public void compareTextsWithMissingWord() {
        // given
        String originalText = "The text read is identical to the original one.";
        String textRead = "The text read is to the original one.";

        // when
        Pair<Integer, String> res = CompareText.calculateFullResult(originalText, textRead);
        Pair<Integer, String> expected = new Pair<>(88, "The text read is <b> identical </b> to the original one.<br/><br/><br/> Vous deviez dire 9 mots, vous en avez dit 8.<br/><br/> Parmi ceux-ci, 8 étaient corrects. <br/> Vous avez ajouté 0 mot(s) et vous en avez oublié 1.");

        // then
        assertThat(res, equalTo(expected));
    }

    @Test
    public void compareTextsWithAddedWord() {
        // given
        String originalText = "The text read is identical to the original one.";
        String textRead = "The text read is not identical to the original one.";

        // when
        Pair<Integer, String> res = CompareText.calculateFullResult(originalText, textRead);
        Pair<Integer, String> expected = new Pair<>(92, "The text read is <strike> not </strike>identical to the original one.<br/><br/><br/> Vous deviez dire 9 mots, vous en avez dit 10.<br/><br/> Parmi ceux-ci, 9 étaient corrects. <br/> Vous avez ajouté 1 mot(s) et vous en avez oublié 0.");

        // then
        assertThat(res, equalTo(expected));
    }
}
