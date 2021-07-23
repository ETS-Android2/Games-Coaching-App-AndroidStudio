package com.example.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AllSessionsFragment extends Fragment {

    RecyclerView recyclerView;

    String name[], date[], startTime[], endTime[];

    int sessionId[];
    int coachId[];
    String game[];

    String selectedGame;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_all_sessions, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Context context = getActivity();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "com.example.app", Context.MODE_PRIVATE);
        selectedGame = sharedPreferences.getString("selectedGame", "dota2");

        Log.d("RECYCLERVIEWFRAGMENT", selectedGame);

/*
        recyclerView = findViewById(R.id.recyclerView);

        s1 = getResources().getStringArray((R.array.programming_languages));
        s2 = getResources().getStringArray(R.array.description);

        RecyclerviewAdapter adapter = new RecyclerviewAdapter(this, s1, s2, images);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
*/

        String url = "http://192.168.0.217:5000/api/sessions_student/all/" + selectedGame;

/*
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("http").host("192.168.0.217").port(5000).addPathSegment("api").addPathSegment("sessions_student").addPathSegment("all").addPathSegment(selectedGame)
                .build();

        Log.d("HTTPURL", httpUrl.toString());

 */

        Log.d("RECYCLERVIEWFRAGMENT", url);



        getRequest(url, context);

    }


    public void getRequest(String url, Context context) {

        // Http Request
        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            Log.d("DEBUG", "3");

                            String responseString = response.body().string().trim();

                            Log.d("DEBUG", responseString);

                            JSONObject Jobject = new JSONObject(responseString);

                            JSONArray Jarray = Jobject.getJSONArray("sessions");

                            name = new String[Jarray.length()];
                            date = new String[Jarray.length()];
                            startTime = new String[Jarray.length()];
                            endTime = new String[Jarray.length()];

                            sessionId = new int[Jarray.length()];
                            coachId = new int[Jarray.length()];

                            game = new String[Jarray.length()];

                            // assign values
                            for (int i = 0; i < Jarray.length(); i++) {

                                JSONObject jsonObject = Jarray.getJSONObject(i);

                                name[i] = jsonObject.getString("name");
                                date[i] = jsonObject.getString("date");
                                startTime[i] = jsonObject.getString("starttime");
                                endTime[i] = jsonObject.getString("endtime");

                                sessionId[i] = jsonObject.getInt("id");
                                coachId[i] = jsonObject.getInt("coachid");

                                game[i] = jsonObject.getString("game");
                            }

                            Log.d("Test", "Response from the server : " + responseString);

                            if (response.isSuccessful()) {

                                Log.d("LOGIN", "Successful Login");

                                // create recyclerview
                                recyclerView = getView().findViewById(R.id.recyclerView);

                                AllSessionsAdapter adapter = new AllSessionsAdapter(context, name, date, startTime, endTime, sessionId, coachId, game);

                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));

                            } else if (!response.isSuccessful()) {
                                Log.d("LOGIN", "NOT SUCCESSFULL");
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


    @Override
    public void onResume() {
        super.onResume();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here

        Context context = getActivity();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "com.example.app", Context.MODE_PRIVATE);
        selectedGame = sharedPreferences.getString("selectedGame", "dota2");

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("http").host("192.168.0.217").port(5000).addPathSegment("api").addPathSegment("sessions_student").addPathSegment("all").addPathSegment(selectedGame)
                .build();

        String url = httpUrl.toString();

        getRequest(url, context);


    }
}