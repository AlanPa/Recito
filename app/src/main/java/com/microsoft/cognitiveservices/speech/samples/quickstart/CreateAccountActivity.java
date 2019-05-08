package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class CreateAccountActivity extends AppCompatActivity {
    private EditText idClient;
    private EditText  passwordClient;
    private EditText checkPasswordClient;
    private EditText  emailClient;
    private final String urlTORequest = "https://recitoback.azurewebsites.net/createAccount";
    private Context curContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        idClient = findViewById(R.id.Id_Client_Input_Sign_Up);
        passwordClient = findViewById(R.id.Password_Input_Sign_Up);
        checkPasswordClient = findViewById(R.id.Check_Password_Input_Sign_Up);
        emailClient=findViewById(R.id.Email_Client_Input_Sign_Up);
        curContext=getApplicationContext();
    }

    public void onClickSignUp(View view) {
        String idClientInput = idClient.getText().toString();
        String passwordInput = passwordClient.getText().toString();
        String checkPasswordInput = checkPasswordClient.getText().toString();
        String emailClientInput = emailClient.getText().toString();
        if(passwordInput.compareTo(checkPasswordInput)!=0)
        {
            Toast.makeText(getApplicationContext(),"Les deux champs de mot de passe ne sont pas identiques",Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            new FetchTask().execute(urlTORequest, idClientInput,passwordInput,emailClientInput);
        }
    }


    private class FetchTask extends AsyncTask<String, Void, String> {
        String errorMessage="Une erreur s'est produite";
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
            json.addProperty("passwordClient", strings[2]);
            json.addProperty("emailClient", strings[3]);

            Request request = new Request.Builder()
                    .url(strings[0])
                    .post(RequestBody.create(JSON, json.toString()))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                //Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonTest = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonTest);
                if(jsonObject.has("create"))
                {
                    if(jsonObject.getBoolean("create"))
                    {
                        return "true";
                    }
                    else
                    {
                        return "false";
                    }
                }
                else
                {
                    if(jsonObject.has("Status") && jsonObject.getString("Status").compareTo("Error")==0)
                    {
                        errorMessage=jsonObject.getString("Message");
                    }
                    return "false";
                }
            } catch (IOException | JSONException e) {
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.compareTo("true")==0)
            {
                Intent intent = new Intent(CreateAccountActivity.this, ConnectionActivity.class);
                finish();
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
