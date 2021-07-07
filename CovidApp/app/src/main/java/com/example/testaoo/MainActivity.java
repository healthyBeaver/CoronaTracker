package com.example.testaoo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.testaoo.activities.StatewiseDataActivity;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    BigDecimal infected;
    BigDecimal infectCountry;
    BigDecimal deceased;
    String statistics;
    String date;
    String oldTests;
    BigInteger testsInt;
    String totalTestsCopy;
    public static int infection = 0;
    public static boolean isRefreshed;
    private long backPressTime;
    private Toast backToast;
    String version;
    TextView textView_infected, textView_infectCountry, textView_deceased, textView_statistics, textView_date,
            textview_time;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;

    public static CountryData cd;
    boolean loadStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        loadStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        version = String.valueOf(R.string.version);
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);

        // get the layouts with IDs
        textView_infected = findViewById(R.id.infected_textView);
        textView_infectCountry = findViewById(R.id.infectCountry_textView);
        textView_deceased = findViewById(R.id.deceased_textView);
        textView_statistics = findViewById(R.id.statistics_textView);
        swipeRefreshLayout = findViewById(R.id.main_refreshLayout);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        Objects.requireNonNull(getSupportActionBar()).setTitle("Global Covid-19 Status");

        cd = new CountryData();
        cd.execute();
        showProgressDialog();


        // methods to get data by refreshing window
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshed = true;
                loadStatus = false;
                cd = new CountryData();
                cd.execute();
                showProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Data refreshed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // when user press back button on the basic screen,
    // the app checks whether the user wants to exit the application.
    @Override
    public void onBackPressed() {
        if (backPressTime + 800 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressTime = System.currentTimeMillis();
    }

    // method for putting data with texts
    private void putData() {
        NumberFormat numFormat = NumberFormat.getInstance();
        System.out.println("Testing Format: " + infected + " with " + numFormat.format(infected));
        textView_infected.setText(numFormat.format(infected));
        textView_deceased.setText(numFormat.format(deceased));
        textView_infectCountry.setText(numFormat.format(infectCountry));
        textView_statistics.setText(statistics);
    }

    // method for fetching data
    public void fetchData() {
        final PieChart mPieChart = findViewById(R.id.piechart);
        mPieChart.clearChart();
        mPieChart.addPieSlice(new PieModel("Infected", Integer.parseInt(cd.totalInfected.toString()), Color.parseColor("yellow")));
        mPieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(cd.totalDeceased.toString()), Color.parseColor("red")));
        mPieChart.startAnimation();
    }

    // method for displaying date and time
    public String formatDate(String date, int testCase) {
        Date mDate = null;
        String dateFormat;
        try {
            mDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).parse(date);
            if (testCase == 0) {
                dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(mDate);
                return dateFormat;
            } else if (testCase == 1) {
                dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US).format(mDate);
                return dateFormat;
            } else if (testCase == 2) {
                dateFormat = new SimpleDateFormat("hh:mm a", Locale.US).format(mDate);
                return dateFormat;
            } else {
                Log.d("error", "Wrong input! Choose from 0 to 2");
                return "Error";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }


    // method for showing progress dialog: if internet is slow or not connected
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                while(!cd.loaded && !cd.connectError){
                    break;
                }
                if (cd.loaded && !cd.connectError){
                    progressDialog.cancel();
                    infected = cd.totalInfected;
                    infectCountry = cd.totalInfectedCountry;
                    deceased = cd.totalDeceased;
                    BigDecimal sumTemp = (cd.totalDeceased.multiply(new BigDecimal(100))).divide(cd.totalInfected.add(cd.totalDeceased),2,BigDecimal.ROUND_HALF_EVEN);
                    statistics = sumTemp + "%";
                    putData();
                    fetchData();
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.cancel();
                    Toast.makeText(MainActivity.this, "Internet slow/not available", Toast.LENGTH_SHORT).show();
                }
            }
        };
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 8000);
    }

    // method for connecting country data part
    public void openStatewise(View view) {
        Intent intent = new Intent(this, StatewiseDataActivity.class);
        startActivity(intent);
    }
}