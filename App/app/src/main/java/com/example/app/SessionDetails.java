package com.example.app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.squareup.picasso.Picasso;

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

public class SessionDetails extends AppCompatActivity {

    ImageView coachImage;
    TextView name, date, startTime, endTime;

    String data1, data2, data3, data4;

    int sessionId;
    int coachId;

    String game;

    ImageView gameIcon;

    // to disable
    boolean book;

    TextView booked;
    Button bookSession;
    Button cancelSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);


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



        coachImage = findViewById(R.id.session_details_coachImage);

        name = findViewById(R.id.session_details_name);
        date = findViewById(R.id.session_details_date);
        startTime = findViewById(R.id.session_details_startTime);
        //endTime = findViewById(R.id.session_details_endTime);

        gameIcon = findViewById(R.id.session_details_gameIcon);


        // to disable
        booked = findViewById(R.id.session_details_booked);
        bookSession = findViewById(R.id.session_details_bookSession);
        cancelSession = findViewById(R.id.session_details_cancelSession);

        getData();
        disableWidgets();
        setData();

        coachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SessionDetails.this, CoachContact.class);
                intent.putExtra("coachId", coachId);

                startActivity(intent);
            }
        });
    }

    private void getData(){
        // Fehlerbehandlung
        if(getIntent().hasExtra("data1")
        && getIntent().hasExtra("data2")
        && getIntent().hasExtra("data3")
        && getIntent().hasExtra("data4")
        && getIntent().hasExtra("sessionId")
        && getIntent().hasExtra("book")
        && getIntent().hasExtra("coachId")
        && getIntent().hasExtra("game")){

            data1 = getIntent().getStringExtra("data1");
            data2 = getIntent().getStringExtra("data2");
            data3 = getIntent().getStringExtra("data3");
            data4 = getIntent().getStringExtra("data4");
            sessionId = getIntent().getIntExtra("sessionId", -1);
            coachId = getIntent().getIntExtra("coachId", -1);
            game = getIntent().getStringExtra("game");

            book = getIntent().getBooleanExtra("book", false);

        } else{
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }

    private void disableWidgets(){
        if(book){
            booked.setVisibility(View.INVISIBLE);
            cancelSession.setVisibility(View.INVISIBLE);

            bookSession.setVisibility(View.VISIBLE);
        } else{
            bookSession.setVisibility(View.INVISIBLE);

            booked.setVisibility(View.VISIBLE);
            cancelSession.setVisibility(View.VISIBLE);
        }
    }

    private void setData(){
        name.setText(data1);
        date.setText(data2);
        startTime.setText(data3 + " - " + data4);
        // endTime.setText(data4);

        switch(game){
            case "overwatch":
                gameIcon.setImageResource(R.drawable.icon_overwatch_white);
                break;
            case "dota2":
                gameIcon.setImageResource(R.drawable.icon_dota2);
                break;
            case "csgo":
                gameIcon.setImageResource(R.drawable.icon_csgo);
                break;
            case "wow":
                gameIcon.setImageResource(R.drawable.icon_wow);
                break;
            default:
                gameIcon.setImageResource(R.drawable.icon_dota2);
                break;
        }

        String coachImageURL = "http://192.168.0.217:5000/api/sessions_student/coaches/image/" + coachId;
        Log.d("COACHIMAGEURL", coachImageURL);
        Picasso.get()
                .load(coachImageURL)
                .into(coachImage);
    }

    public void bookSession(View v){
        Log.d("SESSIONDETAILS","clicked bookSession");

        Log.d("SESSIONDETAILS", "id: " + sessionId);

        if(sessionId == -1){
            Log.d("SESSIONDETAILS", "sessionId failed");
            Toast.makeText(this, "sessionId failed", Toast.LENGTH_SHORT).show();
            return;
        }


        JSONObject postObject = new JSONObject();
        try{
            postObject.put("sessionId", sessionId);
        }catch(JSONException e){
            e.printStackTrace();
            Log.d("SESSIONDETAILS", "failed putting sessionId into JSONObject");
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postObject.toString());

        bookRequest("http://192.168.0.217:5000/api/sessions_student/book", body);
    }


    public void cancelSession(View v){
        if(sessionId == -1){
            Log.d("SESSIONDETAILS", "sessionId failed");
            Toast.makeText(this, "sessionId failed", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject postObject = new JSONObject();
        try{
            postObject.put("sessionId", sessionId);
        }catch(JSONException e){
        e.printStackTrace();
        Log.d("SESSIONDETAILS", "failed putting sessionId into JSONObject");
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postObject.toString());

        cancelRequest("http://192.168.0.217:5000/api/sessions_student/cancel", body);
    }



    private void bookRequest(String url, RequestBody body) {
        CookieJar cookieJar = new PersistentCookieJar(
                new SetCookieCache(),
                new SharedPrefsCookiePersistor(getApplicationContext()));

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
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
                        /*TextView responseTextLogin = findViewById(R.id.session_details_success);
                        responseTextLogin.setText("Failed to Connect to Server. Please Try Again.");*/
                        TextView responseTextLogin = findViewById(R.id.session_details_success);
                        responseTextLogin.setText("failed to connect");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseTextLogin = findViewById(R.id.session_details_success);
                        try {
                            String loginResponseString = response.body().string().trim();
                            JSONObject Jobject = new JSONObject(loginResponseString);

                            boolean success = Jobject.getBoolean("success");

                            Log.d("SESSIONDETAILS", "Response from the server: " + loginResponseString);
                            if (success) {
                                Log.d("SESSIONDETAILS", "Successfully booked");
                                bookSession.setVisibility(View.INVISIBLE);
                                booked.setVisibility(View.VISIBLE);
                                responseTextLogin.setText("success");

                            } else if (!success) {
                                Log.d("SESSIONDETAILS", "Failed booking session");
                                responseTextLogin.setText("failed booking session");

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


    private void cancelRequest(String url, RequestBody body){
        CookieJar cookieJar = new PersistentCookieJar(
                new SetCookieCache(),
                new SharedPrefsCookiePersistor(getApplicationContext()));

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure
                call.cancel();
                Log.d("FAIL", e.getMessage());

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*TextView responseTextLogin = findViewById(R.id.session_details_success);
                        responseTextLogin.setText("Failed to Connect to Server. Please Try Again.");*/
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView successTextView = findViewById(R.id.session_details_success);
                        try {
                            String responseString = response.body().string().trim();
                            JSONObject Jobject = new JSONObject(responseString);

                            boolean success = Jobject.getBoolean("success");

                            Log.d("SESSIONDETAILS", "Response from the server: " + responseString);
                            if (success) {
                                Log.d("SESSIONDETAILS", "Successfully canceled booking");
                                cancelSession.setVisibility(View.INVISIBLE);
                                booked.setVisibility(View.INVISIBLE);
                                successTextView.setText("canceled");

                            } else if (!success) {
                                Log.d("SESSIONDETAILS", "Failed cancel booked session");
                                successTextView.setText("failed booking session");

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            successTextView.setText("Something went wrong. Please try again later.");
                        }
                    }
                });
            }
        });


    }

}