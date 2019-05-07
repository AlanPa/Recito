package com.microsoft.cognitiveservices.speech.samples.quickstart;

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
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.FormBody;
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
   // private String title="Votre texte à apprendre :";
    private String currentText="Maître \ncorbeau\nsur un arbre perché\ntenait dans son bec un fromage.";
    private TableLayout currentTable = null;
    private Button startReciteButton=null;
    private ImageButton readReciteButton=null;
    private long currentTextID=-1;
    private TextToSpeech tts;
    private ArrayList<Integer> orderText=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_manager);

        //currentTextView = findViewById(R.id.Text_Item_Text);
        startReciteButton = findViewById(R.id.Start_Button_Item_Text);
        readReciteButton = findViewById(R.id.Read_Button_Item_Text);
        currentTable = findViewById(R.id.Table_Text);

        //Je remplis le texte que l'utilisateur va devoir dire
        Intent curIntent = getIntent();
        //currentText=curIntent.getStringExtra("currentText");//AAAAA DECOMMENTER
        String[] repliques= currentText.split("\n");
        for (String laReplique:repliques) {
            if(!laReplique.equals(""))
            {
                TableRow row= new TableRow(this);
                TextView leText = new TextView(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                String textTemporaire = laReplique.replace("\n","\n\n");
                leText.setText(textTemporaire);
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


        //new FetchTask().execute("http://localhost:4000/GetId?id=5", currentTextID);


        tts=new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.FRENCH);
            }
        });

        startReciteButton.setOnClickListener(view -> {
            Intent ReciteTexteActivity = new Intent(TextManagerActivity.this, ReciterTexte.class);
            ReciteTexteActivity.putExtra(CURRENT_TEXT_ID, currentTextID);
            ReciteTexteActivity.putExtra(CURRENT_TEXT_KEY, currentText);

            for(int i=0; i< currentTable.getChildCount();i++)
            {
                TableRow tr = (TableRow) currentTable.getChildAt(i);
                TextView tv = (TextView) tr.getChildAt(0);
                if(tv.getBackground().getAlpha() == 99)
                {
                    orderText.add(0);
                }
                else
                {
                    orderText.add(1);
                }
            }
            ReciteTexteActivity.putExtra(ORDER_TEXT_KEY,orderText);
            setResult(RESULT_OK,ReciteTexteActivity);
            finish();
            startActivity(ReciteTexteActivity);
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
            Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
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
            RequestBody body = new FormBody.Builder()
                    .add("idText", idText)
                    .build();
            Request request = new Request.Builder()
                    .url(stringUrl)
                    .addHeader("Content-type","application/json")
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonTest = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonTest);
                System.out.println("---------------------------------"+jsonObject.toString());
                return jsonObject.getString("text");
            } catch (IOException | JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                currentText=null;
            } else {
               // currentTextView.setText(s);
            }
        }
    }
}
