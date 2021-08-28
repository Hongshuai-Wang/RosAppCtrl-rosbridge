package net.xxhong.rosclient.ui;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import net.xxhong.rosclient.R;
import net.xxhong.rosclient.RCApplication;
import com.jilk.ros.rosbridge.ROSBridgeClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import butterknife.ButterKnife;

public class Select extends Activity {

    static int progress=1;//当前选择的进度
    public static HashMap<String,String> hashMap;
    public static ArrayList<String> order = new ArrayList<String>();

    public static ArrayList<String> mid_order=new ArrayList<>();
    public static String[] show_data;
    public static String now_select="x";//未知
    public  static String last_select="x";//未知
    private static  int count=0;
    ROSBridgeClient client;
    int flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        ButterKnife.bind(this);
        initiate();
        client = ((RCApplication)getApplication()).getRosClient();
    }



    private void initiate()
    {
        ////////////////////////////////////生成动态景点数据///////////////////////////////////////
        now_select="x";
        last_select="x";

        mid_order.clear();
        hashMap=Call.hashMap;
        LinearLayout this_form = (LinearLayout)findViewById(R.id.mylayout);
        //生成动态的textView
        TextView textView=new TextView(this_form.getContext());
        textView.setText("请选择您要到的中间点");

        textView.getText();
        this_form.addView(textView,0);
        //生成动态的checkbox
        Set<Map.Entry<String, String>> en = hashMap.entrySet();
        int i=0;
        for(Map.Entry<String, String> entry : en) {
            String value=entry.getValue();
            final CheckBox checkBox=new CheckBox(this_form.getContext());
            checkBox.setText(value);
            this_form.addView(checkBox,i+1);
            i++;
            //设置checkbox监听
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub
                    if(isChecked){
                        if(checkBox.isEnabled())
                        {
                            mid_order.add(value_to_key(checkBox.getText().toString()));
                        }
                    }else{
                        mid_order.remove(value_to_key(checkBox.getText().toString()));
                    }
                }
            });




        }
        ////////////////////////////////////生成动态景点数据///////////////////////////////////////



        //////////////////// 设置起点按钮（勾选+不可操作）
        String start_position=hashMap.get(Call.start);//获取初始位置名字
        set_enable(start_position,false);//设置不可选
        set_check(start_position,true);//勾选

        order.clear();//清空
        order.add(Call.start);//加入初始位置索引

        /////////////// 显示除终点外剩余可选点
        show_data=Call.remain_choice;
        Spinner spinner=(Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,show_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner .setAdapter(adapter);
        int index1=spinner.getSelectedItemPosition();
        now_select=show_data[index1];
        set_check(now_select,true);
        dis_enable_checked_checkbox(now_select);

        order.add(value_to_key(now_select));//加入终点索引
        if (count>=1)
        {
            String c=now_select;
        }
        //下拉框设置监听
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*
                以控件名字来勾选checkbox
                 */
                now_select=show_data[position];
                //获取

                set_check(now_select,false);//提前设置为false
                dis_enable_unchecked_checkbox(now_select);//
                set_check(now_select,true);//设置为true



                order.set(1, value_to_key(now_select));//更新终点索引 终点放第二个

                set_check(last_select,false);
                set_enable(last_select,true);
                last_select=now_select;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }

    public void cancel(View view)
    {
        reset();
    }
    public void reset()
    {
        //遍历所有checkbox控件，若enable=True 则checked=True

        LinearLayout  this_form = (LinearLayout)findViewById(R.id.mylayout);
        int count = this_form.getChildCount();
        for(int i = 0;i < count;i++){
//					获得子控件对象
            View child = this_form.getChildAt(i);
//					判断是否是CheckBox
            if(child instanceof CheckBox){
//						转为CheckBox对象
                CheckBox cb = (CheckBox)child;

                if( cb.isEnabled() && cb.isChecked()){
                    cb.setChecked(false);
                }
            }
        }
    }
    private void dis_enable_unchecked_checkbox(String name)
    {
        LinearLayout  this_form = (LinearLayout)findViewById(R.id.mylayout);
        int count = this_form.getChildCount();
        for(int i = 0;i < count;i++){
//					获得子控件对象
            View child = this_form.getChildAt(i);
//					判断是否是CheckBox
            if(child instanceof CheckBox){
//					转为CheckBox对象
                CheckBox cb = (CheckBox)child;
                String this_name=cb.getText().toString();
                if( this_name.equals(name) &&!cb.isChecked()){
                    cb.setEnabled(false);
                }
            }
        }
    }



    private void dis_enable_checked_checkbox(String name)
    {
        LinearLayout  this_form = (LinearLayout)findViewById(R.id.mylayout);
        int count = this_form.getChildCount();
        for(int i = 0;i < count;i++){
//					获得子控件对象
            View child = this_form.getChildAt(i);
//					判断是否是CheckBox
            if(child instanceof CheckBox){
//					转为CheckBox对象
                CheckBox cb = (CheckBox)child;
                String this_name=cb.getText().toString();
                if( this_name.equals(name) && cb.isChecked()){
                    cb.setEnabled(false);
                }
            }
        }
    }


    public void random_select(View view)
    {
//        reset();

        StringBuilder result2= new StringBuilder();
        String new_start=hashMap.get(Call.start);
        String new_end=last_select;
        result2.append("起点:"+new_start+"\n");
        result2.append("规划中间点:");
        for (int i = 0; i< mid_order.size(); i++)
        {
            result2.append(hashMap.get( mid_order.get(i)));
            result2.append(";");
        }
        result2.append("\n"+"终点："+new_end+"\n");
        tip(result2.toString(),true);



    }


    private boolean chose_mid()
    {
        //可以选择多个中间点
        ArrayList<String> list=get_chose();
        for(int i=0 ;i<list.size();i++)
        {
            String index=value_to_key(list.get(i));
            order.add(index);
        }


        return true;
    }

    public ArrayList<String> get_chose()
    {
        ArrayList<String> list = new ArrayList<>();
        LinearLayout  this_form = (LinearLayout)findViewById(R.id.mylayout);
        int count = this_form.getChildCount();
        for(int i = 0;i < count;i++){
//					获得子控件对象
            View child = this_form.getChildAt(i);
//					判断是否是CheckBox
            if(child instanceof CheckBox) {
                CheckBox cb = (CheckBox)child;
                if( cb.isEnabled() && cb.isChecked()){
                    list.add(cb.getText().toString());
                }
            }
        }
        return list;
    }

    public String value_to_key(String value)
    {
        Set<Map.Entry<String, String>> en = hashMap.entrySet();
        for(Map.Entry<String, String> entry : en) {
//            String value=entry.getValue();
//            String key=entry.getKey();
            if (entry.getValue().equals(value))
            {
                return entry.getKey();
            }
        }
        return "";
    }

    private  void update_textView(String new_text)
    {
        LinearLayout  this_form = (LinearLayout)findViewById(R.id.mylayout);
        int count = this_form.getChildCount();
        View child=this_form.getChildAt(0);
        if(child instanceof TextView) {
            ((TextView) child).setText(new_text);
        }
    }

    public void order_select(View view)
    {

        StringBuilder result2= new StringBuilder();
        String new_start=hashMap.get(Call.start);
        String new_end=last_select;
        result2.append(new_start+"(起点)->");

        for (int i = 0; i< mid_order.size(); i++)
        {
            result2.append(hashMap.get( mid_order.get(i)));
            result2.append("->");
        }
        result2.append(new_end+"(终点)");
        tip(result2.toString(),false);
    }
    private void generate_line()
    {
        String end=order.get(1);
        order.remove(1);
        for(int i=0;i<mid_order.size();i++)
        {
            order.add(mid_order.get(i));
        }
        order.add(end);
    }

    private void tip(final String show_text, final boolean is_random)
    {
        String title;
        if(is_random)
        {
            title="随机规划路线如下:";
        }
        else
        {
            title="顺序路线如下:";
        }
        new AlertDialog.Builder(Select.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(title)
                .setMessage(show_text)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        clear_mid_choice();
                        uncheck_all_enable();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //点击确定则发送消息给小车，并跳到下一个界面
                                update_textView("路线规划已完成");
                                generate_line();
                                send_msg(is_random);
                                dis_enable_all();

                                Intent i=new Intent(Select.this,PathSelect.class);
                                startActivity(i);
                            }
                        }
                ).create().show();
    }

    private void  dis_enable_all()
    {
        Button confirm =(Button)findViewById(R.id.confrim);
        confirm.setEnabled(false);


        Set<Map.Entry<String, String>> en = hashMap.entrySet();
        for(Map.Entry<String, String> entry : en) {
            String value = entry.getValue();
            dis_enable_checked_checkbox(value);
            //dis_enable_unchecked_checkbox(value);
            set_enable(value,false);
        }
    }


    public void Return(View view)
    {
        count+=1;
        startActivity(new Intent(Select.this, Call.class));
    }

    private void set_enable(String name,boolean set)
    {
        LinearLayout  this_form = (LinearLayout)findViewById(R.id.mylayout);
        int count = this_form.getChildCount();
        for(int i = 0;i < count;i++){
//					获得子控件对象
            View child = this_form.getChildAt(i);
//					判断是否是CheckBox
            if(child instanceof CheckBox){
//					转为CheckBox对象
                CheckBox cb = (CheckBox)child;
                String this_name=cb.getText().toString();
                if( this_name.equals(name) ){
                    cb.setEnabled(set);
                }
            }
        }
    }

    private  void set_check(String name, boolean set)
    {
        LinearLayout  this_form = (LinearLayout)findViewById(R.id.mylayout);
        int count = this_form.getChildCount();
        for(int i = 0;i < count;i++){
//					获得子控件对象
            View child = this_form.getChildAt(i);
//					判断是否是CheckBox
            if(child instanceof CheckBox){
//					转为CheckBox对象
                CheckBox cb = (CheckBox)child;
                String this_name=cb.getText().toString();
                if( this_name.equals(name) ){
                    cb.setChecked(set);
                }
            }
        }


    }

    private void clear_mid_choice()
    {
        order.clear();
        order.add(Call.start);//加入初始位置索引
        Spinner spinner=(Spinner)findViewById(R.id.spinner2);
        int index1=spinner.getSelectedItemPosition();
        order.add(value_to_key(show_data[index1]));//加入终点索引

    }

    private void send_msg(boolean is_random)
    {

        //StringBuilder send_msg= new StringBuilder();
        String send_msg="";
        send_msg+=Call.start;//加入起点
//        for(int i=0;i<order.size();i++)
//        {
//            send_msg.append(order.get(i));
//        }
        //把send_msg发送


        for (int i = 0; i< mid_order.size(); i++)
        {
            send_msg+=mid_order.get(i);

        }
        send_msg+=value_to_key(last_select);
        if(is_random)
        {
            send_msg="r"+send_msg;
        }
        else
        {
            send_msg="o"+send_msg;
        }
        MainActivity.publish(client,MainActivity.node_list[0],send_msg.toString());
    }

    private void uncheck_all_enable()
    {
        LinearLayout  this_form = (LinearLayout)findViewById(R.id.mylayout);
        int count = this_form.getChildCount();
        for(int i = 0;i < count;i++){
//					获得子控件对象
            View child = this_form.getChildAt(i);
//					判断是否是CheckBox
            if(child instanceof CheckBox){
//					转为CheckBox对象
                CheckBox cb = (CheckBox)child;
                if(cb.isChecked()&&cb.isEnabled())
                {
                    cb.setChecked(false);
                }
            }
        }
    }


    private  void disorganize()
    {
//                for(int index=order.size()-2; index>=1; index--) {
//            //从0到index处之间随机取一个值，跟index处的元素交换
//            Random random=new Random();
//            int p1=random.nextInt(index);
//            if (p1==0)
//            {
//                p1++;
//            }
//            int p2=index;
//
//            String temp =order.get(p1);
//            order.set(p1, order.get(p2));
//            order.set(p2, temp);  //更好位置
//        }
    }
}
