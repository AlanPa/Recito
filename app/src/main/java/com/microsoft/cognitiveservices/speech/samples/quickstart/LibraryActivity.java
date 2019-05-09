package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LibraryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView listView;
    private String idClient;
    private TextView lastItemScoreView;
    private int lastItemPosition;
    private final String urlTORequest = "https://recitoback.azurewebsites.net/getLibrary";

    private String libTitles[];
    private String libAuthors[];
    private String libScores[];
    private String libIdText[];
    private String libImages[];
    private Context curContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bibliothèque");
        listView = findViewById(R.id.Main_Library);
        idClient = getIntent().getStringExtra("idClient");

        //String titles[]={"a", "b", "c"};
        //String authors[]={"A", "B", "C"};
        //String scores[]={"77", "120", "550"};
        //int images[]={"a1", "b2", "c3"};

        new FetchTask().execute(urlTORequest,idClient);

        //MyAdapter adapter = new MyAdapter(curContext, titles, authors, scores/*, images*/);
        //listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastItemScoreView = view.findViewById(R.id.Score_Text_Library_Row);
                lastItemPosition = position;
                String curTextId = libIdText[position];
                Intent libraryIntent = new Intent(LibraryActivity.this, TextManagerActivity.class);
                libraryIntent.putExtra("idClient", idClient);
                libraryIntent.putExtra("idText", curTextId);
                setResult(RESULT_OK, libraryIntent);
                startActivityForResult(libraryIntent, 122);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.Deconexion_Item_Menu:
                Intent intent = new Intent(LibraryActivity.this, ConnectionActivity.class);
                finish();
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "Une erreur s'est prodite lors de la déconnexion",Toast.LENGTH_SHORT);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAddTextButton(View view) {
        Intent loadPDFIntent = new Intent(LibraryActivity.this, LoadPdfActivity.class);
        loadPDFIntent.putExtra("idClient",idClient);
        setResult(RESULT_OK, loadPDFIntent);
        startActivityForResult(loadPDFIntent, 123);
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String rTitle[];
        String rAuthor[];
        String rScore[];
        String rImages[];

        MyAdapter(Context c, String title[], String author[], String score[]) {
            super(c, R.layout.library_row, R.id.Title_Text_Library_Row, title);
                this.context = c;
                this.rTitle = title;
                this.rAuthor = author;
                this.rScore = score;
            }

            @NonNull
            @Override
            public View getView ( int position, @Nullable View convertView, @NonNull ViewGroup parent){
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View row = layoutInflater.inflate(R.layout.library_row, parent, false);
                TextView curTitle = row.findViewById(R.id.Title_Text_Library_Row);
                TextView curAuthor = row.findViewById(R.id.Author_Text_Library_Row);
                TextView curScore = row.findViewById(R.id.Score_Text_Library_Row);
                //pourajouter des images si on a le temps
                ImageView curImage = row.findViewById(R.id.Image_Library_Row);

                curTitle.setText(rTitle[position]);
                curAuthor.setText(rAuthor[position]);
                curScore.setText(rScore[position]);

                return row;

            }
        }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Vous ne pouvez pas revenir en arrière avant d'avoir terminé l'action en cours",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==122 && resultCode==RESULT_OK)
        {
            int textScore = data.getIntExtra("textScore", -1);
            String idText = data.getStringExtra("idText");
            if(textScore!=-1 && libIdText[lastItemPosition].compareTo(idText)==0)
            {
                lastItemScoreView.setText(Integer.toString(textScore));
                libScores[lastItemPosition]=Integer.toString(textScore);
            }
            else
            {
                Toast.makeText(this, "Une erreur s'est produite", Toast.LENGTH_SHORT
                );
            }

        }
        else if(requestCode==123 && resultCode==RESULT_OK)
        {
            String tmpTitle[] = new String[libTitles.length+1];
            String tmpAuthor[] = new String[libAuthors.length+1];
            String tmpScore[] = new String[libScores.length+1];
            String tmpImages[] = new String[libImages.length+1];
            String tmpIdText[] = new String[libIdText.length+1];
            String author = data.getStringExtra("author");
            String title = data.getStringExtra("title");
            String id = data.getStringExtra("id");

            for(int i=0; i<libIdText.length; i++)
            {
                tmpTitle[i]=libTitles[i];
                tmpAuthor[i]=libAuthors[i];
                tmpScore[i]=libScores[i];
                tmpImages[i]=libImages[i];
                tmpIdText[i]=libIdText[i];
            }
            tmpTitle[tmpTitle.length-1]=title;
            tmpAuthor[tmpAuthor.length-1]=author;
            tmpScore[tmpScore.length-1]="0%";
            tmpImages[tmpImages.length-1]="0";
            tmpIdText[tmpIdText.length-1]=id;

            libTitles = tmpTitle;
            libAuthors = tmpAuthor;
            libScores = tmpScore;
            libIdText = tmpIdText;
            libImages = tmpImages;


            MyAdapter adapter = new MyAdapter(curContext, libTitles, libAuthors, libScores/*, images*/);
            listView.setAdapter(adapter);
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

            MediaType JSON =MediaType.parse("application/json; charset=utf-8");
            JsonObject json = new JsonObject();
            json.addProperty("idClient", strings[1]);

            Request request = new Request.Builder()
                    .url(strings[0])
                    .post(RequestBody.create(JSON, json.toString()))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                //Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonTest = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonTest);
                System.out.println("---------------------------------"+jsonObject.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("library");
                int librarySize= jsonArray.length();
                libTitles = new String[librarySize];
                libAuthors = new String[librarySize];
                libScores = new String[librarySize];
                libIdText = new String[librarySize];
                libImages = new String[librarySize];
                for (int i = 0; i < librarySize; i++) {
                    JSONObject curJsonObject = jsonArray.getJSONObject(i);
                    libTitles[i]=curJsonObject.getString("nom");
                    libAuthors[i]=curJsonObject.getString("auteur");
                    libScores[i]=curJsonObject.getString("score")+"%";
                    libIdText[i]=curJsonObject.getString("id");
                }
                return "Ok";
            } catch (IOException | JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
               //faire un Toast
            } else {
                MyAdapter adapter = new MyAdapter(curContext, libTitles, libAuthors, libScores/*, images*/);
                listView.setAdapter(adapter);
            }
        }
    }
    }
