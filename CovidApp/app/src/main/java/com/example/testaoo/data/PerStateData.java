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

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.Objects;

import static com.example.testaoo.activities.StatewiseDataActivity.STATE_ACTIVE;
import static com.example.testaoo.activities.StatewiseDataActivity.STATE_INFECTED;
import static com.example.testaoo.activities.StatewiseDataActivity.STATE_DECEASED;
import static com.example.testaoo.activities.StatewiseDataActivity.STATE_LAST_UPDATE;
import static com.example.testaoo.activities.StatewiseDataActivity.STATE_NAME;
import static com.example.testaoo.activities.StatewiseDataActivity.STATE_RECOVERED;

public class PerStateData extends AppCompatActivity {

    TextView perStateConfirmed, perStateActive, perStateDeceased, perStateNewConfirmed, perStateNewRecovered, perStateNewDeceased, perStateUpdate, perStateRecovered, perstateName;
    PieChart mPieChart;
    String stateName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_per_state_data);

        Intent intent = getIntent();
        stateName = intent.getStringExtra(STATE_NAME);
        String stateConfirmed = intent.getStringExtra(STATE_INFECTED);
        String stateActive = intent.getStringExtra(STATE_ACTIVE);
        String stateDeceased = intent.getStringExtra(STATE_DECEASED);
        String stateLastUpdate = intent.getStringExtra(STATE_LAST_UPDATE);
        String stateRecovery = intent.getStringExtra(STATE_RECOVERED);

        Objects.requireNonNull(getSupportActionBar()).setTitle(stateName);
        perStateConfirmed = findViewById(R.id.perstate_confirmed_textview);
        perStateActive = findViewById(R.id.perstate_active_textView);
        perStateRecovered = findViewById(R.id.perstate_recovered_textView);
        perStateDeceased = findViewById(R.id.perstate_death_textView);
        perStateUpdate = findViewById(R.id.perstate_lastupdate_textView);
        perStateNewConfirmed = findViewById(R.id.perstate_confirmed_new_textView);
        perStateNewRecovered = findViewById(R.id.perstate_recovered_new_textView);
        perStateNewDeceased = findViewById(R.id.perstate_death_new_textView);
        perstateName = findViewById(R.id.district_data_title);
        mPieChart = findViewById(R.id.piechart_perstate);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        String activeCopy = stateActive;
        String recoveredCopy = stateRecovery;
        String deceasedCopy = stateDeceased;

        int stateActiveInt = Integer.parseInt(stateActive);
        stateActive = NumberFormat.getInstance().format(stateActiveInt);

        int stateDeceasedInt = Integer.parseInt(stateDeceased);
        stateDeceased = NumberFormat.getInstance().format(stateDeceasedInt);

        int stateRecoveredInt = Integer.parseInt(stateRecovery);
        stateRecovery = NumberFormat.getInstance().format(stateRecoveredInt);

        //assert stateActive != null;
        mPieChart.addPieSlice(new PieModel("Active", Integer.parseInt(activeCopy), Color.parseColor("#007afe")));
        mPieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(recoveredCopy), Color.parseColor("#08a045")));
        mPieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(deceasedCopy), Color.parseColor("#F6404F")));

        mPieChart.startAnimation();

        MainActivity object = new MainActivity();
        String formatDate = object.formatDate(stateLastUpdate, 0);
        perStateConfirmed.setText(stateConfirmed);
        perStateActive.setText(stateActive);
        perStateDeceased.setText(stateDeceased);
        perStateUpdate.setText(formatDate);
        perStateRecovered.setText(stateRecovery);
        perstateName.setText("District data of "+stateName);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
