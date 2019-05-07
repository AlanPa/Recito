package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoadPdfActivity extends AppCompatActivity {
    public static final int REQUEST_OPEN_RESULT_CODE=10;
    public static final int PERMISSION_REQUEST_STORAGE=1000;
    private File pdfFile = null;
    private String pathUri = null;
    private String currentText;
    private Uri uri=null;
    private TextView uriFileLabel = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }
        setContentView(R.layout.activity_load_pdf);
        uriFileLabel=findViewById(R.id.Hint_For_Load_Page);

    }

    public void onChooseFileButton(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        //intent.setType("text/*");
        startActivityForResult(intent, REQUEST_OPEN_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_OPEN_RESULT_CODE && resultCode== Activity.RESULT_OK)
        {
            if(data!=null)
            {
                Context context = this.getApplicationContext();
                String path = context.getExternalFilesDir(null).getPath();
                uri =data.getData();
                if(uri.getPath().contains(path.split("/")[0]))
                {
                    pathUri=uri.getPath().substring(uri.getPath().indexOf(":")+1);
                }
                else
                {
                    pathUri=path.split("Android")[0].substring(1)+ uri.getPath().substring(uri.getPath().indexOf(":")+1);
                }
                String uriElements []= pathUri.split("/");
                uriFileLabel.setText(uriElements[uriElements.length-1]);
            }
        }
    }

    public void onValidationButton(View view) {

        try{
            pdfFile = new File(pathUri);
            /*BufferedReader br = new BufferedReader((new FileReader(pdfFile)));
            String line;
            while((line=br.readLine())!=null)
            {
                currentText+=line;
                currentText+="\n";
            }
            br.close();*/
            /*PDDocument document = null;
            //content://com.android.providers.downloads.documents/document/raw:/storage/emulated/0/Download/1.pdf
            document = PDDocument.load(new File(uri));
            document.getClass();
            if( !document.isEncrypted() ){
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition( true );
                PDFTextStripper Tstripper = new PDFTextStripper();
                currentText = Tstripper.getText(document);
            }*/


        }catch(Exception e){
            e.printStackTrace();
        }
        if (pdfFile!=null) {
            new FetchTask().execute("https://recitoback.azurewebsites.net/RetrieveFile");
        }
        else
        {
            /*String defaultText ="Maître corbeau sur un arbre perché tenait en son bec un fromage.\n" +
                    "Maître renard par l'odeur alléché lui tint à peu près ce langage.\n" +
                    "Ceci est la deuxième phrase.\n" +
                    "C'est bien, tu connais ton texte.\n" +
                    "Ceci est la troisième phrase.\n" +
                    "Bravo !\n" +
                    "Ceci est la quatrième phrase.";*/
            //String defaultText ="Maître corbeau\nsur un arbre perché\ntenait dans son bec un fromage.";

            String defaultText ="Maître corbeau sur un arbre perché, tenait en son bec un fromage\n" +
                    "Maître renard par l'odeur alléché lui tint à peu près ce langage\n" +
                    "Et bonjour monsieur du corbeau\n"+
                    "Que vous êtes joli, que vous me semblez beau\n"+
                    "Sans mentir si votre ramage se rapporte à votre plumage\n"+
                    "Vous êtes le phénix des hôtes de ces bois";

            Intent intent = new Intent(LoadPdfActivity.this, TextManagerActivity.class);
            intent.putExtra(TextManagerActivity.CURRENT_TEXT_KEY, defaultText);
            setResult(RESULT_OK,intent);
            startActivity(intent);
        }
    }

    private class FetchTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            String stringUrl = strings[0];
            //String pdfFile = strings[1];

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", pdfFile.getName(),
                            RequestBody.create(MediaType.parse("application/pdf"), pdfFile))
                    .build();
            /*RequestBody body = new FormBody.Builder()
                    .add("file", pdfFile)
                    .build();*/
            Request request = new Request.Builder()
                    .url(stringUrl)
                    .addHeader("Content-type","multipart/form-data")
                    .addHeader("Content-Disposition","form-data")
                    .post(requestBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                //Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonTest = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonTest);
                System.out.println("---------------------------------"+jsonObject.toString());
                return jsonObject.getString("Text");
            } catch (IOException | JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                currentText=null;
                Toast.makeText(getApplicationContext(),"Une erreur s'est produite",Toast.LENGTH_SHORT).show();
            } else {
                //Intent intentLoadFile = new Intent(LoadPdfActivity.this, LibraryActivity.class);
                Intent intentLoadFile = new Intent(LoadPdfActivity.this, TextManagerActivity.class);
                intentLoadFile.putExtra("currentText", s);
                setResult(RESULT_OK,intentLoadFile);
                startActivity(intentLoadFile);
            }
        }
    }


}
