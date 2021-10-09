package com.example.firstjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button button;
    ArrayList<Item> items = new ArrayList<>();
    RecyclerAdapter recyclerAdapter;

    ArrayList<String> itemPlaces = new ArrayList<>();
    ArrayList<String> itemLinks = new ArrayList<>();

    ItemClickCallbackListener clickCallbackListener = new ItemClickCallbackListener() {
        @Override
        public void callBack(String title, String url) {
            Intent intent = new Intent(getApplicationContext(), ReservationStatusActivity.class);
            intent.putExtra("intoTitle", title);
            intent.putExtra("intoUrl", url);
            intent.putExtra("intoSearchType", ReservationStatusActivity.TYPE_SEARCH);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new RecyclerAdapter(getApplicationContext(), items, R.layout.activity_main);
        recyclerAdapter.setItemClickCallBackListener(clickCallbackListener);
        recyclerView.setAdapter(recyclerAdapter);

        // 목록 검색
        JATPlaygroundName jsoupAsyncTask = new JATPlaygroundName();
        jsoupAsyncTask.execute();

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
    }

    private class JATPlaygroundName extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String htmlPageUrl = "https://reserv.bucheon.go.kr/site/main/lending/lendingList?tab=0&viewMode=list&inst_cate=01&search_area_div=&lending_inst_nm=football";
                Document doc = Jsoup.connect(htmlPageUrl).get();
                Elements tables = doc.select("table[class=table-col pc-col]").select("tbody").select("tr");
                for (Element item : tables) {
                    Elements tds = item.select("td");
                    String placeName = tds.select("a").text().trim();
                    String placeLink = tds.select("a").attr("href");
                    String bill = tds.get(4).text().trim();
                    String receptionStatus = tds.select("span").text().trim();

                    items.add(new Item(placeName, placeLink, bill, receptionStatus));
                    itemPlaces.add(placeName);
                    itemLinks.add(placeLink);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            recyclerAdapter.notifyDataSetChanged();
        }
    }
}