package yqb.com.example.drawviewdemo.spider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SpiderChartView extends View {

    //数据个数
    private int count = 5;
    //五边形角度   Math.sin/cos里面的值都是弧度而非角度
    private float angle = (float) (Math.PI*2/count);
    //网格最大半径
    private float radius;
    //中心X
    private int centerX;
    //中心Y
    private int centerY;
    //各维度分值
    private double[] data = {2,3,5,4,1};
    //数据最大值
    private float maxValue = 5;
    //网格区画笔
    private Paint mainPaint;
    //数据区画笔
    private Paint valuePaint;

    public SpiderChartView(Context context) {
        super(context);
    }

    public SpiderChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mainPaint = new Paint();
        mainPaint.setColor(Color.parseColor("#b5b5b5"));
        mainPaint.setStrokeWidth(3);
        mainPaint.setAntiAlias(true);
        mainPaint.setStyle(Paint.Style.STROKE);

        valuePaint=new Paint();
        valuePaint.setColor(Color.parseColor("#f39700"));
        valuePaint.setStrokeWidth(3);
        valuePaint.setAntiAlias(true);
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPolygon(canvas);
        drawLines(canvas);
        drawRegion(canvas);
    }

    /*
    绘制多边形
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        //r是蜘蛛丝之间的间距
        float r = radius/(count-1);
        //中心点不用绘制
        for(int i=1; i<=count; i++){
            //当前半径
            float curR = r*i;
            path.reset();
            for(int j=0; j<count; j++){
                if(j == 0) {
                    float x = (float) (centerX + curR*Math.sin(angle));
                    float y = (float) (centerY - curR*Math.cos(angle));
                    path.moveTo(x,y);
                }else{
                    //根据半径，计算出蜘蛛丝上每个点的坐标
                    float x1 = (float) (centerX + curR*Math.sin(angle/2));
                    float y1 = (float) (centerY + curR*Math.cos(angle/2));
                    path.lineTo(x1,y1);
                    float x2 = (float) (centerX - curR*Math.sin(angle/2));
                    float y2 = (float) (centerY + curR*Math.cos(angle/2));
                    path.lineTo(x2,y2);
                    float x3 = (float) (centerX - curR*Math.sin(angle));
                    float y3 = (float) (centerY - curR*Math.cos(angle));
                    path.lineTo(x3,y3);
                    float x4 = (float) (centerX);
                    float y4 = (float) (centerY - curR);
                    path.lineTo(x4,y4);
                    float x = (float) (centerX + curR*Math.sin(angle));
                    float y = (float) (centerY - curR*Math.cos(angle));
                    path.lineTo(x, y);
                }
            }
            //闭合路径
            path.close();
            canvas.drawPath(path, mainPaint);
        }
    }

    /*
    绘制直线
     */
    private void drawLines(Canvas canvas) {
        Path path = new Path();
        path.reset();
        //r是蜘蛛丝之间的间距
        float r = radius/(count-1);
        //当前半径
        float curR = r*5;
        float x = (float) (centerX+curR*Math.sin(angle));
        float y = (float) (centerY-curR*Math.cos(angle));
        path.moveTo(x, y);
        path.lineTo(centerX,centerY);
        float x1 = (float) (centerX+curR*Math.sin(angle/2));
        float y1 = (float) (centerY+curR*Math.cos(angle/2));
        path.moveTo(x1, y1);
        path.lineTo(centerX,centerY);
        float x2 = (float) (centerX-curR*Math.sin(angle/2));
        float y2 = (float) (centerY+curR*Math.cos(angle/2));
        path.moveTo(x2, y2);
        path.lineTo(centerX,centerY);
        float x3 = (float) (centerX-curR*Math.sin(angle));
        float y3 = (float) (centerY-curR*Math.cos(angle));
        path.moveTo(x3, y3);
        path.lineTo(centerX,centerY);
        float x4 = (float) (centerX);
        float y4 = (float) (centerY-curR);
        path.moveTo(x4, y4);
        path.lineTo(centerX,centerY);
        path.close();
        canvas.drawPath(path, mainPaint);
    }

    /*
    绘制区域
     */
    private void drawRegion(Canvas canvas) {
        Path path = new Path();
        valuePaint.setAlpha(255);
        //r是蜘蛛丝之间的间距
        float r = radius/(count-1);
        double percent1;
        if(data[0] != maxValue){
            percent1 = data[0]%maxValue;
        }else{
            percent1 = maxValue;
        }
        float x1 = (float) (centerX+r*percent1*Math.sin(angle));
        float y1= (float) (centerY-r*percent1*Math.cos(angle));
        //绘制小圆点
        path.moveTo(x1, y1);
        canvas.drawCircle(x1,y1,5,valuePaint);
        double percent2;
        if(data[1] != maxValue){
            percent2 = data[1]%maxValue;
        }else{
            percent2 = maxValue;
        }
        float x2 = (float) (centerX+r*percent2*Math.sin(angle/2));
        float y2= (float) (centerY+r*percent2*Math.cos(angle/2));
        //绘制小圆点
        path.lineTo(x2, y2);
        canvas.drawCircle(x2,y2,5,valuePaint);
        double percent3;
        if(data[2] != maxValue){
            percent3 = data[2]%maxValue;
        }else{
            percent3 = maxValue;
        }
        float x3 = (float) (centerX-r*percent3*Math.sin(angle/2));
        float y3= (float) (centerY+r*percent3*Math.cos(angle/2));
        //绘制小圆点
        path.lineTo(x3, y3);
        canvas.drawCircle(x3,y3,5,valuePaint);
        double percent4;
        if(data[3] != maxValue){
            percent4 = data[3]%maxValue;
        }else{
            percent4 = maxValue;
        }
        float x4 = (float) (centerX-r*percent4*Math.sin(angle));
        float y4= (float) (centerY-r*percent4*Math.cos(angle));
        //绘制小圆点
        path.lineTo(x4, y4);
        canvas.drawCircle(x4,y4,5,valuePaint);
        double percent5;
        if(data[4] != maxValue){
            percent5 = data[4]%maxValue;
        }else{
            percent5 = maxValue;
        }
        float x5 = (float) (centerX);
        float y5= (float) (centerY-r*percent5);
        //绘制小圆点
        path.lineTo(x5, y5);
        canvas.drawCircle(x5,y5,5,valuePaint);
        path.lineTo(x1, y1);
        path.close();
        valuePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, valuePaint);
        valuePaint.setAlpha(90);
        //绘制填充区域
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, valuePaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.centerX = getWidth()/2;
        this.centerY = getHeight()/2;

        //防止绘制后View超出屏幕大小，首先获取屏幕宽高的较小值
        //获取到直径
        int min = Math.min(w, h);
        //获取半径
        radius = (int) (min * 0.7f / 2);
    }
}
