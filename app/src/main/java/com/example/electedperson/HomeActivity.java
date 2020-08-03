package com.example.electedperson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle nToggle;
    NavigationView navigationView;
    private Long calsBurned;
    private Long calsConsumed;
    private CombinedChart chart;
    int index;
    ArrayList<BarEntry> entries1 = new ArrayList<>();
    ArrayList<BarEntry> entries2 = new ArrayList<>();
    ArrayList<Entry> entries = new ArrayList<>();
    private final int count = 7;
    protected final String[] months = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        @SuppressLint("WrongViewCast") Toolbar my_toolbar = findViewById(R.id.actionBar);
        my_toolbar.setTitle("");
        setSupportActionBar(my_toolbar);

        DrawerLayout nDrawerLayout = findViewById(R.id.navigationMenu);
        nToggle = new ActionBarDrawerToggle(this, nDrawerLayout, R.string.open, R.string.close);

        nDrawerLayout.addDrawerListener(nToggle);
        nToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        //graph
        chart = findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        final XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return months[(int) value % months.length];
            }
        });

        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
        db1.collection("Fixed_Pothole").document("2020")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                entries.add(new Entry(0 + 0.5f, document.getLong("Jan")));
                                entries.add(new Entry(1 + 0.5f, document.getLong("Feb")));
                                entries.add(new Entry(2 + 0.5f, document.getLong("Mar")));
                                entries.add(new Entry(3 + 0.5f, document.getLong("Apr")));
                                entries.add(new Entry(4 + 0.5f, document.getLong("May")));
                                entries.add(new Entry(5 + 0.5f, document.getLong("Jun")));
                                entries.add(new Entry(6 + 0.5f, document.getLong("Jul")));
                                calsBurned = document.getLong("Jul");
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("Reported_Pothole").document("2020")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document != null) {
                                                        entries1.add(new BarEntry(0, document.getLong("Jan")));
                                                        entries2.add(new BarEntry(0, document.getLong("Jan")));
                                                        entries1.add(new BarEntry(0, document.getLong("Feb")));
                                                        entries2.add(new BarEntry(0, document.getLong("Feb")));
                                                        entries1.add(new BarEntry(0, document.getLong("Mar")));
                                                        entries2.add(new BarEntry(0, document.getLong("Mar")));
                                                        entries1.add(new BarEntry(0, document.getLong("Apr")));
                                                        entries2.add(new BarEntry(0, document.getLong("Apr")));
                                                        entries1.add(new BarEntry(0, document.getLong("May")));
                                                        entries2.add(new BarEntry(0, document.getLong("May")));
                                                        entries1.add(new BarEntry(0, document.getLong("Jun")));
                                                        entries2.add(new BarEntry(0, document.getLong("Jun")));
                                                        entries1.add(new BarEntry(0, document.getLong("Jul")));
                                                        entries2.add(new BarEntry(0, document.getLong("Jul")));
                                                        calsConsumed = document.getLong("Jul");
                                                        CombinedData data = new CombinedData();
                                                        data.setData(generateLineData());
                                                        data.setData(generateBarData());
                                                        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
                                                        chart.setData(data);
                                                        chart.invalidate();
                                                        updateChart();
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }


    private LineData generateLineData() {

        LineData d = new LineData();
        LineDataSet set = new LineDataSet(entries, "Fixed Pothole");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }


    private BarData generateBarData() {

        BarDataSet set1 = new BarDataSet(entries1, "");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(255, 255, 255));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarDataSet set2 = new BarDataSet(entries2, "Recorded Pothole");
        set2.setColor(Color.rgb(60, 220, 78));
        set2.setValueTextColor(Color.rgb(60, 220, 78));
        set2.setValueTextSize(10f);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset

        BarData d = new BarData(set1, set2);
        d.setBarWidth(barWidth);

        // make this BarData object grouped
        d.groupBars(0, groupSpace, barSpace); // start at x = 0

        return d;
    }

    private void updateChart(){
        // Update the text in a center of the chart:
        TextView numberOfCals = findViewById(R.id.number_of_calories);
        numberOfCals.setText(String.valueOf(calsBurned) + " / " + calsConsumed);

        // Calculate the slice size and update the pie chart:
        ProgressBar pieChart = findViewById(R.id.stats_progressbar);
        double d = (double) calsBurned / (double) calsConsumed;
        int progress = (int) (d * 100);
        pieChart.setProgress(progress);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (nToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.logout:
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
//                break;
//            case R.id.about_us:
//                startActivity(new Intent(HomeActivity.this, AboutUs.class));
//                break;
//            case R.id.help:
//                Intent Getintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vishwamshukla.intelaedu.com/"));
//                startActivity(Getintent);
//                break;
//            case R.id.chats:
//                startActivity(new Intent(HomeActivity.this, ChatsActivity.class));
//                break;
//        }
        return false;
    }
}