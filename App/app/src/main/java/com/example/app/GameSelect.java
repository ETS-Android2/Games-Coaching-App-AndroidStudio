package com.example.app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GameSelect extends AppCompatActivity implements View.OnClickListener {

    ImageView overwatch, dota2, csgo, wow;

    String selectedGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_select);


        /*** -------------------------------------------------------------------- ***/
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
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.cyan));
        /*** -------------------------------------------------------------------- ***/


        overwatch = findViewById(R.id.game_select_overwatch);
        overwatch.setOnClickListener(this);

        dota2 = findViewById(R.id.game_select_dota2);
        dota2.setOnClickListener(this);

        csgo = findViewById(R.id.game_select_csgo);
        csgo.setOnClickListener(this);

        wow = findViewById(R.id.game_select_wow);
        wow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.game_select_overwatch:
                selectedGame = "overwatch";
                break;
            case R.id.game_select_dota2:
                selectedGame = "dota2";
                break;
            case R.id.game_select_csgo:
                selectedGame = "csgo";
                break;
            case R.id.game_select_wow:
                selectedGame = "wow";
                break;
            default:
                selectedGame = "dota2";
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("selectedGame", selectedGame);

        startActivity(intent);


    }
}