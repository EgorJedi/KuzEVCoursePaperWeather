package com.example.kuzevcoursepaperweather;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.kuzevcoursepaperweather.databinding.FragmentSecondBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetWeather implements WeatherInterface {

    @Override
    public Observable<ArrayList<WeatherModel>> getWeather(String url) {
        return Observable.create(observableEmitter -> {
            ArrayList<WeatherModel> weatherModels = new ArrayList<>();
            Document document;
            try {
                // Парсим сайт с помощью JSOUP
                document = Jsoup.parse(url);

                // Получаем название города, текущую погоду и температуру
                Elements dayTime = document.getElementsByClass("header").get(0)
                        .getElementsByAttribute("href");
                Elements temp = document.getElementsByClass("temp_c");
                Elements icon = document.getElementsByClass("symb").get(0)
                        .select("img[src$=.png]");
                WeatherModel weatherModel = new WeatherModel();
                weatherModel.setDateTime(dayTime.get(0).text());
                weatherModel.setMinTemp(temp.get(0).text());
                weatherModel.setMaxTemp("");
                weatherModel.setImageUrl(icon.get(0).attr("src"));
                weatherModels.add(weatherModel);

                // Получаем прогноз погоды на день
                dayTime = document.getElementsByClass("dayparts").get(0)
                        .select("h5");
                icon = document.getElementsByClass("dayparts").get(0)
                        .select("img[src$=.png]");
                for (int i = 0; i < 4; i++) {
                    weatherModel = new WeatherModel();
                    weatherModel.setDateTime(dayTime.get(i).text());
                    weatherModel.setImageUrl(icon.get(i).attr("src"));
                    weatherModel.setMinTemp(temp.get(i + 10).text());
                    weatherModel.setMaxTemp("");
                    weatherModels.add(weatherModel);
                }

                // Получаем прогноз погоды на 3 дня
                dayTime = document.getElementsByClass("daily").get(0)
                        .select("h5");
                icon = document.getElementsByClass("fluid");
                for (int i = 0; i < 3; i++) {
                    weatherModel = new WeatherModel();
                    weatherModel.setDateTime(
                            findPattern(dayTime.get(i).text(), "[а-я]{2}", 0) + " " +
                                    findPattern(dayTime.get(i).text(), "\\d{1,2}\\.\\d{1,2}", 0)
                    );
                    weatherModel.setImageUrl(icon.get(i).attr("src"));
                    weatherModel.setMaxTemp(temp.get(i * 2 + 3).text());
                    weatherModel.setMinTemp(temp.get(i * 2 + 4).text());
                    weatherModels.add(weatherModel);
                }

                observableEmitter.onNext(weatherModels);
            } catch (Exception e) {
                observableEmitter.onError(e);
            } finally {
                observableEmitter.onComplete();
            }
        });
    }

    public String findPattern(String sourceString, String regExp, int captureGroup) {
        String result = null;
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(sourceString);
        while (matcher.find())
            result = matcher.group(captureGroup);
        return result;
    }
}
