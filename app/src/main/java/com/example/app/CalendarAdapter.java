package com.example.app;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;
    private final ArrayList<Integer> daysToHighlight;

    View view;

    public CalendarAdapter(ArrayList<String> daysOfMonth, ArrayList<Integer> daysToHighlight,OnItemListener onItemListener)
    {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.daysToHighlight = daysToHighlight;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.15);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        holder.dayOfMonth.setText(daysOfMonth.get(position));

        if(!daysOfMonth.isEmpty() && !daysToHighlight.isEmpty()) {
            Log.d("CALENDARADAPTER", "daysToHighlight: " + daysToHighlight.get(0).toString() + " position: " + daysOfMonth.get(position) + " TRUE/FALSE: " + daysToHighlight.contains(daysOfMonth.get(position)));
        }

        if(!daysOfMonth.get(position).isEmpty() && !daysToHighlight.isEmpty()) {
            if (daysToHighlight.get(position) > -1) {
                Log.d("CALENDARADAPTER", "inside of if");
                //holder.dayOfMonth.setBackgroundColor(Color.RED);

                view.setBackground(view.getContext().getDrawable(R.drawable.booked_shape));
                holder.dayOfMonth.setBackgroundColor(view.getContext().getColor(R.color.cyan));
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return daysOfMonth.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, String dayText);
    }
}