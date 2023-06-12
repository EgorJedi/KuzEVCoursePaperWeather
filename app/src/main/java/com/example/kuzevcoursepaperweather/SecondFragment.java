package com.example.kuzevcoursepaperweather;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuzevcoursepaperweather.databinding.FragmentSecondBinding;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private String cityUrl;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);

        /* An instance of this class will be registered as a JavaScript interface */
        cityUrl = getArguments().getString("url");
        binding.progressCircular.getProgress();
        binding.progressCircular.setVisibility(View.VISIBLE);
        class MyJavaScriptInterface
        {
            @JavascriptInterface
            @SuppressWarnings("unused")
            public void processHTML(String html)
            {
                // process the html as needed by the app
                new GetWeather().getWeather(html)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ArrayList<WeatherModel>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(ArrayList<WeatherModel> weatherModels) {

                                // Отображаем текущую погоду
                                binding.city.setText(weatherModels.get(0).getDateTime());
                                Picasso.get().load("https:" + weatherModels.get(0)
                                        .getImageUrl()).resize(100, 100)
                                        .into(binding.currentIcon);
                                binding.currentTemp.setText(weatherModels.get(0).getMinTemp());

                                // Отображаем погоду на день
                                ArrayList<WeatherModel> todayModel = new ArrayList<>();
                                for (int i = 1; i < 5; i++) {
                                    todayModel.add(weatherModels.get(i));
                                }
                                WeatherRecycleAdapter todayRecycleAdapter = new WeatherRecycleAdapter(
                                        getActivity(), todayModel);
                                binding.todayRecycler.setAdapter(todayRecycleAdapter);

                                // Отображаем погоду на 3 дня
                                ArrayList<WeatherModel> forecastModel = new ArrayList<>();
                                for (int i = 5; i < 8; i++) {
                                    forecastModel.add(weatherModels.get(i));
                                }
                                WeatherRecycleAdapter forecastRecycleAdapter = new WeatherRecycleAdapter(
                                        getActivity(), forecastModel);
                                binding.forecastRecycler.setAdapter(forecastRecycleAdapter);
                                binding.progressCircular.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
            }
        }


        //final WebView browser = (WebView)findViewById(R.id.web);
        /* JavaScript must be enabled if you want it to work, obviously */
        binding.web.getSettings().setJavaScriptEnabled(true);

        /* Register a new JavaScript interface called HTMLOUT */
        binding.web.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

        /* WebViewClient must be set BEFORE calling loadUrl! */
        binding.web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                /* This call inject JavaScript into the page which just finished loading. */
                binding.web.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
        });

        /* load a web page */
        binding.web.loadUrl(cityUrl);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}