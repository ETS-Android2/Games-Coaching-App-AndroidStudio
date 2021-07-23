package com.example.app;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CoachContact extends AppCompatActivity {

    String name, email, phone, discord;
    int coachId;

    TextView nameTextView;
    TextView emailTextView;
    TextView phoneTextView;
    TextView discordTextView;

    ImageView coachImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_contact);



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



        nameTextView = findViewById(R.id.coach_contact_name);
        emailTextView = findViewById(R.id.coach_contact_email);
        phoneTextView = findViewById(R.id.coach_contact_phone);
        discordTextView = findViewById(R.id.coach_contact_discord);

        coachImage = findViewById(R.id.coach_contact_coachImage);

        // getIntent
        if(getIntent().hasExtra("coachId")){
            coachId = getIntent().getIntExtra("coachId", -1);
        } else{
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }

        if(coachId > -1){
            String url = "http://192.168.0.217:5000/api/sessions_student/coaches/" + coachId;

            getRequest(url);
        }



        String coachImageURL = "http://192.168.0.217:5000/api/sessions_student/coaches/image/" + coachId;
        Log.d("COACHIMAGEURL", coachImageURL);
        Picasso.get()
                .load(coachImageURL)
                .into(coachImage);
    }



    public void getRequest(String url) {

        // Http Request
        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Log.d("DEBUG", "2");

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

                        try {

                            Log.d("COACHCONTACT", "3");

                            String responseString = response.body().string().trim();

                            Log.d("COACHCONTACT", responseString);

                            JSONObject Jobject = new JSONObject(responseString);

                            JSONArray Jarray = Jobject.getJSONArray("coach");


                            // assign values
                            JSONObject jsonObject = Jarray.getJSONObject(0);

                            name = jsonObject.getString("name");
                            email = jsonObject.getString("email");
                            phone = jsonObject.getString("phone");
                            discord = jsonObject.getString("discord");


                            Log.d("COACHCONTACT", "Response from the server : " + responseString);

                            if (response.isSuccessful()) {
                                nameTextView.setText(name);
                                emailTextView.setText(email);
                                phoneTextView.setText(phone);
                                discordTextView.setText(discord);

                            } else if (!response.isSuccessful()) {
                                Log.d("COACHCONTACT", "NOT SUCCESSFULL");
                            }


                        } catch (Exception e) {
                            Log.d("COACHCONTACT", "caught exception");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
