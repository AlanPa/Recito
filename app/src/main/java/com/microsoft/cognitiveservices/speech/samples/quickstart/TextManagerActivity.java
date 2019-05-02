package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TextManagerActivity extends AppCompatActivity {
    private static final int RECITE_TEXT_ACTIVITY = 4;
    public static final String CURRENT_TEXT_ID="current_text_id";
    public static final String CURRENT_TEXT_KEY="current_text_key";
    private String currentText="blalalala";
    private TextView currentTextView=null;
    private Button startReciteButton=null;
    private Button readReciteButton=null;
    private String currentTextID="test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_manager);

        currentTextView = findViewById(R.id.Text_Item_Text);
        startReciteButton = findViewById(R.id.Start_Button_Item_Text);
        readReciteButton = findViewById(R.id.Read_Button_Item_Text);

        //Je remplis le currentText
        //getText("test");
        new FetchTask().execute("http://localhost:4000/GetId?id=5", currentTextID);

        currentTextView.setText(currentText);
        currentTextView.setMovementMethod(new ScrollingMovementMethod());

        startReciteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reciteTexteActivity = new Intent(TextManagerActivity.this, ReciterTexte.class);
                reciteTexteActivity.putExtra(CURRENT_TEXT_ID, currentTextID);
                startActivity(reciteTexteActivity);
            }
        });

        readReciteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Partie Clementine avec la lecture du currentTexte;
            }
        });
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
            String idText = strings[1];
            Request request = new Request.Builder().url(stringUrl).build();
            try {
                Response response = client.newCall(request).execute();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();



                String tmp = response.body().string();
                JsonParser parser = new JsonParser();
                JsonElement json = parser.parse(tmp);
                System.out.println("---------------------------------"+json.toString());
                return tmp;
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                currentText=null;
            } else {
                currentTextView.setText(s);
            }
        }
    }
    /*private String getText(String idText)
    {
        String text = null;
        String adresse = "https://www.google.fr";
        System.setProperty("http.keepAlive", "false");
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        URLConnection connexion = null;
        HttpURLConnection httpUrlConnection = null;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            boolean wifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            //if !wifi - alerte - pas en wifi


            try {

                // Encodage des paramètres de la requête
                String donnees = URLEncoder.encode("textID", "UTF-8") + "=" + URLEncoder.encode(idText, "UTF-8");

                // On a envoyé les données à une adresse distante
                URL url = new URL(adresse);
                connexion = url.openConnection();
                httpUrlConnection = (HttpURLConnection) connexion;
                httpUrlConnection.setDoOutput(true);
                httpUrlConnection.setChunkedStreamingMode(0);

                // On envoie la requête ici
                writer = new OutputStreamWriter(httpUrlConnection.getOutputStream());

                // On insère les données dans notre flux
                writer.write(donnees);

                // Et on s'assure que le flux est vidé
                writer.flush();

                // On lit la réponse ici
                System.out.println("reponse code : "+httpUrlConnection.getResponseCode());
                if (httpUrlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    System.out.println("Connection réussi");

                    reader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
                    String ligne;

                    // Tant que « ligne » n'est pas null, c'est que le flux n'a pas terminé d'envoyer des informations
                    while ((ligne = reader.readLine()) != null) {
                        text += ligne;
                    }
                }
                else
                {
                    //on peut catcher les erreurs
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    writer.close();
                } catch (Exception e) {
                }
                try {
                    reader.close();
                } catch (Exception e) {
                }
                try {
                    httpUrlConnection.disconnect();
                } catch (Exception e) {
                }
            }
        }

        return text;
    }*/

    private String getText(String idText) {
        String text = null;
        //System.setProperty("http.keepAlive", "false");

        OkHttpClient client = new OkHttpClient();
        //MediaType mediaType = MediaType.parse("application/json");
        //RequestBody body = RequestBody.create(mediaType, idText);
        final String SERVER_ADRESS = "https://www.google.com";

        Request request = new Request.Builder()
                .url(SERVER_ADRESS).build();
        Response response;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            boolean wifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            //if !wifi - alerte - pas en wifi

            client = new OkHttpClient();



            /*try {
                response = client.newCall(request).execute();
                System.out.println(response.body().string());
            } catch (IOException e) {
                return e.getMessage();
            }*/
        }
        return text;
    }
}
