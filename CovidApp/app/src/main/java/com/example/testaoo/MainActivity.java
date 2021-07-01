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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    String infected;
    String infectCountry;
    String deceased;
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
        textView_date = findViewById(R.id.date_textView);
        swipeRefreshLayout = findViewById(R.id.main_refreshLayout);
        textview_time = findViewById(R.id.time_textView);

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
                fetchData();
//                cd.getTotalPopul();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Data refreshed!", Toast.LENGTH_SHORT).show();
            }
        });
//        fetchData();
//        cd.getTotalPopul();

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
        // data for "Infected" part
//        BigInteger confirmedInt = new BigInteger(infected);
//        infected = NumberFormat.getInstance().format(confirmedInt);
//        System.out.println("Testing the put data function \t" + infected);
//        System.out.println("Testing the put data function \t" + deceased);
        textView_infected.setText(infected);

        // data for "Recovered" part
//        BigInteger recoveredInt = new BigInteger(recovered);
//        recovered = NumberFormat.getInstance().format(recoveredInt);
//        textView_recovered.setText(recovered);

        // data fpr "Deceased" part
//        BigInteger deathsInt = new BigInteger(deceased);
//        deceased = NumberFormat.getInstance().format(deathsInt);
        textView_deceased.setText(deceased);
        textView_infectCountry.setText(infectCountry);
        textView_statistics.setText(statistics);

        // data for time and date part
//        String dateFormat = formatDate(date, 1);
//        String timeFormat = formatDate(date, 2);
//        textView_date.setText(dateFormat);
//        textview_time.setText(timeFormat);
    }

    // method for fetching data
    public void fetchData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        String apiUrl = "https://api.covid19india.org/data.json";

        final PieChart mPieChart = findViewById(R.id.piechart);
        mPieChart.clearChart();
        mPieChart.addPieSlice(new PieModel("Infected", Integer.parseInt(cd.totalInfected.toString()), Color.parseColor("yellow")));
        mPieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(cd.totalDeceased.toString()), Color.parseColor("red")));
//        mPieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(deathsCopy), Color.parseColor("#F6404F")));
        mPieChart.startAnimation();
//        String[] wdata = cd.worldData();
//        System.out.println(wdata[0]);
//        infected = wdata[0];
//        deceased = wdata[2];
//        putData();
        //Example: Fetching the API from URL
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    //Since the objects of JSON are in an Array we need to define the array from which we can fetch objects
//                    JSONArray jsonArray = response.getJSONArray("statewise");
//                    JSONObject statewise = jsonArray.getJSONObject(0);
//
//
//                    if (isRefreshed) {
//                        //Inserting the fetched data into variables
//                        infected = statewise.getString("infected");
//                        date = statewise.getString("lastupdatedtime");
//                        recovered = statewise.getString("recovered");
//                        deceased = statewise.getString("deceased");
//
//                        Runnable progressRunnable = new Runnable() {
//
//                            @SuppressLint("SetTextI18n")
//                            @Override
//                            public void run() {
//                                progressDialog.cancel();
//                                String deathsCopy = deceased;
//                                String recoveredCopy = recovered;
//                                String infectedCopy = infected;
//                                putData();
//
//                                // display the data with pie chart
//                                mPieChart.addPieSlice(new PieModel("Infected", Integer.parseInt(infectedCopy), Color.parseColor("yellow")));
//                                mPieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(recoveredCopy), Color.parseColor("#08a045")));
//                                mPieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(deathsCopy), Color.parseColor("#F6404F")));
//                                mPieChart.startAnimation();
//                                fetchTests();
//                            }
//                        };
//                        Handler pdCanceller = new Handler();
//                        pdCanceller.postDelayed(progressRunnable, 0);
//                    } else {
//                        //Inserting the fetched data into variables
//                        infected = statewise.getString("infected");
//                        date = statewise.getString("lastupdatedtime");
//                        recovered = statewise.getString("recovered");
//                        deceased = statewise.getString("deaths");
//
//                        if (!date.isEmpty()) {
//                            Runnable progressRunnable = new Runnable() {
//
//                                @SuppressLint("SetTextI18n")
//                                @Override
//                                public void run() {
//                                    progressDialog.cancel();
//                                    String deathsCopy = deceased;
//                                    String recoveredCopy = recovered;
//                                    String infectedCopy = infected;
//                                    putData();
//
//                                    // display the data with pie chart
//                                    mPieChart.addPieSlice(new PieModel("Infected", Integer.parseInt(infectedCopy), Color.parseColor("yellow")));
//                                    mPieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(recoveredCopy), Color.parseColor("#08a045")));
//                                    mPieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(deathsCopy), Color.parseColor("#F6404F")));
//                                    mPieChart.startAnimation();
//                                    fetchTests();
//                                }
//                            };
//                            Handler pdCanceller = new Handler();
//                            pdCanceller.postDelayed(progressRunnable, 1000);
////                            while(!cd.loaded){
////
////                            }
////                            infection = 1;
//                        }
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        });
//        requestQueue.add(jsonObjectRequest);
    }

    // method for fetching sample tests data
//    public void fetchTests() {
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        String apiUrl = "https://api.covid19india.org/data.json";
//        JsonObjectRequest jsonObjectRequestTests = new JsonObjectRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    JSONArray jsonArray = response.getJSONArray("tested");
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject statewise = jsonArray.getJSONObject(i);
//                        totalTests = statewise.getString("totalsamplestested");
//                    }
//
//                    for (int i = 0; i < jsonArray.length() - 1; i++) {
//                        JSONObject statewise = jsonArray.getJSONObject(i);
//                        oldTests = statewise.getString("totalsamplestested");
//                    }
//                    if (totalTests.isEmpty()) {
//                        for (int i = 0; i < jsonArray.length() - 1; i++) {
//                            JSONObject statewise = jsonArray.getJSONObject(i);
//                            totalTests = statewise.getString("totalsamplestested");
//                        }
//                        totalTestsCopy = totalTests;
//                        testsInt = new BigInteger(totalTests);
//                        totalTests = NumberFormat.getInstance().format(testsInt);
//                        textView_tests.setText(totalTests);
//
//
//                        for (int i = 0; i < jsonArray.length() - 2; i++) {
//                            JSONObject statewise = jsonArray.getJSONObject(i);
//                            oldTests = statewise.getString("totalsamplestested");
//                        }
//
//                    } else {
//                        totalTestsCopy = totalTests;
//                        testsInt = new BigInteger(totalTests);
//                        totalTests = NumberFormat.getInstance().format(testsInt);
//                        textView_tests.setText(totalTests);
//
//                        if (oldTests.isEmpty()) {
//                            for (int i = 0; i < jsonArray.length() - 2; i++) {
//                                JSONObject statewise = jsonArray.getJSONObject(i);
//                                oldTests = statewise.getString("totalsamplestested");
//                            }
//                        }
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        });
//        requestQueue.add(jsonObjectRequestTests);
//    }

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
                }
                if (cd.loaded && !cd.connectError){
                    progressDialog.cancel();
                    String[] wdata = cd.worldData();
//                    System.out.println(wdata[0]);
                    infected = wdata[0];
                    infectCountry = wdata[1];
                    deceased = wdata[2];
//                    deceasedCountry = wdata[3];
//                    System.out.println("Deceased: " + cd.totalDeceased);
//                    System.out.println("Infected: " + cd.totalInfected);

                    BigDecimal sumTemp = (cd.totalDeceased.multiply(new BigDecimal(100))).divide(cd.totalInfected.add(cd.totalDeceased),2,BigDecimal.ROUND_HALF_EVEN);
//                    System.out.println("Ratio: " + sumTemp);
//                    sumTemp = sumTemp.multiply(new BigDecimal(100));
                    statistics = sumTemp + "%";
//                    sumTemp = sumTemp.setScale(2, BigDecimal.ROUND_HALF_EVEN);
//                    statistics.concat("Deceased ratio: " + sumTemp +"%");
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
