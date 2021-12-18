package com.example.firstjava;

import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ReservationStatusActivity extends AppCompatActivity {

    public static final int TYPE_SEARCH = 0;
    public static final int TYPE_SEARCH_ALL = 1;

    private String reservationTitle;
    private String reservationPlace;
    private String reservationUrl;
    private int searchType;
    private ArrayList<String> itemPlaces = new ArrayList<>();
    private ArrayList<String> itemLinks = new ArrayList<>();

    private ProgressDialog customProgressDialog;

    private EditText editText;
    private TextView textViewDate;
    private Button search;

    private List<ReservationItem> items = new ArrayList<>();
    private ReservationRecyclerAdapter recyclerAdapter;

    private String filterName = "";
    private String search_Year = "";
    private String search_Month = "";
    private String replace_Month = "";

    private HashMap<String, ArrayList<String>> map;
    private int teamSearchCount;

    DatePickerDialog.OnDateSetListener datePickerDialogListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            setYearMonth(year, monthOfYear);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservationstatus);
        reservationTitle = getIntent().getStringExtra("intoTitle");
        reservationUrl = getIntent().getStringExtra("intoUrl");
        searchType = getIntent().getIntExtra("intoSearchType", TYPE_SEARCH);
        if(searchType == TYPE_SEARCH_ALL) {
            itemPlaces = getIntent().getStringArrayListExtra("intoPlace");
            itemLinks = getIntent().getStringArrayListExtra("intoLink");

            reservationPlace = itemPlaces.get(teamSearchCount);
            reservationUrl = itemLinks.get(teamSearchCount);

            map = new HashMap<String, ArrayList<String>>();
            teamSearchCount = 0;
        }
        getSupportActionBar().setTitle(reservationTitle);

        //
        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        //
        editText = (EditText) findViewById(R.id.editText);

        //
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YearMonthPickerDialog pd = new YearMonthPickerDialog();
                pd.setListener(datePickerDialogListener);
                pd.setDate(Integer.parseInt(search_Year), Integer.parseInt(search_Month));
                pd.show(getSupportFragmentManager(), "YearMonthPicker");
            }
        });

        //
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reservationRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        recyclerAdapter = new ReservationRecyclerAdapter(items);
        recyclerView.setAdapter(recyclerAdapter);

        search = (Button) findViewById(R.id.buttonSearch);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterName = editText.getText().toString();

                if(searchType == TYPE_SEARCH_ALL) {
                    if(filterName.isEmpty()) {
                        Toast.makeText(ReservationStatusActivity.this, "팀명을 입력하세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    teamSearchCount = 0;
                    map.clear();

                    reservationPlace = itemPlaces.get(teamSearchCount);
                    reservationUrl = itemLinks.get(teamSearchCount);
                }

                items.clear();
                setEnableControl(false);

                callJsoupAsyncTask();
            }
        });

        Calendar cal = Calendar.getInstance();
        setYearMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);

        callJsoupAsyncTask();
    }

    public void setEnableControl(boolean isEnable) {
        editText.setEnabled(isEnable);
        search.setEnabled(isEnable);
    }

    public void setYearMonth(int year, int month) {
        search_Year = Integer.toString(year);
        search_Month = String.format("%02d", month);
        replace_Month = Integer.toString(month);

        String result = search_Year + "년 " +  search_Month + "월";
        textViewDate.setText(result);
    }

    public void callJsoupAsyncTask() {
        if(searchType == TYPE_SEARCH) {
            customProgressDialog.show();
            JATReservationState jsoupAsyncTask = new JATReservationState();
            jsoupAsyncTask.execute();
        }
        else if(searchType == TYPE_SEARCH_ALL && filterName.equals("") == false) {
            customProgressDialog.show();
            JATAllReservationState jsoupAsyncTask = new JATAllReservationState();
            jsoupAsyncTask.execute();
        }
    }

    private class JATReservationState extends AsyncTask<Void, Void, Void> {

        List<String> reservations = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String htmlPageUrl = "https://reserv.bucheon.go.kr" + reservationUrl + "&sch_year=" + search_Year + "&sch_month=" + search_Month;
                Document doc = Jsoup.connect(htmlPageUrl).get();

                Elements ulList = doc.select("ul[class=reservation]");
                for (Element item : ulList) {
                    String date = item.attr("id");
                    if (date.startsWith(replace_Month)) {
                        date = date.substring(replace_Month.length(), date.length());
                    }
                    date = replace_Month + "." + date;

                    String time = "";
                    String team = "";
                    String result = "";
                    Elements liList = item.select("li");
                    for(Element liItem : liList) {
                        String liClass = liItem.attr("class");
                        String liString = liItem.text().trim();

                        if(liClass.equals("tm")) {
                            time = liString;
                        }

                        if(liClass.equals("red")) {
                            team = liString;

                            if(filterName.equals("") || team.contains(filterName)) {
                                result = time + "#" + team;
                                reservations.add(result);
                            }
                        }
                    }

                    if(result.length() > 0) {
                        items.add(new ReservationItem(ReservationRecyclerAdapter.TYPE_HEADER, date));
                        for(String reservation : reservations) {
                            items.add(new ReservationItem(ReservationRecyclerAdapter.TYPE_ITEM, reservation));
                        }
                        reservations.clear();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            customProgressDialog.dismiss();

            recyclerAdapter.notifyDataSetChanged();
            setEnableControl(true);
        }
    }

    private class JATAllReservationState extends AsyncTask<Void, Void, Void> {

        List<String> reservations = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String htmlPageUrl = "https://reserv.bucheon.go.kr" + reservationUrl + "&sch_year=" + search_Year + "&sch_month=" + search_Month;
                Document doc = Jsoup.connect(htmlPageUrl).get();

                Elements ulList = doc.select("ul[class=reservation]");
                for (Element item : ulList) {
                    String date = item.attr("id");
                    if (date.startsWith(replace_Month)) {
                        date = date.substring(replace_Month.length(), date.length());
                    }
                    date = replace_Month + "." + date;

                    String time = "";
                    String team = "";
                    String result = "";
                    Elements liList = item.select("li");
                    for(Element liItem : liList) {
                        String liClass = liItem.attr("class");
                        String liString = liItem.text().trim();

                        if(liClass.equals("tm")) {
                            time = liString;
                        }

                        if(liClass.equals("red")) {
                            team = liString;

                            if(team.contains(filterName)) {
                                result = time + "#" + team + "#" + reservationPlace;
                                reservations.add(result);
                            }
                        }
                    }

                    if(result.length() > 0) {
                        String key = date;
                        ArrayList<String> dateValue = map.get(key);

                        if(dateValue == null) {
                            ArrayList<String> value = new ArrayList<>();
                            for(String reservation : reservations) {
                                value.add(reservation);
                            }
                            map.put(key, value);
                        }
                        else {
                            for(String reservation : reservations) {
                                dateValue.add(reservation);
                            }
                        }

                        reservations.clear();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(++teamSearchCount < itemLinks.size()) {
                reservationPlace = itemPlaces.get(teamSearchCount);
                reservationUrl = itemLinks.get(teamSearchCount);
                callJsoupAsyncTask();
            }
            else {
                customProgressDialog.dismiss();

                List<String> keyList = new ArrayList<>(map.keySet());
                keyList.sort((s1, s2)->s1.compareTo(s2));

                for(String key : keyList) {
                    items.add(new ReservationItem(ReservationRecyclerAdapter.TYPE_HEADER, key));
                    for(String reservation : map.get(key)) {
                        items.add(new ReservationItem(ReservationRecyclerAdapter.TYPE_ITEM, reservation));
                    }
                }

                recyclerAdapter.notifyDataSetChanged();
                setEnableControl(true);
            }
        }
    }
}