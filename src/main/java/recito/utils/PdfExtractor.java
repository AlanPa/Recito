package recito.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfExtractor {

    @Value("${azure.token.translator:}")
    private static String tokenAzure;

    private static final Logger log = LoggerFactory.getLogger(PdfExtractor.class);

    private PdfExtractor(){}

    public static String getLanguage(String text) throws IOException{
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "[{\n\t\"Text\": \""+text+"\"\n}]");
        final String SERVER_ADRESS = "https://api.cognitive.microsofttranslator.com/detect?api-version=3.0";

        try{
            Request request = new Request.Builder()
                    .url(SERVER_ADRESS).post(body)
                    .addHeader("Ocp-Apim-Subscription-Key", tokenAzure)
                    .addHeader("Content-type", "application/json").build();
            Response response = client.newCall(request).execute();
            String jsonTest = response.body().string();

            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(jsonTest);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(json);
        }catch (IOException e){
            throw new IOException("Réponse de l'API avec une erreur : "+e.getMessage());
        }
    }

    public static String extract(MultipartFile file) throws IOException{
        InputStream is;
        PDDocument pd;
        String textFromPdf;

        String contentType=file.getContentType();
        String fileName=file.getOriginalFilename();

        if(contentType==null||!contentType.equals("application/pdf")){
            throw new IOException("Mauvais type de fichier, un pdf est demandé !");
        }

        if(fileName==null||fileName.length()<4||!fileName.endsWith(".pdf")){
            throw new IOException("Mauvais type de fichier, un pdf est attendu !");
        }

        is=new BufferedInputStream(file.getInputStream());
        is.mark(10);
        byte[] startOctect=new byte[5];
        int isOK=is.read(startOctect,0,5);
        is.reset();
        if (!(isOK!=-1 &&
                startOctect[0] == 0x25 && // %
                startOctect[1] == 0x50 && // P
                startOctect[2] == 0x44 && // D
                startOctect[3] == 0x46 && // F
                startOctect[4] == 0x2D)){
            throw new IOException("PDF corrompu ou trop court !");
        }

        try {
            pd = PDDocument.load(is);
            log.info("PDF have {} pages !",pd.getNumberOfPages());
            log.info("PDF is encrypted ? -> {}",pd.isEncrypted());

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1); //Start extracting from page 2
            stripper.setEndPage(pd.getNumberOfPages()); //Extract till page 3

            textFromPdf=stripper.getText(pd);
        } catch (Exception e){
            //FIXME Seems to not be throwed by pdfbo when a corrupt pdf is used, to investigate
            throw new IOException("La lecture du PDF a rencontrée l'erreur suivante : "+e.getMessage());
        }

        log.debug("-----------------Content---------------");
        log.debug(textFromPdf);

        pd.close();
        is.close();

        return textFromPdf;
    }

}
