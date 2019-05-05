package recito.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class PdfExtractorTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void getTextWithBadContent() throws Exception {
        FileInputStream inputFile = new FileInputStream( "src/test/resources/correct.pdf");
        MockMultipartFile file = new MockMultipartFile("file", "NameOfTheFile.pdf", "application/json", inputFile);
        exception.expect(IOException.class);
        exception.expectMessage("Mauvais type de fichier, un pdf est demandé !");
        PdfExtractor.extract(file);
    }

    @Test
    public void getTextWithBadName() throws Exception {
        FileInputStream inputFile = new FileInputStream( "src/test/resources/correct.pdf");
        MockMultipartFile file = new MockMultipartFile("file", "NameOfTheFile.pdf.exe", "application/pdf", inputFile);
        exception.expect(IOException.class);
        exception.expectMessage("Mauvais type de fichier, un pdf est attendu !");
        PdfExtractor.extract(file);
    }

    @Test
    public void getTextWithPdfCorrect() throws Exception {
        FileInputStream inputFile = new FileInputStream( "src/test/resources/correct.pdf");
        MockMultipartFile file = new MockMultipartFile("file", "NameOfTheFile.pdf", "application/pdf", inputFile);
        assertThat(PdfExtractor.extract(file), equalTo("This is a correct pdf\n"));
    }

    @Test
    public void getTextWithPdfCorrupted() throws Exception {
        FileInputStream inputFile = new FileInputStream( "src/test/resources/corrupted.pdf");
        MockMultipartFile file = new MockMultipartFile("file", "NameOfTheFile.pdf", "application/pdf", inputFile);
        // FIXME See fixme in PdfExtractor class
        // exception.expect(IOException.class);
        PdfExtractor.extract(file);
    }

    @Test
    public void getTextWithPdfCorrectButEmpty() throws Exception {
        FileInputStream inputFile = new FileInputStream( "src/test/resources/correctAndEmpty.pdf");
        MockMultipartFile file = new MockMultipartFile("file", "NameOfTheFile.pdf", "application/pdf", inputFile);
        exception.expect(IOException.class);
        exception.expectMessage("PDF corrompu ou trop court !");
        PdfExtractor.extract(file);
    }
}
