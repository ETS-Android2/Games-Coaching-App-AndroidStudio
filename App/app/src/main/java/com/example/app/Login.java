package com.example.app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;


        // change action bar color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(getColor(R.color.purple));
        actionBar.setBackgroundDrawable(colorDrawable);



        // change status bar color
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this ,R.color.cyan));

    }

    public void submit(View v){
        EditText emailView = findViewById(R.id.loginEmail);
        EditText passwordView = findViewById(R.id.loginPassword);

        String email = emailView.getText().toString().trim(); // eliminate leading and trailing spaces with trim
        String password = passwordView.getText().toString().trim();

        if(email.length() == 0 || password.length() == 0){
            Toast.makeText(getApplicationContext(), "Something is wrong", Toast.LENGTH_LONG);
            return;
        }

        JSONObject loginForm = new JSONObject();
        try{
            loginForm.put("email", email);
            loginForm.put("password", password);
        }catch(JSONException e){
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), loginForm.toString());

        postRequest("http://192.168.0.217:5000/studentauth/login", body);
    }

    public void postRequest(String postUrl, RequestBody postBody){

        // Cookiejar
        CookieJar cookieJar = new PersistentCookieJar(
                new SetCookieCache(),
                new SharedPrefsCookiePersistor(getApplicationContext()));

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure
                call.cancel();
                Log.d("FAIL", e.getMessage());

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseTextLogin = findViewById(R.id.result);
                        responseTextLogin.setText("Failed to Connect to Server. Please Try Again.");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseTextLogin = findViewById(R.id.result);
                        try {
                            String loginResponseString = response.body().string().trim();
                            JSONObject Jobject = new JSONObject(loginResponseString);

                            boolean success = Jobject.getBoolean("success");

                            Log.d("LOGIN", "Response from the server : " + loginResponseString);
                            if (success) {
                                Log.d("LOGIN", "Successful Login");
                                responseTextLogin.setText("success");
                                Intent intent = new Intent(context, GameSelect.class);
                                startActivity(intent);
                            } else if (!success) {
                                responseTextLogin.setText("Login Failed. Invalid username or password.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            responseTextLogin.setText("Something went wrong. Please try again later.");
                        }
                    }
                });
            }
        });
    }

    public void register(View v){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}