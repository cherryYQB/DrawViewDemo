package yqb.com.example.drawviewdemo.pie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import yqb.com.example.drawviewdemo.R;
import yqb.com.example.drawviewdemo.model.PieBean;

public class PieActivity extends AppCompatActivity {

    //扇形显示的颜色值
    private int[] colors = {0xFFCCFF00,0xFF6495ED,0xFFE32636,0xFF800000,0xFF808000,0xFFFF8C69
            ,0xFF808080,0xFFE68800,0xFF7CFC00};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie);

        initView();
    }

    private void initView() {
        PieView pieView = (PieView) findViewById(R.id.pieView);
        pieView.setData(getPieData());
    }

    //模拟数据源
    private List getPieData() {
        List pieDataList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            pieDataList.add(new PieBean(i+1, colors[i]));
        }
        return pieDataList;
    }
}
