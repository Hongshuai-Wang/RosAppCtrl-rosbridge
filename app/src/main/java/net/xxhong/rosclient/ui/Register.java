package net.xxhong.rosclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.xxhong.rosclient.R;

//import androidx.appcompat.app.AppCompatActivity;

public class Register extends Activity {

    //private static final android.R.attr R = ;
    private SQLite mSQlite;
    private EditText username;
    private EditText userpassword;
    private Button reday;
    private Button back;
    private User view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(net.xxhong.rosclient.R.layout.activity_register);

        reday = (Button) findViewById(R.id.ready);
        back = (Button) findViewById(R.id.back);
        username = (EditText) findViewById(R.id.username_register);
        userpassword = (EditText) findViewById( R.id.userpassword_register);

        // 返回主叶面
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Register.this,MainActivity.class);
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 注册
        reday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = username.getText().toString().trim();
                String password = userpassword.getText().toString().trim();
                if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(password)){
                    mSQlite.add(name,password);
                    Intent intent1 = new Intent(Register.this,MainActivity.class);
                    startActivity(intent1);
                    finish();
                    Toast.makeText(Register.this,"注册成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Register.this,"信息不完备，注册失败", Toast.LENGTH_SHORT).show();}
            }
        });
        //实例化mSQLite,用于登录数据验证
        mSQlite = new SQLite(Register.this);
    }
}
