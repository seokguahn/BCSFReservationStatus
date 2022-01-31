package com.example.firstjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    ProgressDialog customProgressDialog;

    TextView tvWeatherLeft;
    ImageView ivWeather;
    TextView tvWeatherRight;

    Button button;
    ArrayList<Item> items = new ArrayList<>();
    RecyclerAdapter recyclerAdapter;

    ArrayList<String> itemPlaces = new ArrayList<>();
    ArrayList<String> itemLinks = new ArrayList<>();

    String mapTitle;
    String mapUrl;

    CallbackListener CallbackListener = new CallbackListener() {
        @Override
        public void callBackMethod(ItemWeatherInfo info) {
            tvWeatherLeft.setText(info.strdate + "\n" + "부천시날씨");
            Glide.with(getApplicationContext()).load(info.imageSrc).into(ivWeather);
            tvWeatherRight.setText(info.ta + "℃(" + info.weatherdesc + ")" + "\n" + "미세먼지"+ info.air +"㎍/m3 ("+ info.cai +")");
        }
    };

    ItemClickCallbackListener clickCallbackListener = new ItemClickCallbackListener() {
        @Override
        public void callBackMethod(String title, String url, int type) {
            switch (type) {
                case 0:
                {
                    mapTitle = title;
                    mapUrl = url;
                    customProgressDialog.show();
                    JATPlaygroundAddress jsoupAsyncTask = new JATPlaygroundAddress();
                    jsoupAsyncTask.execute();
                }
                    break;
                case 1:
                {
                    Intent intent = new Intent(getApplicationContext(), ReservationStatusActivity.class);
                    intent.putExtra("intoTitle", title);
                    intent.putExtra("intoUrl", url);
                    intent.putExtra("intoSearchType", ReservationStatusActivity.TYPE_SEARCH);
                    startActivity(intent);
                }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        tvWeatherLeft = (TextView)findViewById(R.id.weatherleft);
        ivWeather = (ImageView)findViewById(R.id.weatherSrc);
        tvWeatherRight = (TextView)findViewById(R.id.weatherRight);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new RecyclerAdapter(getApplicationContext(), items, R.layout.activity_main);
        recyclerAdapter.setItemClickCallBackListener(clickCallbackListener);
        recyclerView.setAdapter(recyclerAdapter);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReservationStatusActivity.class);
                intent.putExtra("intoTitle", "팀 예약 현황");
                intent.putExtra("intoUrl", "");
                intent.putExtra("intoSearchType", ReservationStatusActivity.TYPE_SEARCH_ALL);
                intent.putExtra("intoPlace", itemPlaces);
                intent.putExtra("intoLink", itemLinks);
                startActivity(intent);
            }
        });

        // 날씨정보
        BucheonWeatherInfo bucheonWeatherInfo = new BucheonWeatherInfo(getApplicationContext());
        bucheonWeatherInfo.setCallBackListener(CallbackListener);

        // 운동장정보
        customProgressDialog.show();
        JATPlaygroundName jsoupAsyncTask = new JATPlaygroundName();
        jsoupAsyncTask.execute();
    }

    private class JATPlaygroundName extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SSLTrustAllCerts.sslTrustAllCerts();
                String htmlPageUrl = "https://reserv.bucheon.go.kr/site/main/lending/lendingList?tab=0&viewMode=list&inst_cate=01&search_area_div=&lending_inst_nm=football";
                Document doc = Jsoup.connect(htmlPageUrl).get();
                Elements tables = doc.select("table[class=table-col mb-col]").select("tbody").select("tr");
                for (Element item : tables) {
                    Elements tds = item.select("td");
                    String placeName = tds.select("a").text().trim();
                    String placeLink = tds.select("a").attr("href");
                    String bill = "";
                    String receptionStatus = tds.select("span").text().trim();

                    items.add(new Item(placeName, placeLink, bill, receptionStatus));
                    itemPlaces.add(placeName);
                    itemLinks.add(placeLink);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            customProgressDialog.dismiss();
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    private class JATPlaygroundAddress extends AsyncTask<Void, Void, Void> {
        String address;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SSLTrustAllCerts.sslTrustAllCerts();
                String htmlPageUrl = "https://reserv.bucheon.go.kr" + mapUrl;
                Document doc = Jsoup.connect(htmlPageUrl).get();
                address = doc.select("div[class=map-btn]").select("a").attr("onclick");
                address = address.replace("fncGoMap('", "");
                address = address.replace("'); return false;", "");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            customProgressDialog.dismiss();
            MapDialog mapDialog = new MapDialog();
            mapDialog.show(getSupportFragmentManager(), mapTitle + "#" + address);
        }
    }
}