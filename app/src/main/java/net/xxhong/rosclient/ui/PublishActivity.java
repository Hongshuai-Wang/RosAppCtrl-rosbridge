package net.xxhong.rosclient.ui;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.jilk.ros.rosbridge.ROSBridgeClient;
import com.jilk.ros.rosbridge.operation.Publish;

import net.xxhong.rosclient.R;
import net.xxhong.rosclient.RCApplication;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublishActivity extends Activity  {
    ROSBridgeClient client;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);

        client = ((RCApplication)getApplication()).getRosClient();

        String msg = "";
        String detailName = "/landmark_topic";
        String typeName = "std_msgs/String";
        if(flag == 0)
        {
            msg = "{\"op\":\"advertise\",\"topic\":\"" + detailName + "\",\"type\":\"" + typeName + "\"}";
            client.send(msg);
            flag = 1;
        }

    }

    @OnClick({R.id.btn_publish})
    public void onClick(View view) {
            // 输入
            EditText editText=(EditText)findViewById(R.id.editTextTextPersonName) ;
            String number = editText.getText().toString();

            String detailName = "/landmark_topic";
            String msg = "";
            //String typeName = "std_msgs/String";
            String typeName = "std_msgs/String";
            //String data = "\"data\":1211322"; // int 格式
            //String data = "\"data\":\"1211322\""; // string 格式
            String data = "\"data\":\"" + number + "\""; // String

            msg = "";
            msg = "{\"op\":\"publish\",\"topic\":\"" + detailName + "\",\"msg\":{"+data+"}}";
            Toast.makeText(PublishActivity.this, msg, Toast.LENGTH_SHORT).show();
            client.send(msg);
    }
}
