package com.example.kuzevcoursepaperweather;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuzevcoursepaperweather.databinding.FragmentFirstBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private SqliteDatabase mDatabase;
    private ArrayList<CityModel> allCities = new ArrayList<>();
    private CitiesRecyclerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        // Выводим список городов
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.citiesRecycle.setLayoutManager(linearLayoutManager);
        binding.citiesRecycle.setHasFixedSize(true);
        mDatabase = new SqliteDatabase(getActivity());
        allCities = mDatabase.listCities();

        if (allCities.size() > 0) {
            binding.citiesRecycle.setVisibility(View.VISIBLE);

            // определяем слушателя нажатия элемента в списке
            CitiesRecyclerAdapter.OnCityClickListener cityClickListener = new CitiesRecyclerAdapter.OnCityClickListener() {
                @Override
                public void onCityClick(CityModel city, int position) {
                    Bundle bundle = new Bundle();
                    bundle.putString("url", city.getUrl());
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
                }
            };

            mAdapter = new CitiesRecyclerAdapter(getActivity(), allCities, cityClickListener);
            binding.citiesRecycle.setAdapter(mAdapter);

        } else {
            binding.citiesRecycle.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "В списке еще нет городов. Добавьте город.", Toast.LENGTH_LONG).show();
        }

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCityDialog();
            }
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void addCityDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.add_city, null);

        final EditText nameField = (EditText) subView.findViewById(R.id.enter_city_name_RU);
        final EditText urlField = (EditText) subView.findViewById(R.id.enter_country_city_EN);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Добавить город");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("ДОБАВИТЬ ГОРОД", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = nameField.getText().toString();
                final String url = urlField.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getActivity(), "Что-то не так. Проверьте введенные значения.", Toast.LENGTH_LONG).show();
                }
                else {
                    CityModel newCity = new CityModel(0, name, url);
                    mDatabase.addCity(newCity);

                    // Обновляем Activity
                    getActivity().finish();
                    getActivity().startActivity(getActivity().getIntent());                }
            }
        });

        builder.setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Добавление города отменено.", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    public void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mAdapter!=null)
                    mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDatabase != null) { mDatabase.close(); }
        binding = null;
    }
}