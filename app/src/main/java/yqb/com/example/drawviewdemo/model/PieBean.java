package yqb.com.example.drawviewdemo.model;

/*
饼图实体类
 */
public class PieBean {

    //绘制扇形区域的占比个数相加*360
    public float value;
    //绘制扇形区域颜色
    public int color;

    public PieBean(float value, int color) {
        this.value = value;
        this.color = color;
    }
}
