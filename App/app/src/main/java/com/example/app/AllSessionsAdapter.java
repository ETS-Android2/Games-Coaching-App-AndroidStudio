package com.example.app;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class AllSessionsAdapter extends RecyclerView.Adapter<AllSessionsAdapter.MyViewHolder> {

    String data1[], data2[], data3[], data4[];

    int sessionId[];
    int coachId[];

    String game[];

    Context context;

    public AllSessionsAdapter(Context ct, String s1[], String s2[], String s3[], String s4[], int[] sessionId, int coachId[], String game[]){
        context = ct;
        data1 = s1;
        data2 = s2;
        data3 = s3;
        data4 = s4;

        this.sessionId = sessionId;
        this.coachId = coachId;
        this.game = game;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view); // pass view into inner class
    }

    // communicates with MyViewHolder
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(data1[position]);
        holder.date.setText(data2[position]);
        holder.startTime.setText(data3[position] + "-" + data4 [position]);
        //holder.endTime.setText(data4[position]);
        //holder.myImage.setImageResource(images[0]);

        switch(game[position]){
            case "overwatch":
                Picasso.get().load(R.drawable.gameimage_overwatch).fit().centerCrop().into(holder.gameImage);
                break;
            case "dota2":
                Picasso.get().load(R.drawable.gameimage_dota2).fit().centerCrop().into(holder.gameImage);
                break;
            case "csgo":
                Picasso.get().load(R.drawable.gameimage_csgo).fit().centerCrop().into(holder.gameImage);
                break;
            case "wow":
                Picasso.get().load(R.drawable.gameimage_wow).fit().centerCrop().into(holder.gameImage);
                break;
            default:
                Picasso.get().load(R.drawable.gameimage_dota2).fit().centerCrop().into(holder.gameImage);
                break;
        }

        String coachImageURL = "http://192.168.0.217:5000/api/sessions_student/coaches/image/" + coachId[position];

        Log.d("COACHIMAGEURL", coachImageURL);

        Picasso.get()
                .load(coachImageURL)
                .into(holder.coachImage);



        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SessionDetails.class);
                intent.putExtra("data1", data1[position]);
                intent.putExtra("data2", data2[position]);
                intent.putExtra("data3", data3[position]);
                intent.putExtra("data4", data4[position]);
                intent.putExtra("sessionId", sessionId[position]);
                intent.putExtra("book", true);
                intent.putExtra("coachId", coachId[position]);
                intent.putExtra("game", game[position]);

                intent.putExtra("book", true);

                context.startActivity(intent);
            }
        });
    }

    // number of items in recyclerView
    @Override
    public int getItemCount() {
        return data1.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, date, startTime, endTime;
        ImageView coachImage;

        ConstraintLayout constraintLayout;

        ImageView gameImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.my_row_name);
            date = itemView.findViewById(R.id.my_row_date);
            startTime = itemView.findViewById(R.id.my_row_startTime);
            //endTime = itemView.findViewById(R.id.my_row_endTime);

            coachImage = itemView.findViewById(R.id.my_row_coachImage);

            constraintLayout = itemView.findViewById(R.id.my_row);

            gameImage = itemView.findViewById(R.id.my_row_gameImage);
        }
    }
}
