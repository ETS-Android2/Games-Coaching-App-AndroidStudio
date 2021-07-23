package com.example.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener, View.OnClickListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;


    String name[], date[], startTime[], endTime[];


    int sessionId[];
    int coachId[];

    String game[];

    ArrayList<Integer> daysToHighlight;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState)  {

        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initWidgets();

        Context context = context = getActivity();

        selectedDate = LocalDate.now();

        // http-request
        String url = "http://192.168.0.217:5000/api/sessions_student/booked";

        getRequest(url, context);




        // set onClick-Listener
        ImageButton previousMonthButton = getView().findViewById(R.id.fragment_calendar_previousMonthButton);
        previousMonthButton.setOnClickListener(this);

        ImageButton nextMonthButton = getView().findViewById(R.id.fragment_calendar_nextMonthButton);
        nextMonthButton.setOnClickListener(this);
    }


    private void initWidgets()
    {
        calendarRecyclerView = getView().findViewById(R.id.calendarRecyclerView);
        monthYearText = getView().findViewById(R.id.monthYearTV);
    }

    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        highlight(daysInMonth);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, daysToHighlight,this);

        if(getActivity() == null) {
            Log.d("CALENDAR", "getactivity is null");
        }

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date)
    {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 2; i <= 43; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
            {
                daysInMonthArray.add("");
            }
            else
            {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return  daysInMonthArray;
    }


    private void highlight(ArrayList<String> daysInMonth){
        daysToHighlight = new ArrayList<Integer>();

        int year = selectedDate.getYear();
        int month = selectedDate.getMonthValue();

        LocalDate prevdate = selectedDate.minusMonths(1).withDayOfMonth(1);
        prevdate = prevdate.withDayOfMonth(prevdate.lengthOfMonth());
        LocalDate nextdate = selectedDate.plusMonths(1).withDayOfMonth(1);

        Log.d("CALENDAR", "selectedDate: " + selectedDate.toString());
        Log.d("CALENDAR", "prevDate: " + prevdate.toString());
        Log.d("CALENDAR", "nextDate: " + nextdate.toString());

        /*
        LocalDate tempDate = LocalDate.of(2021, 7, 15);

        if(tempDate.isBefore(nextdate) && tempDate.isAfter(prevdate)){
            int a = tempDate.getDayOfMonth();
            daysToHighlight.add(Integer.valueOf(a));
        }*/

        LocalDate tempDate;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int a;

        Log.d("CALENDAR", daysInMonth.toString());


        boolean found = false;
        for(int j = 0; j < daysInMonth.size(); j++){
            if(daysInMonth.get(j).equals("")){
                daysToHighlight.add(Integer.valueOf(-1));
                Log.d("CALENDAR", "string is empty");
                continue;
            }

            found = false;

            for(int i = 0; i < date.length; i++) {
                tempDate = LocalDate.parse(date[i], dateTimeFormatter);
                Log.d("CALENDAR", "tempDate: " + tempDate.toString());

                if (tempDate.isBefore(nextdate) && tempDate.isAfter(prevdate) && Integer.parseInt(date[i].substring(0,2), 10) == Integer.parseInt(daysInMonth.get(j), 10)) {
                    daysToHighlight.add(Integer.valueOf(i));
                    found = true;
                    break;
                }
            }


            if(!found) {
                Log.d("CALENDAR", "date not in array");
                daysToHighlight.add(Integer.valueOf(-1));
            }
        }

        if(!daysToHighlight.isEmpty()) {
            Log.d("CALENDAR", daysToHighlight.toString());
        }

        Log.d("CALENDAR-SIZE", "daysToHighlight: " +daysToHighlight.size() + " daysInMonth: " +daysInMonth.size());
    }

    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public void previousMonthAction()
    {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction()
    {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }


    public void onItemClick(int position, String dayText)
    {
        /*
        if(!dayText.equals(""))
        {
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
        */

        Log.d("DAYTEXT", "daytext: " + dayText + " ...");

        int index = daysToHighlight.get(position);
        if(!dayText.equals("") && index > -1) {

            Log.d("PARSEINT", "PARSEINT(dayText): " + Integer.parseInt(dayText));

            Log.d("INDEX", "index: "+ index);

            Log.d("DATA", "name: " + name[index] + " date: " + date[index]);

            Intent intent = new Intent(getActivity(), SessionDetails.class);
            intent.putExtra("data1", name[index]);
            intent.putExtra("data2", date[index]);
            intent.putExtra("data3", startTime[index]);
            intent.putExtra("data4", endTime[index]);
            intent.putExtra("sessionId", sessionId[index]);
            intent.putExtra("book", false);
            intent.putExtra("coachId", coachId[index]);
            intent.putExtra("game", game[index]);

            getActivity().startActivity(intent);
        }
    }

    // OnClickListeners
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_calendar_previousMonthButton:
                previousMonthAction();
                break;

            case R.id.fragment_calendar_nextMonthButton:
                nextMonthAction();
                break;
        }
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

                            Log.d("CALENDAR", responseString);

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

                            if(response.isSuccessful()) {
                                Log.d("CALENDAR", "Successful request");
                                setMonthView();
                            }else {
                                Log.d("CALENDAR", "Unsuccessful request");
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

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("http").host("192.168.0.217").port(5000).addPathSegment("api").addPathSegment("sessions_student").addPathSegment("booked")
                .build();

        String url = httpUrl.toString();

        getRequest(url, context);


    }
}