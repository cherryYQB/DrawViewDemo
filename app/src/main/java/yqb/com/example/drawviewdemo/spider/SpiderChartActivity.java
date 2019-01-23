package yqb.com.example.drawviewdemo.spider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import yqb.com.example.drawviewdemo.R;

public class SpiderChartActivity extends AppCompatActivity {

    private SpiderChartView spiderChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spider_chart);

        spiderChartView = (SpiderChartView) findViewById(R.id.spiderChartView);
    }
}
