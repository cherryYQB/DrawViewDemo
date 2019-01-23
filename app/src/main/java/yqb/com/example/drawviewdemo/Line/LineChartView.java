package yqb.com.example.drawviewdemo.Line;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yqb.com.example.drawviewdemo.R;
import yqb.com.example.drawviewdemo.utils.MathUtil;

public class LineChartView extends View {

    private int xylinecolor;
    private int xylinewidth;
    private int xytextcolor;
    private int xytextsize;
    private int linecolor;
    private int interval;
    private int bgcolor;
    //屏幕宽
    private int width;
    //屏幕高
    private int height;
    //x轴的原点坐标
    private int xOri;
    //y轴的原点坐标
    private int yOri;
    //第一个点X的坐标
    private float xInit;
    //第一个点对应的最大Y坐标
    private float maxXInit;
    //第一个点对应的最小X坐标
    private float minXInit;
    //x轴坐标对应的数据
    private List<String> xValue = new ArrayList<>();
    //y轴坐标对应的数据
    private List<Integer> yValue = new ArrayList<>();
    //折线对应的数据
    private Map<String, Integer> value = new HashMap<>();
    //点击的点对应的X轴的第几个点，默认1
    private int selectIndex = 1;
    //X轴刻度文本对应的最大矩形，为了选中时，在x轴文本画的框框大小一致
    private Rect xValueRect;
    //速度检测器
    private VelocityTracker velocityTracker;

    private Paint xyPaint;
    private Paint xyTextPaint;
    private Paint linePaint;
    private Context mContext;
    private float startX;

    public LineChartView(Context context) {
        super(context);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context, attrs);
        initPaint();
    }

    public void setValue(Map<String, Integer> value, List<String> xValue, List<Integer> yValue) {
        this.value = value;
        this.xValue = xValue;
        this.yValue = yValue;
        invalidate();
    }

    /*
    初始化xml里面自定义数据
     */
    private void init(Context context, AttributeSet attributeSet) {
        TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.LinechartView);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.LinechartView_xylinecolor://xy坐标轴颜色
                    xylinecolor = array.getColor(attr, xylinecolor);
                    break;
                case R.styleable.LinechartView_xylinewidth://xy坐标轴宽度
                    xylinewidth = (int) array.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, xylinewidth, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.LinechartView_xytextcolor://xy坐标轴文字颜色
                    xytextcolor = array.getColor(attr, xytextcolor);
                    break;
                case R.styleable.LinechartView_xytextsize://xy坐标轴文字大小
                    xytextsize = (int) array.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, xytextsize, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.LinechartView_linecolor://折线图中折线的颜色
                    linecolor = array.getColor(attr, linecolor);
                    break;
                case R.styleable.LinechartView_interval://x轴各个坐标点水平间距
                    interval = (int) array.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, interval, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.LinechartView_bgcolor: //背景颜色
                    bgcolor = array.getColor(attr, bgcolor);
                    break;
            }
        }
        array.recycle();
    }

    /*
    初始化
     */
    private void initPaint() {
        xyPaint = new Paint();
        xyPaint.setAntiAlias(true);
        xyPaint.setStrokeWidth(xylinewidth);
        xyPaint.setStrokeCap(Paint.Cap.ROUND);
        xyPaint.setColor(xylinecolor);

        xyTextPaint = new Paint();
        xyTextPaint.setAntiAlias(true);
        xyTextPaint.setTextSize(xytextsize);
        xyTextPaint.setStrokeCap(Paint.Cap.ROUND);
        xyTextPaint.setColor(xytextcolor);
        xyTextPaint.setStyle(Paint.Style.STROKE);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(xylinewidth);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setColor(linecolor);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(bgcolor);
        drawXY(canvas);
        drawBrokenLineAndPoint(canvas);
    }

    private void drawXY(Canvas canvas) {
        int length = dpToPx(4);//刻度的长度
        //绘制Y坐标
        canvas.drawLine(xOri - xylinewidth / 2, 0,
                xOri - xylinewidth / 2, yOri, xyPaint);
        //绘制y轴箭头
        xyPaint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        path.moveTo(xOri - xylinewidth / 2 - dpToPx(5), dpToPx(12));
        path.lineTo(xOri - xylinewidth / 2, xylinewidth / 2);
        path.lineTo(xOri - xylinewidth / 2 + dpToPx(5), dpToPx(12));
        canvas.drawPath(path, xyPaint);
        //绘制y轴刻度
        int yLength = (int) (yOri * (1 - 0.1f) / (yValue.size() - 1));//y轴上面空出10%,计算出y轴刻度间距
        for (int i = 0; i < yValue.size(); i++) {
            //绘制Y轴刻度
            canvas.drawLine(xOri, yOri - yLength * i + xylinewidth / 2, xOri + length, yOri - yLength * i + xylinewidth / 2, xyPaint);
            xyTextPaint.setColor(xytextcolor);
            //绘制Y轴文本
            String text = yValue.get(i) + "";
            Rect rect = getTextBounds(text, xyTextPaint);
            canvas.drawText(text, 0, text.length(), xOri - xylinewidth - dpToPx(2) - rect.width(), yOri - yLength * i + rect.height() / 2, xyTextPaint);
        }
        //绘制X轴坐标
        canvas.drawLine(xOri, yOri + xylinewidth / 2, width, yOri + xylinewidth / 2, xyPaint);
        //绘制x轴箭头
        xyPaint.setStyle(Paint.Style.STROKE);
        path = new Path();
        //整个X轴的长度
        float xLength = xInit + interval * (xValue.size() - 1) + (width - xOri) * 0.1f;
        if (xLength < width)
            xLength = width;
        path.moveTo(xLength - dpToPx(12), yOri + xylinewidth / 2 - dpToPx(5));
        path.lineTo(xLength - xylinewidth / 2, yOri + xylinewidth / 2);
        path.lineTo(xLength - dpToPx(12), yOri + xylinewidth / 2 + dpToPx(5));
        canvas.drawPath(path, xyPaint);
        //绘制x轴刻度
        for (int i = 0; i < xValue.size(); i++) {
            float x = xInit + interval * i;
            if (x >= xOri) {//只绘制从原点开始的区域
                xyTextPaint.setColor(xytextcolor);
                canvas.drawLine(x, yOri, x, yOri - length, xyPaint);
                //绘制X轴文本
                String text = xValue.get(i);
                Rect rect = getTextBounds(text, xyTextPaint);
                if (i == selectIndex - 1) {
                    xyTextPaint.setColor(linecolor);
                    canvas.drawText(text, 0, text.length(), x - rect.width() / 2, yOri + xylinewidth + dpToPx(2) + rect.height(), xyTextPaint);
                    canvas.drawRoundRect(x - xValueRect.width() / 2 - dpToPx(3), yOri + xylinewidth + dpToPx(1), x + xValueRect.width() / 2 + dpToPx(3), yOri + xylinewidth + dpToPx(2) + xValueRect.height() + dpToPx(2), dpToPx(2), dpToPx(2), xyTextPaint);
                } else {
                    canvas.drawText(text, 0, text.length(), x - rect.width() / 2, yOri + xylinewidth + dpToPx(2) + rect.height(), xyTextPaint);
                }
            }
        }
    }

    /*
    绘制折线和折线交点处对应的点
     */
    private void drawBrokenLineAndPoint(Canvas canvas) {
        if (xValue.size() <= 0)
            return;
        //重新开一个图层
        int layerId = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        drawBrokenLine(canvas);
        drawBrokenPoint(canvas);
        // 将折线超出x轴坐标的部分截取掉
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setColor(bgcolor);
        linePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        RectF rectF = new RectF(0, 0, xOri, height);
        canvas.drawRect(rectF, linePaint);
        linePaint.setXfermode(null);
        //保存图层
        canvas.restoreToCount(layerId);
    }

    /*
    绘制折线对应的点和选中时浮动框
     */
    private void drawBrokenPoint(Canvas canvas) {
        float dp2 = dpToPx(2);
        float dp4 = dpToPx(4);
        float dp7 = dpToPx(7);
        //绘制节点对应的原点
        for (int i = 0; i < xValue.size(); i++) {
            float x = xInit + interval * i;
            float y = yOri - yOri * (1 - 0.1f) * value.get(xValue.get(i)) / yValue.get(yValue.size() - 1);
            //绘制选中的点
            if (i == selectIndex - 1) {
                linePaint.setStyle(Paint.Style.FILL);
                linePaint.setColor(0xffd0f3f2);
                canvas.drawCircle(x, y, dp7, linePaint);
                linePaint.setColor(0xff81dddb);
                canvas.drawCircle(x, y, dp4, linePaint);
                drawFloatTextBox(canvas, x, y - dp7, value.get(xValue.get(i)));
            }
            //绘制普通的节点
            //先遮盖掉原来的线段
            linePaint.setStyle(Paint.Style.FILL);
            linePaint.setColor(Color.WHITE);
            canvas.drawCircle(x, y, dp2, linePaint);
            //再绘制圈
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setColor(linecolor);
            canvas.drawCircle(x, y, dp2, linePaint);
        }
    }

    /*
    绘制显示Y值的浮动框
     */
    private void drawFloatTextBox(Canvas canvas, float x, float y, int text) {
        int dp6 = dpToPx(6);
        int dp18 = dpToPx(18);
        //p1
        Path path = new Path();
        path.moveTo(x, y);
        //p2
        path.lineTo(x - dp6, y - dp6);
        //p3
        path.lineTo(x - dp18, y - dp6);
        //p4
        path.lineTo(x - dp18, y - dp6 - dp18);
        //p5
        path.lineTo(x + dp18, y - dp6 - dp18);
        //p6
        path.lineTo(x + dp18, y - dp6);
        //p7
        path.lineTo(x + dp6, y - dp6);
        //p1
        path.lineTo(x, y);
        canvas.drawPath(path, linePaint);
        linePaint.setColor(Color.WHITE);
        linePaint.setTextSize(spToPx(14));
        Rect rect = getTextBounds(text + "", linePaint);
        canvas.drawText(text + "", x - rect.width() / 2, y - dp6 - (dp18 - rect.height()) / 2, linePaint);
    }

    /*
    绘制折线
     */
    private void drawBrokenLine(Canvas canvas) {
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(linecolor);
        //绘制折线
        Path path = new Path();
        float x = xInit + interval * 0;
        float y = yOri - yOri * (1 - 0.1f) * value.get(xValue.get(0)) / yValue.get(yValue.size() - 1);
        path.moveTo(x, y);
        for (int i = 1; i < xValue.size(); i++) {
            x = xInit + interval * i;
            y = yOri - yOri * (1 - 0.1f) * value.get(xValue.get(i)) / yValue.get(yValue.size() - 1);
            path.lineTo(x, y);
        }
        canvas.drawPath(path, linePaint);
    }

    /*
    dp转化成为px
     */
    public int dpToPx(int dp) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f * (dp >= 0 ? 1 : -1));
    }

    /*
    sp转化为px
     */
    private int spToPx(int sp) {
        float scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (scaledDensity * sp + 0.5f * (sp >= 0 ? 1 : -1));
    }

    /*
    获取丈量文本的矩形
     */
    private Rect getTextBounds(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.getParent().requestDisallowInterceptTouchEvent(true);//当该view获得点击事件，就请求父控件不拦截事件
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (interval * xValue.size() > width - xOri) {//当期的宽度不足以呈现全部数据
                    float dis = event.getX() - startX;
                    startX = event.getX();
                    if (xInit + dis < minXInit) {
                        xInit = minXInit;
                    } else if (xInit + dis > maxXInit) {
                        xInit = maxXInit;
                    } else {
                        xInit = xInit + dis;
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                clickAction(event);
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    /*
    点击X轴坐标或者折线节点
     */
    private void clickAction(MotionEvent event) {
        int dp8 = dpToPx(8);
        float eventX = event.getX();
        float eventY = event.getY();
        for (int i = 0; i < xValue.size(); i++) {
            //节点
            float x = xInit + interval * i;
            float y = yOri - yOri * (1 - 0.1f) * value.get(xValue.get(i)) / yValue.get(yValue.size() - 1);
            if (eventX >= x - dp8 && eventX <= x + dp8 &&
                    eventY >= y - dp8 && eventY <= y + dp8 && selectIndex != i + 1) {//每个节点周围8dp都是可点击区域
                selectIndex = i + 1;
                invalidate();
                return;
            }
            //X轴刻度
            String text = xValue.get(i);
            Rect rect = getTextBounds(text, xyTextPaint);
            x = xInit + interval * i;
            y = yOri + xylinewidth + dpToPx(2);
            if (eventX >= x - rect.width() / 2 - dp8 && eventX <= x + rect.width() + dp8 / 2 &&
                    eventY >= y - dp8 && eventY <= y + rect.height() + dp8 && selectIndex != i + 1) {
                selectIndex = i + 1;
                invalidate();
                return;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //这里需要确定几个基本点，只有确定了xy轴原点坐标，第一个点的X坐标值及其最大最小值
        this.width = getWidth();
        this.height = getHeight();
        //Y轴文本最大宽度
        float textYWdith = getTextBounds("000", xyTextPaint).width();
        for (int i = 0; i < yValue.size(); i++) {//求取y轴文本最大的宽度
            float temp = getTextBounds(yValue.get(i) + "", xyTextPaint).width();
            if (temp > textYWdith)
                textYWdith = temp;
        }
        int dp2 = dpToPx(2);
        int dp3 = dpToPx(3);
        xOri = (int) (dp2 + textYWdith + dp2 + xylinewidth);//dp2是y轴文本距离左边，以及距离y轴的距离
        //X轴文本最大高度
        xValueRect = getTextBounds("000", xyTextPaint);
        float textXHeight = xValueRect.height();
        for (int i = 0; i < xValue.size(); i++) {//求取x轴文本最大的高度
            Rect rect = getTextBounds(xValue.get(i) + "", xyTextPaint);
            if (rect.height() > textXHeight)
                textXHeight = rect.height();
            if (rect.width() > xValueRect.width())
                xValueRect = rect;
        }
        yOri = (int) (height - dp2 - textXHeight - dp3 - xylinewidth);//dp3是x轴文本距离底边，dp2是x轴文本距离x轴的距离
        xInit = interval + xOri;
        minXInit = width - (width - xOri) * 0.1f - interval * (xValue.size() - 1);//减去0.1f是因为最后一个X周刻度距离右边的长度为X轴可见长度的10%
        maxXInit = xInit;
        Log.i("TAG", "xOri:"+xOri+", xValueRect:"+xValueRect+", yOri:"+yOri
                +", xInit:"+xInit+", minXInit:"+minXInit+", maxXInit:"+maxXInit);
    }
}
