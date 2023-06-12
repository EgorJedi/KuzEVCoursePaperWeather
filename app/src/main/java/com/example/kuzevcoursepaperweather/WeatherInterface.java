package com.example.kuzevcoursepaperweather;

import java.util.ArrayList;

import io.reactivex.Observable;

public interface WeatherInterface {
    Observable<ArrayList<WeatherModel>> getWeather(String url);
}
