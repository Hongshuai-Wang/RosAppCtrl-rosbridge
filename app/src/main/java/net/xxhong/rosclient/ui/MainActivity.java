package net.xxhong.rosclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.jilk.ros.ROSClient;
import com.jilk.ros.rosbridge.ROSBridgeClient;
import net.xxhong.rosclient.R;
import net.xxhong.rosclient.RCApplication;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private  long seconds = 5;//服务器返回的时间戳，可以在具体返回的地方赋值
    boolean isNextPg = false;
    boolean conneSucc = false;


    String ip = "192.168.0.105";
    String port = "9090";

    public static String []node_list=new String[]{"/landmark_topic","/car_pose","/cmd_vel"};
    //public static String []node_list=new String[]{"/landmark_topic","/turtle1/pose","/turtle1/cmd_vel"};

    public static String name = "1";
    public static String password = "1";

    private SQLite mSQlite;
    private EditText username;
    private EditText userpassword;
    private Button login;
    private Button register;
    private Button forgotpw;

    @Bind(R.id.txt_second)
    TextView tvSec;
    @Bind(R.id.text_ip)
    EditText etIP;

    static ROSBridgeClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏标题栏以及状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        login = (Button) findViewById(R.id.btn_connect);
        register = (Button) findViewById(R.id.btn_register);
        forgotpw = (Button) findViewById(R.id.btn_fgtpw);
        username = (EditText) findViewById(R.id.userName);
        userpassword = (EditText) findViewById(R.id.userpassword);

        username.setText(name);
        userpassword.setText(password);

        etIP.setText(ip);

        // 注册账号
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5 = new Intent(MainActivity.this, Register.class);
                startActivity(intent5);
                finish();
            }
        });

        //登录
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = username.getText().toString().trim();
                password = userpassword.getText().toString().trim();

                // 检测输入是否为空
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {
                    ArrayList<User> data = mSQlite.getAllDATA();
                    boolean is_user = false;
                    // 验证账号密码是否匹配
                    for (int i = 0; i < data.size(); i++) {
                        User userdata= data.get(i);   //可存储账号数量

                        if (name.equals(userdata.getName()) && password.equals(userdata.getPassword())) {
                            is_user = true;
                            break;
                        } else {
                            is_user = false;
                        }
                    }
                    // 账号密码匹配成功
                    if (is_user) {
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        /*Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.putExtra("username",name);
                        intent.putExtra("password",password);  //展示账号密码功能*/

                        ip = etIP.getText().toString();
                        connect(ip, port);
                        isNextPg = true;
                        /*if(conneSucc)
                        {
                            startActivity(new Intent(MainActivity.this, Call.class));
                        }*/
                        //startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //构造实例化对象
        mSQlite = new SQLite(MainActivity.this);

        // 延迟操作
       // handler.sendEmptyMessageDelayed(0,5000);
       // runnable.run();
    }

    private void connect(String ip, String port) {
        client = new ROSBridgeClient("ws://" + ip + ":" + port);
        conneSucc = client.connect(new ROSClient.ConnectionStatusListener() {
            @Override
            public void onConnect() {
                client.setDebug(true);
                ((RCApplication)getApplication()).setRosClient(client);
                showTip("Connect MG success");
                Log.d(TAG,"Connect MG success");
                //startActivity(new Intent(MainActivity.this,NodesActivity.class));
                startActivity(new Intent(MainActivity.this, Call.class));
            }

            @Override
            public void onDisconnect(boolean normal, String reason, int code) {
                showTip("MG disconnect");
                Log.d(TAG,"MG disconnect");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                showTip("MG communication error");
                Log.d(TAG,"MG communication error");
            }
        });
    }

    private void showTip(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, tip,Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 延迟处理消息
    /*private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(!isNextPg) {
                //String ip = etIP.getText().toString();
                //String port = etPort.getText().toString();
                *//*ip = etIP.getText().toString();
                connect(ip, port);
                super.handleMessage(msg);
                isNextPg = true;*//*
            }
        }
    };*/


    // 倒计时
        /*private Handler mHandler = new Handler();
        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (seconds > 0){
                    seconds--;
                    tvSec.setText(String.valueOf(seconds) + "s");
                    mHandler.postDelayed(this,1000);
                }else {
                    //倒计时结束，do something
                    tvSec.setText("");
                }
            }
        };*/


    /*@OnClick({R.id.btn_connect})
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_connect:
                //String ip = etIP.getText().toString();
                //String port = etPort.getText().toString();
                ip = etIP.getText().toString();
                startActivity(new Intent(MainActivity.this, Call.class));
                connect(ip, port);
                isNextPg = true;
                break;
            default:break;
        }
    }*/



   static public void publish(ROSBridgeClient client,String detailName,String data)
    {
        String msg="";
        String Data = "\"data\":\"" + data + "\""; // String
        msg = "{\"op\":\"publish\",\"topic\":\"" + detailName + "\",\"msg\":{"+Data+"}}";
        client.send(msg);
    }

    static public  void subscribe(ROSBridgeClient client,String detailName,boolean is_subscribe)
    {
        if(is_subscribe)
        {
            client.send("{\"op\":\"subscribe\",\"topic\":\"" + detailName + "\"}");
        }
        else
        {
            client.send("{\"op\":\"unsubscribe\",\"topic\":\"" + detailName + "\"}");
        }
    }

}
