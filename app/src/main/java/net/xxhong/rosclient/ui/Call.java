package net.xxhong.rosclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.xxhong.rosclient.R;
import net.xxhong.rosclient.RCApplication;

import android.view.View;

import android.widget.Spinner;
import android.widget.ArrayAdapter;

import com.jilk.ros.rosbridge.ROSBridgeClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;

public class Call extends Activity {

    ROSBridgeClient client;
    int flag = 0;

    public static  String start;
    public static HashMap<String,String> hashMap=new HashMap<>();//编号名字对照字典
    public static String[] remain_choice;
    public static HashMap<String,float[]>originMap=new HashMap<>();
    public static HashMap<String,float[]>pointMap=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);
        ButterKnife.bind(this);
        initiate();

        //client = ((RCApplication)getApplication()).getRosClient();
        createTopic();

        pointMap.clear();
    }


    public void subscribe_data()
    {
        //景点数据
        hashMap.put("a","松江(a)");
        hashMap.put("b","奉贤(b)");
        hashMap.put("c","宝山(c)");
        hashMap.put("d","闵行(d)");
        hashMap.put("e","静安(e)");
        hashMap.put("f","浦东(f)");
        hashMap.put("g","黄浦(g)");
        hashMap.put("h","嘉定(h)");
        hashMap.put("i","返程(i)");

        /*pointMap.put("a", new float[]{(float)2.8, (float)5.2});
        pointMap.put("b", new float[]{(float)3.9, (float)3.7});
        pointMap.put("c", new float[]{(float)3.2, (float)2.9});
        pointMap.put("d", new float[]{(float)3.2, (float)1.5});
        pointMap.put("e", new float[]{(float)2.0, (float)0.5});
        pointMap.put("f", new float[]{(float)0.8, (float)2.1});
        pointMap.put("g", new float[]{(float)1.6, (float)4.8});
        pointMap.put("h", new float[]{(float)1.6, (float)3.4});
        pointMap.put("i", new float[]{(float)0.4, (float)5.5});*/

        originMap.put("a", new float[]{(float)2.8, (float)5.3});
        originMap.put("b", new float[]{(float)3.9, (float)3.7});
        originMap.put("c", new float[]{(float)3.3, (float)2.9});
        originMap.put("d", new float[]{(float)3.2, (float)0.5});
        originMap.put("e", new float[]{(float)0.8, (float)0.5});
        originMap.put("f", new float[]{(float)0.4, (float)2.9});
        originMap.put("g", new float[]{(float)1.7, (float)4.8});
        originMap.put("h", new float[]{(float)1.7, (float)3.3});
        originMap.put("i", new float[]{(float)0.4, (float)5.5});
    }

    void initiate()
    {
        subscribe_data();
        Spinner spinner=(Spinner) findViewById(R.id.spinner);
        String[] show_data=dic_to_list();


        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,show_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner .setAdapter(adapter);

    }

    public String[] dic_to_list()
    {
        int count=hashMap.size();

        String [] result=new String[count];
        Set<Map.Entry<String, String>> en = hashMap.entrySet();
        int i=0;
        for(Map.Entry<String, String> entry : en) {
            String value=entry.getValue();
            result[i]=value;
            i++;
        }
        return  result;
    }

    public void createTopic()
    {
        String msg = "";
        String detailName = "/landmark_topic";
        String typeName = "std_msgs/String";
        if(flag == 0)
        {
            msg = "{\"op\":\"advertise\",\"topic\":\"" + detailName + "\",\"type\":\"" + typeName + "\"}";
            MainActivity.client.send(msg);
            flag = 1;
        }
    }
    public void  call(View view)
    {

        Spinner spinner=(Spinner) findViewById(R.id.spinner);
        int index=spinner.getSelectedItemPosition();



        //获取字典
        start= value_to_key(spinner.getItemAtPosition(index).toString());

        //把start 发出去
//        String data = "\"data\":\"" + start + "\""; // String
//        String detailName = "/landmark_topic"; // 话题名
//        String msg = "";    // JSON消息
//        msg = "{\"op\":\"publish\",\"topic\":\"" + detailName + "\",\"msg\":{"+data+"}}";
//        MainActivity.client.send(msg);
        //MainActivity.publish(MainActivity.node_list[0],start);


        update_remain_choice();
        startActivity(new Intent(Call.this, Select.class));
    }

    public void Return(View view)
    {
        startActivity(new Intent(Call.this, MainActivity.class));
    }

    private void update_remain_choice()
    {
        if (hashMap.size()>1)
        {
            remain_choice=new String[hashMap.size()-1];

            Set<Map.Entry<String, String>> en = hashMap.entrySet();
            int i=0;
            for(Map.Entry<String, String> entry : en) {
                String value = entry.getValue();
                String key = entry.getKey();
                if(!key.equals(start) && !start.equals(null))
                {
                    remain_choice[i]=value;
                    i++;
                }

            }
        }
    }


    public String value_to_key(String value1)
    {
        Set<Map.Entry<String, String>> en = hashMap.entrySet();
        for(Map.Entry<String, String> entry : en) {
            //String value=entry.getValue();
            // String key=entry.getKey();
            if (entry.getValue().equals(value1))
            {
                return entry.getKey();
            }
        }
        return "";
    }
}
