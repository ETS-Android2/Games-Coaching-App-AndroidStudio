package com.example.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register extends AppCompatActivity {

    Button registerButton;
    Button backToLogin;

    TextView successText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);




        // change status bar color
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this ,R.color.cyan));




        registerButton = findViewById(R.id.register_registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(v);
            }
        });


        backToLogin = findViewById(R.id.register_backToLogin);
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);

                startActivity(intent);
            }
        });
        backToLogin.setVisibility(View.INVISIBLE);


        successText = findViewById(R.id.register_successText);
        successText.setVisibility(View.INVISIBLE);


    }

    public void register(View v){
        EditText emailView = findViewById(R.id.register_email);
        EditText passwordView = findViewById(R.id.register_password);

        String email = emailView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();

        if(email.length() == 0 || password.length() == 0){
            Toast.makeText(getApplicationContext(), "Something went wrong, check your input", Toast.LENGTH_SHORT).show();
        } else{
            JSONObject registrationForm = new JSONObject();

            try{
                registrationForm.put("email", email);
                registrationForm.put("password", password);
            } catch(JSONException e){
                e.printStackTrace();;
            }

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), registrationForm.toString());

            postRequest("http://192.168.0.217:5000/studentauth/register", body);
        }

    }

    public void postRequest(String postUrl, RequestBody body){
        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Log.d("Fail", e.getMessage());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.register_result);
                        responseText.setText("Failed to connect to server, please try again");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final TextView responseText = findViewById(R.id.register_result);

                try {
                    final String responseString = response.body().string().trim();
                    JSONObject Jobject = new JSONObject(responseString);

                    boolean success = Jobject.getBoolean("success");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (success == true) {
                                responseText.setText("Register completed successfully");
                                successText.setText("Register completed successfully");
                                successText.setVisibility(View.VISIBLE);
                                backToLogin.setVisibility(View.VISIBLE);
                            } else if(success == false){
                                responseText.setText("Email already has an account");
                                successText.setText("Email already has an account");
                                successText.setVisibility(View.VISIBLE);
                            } else{
                                responseText.setText("Something went wrong, please try again");
                                successText.setText("Something went wrong, please try again");
                                successText.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}