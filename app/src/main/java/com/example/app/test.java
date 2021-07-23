package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        String url = "http://192.168.0.217:5000/studentauth/login";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseTextLogin = findViewById(R.id.test_text);
                        try {
                            String responseString = response.body().string().trim();
                            JSONObject Jobject = new JSONObject(responseString);

                            boolean loggedIn = Jobject.getBoolean("loggedIn");

                            Log.d("Test", "Response from the server : " + responseString);

                            if (loggedIn) {

                                Log.d("LOGIN", "Successful Login");
                                responseTextLogin.setText("success");
                            } else if (!loggedIn) {
                                responseTextLogin.setText("Login Failed. Invalid username or password.");
                            }


                        } catch (Exception e) {
                            Log.d("Test", "caught exception");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }
}