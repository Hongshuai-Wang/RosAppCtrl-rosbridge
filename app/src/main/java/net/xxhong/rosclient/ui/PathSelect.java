package net.xxhong.rosclient.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.jilk.ros.rosbridge.ROSBridgeClient;
import net.xxhong.rosclient.R;
import net.xxhong.rosclient.RCApplication;
import net.xxhong.rosclient.entity.PublishEvent;
import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.widget.TextView;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.Map;
import java.util.Set;

import android.graphics.Color;
import android.widget.Toast;

public class PathSelect extends Activity{


    private Button get_off_bt;
    //private int btn;
    public ArrayList<Line> lines=new ArrayList<>();
    public ArrayList<String> global_order;
    public float next_x=0;
    public float next_y=0;
    public float ratio_x=344;
    public float ratio_y=350;
    public String speed="0";
    public float dis=10000;
    private static boolean watch_dog;
    private static boolean start;
    private static  float w = 1;
    private static  float h = 1;
    boolean is_get = true;
    boolean is_end = false;
    public  static float s=0;
    public static int total_nodes = 1;

    public ROSBridgeClient client;

    @SuppressLint("NonConstantResourceId")
    @Bind(R.id.button)
    Button btnStop;

    @SuppressLint("NonConstantResourceId")
    @Bind(R.id.textView14)
    TextView textView14;

    @Bind(R.id.textView17)
    TextView textView17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_select);
        EventBus.getDefault().register(this);

        get_off_bt = (Button) findViewById(R.id.button);

        ButterKnife.bind(this);
        watch_dog=true;
        start=false;
        dis=10000;
        client = ((RCApplication) getApplication()).getRosClient();

        // ??????????????????
        get_global_order();

        load_map_info();//??????????????????

        //MainActivity.subscribe(client,MainActivity.node_list[1],true);//????????????
        //MainActivity.subscribe(client,MainActivity.node_list[2],true);//????????????

        //initiate();
        get_map_size();
        //handler.sendEmptyMessageDelayed(0,1500); //??????????????????
        initiate();
        MainActivity.subscribe(client,MainActivity.node_list[1],true);//????????????
        MainActivity.subscribe(client,MainActivity.node_list[2],true);//????????????
    }


    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            get_map_size();
            initiate();
            //Toast.makeText(PathSelect.this, "?????????????????????",Toast.LENGTH_SHORT).show();

        }
    };


    private void get_map_size()
    {
/*        LinearLayout layout = (LinearLayout)findViewById(R.id.drawArea);
        int count = layout.getChildCount();
        for (int i = 0; i< count; ++i)
        {
            // ?????????????????????
            View child = layout.getChildAt(i);
            // ???????????????check box
            if(child instanceof Draw_Map){
                View view1 = (View)child;
                Draw_Map dm = (Draw_Map)child;

                w = view1.getWidth();
                h = view1.getHeight();

                //??????????????????????????????
                ratio_x = w/(float)4;
                ratio_y = h/(float)5.8;

            }
        }*/
        //w = 1400;
        //h = 1940;
        w=1080;
        h=2000;

        //??????????????????????????????
        ratio_x = w/(float)4;
        ratio_y = h/(float)5.8;

        scale_points();//??????????????????

        Draw_Map.is_parse_point=true;
    }


    private void get_global_order()
    {
        //?????????????????????????????????
        global_order=Select.order;
        total_nodes = global_order.size();
    }

    //Receive data from ROS server, send from ROSBridgeWebSocketClient onMessage()
    // ???????????????????????????
    public void onEvent(final PublishEvent event) {
        //show data on TextView
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // ??????
                if(MainActivity.node_list[1].equals(event.name) ) {
                    parse_position(event.msg);
                    move();
                }
                // ??????
                if(MainActivity.node_list[2].equals(event.name))
                {
                    parse_speed(event.msg);
                }
            }
        });

    }

    void parse_speed(String event_msg)
    {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(event_msg);
            JSONObject jsonInfo = (JSONObject)jsonObj.get("linear");//???????????????

            String speed1=jsonInfo.get("x").toString();
            speed= String.format("%.2f",Float.parseFloat(speed1));

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parse_position(String event_msg)
    {
        try {

            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(event_msg);

            JSONObject jsonInfo = (JSONObject)jsonObj.get("position");//???????????????
            String x=jsonInfo.get("x").toString();
            String y=jsonInfo.get("y").toString();

            /*String x=jsonObj.get("x").toString();
            String y=jsonObj.get("y").toString();*/


            next_x=Float.parseFloat(x);
            next_y=Float.parseFloat(y);

            next_x = next_x*ratio_x  + (float)0.25 * ratio_x;
            next_y = h -( next_y*ratio_y + (float)0.3 * ratio_y);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load_map_info()
    {
        ArrayList<float[]> now_points=new ArrayList<>();
        for(int i=0;i<global_order.size();i++)
        {
            //float []point=Call.pointMap.get(global_order.get(i));
            float []point=Call.originMap.get(global_order.get(i));
            now_points.add(point);
        }


        //Draw_Map.initiate();
        Draw_Map.progress=0;

        Draw_Map.passed_records.clear();
        Draw_Map.order=global_order;
        Draw_Map.points=now_points;
        Draw_Map.hashMap=Call.hashMap;
        //Draw_Map.pointMap=Call.pointMap;
        Draw_Map.pointMap=Call.originMap;

//        Draw_Map.current_x =Draw_Map.points.get(0)[0];
//        Draw_Map.current_y =Draw_Map.points.get(0)[1];
        Draw_Map.current_x =4000;
        Draw_Map.current_y =5800;

    }


    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void initiate()
    {
        //??????????????????
        LinearLayout layout= (LinearLayout) findViewById(R.id.drawArea);
        layout.removeAllViews();
        final Draw_Map view=new Draw_Map(this);
        view.setMinimumHeight(300);
        view.setMinimumWidth(500);

        //??????view????????????
        view.invalidate();
        layout.addView(view);

        //????????????+??????
        textView14.setText("Dis: "+ String.format("%.2f",dis/1000)+"km");
        textView17.setText("V:"+speed+"m/s");
        textView17.setTextColor(Color.WHITE);
        textView14.setTextColor(Color.WHITE);

        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.line);
        linearLayout.removeAllViews();

        int show_number = 3;
        int start_index=show_number*(Draw_Map.progress/show_number);

        // ?????????????????????
        /*for(int i=start_index;i<global_order.size();i++)
        {
            if(i>=start_index+show_number)
            {
                break;
            }

            TextView textView=new TextView(this);
            textView.setTextSize(16);
            textView.setTextColor(Color.WHITE);

            if(i!=global_order.size()-1)
            {
                textView.setText(Call.hashMap.get(global_order.get(i))+"->");
            }
            else
            {
                textView.setText(Call.hashMap.get(global_order.get(i)));
            }
            if(Draw_Map.passed_records.contains(global_order.get(i)))
            {
                textView.setTextColor(Color.GREEN);
            }
            linearLayout.addView(textView);
        }*/

    }

    @OnClick({R.id.button})
    public void onClick(View view) {
//      watch_dog = false;

        String show_text="";
        if(dis<220)
        {
            show_text="??????????????????????????????????????????????????????";
            // ????????????
            MainActivity.subscribe(client,MainActivity.node_list[1],false);
           // ?????????
            new AlertDialog.Builder(PathSelect.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("??????")
                    .setMessage(show_text)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //client.send("{\"op\":\"unsubscribe\",\"topic\":\"" + "/turtle1/pose" + "\"}");
                                    Draw_Map.passed_records.clear();
                                    startActivity(new Intent(PathSelect.this, PayActivity.class));
                                }
                            }
                    ).create().show();
        }
        else
        {
            show_text="??????????????????????????????";
            new AlertDialog.Builder(PathSelect.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("??????")
                    .setMessage(show_text)
                    .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            watch_dog=true;
                        }
                    })
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    client.send("{\"op\":\"unsubscribe\",\"topic\":\"" + "/turtle1/pose" + "\"}");
                                    Draw_Map.passed_records.clear();
                                    startActivity(new Intent(PathSelect.this, PayActivity.class));
                                }
                            }
                    ).create().show();
        }
    }

    private void move()
    {
        Draw_Map.current_x =next_x;
        Draw_Map.current_y =next_y;
        initiate();

        if(!start)
        {
            judge_begin();//?????????????????????
        }

        if(watch_dog)
        {
            judge_end();//?????????????????????
        }
    }

    private void scale_points()
    {
        //Set<Map.Entry<String, float[]>> en = Call.pointMap.entrySet();
        Set<Map.Entry<String, float[]>> en = Call.originMap.entrySet();
        for(Map.Entry<String, float[]> entry : en) {

            String key = entry.getKey();
            float [] new_point=new float[2];
            //float origin_x= Call.pointMap.get(key)[0];
            //float origin_y= Call.pointMap.get(key)[1];

            float origin_x= Call.originMap.get(key)[0];
            float origin_y= Call.originMap.get(key)[1];

            new_point[0]=  origin_x * ratio_x;
            new_point[1]=  origin_y * ratio_y;


            Call.pointMap.put(key,new_point);

        }
    }


    private float step=10;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_W)
        {
            manual_move("up");
            //Toast.makeText(PathSelect.this, "you have pressed c",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(keyCode==KeyEvent.KEYCODE_A)
        {
            manual_move("left");
            //Toast.makeText(PathSelect.this, "you have pressed D",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(keyCode==KeyEvent.KEYCODE_S)
        {
            manual_move("down");
            //Toast.makeText(PathSelect.this, "you have pressed D",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(keyCode==KeyEvent.KEYCODE_D)
        {
            manual_move("right");
            //Toast.makeText(PathSelect.this, "you have pressed D",Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.onKeyUp(keyCode,event);
    }

    private void manual_move(String direction)
    {

        if(direction.equals("up"))
        {
            if(Draw_Map.current_y-step>0)
            {
                Draw_Map.current_y-=step;
            }
            else
            {
                Draw_Map.current_y=0;
            }

            //?????????????????????
        }
        if(direction.equals("down"))
        {

            if(Draw_Map.current_y+step<h)
            {
                Draw_Map.current_y+=step;
            }
            else {
                Draw_Map.current_y = h;
            }
        }
        if(direction.equals("left"))
        {
            if(Draw_Map.current_x-step>0)
            {
                Draw_Map.current_x-=step;
            }
            else
            {
                Draw_Map.current_x=0;
            }
        }
        if(direction.equals("right"))
        {
            if(Draw_Map.current_x+step<w)
            {
                Draw_Map.current_x+=step;
            }
            else {
                Draw_Map.current_x = w;
            }
        }

        initiate();
        if(!start)
        {
            judge_begin();
        }

        if(watch_dog)
        {
            judge_end();
        }
    }


    public void judge_end()
    {
        float [] last_points=Call.pointMap.get(Draw_Map.order.get(Draw_Map.order.size()-1));
        dis=(Draw_Map.current_x-last_points[0])*(Draw_Map.current_x-last_points[0])+(Draw_Map.current_y-last_points[1])*(Draw_Map.current_y-last_points[1]);
        dis=(float) Math.sqrt(dis);

        // ??????????????????????????????????????????pass_record????????????????????????????????????????????????
        //if (Draw_Map.passed_records.contains(Draw_Map.order.get(Draw_Map.order.size()-1))) {
        if (dis < 220) {
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {*/
                    watch_dog=false;
                    Toast.makeText(PathSelect.this, "???????????????", Toast.LENGTH_SHORT).show();
                    //String show_text = "??????????????????????????????????????????????????????";
                    Draw_Map.passed_records.clear();
                    startActivity(new Intent(PathSelect.this, PayActivity.class));
                    /*new AlertDialog.Builder(PathSelect.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("??????")
                            .setMessage(show_text)

                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            client.send("{\"op\":\"unsubscribe\",\"topic\":\"" + MainActivity.node_list[1] + "\"}");
                                            Draw_Map.passed_records.clear();
                                            startActivity(new Intent(PathSelect.this, PayActivity.class));
                                        }
                                    }
                            ).create().show();*/

            /*AlertDialog  dialog = new AlertDialog.Builder(PathSelect.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("??????")
                            .setMessage(show_text)

                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            client.send("{\"op\":\"unsubscribe\",\"topic\":\"" + MainActivity.node_list[1] + "\"}");
                                            Draw_Map.passed_records.clear();
                                            startActivity(new Intent(PathSelect.this, PayActivity.class));
                                        }
                                    }
                            ).create();
                            dialog.show();
                            dialog.dismiss();*/
               // }

          // });

        }
    }


    public  void judge_begin()
    {
        float [] first_points=Call.pointMap.get(Draw_Map.order.get(0));
        dis=(Draw_Map.current_x-first_points[0])*(Draw_Map.current_x-first_points[0])+(Draw_Map.current_y-first_points[1])*(Draw_Map.current_y-first_points[1]);
        dis=(float) Math.sqrt(dis);

        if (Draw_Map.passed_records.contains(Draw_Map.order.get(0))) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    start=true;
                    Toast.makeText(PathSelect.this, "???????????????", Toast.LENGTH_SHORT).show();
                    //String show_text = "???????????????,???????????????";

                    /*new AlertDialog.Builder(PathSelect.this)
                            .setTitle("??????")
                            .setMessage(show_text)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            //Toast.makeText(PathSelect.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                                            start=true;
                                        }
                                    }
                            ).show();*/
                }
            });

        }
    }
}
