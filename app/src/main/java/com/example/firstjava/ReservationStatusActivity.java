package com.example.firstjava;

import android.app.DatePickerDialog;
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
import java.util.List;

public class ReservationStatusActivity extends AppCompatActivity {

    ArrayList<String> itemPlaces = new ArrayList<>();
    ArrayList<String> itemLinks = new ArrayList<>();
    Boolean isTeamSearch = false;
    int teamSearchCount = 0;

    private EditText editText;
    private TextView textViewDate;

    private List<String> reservationDates = new ArrayList<>();
    private List<String> reservations = new ArrayList<>();

    private List<ReservationItem> items = new ArrayList<>();
    private ReservationRecyclerAdapter recyclerAdapter;

    String reservationTitle;
    String reservationUrl;

    String filterName = "";
    String search_Year = "";
    String search_Month = "";
    String replace_Month = "";

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
        if(reservationUrl.equals("TEAM_SEARCH")) {
            itemPlaces = getIntent().getStringArrayListExtra("intoPlace");
            itemLinks = getIntent().getStringArrayListExtra("intoLink");
            isTeamSearch = true;
            reservationUrl = itemLinks.get(teamSearchCount);
        }
        getSupportActionBar().setTitle(reservationTitle);

        //
        editText = (EditText) findViewById(R.id.editText);

        //
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YearMonthPickerDialog pd = new YearMonthPickerDialog();
                pd.setListener(datePickerDialogListener);
                pd.show(getSupportFragmentManager(), "YearMonthPicker");
            }
        });

        //
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reservationRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        //items.add(new ReservationItem(ReservationRecyclerAdapter.TYPE_HEADER, "헤더"));
        //items.add(new ReservationItem(ReservationRecyclerAdapter.TYPE_ITEM, "아이템"));
        recyclerAdapter = new ReservationRecyclerAdapter(items);
        recyclerView.setAdapter(recyclerAdapter);

        Button search = (Button) findViewById(R.id.buttonSearch);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterName = editText.getText().toString();
                reservations.clear();
                items.clear();

                JATReservationState jsoupAsyncTask = new JATReservationState();
                jsoupAsyncTask.execute();
            }
        });

        Calendar cal = Calendar.getInstance();
        setYearMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);

        JATReservationState jsoupAsyncTask = new JATReservationState();
        jsoupAsyncTask.execute();
    }

    public void setYearMonth(int year, int month) {
        search_Year = Integer.toString(year);
        search_Month = String.format("%02d", month);
        replace_Month = Integer.toString(month);

        String result = search_Year + "년 " +  search_Month + "월";
        textViewDate.setText(result);
    }

    private class JATReservationState extends AsyncTask<Void, Void, Void> {

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
                    date = search_Year + "년 " + replace_Month + "월 " + date + "일";

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

                            if(filterName.equals("")) {
                                result = time + " | " + team;
                                reservations.add(result);
                            }
                            else {
                                if(team.contains(filterName)) {
                                    result = time + " | " + team;
                                    reservations.add(result);
                                }
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
            Toast.makeText(ReservationStatusActivity.this, "검색 완료", Toast.LENGTH_SHORT).show();
            recyclerAdapter.notifyDataSetChanged();
        }
    }
}

