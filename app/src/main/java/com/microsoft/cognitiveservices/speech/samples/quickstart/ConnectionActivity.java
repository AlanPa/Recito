package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectionActivity extends AppCompatActivity {
    private EditText loginClient;
    private EditText  passwordClient;
    private Button connexionButton;
    private final String urlTORequest = "https://recitoback.azurewebsites.net/signIn";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        loginClient = findViewById(R.id.Id_User_Connection);
        passwordClient = findViewById(R.id.Password_User_Connection);
        connexionButton = findViewById(R.id.Connection_Button_Connection);
    }
    public void ConnexionButtonClicked(View view) {
        connexionButton.setEnabled(false);
        connexionButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        String loginClientInput = loginClient.getText().toString();
        String passwordClientInput = passwordClient.getText().toString();
        //temporaire
       // Intent intent = new Intent(ConnectionActivity.this, LibraryActivity.class);
       // startActivity(intent);
        new FetchTask().execute(urlTORequest, loginClientInput,passwordClientInput);
    }

    private class FetchTask extends AsyncTask<String, Void, String> {
        //String loginClientString;
        String idClientString;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON =MediaType.parse("application/json; charset=utf-8");
            JsonObject json = new JsonObject();
            json.addProperty("login", strings[1]);
            json.addProperty("passwordClient", strings[2]);

            Request request = new Request.Builder()
                    .url(strings[0])
                    .post(RequestBody.create(JSON, json.toString()))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonTest = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonTest);

                if(jsonObject.getBoolean("signIn"))
                {
                    idClientString= jsonObject.getJSONObject("client").getString("id");
                    return "true";
                }
                else
                {
                    return "false";
                }

            } catch (IOException | JSONException e) {
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            connexionButton.setEnabled(true);
            connexionButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            if (s.compareTo("false")==0) {
                Toast.makeText(getApplicationContext(),"identifiant et/ou mot de passe incorrect",Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(ConnectionActivity.this, LibraryActivity.class);
                intent.putExtra("idClient", idClientString);
                setResult(RESULT_OK, intent);
                finish();
                startActivity(intent);
            }
        }
    }
}
