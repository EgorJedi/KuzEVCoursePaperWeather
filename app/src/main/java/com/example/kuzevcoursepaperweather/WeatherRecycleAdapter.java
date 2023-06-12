package com.example.kuzevcoursepaperweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WeatherRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<WeatherModel> weatherModels;
    private Context context;

    public WeatherRecycleAdapter(Context context, List<WeatherModel> weatherModels) {
        this.context = context;
        this.weatherModels = weatherModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_item, parent,false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final WeatherViewHolder viewHolder = (WeatherViewHolder) holder;
        viewHolder.dayTime.setText(weatherModels.get(position).getDateTime());
        Picasso.get().load("https:" + weatherModels.get(position).getImageUrl()).into(viewHolder.conditionIcon);
        viewHolder.minTemp.setText(weatherModels.get(position).getMinTemp());
        viewHolder.maxTemp.setText(weatherModels.get(position).getMaxTemp());
    }

    @Override
    public int getItemCount() {
        return weatherModels.size();
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        final TextView dayTime, minTemp, maxTemp;
        final ImageView conditionIcon;
        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTime = itemView.findViewById(R.id.day_time);
            conditionIcon = itemView.findViewById(R.id.image);
            minTemp = itemView.findViewById(R.id.min_temp);
            maxTemp = itemView.findViewById(R.id.max_temp);
        }
    }
}
