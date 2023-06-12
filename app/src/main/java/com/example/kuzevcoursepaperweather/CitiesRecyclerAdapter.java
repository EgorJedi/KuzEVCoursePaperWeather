package com.example.kuzevcoursepaperweather;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CitiesRecyclerAdapter extends RecyclerView.Adapter<CitiesRecyclerAdapter.CitiesViewHolder> implements Filterable {

    interface OnCityClickListener {
        void onCityClick(CityModel city, int position);
    }

    private OnCityClickListener onClickListener;
    private Context context;
    private ArrayList<CityModel> listCities;
    private SqliteDatabase mDatabase;

    public CitiesRecyclerAdapter(Context context, ArrayList<CityModel> listCities, OnCityClickListener onClickListener) {
        this.context = context;
        this.listCities = listCities;
        mDatabase = new SqliteDatabase(context);
        this.onClickListener = onClickListener;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @NonNull
    @Override
    public CitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cities_list, parent, false);
        return new CitiesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitiesViewHolder holder, int position) {
        CityModel city = listCities.get(position);

        holder.name.setText(city.getName());
        holder.url.setText(city.getUrl());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // вызываем метод слушателя, передавая ему данные
                onClickListener.onCityClick(city, holder.getAdapterPosition());
            }
        });

        holder.editCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTaskDialog(city);
            }
        });

        holder.deleteCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // удаляем строку из базы данных
                mDatabase.deleteCity(city.getId());

                // обновляем Activity
                ((Activity) context).finish();
                context.startActivity(((Activity) context).getIntent());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCities.size();
    }

    private void editTaskDialog(final CityModel cities) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.add_city, null);

        final EditText nameField = (EditText) subView.findViewById(R.id.enter_city_name_RU);
        final EditText urlField = (EditText) subView.findViewById(R.id.enter_country_city_EN);

        if (cities != null) {
            nameField.setText(cities.getName());
            urlField.setText(String.valueOf(cities.getUrl()));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Редактирование города");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("РЕДАКТИРОВАТЬ ГОРОД", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = nameField.getText().toString();
                final String url = urlField.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(context, "Что-то не так. Проверьте введенные значения", Toast.LENGTH_LONG).show();
                }
                else {
                    mDatabase.updateCities(new CityModel(cities.getId(), name, url));

                    // обновляем Activity
                    ((Activity)context).finish();
                    context.startActivity(((Activity) context).getIntent());
                }
            }
        });

        builder.setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Редактирование города отменено.", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    public static class CitiesViewHolder extends RecyclerView.ViewHolder {

        public TextView name, url;
        public ImageView editCity;
        public ImageView deleteCity;

        public CitiesViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.city_name);
            url = (TextView) itemView.findViewById(R.id.city_url);
            editCity = (ImageView)itemView.findViewById(R.id.edit_city);
            deleteCity = (ImageView) itemView.findViewById(R.id.delete_city);
        }
    }
}
