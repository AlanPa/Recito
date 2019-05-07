package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText idClient;
    private EditText  passwordClient;
    private EditText checkPasswordClient;
    private EditText  emailClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        idClient = findViewById(R.id.Id_Client_Input_Sign_Up);
        passwordClient = findViewById(R.id.Password_Input_Sign_Up);
        checkPasswordClient = findViewById(R.id.Check_Password_Input_Sign_Up);
        emailClient=findViewById(R.id.Email_Client_Input_Sign_Up);
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
            new FetchTask().execute("url", idClientInput,passwordInput,emailClientInput);
        }
    }


    private class FetchTask extends AsyncTask<String, Void, String> {
        String stringUrl;
        String idClient;
        String passwordClient;
        String emailClient;
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
            emailClient = strings[3];
            RequestBody body = new FormBody.Builder()
                    .add("idClient", idClient)
                    .add("passwordClient", passwordClient)
                    .add("emailClient", emailClient)
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
                Toast.makeText(getApplicationContext(),"Une erreur s'est produite",Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(CreateAccountActivity.this, TextManagerActivity.class);
                intent.putExtra("idClient", idClient);
                setResult(RESULT_OK, intent);
                startActivity(intent);
            }
        }
    }
}
