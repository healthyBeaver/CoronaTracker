package com.example.testaoo.data;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.testaoo.MainActivity;
import com.example.testaoo.R;
import com.example.testaoo.activities.DistrictwiseDataActivity;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.Objects;

import static com.example.testaoo.activities.StatewiseDataActivity.COUNTRY_ACTIVE;
import static com.example.testaoo.activities.StatewiseDataActivity.COUNTRY_DECEASED;
import static com.example.testaoo.activities.StatewiseDataActivity.COUNTRY_INFECTED;
import static com.example.testaoo.activities.StatewiseDataActivity.COUNTRY_LAST_UPDATE;
import static com.example.testaoo.activities.StatewiseDataActivity.COUNTRY_NAME;
import static com.example.testaoo.activities.StatewiseDataActivity.COUNTRY_RECOVERED;

public class PerStateData extends AppCompatActivity {

    TextView perCountryConfirmed, perCountryActive, perCountryDeceased, perCountryNewConfirmed,
            perCountryNewRecovered, perCountryNewDeceased, perCountryUpdate, perCountryRecovered,
            perCountryName;
    PieChart mPieChart;
    String CountryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_per_state_data);

        Intent intent = getIntent();
        CountryName = intent.getStringExtra(COUNTRY_NAME);
        String countryConfirmed = intent.getStringExtra(COUNTRY_INFECTED);
        String countryActive = intent.getStringExtra(COUNTRY_ACTIVE);
        String countryDeceased = intent.getStringExtra(COUNTRY_DECEASED);
        String countryLastUpdate = intent.getStringExtra(COUNTRY_LAST_UPDATE);
        String countryRecovery = intent.getStringExtra(COUNTRY_RECOVERED);

        Objects.requireNonNull(getSupportActionBar()).setTitle(CountryName);
        perCountryConfirmed = findViewById(R.id.perstate_confirmed_textview);
        perCountryActive = findViewById(R.id.perstate_active_textView);
        perCountryRecovered = findViewById(R.id.perstate_recovered_textView);
        perCountryDeceased = findViewById(R.id.perstate_death_textView);
        perCountryUpdate = findViewById(R.id.perstate_lastupdate_textView);
        perCountryNewConfirmed = findViewById(R.id.perstate_confirmed_new_textView);
        perCountryNewRecovered = findViewById(R.id.perstate_recovered_new_textView);
        perCountryNewDeceased = findViewById(R.id.perstate_death_new_textView);
        perCountryName = findViewById(R.id.district_data_title);
        mPieChart = findViewById(R.id.piechart_perstate);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        String activeCopy = countryActive;
        String recoveredCopy = countryRecovery;
        String deceasedCopy = countryDeceased;

        int countryActiveInt = Integer.parseInt(countryActive);
        countryActive = NumberFormat.getInstance().format(countryActiveInt);

        int countryDeceasedInt = Integer.parseInt(countryDeceased);
        countryDeceased = NumberFormat.getInstance().format(countryDeceasedInt);

        int countryRecoveredInt = Integer.parseInt(countryRecovery);
        countryRecovery = NumberFormat.getInstance().format(countryRecoveredInt);

        //assert stateActive != null;
        mPieChart.addPieSlice(new PieModel("Active", Integer.parseInt(activeCopy), Color.parseColor("#007afe")));
        mPieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(recoveredCopy), Color.parseColor("#08a045")));
        mPieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(deceasedCopy), Color.parseColor("#F6404F")));

        mPieChart.startAnimation();

        MainActivity object = new MainActivity();
        String formatDate = object.formatDate(countryLastUpdate, 0);
        perCountryConfirmed.setText(countryConfirmed);
        perCountryActive.setText(countryActive);
        perCountryDeceased.setText(countryDeceased);
        perCountryUpdate.setText(formatDate);
        perCountryRecovered.setText(countryRecovery);
        perCountryName.setText("District data of " + CountryName);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void openDistrictData(View view) {
        Intent intent = new Intent(this, DistrictwiseDataActivity.class);
        intent.putExtra(COUNTRY_NAME, CountryName);
        startActivity(intent);
    }
}