package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectionActivity extends AppCompatActivity {
    private EditText idClient;
    private EditText  passwordClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        idClient = findViewById(R.id.Id_User_Connection);
        passwordClient = findViewById(R.id.Password_User_Connection);
    }
    public void ConnexionButtonClicked(View view) {
        String idClientInput = idClient.getText().toString();
        String passwordClientInput = passwordClient.getText().toString();
        new FetchTask().execute("url", idClientInput,passwordClientInput);
    }

    private class FetchTask extends AsyncTask<String, Void, String> {
        String stringUrl;
        String idClient;
        String passwordClient;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            stringUrl = strings[0];
            idClient = strings[1];
            passwordClient = strings[2];
            RequestBody body = new FormBody.Builder()
                    .add("idClient", idClient)
                    .add("passwordClient", passwordClient)
                    .build();
            Request request = new Request.Builder()
                    .url(stringUrl)
                    .addHeader("Content-type","application/json")
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                //Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
            if (s != "success") {
                Toast.makeText(getApplicationContext(),"identifiant et/ou mot de passe est incorrecte",Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(ConnectionActivity.this, TextManagerActivity.class);
                intent.putExtra("idClient", idClient);
                setResult(RESULT_OK, intent);
                startActivity(intent);
            }
        }
    }
}
