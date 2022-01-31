package com.example.firstjava;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/*
{"weatherInfo":{"icon":"01","ta":"-1.8","air":"38","stn_id":"112","cai":"보통","strdate":"01월01일21시","weatherdesc":"맑음"},"success":true}
01월01일21시    -1.8℃(맑음)      부천시날씨       미세먼지38㎍/m3 (보통)
*/

public class BucheonWeatherInfo {
    Context context;
    CallbackListener CallbackListener;

    public void setCallBackListener(CallbackListener CallbackListener) {
        this.CallbackListener = CallbackListener;
    }

    BucheonWeatherInfo(Context context) {
        this.context = context;
        weatherInfo();
    }

    private void weatherInfo() {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://reserv.bucheon.go.kr/site/main/weatherInfoAjax";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.
                    ItemWeatherInfo info = new ItemWeatherInfo(response);
                    CallbackListener.callBackMethod(info);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("[BucheonWeatherInfo]", "That didn't work!");
                }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
