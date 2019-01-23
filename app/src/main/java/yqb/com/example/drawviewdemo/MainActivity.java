package yqb.com.example.drawviewdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import yqb.com.example.drawviewdemo.Line.LineChartActivity;
import yqb.com.example.drawviewdemo.pie.PieActivity;
import yqb.com.example.drawviewdemo.spider.SpiderChartActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button peiButton;
    private Button spiderChartButton;
    private Button lineChartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        peiButton = (Button) findViewById(R.id.pie);
        spiderChartButton = (Button) findViewById(R.id.spider_chart);
        lineChartButton = (Button) findViewById(R.id.line_chart);

        peiButton.setOnClickListener(this);
        spiderChartButton.setOnClickListener(this);
        lineChartButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.pie:
                intent = new Intent(this, PieActivity.class);
                startActivity(intent);
                break;
            case R.id.spider_chart:
                intent = new Intent(this, SpiderChartActivity.class);
                startActivity(intent);
                break;
            case R.id.line_chart:
                intent = new Intent(this, LineChartActivity.class);
                startActivity(intent);
                break;
        }
    }
}
