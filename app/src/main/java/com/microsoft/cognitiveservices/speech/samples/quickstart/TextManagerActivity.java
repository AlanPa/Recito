package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;


public class TextManagerActivity extends AppCompatActivity {
    public static final String CURRENT_TEXT_ID="current_text_id";
    public static final String CURRENT_TEXT_KEY="current_text_key";
    public static final String ORDER_TEXT_KEY="order_text_key";
    public static final String CLIENT_ID="id_client";
    private final String urlTORequest = "https://recitoback.azurewebsites.net/getText";
    private String currentText="Chargement...";
    private TableLayout currentTable = null;
    private Button startReciteButton=null;
    private ImageButton readReciteButton=null;
    private String currentTextID="none";
    private TextToSpeech tts;
    private ArrayList<Integer> orderText=new ArrayList<>();
    private boolean somethingIsSelected = false;
    private Context curContext;
    Intent ReciteTexteActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_manager);

        //currentTextView = findViewById(R.id.Text_Item_Text);
        startReciteButton = findViewById(R.id.Start_Button_Item_Text);
        readReciteButton = findViewById(R.id.Read_Button_Item_Text);
        currentTable = findViewById(R.id.Table_Text);

        Intent curIntent = getIntent();
        curContext=getApplicationContext();
        currentTextID = curIntent.getStringExtra("idText");
        new FetchTask().execute(urlTORequest, curIntent.getStringExtra("idClient"), currentTextID);

        //currentTextView.setText(currentText);
        //currentTextView.setMovementMethod(new ScrollingMovementMethod());

        tts=new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.FRENCH);
            }
        });



        startReciteButton.setOnClickListener(view -> {

            for(int i=0; i< currentTable.getChildCount();i++)
            {
                TableRow tr = (TableRow) currentTable.getChildAt(i);
                TextView tv = (TextView) tr.getChildAt(0);
                if(tv.getBackground().getAlpha() == 99)
                {
                    orderText.add(0);
                    somethingIsSelected = true;
                }
                else
                {
                    orderText.add(1);
                }
            }

            if (!somethingIsSelected){
                orderText.clear();
                Toast.makeText(getApplicationContext(), "Veuillez sélectionner au moins une réplique avant de commencer une répétition.",Toast.LENGTH_SHORT).show();
            }
            else {
                ReciteTexteActivity = new Intent(TextManagerActivity.this, ReciterTexte.class);
                ReciteTexteActivity.putExtra(CURRENT_TEXT_ID, currentTextID);
                ReciteTexteActivity.putExtra(CURRENT_TEXT_KEY, currentText);
                ReciteTexteActivity.putExtra(ORDER_TEXT_KEY, orderText);
                ReciteTexteActivity.putExtra(CLIENT_ID, curIntent.getStringExtra("idClient"));
                //ReciteTexteActivity.putExtras(getIntent());
                setResult(RESULT_OK, ReciteTexteActivity);
                startActivityForResult(ReciteTexteActivity,1111);
            }
        });

        readReciteButton.setOnClickListener(view -> {
           // String toSpeak = currentText;
            String toSpeak ="";
            for(int i=0; i< currentTable.getChildCount();i++)
            {
                TableRow tr = (TableRow) currentTable.getChildAt(i);
                TextView tv = (TextView) tr.getChildAt(0);
                toSpeak += tv.getText();
            }
            tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        });
    }

    public void onPause(){
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            int textScore = data.getIntExtra("textScore", -1);
            String idText = data.getStringExtra("idText");
            ReciteTexteActivity.putExtra("textScore", textScore);
            ReciteTexteActivity.putExtra("idText", idText);
            setResult(RESULT_OK, ReciteTexteActivity);
            finish();

        }
    }

    private class FetchTask extends AsyncTask<String, Void, String> {
        private String errorMessage="Error : Texte non trouvé";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON =MediaType.parse("application/json; charset=utf-8");
            JsonObject json = new JsonObject();
            json.addProperty("idClient", strings[1]);
            json.addProperty("idText", strings[2]);

            Request request = new Request.Builder()
                    .url(strings[0])
                    .post(RequestBody.create(JSON, json.toString()))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonTest = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonTest);
                System.out.println("---------------------------------"+jsonObject.toString());
                if(jsonObject.has("text"))
                {
                    currentText=jsonObject.getJSONObject("text").getString("contenu");
                    return "true";
                }
                else
                {
                    if(jsonObject.has("Message"))
                    {
                        errorMessage=jsonObject.getString("Message");
                    }
                    currentText="Error : Texte non trouvé";
                    return "false";
                }

            } catch (IOException | JSONException e) {
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.compareTo("false")==0) {
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
            String[] repliques= currentText.split("\n");
            for (String laReplique:repliques) {
                if(!laReplique.equals(""))
                {
                    TableRow row= new TableRow(curContext);
                    TextView leText = new TextView(curContext);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);
                    leText.setText("  "+laReplique+"\n");
                    leText.setMovementMethod(new ScrollingMovementMethod());
                    leText.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light,null));
                    leText.getBackground().setAlpha(0);
                    leText.setClickable(true);
                    leText.setOnClickListener(v -> {
                        if( leText.getBackground().getAlpha() == 0)
                        {
                            leText.getBackground().setAlpha(100);
                        }
                        else{

                            leText.getBackground().setAlpha(0);
                        }

                    });
                    row.addView(leText);
                    currentTable.addView(row);
                }
            }

        }
    }
}
