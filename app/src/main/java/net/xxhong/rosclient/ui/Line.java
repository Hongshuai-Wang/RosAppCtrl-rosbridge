package net.xxhong.rosclient.ui;

import android.app.Activity;

public class Line extends Activity {
    public float start_x;
    public  float start_y;
    public  float end_x;
    public float end_y;

    public float direction_x;
    public float direction_y;

    public float vector_x;
    public float vector_y;
    public Line(float start_x,float start_y,float end_x,float end_y)
    {
        this.start_x=start_x;
        this.start_y=start_y;
        this.end_x=end_x;
        this.end_y=end_y;

        vector_x=end_x-start_x;
        vector_y=end_y-start_y;
        get_direction();


    }

    public float locate(float x ,float y)
    {
        //<=0在直线上(包含端点)；>0在直线外
//        float [] amend_position=amend(x,y);
//
//
//        float amend_x=amend_position[0];
//        float amend_y=amend_position[1];
        float amend_x=x;
        float amend_y=y;
        float vector1_x=amend_x-start_x;
        float vector1_y=amend_y-start_y;

        float vector2_x=amend_x-end_x;
        float vector2_y=amend_y-end_y;
        return  vector1_x*vector2_x+vector1_y*vector2_y;
    }

    public float[] amend(float x,float y)
    {
        //将不在直线上的点修正到直线上
        float [] result=new float[2];
        float vector_x=x-start_x;
        float vector_y=x-start_y;
        float dot=vector_x*direction_x+vector_y*direction_y;
        result[0]=start_x+direction_x*dot;
        result[1]=start_y+direction_y*dot;
        return result;
    }
    private void get_direction()
    {
        direction_x=(float)((end_x-start_x)/Math.sqrt(vector_x*vector_x+vector_y*vector_y));
        direction_y=(float) ((end_y-start_y)/Math.sqrt(vector_x*vector_x+vector_y*vector_y));
    }

}
