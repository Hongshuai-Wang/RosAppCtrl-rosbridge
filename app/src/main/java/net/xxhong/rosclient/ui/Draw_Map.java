package net.xxhong.rosclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Draw_Map extends View {


    //需要传入参数
    public static ArrayList<float[]> points;//所有点坐标{[x1,y1],[x2,y2].....}
    public static ArrayList<String>order;//顺序["1","3","4"....]
    public static HashMap<String,String> hashMap;//字典
    public static HashMap<String,float[]>pointMap;
    public static ArrayList<String> passed_records =new ArrayList<>();//"a,c,d"

    public static float dis_threshold=200;

    public static boolean is_parse_point=false;


    //记录当前位置
    public static float current_x;//当前x
    public static float current_y;//当前y
    public static int progress=0;

    private Paint not_passed_paintSet(){
        //设置已经通过Paint
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5f);
        paint.setTextSize(48);
        return paint;
    }//过程点的画图设置

    private Paint passed_paintSet(){
        //设置通过Paint
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5f);
        paint.setTextSize(48);
        return paint;
    }//过程点的画图设置

    private Paint position_paintSet(){
        //设置当前位置Paint
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5f);
        paint.setTextSize(70);
        return paint;
    }//当前点的画图设置

    public Draw_Map(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint not_passed_paint= not_passed_paintSet();
        Paint passed_paint= passed_paintSet();
        Paint position_paint= position_paintSet();

        canvas.drawCircle(current_x, current_y, 20, position_paint);
        canvas.drawText("MG:("+String.format("%.2f",current_x)+","+String.format("%.2f",current_y)+")", current_x -350, current_y,position_paint);



        if(is_parse_point) {
            Set<Map.Entry<String, float[]>> en = Call.pointMap.entrySet();
            for (Map.Entry<String, float[]> entry : en) {
                float[] value = entry.getValue();
                String key = entry.getKey();
                String show_str = Call.hashMap.get(key);
                if (key.equals(order.get(0))) {
                    show_str += "(起点)";
                }
                if (key.equals(order.get(order.size() - 1))) {
                    show_str += "(终点)";
                }



                float text_position_x=value[0] + 20;
                float text_position_y=value[1];
                //

                if(key.equals("b"))
                {
                    text_position_x=value[0]-230;
                }

                //遍历那些未经过的点
                if (!passed_records.contains(key)) //若
                {
                    float dis = (float) Math.sqrt((current_x - value[0]) * (current_x - value[0]) + (current_y - value[1]) * (current_y - value[1]));
                    if (dis < dis_threshold) //距离小于阈值
                    {
                        passed_records.add(key);
                        canvas.drawCircle(value[0], value[1], 6, passed_paint);
                        canvas.drawText(show_str, text_position_x, text_position_y, passed_paint);
                    }
                    else {
                        canvas.drawCircle(value[0], value[1], 6,  not_passed_paint);
                        canvas.drawText(show_str, text_position_x, text_position_y, not_passed_paint);
                    }

                } else
                {
                    canvas.drawCircle(value[0], value[1], 6, passed_paint);
                    canvas.drawText(show_str, text_position_x, text_position_y, passed_paint);
                }
            }
        }
    }

   /* @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint not_passed_paint= not_passed_paintSet();
        Paint passed_paint= passed_paintSet();
        Paint position_paint= position_paintSet();

        canvas.drawCircle(current_x, current_y, 20, position_paint);
        canvas.drawText("MG:("+String.format("%.2f",current_x)+","+String.format("%.2f",current_y)+")", current_x -350, current_y,position_paint);



        if(is_parse_point) {
            Set<Map.Entry<String, float[]>> en = Call.pointMap.entrySet();
            for (Map.Entry<String, float[]> entry : en) {
                float[] value = entry.getValue();
                String key = entry.getKey();
                String show_str = Call.hashMap.get(key);
                if (key.equals(order.get(0))) {
                    show_str += "(起点)";
                }
                if (key.equals(order.get(order.size() - 1))) {
                    show_str += "(终点)";
                }



                float text_position_x=value[0] + 20;
                float text_position_y=value[1];
                //

                if(key.equals("b"))
                {
                    text_position_x=value[0]-230;
                }

                //遍历那些未经过的点
                if (!passed_records.contains(key)) //若
                {
                    float dis = (float) Math.sqrt((current_x - value[0]) * (current_x - value[0]) + (current_y - value[1]) * (current_y - value[1]));
                    if (dis < dis_threshold && order.contains(key)&&order.indexOf(key)==progress) //距离小于阈值
                    {

                        passed_records.add(key);
                        canvas.drawCircle(value[0], value[1], 6, passed_paint);
                        canvas.drawText(show_str, text_position_x, text_position_y, passed_paint);
                        progress+=1;
                    }
                    else {
                        canvas.drawCircle(value[0], value[1], 6,  not_passed_paint);
                        canvas.drawText(show_str, text_position_x, text_position_y, not_passed_paint);
                    }

                } else
                {
                    canvas.drawCircle(value[0], value[1], 6, passed_paint);
                    canvas.drawText(show_str, text_position_x, text_position_y, passed_paint);
                }
            }
        }
    }*/

}
